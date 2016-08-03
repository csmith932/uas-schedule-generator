package gov.faa.ang.swac.scheduler.forecast.clone;

import gov.faa.ang.swac.common.flightmodeling.ScheduleRecord;
import gov.faa.ang.swac.scheduler.flight_data.ScheduleRecordCloner;
import gov.faa.ang.swac.scheduler.forecast.airport_data.ForecastAirportDataPair;
import gov.faa.ang.swac.scheduler.forecast.trip_distribution.ForecastTripDistAirportData;
import gov.faa.ang.swac.scheduler.mathematics.statistics.HQRandom;
import java.util.*;

/**
 * A Class that carries out the flight cloning process.  Flight cloning
 * may also remove flights on city pairs if the projected total is less
 * than the original total.
 * 
 * @author James Bonn
 * @version 1.0
 */
public class ForecastCloner
{
	private static final int maxClonesPerPair = 99;
    
    private HQRandom hqRandomFC = null;
	private ScheduleRecordCloner schedRecCloner = null;

    private List<ScheduleRecord> clonedFlights;
    private HashMap<Integer,ScheduleRecord> removedFlights;

	private double timeShiftStdDevMins;
	
    public ForecastCloner(HQRandom generator, ScheduleRecordCloner demandCloner)
    {
    	this.hqRandomFC     = generator;
    	this.schedRecCloner = demandCloner;
        
        this.clonedFlights  = new ArrayList<ScheduleRecord>();
        this.removedFlights = new LinkedHashMap<Integer,ScheduleRecord>();
    }

    public List<ScheduleRecord> getClonedFlights()
    {
        return this.clonedFlights;
    }

    public int getClonedFlightCount() 
    {
    	return getClonedFlights().size();
    }
    
    public HashMap<Integer,ScheduleRecord> getRemovedFlights()
    {
        return this.removedFlights;
    }
    
    public void setCloneTimeShiftStDev(double timeMins)
    {
    	this.timeShiftStdDevMins = timeMins;
    }    
      
    public void cloneFlights(
        List<ForecastTripDistAirportData> airportList)
    {
        // Clone all flights involving the airports in the given list
        
        for(ForecastTripDistAirportData airport : airportList)
        {
            // We only need to use either the "coming from" list
            // or the "going to" list to capture all the flights.
            // We arbitrarily choose the "coming from" list.
            cloneFlights(airport.getComingFrom());
        }
    }

    public void cloneFlights(
        Collection<ForecastAirportDataPair> pairs)
    {
        // Clone all flights between the given O-D pairs
        for (ForecastAirportDataPair pair : pairs)
        {
            cloneFlights(pair); 
        }
    }
    
    private void cloneFlights(
        ForecastAirportDataPair pair)
    {
    	// Clone all flights between the given city pair
        
        int nBaseFlights        = (int)pair.getInitialFlightCount();
        int nForecastFlights    = (int)pair.getProjectedFlightCountFinal();
        
        List<ScheduleRecord> schedRecList = pair.getFlights();

        if (nBaseFlights < nForecastFlights)
        {
            // need to clone some
            int totalClones = (nForecastFlights - nBaseFlights);
            if (maxClonesPerPair < totalClones)
            {
                totalClones = maxClonesPerPair;
            }
            
            // Get what the clone count to make for each base flight
            int [] nClonesToMake = chooseIndices(totalClones,nBaseFlights);
            
            for(int iBaseRec = 0; iBaseRec < nBaseFlights; ++iBaseRec)
            {
                if (0 < nClonesToMake[iBaseRec])
                {
                    ScheduleRecord schedRec = schedRecList.get(iBaseRec);
                    
                    this.clonedFlights.addAll(
                        this.schedRecCloner.cloneScheduleRecord(
                            schedRec, 
                            nClonesToMake[iBaseRec], 
                            this.timeShiftStdDevMins));
                }
            }
        }
        
        if (nForecastFlights < nBaseFlights)
        {
            // need to remove some
            int totalToRemove = nBaseFlights - nForecastFlights;
            
            int[] nRecsToRemove = chooseIndices(totalToRemove,nBaseFlights);
            
            for(int iBaseRec = 0; iBaseRec < nRecsToRemove.length; ++iBaseRec)
            {
                if (0 < nRecsToRemove[iBaseRec])
                {
                    // remove this flight
                    ScheduleRecord schedRec = schedRecList.get(iBaseRec);
                    this.removedFlights.put(
                        schedRec.idNum, 
                        schedRec);
                }
            }
        }
    }

    /**
     * @return a list of {@link ScheduleRecord} flights that are created clones
     */
    private int [] chooseIndices(int howManyToChoose, int howManyToChooseFrom)
    {
        // Choose n from m (first with and then without replacement).
        // n may be greater than m
        // where:
        // n = # to draw, select or choose,
        // m = # of possible outcomes from each choice.
        
        int [] resultArray = new int[howManyToChooseFrom];
        
        // First, make sure all are selected at least n/m times.
        // This is effectively selection WITH replacement.
        Arrays.fill(resultArray,howManyToChoose/howManyToChooseFrom);

        // Randomly distribute the remaining clones WITHOUT replacement
        int nLeftToSelect = howManyToChoose%howManyToChooseFrom;    
        
        for(int iBase = 0; 
            iBase<howManyToChooseFrom && 0<nLeftToSelect; 
            ++iBase)
        {
            int remainingSpots = howManyToChooseFrom-iBase;
            
            if (remainingSpots <= nLeftToSelect ||
                remainingSpots*hqRandomFC.nextDouble() < nLeftToSelect)
            {
                ++resultArray[iBase];
                --nLeftToSelect;
            }
        }
        // REFERENCE: 
        // http://eyalsch.wordpress.com/2010/04/01/random-sample/
        // Section. Full Scan.
        
        
        // THIS METHOD JUST LOOKS INCORRECT
        // For each remaining selection,select WITHOUT replecement.
        /*for(int i = 0; i<remainder; ++i)
        {
            // Randomly choose a starting index
            int randInd = 1 + (int)Math.floor(
                (howManyToChooseFrom - i) * hqRandomFC.nextDouble()); 
            int uniqInd = 0;
            while (0<randInd && uniqInd<howManyToChooseFrom)
            {
                if (resultArray[uniqInd] == howManyPer)
                {
                    --randInd;
                }
                
                if (randInd == 0)
                {
                    ++resultArray[uniqInd];
                }
                
                ++uniqInd;
            }
        }*/

        return resultArray;
    }
    
    public boolean hasRemoved(ScheduleRecord schedRec)
    {
        return this.removedFlights.containsKey(schedRec.idNum);
    }
    
    public void clearFlightLists() 
    {
    	this.clonedFlights.clear();
    	this.removedFlights.clear();
    }    
}
