/**
 * Copyright "TBD", Metron Aviation & CSSI.  All rights reserved.
 * 
 * This computer Software was developed with the sponsorship of the U.S. Government
 * under Contract No. DTFAWA-10-D-00033, which has a copyright license in accordance with AMS 3.5-13.(c)(1).
 */

package gov.faa.ang.swac.common;

import java.io.Serializable;
import java.util.Arrays;
import java.util.EnumMap;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import gov.faa.ang.swac.common.FlightPlanMessageLogger.Reason;
import gov.faa.ang.swac.common.datatypes.Altitude;
import gov.faa.ang.swac.common.datatypes.Timestamp;
import gov.faa.ang.swac.common.flightmodeling.Aircraft;
import gov.faa.ang.swac.common.flightmodeling.EquipmentSuffix;
import gov.faa.ang.swac.common.flightmodeling.FlightCancellationReason;
import gov.faa.ang.swac.common.flightmodeling.FlightLeg;
import gov.faa.ang.swac.common.flightmodeling.FlightTurnImpact;
import gov.faa.ang.swac.common.flightmodeling.FuelUsageRecord;
import gov.faa.ang.swac.common.flightmodeling.TrajectoryPoint;
import gov.faa.ang.swac.common.flightmodeling.fileio.FlightDigestRecord;



public final class FlightRecord implements Serializable, Comparable<FlightRecord>
{
	private static final Logger logger = LogManager.getLogger(FlightRecord.class);
	/**
	 * 
	 */
	private static final long serialVersionUID = 6315253143756149135L;
	
	public static final String VFR = "VFR";
	
	private Integer itineraryNumber = null; // this can be negative. keep as Integer.
	private int legNumber = Integer.MIN_VALUE;
	private int simAirframeId = Integer.MIN_VALUE; // obtained from Sim Airframe, which has int type.
	private int simFlightId = Integer.MIN_VALUE; // obtained from SimFlight, which has int type.
	
	private String region;
	
	// From Schedule File
	private Integer scheduleId = null;
	private String carrierId;
	private String flightId;
	private String filedFlightPlanType = null;
	
	private String filedEtmsAircraftType = null;
	private String filedBadaAircraftType = null;
	private String evolvedEtmsAircraftType = null;
	private String evolvedBadaAircraftType = null;
	private String flownBadaAircraftType = null;
	
	private String filedEtmsAircraftCategory = null;
	private String evolvedEtmsAircraftCategory = null;
	
	private EquipmentSuffix simEquipment = null;
	private boolean dayOverrideFlag = false;
	
	private String filedUserClass = null;
	private String filedAtoUserClass = null;
	private String filedDepAirport = null;
	private int filedDepAirportCountryCode = Integer.MIN_VALUE; //HK: merged_airport_data.txt shows no negative country codes. 
	private String filedArrAirport = null;
	private int filedArrAirportCountryCode = Integer.MIN_VALUE;
	private Timestamp filedGateOutTime = null;
	private Timestamp filedGateInTime = null;
	private Altitude filedCruiseAltitude = null;
	
	// From Smoothing/Trimming file
	private double smoothTime = Double.NaN;
	
	// From MAP file
	private String mapFlightRuleType = null;
	
	
	private double plannedRouteDistance = 0.0;
	private double finalRouteDistance = 0.0;
	
	private double plannedFirDistance = 0.0;
	private double finalFirDistance = 0.0;
	
	// From FUEL file
	private FuelUsageRecord FuelUsageRecord = null;
	
	// From FC file
	private double fcEnrouteTime = Double.NaN;
	
	//From TAM Log File
	private String sidSelected = null;
	private int sidFitness = Integer.MIN_VALUE;
	private String starSelected = null;
	private int starFitness = Integer.MIN_VALUE;
	private String iapSelected = null;
	
	// From GDP Flight Log
	private Timestamp assignedEdct = null;
	private Timestamp appliedEdct = null;
	
	// New simulation flight trace fields (1.7)
	//jrc EnumMap is the best way to handle Enum based indexing.
	private EnumMap<FlightPhase, String> simFlightPhaseResources = new EnumMap<FlightPhase, String>(FlightPhase.class);
	private EnumMap<FlightPhase, Timestamp> simFlightPhaseStartTimes = new EnumMap<FlightPhase, Timestamp>(FlightPhase.class);
	private EnumMap<FlightPhase, Timestamp> simFlightPhaseEndTimes = new EnumMap<FlightPhase, Timestamp>(FlightPhase.class);
	
	// Calculated from simulation flight trace fields (1.7)
	private Timestamp simGateOutTime = null;
	private Timestamp simWheelsOffTime = null;
	private Timestamp simWheelsOnTime = null;
	private Timestamp simGateInTime = null;
	
	private double simGateDelay = Double.NaN;
	private double simDepartureSurfaceDelay = Double.NaN;
	private double simArrivalSurfaceDelay = Double.NaN;
	private double simSectorDelay = Double.NaN;
	
	private double simDepartureTurnaroundDistributionValue = Double.NaN;
	private double simArrivalTurnaroundDistributionValue = Double.NaN;
	private double simPushbackDistributionValue = Double.NaN;
	
	private double simNominalTaxiOutTime = Double.NaN;
	private double simActualTaxiOutTime = Double.NaN;
	private double simTaxiOutDelay = Double.NaN;
	private double simNominalTaxiInTime = Double.NaN;
	private double simActualTaxiInTime = Double.NaN;
	private double simTaxiInDelay = Double.NaN;
	
	private double simRerouteClearanceQueuingDelay = Double.NaN;
	private double simRerouteClearanceServiceTime = Double.NaN;
	private double simRerouteClearanceTotalDelay = Double.NaN;
	private Timestamp rerouteClearanceQueueEnd = null;

	private String simDepartureParetoCurve;
	private double simWxEquipmentDepartureQueueDelay = Double.NaN;
	private String simDepartureFix = null;
	private double simDepartureFixDelay = Double.NaN;
	
	private Timestamp topOfClimbTime = null;
	private Timestamp topOfDescentTime = null;
	
	private String simArrivalParetoCurve;
	private String simArrivalFix = null;
	private double simArrivalFixDelay = Double.NaN;
	private double simWxEquipmentArrivalQueueDelay = Double.NaN;
	private StringBuilder simSectorTrace = new StringBuilder("");
	private double simTotalRestrictionDelay = Double.NaN;
	
	private Timestamp simLastSectorQueueStart = null;
	
	private boolean rerouteClearanceFlag = true;
	private boolean preDepartureRerouteFlag = false;
	private boolean rerouteFlag = false;
	
	private boolean departureRampBypassFlag = false;
	private double departureRampQueuingDelay = 0;	
	private double departureRampServiceTime = 0;
	private boolean arrivalRampBypassFlag = false;
	private double arrivalRampQueuingDelay = 0;	
	private double arrivalRampServiceTime = 0;
	
	// Schedule Output Fields
	
	// From BAD file
	private EnumMap<Reason, Boolean> flags = new EnumMap<Reason, Boolean>(Reason.class);
	
	// From Formatter bad flights file
	private boolean formatterOverflow = false;
	
	// From GDP flight log
	private boolean cancelledByGdp = false;
	
	// From Sim Engine output files
	private boolean simPurgedFlight = false;
	
