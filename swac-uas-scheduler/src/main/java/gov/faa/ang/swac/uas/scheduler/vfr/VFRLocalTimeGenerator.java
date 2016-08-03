package gov.faa.ang.swac.uas.scheduler.vfr;

import gov.faa.ang.swac.common.datatypes.Timestamp;
import gov.faa.ang.swac.uas.scheduler.mathematics.statistics.HQRandom;

/**
 * A Class that generates local VFR times based on a trapezoid distribution. The trapezoid
 * distribution was chosen after analysis of hourly TTAP data for VFR flights across all airports,
 * not by an airport by airport analysis.
 * 
 * @author James Bonn
 * @version 1.0
 */
public class VFRLocalTimeGenerator
{
    private double x1 = 5.5;
    private double x2 = 10.0;
    private double x3 = 16.0;
    private double x4 = 22.5;

    private double d21 = x2 - x1;
    private double d32 = x3 - x2;
    private double d43 = x4 - x3;
    private double denom = x4 + x3 - x2 - x1;

    private double cutOff1 = d21 / denom;
    private double area2 = 2.0 * d32 / denom;
    private double cutOff2 = cutOff1 + area2;

    private HQRandom hqRandomVFR = null;

    public VFRLocalTimeGenerator(HQRandom generator)
    {
        this.hqRandomVFR = generator;
    }

    /**
     * Create a random local time for VFR flights based on a trapezoid distribution.
     * 
     * @param localDate
     * @return a local time for a VFR flight
     */
    public Timestamp createLocalVfrTime(Timestamp localDate)
    {
        double randNum = hqRandomVFR.nextDouble();
        double hour = invertTrapezoidalPdf(randNum);

        Timestamp localTime = localDate.truncateToDay();

        localTime = localTime.hourAdd(hour);

        return localTime;
    }

    /**
     * Given a uniformly generated random number, return a random number from a trapezoid
     * distribution
     * 
     * @param randNum
     * @return a trapezoidally distributed random number
     */
    public double invertTrapezoidalPdf(double randNum)
    {
        // invert the pdf given by the trapezoid:
        // f(x) = ... 0 for x <= x1
        // [(x - x1)/(x2 - x1)]*[2/(x4 + x3 - x2 - x1)] for x1 < x <= x2 {triangle from x1 to x2}
        // 2/(x4 + x3 - x2 - x1) for x2 < x <= x3 {flat from x2 to x3}
        // [(x4 - x)/(x4 - x3)]*[2/(x4 + x3 - x2 - x1)] for x3 < x <= x4 {triangle from x3 to x4}
        // 0 x > x4
        //
        // The CDF is thus given by:
        // F(x) = ... 0 for x <= x1
        // [(x - x1)^2/(x2 - x1)]*[1/(x4 + x3 - x2 - x1)] for x1 < x <= x2
        // 2x/(x4 + x3 - x2 - x1) + 2(x2 - x1)/(x4 + x3 - x2 - x1) for x2 < x <= x3
        // 1 - [(x4 - x)^2/(x4 - x3)]*[1/(x4 + x3 - x2 - x1)] for x3 < x <= x4
        // 1 for x > x4
        //
        // Inverting the CDF, we get:
        // G(y) = ... 0 for y <= 0
        // x1 + sqrt[y*(x2 - x1)*(x4 + x3 - x2 - x1)] for 0 < y <= (x2 - x1)/(x4 + x3 - x2 - x1)
        // x2 + (x4 + x3 - x2 - x1)/2*(y - (x2 - x1)/(x4 + x3 - x2 - x1)) for (x2 - x1)/(x4 + x3 -
        // x2 - x1) < y < (2x3 - x2 - x1)/(x4 + x3 - x2 - x1)
        // x4 - sqrt[(1 - y)*(x4 - x3)*(x4 + x3 - x2 - x1)] for (2x3 - x2 - x1)/(x4 + x3 - x2 - x1)
        // < y < 1
        // 1 for y >= 1
        //

        double invTrap = 0;
        if (randNum >= 1)
        {
            invTrap = 1;
        }
        else
        {
            if (randNum > 0)
            {
                if (randNum < cutOff1)
                {
                    // first part of inverse cdf
                    invTrap = x1 + Math.sqrt(randNum * denom * d21);
                }
                else
                {
                    if (randNum < cutOff2)
                    {
                        // second part of inverse cdf
                        invTrap = d32 * (randNum - cutOff1) / area2 + x2;
                    }
                    else
                    {
                        // last part of inverse cdf
                        invTrap = x4 - Math.sqrt((1.0 - randNum) * denom * d43);
                    }
                }
            }
        }

        return invTrap;
    }

    /**
     * Set the x-axis values for the trapezoidal distribution.
     * 
     * @param x_1 the x value where the trapezoid distribution starts (the left side of the linearly
     *            increasing section)
     * @param x_2 the x value where the trapezoid distribution changes from linearly increasing to
     *            flat
     * @param x_3 the x value where the trapezoid distribution changes from flat to linearly
     *            decreasing
     * @param x_4 the x value where the trapezoidal distribution ends (the right side of the
     *            linearly descreasing section)
     * @return true if the input data is good (ordered x_1 < x_2 < x_3 < x_4), false otherwise
     */
    public boolean setTrapezoidalVars(double x_1, double x_2, double x_3, double x_4)
    {
        if ((x_4 > x_3) && (x_3 > x_2) && (x_2 > x_1))
        {
            x1 = x_1;
            x2 = x_2;
            x3 = x_3;
            x4 = x_4;

            d21 = x2 - x1;
            d32 = x3 - x2;
            d43 = x4 - x3;

            denom = x4 + x3 - x2 - x1;

            cutOff1 = d21 / denom;
            area2 = 2.0 * d32 / denom;
            cutOff2 = cutOff1 + area2;

            return true;
        }

        return false;
    }
}
