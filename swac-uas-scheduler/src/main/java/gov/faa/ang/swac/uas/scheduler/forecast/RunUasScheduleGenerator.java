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
import gov.faa.ang.swac.uas.scheduler.forecast.airport_data.ForecastAirportCountsRecord;

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
    private DataMarshaller mergedAirportDataFile;
    private DataMarshaller tafAopsFile;
    // configuration:
    private double cloneTimeShiftStDev;
    private int numHoursFromGMT;
    private int numDaysToForecast;
    // processor:
    private ForecastScheduleCreator scheduleGenerator;
    // outputData:
    private DataMarshaller forecastSchedule;

    public RunUasScheduleGenerator() {  }

    private RunUasScheduleGenerator(RunUasScheduleGenerator b) {
    	super(b);
        
        this.cloneTimeShiftStDev = b.cloneTimeShiftStDev;
        this.numHoursFromGMT = b.numHoursFromGMT;
        this.numDaysToForecast = b.numDaysToForecast;
    }

    public DataMarshaller getInputScheduleFile() {
        return this.inputScheduleFile;
    }

    public void setInputScheduleFile(DataMarshaller scheduleFile) {
        this.inputScheduleFile = scheduleFile;
    }

    public DataMarshaller getMergedAirportDataFile() {
        return this.mergedAirportDataFile;
    }

    public void setMergedAirportDataFile(DataMarshaller mergedAirportDataFile) {
        this.mergedAirportDataFile = mergedAirportDataFile;
    }

    public DataMarshaller getTafAopsFile() {
        return this.tafAopsFile;
    }

    public void setTafAopsFile(DataMarshaller tafAopsFile) {
        this.tafAopsFile = tafAopsFile;
    }

    public double getCloneTimeShiftStDev() {
        return this.cloneTimeShiftStDev;
    }

    public void setCloneTimeShiftStDev(double val) {
        this.cloneTimeShiftStDev = val;
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
            this.scheduleGenerator.setNumHoursFromGMT(this.numHoursFromGMT);
            this.scheduleGenerator.setNumDaysToForecast(this.numDaysToForecast);
            this.scheduleGenerator.setForecastClonerRandom((long) forecastClonerRandomSeed);
            this.scheduleGenerator.setScheduleClonerRandom((long) scheduleClonerRandomSeed);
            this.scheduleGenerator.setVfrLocalTimeRandom((long) vfrLocalTimeRandomSeed);

            //---------------------------------------------------------------------------------------------------------
            // LOAD/READ
            // 1. flight plans
            // 5. merged airport data
            // 7. forecasted TAF data
            //---------------------------------------------------------------------------------------------------------

            // 1. Load schedule records -------------------------------------------------------------------------------

            List<ScheduleRecord> baseSchedRecList = new ArrayList<ScheduleRecord>();
            logger.debug("loading schedule records...");
            this.inputScheduleFile.load(baseSchedRecList);

            this.scheduleGenerator.setInputScheduleFile(baseSchedRecList);

            // 5. Load merged airport data ----------------------------------------------------------------------------
            // Uses a list, but there is only one AirportDataMap object.

            List<AirportDataMap> airportDataMapList = new ArrayList<AirportDataMap>(1);
            logger.debug("loading airport data...");
            this.mergedAirportDataFile.load(airportDataMapList);

            this.scheduleGenerator.setMergedAirportDataFile(airportDataMapList.get(0));

            // 7. Load forecasted TAF data ----------------------------------------------------------------------------

            List<ForecastAirportCountsRecord> forecastAirportCountsRecTafList = new ArrayList<ForecastAirportCountsRecord>();
            logger.debug("loading airport forecast TAF counts...");
            this.tafAopsFile.load(forecastAirportCountsRecTafList);

            this.scheduleGenerator.setTafAopsFile(forecastAirportCountsRecTafList);

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

            //---------------------------------------------------------------------------------------------------------
            // MEMORY CLEANUP
            //---------------------------------------------------------------------------------------------------------
            this.scheduleGenerator.setInputScheduleFile(null);
            this.scheduleGenerator.setMergedAirportDataFile(null);
            this.scheduleGenerator.setTafAopsFile(null);

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

        retval = validateFiles(new DataMarshaller[]{inputScheduleFile,
                    mergedAirportDataFile, tafAopsFile}, level);
        return retval;
    }
}
