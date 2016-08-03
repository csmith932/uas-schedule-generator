package gov.faa.ang.swac.uas.scheduler.forecast;

import gov.faa.ang.swac.common.flightmodeling.ScheduleRecord;
import gov.faa.ang.swac.uas.scheduler.forecast.UserClassDataSplitter.UserClass;
import gov.faa.ang.swac.uas.scheduler.forecast.airport_data.ForecastAirportCountsMap;
import gov.faa.ang.swac.uas.scheduler.forecast.airport_data.ForecastAirportDataMerger;
import gov.faa.ang.swac.uas.scheduler.forecast.clone.ForecastCloner;
import gov.faa.ang.swac.uas.scheduler.forecast.merge.ForecastFlightListMerger;
import gov.faa.ang.swac.uas.scheduler.forecast.trip_distribution.ForecastTripDistAirportData;
import gov.faa.ang.swac.uas.scheduler.forecast.trip_distribution.ForecastTripDistAprtProjGenerator;
import gov.faa.ang.swac.uas.scheduler.forecast.trip_distribution.ForecastTripDistribution;

import java.util.List;

public class ForecastUnitProcessor
{   
    // Input
    private double  integerizationTolerance;
    private int     fratarMaxSteps;
    private double  fratarConvergenceCriteria;   
    
    // Output
	private int     nClonedFlights;    

    public ForecastUnitProcessor(
        double integerizationTolerance,
        int fratarMaxSteps,
        double fratarConvergenceCriteria)
    {
        this.integerizationTolerance    = integerizationTolerance;
        this.fratarMaxSteps             = fratarMaxSteps;
        this.fratarConvergenceCriteria  = fratarConvergenceCriteria;
        
        this.nClonedFlights             = 0;     
    }

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
        
        ForecastTripDistribution tripDistributor =
            new ForecastTripDistribution();
        tripDistributor.distributeTrips(retainedAirportList);
       
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
	
}