	private boolean[] scheduleOutputFlags = new boolean[11];
	
	private FlightCancellationReason cancelled = null;
	
	private boolean validFlight = true;
	
	private boolean stepClimbEligible = false;
	
	// used to record TBFM entry and exit events to determine resource crossing times.
	// for TBFM the crossing time is equivalent to the absorbed delay.
	private Timestamp lastTbfmEntryTime = null;
	private String lastTbfmResourceId = null; // for validation only.
	private double totalTbfmDelay = 0.0; 
	
	private double unimpededRerouteDuration = 0; // minutes
	private int numSuccessfulReroutes = 0;
	
	// Airport turn impact.
	private FlightTurnImpact departureTurnImpact = FlightTurnImpact.NOT_AFFECTED;
	private FlightTurnImpact arrivalTurnImpact = FlightTurnImpact.NOT_AFFECTED;
	
	private boolean shortenedArrival;
	
	///
	/// Continuous Descent Approach
	///
	
	private String cdaCurveName;
	private double cdaProbability;
	private boolean isCdaSelected;
	
	// High Fidelity Oceanic Modeling
	private boolean oceanicFlag;
	private boolean oceanicRerouteFlag;
	private double oceanicDelay = 0;
	private double oceanicNominalFuelUsage = 0;
	private double oceanicActualFuelUsage = 0;
	private String oceanicRegionsTraversed;
	
	public boolean isValidFlight() {
		return validFlight;
	}
	
	public void setValidFlight(boolean validFlight) {
		this.validFlight = validFlight;
		//		if (!validFlight) // For now, don't clutter the debug log with anything except invalid flights
		//		{
		//			StringBuilder str = new StringBuilder();
		//			str.append("scheduleId=" + this.scheduleId + " being marked as ");
		//			str.append(validFlight ? "VALID" : "INVALID"); 
		//			str.append("\n");
		//			str.append(this.toString());
		//			logger.debug(str.toString());
		//		}
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("FlightRecord [itineraryNumber=");
		builder.append(itineraryNumber);
		builder.append(", legNumber=");
		builder.append(getLegNumber());
		builder.append(", simAirframeId=");
		builder.append(getSimAirframeId());
		builder.append(", simFlightId=");
		builder.append(getSimFlightId());
		builder.append(", scheduleId=");
		builder.append(scheduleId);
		builder.append(", carrierId=");
		builder.append(carrierId);
		builder.append(", flightId=");
		builder.append(flightId);
		builder.append(", filedFlightPlanType=");
		builder.append(filedFlightPlanType);
		builder.append(", filedEtmsAircraftType=");
		builder.append(filedEtmsAircraftType);
		builder.append(", filedBadaAircraftType=");
		builder.append(filedBadaAircraftType);
		builder.append(", evolvedEtmsAircraftType=");
		builder.append(evolvedEtmsAircraftType);
		builder.append(", evolvedBadaAircraftType=");
		builder.append(evolvedBadaAircraftType);
		builder.append(", flownBadaAircraftType=");
		builder.append(flownBadaAircraftType);
		builder.append(", filedEtmsAircraftCategory=");
		builder.append(filedEtmsAircraftCategory);
		builder.append(", evolvedEtmsAircraftCategory=");
		builder.append(evolvedEtmsAircraftCategory);
		builder.append(", simEquipment=");
		builder.append(simEquipment);
		builder.append(", filedUserClass=");
		builder.append(filedUserClass);
		builder.append(", filedAtoUserClass=");
		builder.append(filedAtoUserClass);
		builder.append(", filedDepAirport=");
		builder.append(filedDepAirport);
		builder.append(", filedDepAirportCountryCode=");
		builder.append(filedDepAirportCountryCode);
		builder.append(", filedArrAirport=");
		builder.append(filedArrAirport);
		builder.append(", filedArrAirportCountryCode=");
		builder.append(filedArrAirportCountryCode);
		builder.append(", filedGateOutTime=");
		builder.append(filedGateOutTime);
		builder.append(", filedGateInTime=");
		builder.append(filedGateInTime);
		builder.append(", filedCruiseAltitude=");
		builder.append(filedCruiseAltitude);
		builder.append(", smoothTime=");
		builder.append(smoothTime);
		builder.append(", mapFlightRuleType=");
		builder.append(mapFlightRuleType);
		
		builder.append(", plannedRouteDist=");
		builder.append(this.plannedRouteDistance);
		builder.append(", finalRouteDist=");
		builder.append(this.finalRouteDistance);
		
		builder.append(", plannedFirDist=");
		builder.append(this.plannedFirDistance);
		builder.append(", finalFirDist=");
		builder.append(this.finalFirDistance);
		
		builder.append(", FuelUsageRecord=");
		builder.append(FuelUsageRecord);
		builder.append(", fcEnrouteTime=");
		builder.append(fcEnrouteTime);
		builder.append(", sidSelected=");
		builder.append(sidSelected);
		builder.append(", sidFitness=");
		builder.append(sidFitness);
		builder.append(", starSelected=");
		builder.append(starSelected);
		builder.append(", starFitness=");
		builder.append(starFitness);
		builder.append(", iapSelected=");
		builder.append(iapSelected);		
		builder.append(", assignedEdct=");
		builder.append(assignedEdct);
		builder.append(", simFlightPhaseResources=");
		builder.append(Arrays.toString(simFlightPhaseResources.values().toArray()));
		builder.append(", simFlightPhaseStartTimes=");
		builder.append(Arrays.toString(simFlightPhaseStartTimes.values().toArray()));
		builder.append(", simFlightPhaseEndTimes=");
		builder.append(Arrays.toString(simFlightPhaseEndTimes.values().toArray()));
		builder.append(", simGateOutTime=");
		builder.append(simGateOutTime);
		builder.append(", simWheelsOffTime=");
		builder.append(simWheelsOffTime);
		builder.append(", simWheelsOnTime=");
		builder.append(simWheelsOnTime);
		builder.append(", simGateInTime=");
		builder.append(simGateInTime);
		builder.append(", simGateDelay=");
		builder.append(simGateDelay);
		builder.append(", simSurfaceDelay=");
		builder.append(simDepartureSurfaceDelay);
		builder.append(", simArrivalDelay=");
		builder.append(simArrivalSurfaceDelay);
		builder.append(", simSectorDelay=");
		builder.append(simSectorDelay);
		builder.append(", simDepartureTurnaroundDistributionValue=");
		builder.append(simDepartureTurnaroundDistributionValue);
		builder.append(", simArrivalTurnaroundDistributionValue=");
		builder.append(simArrivalTurnaroundDistributionValue);
		builder.append(", simPushbackDistributionValue=");
		builder.append(simPushbackDistributionValue);
		builder.append(", simNominalTaxiOutTime=");
		builder.append(simNominalTaxiOutTime);
		builder.append(", simActualTaxiOutTime=");
		builder.append(simActualTaxiOutTime);
		builder.append(", simNominalTaxiInTime=");
		builder.append(simNominalTaxiInTime);
		builder.append(", simActualTaxiInTime=");
		builder.append(simActualTaxiInTime);
		builder.append(", simDepartureFix=");
		builder.append(simDepartureFix);
		builder.append(", simDepartureFixDelay=");
		builder.append(simDepartureFixDelay);
		builder.append(", simArrivalFix=");
		builder.append(simArrivalFix);
		builder.append(", simArrivalFixDelay=");
		builder.append(simArrivalFixDelay);
		builder.append(", simWxEquipmentArrivalQueueDelay=");
		builder.append(simWxEquipmentArrivalQueueDelay);
		builder.append(", simSectorTrace=");
		builder.append(simSectorTrace);
		builder.append(", simTotalRestrictionDelay=");
		builder.append(simTotalRestrictionDelay);
		builder.append(", flags=");
		builder.append(Arrays.toString(flags.values().toArray()));
		builder.append(", formatterOverflow=");
		builder.append(formatterOverflow);
		builder.append(", cancelled=");
		builder.append((cancelled == null ? "" : cancelled.name()));
		builder.append(", simPurgedFlight=");
		builder.append(simPurgedFlight);
		builder.append(", scheduleOutputFlags=");
		builder.append(Arrays.toString(scheduleOutputFlags));
		builder.append(", validFlight=");
		builder.append(validFlight);
		builder.append(", originalFlight=");
		builder.append(originalFlight);
		builder.append("]");
		return builder.toString();
	}
	
