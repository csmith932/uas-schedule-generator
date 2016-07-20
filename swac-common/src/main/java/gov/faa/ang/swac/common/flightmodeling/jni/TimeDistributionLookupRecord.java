package gov.faa.ang.swac.common.flightmodeling.jni;

import gov.faa.ang.swac.common.datatypes.Timestamp;
import gov.faa.ang.swac.datalayer.storage.fileio.HeaderUtils;
import gov.faa.ang.swac.datalayer.storage.fileio.TextSerializable;
import gov.faa.ang.swac.datalayer.storage.fileio.WithHeader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;

public class TimeDistributionLookupRecord implements Serializable, TextSerializable, WithHeader {
	
	private static org.apache.log4j.Logger logger = org.apache.log4j.LogManager.getLogger(TimeDistributionLookupRecord.class);
	
	/** The IATA Carrier. */
	private String iataCarrier;
	/** The ICAO Carrier. */
	private String icaoCarrier;
	/** The airport code. */
	private String airportCode;
	/** The equipment category. */
	private Integer equipmentCat;
	/** The time distribution */
	private TimeDistribution timeDistribution;

	
	public TimeDistributionLookupRecord(){}

	public String getAirportCode() { return airportCode; }

	public TimeDistribution getTimeDistribution() { return timeDistribution; }
	
	public Integer getEquipmentCat() { return equipmentCat; }

	public String getIATACarrier() { return iataCarrier; }

	public String getICAOCarrier() { return icaoCarrier; }

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
		if (line.startsWith("#")) return; 
		
		String[] fields = line.split(",");
		if (fields.length != expectedFieldCount()) {
			logger.warn(getClass().getSimpleName() + " records have switched to a csv format");
			return;
		}

		parseFields(fields);
	}
	
	protected int expectedFieldCount() { 
		return 6;
	}
	
	protected void parseFields(String [] fields) {
		this.iataCarrier = fields[0].trim();
        this.icaoCarrier = fields[1].trim();
        this.airportCode = fields[2].trim();
        if (fields[3].trim().equals("-")){
        	this.equipmentCat = null;
        }
        else{
        	this.equipmentCat = Integer.parseInt(fields[3].trim());
        }
        String meanStr = fields[4].trim();
        String stdDevStr = fields[5].trim();
        double mean; double stdDev;
        if (meanStr.equals("-")) mean = 0; else mean = Double.parseDouble(meanStr)*Timestamp.MILLISECS_MIN;
        if (stdDevStr.equals("-")) stdDev = 0; else stdDev = Double.parseDouble(stdDevStr)*Timestamp.MILLISECS_MIN;
        this.timeDistribution = new TimeDistribution(mean, stdDev);
	}
	
	@Override
	public void writeItem(PrintWriter writer) throws IOException {
		writer.println(this.toString());
	}
}
