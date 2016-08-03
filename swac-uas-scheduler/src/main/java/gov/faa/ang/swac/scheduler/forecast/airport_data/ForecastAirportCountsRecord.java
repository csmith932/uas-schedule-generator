package gov.faa.ang.swac.scheduler.forecast.airport_data;

import gov.faa.ang.swac.datalayer.storage.fileio.HeaderUtils;
import gov.faa.ang.swac.datalayer.storage.fileio.TextSerializable;
import gov.faa.ang.swac.datalayer.storage.fileio.WithHeader;
import gov.faa.ang.swac.scheduler.forecast.trip_distribution.ForecastTripDistAirportDataCount;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class ForecastAirportCountsRecord implements TextSerializable, WithHeader
{
	public String yyyymmdd;
	public String airportName;
	public ForecastTripDistAirportDataCount count;
	
	public int getYear()
	{
		return Integer.parseInt(this.yyyymmdd.substring(0,4));
	}
	
	@Override
	public void readItem(BufferedReader reader) throws IOException
	{
		String currentLine = reader.readLine();
		String[] values = currentLine.split(",");

		yyyymmdd = values[0];
        airportName = values[1];
        int countGA = Integer.parseInt(values[2]);
        int countMil = Integer.parseInt(values[3]);
        int countOther = Integer.parseInt(values[4]) + Integer.parseInt(values[5]);
            
        this.count = new ForecastTripDistAirportDataCount(countGA, countMil, countOther);
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
	
	@Override
	public String toString() {
		return "ForecastAirportCountsRecord [yyyymmdd=" + yyyymmdd
				+ ", airportName=" + airportName + ", count=" + count + "]";
	}
}