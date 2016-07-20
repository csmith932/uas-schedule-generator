/**
 * Copyright "TBD", Metron Aviation & CSSI.  All rights reserved.
 * 
 * This computer Software was developed with the sponsorship of the U.S. Government
 * under Contract No. DTFAWA-10-D-00033, which has a copyright license in accordance with AMS 3.5-13.(c)(1).
 */

package gov.faa.ang.swac.common.flightmodeling;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import gov.faa.ang.swac.common.datatypes.Altitude;
import gov.faa.ang.swac.common.datatypes.Latitude;
import gov.faa.ang.swac.common.datatypes.Longitude;
import gov.faa.ang.swac.common.datatypes.Timestamp;
import gov.faa.ang.swac.common.flightmodeling.AltitudeRestriction.RestrictionType;
import gov.faa.ang.swac.common.flightmodeling.IResourceInfo.ResourceType;
import gov.faa.ang.swac.common.flightmodeling.fileio.BadaRecord;
import gov.faa.ang.swac.common.geometry.GCPoint;
import gov.faa.ang.swac.common.geometry.GCPointAlt;
import gov.faa.ang.swac.common.geometry.GCPointAltTime;


/**
 * An implementation of {@link ITrajectoryPoint} that includes information useful
 * for trajectories. Information such as the stage of flight at the point, the 
 * instantaneous true airspeed, fuel burn rate, and climb rate, and wind information
 * at that point, if wind has been input into the system.
 * 
 * @author James Bonn
 */
public class TrajectoryPoint extends GCPointAltTime implements Serializable
{
    private static final long serialVersionUID = 5512891876094666819L;
    
    // toString related members
    protected static final String SEP = "/";
    public static final String TEXT_RECORD_KEY = "TRAJECTORYPOINT: name/lat/lon/alt/time/isWayPoint/isRoutePoint/altRest/altRest/...";
    
    //========================================
    // From IWayPoint
    //========================================
    private List<AltitudeRestriction> altitudeRestrictions = new ArrayList<AltitudeRestriction>(1);

    //========================================
    // From ITrajectoryPoint
    //========================================
    
    private BadaRecord.FlightStage flightStage = null;
    private double instantaneousTrueAirspeed = Double.NaN; // knots
    private double instantaneousFuelRate = Double.NaN; // kg/min
    private double delayFuelRate = Double.NaN; // kg/min  This is the rate we'll use when certain Resources (AF, DF, RN) are in a delay queue.
    private boolean isRoutePoint = false;
    protected boolean isRunwayPoint = false;
    private IResourceInfo resourceInfo = null;
    
    /** Field for miscellaneous metadata that a module would like to attach. 
     * Since it has no defined semantics, it should not be trusted from one module to another */
    private String tag;
    
    public TrajectoryPoint()
    {
    	super();
    }
    
    public TrajectoryPoint(GCPoint point)
    {
        super(point);
    }
    
    public TrajectoryPoint(GCPointAlt point)
    {
        super(point);
    }
    
    public TrajectoryPoint(GCPointAltTime point)
    {
        super(point);
    }
    
    public TrajectoryPoint(Latitude lat, Longitude lon)
    {
        super(lat, lon);
    }
    
    public TrajectoryPoint(Latitude lat, Longitude lon, Altitude alt)
    {
        super(lat, lon, alt);
    }
    
    public TrajectoryPoint(Latitude lat, Longitude lon, Altitude alt, Timestamp t)
    {
        super(lat, lon, alt, t);
    }
    
    /**
     * Copies over all the member variables of original into a new instance of TrajectoryPoint and replaces the 'resourceInfo' member 
     * of new instance with newResourceType.  Will also set value of 'accruedDelayAtPoint' in new instance to zero if specified by caller.
     * 
     * 
     * @param original
     * @param newResourceType used to replace 'resourceInfo' of new instance
     * @param clearAccruedDelay set to TRUE to zero out 'accruedDelayAtPoint' value in new instance.
     * @return new instance of TrajectoryPoint
     */
    public static TrajectoryPoint newInstance(TrajectoryPoint original, IResourceInfo newResourceType, boolean clearAccruedDelay){
    	TrajectoryPoint p = new TrajectoryPoint(original);
   		p.resourceInfo = newResourceType;
   		
    	if (clearAccruedDelay){
    		p.accruedDelayAtPoint = 0.0;
    	}
    	
    	return p;
    }
    
