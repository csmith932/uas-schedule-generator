/**
 * Copyright "TBD", Metron Aviation & CSSI. All rights reserved. This computer Software was
 * developed with the sponsorship of the U.S. Government under Contract No. DTFAWA-10-D-00033, which
 * has a copyright license in accordance with AMS 3.5-13.(c)(1).
 */

package gov.faa.ang.swac.common.flightmodeling;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import gov.faa.ang.swac.common.FlightPlanMessageLogger.Reason;
import gov.faa.ang.swac.common.Pair;
import gov.faa.ang.swac.common.datatypes.Altitude;
import gov.faa.ang.swac.common.datatypes.Timestamp;
import gov.faa.ang.swac.common.entities.Carrier;
import gov.faa.ang.swac.common.entities.EdctSource;
import gov.faa.ang.swac.common.flightmodeling.AltitudeRestriction.AltitudeType;
import gov.faa.ang.swac.common.flightmodeling.AltitudeRestriction.RestrictionType;
import gov.faa.ang.swac.common.flightmodeling.IResourceInfo.ResourceType;
import gov.faa.ang.swac.common.flightmodeling.Itinerary.Type;
import gov.faa.ang.swac.common.flightmodeling.fileio.BadaRecord.FlightStage;
import gov.faa.ang.swac.common.flightmodeling.jni.Route;
import gov.faa.ang.swac.common.flightmodeling.jni.TimeDistribution;
import gov.faa.ang.swac.common.geometry.GCUtilities;
import gov.faa.ang.swac.common.random.RandomStream;


// FIXME: fields and properties have changed a lot: verify and update clone, equals, hashcode, and
// read/write functions

/**
 * Represents a flight leg. It is primarily based on data contained in {@link ScheduleRecord}.
 * <p>
 * A {@link FlightLeg} represents data for a single, scheduled flight... but not the aircraft that
 * flies it.
 * 
 * @author Jason Femino - CSSI, Inc.
 */
public class FlightLeg implements gov.faa.ang.swac.common.flightmodeling.jni.FlightPlan, Cloneable, Serializable
{
	private static org.apache.log4j.Logger logger = org.apache.log4j.LogManager.getLogger(FlightLeg.class);

    /**
     * The minimum distance, in nautical miles, that must be between two waypoints to consider them
     * distinct. If the distance between two waypoints is strictly less that this, one of the
     * waypoints must be dropped. To turn off this feature, set waypointDistanceTolerance to
     * <code>null</code>.
     */
    private static final Double WAYPOINT_DISTANCE_TOLERANCE = 1.0; // nmi

    public enum FlightRuleType
    {
        IFR, // Instrument Flight Rules
        VFR
        // Visual Flight Rules
    }

    public enum FlightDirection
    {
        WESTBOUND, EASTBOUND;
    }

    public enum ModeledState
    {
        SCHEDULE,	// Original filed waypoints
        WAY,		// 2D modeled and filtered
        ROUTE,		// 3D constraints applied
        TRAJECTORY,	// 4D interpolated
        CROSSINGS	// 4D resource crossings inserted; filtered to crossings and original route points
    }

    // ---------------------
    // Static class members
    // ---------------------

    // toString related members
    public static final String SEP = ",";
    public static final String WAY_TEXT_RECORD_KEY = "WAY: " + TrajectoryPoint.TEXT_RECORD_KEY + SEP + TrajectoryPoint.TEXT_RECORD_KEY + " ...";
    public static final String TEXT_RECORD_KEY = "FLIGHT_LEG: scheduleId, flightId, filedFlightRuleType, filedFlightPlanType, filedAltitude, filedAirspeed\n" + TerminusDeparture.TEXT_RECORD_KEY + "\n" + TerminusArrival.TEXT_RECORD_KEY + "\n" + WAY_TEXT_RECORD_KEY;

    // -----------------------
    // Instance class members
    // -----------------------
    private final String filedFlightPlanType;
    //private final String flightNumber;
    private Altitude filedAltitude = null;
    private final Integer filedAirspeed;
    private final TerminusDeparture departure;
    private final TerminusArrival arrival;
    private Integer takeOffMass = null;
    private boolean stepClimbEligible = false;
    private String arrivalFixName = null; // for quick lookup of Fix name by TBFM
    private boolean rnpEquipped;
    
    private enum ShortenedArrivalState{
    	INELIGIBLE,
    	ELIGIBLE,
    	REROUTED;
    }
    
    private ShortenedArrivalState shortenedArrivalState = ShortenedArrivalState.INELIGIBLE;
    
    // these won't be set to a valid number unless rerouting is enabled.
    private double maxConvection = -1.0;
    private double maxSua = -1.0;
    
    /** Keeps track of what stage of trajectory modeling has been completed */
    private ModeledState modeledState = ModeledState.SCHEDULE;

    // TODO: This is the flag for a q-routes flight, which we are using to eliminated some
    // unnecessary file I/O. The entire
    // Q-Routes process should be reviewed after the I/O is sufficiently untangled
    private boolean qRoute;
    // TODO: This is from TAM
    // TODO: Make private!!
    public String sidSelected = "";
    public int sidFitness = 0;
    public String starSelected = "";
    public int starFitness = 0;
    public String iap = "";
    public String depRunway = "";
    public String arrRunway = "";

    // TODO: this is from FuelOutputHandler
    // TODO: Make private!!
    public FuelUsageRecord fuelUsage;
    
    private boolean isInitialized;

	/////////////////////////
	// From jni.FlightPlan
	/////////////////////////
	//TODO:  all of these will be gone or moved to SimFlight when CSIM is obsolete
	private Itinerary parentAirframe = null;
    
	private Integer flightId = null;
	private String flightNumber = null;
	private Timestamp edct;
	private EdctSource edctSource;
	
	private TimeDistribution turnaroundDist = null;
	private TimeDistribution pushbackDist = null;
	private TimeDistribution rerouteClearanceDist = null;
	private TimeDistribution departureRampDist = null;
	private TimeDistribution arrivalRampDist = null;
	private TimeDistribution taxiInDist = null;
	private TimeDistribution taxiOutDist = null;
	private double taxiInDistSample;
	private double taxiOutDistSample;
	private RandomStream routeShorteningStream;
	
	private long minimumDepartureRampServiceTime = 0;
	private long minimumArrivalRampServiceTime = 0;
	private boolean isRerouted = false;
	private boolean failedReroute = false;
	private boolean rerouteClearanceFlag = false;
	private boolean eligibleForDepartureRampBypass = false;
	private boolean eligibleForArrivalRampBypass = false;
	
	private Route flightRoute = null;
	/////////////////////////

	/**
	 * Interface used to determine oceanic separation.  Implementing class will query javascript
	 */
	private OceanicSeparationRetriever oceanicSeparationRetriever= null;
	/**
	 * Interface used to determine step climb retry time.  Implementing class will query javascript
	 */
	private StepClimbRetryTimeRetriever stepClimbRetryTimeRetriever = null;
    

