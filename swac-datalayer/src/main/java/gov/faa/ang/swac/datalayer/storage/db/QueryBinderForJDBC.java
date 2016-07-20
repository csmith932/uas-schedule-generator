/**
 * Copyright "TBD", Metron Aviation & CSSI.  All rights reserved.
 * 
 * This computer Software was developed with the sponsorship of the U.S. Government
 * under Contract No. DTFAWA-10-D-00033, which has a copyright license in accordance with AMS 3.5-13.(String name, c)(String name, 1).
 */

package gov.faa.ang.swac.datalayer.storage.db;

import gov.faa.ang.swac.common.datatypes.Timestamp;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;



public class QueryBinderForJDBC implements QueryBinder {
	
	private PreparedStatement preparedStatement;
	private int parameterIndex;
	private int batchCount;

	public QueryBinderForJDBC(PreparedStatement preparedStatement) {
		this.preparedStatement = preparedStatement;
		this.parameterIndex = 1;
		this.batchCount = 0;
	}
	
	@Override	
	public void addBatch() throws SQLException {
		preparedStatement.addBatch();
		complete();
	}

	@Override	
	public void bindField(int field) throws SQLException {
		preparedStatement.setInt(parameterIndex++, field);
	}
	
	@Override
	public void bindField(boolean field) throws SQLException {
		preparedStatement.setBoolean(parameterIndex++, field);
	}

	@Override
	public void bindField(double field) throws SQLException {
		preparedStatement.setDouble(parameterIndex++, field);
	}

	@Override
	public void bindField(Object field) throws SQLException {
		if (field == null)
			preparedStatement.setNull(parameterIndex++, Types.VARCHAR);
		else
			preparedStatement.setString(parameterIndex++, field.toString());
	}

	@Override
	//@param sqlType java.sql.Types
	public void bindField(Object field, int sqlType) throws SQLException {
		if (field == null)
			preparedStatement.setNull(parameterIndex++, sqlType);
		else
			preparedStatement.setString(parameterIndex++, field.toString());
	}

	@Override
	public void bindField(Integer field) throws SQLException {
		if (field == null)
			preparedStatement.setNull(parameterIndex++, Types.INTEGER);
		else
			preparedStatement.setInt(parameterIndex++, field);
	}

	@Override
	public void bindField(Double field) throws SQLException {
		if (field != null && (field.isNaN() || field.isInfinite()))
			field = null;
		if (field == null)
			preparedStatement.setNull(parameterIndex++, Types.DOUBLE);
		else
			preparedStatement.setDouble(parameterIndex++, field);
	}

	@Override
	public void bindField(Boolean field) throws SQLException {
		if (field == null)
			preparedStatement.setNull(parameterIndex++, Types.BOOLEAN);
		else
			preparedStatement.setBoolean(parameterIndex++, field);
	}

	@Override
	public void bindField(Enum<?> field) throws SQLException {
		if (field == null)
			preparedStatement.setNull(parameterIndex++, Types.VARCHAR);
		else
			preparedStatement.setString(parameterIndex++, field.name());
	}

	@Override
	public void bindField(Timestamp field) throws SQLException {
		if (field == null)
			preparedStatement.setNull(parameterIndex++, Types.TIMESTAMP);
		else {
			java.sql.Timestamp sqlTimestamp = new java.sql.Timestamp(field.getTime());
			preparedStatement.setTimestamp(parameterIndex++, sqlTimestamp);
		}
	}

	/**
	 * Wrapper around bindField(Timestamp) to allow for Timestamps to be truncated to dates when bound to a prepared statement.
	 * @param field
	 * @param asDate
	 * @throws SQLException
	 */
	@Override
	public void bindField(Timestamp field, boolean asDate) throws SQLException {
		if (asDate) {
			if (field == null)
				preparedStatement.setNull(parameterIndex++, Types.DATE);
			else {
				java.sql.Date sqlDate = new java.sql.Date(field.getTime());
				preparedStatement.setDate(parameterIndex++, sqlDate);
			}
		} else
			bindField(field);
	}

	public int getBatchCount() { 
		return batchCount;
	}
	
	public void resetBatchCount() { 
		batchCount = 0;
	}
	public void complete() {
		parameterIndex = 1;
		batchCount++;
	}
}