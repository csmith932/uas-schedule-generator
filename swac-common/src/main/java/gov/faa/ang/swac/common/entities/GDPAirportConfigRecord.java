/**
 * Copyright "TBD", Metron Aviation & CSSI.  All rights reserved.
 * 
 * This computer Software was developed with the sponsorship of the U.S. Government
 * under Contract No. DTFAWA-10-D-00033, which has a copyright license in accordance with AMS 3.5-13.(c)(1).
 */

package gov.faa.ang.swac.common.entities;

import gov.faa.ang.swac.datalayer.storage.fileio.TextSerializable;
import gov.faa.ang.swac.datalayer.storage.fileio.WithHeader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class GDPAirportConfigRecord implements TextSerializable, WithHeader
{
    public static double DEFAULT_GDP_IMPLEMENTATION_TIME_LEAD   = 30;
    public static double DEFAULT_GDP_DEPARTURE_EXEMPTION_BUFFER = 30;
    
	/** Default airport configuration identifier. */
	public static String AIRPORT_CONFIGURATION_DEFAULT = "DEFAULT";

    public String airportCode;
    
    public double timeBinSize;  // time bin size (mins)
    public double queuedFlightThreshold;   // flights
    public double maxFlightDelayThreshold;  // minutes
    public double totalFlightDelayThreshold;    // minutes
    
    public double gdpImplementationTimeLead;    // minutes;
    public double gdpDepartureExemptionBuffer;  // minutes;
    
    public double cancellationQuantitySlope;    // constant
    public double cancellationQuantityConstant; // constant
    
    public double cancellationTimeSlope;    // constant
    public double cancellationTimeConstant; // constant
    
    public boolean supportCancellations;

    

	@Override
	public void readItem(BufferedReader reader) throws IOException {
		String[] fields = reader.readLine().split(",");
        
        this.airportCode = fields[0].trim().toUpperCase();
        this.timeBinSize = Double.parseDouble(fields[1].trim());
        this.queuedFlightThreshold = Double.parseDouble(fields[2].trim());
        this.maxFlightDelayThreshold = Double.parseDouble(fields[3].trim());
        this.totalFlightDelayThreshold = Double.parseDouble(fields[4].trim());
        
        this.gdpImplementationTimeLead = Double.parseDouble(fields[5].trim());
        this.gdpDepartureExemptionBuffer = Double.parseDouble(fields[6].trim());
        
        if (fields.length >= 11)
        {
            this.cancellationQuantitySlope = Double.parseDouble(fields[7].trim());
            this.cancellationQuantityConstant = Double.parseDouble(fields[8].trim());
            this.cancellationTimeSlope = Double.parseDouble(fields[9].trim());
            this.cancellationTimeConstant = Double.parseDouble(fields[10].trim());
            
            this.supportCancellations = true;
        }
        else
            this.supportCancellations = false;

	}

	@Override
	public void writeItem(PrintWriter writer) throws IOException {
		writer.println(this.toString());
	}

	@Override
	public long readHeader(BufferedReader reader) throws IOException {
		reader.readLine();
		return -1;
	}

	@Override
	public void writeHeader(PrintWriter writer, long numRecords)
			throws IOException {
		throw new UnsupportedOperationException();
	}
}
