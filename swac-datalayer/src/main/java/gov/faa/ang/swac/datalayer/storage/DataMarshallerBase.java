/**
 * Copyright "TBD", Metron Aviation & CSSI.  All rights reserved.
 * 
 * This computer Software was developed with the sponsorship of the U.S. Government
 * under Contract No. DTFAWA-10-D-00033, which has a copyright license in accordance with AMS 3.5-13.(c)(1).
 */

package gov.faa.ang.swac.datalayer.storage;

import gov.faa.ang.swac.datalayer.AdHocDataAccess;
import gov.faa.ang.swac.datalayer.AppendableDataSubscriber;
import gov.faa.ang.swac.datalayer.DataAccessException;
import gov.faa.ang.swac.datalayer.AdHocDataAccess.LogLevel;
import gov.faa.ang.swac.datalayer.DataSubscriber;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * Abstract base class for DataMarshaller implementations. It provides functionality to ensure type safety for the collections
 * being marshalled in and out of storage. Parameterized list arguments can be checked against the class declared in the constructor.
 * This is the responsibility of subclasses in the load and save methods.
 * @author csmith
 *
 */
public abstract class DataMarshallerBase implements DataMarshaller
{
	protected static final Logger logger = LogManager.getLogger(DataMarshallerBase.class);
	
	protected final Class<?> clazz;
	protected String source;
	
	protected DataMarshallerBase(Class<?> clazz, String source)
	{
		this.clazz = clazz;
		this.source = source;
	}
	
	@Override
	public final Class<?> getDataType()
	{
		return this.clazz;
	}
	
	protected final <T> void validateParameterizedType(List<T> obj)
	{
		Method getMethod;
		try 
		{
			getMethod = obj.getClass().getMethod("get", int.class);
		} 
		catch (SecurityException ex) 
		{
			throw new RuntimeException("Error reflecting List.class. This may indicate a problem with the ClassLoader", ex);
		} 
		catch (NoSuchMethodException ex) 
		{
			throw new RuntimeException("Error reflecting List.class. This may indicate a problem with the ClassLoader", ex);
		}
		
		Type returnType = getMethod.getGenericReturnType();

		if(returnType instanceof ParameterizedType)
		{
		    ParameterizedType type = (ParameterizedType) returnType;
		    Type[] typeArguments = type.getActualTypeArguments();
		    for(Type typeArgument : typeArguments)
		    {
		        Class<?> typeArgClass = (Class<?>) typeArgument;
		        if (!this.clazz.equals(typeArgClass))
		        {
		        	// TODO: comment out type validation until reflective loading in Batch and ScenarioExecution is finished and reconciled
		        	//throw new IllegalArgumentException("Error: Type mismatch between return data and parameterized list");
		        }
		    }
		}
	}
	
	@Override
	public final <T> void load(List<T> data) throws DataAccessException
	{
		loadInternal(data);
		for (DataSubscriber listener : this.subscribers)
		{
			listener.onLoad(this);
		}
	}
	
	@Override
	public final <T> void save(List<T> data) throws DataAccessException
	{
		saveInternal(data);
		// Dump debug data by default if log level is DEBUG
		if (AdHocDataAccess.getLogLevel().equals(LogLevel.DEBUG))
		{
			AdHocDataAccess.dumpData(source, data);
		}
	
		for (DataSubscriber listener : this.subscribers)
		{
			listener.onSave(this);
		}
	}
	
	@Override
	public final <T> void append(T data) throws DataAccessException {
		appendInternal(data);
		for (DataSubscriber listener : this.subscribers)
		{
			if (listener instanceof AppendableDataSubscriber) {
				((AppendableDataSubscriber)listener).onAppend(this);
			}
		}
	}
	
	@Override
	public <T> T read() throws DataAccessException {
		throw new UnsupportedOperationException("Read is undefined for marshaller of type: " + getClass().getSimpleName());
	}
	
	public void close() throws DataAccessException {
		logger.warn("Data marshaller does not implement close() and may leak resources: " + getClass().getSimpleName());
	}
	 
	protected abstract <T> void loadInternal(List<T> data) throws DataAccessException;
	protected abstract <T> void saveInternal(List<T> data) throws DataAccessException;
	protected <T> void appendInternal(T data) throws DataAccessException {
		throw new DataAccessException("Append is not supported by this data marshaller type: " + getClass().getSimpleName());
	}
	// Event hooking
	
	private List<DataSubscriber> subscribers = new ArrayList<DataSubscriber>();
	
	@Override
	public void subscribe(DataSubscriber listener)
	{
		// The number of elements is expected to be small, and order needs to be preserved without forcing
		// DataSubscribers to implement Comparable, so enforce Set semantics (i.e. uniqueness of elements) manually 
		if (!this.subscribers.contains(listener))
		{
			this.subscribers.add(listener);
		}
	}
	
	@Override
	public void unsubscribe(DataSubscriber listener)
	{
		this.subscribers.remove(listener);
	}
	
	@Override
	public void onLoad(Object source)
	{
		// Hook
	}
	
	@Override
	public void onSave(Object source)
	{
		// Hook
	}
	
	@Override
	public boolean validateExistence() {
		// Extension hook
		return true;
	}
	
	@Override
	public boolean validateSchema() {
		// Extension hook
		return true;
	}
	
	@Override
	public boolean validateData() {
		// Extension hook
		return true;
	}
}
