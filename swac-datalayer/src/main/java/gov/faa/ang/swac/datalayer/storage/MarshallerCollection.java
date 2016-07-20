/**
 * Copyright "TBD", Metron Aviation & CSSI.  All rights reserved.
 * 
 * This computer Software was developed with the sponsorship of the U.S. Government
 * under Contract No. DTFAWA-10-D-00033, which has a copyright license in accordance with AMS 3.5-13.(c)(1).
 */

package gov.faa.ang.swac.datalayer.storage;

import gov.faa.ang.swac.datalayer.DataAccessException;

import java.util.Collection;
import java.util.ArrayList;
import java.util.List;

public class MarshallerCollection extends DataMarshallerBase
{
	private List<DataMarshaller> marshallers;

	public MarshallerCollection(Class<?> clazz, Collection<DataMarshaller> items)
	{
		super(clazz, items.toString());
		this.marshallers = new ArrayList<DataMarshaller>(items);
	}

	@Override
	public <T> void loadInternal(List<T> output) throws DataAccessException
	{
		for (DataMarshaller m : marshallers)
		{
			m.load(output);
		}
	}

	@Override
	public <T> void saveInternal(List<T> data) throws DataAccessException
	{
		for (DataMarshaller m : marshallers)
		{
			m.save(data);
		}
	}

	@Override
	public boolean exists()
	{
		for (DataMarshaller m : marshallers)
		{
			if (!m.exists())
			{
				return false;
			}
		}
		return true;
	}

}
