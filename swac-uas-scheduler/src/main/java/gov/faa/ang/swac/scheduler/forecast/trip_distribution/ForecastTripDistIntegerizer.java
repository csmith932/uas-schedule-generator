package gov.faa.ang.swac.scheduler.forecast.trip_distribution;


import gov.faa.ang.swac.scheduler.forecast.airport_data.ForecastAirportDataPair;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 * A Class that performs the "integerization" of the results of the 
 * Fratar trip distribution technique.  
 * <P>
 * The results of Fratar are that each city pairs has a number of flights, but
 * that number can be (almost always will be) a decimal that is not an integer.
 * The schedules need an integer number of flights.  This class finds the
 * citypairs where it is appropriate to stay with the rounded down number of
 * flights and those where it is appropriate to round up the number of flights.
 * <P>
 * The {@link #setAprtThreshold} method will set a threshold value that
 * determines how far over the airport projected total that an airport can go
 * before not allowing any more flights to be added.
 * 
 * @author James Bonn
 * @version 1.0
 */
public class ForecastTripDistIntegerizer
{
    private double aprtThreshold;

    /**
     * Default Constructor.
     */
    public ForecastTripDistIntegerizer()
    {
        aprtThreshold = 0.5;
    }

    /**
     * Constructor where the threshold for determining whether or not
     * a new airport can be added is set.
     * @param aprtThreshold
     */
    public ForecastTripDistIntegerizer(double aprtThreshold)
    {
        this.aprtThreshold = aprtThreshold;
    }

    /**
     * @param aprts
     */
    public void integerize(List<ForecastTripDistAirportData> aprts)
    {
        ArrayList<ForecastAirportDataPair> cps = 
            createCityPairList(aprts);
        
        setInitialIntegerData(cps);
        
        ListIterator<ForecastAirportDataPair> cpIter = 
            cps.listIterator();
        ForecastAirportDataPair cp = null;
        
        while(cpIter.hasNext())
        {
            cp = cpIter.next();
            addFlight(cp);
        }
    }
    
    private boolean addFlight(ForecastAirportDataPair cp)
    {
        boolean added = false;
        ForecastTripDistAirportData deptAprt = null;
        ForecastTripDistAirportData arrAprt = null;
        
        deptAprt = (ForecastTripDistAirportData) cp.getOrigin();
        arrAprt = (ForecastTripDistAirportData) cp.getDestination();
        
        if(cp.getRemainderProjectedRaw() > 0)
        {
            if((deptAprt.getCalcIntDep() < 
                (deptAprt.getCalculatedTotalDep() - aprtThreshold)) &&
                (arrAprt.getCalcIntArr() <
                (arrAprt.getCalculatedTotalArr() - aprtThreshold)))
            {
                // add flight
                cp.setProjectedFlightCountFinal(
                    cp.getProjectedFlightCountFinal() + 1);
                deptAprt.setCalcIntDep(
                    deptAprt.getCalcIntDep() + 1);
                arrAprt.setCalcIntArr(
                    arrAprt.getCalcIntArr() + 1);
                added = true;
            }
        }
        
        return added;
    }
    
    private void setInitialIntegerData(ArrayList<ForecastAirportDataPair> cps)
    {
        ListIterator<ForecastAirportDataPair> cpIter = 
            cps.listIterator();
        ForecastAirportDataPair cp = null;
        
        ForecastTripDistAirportData deptAprt = null;
        ForecastTripDistAirportData arrAprt = null;
        
        while(cpIter.hasNext())
        {
            cp = cpIter.next();
            deptAprt = (ForecastTripDistAirportData) cp.getOrigin();
            arrAprt = (ForecastTripDistAirportData) cp.getDestination();
            
            // raw calculated values from Fratar
            cp.setProjectedFlightCountRaw(
                cp.getInitialFlightCount() * 
                deptAprt.getNewDepCoeff() * arrAprt.getNewArrCoeff());
            deptAprt.setCalculatedTotalDep(
                deptAprt.getCalculatedTotalDep() + 
                cp.getProjectedFlightCountRaw());
            arrAprt.setCalculatedTotalArr(
                arrAprt.getCalculatedTotalArr() +
                cp.getProjectedFlightCountRaw());
            
            // initial integer values
            cp.setProjectedFlightCountFinal(
                Math.max(1,Math.floor(cp.getProjectedFlightCountRaw())));
            deptAprt.setCalcIntDep(
                deptAprt.getCalcIntDep() + 
                (int) cp.getProjectedFlightCountFinal());
            arrAprt.setCalcIntArr(
                arrAprt.getCalcIntArr() + 
                (int) cp.getProjectedFlightCountFinal());
        }
    }

    /**
     * Given an airports list, create a full list of citypairs from those
     * airports.
     * @param aprts
     * @return a list of {@link ForecastCityPairAirports} of all citypairs
     * from the closed system of airports
     */
    public ArrayList<ForecastAirportDataPair> createCityPairList(
        List<ForecastTripDistAirportData> aprts)
    {
        ArrayList<ForecastAirportDataPair> cps = 
            new ArrayList<ForecastAirportDataPair>();
        
        for(ForecastTripDistAirportData deptAprt : aprts)
        {
            cps.addAll(deptAprt.getComingFrom());
        }
        
        return cps;
    }

    /**
     * Set the threshold for determining whether a new flight can be added
     * at an airport (default is 0.5).
     * @param aprtThreshold
     */
    public void setAprtThreshold(double aprtThreshold)
    {
        this.aprtThreshold = aprtThreshold;
    }

    /**
     * @return the threshold for determining whether a new flight can be added
     * at an airport
     */
    public double getAprtThreshold()
    {
        return aprtThreshold;
    }
}