    public TrajectoryPoint(TrajectoryPoint point)
    {
    	super(point);
        
    	this.flightStage = point.flightStage;
    	this.instantaneousFuelRate = point.instantaneousFuelRate;
    	this.delayFuelRate = point.delayFuelRate;
    	this.instantaneousTrueAirspeed = point.instantaneousTrueAirspeed;
    	this.isRoutePoint = point.isRoutePoint;
    	this.isRunwayPoint = point.isRunwayPoint;
        
        try {
            this.resourceInfo = (point.resourceInfo == null ? null : point.resourceInfo.clone());
        } catch (CloneNotSupportedException cnse) {
            this.resourceInfo = point.resourceInfo;
        }
        
        this.groundSpeed = point.groundSpeed;
        this.resourceTransitTime = point.resourceTransitTime;
        this.timeToNextResource = point.timeToNextResource;
        
        this.altitudeRestrictions.addAll(point.altitudeRestrictions());
        
        this.accruedDelayAtPoint = point.accruedDelayAtPoint;
        this.inFirRegion = point.inFirRegion;
        this.fuelUsedToPoint = point.fuelUsedToPoint;
        this.tag = point.tag;
    }

	public List<AltitudeRestriction> altitudeRestrictions()
    {
		return this.altitudeRestrictions;
    }

    /**
     * Returns <code>true</code> if any of the {@link AltitudeRestriction}s contained in this {@link TrajectoryPoint} match the input {@link RestrictionType}.<br>
     * Returns <code>false</code> otherwise. 
     * @param restrictionType
     */
    public boolean hasRestrictionType(AltitudeRestriction.RestrictionType restrictionType)
    {
        for (AltitudeRestriction altitudeRestriction : this.altitudeRestrictions())
        {
            if (altitudeRestriction.restrictionType() == restrictionType)
            {
                return true;
            }
        }
        
        return false;
    }

    /**
     * Returns <code>true</code> if any of the {@link AltitudeRestriction}s contained in this {@link TrajectoryPoint} match the input {@link AltitudeType}.<br>
     * Returns <code>false</code> otherwise. 
     * @param altitudeType
     */
    public boolean hasAltitudeType(AltitudeRestriction.AltitudeType altitudeType)
    {
        for (AltitudeRestriction altitudeRestriction : this.altitudeRestrictions())
        {
            if (altitudeRestriction.altitudeType() == altitudeType)
            {
                return true;
            }
        }
        
        return false;
    }
    
    //========================================
    // From ITrajectoryPoint
    //========================================
    
    /**
     * Sets the stage variable. Negative means Descent, positive means Climb, 0 means Cruise.
     * @param stage an integer representing what stage of the flight the current {@link TrajectoryPoint} is in.
     */
    public void setStage(BadaRecord.FlightStage flightStage)
    {
        this.flightStage = flightStage;
    }

    /**
     * Gets the flight stage of this {@link TrajectoryPoint}.
     */
    public BadaRecord.FlightStage stage()
    {
        return this.flightStage;
    }

    /**
     * Sets the instantaneous fuel burn rate.
     * @param instantaneousFuelRate Value of the fuel burn rate in kg/min.
     */
    public void setInstantaneousFuelRate(double instantaneousFuelRate)
    {
        this.instantaneousFuelRate = instantaneousFuelRate;
    }

    /**
     * Gets the instantaneous fuel burn rate.
     * @return Value of fuel burn rate in kg/min.
     */
    public double instantaneousFuelRate()
    {
        return this.instantaneousFuelRate;
    }
    
    /**
     * Sets the delay fuel burn rate.
     * @param fuelRate fuel burn rate in kg/min.
     */
    public void setDelayFuelRate(double fuelRate)
    {
        this.delayFuelRate = fuelRate;
    }

    /**
     * Gets the delay fuel burn rate.  Fuel rate value to use when a Flight is delayed at this particular Resource.
     * @return Value of fuel burn rate in kg/min.
     */
    public double delayFuelRate()
    {
        return this.delayFuelRate;
    }    
    
    
    /**
     * Sets the instantaneous true air speed.
     * @param instantaneousTrueAirspeed Value of the air speed, in knots.
     */
    public void setInstantaneousTrueAirspeed(double instantaneousTrueAirspeed)
    {
        this.instantaneousTrueAirspeed = instantaneousTrueAirspeed;
    }

