package gov.faa.ang.swac.common.datatypes;

import java.io.Serializable;


/**
 * Specializes in the manipulation and text representation of {@link Angle}s that represent latitude.
 * <p>
 * {@link Latitude} objects are immutable.
 * @author Jason Femino - CSSI, Inc.
 */
public class Latitude extends Angle implements Serializable
{
	private static final long serialVersionUID = -2237665450379356597L;

	public Latitude()
	{
	}
	
	public Latitude(Latitude latitude)
	{
		this.angleRads = (latitude.angleRads == null ? null : latitude.angleRads);
	}
	
	public Latitude(double latitude, Units units) throws IllegalArgumentException
	{
		switch (units)
		{
		case RADIANS:
			this.angleRads = latitude;
			break;
		case DEGREES:
			this.angleRads = latitude * Angle.DEGREES_TO_RADIANS;
			break;
		default:
			throw new IllegalArgumentException("Latitude() error: Unhandled Units \"" + units + "\". Exiting.");
		}
	}

	/**
	 * Convenience method for {@link #Latitude(double, Units)}.
	 */
	public static Latitude valueOfDegrees(double degrees)
	{
		return new Latitude(degrees, Units.DEGREES);
	}
	
	/**
	 * Convenience method for {@link #Latitude(double, Units)}.
	 */
	public static Latitude valueOfRadians(double radians)
	{
		return new Latitude(radians, Units.RADIANS);
	}

	@Override
	public boolean equals(Object o)
	{
		return o instanceof Latitude && this.angleRads.equals(((Latitude)o).angleRads);
	}
	
	/**
	 * Ensures that value of this {@link Latitude} is within the range:<br>
	 * "<code>[-PI/2, PI/2]</code>" radians <b>OR</b> "<code>[-90, 90]</code>" degrees
	 * <p>
	 * <b>NOTE:</b> The set notation denotes that both ends of the range are inclusive.
	 */
	@Override
	public Latitude normalized()
	{
		if (this.angleRads == null)
		{
			return null;
		}
		
		if (isNormalized())
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
		
		// Fold over the ends to fall inside [-PI/2,+PI/2]
		if (Math.PI/2 < normalizedRadians)
		{
			normalizedRadians = Math.PI - normalizedRadians;
		}
		else if (normalizedRadians < -Math.PI/2)
		{
			normalizedRadians = -Math.PI - normalizedRadians;
		}
		return new Latitude(normalizedRadians, Units.RADIANS);
	}

	/**
	 * Returns true if and only if the value of this {@link Latitude} is within the range:<br>
	 * "<code>[-PI/2, PI/2]</code>" radians <b>OR</b><br>
	 * "<code>[-90, 90]</code>" degrees
	 * <b>NOTE:</b> The set notation denotes that both ends of the range are inclusive.
	 */
	@Override
	public boolean isNormalized()
	{
		return this.angleRads != null &&
			-Math.PI/2 <= this.angleRads &&
			this.angleRads <= Math.PI/2;
	}
	
    /**
     * Determines if a {@link Latitude} is north of this {@link Latitude}.
     */
    public boolean isNorthOf(final Latitude b)
    {
    	return b.normalized().angleRads < this.normalized().angleRads;
    }

    /**
     * Determines if a {@link Latitude} is south of this {@link Latitude}.
     */
    public boolean isSouthOf(final Latitude b)
    {
    	return this.normalized().angleRads < b.normalized().angleRads;
    }
    
	public static String toString(double degrees, Format format)
	{
		return toString(degrees, format, "N", "S");
	}
	
	@Override
	public String toString(Format format)
	{
		return toString(this.degrees(), format, "N", "S");
	}
    
    @Override
    public Latitude clone() {
        return new Latitude(this);
    }
}