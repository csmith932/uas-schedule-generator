package gov.faa.ang.swac.uas.scheduler.forecast.trip_distribution;

import java.util.List;

/**
 * A Class with static methods to reset and to generate the airport by
 * airport projected departure and arrival values.
 * 
 * @author James Bonn
 * @version 1.0
 */
public class ForecastTripDistAprtProjGenerator
{
    private ForecastTripDistAprtProjGenerator()
    {
    }

    /**
     * For each airport in the list, calculate their projected
     * departure and arrival goals.
     * @param aprts
     */
    public static void generateAirportProjections(
        List<ForecastTripDistAirportData> aprts)
    {
        ForecastTripDistAirportDataCount tafBase = null;
        ForecastTripDistAirportDataCount tafProj = null;
        ForecastTripDistAirportDataCount etmsDep = null;
        ForecastTripDistAirportDataCount etmsArr = null;
        
        double etmsDepProj = 0;
        double etmsArrProj = 0;
        double [] typeProj = null;
        
        for(ForecastTripDistAirportData aprt : aprts)
        {
            etmsDep = aprt.getEtmsDep();
            etmsArr = aprt.getEtmsArr();
            tafBase = aprt.getTafBase();
            tafProj = aprt.getTafForecast();
            
            aprt.setInitialTotalDep(etmsDep.getTotal());
            aprt.setInitialTotalArr(etmsArr.getTotal());
            
            etmsDepProj = 0;
            etmsArrProj = 0;
            
	        typeProj = generateOneProjection(etmsDep.getNumGA(),
	                etmsArr.getNumGA(), tafBase.getNumGA(),
	                tafProj.getNumGA());            	

            etmsDepProj += typeProj[0];
            etmsArrProj += typeProj[1];
            
            typeProj = generateOneProjection(etmsDep.getNumMil(),
                            etmsArr.getNumMil(), tafBase.getNumMil(),
                            tafProj.getNumMil());
            etmsDepProj += typeProj[0];
            etmsArrProj += typeProj[1];

            typeProj = generateOneProjection(etmsDep.getNumOther(),
                            etmsArr.getNumOther(), tafBase.getNumOther(),
                            tafProj.getNumOther());
         
            etmsDepProj += typeProj[0];
            etmsArrProj += typeProj[1];

            aprt.setProjectedTotalDep(etmsDepProj);
            aprt.setProjectedTotalArr(etmsArrProj);

        }
    }
    
    private static double [] generateOneProjection(double etmsDepCount,
        double etmsArrCount, double tafBase, double tafProj)
    {
    	
        double [] etmsProj = {etmsDepCount, etmsArrCount};
        
        if(tafBase > 0 && tafProj > 0)
        {
            double ave = (etmsDepCount + etmsArrCount)/2.0;
            double diff = (etmsDepCount - etmsArrCount)/2.0;
            
            ave *= tafProj/tafBase;
            
            //dep
            etmsProj[0] = ave + diff;
            //arr
            etmsProj[1] = ave - diff;
          
        }
        
    
        return etmsProj;
    }

    /**
     * For each airport in the list, reset the airport projections and
     * trip distribution calculations to their defaults.
     * @param aprts
     */
    public static void resetAirportProjections(
        List<ForecastTripDistAirportData> airportList)
    {
        for(ForecastTripDistAirportData airport : airportList)
        {
            airport.resetProjections();
        }
    }
}
