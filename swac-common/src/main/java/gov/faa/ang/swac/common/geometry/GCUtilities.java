/**
 * Copyright "TBD", Metron Aviation & CSSI.  All rights reserved.
 * 
 * This computer Software was developed with the sponsorship of the U.S. Government
 * under Contract No. DTFAWA-10-D-00033, which has a copyright license in accordance with AMS 3.5-13.(c)(1).
 */

package gov.faa.ang.swac.common.geometry;

import gov.faa.ang.swac.common.datatypes.Angle;
import gov.faa.ang.swac.common.datatypes.Latitude;
import gov.faa.ang.swac.common.datatypes.Longitude;
import gov.faa.ang.swac.common.datatypes.Vector3D;
import gov.faa.ang.swac.common.geometry.SphericalUtilities.InvalidInputException;
import gov.faa.ang.swac.common.utilities.Mathematics;

/**
 * This class of utility functions is meant to make Great Circle (GC) calculations very simple.
 * 
 * Many of the calculations are coded versions of equations found on the <I>Aviation Formulary</I> web page maintained by Ed Williams:
 * <a href="http://williams.best.vwh.net/avform.htm">http://williams.best.vwh.net/avform.htm</a>
 * <p>
 * <b>Conventions:</b><br>
 * - These algorithms assume the Earth to be a perfect sphere. See {@link #EARTH_RADIUS_KM}. <br>
 * - East Longitude is positive, West Longitude is negative<br>
 * - Polygons on the surface of a sphere have their points defined in a counter-clockwise direction.<br>
 *   
 * @author James Bonn - FAA, AJP
 * @author Jason Femino - CSSI, Inc.
 */
public final class GCUtilities
{
	/**
	 * The radius of a simplified, spherical Earth (in kilometers). Attributed to a Fédération Aéronautique Internationale standard.
	 * <p>
	 * <b>NOTE: </b>This value lies between the generally accepted polar and equatorial radii of the Earth:<br>
	 * Polar radius ~= 6,356.750 km (3,949.901 mi)<br>
	 * Equatorial radius ~= 6,378.135 km (3,963.189 mi)
	 */
	public final double EARTH_RADIUS_KM = 6371.0;
	public final double DEGREES_TO_RADIANS = Math.PI/180.0;
	public final double RADIANS_TO_DEGREES = 180.0/Math.PI;

	double NUMERICAL_TOLERANCE = 0.000000001;
	double ZERO_TOLERANCE = 0.00000000001;

	final static Vector3D zeroVector = new Vector3D(0, 0, 0);

	/**
	 * Determines the angle between p1 and p2 along the surface a unit sphere (in radians).<br>
	 * <b>i.e.</b> The angle <code>p1->sphere center->p2</code>
	 * @param p1
	 * @param p2
	 * @return angle between p1 and p2 along the surface of a unit sphere (in unitless radians)
	 */
	public static double angle(GCPoint p1, GCPoint p2)
	{
		return SphericalUtilities.angle(p1.latitude.radians(), p1.longitude.radians(), p2.latitude.radians(), p2.longitude.radians());
	}

	/**
	 * Determines the great circle distance between p1 and p2 in nautical miles (assuming the Earth is spherical with a radius of {@link #EARTH_RADIUS_KM}).<br>
	 * @param p1
	 * @param p2
	 * @return distance in nautical miles
	 */
	public static double gcDistance(GCPoint p1, GCPoint p2)
	{
		return angle(p1, p2) * SphericalUtilities.RADIANS_TO_NMI;
	}
	
	/**
	 * For cases in which a Euclidean approximation is satisfactory, euclideanDistance is a less computationally intensive method
	 * to calculate distance
	 * @param p1
	 * @param p2
	 * @return distance in nautical miles
	 */
	public static double euclideanDistance(GCPoint point1, GCPoint point2)
	{
		return euclideanDistance(point1.vector(),point2.vector());
	}

	public static double euclideanDistance(Vector3D p1, Vector3D p2)
	{
		double deltaX = p1.x() - p2.x();
		double deltaY = p1.y() - p2.y();
		double deltaZ = p1.z() - p2.z();
		
		return Math.sqrt((deltaX*deltaX)+(deltaY*deltaY)+(deltaZ*deltaZ))*SphericalUtilities.RADIANS_TO_NMI;
	}	
	
	// No need to recalculate this every time
	private static final double TWO_PI = 2 * Math.PI;
	
	/**
	 * Maps angles to the [0,2pi) interval
	 * @param radians
	 * @return
	 */
	@Deprecated
	private static double normalizeAngle(double radians) {
		while (radians >= TWO_PI)
		{
			radians -= TWO_PI;
		}
		while (radians < 0)
		{
			radians += TWO_PI;
		}
		return radians;
	}

    /**
     * Finds the {@link GCPoint} that is a certain distance away from a given point at a certain initial heading.
     * 
     * @param p starting point
     * @param distance distance to travel (in radians)
     * @param heading initial true course heading
     */
    public static GCPoint findPoint(GCPoint p, double distance, Angle heading)
    {
        double temp = Math.sin(p.latitude().radians()) * Math.cos(distance) + 
                      Math.cos(p.latitude().radians()) * Math.sin(distance) * Math.cos(heading.radians());
        
        if (temp > 1)
        {
            temp = 1;
        }
        else if (temp < -1)
        {
            temp = -1;
        }
        
        double lat = Math.asin(temp);
        
        double temp1 = Math.sin(heading.radians()) * Math.sin(distance) * Math.cos(p.latitude().radians());
        double temp2 = Math.cos(distance) - Math.sin(p.latitude().radians()) * Math.sin(lat);
        
        double tlon = Math.atan2(temp1, temp2);
        
        // changed from "- tlon" to account for West < 0
        double lon = Mathematics.mod(p.longitude().radians() + tlon + Math.PI, 2*Math.PI) - Math.PI;
        
        return new GCPoint( Latitude.valueOfRadians(lat), Longitude.valueOfRadians(lon));
    }

