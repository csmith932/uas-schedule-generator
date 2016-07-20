package gov.faa.ang.swac.controller.core;

import java.util.Map;
import java.util.Random;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import gov.faa.ang.swac.controller.ExitException;
import gov.faa.ang.swac.controller.core.Batch.MonteCarloMode;
import gov.faa.ang.swac.controller.core.component.GenericFileOutputBean;
import gov.faa.ang.swac.controller.core.montecarlo.replay.ConfigurationCache;
import gov.faa.ang.swac.datalayer.DataAccessException;
import gov.faa.ang.swac.datalayer.MappedDataAccess;
import gov.faa.ang.swac.datalayer.identity.FileDataDescriptor;
import gov.faa.ang.swac.datalayer.identity.IntermediateDataDescriptor;
import gov.faa.ang.swac.datalayer.storage.DataMarshaller;
import gov.faa.ang.swac.datalayer.storage.fileio.FileMarshaller;

public class GenericFileOutputTaskConfiguration extends AbstractOutputTaskConfiguration
{
	private static final Logger logger = LogManager.getLogger(GenericFileOutputTaskConfiguration.class);

	private FileDataDescriptor file;
	private boolean prependScenarioName = true;
	private boolean appendSwacVersion = true;
	
	public FileDataDescriptor getFile() {
		return file;
	}

	public void setFile(FileDataDescriptor file) {
		this.file = file;
	}
	
	public boolean isPrependScenarioName() {
		return prependScenarioName;
	}

	public void setPrependScenarioName(boolean prependScenarioName) {
		this.prependScenarioName = prependScenarioName;
	}

	public boolean isAppendSwacVersion() {
		return appendSwacVersion;
	}

	public void setAppendSwacVersion(boolean appendSwacVersion) {
		this.appendSwacVersion = appendSwacVersion;
	}

	@Override
	public AbstractTask createTaskExecution(ScenarioExecution scenario, 
			MappedDataAccess dao, 
			int instanceId,
			Random random) throws DataAccessException
	{
		GenericFileOutputBean execution = new GenericFileOutputBean();
		
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
		logger.debug("Initializing generic file output task execution");
		
		// Cast the execution - should never be called with any other class
		if (!(execution instanceof GenericFileOutputBean))
		{
			throw new IllegalStateException();
		}
		GenericFileOutputBean fileOutput = (GenericFileOutputBean)execution;
		
		// Link the input
		IntermediateDataDescriptor intermediate = new IntermediateDataDescriptor((IntermediateDataDescriptor)this.data); // Copy to avoid tampering with configuration
		intermediate.setInstanceId(execution.getInstanceId());

		DataMarshaller marshaller = dao.getMarshaller(intermediate); // Register the linkage with the data layer
		if (marshaller == null)
		{
			throw new ExitException("Error retrieving data marshaller for descriptor " + this.data.toString());
		}
		logger.debug("Setting input data marshaller: task=" + execution.toString() + "; marshaller=" + marshaller.toString());
		fileOutput.setInputData(marshaller);
		
		// ...and then the output
		FileMarshaller out = (FileMarshaller)dao.getMarshaller(getScenarioSpecificOutputFile(scenario));
		if (out == null)
		{
			throw new ExitException("Error retrieving data marshaller for descriptor " + this.file.toString());
		}
		logger.debug("Setting output data marshaller: task=" + execution.toString() + "; marshaller=" + out.toString());
		fileOutput.setOutputFile(out);
	}
	
	/**
	 * Workaround for parameterized output file names
	 * @return
	 */
	private FileDataDescriptor getScenarioSpecificOutputFile(ScenarioExecution scenario)
	{
		StringBuilder fName = new StringBuilder();
		if (this.prependScenarioName)
		{
			fName.append(scenario.getScenarioName());
		}
		String resourceName = this.file.getResourceName();
		if (this.appendSwacVersion)
		{
			int extensionIdx = this.file.getResourceName().lastIndexOf('.');
			fName.append(resourceName.substring(0,extensionIdx));
			fName.append(scenario.getParent().getSwacVersion());
			fName.append(resourceName.substring(extensionIdx));
		}
		else
		{
			fName.append(this.file.getResourceName());
		}
		
		FileDataDescriptor retVal=null;
		try {
			retVal = new FileDataDescriptor(this.file,"");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new IllegalStateException(e);
		}
		retVal.setResourceName(fName.toString());
		return retVal;
	}

	///
	/// Flag normal setters & getters for TaskConfiguration as illegal
	///
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("GenericFileOutputBean");
		builder.append("#");
		builder.append(this.hashCode());
		return builder.toString();
	}
}
