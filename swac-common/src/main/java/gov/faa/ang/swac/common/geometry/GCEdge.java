/**
 * Copyright "TBD", Metron Aviation & CSSI.  All rights reserved.
 * 
 * This computer Software was developed with the sponsorship of the U.S. Government
 * under Contract No. DTFAWA-10-D-00033, which has a copyright license in accordance with AMS 3.5-13.(c)(1).
 */

package gov.faa.ang.swac.common.geometry;

import gov.faa.ang.swac.common.datatypes.Vector3D;
import gov.faa.ang.swac.common.geometry.GCPoint;
import gov.faa.ang.swac.common.geometry.SphericalUtilities.IntersectionType;
import gov.faa.ang.swac.common.geometry.SphericalUtilities.InvalidInputException;


import java.util.Comparator;

/**
 * A class that represents a Great Circle segment... that is the part of the great circle that uniquely connects two {@link GCPoint}s.
 * <p>
 * If two {@link GCPoint}s are not directly opposite to each other on a sphere, there are always two segments that connect them (the long segment and the short segment).
 * A {@link GCEdge} is always the short segment.
 * <p>
 * NOTE: For a {@link GCEdge} to uniquely connect two {@link GCPoint}s, those points:<br>
 * - Must be unique (i.e. they may not represent the same location [e.g. (90� lat, 0� lon) and (90� lat, 45� lon) are both the North Pole]).<br>
 * - Must not be anti-podal (i.e. directly opposite each other on the sphere [e.g. the North & South Poles]).
 * <p>
 * @author Jason Femino - CSSI, Inc.
 */
public class GCEdge extends GCObject
{
	private static final long serialVersionUID = -4787474298369569968L;
	private int instanceCounter = 0;
	public int instanceId = instanceCounter++;
	protected GCPoint first = null;
	protected GCPoint second = null;
	
	public GCEdge(GCPoint first, GCPoint second)
	{
		this.first = first;
		this.second = second;
	}
	
	/**
	 * Returns the first {@link GCPoint} of {@link GCEdge}.
	 */
	public GCPoint first()
	{
		return this.first;
	}
	
	/**
	 * Sets first {@link GCPoint} of {@link GCEdge}.
	 */
	public void setFirst(GCPoint p)
	{
		this.first = p;
	}
	
	/**
	 * Returns the second {@link GCPoint} of {@link GCEdge}.
	 */
	public GCPoint second()
	{
		return this.second;
	}
	
	/**
	 * Sets second {@link GCPoint} of {@link GCEdge}.
	 */
	public void setSecond(GCPoint p)
	{
		this.second = p;
	}
	
//	/**
//	 * Swaps the {@link #first} {@link GCPoint} with the {@link #second} {@link GCPoint}. 
//	 */
//	@Deprecated
//	public GCEdge reverse()
//	{
//		GCPoint temp = this.first;
//		this.first = this.second;
//		this.second = temp;
//		return this;
//	}
	
	/**
	 * Compares this {@link GCEdge} to the specified object.
	 * The result is true if the argument is not null and is a {@link GCEdge} that represents the same {@link GCPoint}s as this {@link GCEdge} (in either order).
	 * @return <code>true</code> if argument represents a {@link GCEdge} that is equivalent to this {@link GCEdge}, false otherwise. 
	 */
	@Override
	public boolean equals(Object o)
	{
		if (o == null || !(o instanceof GCEdge))
		{
			return false;
		}
		
		// Edges are equal if they have the same end-points (or are the same reference)
		GCEdge edge = (GCEdge)o;
		if ( this == edge ||
		    (this.first.vector().equals(edge.first.vector()) && this.second.vector().equals(edge.second.vector())) || 
			(this.first.vector().equals(edge.second.vector()) && this.second.vector().equals(edge.first.vector())) ) 
		{
			return true;
		}
		
		return false;
	}
	
	@Override
	public int hashCode()
	{
	    return this.first.hashCode() + this.second.hashCode();
	}
	
