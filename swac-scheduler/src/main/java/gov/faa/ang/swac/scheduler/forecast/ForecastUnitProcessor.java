package gov.faa.ang.swac.scheduler.forecast;

import gov.faa.ang.swac.common.datatypes.Timestamp;
import gov.faa.ang.swac.common.flightmodeling.ScheduleRecord;
import gov.faa.ang.swac.scheduler.forecast.UserClassDataSplitter.UserClass;
import gov.faa.ang.swac.scheduler.forecast.airport_data.CountryRegionHash;
import gov.faa.ang.swac.scheduler.forecast.airport_data.ForecastAirportCountsMap;
import gov.faa.ang.swac.scheduler.forecast.airport_data.ForecastAirportDataMerger;
import gov.faa.ang.swac.scheduler.forecast.airport_data.ForecastInternationalAirportData;
import gov.faa.ang.swac.scheduler.forecast.clone.ForecastCloner;
import gov.faa.ang.swac.scheduler.forecast.merge.ForecastFlightListMerger;
import gov.faa.ang.swac.scheduler.forecast.trip_distribution.ForecastTripDistAirportData;
import gov.faa.ang.swac.scheduler.forecast.trip_distribution.ForecastTripDistAprtProjGenerator;
import gov.faa.ang.swac.scheduler.forecast.trip_distribution.ForecastTripDistribution;

import java.util.Collections;
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
        List<ForecastTripDistAirportData> removedAirportList,       
        ForecastAirportCountsMap tafData,
        ForecastInternationalAirportData internationalData,
        CountryRegionHash countryRegionHash,
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
        ForecastAirportDataMerger.mergeInternationalData(
            retainedAirportList, 
            internationalData, 
            ForecastAirportDataMerger.DATA_TAF_FORECAST,
            countryRegionHash,
            userClass);
        
        // BUG? Robert Lakatos did not include this line:
        // Not necessary since this is included in ForecastTripDistribution.
        /*ForecastTripDistAprtProjGenerator.generateAirportProjections(
            retainedAirportList);*/
        
        ForecastTripDistribution tripDistributor =
            new ForecastTripDistribution(integerizationTolerance);
        tripDistributor.distributeTrips(
            retainedAirportList,
            fratarMaxSteps,
            fratarConvergenceCriteria);
       
        cloner.clearFlightLists();
        cloner.cloneFlights(retainedAirportList);
        nClonedFlights = cloner.getClonedFlightCount();
		
        // Merge the retained and removed flights
        List<ScheduleRecord> schedRecList = ForecastFlightListMerger.merge(
            retainedAirportList,
            cloner,
            removedAirportList);

        return schedRecList;
	}
	
	public int getNumClonedFlights() 
    {
		return nClonedFlights;
	}
	
}