	private boolean originalFlight = true;
	
	public FlightRecord()
	{
		for (Reason s : Reason.values()) {
		    flags.put(s, false);
		}
				
		for (int i = 0; i < scheduleOutputFlags.length; i++)
		{
			scheduleOutputFlags[i] = false;
		}
	}
	
	public FlightRecord(Integer scheduleId)
	{
		this();
		this.scheduleId = scheduleId;
	}
	
	public FlightRecord(FlightLeg flightLeg, Aircraft aircraft)
	{
		this();
		initFlightLeg(flightLeg);
		initAircraft(aircraft);
	}
	
	public void initFlightLeg(FlightLeg flightLeg)
	{
		scheduleId = flightLeg.flightId();
		flightId = flightLeg.flightNumber();
		filedFlightPlanType = flightLeg.filedFlightPlanType();
		filedDepAirport = flightLeg.departure().airportName();
		filedDepAirportCountryCode = flightLeg.departure().countryCode() == null ? Integer.MIN_VALUE : flightLeg.departure().countryCode();
		filedArrAirport = flightLeg.arrival().airportName();
		filedArrAirportCountryCode = flightLeg.arrival().countryCode() == null ? Integer.MIN_VALUE : flightLeg.arrival().countryCode();
		
		filedCruiseAltitude = flightLeg.filedAltitude();
	}
	
	public void initAircraft(Aircraft aircraft)
	{
		carrierId = aircraft.carrierId();
		filedBadaAircraftType = aircraft.filedBadaAircraftType();
		filedEtmsAircraftType = aircraft.filedEtmsAircraftType();
		filedUserClass = aircraft.userClass();
		filedAtoUserClass = aircraft.atoUserClass();
	}
	
	public String filedAircraftId()
	{
		return carrierId + flightId;
	}
	
