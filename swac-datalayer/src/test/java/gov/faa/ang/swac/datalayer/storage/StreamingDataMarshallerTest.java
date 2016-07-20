package gov.faa.ang.swac.datalayer.storage;

import static org.junit.Assert.*;
import gov.faa.ang.swac.datalayer.DataAccessException;
import gov.faa.ang.swac.datalayer.DataSubscriber;
import gov.faa.ang.swac.datalayer.MappedDataAccess;
import gov.faa.ang.swac.datalayer.identity.IntermediateDataDescriptor;
import gov.faa.ang.swac.datalayer.identity.StreamingDataDescriptor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class StreamingDataMarshallerTest implements DataSubscriber {
	List<DataMarshaller> sources = new ArrayList<DataMarshaller>();
	StreamingDataMarshaller stream;
	Integer count = 0;
	boolean flag;
	
	@Before
	public void setUp()
	{
		try {
			MappedDataAccess dao = new MappedDataAccess();
			IntermediateDataDescriptor desc = new IntermediateDataDescriptor();
			desc.setDataSource(this);
			desc.setDataType(Integer.class);
			desc.setPersistent(false);
			
			StreamingDataDescriptor streamingDesc = new StreamingDataDescriptor();
			streamingDesc.setDataType(Integer.class);
			streamingDesc.setBaseDescriptor(desc);
			this.stream = (StreamingDataMarshaller) dao.getMarshaller(streamingDesc);
			
			IntermediateDataDescriptor desc1 = new IntermediateDataDescriptor(desc);
			desc1.setInstanceId(1);
			DataMarshaller marshaller = dao.getMarshaller(desc1);
			sources.add(marshaller);
			marshaller.subscribe(stream);
		
			IntermediateDataDescriptor desc2 = new IntermediateDataDescriptor(desc);
			desc2.setInstanceId(2);
			marshaller = dao.getMarshaller(desc2);
			sources.add(marshaller);
			marshaller.subscribe(stream);
		
			IntermediateDataDescriptor desc3 = new IntermediateDataDescriptor(desc);
			desc3.setInstanceId(3);
			marshaller = dao.getMarshaller(desc3);
			sources.add(marshaller);
			marshaller.subscribe(stream);
		
			IntermediateDataDescriptor desc4 = new IntermediateDataDescriptor(desc);
			desc4.setInstanceId(4);
			marshaller = dao.getMarshaller(desc4);
			sources.add(marshaller);
			marshaller.subscribe(stream);
			
			stream.subscribe(this);
			
		} catch (DataAccessException e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void test()
	{
		for (DataMarshaller marshaller : this.sources)
		{
			count++;
			try {
				marshaller.save(Arrays.asList(new Integer[] { count }));
				assertTrue(flag);
			} catch (DataAccessException e) {
				e.printStackTrace();
				fail();
			}
		}
	}
	
	@Override
	public void onLoad(Object source) {
		// Do nothing
	}
	
	@Override
	public void onSave(Object source) {
		List<Integer> temp = new ArrayList<Integer>();
		try {
            this.stream.load(temp);
            flag = ((temp.size() == 1) && temp.get(0).equals(count));
            //System.out.println(temp);
		} catch (DataAccessException e) {
		}
	}
}
