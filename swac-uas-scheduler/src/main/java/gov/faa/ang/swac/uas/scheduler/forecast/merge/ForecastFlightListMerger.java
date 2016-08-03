package gov.faa.ang.swac.uas.scheduler.forecast.merge;

import gov.faa.ang.swac.common.flightmodeling.ScheduleRecord;
import gov.faa.ang.swac.uas.scheduler.forecast.airport_data.ForecastAirportDataPair;
import gov.faa.ang.swac.uas.scheduler.forecast.clone.ForecastCloner;
import gov.faa.ang.swac.uas.scheduler.forecast.trip_distribution.ForecastTripDistAirportData;

import java.util.ArrayList;
import java.util.List;

/**
 * A Class that merges the different flight lists together into one list.
 * 
 * @author James Bonn
 * @version 1.0
 */
public class ForecastFlightListMerger
{
    private static String removedAprtSourceType = "ORIGINAL_EXTRA_CP";
    
    private ForecastFlightListMerger()
    {
    }

    /**
     * Merged all of the different lists of flights into the final flight
     * list for the Forecast process.
     * @param aprts list of airports in the closed system
     * @param cloner has the list of clone flights to add and the list of
     * flights needing to be removed because of the trip distribution model
     * @param removedAprts list of airports not in the closed system
     * @param vfrFlights list of VFR flights created to match OPSNET totals
     * @return a new list of {@link DemandFlight}
     */
    public static List<ScheduleRecord> merge(
        List<ForecastTripDistAirportData> airportList,
        ForecastCloner cloner,        
        List<ForecastTripDistAirportData> removedAirportList,        
        List<ScheduleRecord> vfrSchedRecList)
    {
        List<ScheduleRecord> results = new ArrayList<ScheduleRecord>();
        
        results.addAll(getFlightsByAirport(airportList,cloner));
        results.addAll(getFlightsFromRemovedAirports(removedAirportList));
        results.addAll(vfrSchedRecList);
        
        return results;
    }
    
    public static List<ScheduleRecord> merge(
        List<ForecastTripDistAirportData> airportList,
        ForecastCloner cloner)
    {
        List<ScheduleRecord> results = new ArrayList<ScheduleRecord>();
        
        results.addAll(getFlightsByAirport(airportList,cloner));
        
        return results;
    }
    
    private static List<ScheduleRecord> getFlightsByAirport(
        List<ForecastTripDistAirportData> airportList,
        ForecastCloner cloner)
    {
        ArrayList<ScheduleRecord> results = new ArrayList<ScheduleRecord>();
        
        for(ForecastTripDistAirportData airport : airportList)
        {
            for(ForecastAirportDataPair pair : airport.getComingFrom())
            {
                for(ScheduleRecord schedRec : pair.getFlights())
                {
                    if (!cloner.hasRemoved(schedRec))
                    {
                        results.add(schedRec);
                    }
                }
            }
        }
        
        results.addAll(cloner.getClonedFlights());
        
        return results;
    }
    
    private static List<ScheduleRecord> getFlightsFromRemovedAirports(
        List<ForecastTripDistAirportData> airportList)
    {
        ArrayList<ScheduleRecord> results = new ArrayList<ScheduleRecord>();
        
        for(ForecastTripDistAirportData airport : airportList)
        {
            for(ForecastAirportDataPair cp : airport.getComingFrom())
            {
                results.addAll(cp.getFlights());
            }
            
            for(ForecastAirportDataPair cp : airport.getGoingTo())
            {
                results.addAll(cp.getFlights());
            }
        }
        
        for(ScheduleRecord schedRec : results)
        {
            schedRec.flightPlanType = removedAprtSourceType;
        }
        
        return results;
    }

    /*public static void setRemovedAprtSourceType(String str)
    {
        removedAprtSourceType = str;
    }

    public static String getRemovedAprtSourceType()
    {
        return removedAprtSourceType;
    }*/
}
