package gov.faa.ang.swac.common.geometry;

import gov.faa.ang.swac.common.datatypes.Vector3D;
import gov.faa.ang.swac.common.utilities.Mathematics;

/**
 * Utility functions for spherical geometry.
 * <p>
 * CONVENTIONS:<br>
 * - These algorithms assume the Earth to be a perfect sphere with a radius of {@link #EARTH_RADIUS_KM}.
 * - North Latitude is positive, South Latitude is negative<br>
 * - East Longitude is positive, West Longitude is negative<br>
 *   
 * @author James Bonn - FAA, AJP
 * @author Jason Femino - CSSI, Inc.
 */
public class SphericalUtilities
{
	/**
	 * Radius of the Earth in kilometers.
	 * <p>
	 * <b>NOTE:</b> This is a some-what weighted average. The equatorial radius (~6,378.135 km/3,963.189 mi) is actually about 21km greater than the Polar radius (~6,356.750 km/3,949.901 mi).
	 */
	final static double EARTH_RADIUS_KM = 6371.0;
	
	/**
	 * Multiply degrees by this value to convert to radians.
	 */
    public final static double DEGREES_TO_RADIANS = Math.PI/180;
	
	/**
	 * Multiply radians by this value to convert to degrees.
	 */
    public final static double RADIANS_TO_DEGREES = 180/Math.PI;
    
    /**
     * Multiply the angle between two points on the surface of the Earth (whose vertex is the center) to get a distance between those points (in nautical miles).
     */
	public final static double RADIANS_TO_NMI = 10800.0/Math.PI;


	public static class InvalidInputException extends Exception
	{
		private static final long serialVersionUID = -465040392412960885L;
		public InvalidInputException() { super(); }
		public InvalidInputException(String message) { super(message); }
	}
	
	/**
	 * Determines the great circle distance between p1 and p2 in nautical miles (assuming the Earth is spherical with a radius of {@link #EARTH_RADIUS_KM}).<br>
	 * @param p1
	 * @param p2
	 * @return distance in nautical miles
	 */
	@Deprecated
	public static double gcDistance(double lat1, double lon1, double lat2, double lon2)
	{
		return angle(lat1, lon1, lat2, lon2) * RADIANS_TO_NMI;
	}

	/**
	 * Returns a unit length {@link Vector3D} that corresponds to (latRad, lonRad).<br>
	 * Result will be normal (unit length).
	 * @param latRad latitude (in radians) 
	 * @param lonRad longitude (in radians)
	 */
    public static Vector3D latLonToVector(double latRad, double lonRad)
	{
    	final double cosLat = Math.cos(latRad);
    	
		final double x = cosLat * Math.cos(lonRad); 
		final double y = cosLat * Math.sin(lonRad);
		final double z = Math.sin(latRad);
		
		return new Vector3D(x, y, z);
	}
    
    /**
     * Given a {@link Vector3D} returns an array containing its latitude and longitude (in radians).<br>
     * <b>e.g.</b>If you call:<br>
     * &nbsp;&nbsp;&nbsp;<code>double[] latLon = vectorToLatLon(vector);</code><br>
     * Then latitude/longitude are retrieved by:<br>
     * &nbsp;&nbsp;&nbsp;<code>double lat = latLon[0];</code> (in radians)<br>
     * &nbsp;&nbsp;&nbsp;<code>double lon = latLon[1];</code> (in radians)
     */
    public static double[] vectorToLatLon(Vector3D vector)
    {
    	double[] latLon = new double[2];
    	Vector3D v = vector.getNormal();      // Need normal vector, use a copy (so we don't modify the original)
    	latLon[0] = Math.asin(v.z());         // latitude
    	latLon[1] = Math.atan2(v.y(), v.x()); // longitude
    	
    	return latLon;
    }

	/**
	 * Given vectors v1, v2, & v3...
	 * Determines if vector v3 is on the Great Circle defined by v1 -> v2.
	 * @param v1
	 * @param v2
	 * @param v3
	 * @return true if v3 is on the Great Circle defined by v1->v2... false otherwise.
	 * @throws InvalidInputException if {@link Vector3D}s v1 & v2 are identical or antipodal.
	 */
    @Deprecated
	public static boolean isOn(Vector3D v1, Vector3D v2, Vector3D v3) throws InvalidInputException
	{
		// If v1 & v2 are identical, then they cannot determine a great circle
		// if (v1.equals(v2) || Mathematics.equals(Vector3D.angle(v1, v2), Math.PI))
		if (v1.equals(v2))
		{
			throw new InvalidInputException("isLeft(): Vectors v1 & v2 cannot be the same or antipodal!");
		}
		
		double angle = gcInteriorAngle(v1, v2, v3);
		return Mathematics.equals(angle, 0) || Mathematics.equals(angle, Math.PI);
	}
	
