package gov.faa.ang.swac.common.datatypes;

import gov.faa.ang.swac.common.datatypes.DegMinSec;

import java.io.Serializable;

/**
 * A class that specializes in the manipulation of angles and the formatting of the text representation of that data.
 * <p>
 * {@link Angle} objects are immutable
 * @author Jason Femino - CSSI, Inc.
 */
public class Angle implements Serializable, Comparable<Angle>, Cloneable
{
	private static final long serialVersionUID = -7383942240096445338L;
	public final static double DEGREES_TO_RADIANS = Math.PI/180.0f;
    public final static double RADIANS_TO_DEGREES = 180.0f/Math.PI;
	protected static final double TWO_PI = 2*Math.PI;
    
	public enum Units
	{
		RADIANS,
		DEGREES
	}

	/**
	 * Specifies the format of the output of the print() method:
	 * <ul>
	 * <li><b>DECIMAL</b> -> "<code>{-}DD.DDDD</code>"</li>
	 * <li><b>COMPACT</b> -> "<code>{-}DDMMSS</code>"</li>
	 * <li><b>SHORT</b> -> "<code>{-}DD.MM.SS</code>"</li>
	 * <li><b>LONG</b> -> "<code>{-}DD deg, MM min, SS sec</code>"</li>
	 * <li><b>SYMBOL</b> -> "<code>{-}DD&deg; MM' SS"</code>"</li>
	 * </ul>
	 * For subclasses only:
	 * <ul>
	 * <li><b>DECIMAL_NSEW</b> -> "<code>DD.DDDD [NSEW]</code>"</li>
	 * <li><b>COMPACT_NSEW</b> -> "<code>DDMMSS [NSEW]</code>"</li>
	 * <li><b>SHORT_NSEW</b> -> "<code>DD.MM.SS [NSEW]</code>"</li>
	 * <li><b>LONG_NSEW</b> -> "<code>DD deg, MM min, SS sec [NSEW]</code>"</li>
	 * <li><b>SYMBOL_NSEW</b> -> "<code>DD&deg; MM' S.SS" [NSEW]</code>"</li>
	 * </ul>
	 */
	public enum Format
	{
		DECIMAL,       // {-}DD.DDDD
		COMPACT,       // {-}DDMMSSSS
		SHORT,         // {-}DD.MM.SSSS
		LONG,          // {-}DD deg, MM min, SS.SS sec
		SYMBOL,        // {-}DD&deg; MM' SS.SS"
		DECIMAL_NSEW,  // DD.DDDD [NSEW]
		COMPACT_NSEW,  // DDMMSSSS [NSEW]
		SHORT_NSEW,    // DD.MM.SSSS [NSEW]
		LONG_NSEW,     // DD deg, MM min, SS.SS sec [NSEW]
		SYMBOL_NSEW,   // DD&deg; MM' SS.SS" [NSEW]
	}

	Double angleRads = null; // Stored as radians
	
	public Angle()
	{
	}
	
	public Angle(Angle angle)
	{
		this.angleRads = angle.angleRads;
	}

	public Angle(double angle, Units units)
	{
		switch (units)
		{
		case RADIANS:
			this.angleRads = angle;
			break;
		case DEGREES:
			this.angleRads = angle * Angle.DEGREES_TO_RADIANS;
			break;
		default:
			throw new IllegalArgumentException("Angle() error: Unhandled Units \"" + units + "\". Exiting.");
		}
	}

	/**
	 * Convenience method for {@link #Angle(double, Units)}.
	 */
	public static Angle valueOfDegrees(double degrees)
	{
		return new Angle(degrees, Units.DEGREES);
	}
	
	/**
	 * Convenience method for {@link #Angle(double, Units)}.
	 */
	public static Angle valueOfRadians(double radians)
	{
		return new Angle(radians, Units.RADIANS);
	}
	
	@Override
	public boolean equals(Object o)
	{
		return o instanceof Angle && this.angleRads.equals(((Angle)o).angleRads);
	}
	
	@Override
	public int compareTo(Angle o)
	{
		return this.angleRads.compareTo(o.angleRads);
	}
	
	@Override
	public int hashCode()
	{
		return this.angleRads.hashCode(); 
	}
	
	/**
	 * Returns the value of this {@link Angle} in degrees.
	 */
	public Double degrees()
	{
		return (this.angleRads * Angle.RADIANS_TO_DEGREES);
	}
	
	/**
	 * Returns the value of this {@link Angle} in radians.
	 */
	public Double radians()
	{
		return this.angleRads;
	}
	
