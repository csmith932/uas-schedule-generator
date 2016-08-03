package gov.faa.ang.swac.scheduler.forecast.airport_data;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import gov.faa.ang.swac.common.datatypes.REGION;
import gov.faa.ang.swac.datalayer.storage.fileio.HeaderUtils;
import gov.faa.ang.swac.datalayer.storage.fileio.TextSerializable;
import gov.faa.ang.swac.datalayer.storage.fileio.WithHeader;
import gov.faa.ang.swac.scheduler.forecast.trip_distribution.ForecastTripDistAirportData;

/**
 * A Class that links international airport codes to growth rates.  It does
 * this in two steps.  First, there is a static map between international
 * countries and the region in the world that fits the APO International 
 * forecast.  Next, there is a map from the region to a total number of flights.
 * One instance of this class can be for one specific year, for example.
 * 
 * @author James Bonn, Casey Smith
 * @version 2.0
 */
public class ForecastInternationalAirportData implements TextSerializable, WithHeader
{
	private int year;
    private Double atlantic;
    private Double pacific;
    private Double latin_america;
    private Double canada;
    
    public int getYear()
    {
    	return this.year;
    }
    
    /**
     * Given a {@link ForecastTripDistAirportData} airport, find the 
     * number of flights associated with the region it lies in.
     * @param aprt
     * @return the number of flights in the region that the input airport is in.
     */
    public Double getRegionCount(REGION region)
    {
        if (region == null) { return null; }
        switch (region)
        {
        	case ATLANTIC:
        		return this.atlantic;
        	case PACIFIC:
        		return this.pacific;
        	case LATIN_AMERICA:
        		return this.latin_america;
        	case CANADA:
        		return this.canada;
        	default:
        		return null;
        }
    }

	@Override
	public void readItem(BufferedReader reader) throws IOException
	{
		String currentLine = reader.readLine();
		String[] values = currentLine.split(",");

		year = Integer.parseInt(values[0]);
	
		atlantic = Double.parseDouble(values[1]);
		pacific = Double.parseDouble(values[2]);
		latin_america = Double.parseDouble(values[3]);
		canada = Double.parseDouble(values[4]);
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
