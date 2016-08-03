package gov.faa.ang.swac.scheduler.flight_data;

import gov.faa.ang.swac.common.datatypes.Timestamp;

/**
 * A Class representing all of the runway and gate times for the flight along with information on
 * how those times were set.
 * 
 * @author James Bonn
 * @version 1.0
 */
public class FlightTimesData
{
    private Timestamp filedGateOut;
    private Timestamp filedRunwayOff;
    private Timestamp filedRunwayOn;
    private Timestamp filedGateIn;

    private String filedGateOutType;
    private String filedRunwayOffType;
    private String filedRunwayOnType;
    private String filedGateInType;

    private Timestamp scheduledGateOut;
    private Timestamp scheduledGateIn;

    private double nominalTaxiOutSeconds;
    private double nominalTaxiInSeconds;

    /**
     * Default Constructor.
     */
    public FlightTimesData()
    {
    }

    /**
     * Set the gate out time.
     * 
     * @param filedGateOut
     */
    public void setFiledGateOut(Timestamp filedGateOut)
    {
        this.filedGateOut = filedGateOut;
    }

    /**
     * @return gate out time
     */
    public Timestamp getFiledGateOut()
    {
        return filedGateOut;
    }

    /**
     * Set the runway off time.
     * 
     * @param filedRunwayOff
     */
    public void setFiledRunwayOff(Timestamp filedRunwayOff)
    {
        this.filedRunwayOff = filedRunwayOff;
    }

    /**
     * @return runway off time
     */
    public Timestamp getFiledRunwayOff()
    {
        return filedRunwayOff;
    }

    /**
     * Set the runway on time.
     * 
     * @param filedRunwayOn
     */
    public void setFiledRunwayOn(Timestamp filedRunwayOn)
    {
        this.filedRunwayOn = filedRunwayOn;
    }

    /**
     * @return the runway on time
     */
    public Timestamp getFiledRunwayOn()
    {
        return filedRunwayOn;
    }

    /**
     * Set the gate in time.
     * 
     * @param filedGateIn
     */
    public void setFiledGateIn(Timestamp filedGateIn)
    {
        this.filedGateIn = filedGateIn;
    }

    /**
     * @return gate in time
     */
    public Timestamp getFiledGateIn()
    {
        return filedGateIn;
    }

    /**
     * Set the scheduled departure time.
     * 
     * @param scheduledGateOut
     */
    public void setScheduledGateOut(Timestamp scheduledGateOut)
    {
        this.scheduledGateOut = scheduledGateOut;
    }

    /**
     * @return the scheduled departure time
     */
    public Timestamp getScheduledGateOut()
    {
        return scheduledGateOut;
    }

    /**
     * Set the scheduled arrival time.
     * 
     * @param scheduledGateIn
     */
    public void setScheduledGateIn(Timestamp scheduledGateIn)
    {
        this.scheduledGateIn = scheduledGateIn;
    }

    /**
     * @return the scheduled arrival time
     */
    public Timestamp getScheduledGateIn()
    {
        return scheduledGateIn;
    }

    /**
     * Set the taxi out seconds.
     * 
     * @param nominalTaxiOutSeconds
     */
    public void setNominalTaxiOutSeconds(double nominalTaxiOutSeconds)
    {
        this.nominalTaxiOutSeconds = nominalTaxiOutSeconds;
    }

    /**
     * @return the taxi out seconds
     */
    public double getNominalTaxiOutSeconds()
    {
        return nominalTaxiOutSeconds;
    }

    /**
     * Set the taxi in seconds.
     * 
     * @param nominalTaxiInSeconds
     */
    public void setNominalTaxiInSeconds(double nominalTaxiInSeconds)
    {
        this.nominalTaxiInSeconds = nominalTaxiInSeconds;
    }

    /**
     * @return the taxi in seconds
     */
    public double getNominalTaxiInSeconds()
    {
        return nominalTaxiInSeconds;
    }

    /**
     * Set the type of data for the gate out time.
     * 
     * @param filedGateOutType
     */
    public void setFiledGateOutType(String filedGateOutType)
    {
        this.filedGateOutType = filedGateOutType;
    }

    /**
     * @return the type of data for the gate out time
     */
    public String getFiledGateOutType()
    {
        return filedGateOutType;
    }

    /**
     * Set the type of data for the runway off time.
     * 
     * @param filedRunwayOffType
     */
    public void setFiledRunwayOffType(String filedRunwayOffType)
    {
        this.filedRunwayOffType = filedRunwayOffType;
    }

    /**
     * @return the type of data for the runway off time
     */
    public String getFiledRunwayOffType()
    {
        return filedRunwayOffType;
    }

    /**
     * Set the type of data for the runway on time.
     * 
     * @param filedRunwayOnType
     */
    public void setFiledRunwayOnType(String filedRunwayOnType)
    {
        this.filedRunwayOnType = filedRunwayOnType;
    }

    /**
     * @return the type of data for the runway on time
     */
    public String getFiledRunwayOnType()
    {
        return filedRunwayOnType;
    }

    /**
     * Set the type of data for the gate in time.
     * 
     * @param filedGateInType
     */
    public void setFiledGateInType(String filedGateInType)
    {
        this.filedGateInType = filedGateInType;
    }

    /**
     * @return the type of data for the gate in time
     */
    public String getFiledGateInType()
    {
        return filedGateInType;
    }

    /**
     * @return true if either the scheduled gate out time or the scheduled gate in time is not null
     */
    public boolean isScheduled()
    {
        return (scheduledGateOut != null || scheduledGateIn != null);
    }
}
