/**
 * Copyright "TBD", Metron Aviation & CSSI. All rights reserved. This computer Software was
 * developed with the sponsorship of the U.S. Government under Contract No. DTFAWA-10-D-00033, which
 * has a copyright license in accordance with AMS 3.5-13.(c)(1).
 */

package gov.faa.ang.swac.controller.core.montecarlo;

import java.lang.reflect.Method;
import java.util.Random;
import java.util.Map.Entry;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import gov.faa.ang.swac.controller.ExitException;
import gov.faa.ang.swac.controller.core.AbstractTask;
import gov.faa.ang.swac.controller.core.TaskConfiguration;
import gov.faa.ang.swac.controller.core.ScenarioExecution;
import gov.faa.ang.swac.datalayer.DataAccessException;
import gov.faa.ang.swac.datalayer.MappedDataAccess;
import gov.faa.ang.swac.datalayer.identity.DataDescriptor;
import gov.faa.ang.swac.datalayer.identity.IntermediateDataDescriptor;
import gov.faa.ang.swac.datalayer.identity.StreamingDataDescriptor;
import gov.faa.ang.swac.datalayer.storage.DataMarshaller;

public class MonteCarloReduceTaskConfiguration extends TaskConfiguration 
{
	private static final Logger logger = LogManager.getLogger(MonteCarloReduceTaskConfiguration.class);

	@Override
	public AbstractTask createTaskExecution(ScenarioExecution scenario, 
			MappedDataAccess dao, 
			int instanceId,
			Random random) throws DataAccessException
	{
		// Identify reduce tasks with the scenario as a whole, not the individual instance
		return super.createTaskExecution(scenario, dao, scenario.getScenarioId(), random);
	}
	
	/**
	 * In the Monte Carlo reduce override, we look up the streaming marshallers and their instance-specific sources, and hook listeners
	 */
	@Override
	public void linkInputs(AbstractTask execution,
			ScenarioExecution scenario, 
			MappedDataAccess dao, 
			int instanceId) throws DataAccessException
	{
		logger.debug("Initializing Monte Carlo reduce task execution: " + execution.toString());
		for (Entry<String,DataDescriptor> entry : this.getInputData().entrySet())
		{
			// Retrieve the JavaBean property setter for this property name. TaskConfiguration should automatically resolve the type to DataMarshaller
			Method setter = this.getSetter(entry.getKey(), DataMarshaller.class, execution);
			if (setter == null)
			{
				throw new ExitException("Invalid linkage between TaskConfiguration input data specification and target task: "
						+ "task=" + execution.getTaskName()
						+ "; property=" + entry.getKey()
						+ "; data=" + entry.getValue().toString());
			}
			
			DataDescriptor descriptor = entry.getValue();
			
			DataMarshaller marshaller = null;
			
			if (descriptor instanceof StreamingDataDescriptor)
			{
				marshaller = scenario.getParameterizedMarshaller((StreamingDataDescriptor)descriptor); // Register the linkage with the data layer
				
				// Now we look up the intermediate data descriptor
				DataDescriptor baseDescriptor = ((StreamingDataDescriptor)descriptor).getBaseDescriptor();
				if (!(baseDescriptor instanceof IntermediateDataDescriptor))
				{
					throw new ExitException("IntermediateDataDescriptors are the only well defined inputs to StreamingDataDescriptors: " + baseDescriptor);
				}
				IntermediateDataDescriptor intermediate = new IntermediateDataDescriptor((IntermediateDataDescriptor)baseDescriptor); // Defensive copy
				intermediate.setInstanceId(instanceId); // Set to the current instance
				
				// Now we hook a listener
				DataMarshaller intermediateMarshaller = dao.getMarshaller(intermediate); 
				logger.debug("Monte Carlo reduce task hooking a listener for input data: publisher=" + intermediateMarshaller + "; subscriber=" + marshaller.toString());
				intermediateMarshaller.subscribe(marshaller);
				
				invokeSetter(setter, execution, marshaller); // This may be executed redundantly, but it shouldn't matter
			}
			else
			{
				throw new ExitException("Invalid data descriptor type");
			}
		}
		
		
	}
}
