package gov.faa.ang.swac.uas.scheduler.forecast.trip_distribution;

import gov.faa.ang.swac.common.datatypes.Timestamp;
import gov.faa.ang.swac.common.flightmodeling.ScheduleRecord;
import gov.faa.ang.swac.uas.scheduler.airport_data.AirportData;
import gov.faa.ang.swac.uas.scheduler.airport_data.AirportDataMap;
import gov.faa.ang.swac.uas.scheduler.airport_data.AirportDataPair;
import gov.faa.ang.swac.uas.scheduler.forecast.MissionAirportPairKey;
import gov.faa.ang.swac.uas.scheduler.forecast.trip_distribution.ForecastTripDistAirportDataCount.MissionType;

import java.io.IOException;
import java.util.*;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * A Class that reads the baseline set of flights from a Forecast Demand formatted file. While
 * reading in the flights, a list of airports is created and
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
    /**
     * Given an input file, read through all of the flights, create a list of airports and assign
     * the flights to them.
     * 
     * @param input
     * @return the number of flights read in and assigned to airports
     * @throws IOException
     */
    public static Map<MissionAirportPairKey,List<ScheduleRecord>> load(
        List<ScheduleRecord> schedRecList,
        AirportDataMap airportDataMap,
        Timestamp startTime, 
        Timestamp endTime)
    {
    	Map<MissionAirportPairKey,List<ScheduleRecord>> map = new LinkedHashMap<MissionAirportPairKey,List<ScheduleRecord>>();
        
        if (startTime.before(endTime))
        {
            for (ScheduleRecord schedRec : schedRecList)
            {
                AirportDataPair pair = assignFlightToAirports(
                    schedRec, 
                    airportDataMap,
                    startTime, 
                    endTime);
                if (pair != null) {
                	MissionType mission = MissionType.fromUserClass(schedRec.atoUserClass);
        			MissionAirportPairKey key = new MissionAirportPairKey(mission, pair);
        			List<ScheduleRecord> schedule = map.get(key);
        			if (schedule == null) {
        				schedule = new ArrayList<ScheduleRecord>();
        				map.put(key, schedule);
        			}
        			schedule.add(schedRec);
                }
            }
        }
        else
        {
            logger.debug("end time " + endTime + " PRECEEDS start time " + startTime);   
        }
        
        return map;
    }

    private static AirportDataPair assignFlightToAirports(
        ScheduleRecord schedRec,
        AirportDataMap airportDataMap,
        Timestamp startTime,
        Timestamp endTime)
    {
        // 0. If data is null, there is no assignment
        if (schedRec == null ||
            schedRec.runwayOffTime == null ||
            schedRec.runwayOnTime == null)
        {
            return null;
        }  
        
        // 1. Departure must precedpda arrival
        if (!schedRec.runwayOffTime.before(schedRec.runwayOnTime))
        {
            return null;   
        }
        
        // 2. Update flights completely outside of the window
        if (endTime.before(schedRec.runwayOffTime) || 
            schedRec.runwayOnTime.before(startTime))
        {
            // Our base schedule is for a different date. To add resilience, we update the date to coincide with the modeled base date
        	Timestamp oldRunwayOffTime = schedRec.runwayOffTime;
        	Timestamp oldRunwayOnTime = schedRec.runwayOnTime;
        	long timeOfDay = oldRunwayOffTime.milliDifference(oldRunwayOffTime.truncateToDay());
        	long deltaT = oldRunwayOnTime.milliDifference(oldRunwayOffTime);
        	Timestamp startDay = startTime.truncateToDay();
        	schedRec.runwayOffTime = startDay.milliAdd(timeOfDay);
        	schedRec.runwayOnTime = schedRec.runwayOffTime.milliAdd(deltaT);
        }
        
        // 3. No flight is longer than 24 hours; specifically,
        // No flight can depart before the start and land after the end time
        if (schedRec.runwayOffTime.before(startTime) &&    
            endTime.before(schedRec.runwayOnTime))
        {
            return null;   
        }

        // Get the origin airport
        AirportData originAirport = airportDataMap.getAirport(schedRec.depAprtEtms);
        if (originAirport == null)
        {
            originAirport = airportDataMap.getAirport(schedRec.depAprtIcao);
            if (originAirport == null)
            {
                return null;
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
                return null;
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

        return new AirportDataPair(originAirport, destinAirport);
    }
}
