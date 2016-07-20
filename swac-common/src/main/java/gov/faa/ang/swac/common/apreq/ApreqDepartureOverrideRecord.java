package gov.faa.ang.swac.common.apreq;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import gov.faa.ang.swac.datalayer.storage.fileio.TextSerializable;
import gov.faa.ang.swac.datalayer.storage.fileio.WithHeader;

public class ApreqDepartureOverrideRecord implements TextSerializable, WithHeader{

	public String airport;
	public String streamDelayType;
	public double streamTimeWindow;
	public double streamDelayThreshold;
	public double triggerHistoricalQueueDelayCoeff;
	public double triggerCurrentQueueDelayCoeff;
	public double triggerEdctDelayCoeff;
	public double edctEntryQueueLengthCoeff;
	public double edctAvgExitQueueLengthCoeff;
	public double edctFlightAtAiroprtWithEdctCoeff;
	
	public ApreqDepartureOverrideRecord(){
		//required for reflection.
	}
	
	public ApreqDepartureOverrideRecord(String airport, 
										String delayType,
										double streamTimeWindow, 
										double streamDelayThreshold, 
										double triggerHistoricalCoeff, 
										double triggerCurrentQueueCoeff, 
										double triggerEdctCoeff, 
										double edctEntryQueueCoeff, 
										double edctAvgExitQueueLengthCoeff, 
										double edctFlightAtAiroprtWithEdctCoeff){
		this.airport = airport;
		this.streamDelayType = delayType;
		this.streamTimeWindow = streamTimeWindow;
		this.streamDelayThreshold = streamDelayThreshold;
		this.triggerHistoricalQueueDelayCoeff = triggerHistoricalCoeff;
		this.triggerCurrentQueueDelayCoeff = triggerCurrentQueueCoeff;
		this.triggerEdctDelayCoeff = triggerEdctCoeff;
		this.edctEntryQueueLengthCoeff = edctEntryQueueCoeff;
		this.edctAvgExitQueueLengthCoeff = edctAvgExitQueueLengthCoeff;
		this.edctFlightAtAiroprtWithEdctCoeff = edctFlightAtAiroprtWithEdctCoeff;
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
    	this.streamDelayType					= items[1].trim();
    	this.streamTimeWindow					= Double.valueOf(items[2].trim());
    	this.streamDelayThreshold 				= Double.valueOf(items[3].trim());
    	this.triggerHistoricalQueueDelayCoeff	= Double.valueOf(items[4].trim());
    	this.triggerCurrentQueueDelayCoeff		= Double.valueOf(items[5].trim());
    	this.triggerEdctDelayCoeff				= Double.valueOf(items[6].trim());
    	this.edctEntryQueueLengthCoeff			= Double.valueOf(items[7].trim());
    	this.edctAvgExitQueueLengthCoeff		= Double.valueOf(items[8].trim());
    	this.edctFlightAtAiroprtWithEdctCoeff	= Double.valueOf(items[9].trim());
		
	}

	@Override
	public void writeItem(PrintWriter arg0) throws IOException {
		// TODO Auto-generated method stub
		
	}

}