    public FlightLeg()
    {
        this.flightNumber = "";
        this.filedFlightPlanType = "IFR";
        setFiledAltitude(null);
        this.filedAirspeed = null;
        this.departure = new TerminusDeparture(new TerminusDeparture());
        this.arrival = new TerminusArrival(new TerminusArrival());
        setFiledWayPoints(new ArrayList<TrajectoryPoint>() );
        this.edct = new Timestamp(0);
        this.isInitialized = false;
    }
    
    public FlightLeg(ScheduleRecord scheduleRecord)
    {
        setFlightId(scheduleRecord.idNum);
        this.flightNumber = scheduleRecord.flightId();
        this.filedFlightPlanType = scheduleRecord.flightPlanType;
        
        setFiledAltitude(scheduleRecord.filedCruiseAltitude);
        this.filedAirspeed = 
            !Double.isNaN(scheduleRecord.filedSpeed) ?
            Double.valueOf(scheduleRecord.filedSpeed).intValue() :
            null;

        this.departure = new TerminusDeparture(scheduleRecord);
        this.arrival = new TerminusArrival(scheduleRecord);
        
        setFiledWayPoints(scheduleRecord.etmsFiledWaypoints);
        
        this.edct = new Timestamp(0);
        this.isInitialized = false;
    }
    
    public FlightLeg(FlightLeg original)
    {
    	 this.flightId = (original.flightId == null ? null : original.flightId.intValue());
    	 this.filedFlightPlanType = original.filedFlightPlanType;
    	 this.flightNumber = original.flightNumber;
         this.filedAltitude = original.filedAltitude;
    	 this.filedAirspeed = (original.filedAirspeed == null ? null : original.filedAirspeed.intValue());
    	 this.departure = (original.departure == null ? null : original.departure.clone());
    	 this.arrival = (original.arrival == null ? null : original.arrival.clone());
    	 this.takeOffMass = (original.takeOffMass == null ? null : original.takeOffMass.intValue());
    	 this.stepClimbEligible = original.stepClimbEligible;
    	 this.arrivalFixName = original.arrivalFixName;
         this.rnpEquipped = original.rnpEquipped;
    	 this.shortenedArrivalState = original.shortenedArrivalState;
    	 this.maxConvection = original.maxConvection;
    	 this.maxSua = original.maxSua;
    	 this.flightRoute = (original.flightRoute == null ? null : original.cloneRoute());
    	 this.qRoute = original.qRoute;
    	 this.sidSelected = original.sidSelected;
    	 this.sidFitness = original.sidFitness;
    	 this.starSelected = original.starSelected;
    	 this.starFitness = original.starFitness;
    	 this.iap = original.iap;
    	 this.fuelUsage = original.fuelUsage; // shallow copy.
    	 this.edct = original.edct.clone();
         this.isInitialized = original.isInitialized;
    	 
    	 this.parentAirframe = original.parentAirframe; // Shallow copy here to prevent recursion.
    	 this.modeledState = original.modeledState;
         
         this.depRunway = original.depRunway;
         this.arrRunway = original.arrRunway;
         
         this.isRerouted = original.isRerouted;
         this.failedReroute = original.failedReroute;
         
         this.pushbackDist = original.pushbackDist;
         this.departureRampDist = original.departureRampDist;
         this.arrivalRampDist = original.arrivalRampDist;
         this.rerouteClearanceDist = original.rerouteClearanceDist;
         this.turnaroundDist = original.turnaroundDist;
         this.taxiInDist = original.taxiInDist;
         this.taxiOutDist = original.taxiOutDist;
         this.taxiInDistSample = original.taxiInDistSample;
         this.taxiOutDistSample = original.taxiOutDistSample;
         this.routeShorteningStream = original.routeShorteningStream;
         
         this.oceanicSeparationRetriever = original.oceanicSeparationRetriever;
         this.stepClimbRetryTimeRetriever = original.stepClimbRetryTimeRetriever;
    }


    public ModeledState getModeledState() {
		return modeledState;
	}

	public void setModeledState(ModeledState modeledState) {
		this.modeledState = modeledState;
	}

    public boolean isQRoute()
    {
        return this.qRoute;
    }

    public void setQRoute(boolean val)
    {
        this.qRoute = val;
    }

    public void setIsInitialized(boolean val) {
        this.isInitialized = val;
    }
    
    public boolean isInitialized() {
        return this.isInitialized;
    }


    /**
     * Difference between timestamp at arrival airport and departure airport.
     * <BR><BR>
     * NOTE!: If using this method to obtain unimpeded airborne time, this method must be called
     * before this flight is modeled in the Sim, otherwise accrued delay will invalidate the assumption
     * that the airborne time is unimpeded. 
     * 
     * @return minutes this FlightLeg spent in the air
     */
    public Double getEnRouteTime()
    {
        return computedArrivalTime().minDifference(this.flightRoute.get(0).timestamp());  // Enroute time is airborne time only
    }

    /**
     * Sets the flight rule type for this {@link FlightLeg}.
     */
    public FlightRuleType flightRuleType()
    {
    	if (filedFlightPlanType.contains("VFR"))
        {
    		return FlightRuleType.VFR;
        }
    	else if (departure.airportName() != null  && departure.airportName().length() > 0 &&
    				arrival.airportName() != null  && arrival.airportName().length() > 0 &&
    				departure.airportLocation() != null &&
    				arrival.airportLocation() != null &&
    				!( departure.gateDateTime() == null && 
    						departure.runwayDateTime() == null  && 
    						departure.scheduledDateTime() == null ))
    	{
    		return FlightRuleType.IFR;
    	}
    	else
    	{
    		return FlightRuleType.VFR;
    	}
    }

    /**
     * Set the filed flight plan type for this {@link FlightLeg}.
     */
    public String filedFlightPlanType()
    {
        return this.filedFlightPlanType;
    }

    /**
     * The filed altitude for this {@link FlightLeg}.
     */
    public Altitude filedAltitude()
    {
        return this.filedAltitude;
    }

    /**
     * Sets the filed altitude for this {@link FlightLeg}.
     */
    public void setFiledAltitude(Altitude altitude)
    {
        this.filedAltitude = altitude;
    }

    /**
     * The filed air speed for this {@link FlightLeg}.
     */
    public Integer filedAirspeed()
    {
        return this.filedAirspeed;
    }

    /**
     * Sets the {@link TerminusDeparture} for this {@link FlightLeg}.
     */
    public TerminusDeparture departure()
    {
        return this.departure;
    }

    /**
     * Sets the {@link TerminusArrival} for this {@link FlightLeg}.
     */
    public TerminusArrival arrival()
    {
        return this.arrival;
    }

    /**
     * The estimate take off mass for this {@link FlightLeg}, in kg.
     */
    public Integer takeOffMass()
    {
        return this.takeOffMass;
    }

    /**
     * Sets the estimate take off mass for this {@link FlightLeg}, in kg.
     */
    public void setTakeOffMass(Integer mass)
    {
        this.takeOffMass = mass;
    }
    
    public void setStepClimbEligible(boolean val){
    	this.stepClimbEligible = val;
    }
    
