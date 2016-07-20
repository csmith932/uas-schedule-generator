/**
 * Copyright "TBD", Metron Aviation & CSSI. All rights reserved.
 *
 * This computer Software was developed with the sponsorship of the U.S.
 * Government under Contract No. DTFAWA-10-D-00033, which has a copyright
 * license in accordance with AMS 3.5-13.(c)(1).
 */
package gov.faa.ang.swac.controller;

import gov.faa.ang.swac.datalayer.AdHocDataAccess;
import gov.faa.ang.swac.datalayer.MappedDataAccess;
import java.util.List;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.Resource;

public final class GlobalApplicationContext {

    public static final String SWAC_HOME_ENV_VAR_NAME = "SWAC_HOME";
    public static final String SWAC_HOME_DIR_PROP_NAME = "swac.home.dir";
    public static final String SWAC_WORK_ENV_VAR_NAME = "SWAC_WORK";
    public static final String SWAC_WORK_DIR_PROP_NAME = "swac.work.dir";
    public static final String SWAC_HOME_APPLICATION_CONTEXT_FILENAME = "applicationContextMain.xml";
//    public static final String LOG4J_NORMAL_CONFIG = "log4j-normal.xml";
//    public static final String LOG4J_MONTE_CARLO_CONFIG = "log4j-monte-carlo.xml";
    private static Logger logger = LogManager.getLogger(GlobalApplicationContext.class);
    private static GlobalApplicationContext INSTANCE;
//    public static enum Log4jSetting {
//
//        NORMAL, MONTE_CARLO, REPLAY
//    }
    private String swacHomeDir;
    private String swacWorkDir;
    private String swacBinDir;
    private String swacLibCDir;
    private String swacLibJavaDir;
    private String swacConfigDir;
    private String swacDataDir;
    private String swacScenariosDir;
    private String swacLogDir;
    private String swacTempDir;
//    private String swacOutputDir;
//    private String swacReportDir;
    private String swacDefaultImportsFile;
    private String swacDefaultExportsFile;
    private String swacVersion;
    @SuppressWarnings("unused")
    private List<Resource> swacExportData;
    @SuppressWarnings("unused")
    private List<Resource> swacExportDataTemplates;
    private List<String> swacLogFiles;

    private GlobalApplicationContext() {
        // Private constructor prevents instantiation.
    }

    public String getSwacHomeDir() {
        return this.swacHomeDir;
    }

    public void setSwacHomeDir(String val) {
        this.swacHomeDir = val;
    }

    public String getSwacWorkDir() {
        return this.swacWorkDir;
    }

    public void setSwacWorkDir(String val) {
        this.swacWorkDir = val;
    }

    public String getSwacBinDir() {
        return this.swacBinDir;
    }

    public void setSwacBinDir(String val) {
        this.swacBinDir = val;
    }

    public String getSwacLibCDir() {
        return this.swacLibCDir;
    }

    public void setSwacLibCDir(String val) {
        this.swacLibCDir = val;
    }

    public String getSwacLibJavaDir() {
        return this.swacLibJavaDir;
    }

    public void setSwacLibJavaDir(String val) {
        this.swacLibJavaDir = val;
    }

    public String getSwacConfigDir() {
        return this.swacConfigDir;
    }

    public void setSwacConfigDir(String val) {
        this.swacConfigDir = val;
    }

    public String getSwacDataDir() {
        return this.swacDataDir;
    }

    public void setSwacDataDir(String val) {
        this.swacDataDir = val;
    }

    public String getSwacScenariosDir() {
        return this.swacScenariosDir;
    }

    public void setSwacScenariosDir(String val) {
        this.swacScenariosDir = val;
    }

    public String getSwacLogDir() {
        return this.swacLogDir;
    }

    public void setSwacLogDir(String val) {
        this.swacLogDir = val;
    }

    public String getSwacTempDir() {
        return this.swacTempDir;
    }

    public void setSwacTempDir(String val) {
        this.swacTempDir = val;
    }

//    public String getSwacOutputDir() {
//        return this.swacOutputDir;
//    }
//
//    public void setSwacOutputDir(String val) {
//        this.swacOutputDir = val;
//    }
//
//    public String getSwacReportDir() {
//        return this.swacReportDir;
//    }
//
//    public void setSwacReportDir(String val) {
//        this.swacReportDir = val;
//    }
    public String getSwacDefaultImportsFile() {
        return this.swacDefaultImportsFile;
    }

    public void setSwacDefaultImportsFile(String val) {
        this.swacDefaultImportsFile = val;
    }

    public String getSwacDefaultExportsFile() {
        return this.swacDefaultExportsFile;
    }

    public void setSwacDefaultExportsFile(String val) {
        this.swacDefaultExportsFile = val;
    }

    public List<String> getSwacLogFiles() {
        return this.swacLogFiles;
    }

    public void setSwacLogFiles(List<String> val) {
        this.swacLogFiles = val;
    }

    public String getSwacVersion() {
        return swacVersion;
    }

    public void setSwacVersion(String swacVersion) {
        this.swacVersion = swacVersion;
    }

    public static GlobalApplicationContext getInstance() {
        if (INSTANCE == null) {
            try {
                // Validate and set SWAC_HOME and SWAC_WORK global properties
                if (System.getProperty(SWAC_HOME_DIR_PROP_NAME) == null) {
                    String home = System.getenv(SWAC_HOME_ENV_VAR_NAME);
                    if (home == null) {
                        throw new ExitException("SWAC_HOME environment variable is not configured.");
                    }
                    System.setProperty(SWAC_HOME_DIR_PROP_NAME, home);
                }
                if (System.getProperty(SWAC_WORK_DIR_PROP_NAME) == null) {
                    String work = System.getenv(SWAC_WORK_ENV_VAR_NAME);
                    if (work == null) {
                        throw new ExitException("SWAC_HOME environment variable is not configured.");
                    }
                    System.setProperty(SWAC_WORK_DIR_PROP_NAME, work);
                }

                ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(SWAC_HOME_APPLICATION_CONTEXT_FILENAME);
                INSTANCE = ctx.getBean(GlobalApplicationContext.class);

                // Set the static path for ad hoc access to the file system
                AdHocDataAccess.setResourceManager(INSTANCE.createDao(null, null));
            } catch (Exception ex) {
                logger.fatal("Invalid configuration for SWAC Controller. Aborting...", ex);
                logger.trace(ex.getStackTrace());
                throw new RuntimeException(ex);
            }
        }

        return INSTANCE;
    }

    public MappedDataAccess createDao(String batchFileName, String scenarioName) {
        MappedDataAccess retVal = new MappedDataAccess();

        retVal.setBatchName(batchFileName);
        retVal.setScenarioName(scenarioName);
        retVal.setBinPath(this.swacBinDir);
        retVal.setConfigPath(this.swacConfigDir);
        retVal.setRootPath(this.swacWorkDir);
        retVal.setDataDir(this.swacDataDir);
        retVal.setTempDir(this.swacTempDir);

        return retVal;
    }
}
