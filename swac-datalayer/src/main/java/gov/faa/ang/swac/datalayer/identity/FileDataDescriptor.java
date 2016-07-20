/**
 * Copyright "TBD", Metron Aviation & CSSI.  All rights reserved.
 * 
 * This computer Software was developed with the sponsorship of the U.S. Government
 * under Contract No. DTFAWA-10-D-00033, which has a copyright license in accordance with AMS 3.5-13.(c)(1).
 */

package gov.faa.ang.swac.datalayer.identity;

import gov.faa.ang.swac.datalayer.ResourceManager;
import gov.faa.ang.swac.datalayer.ResourceManager.LOCATION;
import gov.faa.ang.swac.datalayer.storage.DataMarshaller;
import gov.faa.ang.swac.datalayer.storage.ReadOnlyDataMarshaller;
import gov.faa.ang.swac.datalayer.storage.fileio.FileMarshaller;

public class FileDataDescriptor extends ParameterizedDataDescriptor
{
	private LOCATION location;
	private String resourceName;
	private boolean readOnly;
	private String schemaName;  
	
	public FileDataDescriptor() {
		super();
	}

	public FileDataDescriptor(FileDataDescriptor file) {
		super(file);
		this.location = file.location;
		this.resourceName = file.resourceName;
		this.readOnly = file.readOnly;
		this.schemaName=null;
	}
	
	/**
	 * 
	 * @param file
	 * @param schemaName
	 * @throws Exception
	 */
	public FileDataDescriptor(FileDataDescriptor file, String schemaName) throws Exception {
		super(file);
		this.location = file.location;
		this.resourceName = file.resourceName;
		this.readOnly = file.readOnly;
		this.schemaName=schemaName;
	}	
	
	public String getSchemaName(){
		return schemaName;
	}
	
	public void setSchemaName(String schemaName){
		this.schemaName=schemaName;
	}
	
	public LOCATION getLocation()
	{
		return location;
	}

	public void setLocation(LOCATION location)
	{
		this.location = location;
	}

	public String getResourceName()
	{
		return resourceName;
	}

	public void setResourceName(String resourceName)
	{
		this.resourceName = resourceName;	
	}

	public boolean isReadOnly()
	{
		return readOnly;
	}

	public void setReadOnly(boolean readOnly)
	{
		this.readOnly = readOnly;
	}

	@Override
	public DataMarshaller createMarshaller(ResourceManager resMan)
	{
		DataMarshaller marshaller = new FileMarshaller(this.getDataType(), resMan.getFile(this.location, this.resourceName), this.isFaultTolerant(),this.schemaName);
		if (this.readOnly)
		{
			marshaller = new ReadOnlyDataMarshaller(marshaller);
		}

		return marshaller;
	}
        
        @Override
        public FileDataDescriptor clone() {
            return new FileDataDescriptor(this);
        }

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((location == null) ? 0 : location.hashCode());
		result = prime * result + ((resourceName == null) ? 0 : resourceName.hashCode());
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
		FileDataDescriptor other = (FileDataDescriptor) obj;
		if (location != other.location)
		{
			return false;
		}
		if (resourceName == null)
		{
			if (other.resourceName != null)
			{
				return false;
			}
		} else if (!resourceName.equals(other.resourceName))
		{
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		Class<?> dataType = getDataType();
		return "FileDataDescriptor [location=" + location + ", resourceName="
				+ resourceName + ", readOnly=" + readOnly + ", faultTolerant="
				+ isFaultTolerant() + ", getDataType()=" + (dataType == null ? "null" : dataType.getSimpleName()) + "]";
	}
}
