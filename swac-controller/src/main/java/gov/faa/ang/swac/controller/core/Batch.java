/**
 * Copyright "TBD", Metron Aviation & CSSI. All rights reserved. This computer Software was
 * developed with the sponsorship of the U.S. Government under Contract No. DTFAWA-10-D-00033, which
 * has a copyright license in accordance with AMS 3.5-13.(c)(1).
 */
package gov.faa.ang.swac.controller.core;

import gov.faa.ang.swac.common.datatypes.Timestamp;
import gov.faa.ang.swac.controller.ExitException;
import gov.faa.ang.swac.controller.GlobalApplicationContext;
import gov.faa.ang.swac.controller.ScenarioApplicationContext;
import gov.faa.ang.swac.controller.core.component.TemplateImporter;
import gov.faa.ang.swac.controller.core.montecarlo.replay.CachedScenarioConfiguration;
import gov.faa.ang.swac.controller.core.montecarlo.replay.ConfigurationCache;
import gov.faa.ang.swac.datalayer.AdHocDataAccess;
import gov.faa.ang.swac.datalayer.DataAccessException;
import gov.faa.ang.swac.datalayer.MappedDataAccess;
import gov.faa.ang.swac.datalayer.AdHocDataAccess.LogLevel;
import gov.faa.ang.swac.datalayer.ResourceManager.LOCATION;
import gov.faa.ang.swac.datalayer.identity.DataDescriptor;
import gov.faa.ang.swac.datalayer.identity.FileDataDescriptor;
import gov.faa.ang.swac.datalayer.identity.IntermediateDataDescriptor;
import gov.faa.ang.swac.datalayer.storage.DataMarshaller;
import gov.faa.ang.swac.datalayer.storage.db.JDBCConnectionFactory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class Batch {
    private static final Logger logger = LogManager.getLogger(Batch.class);
    //all failedTaskNames in this package will be disabled if Monte Carlo is turned OFF
    private static final String MONTE_CARLO_PACKAGE = "gov.faa.ang.swac.controller.core.montecarlo";
    private static final int TIMEOUT_DAYS = 30;
    private static boolean initializeLogging = true;
	
    public enum MonteCarloMode { ON, OFF, REPLAY }
    
    private MonteCarloMode monteCarloMode;

    public MonteCarloMode getMonteCarloMode() {
        return this.monteCarloMode;
    }

    public void setMonteCarloMode(MonteCarloMode val) {
        this.monteCarloMode = val;
    }
    
    private ExecutorServiceFactory executorServiceFactory;
    
    public ExecutorServiceFactory getExecutorServiceFactory() {
    	return executorServiceFactory;
    }
    
    public void setExecutorServiceFactory(ExecutorServiceFactory val) {
    	executorServiceFactory = val;
    }
    
    private int scenarioExecutionId;

    public int getScenarioExecutionId() {
        return this.scenarioExecutionId;
    }

    public void setScenarioExecutionId(int val) {
        this.scenarioExecutionId = val;
    }
    private String batchName;

    public String getBatchName() {
        return this.batchName;
    }

    public void setBatchName(String val) {
        this.batchName = val;
    }
    private List<TaskConfiguration> tasks;

    public List<TaskConfiguration> getTasks() {
        return this.tasks;
    }

    public void setTasks(List<TaskConfiguration> val) {
        this.tasks = val;
    }
    private BatchFiscalYear forecastFiscalYears = new BatchFiscalYear();

    public BatchFiscalYear getForecastFiscalYears() {
        return this.forecastFiscalYears;
    }

    public void setForecastFiscalYears(BatchFiscalYear val) {
        this.forecastFiscalYears = val;
    }
    private BatchTimestamp baseDates = new BatchTimestamp();

    public BatchTimestamp getBaseDates() {
        return this.baseDates;
    }

    public void setBaseDates(BatchTimestamp val) {
        this.baseDates = val;
    }
    private BatchClassifier classifiers = new BatchClassifier();

    public BatchClassifier getClassifiers() {
        return this.classifiers;
    }

    public void setClassifiers(BatchClassifier val) {
        this.classifiers = val;
    }
    private LogLevel logLevel;

    public LogLevel getLogLevel() {
        return this.logLevel;
    }

    public void setLogLevel(LogLevel val) {
        this.logLevel = val;
    }
    private int sampleSize;

    public int getSampleSize() {
        return this.sampleSize;
    }

    public void setSampleSize(int val) {
        this.sampleSize = val;
    }
    
    private int innerLoopCount;
    
    public int getInnerLoopCount() {
        return innerLoopCount;
    }

    public void setInnerLoopCount(int innerLoopCount) {
        this.innerLoopCount = innerLoopCount;
    }
    
    private int randomSeed;

    public int getRandomSeed() {
        return this.randomSeed;
    }

    public void setRandomSeed(int val) {
        this.randomSeed = val;
    }
    
    private String swacVersion;

    public String getSwacVersion() {
        return this.swacVersion;
    }

    public void setSwacVersion(String val) {
        this.swacVersion = val;
    }
    
    private FileDataDescriptor configurationCache;

    public FileDataDescriptor getConfigurationCache() {
        return this.configurationCache;
    }

    public void setConfigurationCache(FileDataDescriptor val) {
        this.configurationCache = val;
    }
    
    private transient MappedDataAccess dao;

    public MappedDataAccess getDao() {
        return this.dao;
    }

    public void setDao(MappedDataAccess val) {
        this.dao = val;
    }
    
    private transient JDBCConnectionFactory databaseConnectionFactory = new JDBCConnectionFactory();

    public JDBCConnectionFactory getDatabaseConnectionFactory() {
        return this.databaseConnectionFactory;
    }

    public void setDatabaseConnectionFactory(JDBCConnectionFactory val) {
        this.databaseConnectionFactory = val;
    }
    
    public Batch() {
        this.classifiers = new BatchClassifier();
        this.baseDates = new BatchTimestamp();
        this.forecastFiscalYears = new BatchFiscalYear();
        this.logLevel = LogLevel.valueOf(System.getProperty("swac.log.level"));
    }

    public void run(ScenarioApplicationContext ctx) {
        try {
        	logger.info("START " + this.batchName);
        	
        	prepareRun(ctx);
        	
        	List<Job> execQueue = fillExecutionQueue(ctx);
        	
        	// Everything is initialized: validate and/or execute the task queue
        	// NOTE: the integrity of the configuration was checked already - this validation is for the input data
        	// TODO: If you didn't see the note in ScenarioApplicationContext, we need to get a validation setting into the app from the command line or config, or else this will always default to NORMAL
                switch (ctx.getValidationSetting()) {
                    case DEEP_NO_EXECUTE:
                    	// Validate and skip execution
                    	deepDataValidate(execQueue,false);
                        break;
                    case DEEP:
                        deepDataValidate(execQueue,true);
                        // Validation passed: time to run!
                        //(falls through)
                    case NORMAL: //falls through
                    default:
                    	execute(execQueue);
                        break;
                }
           
            logger.info("END " + this.batchName);

        }catch (OutOfMemoryError e){
            StringBuilder stackTrace = new StringBuilder();
			for (StackTraceElement ste : e.getStackTrace()) {
			    stackTrace.append(ste.toString() + "\n");
			}
            logger.fatal("OUT OF MEMORY IN " + this.batchName + ", ABORTING...");
            logger.debug(e.getMessage() + "\n" + stackTrace);
        }
        catch (Exception ex) {
            logger.fatal("ERROR ENCOUNTERED IN " + this.batchName + ", ABORTING...");
            if (ex.getMessage() != null)
            	logger.fatal(ex.getMessage());
            logger.debug(ex.getMessage(), ex);
        }
    }
    
    private void prepareRun(ScenarioApplicationContext ctx) throws DataAccessException, SQLException {
            // Clean the output/reports directories
            dao.clean(LOCATION.OUTPUT);
            dao.clean(LOCATION.REPORT);
            
            configMonteCarloSettings(ctx);

            logger.info("Register metadata");
            registerMetadata(dao);
            
            logger.info("Preprocess task list");
            preprocessTaskList();

            // NOTE: This only validates the integrity of the configuration, not the source data
            logger.info("Validate data flow");
            validateDataFlow();

            // NOTE: This only validates the integrity of the configuration, not the source data
            logger.info("Validate task configurations");
            validateTaskConfigurations();

            logger.info("Initialize tasks");

            // This insures that we have at least one permutation of classifiers-baseDates-forecastFiscalYears on which to run
            // For scenarios like dataProcessor.xml these parameters aren't always defined explicitly 
            
            if(this.classifiers.isEmpty()) {
                this.classifiers.add("");
            }
            
            if(this.baseDates.isEmpty()) {
                this.baseDates.add(new Timestamp());
            }
            
            if(this.forecastFiscalYears.isEmpty()) {
                this.forecastFiscalYears.add(Integer.MAX_VALUE);
            }
            
    }
    
    private List<Job> fillExecutionQueue(ScenarioApplicationContext ctx) throws DataAccessException {
    		ConfigurationCache cache = null;
    		List<Job> execQueue = new ArrayList<Job>();
    		
    		switch (this.monteCarloMode)
            {
	            case ON:
	            	logger.info("Monte Carlo mode enabled");
	            	logger.info("Loading configuration cache from " + this.configurationCache.toString());
	                cache = loadConfigurationCache();
	                for (String classifier : this.classifiers) {
	            		logger.info("Initializing tasks for classifier " + classifier);
	                    for (Timestamp baseDate : this.baseDates) {
	                    	logger.info("Initializing tasks for base date " + baseDate);
		                    for (Integer forecastFiscalYear : this.forecastFiscalYears) {
		                    	logger.info("Initializing tasks for forecast FY " + forecastFiscalYear + "; " + this.sampleSize +" X "+this.innerLoopCount+ " Monte Carlo iterations");
			                    // Create scenario context
	                            ScenarioExecution exec = new ScenarioExecution(this,
	                                    baseDate,
	                                    forecastFiscalYear,
	                                    classifier);
	                        	execQueue.addAll(exec.initializeMonteCarlo(this.sampleSize, this.innerLoopCount, this.randomSeed, this.databaseConnectionFactory, cache));
	                        }
	                    }
	            	}
	                logger.info("Initialization complete; saving configuration cache to " + this.configurationCache.toString());
	                saveConfigurationCache(cache);
	                break;
	            case OFF:
	            	logger.info("Monte Carlo mode disabled");
	            	for (String classifier : this.classifiers) {
	            		logger.info("Initializing tasks for classifier " + classifier);
	                    for (Timestamp baseDate : this.baseDates) {
	                    	logger.info("Initializing tasks for base date " + baseDate);
		                    for (Integer forecastFiscalYear : this.forecastFiscalYears) {
		                    	logger.info("Initializing tasks for forecast FY " + forecastFiscalYear + "; 1 iteration with no Monte Carlo sampling or aggregation");
			                    // Create scenario context
	                            ScenarioExecution exec = new ScenarioExecution(this,
	                                    baseDate,
	                                    forecastFiscalYear,
	                                    classifier);
	                            execQueue.addAll(exec.initializeNormal(this.databaseConnectionFactory));
	                        }
	                    }
	            	}
	            	break;
	            case REPLAY:
	            	logger.info("Loading configuration cache from " + this.configurationCache.toString());
	                cache = loadConfigurationCache();
	                
	                if (cache.contains(this.scenarioExecutionId)){
	                	
	                	CachedScenarioConfiguration config = cache.getScenario(this.scenarioExecutionId);
		                // Create scenario context
		            	logger.info("Replay mode enabled; replaying scenario " + this.scenarioExecutionId + 
		            			"( base date = " + config.getBaseDate() +
		            			"; forecast FY = " + config.getForecastFiscalYear() +
		            			"; classifier = " + config.getClassifier() + " )");
		            	ScenarioExecution exec = new ScenarioExecution(this,
		                        config.getBaseDate(),
		                        config.getForecastFiscalYear(),
		                        config.getClassifier());
		                execQueue.addAll(exec.initializeReplay(cache, this.databaseConnectionFactory, this.scenarioExecutionId));
	                }
	                else{
	                	logger.error("ScenarioExecutionId: " + this.scenarioExecutionId + " could not be found! Verify Id exists in ConfigurationCache.xml");
	                }
	                
	            	break;
	            default:
	            	throw new IllegalStateException("This should be unreachable code: Check MonteCarloMode enum");
            }
            
            // Log the completed data dependency graph before clearing the dao
            logger.debug(dao.toString());

            // Now that everything is wired together using marshallers, the descriptor-->marshaller mapping is no longer needed
            this.dao.clearMap();

            return execQueue;
    }
    
    private void deepDataValidate(List<Job> execQueue,boolean execute) {
        boolean valid = true;
        StringBuilder failedTaskNames = new StringBuilder("");
        for (Job job : execQueue) {
            for (AbstractTask task: job)	{
                if (task.isEnabled()) {
                    logger.info("Validating task " + (task.getClass().getSimpleName() + "..."));
                    if (!task.validate(AbstractTask.VALIDATION_LEVEL.DEEP)){
                        valid = false;
                        failedTaskNames.append("\t" + task.getClass().getSimpleName() + "\n");
                        logger.fatal("ERROR validating data for task " + task.getClass().getSimpleName());
                    } else {
                        logger.info((task.getClass().getSimpleName() + " data validated OK"));
                    }
                }
            }
        }

        //Only throw an exception if we're trying to run the scenario.
        if (execute && !valid) {
            throw new IllegalStateException("The following tasks in " + this.batchName + " failed validation.\n\t" + failedTaskNames.toString().trim());
        }
    }

	private void execute(List<Job> execQueue) throws Exception {
		switch (this.monteCarloMode)
        {
            case ON:
            	 // disable Report generation due to file locking concerns.
            	AdHocDataAccess.setLogLevel(LogLevel.NONE);
            	executeOnExecutor(execQueue);
                break;
            case OFF:
            case REPLAY:
            	executeSerially(execQueue);
                break;
            default:
            	throw new IllegalStateException("This should be unreachable code: Check MonteCarloMode enum");
        }
	}
		
	private void executeSerially(List<Job> execQueue) {
        while (execQueue.size() > 0) {
            Job t = execQueue.remove(0);
            t.run();
        }
    }
	
	/**
	 * Note: If this is executed in non-Monte-Carlo mode, the reporting will not make much sense. But the justification for multithreading is MC
	 * so this should be fine. 
	 * @param execQueue
	 * @throws Exception
	 */
	private void executeOnExecutor(List<Job> execQueue) throws Exception {
		
		ExecutorService executor = executorServiceFactory.create();
		// By convention, the Job queue will be initialized with the Monte Carlo aggregation tasks as the last Job in the list,
		// so we queue all but the last one for execution on the Executor, then wait for them to finish, then run the MC aggregation
		Job mcAggregationJob = execQueue.remove(execQueue.size() - 1);
		int total = execQueue.size();
		assert total == this.sampleSize * this.innerLoopCount;
		
		try {
	        
			// This blocks until all tasks are completed
			List<Future<MonteCarloStatusReportRecord>> mcStatus = executor.invokeAll(execQueue, TIMEOUT_DAYS, TimeUnit.DAYS);
			
			List<MonteCarloStatusReportRecord> mcStatusReport = new ArrayList<MonteCarloStatusReportRecord>();
			for (Future<MonteCarloStatusReportRecord> status : mcStatus) {
				// This also blocks until completion, but all the Futures should be ready
				MonteCarloStatusReportRecord rec = status.get();
				mcStatusReport.add(rec);
			}

	        int successes = 0;
	        for (MonteCarloStatusReportRecord rec : mcStatusReport) {
	        	if (rec.isSuccess()) {
	        		successes++;
	        	}
	        }
			
	        if (successes > 0){ //only run MC aggregator if we have valid results to compute.
				// NOTE: we are not submitting the final MC job to the executor, but rather running it on the main control thread to ensure we don't exit early
	        	mcAggregationJob.run();
	        }else{
	        	logger.info("No successful runs completed.  Monte Carlo Output suppressed.");
	        }
        
	        // Finally, print the MC status report
	        AdHocDataAccess.dumpData(this.batchName, mcStatusReport, true);

	        // ...and the log summary
	        if (successes == total) {
	        	logger.info(successes + "/" + total + " Monte Carlo instances succeeded.");
	        } else {
	        	logger.error(successes + "/" + total + " Monte Carlo instances succeeded.");
	        	logger.error("Aggregate results are corrupted by the absence of expected data points.");
	        	logger.error("Check 'MonteCarloStatusReportRecord.dmp' file in report folder for failed runs.");
	        }
        
		} catch (InterruptedException ex) {
			// XXX: This looks redundant but it may be necessary to shutdown the pool before interrupting the thread, even if there's a shutdown in the finally
        	executor.shutdownNow();
        	logger.fatal("Thread interrupted.", ex);
        	Thread.currentThread().interrupt();
        } finally {
        	executor.shutdownNow();
        	this.databaseConnectionFactory.closeRemainingDatabases();
        }
    }
	
    /**
     * Starting from the end of this tasks' list, removes any disabled tasks.  If task is enabled, checks to make sure any data inputs have a valid data source.
     * If no data source is provided by user or provided data source is a disabled task, searches for first upstream task providing the data and uses that as source instead.
     * 
     */
    private void preprocessTaskList() {
    	
    	ListIterator<TaskConfiguration> downstreamItit = this.tasks.listIterator(this.tasks.size());
    	while (downstreamItit.hasPrevious()){
    		
    		TaskConfiguration downstreamTaskConfig = downstreamItit.previous();
    		if (downstreamTaskConfig.isEnabled()){
    			// explicitly check for TaskConfiguration class type.  Subclasses (e.g. AbstractOutputTaskConfiguration) can have undesired behavior and shouldn't have data dependencies anyways.
    			if (downstreamTaskConfig.getClass().equals(TaskConfiguration.class)){ 
    				for (Map.Entry<String, DataDescriptor> downstreamEntry : downstreamTaskConfig.getInputData().entrySet()){
    					
    					DataDescriptor inputDataDescriptor = downstreamEntry.getValue();
    					Class<?> inputDataType = inputDataDescriptor.getDataType();
    					if (inputDataDescriptor.getClass().equals(IntermediateDataDescriptor.class)){
    						
    						Object inputDataSource = ((IntermediateDataDescriptor) inputDataDescriptor).getDataSource();
    						if (inputDataSource == null || inputDataSource.getClass().equals(TaskConfiguration.class)){
    							
    							// we have finally confirmed all the appropriate class data types, now search task list for upstream data source
    							boolean sourceFound = false;
    							ListIterator<TaskConfiguration> upstreamItit = this.tasks.listIterator(downstreamItit.previousIndex()+1);
    							while (upstreamItit.hasPrevious() && !sourceFound){
    								
    								TaskConfiguration upstreamTaskConfig = upstreamItit.previous();
    								if (upstreamTaskConfig.getClass().equals(TaskConfiguration.class)){
	    								if (upstreamTaskConfig.isEnabled()){
		    								if (inputDataSource == null || upstreamTaskConfig.equals(inputDataSource)){ 
		    									for (Class<?> outputDataType : upstreamTaskConfig.getOutputData().values()){ 
		    										if (outputDataType.equals(inputDataType)){
		    											sourceFound = true;
		    											if (inputDataSource == null){ // only set new if data source was never specified by user or if original source is disabled.
		    												logger.debug(downstreamTaskConfig.toString() + " input dataType: " + inputDataType.getSimpleName() + " has a NEW data source: " + upstreamTaskConfig.toString());
		    												((IntermediateDataDescriptor) inputDataDescriptor).setDataSource(upstreamTaskConfig);
		    											}
		    											break;
		    										}
		   										}
		    								}
		    								
	    								}else if (inputDataSource == null || upstreamTaskConfig.equals(inputDataSource)){
	    									
	    									// check special case: if upstream task gets the data from a File there will be no other upstream task providing data 
	    									for (DataDescriptor dd : upstreamTaskConfig.getInputData().values()){
	    										if (dd.getDataType().equals(inputDataType)){
	    											if (dd.getClass().equals(FileDataDescriptor.class)){
	    												logger.debug(downstreamTaskConfig.toString() + " input dataType: " + inputDataType.getSimpleName() + " has a NEW FileDataDecriptor data source: " + dd.toString());
	    												downstreamEntry.setValue(dd); // HK: this overwrites original copy in downstream TaskConfiguration.  Setting the DataDescriptor's data source directly fails validation later on...this doesn't seem right.
	    												sourceFound = true;
	    												break;
	    											}
	    										}
	    									}
	    									
	        								if (!sourceFound && inputDataSource != null){
	        									// referenced upstream task is disabled, set source to null so we will simply find next available task that outputs data.
	        									logger.debug(downstreamTaskConfig.toString() + " input dataType: " + inputDataType.getSimpleName() + " has a DISABLED source: " + inputDataSource.toString() + ".  Search for 1st available source.");
	        									inputDataSource = null;
	        								}
	    								}
    								}
    							}
    							
    							if (!sourceFound && !inputDataDescriptor.isFaultTolerant()){
    								throw new ExitException("Processing pipeline corrupted.  " + downstreamTaskConfig.toString() + " couldn't find data source for: " + inputDataType.getCanonicalName()); 
    							}
    						}
    					}
    				}
    			}
    			
    		}else{
    			downstreamItit.remove();
    		}
    	}
    }

    /**
     * Inspect input data descriptors for all failedTaskNames. File or parameterized data should exist, but
     * parameterized data cannot be validated yet. Intermediate data should be listed as an output
     * from the source task.
     * 
     * @throws DataAccessException
     */
    private void validateDataFlow() throws DataAccessException {
        List<String> missingFiles = new ArrayList<String>();
        for (int iTask=0; iTask < this.tasks.size(); ++iTask) {
        	TaskConfiguration task = this.tasks.get(iTask);
            for (DataDescriptor descriptor : task.getInputData().values()) {
                if (descriptor instanceof IntermediateDataDescriptor) {
                    // For intermediates, we need to identify the source, then search through its
                    // output data for the appropriate data type
                    IntermediateDataDescriptor intermediate = (IntermediateDataDescriptor) descriptor;
                    String type = intermediate.getDataType().getName();
                    Object source = intermediate.getDataSource();
                    
                    boolean found = false;
                    if (source instanceof TaskConfiguration) {
                    	// first makes sure sourceTask isn't downstream of current task.
                    	TaskConfiguration sourceTask = ((TaskConfiguration) source);
                    	boolean downstreamDependency = true;
                    	for (int iSource=0; iSource < this.tasks.size(); ++iSource){
                    		if (this.tasks.get(iSource) == sourceTask){  // one of the few times we want Object equality (==)
                    			if (iSource < iTask){
                    				downstreamDependency = false;
                    			}
                    			break;
                    		}
                    	}
                    	if (downstreamDependency){
                    		throw new ExitException("Corrupt batch configuration: " + task.toString() + " has an Intermediate data descriptor " + intermediate.toString() + " referencing a downstream task.");
                    	}
                    	
                    	// now make sure sourceTask outputs desired data type.
                        for (Class<?> output : sourceTask.getOutputData().values()) {
                            if (output.getName().equals(type)) {
                                // Dependency valid
                                found = true;
                                break;
                            }
                        }
                        if (!found) {
                            throw new ExitException("Corrupt batch configuration: Intermediate data descriptor " + intermediate.toString() + " identifies data source that does not produce an output of the same type");
                        }
                    } else {
                        throw new ExitException("Corrupt batch configuration: Intermediate data descriptor " + intermediate.toString() + " identifies a non-task object as its data source");
                    }
                } else if (descriptor instanceof FileDataDescriptor) {
                    // File descriptors: check for existence
                    // Note: parameterized data cannot be verified at this point because it varies
                    // on a per-scenario basis

                    // We need to make sure the descriptor has been registered with the dao before
                    // testing for existence
                    try {
                        dao.getMarshaller(descriptor);
                        if (!dao.exists(descriptor)) {
                            missingFiles.add(descriptor.toString());
                        }
                    } catch (NullPointerException npe) {
                        missingFiles.add(descriptor.toString());
                    }
                }
            }
        }
        if (!missingFiles.isEmpty()) {
            StringBuilder message = new StringBuilder("Missing input data:\n");
            for (String file : missingFiles) {
                message.append("\t" + file + "\n");
            }
            throw new ExitException(message.toString());
        }
    }

    /**
     * Validates that all input, output, and configuration items map to bean properties of the
     * TaskConfiguration target
     */
    private void validateTaskConfigurations() {
        for (TaskConfiguration task : this.tasks) {
            task.validateConfiguration();
        }
    }
    // /
    // / Moved from RunImportData
    // /
    private List<DataDescriptor> defaultImports = new ArrayList<DataDescriptor>();
    private List<DataDescriptor> defaultExports = new ArrayList<DataDescriptor>();
    private TemplateImporter templateImporter;

    public List<DataDescriptor> getDefaultImports() {
        return this.defaultImports;
    }

    public void setDefaultImports(List<DataDescriptor> defaultImports) {
        this.defaultImports = defaultImports;
    }

    public List<DataDescriptor> getDefaultExports() {
        return this.defaultExports;
    }

    public void setDefaultExports(List<DataDescriptor> defaultExports) {
        this.defaultExports = defaultExports;
    }

    public TemplateImporter getTemplateImporter() {
        return templateImporter;
    }

    public void setTemplateImporter(TemplateImporter templateImporter) {
        this.templateImporter = templateImporter;
    }

    public void registerMetadata(MappedDataAccess dao) {
        // TODO: while everything is in memory we can't pre-load this, but with a better data layer
        // back-end we should import pre-emptively
        try {
            if (templateImporter != null) {
                // TODO: there should be a better way of searching the data directory
                FileDataDescriptor dataDir = new FileDataDescriptor();
                dataDir.setLocation(LOCATION.DATA);
                dataDir.setResourceName("");
                templateImporter.run(dao.getAbsoluteFile(dataDir), dao);
            }

            for (DataDescriptor d : this.defaultImports) {
                dao.getMarshaller(d);
            }
            for (DataDescriptor d : this.defaultExports) {
                dao.getMarshaller(d);
            }
        } catch (DataAccessException ex) {
            logger.trace(ex.getStackTrace());
            throw new ExitException("TODO", ex);
        }
    }

    /**
     * To avoid redundant loading of static data, FileDataDescriptors are given special handling.
     * During scenario initialization, the data is loaded and linked to this Batch as the data
     * source for an IntermediateDataDescriptor. Any task referencing the FileDataDescriptor is
     * rerouted to the intermediate data by this method.
     * 
     * @param descriptor
     * @return
     * @throws DataAccessException
     */
    public DataMarshaller loadStaticData(DataDescriptor descriptor) throws DataAccessException {
//        IntermediateDataDescriptor newDescriptor = new IntermediateDataDescriptor();
//        newDescriptor.setDataSource(this);
//        newDescriptor.setDataType(descriptor.getDataType());
//        newDescriptor.setPersistent(false); // Hold in memory
//
//        // Check to see if it has already been loaded
//        if (!dao.exists(newDescriptor)) {
//            List<?> data = new ArrayList();
//            dao.load(descriptor, data);
//            dao.save(newDescriptor, data);
//        }
//
//        return dao.getMarshaller(newDescriptor);
        
    	// XXX: Loading all static data takes less than 3 seconds, which does not meaningfully impact run time
    	// But it does expose risk of data corruption from one instance to the next because the objects in the
    	// collection aren't necessarily immutable. This should be revisited if/when the data layer is revised
    	// to use a different storage medium (i.e. database)
        return dao.getMarshaller(descriptor);
    }

    /**
     * configure all Monte Carlo settings based on Monte Carlo mode (ON, OFF or REPLAY).
     */
    private void configMonteCarloSettings(ScenarioApplicationContext ctx) {
        GlobalApplicationContext gac = GlobalApplicationContext.getInstance();
        // TODO: For thread safety, it is probably better to do this at the global level. But we
        // don't have a global config file that
        // is user editable at this time.
        AdHocDataAccess.setLogLevel(this.logLevel);
        AdHocDataAccess.setResourceManager(dao);
        
        if (this.monteCarloMode.equals(MonteCarloMode.ON)) {
            // TODO: STS - This is not thread-safe. We should look into synchronizing the updateLog4jSettings method.
            if (Batch.initializeLogging) {
                Batch.initializeLogging = false;
                ctx.updateLog4jSettings(ScenarioApplicationContext.Log4jSetting.MONTE_CARLO);
            }
            //AdHocDataAccess.setLogLevel(LogLevel.NONE); //this disables all logging to Report folder
            
            // Disable all file output that doesn't originate from a Monte Carlo module
            for (TaskConfiguration task : this.tasks) {
            	if (task instanceof AbstractOutputTaskConfiguration)
            	{
            		Object source = ((AbstractOutputTaskConfiguration)task).getData().getDataSource();
            		if (!source.getClass().getPackage().getName().equals(MONTE_CARLO_PACKAGE)) {
            			task.setEnabled(false);
            		}
            	}
            }
            
            // Saving to the ConfigurationCache is delegated to TaskConfigurations
        } else if (this.monteCarloMode.equals(MonteCarloMode.OFF)) {
            ctx.updateLog4jSettings(ScenarioApplicationContext.Log4jSetting.NORMAL);
            
            // In normal mode, disable all Monte Carlo modules
            
            for (TaskConfiguration task : this.tasks) {
                if (task.getClass().getPackage().getName().equals(MONTE_CARLO_PACKAGE)) {
                    task.setEnabled(false);
                }
            }

        } else if (this.monteCarloMode.equals(MonteCarloMode.REPLAY)) {
            ctx.updateLog4jSettings(ScenarioApplicationContext.Log4jSetting.REPLAY);
            
            // In replay mode, set sample size to one, do NOT disable Monte Carlo modules,
            // and override configuration properties from the configuration cache rather than
            // sample from randomizedConfiguration. All of this logic is delegated to
            // ScenarioExecution and TaskConfigurations...but it's worth noting the intended behavior here
            
        } else {

            logger.warn("Proper Monte Carlo mode not found.  Setting to OFF.");

            this.monteCarloMode = MonteCarloMode.OFF;
            this.sampleSize = 1;

            for (TaskConfiguration task : this.tasks) {
                if (task.getClass().getPackage().getName().equals(MONTE_CARLO_PACKAGE)) {
                    task.setEnabled(false);
                }
            }
        }
    }

    /**
     * Loads the cache
     * @return
     * @throws DataAccessException
     */
    private ConfigurationCache loadConfigurationCache() throws DataAccessException {
    	// Just in case, we don't want to crash the simulation because we can't find the cache
    	this.configurationCache.setFaultTolerant(true);
    	
		List<ConfigurationCache> cache = new ArrayList<ConfigurationCache>();
		this.dao.load(this.configurationCache, cache);
		
		// File may not exist, in which case we create a new one
		if (cache.size() == 0)
		{
			return new ConfigurationCache();
		}
		return cache.get(0);
	}
	
    /**
     * Saves the cache
     * @param cache
     * @throws DataAccessException
     */
    private void saveConfigurationCache(ConfigurationCache cache) throws DataAccessException {
		this.dao.save(this.configurationCache, Arrays.asList(new ConfigurationCache[] { cache }));
	}
}
