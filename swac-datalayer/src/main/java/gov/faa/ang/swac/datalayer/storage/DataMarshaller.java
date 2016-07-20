/**
 * Copyright "TBD", Metron Aviation & CSSI.  All rights reserved.
 * 
 * This computer Software was developed with the sponsorship of the U.S. Government
 * under Contract No. DTFAWA-10-D-00033, which has a copyright license in accordance with AMS 3.5-13.(c)(1).
 */

package gov.faa.ang.swac.datalayer.storage;

import gov.faa.ang.swac.datalayer.DataAccessException;
import gov.faa.ang.swac.datalayer.DataPublisher;
import gov.faa.ang.swac.datalayer.DataSubscriber;

import java.util.List;


/**
 * DataMarshaller is the interface for a generic pipe between the application and a storage medium
 * @author csmith
 *
 */
public interface DataMarshaller extends DataPublisher, DataSubscriber
{
	/**
	 * Fills a list with records from the location affiliated with this Marshaller. 
	 * Depending on the implementation, this may be all the data linked to this
	 * marshaller or a subset - perhaps affiliated with a single file in a list of files. 
	 * By convention, load should append records to the list if the list is not empty.
	 * 
	 * @param <T>
	 * @param output A non-null, modifiable list.
	 * @throws DataAccessException
	 */
	public <T> void load(List<T> output) throws DataAccessException;
	
	/**
	 * Saves the records in the list to the location affiliated with this Marshaller
	 * @param <T>
	 * @param data A non-null list containing the records to be saved
	 * @throws DataAccessException
	 */
	public <T> void save(List<T> data) throws DataAccessException;
	
	/**
	 * Saves one record using append semantics; assumption is that system resources
	 * will be opened automatically, left open and must be closed manually. Looping over all records in a
	 * list and calling append, then close, should have identical results to calling
	 * save once on the list
	 * 
	 * @param <T>
	 * @param data A data record of type T to be saved
	 * @throws DataAccessException
	 */
	public <T> void append(T data) throws DataAccessException;
	
	/**
	 * Reads one record, with the assumption that a cursor will be advanced to the next record.
	 * Looping over read until it returns null and adding the items to a list should give the
	 * same results as load. There is currently no facility for resetting or seeking with a cursor,
	 * however new calls to save (but not append) should reset the cursor to a valid state.
	 * The assumption is that read will automatically open system resources if necessary and
	 * leave them in an open state, requiring a call to close in order to clean them up.
	 * 
	 * @param <T>
	 * @return A single data record or null, if there are no more records
	 * @throws DataAccessException
	 */
	public <T> T read() throws DataAccessException;
	
	/**
	 * Close any connections and dispose of any resources reserved by this Marshaller
	 */
	public void close() throws DataAccessException;
	
	@Deprecated
	public Class<?> getDataType();
	
	/**
	 * Basic existence check predicate.
	 * @return
	 */
	boolean exists();
	
	///
	/// Validation Methods
	///
	
	/**
	 * Differs from exists() in that non-existence of data may be a valid state - say, for a MemoryMarshaller
	 * that is intended to store intermediate data but that data has not been generated yet. Also, by convention,
	 * validate methods should log details of invalid results. 
	 */
	public boolean validateExistence();  
	
	/**
	 * Intermediate level validation check should confirm that the format of input data is valid, if not the content. 
	 */
	public boolean validateSchema();
	
	/**
	 * Deep validation confirms the logical content of data records. This may be a time consuming operation to be executed with discretion. 
	 */
	public boolean validateData();
}
