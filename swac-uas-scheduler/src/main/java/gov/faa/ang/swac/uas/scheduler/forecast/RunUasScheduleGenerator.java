/**
 * Copyright "TBD", Metron Aviation & CSSI. All rights reserved.
 *
 * This computer Software was developed with the sponsorship of the U.S.
 * Government under Contract No. DTFAWA-10-D-00033, which has a copyright
 * license in accordance with AMS 3.5-13.(c)(1).
 */
package gov.faa.ang.swac.uas.scheduler.forecast;

import java.util.ArrayList;
import java.util.List;

import gov.faa.ang.swac.common.flightmodeling.FlightPlan;
import gov.faa.ang.swac.common.flightmodeling.ScheduleRecord;
import gov.faa.ang.swac.controller.ExitException;
import gov.faa.ang.swac.controller.core.CloneableAbstractTask;
import gov.faa.ang.swac.datalayer.DataAccessException;
import gov.faa.ang.swac.datalayer.storage.DataMarshaller;
import gov.faa.ang.swac.uas.scheduler.airport_data.AirportDataMap;
import gov.faa.ang.swac.uas.scheduler.data.ASPMTaxiTimes.ASPMTaxiTimesRecord;
import gov.faa.ang.swac.uas.scheduler.forecast.airport_data.ForecastAirportCountsRecord;
import gov.faa.ang.swac.uas.scheduler.forecast.airport_data.ForecastAirportCountsRecordTaf;
import gov.faa.ang.swac.uas.scheduler.forecast.airport_data.ForecastInternationalAirportData;
import gov.faa.ang.swac.uas.scheduler.forecast.airport_data.CountryRegionHash.CountryRegionRecord;
import gov.faa.ang.swac.uas.scheduler.vfr.VFRHelicopterMap;

public final class RunUasScheduleGenerator extends CloneableAbstractTask {

    private static final org.apache.log4j.Logger logger =
            org.apache.log4j.LogManager.getLogger(RunUasScheduleGenerator.class);
    public static final Class<?>[] OUTPUT_DATA_TYPES =
            new Class<?>[]{FlightPlan.class};
    public double forecastClonerRandomSeed;
    public double scheduleClonerRandomSeed;
    public double vfrLocalTimeRandomSeed;

    public double getForecastClonerRandomSeed() {
        return forecastClonerRandomSeed;
    }

    public void setForecastClonerRandomSeed(Double forecastClonerRandomSeed) {
        this.forecastClonerRandomSeed = forecastClonerRandomSeed;
    }

    public void setForecastClonerRandomSeed(String forecastClonerRandomSeed) {
        this.forecastClonerRandomSeed = Double.valueOf(forecastClonerRandomSeed);
    }

    public double getScheduleClonerRandomSeed() {
        return scheduleClonerRandomSeed;
    }

    public void setScheduleClonerRandomSeed(Double scheduleClonerRandomSeed) {
        this.scheduleClonerRandomSeed = scheduleClonerRandomSeed;
    }

    public void setScheduleClonerRandomSeed(String scheduleClonerRandomSeed) {
        this.scheduleClonerRandomSeed = Double.valueOf(scheduleClonerRandomSeed);
    }

    public double getVfrLocalTimeRandomSeed() {
        return vfrLocalTimeRandomSeed;
    }

    public void setVfrLocalTimeRandomSeed(Double vfrLocalTimeRandomSeed) {
        this.vfrLocalTimeRandomSeed = vfrLocalTimeRandomSeed;
    }

    public void setVfrLocalTimeRandomSeed(String vfrLocalTimeRandomSeed) {
        this.vfrLocalTimeRandomSeed = Double.valueOf(vfrLocalTimeRandomSeed);
    }
    // inputData:
    private DataMarshaller inputScheduleFile;
    private DataMarshaller aspmNominalTaxiTimesFile;
    private DataMarshaller internationalCountryRegionMapFile;
    private DataMarshaller internationalOpsCountsFile;
    private DataMarshaller mergedAirportDataFile;
    private DataMarshaller opsnetFile;
    private DataMarshaller tafAopsFile;
    private DataMarshaller vfrHelicopterPercentFile;
    // configuration:
    private double cloneTimeShiftStDev;
    private double integerizationTolerance;
    private int fratarMaxSteps;
    private double fratarConvergenceCriteria;
    private int numHoursFromGMT;
    private int numDaysToForecast;
    // processor:
    private ForecastScheduleCreator scheduleGenerator;
    // outputData:
    private DataMarshaller forecastSchedule;
    private DataMarshaller outputCountryRegionRecordList;
    private DataMarshaller outputMergedAirportDataFile;
    

