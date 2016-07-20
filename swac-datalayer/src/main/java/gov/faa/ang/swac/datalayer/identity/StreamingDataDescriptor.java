package gov.faa.ang.swac.datalayer.identity;


import gov.faa.ang.swac.datalayer.DataAccessException;
import gov.faa.ang.swac.datalayer.ResourceManager;
import gov.faa.ang.swac.datalayer.storage.DataMarshaller;
import gov.faa.ang.swac.datalayer.storage.StreamingDataMarshaller;

public class StreamingDataDescriptor extends ParameterizedDataDescriptor
{
	public StreamingDataDescriptor()
	{
		super();
	}
	
	public StreamingDataDescriptor(StreamingDataDescriptor val)
	{
		super(val);
	}
	
	@Override
	public DataMarshaller createMarshaller(ResourceManager resMan)
			throws DataAccessException {
		StreamingDataMarshaller retVal = new StreamingDataMarshaller(this.getDataType(), this.getBaseDescriptor().toString());
		return retVal;
	}

	@Override
	public String toString() {
		return "StreamingDataDescriptor [baseDescriptor=" + baseDescriptor
				+ ", baseDate=" + baseDate + ", forecastFiscalYear="
				+ forecastFiscalYear + ", classifier=" + classifier
				+ ", getDataType()=" + getDataType().getSimpleName() + "]";
	}
	
	
	@Override
	public StreamingDataDescriptor clone()
	{
		return new StreamingDataDescriptor(this);
	}
}