	/**
	 * Given vectors v1, v2, & v3...
	 * Determines if vector v3 is to the left of the Great Circle defined by v1 -> v2.
	 * @param v1
	 * @param v2
	 * @param v3
	 * @return true if v3 is strictly left of the Great Circle defined by v1->v2... false otherwise.
	 * @throws InvalidInputException if {@link Vector3D}s v1 & v2 are identical or antipodal.
	 */
	public static boolean isLeft(Vector3D v1, Vector3D v2, Vector3D v3) throws InvalidInputException
	{
		// If v1 & v2 are identical, then they cannot determine a great circle
		// if (v1.equals(v2) || Mathematics.equals(Vector3D.angle(v1, v2), Math.PI))
		if (v1.equals(v2))
		{
			throw new InvalidInputException("isLeft(): Vectors v1 (" + v1 + ") & v2 (" + v2 +") cannot be the same or antipodal!");
		}
		
		// First, get the vector normal to v1->v2
		Vector3D vn = v1.crossProduct(v2);
		
		// Then, calculate dot-product of vn with v3 and check sign
		double dotProduct = vn.dotProduct(v3);
		if (!Mathematics.equals(dotProduct, 0.0, Mathematics.zeroTolerance()) && dotProduct > 0)
		{
			return true;
		}
		return false;
	}

	/**
	 * Given vectors v1, v2, & v3...
	 * Determines if vector v3 is left of OR on the Great Circle defined by v1 -> v2.
	 * @param v1
	 * @param v2
	 * @param v3
	 * @return true if v3 is left of (or on) the Great Circle defined by v1->v2... false otherwise.
	 * @throws InvalidInputException if {@link Vector3D}s v1 & v2 are identical or antipodal.
	 */
	public static boolean isLeftOrOn(Vector3D v1, Vector3D v2, Vector3D v3) throws InvalidInputException
	{
		// If v1 & v2 are identical, then they cannot determine a great circle
		// if (v1.equals(v2) || Mathematics.equals(Vector3D.angle(v1, v2), Math.PI))
		if (v1.equals(v2))
		{
			throw new InvalidInputException("isLeftOrOn(): Vectors v1 & v2 cannot be the same or antipodal!");
		}
		
		// First, get the vector normal to v1->v2
		Vector3D vn = v1.crossProduct(v2);
		
		// Then, calculate dot-product of vn with v3 and check sign
		double dotProduct = vn.dotProduct(v3); 
		if (Mathematics.equals(dotProduct, 0, Mathematics.zeroTolerance()) || dotProduct > 0)
		{
			return true;
		}
		return false;
	}

	/**
	 * Given vectors v1, v2, & v3...
	 * Determines if vector v3 is to the right of the Great Circle defined by v1 -> v2.
	 * @param v1
	 * @param v2
	 * @param v3
	 * @return true if v3 is strictly left of the Great Circle defined by v1->v2... false otherwise.
	 * @throws InvalidInputException if {@link Vector3D}s v1 & v2 are identical or antipodal.
	 */
	public static boolean isRight(Vector3D v1, Vector3D v2, Vector3D v3) throws InvalidInputException
	{
		// If v1 & v2 are identical, then they cannot determine a great circle
		// if (v1.equals(v2) || Mathematics.equals(Vector3D.angle(v1, v2), Math.PI))
		if (v1.equals(v2))
		{
			throw new InvalidInputException("isRight(): Vectors v1 & v2 cannot be the same or antipodal!");
		}
		
		// First, get the vector normal to v1->v2
		Vector3D vn = v1.crossProduct(v2).normalize();
		
		// Then, calculate dot-product of vn with v3 and check sign
		double dotProduct = vn.dotProduct(v3);
		if (!Mathematics.equals(dotProduct, 0, Mathematics.zeroTolerance()) && dotProduct < 0)
		{
			return true;
		}
		return false;
	}

	/**
	 * Given vectors v1, v2, & v3...
	 * Determines if vector v3 is to the right of (or on) the Great Circle defined by v1 -> v2.
	 * @param v1
	 * @param v2
	 * @param v3
	 * @return true if v3 is right of (or on) of the Great Circle defined by v1->v2... false otherwise.
	 */
	@Deprecated
	public static boolean isRightOrOn(Vector3D v1, Vector3D v2, Vector3D v3) throws InvalidInputException
	{
		return !isLeft(v1, v2, v3);
	}
	
