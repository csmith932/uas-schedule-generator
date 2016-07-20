/**
 * Copyright "TBD", Metron Aviation & CSSI.  All rights reserved.
 * 
 * This computer Software was developed with the sponsorship of the U.S. Government
 * under Contract No. DTFAWA-10-D-00033, which has a copyright license in accordance with AMS 3.5-13.(c)(1).
 */

package gov.faa.ang.swac.common.geometry;

import java.io.Serializable;

import gov.faa.ang.swac.common.datatypes.Altitude;
import gov.faa.ang.swac.common.datatypes.Angle;
import gov.faa.ang.swac.common.datatypes.Latitude;
import gov.faa.ang.swac.common.datatypes.Longitude;
import gov.faa.ang.swac.common.datatypes.Vector3D;
import gov.faa.ang.swac.common.utilities.Mathematics;

/**
 * This class represents a point on the surface of a unit-sphere.
 * <p>
 * The interface to the {@link GCPoint} is primarily through {@link Latitude} & {@link Longitude} but there is an underlying {@link Vector3D} that can be used for spherical geometry.
 * <p>
 * @see Latitude
 * @see Longitude
 * @author Jason Femino - CSSI, Inc.
 */
// TODO: Have a definable resolution (1 deg, 1 sec, .01 sec) for the point
public class GCPoint extends GCObject implements Comparable<GCPoint>, Serializable
{
	private static final long serialVersionUID = -8548330152477122091L;

	protected Latitude latitude = null;
	protected Longitude longitude = null;
	private Vector3D vector = null;
	
    public GCPoint()
	{
		super();
		latitude = Latitude.valueOfRadians(Double.NaN);
		longitude = Longitude.valueOfRadians(Double.NaN);
	}
	
	/**
	 * Storage constructor: Creates a new {@link GCPoint} storing the input {@link Latitude}, {@link Longitude}, & {@link Altitude} references.
	 * <p>
	 * <b>Note:</b> To create a new {@link GCPoint} that stores copies of input objects, use {@link #GCPoint(GCPoint)} copy constructor.
	 */
    public GCPoint(Latitude latitude, Longitude longitude)
	{
    	this.latitude = latitude;
    	this.longitude = longitude;
	}

	/**
	 * Copy constructor: Creates a new {@link GCPoint} by storing new copies of the input {@link GCPoint}'s members.
	 * <p>
	 * <b>Note:</b> To create a new {@link GCPoint} that stores the actual input objects, use {@link #GCPoint(Latitude, Longitude)} storage constructor.
	 */
    public GCPoint(GCPoint point)
    {
        super(point);
        
    	this.latitude = (point.latitude == null ? null : point.latitude.clone());
    	this.longitude = (point.longitude == null ? null : point.longitude.clone());
    }
    
    /**
     * Constructor. Creates a new {@link GCPoint} from a {@link Vector3D}.
     * <p>
     * <b>NOTE:</b> The new {@link GCPoint} will contain a reference to the {@link Vector3D} object, not a copy.
     * @param vector
     */
    public GCPoint(Vector3D vector)
    {
    	double[] latLon = SphericalUtilities.vectorToLatLon(vector);
    	this.latitude = Latitude.valueOfRadians(latLon[0]);
    	this.longitude = Longitude.valueOfRadians(latLon[1]);
    	this.vector = vector;
    }
    
    public Vector3D vector()
    {
    	// Create a new vector, if necessary
		if (this.vector == null)
		{
			this.vector = SphericalUtilities.latLonToVector(this.latitude.radians(), this.longitude.radians());
		}

    	return this.vector;
    }
    
    /**
     * Creates a copy of the input {@link GCPoint} by storing new copies of the input {@link GCPoint}'s members.
     */
    public void copy(GCPoint p2)
    {
    	this.latitude = new Latitude(p2.latitude);
    	this.longitude = new Longitude(p2.longitude);
    	this.vector = null; // Invalidate the current vector
    }
    
    public int compareTo(GCPoint p2)
    {
    	return compare(this, p2);    		
    }
    
