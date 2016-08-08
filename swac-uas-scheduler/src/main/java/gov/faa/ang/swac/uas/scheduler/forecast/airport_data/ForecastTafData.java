package gov.faa.ang.swac.uas.scheduler.forecast.airport_data;

import java.util.HashMap;
import java.util.List;

import gov.faa.ang.swac.uas.scheduler.forecast.trip_distribution.ForecastTripDistAirportDataCount;

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

    public ForecastTafData(List<ForecastAirportCountsRecord> recordSet, int baseFiscalYear, int forecastFiscalYear)
    {
    	for (ForecastAirportCountsRecord rec : recordSet)
    	{
    		int year = rec.getYear();
    		if (year == baseFiscalYear || year == forecastFiscalYear)
			{
				this.addYearData(year, rec.airportName, rec.count);
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
     * Given a year, airport code, and 
     * {@link ForecastTripDistAirportDataCount} count data,
     * add the data to the map.
     * @param year
     * @param aprtCode
     * @param aprtCount
     * @param addToDefault if true, the default counts for the given
     * year's map are incremented by the input counts
     */
    private void addYearData(
        Integer year, 
        String aprtCode, 
        ForecastTripDistAirportDataCount aprtCount)
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
    }
}