    public DataMarshaller getOutputMergedAirportDataFile() {
		return outputMergedAirportDataFile;
	}

	public void setOutputMergedAirportDataFile(
			DataMarshaller outputMergedAirportDataFile) {
		this.outputMergedAirportDataFile = outputMergedAirportDataFile;
	}

	public DataMarshaller getOutputCountryRegionRecordList() {
        return outputCountryRegionRecordList;
    }

    public void setOutputCountryRegionRecordList(
            DataMarshaller outputCountryRegionRecordList) {
        this.outputCountryRegionRecordList = outputCountryRegionRecordList;
    }

    public RunUasScheduleGenerator() {  }

    private RunUasScheduleGenerator(RunUasScheduleGenerator b) {
    	super(b);
        
        this.cloneTimeShiftStDev = b.cloneTimeShiftStDev;
        this.integerizationTolerance = b.integerizationTolerance;
        this.fratarMaxSteps = b.fratarMaxSteps;
        this.fratarConvergenceCriteria = b.fratarConvergenceCriteria;
        this.numHoursFromGMT = b.numHoursFromGMT;
        this.numDaysToForecast = b.numDaysToForecast;
    }

    public DataMarshaller getInputScheduleFile() {
        return this.inputScheduleFile;
    }

    public void setInputScheduleFile(DataMarshaller scheduleFile) {
        this.inputScheduleFile = scheduleFile;
    }

    public DataMarshaller getAspmNominalTaxiTimesFile() {
        return this.aspmNominalTaxiTimesFile;
    }

    public void setAspmNominalTaxiTimesFile(DataMarshaller aspmNominalTaxiTimesFile) {
        this.aspmNominalTaxiTimesFile = aspmNominalTaxiTimesFile;
    }

    public DataMarshaller getInternationalCountryRegionMapFile() {
        return this.internationalCountryRegionMapFile;
    }

    public void setInternationalCountryRegionMapFile(DataMarshaller internationalCountryRegionMapFile) {
        this.internationalCountryRegionMapFile = internationalCountryRegionMapFile;
    }

    public DataMarshaller getInternationalOpsCountsFile() {
        return this.internationalOpsCountsFile;
    }

    public void setInternationalOpsCountsFile(DataMarshaller internationalOpsCountsFile) {
        this.internationalOpsCountsFile = internationalOpsCountsFile;
    }

    public DataMarshaller getMergedAirportDataFile() {
        return this.mergedAirportDataFile;
    }

    public void setMergedAirportDataFile(DataMarshaller mergedAirportDataFile) {
        this.mergedAirportDataFile = mergedAirportDataFile;
    }

    public DataMarshaller getOpsnetFile() {
        return this.opsnetFile;
    }

    public void setOpsnetFile(DataMarshaller opsnetFile) {
        this.opsnetFile = opsnetFile;
    }

    public DataMarshaller getTafAopsFile() {
        return this.tafAopsFile;
    }

    public void setTafAopsFile(DataMarshaller tafAopsFile) {
        this.tafAopsFile = tafAopsFile;
    }

    public DataMarshaller getVfrHelicopterPercentFile() {
        return this.vfrHelicopterPercentFile;
    }

    public void setVfrHelicopterPercentFile(DataMarshaller vfrHelicopterPercentFile) {
        this.vfrHelicopterPercentFile = vfrHelicopterPercentFile;
    }

    public double getCloneTimeShiftStDev() {
        return this.cloneTimeShiftStDev;
    }

    public void setCloneTimeShiftStDev(double val) {
        this.cloneTimeShiftStDev = val;
    }

    public double getIntegerizationTolerance() {
        return this.integerizationTolerance;
    }

    public void setIntegerizationTolerance(double val) {
        this.integerizationTolerance = val;
    }

