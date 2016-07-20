/**
 * Copyright "TBD", Metron Aviation & CSSI.  All rights reserved.
 * 
 * This computer Software was developed with the sponsorship of the U.S. Government
 * under Contract No. DTFAWA-10-D-00033, which has a copyright license in accordance with AMS 3.5-13.(c)(1).
 */

package gov.faa.ang.swac.common.flightmodeling;

import gov.faa.ang.swac.common.datatypes.Altitude;
import gov.faa.ang.swac.common.datatypes.Patterns;
import gov.faa.ang.swac.common.utilities.Mathematics;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;


/**
 * Contains information for an altitude restriction.
 * @author Jason Femino - CSSI, Inc.
 */
public class AltitudeRestriction implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6469227295149640200L;

	private static Logger logger = LogManager.getLogger(AltitudeRestriction.class);
	
	public static final String TEXT_RECORD_PATTERN = 
		"("+RestrictionType.pattern()+")" +   // RestrictionType
		"([+|-])" +               // AltitudeType (NOTE: The plus-symbol MUST come first in this list or Pattern.compile() will fail!)
		"("+Patterns.INTEGER+")"; // Altitude (feet)
	public static final String TEXT_RECORD_PATTERN_NON_CAPTURING = 
		"(?:"+RestrictionType.pattern()+")" +   // RestrictionType
		"(?:[+|-])" +               // AltitudeType (NOTE: The plus-symbol MUST come first in this list or Pattern.compile() will fail!)
		"(?:"+Patterns.INTEGER+")"; // Altitude (feet)
	protected static final Pattern textRecordPattern = Pattern.compile("^" + TEXT_RECORD_PATTERN  + "$");
	
	public enum AltitudeType
	{
		AT          { @Override public String symbol() { return "|"; } },
		AT_OR_BELOW { @Override public String symbol() { return "-"; } },
		AT_OR_ABOVE { @Override public String symbol() { return "+"; } };
		
		abstract public String symbol();
	}
	
	public enum RestrictionType
	{
		SID,    // Standard Instrument Departure
		STAR,   // Standard Terminal Arrival Route
		IAP;    // Instrument Approach Procedure
		
		private static String pattern()
		{
			RestrictionType[] values = RestrictionType.values();
			String pattern = "";
			for (int i=0; i<values.length; i++)
			{
				// XXX: String concatenation abuse, but only usage is for a static final string in the parent so scope is limited
				pattern += values[i].toString();
				if (i<=values.length) { pattern += "|"; }
			}
			return pattern;
		}
	}
	
	private final AltitudeType altitudeType;
	private final Altitude altitude;
	private final RestrictionType restrictionType;

	public AltitudeRestriction(RestrictionType restrictionType, AltitudeType altitudeType, Altitude altitude)
	{
		this.restrictionType = restrictionType;
		this.altitudeType = altitudeType;
		this.altitude = altitude;
	}

        public AltitudeRestriction(AltitudeRestriction org) {
            this.altitude = org.altitude;
            this.altitudeType = org.altitudeType;
            this.restrictionType = org.restrictionType;
        }
	
	@Override
    public boolean equals(Object o)
    {
	    if (!(o instanceof AltitudeRestriction))
	    {
	            return false;
	    }
	
	    AltitudeRestriction altitudeRestriction = (AltitudeRestriction)o;
	
	    if (this.restrictionType == altitudeRestriction.restrictionType &&
	    	this.altitudeType == altitudeRestriction.altitudeType &&
	    	(this.altitude == null && altitudeRestriction.altitude == null || 
	    			(this.altitude != null && altitudeRestriction.altitude != null && Mathematics.equals(this.altitude.feet(), altitudeRestriction.altitude.feet()))) )
	    {
	            return true;
	    }
	
	    return false;
    }
	
	public AltitudeType altitudeType()
	{
		return this.altitudeType;
	}
	
	public Altitude altitude()
	{
		return this.altitude;
	}
	
	public RestrictionType restrictionType()
	{
		return this.restrictionType;
	}
	
	public String altitudeString()
	{
		return (this.altitudeType == null ? "" : this.altitudeType.symbol()) +
		       (this.altitude == null || this.altitude.feet() == null ? "" : this.altitude.feet().intValue());
	}
	
	@Override
	public String toString()
	{
		return (this.restrictionType == null ? "" : this.restrictionType) +
			   (this.altitudeType == null ? "" : this.altitudeType.symbol()) +
			   (this.altitude == null || this.altitude.feet() == null ? "" : this.altitude.feet().intValue());
	}
	
	public static AltitudeRestriction fromTextRecord(String str)
	{
		Matcher matcher = textRecordPattern.matcher(str);
		
		if (matcher.find())
		{
			RestrictionType restrictionType = RestrictionType.valueOf(matcher.group(1));

			Character altitudeTypeChar = matcher.group(2).charAt(0);
			AltitudeType altitudeType = null;
			switch (altitudeTypeChar)
			{
			case '|':
				altitudeType = AltitudeType.AT;
				break;
			case '+':
				altitudeType = AltitudeType.AT_OR_ABOVE;
				break;
			case '-':
				altitudeType = AltitudeType.AT_OR_BELOW;
				break;
			default:
				logger.error("AltitudeRestriction.fromTextRecord() error: Unhandled AltitudeType char ('" + altitudeTypeChar + "') in \"" + str + "\"! Returning null.");
				return null;
			}
			
			Altitude altitude = Altitude.valueOfFeet(Double.valueOf(matcher.group(3)));
			
			return new AltitudeRestriction(restrictionType, altitudeType, altitude);
		}
		
		return null;
	}
        
    @Override
    public AltitudeRestriction clone() {
        return new AltitudeRestriction(this);
    }
}