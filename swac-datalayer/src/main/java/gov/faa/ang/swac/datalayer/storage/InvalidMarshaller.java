/**
 * Copyright "2012", Metron Aviation & CSSI.  All rights reserved.
 * 
 * This computer Software was developed with the sponsorship of the U.S. Government
 * under Contract No. DTFAWA-10-D-00033, which has a copyright license in accordance with AMS 3.5-13.(c)(1).
 */

package gov.faa.ang.swac.datalayer.storage;

import gov.faa.ang.swac.datalayer.DataAccessException;
import gov.faa.ang.swac.datalayer.DataSubscriber;

import java.util.List;

/**
 * InvalidMarshaller exists as a placeholder to preserve the integrity of the descriptor-->marshaller pairing even
 * when the descriptor cannot be resolved to a legitimate marshaller. By convention, a descriptor that is invalid
 * will construct a marshaller anyway, and validation is performed against the marshaller. This class preserves
 * that convention. Primary usage is in ParameterizedDataDescriptor, which normally relies on a base descriptor to 
 * create its marshaller, but if no such base marshaller exists then an InvalidMarshaller must be created.
 * 
 * @author csmith
 *
 */
public class InvalidMarshaller implements DataMarshaller
{
	private final String message;
	
	public InvalidMarshaller(String message) 
	{
		this.message = message;
	}

	@Override
	public <T> void load(List<T> output) throws DataAccessException 
	{
		throw new UnsupportedOperationException("Attempting to load data from an invalid marshaller: " + message);
	}

	@Override
	public <T> void save(List<T> data) throws DataAccessException 
	{
		throw new UnsupportedOperationException("Attempting to load data from an invalid marshaller: " + message);
	}
	
	@Override
	public <T> void append(T data) throws DataAccessException 
	{
		throw new UnsupportedOperationException("Attempting to append data to an invalid marshaller: " + message);
	}
	
	@Override
	public <T> T read() throws DataAccessException {
		throw new UnsupportedOperationException("Read is undefined for marshaller of type: " + getClass().getSimpleName());
	}
	
	@Override
	public void close() throws DataAccessException {
		throw new UnsupportedOperationException("Attempting to close an invalid marshaller: " + message);
	}

	@Deprecated
	@Override
	public Class<?> getDataType() 
	{
		throw new UnsupportedOperationException("Invalid marshaller: " + message);
	}

	@Override
	public boolean exists()
	{
		return false;
	}

	@Override
	public void subscribe(DataSubscriber listener) {
		// Nothing
	}

	@Override
	public void unsubscribe(DataSubscriber listener) {
		// Nothing
	}

	@Override
	public void onLoad(Object source) {
		// Nothing
	}

	@Override
	public void onSave(Object source) {
		// Nothing
	}

	@Override
	public boolean validateExistence() {
		return false;
	}
	
	@Override
	public boolean validateSchema() {
		return false;
	}
	
	@Override
	public boolean validateData() {
		return false;
	}

	@Override
	public String toString() {
		return "InvalidMarshaller [message=" + message + "]";
	}
}
