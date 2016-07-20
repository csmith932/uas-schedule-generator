/**
 * Copyright "2013", Metron Aviation & CSSI. All rights reserved. This computer Software was
 * developed with the sponsorship of the U.S. Government under Contract No. DTFAWA-10-D-00033, which
 * has a copyright license in accordance with AMS 3.5-13.(c)(1).
 */
package gov.faa.ang.swac.controller.core;

import gov.faa.ang.swac.datalayer.MappedDataAccess;
import gov.faa.ang.swac.datalayer.storage.MemoryMarshaller;
import gov.faa.ang.swac.datalayer.storage.StreamingDataMarshaller;
import gov.faa.ang.swac.datalayer.storage.db.JDBCConnectionFactory;

import java.util.Vector;
import java.util.concurrent.Callable;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * Grouping of tasks that can be sent out for parallel processing independently of other jobs.
 * 
 * @author csmith
 *
 */
public class Job extends Vector<AbstractTask> implements Runnable, Callable<MonteCarloStatusReportRecord> {
	private static final Logger logger = LogManager.getLogger(Job.class);
        
    private final JDBCConnectionFactory connection;
    private final MappedDataAccess mda; 
    private Integer instanceId = null;
    
    public Job() {
        super();
        this.connection = null;
        this.mda = null;
    }
    
    public Job(JDBCConnectionFactory connection, MappedDataAccess mda) {
        super();
        this.connection = connection;
        this.mda = mda;
    }

    public void setInstanceId(int instanceId) {
        this.instanceId = instanceId;
    }
    
	@Override
	public void run() {
		try {
			execute();
		} catch (Throwable ex) {
			// XXX: Because of this universal catch, it is impossible for anything to propagate up the stack from here. Some legacy calling code includes try/catches that are now useless
			logger.fatal("Unrecoverable error. Aborting SWAC. To troubleshoot, see debug log and stack trace.");
			logger.debug("Unrecoverable error. Aborting SWAC. To troubleshoot, see debug log and stack trace.", ex);
			ex.printStackTrace();
		}
	}	
	
	@Override
	public MonteCarloStatusReportRecord call() throws Exception {
		double time = System.currentTimeMillis();
		MonteCarloStatusReportRecord status = new MonteCarloStatusReportRecord();
		try {
				status.setScenarioExecutionId(this.instanceId);
				
				execute();
			
				time = (System.currentTimeMillis() - time)/1000.0;
				status.setRunTime(time);
				status.setSuccess(true);
				status.setErrorMessage("");
				logger.info("SCENARIO_EXECUTION_ID=" + this.instanceId + " completed successfully in " + time + " seconds.");
		} catch (Throwable ex) {
			// XXX: Because of this universal catch, it is impossible for anything to propagate up the stack from here. Some legacy calling code includes try/catches that are now useless
			time = (System.currentTimeMillis() - time)/1000.0;
			status.setRunTime(time);
			status.setSuccess(false);
			status.setErrorMessage(ex.getMessage());
			logger.fatal("Unrecoverable error in SCENARIO_EXECUTION_ID=" + this.instanceId + ". Aborting instance. To troubleshoot, execute replay with this parameter.");
			logger.debug("Unrecoverable error in SCENARIO_EXECUTION_ID=" + this.instanceId + ". Aborting instance. To troubleshoot, execute replay with this parameter.", ex);
			ex.printStackTrace();
		} 
		
		return status;
		
	}
	
	private void execute() throws Exception {
		try {
			if (this.connection != null && this.instanceId != null){
				logger.debug("Initializing Db for instanceId=" + this.instanceId);
				this.connection.executeDbScripts(this.mda, this.instanceId.toString());
			}		
		
			
	        while (!isEmpty()) {
	            AbstractTask t = remove(0);
	            
	            t.runWithCleanup();
	            if (t.abort != null) {
	                throw t.abort;
	            }
	            
	            // HK: Do not invoke Garbage Collection as it stops all Threads from executing after each Task.
	            // TODO: In search of memory leaks!
	            t = null;
	            
	            logger.debug("In-memory collections referenced by data layer:");
	            MemoryMarshaller.logIntermediateDataUsage();
	            StreamingDataMarshaller.logIntermediateDataUsage();
	            logger.debug("Free memory remaining=" + Runtime.getRuntime().freeMemory() + " bytes");
	        }
	        
		} finally {
			if (this.connection != null && this.instanceId != null) {
				logger.debug("Closing Db for instanceId=" + this.instanceId);
				this.connection.closeDatabaseInstance(this.instanceId);
			}
			
	        while (!isEmpty()) { //cleanup in case Job exits prematurely.
	            AbstractTask t = remove(0);
	            t.cleanup();
	            t = null;
	        }
		}
    }
}
