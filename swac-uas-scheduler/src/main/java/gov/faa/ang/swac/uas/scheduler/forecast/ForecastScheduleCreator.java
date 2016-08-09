/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.faa.ang.swac.uas.scheduler.forecast;

import gov.faa.ang.swac.common.datatypes.Timestamp;
import gov.faa.ang.swac.common.flightmodeling.ScheduleRecord;
import gov.faa.ang.swac.uas.scheduler.airport_data.AirportData;
import gov.faa.ang.swac.uas.scheduler.airport_data.AirportDataMap;
import gov.faa.ang.swac.uas.scheduler.airport_data.AirportDataPair;
import gov.faa.ang.swac.uas.scheduler.flight_data.ScheduleRecordCloner;
import gov.faa.ang.swac.uas.scheduler.forecast.airport_data.*;
import gov.faa.ang.swac.uas.scheduler.forecast.clone.ForecastCloner;
import gov.faa.ang.swac.uas.scheduler.forecast.trip_distribution.ForecastTripDistributionDataLoader;
import gov.faa.ang.swac.uas.scheduler.forecast.trip_distribution.ForecastTripDistAirportDataCount.MissionType;
import gov.faa.ang.swac.uas.scheduler.mathematics.statistics.HQRandom;
import gov.faa.ang.swac.uas.scheduler.utils.DateUtils;
import gov.faa.ang.swac.uas.scheduler.vfr.VFRLocalTimeGenerator;
import gov.faa.ang.swac.uas.scheduler.vfr.VfrSchedRecCreator;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 *
 * @author chall
 */
public class ForecastScheduleCreator 
{
    private static final Logger logger = LogManager.getLogger(ForecastScheduleCreator.class);
    
    /* configurable via scenario.xml and scenario.properties so 
     * that they can be varied using Monte Carlo.
     */
    public long forecastClonerRandom;
    public long scheduleClonerRandom;
    public long vfrLocalTimeRandom;
    
	public long getForecastClonerRandom() {
		return forecastClonerRandom;
	}

	public void setForecastClonerRandom(long forecastClonerRandom) {
		this.forecastClonerRandom = forecastClonerRandom;
	}

	public long getScheduleClonerRandom() {
		return scheduleClonerRandom;
	}

	public void setScheduleClonerRandom(long scheduleClonerRandom) {
		this.scheduleClonerRandom = scheduleClonerRandom;
	}

	public long getVfrLocalTimeRandom() {
		return vfrLocalTimeRandom;
	}

	public void setVfrLocalTimeRandom(long vfrLocalTimeRandom) {
		this.vfrLocalTimeRandom = vfrLocalTimeRandom;
	}

	// inputData:
    private List<ScheduleRecord> inputScheduleFile;
    private AirportDataMap mergedAirportDataFile;
    private List<ForecastAirportCountsRecord> tafAopsFile;
 
    // configuration:
    private double  cloneTimeShiftStDev;
    private int     numHoursFromGMT;
    private int     numDaysToForecast;
    
    // outputData:
    private List<ScheduleRecord> outputScheduleFile;
    
    public List<ScheduleRecord> getInputScheduleFile()
    {
        return this.inputScheduleFile;
    }
    
    public void setInputScheduleFile(List<ScheduleRecord> val)
    {
        this.inputScheduleFile = val;
    }
    
    public AirportDataMap getMergedAirportDataFile()
    {
        return this.mergedAirportDataFile;
    }

    public void setMergedAirportDataFile(AirportDataMap val)
    {
        this.mergedAirportDataFile = val;
    }

    public List<ForecastAirportCountsRecord> getTafAopsFile()
    {
        return this.tafAopsFile;
    }

    public void setTafAopsFile(List<ForecastAirportCountsRecord> val)
    {
        this.tafAopsFile = val;
    }

    public List<ScheduleRecord> getOutputScheduleFile()
    {
        return this.outputScheduleFile;
    }
    public void setOutputScheduleFile(List<ScheduleRecord> val)
    {
        this.outputScheduleFile = val;
    }
   
    public double getCloneTimeShiftStDev()
    {
        return this.cloneTimeShiftStDev;
    }
    public void setCloneTimeShiftStDev(double val)
    {
        this.cloneTimeShiftStDev = val;
    }

    public int getNumHoursFromGMT()
    {
        return this.numHoursFromGMT;
    }

    public void setNumHoursFromGMT(int val)
    {
        this.numHoursFromGMT = val;
    }

    public int getNumDaysToForecast()
    {
        return this.numDaysToForecast;
    }

    public void setNumDaysToForecast(int val)
    {
        this.numDaysToForecast = val;
    }

    public void run(
        final Timestamp startDate, 
        final int forecastFiscalYear)
    {
        // Adjust and format input dates ----------------------------------------------------------
                
        final int baseFiscalYear    = startDate.getFiscalYear();
        
        Timestamp date = new Timestamp(startDate.getTime()).truncateToDay();
        Timestamp startTime         = date.hourAdd(this.numHoursFromGMT);// 0900 ZULU
        Timestamp endTime           = startTime.dayAdd(this.numDaysToForecast).secondAdd(-1.0);

        
        // Adapt inputs to local variables & objects ----------------------------------------------
        
        final Map<MissionAirportPairKey,Integer> allTafData = ForecastTafData.forecastTafData(
            this.tafAopsFile,
            forecastFiscalYear,
            DateUtils.getQuarter(startDate),
            this.mergedAirportDataFile);
        
        //-----------------------------------------------------------------------------------------
        // Build a list of objects which link the airports
        // to their respective arriving and departing flights
        
        Map<MissionAirportPairKey,List<ScheduleRecord>> scheduleMap = 
            ForecastTripDistributionDataLoader.load(
                this.inputScheduleFile,
                mergedAirportDataFile,
                startTime,
                endTime);

        //-----------------------------------------------------------------------------------------
 
        // Initialize random number generators
        
        HQRandom generator1 = new HQRandom(forecastClonerRandom);
        HQRandom generator2 = new HQRandom(scheduleClonerRandom);
        HQRandom generator3 = new HQRandom(vfrLocalTimeRandom);
        
        //-----------------------------------------------------------------------------------------

        VFRLocalTimeGenerator vfrLocalTimeGenerator = 
            new VFRLocalTimeGenerator(generator3);
        VfrSchedRecCreator vfrLoader = 
            new VfrSchedRecCreator(
                startTime,
                -1,
                -1,
                vfrLocalTimeGenerator);
        logger.trace("created vfr loader");  

        ScheduleRecordCloner schedRecCloner = 
            new ScheduleRecordCloner(generator2);
        
        ForecastCloner forecastCloner = 
            new ForecastCloner(
                generator1,
                schedRecCloner);
        forecastCloner.setCloneTimeShiftStDev(
            cloneTimeShiftStDev);

        ForecastProcessor processor = new ForecastProcessor(
        	scheduleMap,
            allTafData,
            forecastCloner,    
            vfrLoader);
        this.outputScheduleFile = processor.process(
            baseFiscalYear,
            forecastFiscalYear,
            startTime,
            endTime);
    }
}
