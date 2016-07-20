/**
 * Copyright "TBD", Metron Aviation & CSSI.  All rights reserved.
 * 
 * This computer Software was developed with the sponsorship of the U.S. Government
 * under Contract No. DTFAWA-10-D-00033, which has a copyright license in accordance with AMS 3.5-13.(c)(1).
 */

package gov.faa.ang.swac.datalayer.storage.db;

import gov.faa.ang.swac.common.datatypes.Timestamp;
import gov.faa.ang.swac.datalayer.MappedDataAccess;
import gov.faa.ang.swac.datalayer.ResourceManager;
import gov.faa.ang.swac.datalayer.identity.FileDataDescriptor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JDBCConnectionFactory {
	private List<String> exemptDbTemplates;
	private Map<String,JDBCDatabase> databaseTemplates;
	private Map<String,JDBCDatabase> databases;
        
	public JDBCConnectionFactory() {
	    this.databases = new HashMap<String, JDBCDatabase>();
	}

	public void setExemptDbTemplates(List<String> exemptTemplates){
		this.exemptDbTemplates = exemptTemplates;
	}
	
	public Map<String, JDBCDatabase> getDatabaseTemplates() {
		return databaseTemplates;
	}

	public void setDatabaseTemplates(Map<String, JDBCDatabase> databaseTemplates) {
		this.databaseTemplates = databaseTemplates;
	}

	public Map<String, JDBCDatabase> getDatabases() {
		return databases;
	}

	public void setDatabases(Map<String, JDBCDatabase> databases) {
		this.databases = databases;
	}

	public Connection getConnection(String name) throws SQLException {
		return this.databases.get(name).getConnection();
	}

	/**
	 * Creates the DB Instance and establishes a connection without executing any of the initialization scripts.
	 * 
	 * @param resMan
	 * @param instanceId
	 * @throws SQLException
	 */
	public void createDbInstance(ResourceManager resMan, String instanceId, Timestamp baseDay, int forecastYear, String classifier) throws SQLException {
		
		if (this.databaseTemplates != null){
			for (String key : this.databaseTemplates.keySet())
			{
				String newKey = key + "-" + instanceId;
				
				if (!this.databases.containsKey(newKey)) {
					JDBCDatabase db = this.databaseTemplates.get(key);
					JDBCDatabase newDb = db.createNewInstance(instanceId);
					this.databases.put(newKey, newDb);
					if (newDb instanceof H2Database) {
						H2Database h2NewDb = (H2Database)newDb;
	                                
                        // Since all files are parameterizable we must ensure the correct file names are used.
                        List<FileDataDescriptor> descriptors = new ArrayList<FileDataDescriptor>();
                        if (h2NewDb.getOnDiskDbFile() != null) {
                            descriptors.add(h2NewDb.getOnDiskDbFile());
                        }
                        if (h2NewDb.getInitializationScripts() != null  && !h2NewDb.getInitializationScripts().isEmpty()) {
                            descriptors.addAll(h2NewDb.getInitializationScripts());
                        }
                        if (h2NewDb.getLinkedFileTables() != null && !h2NewDb.getLinkedFileTables().isEmpty()) {
                            descriptors.addAll(h2NewDb.getLinkedFileTables().values());
                        }
                        if (h2NewDb.getlinkedCsvFiles() != null && !h2NewDb.getlinkedCsvFiles().isEmpty()) {
                            descriptors.addAll(h2NewDb.getlinkedCsvFiles().values());
                        }
                        if (h2NewDb.getPostInitializationScripts() != null && !h2NewDb.getPostInitializationScripts().isEmpty()) {
                            descriptors.addAll(h2NewDb.getPostInitializationScripts());
                        }
                        
                        for (FileDataDescriptor fdd : descriptors) {
                            boolean modified = false;
                            if (fdd.getBaseDate() != null && !fdd.getBaseDate().equalValue(baseDay)) {
                                fdd.setBaseDate(baseDay);
                                modified = true;
                            }
                            if (fdd.getForecastFiscalYear() != null && fdd.getForecastFiscalYear().intValue() != forecastYear) {
                                fdd.setForecastFiscalYear(forecastYear);
                                modified = true;
                            }
                            if (fdd.getClassifier() != null && !fdd.getClassifier().contentEquals(classifier)) {
                                fdd.setClassifier(classifier);
                                modified = true;
                            }
                            if (modified) {
                                if (resMan instanceof MappedDataAccess) {
                                    fdd.setResourceName("");
                                    MappedDataAccess mda = (MappedDataAccess)resMan;
                                    String name = mda.getAbsoluteFile(fdd).getName();
                                    fdd.setResourceName(name);
                                }
                            }
                        }
					}
				}
				this.databases.get(newKey).createConnectionPool(resMan);
			}
		}
	}
	
	/**
	 * Finds the DB Instance and executes necessary DB scripts.
	 * 
	 * @param resMan
	 * @param instanceId
	 * @throws SQLException
	 */
	public void executeDbScripts(ResourceManager resMan, String instanceId) throws SQLException {
		if (this.databaseTemplates != null){
			for (String key : this.databaseTemplates.keySet())
			{
				String newKey = key + "-" + instanceId;
				JDBCDatabase db = this.databases.get(newKey);
				if (db != null) {
					db.executeDbScripts(resMan);
				}
			}
		}
	}
	
	/**
	 * Closes the connectionPool for the database(s) with matching instanceId
	 * and removes that DB from the databases HashMap if the database is not exempt.
	 * 
	 * @param instanceId
	 */
    public void closeDatabaseInstance(int instanceId) {
    	if (this.databaseTemplates != null){
	        for (String key : this.databaseTemplates.keySet()) {
	        	if (!this.exemptDbTemplates.contains(key)){
		            String newKey = key + "-" + Integer.toString(instanceId);
		            JDBCDatabase db = this.databases.remove(newKey);
		            db.closeDatabase();
		            db = null;
	        	}
	        }
    	}
    }
    
    /**
	 * Closes the connectionPool for any remaining database connections.
     */
    public void closeRemainingDatabases(){
    	for (String key : this.databases.keySet()) {
    		JDBCDatabase db = this.databases.get(key);
    		db.closeDatabase();
    		db = null;
    	}
    }
}
