package gov.faa.ang.swac.common.passenger;

import gov.faa.ang.swac.datalayer.storage.fileio.HeaderUtils;
import gov.faa.ang.swac.datalayer.storage.fileio.TextSerializable;
import gov.faa.ang.swac.datalayer.storage.fileio.WithHeader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;

public class BadaSeats implements TextSerializable, WithHeader, Serializable
{
	private String badaType;
	private double averageSeats;
	private double averageLoadFactor;

	public BadaSeats()
	{
	}

	public String getBadaType()
	{
		return badaType;
	}

	public void setBadaType(String badaType)
	{
		this.badaType = badaType;
	}

	public double getAverageSeats()
	{
		return averageSeats;
	}

	public void setAverageSeats(double averageSeats)
	{
		this.averageSeats = averageSeats;
	}

	public double getAverageLoadFactor()
	{
		return averageLoadFactor;
	}

	public void setAverageLoadFactor(double averageLoadFactor)
	{
		this.averageLoadFactor = averageLoadFactor;
	}

	public String getKey()
	{
		return badaType;
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
	public void readItem(BufferedReader reader) throws IOException {
		try
		{
			String line = reader.readLine();
			String[] fields = line.trim().split(",");
			this.badaType = fields[0].trim();
			this.averageSeats = Double.valueOf(fields[1].trim());
			this.averageLoadFactor = Double.valueOf(fields[2].trim());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}

	@Override
	public void writeItem(PrintWriter writer) throws IOException {
		// TODO Auto-generated method stub
		
	}
}
