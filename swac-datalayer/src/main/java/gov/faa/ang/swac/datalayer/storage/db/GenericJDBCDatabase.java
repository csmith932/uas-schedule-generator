/**
 * Copyright "TBD", Metron Aviation & CSSI. All rights reserved.
 *
 * This computer Software was developed with the sponsorship of the U.S.
 * Government under Contract No. DTFAWA-10-D-00033, which has a copyright
 * license in accordance with AMS 3.5-13.(c)(1).
 */
package gov.faa.ang.swac.datalayer.storage.db;

import gov.faa.ang.swac.datalayer.ResourceManager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class GenericJDBCDatabase implements JDBCDatabase {
    private static final Logger logger = LogManager.getLogger(GenericJDBCDatabase.class);
    
	private String jdbcUrl;
	private String jdbcDriver;
	private String jdbcUser;
	private String jdbcPass;
	private boolean autoCommitTransactions = false;
	private Connection connection;
	
	//private String debugId = "unk";
	
	public GenericJDBCDatabase() { }
	
    public GenericJDBCDatabase(GenericJDBCDatabase genericJDBCDatabase, String scenarioExecutionId) {
    	jdbcUrl = genericJDBCDatabase.jdbcUrl;
    	jdbcDriver = genericJDBCDatabase.jdbcDriver;
    	jdbcUser = genericJDBCDatabase.jdbcUser;
    	jdbcPass = genericJDBCDatabase.jdbcPass;
    	autoCommitTransactions = genericJDBCDatabase.autoCommitTransactions;
    	//debugId = scenarioExecutionId;
	}

	public void setJdbcUrl(String jdbcUrl) {
		this.jdbcUrl = jdbcUrl;
    }

    public void setJdbcDriver(String jdbcDriver) { 
    	this.jdbcDriver = jdbcDriver;
    }

	public void setJdbcUser(String jdbcUser) {
		// If config key left unspecified, spring will not be able to substitute placeholder  
		if (! jdbcUser.equals("${REPORT_UPLOAD_JDBC_USER}"))
			this.jdbcUser = (jdbcUser.isEmpty()) ? null : jdbcUser;
	}

	public void setJdbcPass(String jdbcPass) {
		// If config key left unspecified, spring will not be able to substitute placeholder
		if (! jdbcPass.equals("${REPORT_UPLOAD_JDBC_PASS}"))
			this.jdbcPass = (jdbcPass.isEmpty()) ? null : jdbcPass;
	}
	
	public void setAutoCommitTransactions(boolean autoCommitTransactions) { 
		this.autoCommitTransactions = autoCommitTransactions;
	}
	
    @Override
    public Connection getConnection() throws SQLException {
    	if (connection == null) {
	        try {
				Class.forName(jdbcDriver);
			} catch (ClassNotFoundException e) {
				logger.warn("Unable to load db database driver " + jdbcDriver, e);
				throw new SQLException(e);
			}
	
	        try {
	        	if (jdbcUser == null)
	        		connection = DriverManager.getConnection(jdbcUrl);
	        	else
	        		connection = DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcPass);
	        	if (connection.getAutoCommit() != autoCommitTransactions)
	        		connection.setAutoCommit(autoCommitTransactions);
	        	
	        	//System.out.printf("#db %s create connection with autocommit %s\n", debugId, autoCommitTransactions);
			} catch (SQLException e) {
				logger.warn("Failed to create database for " + jdbcUrl, e);
				throw e;
			} catch (Exception e) {
				logger.warn("Weird Failed to create database for " + jdbcUrl, e);
				throw new SQLException("Wrapped", e);
			}
    	} 
    	return connection;
    }

    /**
     * This should only be called once per SWAC execution Thread.
     * 
     * @throws SQLException
     */
    @Override
    public void createConnectionPool(ResourceManager resMan) throws SQLException {
   		
    }

    @Override
    public void executeDbScripts(ResourceManager resMan) throws SQLException{
    }
    

    public void closeDatabase() {
    	if (connection != null) {
	    	try{
//	    		System.out.printf("#db " + debugId + " connection closing: here is the stack:--------------\n");
//	    		Thread.dumpStack();
//	    		System.out.println("#---------------------------------");
	    		connection.close();
	    		connection = null;
	    	}
	    	catch(Exception e){
	    		logger.debug("Error closing db " + jdbcDriver);
	    	}
    	}
    }
    
    
    @Override
    public GenericJDBCDatabase createNewInstance(String scenarioExecutionId) {
        return new GenericJDBCDatabase(this, scenarioExecutionId);
    }
}
