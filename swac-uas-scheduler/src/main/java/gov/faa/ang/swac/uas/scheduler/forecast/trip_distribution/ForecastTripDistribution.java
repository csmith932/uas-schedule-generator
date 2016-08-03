package gov.faa.ang.swac.uas.scheduler.forecast.trip_distribution;

import java.util.List;

/**
 * A Class that performs the Fratar trip distribution methods and the 
 * "integerization" technique.
 * 
 * @author James Bonn
 * @version 1.0
 */
public class ForecastTripDistribution
{
    private double integerizationTolerance;
    
    /**
     * Constructor.
     * @param integerizationTolerance
     */
    public ForecastTripDistribution(double integerizationTolerance)
    {
        this.integerizationTolerance = integerizationTolerance;
    }

    /**
     * Perform the Fratar trip distribution technique and the "integerization"
     * technique on the input set of airports.
     * @param aprts
     */
    public void distributeTrips(
        List<ForecastTripDistAirportData> airportList, 
        int fratarMaxSteps, 
        double fratarConvergenceCriteria)
    {
        ForecastTripDistAprtProjGenerator.generateAirportProjections(airportList);
 
        ForecastTripDistFratar fratar = new ForecastTripDistFratar(
            fratarMaxSteps, 
            fratarConvergenceCriteria);
        fratar.computeFratarCoefficients(airportList);
        
        ForecastTripDistIntegerizer integerizer = 
            new ForecastTripDistIntegerizer(integerizationTolerance);
        integerizer.integerize(airportList);
    }
}
