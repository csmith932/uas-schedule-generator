/**
 * Copyright "TBD", Metron Aviation & CSSI. All rights reserved.
 *
 * This computer Software was developed with the sponsorship of the U.S.
 * Government under Contract No. DTFAWA-10-D-00033, which has a copyright
 * license in accordance with AMS 3.5-13.(c)(1).
 */
package gov.faa.ang.swac.controller;

import gov.faa.ang.swac.datalayer.AdHocDataAccess;
import gov.faa.ang.swac.datalayer.AdHocDataAccess.LogLevel;
import gov.faa.ang.swac.datalayer.MappedDataAccess;

import java.io.File;
import java.util.Enumeration;
import java.util.List;

import org.apache.log4j.Appender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.springframework.core.io.Resource;

public final class ScenarioApplicationContext {

    public static final String LOG4J_NORMAL_CONFIG = "log4j-normal.xml";
    public static final String LOG4J_MONTE_CARLO_CONFIG = "log4j-monte-carlo.xml";
    
    private static Logger logger = LogManager.getLogger(ScenarioApplicationContext.class);

    public static enum Log4jSetting {
        NORMAL, MONTE_CARLO, REPLAY
    }

    public static enum ValidationSetting {
        NORMAL, DEEP, DEEP_NO_EXECUTE
    }
    private GlobalApplicationContext gac;
    private String swacScenarioDir;
    private String swacLogDir;
    private String swacTempDir;
    private String swacOutputDir;
    private String swacReportDir;
    private String swacDbDir;
    private String swacCacheDir;
    private String swacScenarioImportsFile;
    private List<Resource> swacLogFiles;
    private ValidationSetting validationSetting;

    public ScenarioApplicationContext() {
        this.gac = GlobalApplicationContext.getInstance();
        this.validationSetting = ValidationSetting.valueOf(System.getProperty("swac.validation.level"));
    }

    public ScenarioApplicationContext(String scenarioName) {
        this.gac = GlobalApplicationContext.getInstance();
        this.validationSetting = ValidationSetting.valueOf(System.getProperty("swac.validation.level"));
        this.init(scenarioName);
    }

    public String getSwacScenarioDir() {
        return this.swacScenarioDir;
    }

    public void setSwacScenarioDir(String val) {
        this.swacScenarioDir = val;
    }

    public String getSwacLogDir() {
        if (this.swacLogDir == null) {
            return this.gac.getSwacLogDir();
        } else {
            return this.swacLogDir;
        }
    }

    public void setSwacLogDir(String val) {
        this.swacLogDir = val;
    }

    public String getSwacTempDir() {
        if (this.swacTempDir == null) {
            return this.gac.getSwacTempDir();
        } else {
            return this.swacTempDir;
        }
    }

    public void setSwacTempDir(String val) {
        this.swacTempDir = val;
    }

    public String getSwacOutputDir() {
        return this.swacOutputDir;
    }

    public void setSwacOutputDir(String val) {
        this.swacOutputDir = val;
    }

    public String getSwacReportDir() {
        return this.swacReportDir;
    }

    public void setSwacReportDir(String val) {
        this.swacReportDir = val;
    }

    public String getSwacDbDir() {
        return this.swacDbDir;
    }

    public void setSwacDbDir(String val) {
        this.swacDbDir = val;
    }

    public String getSwacCacheDir() {
        return this.swacCacheDir;
    }

    public void setSwacCacheDir(String val) {
        this.swacCacheDir = val;
    }

    public String getSwacScenarioImportsFile() {
        return this.swacScenarioImportsFile;
    }

    public void setSwacScenarioImportsFile(String val) {
        this.swacScenarioImportsFile = val;
    }

    public List<Resource> getSwacLogFiles() {
        return this.swacLogFiles;
    }

    public void setSwacLogFiles(List<Resource> val) {
        this.swacLogFiles = val;
    }

    public ValidationSetting getValidationSetting() {
        return this.validationSetting;
    }

    // TODO: need to set this based on command line params
    public void setValidationSetting(ValidationSetting val) {
        this.validationSetting = val;
    }

    public String getSwacHomeDir() {
        return this.gac.getSwacHomeDir();
    }

    public String getSwacWorkDir() {
        return this.gac.getSwacWorkDir();
    }

    public String getSwacBinDir() {
        return this.gac.getSwacBinDir();
    }

    public String getSwacLibCDir() {
        return this.gac.getSwacLibCDir();
    }

    public String getSwacLibJavaDir() {
        return this.gac.getSwacLibJavaDir();
    }

    public String getSwacConfigDir() {
        return this.gac.getSwacConfigDir();
    }