	public static String toString(GCPoint p1, GCPoint p2)
	{
		return p1.toString() + " -> " + p2.toString();		
	}

//	/**
//	 * Given two points "p1" & "p2", returns a String that identifies each {@link GCEdge} uniquely.
//	 * 
//	 * The {@link GCEdge} from "p1" to "p2" is considered equivalent to the {@link GCEdge} from "p2" to "p1".
//	 * Likewise the toKey() method returns the same value despite the order of the parameters.
//	 * The {@link GCEdge} key is a concatenation of the {@link GCPoint}.key()s with " - " between them.
//	 * @param p1
//	 * @param p2
//	 * @return {@link GCEdge} key String
//	 */
//	@Deprecated
//	public static String toKey(GCPoint p1, GCPoint p2)
//	{
//		return toString(p1, p2);
//	}
//
//	/**
//	 * Returns one of the {@link String}s that identifies each {@link GCEdge} uniquely.
//	 */
//	@Deprecated
//	public String key()
//	{
//		return toKey(this.first, this.second);
//	}
//	
//	/**
//	 * Returns the other {@link String} that identifies each {@link GCEdge} uniquely.
//	 */
//	@Deprecated
//	public String keyReverse()
//	{
//		return toKey(this.second, this.first);
//	}
//	
//	/**
//	 * Vector defined by the points first->second.
//	 */
//	@Deprecated
//	public Vector3D asVector()
//	{
//		return new Vector3D(this.first.vector().minus(this.second.vector()));
//	}
//
//	/**
//	 * Determines if p is on the {@link GCEdge} between the endpoints.
//	 * @param p
//	 * @return true if p on the Great Circle defined by {@link GCEdge} (and between the endpoints)... false otherwise.
//	 * @throws InvalidInputException 
//	 */
//	@Deprecated
//	public boolean isOn(GCPoint p) throws InvalidInputException
//	{
//		return SphericalUtilities.isOn(this.first.vector(), this.second.vector(), p.vector());
//	}
//
//	/**
//	 * Determines if p is to the left of the Great Circle defined by {@link GCEdge}.
//	 * @param p
//	 * @return true if p is strictly left of the Great Circle defined by {@link GCEdge}... false otherwise.
//	 * @throws InvalidInputException 
//	 */
//	@Deprecated
//	public boolean isLeft(GCPoint p) throws InvalidInputException
//	{
//		return SphericalUtilities.isLeft(this.first.vector(), this.second.vector(), p.vector());
//	}
//
//	/**
//	 * Determines if p is left of OR on the Great Circle defined by {@link GCEdge}.
//	 * @param p
//	 * @return true if p is left of (or on) the Great Circle defined by {@link GCEdge}... false otherwise.
//	 * @throws InvalidInputException 
//	 */
//	@Deprecated
//	public boolean isLeftOrOn(GCPoint p) throws InvalidInputException
//	{
//		return SphericalUtilities.isLeftOrOn(this.first.vector(), this.second.vector(), p.vector());
//	}
//
//	/**
//	 * Determines if p is to the right of the Great Circle defined by {@link GCEdge}.
//	 * @param p
//	 * @return true if p is strictly left of the Great Circle defined by {@link GCEdge}... false otherwise.
//	 * @throws InvalidInputException 
//	 */
//	@Deprecated
//	public boolean isRight(GCPoint p) throws InvalidInputException
//	{
//		return SphericalUtilities.isRight(this.first.vector(), this.second.vector(), p.vector());
//	}
//
//	/**
//	 * Determines if p is to the right of (or on) the Great Circle defined by {@link GCEdge}.
//	 * @param p
//	 * @return true if p is right of (or on) of the Great Circle defined by {@link GCEdge}... false otherwise.
//	 * @throws InvalidInputException 
//	 */
//	@Deprecated
//	public boolean isRightOrOn(GCPoint p) throws InvalidInputException
//	{
//		return SphericalUtilities.isRightOrOn(this.first.vector(), this.second.vector(), p.vector());
//	}
//
//	/**
//	 * Determines if e is strictly to the left of the Great Circle defined by this {@link GCEdge}.
//	 * (That is if both of e's endpoints are strictly to the left of this {@link GCEdge}.
//	 * @throws InvalidInputException 
//	 */
//	@Deprecated
//	public boolean isLeft(GCEdge e) throws InvalidInputException
//	{
//		return SphericalUtilities.isLeft(this.first.vector(), this.second.vector(), e.first.vector()) &&
//		SphericalUtilities.isLeft(this.first.vector(), this.second.vector(), e.second.vector());
//	}
//	
//	/**
//	 * Determines if e is to the left of, or on, the Great Circle defined by this {@link GCEdge}.
//	 * (That is if both of e's endpoints are to the left of, or on, this {@link GCEdge}.
//	 * @throws InvalidInputException 
//	 */
//	@Deprecated
//	public boolean isLeftOrOn(GCEdge e) throws InvalidInputException
//	{
//		return SphericalUtilities.isLeftOrOn(this.first.vector(), this.second.vector(), e.first.vector()) &&
//		SphericalUtilities.isLeftOrOn(this.first.vector(), this.second.vector(), e.second.vector());
//	}
//	
//	/**
//	 * Determines if e is strictly to the right of the Great Circle defined by this {@link GCEdge}.
//	 * (That is if both of e's endpoints are strictly to the right of this {@link GCEdge}.
//	 * @throws InvalidInputException 
//	 */
//	@Deprecated
//	public boolean isRight(GCEdge e) throws InvalidInputException
//	{
//		return SphericalUtilities.isRight(this.first.vector(), this.second.vector(), e.first.vector()) &&
//		SphericalUtilities.isRight(this.first.vector(), this.second.vector(), e.second.vector());
//	}
//	
//	/**
//	 * Determines if e is to the right of, or on, the Great Circle defined by this {@link GCEdge}.
//	 * (That is if both of e's endpoints are to the right of, or on, this {@link GCEdge}.
//	 * @throws InvalidInputException 
//	 */
//	@Deprecated
//	public boolean isRightOrOn(GCEdge e) throws InvalidInputException
//	{
//		return SphericalUtilities.isRightOrOn(this.first.vector(), this.second.vector(), e.first.vector()) &&
//		SphericalUtilities.isRightOrOn(this.first.vector(), this.second.vector(), e.second.vector());
//	}
//
//	/**
//	 * Determines if p is left of OR on the Great Circle defined by {@link GCEdge}.
//	 * @param p
//	 * @return true if p is left of (or on) the Great Circle defined by {@link GCEdge}... false otherwise.
//	 * @throws InvalidInputException 
//	 */
//	@Deprecated
//	public boolean isLeftOrOn(SimplePolygon p) throws InvalidInputException
//	{
//		for (GCPoint point : p.points())
//		{
//			// Don't bother to check point if it is identical to either edge points (can cause mathematical anomalies in isLeft())
//			if (!this.first.equals(point) && !this.second.equals(point) && !this.isLeftOrOn(point))
//			{
//				return false;
//			}
//		}
//		
//		return true;
//	}
//
//	/**
//	 * Determines if p is to the right of (or on) the Great Circle defined by {@link GCEdge}.
//	 * @param p
//	 * @return true if p is right of (or on) of the Great Circle defined by {@link GCEdge}... false otherwise.
//	 * @throws InvalidInputException 
//	 */
//	@Deprecated
//	public boolean isRightOrOn(SimplePolygon p) throws InvalidInputException
//	{
//		for (GCPoint point : p.points())
//		{
//			// Don't bother to check point if it is identical to either edge points (can cause mathematical anomalies in isLeft())
//			if (!this.first.equals(point) && !this.second.equals(point) && !this.isRightOrOn(point))
//			{
//				return false;
//			}
//		}
//		
//		return true;
//	}
//
//	/**
//	 * Cross product: first X second
//	 */
//	@Deprecated
//	protected Vector3D crossProduct()
//	{
//		return this.first.vector().crossProduct(this.second.vector());
//	}
	
	/**
	 * Determines the intersection of two Great Circle segments: a1->a2 with b1->b2.
	 * @param a1
	 * @param a2
	 * @param b1
	 * @param b2
	 * @return The normalized Vector3D containing the intersection of a with b, if it exists. null otherwise. 
	 */
	public GCPoint intersection(GCEdge edge, IntersectionType intersectionType) throws InvalidInputException
	{
		GCPoint intersection = null;

		Vector3D vector = SphericalUtilities.intersection(this.first.vector(), this.second.vector(), edge.first.vector(), edge.second.vector(), intersectionType);
		
		if (vector != null)
		{
			intersection = new GCPoint(vector);
		}
		
		return intersection;
	}
	
	@Override
	public String toString()
	{
		return this.instanceId + ": " + toString(this.first(), this.second());
	}

    public static class InstanceIdComparator implements Comparator<GCEdge>
    {
        public int compare(GCEdge a, GCEdge b)
        {
            if (a.instanceId < b.instanceId) { return -1; }
            else if (a.instanceId > b.instanceId) { return 1; }
            return 0;
        }
    }
}