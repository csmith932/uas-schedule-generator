/**
 * Copyright "TBD", Metron Aviation & CSSI. All rights reserved.
 *
 * This computer Software was developed with the sponsorship of the U.S.
 * Government under Contract No. DTFAWA-10-D-00033, which has a copyright
 * license in accordance with AMS 3.5-13.(c)(1).
 */
package gov.faa.ang.swac.datalayer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

/**
 * ResourceManager acts as a gateway to the underlying file system, abstracting
 * away the directory structure and manual manipulation of files. Calling code
 * locates resources by specifying a string resource name and one of several
 * enumerated locations. Interaction with resources is via streams only, with an
 * existence verification predicate and the option to create temporary
 * resources.
 *
 * @author csmith
 *
 */
public class ResourceManager {
    public enum LOCATION {
        ROOT, BIN, CONFIG, DATA, TEMP, OUTPUT, REPORT, CACHE, DB, SCENARIO, CLASSPATH, TM_REPORT, TAM_REPORT, TAM_NETWORK_REPORT
    }
    
    private String rootPath;
    private File rootFile;
    private String binPath;
    private File binFile;
    private String configPath;
    private File configFile;
    private String dataDir;
    private String tempDir;
    private String outputDir;
    private String reportDir;
    private String dbDir;
    private String cacheDir;
    private String scenarioDir;
    private String tmReportDir;
    private String tamReportDir;
    private String tamNetworkReportDir;
    private String batchName;
    private String scenarioName;
    private static final String TEMP_FILE_PREFIX = "swac";

    public ResourceManager() {
    }

    public ResourceManager(ResourceManager val) {
        rootPath = val.rootPath;
        rootFile = val.rootFile;
        binPath = val.binPath;
        binFile = val.binFile;
        configPath = val.configPath;
        configFile = val.configFile;
        dataDir = val.dataDir;
        tempDir = val.tempDir;
        outputDir = val.outputDir;
        reportDir = val.reportDir;
        dbDir = val.dbDir;
        cacheDir = val.cacheDir;
        scenarioDir = val.scenarioDir;
        batchName = val.batchName;
        scenarioName = val.scenarioName;
    }

