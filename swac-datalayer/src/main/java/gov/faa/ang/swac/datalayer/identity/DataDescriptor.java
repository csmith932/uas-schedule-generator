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

/**
 * Abstract base class for data location identifiers. The datalayer interacts with disparate data sources, ranging from
 * files to in-memory data stores. DataDescriptors can be typed to different sources, but used polymorphically in calling
 * code as keys for a key-value store interface to the datalayer 
 * @author csmith
 *
 */
public abstract class DataDescriptor implements Cloneable
{
	private boolean faultTolerant;
	
	@Override
	public String toString() {
		return "DataDescriptor [dataType=" + dataType + "]";
	}

	private Class<?> dataType;

	public final Class<?> getDataType()
	{
		return dataType;
	}

	public final void setDataType(Class<?> dataType)
	{
		this.dataType = dataType;
	}
	
	public DataDescriptor()
	{
		this.faultTolerant = false;
	}

	public DataDescriptor(DataDescriptor source)
	{
		this.dataType = source.dataType;
		this.faultTolerant = source.faultTolerant;
	}

	public abstract DataMarshaller createMarshaller(ResourceManager resMan) throws DataAccessException;

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dataType == null) ? 0 : dataType.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (obj == null)
		{
			return false;
		}
		if (getClass() != obj.getClass())
		{
			return false;
		}
		DataDescriptor other = (DataDescriptor) obj;
		if (dataType == null)
		{
			if (other.dataType != null)
			{
				return false;
			}
		} else if (!dataType.equals(other.dataType))
		{
			return false;
		}
		return true;
	}
        
        @Override
        public abstract DataDescriptor clone() throws CloneNotSupportedException;
        
        public void setFaultTolerant(boolean faultTolerant)
    	{
    		this.faultTolerant = faultTolerant;
    	}

    	public boolean isFaultTolerant()
    	{
    		return faultTolerant;
    	}
}
