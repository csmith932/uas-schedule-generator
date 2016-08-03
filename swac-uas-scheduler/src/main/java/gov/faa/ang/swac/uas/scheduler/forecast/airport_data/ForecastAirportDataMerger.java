package gov.faa.ang.swac.uas.scheduler.forecast.airport_data;

import gov.faa.ang.swac.common.datatypes.REGION;
import gov.faa.ang.swac.uas.scheduler.forecast.UserClassDataSplitter;
import gov.faa.ang.swac.uas.scheduler.forecast.trip_distribution.ForecastTripDistAirportData;
import gov.faa.ang.swac.uas.scheduler.forecast.trip_distribution.ForecastTripDistAirportDataCount;

import java.util.List;

public class ForecastAirportDataMerger
{
    public final static int DATA_TAF_BASE = 1;
    public final static int DATA_TAF_FORECAST = 2;
    public final static int DATA_OPSNET = 3;
    
    private ForecastAirportDataMerger()
    {
    }

    public static void setInitialEtmsCountData(
        List<ForecastTripDistAirportData> list)
    {
        for(ForecastTripDistAirportData airportData : list)
        {
            airportData.calculateInitialEtmsCounts();
        }
    }
    
    public static void mergeCountDataIntoAirports(
        List<ForecastTripDistAirportData> airportList,
        ForecastAirportCountsMap countData,
        int dataType)
    {
        mergeCountDataIntoAirports(
            airportList,
            countData,
            dataType,
            null);
    }

    public static void mergeCountDataIntoAirports(
        List<ForecastTripDistAirportData> airportList,
        ForecastAirportCountsMap countData,
        int dataType, 
        UserClassDataSplitter.UserClass userClass)
    {
        boolean fallThroughToDefault = true;
        if (dataType == DATA_OPSNET)
        {
            fallThroughToDefault = false;
        }
        
        for(ForecastTripDistAirportData airport : airportList)
        {
            ForecastTripDistAirportDataCount counts = 
                    countData.getCounts(airport,fallThroughToDefault);                

            if (userClass != null)
            {
            	switch (userClass) 
                {
                    case GA:
                        counts = new ForecastTripDistAirportDataCount(counts.getNumGA(),0,0);
                        break;
                    case MIL:
                        counts = new ForecastTripDistAirportDataCount(0,counts.getNumMil(),0);
                        break;
                    case OTHER: 
                        counts = new ForecastTripDistAirportDataCount(0,0,counts.getNumOther());
                        break;
                    default :
                        // do nothing keep all counts	
                        break;
            	}
            }
            
            if (counts != null)
            {
                assign(airport,counts,dataType);
            }
        }
    }    
    
    private static void assign(
        ForecastTripDistAirportData airport,
        ForecastTripDistAirportDataCount counts, 
        int dataType)
    {
        switch(dataType)
        {
            case DATA_TAF_BASE:
                airport.setTafBase(counts);
                break;
            case DATA_TAF_FORECAST:
                airport.setTafForecast(counts);
                break;
            case DATA_OPSNET:
                airport.setOpsnetBase(counts);
                break;
            default:
                break;
        }
    }
}
