package gov.faa.ang.swac.scheduler.forecast;

import gov.faa.ang.swac.common.flightmodeling.ScheduleRecord;
import gov.faa.ang.swac.scheduler.airport_data.AirportData;
import gov.faa.ang.swac.scheduler.forecast.airport_data.ForecastAirportDataPair;
import gov.faa.ang.swac.scheduler.forecast.trip_distribution.ForecastTripDistAirportData;

import java.util.*;

public class UserClassDataSplitter 
{
	public enum UserClass 
    {
		GA, 
        MIL, 
        OTHER;
        
        private static UserClass getValue(String str)
        {
            if (str.equals("G"))
            {
                return GA;
            }
            
            if (str.equals("M"))
            {
                return MIL;
            }
           
            return OTHER;
        }       

        
	}
	
	private List<Map<String,ForecastTripDistAirportData>> airports;
	
	UserClassDataSplitter()
    {
		airports = new ArrayList<Map<String,ForecastTripDistAirportData>>();
		airports.add(new LinkedHashMap<String,ForecastTripDistAirportData>());
		airports.add(new LinkedHashMap<String,ForecastTripDistAirportData>());
		airports.add(new LinkedHashMap<String,ForecastTripDistAirportData>());
    }

   	private ForecastTripDistAirportData getAirport(AirportData airport, int idx) 
    {
        String key = airport.getMostLikelyCode();

        ForecastTripDistAirportData newAirport = airports.get(idx).get(key);
        if (newAirport == null) 
        {
            newAirport = new ForecastTripDistAirportData(airport);
            airports.get(idx).put(key,newAirport);
        }

        return newAirport;
    } 
    
   	private List<ForecastTripDistAirportData> getAirportList(int idx) 
    {
        List<ForecastTripDistAirportData> retval = 
            new ArrayList<ForecastTripDistAirportData>(airports.size());
        retval.addAll(airports.get(idx).values());
        return retval;
    } 
    
    private void clear(int idx)
    {
        airports.get(idx).clear();
    }
    

	public void split(List<ForecastTripDistAirportData> airportList) 
    {	
        // Loop over each airport
		for (ForecastTripDistAirportData airport : airportList) 
        {
            // Loop over each flight originating from here.
            // This way, all flights are included.
			for (ForecastAirportDataPair pair : airport.getGoingTo()) 
            {
                // Loop over each flight between this O-D pair
				for (ScheduleRecord schedRec : pair.getFlights()) 
                {
                    // Find or construct networked airport data pair
                    // & add this flight to the appropriate subnetwork
                    
                    UserClass userClass = UserClass.getValue(schedRec.userClass);
                    
                    ForecastTripDistAirportData originCopy = getAirport(pair.getOrigin(), userClass.ordinal());
                    ForecastTripDistAirportData destinCopy = getAirport(pair.getDestination(), userClass.ordinal());   
                    
					originCopy.addDepartureGoingTo(destinCopy,schedRec);
				    destinCopy.addArrivalComingFrom(originCopy,schedRec);
				}
			}
		}
    }
    
	public List<ForecastTripDistAirportData> getAirportList(UserClass userClass) 
    {
		return getAirportList(userClass.ordinal());
	}

	public void clear() 
    {
        for (UserClass userClass : UserClass.values())
        {
            clear(userClass.ordinal());
        }
	}
}