    public boolean stepClimbEligible(){
    	return this.stepClimbEligible;
    }
    
    public String arrivalFixName(){
    	return this.arrivalFixName;
    }
    
    /**
     * Based on Equipment Evolution suffix and arrival airport is 
     * this FlightLeg's aircraft determined to be RNP equipped.
     * 
     * @return TRUE if RNP Equipped, FALSE by default.
     */
    public boolean rnpEquipped(){
    	return this.rnpEquipped;
    }
    
    public void setRnpEquipped(boolean val){
    	this.rnpEquipped = val;
    }
    
    /**
     * 
     * @return TRUE if flight is eligible for or has already been rerouted with a shortened arrival route, FALSE otherwise.
     */
    public boolean eligibleForShortedArrival(){
    	return this.shortenedArrivalState.ordinal() >= ShortenedArrivalState.ELIGIBLE.ordinal();
    }
    
    /**
     * 
     * @return TRUE if flight has been rerouted with a shortened arrival route, FALSE otherwise.
     */
    public boolean hasShortenedArrival(){
    	return this.shortenedArrivalState.equals(ShortenedArrivalState.REROUTED);
    }
    
    
    /**
     * Sets ShortenedArrivalState to "ELIGIBLE"
     */
    public void makeEligibleForShortenedArrival(){
    	this.shortenedArrivalState = ShortenedArrivalState.ELIGIBLE;
    }
    
    /**
     * Sets ShortenedArrivalState to "INELIGIBLE"
     */
    public void makeIneligibleForShortenedArrival(){
    	this.shortenedArrivalState = ShortenedArrivalState.INELIGIBLE;
    }
    
    /**
     * Sets ShortenedArrivalState to "REROUTED"
     */
    public void markReroutedWithShortenedArrival(){
    	this.shortenedArrivalState = ShortenedArrivalState.REROUTED;
    }
    
    public void setMaxConvection(double val){
    	this.maxConvection = val;
    }
    
    public double maxConvection(){
    	return this.maxConvection;
    }
    
    public void setMaxSua(double val){
    	this.maxSua = val;
    }
    
    public double maxSua(){
    	return this.maxSua;
    }
    
    
    /**
     * Creates a {@link List<TrajectoryPoint>} between the departure and arrival airports as follows:
     * <p>
     * - Add departure airport location to {@link List<TrajectoryPoint>}<br>
     * - Add each waypoint to the {@link List<TrajectoryPoint>}<br>
     * - End arrival airport location to {@link List<TrajectoryPoint>}
     * <p>
     * Additional rules when adding waypoints:<br>
     * - If {@link WAYPOINT_DISTANCE_TOLERANCE} is null, then all waypoints are used.<br>
     * - If {@link WAYPOINT_DISTANCE_TOLERANCE} is non-null, then a waypoint must be farther than
     * {@link WAYPOINT_DISTANCE_TOLERANCE} from the previous waypoint to be added.<br>
     * - In either case, both departure and arrival airports are always used (if they exist).<br>
     * (i.e. If {@link WAYPOINT_DISTANCE_TOLERANCE} is non-null AND the arrival airport is within
     * {@link WAYPOINT_DISTANCE_TOLERANCE} of the previous waypoint, the waypoint is dropped and the
     * arrival airport used.)
     * 
     * @param points
     */
    private void makeWay()
    {
        List<TrajectoryPoint> wayPoints = (this.flightRoute == null ? new ArrayList<TrajectoryPoint>(2) : new ArrayList<TrajectoryPoint>(this.flightRoute.size() + 2));
        Double waypointDistanceTolerance = WAYPOINT_DISTANCE_TOLERANCE;
        
        // determine if flightRoute already has departure or arrival airport so we don't add them twice.
        boolean addDeparture = this.flightRoute == null || this.flightRoute.isEmpty() || !ResourceType.AP.equals(this.flightRoute.get(0).resourceType());
        boolean addArrival = this.flightRoute == null || this.flightRoute.size() < 2 || !ResourceType.AP.equals(this.flightRoute.get(this.flightRoute.size()-1).resourceType());

        // Add departure airport
        if (this.departure() != null && this.departure().airportLocation() != null && addDeparture)
        {
            TrajectoryPoint departureAirport = new TrajectoryPoint(this.departure().airportLocation());
            departureAirport.setGroundSpeed(0.0);
            departureAirport.setResourceTransitTime(0);
            
            departureAirport.setResourceInfo(this.departure());
      
            wayPoints.add(departureAirport);
        }

        // Add each waypoint
        if (this.flightRoute != null)
        {
            for (TrajectoryPoint point : this.flightRoute)
            {
                // If we're using waypoint distance tolerance, then check the distance from it to
                // the current point.
                // (This is necessary mostly to protect against duplicate with similar coordinates,
                // but with different rounding,
                // creating impossibly short legs of nearly random direction. That would result in
                // problems with the FixInserter algorithm.)
                if (waypointDistanceTolerance != null && wayPoints.size() > 0)
                {
                    // Only add the current point if its distance from the previous point is >= the
                    // distance tolerance
                	// HKK: why are we getting two departure airports??
                    if (point.resourceType() != null || GCUtilities.gcDistance(wayPoints.get(wayPoints.size() - 1), point) >= waypointDistanceTolerance)
                    {
                        wayPoints.add(point);
                    }
                }
                else
                // ELSE not using waypiont distance tolerance... just add the current point
                {
                    wayPoints.add(point);
                }
            }
        }

        // Add arrival airport
        if (this.arrival() != null && this.arrival().airportLocation() != null && addArrival)
        {
            TrajectoryPoint arrivalAirport = new TrajectoryPoint(this.arrival().airportLocation());
            
            arrivalAirport.setGroundSpeed(0.0);
            arrivalAirport.setResourceTransitTime(0);
            
            arrivalAirport.setResourceInfo(this.arrival());
            
            wayPoints.add(arrivalAirport);
            if (waypointDistanceTolerance != null && wayPoints.size() > 2 && GCUtilities.gcDistance(wayPoints.get(wayPoints.size() - 2),
                    arrivalAirport) < waypointDistanceTolerance)
            {
                wayPoints.remove(wayPoints.size() - 2);
            }
        }

        this.setWay(wayPoints);
        this.modeledState = ModeledState.WAY;
    }

    /**
     * Given a {@link List<TrajectoryPoint>}, it creates a {@link List<TrajectoryPoint>} by assigning altitudes to each
     * {@link TrajectoryPoint} using that {@link TrajectoryPoint}'s {@link AltitudeRestriction}s.
     */
    public void makeRoute(boolean keepOriginalWaypoints)
    {
    	makeRoute(-1, keepOriginalWaypoints);
    }
    
