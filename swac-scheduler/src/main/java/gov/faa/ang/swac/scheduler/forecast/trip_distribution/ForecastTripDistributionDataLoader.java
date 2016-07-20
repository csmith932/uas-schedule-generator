package gov.faa.ang.swac.scheduler.forecast.trip_distribution;

import gov.faa.ang.swac.common.datatypes.Timestamp;
import gov.faa.ang.swac.common.flightmodeling.ScheduleRecord;
import gov.faa.ang.swac.scheduler.airport_data.AirportData;
import gov.faa.ang.swac.scheduler.airport_data.AirportDataMap;
import java.io.IOException;
import java.util.*;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * A Class that reads the baseline set of flights from a Forecast Demand formatted file. While
 * reading in the flights, a list of {@link ForecastTripDistAirportData} airports is created and
 * those airports have the flights assigned to them. Also, while assigning flights, the airports are
 * also getting the citypairs assigned.
 * 
 * @author James Bonn
 * @version 1.0
 */
public class ForecastTripDistributionDataLoader
{
    private static Logger logger = 
        LogManager.getLogger(ForecastTripDistributionDataLoader.class);

    private static final int DEFAULT_ALL_AIRPORTS_SIZE = 2500;

    /**
     * Given an input file, read through all of the flights, create a list of airports and assign
     * the flights to them.
     * 
     * @param input
     * @return the number of flights read in and assigned to airports
     * @throws IOException
     */
    public static List<ForecastTripDistAirportData> load(
        List<ScheduleRecord> schedRecList,
        AirportDataMap airportDataMap,
        Timestamp startTime, 
        Timestamp endTime)
    {
        Map<String, ForecastTripDistAirportData> tripMap =
            new LinkedHashMap<String, ForecastTripDistAirportData>(
                DEFAULT_ALL_AIRPORTS_SIZE);
        
        int nAssignedFlights = 0;
        
        if (startTime.before(endTime))
        {
            for (ScheduleRecord schedRec : schedRecList)
            {
                boolean assigned = assignFlightToAirports(
                    schedRec, 
                    airportDataMap,
                    startTime, 
                    endTime,
                    tripMap);

                if (assigned)
                {
                    ++nAssignedFlights;
                }
            }
        }
        else
        {
            logger.debug("end time " + endTime + " PRECEEDS start time " + startTime);   
        }
        
        logger.debug("loaded " + nAssignedFlights + " flights");

        return new ArrayList<ForecastTripDistAirportData>(tripMap.values());
    }

    private static boolean assignFlightToAirports(
        ScheduleRecord schedRec,
        AirportDataMap airportDataMap,
        Timestamp startTime,
        Timestamp endTime,       
        Map<String,ForecastTripDistAirportData> flightMap)
    {
        // 0. If data is null, there is no assignment
        if (schedRec == null ||
            schedRec.runwayOffTime == null ||
            schedRec.runwayOnTime == null)
        {
            return false;
        }  
        
        // 1. Departure must preceed arrival
        if (!schedRec.runwayOffTime.before(schedRec.runwayOnTime))
        {
            return false;   
        }
        
        // 2. Reject flights completely outside of the window
        if (endTime.before(schedRec.runwayOffTime) || 
            schedRec.runwayOnTime.before(startTime))
        {
            return false;   
        }
        
        // 3. No flight is longer than 24 hours; specifically,
        // No flight can depart before the start and land after the end time
        if (schedRec.runwayOffTime.before(startTime) &&    
            endTime.before(schedRec.runwayOnTime))
        {
            return false;   
        }

        // Get the origin airport
        AirportData originAirport = airportDataMap.getAirport(schedRec.depAprtEtms);
        if (originAirport == null)
        {
            originAirport = airportDataMap.getAirport(schedRec.depAprtIcao);
            if (originAirport == null)
            {
                return false;
            }     
        }
        
        // 4. No flight landing after the end time originating from foreign airport 
        // Commented out on FAA request, as its filtering oceanic flights that they
        // don't want filtered.
//        if (endTime.before(schedRec.runwayOnTime) &&
//            originAirport.isForeign())
//        {
//            return false;
//        }
        
        // Get the destination airport
        AirportData destinAirport = airportDataMap.getAirport(schedRec.arrAprtEtms);
        if (destinAirport == null)
        {
            destinAirport = airportDataMap.getAirport(schedRec.arrAprtIcao);
            if (destinAirport == null)
            {
                return false;
            }
        }
        
        // 5. No flights takeoff before the start time destined for foreign airport
        // Commented out on FAA request, as its filtering oceanic flights that they
        // don't want filtered.
//        if (schedRec.runwayOffTime.before(startTime) && 
//            destinAirport.isForeign())
//        {
//            return false;
//        }

        // Get the map entry for the origin airport
        ForecastTripDistAirportData originAirportData;
        String originAirportId = originAirport.getIcaoFaaCode();
        if (flightMap.containsKey(originAirportId))
        {
            originAirportData = flightMap.get(originAirportId);
        }
        else
        {
            originAirportData = new ForecastTripDistAirportData(originAirport);
            flightMap.put(originAirportId, originAirportData);
        }

        // Get the map entry for the destination airport
        ForecastTripDistAirportData destinAirportData;
        String destinAirportId = destinAirport.getIcaoFaaCode(); 
        if (flightMap.containsKey(destinAirportId))
        {
            destinAirportData = flightMap.get(destinAirportId);
        }
        else
        {
            destinAirportData = new ForecastTripDistAirportData(destinAirport);
            flightMap.put(destinAirportId,destinAirportData);
        }

        // Add the departure to the origin & the arrival to the destination
        originAirportData.addDepartureGoingTo(destinAirportData, schedRec);
        destinAirportData.addArrivalComingFrom(originAirportData, schedRec);

        return true;
    }
}