	/**
	 * Ensures that value of this {@link Angle} is within the range:<br>
	 * <code>[0,2*PI)</code> radians <b>OR </b> 
	 * <code>[0,360)</code> degrees.
	 * <p>
	 * <b>NOTE:</b> The set notation denotes that '<code>[</code>' is inclusive and '<code>)</code>' is not.
	 */
	public Angle normalized()
	{
		if (this.angleRads == null)
		{
			return null;
		}
		
		if (!isNormalized())
		{
			double normalizedRadians = this.angleRads;
			normalizedRadians %= TWO_PI;
			if (normalizedRadians < 0)
			{
				normalizedRadians += TWO_PI;
			}
			
			return new Angle(normalizedRadians, Units.RADIANS);
		}

		return new Angle(this);
	}
	
	/**
	 * Returns true if and only if the value of this {@link Angle} is within the range:<br>
	 * <code>[0,2*PI)</code> radians <b>OR </b> 
	 * <code>[0,360)</code> degrees.<br>
	 * <b>NOTE:</b> The set notation denotes that '<code>[</code>' is inclusive and '<code>)</code>' is not.
	 */
	public boolean isNormalized()
	{
		return this.angleRads != null && 0 <= this.angleRads && this.angleRads < TWO_PI;
	}
	
	/**
	 * Returns true if and only if the value of this is between the arguments.
	 * @param minAngle the minimum {@link Angle}
	 * @param maxAngle the maximum {@link Angle}
	 */
	public boolean isBetween(Angle minAngle, Angle maxAngle)
	{
		double minAngleRads = minAngle.normalized().radians();
		double maxAngleRads = maxAngle.normalized().radians();
		double thisAngleRads = this.normalized().radians();
		
		// Handle the ordinary case (minAngle <= maxAngle)
		if (minAngleRads <= maxAngleRads)
		{
			return minAngleRads <= thisAngleRads && thisAngleRads <= maxAngleRads;
		}
		
		// For maxAngle < minAngle
		return minAngleRads <= thisAngleRads || thisAngleRads <= maxAngleRads;
	}
	
	/**
	 * Prints degrees using the specified {@link Format} and (if applicable) the specified positive/negative symbols.
	 * @param degrees angle (in degrees)
	 * @param format {@link Format}
	 * @param positive string that denotes positive angle (e.g. "<code>+</code>" or "<code>N</code>" or "<code>E</code>")
	 * @param negative string that denotes negative angle (e.g. "<code>-</code>" or "<code>S</code>" or "<code>W</code>")
	 * @see {@link Format}
	 */
	protected static String toString(double degrees, Format format, String positive, String negative)
	{
		String output = new String();
		
		switch (format)
		{
		case DECIMAL:
			output = String.format("%1$1.15f", degrees);
			break;

		case COMPACT:
			output = DegMinSec.toString(new DegMinSec(degrees), DegMinSec.Format.COMPACT);
			break;

		case SHORT:
			output = DegMinSec.toString(new DegMinSec(degrees), DegMinSec.Format.SHORT);
			break;

		case LONG:
			output = DegMinSec.toString(new DegMinSec(degrees), DegMinSec.Format.LONG);
			break;
			
		case SYMBOL:
			output = DegMinSec.toString(new DegMinSec(degrees), DegMinSec.Format.SYMBOL);
			break;
			
		case DECIMAL_NSEW:
			output = String.format("%1$1.15f", Math.abs(degrees)) + " " + (degrees < 0 ? negative : positive);			
			break;
			
		case COMPACT_NSEW:
			output = DegMinSec.toString(new DegMinSec(Math.abs(degrees)), DegMinSec.Format.COMPACT) + " " + (degrees < 0 ? negative : positive);			
			break;
			
		case SHORT_NSEW:
			output = DegMinSec.toString(new DegMinSec(Math.abs(degrees)), DegMinSec.Format.SHORT) + " " + (degrees < 0 ? negative : positive);			
			break;
			
		case LONG_NSEW:
			output = DegMinSec.toString(new DegMinSec(Math.abs(degrees)), DegMinSec.Format.LONG) + " " + (degrees < 0 ? negative : positive);			
			break;
			
		case SYMBOL_NSEW:
		default:
			output = DegMinSec.toString(new DegMinSec(Math.abs(degrees)), DegMinSec.Format.SYMBOL) + " " + (degrees < 0 ? negative : positive);			
			break;
		}

		return output;
	}
	
	/**
	 * Prints degrees using the specified {@link Format}.
	 * @param degrees angle (in degrees)
	 * @param format {@link Format}
	 * @see {@link Format}
	 */
	public static String toString(double degrees, Format format)
	{
		return toString(degrees, format, "", ""); // The positive & negative symbols are unused.
	}
	
	/**
	 * Prints {@link Angle} (in degrees) using the specified {@link Format}.
	 * @param format {@link Format}
	 * @see {@link Format}
	 */
	public String toString(Format format)
	{
		return toString(this.degrees(), format);
	}
	
	@Override
	public String toString()
	{
		return toString(Format.DECIMAL) + " deg";
	}
        
    @Override
    public Angle clone() {
        return new Angle(this);
    }
}