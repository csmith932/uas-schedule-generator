package gov.faa.ang.swac.uas.scheduler.forecast.trip_distribution;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class ForecastTripDistAirportRemover
{
    // do not instantiate
    private ForecastTripDistAirportRemover()
    {
    }

    public static List<ForecastTripDistAirportData> 
        removeSinksAndSources(List<ForecastTripDistAirportData> airportList)
    {
        // Construct a list of removed airports
        List<ForecastTripDistAirportData> removedAirportList = 
            new ArrayList<ForecastTripDistAirportData>();
        
        // Search for more sinks or sources
        boolean searchForSinkOrSource = true;     
        while (searchForSinkOrSource && !airportList.isEmpty())
        {
            boolean foundSinkOrSource = false;
            
            // Iterate over all airports to find and remove sinks & sources
            ListIterator<ForecastTripDistAirportData> iter =
                airportList.listIterator();
            while(iter.hasNext())
            {
                ForecastTripDistAirportData airportData = iter.next();
                if(airportData.isSourceOrSink())
                {
                    foundSinkOrSource = true;
                    airportData.removeConnectedPairs();
                    removedAirportList.add(airportData);
                    iter.remove();
                }
            }
            
            if (!foundSinkOrSource)
            {
                searchForSinkOrSource = false;
            }
        }
        
        return removedAirportList;
    }
}
