/**
 * Copyright "TBD", Metron Aviation & CSSI.  All rights reserved.
 * 
 * This computer Software was developed with the sponsorship of the U.S. Government
 * under Contract No. DTFAWA-10-D-00033, which has a copyright license in accordance with AMS 3.5-13.(c)(1).
 */

package gov.faa.ang.swac.common.flightmodeling;

import gov.faa.ang.swac.common.datatypes.Altitude;
import gov.faa.ang.swac.common.datatypes.Latitude;
import gov.faa.ang.swac.common.datatypes.Longitude;
import gov.faa.ang.swac.common.datatypes.Timestamp;
import gov.faa.ang.swac.common.geometry.GCPointAlt;


import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Represents airport departure information for {@link FlightLeg}.
 * @author Jason Femino - CSSI, Inc.
 */
public class TerminusDeparture extends Terminus implements Serializable
{
    //---------------------
	// Static class members
	//---------------------

	// toString related members
	public static final String TEXT_RECORD_KEY = "DEP: " + Terminus.TEXT_RECORD_KEY + ", gateDateTimeFlag";
	public static final String TEXT_RECORD_PATTERN = "DEP: " + Terminus.TEXT_RECORD_PATTERN + SEP + "\\s*(.*)\\s*";                               // Terminus + gateDateTime
	public static final String TEXT_RECORD_PATTERN_NON_CAPTURING = "DEP: " + Terminus.TEXT_RECORD_PATTERN_NON_CAPTURING + SEP + "\\s*(?:.*)\\s*"; // Terminus + gateDateTimeFlag
	protected static final Pattern textRecordPattern = Pattern.compile("^" + TEXT_RECORD_PATTERN + "$");

	//-----------------------
	// Instance class members
	//-----------------------
	private String gateDateTimeFlag = null;

    /**
     * Default constructor
     */
    public TerminusDeparture()
    {
    }
    
    public TerminusDeparture(ScheduleRecord scheduleRecord)
    {
    	setCountryCode( scheduleRecord.depAirportCountryCode);
    	setAirportName( scheduleRecord.depAprtEtms );
    	
    	if ( !Double.isNaN(scheduleRecord.depLatitude) && !Double.isNaN(scheduleRecord.depLongitude) )
    	{
    		double elevation = 0;
    		if ( !Double.isNaN(scheduleRecord.depElevation) )
    		{
    			elevation = scheduleRecord.depElevation;
    		}
    		GCPointAlt airportLocation = new GCPointAlt(
    				Latitude.valueOfDegrees(scheduleRecord.depLatitude),
    				Longitude.valueOfDegrees(scheduleRecord.depLongitude),
    				Altitude.valueOfFeet(elevation));
    		setAirportLocation( airportLocation );
    	}
    	
    	setScheduledFlag( scheduleRecord.scheduledFlag );
    	setScheduledDateTime( scheduleRecord.scheduledDepTime );
    	setGateDateTimeFlag( scheduleRecord.gateOutTimeFlag );
    	setGateDateTime( scheduleRecord.gateOutTime );
    	setRunwayDateTimeFlag( scheduleRecord.runwayOffTimeFlag );
    	setRunwayDateTime( scheduleRecord.runwayOffTime );
    }

    public TerminusDeparture(TerminusDeparture departure) {
		super(departure);
		this.gateDateTimeFlag = departure.gateDateTimeFlag;
	}

	@Override
    public boolean equals(Object o)
    {
    	if (!(o instanceof TerminusDeparture))
    	{
    		return false;
    	}
    	
    	TerminusDeparture terminusDeparture = (TerminusDeparture)o;
    	

    	if ( super.equals(terminusDeparture) &&
       	     ((this.gateDateTimeFlag == null && terminusDeparture.gateDateTimeFlag == null) || (this.gateDateTimeFlag != null && terminusDeparture.gateDateTimeFlag != null && this.gateDateTimeFlag.equals(terminusDeparture.gateDateTimeFlag))) )
    	{	
        	return true;
    	}
    	
    	return false;
    }
    
    public void setGateDateTimeFlag(String flag)
    {
        this.gateDateTimeFlag = flag;
    }

    public String gateDateTimeFlag()
    {
        return this.gateDateTimeFlag;
    }

    @Override
	public String toString()
	{
	    return "DEP: " + super.toString() + SEP +
	    		" " + (this.gateDateTimeFlag   == null ? "" : this.gateDateTimeFlag);
	}
    
    public String basicToString()
    {
    	return super.toString() + SEP +
	    		" " + (this.gateDateTimeFlag   == null ? "" : this.gateDateTimeFlag);
    }
	
	public static TerminusDeparture fromTextRecord(String str)
	{
		TerminusDeparture terminus = null;
		
		// If this string matches the pattern, initialize the object
		Matcher matcher = textRecordPattern.matcher(str);
		if ( matcher.find() )
		{
			try
			{
				// Note: No need to use trim() with any of the matcher.group() calls... the pattern should strip them out
				terminus = new TerminusDeparture();
				GCPointAlt point = null;
				if (matcher.group(1) != null) { terminus.setAirportName( matcher.group(1) ); }
				
				// Convert lat/lon/alt into GCPointAlt
				if (matcher.group(2) != null && matcher.group(3) != null)
				{
					Latitude lat = Latitude.valueOfDegrees(Double.valueOf(matcher.group(2)));
					Longitude lon = Longitude.valueOfDegrees(Double.valueOf(matcher.group(3)));
					Altitude alt = Altitude.valueOfFeet(0.0);
					point = new GCPointAlt(lat, lon, alt);
				}
				if (point != null && matcher.group(4) != null)
				{
					point.setAltitude( Altitude.valueOfFeet(Double.valueOf(matcher.group(4)) ));
				}
				terminus.setAirportLocation( point );					

				if (matcher.group(5) != null)  { terminus.setScheduledDateTime( Timestamp.myValueOf(matcher.group(5)) ); }
				if (matcher.group(6) != null)  { terminus.setScheduledFlag( Boolean.valueOf(matcher.group(6)) ); }
				if (matcher.group(7) != null)  { terminus.setRunwayDateTime( Timestamp.myValueOf(matcher.group(7)) ); }
				if (matcher.group(8) != null)  { terminus.setRunwayDateTimeFlag( matcher.group(8) ); }
				if (matcher.group(9) != null) { terminus.setGateDateTime( Timestamp.myValueOf(matcher.group(9)) ); }
				if (matcher.group(10) != null) { terminus.setGateDateTimeFlag( matcher.group(10) ); }
			}
			catch (Exception e)
			{
				logger.warn("Error parsing TerminusDeparture string \"" + str + "\", returning null!");
				terminus = null;
			}
		}
		else
		{
			logger.warn("Terminus error: Input \"" + str + "\", does not match pattern \"" + textRecordPattern.pattern() + "\"! Returning null.");
		}

		
		return terminus;
	}
    
    @Override
    public TerminusDeparture clone()
    {
    	return new TerminusDeparture(this);
    }
}