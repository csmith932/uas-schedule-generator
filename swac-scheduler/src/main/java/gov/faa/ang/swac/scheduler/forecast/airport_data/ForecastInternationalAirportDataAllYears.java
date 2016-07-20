package gov.faa.ang.swac.scheduler.forecast.airport_data;

import java.util.HashMap;

/**
 * A Class of mappings from the year to a {@link ForecastInternationalAirportData}
 * object.
 * 
 * @author James Bonn
 * @version 1.0
 */
public class ForecastInternationalAirportDataAllYears
{
    HashMap<Integer, ForecastInternationalAirportData> allYears;

    /**
     * Default Constructor.
     */
    public ForecastInternationalAirportDataAllYears()
    {
        allYears = new HashMap<Integer, ForecastInternationalAirportData>();
    }

    /**
     * Get the {@link ForecastInternationalAirportData} object for the given year.
     * @param year
     * @return the {@link ForecastInternationalAirportData} object for the given year
     */
    public ForecastInternationalAirportData getYearData(Integer year)
    {
        return allYears.get(year);
    }

    /**
     * Add a mapping from a year to a {@link ForecastInternationalAirportData} object.
     * @param year
     * @param data
     */
    public void setYearData(Integer year, ForecastInternationalAirportData data)
    {
        allYears.put(year, data);
    }
}
