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

public class ApreqDepartureAirports implements TextSerializable, WithHeader, Serializable
{

	private List<String> airportsList = new ArrayList<String>();

	public List<String> getAirportsList() {
		return airportsList;
	}

	public void setAirportsList(List<String> airportsList) {
		this.airportsList = airportsList;
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
		String str = null;
		while( (str =reader.readLine()) != null)
		{
			airportsList.add(str.trim());
		}
	}

	@Override
	public void writeItem(PrintWriter writer) throws IOException {
		
	}

}
