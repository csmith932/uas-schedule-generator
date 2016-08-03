package gov.faa.ang.swac.uas.scheduler.forecast;

import gov.faa.ang.swac.common.flightmodeling.ScheduleRecord;
import gov.faa.ang.swac.uas.scheduler.forecast.UserClassDataSplitter.UserClass;
import gov.faa.ang.swac.uas.scheduler.forecast.airport_data.ForecastAirportCountsMap;
import gov.faa.ang.swac.uas.scheduler.forecast.airport_data.ForecastAirportDataMerger;
import gov.faa.ang.swac.uas.scheduler.forecast.airport_data.ForecastAirportDataPair;
import gov.faa.ang.swac.uas.scheduler.forecast.clone.ForecastCloner;
import gov.faa.ang.swac.uas.scheduler.forecast.merge.ForecastFlightListMerger;
import gov.faa.ang.swac.uas.scheduler.forecast.trip_distribution.ForecastTripDistAirportData;
import gov.faa.ang.swac.uas.scheduler.forecast.trip_distribution.ForecastTripDistAprtProjGenerator;

import java.util.List;

public class ForecastUnitProcessor
{   
    // Output
	private int     nClonedFlights = 0;    

    public List<ScheduleRecord> process(
        List<ForecastTripDistAirportData> retainedAirportList,
        ForecastAirportCountsMap tafData,
        ForecastCloner cloner,
        UserClass userClass) 
    {
        ForecastTripDistAprtProjGenerator.resetAirportProjections(
            retainedAirportList);        
        ForecastAirportDataMerger.mergeCountDataIntoAirports(
            retainedAirportList, 
            tafData,
            ForecastAirportDataMerger.DATA_TAF_FORECAST, 
            userClass);
        
        // BUG? Robert Lakatos did not include this line:
        // Not necessary since this is included in ForecastTripDistribution.
        /*ForecastTripDistAprtProjGenerator.generateAirportProjections(
            retainedAirportList);*/
        
        distributeTrips(retainedAirportList);
       
        cloner.clearFlightLists();
        cloner.cloneFlights(retainedAirportList);
        nClonedFlights = cloner.getClonedFlightCount();
		
        // Merge the retained and removed flights
        List<ScheduleRecord> schedRecList = ForecastFlightListMerger.merge(
            retainedAirportList,
            cloner);

        return schedRecList;
	}
	
	public int getNumClonedFlights() 
    {
		return nClonedFlights;
	}
	
	/**
     * Perform the Fratar trip distribution technique and the "integerization"
     * technique on the input set of airports.
     * @param aprts
     */
    private void distributeTrips(
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
