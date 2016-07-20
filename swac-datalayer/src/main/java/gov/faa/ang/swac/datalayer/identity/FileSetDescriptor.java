/**
 * Copyright "TBD", Metron Aviation & CSSI.  All rights reserved.
 * 
 * This computer Software was developed with the sponsorship of the U.S. Government
 * under Contract No. DTFAWA-10-D-00033, which has a copyright license in accordance with AMS 3.5-13.(c)(1).
 */

package gov.faa.ang.swac.datalayer.identity;

import gov.faa.ang.swac.datalayer.ResourceManager;
import gov.faa.ang.swac.datalayer.storage.DataMarshaller;
import gov.faa.ang.swac.datalayer.storage.MarshallerCollection;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Allows multiple files to be described in a compact manner. Loading from this descriptor will merge the contents from all files
 * into one collection
 * 
 * TODO: this is a shortcut that probably warrants change in any data layer API revisions
 * 
 * @author csmith
 *
 */
public class FileSetDescriptor extends FileDataDescriptor implements Iterable<FileDataDescriptor>
{
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result;
        
        for (String name : this.resourceNames) {
            result += name.hashCode();
        }
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		FileSetDescriptor other = (FileSetDescriptor) obj;
		if (this.resourceNames == null)
		{
			if (other.resourceNames != null)
				return false;
		} else if (!this.resourceNames.equals(other.resourceNames))
			return false;
		return true;
	}

	private List<String> resourceNames = new ArrayList<String>();

	public List<String> getResourceNames()
	{
		return this.resourceNames;
	}

	public void setResourceNames(List<String> resourceNames)
	{
		this.resourceNames = resourceNames;
	}

	public void setResourceNames(String resourceNames)
	{
		this.resourceNames = Arrays.asList(resourceNames.split(","));
	}

    public FileSetDescriptor() {
        super();
    }

    public FileSetDescriptor(FileDataDescriptor fdd) {
        super(fdd);
    }

    public FileSetDescriptor(FileSetDescriptor fsd) {
        super(fsd);
        this.resourceNames = new ArrayList<String>();
        this.resourceNames.addAll(fsd.resourceNames);
    }

	@Override
	public Iterator<FileDataDescriptor> iterator()
	{
		final Iterator<String> iterator = this.resourceNames.iterator();
		
		return new Iterator<FileDataDescriptor>() {

			@Override
			public boolean hasNext()
			{
				return iterator.hasNext();
			}

			@Override
			public FileDataDescriptor next()
			{
				String resource = iterator.next();
				FileDataDescriptor descriptor = new FileDataDescriptor();
				descriptor.setDataType(getDataType());
				descriptor.setLocation(getLocation());
				descriptor.setReadOnly(isReadOnly());
				descriptor.setResourceName(resource);
				descriptor.setFaultTolerant(isFaultTolerant());
				
				return descriptor;
			}

			@Override
			public void remove()
			{
				throw new UnsupportedOperationException();
			}
			
		};
	}
	
	@Override
	public DataMarshaller createMarshaller(ResourceManager resMan)
	{
		List<DataMarshaller> marshallers = new ArrayList<DataMarshaller>();
		for (FileDataDescriptor m : this)
		{
			marshallers.add(m.createMarshaller(resMan));
		}
		return new MarshallerCollection(getDataType(), marshallers);
	}

    @Override
    public FileSetDescriptor clone() {
        return new FileSetDescriptor(this);
    }

    @Override
    public String toString() {
		Class<?> dataType = getDataType();
		StringBuilder rtn = new StringBuilder();
        rtn.append("FileSetDescriptor [location=" + getLocation() + ", resourceNames=[");

        for (String name : this.resourceNames) {
            rtn.append(name + ",");
        }
        int index = rtn.lastIndexOf(",");
        rtn.replace(index, index + 1, "]");

        rtn.append(", readOnly=" + isReadOnly() + ", faultTolerant=");
        rtn.append(isFaultTolerant() + ", getDataType()=" + (dataType == null ? "null" : dataType.getSimpleName()) + "]");

        return rtn.toString();
    }
}
