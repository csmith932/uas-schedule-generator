/**
 * Copyright "TBD", Metron Aviation & CSSI.  All rights reserved.
 * 
 * This computer Software was developed with the sponsorship of the U.S. Government
 * under Contract No. DTFAWA-10-D-00033, which has a copyright license in accordance with AMS 3.5-13.(c)(1).
 */

package gov.faa.ang.swac.datalayer.storage.db;

import gov.faa.ang.swac.datalayer.ResourceManager;

import java.sql.Connection;
import java.sql.SQLException;

public interface JDBCDatabase {
	public JDBCDatabase createNewInstance(String scenarioExecutionId);
	public void createConnectionPool(ResourceManager resMan) throws SQLException;
	public void executeDbScripts(ResourceManager resMan) throws SQLException;
	public Connection getConnection() throws SQLException;	
	public void closeDatabase();
}