	/**
	 * Returns true if the vector z is "between" vectors u and v
	 * ("between" is determined via a cone that is spanned by
	 * u and v centered at the bisector of u and v with vertex 
	 * at the center of the unit sphere).
	 * @param z input vector
	 * @param u first input vector determining the cone
	 * @param v second input vector determining the cone
	 * @return true if z is within the above mentioned cone
	 */
	public static boolean isBetween (Vector3D z, Vector3D u, Vector3D v)
	{
		return isBetween(z,u,v,true);
	}
	
	/**
	 * Returns true if the vector z is "between" vectors u and v
	 * ("between" is determined via a cone that is spanned by
	 * u and v centered at the bisector of u and v with vertex 
	 * at the center of the unit sphere).
	 * ASSUMPTION: input vectors are unit length.
	 * @param z input vector
	 * @param u first input vector determining the cone
	 * @param v second input vector determining the cone
	 * @param normalize set this to true if u, v, and z are not known to be unit vectors
	 * @return true if z is within the above mentioned cone
	 */
	public static boolean isBetween (Vector3D z, Vector3D u, Vector3D v, boolean normalize)
	{
		boolean retval = false;
				
		Vector3D z1 = z;
		Vector3D u1 = u;
		Vector3D v1 = v;
		
		if (normalize)
		{
			z1 = z.getNormal();
			u1 = u.getNormal();
			v1 = v.getNormal();
		}		
		
		Vector3D m = u1.plus(v1).getNormal();
		double val1 = m.dotProduct(z1);
		double val2 = m.dotProduct(u1);
		
		if (val1 > val2) 
		{
			retval = true;
		}
		return retval;
	}
	
	public enum Sidedness { LEFT, ON, RIGHT }
	public static Sidedness side(Vector3D v1, Vector3D v2, Vector3D v3) throws InvalidInputException
	{
		if (isLeft(v1, v2, v3))
		{
			return Sidedness.LEFT;
		}
		else if (isRight(v1, v2, v3))
		{
			return Sidedness.RIGHT;
		}
		
		return Sidedness.ON;
	}
	
	@Deprecated
	public static double determinant(
			double x1, double y1, double z1,
			double x2, double y2, double z2,
			double x3, double y3, double z3)
	{
		return x1*y2*z3 + y1*z2*x3 + z1*x2*y3 - x3*y2*z1 - y3*z2*x1 - z3*x2*y1;
	}
	
	@Deprecated
	public static double determinant(Vector3D v1, Vector3D v2, Vector3D v3)
	{
		return determinant(v1.x(),v1.y(),v1.z(),v2.x(),v2.y(),v2.z(),v3.x(),v3.y(),v3.z());
	}
	
