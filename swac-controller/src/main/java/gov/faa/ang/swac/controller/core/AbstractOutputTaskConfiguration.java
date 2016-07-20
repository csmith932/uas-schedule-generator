package gov.faa.ang.swac.controller.core;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import gov.faa.ang.swac.controller.core.component.GenericFileOutputBean;
import gov.faa.ang.swac.datalayer.identity.DataDescriptor;
import gov.faa.ang.swac.datalayer.identity.IntermediateDataDescriptor;

/**
 * Abstract task configuration class for report writing and uploading
 */
public abstract class AbstractOutputTaskConfiguration extends TaskConfiguration
{
	private static final Logger logger = LogManager.getLogger(AbstractOutputTaskConfiguration.class);

	protected IntermediateDataDescriptor data;
	
	public IntermediateDataDescriptor getData() {
		return data;
	}

	public void setData(IntermediateDataDescriptor data) {
		this.data = data;
	}


	@Override
	public boolean isGlobal()
	{
		// Use the same global/local behavior as the data source
		if (!(this.data.getDataSource() instanceof TaskConfiguration))
		{
			throw new IllegalStateException();
		}
		else
		{
			TaskConfiguration dataSource = (TaskConfiguration)this.data.getDataSource();
			return dataSource.isGlobal();
		}
	}
	
	@Override
	public boolean isEnabled()
	{
		// Trivial case
		if (!super.isEnabled())
		{
			return false;
		}
		
		// Use the same enabled behavior as the data source
		if (!(this.data.getDataSource() instanceof TaskConfiguration))
		{
			throw new IllegalStateException();
		}
		else
		{
			TaskConfiguration dataSource = (TaskConfiguration)this.data.getDataSource();
			return dataSource.isEnabled();
		}
	}
	
	public void validateConfiguration() {
		// TODO: Does anything need to be validated? 
	}

	///
	/// Flag normal setters & getters for TaskConfiguration as illegal
	///
	
	// TODO: Probably better to extract common behavior to an interface and not inherit directly from TaskConfiguration
	
	@Override
	public Class<? extends CloneableAbstractTask> getTarget() {
		return GenericFileOutputBean.class;
	}
	
	@Override
	public Map<String, DataDescriptor> getInputData() {
		Map<String, DataDescriptor> retVal = new HashMap<String, DataDescriptor>();
		retVal.put("inputData", this.data);
		return retVal;
	}
	@Override
	public void setInputData(Map<String, DataDescriptor> inputData) {
		throw new UnsupportedOperationException();
	}
	@Override
	public Map<String,Class<?>> getOutputData() {
		throw new UnsupportedOperationException();
	}
	@Override
	public void setOutputData(Map<String,Class<?>> outputData) {
		throw new UnsupportedOperationException();
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AbstractOutputTaskConfiguration");
		builder.append("#");
		builder.append(this.hashCode());
		return builder.toString();
	}
}