	/**
	 * Processes FlightRecord fields as needed.
	 * 
	 * This method should be called when all simulation fields have been loaded
	 * from the flight trace file.
	 */
	public void processFlightRecord()
	{
		// Sim OOOI times
		Timestamp simDepGateQueueEndTime = simFlightPhaseEndTimes.get(FlightPhase.DEPARTURE_GATE_QUEUE);
		if (simDepGateQueueEndTime != null)
		{
			simGateOutTime = simDepGateQueueEndTime;
		}
		
		Timestamp simDepartureEndTime = simFlightPhaseEndTimes.get(FlightPhase.DEPARTURE);
		if (simDepartureEndTime != null)
		{
			simWheelsOffTime = simDepartureEndTime;
		}
		
		if (simFlightPhaseEndTimes.get(FlightPhase.ARRIVAL) != null)
		{
			simWheelsOnTime = simFlightPhaseEndTimes.get(FlightPhase.ARRIVAL);
		}
		
		if (simFlightPhaseEndTimes.get(FlightPhase.ARR_GATE) != null)
		{
			simGateInTime = simFlightPhaseEndTimes.get(FlightPhase.ARR_GATE);
		}
		
		// Gate delay calculation
		if (simGateOutTime != null && filedGateOutTime != null)
		{
			simGateDelay = simGateOutTime.minDifference(filedGateOutTime);
		}
		
		// Surface delay calculation
		Timestamp departureRampQueueStartTime = simFlightPhaseStartTimes.get(FlightPhase.DEPARTURE_RAMP_QUEUE);
		Timestamp departureRampQueueEndTime = simFlightPhaseEndTimes.get(FlightPhase.DEPARTURE_RAMP_QUEUE);
		if (departureRampQueueStartTime != null && departureRampQueueEndTime != null) {
			departureRampQueuingDelay = departureRampQueueEndTime.minDifference(departureRampQueueStartTime);
		}
		
		Timestamp arrivalRampQueueStartTime = simFlightPhaseStartTimes.get(FlightPhase.ARRIVAL_RAMP_QUEUE);
		Timestamp arrivalRampQueueEndTime = simFlightPhaseEndTimes.get(FlightPhase.ARRIVAL_RAMP_QUEUE);
		if (arrivalRampQueueStartTime != null && arrivalRampQueueEndTime != null) {
			arrivalRampQueuingDelay = arrivalRampQueueEndTime.minDifference(arrivalRampQueueStartTime);
		}
				
		Timestamp taxiOutStartTime = simFlightPhaseStartTimes.get(FlightPhase.TAXI_OUT);
		Timestamp taxiOutEndTime = simFlightPhaseEndTimes.get(FlightPhase.TAXI_OUT);
		
		double taxiOutDuration = 0.0;
		if (taxiOutStartTime != null && taxiOutEndTime != null)
		{
			taxiOutDuration = taxiOutEndTime.minDifference(taxiOutStartTime);
			simActualTaxiOutTime = taxiOutDuration;
			
			if (!Double.isNaN(simNominalTaxiOutTime)) {
				simTaxiOutDelay = simActualTaxiOutTime - simNominalTaxiOutTime;
			}
		}
		
		Timestamp rerouteClearanceStartTime = simFlightPhaseStartTimes.get(FlightPhase.REROUTE_CLEARANCE);
		Timestamp rerouteClearanceEndTime = simFlightPhaseEndTimes.get(FlightPhase.REROUTE_CLEARANCE);
		if (rerouteClearanceStartTime != null && rerouteClearanceEndTime != null) {
			simRerouteClearanceTotalDelay = rerouteClearanceEndTime.minDifference(rerouteClearanceStartTime);
			simRerouteClearanceQueuingDelay = simRerouteClearanceTotalDelay - simRerouteClearanceServiceTime;
			rerouteClearanceQueueEnd = rerouteClearanceStartTime.minuteAdd(simRerouteClearanceQueuingDelay);
		}
		
		if (simWheelsOffTime != null && simGateOutTime != null)
		{
			simDepartureSurfaceDelay = simWheelsOffTime.minDifference(simGateOutTime) - simNominalTaxiOutTime - departureRampServiceTime;
		}
		
		if (simGateInTime != null && simWheelsOnTime != null)
		{
			simArrivalSurfaceDelay = simGateInTime.minDifference(simWheelsOnTime) - simNominalTaxiInTime - arrivalRampServiceTime;
		}
		
		// Remaining distribution values
		Timestamp turnaroundStartTime = simFlightPhaseStartTimes.get(FlightPhase.DEP_TURNAROUND);
		Timestamp turnaroundEndTime = simFlightPhaseEndTimes.get(FlightPhase.DEP_TURNAROUND);
		if (turnaroundEndTime != null && turnaroundStartTime != null)
		{
			simDepartureTurnaroundDistributionValue = turnaroundEndTime.minDifference(turnaroundStartTime);
		}
		turnaroundStartTime = simFlightPhaseStartTimes.get(FlightPhase.ARR_TURNAROUND);
		turnaroundEndTime = simFlightPhaseEndTimes.get(FlightPhase.ARR_TURNAROUND);
		if (turnaroundEndTime != null && turnaroundStartTime != null)
		{
			simArrivalTurnaroundDistributionValue = turnaroundEndTime.minDifference(turnaroundStartTime);
		}
		
		Timestamp taxiInStartTime = simFlightPhaseStartTimes.get(FlightPhase.TAXI_IN);
		Timestamp taxiInEndTime = simFlightPhaseEndTimes.get(FlightPhase.TAXI_IN);
		if (taxiInStartTime != null && taxiInEndTime != null)
		{
			simActualTaxiInTime = taxiInEndTime.minDifference(taxiInStartTime);
			
			if (!Double.isNaN(simNominalTaxiInTime)) {
				simTaxiInDelay = simActualTaxiInTime - simNominalTaxiInTime;
			}
		}
		
		// Sim fix information
		simDepartureFix = simFlightPhaseResources.get(FlightPhase.DEPARTURE_FIX);
		Timestamp depFixStartTime = simFlightPhaseStartTimes.get(FlightPhase.DEPARTURE_FIX);
		Timestamp depFixEndTime = simFlightPhaseEndTimes.get(FlightPhase.DEPARTURE_FIX);
		if (depFixStartTime != null && depFixEndTime != null)
		{
			simDepartureFixDelay = depFixEndTime.minDifference(depFixStartTime);
		}
		
		simArrivalFix = simFlightPhaseResources.get(FlightPhase.ARRIVAL_FIX);
		Timestamp arrFixStartTime = simFlightPhaseStartTimes.get(FlightPhase.ARRIVAL_FIX);
		Timestamp arrFixEndTime = simFlightPhaseEndTimes.get(FlightPhase.ARRIVAL_FIX);
		if (arrFixStartTime != null && arrFixEndTime != null)
		{
			simArrivalFixDelay = arrFixEndTime.minDifference(arrFixStartTime);
		}
		
		// Sector enroute time
		// insert top of climb and top of descent here
		
		
		Timestamp sectorStartTime = null, sectorEndTime = null;
		
		if (simDepartureFix != null)
		{
			sectorStartTime = simFlightPhaseEndTimes.get(FlightPhase.DEPARTURE_FIX);
		}
		else
		{
			sectorStartTime = simFlightPhaseEndTimes.get(FlightPhase.DEPARTURE);
		}
		
		if (simArrivalFix != null)
		{
			sectorEndTime = simFlightPhaseStartTimes.get(FlightPhase.ARRIVAL_FIX);
		}
		else
		{
			sectorEndTime = simFlightPhaseStartTimes.get(FlightPhase.ARRIVAL);
		}
		
		simFlightPhaseStartTimes.put(FlightPhase.SECTOR_QUEUE, sectorStartTime);
		simFlightPhaseEndTimes.put(FlightPhase.SECTOR_QUEUE, sectorEndTime);
		
		// assigned EDCT time
		Timestamp edctGateStartTime = simFlightPhaseStartTimes.get(FlightPhase.EDCT_GATE);
		Timestamp edctGateEndTime = simFlightPhaseEndTimes.get(FlightPhase.EDCT_GATE);
		double edctGateDuration = 0;
		if (edctGateStartTime != null && edctGateEndTime != null) {
			edctGateDuration = edctGateEndTime.minDifference(edctGateStartTime);
		}
		
		Timestamp edctSurfaceStartTime = simFlightPhaseStartTimes.get(FlightPhase.EDCT_SURFACE);
		Timestamp edctSurfaceEndTime = simFlightPhaseEndTimes.get(FlightPhase.EDCT_SURFACE);
		double edctSurfaceDuration = 0;
		
		if (edctSurfaceStartTime != null && edctSurfaceEndTime != null) {
			edctSurfaceDuration = edctSurfaceEndTime.minDifference(edctSurfaceStartTime);
		}
		
		double edctDelay = edctGateDuration + edctSurfaceDuration;
		if (edctDelay > 0) {
			appliedEdct = filedGateOutTime.minuteAdd(edctDelay);
		}
	}
	
	/** This is a total hack and needs to be done properly later. */
	public void setScheduleOutputFlags()
	{
		int idx = findFirstBadScheduleOutputFlag();
		
		if (idx >= 0)
		{
			scheduleOutputFlags[idx] = true;
			setValidFlight(false);
		}
	}
	
	private int findFirstBadScheduleOutputFlag()
	{
		if (flags.get(Reason.TRIMMED)) {
			return 0;
		}
		
		int shortFlightIdx = 1;
		int missingAirportIdx = shortFlightIdx + 1;
		int missingTimesIdx = missingAirportIdx + 1;
		int invalidTimesIdx = missingTimesIdx + 1;
		int missingBadBadaIdx = invalidTimesIdx + 1;
		int notVfrModeledIdx = missingBadBadaIdx + 1;
		int invalidFlightPathIdx = notVfrModeledIdx + 1;
		int ifrRemovedOtherIdx = invalidFlightPathIdx + 1;

		if (flags.get(Reason.IFR_SHORT_FLIGHT))
		{
			return shortFlightIdx;
		}
		
		if (flags.get(Reason.MISSING_AIRPORTS) ||
				flags.get(Reason.MISSING_AIRPORT_COORDINATES) ||
				flags.get(Reason.IFR_MISSING_DEP_AIRPORT) ||
				flags.get(Reason.IFR_MISSING_ARR_AIRPORT) ||
				flags.get(Reason.IFR_MISSING_DEP_LOCATION) ||
				flags.get(Reason.IFR_MISSING_ARR_LOCATION))
		{
			return missingAirportIdx;
		}
		
		if (flags.get(Reason.MISSING_DEP_ARR_TIMES) ||
				flags.get(Reason.IFR_MISSING_DEP_DATE_TIMES))
		{
			return missingTimesIdx;
		}
		
		if (flags.get(Reason.DEP_TIME_INVALID) ||
				flags.get(Reason.ARR_TIME_INVALID) ||
				flags.get(Reason.ENROUTE_TIME_INVALID))
		{
			return invalidTimesIdx;
		}
		
		if (flags.get(Reason.IFR_MISSING_BADA))
		{
			return missingBadBadaIdx;
		}
		
		if (filedFlightPlanType.contains(VFR))
		{
			if (flags.get(Reason.VFR_DEP_AIRPORT_NOT_ALLOWED) ||
					flags.get(Reason.VFR_ARR_AIRPORT_NOT_ALLOWED))
			{
				return notVfrModeledIdx;
			}
		}
		else
		{
			if (flags.get(Reason.VFR_DEP_AIRPORT_NOT_ALLOWED) ||
					flags.get(Reason.VFR_ARR_AIRPORT_NOT_ALLOWED))
			{
				return notVfrModeledIdx;
			}
		}
		
		if (flags.get(Reason.IFR_INVALID_FLIGHT_PATH))
		{
			return invalidFlightPathIdx;
		}
		
		if (flags.get(Reason.IFR_REMOVED) || 
				flags.get(Reason.IFR_INVALID_FILED_ALT) ||
				flags.get(Reason.IFR_ROUND_ROBIN) ||
				flags.get(Reason.OUTSIDE_BOX))
		{
			return ifrRemovedOtherIdx;
		}
		
		int canceledIdx = ifrRemovedOtherIdx + 1;
		if (cancelled != null) {
			return canceledIdx;
		}
		
		int simWheelsOnIdx = canceledIdx + 1;
		if (simWheelsOnTime == null) {
			return simWheelsOnIdx;
		}
		
		return -1;
	}
	