	public enum IntersectionType { STRICT, NONSTRICT }
	/**
	 * Determines the intersection of two Great Circle segments: a1->a2 with b1->b2.
	 * <p>
	 * This method can detect and return any of the following types of intersection:<br>
	 * - X-intersections: Two segments that intersect between their endpoints<br>
	 * - T-intersections: One segment has an endpoint in the middle of the other segment<br>
	 * - L-intersections: Both segments share an endpoint
	 * - I-intersections: Both segments are in-line AND share one (and only one) endpoint
	 * <p>
	 * <b>If <code>strict</code> is <code>true</code>, then only X-intersections are returned.</b>
	 * @param a1
	 * @param a2
	 * @param b1
	 * @param b2
	 * @return The normalized {@link Vector3D} that is the intersection of a with b, if it exists... <code>null</code> otherwise (or if a and b are colinear, but don't share an endpoint). 
	 */
	public static Vector3D intersection(Vector3D aa1, Vector3D aa2, Vector3D bb1, Vector3D bb2, IntersectionType intersectionType) throws InvalidInputException
	{
		// Use unit vectors throughout
		Vector3D a1 = aa1.getNormal();
		Vector3D a2 = aa2.getNormal();
		Vector3D b1 = bb1.getNormal();
		Vector3D b2 = bb2.getNormal();
		
		Vector3D retVal = null;		
		
		Sidedness a1ToB = side(b1, b2, a1);
		Sidedness a2ToB = side(b1, b2, a2);
		Sidedness b1ToA = side(a1, a2, b1);
		Sidedness b2ToA = side(a1, a2, b2);
		
		// I, L, T, or no intersection
		if (a1ToB == Sidedness.ON || a2ToB == Sidedness.ON || b1ToA == Sidedness.ON || b2ToA == Sidedness.ON)
		{
			// These intersection types are not allowed in strict mode
			if (intersectionType == IntersectionType.STRICT)
			{
				return null;
			}
			
			// I or no intersection (colinear)
			if ( b1ToA == Sidedness.ON && b2ToA == Sidedness.ON )
			{
				Vector3D sharedEnd;
				Vector3D aEnd;
				Vector3D bEnd;
				
				// Check that segments only share end-point a1/b1 AND that a2 & b2 are not on the same side of a1/b1 
				if (Vector3D.equals(a1, b1) && !Vector3D.equals(a2, b2))
				{
					sharedEnd = a1; aEnd = a2; bEnd = b2;
				}
				else if (Vector3D.equals(a1, b2) && !Vector3D.equals(a2, b1))
				{
					sharedEnd = a1; aEnd = a2; bEnd = b1;
				}
				else if (Vector3D.equals(a2, b1) && !Vector3D.equals(a1, b2))
				{
					sharedEnd = a2; aEnd = a1; bEnd = b2;
				}
				else if (Vector3D.equals(a2, b2) && !Vector3D.equals(a1, b1))
				{
					sharedEnd = a2; aEnd = a1; bEnd = b1;					
				}
				else
				{
					return null;
				}
				
				Vector3D perpendicular = rotate(aEnd, sharedEnd, Math.PI/2.0);
				if ( side(sharedEnd, perpendicular, aEnd) != side(sharedEnd, perpendicular, bEnd) )
				{
					retVal = sharedEnd;
				}
				else
				{
					return null;
				}
			}
			// L intersection at a1
			else if (Vector3D.equals(a1, b1) || Vector3D.equals(a1, b2))
			{
				retVal = a1;
			}
			// L intersection at a2
			else if (Vector3D.equals(a2, b1) || Vector3D.equals(a2, b2))
			{
				retVal = a2;
			}
			// T intersection at a1
			else if (a1ToB == Sidedness.ON && isBetween(a1, b1, b2, false))
			{
				retVal = a1;
			}
			// T intersection at a2
			else if (a2ToB == Sidedness.ON && isBetween(a2, b1, b2, false))
			{
				retVal = a2;
			}
			// T intersection at b1
			else if (b1ToA == Sidedness.ON && isBetween(b1, a1, a2, false))
			{
				retVal = b1;
			}
			// T intersection at b2
			else if (b2ToA == Sidedness.ON && isBetween(b2, a1, a2, false))
			{
				retVal = b2;
			}
			// No intersection
			else
			{
				return null;		
			}			
		}
		// No intersection
		else if (a1ToB == a2ToB || b1ToA == b2ToA)
		{
			return null;
		}
		// X or no intersection (antipodal)
		else
		{
			// Get the vectors of the intersections of the Great Circles: z1 = (a1Xa2) X (b1Xb2), z2 = -z1
			// Normalize each result to avoid roundoff error at small angles
			Vector3D x = a1.crossProduct(a2).normalize();
			Vector3D y = b1.crossProduct(b2).normalize();
			Vector3D intersection1 = x.crossProduct(y).normalize();
			Vector3D intersection2 = intersection1.getComplement();
			
			// X intersection at intersection1
			if (isBetween(intersection1, a1, a2, false) && isBetween(intersection1, b1, b2, false))
			{
				retVal = intersection1;
			}
			// X intersection at intersection2
			else if (isBetween(intersection2, a1, a2, false) && isBetween(intersection2, b1, b2, false))
			{
				retVal = intersection2;
			}
			// No intersection (antipodal)
			else
			{
				return null;
			}
		}
		
		return retVal == null ? null : retVal.getNormal();
	}
	
