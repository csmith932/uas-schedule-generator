/**
 * Copyright "TBD", Metron Aviation & CSSI.  All rights reserved.
 * 
 * This computer Software was developed with the sponsorship of the U.S. Government
 * under Contract No. DTFAWA-10-D-00033, which has a copyright license in accordance with AMS 3.5-13.(c)(1).
 */

package gov.faa.ang.swac.common.flightmodeling;

import java.io.Serializable;

import gov.faa.ang.swac.common.datatypes.Patterns;
import gov.faa.ang.swac.common.datatypes.Timestamp;
import gov.faa.ang.swac.common.geometry.GCPointAlt;


/**
 * Represents information common to all departures/arrivals for an {@link FlightLeg}.<p>
 * This is an abstract base class. Please implement either {@link TerminusDeparture} or {@link TerminusArrival}.
 * @author Jason Femino - CSSI, Inc.
 */
public class Terminus extends AbstractResourceInfo implements Cloneable, Serializable
{
    protected static org.apache.log4j.Logger logger = org.apache.log4j.LogManager.getLogger(Terminus.class);

    //---------------------
    // Static class members
    //---------------------

    // toString related members
    protected static final String SEP = ",";
    protected static final String TEXT_RECORD_KEY = "aptName, aptLat, aptLon, aptAlt, schedDateTime, schedFlag, runwayDateTime, runwayFlag, gateDateTime";
    protected static final String TEXT_RECORD_PATTERN =
        "\\s*(\\S*)\\s*" + SEP +                    // airport
        "\\s*("+Patterns.FLOAT+")?\\s*" + SEP +     // airportLocation (latitude)
        "\\s*("+Patterns.FLOAT+")?\\s*" + SEP +     // airportLocation (longitude)
        "\\s*("+Patterns.FLOAT+")?\\s*" + SEP +     // airportLocation (elevation)
        "\\s*("+Patterns.DATE_TIME+")?\\s*" + SEP + // scheduledDateTime
        "\\s*("+Patterns.BOOLEAN+")\\s*" + SEP +    // scheduledFlag
        "\\s*("+Patterns.DATE_TIME+")?\\s*" + SEP + // runwayDateTime
        "\\s*(\\S*)?" + SEP +                       // runwayDateTimeFlag
        "\\s*("+Patterns.DATE_TIME+")?\\s*";        // gateDateTime
    protected static final String TEXT_RECORD_PATTERN_NON_CAPTURING =
        "\\s*(?:\\S*)\\s*" + SEP +                    // airport
        "\\s*(?:"+Patterns.FLOAT+")?\\s*" + SEP +     // airportLocation (latitude)
        "\\s*(?:"+Patterns.FLOAT+")?\\s*" + SEP +     // airportLocation (longitude)
        "\\s*(?:"+Patterns.FLOAT+")?\\s*" + SEP +     // airportLocation (elevation)
        "\\s*(?:"+Patterns.DATE_TIME+")?\\s*" + SEP + // scheduledDateTime
        "\\s*(?:"+Patterns.BOOLEAN+")\\s*" + SEP +    // scheduledFlag
        "\\s*(?:"+Patterns.DATE_TIME+")?\\s*" + SEP + // runwayDateTime
        "\\s*(?:\\S*)?" + SEP +                       // runwayDateTimeFlag
        "\\s*(?:"+Patterns.DATE_TIME+")?\\s*";        // gateDateTime
    
    //-----------------------
    // Instance class members
    //-----------------------
    private Integer countryCode;
    private String airportName;
    private GCPointAlt airportLocation;
    
    private Boolean scheduled = null;
    private Timestamp scheduledDateTime = null;
    private Timestamp gateDateTime = null;
    private Timestamp runwayDateTime = null;
    private String runwayDateTimeFlag = null;
        
    /**
     * Default constructor
     */
    public Terminus()
    {
    }
    
    public Terminus(Terminus terminus) 
    {
		this.copy(terminus);
	}

	@Override
    public boolean equals(Object o)
    {
        if (!(o instanceof Terminus))
        {
            return false;
        }
        
        Terminus other = (Terminus)o;
        
        return (this.countryCode == null ? other.countryCode == null : this.countryCode.equals(other.countryCode))
            && (this.airportName == null ? other.airportName == null : this.airportName.equals(other.airportName))
            && (this.airportLocation == null ? other.airportLocation == null : this.airportLocation.equals(other.airportLocation))
            && (this.scheduled == null ? other.scheduled == null : this.scheduled.equals(other.scheduled))
            && (this.scheduledDateTime == null ? other.scheduledDateTime == null : this.scheduledDateTime.equals(other.scheduledDateTime))
            && (this.gateDateTime == null ? other.gateDateTime == null : this.gateDateTime.equals(other.gateDateTime))
            && (this.runwayDateTime == null ? other.runwayDateTime == null : this.runwayDateTime.equals(other.runwayDateTime))
            && (this.runwayDateTimeFlag == null ? other.runwayDateTimeFlag == null : this.runwayDateTimeFlag.equals(other.runwayDateTimeFlag));
    }
    
