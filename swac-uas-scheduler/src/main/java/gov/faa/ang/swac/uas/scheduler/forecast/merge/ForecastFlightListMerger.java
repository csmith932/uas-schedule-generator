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
    private ForecastFlightListMerger()
    {
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
    
}
