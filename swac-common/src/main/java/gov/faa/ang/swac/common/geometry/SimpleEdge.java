/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.faa.ang.swac.common.geometry;

import gov.faa.ang.swac.common.utilities.Mathematics;

/**
 * @author ssmitz
 * 
 * cross represents whether this edge crosses the antemeridian. 
 * Azimuth and elevation should be pre-computed to avoid redundant processing.
 */
public class SimpleEdge {
    private double lat1;
    private double lon1;
    private double lon2;
    private boolean cross;
    private double azimuth;
    private double elevation;

    public double getLat1() {
        return lat1;
    }

    public double getLon1() {
        return lon1;
    }

    public double getLon2() {
        return lon2;
    }

    public boolean isCross() {
        return cross;
    }

    public double getAzimuth() {
        return azimuth;
    }

    /**
     * 
     * @return change in latitude in degrees
     */
    public double getElevation() {
        return elevation;
    }

    public SimpleEdge() {  }
    
    public SimpleEdge(GCPoint p1, GCPoint p2) {
        double ilat = p1.latitude.normalized().degrees();
        double ilon = p1.longitude.normalized().degrees();

        double jlat = p2.latitude.normalized().degrees();
        double jlon = p2.longitude.normalized().degrees();

        this.lat1 = ilat;
        this.lon1 = ilon;
        this.lon2 = jlon;

        // pre-compute azimuth and elevation;
        this.azimuth = SimpleEdge.azimuth(ilon, jlon);
        this.elevation = jlat - ilat;

        // If being west of a point is not equivalent to having lower longitude, then the antemeridian is crossed
        this.cross = SimpleEdge.azimuth(ilon, jlon) != (jlon - ilon);
    }

    /**
     * Assuming lon values are in degrees -180 < lon < 180, returns the angle
     * from lon1 to lon2, respective of the antemeridian. @param lon1 @param
     * lon2 @return
     */
    public static double azimuth(double lon1, double lon2) {
        double val = lon2 - lon1;
        if (val < -180) {
            return val + 360;
        } else if (val > 180) {
            return val - 360;
        } else {
            return val;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof SimpleEdge) {
            SimpleEdge se = (SimpleEdge) o;
            
            if(	this.cross == se.cross &&
	            Math.abs(this.lat1 - se.lat1) < Mathematics.numericalTolerance() &&
	            Math.abs(this.lon1 - se.lon1) < Mathematics.numericalTolerance() &&
	            Math.abs(this.lon2 - se.lon2) < Mathematics.numericalTolerance() &&
	            Math.abs(this.azimuth - se.azimuth) < Mathematics.numericalTolerance() &&
	            Math.abs(this.elevation - se.elevation) < Mathematics.numericalTolerance()){
            	return true;
            }
        } 

        return false;
    }
    
    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(azimuth);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + (cross ? 1231 : 1237);
		temp = Double.doubleToLongBits(elevation);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(lat1);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(lon1);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(lon2);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}
}
