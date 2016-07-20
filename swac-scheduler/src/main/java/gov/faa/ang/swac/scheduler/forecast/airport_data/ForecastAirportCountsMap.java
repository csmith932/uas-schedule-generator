package gov.faa.ang.swac.scheduler.forecast.airport_data;

import gov.faa.ang.swac.scheduler.forecast.trip_distribution.ForecastTripDistAirportData;
import gov.faa.ang.swac.scheduler.forecast.trip_distribution.ForecastTripDistAirportDataCount;

import java.util.HashMap;
import java.util.List;

/**
 * A Class that creates a map between an airport code and a
 * {@link ForecastTripDistAirportDataCount} object.  For example,
 * this could be used to read in all of the baseline day OPSNET counts.
 * These counts can then be merged into the list of airports that we are
 * concerned with.
 * 
 * @author James bonn
 * @version 1.0
 */
public class ForecastAirportCountsMap
{
    private final HashMap<String, ForecastTripDistAirportDataCount> data = new HashMap<String, ForecastTripDistAirportDataCount>();
    private final ForecastTripDistAirportDataCount defaultData = new ForecastTripDistAirportDataCount();
    
    public ForecastAirportCountsMap() {}
    
    public ForecastAirportCountsMap(List<ForecastAirportCountsRecord> recordSet, String yyyymmdd)
    {
    	for (ForecastAirportCountsRecord rec : recordSet)
    	{
    		if (rec.yyyymmdd.equals(yyyymmdd))
    		{
    			this.data.put(rec.airportName, rec.count);
    		}
    	}
    }
    
    /**
     * Add an airport code to 
     * {@link ForecastTripDistAirportDataCount} 
     * counts data map link.
     * @param aprtName
     * @param counts
     */
    public void addData(String aprtName, ForecastTripDistAirportDataCount counts)
    {
        data.put(aprtName, counts);
    }

    /**
     * Add an {@link ForecastTripDistAirportData} airport to 
     * {@link ForecastTripDistAirportDataCount} counts data map link.
     * @param aprt
     * @param counts
     */
    public void addData(ForecastTripDistAirportData aprt, 
        ForecastTripDistAirportDataCount counts)
    {
        addData(aprt.getMostLikelyCode(), counts);
    }

    /**
     * Given the individual category counts and the airport code, 
     * add in the mapping link.
     * @param aprtName
     * @param countGA
     * @param countMil
     * @param countOther
     */
    public void addData(String aprtName, double countGA, double countMil,
        double countOther)
    {
        addData(aprtName, new ForecastTripDistAirportDataCount(
                              countGA, countMil, countOther));
    }

    /**
     * Given an airport name, find the associated counts.
     * @param aprtName
     * @return {@link ForecastTripDistAirportDataCount} count data for the
     * given airport
     */
    public ForecastTripDistAirportDataCount getCounts(String aprtName)
    {
        return data.get(aprtName);
    }

    /**
     * Given an {@link ForecastTripDistAirportData} airport, find 
     * the associated counts.  Does not return a default value if
     * the airport has not been mapped to a counts object.
     * @param aprt
     * @return {@link ForecastTripDistAirportDataCount} count data for the
     * given airport
     */
    public ForecastTripDistAirportDataCount getCounts(
        ForecastTripDistAirportData aprt)
    {
        return getCounts(aprt, false);
    }

    /**
     * Given an {@link ForecastTripDistAirportData} airport, find 
     * the associated counts.
     * @param aprt
     * @param useDefault If true, and the airport has no mapping associated,
     * use the default count value
     * @return {@link ForecastTripDistAirportDataCount} count data for the
     * given airport
     */
    public ForecastTripDistAirportDataCount getCounts(
        ForecastTripDistAirportData aprt, boolean useDefault)
    {
        ForecastTripDistAirportDataCount counts = null;
    
        counts = data.get(aprt.getIcaoFaaCode());
        
        if(counts == null)
        {
            counts = data.get(aprt.getFaaIcaoCode());
        }
        
        if(counts == null && useDefault)
        {
            counts = defaultData;
        }
        
        return counts;
    }

    /**
     * @return the default count data for this map
     */
    public ForecastTripDistAirportDataCount getDefaultData()
    {
        return defaultData;
    }

    /**
     * @return true if there is at least one mapping (not including a
     * default value)
     */
    public boolean hasData()
    {
        boolean hasData = false;
        if(data.size() > 0)
        {
            hasData = true;
        }
        
        return hasData;
    }
}
