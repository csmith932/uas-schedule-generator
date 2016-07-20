package gov.faa.ang.swac.common.datatypes;

import java.io.Serializable;

/**
 * Specializes in the manipulation and text representation of {@link Angle}s that represent longitude.
 * <p>
 * {@link Longitude} objects are immutable.
 * @author Jason Femino - CSSI, Inc.
 */
public class Longitude extends Angle implements Serializable
{
	private static final long serialVersionUID = -3179399292116631871L;
	
	public Longitude()
	{
	}	
	
	public Longitude(Longitude longitude)
	{
		this.angleRads = (longitude.angleRads == null ? null : longitude.angleRads.doubleValue());
	}

	public Longitude(double longitude, Units units)
	{
		switch (units)
		{
		case RADIANS:
			this.angleRads = longitude;
			break;
		case DEGREES:
			this.angleRads = longitude * Angle.DEGREES_TO_RADIANS;
			break;
		default:
			throw new IllegalArgumentException("Longitude() error: Unhandled Units \"" + units + "\". Exiting.");
		}
	}

	/**
	 * Convenience method for {@link #Longitude(double, Units)}.
	 */
	public static Longitude valueOfDegrees(double degrees)
	{
		return new Longitude(degrees, Units.DEGREES);
	}
	
	/**
	 * Convenience method for {@link #Longitude(double, Units)}.
	 */
	public static Longitude valueOfRadians(double radians)
	{
		return new Longitude(radians, Units.RADIANS);
	}

	@Override
	public boolean equals(Object o)
	{
		return o instanceof Longitude && this.angleRads.equals(((Longitude)o).angleRads);
	}
	
	/**
	 * Ensures that value of this {@link Longitude} is within the range:<br>
	 * "<code>(-PI, PI]</code>" radians <b>OR</b> "<code>(-180, 180]</code>" degrees.
	 * <p>
	 * <b>NOTE:</b> The set notation denotes that '<code>(</code>' is not inclusive and '<code>]</code>' is.
	 */
	@Override
	public Longitude normalized()
	{
		if (this.angleRads == null)
		{
			return null;
		}
		
		if (this.isNormalized())
		{
			return this;
		}

		// Normalize to the interval [0,2*PI)
		double normalizedRadians = super.normalized().radians();
					
		// Map the interval [0,2*PI) to the interval (-PI,+PI]
		if (Math.PI < normalizedRadians)
		{
			normalizedRadians -= 2*Math.PI;
		}
	
		return new Longitude(normalizedRadians, Units.RADIANS);
	}
	
	/**
	 * Returns true if and only if the value of this {@link Longitude} is within the range:<br>
	 * "<code>(-PI, PI]</code>" radians <b>OR</b> "<code>(-180, 180]</code>" degrees.
	 * <b>NOTE:</b> The set notation denotes that '<code>(</code>' is not inclusive and '<code>]</code>' is.
	 */
	@Override
	public boolean isNormalized()
	{
		return 
			this.angleRads != null &&
			-Math.PI < this.angleRads &&
			this.angleRads <= Math.PI;
	}
	
    /**
     * Determines if a {@link Longitude} is west of this {@link Longitude}.
     */
    public boolean isWestOf(final Longitude b)
    {
    	// Compute the difference (-2*PI,2*PI)
    	final double delta = (this.normalized().angleRads - b.normalized().angleRads);
    	
    	// If the difference is in (-PI,0) or (PI,2*PI), then this is west of b.
    	// This may be clearer: (lon1 - PI) < lon2 < lon1 *OR* (lon1 + PI) < lon2 < lon1 + 2*PI
    	return (-Math.PI < delta && delta < 0) || (+Math.PI < delta && delta < 2*Math.PI);
    }

    /**
     * Determines if a {@link Longitude} is east of this {@link Longitude}.
     */
    public boolean isEastOf(final Longitude b)
    {
    	// Compute the difference (-2*PI,2*PI)
    	final double delta = (b.normalized().angleRads - this.normalized().angleRads);
    	
    	// If the difference is in (-PI,0) or (PI,2*PI), then this is east of b.
    	// This may be clearer: (lon2 - PI) < lon1 < lon2 *OR* (lon2 + PI) < lon1 < lon2 + 2*PI
    	return (-Math.PI < delta && delta < 0) || (+Math.PI < delta && delta < 2*Math.PI);
    }    
    
    /**
     * Determines if a {@link Longitude} is on the opposite side of this {@link Longitude}.
     */
    public boolean isOppositeTo(final Longitude b)
    {
    	// Compute the distance (-2*PI,2*PI)
    	final double delta = (b.normalized().angleRads - this.normalized().angleRads);

    	return (Math.abs(delta) == Math.PI);
    }
    
	public static String toString(double degrees, Format format)
	{
		return toString(degrees, format, "E", "W");
	}
	
	@Override
	public String toString(Format format)
	{
		return toString(this.degrees(), format, "E", "W");
	}
        
    @Override
    public Longitude clone() {
        return new Longitude(this);
    }
}