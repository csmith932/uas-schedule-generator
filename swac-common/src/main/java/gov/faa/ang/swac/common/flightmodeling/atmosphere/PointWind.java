/**
 * Copyright "TBD", Metron Aviation & CSSI.  All rights reserved.
 * 
 * This computer Software was developed with the sponsorship of the U.S. Government
 * under Contract No. DTFAWA-10-D-00033, which has a copyright license in accordance with AMS 3.5-13.(c)(1).
 */

package gov.faa.ang.swac.common.flightmodeling.atmosphere;

import java.io.Serializable;

import gov.faa.ang.swac.common.utilities.Mathematics;

public class PointWind implements Serializable, Cloneable
{
	public static final boolean DEGREES = true;
	public static final boolean RADIANS = false;
	
    private double windSpeed; // in knots
    private double windHeadingDegrees; // in degrees, 0 is North, positive is clockwise
    
    private static final double degToRad = Math.PI/180.0;
    private static final double radToDeg = 180.0/Math.PI;
    
    public PointWind()
    {
        this.windSpeed = 0;
        this.windHeadingDegrees = 0;
    }
    
    public PointWind(double windSpeed, double windHeadingDegrees)
    {
        this.windSpeed = windSpeed;
        this.windHeadingDegrees = Mathematics.mod(windHeadingDegrees, 360);
    }
    
    public PointWind(PointWind p1)
    {
        this.windSpeed = p1.getWindSpeed();
        this.windHeadingDegrees = Mathematics.mod(p1.getWindHeading(DEGREES), 360);
    }
    public void setWindSpeed(double knots)
    {
        this.windSpeed = knots;
    }

    public double getWindSpeed()
    {
        return this.windSpeed;
    }

    public void setWindHeading(double windHeading, boolean inDegrees)
    {
    	if (inDegrees)
    	{
            this.windHeadingDegrees = windHeading;    		
    	}
    	else
    	{
            this.windHeadingDegrees = windHeading * radToDeg;
    	}
    }

    public double getWindHeading(boolean inDegrees)
    {
    	if (inDegrees)
    	{
    		return this.windHeadingDegrees;
    	}

    	return this.windHeadingDegrees * degToRad;
    }
    
    @Override
    public String toString()
    {
    	return String.format("%1$1.2f knots @ %2$1.2f deg", getWindSpeed(), getWindHeading(DEGREES));
    }
    
    @Override
    public PointWind clone() {
        return new PointWind(this);
    }
}
