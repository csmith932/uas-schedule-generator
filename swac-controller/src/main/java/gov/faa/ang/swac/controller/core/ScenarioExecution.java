/**
 * Copyright "TBD", Metron Aviation & CSSI. All rights reserved.
 *
 * This computer Software was developed with the sponsorship of the U.S.
 * Government under Contract No. DTFAWA-10-D-00033, which has a copyright
 * license in accordance with AMS 3.5-13.(c)(1).
 */
package gov.faa.ang.swac.controller.core;

import gov.faa.ang.swac.common.datatypes.Timestamp;
import gov.faa.ang.swac.controller.ExitException;
import gov.faa.ang.swac.controller.core.Batch.MonteCarloMode;
import gov.faa.ang.swac.controller.core.montecarlo.replay.CachedScenarioConfiguration;
import gov.faa.ang.swac.controller.core.montecarlo.replay.ConfigurationCache;
import gov.faa.ang.swac.datalayer.DataAccessException;
import gov.faa.ang.swac.datalayer.MappedDataAccess;
import gov.faa.ang.swac.datalayer.identity.FileDataDescriptor;
import gov.faa.ang.swac.datalayer.identity.FileSetDescriptor;
import gov.faa.ang.swac.datalayer.identity.ParameterizedDataDescriptor;
import gov.faa.ang.swac.datalayer.storage.DataMarshaller;
import gov.faa.ang.swac.datalayer.storage.db.JDBCConnectionFactory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class ScenarioExecution {

    private static final Logger logger = LogManager.getLogger(ScenarioExecution.class);
    private Batch parent;

    public Batch getParent() {
        return this.parent;
    }
    private MappedDataAccess dao;

    public MappedDataAccess getDao() {
        return this.dao;
    }
    private int forecastFiscalYear;

    public int getForecastFiscalYear() {
        return this.forecastFiscalYear;
    }
    private Timestamp baseDate;

    public Timestamp getBaseDate() {
        return this.baseDate;
    }
    private String classifier;

    public String getClassifier() {
        return this.classifier;
    }

    public void setClassifier(String val) {
        this.classifier = val;
    }
    private Timestamp startDate;

    private void updateStartDate() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(baseDate.toDate());

        int yearsToAdd = forecastFiscalYear - cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);

        // Fiscal year is one ahead of calendar year in the last 3 months
        if (month == Calendar.OCTOBER || month == Calendar.NOVEMBER || month == Calendar.DECEMBER) {
            yearsToAdd--;
        }

        cal.add(Calendar.YEAR, yearsToAdd);

        startDate = new Timestamp(cal.getTimeInMillis());
    }

    public Timestamp getStartDate() {
        return startDate;
    }

    public Timestamp baseTimeToSimTime(Timestamp base) {
    	if (base == null) {
            return null;
        }
    	
    	return baseTimeToSimTime(base.toDate());
    }
    
    public Timestamp baseTimeToSimTime(Date base) {
        if (base == null) {
            return null;
        }

        long delta = startDate.getTime() - baseDate.getTime();
        return new Timestamp(base.getTime() + delta);
    }

	public Date baseDateToSimDate(Date base) {
		if (base == null) {
			return null;
		}

		long delta = startDate.getTime() - baseDate.getTime();
		return new Date(base.getTime() + delta);
	}

    public String getScenarioName() {
        return classifier + "_" + baseDate.toBonnDateOnlyString() + "_" + forecastFiscalYear;
    }

    public int getScenarioId() {
        return getScenarioName().hashCode();
    }

    public ScenarioExecution(Batch parent, Timestamp baseDate, int forecastFiscalYear, String classifier) {
        this.parent = parent;
        this.baseDate = baseDate;
        this.forecastFiscalYear = forecastFiscalYear;
        updateStartDate();
        this.classifier = classifier;

        this.dao = new MappedDataAccess(parent.getDao());
        this.dao.setScenarioName(getScenarioName());

        logger.debug("Created ScenarioExecution: " + this.toString());
    }

    /**
     * @param sampleSize
     * @param randomSeed
     * @return
     * @throws DataAccessException
     */
    public List<Job> initializeNormal(JDBCConnectionFactory databaseConnectionFactory) throws DataAccessException {
        logger.debug("Initializing scenarioId=" + this.getScenarioId());

        ArrayList<Job> retVal = new ArrayList<Job>();

        Map<TaskConfiguration, AbstractTask> globalTasks = new HashMap<TaskConfiguration, AbstractTask>();

        int instanceId = this.getScenarioId();
        retVal.add(this.initializeInstance(null, instanceId, globalTasks, null, null, databaseConnectionFactory, MonteCarloMode.OFF));

        retVal.add(addGlobalTasks(globalTasks));

        return retVal;
    }

    /**
     * @param sampleSize
     * @param randomSeed
     * @return
     * @throws DataAccessException
     */
    public List<Job> initializeMonteCarlo(int sampleSize, int innerLoopCount, int randomSeed, JDBCConnectionFactory databaseConnectionFactory, ConfigurationCache cache) throws DataAccessException {
        logger.debug("Initializing scenarioId=" + this.getScenarioId());
        // Seed random channels for Monte Carlo parameters - one for each task in the pipeline, with an independent seed
        List<Random> randomChannels = new ArrayList<Random>();
        for (TaskConfiguration task : this.parent.getTasks()) {
            int seed = randomSeed + task.getTarget().getName().hashCode();
            logger.debug("Random stream initialized with seed=" + seed + " for task=" + task.toString());
            randomChannels.add(new Random(seed));
        }

        List<Job> retVal = new ArrayList<Job>();

        Map<TaskConfiguration, AbstractTask> globalTasks = new HashMap<TaskConfiguration, AbstractTask>();
        Map<String, Map<String, Double>> outerLoopValues = new HashMap<String, Map<String, Double>>();

        int instanceCount = 0;

        for (int i = 0; i < sampleSize; i++) {
            for (int j = 0; j < innerLoopCount; j++) {
                int instanceId = (this.getScenarioName() + instanceCount).hashCode();

                CachedScenarioConfiguration scenarioConfig = cache.getScenario(instanceId);
                scenarioConfig.setBaseDate(this.baseDate);
                scenarioConfig.setForecastFiscalYear(this.forecastFiscalYear);
                scenarioConfig.setClassifier(this.classifier);

                Job job = this.initializeInstance(randomChannels, instanceId, globalTasks, cache, outerLoopValues, databaseConnectionFactory, MonteCarloMode.ON, sampleSize * innerLoopCount);
                retVal.add(job);
                job.insertElementAt(statusMessageTask(monteCarloInstanceStartMessage(i, j, instanceId)), 0);
                job.add(statusMessageTask(monteCarloInstanceEndMessage(i, j, instanceId)));
                instanceCount++;
            }
            outerLoopValues.clear();
        }
        Job global = addGlobalTasks(globalTasks);
        global.insertElementAt(statusMessageTask(monteCarloBatchEndMessage()), 0);
        global.add(statusMessageTask(monteCarloAggregationEndMessage()));
        retVal.add(global);

        // XXX: This message used to be queued as a status message task/job at the beginning of the run, but that was awkward and resulted in an improper Job count
        // This is effectively the same point during execution when it was generated, so display it now
        logger.info("<------------------ BEGIN MONTE CARLO SAMPLE DATA GENERATION ----------------->");
        
        return retVal;
    }

    private AbstractTask statusMessageTask(final String message) {
        return new AbstractTask() {
            @Override
            public void runWithCleanup() {
                logger.info(message);
            }

            @Override
            public void run() {
                logger.info(message);
            }

            @Override
            public String getTaskName() {
                return "STATUS";
            }

            @Override
            public ScenarioExecution getParent() {
                return ScenarioExecution.this;
            }

            @Override
            public boolean validate(VALIDATION_LEVEL level) {
                // Not needed here...
                return true;
            }
        };
    }
    private static final String HYPHENS = "<----------------------------------------------------------------------------->";

    private static String padWithHyphens(String count) {
        int m = (80 - count.length()) / 2;
        int n = m + count.length(); // Due to roundoff, m != n for odd length

        return HYPHENS.substring(0, m) + count + HYPHENS.substring(n);
    }

    private String monteCarloInstanceStartMessage(int outer, int inner, int instanceId) {
        return padWithHyphens(" BEGIN MONTE CARLO SAMPLE RUN: OUTER LOOP " + (outer + 1) + ", INNER LOOP " + (inner + 1) + "(" + instanceId + ") ");
    }

    private String monteCarloInstanceEndMessage(int outer, int inner, int instanceId) {
        return padWithHyphens(" END MONTE CARLO SAMPLE RUN: OUTER LOOP " + (outer + 1) + ", INNER LOOP " + (inner + 1) + "(" + instanceId + ") ");
    }

    private String monteCarloBatchEndMessage() {
        return "<------------------- END MONTE CARLO SAMPLE DATA GENERATION ------------------>";
    }

    private String monteCarloAggregationEndMessage() {
        return "<------------------------ END MONTE CARLO AGGREGATION ------------------------>";
    }

    /**
     * @param sampleSize
     * @param randomSeed
     * @return
     * @throws DataAccessException
     */
    public List<Job> initializeReplay(ConfigurationCache cache, JDBCConnectionFactory databaseConnectionFactory, int instanceId) throws DataAccessException {
        logger.debug("Initializing scenarioId=" + this.getScenarioId());

        List<Job> retVal = new ArrayList<Job>();

        Map<TaskConfiguration, AbstractTask> globalTasks = new HashMap<TaskConfiguration, AbstractTask>();

        retVal.add(this.initializeInstance(null, instanceId, globalTasks, cache, null, databaseConnectionFactory, MonteCarloMode.REPLAY));

        retVal.add(addGlobalTasks(globalTasks));

        return retVal;
    }

    private Job addGlobalTasks(Map<TaskConfiguration, AbstractTask> globalTasks) {
        logger.debug("Adding global tasks to execution queue for ScenarioExecution:" + this.toString());
        
        // First sort global task configurations by priority level.
        List<TaskConfiguration> configs = new ArrayList<TaskConfiguration>();
        for (TaskConfiguration config : globalTasks.keySet()){
        	configs.add(config);
        }
        Collections.sort(configs);
        
        // As of SWAC 2.0, expected task ordering: MC Aggregators, DB Report uploads, File Output.
        List<AbstractTask> tasks = new ArrayList<AbstractTask>();
        for (TaskConfiguration config : configs){
        	tasks.add(globalTasks.get(config));
        }
        
        Job retVal = new Job();
        retVal.addAll(tasks);
        return retVal;
    }

    protected Job initializeInstance(List<Random> randomChannels, int instanceId, Map<TaskConfiguration, AbstractTask> globalTasks, ConfigurationCache cache, Map<String, Map<String, Double>> outerLoopValues, JDBCConnectionFactory databaseConnectionFactory, MonteCarloMode mode) throws DataAccessException {
    	return initializeInstance(randomChannels, instanceId, globalTasks, cache, outerLoopValues, databaseConnectionFactory, mode, 1);
    }
    
    protected Job initializeInstance(List<Random> randomChannels, int instanceId, Map<TaskConfiguration, AbstractTask> globalTasks, ConfigurationCache cache, Map<String, Map<String, Double>> outerLoopValues, JDBCConnectionFactory databaseConnectionFactory, MonteCarloMode mode, int numInstances) throws DataAccessException {
        logger.debug("Initializing scenario instance: instanceId=" + instanceId + "; " + this.toString());
        //boolean allTasksValidated = true;

        Job retVal = new Job(databaseConnectionFactory, this.dao);
        retVal.setInstanceId(instanceId);
                
        try {
            // Pass in baseDate, forecastYear, and classifier separately to avoid creating a circular dependency.
            databaseConnectionFactory.createDbInstance(dao, Integer.toString(instanceId), this.getBaseDate(), this.getForecastFiscalYear(), this.getClassifier());
        } catch (SQLException ex) {
            logger.error("Error creating database!");
            throw new ExitException(ex);
        }		

        for (int i = 0; i < this.parent.getTasks().size(); i++) {
            TaskConfiguration task = this.parent.getTasks().get(i);
            if (!task.isEnabled()) {
                // Disabled tasks should not appear in the list by the time we initialize executions: abort
                throw new ExitException("Error: Attempting to initialize a scenario execution when disabled tasks have not been removed from the pipelin");
            }

            // Global tasks should only be created once, initialized for each instance (to link aggregators), and added to the execution queue at the very end
            AbstractTask execution = globalTasks.get(task);
            if (execution == null) // Don't re-create global tasks
            {
                execution = task.createTaskExecution(this, this.dao, instanceId, randomChannels == null ? null : randomChannels.get(i));
                execution.setParent(this);
                execution.setNumInstances(numInstances);

                // If it is a global task, insert it in the map so that it isn't re-created
                if (task.isGlobal()) {
                    globalTasks.put(task, execution);
                    logger.debug("Global task created and reserved: " + execution.toString());
                } else {
                    // Global tasks will be added at the end
                    retVal.add(execution);
                    logger.debug("Task created and added to the execution queue: " + execution.toString());
                }
            }

            // Always invoke initialization
            task.initializeTaskExecution(execution, this, this.dao, instanceId, randomChannels == null ? null : randomChannels.get(i), cache, outerLoopValues, mode);
            
            /*
            boolean taskIsValid = execution.validate();

            if (!taskIsValid) {
                allTasksValidated = false;
                logger.error("Task \"" + execution.toString() + "\" has failed validation check!");

            }
            */
        }

        //logger.info(allTasksValidated ? "All tasks successfully validated." : "Some tasks failed validation check!");
        
        return retVal;
    }

    protected DataMarshaller getStaticMarshaller(FileDataDescriptor descriptor) throws DataAccessException {
        // Load or link to static data in Batch dao
        FileDataDescriptor fdd = descriptor.clone();
        boolean modified = false;
        
        // Validate the parameters.
        if (fdd.getBaseDate() != null && !fdd.getBaseDate().equalValue(this.getBaseDate())) {
            fdd.setBaseDate(this.getBaseDate());
            modified = true;
        }
        
        if (fdd.getForecastFiscalYear() != null && fdd.getForecastFiscalYear().intValue() != this.getForecastFiscalYear()) {
            fdd.setForecastFiscalYear(this.getForecastFiscalYear());
            modified = true;
        }
        
        if (fdd.getClassifier() != null && !fdd.getClassifier().contentEquals(this.getClassifier())) {
            fdd.setClassifier(this.getClassifier());
            modified = true;
        }
        
        if (modified) {
            fdd.setResourceName(null);
        }
        return this.getParent().loadStaticData(fdd);
    }

    public DataMarshaller getParameterizedMarshaller(ParameterizedDataDescriptor descriptor) throws DataAccessException {
        // Parameterized data should already have metadata registered; retrieve marshaller
        ParameterizedDataDescriptor pDescriptor = descriptor.clone();
        pDescriptor.setBaseDate(this.getBaseDate());
        pDescriptor.setForecastFiscalYear(this.getForecastFiscalYear());
        pDescriptor.setClassifier(this.getClassifier());
        return dao.getMarshaller(pDescriptor);
    }

    @Override
    public String toString() {
        return "ScenarioExecution [forecastFiscalYear=" + forecastFiscalYear
                + ", baseDate=" + baseDate + ", classifier=" + classifier
                + ", startDate=" + startDate + "]";
    }
}
