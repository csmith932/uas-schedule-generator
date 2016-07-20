package gov.faa.ang.swac.scheduler.regiongen;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import gov.faa.ang.swac.common.datatypes.REGION;
import gov.faa.ang.swac.common.flightmodeling.AircraftUserClassNationalityConstants;
import gov.faa.ang.swac.common.flightmodeling.FlightLeg;
import gov.faa.ang.swac.common.flightmodeling.Itinerary;
import gov.faa.ang.swac.controller.ExitException;
import gov.faa.ang.swac.controller.core.CloneableAbstractTask;
import gov.faa.ang.swac.datalayer.DataAccessException;
import gov.faa.ang.swac.datalayer.storage.DataMarshaller;
import gov.faa.ang.swac.scheduler.airport_data.AirportData;
import gov.faa.ang.swac.scheduler.airport_data.AirportDataMap;
import gov.faa.ang.swac.scheduler.forecast.airport_data.CountryRegionHash;
import gov.faa.ang.swac.scheduler.forecast.airport_data.CountryRegionHash.CountryRegionRecord;

/**
 * This Task will set each itinerary's REGION based on user class and the
 * airports used in the flight legs.
 * 
 * This class doesn't really belong in the scheduler project, but due to this
 * class's imports and project dependency relationships, it was the least worst
 * location. As it uses scheduler airport data and region data, it has to be in
 * scheduler project or a project that depends on scheduler. The scheduler
 * project was the only real viable option.
 * 
 * @author cunningham
 */
public class RunRegionGen extends CloneableAbstractTask {

    private static org.apache.log4j.Logger logger = org.apache.log4j.LogManager.getLogger(RunRegionGen.class);
    private DataMarshaller inputItineraries;

    public DataMarshaller getInputItineraries() {
        return this.inputItineraries;
    }

    public void setInputItineraries(DataMarshaller val) {
        this.inputItineraries = val;
    }
    private DataMarshaller internationalCountryRegionMap;

    public DataMarshaller getInternationalCountryRegionMap() {
        return internationalCountryRegionMap;
    }

    public void setInternationalCountryRegionMap(
            DataMarshaller internationalCountryRegionMap) {
        this.internationalCountryRegionMap = internationalCountryRegionMap;
    }
    private DataMarshaller outputItineraries;

    public DataMarshaller getOutputItineraries() {
        return outputItineraries;
    }

    public void setOutputItineraries(DataMarshaller outputItineraries) {
        this.outputItineraries = outputItineraries;
    }
    
    private DataMarshaller mergedAirportData;



	public DataMarshaller getMergedAirportData() {
		return mergedAirportData;
	}

	public void setMergedAirportData(DataMarshaller mergedAirportData) {
		this.mergedAirportData = mergedAirportData;
	}
	
	private AirportDataMap airportDataMap;

    public RunRegionGen() {  }
    
    public RunRegionGen(RunRegionGen org) {
    	super(org);
    }
        
	@Override
    public void run() {
        try {
            List<CountryRegionRecord> countryRegionRecordList = new ArrayList<CountryRegionRecord>();
            logger.debug("loading country region records...");
            this.internationalCountryRegionMap.load(countryRegionRecordList);
            List<Itinerary> itineraries = new ArrayList<Itinerary>();
            logger.debug("loading itineraries...");
            this.inputItineraries.load(itineraries);
            CountryRegionHash countryRegionHash = new CountryRegionHash(countryRegionRecordList);
            
            List<AirportDataMap> airportDataMapList = new ArrayList<AirportDataMap>(1);
            logger.debug("loading airport data...");
            this.mergedAirportData.load(airportDataMapList);
            
            airportDataMap = airportDataMapList.get(0);

            for (Itinerary itin : itineraries) {
                String user_class = itin.aircraft.atoUserClass();
                if (user_class == null || user_class.trim().length() == 0) {
                    continue;
                }
                String[] user_class_fields = user_class.split(" ");
                String nationality = user_class_fields[0];
                if (nationality.equals(AircraftUserClassNationalityConstants.USER_CLASS_NATIONALITY_DOMESTIC)) {
                    itin.setRegion(REGION.US);
                } else if (nationality.equals(AircraftUserClassNationalityConstants.USER_CLASS_NATIONALITY_FOREIGN)) {
                    //Find and set Region to Itinerary
                    Set<REGION> regions = new HashSet<REGION>();
                    for (FlightLeg fLeg : itin.flightLegs()) 
                    {
                        //get region of arrival airport
                    	AirportData arpData = airportDataMap.getAirport(fLeg.arrivalAirport());
                        if(arpData != null)
                        {
                        	REGION region = countryRegionHash.getRegion(arpData);
                        	if(region != null)
                        	{
                        		regions.add(region);
                        	}
                        	
                        }
                        // get region of departure
                        arpData = airportDataMap.getAirport(fLeg.departureAirport());
                        if(arpData != null)
                        {
                        	REGION region = countryRegionHash.getRegion(arpData);
                        	if(region != null)
                        	{
                        		regions.add(region);
                        	}
                        	
                        }
                    }
                    
                    //Set region based on priority PACIFIC>ATLANTIC>LATIN_AMERICA>CANADA
                    if (regions.contains(REGION.PACIFIC)) {
                        itin.setRegion(REGION.PACIFIC);
                    } else if (regions.contains(REGION.ATLANTIC)) {
                        itin.setRegion(REGION.ATLANTIC);
                    } else if (regions.contains(REGION.LATIN_AMERICA)) {
                        itin.setRegion(REGION.LATIN_AMERICA);
                    } else if (regions.contains(REGION.CANADA)) {
                        itin.setRegion(REGION.CANADA);
                    }
                }
            }

            // Save
            logger.debug("saving itineraries...");
            this.outputItineraries.save(itineraries);
        } catch (DataAccessException ex) {
            this.abort(new ExitException("ERROR ENCOUNTERED IN REGION GEN, ABORTING..."));
            logger.trace(ex.getStackTrace());
            return;
        }

    }

    @Override
    public boolean validate(VALIDATION_LEVEL level) {
        boolean retval = false;

        retval = validateFiles(new DataMarshaller[]{internationalCountryRegionMap}, level);

        return retval;
    }

    @Override
    public RunRegionGen clone() {
        return new RunRegionGen(this);
    }
}
