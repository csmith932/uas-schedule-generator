/**
 * Copyright "TBD", Metron Aviation & CSSI.  All rights reserved.
 * 
 * This computer Software was developed with the sponsorship of the U.S. Government
 * under Contract No. DTFAWA-10-D-00033, which has a copyright license in accordance with AMS 3.5-13.(c)(1).
 */

package gov.faa.ang.swac.common.geometry;

import java.io.Serializable;

import gov.faa.ang.swac.common.datatypes.Altitude;
import gov.faa.ang.swac.common.datatypes.Latitude;
import gov.faa.ang.swac.common.datatypes.Longitude;

/**
 * This class extends {@link GCPoint} by adding {@link Altitude}.
 * 
 * @see GCPoint
 * @author Jason Femino - CSSI, Inc.
 */
public class GCPointAlt extends GCPoint implements Serializable
{
	private static final long serialVersionUID = 7647953860606314673L;

	protected Altitude altitude = null;
	
    protected GCPointAlt()
	{
		super();
		this.altitude = Altitude.NULL;
	}
	
    /**
	 * Storage constructor: Creates a new {@link GCPointAlt} storing the input {@link Latitude}, {@link Longitude}, & {@link Altitude} references.
	 * <p>
	 * <b>Note:</b> To create a new {@link GCPointAlt} that stores copies of input objects, use {@link #GCPointAlt(GCPointAlt)} copy constructor.
	 */
    public GCPointAlt(Latitude latitude, Longitude longitude)
	{
    	super(latitude, longitude);
	}

    /**
	 * Storage constructor: Creates a new {@link GCPointAlt} storing the input {@link Latitude}, {@link Longitude}, & {@link Altitude} references.
	 * <p>
	 * <b>Note:</b> To create a new {@link GCPointAlt} that stores copies of input objects, use {@link #GCPointAlt(GCPointAlt)} copy constructor.
	 */
    public GCPointAlt(Latitude lat, Longitude lon, Altitude altitude)
	{
    	super(lat, lon);
		this.altitude = altitude;
	}

    /**
	 * Copy constructor: Creates a new {@link GCPointAlt} by storing new copies of the input {@link GCPointAlt}'s members.
	 * <p>
	 * <b>Note:</b> To create a new {@link GCPointAlt} that stores the actual input objects, use {@link #GCPointAlt(Latitude, Longitude, Altitude)} storage constructor.
	 */
    public GCPointAlt(GCPoint point)
    {
    	super(point);
    }
	
    /**
	 * Copy constructor: Creates a new {@link GCPointAlt} by storing new copies of the input {@link GCPointAlt}'s members.
	 * <p>
	 * <b>Note:</b> To create a new {@link GCPointAlt} that stores the actual input objects, use {@link #GCPointAlt(Latitude, Longitude, Altitude)} storage constructor.
	 */
    public GCPointAlt(GCPointAlt point)
    {
    	super(point);
    	this.altitude = point.altitude;
    }
	
    /**
     * Creates a copy of the input {@link GCPointAlt} by storing new copies of the input {@link GCPointAlt}'s members.
     */
    public void copy(GCPointAlt p2)
    {
    	super.copy(p2);
    	this.altitude = p2.altitude;
    }
    
    public Altitude altitude()
    {
    	return this.altitude;
    }
    
    public Integer flightLevel() {
    	if (altitude == null)
    		return null;
    	return altitude.flightLevel();
    }
    
    public void setAltitude(Altitude altitude)
    {
    	this.altitude = altitude;
    }
    
	@Override
	public String toString()
	{
		return super.toString() + (this.altitude != null ? "/" + this.altitude.toString() : "");
	}
    
    @Override
    public GCPointAlt clone() {
        return new GCPointAlt(this);
    }
}