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
 * Represents airport arrival information for {@link FlightLeg}.
 * @author Jason Femino - CSSI, Inc.
 */
public class TerminusArrival extends Terminus implements Serializable
{
    //---------------------
	// Static class members
	//---------------------
	// toString related members
	public static final String TEXT_RECORD_KEY = "ARR: " + Terminus.TEXT_RECORD_KEY;
	public static final String TEXT_RECORD_PATTERN = "ARR: " + Terminus.TEXT_RECORD_PATTERN;
	public static final String TEXT_RECORD_PATTERN_NON_CAPTURING = "ARR: " + Terminus.TEXT_RECORD_PATTERN_NON_CAPTURING;
	protected static final Pattern textRecordPattern = Pattern.compile("^" + TEXT_RECORD_PATTERN + "$");

	/**
     * Default constructor
     */
    public TerminusArrival()
    {
    }

    public TerminusArrival(ScheduleRecord scheduleRecord)
    {
    	setCountryCode( scheduleRecord.arrAirportCountryCode);
    	setAirportName( scheduleRecord.arrAprtEtms );
    	
    	if ( !Double.isNaN(scheduleRecord.arrLatitude) && !Double.isNaN(scheduleRecord.arrLongitude) )
    	{
    		double elevation = 0;
    		if ( !Double.isNaN(scheduleRecord.arrElevation) )
    		{
    			elevation = scheduleRecord.arrElevation;
    		}
    		GCPointAlt airportLocation = new GCPointAlt(
    				Latitude.valueOfDegrees(scheduleRecord.arrLatitude),
    				Longitude.valueOfDegrees(scheduleRecord.arrLongitude),
    				Altitude.valueOfFeet(elevation));
    		setAirportLocation( airportLocation );
    	}
    	
    	setScheduledFlag( scheduleRecord.scheduledFlag );
    	setScheduledDateTime( scheduleRecord.scheduledArrTime );
    	setGateDateTime( scheduleRecord.gateInTime );
    	setRunwayDateTimeFlag( scheduleRecord.runwayOnTimeFlag );
    	setRunwayDateTime( scheduleRecord.runwayOnTime );
    }
    
    public TerminusArrival(TerminusArrival arrival) {
		super(arrival);
	}

	@Override
    public boolean equals(Object o)
    {
    	if (!(o instanceof TerminusArrival))
    	{
    		return false;
    	}
    	
    	TerminusArrival terminusArrival = (TerminusArrival)o;
    	
    	if (super.equals(terminusArrival))
    	{	
        	return true;
    	}
    	
    	return false;
    }

    @Override
	public String toString()
	{
	    return "ARR: " + super.toString();
	}
    
    public String basicToString()
    {
    	return super.toString();
    }
	
	public static TerminusArrival fromTextRecord(String str)
	{
		TerminusArrival terminus = null;
		
		// If this string matches the pattern, initialize the object
		Matcher matcher = textRecordPattern.matcher(str);
		if ( matcher.find() )
		{
			try
			{
				// Note: No need to use trim() with any of the matcher.group() calls... the pattern should strip them out
				GCPointAlt point = null;
				terminus = new TerminusArrival();
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

				if (matcher.group(5) != null) { terminus.setScheduledDateTime( Timestamp.myValueOf(matcher.group(5)) ); }
				if (matcher.group(6) != null) { terminus.setScheduledFlag( Boolean.valueOf(matcher.group(6)) ); }
				if (matcher.group(7) != null) { terminus.setRunwayDateTime( Timestamp.myValueOf(matcher.group(7)) ); }
				if (matcher.group(8) != null) { terminus.setRunwayDateTimeFlag( matcher.group(8) ); }
				if (matcher.group(9) != null) { terminus.setGateDateTime( Timestamp.myValueOf(matcher.group(9)) ); }
			}
			catch (Exception e)
			{
				logger.warn("Error parsing TerminusArrival string \"" + str + "\", returning null!");
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
    public TerminusArrival clone()
    {
    	return new TerminusArrival(this);
    }
}