	/*  Replaced with new version
	
	public static Vector3D intersection(Vector3D a1, Vector3D a2, Vector3D b1, Vector3D b2, IntersectionType intersectionType) throws InvalidInputException
	{
		// If segment a is entirely left/on/right of b, then there cannot be an intersection (except an I-intersection)
		Sidedness a1ToB = side(b1, b2, a1);
		Sidedness a2ToB = side(b1, b2, a2);
		if (a1ToB == a2ToB && intersectionType == IntersectionType.STRICT)
		{
			return null;
		}
		
		// If segment b is entirely left/on/right of a, then there cannot be an intersection (except an I-intersection)
		Sidedness b1ToA = side(a1, a2, b1);
		Sidedness b2ToA = side(a1, a2, b2);
		if (b1ToA == b2ToA && intersectionType == IntersectionType.STRICT)
		{
			return null;
		}
	
		String typeOfIntersection = "";
		
		// I-intersection
		if ( b1ToA == Sidedness.ON && b2ToA == Sidedness.ON )
		{
			typeOfIntersection = "I";
			Vector3D sharedEnd;
			Vector3D aEnd;
			Vector3D bEnd;
			
			// Check that segments only share end-point a1/b1 AND that a2 & b2 are not on the same side of a1/b1 
			if (Vector3D.equals(a1, b1) && !Vector3D.equals(a2, b2))
			{
				sharedEnd = a1; aEnd = a2; bEnd = b2;
			}
			else if (Vector3D.equals(a1, b2) && !Vector3D.equals(a2, b1))
			{
				sharedEnd = a1; aEnd = a2; bEnd = b1;
			}
			else if (Vector3D.equals(a2, b1) && !Vector3D.equals(a1, b2))
			{
				sharedEnd = a2; aEnd = a1; bEnd = b2;
			}
			else if (Vector3D.equals(a2, b2) && !Vector3D.equals(a1, b1))
			{
				sharedEnd = a2; aEnd = a1; bEnd = b1;					
			}
			else
			{
				return null;
			}
			
			Vector3D perpendicular = rotate(aEnd, sharedEnd, Math.PI/2.0);
			if ( side(sharedEnd, perpendicular, aEnd) != side(sharedEnd, perpendicular, bEnd) )
			{
				return sharedEnd;
			}

			return null;
		}
		else if ( (b1ToA == Sidedness.ON || b2ToA == Sidedness.ON) && (a1ToB == Sidedness.ON || a2ToB == Sidedness.ON) )
		{
			typeOfIntersection = "L";
		}
		else if ( ((b1ToA == Sidedness.LEFT && b2ToA == Sidedness.RIGHT) || (b1ToA == Sidedness.RIGHT && b2ToA == Sidedness.LEFT))
			   && ((a1ToB == Sidedness.LEFT && a2ToB == Sidedness.RIGHT) || (a1ToB == Sidedness.RIGHT && a2ToB == Sidedness.LEFT)))
		{
			typeOfIntersection = "X";
		}
		else
		{
			typeOfIntersection = "T";
		}

		if (intersectionType == IntersectionType.STRICT && typeOfIntersection != "X")
		{
			return null;
		}

		// Now we know that we may have some sort of intersection...
		// but we still don't know if the segments are on the same side of the sphere (intersection) or the opposite (no intersection).
		
		// Get the vector of the intersection of the Great Circles: z = (a1Xa2) X (b1Xb2)  
		Vector3D x = a1.crossProduct(a2);
		Vector3D y = b1.crossProduct(b2);
		Vector3D intersection = x.crossProduct(y);
		
		// Get the dot-product of z with all four points...
		// If the signs are all positive... then we have the intersection
		// If the signs are all negative... then we have the complement of the intersection
		// If the signs are not all the same... then there is no intersection
		
		// TODO: This check fails if point of "L" intersection makes perpendiculars to the other endpoints
		boolean sign = Mathematics.greaterThan(intersection.dotProduct(a1), 0);
		if ( Mathematics.greaterThan(intersection.dotProduct(a2), 0) == sign &&
				Mathematics.greaterThan(intersection.dotProduct(b1), 0) == sign &&
				Mathematics.greaterThan(intersection.dotProduct(b2), 0) == sign )
		{
			if (sign)
			{
				intersection.normalize();
			}
			else
			{
				intersection.complement();
				intersection.normalize();
			}
		}
		else
		{
			intersection = null;
		}

		return intersection;
	}
	
	*/
	
	/**
	 * Rotates a 3D vector "v" counter-clockwise around the x-axis by an angle of "a" radians...
	 * 
	 * | 1    0       0    |     | x |     | x'|  (x' == x)
	 * | 0  cos(a) -sin(a) |  x  | y |  =  | y'|
	 * | 0  sin(a)  cos(a) |     | z |     | z'|
	 * 
	 * @param v
	 * @param a
	 * @return Vector3D equivalent to "v" rotated "a" radians about x-axis 
	 */
	@Deprecated
	public static Vector3D rotateX(Vector3D v, double a)
	{
		double y = (Math.cos(a) * v.y()) + (-Math.sin(a) * v.z());
		double z = (Math.sin(a) * v.y()) + ( Math.cos(a) * v.z());
		Vector3D v2 = new Vector3D(v.x(), y, z);
		v2.normalize();
		return v2;
	}
	
