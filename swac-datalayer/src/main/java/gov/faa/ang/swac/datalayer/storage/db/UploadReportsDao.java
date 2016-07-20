/**
 * Copyright "TBD", Metron Aviation & CSSI. All rights reserved.
 *
 * This computer Software was developed with the sponsorship of the U.S.
 * Government under Contract No. DTFAWA-10-D-00033, which has a copyright
 * license in accordance with AMS 3.5-13.(c)(1).
 */
package gov.faa.ang.swac.datalayer.storage.db;

import gov.faa.ang.swac.datalayer.DataAccessException;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.List;

public class UploadReportsDao extends JDBCDao<UploadableRecord> 
{
    //private static final org.apache.log4j.Logger logger = org.apache.log4j.LogManager.getLogger(UploadReportsDao.class);

	/**
	 * Name of table to insert records too
	 */
	private String tableName;

	/**
	 * Converts logical data types to actual type for a particular database.  
	 */
	private FieldTypeConverter converter;

	/**
	 * Max number of records to batch until inserting into db.  Guard against out of memory errors
	 */
    private int maxBatchSize = 10000; 
    
	/**
	 * Flag on whether to close on finalize
	 */
    private boolean closeOnFinalize = false;
    
    public UploadReportsDao() { 
	}
	
    public UploadReportsDao(UploadReportsDao copy) {
    	super(copy);
    	this.tableName = copy.tableName;
    	this.converter = copy.converter;
    	this.maxBatchSize = copy.maxBatchSize;
    }

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	
	public void setConverter(FieldTypeConverter converter) {
		this.converter = converter;
	}
	
	public void setFieldTypeConverterFactory(FieldTypeConverterFactory factory) { 
		this.converter = factory.getFieldTypeConverter();
	}
	
	public void setMaxBatchSize(int maxBatchSize) {    	
    	this.maxBatchSize = maxBatchSize < 0 ? Integer.MAX_VALUE : maxBatchSize;
    }
	
	@Override
    public int insertAll(List<UploadableRecord> records) throws DataAccessException
    {
    	if (records.isEmpty())
    		return 0;
    	
    	boolean tableExists = false;
    	try {
    		tableExists = doesTableExist();
    	} catch (SQLException ex) {
    		String msg = String.format("Unable to determine if table %s exists. Err code '%s', msg: '%s'", tableName, ex.getErrorCode(), ex.getMessage());
    		throw new DataAccessException(msg, ex);
    	}
    	
    	UploadableRecord sampleRecord = records.get(0);
    	QueryBuilder insertQuery = new InsertTableQueryBuilder(tableName);
    	if (! tableExists) {
    		String createTableSql = null;
			try {
    			QueryBuilder createTableQuery = new CreateTableQueryBuilder(tableName, converter);
    			QueryBuilder compoundQuery = new CompoundQueryBuilder(createTableQuery, insertQuery);
    			sampleRecord.describeFields(compoundQuery);
    			createTableSql = createTableQuery.toQueryString();
    			// temp debug- with so many record classes having empty stubs, we get sql statemnets liek "create table _tablename_ ( )"
    			if (createTableSql.endsWith("( )")) {
    				System.out.println("## need to fill out create table stub for " + tableName + ".. connection null? " + (connection == null));
    				return 0;
    			} 
    			createTable(createTableSql);
    		} catch (SQLException ex) {
				// If an oracle user does not have certain permissions than our prior doesTableExist logic will not
				// correctly detect the tables existence. The subsequent attempt to create a table will error out here
				// with error code 955. If this is the case, than proceed with the insert, otherwise, error out. 
    			if (ex.getErrorCode() != 955) {
	    			String msg = String.format("Unable to create table %s. Err code '%s', msg: '%s', sql '%s'", 
	    				tableName, ex.getErrorCode(), ex.getMessage(), createTableSql);
    				throw new DataAccessException(msg, ex);
    			}
			}
		} else  {
			sampleRecord.describeFields(insertQuery);
		}
    	
    	PreparedStatement insertOutputReportRecordStmt = null;
    	String insertSql = insertQuery.toQueryString();
	    try {
	    	insertOutputReportRecordStmt = this.connection.prepareStatement(insertSql);
		} catch (SQLException ex) {
			String msg = String.format("Unable to prepare insert statement for table %s. Err code '%s', msg: '%s', sql '%s'", 
	    				tableName, ex.getErrorCode(), ex.getMessage(), insertSql);
    		throw new DataAccessException(msg, ex);
		}
		
		try {
			bindAndExecuteRecords(insertOutputReportRecordStmt, records);
	    	return records.size();
		} catch (Exception ex) {
			String errorCode = "unk";
			if (ex instanceof SQLException)
				errorCode = "" + ((SQLException) ex).getErrorCode();
			String msg = String.format("Unable to insert records for table %s. Err code '%s', msg: '%s', sql '%s'", 
	    				tableName, errorCode, ex.getMessage(), insertSql);
    		throw new DataAccessException(msg, ex);
		} finally {
			try { insertOutputReportRecordStmt.close();	} catch (SQLException e) {}
		}
    }


	private void createTable(String sql) throws SQLException  {
		Statement statement = connection.createStatement();
		statement.executeUpdate(sql);
		statement.close();
	}
	
	private boolean doesTableExist() throws SQLException  {
		DatabaseMetaData md = connection.getMetaData();
		ResultSet rs = md.getTables(null, null, tableName, null);
		boolean exists = rs.next();
		assert(! rs.next());
		return exists;
	} 
    
    private void bindAndExecuteRecords(PreparedStatement insertOutputReportRecordStmt, Collection<UploadableRecord> records) throws Exception {
		QueryBinderForJDBC queryBinder = new QueryBinderForJDBC(insertOutputReportRecordStmt);
		for (UploadableRecord record : records) {
			record.bindFields(queryBinder);
			if (queryBinder.getBatchCount() >= maxBatchSize) {
				insertOutputReportRecordStmt.executeBatch();
				queryBinder.resetBatchCount();
			}
		}
    	if (queryBinder.getBatchCount() >= 0) {
			insertOutputReportRecordStmt.executeBatch();
			queryBinder.resetBatchCount();
		}
		if (! connection.getAutoCommit()) {
			connection.commit();
		}
	}
    
    @Override
    public UploadReportsDao copy(Connection connection) 
    {
        UploadReportsDao retVal = new UploadReportsDao(this);
        retVal.setConnection(connection);

        return retVal;
    }

    @Override
    public void finalize() 
    {
        super.finalize();
    }
    
    public String toString() { 
    	return getClass().getSimpleName() + " w for table "  + tableName;
    }
}
