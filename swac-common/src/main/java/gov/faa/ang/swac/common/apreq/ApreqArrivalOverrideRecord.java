package gov.faa.ang.swac.common.apreq;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import gov.faa.ang.swac.datalayer.storage.fileio.TextSerializable;
import gov.faa.ang.swac.datalayer.storage.fileio.WithHeader;

public class ApreqArrivalOverrideRecord implements TextSerializable, WithHeader{

	public String airport;
	public String trafficDelayType;
	public double trafficTimeWindow;
	public double trafficDelayThreshold;
	public double triggerHistoricalQueueDelayCoeff;
	public double triggerCurrentQueueDelayCoeff;
	public double edctAirportQueueDelayCoeff;
	public double edctFixQueueDelayCoeff;
	
	public ApreqArrivalOverrideRecord(){
		// required for reflection
	}
	
	public ApreqArrivalOverrideRecord(String airport, 
										String delayType, 
										double timeWindow, 
										double delayThreshold, 
										double triggerHistoricalCoeff, 
										double triggerCurrentCoeff,
										double edctAirportCoeff, 
										double edctFixCoeff) {
		this.airport							= airport;
		this.trafficDelayType					= delayType;
		this.trafficTimeWindow					= timeWindow;
		this.trafficDelayThreshold				= delayThreshold;
		this.triggerHistoricalQueueDelayCoeff	= triggerHistoricalCoeff;
		this.triggerCurrentQueueDelayCoeff		= triggerCurrentCoeff;
		this.edctAirportQueueDelayCoeff			= edctAirportCoeff;
		this.edctFixQueueDelayCoeff				= edctFixCoeff;
	}
	
	@Override
	public long readHeader(BufferedReader reader) throws IOException {
		reader.readLine(); // discard
		return -1; // -1 tells FileMarshaller to keep reading...
	}

	@Override
	public void writeHeader(PrintWriter arg0, long arg1) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void readItem(BufferedReader reader) throws IOException {
		
    	String items[] = reader.readLine().split(",");
    	
		this.airport							= items[0].trim();
		this.trafficDelayType					= items[1].trim();
		this.trafficTimeWindow					= Double.valueOf(items[2].trim());
		this.trafficDelayThreshold				= Double.valueOf(items[3].trim());
		this.triggerHistoricalQueueDelayCoeff	= Double.valueOf(items[4].trim());
		this.triggerCurrentQueueDelayCoeff		= Double.valueOf(items[5].trim());
		this.edctAirportQueueDelayCoeff			= Double.valueOf(items[6].trim());
		this.edctFixQueueDelayCoeff				= Double.valueOf(items[7].trim());
		
	}

	@Override
	public void writeItem(PrintWriter arg0) throws IOException {
		// TODO Auto-generated method stub
		
	}

}
