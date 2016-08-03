package gov.faa.ang.swac.scheduler.forecast.trip_distribution;

import gov.faa.ang.swac.scheduler.forecast.airport_data.ForecastAirportDataPair;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * A Class that performs the Fratar trip distribution technique.  The process
 * iterates until the given convergence criteria is satisfied or until the given
 * maximum number of steps is reached.
 * 
 * @author James Bonn
 * @version 1.0
 */
public class ForecastTripDistFratar
{
    private int maxSteps;
    private double convergenceTolerance;
    
    /**
     * Constructor that sets the maximum number of steps and the convergence
     * criteria.
     * @param maxSteps
     * @param convergenceTolerance
     */
    public ForecastTripDistFratar(int maxSteps, double convergenceTolerance)
    {
        this.maxSteps = maxSteps;
        this.convergenceTolerance = convergenceTolerance;
    }

    /**
     * Perform the Fratar trip distribution technique and calculate the
     * airport by airport departure and arrival coefficients.
     * @param aprts
     * @return the number of steps performed
     */
    public int computeFratarCoefficients(
        List<ForecastTripDistAirportData> aprts)
    {
        int numSteps = 0;
        
        boolean stopIterating = false;
        double coeff = 0;
        
        while(!stopIterating)
        {
            numSteps++;
            
            // compute departure coefficients
            for(ForecastTripDistAirportData aprt : aprts)
            {

                coeff = computeFratarDepCoeff(aprt);
                aprt.setOldDepCoeff(aprt.getNewDepCoeff());
                aprt.setNewDepCoeff(coeff);

            }
            
            // compute arrival coefficients
            for(ForecastTripDistAirportData aprt : aprts)
            {

            	coeff = computeFratarArrCoeff(aprt);
                aprt.setOldArrCoeff(aprt.getNewArrCoeff());
                aprt.setNewArrCoeff(coeff);

 
            }
            
            stopIterating = stopIterating(aprts, numSteps);
        }
        
        return numSteps;
    }
    
    private double computeFratarDepCoeff(ForecastTripDistAirportData deptAprt)
    {
        double deptCoeff = deptAprt.getNewDepCoeff();
        
        Collection<ForecastAirportDataPair> cps = deptAprt.getGoingTo();
        ForecastTripDistAirportData arrAprt = null;
        
        double sum = 0;
        for(ForecastAirportDataPair cp : cps)
        {
            arrAprt = (ForecastTripDistAirportData) cp.getDestination();
            sum += cp.getInitialFlightCount()*arrAprt.getNewArrCoeff();

        }
        
        if(sum > 0)
        {
            deptCoeff = deptAprt.getProjectedTotalDep()/sum;
        }
        
        return deptCoeff;
    }
    
    private double computeFratarArrCoeff(ForecastTripDistAirportData arrAprt)
    {
        double arrCoeff = arrAprt.getNewArrCoeff();
        
        Collection<ForecastAirportDataPair> cps = arrAprt.getComingFrom();
        ForecastTripDistAirportData deptAprt = null;
        
        double sum = 0;
        for(ForecastAirportDataPair cp : cps)
        {
            deptAprt = (ForecastTripDistAirportData) cp.getOrigin();
            sum += cp.getInitialFlightCount()*deptAprt.getNewDepCoeff();
           
        }
        
        if(sum > 0)
        {
            arrCoeff = arrAprt.getProjectedTotalArr()/sum;

        }
        
        return arrCoeff;
    }
    
    private boolean stopIterating(List<ForecastTripDistAirportData> aprts,
        int stepNum)
    {
        boolean stopIterating = false;
        
        if(stepNum >= maxSteps)
        {
            stopIterating = true;
        }
        else
        {
            double maxLinkStrengthChange = 0;
            double oldLinkStrength = 0;
            double newLinkStrength = 0;
            
            ForecastTripDistAirportData deptAprt = null;
            ForecastTripDistAirportData arrAprt = null;

            Collection<ForecastAirportDataPair> cps = null;
            ForecastAirportDataPair cp = null;
            
            ListIterator<ForecastTripDistAirportData> aprtIter = 
                aprts.listIterator();
            Iterator<ForecastAirportDataPair> cpIter = null;
            
            boolean possiblyConverged = true;
            
            while(possiblyConverged && aprtIter.hasNext())
            {
                deptAprt = aprtIter.next();
                
                cps = deptAprt.getGoingTo();
                cpIter = cps.iterator();
                
                while(possiblyConverged && cpIter.hasNext())
                {
                    cp = cpIter.next();
                    
                    arrAprt = (ForecastTripDistAirportData) cp.getDestination();
                    
                    oldLinkStrength = deptAprt.getOldDepCoeff()*
                        arrAprt.getOldArrCoeff();
                    newLinkStrength = deptAprt.getNewDepCoeff()*
                        arrAprt.getNewArrCoeff();
                    if(oldLinkStrength > 0)
                    {
                        maxLinkStrengthChange = Math.max(maxLinkStrengthChange,
                            Math.abs((oldLinkStrength - newLinkStrength)/oldLinkStrength));
                    }
                    
                    if(maxLinkStrengthChange > convergenceTolerance)
                    {
                        possiblyConverged = false;
                    }
                }
            }
            
            stopIterating = possiblyConverged;
        }
        
        return stopIterating;
    }

    /**
     * Set the maximum number of Fratar steps allowed.
     * @param maxSteps
     */
    public void setMaxSteps(int maxSteps)
    {
        this.maxSteps = maxSteps;
    }

    /**
     * @return the maximum number of Fratar steps allowed
     */
    public int getMaxSteps()
    {
        return maxSteps;
    }

    /**
     * Set the convergence criteria value.
     * @param convergenceTolerance
     */
    public void setConvergenceTolerance(double convergenceTolerance)
    {
        this.convergenceTolerance = convergenceTolerance;
    }

    /**
     * @return the convergence criteria value
     */
    public double getConvergenceTolerance()
    {
        return convergenceTolerance;
    }
}
