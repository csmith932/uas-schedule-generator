package gov.faa.ang.swac.uas.scheduler.input;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import gov.faa.ang.swac.datalayer.storage.fileio.HeaderUtils;
import gov.faa.ang.swac.datalayer.storage.fileio.TextSerializable;
import gov.faa.ang.swac.datalayer.storage.fileio.WithHeader;
import gov.faa.ang.swac.uas.scheduler.forecast.trip_distribution.ForecastTripDistAirportDataCount.MissionType;

public class UasVfrRecord implements TextSerializable, WithHeader {
	public MissionType missionType;
	public String departure;
	public String arrival;
	public double fraction;
	public String badaType;
	public double cruiseTas;
	public double cruiseAltitude;
	public double duration;
	public List<Waypoint> waypoints;
	
	@Override
	public void readItem(BufferedReader reader) throws IOException
	{
		String currentLine = reader.readLine();
		String[] values = currentLine.split(",");

		missionType = MissionType.fromUserClass(values[0]);
		departure = values[1];
		arrival = values[2];
		fraction = Double.parseDouble(values[3]);
		badaType = values[4];
		cruiseTas = Double.parseDouble(values[5]);
		cruiseAltitude = Double.parseDouble(values[6]);
		duration = Double.parseDouble(values[7]);
        waypoints = values.length == 9 ? Waypoint.fromWaypointString(values[8]) : new ArrayList<Waypoint>();
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
