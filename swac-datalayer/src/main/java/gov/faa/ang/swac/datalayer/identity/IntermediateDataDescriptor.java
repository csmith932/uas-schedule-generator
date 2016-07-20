/**
 * Copyright "TBD", Metron Aviation & CSSI.  All rights reserved.
 * 
 * This computer Software was developed with the sponsorship of the U.S. Government
 * under Contract No. DTFAWA-10-D-00033, which has a copyright license in accordance with AMS 3.5-13.(c)(1).
 */

package gov.faa.ang.swac.datalayer.identity;

import gov.faa.ang.swac.datalayer.DataAccessException;
import gov.faa.ang.swac.datalayer.ResourceManager;
import gov.faa.ang.swac.datalayer.storage.DataMarshaller;
import gov.faa.ang.swac.datalayer.storage.MemoryMarshaller;
import gov.faa.ang.swac.datalayer.storage.fileio.FileMarshaller;

import java.io.IOException;

public class IntermediateDataDescriptor extends DataDescriptor
{

	private Object dataSource;
	private int instanceId;
	private boolean persistent;
	
	public Object getDataSource()
	{
		return dataSource;
	}

	public void setDataSource(Object dataSource)
	{
		this.dataSource = dataSource;
	}

	public int getInstanceId()
	{
		return instanceId;
	}

	public void setInstanceId(int instanceId)
	{
		this.instanceId = instanceId;
	}

	public boolean isPersistent()
	{
		return persistent;
	}

	public void setPersistent(boolean persistent)
	{
		this.persistent = persistent;
	}
	
	public IntermediateDataDescriptor()
	{
		super();
	}

	public IntermediateDataDescriptor(IntermediateDataDescriptor source)
	{
		super(source);
		this.dataSource = source.dataSource;
		this.persistent = source.persistent;
		this.instanceId = source.instanceId;
	}

	@Override
	public DataMarshaller createMarshaller(ResourceManager resMan) throws DataAccessException
	{
		if (this.persistent)
		{
			try
			{
				return new FileMarshaller(this.getDataType(), resMan.createTemporaryResource());
			} catch (IOException ex)
			{
				throw new DataAccessException("Error creating temporary file.", ex);
			}
		} else
		{
			// Keep this around because it allows for more verbose debug logging, but disable it for normal use because it generates ugly report names
			//MemoryMarshaller retVal = new MemoryMarshaller(this.getDataType(), this.toString());
			MemoryMarshaller retVal = new MemoryMarshaller(this.getDataType(), this.dataSource.toString());
			return retVal;
		}
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((dataSource == null) ? 0 : dataSource.hashCode());
		result = prime * result + instanceId;
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (!super.equals(obj))
		{
			return false;
		}
		if (getClass() != obj.getClass())
		{
			return false;
		}
		IntermediateDataDescriptor other = (IntermediateDataDescriptor) obj;
		if (dataSource == null)
		{
			if (other.dataSource != null)
			{
				return false;
			}
		} else if (!dataSource.equals(other.dataSource))
		{
			return false;
		}
		if (instanceId != other.instanceId)
		{
			return false;
		}
		return true;
	}
	
	@Override
	public String toString() {
		return "IntermediateDataDescriptor [dataSource=" + dataSource
				+ ", instanceId=" + instanceId
				+ ", dataType=" + getDataType().getSimpleName() + "]";
	}
        
        @Override
        public IntermediateDataDescriptor clone() {
            return new IntermediateDataDescriptor(this);
        }
}