    public String getSwacDataDir() {
        return this.gac.getSwacDataDir();
    }

    public String getSwacDefaultImportsFile() {
        return this.gac.getSwacDefaultImportsFile();
    }

    public String getSwacDefaultExportsFile() {
        return this.gac.getSwacDefaultExportsFile();
    }

    public String getSwacVersion() {
        return this.gac.getSwacVersion();
    }

    public void init(String scenarioName) {
        this.swacScenarioDir = this.gac.getSwacScenariosDir() + File.separator + scenarioName;

        this.swacLogDir = this.swacScenarioDir + File.separator + "log";
        this.swacTempDir = this.swacScenarioDir + File.separator + "temp";
        this.swacOutputDir = this.swacScenarioDir + File.separator + "outputs";
        this.swacReportDir = this.swacScenarioDir + File.separator + "reports";
        this.swacDbDir = this.swacScenarioDir + File.separator + "db";
        this.swacCacheDir = this.swacScenarioDir + File.separator + "cache";

        // Set the static path for ad hoc access to the file system
        AdHocDataAccess.setResourceManager(createDao(scenarioName, null));
    }

    public MappedDataAccess createDao(String batchFileName, String scenarioName) {
        MappedDataAccess retVal = this.gac.createDao(batchFileName, scenarioName);

        retVal.setBatchName(batchFileName);
        retVal.setScenarioName(scenarioName);
        retVal.setOutputDir(this.swacOutputDir);
        retVal.setReportDir(this.swacReportDir);
        retVal.setTempDir(this.swacTempDir);
        retVal.setDbDir(this.swacDbDir);
        retVal.setCacheDir(this.swacCacheDir);
        retVal.setScenarioDir(this.swacScenarioDir);
        
        retVal.setTmReportDir(this.swacReportDir + File.separator + "TRAJ_MOD_KML");
        retVal.setTamReportDir(this.swacReportDir + File.separator + "TAM_KML");
        retVal.setTamNetworkReportDir(this.swacReportDir + File.separator + "TAM_KML" + File.separator + "network");

        return retVal;
    }

    public void updateLog4jSettings(Log4jSetting mode) {
        LogManager.resetConfiguration();
        String location = this.swacScenarioDir + File.separator;
        
        switch (mode) {
            case NORMAL: {
                DOMConfigurator.configure(location + LOG4J_NORMAL_CONFIG);
                break;
            }
            case MONTE_CARLO: {
                DOMConfigurator.configure(location + LOG4J_MONTE_CARLO_CONFIG);
                break;
            }
            case REPLAY: {
                DOMConfigurator.configure(location + LOG4J_NORMAL_CONFIG);
                break;
            }
            default: {
                break;
            }
        }

        LogLevel swacLogLevel = AdHocDataAccess.getLogLevel();
        Level log4jLevel = Level.INFO;
        switch (swacLogLevel) {
            case NONE: {
            	log4jLevel = Level.FATAL;
                break;
            }
            case DEBUG: {
            	log4jLevel = Level.DEBUG;
                break;
            }
		default:
			break;
        }
        System.setProperty("log4j.threshold", log4jLevel.toString());
        
        @SuppressWarnings("unchecked")
		Enumeration<Logger> allLoggers = LogManager.getCurrentLoggers();
        
        while (allLoggers.hasMoreElements()) {
            Logger currentLog4Jlogger = allLoggers.nextElement();
            if (currentLog4Jlogger.getLevel() == null || log4jLevel.isGreaterOrEqual(currentLog4Jlogger.getLevel())) {
                currentLog4Jlogger.setLevel(log4jLevel);
                
                @SuppressWarnings("rawtypes")
				Enumeration en = currentLog4Jlogger.getAllAppenders();
                if (en != null) { 
	                while (en.hasMoreElements()) {
	                	Object appender = en.nextElement();
	                	if (appender instanceof FileAppender) { 
	                		FileAppender fileAppender = (FileAppender) appender;
	                		String fileName = fileAppender.getFile();
	                		// fileName can be null if another application has a lock on the file.
	                		if (fileName != null) { 
		                		File file = new File(fileName);
		                		if (swacLogLevel.equals(LogLevel.VERBOSE) && file.getParentFile().getName().equals("log")) {
		                			fileAppender.setThreshold(Level.WARN);
		                			currentLog4Jlogger.setLevel(Level.WARN);
		                		}
	                		}
	                	}
	                }
                }
                if (swacLogLevel != LogLevel.DEBUG) {
                    currentLog4Jlogger.removeAppender("DEBUG_LOG");
                    currentLog4Jlogger.removeAppender("SIM_TRACE_LOG");
                }
            }
        }
    }
}
