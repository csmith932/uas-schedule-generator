package gov.faa.ang.swac.common.passenger;

import gov.faa.ang.swac.datalayer.storage.fileio.HeaderUtils;
import gov.faa.ang.swac.datalayer.storage.fileio.TextSerializable;
import gov.faa.ang.swac.datalayer.storage.fileio.WithHeader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;

/**
 * A class containing the important data from the TAF enplanement data.
 * 
 * Important data is the airport code, the year, the domestic count (carrier plus commuter), 
 * and the US international count (us flag data)
 * 
 * @author James Bonn
 *
 */
public class TafEnplanementData implements TextSerializable, WithHeader, Serializable
{
	private String airportCode;
	private int year;
	private double domesticCount;
	private double usInternationalCount;

	public TafEnplanementData()
	{
	}

	public String getAirportCode()
	{
		return airportCode;
	}

	public void setAirportCode(String airportCode)
	{
		this.airportCode = airportCode;
	}

	public int getYear()
	{
		return year;
	}

	public void setYear(int year)
	{
		this.year = year;
	}

	public double getDomesticCount()
	{
		return domesticCount;
	}

	public void setDomesticCount(double domesticCount)
	{
		this.domesticCount = domesticCount;
	}

	public double getUsInternationalCount()
	{
		return usInternationalCount;
	}

	public void setUsInternationalCount(double usInternationalCount)
	{
		this.usInternationalCount = usInternationalCount;
	}

	/**
	 * Keyed on the airport code
	 * 
	 * @return
	 */
	public String getKey()
	{
		return airportCode;
	}

	@Override
	public long readHeader(BufferedReader reader) throws IOException {
		HeaderUtils.readHeaderHashComment(reader);
		return -1;
	}

	@Override
	public void writeHeader(PrintWriter writer, long numRecords)
			throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void readItem(BufferedReader reader) throws IOException 
	{
		String line = reader.readLine();
		String [] lineData = line.split(",");
		this.airportCode = lineData[0].trim();
		this.year = Integer.valueOf(lineData[1].trim());
		this.domesticCount = Double.valueOf(lineData[2].trim());
		this.usInternationalCount = Double.valueOf(lineData[3].trim());		
	}

	@Override
	public void writeItem(PrintWriter writer) throws IOException {
		// TODO Auto-generated method stub
		
	}
}