    /**
     * Given a {@link List<TrajectoryPoint>}, it creates a {@link List<TrajectoryPoint>} by assigning altitudes to each
     * {@link TrajectoryPoint} using that {@link TrajectoryPoint}'s {@link AltitudeRestriction}s.
     */
    public void makeRoute(int lastPointSimulated, boolean keepOriginalWaypoints)
    {
    	List<TrajectoryPoint> wayPoints = this.way();
        // Create a new list of RoutePoints from the input WayPoints
        List<TrajectoryPoint> routePoints = new ArrayList<TrajectoryPoint>(this.way().size());
        
        // Add the already-flown points as-is
        for (int i = 0; i <= lastPointSimulated; i++) {
        	routePoints.add(new TrajectoryPoint(wayPoints.get(i))); // We always add copies in case something else may try to interfere with the original
        }
        
        for (int i = lastPointSimulated + 1; i < wayPoints.size(); i++)
        {
        	TrajectoryPoint routePoint = new TrajectoryPoint(wayPoints.get(i));
        	routePoint.setSmoothable(true); //assumption: makeRoute() will be called before Trajectory Interpolation
            routePoints.add(routePoint);
        
            Altitude altitude = routePoint.altitude() == null ? Altitude.NULL : routePoint.altitude();

            // This TrajectoryPoint has no AltitudeRestrictions, assign the filed cruise altitude as its
            // altitude
            if (routePoint.altitudeRestrictions().size() == 0)
            {
                altitude = this.filedAltitude;
            }
            // This TrajectoryPoint has at least one AltitudeRestriction, determine the
            // AltitudeRestrictions and types
            else
            {
                // Types of altitude restrictions this waypoint may have
                AltitudeRestriction atOrBelowRestriction = null; // Treat "AT" as "AT_OR_BELOW"

                // ----------------------------------------
                // Determine lowest at/atOrBelow restriction (and the highest CRUISE restriction)
                // ----------------------------------------
                for (AltitudeRestriction altitudeRestriction : routePoint.altitudeRestrictions())
                {
                    if (altitudeRestriction.altitudeType() == AltitudeType.AT_OR_BELOW || altitudeRestriction.altitudeType() == AltitudeType.AT)
                    {
                        // If restriction is non-CRUISE, find the lowest...
                        if (atOrBelowRestriction == null || altitudeRestriction.altitude().feet() < atOrBelowRestriction.altitude()
                                .feet())
                        {
                            atOrBelowRestriction = altitudeRestriction;
                        }
                    }
                }

                // ----------------------------------------
                // Choose TrajectoryPoint altitude
                // ----------------------------------------
                if (atOrBelowRestriction != null)
                {
                    // Set TrajectoryPoint altitude to the higher of: filed cruise altitude OR
                    // restriction altitude
                    altitude = Altitude.valueOfFeet(Math.min(this.filedAltitude.feet(),
                            atOrBelowRestriction.altitude().feet()));
                }
                else
                {
                    altitude = Altitude.valueOfFeet(this.filedAltitude.feet());
                }

                // ----------------------------------------
                // Ensure SID/STAR/IAP altitudes are not below airport altitudes
                // ----------------------------------------
                if (routePoint.hasRestrictionType(RestrictionType.SID))
                {
                    if (altitude.feet() < this.departure.airportLocation().altitude().feet())
                    {
                        StringBuilder message = new StringBuilder("Schedule ID " + this.flightId + ": Contains TrajectoryPoint (" + routePoint.name() + ") with departure restriction altitude (");
                        for (AltitudeRestriction altitudeRestriction : routePoint.altitudeRestrictions())
                        {
                            message.append(altitudeRestriction.toString() + " ");
                        }
                        message.append(") that is below departure airport (\"" + this.departure.airportName() + "\" @ " + this.departure.airportLocation()
                                .altitude() + "). Using airport altitude.");
                        logger.warn(message.toString());
                        altitude = Altitude.valueOfFeet(this.departure.airportLocation()
                                .altitude()
                                .feet());
                    }
                }

                if (routePoint.hasRestrictionType(RestrictionType.STAR) || routePoint.hasRestrictionType(RestrictionType.IAP))
                {
                    if (altitude.feet() < this.arrival.airportLocation().altitude().feet())
                    {
                        StringBuilder message = new StringBuilder("Schedule ID " + this.flightId + ": Contains TrajectoryPoint (" + routePoint.name() + ") with arrival restriction altitude (");
                        for (AltitudeRestriction altitudeRestriction : routePoint.altitudeRestrictions())
                        {
                            message.append(altitudeRestriction.toString() + " ");
                        }
                        message.append(") that is below arrival airport (\"" + this.arrival.airportName() + "\" @ " + this.arrival.airportLocation()
                                .altitude() + "). Using airport altitude.");
                        logger.warn(message.toString());
                        altitude = Altitude.valueOfFeet(this.arrival.airportLocation()
                                .altitude()
                                .feet());
                    }
                }
            }

            routePoint.setAltitude(altitude);
            
            // Mark as a WP crossing for retention in crossings finder
            if (routePoint.resourceType() == null && keepOriginalWaypoints)
            {
            	routePoint.setResourceInfo(WaypointResourceInfo.createWaypoint(routePoint.name()));
            }

        }

        this.assignFinalAltitudes(routePoints, lastPointSimulated);
        this.setRoute(routePoints);
    }

    /**
     * Gets the {@link List} of filed {@link TrajectoryPoints} for this {@link FlightLeg}.
     */
    public void setFiledWayPoints(List<TrajectoryPoint> filedWayPoints)
    {
    	if (filedWayPoints == null)
    	{
    		this.flightRoute = new Route();
    	}
    	else
    	{
    		this.flightRoute = new Route(filedWayPoints);
    	}
        // Force lazy synchronization of Way
        this.modeledState = ModeledState.SCHEDULE;
    }

    /**
     * Gets the {@link List<TrajectoryPoint>} for this {@link FlightLeg}.
     * <p>
     * If the {@link List<TrajectoryPoint>} object is null, and the member {@link #filedWayPoints} is not null, it
     * calls {@link #makeWay(List)} to create a {@link List<TrajectoryPoint>} and return it.
     */
	public List<TrajectoryPoint> way()
    {
        // Create Way (if it doesn't already exist or is empty)
        if (this.modeledState.ordinal() < ModeledState.WAY.ordinal())
        {
            this.makeWay();
        }

        return this.flightRoute;
    }

    /**
     * Sets the {@link List<TrajectoryPoint>} for this {@link FlightLeg}.
     */
    public void setWay(List<TrajectoryPoint> way)
    {
    	validateWay(way);
        this.flightRoute = new Route(way);
        this.modeledState = ModeledState.WAY;
    }
    
    /**
     * Gets the {@link List<TrajectoryPoint>} for this {@link FlightLeg}.
     * <p>
     * If the {@link List<TrajectoryPoint>} object is null, and the member {@link #way} is not null, it calls
     * {@link #makeRoute(List)} to create a {@link List<TrajectoryPoint>} and return it.
     */
    public List<TrajectoryPoint> route()
    {
        // Create route (if it doesn't already exist, and the Way has been set)
        if (this.modeledState.ordinal() < ModeledState.ROUTE.ordinal())
        {
            throw new IllegalStateException("Attempt to retrieve 3-D route from flight leg that is only in modeled stae " +
            		this.modeledState + ". Flight leg=" + this.toString());
        }

        return Collections.unmodifiableList(this.flightRoute);
    }