	/**
	 * Rotates a 3D vector "v" counter-clockwise around the y-axis by an angle of "a" radians...
	 * 
	 * |  cos(a)  0   sin(a) |     | x |     | x'|
	 * |    0     1    0     |  x  | y |  =  | y'|  (y' == y)
	 * | -sin(a)  0   cos(a) |     | z |     | z'|
	 * 
	 * @param v
	 * @param a
	 * @return Vector3D equivalent to "v" rotated "a" radians about y-axis 
	 */
	@Deprecated
	public static Vector3D rotateY(Vector3D v, double a)
	{
		double x = (Math.cos(a) * v.x()) + (Math.sin(a) * v.z());
		double z = (Math.sin(a) * v.x()) + (Math.cos(a) * v.z());
		Vector3D v2 = new Vector3D(x, v.y(), z);
		v2.normalize();
		return v2;
	}
	
	/**
	 * Rotates a 3D vector "v" counter-clockwise around the z-axis by an angle of "a" radians...
	 * 
	 * | cos(a) -sin(a)  0 |     | x |     | x'|
	 * | sin(a)  cos(a)  0 |  x  | y |  =  | y'|
	 * |  0       0      1 |     | z |     | z'|  (z' == z)
	 * 
	 * @param v
	 * @param a
	 * @return Vector3D equivalent to "v" rotated "a" radians about z-axis 
	 */
	@Deprecated
	public static Vector3D rotateZ(Vector3D v, double a)
	{
		double x = (Math.cos(a) * v.x()) + (-Math.sin(a) * v.y());
		double y = (Math.sin(a) * v.x()) + ( Math.cos(a) * v.y());
		Vector3D v2 = new Vector3D(x, y, v.z());
		v2.normalize();
		return v2;
	}
	
	/**
	 * Calculates the rotation of the point (x,y,z) about the line through (a,b,c) parallel to unit-vector <u,v,w> by the angle "phi" radians.
	 * @param x1   x-component of point to rotate
	 * @param y1   y-component of point to rotate
	 * @param z1   z-component of point to rotate
	 * @param x2   x-component of point through which the line of rotation passes
	 * @param y2   y-component of point through which the line of rotation passes
	 * @param z2   z-component of point through which the line of rotation passes
	 * @param x3   x-component of vector parallel to line of rotation
	 * @param y3   y-component of vector parallel to line of rotation
	 * @param z3   z-component of vector parallel to line of rotation
	 * @param phi   angle of rotation in radians
	 * @return Vector3D that represents (x,y,z) rotated about line through (a,b,c) parallel to <u,v,w> by an angle of "phi" radians
	 */
	public static Vector3D rotate(double x1, double y1, double z1, double x2, double y2, double z2, double x3, double y3, double z3, double phi)
	{
		double x32 = x3 * x3;
		double y32 = y3 * y3;
		double z32 = z3 * z3;
		double squares = x32 + y32 + z32; 
 		double x = ( x2*(y32+ z32) + x3*(-y2*y3-z2*z3+x3*x1+y3*y1+z3*z1) + ((x1-x2)*(y32+z32) + x3*(y2*y3+z2*z3-y3*y1-z3*z1)) * Math.cos(phi)+Math.sqrt(squares)*( y2*z3-z2*y3-z3*y1+y3*z1)*Math.sin(phi) ) / squares;
 		double y = ( y2*(x32+z32) + y3*(-x2*x3-z2*z3+x3*x1+y3*y1+z3*z1) + ((y1-y2)*(x32+z32) + y3*(x2*x3+z2*z3-x3*x1-z3*z1)) * Math.cos(phi)+Math.sqrt(squares)*(-x2*z3+z2*x3+z3*x1-x3*z1)*Math.sin(phi) ) / squares;
 		double z = ( z2*(x32+y32) + z3*(-x2*x3-y2*y3+x3*x1+y3*y1+z3*z1) + ((z1-z2)*(x32+y32) + z3*(x2*x3+y2*y3-x3*x1-y3*y1)) * Math.cos(phi)+Math.sqrt(squares)*( x2*y3-y2*x3-y3*x1+x3*y1)*Math.sin(phi) ) / squares;
 		return new Vector3D(x, y, z);
	}

	/**
	 * Rotates a 3D vector "v" counter-clockwise around a unit-vector "u" by an angle of "a" radians...
	 *
     * NOTE: If the vector "u" is not normalized to unit-length... the behavior of this method is undefined.
	 * @param v  Vector to be rotated
	 * @param u  Unit-vector around which to rotate
	 * @param a  Angle of rotation (in radians)
	 * @return Vector3D equivalent to "v" rotated "a" radians about vector u 
	 */
	public static Vector3D rotate(Vector3D v, Vector3D u, double a)
	{
		return rotate(v.x(), v.y(), v.z(), 0, 0, 0, u.x(), u.y(), u.z(), a);
	}

