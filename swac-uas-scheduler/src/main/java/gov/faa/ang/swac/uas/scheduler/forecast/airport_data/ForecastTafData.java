package gov.faa.ang.swac.uas.scheduler.forecast.airport_data;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import gov.faa.ang.swac.uas.scheduler.airport_data.AirportData;
import gov.faa.ang.swac.uas.scheduler.airport_data.AirportDataMap;
import gov.faa.ang.swac.uas.scheduler.airport_data.AirportDataPair;
import gov.faa.ang.swac.uas.scheduler.forecast.MissionAirportPairKey;
import gov.faa.ang.swac.uas.scheduler.forecast.trip_distribution.ForecastTripDistAirportDataCount.MissionType;

/**
 * Static helper that filters forecast counts by year and quarter and builds a map between airport pair and mission, and forecast counts
 * 
 * @author James Bonn
 * @version 1.0
 */
public class ForecastTafData
{
    public static Map<MissionAirportPairKey,Integer> forecastTafData(List<ForecastAirportCountsRecord> recordSet, int forecastFiscalYear, String quarter, AirportDataMap airportData)
    {
    	Map<MissionAirportPairKey,Integer> map = new LinkedHashMap<MissionAirportPairKey,Integer>();
    	for (ForecastAirportCountsRecord rec : recordSet)
    	{
    		if (rec.year == forecastFiscalYear && quarter.equals(rec.quarter))
			{
    			for (MissionType mission : MissionType.values()) {
    				AirportData departure = airportData.getAirport(rec.departure);
    				AirportData arrival = airportData.getAirport(rec.arrival);
    				AirportDataPair pair = new AirportDataPair(departure, arrival);
    				MissionAirportPairKey key = new MissionAirportPairKey(mission, pair);
    				map.put(key, rec.count.getCount(mission));
    			}
			}
    	}
    	return map;
    }
}
