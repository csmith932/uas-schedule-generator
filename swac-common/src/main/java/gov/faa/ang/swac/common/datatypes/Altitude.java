package gov.faa.ang.swac.common.datatypes;

import java.io.Serializable;

/**
 * Represents height above mean sea level.
 * <p>
 * {@link Altitude} objects are immutable.
 * <p>
 * <b>NOTE:</b> Heights that are "above ground level" should be converted to "above mean sea level" before assignment to an {@link Altitude} object.
 * @author Jason Femino - CSSI, Inc.
 */
public class Altitude implements Serializable, Comparable<Altitude>
{
	private static final long serialVersionUID = 5393997086087232945L;
	public static final double FEET_TO_METERS = 0.3048;
	public static final double METERS_TO_FEET = 3.281;
	
	public enum Units
	{
		FEET,
		METERS
	}
	
	private final Double alt; // Stored in feet
	
	public static final Altitude NULL = new Altitude();
	public static final Altitude SEA_LEVEL = Altitude.valueOfFeet(0);
	
	private Altitude()
	{
		alt = Double.NaN;
	}
	
	public Altitude(double altitude, Units units)
	{
		switch (units)
		{
		case FEET:
			this.alt = altitude;
			break;
		case METERS:
			this.alt = altitude * METERS_TO_FEET;
			break;
		default:
			throw new IllegalArgumentException("Altitude() error: Unhandled Units \"" + units + "\". Exiting.");
		}
	}
	
   /**
     * Convenience method for {@link #Altitude(double, Units)}.
     */
    public static Altitude valueOfFeet(double feet)
    {
        return new Altitude(feet, Units.FEET);
    }
    
    /**
     * Convenience method for {@link #Altitude(double, Units)}.
     */
    public static Altitude valueOfMeters(double meters)
    {
        return new Altitude(meters, Units.METERS);
    }
	
	@Override
	public boolean equals(Object o)
	{
		return o instanceof Altitude && this.alt.equals(((Altitude)o).alt);
	}
	
	@Override
	public int compareTo(Altitude o)
	{
		return this.alt.compareTo(o.alt);
	}
	
	@Override
	public int hashCode()
	{
		return this.alt.hashCode(); 
	}

	public Double feet()
	{
		return this.alt;
	}
	
	public Double meters()
	{
		return this.alt == null ? null : this.alt * FEET_TO_METERS;
	}
	
	/**
	 * Converts this {@link Altitude} to a {@link Integer} flight level (in hundreds of feet - truncated, not rounded).
	 */
	public Integer flightLevel()
	{
		if (this.alt == null)
		{
			return null;
		}
		
		return (int)Math.round(this.feet() / 100.0);
	}

	@Override
	public String toString()
	{
		return (this.alt == null ? "null" : String.format("%1$d ft", Math.round(this.alt)));
	}
}