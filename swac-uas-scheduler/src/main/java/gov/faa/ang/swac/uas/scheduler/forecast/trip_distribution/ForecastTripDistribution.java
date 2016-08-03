package gov.faa.ang.swac.uas.scheduler.forecast.trip_distribution;

import java.util.Collection;
import java.util.List;

import gov.faa.ang.swac.uas.scheduler.forecast.airport_data.ForecastAirportDataPair;

/**
 * A Class that performs the Fratar trip distribution methods and the 
 * "integerization" technique.
 * 
 * @author James Bonn
 * @version 1.0
 */
public class ForecastTripDistribution
{
    /**
     * Perform the Fratar trip distribution technique and the "integerization"
     * technique on the input set of airports.
     * @param aprts
     */
    public void distributeTrips(
        List<ForecastTripDistAirportData> airportList)
    {
    	// TODO: CSS this step formerly translated per-airport dep and arr into per-airport-pair dep and arr. Our projections will already be per-pair (usually round robin) so it's moot, but make sure we stitch the data together properly
    	
        ForecastTripDistAprtProjGenerator.generateAirportProjections(airportList);
 
        for(ForecastTripDistAirportData aprt : airportList)
        {
        	for (ForecastAirportDataPair cp : aprt.getGoingTo()) {
        		// TODO: CSS This is accurate if and only if all flights are round robin. We should just get pairwise source data rather than per-airport projections
        		cp.setProjectedFlightCountFinal(aprt.getProjectedTotalArr());
        	}
        }
        
    }
}