    /**
     * Gets the instantaneous true air speed.
     * @return Air speed in knots
     */
    public double instantaneousTrueAirspeed()
    {
        return this.instantaneousTrueAirspeed;
    }
    
    /**
     * Returns {@link IResourceInfo} associated with this {@link TrajectoryPoint}. 
     */
    public IResourceInfo resourceInfo()
    {
        return this.resourceInfo;
    }

    /**
     * Returns {@link IResourceInfo} associated with this {@link TrajectoryPoint}. 
     */
    public void setResourceInfo(IResourceInfo resourceInfo)
    {
        this.resourceInfo = resourceInfo;
    }
    
    /**
     * Gets the flag that denotes if this {@link TrajectoryPoint} is a fixed route point that is not subject to revision in trajectory interpolation
     */
    public boolean isRoutePoint()
    {
        return this.isRoutePoint;
    }
    
    /**
     * Sets the flag that denotes if this {@link TrajectoryPoint} is considered invariant for trajectory re-modeling.
     */
    public void setIsRoutePoint(boolean isRoutePoint)
    {
        this.isRoutePoint = isRoutePoint;
    }
    
    public double modifier() {
        return this.resourceInfo == null ? IResourceInfo.DEFAULT_MODIFIER : this.resourceInfo.modifier();
    }
    
    public void setModifier(double modifier) {
        if (this.resourceInfo != null) {
        	this.resourceInfo.setModifier(modifier);
        }
    }
    
    public String modifierBase() {
    	return this.resourceInfo == null ? IResourceInfo.DEFAULT_MODIFIER_BASE : this.resourceInfo.modifierBase();
    }
    
    public void setModifierBase(String base) {
    	if (this.resourceInfo != null) {
        	this.resourceInfo.setModifierBase(base);
        }
    }
    
    public boolean isHoldingFix(){
   		if (ResourceType.WP.equals(this.resourceType())){
   			return ((WaypointResourceInfo)this.resourceInfo).isHoldingFix();
    	}
    	return false;
    }
    
    public boolean isIapPoint(){
    	if (ResourceType.WP.equals(this.resourceType())){
   			return ((WaypointResourceInfo)this.resourceInfo).isIapPoint();
    	}
    	return false;
    }
    
    @Override
    public String toString()
    {
    	return this.toWaypointString();
    }
    
    public String toTrajectoryPointString()
    {
    	String retVal = null;
    	try
    	{
    		retVal = this.toCompleteString();
    	}
    	catch (NullPointerException ex)
    	{
    		// There is a risk of null fields in the merged data structure
    		retVal = this.toWaypointString();
    	}
    	return retVal;
    }
    
    @Override
    public TrajectoryPoint clone()
    {
        return new TrajectoryPoint(this);
    }
    
    ///
    /// From CrossingsInfo
    ///
    
    private long resourceTransitTime;
    private long timeToNextResource; // minutes
    private double groundSpeed; // nm/hr
    
    /**
     * Returns the {@link IResourceInfo.ResourceType} associated with this {@link CrossingInfo}.
     */
    public IResourceInfo.ResourceType resourceType()
    {
        return this.resourceInfo == null ? null : this.resourceInfo.resourceType();
    }
	
    public boolean isResourceType(ResourceType resourceType) {
    	return this.resourceInfo == null ? false : this.resourceInfo.resourceType() == resourceType;
    }
    
    public boolean isResourceTypeAirport() { 
    	return this.resourceInfo == null ? false : this.resourceInfo.resourceType() == ResourceType.AP;
    }
    
    public boolean isResourceTypeArrivalFix() { 
    	return this.resourceInfo == null ? false : this.resourceInfo.resourceType() == ResourceType.AF;
    }
    
    public boolean isResourceTypeDepartureFix() { 
    	return this.resourceInfo == null ? false : this.resourceInfo.resourceType() == ResourceType.DF;
    }
    
    public boolean isResourceTypeSector() { 
    	return this.resourceInfo == null ? false : this.resourceInfo.resourceType() == ResourceType.SC;
    }

    public boolean isResourceTypeWaypoint() { 
    	return this.resourceInfo == null ? false : this.resourceInfo.resourceType() == ResourceType.WP;
    }
    
