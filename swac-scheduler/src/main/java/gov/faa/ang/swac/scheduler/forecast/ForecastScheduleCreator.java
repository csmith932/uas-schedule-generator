/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.faa.ang.swac.scheduler.forecast;

import gov.faa.ang.swac.common.datatypes.Timestamp;
import gov.faa.ang.swac.common.flightmodeling.ScheduleRecord;
import gov.faa.ang.swac.scheduler.airport_data.AirportDataMap;
import gov.faa.ang.swac.scheduler.data.ASPMTaxiTimes;
import gov.faa.ang.swac.scheduler.flight_data.ScheduleRecordCloner;
import gov.faa.ang.swac.scheduler.forecast.airport_data.*;
import gov.faa.ang.swac.scheduler.forecast.clone.ForecastCloner;
import gov.faa.ang.swac.scheduler.forecast.trip_distribution.ForecastTripDistAirportData;
import gov.faa.ang.swac.scheduler.forecast.trip_distribution.ForecastTripDistributionDataLoader;
import gov.faa.ang.swac.scheduler.forecast.vfr.ForecastVfrSchedRecCreator;
import gov.faa.ang.swac.scheduler.mathematics.statistics.HQRandom;
import gov.faa.ang.swac.scheduler.vfr.VFRHelicopterMap;
import gov.faa.ang.swac.scheduler.vfr.VFRLocalTimeGenerator;
import java.util.List;
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
    private List<ASPMTaxiTimes.ASPMTaxiTimesRecord> aspmNominalTaxiTimesFile;
    private List<CountryRegionHash.CountryRegionRecord> internationalCountryRegionMapFile;
    private List<ForecastInternationalAirportData> internationalOpsCountsFile;
    private AirportDataMap mergedAirportDataFile;
    private List<ForecastAirportCountsRecord> opsnetFile;
    private List<ForecastAirportCountsRecordTaf> tafAopsFile;
    private VFRHelicopterMap vfrHelicopterPercentFile;
 
    // configuration:
    private double  cloneTimeShiftStDev;
    private double  integerizationTolerance; 
    private int     fratarMaxSteps;
    private double  fratarConvergenceCriteria;
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
    
    public List<ASPMTaxiTimes.ASPMTaxiTimesRecord> getAspmNominalTaxiTimesFile()
    {
        return this.aspmNominalTaxiTimesFile;
    }

    public void setAspmNominalTaxiTimesFile(List<ASPMTaxiTimes.ASPMTaxiTimesRecord> val)
    {
        this.aspmNominalTaxiTimesFile = val;
    }
    public List<CountryRegionHash.CountryRegionRecord> getInternationalCountryRegionMapFile()
    {
        return this.internationalCountryRegionMapFile;
    }

    public void setInternationalCountryRegionMapFile(List<CountryRegionHash.CountryRegionRecord> val)
    {
        this.internationalCountryRegionMapFile = val;
    }
    
    public List<ForecastInternationalAirportData> getInternationalOpsCountsFile()
    {
        return this.internationalOpsCountsFile;
    }

    public void setInternationalOpsCountsFile(List<ForecastInternationalAirportData> val)
    {
        this.internationalOpsCountsFile = val;
    }
    
    public AirportDataMap getMergedAirportDataFile()
    {
        return this.mergedAirportDataFile;
    }

    public void setMergedAirportDataFile(AirportDataMap val)
    {
        this.mergedAirportDataFile = val;
    }

    public List<ForecastAirportCountsRecord> getOpsnetFile()
    {
        return this.opsnetFile;
    }

    public void setOpsnetFile(List<ForecastAirportCountsRecord> val)
    {
        this.opsnetFile = val;
    }

    public List<ForecastAirportCountsRecordTaf> getTafAopsFile()
    {
        return this.tafAopsFile;
    }

    public void setTafAopsFile(List<ForecastAirportCountsRecordTaf> val)
    {
        this.tafAopsFile = val;
    }

    public VFRHelicopterMap getVfrHelicopterPercentFile()
    {
        return this.vfrHelicopterPercentFile;
    }

    public void setVfrHelicopterPercentFile(VFRHelicopterMap val)
    {
        this.vfrHelicopterPercentFile = val;
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

    public double getIntegerizationTolerance()
    {
        return this.integerizationTolerance;
    }

    public void setIntegerizationTolerance(double val)
    {
        this.integerizationTolerance = val;
    }

    public int getFratarMaxSteps()
    {
        return this.fratarMaxSteps;
    }

    public void setFratarMaxSteps(int val)
    {
        this.fratarMaxSteps = val;
    }

    public double getFratarConvergenceCriteria()
    {
        return this.fratarConvergenceCriteria;
    }

    public void setFratarConvergenceCriteria(double val)
    {
        this.fratarConvergenceCriteria = val;
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
        final String yyyymmdd       = Timestamp.toBonnDateOnlyString(startDate);
        final String yyyymm         = yyyymmdd.substring(0,6);
        
        Timestamp date = new Timestamp(startDate.getTime()).truncateToDay();
        Timestamp startTime         = date.hourAdd(this.numHoursFromGMT);// 0900 ZULU
        Timestamp endTime           = startTime.dayAdd(this.numDaysToForecast).secondAdd(-1.0);

        
        // Adapt inputs to local variables & objects ----------------------------------------------
        
        final AirportDataMap airportMap =
            mergedAirportDataFile;
        
        final ASPMTaxiTimes taxiTimes = new ASPMTaxiTimes(
            this.aspmNominalTaxiTimesFile,
            yyyymm);
        
        final CountryRegionHash countryRegionHash = new CountryRegionHash(
            this.internationalCountryRegionMapFile);
        
        final ForecastInternationalAirportDataAllYears allIntl = 
            new ForecastInternationalAirportDataAllYears();
        for (ForecastInternationalAirportData rec : this.internationalOpsCountsFile)
        {
            int year = rec.getYear();
            if (year == forecastFiscalYear || year == baseFiscalYear)
            {
                allIntl.setYearData(year,rec);
            }
        }
        
        final ForecastAirportCountsMap opsnetData = new ForecastAirportCountsMap(
            this.opsnetFile,
            yyyymmdd);
        
        final ForecastTafData allTafData = new ForecastTafData(
            this.tafAopsFile,
            forecastFiscalYear,
            baseFiscalYear);
        
        final VFRHelicopterMap helicopterMap =
            vfrHelicopterPercentFile;

        //-----------------------------------------------------------------------------------------
        // Build a list of objects which link the airports
        // to their respective arriving and departing flights
        
        List<ForecastTripDistAirportData> airportDataList = 
            ForecastTripDistributionDataLoader.load(
                this.inputScheduleFile,
                airportMap,
                startTime,
                endTime);

        //-----------------------------------------------------------------------------------------
 
        // Initialize random number generators
        
       
        
//        HQRandom temp = new HQRandom(12345678L); // this is the default but feel free to change it
//        
//        // For reproducibility, create 3 new streams with different entry
//        // points using the temp generator. Keep references if you want to
//        // manipulate the generators later (e.g., reset them to some particular
//        // state or query for their internal states)
//        
//        HQRandom generator1 = new HQRandom(temp.nextLong());
//        HQRandom generator2 = new HQRandom(temp.nextLong());
//        HQRandom generator3 = new HQRandom(temp.nextLong());
        
        
        HQRandom generator1 = new HQRandom(forecastClonerRandom);
        HQRandom generator2 = new HQRandom(scheduleClonerRandom);
        HQRandom generator3 = new HQRandom(vfrLocalTimeRandom);
        
        //-----------------------------------------------------------------------------------------

        VFRLocalTimeGenerator vfrLocalTimeGenerator = 
            new VFRLocalTimeGenerator(generator3);
        ForecastVfrSchedRecCreator vfrLoader = 
            new ForecastVfrSchedRecCreator(
                startTime,
                -1,
                -1,
                vfrLocalTimeGenerator);
        vfrLoader.setAirportMap(airportMap);
        vfrLoader.setTaxiTimes(taxiTimes);
        vfrLoader.setHelicopterMap(helicopterMap);
        logger.trace("created vfr loader");  

        ScheduleRecordCloner schedRecCloner = 
            new ScheduleRecordCloner(generator2);
        
        ForecastCloner forecastCloner = 
            new ForecastCloner(
                generator1,
                schedRecCloner);
        forecastCloner.setCloneTimeShiftStDev(
            cloneTimeShiftStDev);

        /*ForecastUnitProcessor unitProcessor = 
            new ForecastUnitProcessor(
                integerizationTolerance,
                fratarMaxSteps,
                fratarConvergenceCriteria);*/
        ForecastUnitProcessor unitProcessor = 
            new ForecastUnitProcessor(
                integerizationTolerance,
                fratarMaxSteps,
                fratarConvergenceCriteria);
        
        /*ForecastProcessor processor = new ForecastProcessor(
            airportDataList,
            allTafData,
            allIntl,
            opsnetData,
            countryRegionHash,
            vfrLoader,
            unitProcessor,
            forecastCloner);*/
        ForecastProcessor processor = new ForecastProcessor(
            airportDataList,
            allTafData,
            allIntl,
            opsnetData,
            countryRegionHash,
            forecastCloner,    
            vfrLoader,
            unitProcessor);
        this.outputScheduleFile = processor.process(
            baseFiscalYear,
            forecastFiscalYear,
            startTime,
            endTime);
    }
}
