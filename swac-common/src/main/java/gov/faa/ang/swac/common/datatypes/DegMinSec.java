package gov.faa.ang.swac.common.datatypes;

/**
 * A class that specializes in the manipulation of data containing degrees, minutes, and seconds...
 * and the formatting of the text representation of that data.
 * 
 * @author Jason Femino - CSSI, Inc.
 */
public class DegMinSec implements Comparable<DegMinSec>
{
    public enum InputFormat
    {
        I_COMPACT, // {-}DDMMSS, or {-}DDDMMSS
    }

    public static final Character DEGREE_SYMBOL = '\u00B0';

    // See DegMinSec.toString(DegMinSec, Format) for format details.
    public enum Format
    {
        COMPACT, SHORT, LONG, SYMBOL,
    }

    protected boolean positive = true;
    protected int degrees = 0;
    protected int minutes = 0;
    protected double seconds = 0;

    public DegMinSec()
    {
    }

    public DegMinSec(DegMinSec degMinSec)
    {
        this.positive = degMinSec.positive;
        this.degrees = degMinSec.degrees;
        this.minutes = degMinSec.minutes;
        this.seconds = degMinSec.seconds;
    }

    public DegMinSec(double degrees)
    {
        if (degrees < 0)
        {
            this.positive = false;
            degrees *= -1;
        }
        this.set(degrees);
    }

    /**
     * Creates a new {@link DegMinSec}.<br>
     * It is equivalent to "<code>new DegMinSec().set(positive, degrees, minutes, seconds)</code>"
     * 
     * @see #set(boolean, int, int, double)
     */
    public DegMinSec(boolean positive, int degrees, int minutes, double seconds)
    {
        this.set(positive, degrees, minutes, seconds);
    }

    /**
     * Sets this {@link DegMinSec} by explicitly assigning the input values to this object's
     * members.
     * <p>
     * <b>NOTE:</b>
     * <ul>
     * <li>The sign of this {@link DegMinSec} is determined <u>entirely</u> by the input parameter
     * "positive"</li>
     * <li>The sign of the input degrees, minutes, and seconds will be ignored (using
     * <code>Math.abs()</code>)</li>
     * <li>This method will NOT {@link #normalize()} the {@link DegMinSec}</li>
     * </ul>
     */
    public DegMinSec set(boolean positive, int degrees, int minutes, double seconds)
    {
        this.positive = positive;
        this.degrees = Math.abs(degrees);
        this.minutes = Math.abs(minutes);
        this.seconds = Math.abs(seconds);
        return this;
    }

    /**
     * Sets this {@link DegMinSec} by converting the input degrees into
     * <code>sign + degrees + minutes + seconds</code>.<br>
     * This will result in a {@link #normalize()}d {@link DegMinSec}.
     */
    public DegMinSec set(double degrees)
    {
        // Capture the sign of degrees
        if (degrees < 0)
        {
            this.positive = false;
            degrees *= -1;
        }

        // Integer degrees is the whole-number portion of the double "degrees"
        this.degrees = (int)degrees;

        // Subtract whole-number degrees from double "degrees"... and multiply by 60 to get minutes
        // Integer minutes is the whole-number portion of the double "minutes"
        // Multiplying by "sign" ensures that only degrees is negative... and that minutes & seconds are positive.
        double minutes = (degrees - this.degrees) * 60.0;
        this.minutes = (int)minutes;

        // Subtract whole-number minutes from double "minutes"... and multiply by 60 to get seconds
        // Integer seconds is the whole-number portion of the double "seconds"
        this.seconds = (minutes - this.minutes) * 60.0;

        return this;
    }

    public int compareTo(DegMinSec degMinSec)
    {
        if (this.asDegrees() < degMinSec.asDegrees()) { return -1; }
        if (this.asDegrees() > degMinSec.asDegrees()) { return 1; }

        return 0;
    }

    public int compareTo(double degrees)
    {
        if (this.asDegrees() < degrees) { return -1; }
        if (this.asDegrees() > degrees) { return 1; }

        return 0;
    }