    /**
     * Finds the {@link GCPoint} that is a certain spherical angle away from p1 along the heading towards p2.
     * 
     * @param p1 starting point
     * @param p2 heading point
     * @param spherical angle to travel (in radians)
     */
	 public static GCPoint findPoint(GCPoint p1, GCPoint p2, double angle)
	 {
	     double dist12 = angle(p1, p2);
	     
	     double A = Math.sin(dist12 - angle)/Math.sin(dist12);
	     double B = Math.sin(angle)/Math.sin(dist12);
	     double x = A*Math.cos(p1.latitude().radians())*Math.cos(p1.longitude().radians())
	     	      + B*Math.cos(p2.latitude().radians())*Math.cos(p2.longitude().radians());
	     double y = A*Math.cos(p1.latitude().radians())*Math.sin(p1.longitude().radians())
	              + B*Math.cos(p2.latitude().radians())*Math.sin(p2.longitude().radians());
	     double z = A*Math.sin(p1.latitude().radians()) + B*Math.sin(p2.latitude().radians());
	     double lat = Math.atan2(z, Math.sqrt(x*x + y*y));
	     double lon = Math.atan2(y, x);
	     
	     return new GCPoint( Latitude.valueOfRadians(lat), Longitude.valueOfRadians(lon) );
	 }
	 
	 /**
     * Finds the {@link GCPoint} that is a certain fraction from p1 along the heading towards p2.
     * 
     * @param p1 starting point
     * @param p2 heading point
     * @param alpha fraction to travel [0.0,1.0]
     */
	 public static GCPoint interpolatePoint(GCPoint p1, GCPoint p2, double alpha)
	 {
		 if (alpha == 0.0) {
			return p1;
		} else if (alpha == 1.0) {
			return p2;
		}
		 
	     double dist12 = angle(p1, p2);
	     double angle = alpha * dist12;
	     double sinDist = Math.sin(dist12);
	     
	     double A = Math.sin(dist12 - angle)/sinDist;
	     double B = Math.sin(angle)/sinDist;
	     double x = A*Math.cos(p1.latitude().radians())*Math.cos(p1.longitude().radians())
	     	      + B*Math.cos(p2.latitude().radians())*Math.cos(p2.longitude().radians());
	     double y = A*Math.cos(p1.latitude().radians())*Math.sin(p1.longitude().radians())
	              + B*Math.cos(p2.latitude().radians())*Math.sin(p2.longitude().radians());
	     double z = A*Math.sin(p1.latitude().radians()) + B*Math.sin(p2.latitude().radians());
	     double lat = Math.atan2(z, Math.sqrt(x*x + y*y));
	     double lon = Math.atan2(y, x);
	     
	     return new GCPoint( Latitude.valueOfRadians(lat), Longitude.valueOfRadians(lon) );
	 }
	 
    public static double trueCourse(GCPoint p1, GCPoint p2)
    {
    	return SphericalUtilities.trueCourse(p1.latitude.radians(), p1.longitude.radians(), p2.latitude.radians(), p2.longitude.radians());
    }

    @Deprecated
	public static double alongTrackAngle(GCPoint p1, GCPoint p2, GCPoint p3)
    {
    	return SphericalUtilities.alongTrackDistance(
    			p1.latitude.radians(), p1.longitude.radians(),
    			p2.latitude.radians(), p2.longitude.radians(),
    			p3.latitude.radians(), p3.longitude.radians());
    }
    
	/**
	 * Computes the angle between by p1->p2 and a Great Circle extending due East of p1 by:
	 * - Creating a point p3 due East of p1
	 * - Calculating the angle between vectors p1->p3 and p1->p2
	 * The return value is the angle (in radians) and its range is [0, 2PI) radians.
	 * NOTE: Be careful of the order of points p1 & p2... exchanging them can yield different results.
	 * e.g. If p1 = ( 0 Lat,  0 Lon) & p2 = (10 Lat, 10 Lon), equatorial angle ~=  PI/8 radians ( ~45.4385 deg).
	 *      If p1 = (10 Lat, 10 Lon) & p2 = ( 0 Lat,  0 Lon), equatorial angle ~= 3PI/2 radians (~220.4474 deg).
	 * @param p1
	 * @param p2
	 * @return Angle between Equator (West to East pointing) in radians [0, 2PI)
	 */
    @Deprecated
	public static double equatorialAngle(GCPoint p1, GCPoint p2) throws InvalidInputException
	{
		// Create a second equatorial point 45 degrees East of eq1
		Vector3D p3 = SphericalUtilities.rotateZ(p1.vector(), Math.PI/4);

		/*
		// GCAngle() returns the an answer between 0 to PI radians (0-180 degrees)...
		// if edge dips below equator, force the answer to go around the other way
		double angle = Utilities.GCangle(p1, p3, p1, p2);
		if (GCUtilities.isRight( p1, p3, p2) )
		{
			angle = 2*Math.PI - angle;
		}
		*/
		return SphericalUtilities.gcInteriorAngle(p3, p1.vector(), p2.vector());
	}	

	@Deprecated
	public static double equatorialAngle(GCEdge edge) throws InvalidInputException
	{
		return equatorialAngle(edge.first, edge.second);
	}
}