    /**
     * Sets the {@link List<TrajectoryPoint>} for this {@link FlightLeg}.
     */
    public void setRoute(List<TrajectoryPoint> route)
    {
    	validateRoute(route);
        this.flightRoute = new Route(route);
        this.modeledState = ModeledState.ROUTE;
    }
    
    /**
     * Shifts all departure/arrival times by a number of minutes.<br>
     * Used during schedule smoothing.
     * 
     * @param minutes
     */
    public void shiftTimes(double minutes)
    {
        if (this.departure != null)
        {
            this.departure.shiftTimes(minutes);
        }

        if (this.arrival != null)
        {
            this.arrival.shiftTimes(minutes);
        }
    }

    @Override
    public String toString()
    {
        return "FLIGHT_LEG:" + 
            " " + (this.flightId == null ? "" : this.flightId) + SEP +
            " " + (this.flightNumber == null ? "" : this.flightNumber) + SEP +
            " " + (this.flightRuleType().name()) + SEP +
            " " + (this.filedFlightPlanType == null ? "" : this.filedFlightPlanType) + SEP +
            " " + (this.filedAltitude == null ? "" : (int)Math.round(this.filedAltitude.feet())) + SEP +
            " " + (this.filedAirspeed == null ? "" : this.filedAirspeed) + "\n" +
            (this.departure == null ? "" : this.departure.toString()) + "\n" +
            (this.arrival == null ? "" : this.arrival.toString()) + "\n" +
            //(this.way() == null ? "" : this.flightRoute.toString()); // causes infinite recursion
            (this.flightRoute == null ? "" : this.flightRoute.toString()); // fix for infinite recursion   
    }

    private void assignFinalAltitudes(List<TrajectoryPoint> routePoints, int lastPointSimulated)
    {
        Altitude arrRestAlt = this.filedAltitude();

        int startIndex = lastPointSimulated +1;
        for (int i = startIndex; i < routePoints.size(); i++)
        {
            if (routePoints.get(i).hasRestrictionType(RestrictionType.SID))
            {
                // set previous points' altitudes to the departure/cruise restriction altitude if
                // not already below
                Altitude alt = routePoints.get(i).altitude();
                for (int j = startIndex; j < i; j++)
                {
                    if (routePoints.get(j).altitude().feet() > alt.feet())
                    {
                        routePoints.get(j).setAltitude(alt);
                    }
                }
            }
            else if ((routePoints.get(i).hasRestrictionType(RestrictionType.STAR) || routePoints.get(i)
                    .hasRestrictionType(RestrictionType.IAP)) && routePoints.get(i)
                    .altitude()
                    .feet() < arrRestAlt.feet())
            {

                // found an arrival restriction that is lower than previous altitude restriction (or
                // cruiseAlt if first arrival restriction)
                Altitude alt = routePoints.get(i).altitude();
                arrRestAlt = alt;
                for (int j = i + 1; j < routePoints.size(); j++)
                {
                    if (routePoints.get(j).altitude().feet() > alt.feet())
                    {
                        routePoints.get(j).setAltitude(alt);
                    }
                }
            }
        }
    }

    @Override
    public FlightLeg clone()
    {
        return new FlightLeg(this);
    }

    /**
     * @return Eastbound or Westbound, depending on the relative location of the arrival and
     *         departure airports. Flights traversing more than 180 degrees of longitude are not
     *         handled properly. Subsegments of a flight that occur alternately in eastbound and
     *         westbound directions are not resolved separately. When arrival and departure have the
     *         same longitude, the direction defaults to Eastbound.
     */
    public FlightDirection getDirection()
    {
        if (this.arrival.airportLocation().isWestOf(this.departure.airportLocation())) { return FlightDirection.WESTBOUND; }

        return FlightDirection.EASTBOUND;
    }

    /**
     * Returns the {@link Itinerary.Type} for the given {@link FlightPlan} and a list of
     * {@link Reason}s if this {@link FightPlan} cannot be either a departure or arrival VFR
     * {@link ItineraryVFR}. The {@link Itinerary.Type} is determined from the {@link FlightPlan} as
     * follows:<br>
     * &nbsp;&nbsp;a) {@link Itinerary.Type#DEP} if {@link FlightPlan} has a
     * {@link TerminusDeparture}, departure airport name, and a departure date/time<br>
     * &nbsp;&nbsp;b) {@link Itinerary.Type#ARR} if {@link FlightPlan} has a {@link TerminusArrival}
     * , arrival airport name, and an arrival date/time<br>
     * &nbsp;&nbsp;c) {@link Itinerary.Type#DEP_ARR} if both a) and b) are true<br>
     * &nbsp;&nbsp;c) <code>null</code> if neither a) nor b) are true<br>
     */
    public Itinerary.Type vfrType()
    {
        Itinerary.Type vfrType = null;
        // Check departure info for problems
        boolean validDep = true;
        TerminusDeparture dep = this.departure();
        if (dep == null)
        {
            validDep = false;
        }
        else if (dep.airportName() == null || dep.airportName().trim().length() == 0)
        {
            validDep = false;
        }
        else if (dep.runwayDateTime() == null)
        {
            validDep = false;
        }

        // Check arrival info for problems
        boolean validArr = true;
        TerminusArrival arr = this.arrival();
        if (arr == null)
        {
            validArr = false;
        }
        else if (arr.airportName() == null || arr.airportName().trim().length() == 0)
        {
            validArr = false;
        }
        else if (arr.runwayDateTime() == null)
        {
            validArr = false;
        }

        // Compare departure and arrival validity flags
        if (validDep && validArr)
        {
            vfrType = Itinerary.Type.VFR_DEP_ARR;
        }
        else if (validDep)
        {
            vfrType = Itinerary.Type.VFR_DEP;
        }
        else if (validArr)
        {
            vfrType = Itinerary.Type.VFR_ARR;
        }

        return vfrType;
    }

    ////////////////////////
    // From Trajectory
    ////////////////////////
   
    /**
     * Gets the arrival date/time of this {@link FlightLeg}.
     * <p>
     * @return the date/time of the last point in the {@link FlightLeg} (or <code>null</code> if the
     *         {@link FlightLeg} is empty).
     */
    public Timestamp computedArrivalTime()
    {
        Timestamp estimatedArrivalTime = null;
        if (this.flightRoute != null && this.flightRoute.size() > 0)
        {
            estimatedArrivalTime = this.flightRoute.get(this.flightRoute.size() - 1).timestamp();
        }

        return estimatedArrivalTime;
    }

    private Route cloneRoute()
    {
    	Route v = null;
        if (this.flightRoute != null)
        {
            v = new Route(this.flightRoute.size());
            for (TrajectoryPoint p : this.flightRoute)
            {
                v.add(p.clone());
            }
        }

        return v;
    }
    
