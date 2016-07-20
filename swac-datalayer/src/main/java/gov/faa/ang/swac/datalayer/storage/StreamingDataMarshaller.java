package gov.faa.ang.swac.datalayer.storage;

import gov.faa.ang.swac.datalayer.DataAccessException;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class StreamingDataMarshaller extends DataMarshallerBase
{
	private static Logger logger = LogManager.getLogger(StreamingDataMarshaller.class);
	
	private Queue<List<?>> stream = new LinkedList<List<?>>();

	public StreamingDataMarshaller(Class<?> clazz, String source) {
		super(clazz, source);
	}

	@Override
	public synchronized boolean exists() {
		return this.stream.peek() != null;
	}

	@Override
	public synchronized <T> void loadInternal(List<T> output) 
	{
		this.validateParameterizedType(output);
		// type validation is performed on saved collection and again on this collection to be filled
		@SuppressWarnings("unchecked")
		List<T> typedList = (List<T>)this.stream.remove();
		logger.debug("StreamingDataMarshaller loaded from stream: " + typedList.size() + " records");
		dec(this.clazz);
		output.addAll(typedList);
	}

	@Override
	public <T> void saveInternal(List<T> data) 
	{
		this.validateParameterizedType(data);
		
		if (this.stream != null)
		{
			logger.debug("StreamingDataMarshaller saving to stream: " + data);
			this.stream.add(data);
			inc(this.clazz);
		}
	}

	@Override
	public synchronized void onLoad(Object source)
	{
		logger.debug("StreamingDataMarshaller " + this.toString() + " handling load event from source=" + source.toString());
	}
	
	@Override
	public synchronized void onSave(Object source)
	{
		if (!(source instanceof DataMarshaller))
		{
			throw new IllegalArgumentException("StreamingDataMarshaller hooked to an illegal source");
		}
		logger.debug("StreamingDataMarshaller " + this.toString() + " detected save event from source=" + source.toString() + "; loading contents into stream.");
		List<?> storage = new ArrayList();
		try {
			DataMarshaller dataSource = (DataMarshaller)source;
			dataSource.load(storage);
			logger.debug("StreamingDataMarshaller loaded from source: " + storage);
			// TODO: this might be the source of a memory leak - intermediate data may need to be cleared from the source marshaller
			this.save(storage);
		} catch (DataAccessException e) {
			throw new IllegalStateException("SteamingDataMarshaller.onSave should never throw DataAccessExceptions", e);
		}
	}
	
	private static final Map<String,Long> instanceCounts = new TreeMap<String,Long>();
	private synchronized void inc(Class<?> clazz)
	{
		instanceCounts.put(clazz.getSimpleName(), (instanceCounts.containsKey(clazz.getSimpleName()) ? instanceCounts.get(clazz.getSimpleName()) : 0) + 1);
	}
	private synchronized void dec(Class<?> clazz)
	{
		instanceCounts.put(clazz.getSimpleName(), (instanceCounts.containsKey(clazz.getSimpleName()) ? instanceCounts.get(clazz.getSimpleName()) : 0) - 1);
	}
	public static synchronized void logIntermediateDataUsage()
	{
		for (Entry<String,Long> entry : instanceCounts.entrySet())
		{
			if (entry.getValue() != 0)
			{
				String msg = entry.getKey() + "=" + entry.getValue();
				logger.debug(msg);
			}
		}
	}
}
