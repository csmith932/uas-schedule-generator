/**
 * Copyright "TBD", Metron Aviation & CSSI.  All rights reserved.
 * 
 * This computer Software was developed with the sponsorship of the U.S. Government
 * under Contract No. DTFAWA-10-D-00033, which has a copyright license in accordance with AMS 3.5-13.(c)(1).
 */

package gov.faa.ang.swac.common.entities;

import gov.faa.ang.swac.common.datatypes.Timestamp;
import gov.faa.ang.swac.common.utilities.SolarData;
import gov.faa.ang.swac.common.utilities.SolarUtility;


/** 
 *
 */
public class AirportSurfaceModeling /* implements TextSerializable, WithHeader */
{
    private String airportCode = null;
	private double lat = 0;
	private double lon = 0;
	private Integer numRunway = null;
	private String timeZone = null;
	private Integer gmtAdj = null;
	private Integer gmtAdjDst = null;
	private double nominalTaxiOut;
	private double nominalTaxiIn;
	private double nominalTaxiOutDelay;
	private double nominalTaxiInDelay;
	private boolean validSurfaceData = false;
	
	
	public AirportSurfaceModeling(Airport apt) {
	    this.airportCode = apt.getName();
	    this.lat = apt.getLatitude().degrees();
	    this.lon = apt.getLongitude().degrees();
	    
	    this.numRunway = apt.getNumRunways();
	    this.timeZone = apt.getTimeZone();
	    
	    if (apt.getUtcOffset() == null) {
	        this.gmtAdj = null;
	        this.gmtAdjDst = null;
	    } else {
	        this.gmtAdj = apt.getUtcOffset().intValue();
            this.gmtAdjDst = apt.isDst() ? this.gmtAdj + 1 : this.gmtAdj;
	    }
	    
	    this.nominalTaxiOut = (apt.getTaxiOutTime() == null) ? 0 : apt.getTaxiOutTime();
	    this.nominalTaxiIn = (apt.getTaxiInTime() == null) ? 0 : apt.getTaxiInTime();
	    this.nominalTaxiOutDelay = (apt.getTaxiOutDelay() == null) ? 0 : apt.getTaxiOutDelay();
	    this.nominalTaxiInDelay = (apt.getTaxiInDelay() == null) ? 0 : apt.getTaxiInDelay();
	    
		if (numRunway == null || timeZone == null || gmtAdj == null || gmtAdjDst == null
				|| apt.getTaxiOutTime() == null || (apt.getTaxiInTime() == null)) 
			validSurfaceData = false; // non-core airport
		else 
			validSurfaceData = true; // core airport
	}
	
	public String getAirportCode() {
		return airportCode;
	}

	public Integer getNumRunway() {
		return numRunway;
	}

	public String getTimeZone() {
		return timeZone;
	}

	public Integer getGmtAdj() {
		return gmtAdj;
	}

	public Integer getGmtAdjDst() {
		return gmtAdjDst;
	}

	public double getNominalTaxiOut() {
		return nominalTaxiOut;
	}

	public double getNominalTaxiIn() {
		return nominalTaxiIn;
	}

	public double getNominalTaxiOutDelay() {
		return nominalTaxiOutDelay;
	}

	public double getNominalTaxiInDelay() {
		return nominalTaxiInDelay;
	}

	public boolean isValidSurfaceData() {
		return validSurfaceData;
	}
	
	public boolean isDayLight(Timestamp currTime) { 
		SolarData solarData = SolarUtility.calcSunriseSet(currTime.toDate(), lat, lon);
     
		return SolarUtility.isDayLight(solarData, currTime.toDate());
	}
		
	
	/*
	@Override
	public void readItem(BufferedReader reader) throws IOException {
		String[] fields = reader.readLine().split(",");
        
        this.airportCode = fields[0].trim().toUpperCase();
        this.lat = Double.parseDouble(fields[1].trim());
        this.lon = Double.parseDouble(fields[2].trim());
        this.numRunway = Integer.valueOf(fields[3].trim());
        this.timeZone = fields[4].trim().toUpperCase();
        this.gmtAdj = Integer.parseInt(fields[5].trim());
        this.gmtAdjDst = Integer.parseInt(fields[6].trim());
        this.nominalTaxiOut = Double.parseDouble(fields[7].trim());
        this.nominalTaxiIn = Double.parseDouble(fields[8].trim());
        this.nominalTaxiOutDelay = Double.parseDouble(fields[9].trim());
        this.nominalTaxiInDelay = Double.parseDouble(fields[10].trim());
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
	*/
	
	
}