    /**
     * Calculates the ground-distance of the flight by summing the great-circle distance between each point along point.
     * @return Total ground-distance in nmi
     */
    public double totalRouteDistance(){
    	return totalRouteDistances().getFirst();
    }
    
    /**
     * Calculates total route distance and portion of route distance inside the FIR region.
     * 
     * @return Pair containing total route distance (First) and route distance inside FIR (Second)
     */
    public Pair<Double,Double> totalRouteDistances(){
        Double totalDistance = 0.0;
        Double totalFirDistance = 0.0;
        for (int i=0; i<this.flightRoute.size()-1; i++){
        	TrajectoryPoint a = this.flightRoute.get(i);
        	TrajectoryPoint b = this.flightRoute.get(i+1);
        	
        	double segmentDist = GCUtilities.gcDistance(a , b);
            totalDistance += segmentDist;
            
            if (a.inFirRegion() && b.inFirRegion()){ // HK: make sure this logic is identical to FuelUsagePlugin for determining if fuel is counted as inside FIR
            	totalFirDistance += segmentDist;
            }
        }
        
        return Pair.create(totalDistance, totalFirDistance);
    }
    
    
    public void clearFix(Fix.Type type)
    {
        int fixIndex = -1;
        
        // Find existing fix index
        for (int i=0; i<this.flightRoute.size(); i++)
        {
            TrajectoryPoint point = this.flightRoute.get(i);
            if (point.resourceInfo() != null && point.resourceInfo() instanceof Fix && ((Fix)point.resourceInfo()).type() == type)
            {
                fixIndex = i;
                break;
            }
        }
    
        // If found, remove fix
        if (fixIndex > -1)
        {
            this.flightRoute.remove(fixIndex);
            
            if (Fix.Type.Arrival.equals(type)){
            	this.arrivalFixName = null;
            }
        }
    }
    
    public String getDepFix() {
    	// Find existing fix index
        for (int i=0; i<this.flightRoute.size(); i++)
        {
            TrajectoryPoint point = this.flightRoute.get(i);
            if (point.resourceInfo() != null && point.resourceInfo() instanceof Fix && ((Fix)point.resourceInfo()).type() == gov.faa.ang.swac.common.flightmodeling.Fix.Type.Departure)
            {
                return ((Fix)point.resourceInfo()).name();
            }
        }
    	return "";
    	
    }

	public List<TrajectoryPoint> getTrajectory() 
	{
		if (this.modeledState.ordinal() < ModeledState.TRAJECTORY.ordinal())
		{
			throw new IllegalStateException();
		}
		return Collections.unmodifiableList(this.flightRoute);
	}

	public void setTrajectory(List<TrajectoryPoint> val) 
	{
		validateTrajectory(val);
		this.flightRoute = new Route(val);
		this.modeledState = ModeledState.TRAJECTORY;
	}
	
	
	public List<TrajectoryPoint> getCrossings() 
	{
		if (this.modeledState.ordinal() < ModeledState.CROSSINGS.ordinal())
		{
			throw new IllegalStateException();
		}
		return Collections.unmodifiableList(this.flightRoute);
	}

	public void setCrossings(List<TrajectoryPoint> val) 
	{
		setCrossings(val, true);
	}
	
	public void setCrossings(List<TrajectoryPoint> val, boolean validate) 
	{
		if (validate)
		{
			validateCrossings(val);
		}
		this.flightRoute = new Route(val);
		this.modeledState = ModeledState.CROSSINGS;
	}
	
	private void findArrivalFix(List<TrajectoryPoint> points){
		this.arrivalFixName = null;
		for (int i=points.size()-1; i > 0; --i){
        	TrajectoryPoint pt = points.get(i);
        	if (ResourceType.AF.equals(pt.resourceType())){
        		this.arrivalFixName = pt.name();
        		break;
        	}
		}
	}
	
	/**
	 * A valid way has airport resource info appended to the beginning and end of the trajectory
	 * @param way
	 */
	private void validateWay(List<TrajectoryPoint> way)
    {
    	if (2 <= way.size())
        {
    		if (!way.get(0).resourceInfo().equals(this.departure()))
        	{
    			String msg = "Invalid departure airport node: " + 
                way.get(0).toTrajectoryPointString() + "\n" +
                this.toString() + "\n" + 
                way.toString();
    			logger.error(msg);
        		// TODO: This is a bad error but it's a little bit overkill to crash on it
    			// throw new RuntimeException(msg);
        	}
            
        	if (!way.get(way.size()-1).resourceInfo().equals(this.arrival()))
        	{
        		String msg = "Invalid arrival airport node: " + 
                way.get(way.size()-1).toTrajectoryPointString() + "\n" +
                this.toString() + "\n" + 
                way.toString();
        		logger.error(msg);
        		// TODO: This is a bad error but it's a little bit overkill to crash on it
        		// throw new RuntimeException(msg);
        	}
        }
    	
    	findArrivalFix(way);
    }

	/**
	 * A valid route is a valid way with altitude information assigned
	 * @param route
	 */
    private void validateRoute(List<TrajectoryPoint> route)
    {
    	validateWay(route);
    	for (TrajectoryPoint point : route)
    	{
    		if (point.altitude() == null)
    		{
    			throw new RuntimeException("Invalid route point: " + point.toTrajectoryPointString());
    		}
    	}
    }

    /**
     * A valid trajectory is a valid route with timestamps and additional metadata
     * @param trajectory
     */
    private void validateTrajectory(List<TrajectoryPoint> trajectory)
    {
    	validateRoute(trajectory);
    	
    	TrajectoryPoint p = trajectory.get(0);
    	for (TrajectoryPoint point : trajectory)
    	{
    		if (point.timestamp() == null)
    		{
                    logger.error("Invalid trajectory flightId=" + this.flightId + " point: " + point.toTrajectoryPointString());
//    			throw new RuntimeException("Invalid trajectory flightId=" + this.flightId + " point: " + point.toTrajectoryPointString());
    		}
    		if (Double.isNaN(point.instantaneousFuelRate()))
    		{
                    logger.error("Invalid trajectory flightId=" + this.flightId + " point: " + point.toTrajectoryPointString());
//    			throw new RuntimeException("Invalid trajectory flightId=" + this.flightId + " point: " + point.toTrajectoryPointString());
    		}
    		if (Double.isNaN(point.instantaneousTrueAirspeed()))
    		{
                    logger.error("Invalid trajectory flightId=" + this.flightId + " point: " + point.toTrajectoryPointString());
//    			throw new RuntimeException("Invalid trajectory flightId=" + this.flightId + " point: " + point.toTrajectoryPointString());
    		}
    		if (point.stage() == null)
    		{
                    logger.error("Invalid trajectory flightId=" + this.flightId + " point: " + point.toTrajectoryPointString());
//    			throw new RuntimeException("Invalid trajectory flightId=" + this.flightId + " point: " + point.toTrajectoryPointString());
    		}
    		if (p.timestamp().after(point.timestamp()))
    		{
                    logger.error("Invalid trajectory point sequence: flightId=" + this.flightId + " p1=" + p.toTrajectoryPointString() + "; p2=" + point.toTrajectoryPointString());
//    			throw new IllegalStateException("Invalid trajectory point sequence: flightId=" + this.flightId + " p1=" + p.toTrajectoryPointString() + "; p2=" + point.toTrajectoryPointString());
    		}
    		p = point;
    	}
    }

