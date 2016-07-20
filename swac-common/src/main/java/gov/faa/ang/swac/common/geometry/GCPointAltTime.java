/**
 * Copyright "TBD", Metron Aviation & CSSI.  All rights reserved.
 * 
 * This computer Software was developed with the sponsorship of the U.S. Government
 * under Contract No. DTFAWA-10-D-00033, which has a copyright license in accordance with AMS 3.5-13.(c)(1).
 */

package gov.faa.ang.swac.common.geometry;

import gov.faa.ang.swac.common.datatypes.Altitude;
import gov.faa.ang.swac.common.datatypes.Latitude;
import gov.faa.ang.swac.common.datatypes.Longitude;
import gov.faa.ang.swac.common.datatypes.Timestamp;

/**
 * This class extends {@link GCPointAlt} by adding a {@link Timestamp}.
 * 
 * @see GCPointAlt
 * @author Jason Femino - CSSI, Inc.
 */
public class GCPointAltTime extends GCPointAlt
{
	private static final long serialVersionUID = 8031776693618741859L;

	protected Timestamp timestamp = new Timestamp();
    private boolean smoothable = false; //used to identify waypoints eligible for turn smoothing.
	
    protected GCPointAltTime()
	{
		super();
	}
	
    /**
	 * Storage constructor: Creates a new {@link GCPointAltTime} storing the input {@link Latitude}, {@link Longitude}, & {@link Altitude} references.
	 * <p>
	 * <b>Note:</b> To create a new {@link GCPointAltTime} that stores copies of input objects, use {@link #GCPointAltTime(GCPointAltTime)} copy constructor.
	 */
    public GCPointAltTime(Latitude latitude, Longitude longitude)
	{
    	super(latitude, longitude);
	}
    
    /**
	 * Storage constructor: Creates a new {@link GCPointAltTime} storing the input {@link Latitude}, {@link Longitude}, & {@link Altitude} references.
	 * <p>
	 * <b>Note:</b> To create a new {@link GCPointAltTime} that stores copies of input objects, use {@link #GCPointAltTime(GCPointAltTime)} copy constructor.
	 */
    public GCPointAltTime(Latitude lat, Longitude lon, Altitude altitude)
	{
    	super(lat, lon, altitude);
	}

    /**
	 * Storage constructor: Creates a new {@link GCPointAltTime} storing the input {@link Latitude}, {@link Longitude}, {@link Altitude}, & {@link Timestamp} references.
	 * <p>
	 * <b>Note:</b> To create a new {@link GCPointAltTime} that stores copies of input objects, use {@link #GCPointAltTime(GCPointAltTime)} copy constructor.
	 */
    public GCPointAltTime(Latitude lat, Longitude lon, Altitude alt, Timestamp time)
	{
    	super(lat, lon, alt);
		this.timestamp = new Timestamp(time);
	}

    /**
	 * Copy constructor: Creates a new {@link GCPointAltTime} by storing new copies of the input {@link GCPointAltTime}'s members.
	 * <p>
	 * <b>Note:</b> To create a new {@link GCPointAltTime} that stores the actual input objects, use {@link #GCPointAltTime(Latitude, Longitude, Altitude)} storage constructor.
	 */
    public GCPointAltTime(GCPoint point)
    {
    	super(point);
    }
	
    /**
	 * Copy constructor: Creates a new {@link GCPointAltTime} by storing new copies of the input {@link GCPointAltTime}'s members.
	 * <p>
	 * <b>Note:</b> To create a new {@link GCPointAltTime} that stores the actual input objects, use {@link #GCPointAltTime(Latitude, Longitude, Altitude)} storage constructor.
	 */
    public GCPointAltTime(GCPointAlt point)
    {
    	super(point);
    }
	
    /**
	 * Copy constructor: Creates a new {@link GCPointAltTime} by storing new copies of the input {@link GCPointAltTime}'s members.
	 * <p>
	 * <b>Note:</b> To create a new {@link GCPointAltTime} that stores the actual input objects, use {@link #GCPointAltTime(Latitude, Longitude, Altitude, Timestamp)} storage constructor.
	 */
    public GCPointAltTime(GCPointAltTime point)
    {
    	super(point);
    	this.timestamp = (point.timestamp == null ? null : point.timestamp.clone());
    	this.smoothable = point.smoothable;
    }
    
    /**
     * Creates a copy of the input {@link GCPointAltTime} by storing new copies of the input {@link GCPointAltTime}'s members.
     */
    public void copy(GCPointAltTime p2)
    {
    	super.copy(p2);
    	this.timestamp = p2.timestamp == null ? null : new Timestamp(p2.timestamp);
    	this.smoothable = p2.smoothable;
    }
	
    public Timestamp timestamp()
    {
    	return this.timestamp;
    }
    
    public void setTimestamp(Timestamp timestamp)
    {
    	this.timestamp = timestamp;
    }
    
    public boolean isSmoothable(){
    	return this.smoothable;
    }
    
    public void setSmoothable(boolean val){
    	this.smoothable = val;
    }
    
	@Override
	public String toString()
	{
		return super.toString() + "/" + (this.timestamp != null ? this.timestamp.toString() : "");
	}
    
    @Override
    public GCPointAltTime clone() {
        return new GCPointAltTime(this);
    }
}