/**
 * Copyright "TBD", Metron Aviation & CSSI.  All rights reserved.
 * 
 * This computer Software was developed with the sponsorship of the U.S. Government
 * under Contract No. DTFAWA-10-D-00033, which has a copyright license in accordance with AMS 3.5-13.(c)(1).
 */

package gov.faa.ang.swac.datalayer.storage;

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * MemoryMarshaller is the trivial implementation of DataMarshaller. It does not physically move data out of
 * the existing memory space. It only shallow-copies the collection to an internal reference. WeakReferences
 * are used to allow garbage collection of data that is no longer referenced elsewhere.
 * @author csmith
 * 
 * XXX: Self-reporting of instance counts disabled because the existence of finalize() delays GC until after the 
 * Finalizer thread can service the queue...potentially causing large amounts of memory to be mismanaged. Also note: static Map is not thread safe
 *
 */
public class MemoryMarshaller extends DataMarshallerBase
{
	private static Logger logger = LogManager.getLogger(MemoryMarshaller.class);
	
	private List<?> ref;
	
	public MemoryMarshaller(Class<?> clazz, String source)
	{
		super(clazz, source);
	}
	
	@Override
	public final boolean exists()
	{
		return (this.ref != null);
	}
	
	@Override
	public <T> void loadInternal(List<T> output) 
	{
		this.validateParameterizedType(output);
		if (ref != null)
		{
			List<?> w = ref;
			if (w != null)
			{
				// type validation is performed on saved collection and again on this collection to be filled
				@SuppressWarnings("unchecked")
				List<T> typedList = (List<T>)w;
				
				output.addAll(typedList);
			}
		}
		logger.debug("Loading data for MemoryMarshaller=" + this.toString());
	}

	@Override
	public <T> void saveInternal(List<T> data) 
	{
		this.validateParameterizedType(data);
		ref = data;
		logger.debug("Saving data for MemoryMarshaller=" + this.toString());
		
//		if (data != null)
//		{
//			inc(this.clazz);
//		}
//		else
//		{
//			dec(this.clazz);
//		}
	}
	
	@SuppressWarnings("unchecked")
	@Override 
	protected <T> void appendInternal(T data) {
		List<?> w = ref;
		if (w == null) {
			w = new LinkedList<T>();
		}
		((List<T>)w).add(data);
		ref = w;
	}
	
	@Override
	public <T> T read() {
		if (ref != null && ref.size() > 0) {
			return (T)ref.remove(0);
		}
		return null;
	}
	
	@Override
	public void close() {
		// TODO: lifecycle isn't well defined enough for us to allow closing from an external source
//		ref = null;
	}
	
	@Override
	public String toString() {
		return "MemoryMarshaller [ref=" + (ref==null?"null":"data")
				+ ", clazz=" + clazz + ", source=" + source + "]";
	}

//	private static final Map<String,Long> instanceCounts = new TreeMap<String,Long>();
//	private void inc(Class<?> clazz)
//	{
//		instanceCounts.put(clazz.getSimpleName(), (instanceCounts.containsKey(clazz.getSimpleName()) ? instanceCounts.get(clazz.getSimpleName()) : 0) + 1);
//	}
//	private void dec(Class<?> clazz)
//	{
//		instanceCounts.put(clazz.getSimpleName(), (instanceCounts.containsKey(clazz.getSimpleName()) ? instanceCounts.get(clazz.getSimpleName()) : 0) - 1);
//	}
	public static void logIntermediateDataUsage()
	{
//		for (Entry<String,Long> entry : instanceCounts.entrySet())
//		{
//			if (entry.getValue() != 0)
//			{
//				String msg = entry.getKey() + "=" + entry.getValue();
//				logger.debug(msg);
//			}
//		}
	}
//	@Override
//	public void finalize()
//	{
//		if (this.ref != null)
//		{
//			dec(this.clazz);
//		}
//	}
}
