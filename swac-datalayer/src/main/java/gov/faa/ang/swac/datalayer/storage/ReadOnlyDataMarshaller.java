/**
 * Copyright "TBD", Metron Aviation & CSSI.  All rights reserved.
 * 
 * This computer Software was developed with the sponsorship of the U.S. Government
 * under Contract No. DTFAWA-10-D-00033, which has a copyright license in accordance with AMS 3.5-13.(c)(1).
 */

package gov.faa.ang.swac.datalayer.storage;

import gov.faa.ang.swac.datalayer.DataAccessException;
import gov.faa.ang.swac.datalayer.DataSubscriber;

import java.util.List;

/**
 * Simple decorator for DataMarshaller provides read-only access to the underlying storage medium. 
 * @author csmith
 *
 */
public class ReadOnlyDataMarshaller implements DataMarshaller
{
	private DataMarshaller marshaller;
	
	public ReadOnlyDataMarshaller(DataMarshaller marshaller) 
	{
		this.marshaller = marshaller;
	}

	@Override
	public <T> void load(List<T> output) throws DataAccessException 
	{
		this.marshaller.load(output);
	}

	@Override
	public <T> void save(List<T> data) throws DataAccessException 
	{
		throw new UnsupportedOperationException("Error: This DataMarshaller is read-only");
	}
	
	@Override
	public <T> void append(T data) throws DataAccessException 
	{
		throw new UnsupportedOperationException("Error: This DataMarshaller is read-only");
	}
	
	@Override
	public <T> T read() throws DataAccessException {
		throw new UnsupportedOperationException("Read is undefined for marshaller of type: " + getClass().getSimpleName());
	}
	
	@Override
	public void close() throws DataAccessException {
		this.marshaller.close();
	}

	@Deprecated
	@Override
	public Class<?> getDataType() 
	{
		return this.marshaller.getDataType();
	}

	@Override
	public boolean exists()
	{
		return this.marshaller.exists();
	}

	@Override
	public void subscribe(DataSubscriber listener) {
		this.marshaller.subscribe(listener);
	}

	@Override
	public void unsubscribe(DataSubscriber listener) {
		this.marshaller.unsubscribe(listener);
	}

	@Override
	public void onLoad(Object source) {
		this.marshaller.onLoad(source);
	}

	@Override
	public void onSave(Object source) {
		this.marshaller.onSave(source);
	}

	@Override
	public boolean validateExistence() {
		return this.marshaller.validateExistence();
	}
	
	@Override
	public boolean validateSchema() {
		return this.marshaller.validateSchema();
	}
	
	@Override
	public boolean validateData() {
		return this.marshaller.validateData();
	}
        
        @Override
        public String toString() {
            return "ReadOnlyDataMarshaller [ " + this.marshaller.toString() + " ]";
        }
}
