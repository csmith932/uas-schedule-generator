/**
 * Copyright "TBD", Metron Aviation & CSSI. All rights reserved.
 *
 * This computer Software was developed with the sponsorship of the U.S.
 * Government under Contract No. DTFAWA-10-D-00033, which has a copyright
 * license in accordance with AMS 3.5-13.(c)(1).
 */
package gov.faa.ang.swac.datalayer.storage.db;

import gov.faa.ang.swac.datalayer.MappedDataAccess;
import gov.faa.ang.swac.datalayer.ResourceManager;
import gov.faa.ang.swac.datalayer.ResourceManager.LOCATION;
import gov.faa.ang.swac.datalayer.identity.FileDataDescriptor;
import java.io.File;
import java.sql.*;
import java.util.*;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.h2.jdbcx.JdbcConnectionPool;

public class H2Database implements JDBCDatabase {

    static {
        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Could not initialize H2 database driver.", e);
        }
    }
    
    private static final Logger logger = LogManager.getLogger(H2Database.class);
    private static final String DEFAULT_MEMORY_CONNECTION_STRING = "jdbc:h2:mem:";
    private static final String DEFAULT_FILE_CONNECTION_STRING = "jdbc:h2:file:";
    private static final String DEFAULT_USERNAME = "sa";
    private static final String DEFAULT_PASSWORD = "";
    
    private boolean inMemory;
    
    private String dbName;
    private String username;
    private String password;
    private String connectionString;
    private FileDataDescriptor onDiskDbFile;
    private Map<String, String> aliases;
    private Map<String, FileDataDescriptor> linkedCsvFiles;
    private Map<String, FileDataDescriptor> linkedFileTables;
    private List<FileDataDescriptor> initializationScripts;
    private List<FileDataDescriptor> postInitializationScripts;
    private JdbcConnectionPool connectionPool;

    public H2Database () {
        this.username = DEFAULT_USERNAME;
        this.password = DEFAULT_PASSWORD;
        this.connectionString = DEFAULT_MEMORY_CONNECTION_STRING;
    }
    
    public H2Database(H2Database org) {
        this();
        
        this.inMemory = org.inMemory;
        this.dbName = org.dbName;
        this.username = org.username;
        this.password = org.password;
        this.connectionString = org.connectionString;

        if (org.onDiskDbFile != null) {
            this.onDiskDbFile = new FileDataDescriptor(org.onDiskDbFile);
        }
        
        if (org.aliases != null) {
            this.aliases = new LinkedHashMap<String, String>();
            this.aliases.putAll(org.aliases);
        }
        
        if (org.linkedFileTables != null) {
            this.linkedFileTables = new LinkedHashMap<String, FileDataDescriptor>();
            this.linkedFileTables.putAll(org.linkedFileTables);
        }
        
        if (org.linkedCsvFiles != null) {
            this.linkedCsvFiles = new LinkedHashMap<String, FileDataDescriptor>();
            this.linkedCsvFiles.putAll(org.linkedCsvFiles);
        }
        
        if (org.initializationScripts != null) {
            this.initializationScripts = new ArrayList<FileDataDescriptor>();
            this.initializationScripts.addAll(org.initializationScripts);
        }
        
        if (org.postInitializationScripts != null) {
            this.postInitializationScripts = new ArrayList<FileDataDescriptor>();
            this.postInitializationScripts.addAll(org.postInitializationScripts);
        }
    }
    
    public H2Database(H2Database org, String scenarioExecutionId) {
        this(org);
        
        this.dbName = org.dbName + "-" + scenarioExecutionId;
        this.connectionString = org.connectionString + "-" + scenarioExecutionId;

        if (org.onDiskDbFile != null) {
            this.onDiskDbFile = new FileDataDescriptor(org.onDiskDbFile);
            this.onDiskDbFile.setResourceName(org.onDiskDbFile.getResourceName() + "-" + scenarioExecutionId);
        }

        if (org.linkedFileTables != null) {
            // These are all disk based databases which will have the scenario execution id appended to their name.
            for (String key : org.linkedFileTables.keySet()) {
                this.linkedFileTables = new LinkedHashMap<String, FileDataDescriptor>();
                FileDataDescriptor newFdd = new FileDataDescriptor(org.linkedFileTables.get(key));
                newFdd.setResourceName(newFdd.getResourceName() + "-" + scenarioExecutionId);
                this.linkedFileTables.put(key, newFdd);
            }
        }
    }
    
    public String getConnectionString() {
        return connectionString;
    }

    public void setConnectionString(String connectionString) {
        this.connectionString = connectionString;
    }

    public List<FileDataDescriptor> getInitializationScripts() {
        return initializationScripts;
    }

    public void setInitializationScripts(
            List<FileDataDescriptor> initializationScripts) {
        this.initializationScripts = initializationScripts;
    }

    public Map<String, FileDataDescriptor> getlinkedCsvFiles() {
        return linkedCsvFiles;
    }

    public void setlinkedCsvFiles(
            Map<String, FileDataDescriptor> linkedCsvFiles) {
        this.linkedCsvFiles = linkedCsvFiles;
    }

    public Map<String, String> getAliases() {
        return aliases;
    }

    public void setAliases(
            Map<String, String> aliases) {
        this.aliases = aliases;
    }

    public List<FileDataDescriptor> getPostInitializationScripts() {
        return postInitializationScripts;
    }

    public void setPostInitializationScripts(
            List<FileDataDescriptor> viewScripts) {
        this.postInitializationScripts = viewScripts;
    }

    public void setLinkedFileTables(Map<String, FileDataDescriptor> linkedFileTables) {
        this.linkedFileTables = linkedFileTables;
    }

    public Map<String, FileDataDescriptor> getLinkedFileTables() {
        return linkedFileTables;
    }

    public FileDataDescriptor getOnDiskDbFile() {
        return this.onDiskDbFile;
    }

    public void setOnDiskDbFile(FileDataDescriptor onDiskDbFile) {
        this.onDiskDbFile = onDiskDbFile;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public boolean getInMemory() {
        return inMemory;
    }

    public void setInMemory(boolean inMemory) {
        this.inMemory = inMemory;
    }

    public void setInMemory(String inMemory) {
        this.inMemory = Boolean.parseBoolean(inMemory);
    }

    public boolean isInitialized() {
        return this.connectionPool != null;
    }

    @Override
    public Connection getConnection() throws SQLException {
        if (!this.isInitialized()) {
            throw new IllegalStateException("Attempting to connect to an uninitialized database: " + this.connectionString);
        }
        return this.connectionPool.getConnection();
        // TODO: decide whether we want to reuse the same connection, create new connections for every request, or connection pool
        // TODO: we also need a plan for closing connections and cleaning up resources
        // return DriverManager.getConnection(connectionString, username, password);
    }

    private void createConnectionString(ResourceManager resMan) {
        if (this.inMemory) {
            this.connectionString = H2Database.DEFAULT_MEMORY_CONNECTION_STRING + this.dbName;
        } else {
            LOCATION loc = this.onDiskDbFile.getLocation();
            String name = this.onDiskDbFile.getResourceName();

            this.connectionString = H2Database.DEFAULT_FILE_CONNECTION_STRING 
                    + resMan.getFile(loc, name).getAbsolutePath();
        }
    }

    /**
     * This should only be called once per SWAC execution Thread.
     * 
     * @throws SQLException
     */
    @Override
    public void createConnectionPool(ResourceManager resMan) throws SQLException {
    	createConnectionString(resMan);
   		this.connectionPool = JdbcConnectionPool.create(connectionString, username, password);
    }

    @Override
    public void executeDbScripts(ResourceManager resMan) throws SQLException{
        initScripts(resMan);
        if (this.linkedCsvFiles != null) {
            createLinkedCsvTables(resMan);
        }

        if (this.linkedFileTables != null) {
            linkFileTables(resMan);
        }

        if (this.postInitializationScripts != null) {
            createViews(resMan);
        }

        initAliases();
    }
    
    private void initAliases() throws SQLException {
        if (this.aliases == null || this.aliases.isEmpty()) {
            return; // Trivial
        }

        Connection conn = this.connectionPool.getConnection();
        Statement stmt = conn.createStatement();

        for (Map.Entry<String, String> entry : this.aliases.entrySet()) {
            // TODO: Current = fail fast. May want recovery language for aliases that already exist
            //String sql = "CREATE ALIAS IF NOT EXISTS"
            String sql = "CREATE ALIAS "
                    + entry.getKey()
                    + " FOR \""
                    + entry.getValue()
                    + "\"";

            stmt.execute(sql);
        }
        conn.close();
    }

    private void createLinkedCsvTables(ResourceManager resMan) throws SQLException {
        Connection conn = this.connectionPool.getConnection();
        Statement stmt = conn.createStatement();

        for (Map.Entry<String, FileDataDescriptor> entry : this.linkedCsvFiles.entrySet()) {
            String name;

            if (resMan instanceof MappedDataAccess
                    && (entry.getValue().getResourceName() == null
                    || entry.getValue().getResourceName().isEmpty())) {
                MappedDataAccess mda = (MappedDataAccess) resMan;
                mda.getAbsoluteFile(entry.getValue()); //populates entry's value if empty.
            }

            LOCATION loc = entry.getValue().getLocation();
            name = entry.getValue().getResourceName();

            String sql = "CREATE TABLE "
                    + entry.getKey()
                    + " AS SELECT * FROM CSVREAD('"
                    + resMan.getFile(loc, name).getAbsolutePath()
                    + "', NULL, 'lineComment=#')";

            stmt.execute(sql);
        }
        conn.close();
    }

    private void initScripts(ResourceManager resMan) throws SQLException {
        Connection conn = this.connectionPool.getConnection();
        Statement command = conn.createStatement();
        for (FileDataDescriptor file : this.initializationScripts) {
            String filePath = resMan.getFile(file.getLocation(), file.getResourceName()).getAbsolutePath();
            command.execute("RUNSCRIPT FROM '" + filePath + "'"); // TODO: may need escape sequence substitution
        }
        conn.close();
    }

    private void linkFileTables(ResourceManager resMan) throws SQLException {
        Connection conn = this.connectionPool.getConnection();
        Statement stmt = conn.createStatement();

        for (Map.Entry<String, FileDataDescriptor> entry : this.linkedFileTables.entrySet()) {
            File file = null;
            String name;

            if (resMan instanceof MappedDataAccess
                    && (entry.getValue().getResourceName() == null
                    || entry.getValue().getResourceName().isEmpty())) {
                MappedDataAccess mda = (MappedDataAccess) resMan;
                file = mda.getAbsoluteFile(entry.getValue());
            }

            LOCATION loc = entry.getValue().getLocation();

            if (file != null) {
                name = file.getAbsolutePath().replace(resMan.getDataDir(), "");
            } else {
                name = entry.getValue().getResourceName();
            }

            String sql = "CREATE LINKED TABLE "
                    + entry.getKey()
                    + "('org.h2.Driver','jdbc:h2:file:"
                    + resMan.getFile(loc, name).getAbsolutePath()
                    + "','sa','','"
                    + entry.getKey() + "')";

            stmt.execute(sql);
        }
        conn.close();
    }

    private void createViews(ResourceManager resMan) throws SQLException {
        Connection conn = this.connectionPool.getConnection();
        Statement command = conn.createStatement();
        for (FileDataDescriptor file : this.postInitializationScripts) {
            if (resMan instanceof MappedDataAccess
                    && (file.getResourceName() == null
                    || file.getResourceName().isEmpty())) {
                MappedDataAccess mda = (MappedDataAccess) resMan;
                mda.getAbsoluteFile(file);
            }
            String filePath = resMan.getFile(file.getLocation(), file.getResourceName()).getAbsolutePath();
            command.execute("RUNSCRIPT FROM '" + filePath + "'"); // TODO: may need escape sequence substitution
        }
        conn.close();
    }

    public void closeDatabase() {
    	try{
	    	Connection conn = this.connectionPool.getConnection();
	    	conn.createStatement().execute("DROP ALL OBJECTS");
	    	conn.close();
    	}
    	catch(Exception e){
    		logger.debug("Error dropping Objects from DB: " + this.dbName);
    	}
        this.connectionPool.dispose();
        this.connectionPool = null;
    }
    
    // TODO need to figure out when/how we are cleaning up db resources
    @Override
    public void finalize() throws Throwable {
        super.finalize();
        this.connectionPool.dispose();
        this.connectionPool = null;
    }
    
    @Override
    public H2Database createNewInstance(String scenarioExecutionId) {
        return new H2Database(this, scenarioExecutionId);
    }
}
