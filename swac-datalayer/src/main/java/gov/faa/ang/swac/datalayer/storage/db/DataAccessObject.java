/**
 * Copyright "TBD", Metron Aviation & CSSI.  All rights reserved.
 * 
 * This computer Software was developed with the sponsorship of the U.S. Government
 * under Contract No. DTFAWA-10-D-00033, which has a copyright license in accordance with AMS 3.5-13.(c)(1).
 */

package gov.faa.ang.swac.datalayer.storage.db;

import java.sql.ResultSet;
import java.util.List;

import gov.faa.ang.swac.datalayer.DataAccessException;
import gov.faa.ang.swac.datalayer.storage.DataMarshaller;
import java.sql.Connection;

// TODO: use of generics needs a tune up...specifically, methods like selectAll that don't take T as a parameter have unchecked type erasure
public interface DataAccessObject<T> extends DataMarshaller {
	/**
	 * Returns a result set based on an arbitrary list of parameters
	 * @param <T>
	 * @param params
	 * @return
	 */
	public List<T> select(Object... params) throws DataAccessException;
	
	/**
	 * Returns all records
	 * @param <T>
	 * @return
	 * @throws DataAccessException 
	 */
	public List<T> selectAll() throws DataAccessException;
	
	/**
	 * Inserts one record
	 * @param <T>
	 * @param record
	 * @return uniqueId, if any
	 */
	public int insert(T record) throws DataAccessException;
	
	/**
	 * Inserts all records in the list
	 * @param <T>
	 * @param records
	 * @return number of records inserted
	 */
	public int insertAll(List<T> records) throws DataAccessException;
	
	/**
	 * Updates a single record. It is assumed that the record has some sort of unique identification to locate the record to be updated
	 * @param <T>
	 * @param record
	 * @return true if a record was actually updated
	 */
	public boolean update(T record) throws DataAccessException;
	
	/**
	 * Deletes the given record, if found
	 * @param <T>
	 * @param record
	 * @return true if a record was found and deleted
	 */
	public boolean delete(T record) throws DataAccessException;
	
	/**
	 * Deletes all records
	 * @param <T>
	 * @return the number of records deleted
	 */
	public int deleteAll() throws DataAccessException;
	
	/**
	 * Execute an arbitrary query, as defined by the specific DAO implementation. For use as a catch-all
	 * @param params
	 * @return 
	 * @throws DataAccessException 
	 */
	public ResultSet executeQuery(Object... params) throws DataAccessException;
        
        /**
         * Parameterized clone, used by data descriptors to clone the prototype metadata, but use a specific connection.
         * @param connection
         * @return 
         */
        public DataAccessObject<T> copy(Connection connection);
}