    public boolean isResourceTypeRestriction() { 
    	return this.resourceInfo == null ? false : this.resourceInfo.resourceType() == ResourceType.RN;
    }
    
    public boolean isResourceTypeArc() { 
    	return this.resourceInfo == null ? false : this.resourceInfo.resourceType() == ResourceType.AR;
    }
    
    public boolean isResourceTypeTopOfClimb() { 
    	return this.resourceInfo == null ? false : this.resourceInfo.resourceType() == ResourceType.TC;
    }

    public boolean isResourceTypeTopOfDescent() { 
    	return this.resourceInfo == null ? false : this.resourceInfo.resourceType() == ResourceType.TD;
    }
    
    public boolean isResourceTypeFreezeHorizon() { 
    	return this.resourceInfo == null ? false : this.resourceInfo.resourceType() == ResourceType.FH;
    }
    
    public boolean isResourceTypeOceanicEntry() { 
    	return this.resourceInfo == null ? false : this.resourceInfo.resourceType() == ResourceType.OE;
    }

    public boolean isResourceTypeOceanicExit() { 
    	return this.resourceInfo == null ? false : this.resourceInfo.resourceType() == ResourceType.OX;
    }
    
    public boolean isResourceTypeOceanicConflictCheck() { 
    	return this.resourceInfo == null ? false : this.resourceInfo.resourceType() == ResourceType.CC;
    }
    
    public boolean isResourceTypeOceanicClimbRequest() { 
    	return this.resourceInfo == null ? false : this.resourceInfo.resourceType() == ResourceType.CR;
    }
    
    public boolean isResourceTypeOceanicResource() { 
    	return ResourceType.CC.equals(this.resourceType()) || ResourceType.CR.equals(this.resourceType()) ||
    			ResourceType.OE.equals(this.resourceType()) || ResourceType.OX.equals(this.resourceType());
    }
    
	/**
	 * @return TRUE if Waypoint is Runway or Helipad Waypoint, FALSE otherwise
	*/
	public boolean isRunwayWaypoint(){
		return this.isRunwayPoint;
	}
    
	/**
	 * Returns the ground speed for this {@link CrossingInfo} (in nm/hr).
	 */
	public double groundSpeed()
	{
		return this.groundSpeed;
	}
	
	public void setGroundSpeed(double val)
	{
		this.groundSpeed = val;
	}
	
	///
	/// Fields used for reconciling information when trajectories are re-modeled (e.g. due to rerouting) mid-flight
	///
	
	private double accruedDelayAtPoint = 0; // minutes
	
	public double getAccruedDelayAtPoint() {
		return this.accruedDelayAtPoint;
	}
	
	public void accrueDelayAtPoint(double minutes) {
		this.accruedDelayAtPoint += minutes;
	}
	
	private FuelUsagePoint fuelUsedToPoint;
	
	public FuelUsagePoint getFuelUsedToPoint() {
		return this.fuelUsedToPoint;
	}
	
	public void setFuelUsedToPoint(FuelUsagePoint val) {
		this.fuelUsedToPoint = val;
	}
	
	private boolean inFirRegion;
	
	public void setInFirRegion(boolean val){
		this.inFirRegion = val;
	}
	
	/**
	 * 
	 * @return TRUE if this point is considered to be inside the Flight Information Region, FALSE otherwise.
	 */
	public boolean inFirRegion(){
		return this.inFirRegion;
	}
	
	///
	/// End re-modeling fields
	///
	
	  public String toWaypointString()
	  {
	      StringBuilder output = new StringBuilder();
	      
	      if (latitude() != null && longitude() != null) {
		      output.append(
		              String.format("%1$1.4f", this.latitude().degrees()) + SEP +
		              String.format("%1$1.4f", this.longitude().degrees()) + SEP +
		              (this.name == null ? "" : this.name) + SEP +
		              (this.timestamp() == null ? "" : this.timestamp().toTrajectoryPointString()) + SEP +
		              (this.resourceInfo == null ? "" : this.resourceInfo));
	      } else {
		      output.append(
		              String.format("%1$1.4f", Double.NaN) + SEP +
		              String.format("%1$1.4f", Double.NaN) + SEP +
		              (this.name == null ? "" : this.name) + SEP +
		              (this.timestamp() == null ? "" : this.timestamp().toTrajectoryPointString()) + SEP +
		              (this.resourceInfo == null ? "" : this.resourceInfo));
	      }
	      // Append AltitudeRestrictions (if specified)
	      if (this.altitudeRestrictions().size() > 0)
	      {
	          for (int i=0; i<this.altitudeRestrictions().size(); i++)
	          {
	              AltitudeRestriction altitudeRestriction = this.altitudeRestrictions().get(i);
	              if (altitudeRestriction != null)
	              {
	                  output.append(altitudeRestriction.toString());
	                  if (i < this.altitudeRestrictions().size()-1) { output.append(SEP); }
	              }
	          }
	      }
	      
	      return output.toString();
	  }

