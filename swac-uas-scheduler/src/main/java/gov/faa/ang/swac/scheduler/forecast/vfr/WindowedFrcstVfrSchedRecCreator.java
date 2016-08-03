package gov.faa.ang.swac.scheduler.forecast.vfr;

import gov.faa.ang.swac.common.datatypes.Timestamp;
import gov.faa.ang.swac.common.flightmodeling.ScheduleRecord;
import gov.faa.ang.swac.scheduler.forecast.airport_data.CountryRegionHash;
import gov.faa.ang.swac.scheduler.forecast.airport_data.ForecastAirportCountsMap;
import gov.faa.ang.swac.scheduler.forecast.airport_data.ForecastAirportDataMerger;
import gov.faa.ang.swac.scheduler.forecast.airport_data.ForecastInternationalAirportData;
import gov.faa.ang.swac.scheduler.forecast.trip_distribution.ForecastTripDistAirportData;
import gov.faa.ang.swac.scheduler.vfr.VFRHelicopterMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WindowedFrcstVfrSchedRecCreator  
{
	private ForecastVfrSchedRecCreator vfrSchedRecCreator;    
	private ForecastAirportCountsMap tafData;
	private ForecastInternationalAirportData intlData;
    private CountryRegionHash countryRegionHash;
	private Timestamp wndStartTime;
	private Timestamp wndEndTime;
    
	private List<ScheduleRecord> schedRecList;

	public WindowedFrcstVfrSchedRecCreator(
        ForecastVfrSchedRecCreator vfrCreator,    
        ForecastAirportCountsMap tafData,
        ForecastInternationalAirportData intlData,
        CountryRegionHash countryRegionHash,
        Timestamp startTime,
        Timestamp endTime)
    {
        this.vfrSchedRecCreator = vfrCreator;    
        this.tafData            = tafData;
        this.intlData           = intlData;
        this.countryRegionHash  = countryRegionHash;
        
        this.wndStartTime       = startTime;
        this.wndEndTime         = endTime;
	}

	public void createVfrFlights(
        List<ForecastTripDistAirportData> airportList,
        List<ScheduleRecord> ifrSchedRecList) 
    {
		this.schedRecList = new ArrayList<ScheduleRecord>();
		
        ForecastAirportDataMerger.mergeCountDataIntoAirports(
            airportList, 
            tafData, 
            ForecastAirportDataMerger.DATA_TAF_FORECAST);
        ForecastAirportDataMerger.mergeInternationalData(
            airportList, 
            intlData, 
            ForecastAirportDataMerger.DATA_TAF_FORECAST,
            countryRegionHash);
		
		countWindowedFlightsByAirports(
            airportList,
            ifrSchedRecList);
		
        for(ForecastTripDistAirportData airport : airportList) 
        {
            int nVfrToAdd = getVfrCountToAdd(airport);
            if (0 < nVfrToAdd) 
            {
            	schedRecList.addAll(
                    this.vfrSchedRecCreator.populateAirport(airport,nVfrToAdd));
            }
        }
	}

 	private void countWindowedFlightsByAirports(
        List<ForecastTripDistAirportData> airportList,
		List<ScheduleRecord> ifrSchedRecList) 
    {
		// obtaining windowed (Z9-Z33) flight counts by airport
        // can be done in two different ways
        // 1. use the created flights themselves with checks on the Z9-Z33 window; this gives the exact count
        // 2. using the computed integerized flight counts by city pair (approximate but good; some flights could have been shifted 
		// outside of the window even if they were
        // the old way of counting from the initial baseline flights is not good!
        
        // we use the first approach here
		
		// reset the counts first and build the airport map
		Map<String, ForecastTripDistAirportData> airportMap = 
        	new HashMap<String, ForecastTripDistAirportData>((int) (airportList.size() * 1.4));
        for (ForecastTripDistAirportData airport : airportList) 
        {
        	airport.resetWindowedFlightCounters();
        	airportMap.put(airport.getMostLikelyCode(),airport);
        }
        
        // Count flights in the window
        for (ScheduleRecord schedRec : ifrSchedRecList) 
        {
        	if (schedRec.runwayOffTime != null && 
                !wndStartTime.after(schedRec.runwayOffTime) && 
                !wndEndTime.before(schedRec.runwayOffTime)) 
            {
            	ForecastTripDistAirportData airport = airportMap.get(schedRec.depAprtEtms);
                if (airport == null) 
                {
                    airport = airportMap.get(schedRec.depAprtIcao);
                }
                
            	if (airport != null) 
                {
            		airport.addToWindowedDepartures(1);
            	}
        	}
       
        	if (schedRec.runwayOnTime != null && 
                !wndStartTime.after(schedRec.runwayOnTime) && 
                !wndEndTime.before(schedRec.runwayOnTime)) 
            {
                ForecastTripDistAirportData airport = airportMap.get(schedRec.arrAprtEtms);
                if (airport == null) 
                {
                    airport = airportMap.get(schedRec.arrAprtIcao);
                }
                
            	if (airport != null) 
                {
            		airport.addToWindowedArrivals(1);
            	}
        	}
        }
	}   
    
    private int getVfrCountToAdd(ForecastTripDistAirportData airport)  
    {
		VFRHelicopterMap helicopterMap = this.vfrSchedRecCreator.getHelicopterMap();
    	
        int numVfrToAdd = airport.getWindowedVfrCountToAdd();
        if (0 < numVfrToAdd && 
            helicopterMap != null) 
        {
            numVfrToAdd = helicopterMap.getNumberVfrOperations(
                airport, 
                numVfrToAdd);
        }
        
        return numVfrToAdd;
    }
    
	public List<ScheduleRecord> getFlights() 
    {
		return schedRecList;
	}    
}