	@Override
	public int compareTo(FlightRecord o) {
		return scheduleId.compareTo(o.scheduleId);
	}
	
	public Timestamp getTopOfClimbTime() {
		return topOfClimbTime;
	}

	public void setTopOfClimbTime(Timestamp topOfClimbTime) {
		this.topOfClimbTime = topOfClimbTime;
	}

	public Timestamp getTopOfDescentTime() {
		return topOfDescentTime;
	}

	public void setTopOfDescentTime(Timestamp topOfDescentTime) {
		this.topOfDescentTime = topOfDescentTime;
	}

	public Timestamp getRerouteClearanceQueueEnd() {
		return this.rerouteClearanceQueueEnd;
	}
	
	public void setLastTbfmEntryTime(Timestamp entryTime, String resourceId){
		this.lastTbfmEntryTime = entryTime;
		this.lastTbfmResourceId = resourceId;
	}
	
	/**
	 * 
	 * @param exitTime
	 * @param resourceId
	 * @return minutes of TBFM delay at provided resourceId
	 */
	public double recordTbfmDelay(Timestamp exitTime, String resourceId){
		assert(this.lastTbfmResourceId.equals(resourceId));
		double minDelay = exitTime.minDifference(this.lastTbfmEntryTime);
		this.totalTbfmDelay += minDelay;
		
		// reset
		this.lastTbfmResourceId = null;
		this.lastTbfmEntryTime = null;
		
		return minDelay;
	}
	
	public void setDepartureTurnImpact(FlightTurnImpact impact) {
		this.departureTurnImpact = impact;
	}
	
	public void setArrivalTurnImpact(FlightTurnImpact impact) {
		this.arrivalTurnImpact = impact;
	}

	/**
	 * Sets unimpeded reroute duration by summing up node crossing times and time-to-next-node times.
	 * Also increments number of successful reroute tries by 1.
	 * 
	 * @param newFlightLeg remodeled FlightLeg with updated resource crossing times and time-to-next nodes
	 */
	public void updateForReroute(FlightLeg newFlightLeg){
		
		// did flight get a shortened arrival route?
		this.shortenedArrival = newFlightLeg.hasShortenedArrival();

		// summing up node transit times is more accurate than calling newFlightLeg.getEnRouteTime()
		// since a reroute occuring in the middle of a flight may have already accrued delay, skewing the getEnRouteTime() value.
		long unimpededTransitTime = 0;
		for (TrajectoryPoint pt : newFlightLeg.flightRoute()){
			unimpededTransitTime += pt.timeToNextNode();
			unimpededTransitTime += pt.nodeCrossingTime();
		}
		
		Pair<Double,Double> distances = newFlightLeg.totalRouteDistances();
		this.finalRouteDistance = distances.getFirst();
		this.finalFirDistance = distances.getSecond();

		this.FuelUsageRecord.updateForReroute(newFlightLeg.fuelUsage);
		this.unimpededRerouteDuration = (double)unimpededTransitTime / (double)Timestamp.MILLISECS_MIN;
		++this.numSuccessfulReroutes;
	}
	

	public Timestamp getAssignedEdct() {
		return assignedEdct;
	}
	
	public void setAssignedEdct(Timestamp assignedEdct) { 
		this.assignedEdct = assignedEdct;
	}
	
	public Timestamp getAppliedEdct() {
		return appliedEdct;
	}
	
	public void setAppliedEdct(Timestamp appliedEdct) { 
		this.appliedEdct = appliedEdct;
	}
	
	
	/**
	 * 
	 * @return total minutes of crossing time from all TBFM Resource crossings.
	 */
	public double totalTbfmDelay(){
		return this.totalTbfmDelay;
	}
	
	/**
	 * Sum of resource transit times and time-to-next node for all waypoints in rerouted flight.
	 * 
	 * @return last successful reroute duration in minutes, zero if no reroute performed.
	 */
	public double unimpededRerouteDuration(){
		return this.unimpededRerouteDuration;
	}

	/**
	 * 
	 * @return number of successful reroutes.
	 */
	public int successfulReroutes(){
		return this.numSuccessfulReroutes;
	}

	public String getSimDepartureParetoCurve() {
		return simDepartureParetoCurve;
	}

	public void setSimDepartureParetoCurve(String simDepartureParetoCurve) {
		this.simDepartureParetoCurve = simDepartureParetoCurve;
	}

	public String getSimArrivalParetoCurve() {
		return simArrivalParetoCurve;
	}

	public void setSimArrivalParetoCurve(String simArrivalParetoCurve) {
		this.simArrivalParetoCurve = simArrivalParetoCurve;
	}
	