	/**
	 * Returns the surface angle between two Great Circles that intersect a given {@link Vector3D}.<br>
	 * This method calculates the angle between vectors v2->v1 & v2->v3.
 	 * 
	 * @param v1 vector determining the great circle v2->v1
	 * @param v2 vector representing the intersection of the great circles v2->v1 & v2->v3
	 * @param v3 vector determining the great circle arc v2->v3
	 * @return angle between the v2->v1 & v2->v3 in radians [0, PI)
	 */
	public static double gcAngle(Vector3D v1, Vector3D v2, Vector3D v3) throws InvalidInputException
	{
		// If v1 & v2 are identical, then they cannot determine a great circle
		if (v2.equals(v1))
		{
			throw new InvalidInputException("gcAngle(): Vectors v1 & v2 cannot be the same!");
		}

		// If v2 & v3 are identical, then they cannot determine a great circle
		if (v2.equals(v3))
		{
			throw new InvalidInputException("gcAngle(): Vectors v2 & v3 cannot be the same!");
		}

		Vector3D m = v2.crossProduct(v1).getNormal();
		Vector3D n = v2.crossProduct(v3).getNormal();
		double dot = m.dotProduct(n);
		
		return Math.acos(dot); // in radians
	}
	
	/**
	 * Returns the surface angle between two Great Circles that intersect at vertex v3.<br>
	 * This method calculates the angle between vectors v1->v3 & v3->v2...
	 * but unlike simply using the arc-cosine function, this method correctly returns angles greater than PI radians (180 degrees).
	 * 
	 * @param v1 vector determining the great circle arc v1->v3
	 * @param v2 vector representing the vertex of the angle
	 * @param v3 vector determining the great circle arc v3->v2
	 * @return angle between the v1->v3 and v3->v2 arcs in radians [0, 2*PI)
	 */
	@Deprecated
	public static double gcInteriorAngle(Vector3D v1, Vector3D v2, Vector3D v3) throws InvalidInputException
	{
		double angle = gcAngle(v1, v2, v3); // in radians
		if (isRight(v1, v2, v3))
		{
			angle = 2*Math.PI - angle;
		}
		return angle;
	}

	/**
	 * Normal projection of the vector z onto the great circle determined by u and v.
	 * The projection line is a great circle, too.
	 * @param z input vector that needs to be projected
	 * @param u first input vector determining the great circle line
	 * @param v second input vector determining the great circle line
	 * @return the resulting projection vector of unit length
	 */
	public static Vector3D normalProjection (Vector3D z, Vector3D u, Vector3D v)
	{
		Vector3D n = u.crossProduct(v).getNormal();
		Vector3D retval = z.minus(n.times(z.dotProduct(n)));
		return retval.getNormal();
	}
	
	/**
	 * Determines the angle between (lat1, lon1) and (lat2, lon2), through the center of the sphere (in radians).<br>
	 * @param lat1 (in radians)
	 * @param lon1 (in radians)
	 * @param lat2 (in radians)
	 * @param lon2 (in radians)
	 * @return angle between (lat1, lon1) and (lat2, lon2) (in radians)
	 */
	public static double angle(double lat1, double lon1, double lat2, double lon2)
	{
		// NOTE: We used to use the following formula, but it not well behaved for extremely small angles (and some situations in which it returns NaN)
		// return Math.acos(Math.sin(lat1) * Math.sin(lat2) + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon2-lon1));
		
//		return 2 * Math.asin(Math.sqrt( Math.pow(Math.sin((lat1-lat2)/2),2) + Math.cos(lat1) * Math.cos(lat2) * Math.pow(Math.sin((lon1-lon2)/2),2)) );
            
            // NOTE: better to multiply than call Math.pow() where possible. Also better to compute a value one time.
            
            double sinHalfLatDif = Math.sin((lat1-lat2)/2);
            double sinHalfLonDif = Math.sin((lon1-lon2)/2);
            
            return 2 * Math.asin(Math.sqrt(sinHalfLatDif * sinHalfLatDif + Math.cos(lat1) * Math.cos(lat2) * sinHalfLonDif * sinHalfLonDif));
	}

