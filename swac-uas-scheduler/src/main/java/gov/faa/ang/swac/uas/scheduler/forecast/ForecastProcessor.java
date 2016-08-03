package gov.faa.ang.swac.uas.scheduler.forecast;

import gov.faa.ang.swac.common.datatypes.Timestamp;
import gov.faa.ang.swac.common.flightmodeling.ScheduleRecord;
import gov.faa.ang.swac.uas.scheduler.forecast.UserClassDataSplitter.UserClass;
import gov.faa.ang.swac.uas.scheduler.forecast.airport_data.*;
import gov.faa.ang.swac.uas.scheduler.forecast.clone.ForecastCloner;
import gov.faa.ang.swac.uas.scheduler.forecast.trip_distribution.ForecastTripDistAirportData;
import gov.faa.ang.swac.uas.scheduler.forecast.vfr.ForecastVfrSchedRecCreator;
import gov.faa.ang.swac.uas.scheduler.forecast.vfr.WindowedFrcstVfrSchedRecCreator;

import java.util.*;

public class ForecastProcessor
{
    private List<ForecastTripDistAirportData> airportDataList;

    private ForecastTafData tafData;
    private ForecastVfrSchedRecCreator vfrCreator;
    private ForecastCloner cloner;
    private ForecastUnitProcessor unitProcessor;   

    public ForecastProcessor(
        List<ForecastTripDistAirportData> airportDataList,
        ForecastTafData tafData,
        ForecastCloner cloner,       
        ForecastVfrSchedRecCreator vfrCreator,
        ForecastUnitProcessor unitProcessor)
    {
        this.airportDataList    = airportDataList;
        this.tafData            = tafData;
        this.cloner             = cloner;       
        this.vfrCreator         = vfrCreator;        
        this.unitProcessor      = unitProcessor;
    }

    public List<ScheduleRecord> process(
        int baseFiscalYear, 
        int forecastFiscalYear,
        Timestamp startTime, 
        Timestamp endTime)
    {
        // Compile the results in a list
        List<ScheduleRecord> resultList = new ArrayList<ScheduleRecord>();
        
    	// Split airports into three sub-networks (i.e., GA, MIL, Other)
    	// and forecast (FRATAR+Clone+VFR) each separately 
    	UserClassDataSplitter splitter = new UserClassDataSplitter();
    	splitter.split(airportDataList);
        
        // Process each user class: GA, MIL, OTHER --------------------------------------------------------------------
        for (UserClassDataSplitter.UserClass userClass : UserClassDataSplitter.UserClass.values())
        {
            // Get a copy of the airport list from the splitter 
            // corresponding to this user class
            
            List<ForecastTripDistAirportData> airportList = 
                splitter.getAirportList(userClass);
            
            // Set the baseline data in the airport data
            setBaselineData(
                airportList,
                baseFiscalYear,
                userClass);
            
            // Forecast this user class & append the resulting flights
            List<ScheduleRecord> subResultList = this.unitProcessor.process(
                airportList, 
                this.tafData.getYearData(forecastFiscalYear),
                this.cloner,
                userClass);
            resultList.addAll(subResultList); 
        }

        // Discard the user class splitter
        splitter.clear();
        splitter = null;
        
        // Setup baseline data for VFR --------------------------------------------------------------------------------
        
		setBaselineData(
            airportDataList,
            baseFiscalYear);
        
        // Discard airport data 
        for (ForecastTripDistAirportData data : airportDataList) 
        {
        	data.clear();
        }

        //-------------------------------------------------------------------------------------------------------------

        // Create VFR Flights 
        WindowedFrcstVfrSchedRecCreator windowedVfrCreator = 
            new WindowedFrcstVfrSchedRecCreator(
                vfrCreator,    
                tafData.getYearData(forecastFiscalYear),
                startTime,
                endTime);     
        windowedVfrCreator.createVfrFlights(
            airportDataList, 
            resultList);
        resultList.addAll(
            windowedVfrCreator.getFlights());
  
        return resultList;
    }

    private void setBaselineData(    
        List<ForecastTripDistAirportData> airportList,
        int baseFiscalYear,
        UserClass userClass) 
    {
    	ForecastAirportDataMerger.setInitialEtmsCountData(airportList);
        
    	ForecastAirportDataMerger.mergeCountDataIntoAirports(
            airportList, 
            tafData.getYearData(baseFiscalYear), 
            ForecastAirportDataMerger.DATA_TAF_BASE, 
            userClass);
    }
    
    private void setBaselineData(
        List<ForecastTripDistAirportData> airportList,
        int baseFiscalYear)
    {
    	ForecastAirportDataMerger.mergeCountDataIntoAirports(
            airportList, 
            tafData.getYearData(baseFiscalYear), 
            ForecastAirportDataMerger.DATA_TAF_BASE);
    }
}