    /** 
     * Gets the country code.
     */
    public Integer countryCode()
    {
    	return this.countryCode;
    }
    
    /**
     * Sets the country code.
     */
    public void setCountryCode(Integer countryCode)
    {
    	this.countryCode = countryCode;
    }

    /**
     * Gets the airport name.
     */
    public String airportName()
    {
        return this.airportName;
    }

    /**
     * Sets the airport name.
     */
    public void setAirportName(String airportName)
    {
        this.airportName = airportName;
    }

    /**
     * Gets the airport location.
     */
    public GCPointAlt airportLocation()
    {
        return this.airportLocation;
    }
    
    /**
     * Sets the airport location.
     */
    public void setAirportLocation(GCPointAlt location)
    {
        this.airportLocation = location;
    }

    /**
     * Gets the scheduled flag.
     */
    public Boolean scheduledFlag()
    {
        return this.scheduled;
    }
    
    /**
     * Sets the scheduled flag.
     */
    public void setScheduledFlag(Boolean scheduled)
    {
        this.scheduled = scheduled;
    }
    
    /**
     * Gets the gate date/time.
     */
    public Timestamp gateDateTime()
    {
        return this.gateDateTime;
    }

    /**
     * Sets the gate date/time.
     */
    public void setGateDateTime(Timestamp dateTime)
    {
        this.gateDateTime = dateTime;
    }

    /**
     * Gets the runway date/time.
     */
    public Timestamp runwayDateTime()
    {
        return this.runwayDateTime;
    }

    /**
     * Sets the runway date/time.
     */
    public void setRunwayDateTime(Timestamp dateTime)
    {
        this.runwayDateTime = dateTime;
    }

    /**
     * Gets the runway date/time flag.
     */
    public String runwayDateTimeFlag()
    {
        return this.runwayDateTimeFlag;
    }

    /**
     * Sets the runway date/time flag.
     */
    public void setRunwayDateTimeFlag(String flag)
    {
        this.runwayDateTimeFlag = flag;
    }

    /**
     * Gets the scheduled date/time.
     */
    public Timestamp scheduledDateTime()
    {
        return this.scheduledDateTime;
    }

    /**
     * Sets the scheduled date/time.
     */
    public void setScheduledDateTime(Timestamp dateTime)
    {
        this.scheduledDateTime = dateTime;
    }

    /**
     * Shifts all date/times by the given amount.  
     */
    public void shiftTimes(double minutes)
    {
        // shift all of the times by the input number of minutes
        if (this.gateDateTime != null)
        {
            this.gateDateTime = this.gateDateTime.minuteAdd(minutes);
        }
        if (this.runwayDateTime != null)
        {
            this.runwayDateTime = this.runwayDateTime.minuteAdd(minutes);
        }
        if (this.scheduledDateTime != null)
        {
            this.scheduledDateTime = this.scheduledDateTime.minuteAdd(minutes);
        }
    }
    
    @Override
	public String toString()
    {
        return       (this.airportName        == null ? "" : this.airportName) + SEP +
               " " + (this.airportLocation    == null ? "" : String.format("%1$1.4f", this.airportLocation.latitude().degrees())) + SEP +
               " " + (this.airportLocation    == null ? "" : String.format("%1$1.4f", this.airportLocation.longitude().degrees())) + SEP +
               " " + (this.airportLocation    == null ? "" : String.format("%1$1.0f", this.airportLocation.altitude().feet())) + SEP +
               " " + (this.scheduledDateTime  == null ? "" : this.scheduledDateTime.toString()) + SEP +
               " " + (this.scheduled          == null ? "" : this.scheduled) + SEP +
               " " + (this.runwayDateTime     == null ? "" : this.runwayDateTime.toString()) + SEP +
               " " + (this.runwayDateTimeFlag == null ? "" : this.runwayDateTimeFlag) + SEP +
               " " + (this.gateDateTime       == null ? "" : this.gateDateTime.toString());
    }

    public void copy(Terminus t)
    {
        this.airportLocation = (t.airportLocation == null ? null : t.airportLocation.clone());
        this.airportName = t.airportName;
        this.countryCode = (t.countryCode == null ? null : t.countryCode.intValue());
        this.gateDateTime = (t.gateDateTime == null ? null : t.gateDateTime.clone());
        this.runwayDateTime = (t.runwayDateTime == null ? null : t.runwayDateTime.clone());
        this.runwayDateTimeFlag= t.runwayDateTimeFlag;
        this.scheduled = t.scheduled;
        this.scheduledDateTime = (t.scheduledDateTime == null ? null : t.scheduledDateTime.clone());
    }
    
    @Override
    public Terminus clone()
    {
        return new Terminus(this);
    }
    

	@Override
	public ResourceType resourceType() {
		return ResourceType.AP;
	}

	@Override
	public long crossingTime() {
		return 0;
	}

	@Override
	public String name() {
		return this.airportName;
	}	
}