    public int deg()
    {
        if (this.positive) { return this.degrees; }

        return this.degrees * -1;
    }

    public int min()
    {
        return this.minutes;
    }

    public double sec()
    {
        return this.seconds;
    }

    public DegMinSec normalize()
    {
        // Ensure that seconds is less than one minute
        while (this.seconds >= 60.0)
        {
            this.seconds -= 60.0;
            this.minutes++;
        }

        // Ensure that minutes is less than one degree
        while (this.minutes >= 60.0)
        {
            this.minutes -= 60.0;
            this.degrees++;
        }

        return this;
    }

    public DegMinSec roundToNearestSecond()
    {
        this.seconds = Math.round(this.seconds);
        return this;
    }

    /**
     * Returns this {@link DegMinSec} as a double degrees. That is: "
     * <code>sign * (degrees + (minutes/60.0) + (seconds/3600.0))</code>"
     * 
     * @return double
     */
    public double asDegrees()
    {
        int sign = 1;

        if (!this.positive)
        {
            sign = -1;
        }

        return sign * (this.degrees + (this.minutes / 60.0) + (this.seconds / 3600.0));
    }

    /**
     * Returns this {@link DegMinSec} as a double minutes. That is: "
     * <code>sign * ((degrees*60) + minutes + (seconds/60.0))</code>"
     * 
     * @return double
     */
    public double asMinutes()
    {
        int sign = 1;

        if (!this.positive)
        {
            sign = -1;
        }

        return sign * ((this.degrees * 60) + this.minutes + (this.seconds / 60.0));
    }

    /**
     * Returns this {@link DegMinSec} as a double seconds. That is: "
     * <code>sign * ((degrees*3600) + (minutes*60) + seconds)</code>"
     * 
     * @return double
     */
    public double asSeconds()
    {
        int sign = 1;

        if (!this.positive)
        {
            sign = -1;
        }

        return sign * ((this.degrees * 3600) + (this.minutes * 60.0) + this.seconds);
    }

    @Override
    public String toString()
    {
        return toString(this, Format.COMPACT);
    }

    public String toString(Format format)
    {
        return toString(this, format);
    }