    /**
     * Compares the relative lat/lon ordinates of p1 & p2.<p>
     * NOTE: Equalities below are determined by {@link Utilities}.
     * @param p1
     * @param p2
     * @return <ul>
     * <li>-1 if:</li>
     *    <ul>
     *    <li>p1 is null</li>
     *    <li>p1.latitude.degrees() < p2.latitude.degrees()</li>
     *    <li>p1.latitude.degrees() == p2.latitude.degrees() && p1.longitude.degrees() < p2.longitude.degrees()</li>
     *    </ul>
     * <li>1 if:</li>
     *    <ul>
     *    <li>p1 is non-null and p2 is null</li>
     *    <li>p1.latitude.degrees() > p2.latitude.degrees()</li>
     *    <li>p1.latitude.degrees() == p2.latitude.degrees() && p1.longitude.degrees() > p2.longitude.degrees()</li>
     *    </ul>
     * <li>0 if:</li>
     *    <ul>
     *    <li>p1 & p2 refer to the same object</li>
     *    <li>p1.latitude.degrees() == p2.latitude.degrees() && p1.longitude.degrees() == p2.longitude.degrees()</li>
     *    </ul>
     * </ul>
     */
	public int compare(GCPoint p1, GCPoint p2)
    {
		// Check if either reference is null
		if (p1 == null)
		{
			return -1;
		}
		else if (p2 == null)
		{
			return +1;
		}
		
		// Neither reference is null...
		if (p1.equals(p2))
		{
			return 0;
		}
		
		// If latitudes equal...
		if (Mathematics.equals(p1.latitude.degrees(), p2.latitude.degrees()))
		{
			// ... compare longitudes
			if (Mathematics.equals(p1.longitude.degrees(), p2.longitude.degrees()))
			{
				return 0;
			}
			else if (p1.longitude.degrees() < p2.longitude.degrees())
			{
				return -1;
			}
			else if (p1.longitude.degrees() > p2.longitude.degrees())
			{
				return +1;
			}
		}
		else if (p1.latitude.degrees() < p2.latitude.degrees())
		{
			return -1;
		}
		else if (p1.latitude.degrees() > p2.latitude.degrees())
		{
			return +1;
		}
		
		return -99; // JLF TODO: Returns this value arbitrarily. Might want to fix this someday.
    }
    
    
    
    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((latitude == null) ? 0 : latitude.hashCode());
		result = prime * result
				+ ((longitude == null) ? 0 : longitude.hashCode());
		return result;
	}

    /**
     * Determines if GCPoint is equivalent to "point".
     * GCPoints are equal if both Object references are the same OR if both Lat/Lon representations are the same. (See GCPoint.key()) 
     * @param o
     * @return true GCPoints are equivalent... false otherwise
     */
	@Override
    public boolean equals(Object obj)
    {
		if (obj == null || !(obj instanceof GCPoint))
		{
			return false;
		}

		// The tolerance units are Earth radii (4,000 miles or 6,400 km)
		// So, 1e-6 Earth radii is 21 feet or 6.4 meters.
		
    	return
    		this == obj ||
    		this.vector().equals(((GCPoint)obj).vector(),1e-6);
    }

	/**
     * Returns the {@link Latitude} of this {@link GCPoint} in degrees.
     */
    public Latitude latitude()
    {
    	return this.latitude;
    }
    
    public void setLatitude(Latitude latitude)
    {
    	this.latitude = latitude;
		this.vector = null; // Lat/lon have changed. Invalidate the vector.
    }
    
    /**
     * Returns the {@link Longitude} of this {@link GCPoint} in degrees.
     */
    public Longitude longitude()
    {
    	return this.longitude;
    }

    public void setLongitude(Longitude longitude)
    {
    	this.longitude = longitude;
		this.vector = null; // Lat/lon have changed. Invalidate the vector.
    }    
    
    public void setLatLon(Latitude latitude, Longitude longitude)
    {
    	this.latitude = latitude;
    	this.longitude = longitude;
		this.vector = null; // Lat/lon have changed. Invalidate the vector.
    }

    public boolean isWestOf(final GCPoint b)
    {
    	return this.longitude.isWestOf(b.longitude);
    }
    
    public boolean isEastOf(final GCPoint b)
    {
    	return this.longitude.isEastOf(b.longitude);
    }
    
    public boolean isNorthOf(final GCPoint b)
    {
    	return this.latitude.isNorthOf(b.latitude);
    }
    
    public boolean isSouthOf(final GCPoint b)
    {
    	return this.latitude.isSouthOf(b.latitude);
    }
    
    public GCPoint getAntipode()
    {
    	return new GCPoint(
    		new Vector3D(
    			vector()).complement());
    } 
    
    public boolean isAntipodeTo(final GCPoint b)
    {
    	return (getAntipode().equals(b));
    } 
    
	public static String toString(double latDeg, double lonDeg, Angle.Format format)
	{
		return Latitude.toString(latDeg, format) + "/" + Longitude.toString(lonDeg, format);
	}
	
	public static String toString(Latitude latitude, Longitude longitude, Angle.Format format)
	{
		if (latitude == null || longitude == null)
		{
			return null;
		}
		
		return toString(latitude.degrees(), longitude.degrees(), format);
	}
	
	public String toString(Angle.Format format)
	{
		return toString(this.latitude, this.longitude, format);
	}
	
	@Override
	public String toString()
	{
		return toString(this.latitude, this.longitude, Angle.Format.DECIMAL);
	}
	
	/**
	 * Given Latitude and Longitude values (in degrees), returns a formatted lat/lon string that identifies each GCPoint uniquely (to within 1 second of angle, that is).
	 * <p>
	 * <b>e.g.:</b> <code>toKey( 15.5, 31.25 )</code> yields <code>"153000/311500"</code>.
	 * @see #toString(double, double, gov.faa.ang.swac.common.datatypes.Angle.Format)
	 * @param latDeg - Latitude in degrees
	 * @param lonDeg - Longitude in degrees
	 * @return Lat/Lon key String
	 */
	public static String toKey(double latDeg, double lonDeg)
	{
		return toString(latDeg, lonDeg, Angle.Format.COMPACT);
	}
	
	/**
	 * Given {@link Latitude} and {@link Longitude} values, returns a formatted lat/lon string that identifies each GCPoint uniquely (to within 1 second of angle, that is).
	 * <p>
	 * <b>e.g.:</b> <code>toKey( 15.5, 31.25 )</code> yields <code>"153000/311500"</code>.
	 * @see #toString(double, double, gov.faa.ang.swac.common.datatypes.Angle.Format)
	 * @return Lat/Lon key String
	 */
	public static String toKey(Latitude latitude, Longitude longitude)
	{
		return toString(latitude, longitude, Angle.Format.COMPACT);
	}
	
	/**
	 * Returns a formatted lat/lon string that identifies each GCPoint uniquely (to within 1 second of angle, that is).
	 * @see #toKey(double, double)
	 * @param latitude - Latitude in degrees
	 * @param longitude - Longitude in degrees
	 * @return Lat/Lon key String
	 */
	public String key()
	{
		return toString(this.latitude, this.longitude, Angle.Format.COMPACT);					
	}
	
	
    @Override
    public GCPoint clone() {
        return new GCPoint(this);
    }
}