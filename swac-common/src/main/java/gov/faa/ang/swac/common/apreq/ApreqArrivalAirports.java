package gov.faa.ang.swac.common.apreq;

import gov.faa.ang.swac.datalayer.storage.fileio.HeaderUtils;
import gov.faa.ang.swac.datalayer.storage.fileio.TextSerializable;
import gov.faa.ang.swac.datalayer.storage.fileio.WithHeader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ApreqArrivalAirports implements TextSerializable, WithHeader, Serializable
{
	private String arrAirport;
	private List<String> depAirportsList;
	
	public String getArrAirport() {
		return arrAirport;
	}

	public void setArrAirport(String arrAirport) {
		this.arrAirport = arrAirport;
	}

	public List<String> getDepAirportsList() {
		return depAirportsList;
	}

	public void setDepAirportsList(List<String> depAirportsList) {
		this.depAirportsList = depAirportsList;
	}

	@Override
	public long readHeader(BufferedReader reader) throws IOException {
		HeaderUtils.readHeaderHashComment(reader);
		return -1;
	}

	@Override
	public void writeHeader(PrintWriter writer, long numRecords)
			throws IOException {
		
	}

	@Override
	public void readItem(BufferedReader reader) throws IOException 
	{
		String line = reader.readLine();
		String[] fields = line.split(",");
		this.arrAirport = fields[0].trim();
		String[] arpList = fields[1].split(":");
		
		depAirportsList = new ArrayList<String>();
		for(String apt : arpList)
		{
			depAirportsList.add(apt.trim());
		}
	}

	@Override
	public void writeItem(PrintWriter writer) throws IOException {
		
	}

}
