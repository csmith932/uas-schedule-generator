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
    private ForecastAirportCountsMap opsnetData;   
    private ForecastVfrSchedRecCreator vfrCreator;
    private ForecastCloner cloner;
    private ForecastUnitProcessor unitProcessor;   

    /**
     * Sorting airports by the number of ETMS operations.
     * Sort order is DECREASING number of operations
     */
    private class CustomSortAirportOperations 
        implements Comparator<ForecastTripDistAirportData> 
    {
        @Override
    	public int compare(
           ForecastTripDistAirportData apt1, 
           ForecastTripDistAirportData apt2) 
        {
    		double nOps1 = 
                apt1.getEtmsDep().getTotal()
                +apt1.getEtmsArr().getTotal();
    		double nOps2 = 
                apt2.getEtmsDep().getTotal()
                +apt2.getEtmsArr().getTotal();

            // To do DECREASING order, 
            // return -1 if already decreasing,
            // return +1 if increasing, etc
    		if(nOps2 < nOps1) 
            {
    			return -1;
    		} 
            else if  (nOps1 < nOps2) 
            {
    			return 1;
    		} 
            else 
            {
    			return 0;
    		}
    	}
    }

    public ForecastProcessor(
        List<ForecastTripDistAirportData> airportDataList,
        ForecastTafData tafData,
        ForecastAirportCountsMap opsnetData,
        ForecastCloner cloner,       
        ForecastVfrSchedRecCreator vfrCreator,
        ForecastUnitProcessor unitProcessor)
    {
        this.airportDataList    = airportDataList;
        
        this.tafData            = tafData;
        this.opsnetData         = opsnetData;
        
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
        
        /*
        // Construct enumerated map to the INCLUDED airports
        Map<UserClassDataSplitter.UserClass,List<ForecastTripDistAirportData>> airportListMap = 
            new EnumMap<UserClassDataSplitter.UserClass,List<ForecastTripDistAirportData>>(
                UserClassDataSplitter.UserClass.class);
        
        // Construct enumerated map to the REMOVED airports
        Map<UserClassDataSplitter.UserClass,List<ForecastTripDistAirportData>> removedAirportListMap = 
            new EnumMap<UserClassDataSplitter.UserClass,List<ForecastTripDistAirportData>>(
                UserClassDataSplitter.UserClass.class);
        */
        
        // Process each user class: GA, MIL, OTHER --------------------------------------------------------------------
        for (UserClassDataSplitter.UserClass userClass : UserClassDataSplitter.UserClass.values())
        {
            // Get a copy of the airport list from the splitter 
            // corresponding to this user class
            /*
            List<ForecastTripDistAirportData> airportList = 
                airportListMap.put(
                    userClass,
                    splitter.getAirportList(userClass));
            
            // Remove sinks & sources from the airport network
            List<ForecastTripDistAirportData> removedAirportList = 
                removedAirportListMap.put(
                    userClass,
                    ForecastTripDistAirportRemover.removeSinksAndSources(airportList));
            */
            
            List<ForecastTripDistAirportData> airportList = 
                splitter.getAirportList(userClass);
            
            // Set the baseline data in the airport data
            setBaselineData(
                airportList,
                baseFiscalYear,
                userClass);
            
            // Sort airports in DESCENDING order of total operations
            // to help the integerization process.
            Collections.sort(
                airportList,
                new CustomSortAirportOperations());
            
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
        
        // Robert Lakatos did not include OPSNET data for user class breakdown
        /*ForecastAirportDataMerger.mergeCountDataIntoAirports(
            airportList,
            opsnetData,
            ForecastAirportDataMerger.DATA_OPSNET, 
            userClass);*/

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
        // BUG? Robert Lakatos did not include this line:
        //ForecastAirportDataMerger.setInitialEtmsCountData(airportList);
        
        ForecastAirportDataMerger.mergeCountDataIntoAirports(
            airportList,
            opsnetData,
            ForecastAirportDataMerger.DATA_OPSNET);
        
    	ForecastAirportDataMerger.mergeCountDataIntoAirports(
            airportList, 
            tafData.getYearData(baseFiscalYear), 
            ForecastAirportDataMerger.DATA_TAF_BASE);
    }
}
