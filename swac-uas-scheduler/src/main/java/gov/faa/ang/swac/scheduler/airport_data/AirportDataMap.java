package gov.faa.ang.swac.scheduler.airport_data;

import gov.faa.ang.swac.datalayer.storage.fileio.HeaderUtils;
import gov.faa.ang.swac.datalayer.storage.fileio.TextSerializable;
import gov.faa.ang.swac.datalayer.storage.fileio.WithHeader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

/**
 * A mapping class between an airport code (FAA Code or ICAO Code) and
 * an {@link AirportData} object.
 * 
 * @author James Bonn
 * @version 1.0
 */
public class AirportDataMap implements TextSerializable, WithHeader
{
    private HashMap<String, AirportData> map;
    
    /**
     * Default Constructor
     */
    public AirportDataMap()
    {
        map = new HashMap<String, AirportData>();
    }
    
    /**
     * Given an {@link AirportData}, add it to the map.
     * 
     * @param airport
     */
    public void addAirport(AirportData airport)
    {
        if(airport.getFaaCode() != null)
        {
            map.put(airport.getFaaCode(), airport);
        }
        if(airport.getIcaoCode() != null)
        {
            map.put(airport.getIcaoCode(), airport);
        }
    }
    
    /**
     * Given an airport code, FAA or ICAO, find the airport in the map
     * @param code FAA or ICAO code
     * @return {@link AirportData} associated with the input airport code, 
     * may return null if airport cannot be found.
     */
    public AirportData getAirport(String code)
    {
        return map.get(code);
    }
    
    @Override
    public void readItem(BufferedReader reader) throws IOException
    {
    	while (reader.ready())
		{
    		// Verify that this is not the last empty line of the file
    		reader.mark(1024);
    		String str = reader.readLine();
    		if (!str.isEmpty())
    		{
    			reader.reset();
    			AirportData rec = new AirportData();
    			rec.readItem(reader);
    			this.addAirport(rec);
    		}
		}
    }
    
    @Override
    public void writeItem(PrintWriter writer)
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