    public int getFratarMaxSteps() {
        return this.fratarMaxSteps;
    }

    public void setFratarMaxSteps(int val) {
        this.fratarMaxSteps = val;
    }

    public double getFratarConvergenceCriteria() {
        return this.fratarConvergenceCriteria;
    }

    public void setFratarConvergenceCriteria(double val) {
        this.fratarConvergenceCriteria = val;
    }

    public int getNumHoursFromGMT() {
        return this.numHoursFromGMT;
    }

    public void setNumHoursFromGMT(int val) {
        this.numHoursFromGMT = val;
    }

    public int getNumDaysToForecast() {
        return this.numDaysToForecast;
    }

    public void setNumDaysToForecast(int val) {
        this.numDaysToForecast = val;
    }

    public ForecastScheduleCreator getScheduleGenerator() {
        return this.scheduleGenerator;
    }

    public void setScheduleGenerator(ForecastScheduleCreator val) {
        this.scheduleGenerator = val;
    }

    public DataMarshaller getForecastSchedule() {
        return this.forecastSchedule;
    }

    public void setForecastSchedule(DataMarshaller scheduleFile) {
        this.forecastSchedule = scheduleFile;
    }

    @Override
    public void run() {
        try {
            // 0. Initialize the forecast schedule creator ------------------------------------------------------------

            this.scheduleGenerator = new ForecastScheduleCreator();
            this.scheduleGenerator.setCloneTimeShiftStDev(this.cloneTimeShiftStDev);
            this.scheduleGenerator.setIntegerizationTolerance(this.integerizationTolerance);
            this.scheduleGenerator.setFratarMaxSteps(this.fratarMaxSteps);
            this.scheduleGenerator.setFratarConvergenceCriteria(this.fratarConvergenceCriteria);
            this.scheduleGenerator.setNumHoursFromGMT(this.numHoursFromGMT);
            this.scheduleGenerator.setNumDaysToForecast(this.numDaysToForecast);
            this.scheduleGenerator.setForecastClonerRandom((long) forecastClonerRandomSeed);
            this.scheduleGenerator.setScheduleClonerRandom((long) scheduleClonerRandomSeed);
            this.scheduleGenerator.setVfrLocalTimeRandom((long) vfrLocalTimeRandomSeed);

            //---------------------------------------------------------------------------------------------------------
            // LOAD/READ
            // 1. flight plans
            // 2. ASPM taxi times
            // 3. countries/regions
            // 4. forecasted international ops data
            // 5. merged airport data
            // 6. forecasted OPSNET data
            // 7. forecasted TAF data
            // 8. VFR helicopter percents
            //---------------------------------------------------------------------------------------------------------

            // 1. Load schedule records -------------------------------------------------------------------------------

            List<ScheduleRecord> baseSchedRecList = new ArrayList<ScheduleRecord>();
            logger.debug("loading schedule records...");
            this.inputScheduleFile.load(baseSchedRecList);

            this.scheduleGenerator.setInputScheduleFile(baseSchedRecList);

            // 2. Load ASPM taxi times --------------------------------------------------------------------------------

            List<ASPMTaxiTimesRecord> aspmTaxiTimesRecordList = new ArrayList<ASPMTaxiTimesRecord>();
            logger.debug("loading ASPM taxi times...");
            this.aspmNominalTaxiTimesFile.load(aspmTaxiTimesRecordList);

            this.scheduleGenerator.setAspmNominalTaxiTimesFile(aspmTaxiTimesRecordList);

            // 3. Load countries/regions -----------------------------------------------------------------------------

            List<CountryRegionRecord> countryRegionRecordList = new ArrayList<CountryRegionRecord>();
            logger.debug("loading country region records...");
            this.internationalCountryRegionMapFile.load(countryRegionRecordList);

            this.scheduleGenerator.setInternationalCountryRegionMapFile(countryRegionRecordList);

            // 4. Load forecasted international ops data --------------------------------------------------------------

            List<ForecastInternationalAirportData> forecastIntlAirportDataList = new ArrayList<ForecastInternationalAirportData>();
            logger.debug("loading international airport forecast data...");
            this.internationalOpsCountsFile.load(forecastIntlAirportDataList);

            this.scheduleGenerator.setInternationalOpsCountsFile(forecastIntlAirportDataList);

            // 5. Load merged airport data ----------------------------------------------------------------------------
            // Uses a list, but there is only one AirportDataMap object.

            List<AirportDataMap> airportDataMapList = new ArrayList<AirportDataMap>(1);
            logger.debug("loading airport data...");
            this.mergedAirportDataFile.load(airportDataMapList);

            this.scheduleGenerator.setMergedAirportDataFile(airportDataMapList.get(0));

            // 6. Load forecasted OPSNET data -------------------------------------------------------------------------

            List<ForecastAirportCountsRecord> forecastAirportCountsRecList = new ArrayList<ForecastAirportCountsRecord>();
            logger.debug("loading airport forecast counts...");
            this.opsnetFile.load(forecastAirportCountsRecList);

            this.scheduleGenerator.setOpsnetFile(forecastAirportCountsRecList);

            // 7. Load forecasted TAF data ----------------------------------------------------------------------------

            List<ForecastAirportCountsRecordTaf> forecastAirportCountsRecTafList = new ArrayList<ForecastAirportCountsRecordTaf>();
            logger.debug("loading airport forecast TAF counts...");
            this.tafAopsFile.load(forecastAirportCountsRecTafList);

            this.scheduleGenerator.setTafAopsFile(forecastAirportCountsRecTafList);

            // 8. Load VFR helicopter percents ------------------------------------------------------------------------
            // Uses a list, but there is only one VFRHelicopterMap object.

            List<VFRHelicopterMap> vfrHelicopterMapList = new ArrayList<VFRHelicopterMap>(1);
            logger.debug("loading VFR helicopter data...");
            this.vfrHelicopterPercentFile.load(vfrHelicopterMapList);

            this.scheduleGenerator.setVfrHelicopterPercentFile(vfrHelicopterMapList.get(0));

            //---------------------------------------------------------------------------------------------------------
            // PROCESS
            //---------------------------------------------------------------------------------------------------------

            logger.debug("starting ForecastScheduleCreator...");
            this.scheduleGenerator.run(
                    this.getParent().getBaseDate(),
                    this.getParent().getForecastFiscalYear());

            //---------------------------------------------------------------------------------------------------------
            // SAVE/WRITE
            //---------------------------------------------------------------------------------------------------------

            // Get the new schedule records
            List<ScheduleRecord> forecastSchedRecList = this.scheduleGenerator.getOutputScheduleFile();

            logger.debug("saving forecast schedule records...");
            this.forecastSchedule.save(forecastSchedRecList);
            logger.debug("saving country region records...");
            this.outputCountryRegionRecordList.save(countryRegionRecordList);
            logger.debug("saving airport data file records...");
            this.outputMergedAirportDataFile.save(airportDataMapList);

            //---------------------------------------------------------------------------------------------------------
            // MEMORY CLEANUP
            //---------------------------------------------------------------------------------------------------------
            this.scheduleGenerator.setInputScheduleFile(null);
            this.scheduleGenerator.setAspmNominalTaxiTimesFile(null);
            this.scheduleGenerator.setInternationalCountryRegionMapFile(null);
            this.scheduleGenerator.setInternationalOpsCountsFile(null);
            this.scheduleGenerator.setMergedAirportDataFile(null);
            this.scheduleGenerator.setOpsnetFile(null);
            this.scheduleGenerator.setTafAopsFile(null);
            this.scheduleGenerator.setVfrHelicopterPercentFile(null);

            this.scheduleGenerator.setOutputScheduleFile(null);
        } catch (DataAccessException ex) {
            logger.trace(ex.getStackTrace());
            throw new ExitException("Fatal", ex);
        }
    }

    @Override
    public RunUasScheduleGenerator clone() {
        return new RunUasScheduleGenerator(this);
    }

    @Override
    public boolean validate(VALIDATION_LEVEL level) {
        boolean retval = false;

        retval = validateFiles(new DataMarshaller[]{inputScheduleFile, aspmNominalTaxiTimesFile, internationalCountryRegionMapFile, internationalOpsCountsFile,
                    mergedAirportDataFile, opsnetFile, tafAopsFile, vfrHelicopterPercentFile}, level);
        return retval;
    }
}
