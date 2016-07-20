package gov.faa.ang.swac.common.entities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;

import gov.faa.ang.swac.datalayer.storage.fileio.HeaderUtils;
import gov.faa.ang.swac.datalayer.storage.fileio.TextSerializable;
import gov.faa.ang.swac.datalayer.storage.fileio.WithHeader;

public class TafAirportCorrelation implements TextSerializable, WithHeader, Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int group;	
	private String airport1;
	private String airport2;
	private double tafMulCorr;

	public String getAirport1() {
		return airport1;
	}

	public void setAirport1(String airport1) {
		this.airport1 = airport1;
	}

	public String getAirport2() {
		return airport2;
	}

	public void setAirport2(String airport2) {
		this.airport2 = airport2;
	}

	public double getTafMulCorr() {
		return tafMulCorr;
	}

	public void setTafMulCorr(double tafMulCorr) {
		this.tafMulCorr = tafMulCorr;
	}
	
	public int getGroup() {
		return group;
	}

	public void setGroup(int group) {
		this.group = group;
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
		
		String line = reader.readLine();
		String[] fields = line.trim().split(",");
		this.setGroup(new Integer(fields[0].trim()));
		this.setAirport1(fields[1].trim());
		this.setAirport2(fields[2].trim());
		this.setTafMulCorr(new Double(fields[3].trim()));
	}

	@Override
	public void writeItem(PrintWriter writer) throws IOException {
		// TODO Auto-generated method stub
		
	}

}
