package gov.faa.ang.swac.common.flightmodeling.jni;

import gov.faa.ang.swac.datalayer.storage.fileio.HeaderUtils;
import gov.faa.ang.swac.datalayer.storage.fileio.TextSerializable;
import gov.faa.ang.swac.datalayer.storage.fileio.WithHeader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;

public class GateGroup implements Serializable, TextSerializable, WithHeader{
	private String gateGroupName;
	private double gateLagTime;
	private int numGates;
	private int rampCapacity; 

	public GateGroup() { }
	
	public GateGroup(String gateGroupName, double gateLagTime, int numGates, int rampCapacity) {
		this.gateGroupName = gateGroupName;
		this.gateLagTime = gateLagTime;
		this.numGates = numGates;
		this.rampCapacity = rampCapacity;
		assert rampCapacity >= 0;
	}

	public String getGateGroupName() {
		return gateGroupName;
	}
	
	public int getNumGates() {
		return numGates;
	}
	
	public double getGateLagTime() {
		return gateLagTime;
	}
	
	public int getRampCapacity() {
		return rampCapacity;
	}
	
	public String determineAirportCode() {
		// TODO figure out if gateGroupName being null can be avoided.
		if (gateGroupName == null)
			return null;
		
		int pos = gateGroupName.indexOf('_');
		if (pos < 0)
			return null;
		
		return gateGroupName.substring(0, pos);
	}

	
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
		if(line == null) {
			return;
		}
		
		line = line.trim();
		if (line.isEmpty()) {
			return;
		}
		
		int q = 0;
		String[] fields = line.trim().split(",");
		gateGroupName = fields[q++];
		gateLagTime = Double.valueOf(fields[q++]);
		numGates = Integer.valueOf(fields[q++]);
		String rampCapacityStr = fields[q++];
		if (rampCapacityStr.equalsIgnoreCase("inf"))
			rampCapacity = Integer.MAX_VALUE;
		else {
			rampCapacity = Integer.valueOf(rampCapacityStr);
			if (rampCapacity < 0)
				rampCapacity = Integer.MAX_VALUE;
		}
		assert(rampCapacity >= 0); 
	}
	
	@Override
	public String toString()
	{
		return gateGroupName+","+gateLagTime+","+numGates+","+rampCapacity;
	}
	
	@Override
	public void writeItem(PrintWriter writer) throws IOException {
		writer.println(this.toString());
	}
}