    /**
     * Returns a {@link String} representation of a {@link DegMinSec} using the following
     * {@link Format}s:<br>
     * <ul>
     * <li>COMPACT: "<code>[-][D]DDMMSS</code>" where:</li>
     * <ul>
     * <li>"<code>[-]</code>" Optional negative sign</li>
     * <li>"<code>[D]DD</code>": Degrees as two digits (mininum), zero-padded</li>
     * <li>"<code>MM</code>": Minutes as two digits, zero-padded</li>
     * <li>"<code>SS</code>": Seconds as two digits, zero-padded, rounded to nearest whole second</li>
     * </ul>
     * <li>SHORT: "<code>[-][D]DD.MM.SS.SS</code>" where:</li>
     * <ul>
     * <li>"<code>[-]</code>" Optional negative sign</li>
     * <li>"<code>[D]DD</code>": Degrees as two digits (mininum), zero-padded</li>
     * <li>"<code>MM</code>": Minutes as two digits, zero-padded</li>
     * <li>"<code>SS.SS</code>": Decimal seconds, zero-padded, rounded to 2 decimal places</li>
     * </ul>
     * <li>LONG: "<code>[-]D deg M min S.SSSS sec</code>" where:</li>
     * <ul>
     * <li>"<code>[-]</code>" Optional negative sign</li>
     * <li>"<code>D</code>": Degrees</li>
     * <li>"<code>M</code>": Minutes</li>
     * <li>"<code>S.SSSS</code>": Decimal seconds, rounded to 4 decimal places</li>
     * </ul>
     * <li>SYMBOL: "<code>[-]D° M' S.SSSS"</code>" where:</li>
     * <ul>
     * <li>"<code>[-]</code>" Optional negative sign</li>
     * <li>"<code>D</code>": Degrees</li>
     * <li>"<code>M</code>": Minutes</li>
     * <li>"<code>S.SSSS</code>": Decimal seconds, rounded to 4 decimal places</li>
     * </ul>
     * </ul>
     * <table border="1">
     * <tr align=center>
     * <td><b>Examples:</b></td>
     * <td><b>COMPACT</b></td>
     * <td><b>SHORT</b></td>
     * <td><b>LONG</b></td>
     * <td><b>SYMBOL</b></td>
     * </tr>
     * <tr align=right>
     * <td>1 degree, 2 minutes, 3 seconds</td>
     * <td><code>010203</code></td>
     * <td><code>01.02.03.00</code></td>
     * <td><code>1 deg 2 min 3.0000 sec</code></td>
     * <td><code>1° 2' 3.0000"</code></td>
     * </tr>
     * <tr align=right>
     * <td>-179 degrees, 15 minutes, 4.56789 seconds</td>
     * <td><code>-1791505</code></td>
     * <td><code>-179.15.04.57</code></td>
     * <td><code>-179 deg 15 min 4.5679 sec</code></td>
     * <td><code>-179° 15' 4.5679"</code></td>
     * </tr>
     * </table>
     */
    public static String toString(DegMinSec degMinSec, Format format) throws IllegalArgumentException
    {
        String output = new String();

        switch (format)
        {
            case COMPACT:
                // Ensure rounding to the nearest second of angle
                long intSeconds = Math.round(degMinSec.seconds);
                long intMinutes = degMinSec.minutes;
                long intDegrees = degMinSec.degrees;

                // Carry seconds and minutes if necessary
                while (intSeconds >= 60)
                {
                    intSeconds -= 60;
                    intMinutes++;
                }
                while (intMinutes >= 60)
                {
                    intMinutes -= 60;
                    intDegrees++;
                }

                output = String.format("%s%02d%02d%02d",
                        degMinSec.positive ? "" : "-",
                        intDegrees,
                        intMinutes,
                        intSeconds);
                break;

            case SHORT:
                output = String.format("%s%02d.%02d.%05.2f",
                        degMinSec.positive ? "" : "-",
                        degMinSec.degrees,
                        degMinSec.minutes,
                        degMinSec.seconds);
                break;

            case LONG:
                output = String.format("%s%d deg %d min %2.4f sec",
                        degMinSec.positive ? "" : "-",
                        degMinSec.degrees,
                        degMinSec.minutes,
                        degMinSec.seconds);
                break;

            default:
            case SYMBOL:
                output = String.format("%s%d" + DEGREE_SYMBOL + " %d' %2.4f\"",
                        degMinSec.positive ? "" : "-",
                        degMinSec.degrees,
                        degMinSec.minutes,
                        degMinSec.seconds);

                if (format != Format.SYMBOL)
                    throw new IllegalArgumentException("DegMinSec.toString() error: Unsupported Format (" + format + "). Using Format.SYMBOL.");
                else
                    break;
        }

        return output;
    }

    /**
     * Conversion utility for certain "legacy" lat/lon formats into an equivalent value in decimal
     * degrees.
     * <p>
     * Input must be of the format "<code>DDMMSS</code>" or "<code>DDDDMMSS</code>"
     */
    public static Double dddmmssToDouble(String dddmmss) throws IllegalArgumentException
    {
        if (dddmmss.length() < 5)
        {
            throw new IllegalArgumentException("DegMinSec.dddmmssToDouble() ERROR: String \"" + dddmmss + "\" is too short. Must be at least 5 chars.");
        }

        double degrees = Double.valueOf(dddmmss.substring(0, dddmmss.length() - 4));
        double minutes = Double.valueOf(dddmmss.substring(dddmmss.length() - 4, dddmmss.length() - 2));
        double seconds = Double.valueOf(dddmmss.substring(dddmmss.length() - 2, dddmmss.length()));

        return degrees + (minutes / 60.0) + (seconds / 3600.0);
    }
}