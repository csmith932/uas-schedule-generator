/**
 * Copyright "TBD", Metron Aviation & CSSI.  All rights reserved.
 * 
 * This computer Software was developed with the sponsorship of the U.S. Government
 * under Contract No. DTFAWA-10-D-00033, which has a copyright license in accordance with AMS 3.5-13.(c)(1).
 */

package gov.faa.ang.swac.common.flightmodeling.atmosphere;

/**
 * Formulas to find the pressure level or the altitude from the Standard Atmosphere
 * definitions.
 * 
 * @author James Bonn
 * @version 1.0 4/6/2007
 */
public class StandardAtmosphere
{
    private StandardAtmosphere()
    {
    }
    
    private static final double kmToFt = 3280.8399; // conversion from Km to Feet
    private static final double seaLevelPressure = 1013.25; // millibars pressure at Sea Level
    private static final double radiusEarth = 6366.7; // radius of the Earth in km
    private static final double GMR = 34.163195; // hydrostatic constant
    
    /**
     * Given the altitude in feet, find the pressure in millibars.
     * 
     * @param feet int value of altitude in feet
     * @return double value of pressure in millibars
     */
    public static double findPressure(int feet)
    {
        // find pressure in millibars given altitude in feet
        double alt = feet/kmToFt; // convert to km
        double h = alt*radiusEarth/(alt + radiusEarth);
        double tBase = 0;
        double pTab = 0;
        double tGrad = 0;
        double deltaH = 0;
        
        if (h < 11)
        { // under 11 km altitude
            tBase = 288.15;
            pTab = 1;
            tGrad = -6.5;
            deltaH = h;
        }
        else
        { // between 11 km and 20 km
            if (h >= 11 && h < 20)
            {
                tBase = 216.65;
                pTab = 0.2233611;
                tGrad = 0;
                deltaH = h - 11;
            }
            else
            { // really between 20 km and 32 km, but we don't go as high as 32 km
                tBase = 216.65;
                pTab = 0.05403295;
                tGrad = 1;
                deltaH = h - 20;
            }
        }
        double tLocal = tBase + tGrad*deltaH;
        double pressure = 0;
        if(tGrad == 0)
        {
            pressure = pTab*Math.exp(-GMR*deltaH/tBase);
        }
        else
        {
            pressure = pTab*Math.pow(tBase/tLocal,GMR/tGrad);
        }
        return pressure*seaLevelPressure;
    }
    
    /**
     * Given the pressure in millibars, find the altitude in feet
     * 
     * @param pressure double value of the pressure in millibars
     * @return int value of the altitude in feet
     */
    public static int findAltitude(double pressure)
    {
        // find altitude in feet given pressure in millibars
        double press = pressure/seaLevelPressure;
        double hTab = 0;
        double tBase = 0;
        double tGrad = 0;
        double pTab = 0;
        
        if(press > 0.2233611)
        { // bottom level, 0 < h < 11
            hTab = 0;
            tBase = 288.15;
            tGrad = -6.5;
            pTab = 1;
        }
        else
        {
            if(press > 0.05403295 && press <= 0.2233611)
            { // between 11 and 20 km
                hTab = 11;
                tBase = 216.65;
                tGrad = 0;
                pTab = 0.2233611;
            }
            else
            { // above 20 km
                hTab = 20;
                tBase = 216.65;
                tGrad = 1;
                pTab = 0.05403295;
            }
        }
        
        double h = 0;
        if(tGrad == 0)
        {
            h = hTab + tBase/GMR*Math.log(pTab/press);
        }
        else
        {
            h = hTab + tBase/tGrad*(Math.pow(pTab/press, tGrad/GMR) - 1);
        }
        h = h*radiusEarth/(radiusEarth - h);
        return (int) (h*kmToFt + .5); // added .5 to round instead of trunc
    }
}