	public void loadFlightDigestRecord(FlightDigestRecord rec, Timestamp startDate) { 
		FuelUsageRecord fuelUsage = FuelUsageRecord;
		
		double simulatedAirborneTime = Double.NaN;
		if (simWheelsOffTime != null && simWheelsOnTime != null)
		{
			simulatedAirborneTime = simWheelsOnTime.minDifference(simWheelsOffTime);
		}
		
		double simulatedAirborneDelay = 0.0; //HK: this was set to null before but we have no null check downstream...
		if (!Double.isNaN(simulatedAirborneTime) && !Double.isNaN(fcEnrouteTime))
		{
			simulatedAirborneDelay = simulatedAirborneTime - fcEnrouteTime;
		}
		
		rec.setItineraryNumber(itineraryNumber);
		rec.setRegion(region);
		//re-introduce legNumber for convenience
		rec.setFlightLegNumber(getLegNumber());
		rec.setScheduleId(scheduleId);
		rec.setSimAirframeId(simAirframeId);
		rec.setSimFlightId(simFlightId);
		rec.setFiledAircraftId(filedAircraftId());
		// Filed ETMS type can differ between several input schedules that are combined into a single itinerary
		// with a single aircraft record.
		// 9/5/2011 JC: the above is okay, as the field has been renamed to "filed_itinerary_etms_ac_type" to reflect
		// the original pre-FE ETMS aircraft type, not the actual scheduled ETMS aircraft type
		rec.setFiledEtmsAircraftType(filedEtmsAircraftType);
		rec.setFiledBadaAircraftType(filedBadaAircraftType);
		rec.setEvolvedEtmsAircraftType(evolvedEtmsAircraftType);
		rec.setEvolvedBadaAircraftType(evolvedBadaAircraftType);
		// report the actual flown BADA aircraft type; set to BadaRecord.getAircraftType()
		rec.setFlownBadaAircraftType(flownBadaAircraftType);
	    rec.setSimEquipment(simEquipment);
		rec.setDayOverrideFlag(dayOverrideFlag);
		rec.setRerouteClearanceFlag(rerouteClearanceFlag);
		rec.setPreDepartureRerouteFlag(preDepartureRerouteFlag);
		rec.setRerouteFlag(rerouteFlag);
		rec.setFiledEtmsAircraftCategory(filedEtmsAircraftCategory);
		rec.setEvolvedEtmsAircraftCategory(evolvedEtmsAircraftCategory);
		rec.setFiledUserClass(filedUserClass);
		rec.setFiledAtoUserClass(filedAtoUserClass);
		rec.setFiledDepAirport(filedDepAirport);
		rec.setFiledDepAirportCountryCode(filedDepAirportCountryCode);
		rec.setFiledArrAirport(filedArrAirport);
		rec.setFiledArrAirportCountryCode(filedArrAirportCountryCode);
		rec.setStartDate(startDate);
		rec.setFiledGateOutTime(filedGateOutTime);
		rec.setFiledGateInTime(filedGateInTime);
		rec.setAssignedEdct(getAssignedEdct());
		rec.setAppliedEdct(getAppliedEdct());
		rec.setSimGateOutTime(simGateOutTime);
		rec.setSimWheelsOffTime(simWheelsOffTime);
		rec.setSimWheelsOnTime(simWheelsOnTime);
		rec.setSimGateInTime(simGateInTime);

		Double blockTime = (filedGateOutTime == null || filedGateInTime == null ? null :
						filedGateInTime.minDifference(filedGateOutTime));
		rec.setBlockTime(blockTime);
		rec.setFcEnrouteTime(fcEnrouteTime);
		rec.setRerouteEnrouteTime(unimpededRerouteDuration());
		rec.setSimulatedAirborneTime(simulatedAirborneTime);
		rec.setSimulatedAirborneDelay(simulatedAirborneDelay);
		rec.setTbfmDelay(totalTbfmDelay());
		rec.setDepartureTurnImpact(departureTurnImpact);
		rec.setArrivalTurnImpact(arrivalTurnImpact);

		Timestamp topOfClimb = getTopOfClimbTime();
		Double topOfClimbMins = topOfClimb == null ? null : topOfClimb.minDifference(startDate);
		rec.setTopOfClimb(topOfClimbMins);
		Timestamp topOfDescent = getTopOfDescentTime();
		Double topOfDescentMins = topOfDescent == null ? null : topOfDescent.minDifference(startDate);
		rec.setTopOfDescent(topOfDescentMins);
			
		for (FlightPhase flightPhase : FlightPhase.values())
		{
			Double startTimeSimMins = timeToDecimalMinutes(simFlightPhaseStartTimes.get(flightPhase), startDate);
			Double endTimeSimMins = timeToDecimalMinutes(simFlightPhaseEndTimes.get(flightPhase), startDate);
			rec.setFlightPhaseStartAndEndTime(flightPhase, startTimeSimMins, endTimeSimMins);
		}

		rec.setSimGateDelay(simGateDelay); // gate delay
		rec.setSimDepartureSurfaceDelay(simDepartureSurfaceDelay); // Dep surface delay
		rec.setSimArrivalSurfaceDelay(simArrivalSurfaceDelay); // Arr surface delay
		rec.setSimSectorDelay(simSectorDelay); // Sector delay

		rec.setSimDepartureTurnaroundDistributionValue(simDepartureTurnaroundDistributionValue); // Departure turnaround distribution value
		rec.setSimArrivalTurnaroundDistributionValue(simArrivalTurnaroundDistributionValue); // Arrival turnaround distribution value
		rec.setSimPushbackDistributionValue(simPushbackDistributionValue); // Pushback distribution value

		rec.setDepartureRampName(simFlightPhaseResources.get(FlightPhase.DEPARTURE_RAMP_QUEUE));
		rec.setDepartureRampQueuingDelay(departureRampQueuingDelay);
		rec.setDepartureRampServiceTime(departureRampServiceTime);
		rec.setDepartureRampBypassFlag(departureRampBypassFlag);
		rec.setArrivalRampName(simFlightPhaseResources.get(FlightPhase.ARRIVAL_RAMP_QUEUE));
		rec.setArrivalRampQueuingDelay(arrivalRampQueuingDelay);
		rec.setArrivalRampServiceTime(arrivalRampServiceTime);
		rec.setArrivalRampBypassFlag(arrivalRampBypassFlag);

		rec.setSimNominalTaxiOutTime(simNominalTaxiOutTime); // taxi out unimpeded
		rec.setSimActualTaxiOutTime(simActualTaxiOutTime); // Taxi out duration
		rec.setSimTaxiOutDelay(simTaxiOutDelay);
		rec.setSimNominalTaxiInTime(simNominalTaxiInTime); // Taxi in nominal value
		rec.setSimActualTaxiInTime(simActualTaxiInTime); // Taxi in actual value
		rec.setSimTaxiInDelay(simTaxiInDelay);

		rec.setSimRerouteClearanceQueuingDelay(simRerouteClearanceQueuingDelay);
		rec.setSimRerouteClearanceServiceTime(simRerouteClearanceServiceTime);
		rec.setSimRerouteClearanceTotalDelay(simRerouteClearanceTotalDelay);
		rec.setRerouteClearanceQueueEnd(timeToDecimalMinutes(getRerouteClearanceQueueEnd(), startDate));

		rec.setSimDepartureParetoCurve(getSimDepartureParetoCurve());	// departure pareto curve
		rec.setSimArrivalParetoCurve(getSimArrivalParetoCurve());		// arrival pareto curve
			
		rec.setSimDepartureFix(simDepartureFix); // Departure fix
		rec.setSimDepartureFixDelay(simDepartureFixDelay); // Departure fix delay
		rec.setSimArrivalFix(simArrivalFix); // Arrival fix
		rec.setSimArrivalFixDelay(simArrivalFixDelay); // Arrival fix delay
		rec.setSimTotalRestrictionDelay(simTotalRestrictionDelay); // Total Restriction delay
		
		rec.setOceanicFlag(oceanicFlag);
		rec.setOceanicRerouteFlag(oceanicRerouteFlag);
		rec.setOceanicDelay(oceanicDelay);
		rec.setOceanicRegionsTraversed(oceanicRegionsTraversed);
		
		rec.setPlannedRouteDist(this.plannedRouteDistance);
		rec.setActualRouteDist(this.finalRouteDistance);
		
		rec.setPlannedFirDist(this.plannedFirDistance);
		rec.setActualFirDist(this.finalFirDistance);
		
		if (fuelUsage != null){			
			rec.setPlannedClimbUsage(fuelUsage.getPlannedClimbUsage()); // Fuel file - climb
			rec.setNominalClimbUsage(fuelUsage.getNominalClimbUsage()); // Fuel file - climb
			rec.setActualClimbUsage(fuelUsage.getActualClimbFuelUsage()); // Fuel file - climb
			
			rec.setPlannedCruiseUsage(fuelUsage.getPlannedCruiseUsage()); // Fuel file - cruise
			rec.setNominalCruiseUsage(fuelUsage.getNominalCruiseUsage()); // Fuel file - cruise
			rec.setActualCruiseUsage(fuelUsage.getActualCruiseFuelUsage()); // Fuel file - cruise
			
			rec.setPlannedDescentUsage(fuelUsage.getPlannedDescentUsage()); // Fuel file - descent
			rec.setNominalDescentUsage(fuelUsage.getNominalDescentUsage()); // Fuel file - descent
			rec.setActualDescentUsage(fuelUsage.getActualDescentFuelUsage()); // Fuel file - descent
			
			rec.setCruiseUsageRate(fuelUsage.getCruiseUsageRate()); // Fuel file - cruise rate
			
			rec.setPlannedFirFuel(fuelUsage.getPlannedFirFuel()); // Fuel file - FIR fuel usage
			rec.setNominalFirFuel(fuelUsage.getNominalFirFuel()); // Fuel file - FIR fuel usage
			rec.setActualFirFuel(fuelUsage.getActualTotalFirFuel()); // Fuel file - FIR fuel usage
			
			rec.setPlannedFirTime(fuelUsage.getPlannedFirTime()); // Fuel file - FIR time
			rec.setNominalFirTime(fuelUsage.getNominalFirTime()); // Fuel file - FIR time
			rec.setActualFirTime(fuelUsage.getActualTotalFirTime()); // Fuel file - FIR time
			
			rec.setPlannedTotalFuelUsage(fuelUsage.getPlannedTotalFuelUsage()); // Fuel file - fuel usage
			rec.setNominalTotalFuelUsage(fuelUsage.getNominalTotalFuelUsage()); // Fuel file - fuel usage
			rec.setActualTotalFuelUsage(fuelUsage.getActualTotalFuelUsage()); // Fuel file - fuel usage
		}
		
		rec.setNominalOceanicFuelUsage(this.oceanicNominalFuelUsage);
		rec.setActualOceanicFuelUsage(this.oceanicActualFuelUsage);

		// Step Climb Eligibility
		rec.setStepClimbEligible(stepClimbEligible);
		
		rec.setShortenedArrival(shortenedArrival);
		
		// CDA
		rec.setCdaCurveName(cdaCurveName); // CDA probability curve
		rec.setCdaProbability(cdaProbability); // CDA probability curve
		rec.setCdaSelected(isCdaSelected); // Selected for CDA

		rec.setSidSelected(sidSelected);
		rec.setSidFitness(sidFitness);
		rec.setStarSelected(starSelected);
		rec.setStarFitness(starFitness);
		rec.setIapSelected(iapSelected);
 
		rec.setFiledCruiseAltitude(filedCruiseAltitude == null ? null : filedCruiseAltitude.feet()); // Filed cruise altitude
		rec.setSimSectorTrace(simSectorTrace.toString().trim());  // Sectors entered		
	}
	