    /**
     * A valid node crossings list contains resource information for every point 
     * @param crossings
     */
	private void validateCrossings(List<TrajectoryPoint> crossings)
    {
    	validateTrajectory(crossings);
    	for (TrajectoryPoint point : crossings)
    	{
    		if (point.name() == null)
    		{
    			throw new RuntimeException("Invalid crossings point: " + point.toTrajectoryPointString());
        	}
    		if (point.resourceType() == null)
    		{
    			throw new RuntimeException("Invalid crossings point: " + point.toTrajectoryPointString());
    		}
    	}
    }


	public Itinerary parentAirframe() {
		return parentAirframe;
	}

	public void setParentAirframe(Itinerary parentAirframe) {
		this.parentAirframe = parentAirframe;
	}

	/**
	 * @return Unique identifier from the input schedule file
	 */
	public Integer flightId() {
		return flightId;
	}
	
	public Type ItinType()
	{
		return parentAirframe.type();		
	}
	
	public Carrier carrier() { 
		return this.parentAirframe.getCarrier(); 
	}
	
	/**
	 * Returns the prime carrier based on this legs' flight number.
	 * @return prime carrier (which may be the current one)
	 */
	public Carrier getPrimeCarrier() {
		if (this.parentAirframe.getCarrier() != null) {
			return this.parentAirframe.getCarrier().getPrimeCarrier(flightNumber);
		} else {
			return null;
		}
	}
	
	public String carrierId()
	{
		return this.parentAirframe.airlineIndicator();
	}
	
	public String filedBadaAircraftType()
	{
		return this.parentAirframe.aircraft.filedBadaAircraftType();
	}
	
	public String filedEtmsAircraftType()
	{
		return this.parentAirframe.aircraft.filedEtmsAircraftType();
	}
	
	public String atoUserClass()
	{
		return this.parentAirframe.aircraft.atoUserClass();
	}

	public void setFlightId(Integer flightId) {
		this.flightId = flightId;
	}

	/**
	 * @return Airline and flight number string; not guaranteed unique
	 */
	public String flightNumber() {
		return flightNumber;
	}

	public void setFlightNumber(String flightNumber) {
		this.flightNumber = flightNumber;
	}

	public String departureAirport() {
		return this.departure().airportName();
	}

	public String arrivalAirport() {
		return this.arrival().airportName();
	}

	public Timestamp scheduledDepDateTimestamp() {
		return this.departure().gateDateTime();
	}

	public Timestamp scheduledArrDateTimestamp() {
		Timestamp gateOutTime = this.departure().gateDateTime();
         // find gate arrival time in minutes from simulation start
        // try at-gate time first (should always work for NASPAC)
        Timestamp simGateInTime = null;
        if (this.arrival().gateDateTime() != null)
        {
            simGateInTime = this.arrival().gateDateTime();
        }
        else if (this.arrival().runwayDateTime() != null)
        {
        	simGateInTime = this.arrival().runwayDateTime();
        	this.arrival.setGateDateTime(simGateInTime);
        }
        else
        {
            simGateInTime = this.computedArrivalTime();
            this.arrival.setGateDateTime(simGateInTime);
            this.arrival.setRunwayDateTime(simGateInTime);
        }
        
        // adjust arrival gate time if needed
        if (gateOutTime != null && simGateInTime.before(gateOutTime))
        {
        	Timestamp newSimGateInTime = this.computedArrivalTime();
            logger.debug("FindCrossingsOutputFileWriter: Schedule ID " + this.flightId() + 
                    ": Sim gate-in time (" + simGateInTime + ") before pushback time (" + gateOutTime +
                    "). Adjusting sim arrival time to \"Sim pushback time + enroute time\" (" + newSimGateInTime + ").");
            simGateInTime = newSimGateInTime;
            this.arrival.setGateDateTime(simGateInTime);
            this.arrival.setRunwayDateTime(simGateInTime);
        }
        
        return simGateInTime;
	}

	public Timestamp edct() {
		return edct;
	}
	
	public EdctSource edctSource() {
		return edctSource;
	}

	public void setEdct(Timestamp edct, EdctSource edctSource) {
		this.edct = edct;
		this.edctSource = edctSource; 
	}

	public void cancelEdct()
	{
		this.edct = new Timestamp(0);
		this.edctSource = null;
	}

	public Route flightRoute() {
		return flightRoute;
	}

	
	public double getTaxiInDistSample() {
		return taxiInDistSample;
	}

	public void setTaxiInDistSample(RandomStream stream) {
		this.taxiInDistSample = stream.getDistributionHoldTime(taxiInDist);
	}
	
	public double getTaxiOutDistSample() {
		return taxiOutDistSample;
	}

	public void setTaxiOutDistSample(RandomStream stream) {
		this.taxiOutDistSample = stream.getDistributionHoldTime(taxiOutDist);
	}

	/**
	 * Draws a uniformly distributed double between 0 and 1 using the route shortening dedicated random number sequence 
	 */
	public double drawRandomValueForRouteShortening() { 
		return routeShorteningStream.uniform(0., 1.); 
	}
	
	public void setRouteShorteningRandomStream(RandomStream routeShorteningStream) {
		this.routeShorteningStream = routeShorteningStream;
	}
	
	public double turnaroundDistMean() {
		return turnaroundDist.getMean();
	}
	
	public long drawTurnaroundHoldTime(RandomStream turnaroundRandomStream) {
		double unadjHoldTime = turnaroundRandomStream.getDistributionHoldTime(turnaroundDist);
		long drawnValue = (long) Math.max(Math.round(unadjHoldTime), 0.0);
		return drawnValue;
	}
	
	public void setTurnaroundDist(TimeDistribution turnaroundDist) {
		this.turnaroundDist = turnaroundDist;
	}

	public TimeDistribution pushbackDist() {
		return pushbackDist;
	}
	
	public void setPushbackDist(TimeDistribution pushbackDist) {
		this.pushbackDist = pushbackDist;
	}
	
	public void setTaxiInDist(TimeDistribution taxiInDist) {
		this.taxiInDist = taxiInDist;
	}

	public void setTaxiOutDist(TimeDistribution taxiOutDist) {
		this.taxiOutDist = taxiOutDist;
	}

	public long drawRerouteClearanceHoldTime(RandomStream rerouteClearanceRandomStream) {
		double unadjHoldTime = rerouteClearanceRandomStream.getDistributionHoldTime(rerouteClearanceDist);
		return (long) Math.max(Math.round(unadjHoldTime), 0.0);
	}
	
	public void setRerouteClearanceDist(TimeDistribution rerouteClearanceDist) {
		this.rerouteClearanceDist = rerouteClearanceDist;
	}
	
