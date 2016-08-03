package gov.faa.ang.swac.uas.scheduler.forecast.airport_data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gov.faa.ang.swac.common.datatypes.REGION;
import gov.faa.ang.swac.datalayer.storage.fileio.HeaderUtils;
import gov.faa.ang.swac.datalayer.storage.fileio.TextSerializable;
import gov.faa.ang.swac.datalayer.storage.fileio.WithHeader;
import gov.faa.ang.swac.uas.scheduler.airport_data.AirportData;
import gov.faa.ang.swac.uas.scheduler.forecast.trip_distribution.ForecastTripDistAirportData;

public class CountryRegionHash
{	
	private final Map<String, REGION> countryRegionHash = new HashMap<String, REGION>();
	private final Map<Integer, REGION> countryCodeRegionHash = new HashMap<Integer, REGION>();
    
	public CountryRegionHash(List<CountryRegionRecord> recordSet)
	{
		for (CountryRegionRecord rec : recordSet)
		{
			countryRegionHash.put(rec.countryRegionKey, rec.region);
			countryCodeRegionHash.put(rec.countryCode, rec.region);
		}
	}
	
	public REGION getRegion(Integer countryCode)
	{
		return countryCodeRegionHash.get(countryCode);
	}
	
	/**
     * Given a {@link ForecastTripDistAirportData} airport, find the 
     * number of flights associated with the region it lies in.
     * @param aprt
     * @return the number of flights in the region that the input airport is in.
     */
 
    public REGION getRegion(AirportData aprt)
    {
        String aprtKey = createKey(aprt);
        return countryRegionHash.get(aprtKey);
    }

    private static String createKey(AirportData aprt)
    {
        return aprt.getCountry() + "_" + aprt.getCountryCode();
    }

    public static class CountryRegionRecord implements TextSerializable, WithHeader
    {
    	public String countryRegionKey;
    	public REGION region;
    	public Integer countryCode;
    	
		@Override
		public void readItem(BufferedReader reader) throws IOException
		{
			String currentLine = reader.readLine();
			String[] values = currentLine.split(";");
			this.countryCode = new Integer(values[0]);
			this.countryRegionKey = createCountryRegionKey(values[0], values[1]);
			this.region = REGION.valueOf(values[2]);
		}
		
		private static String createCountryRegionKey(String cntryCode, String country)
	    {
	        return country + "_" + cntryCode;
	    }

		@Override
		public void writeItem(PrintWriter writer) throws IOException
		{
			writer.println(this.toString());
		}
    	
		@Override
		public long readHeader(BufferedReader reader) throws IOException
		{
			HeaderUtils.readHeaderHashComment(reader);
			return -1;
		}

		@Override
		public void writeHeader(PrintWriter writer, long numRecords) throws IOException
		{
			throw new UnsupportedOperationException();
		}
    }
}