	private Double timeToDecimalMinutes(Timestamp time, Timestamp simStart) {
		if (time == null)
			return null;
		return time.minDifference(simStart);
	}

	public Integer getItineraryNumber() {
		return itineraryNumber;
	}

	public void setItineraryNumber(Integer itineraryNumber) {
		this.itineraryNumber = itineraryNumber;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public int getLegNumber() {
		return legNumber;
	}

	public void setLegNumber(int legNumber) {
		this.legNumber = legNumber;
	}

	public Integer getScheduleId() {
		return scheduleId;
	}

	public void setScheduleId(Integer scheduleId) {
		this.scheduleId = scheduleId;
	}

	public int getSimAirframeId() {
		return simAirframeId;
	}

	public void setSimAirframeId(int simAirframeId) {
		this.simAirframeId = simAirframeId;
	}

	public int getSimFlightId() {
		return simFlightId;
	}

	public void setSimFlightId(int simFlightId) {
		this.simFlightId = simFlightId;
	}

	public void setEvolvedEtmsAircraftType(String evolvedEtmsAircraftType) {
		this.evolvedEtmsAircraftType = evolvedEtmsAircraftType;
	}

	public void setEvolvedBadaAircraftType(String evolvedBadaAircraftType) {
		this.evolvedBadaAircraftType = evolvedBadaAircraftType;
	}

	public void setFlownBadaAircraftType(String flownBadaAircraftType) {
		this.flownBadaAircraftType = flownBadaAircraftType;
	}

	public void setRerouteClearanceFlag(boolean rerouteClearanceFlag) {
		this.rerouteClearanceFlag = rerouteClearanceFlag;
	}

	public void setPreDepartureRerouteFlag(boolean preDepartureRerouteFlag) {
		this.preDepartureRerouteFlag = preDepartureRerouteFlag;
	}

	public void setRerouteFlag(boolean rerouteFlag) {
		this.rerouteFlag = rerouteFlag;
	}

	public void setDayOverrideFlag(boolean dayOverrideFlag) {
		this.dayOverrideFlag = dayOverrideFlag;
	}

	public void setSimEquipment(EquipmentSuffix simEquipment) {
		this.simEquipment = simEquipment;
	}

	public String getFiledEtmsAircraftCategory() {
		return filedEtmsAircraftCategory;
	}

	public void setFiledEtmsAircraftCategory(String filedEtmsAircraftCategory) {
		this.filedEtmsAircraftCategory = filedEtmsAircraftCategory;
	}

	public void setEvolvedEtmsAircraftCategory(
			String evolvedEtmsAircraftCategory) {
		this.evolvedEtmsAircraftCategory = evolvedEtmsAircraftCategory;
	}

	public String getFiledDepAirport() {
		return filedDepAirport;
	}

	public boolean isRerouteFlag() {
		return rerouteFlag;
	}

	public String getFiledArrAirport() {
		return filedArrAirport;
	}

	public Timestamp getFiledGateOutTime() {
		return filedGateOutTime;
	}

	public void setFiledGateOutTime(Timestamp filedGateOutTime) {
		this.filedGateOutTime = filedGateOutTime;
	}

	public void setFiledGateInTime(Timestamp filedGateInTime) {
		this.filedGateInTime = filedGateInTime;
	}

	public Timestamp getFiledGateInTime() {
		return filedGateInTime;
	}

	public Altitude getFiledCruiseAltitude() {
		return filedCruiseAltitude;
	}

	public void setFiledCruiseAltitude(Altitude filedCruiseAltitude) {
		this.filedCruiseAltitude = filedCruiseAltitude;
	}

	public Timestamp getSimWheelsOffTime() {
		return simWheelsOffTime;
	}

	public Timestamp getSimWheelsOnTime() {
		return simWheelsOnTime;
	}

	public double getFcEnrouteTime() {
		return fcEnrouteTime;
	}

	public void setFcEnrouteTime(double fcEnrouteTime) {
		this.fcEnrouteTime = fcEnrouteTime;
	}

	public Timestamp getSimFlightPhaseStartTime(FlightPhase phase) {
		return simFlightPhaseStartTimes.get(phase);
	}

	public void setSimFlightPhaseStartTime(FlightPhase phase, Timestamp simFlightPhaseStartTime) {
		this.simFlightPhaseStartTimes.put(phase, simFlightPhaseStartTime);
	}

	public Timestamp getSimFlightPhaseEndTime(FlightPhase phase) {
		return simFlightPhaseEndTimes.get(phase);
	}

	public void setSimFlightPhaseEndTime(FlightPhase phase, Timestamp simFlightPhaseStartTime) {
		this.simFlightPhaseEndTimes.put(phase, simFlightPhaseStartTime);
	}

	public double getSimGateDelay() {
		return simGateDelay;
	}

	public double getSimDepartureSurfaceDelay() {
		return simDepartureSurfaceDelay;
	}

	public double getSimSectorDelay() {
		return simSectorDelay;
	}
	
	public void setSimSectorDelay(double mit) {
		this.simSectorDelay = mit;
	}

	public void setSimPushbackDistributionValue(double simPushbackDistributionValue) {
		this.simPushbackDistributionValue = simPushbackDistributionValue;
	}

	public String getSimFlightPhaseResource(FlightPhase phase) {
		return simFlightPhaseResources.get(phase);
	}

	public void setSimFlightPhaseResource(FlightPhase phase, String value) {
		simFlightPhaseResources.put(phase, value);
	}
	
	public void setDepartureRampBypassFlag(boolean departureRampBypassFlag) {
		this.departureRampBypassFlag = departureRampBypassFlag;
	}

	public void setDepartureRampServiceTime(double departureRampServiceTime) {
		this.departureRampServiceTime = departureRampServiceTime;
	}

	public void setArrivalRampBypassFlag(boolean arrivalRampBypassFlag) {
		this.arrivalRampBypassFlag = arrivalRampBypassFlag;
	}

	public void setArrivalRampServiceTime(double arrivalRampServiceTime) {
		this.arrivalRampServiceTime = arrivalRampServiceTime;
	}

	public void setSimNominalTaxiOutTime(double simNominalTaxiOutTime) {
		this.simNominalTaxiOutTime = simNominalTaxiOutTime;
	}

	public void setSimNominalTaxiInTime(double simNominalTaxiInTime) {
		this.simNominalTaxiInTime = simNominalTaxiInTime;
	}

	public void setSimRerouteClearanceServiceTime(
			double simRerouteClearanceServiceTime) {
		this.simRerouteClearanceServiceTime = simRerouteClearanceServiceTime;
	}

	public double getSimTotalRestrictionDelay() {
		return simTotalRestrictionDelay;
	}

	public void setSimTotalRestrictionDelay(double simTotalRestrictionDelay) {
		this.simTotalRestrictionDelay = simTotalRestrictionDelay;
	}

	public void setStepClimbEligible(boolean stepClimbEligible) {
		this.stepClimbEligible = stepClimbEligible;
	}
	
	public void setShortenedArrival(boolean shortenedArrival){
		this.shortenedArrival = shortenedArrival;
	}

	public void setCdaCurveName(String cdaCurveName) {
		this.cdaCurveName = cdaCurveName;
	}

	public void setCdaProbability(double cdaProbability) {
		this.cdaProbability = cdaProbability;
	}

	public void setCdaSelected(boolean isCdaSelected) {
		this.isCdaSelected = isCdaSelected;
	}

	public void setSidSelected(String sidSelected) {
		this.sidSelected = sidSelected;
	}

	public void setSidFitness(int sidFitness) {
		this.sidFitness = sidFitness;
	}

	public void setStarSelected(String starSelected) {
		this.starSelected = starSelected;
	}

	public void setStarFitness(int starFitness) {
		this.starFitness = starFitness;
	}

	public void setIapSelected(String iapSelected) {
		this.iapSelected = iapSelected;
	}

	public StringBuilder getSimSectorTrace() {
		return simSectorTrace;
	}

	public String getFiledFlightPlanType() {
		return filedFlightPlanType;
	}

	public double getSmoothTime() {
		return smoothTime;
	}

	public String getMapFlightRuleType() {
		return mapFlightRuleType;
	}

	public void setMapFlightRuleType(String mapFlightRuleType) {
		this.mapFlightRuleType = mapFlightRuleType;
	}
	
	public void setPlannedRouteDist(double routeDist){
		this.plannedRouteDistance = routeDist;
	}
	
	public void setFinalRouteDist(double routeDist){
		this.finalRouteDistance = routeDist;
	}
	
	public void setPlannedFirDist(double routeDist){
		this.plannedFirDistance = routeDist;
	}
	
	public void setFinalFirDist(double routeDist){
		this.finalFirDistance = routeDist;
	}

	public FuelUsageRecord getFuelUsageRecord() {
		return FuelUsageRecord;
	}

	public void setFuelUsageRecord(FuelUsageRecord fuelUsageRecord) {
		FuelUsageRecord = fuelUsageRecord;
	}

	public void setSimWxEquipmentArrivalQueueDelay(
			double simWxEquipmentArrivalQueueDelay) {
		this.simWxEquipmentArrivalQueueDelay = simWxEquipmentArrivalQueueDelay;
	}

	public Timestamp getSimLastSectorQueueStart() {
		return simLastSectorQueueStart;
	}

	public void setSimLastSectorQueueStart(Timestamp simLastSectorQueueStart) {
		this.simLastSectorQueueStart = simLastSectorQueueStart;
	}

	public boolean getFlag(Reason reason) {
		return flags.get(reason);
	}
	
	public void setFlag(Reason reason, boolean flag) {
		flags.put(reason, flag);
	}

	public void setFormatterOverflow(boolean formatterOverflow) {
		this.formatterOverflow = formatterOverflow;
	}

	public boolean[] getScheduleOutputFlags() {
		return scheduleOutputFlags;
	}

	public FlightCancellationReason getCancelled() {
		return cancelled;
	}

	public void setCancelled(FlightCancellationReason cancelled) {
		this.cancelled = cancelled;
	}

	public boolean isOriginalFlight() {
		return originalFlight;
	}

	public void setOriginalFlight(boolean originalFlight) {
		this.originalFlight = originalFlight;
	}
	
	public boolean isOceanicFlight() {
		return oceanicFlag;
	}
	
	public void setOceanicFlag(boolean flag) {
		oceanicFlag = flag;
	}
	
	public boolean isOceanicReroute() {
		return oceanicRerouteFlag;
	}
	
	public void setOceanicRerouteFlag(boolean flag) {
		oceanicRerouteFlag = flag;
	}
	
	public double getOceanicDelay() {
		return oceanicDelay;
	}
	
	public void addOceanicDelay(double delay) {
		oceanicDelay += delay;
	}
	
	public String getOceanicRegionsTraversed() {
		return oceanicRegionsTraversed;
	}
	
	public void appendOceanicRegionTraversal(String oceanicRegion) {
		if (oceanicRegionsTraversed == null) {
			oceanicRegionsTraversed = oceanicRegion;
		} else {
			oceanicRegionsTraversed = oceanicRegionsTraversed + " " + oceanicRegion;
		}
	}
	
	public double getNominalOceanicFuelUsage() {
		return oceanicNominalFuelUsage;
	}
	
	public void setNominalOceanicFuelUsage(double fuel) {
		oceanicNominalFuelUsage = fuel;
	}
	
	public double getActualOceanicFuelUsage() {
		return oceanicActualFuelUsage;
	}
	
	public void setActualOceanicFuelUsage(double fuel) {
		oceanicActualFuelUsage = fuel;
	}
}
