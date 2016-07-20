/**
 * Copyright "TBD", Metron Aviation & CSSI.  All rights reserved.
 * 
 * This computer Software was developed with the sponsorship of the U.S. Government
 * under Contract No. DTFAWA-10-D-00033, which has a copyright license in accordance with AMS 3.5-13.(c)(1).
 */

package gov.faa.ang.swac.common.flightmodeling.jni;


import gov.faa.ang.swac.common.datatypes.Altitude;
import gov.faa.ang.swac.common.datatypes.Timestamp;
import gov.faa.ang.swac.common.flightmodeling.Itinerary;
import gov.faa.ang.swac.common.flightmodeling.Itinerary.Type;
import gov.faa.ang.swac.common.random.RandomStream;
import gov.faa.ang.swac.common.flightmodeling.TerminusArrival;
import gov.faa.ang.swac.common.flightmodeling.TerminusDeparture;


public interface FlightPlan

{
    public Itinerary parentAirframe();
    
    public Integer flightId();
    public Type ItinType();
    public String flightNumber();
    public String carrierId();
    public String filedBadaAircraftType();
    public String filedEtmsAircraftType();
    public String atoUserClass();
    public Altitude filedAltitude();
    public TerminusDeparture departure();
    public TerminusArrival arrival();
    public String departureAirport();
    public String arrivalAirport();
    public Timestamp scheduledDepDateTimestamp();
    public Timestamp scheduledArrDateTimestamp();
    public Timestamp edct();
    public Route flightRoute();
    public TimeDistribution pushbackDist();
	public double turnaroundDistMean();
	public long drawTurnaroundHoldTime(RandomStream turnaroundRandomStream);
		

}
