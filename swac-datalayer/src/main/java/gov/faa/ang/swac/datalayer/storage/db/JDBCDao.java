/**
 * Copyright "TBD", Metron Aviation & CSSI.  All rights reserved.
 * 
 * This computer Software was developed with the sponsorship of the U.S. Government
 * under Contract No. DTFAWA-10-D-00033, which has a copyright license in accordance with AMS 3.5-13.(c)(1).
 */

package gov.faa.ang.swac.datalayer.storage.db;

import gov.faa.ang.swac.datalayer.DataAccessException;
import gov.faa.ang.swac.datalayer.DataSubscriber;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Abstract base class for DAO implementations. It wraps a database Connection and uses unimplemented stubs of the interface
 * for extension hooks.
 * @author csmith
 *
 */
public abstract class JDBCDao<T> implements DataAccessObject<T>  {
	protected Connection connection;
	protected boolean closeOnFinalize = true;
	
	public JDBCDao() {
		
	}
	
	public JDBCDao(JDBCDao copy) {
		this.closeOnFinalize = copy.closeOnFinalize;
	}
	
	protected final void setConnection(Connection connection) {
		this.connection = connection;
	}
	
	public boolean getCloseOnFinalize() { 
		return this.closeOnFinalize;
	}
	
	public void setCloseOnFinalize(boolean val) { 
		this.closeOnFinalize = val;
	}
	
	@Override
	public final boolean exists() {
		return this.connection == null;
	}

	@Override
	public List<T> select(Object... params) throws DataAccessException {
		throw new UnsupportedOperationException(this.getClass().getSimpleName() + " does not implement  method");
	}

	@Override
	public List<T> selectAll() throws DataAccessException {
		throw new UnsupportedOperationException(this.getClass().getSimpleName() + " does not implement  method");
	}

	@Override
	public int insert(T record) throws DataAccessException {
		throw new UnsupportedOperationException(this.getClass().getSimpleName() + " does not implement  method");
	}

	@Override
	public int insertAll(List<T> records) throws DataAccessException {
		throw new UnsupportedOperationException(this.getClass().getSimpleName() + " does not implement  method");
	}

	@Override
	public boolean update(T record) throws DataAccessException {
		throw new UnsupportedOperationException(this.getClass().getSimpleName() + " does not implement  method");
	}

	@Override
	public boolean delete(T record) throws DataAccessException {
		throw new UnsupportedOperationException(this.getClass().getSimpleName() + " does not implement  method");
	}

	@Override
	public int deleteAll() throws DataAccessException {
		throw new UnsupportedOperationException(this.getClass().getSimpleName() + " does not implement  method");
	}

	@Override
	public ResultSet executeQuery(Object... params) throws DataAccessException {
		throw new UnsupportedOperationException(this.getClass().getSimpleName() + " does not implement  method");
	}

	@Override
	public final <S> void load(List<S> output) throws DataAccessException {
		// TODO: need to work out type erasure for generics
		List<S> records = (List<S>) this.selectAll();
		output.addAll(records);
	}

	@Override
	public final <S> void save(List<S> data) throws DataAccessException {
		// TODO: need to work out type erasure for generics
		this.insertAll((List<T>)data);
	}
	
	@Override
	public final <S> void append(S data) throws DataAccessException {
		throw new UnsupportedOperationException("Append not defined for data access object: " + getClass().getSimpleName());
	}
	
	@Override
	public <T> T read() throws DataAccessException {
		throw new UnsupportedOperationException("Read is undefined for marshaller of type: " + getClass().getSimpleName());
	}

	@Override
	public Class<?> getDataType() {
		throw new UnsupportedOperationException(this.getClass().getSimpleName() + " does not implement  method");
	}

	@Override
	public void subscribe(DataSubscriber listener) {
		throw new UnsupportedOperationException(this.getClass().getSimpleName() + " does not implement  method");
	}

	@Override
	public void unsubscribe(DataSubscriber listener) {
		throw new UnsupportedOperationException(this.getClass().getSimpleName() + " does not implement  method");
	}

	@Override
	public void onLoad(Object source) {
		throw new UnsupportedOperationException(this.getClass().getSimpleName() + " does not implement  method");
	}

	@Override
	public void onSave(Object source) {
		throw new UnsupportedOperationException(this.getClass().getSimpleName() + " does not implement  method");
	}
	
        @Override
	public abstract JDBCDao copy(Connection connection);
	
	public void close() throws DataAccessException {
		try {
			this.connection.close();
			this.connection = null;
		} catch (SQLException e) {
			throw new DataAccessException(e);
		}
	}
	
	@Override
	public void finalize() {
		if (closeOnFinalize) {
			try {
				this.close();
			} catch (DataAccessException e) {
				// Don't rethrow in finalize
				e.printStackTrace();
			}
		}
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