	public void setResourceTransitTime(long val) 
	{
		this.resourceTransitTime = val;
	}

	public void setTimeToNextResource(long val) 
	{
		this.timeToNextResource = val;
	}

	public String toCompleteString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TrajectoryPoint [altitudeRestrictions=");
		builder.append(altitudeRestrictions());
		builder.append(", flightStage=");
		builder.append(flightStage);
		builder.append(", instantaneousTrueAirspeed=");
		builder.append(instantaneousTrueAirspeed);
		builder.append(", instantaneousFuelRate=");
		builder.append(instantaneousFuelRate);
		builder.append(", instantaneousClimbRate=");
		builder.append("[DEPRECATED]");
		builder.append(", isRoutePoint=");
		builder.append(isRoutePoint);
		builder.append(", windPoint=");
		builder.append("[DEPRECATED]");
		builder.append(", resourceInfo=");
		builder.append(resourceInfo);
		builder.append(", resourceTransitTime=");
		builder.append(resourceTransitTime);
		builder.append(", timeToNextResource=");
		builder.append(timeToNextResource);
		builder.append(", groundSpeed=");
		builder.append(groundSpeed);
		builder.append(", timestamp=");
		builder.append(timestamp);
		builder.append(", altitude=");
		builder.append(altitude);
		builder.append(", latitude=");
		builder.append(latitude);
		builder.append(", longitude=");
		builder.append(longitude);
		builder.append(", name=");
		builder.append(name);
		builder.append("]");
		return builder.toString();
	}

	/**
	 * Returns the time to cross the resource for this {@link CrossingInfo} (in milliseconds).
	 * <p>
	 * <b>NOTE:</b> This is not to be confused with {@link #timeToNextNode}.
     * Resources may be contained inside other resources (e.g. a {@link Fix} within a {@link Sector}).
     * {@link #nodeCrossingTime} is the number of milliseconds required to cross the entirety of the current resource.
     * <p>
     * e.g.<br>
     * If you have just entered {@link Sector} <code>ZAB015</code>, will pass through {@link Fix} <code>DAVIS</code> in 1 minute,
     * and will enter {@link Sector} <code>ZAB016</code> in 10 minutes, then your {@link #nodeCrossingTime} is 10 minutes, but your
     * {@link #timeToNextNode} is 1 minute.
	 */
	public long nodeCrossingTime() {
		return this.resourceTransitTime;
	}

	/**
     * Returns the time to reach the next resource for this {@link CrossingInfo} (in milliseconds).
     * <p>
     * <b>NOTE:</b> This is not to be confused with {@link #nodeCrossingTime}.
     * Resources may be contained inside other resources (e.g. a {@link Fix} within a {@link Sector}).
     * {@link #timeToNextNode} is the number of milliseconds until the next resource, of any tyupe, is reached.
     * <p>
     * e.g.<br>
     * If you have just entered {@link Sector} <code>ZAB015</code>, will pass through {@link Fix} <code>DAVIS</code> in 1 minute,
     * and will enter {@link Sector} <code>ZAB016</code> in 10 minutes, then your {@link #timeToNextNode} is 1 minute, but your
     * {@link #nodeCrossingTime} is 10 minutes.
     */
	public long timeToNextNode() {
		return this.timeToNextResource;
	}
	
	@Override
	public String name()
	{
		if (this.resourceInfo == null) {
			return this.name == null ? "" : this.name;
		} else {
			return this.resourceInfo.name();
		}
	}
	
	public String getTag() {
		return tag;
	}
	
	public void setTag(String val) {
		tag = val;
	}
}