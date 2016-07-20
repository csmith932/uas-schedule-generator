package gov.faa.ang.swac.common.flightmodeling.jni;

import gov.faa.ang.swac.datalayer.storage.fileio.HeaderUtils;
import gov.faa.ang.swac.datalayer.storage.fileio.TextSerializable;
import gov.faa.ang.swac.datalayer.storage.fileio.WithHeader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;

public class GateGroupIdentification implements Serializable, TextSerializable, WithHeader{
	
	private String airportCode;
	private String airline;
	private String flightMask;
	private String designGroupMask;
	private String gateGroupName;
	
	
	public String getAirportCode() {  return airportCode; }
	
	public String getAirline() { return airline; }
	
	public String getFlightNumberMask() { return flightMask; }

	public String getDesignGroupMask() { return designGroupMask; }

	public String getGateGroupName() { return gateGroupName; }
	
	@Override
	public long readHeader(BufferedReader reader) throws IOException {
		HeaderUtils.readHeaderHashComment(reader);
        return -1;
	}

	@Override
	public void writeHeader(PrintWriter writer, long numRecords) throws IOException {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void readItem(BufferedReader reader) throws IOException {
		String line = reader.readLine();
		if(line == null)
			return;
		
		line = line.trim();
		if (line.isEmpty())
			return;
		
		int q = 0;
		String[] fields = line.trim().split(",");
		airportCode = fields[q++];
		airline = fields[q++];
		flightMask = fields[q++];
		designGroupMask = fields[q++];
		gateGroupName = fields[q++];
	}
	
	@Override
	public String toString()
	{
		return airportCode+","+airline+","+flightMask+","+designGroupMask+","+gateGroupName;
	}
	
	@Override
	public void writeItem(PrintWriter writer) throws IOException {
		writer.println(this.toString());
	}

}