    /**
     * Determines the true course of the great circle trajectory from point (latRad1, lonRad1) to point (latRad2, lonRad2).
     * @param latRad1 latitude of the first point (in radians)
     * @param lonRad1 longitude of the first point (in radians)
     * @param latRad2 latitude of the second point (in radians)
     * @param lonRad2 longitude of the second point (in radians)
     * @return true course from the first point to the second point, following the great circle trajectory (in radians)
     */
    public static double trueCourse(double latRad1, double lonRad1, double latRad2, double lonRad2)
    {
        double trueCourse = 0;
        
        // starting at a pole
        if (Math.cos(latRad1) < .000000001) 
        {
            if (latRad1 > 0) // starting at north pole
            {
                trueCourse = Math.PI;
            }
            else // starting at south pole
            {
                trueCourse = 2*Math.PI;
            }
        }
        else // not a pole
        {
            double angularDistance = angle(latRad1, lonRad1, latRad2, lonRad2);
            if(angularDistance == 0)
            {
                trueCourse = 0;
            }
            else
            {
                double temp = (Math.sin(latRad2) - Math.sin(latRad1)*Math.cos(angularDistance)) / (Math.sin(angularDistance)*Math.cos(latRad1));
                
                if (temp > 1)
                {
                    temp = 1;
                }
                else if (temp < -1)
                {
                    temp = -1;
                }
                
                trueCourse = Math.acos(temp);
                // inequality changed below to represent West < 0.
                if (Math.sin(lonRad2-lonRad1) <= 0)
                {
                    trueCourse = 2*Math.PI - trueCourse;
                }
            }
        }
        
        return trueCourse;
    }
    
    /**
     * Determines the signed distance (latRad3, lonRad3) from the great circle defined by (latRad1, lonRad2) -> (latRad2, lonRad2) on a unit sphere.
     * <p>
     * In other words, this method returns the angle between the point (latRad3, lonRad3) and the projection of that point onto the great circle (latRad1, lonRad1) -> (latRad2, lonRad2). 
     * 
     * @param latRad1 latitude of starting point on the great circle (in radians)
     * @param lonRad1 longitude of starting point on the great circle (in radians)
     * @param latRad2 latitude of ending point on the great circle (in radians)
     * @param lonRad2 longitude of ending point on the great circle (in radians)
     * @param latRad3 latitude of the point off of the great circle (in radians)
     * @param lonRad3 longitude of the point off of the great circle (in radians)
     * @return Distance from point (latRad3, lonRad3) to the great circle (latRad1, lonRad1) -> (latRad2, lonRad2) (in radians)<br>
     * If return value is < 0, (latRad3, lonRad3) is left of the great circle<br>
     * If return value is > 0, (latRad3, lonRad3) is right of the great circle.
     */
    @Deprecated
	public static double crossTrackError(double latRad1, double lonRad1, double latRad2, double lonRad2, double latRad3, double lonRad3) 
    {
        double dist13 = angle(latRad1, lonRad1, latRad3, lonRad3);
        double tc12 = trueCourse(latRad1, lonRad1, latRad2, lonRad2);
        double tc13 = trueCourse(latRad1, lonRad1, latRad3, lonRad3);
        
        double temp = Math.sin(dist13)*Math.sin(tc13-tc12);
        temp = Math.min(Math.max(temp,-1),1);
        double xte = Math.asin(temp);
        
        return xte;
    }
    
    /**
     * Determines the distance from (latRad1, lonRad1) to the projection of (latRad3, lonRad3) to the great circle defined by (latRad1, lonRad1) -> (latRad2, lonRad2).
     * 
     * @param latRad1 latitude of starting point on the great circle (in radians)
     * @param lonRad1 longitude of starting point on the great circle (in radians)
     * @param latRad2 latitude of ending point on the great circle (in radians)
     * @param lonRad2 longitude of ending point on the great circle (in radians)
     * @param latRad3 latitude of point off of the great circle (in radians)
     * @param lonRad3 longitude of point off of the great circle (in radians)
     * @return Distance from (lat1, lon1) to the projection of (lat3, lon3) to the great circle (lat1, lon1) -> (lat2, lon2) (in radians)
     */
    @Deprecated
	public static double alongTrackDistance(double latRad1, double lonRad1, double latRad2, double lonRad2, double latRad3, double lonRad3)
    {
        double dist13 = angle(latRad1, lonRad1, latRad3, lonRad3);
        double xte = crossTrackError(latRad1, lonRad1, latRad2, lonRad2, latRad3, lonRad3);
        
        double temp = Math.sin(dist13)*Math.sin(dist13) - Math.sin(xte)*Math.sin(xte);
        temp = Math.max(temp,0);
        temp = temp/Math.cos(xte);
        temp = Math.min(Math.max(temp,-1),1);
        
        double atd = Math.asin(temp);
        return atd;
    }
}