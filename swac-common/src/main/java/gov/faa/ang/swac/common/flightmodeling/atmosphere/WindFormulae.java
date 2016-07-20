/**
 * Copyright "TBD", Metron Aviation & CSSI.  All rights reserved.
 * 
 * This computer Software was developed with the sponsorship of the U.S. Government
 * under Contract No. DTFAWA-10-D-00033, which has a copyright license in accordance with AMS 3.5-13.(c)(1).
 */

package gov.faa.ang.swac.common.flightmodeling.atmosphere;


import gov.faa.ang.swac.common.datatypes.Angle;
import gov.faa.ang.swac.common.datatypes.Latitude;
import gov.faa.ang.swac.common.datatypes.Longitude;
import gov.faa.ang.swac.common.geometry.GCPoint;
import gov.faa.ang.swac.common.geometry.GCUtilities;

public class WindFormulae
{
    private static final double nmiToRad = Math.PI/10800.0;

    private WindFormulae()
    {
    }
    
    @Deprecated
    public static GCPoint windFindPoint(GCPoint p1, GCPoint p2,
        PointWind w, double airspeed, double numSeconds)
    {
        // find the point numSeconds from p1 to p2 given the wind data w and the airspeed
        GCPoint outputPoint = null;
        
        double airspeedDistanceInRadians = airspeed*numSeconds/3600.0*nmiToRad;
        
        if(w == null || w.getWindSpeed() == 0)
        {
            // no wind, just find the point airspeedDist from p1 towards p2
            outputPoint = GCUtilities.findPoint(p1, p2, airspeedDistanceInRadians);
        }
        else
        {
            // wind
            double distWindSpeed = w.getWindSpeed()*numSeconds/3600.0*nmiToRad;
            double windHeading = w.getWindHeading(PointWind.RADIANS);
            double trueCourse = GCUtilities.trueCourse(p1, p2); // in radians
            windHeading = windHeading + Math.PI/2.0 - trueCourse;
            
            GCPoint tPt = new GCPoint(Latitude.valueOfDegrees(0), Longitude.valueOfDegrees(0));
            GCPoint tPt2 = GCUtilities.findPoint(tPt, distWindSpeed, Angle.valueOfRadians(windHeading));
            double temp = Math.cos(airspeedDistanceInRadians)/Math.cos(tPt2.latitude().radians());
            temp = Math.max(Math.min(temp, 1), -1);
            temp = Math.acos(temp);
            double tempDist1 = tPt2.longitude().radians() + temp;
            double tempDist2 = tPt2.longitude().radians() - temp;
            
            // below now assumes we started with longitude being < 0 for West points
            double distToOutputPoint = Math.max(tempDist1, tempDist2); 
            outputPoint = GCUtilities.findPoint(p1, distToOutputPoint, Angle.valueOfRadians(trueCourse));
        }
        return outputPoint;
    }
    
    // XXX: VERY inefficient. Should just calculate the ground speed given the head/tail component of wind...interpolating a point is more trigonometry than we need
    @Deprecated
    public static double windFindTime(GCPoint p1, GCPoint p2, PointWind w, double airspeed)
    {
        // find the time to get from p1 to p2 with given airspeed and wind w
        // return time in seconds
        double numSeconds = 0;
        double gcDist = GCUtilities.gcDistance(p1, p2);
        
        if(w == null || w.getWindSpeed() == 0)
        {
            // no wind, time is just dist/speed
            numSeconds = gcDist/airspeed*3600.0;
        }
        else
        {
            // wind
            GCPoint tPt = windFindPoint(p1, p2, w, airspeed, 60.0); // one minute
            double tGcDist = GCUtilities.gcDistance(p1, tPt); // nmi/minute
            numSeconds = gcDist/tGcDist*60.0; // nmi/(nmi/minute)*(seconds/minute) = seconds
        }
        return numSeconds;
    }
    
    /**
     * Calculates time it take to fly from p1 to p2 when an aircraft has to change its heading to account
     * for wind in order to fly along the original track from p1 to p2.
     * 
     * @param p1 aircraft start point
     * @param p2 aircraft end point
     * @param w wind info (speed, direction)
     * @param airspeed of aircraft
     * @return seconds required to fly from p1 to p2.
     */
    public static double secondsToNextNode(GCPoint p1, GCPoint p2, PointWind w, double airspeed){
    	
    	double gcDist = GCUtilities.gcDistance(p1, p2);
    	if (w == null || w.getWindSpeed() == 0.0){
            // no wind, time is just dist/speed
            return gcDist/airspeed*3600.0;
    	}

    	// HK: formula obtained from: http://www.delphiforfun.org/programs/math_topics/WindTriangle.htm
    	double acHeading = GCUtilities.trueCourse(p1, p2);
    	double windHeading = w.getWindHeading(PointWind.RADIANS);  // do we need to reverse this wind direction?
    	double windToTrackAngle = acHeading - windHeading;
    	double windCorrectionAngle = Math.asin(w.getWindSpeed()*Math.sin(windToTrackAngle)/airspeed);
    	double actualGroundSpeed = airspeed * Math.cos(windCorrectionAngle) + w.getWindSpeed() * Math.cos(windToTrackAngle);
    	return gcDist / actualGroundSpeed * 3600.0;
    }
    
}
