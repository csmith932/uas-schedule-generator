package gov.faa.ang.swac.scheduler.vfr;


import gov.faa.ang.swac.datalayer.storage.fileio.TextSerializable;
import gov.faa.ang.swac.scheduler.airport_data.AirportData;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

/**
 * A map that gives the number of helicopter operations at specific airports.
 * 
 * @author James Bonn
 * @version 1.0
 */
public class VFRHelicopterMap implements TextSerializable
{
    private HashMap<String, Double> map;

    /**
     * Default Constructor.
     */
    public VFRHelicopterMap()
    {
        map = new HashMap<String, Double>();
    }

    /**
     * Add an airport to the map.  The map is from the airport code to
     * the percent of VFR operations that are to be converted to helicopter.
     * @param code
     * @param pctVfrHelicopter
     */
    public void addAirportHelicopterData(String code, Double pctVfrHelicopter)
    {
        map.put(code, pctVfrHelicopter);
    }

    /**
     * @param code
     * @return the percent of VFR operations that are to be converted
     * to helicopters at the given airport
     */
    public double getAirportHelicopterData(String code)
    {
        return map.get(code);
    }

    /**
     * @param airport
     * @return the percent of VFR operations that are to be converted
     * to helicopters at the given airport
     */
    public double getAirportHelicopterData(AirportData airport)
    {
        return getAirportHelicopterData(airport.getIcaoFaaCode());
    }

    /**
     * @param code
     * @param startingVfrOps
     * @return the number of VFR operations that are to be converted
     * to helicopters
     */
    public int getNumberVfrOperations(String code, int startingVfrOps)
    {
        int vfrOps = startingVfrOps;
        if(map.containsKey(code))
        {
            double pctHelo = getAirportHelicopterData(code);
            vfrOps = (int) Math.round(vfrOps*(1 - pctHelo));
        }
        
        return vfrOps;
    }

    /**
     * @param airport
     * @param startingVfrOps
     * @return the number of VFR operations that are to be converted
     * to helicopters
     */
    public int getNumberVfrOperations(AirportData airport, int startingVfrOps)
    {
        return getNumberVfrOperations(airport.getIcaoFaaCode(), startingVfrOps);
    }

	@Override
	public void readItem(BufferedReader reader) throws IOException
	{
		String currentLine = null;
		
		while ((currentLine = reader.readLine()) != null)
		{
			if (!currentLine.startsWith("#"))
			{
				String[] values = currentLine.split(",");
				this.addAirportHelicopterData(values[0], Double.parseDouble(values[1]));
			}
			
		}
	}

	@Override
	public void writeItem(PrintWriter writer) throws IOException
	{
		writer.println(this.toString());
	}
}
