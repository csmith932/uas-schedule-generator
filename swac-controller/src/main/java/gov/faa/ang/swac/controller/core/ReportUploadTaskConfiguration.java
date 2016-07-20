/**
 * Copyright "TBD", Metron Aviation & CSSI. All rights reserved.
 *
 * This computer Software was developed with the sponsorship of the U.S.
 * Government under Contract No. DTFAWA-10-D-00033, which has a copyright
 * license in accordance with AMS 3.5-13.(c)(1).
 */
package gov.faa.ang.swac.controller.core;

import gov.faa.ang.swac.controller.ExitException;
import gov.faa.ang.swac.controller.core.Batch.MonteCarloMode;
import gov.faa.ang.swac.controller.core.montecarlo.replay.ConfigurationCache;
import gov.faa.ang.swac.datalayer.DataAccessException;
import gov.faa.ang.swac.datalayer.MappedDataAccess;
import gov.faa.ang.swac.datalayer.identity.DataAccessObjectDescriptor;
import gov.faa.ang.swac.datalayer.identity.IntermediateDataDescriptor;
import gov.faa.ang.swac.datalayer.storage.DataMarshaller;
import gov.faa.ang.swac.datalayer.storage.db.DataAccessObject;
import gov.faa.ang.swac.datalayer.storage.db.UploadableRecord;

import java.util.Map;
import java.util.Random;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class ReportUploadTaskConfiguration extends AbstractOutputTaskConfiguration  
{
	private static final Logger logger = LogManager.getLogger(ReportUploadTaskConfiguration.class);
	
	@Override
	public AbstractTask createTaskExecution(ScenarioExecution scenario, 
			MappedDataAccess dao, 
			int instanceId,
			Random random) throws DataAccessException
	{
		RunReportDBUploader execution = new RunReportDBUploader();
		
		execution.setEnabled(this.isEnabled());
		execution.setInstanceId(this.isGlobal() ? scenario.getScenarioId() : instanceId);

		return execution;
	}
	
	@Override
	public void initializeTaskExecution(AbstractTask execution,
			ScenarioExecution scenario, 
			MappedDataAccess dao, 
			int instanceId,
			Random random,
			ConfigurationCache cache,
			Map<String, Map<String, Double>> outerLoopValues,
			MonteCarloMode mode) throws DataAccessException
	{
		logger.debug("Initializing report db upload task execution");
		
		// Cast the execution - should never be called with any other class
		if (!(execution instanceof RunReportDBUploader))
		{
			throw new IllegalStateException();
		}
		RunReportDBUploader dbUploader = (RunReportDBUploader)execution;
		
		// Link the input
		IntermediateDataDescriptor intermediate = new IntermediateDataDescriptor((IntermediateDataDescriptor)this.data); // Copy to avoid tampering with configuration
		intermediate.setInstanceId(execution.getInstanceId());

		//getDataAccessObjects
		DataMarshaller marshaller = dao.getMarshaller(intermediate); // Register the linkage with the data layer
		if (marshaller == null)
		{
			throw new ExitException("Error retrieving data marshaller for descriptor " + this.data.toString());
		}
		logger.debug("Setting input data marshaller: task=" + execution.toString() + "; marshaller=" + marshaller.toString());
		dbUploader.setInputRecords(marshaller);
		
		Map<String, DataAccessObjectDescriptor> daoDescriptorMap = this.getDataAccessObjects();
		DataAccessObjectDescriptor daoDescriptor = daoDescriptorMap.get("uploadDao");//--" + instanceId);
		daoDescriptor.setInstanceId(Integer.toString(instanceId));
		DataMarshaller updateDaoMarshaller = dao.getMarshaller(daoDescriptor); //npe here 2
		if (updateDaoMarshaller == null)
			throw new ExitException("Error retrieving data marshaller for descriptor " + daoDescriptor.toString());
		DataAccessObject<UploadableRecord> updateDao = (DataAccessObject<UploadableRecord> ) updateDaoMarshaller;
		
		logger.debug("Setting dao data marshaller: task=" + execution.toString() + "; marshaller=" + updateDao.toString());
		dbUploader.setUploadDao(updateDao);
	}

}