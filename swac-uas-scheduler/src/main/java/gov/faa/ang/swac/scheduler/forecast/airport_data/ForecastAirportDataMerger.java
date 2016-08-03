package gov.faa.ang.swac.scheduler.forecast.airport_data;

import gov.faa.ang.swac.common.datatypes.REGION;
import gov.faa.ang.swac.scheduler.forecast.UserClassDataSplitter;
import gov.faa.ang.swac.scheduler.forecast.trip_distribution.ForecastTripDistAirportData;
import gov.faa.ang.swac.scheduler.forecast.trip_distribution.ForecastTripDistAirportDataCount;
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
    
    public static void mergeInternationalData(
        List<ForecastTripDistAirportData> airportList,
        ForecastInternationalAirportData data,
        int dataType, 
        CountryRegionHash countryRegionHash)
    {
        mergeInternationalData(
            airportList,
            data,
            dataType, 
            countryRegionHash,
            null);
    }

    public static void mergeInternationalData(
        List<ForecastTripDistAirportData> airportList,
        ForecastInternationalAirportData data,
        int dataType, 
        CountryRegionHash countryRegionHash,
        UserClassDataSplitter.UserClass userClass)
    {
        for(ForecastTripDistAirportData airport : airportList)
        {
        	REGION region = countryRegionHash.getRegion(airport);
            
            Double count = data.getRegionCount(region);
            if (count != null)
            {
                ForecastTripDistAirportDataCount counts = null;
                if (userClass == null)
                {
                   counts = new ForecastTripDistAirportDataCount(count,count,count); 
                }
                else
                {
                    switch (userClass) 
                    {
                        case GA:
                            counts = new ForecastTripDistAirportDataCount(count,0,0);
                            break;
                            
                        case MIL:
                            counts = new ForecastTripDistAirportDataCount(0,count,0);
                            break;
                            
                        case OTHER: 
                            counts = new ForecastTripDistAirportDataCount(0,0,count);
                            break;
                        default : 
                        break;
                    }
                }
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