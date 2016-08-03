package gov.faa.ang.swac.scheduler.forecast.airport_data;

import gov.faa.ang.swac.scheduler.forecast.trip_distribution.ForecastTripDistAirportDataCount;

import java.util.HashMap;
import java.util.List;

/**
 * A Class that maps the year to another map, this one between the airport code
 * and the TAF data.
 * 
 * @author James Bonn
 * @version 1.0
 */
public class ForecastTafData
{
    private final HashMap<Integer, ForecastAirportCountsMap> tafByYear = new HashMap<Integer, ForecastAirportCountsMap>();
    
    public final static boolean ADD_TO_DEFAULT = true;

    public ForecastTafData(List<ForecastAirportCountsRecordTaf> recordSet, int baseFiscalYear, int forecastFiscalYear)
    {
    	for (ForecastAirportCountsRecord rec : recordSet)
    	{
    		int year = rec.getYear();
    		if (year == baseFiscalYear || year == forecastFiscalYear)
			{
				this.addYearData(year, rec.airportName, rec.count, ForecastTafData.ADD_TO_DEFAULT);
			}
    	}
    }

    /**
     * Find the airport to count data mapping given the input year.
     * @param year
     * @return a {@link ForecastAirportCountsMap} from airport code to TAF data count
     */
    public ForecastAirportCountsMap getYearData(Integer year)
    {
        return tafByYear.get(year);
    }

    /**
     * Add a new year of TAF data.
     * @param year
     * @param tafDataMap
     */
    public void setYearData(Integer year, ForecastAirportCountsMap tafDataMap)
    {
        tafByYear.put(year, tafDataMap);
    }

    /**
     * Given a year, airport code, and 
     * {@link ForecastTripDistAirportDataCount} count data,
     * add the data to the map.
     * @param year
     * @param aprtCode
     * @param aprtCount
     * @param addToDefault if true, the default counts for the given
     * year's map are incremented by the input counts
     */
    public void addYearData(
        Integer year, 
        String aprtCode, 
        ForecastTripDistAirportDataCount aprtCount, 
        boolean addToDefault)
    {
        ForecastAirportCountsMap map = null;
        if(tafByYear.containsKey(year))
        {
            map = tafByYear.get(year);
        }
        else
        {
            map = new ForecastAirportCountsMap();
            tafByYear.put(year, map);
        }
        
        map.addData(aprtCode, aprtCount);
        
        if(addToDefault)
        {
            ForecastTripDistAirportDataCount defaultData = 
                map.getDefaultData();
            defaultData.addAllData(aprtCount);
        }
    }
    
    public void debugDumpTAF() { 
    	System.out.println("have taf for years: " + tafByYear.keySet());    	
    }
}
