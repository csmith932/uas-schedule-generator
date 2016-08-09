package gov.faa.ang.swac.uas.scheduler.forecast.airport_data;

import gov.faa.ang.swac.datalayer.storage.fileio.HeaderUtils;
import gov.faa.ang.swac.datalayer.storage.fileio.TextSerializable;
import gov.faa.ang.swac.datalayer.storage.fileio.WithHeader;
import gov.faa.ang.swac.uas.scheduler.forecast.trip_distribution.ForecastTripDistAirportDataCount;
import gov.faa.ang.swac.uas.scheduler.forecast.trip_distribution.ForecastTripDistAirportDataCount.MissionType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class ForecastAirportCountsRecord implements TextSerializable, WithHeader
{
	public int year;
	public String quarter;
	public String departure;
	public String arrival;
	public ForecastTripDistAirportDataCount count;
	
	@Override
	public void readItem(BufferedReader reader) throws IOException
	{
		String currentLine = reader.readLine();
		String[] values = currentLine.split(",");

		year = Integer.parseInt(values[0]);
        quarter = values[1];
        departure = values[2];
        arrival = values[3];
        this.count = new ForecastTripDistAirportDataCount();
        for (MissionType mission : MissionType.values()) {
        	this.count.setCount(mission, Integer.parseInt(values[4 + mission.ordinal()]));
        }
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