    public String getRootPath() {
        return rootPath;
    }

    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
        File f = new File(rootPath);
        if (!f.exists() || !f.isDirectory()) {
            // TODO: commented out to fix chicken-and-egg problem with GlobalApplicationContext and bootstrapping a non-existent work directory
            //throw new IllegalArgumentException("Error: ResourceManager root path is invalid: " + rootPath);
        }
        this.rootFile = f;
    }

    public String getBinPath() {
        return binPath;
    }

    public void setBinPath(String binPath) {
        this.binPath = binPath;
        File f = new File(binPath);
        if (!f.exists() || !f.isDirectory()) {
            // TODO: commented out to fix chicken-and-egg problem with GlobalApplicationContext and bootstrapping a non-existent work directory
            //throw new IllegalArgumentException("Error: ResourceManager bin path is invalid: " + rootPath);
        }
        this.binFile = f;
    }

    public String getConfigPath() {
        return binPath;
    }

    public void setConfigPath(String configPath) {
        this.configPath = configPath;
        File f = new File(configPath);
        if (!f.exists() || !f.isDirectory()) {
            // TODO: commented out to fix chicken-and-egg problem with GlobalApplicationContext and bootstrapping a non-existent work directory
            //throw new IllegalArgumentException("Error: ResourceManager bin path is invalid: " + rootPath);
        }
        this.configFile = f;
    }

    public String getDataDir() {
        return dataDir;
    }

    public void setDataDir(String dataDir) {
        this.dataDir = dataDir;
    }

    public String getTempDir() {
        return tempDir;
    }

    public void setTempDir(String tempDir) {
        this.tempDir = tempDir;
    }

    public String getOutputDir() {
        return outputDir;
    }

    public void setOutputDir(String outputDir) {
        this.outputDir = outputDir;
    }

    public String getReportDir() {
        return reportDir;
    }

    public void setReportDir(String reportDir) {
        this.reportDir = reportDir;
    }

    public String getDbDir() {
        return dbDir;
    }

    public void setDbDir(String dbDir) {
        this.dbDir = dbDir;
    }

    public String getCacheDir() {
        return cacheDir;
    }

    public void setCacheDir(String cacheDir) {
        this.cacheDir = cacheDir;
    }

    public String getScenarioDir() {
        return scenarioDir;
    }

    public void setScenarioDir(String scenarioDir) {
        this.scenarioDir = scenarioDir;
    }

    public String getTmReportDir() {
        return tmReportDir;
    }

    public void setTmReportDir(String reportDir) {
        this.tmReportDir = reportDir;
    }

    public String getTamReportDir() {
        return tamReportDir;
    }

    public void setTamReportDir(String reportDir) {
        this.tamReportDir = reportDir;
    }

    public String getTamNetworkReportDir() {
        return tamNetworkReportDir;
    }

    public void setTamNetworkReportDir(String reportDir) {
        this.tamNetworkReportDir = reportDir;
    }
    
    public String getBatchName() {
        return batchName;
    }

    public void setBatchName(String batchName) {
        this.batchName = batchName;
    }

    public String getScenarioName() {
        return scenarioName;
    }

    public void setScenarioName(String scenarioName) {
        this.scenarioName = scenarioName;
    }

    public boolean exists(LOCATION location, String resourceName) {
        if (location.equals(LOCATION.CLASSPATH)) {
            URL url = getClasspathUrl(resourceName);
            return (url != null);
        } else {
            File path = getFile(location, resourceName);
            return path.exists() && path.isFile();
        }
    }

    @Deprecated
    public InputStream openInput(LOCATION location, String resourceName) throws DataAccessException {
        if (location.equals(LOCATION.CLASSPATH)) {
            URL url = getClasspathUrl(resourceName);
            try {
                return url.openStream();
            } catch (IOException ex) {
                throw new DataAccessException("Error: Resource \"" + resourceName + "\" not found in " + location.toString(), ex);
            }
        } else {
            File path = getFile(location, resourceName);
            try {
                return new FileInputStream(path);
            } catch (FileNotFoundException ex) {
                throw new DataAccessException("Error: Resource \"" + resourceName + "\" not found in " + location.toString(), ex);
            }
        }
    }

    @Deprecated
    public OutputStream openOutput(LOCATION location, String resourceName, boolean append) throws DataAccessException {
        if (location.equals(LOCATION.CLASSPATH)) {
            throw new DataAccessException("Error: Classpath resources are read-only");
        } else {
            File path = getFile(location, resourceName);
            try {
                return new FileOutputStream(path, append);
            } catch (FileNotFoundException ex) {
                throw new DataAccessException("Error: Resource \"" + resourceName + "\" not found in " + location.toString(), ex);
            }
        }
    }

    @Deprecated
    public OutputStream openOutput(LOCATION location, String resourceName) throws DataAccessException {
        if (location.equals(LOCATION.CLASSPATH)) {
            throw new DataAccessException("Error: Classpath resources are read-only");
        } else {
            File path = getFile(location, resourceName);
            try {
                return new FileOutputStream(path);
            } catch (FileNotFoundException ex) {
                throw new DataAccessException("Error: Resource \"" + resourceName + "\" not found in " + location.toString(), ex);
            }
        }
    }

    public static File createTempFile() throws IOException {
        File temp = File.createTempFile(TEMP_FILE_PREFIX, null);
        temp.deleteOnExit();
        return temp;
    }

    public File createTemporaryResource() throws IOException {
        File temp = File.createTempFile(TEMP_FILE_PREFIX, null, new File(this.tempDir));
        temp.deleteOnExit();
        return temp;
    }

    /**
     * Deletes all files in a location
     *
     * @param location
     */
    public void clean(LOCATION location) {
        File cleanDir = getDir(location);
        if (!cleanDir.exists()) {
            // Location does not exist: create empty and return
            cleanDir.mkdirs();
        } else {
            cleanRecursive(cleanDir);
        }
    }

    private static void cleanRecursive(File path) {
        if (path.exists()) {
            File[] files = path.listFiles();
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    cleanRecursive(files[i]);
                } else {
                    files[i].delete();
                }
            }
        }
    }

    protected File getDir(LOCATION location) {
        File dir = null;
        switch (location) {
            case ROOT:
                dir = this.rootFile;
                break;
            case BIN:
                dir = this.binFile;
                break;
            case CONFIG:
                dir = this.configFile;
                break;
            case DATA:
                dir = new File(this.dataDir);
                break;
            case TEMP:
                dir = new File(this.tempDir);
                break;
            case OUTPUT:
                dir = new File(this.outputDir);
                if (scenarioName != null) {
                    dir = new File(dir, scenarioName);
                }
                break;
            case REPORT:
                dir = new File(this.reportDir);
                if (scenarioName != null) {
                    dir = new File(dir, scenarioName);
                }
                break;
            case DB:
                if (scenarioName != null) {
                    dir = new File(this.dbDir);
                } else {
                    throw new IllegalArgumentException("LOCATION is not valid for a file path");
                }
                break;
            case CACHE:
                if (scenarioName != null) {
                    dir = new File(this.cacheDir);
                } else {
                    throw new IllegalArgumentException("LOCATION is not valid for a file path");
                }
                break;
            case SCENARIO:
                if (scenarioName != null) {
                    dir = new File(this.scenarioDir);
                } else {
                    throw new IllegalArgumentException("LOCATION is not valid for a file path");
                }
                break;
            case TM_REPORT:
                dir = new File(this.tmReportDir);
                if (scenarioName != null) {
                    dir = new File(dir, scenarioName);
                }
                break;
            case TAM_REPORT:
                dir = new File(this.tamReportDir);
                if (scenarioName != null) {
                    dir = new File(dir, scenarioName);
                }
                break;
            case TAM_NETWORK_REPORT:
                dir = new File(this.tamNetworkReportDir);
                if (scenarioName != null) {
                    dir = new File(dir, scenarioName);
                }
                break;
            default:
                throw new IllegalArgumentException("LOCATION is not valid for a file path");
        }

        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    public File getFile(LOCATION location, String resourceName) {
        File dir = getDir(location);
        return new File(dir, resourceName);
    }

    protected URL getClasspathUrl(String resourceName) {
        ClassLoader loader = ResourceManager.class.getClassLoader();
        return loader.getResource("classpath:/" + resourceName);
    }
}