	public long drawDepartureRampHoldTime(RandomStream rampRandomStream) {
		double unadjHoldTime = rampRandomStream.getDistributionHoldTime(departureRampDist);
		return (long) Math.max(Math.round(unadjHoldTime), 0.0);
	}

	public void setDepartureRampDist(TimeDistribution departureRampDist) {
		this.departureRampDist = departureRampDist;
	}
	
	public long getMinimumDepartureRampServiceTime() { 
		return minimumDepartureRampServiceTime;
	}
	
	public void setMinimumDepartureRampServiceTime(long minimumRampTime) { 
		this.minimumDepartureRampServiceTime  = minimumRampTime;
	}
	
	public long drawArrivalRampHoldTime(RandomStream rampRandomStream) {
		double unadjHoldTime = rampRandomStream.getDistributionHoldTime(arrivalRampDist);
		return (long) Math.max(Math.round(unadjHoldTime), 0.0);
	}

	public void setArrivalRampDist(TimeDistribution arrivalRampDist) {
		this.arrivalRampDist = arrivalRampDist;
	}
	
	public long getMinimumArrivalRampServiceTime() { 
		return minimumArrivalRampServiceTime;
	}
	
	public void setMinimumArrivalRampServiceTime(long minimumRampTime) { 
		this.minimumArrivalRampServiceTime  = minimumRampTime;
	}
	
	public void setRerouteFlag(boolean flag) {
		isRerouted = flag;
	}
	
	public boolean isRerouted() {
		return isRerouted;
	}
	
	public void setFailedReroute(boolean flag){
		this.failedReroute = flag;
	}
	
	public boolean failedReroute(){
		return this.failedReroute;
	}
	
	public Boolean getRerouteClearanceFlag() {
    	return rerouteClearanceFlag;
    }
    
    public void setRerouteClearanceFlag(boolean flag) {
    	rerouteClearanceFlag = flag;
    }

	public boolean eligibleForDepartureRampBypass() {
		return eligibleForDepartureRampBypass;
	}
	
	public void setEligibleForDepartureRampBypass() { 
		eligibleForDepartureRampBypass = true;
	}
	
	public boolean eligibleForArrivalRampBypass() {
		return eligibleForArrivalRampBypass;
	}
	
	public void setEligibleForArrivalRampBypass() { 
		eligibleForArrivalRampBypass = true;
	}
	
	public Aircraft aircraft()
	{
		return this.parentAirframe().aircraft();
	}
	
	// Returns the indices of the last of contiguous isRoutePoint points at the beginning of the trajectory, and the first
	// of contiguous isRoutePoint points at the end of the trajectory. The segment bounded by these two indices is the maximal
	// extent eligible for trajectory modeling without violating assumptions about what additional preprocessing is expected of points.
	public static int[] getSegmentIndices(List<TrajectoryPoint> trajectory)
	{
		// Trivial cases
		if (trajectory.size() < 2)
		{
			//logger.warn("Trajectory is too short");
			return new int[] { 0, 0 };
		}
		
    	int idx1 = 0;
    	int idx2 = trajectory.size() - 1;
    	
    	// Find the first point not flagged as invariant.
    	for (int i = 0; i < trajectory.size(); i++)
    	{
    		if (!trajectory.get(i).isRoutePoint())
    		{
    			break;
    		}
    		idx1 = i;
    	}
    	
    	// Then find the last point not flagged as invariant, working from the end
    	for (int i = trajectory.size() - 1; i >= 0; i--)
    	{
    		if (!trajectory.get(i).isRoutePoint())
    		{
    			break;
    		}
    		idx2 = i;
    	}
    	
    	if (idx1 >= idx2)
    	{
    		//logger.warn("Flight has no sub-segment eligible for trajectory modeling; all points are marked invariant\n" + trajectory);
    	}
    	
    	return new int[] { idx1, idx2 };
	}
	
	/**
	 * @return BADA nominal cruise TAS at filed altitude, in kts
	 */
	public double getBadaCruiseAirspeed() {
		try {
			return aircraft().badaRecord().trueAirSpeed(filedAltitude(), FlightStage.CRUISE);
		} catch (Exception ex) {
			return Double.NaN;
		}
	}
	
	// XXX: This may be redundant with something elsewhere
	/** 
	 * Assumptions: 
	 * 	(1) Calling code expects Double.NaN for error cases rather than an exception, 
	 * 	(2) Euclidean approximation is close enough, 
	 * 	(3) Modeled state of trajectory makes it valid to retrieve the way() here 
	 */
	
	public double getTotalRouteDistance() {
		List<TrajectoryPoint> route = this.way();
		if (route.size() < 2) {
			return Double.NaN;
		} else if (route.size() == 2) {
			return GCUtilities.euclideanDistance(route.get(0), route.get(1));
		} else {
			double retVal = 0.0;
			Iterator<TrajectoryPoint> iter = route.iterator();
			TrajectoryPoint last = null;
			TrajectoryPoint next = iter.next();
			while (iter.hasNext()) {
				last = next;
				next = iter.next();
				retVal += GCUtilities.euclideanDistance(last, next);
			}
			return retVal;
		}
	}

	/**
	 * Sets the oceanic separation retriever implementation.  Currently set in RunSim
	 */
	public void setOceanSeparationRetriever(OceanicSeparationRetriever oceanicSeparationRetriever) {
		this.oceanicSeparationRetriever = oceanicSeparationRetriever;
	}
	
	/**
	 * Sets the step climb retry time retriever implementation.  Currently set in RunSim
	 */
	public void setStepClimbRetryTimeRetriever(StepClimbRetryTimeRetriever stepClimbRetryTimeRetriever) {
		this.stepClimbRetryTimeRetriever = stepClimbRetryTimeRetriever;
	}
	
    /**
     * Returns the separation standard in nautical miles.
     * 
	 * @param oceanicRegion
	 * @param speed if separation standard is given in time, speed will be used to convert value to distance (nm)
	 * @return separation standard in nautical miles
	 */
    public int getSeparationStandard(String oceanicRegion, double speed) {
        return oceanicSeparationRetriever.getOceanicSeparation(oceanicRegion, parentAirframe.equipmentSuffix(), speed);
    }
    
    /**
     * Returns the step climb retry time interval in millis
     * 
     * @param oceanicRegion
     * @return step climb retry time interval in millis
     */
    public long getStepClimbRetryTime(String oceanicRegion) {
        return stepClimbRetryTimeRetriever.getStepClimbRetryTime(oceanicRegion, parentAirframe.equipmentSuffix());
    }
    
    /**
     * Interface for retrieving/calculating oceanic separations.  Implementating class will be javascript, 
     */
    public interface OceanicSeparationRetriever {
    	int getOceanicSeparation(String oceanicRegion, EquipmentSuffix equipmentSuffix, double speed);
    }
    
    /**
     * Interface for retrieving/calculating step climb retry times.  Implementating class will be javascript, 
     */
    public interface StepClimbRetryTimeRetriever {
    	long getStepClimbRetryTime(String oceanicRegion, EquipmentSuffix equipmentSuffix);
    }
}