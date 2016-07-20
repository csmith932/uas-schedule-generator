/**
 * Copyright "TBD", Metron Aviation & CSSI. All rights reserved. This computer Software was
 * developed with the sponsorship of the U.S. Government under Contract No. DTFAWA-10-D-00033, which
 * has a copyright license in accordance with AMS 3.5-13.(c)(1).
 */

package gov.faa.ang.swac.common.flightmodeling;


/**
 * Represents a flight plan, that is.
 * 
 * @author Sean Smitz - CSSI, Inc.
 */
public class FlightPlan implements Cloneable
{
    private static org.apache.log4j.Logger logger = org.apache.log4j.LogManager.getLogger(FlightPlan.class);

    // ---------------------
    // Static class members
    // ---------------------

    // toString related members
    public static final String SEP = ",";
    public static final String TEXT_RECORD_KEY = "FLIGHT_PLAN:\n" + Aircraft.TEXT_RECORD_KEY + "\n" + FlightLeg.TEXT_RECORD_KEY;

    // -----------------------
    // Instance class members
    // -----------------------
    private Aircraft aircraft;
    private FlightLeg flightLeg;
    
    public FlightPlan(ScheduleRecord scheduleRecord, boolean scheduleTimeOverride)
    {
        init(scheduleRecord, scheduleTimeOverride);
    }
    
    private FlightPlan(FlightPlan org) {
        this.aircraft = (org.aircraft == null ? null : org.aircraft.clone());
        this.flightLeg = (org.flightLeg == null ? null : org.flightLeg.clone());
    }
    
    public void clearScheduleRecord()
    {
    }


    /**
     * Gets the {@link Aircraft} for this {@link FlightPlan}.
     */
    public Aircraft aircraft()
    {
        return this.aircraft;
    }

    /**
     * Sets the {@link Aircraft} for this {@link FlightPlan}.
     */
    public void setAircraft(Aircraft aircraft)
    {
        this.aircraft = aircraft;
    }

    /**
     * Gets the {@link FlightLeg} for this {@link FlightPlan}.
     */
    public FlightLeg flightLeg()
    {
        return this.flightLeg;
    }

    /**
     * Sets the {@link Aircraft} for this {@link FlightPlan}.
     */
    public void setFlightLeg(FlightLeg flightLeg)
    {
        this.flightLeg = flightLeg;
    }

    @Override
    public boolean equals(Object o)
    {
        if (o instanceof FlightPlan)
        {
            FlightPlan flightPlan = (FlightPlan)o;
            if (this.aircraft == flightPlan.aircraft || this.aircraft.equals(flightPlan.aircraft) && 
                this.flightLeg == flightPlan.flightLeg || this.flightLeg.equals(flightPlan.flightLeg)) 
            { 
                return true; 
            }
        }
        return false;
    }


    @Override
    public FlightPlan clone()
    {
        return new FlightPlan(this);
    }

    public String toString()
    {
        return "FLIGHT_PLAN:\n" + 
            (this.aircraft() == null ? "" : this.aircraft().toString()) + "\n" + 
            (this.flightLeg() == null ? "" : this.flightLeg().toString());
    }

    private void init(ScheduleRecord scheduleRecord, boolean scheduleTimeOverride)
    {
        setAircraft(new Aircraft(scheduleRecord));
        setFlightLeg(new FlightLeg(scheduleRecord));
          
        TerminusDeparture departure = this.flightLeg().departure();
        if (departure.scheduledDateTime() != null && 
            departure.gateDateTime() != null && 
            departure.runwayDateTime() != null)
        {
			// Reset the gate-out time to the ETMS scheduled departure time, and shift runway-off time (preserving
			// calculated taxi time) if any of the following conditions hold:
			// - the scheduleTimeOverride is enabled
			// - the gate-out is listed as "ACTUAL"...
			// - the runway-off time is listed as "ACTUAL"...
            if (scheduleTimeOverride || 
            	(departure.gateDateTimeFlag() != null && departure.gateDateTimeFlag().contains("ACTUAL")) || 
                (departure.runwayDateTimeFlag() != null && departure.runwayDateTimeFlag().contains("ACTUAL")))
            {
                // Attempt to preserve the taxi time (gate-out time -> runway-off time)
				double taxiTime = Math.abs(departure.runwayDateTime().secDifference(departure.gateDateTime()));
                departure.setRunwayDateTime(departure.scheduledDateTime().secondAdd(taxiTime));
                departure.setGateDateTime(departure.scheduledDateTime());
                
                logger.debug("Schedule ID " + this.flightLeg().flightId() +
                    " departure gate flag (" + departure.gateDateTimeFlag() + ")" +
                    " or runway flag (" + departure.runwayDateTimeFlag() + ") is \"ACTUAL\"." +
                    " Setting gate-out time to ETMS Scheduled time(" + departure.scheduledDateTime() + ")." +
                    " Shifting dep runway time by " + taxiTime + " seconds.");
            } 
        }
    }
}