/**
 * Copyright "TBD", Metron Aviation & CSSI. All rights reserved.
 *
 * This computer Software was developed with the sponsorship of the U.S.
 * Government under Contract No. DTFAWA-10-D-00033, which has a copyright
 * license in accordance with AMS 3.5-13.(c)(1).
 */
package gov.faa.ang.swac.controller;

import gov.faa.ang.swac.data.DataExport;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.apache.commons.io.FileUtils;

/**
 * Main entry point to SWAC verifies that SWAC_HOME and SWAC_WORK exist
 * (creating SWAC_WORK if necessary), then loads all necessary locations to the
 * classpath of a new class loader and launches Main. Strictly speaking the
 * Bootstrap doesn't need to be run, as long as all required directories exist
 * and are referenced on the classpath
 *
 * @author csmith
 *
 */
public class Bootstrap {

    private static org.apache.log4j.Logger logger = org.apache.log4j.LogManager.getLogger(Bootstrap.class);
    private static final String JAR_EXTENSION = ".jar";
    private static final String MAIN_CLASS = "gov.faa.ang.swac.controller.Main";
    private static final String MAIN_METHOD = "main";

    private Bootstrap() {
        // Static methods only
    }

    public static void main(String[] args) throws MalformedURLException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        try {
            // Fetch the global Spring configuration.
            GlobalApplicationContext context = GlobalApplicationContext.getInstance();

            // Verify SWAC_HOME environment variable.
            // If not found, then the invocation is invalid
            validateSwacHome(context);

            if (!validateSwacExportDirectories(context)) {
                return;
            }

            if (args.length < 1) {
                Main.usage();
                return;
            }


            // Get standard directories
            String lib = context.getSwacLibJavaDir();
            String data = context.getSwacDataDir();
//            String scenarios = context.getSwacScenariosDir();
//            String temp = context.getSwacTempDir();
            String work = context.getSwacWorkDir();

            // Add jar URLs
            List<URL> urls = new ArrayList<URL>();
            for (File f : new File(lib).listFiles()) {
                if (f.getName().endsWith(JAR_EXTENSION)) {
                    urls.add(f.toURI().toURL());
                }
            }

            // Add standard directory URLs
            urls.add(new File(data).toURI().toURL());
//            urls.add(new File(scenarios).toURI().toURL());
//            urls.add(new File(temp).toURI().toURL());
            urls.add(new File(work).toURI().toURL());

            // Add original classpath
            URLClassLoader originalClassLoader = (URLClassLoader) Bootstrap.class.getClassLoader();
            urls.addAll(Arrays.asList(originalClassLoader.getURLs()));

            // Create classloader with URL list
            ClassLoader classloader = new URLClassLoader((URL[]) urls.toArray(new URL[0]), ClassLoader.getSystemClassLoader().getParent());

            Class<?> mainClass = classloader.loadClass(MAIN_CLASS);
            Method main = mainClass.getMethod(MAIN_METHOD, new Class[]{args.getClass()});

            // well-behaved Java packages work relative to the
            // context classloader.  Others don't (like commons-logging)
            Thread.currentThread().setContextClassLoader(classloader);

            main.invoke(null, new Object[]{args});
        } catch (ExitException ex) {
            logger.fatal(ex.getMessage());
            logger.trace(ex.getStackTrace());
            throw new RuntimeException(ex);
        } catch (UnsupportedEncodingException e){
            logger.fatal(e.getMessage());
            logger.trace(e.getStackTrace());
            throw new RuntimeException(e);
        }
    }

    /**
     * SWAC_HOME must exist, or else the application will exit
     *
     * @param context
     */
    private static void validateSwacHome(GlobalApplicationContext context) {
        String swacHomeDir = context.getSwacHomeDir();
        if (swacHomeDir == null) {
            throw new ExitException("SWAC_HOME directory not specified.");
        }
        File swacHome = new File(swacHomeDir);
        if (!swacHome.exists() || !swacHome.isDirectory()) {
            throw new ExitException("SWAC_HOME directory could not be found: directory does not exist.");
        }
    }

    /**
     * If SWAC_WORK doesn't exist, create it. Also create subdirectories and
     * export data files from the jar to the default data directory.
     *
     * @param context
     */
    private static boolean validateSwacExportDirectories(GlobalApplicationContext context) throws UnsupportedEncodingException{
//        String swacHomeConfigDir = context.getSwacConfigDir();
        String swacWorkDir = context.getSwacWorkDir();
        boolean dataExtraction = Boolean.parseBoolean(System.getProperty("swac.data.extraction"));

//        if (swacHomeConfigDir == null) {
//            throw new ExitException("SWAC_HOME directory not specified.");
//        }
        if (swacWorkDir == null) {
            throw new ExitException("SWAC_WORK directory not specified.");
        }
//        File swacHomeConfig = new File(swacHomeConfigDir);
        File swacWork = new File(swacWorkDir);
//        if (!swacHomeConfig.exists()) {
//            logger.info("Creating SWAC_HOME/config directory (" + swacHomeConfigDir + ").");
//            swacHomeConfig.mkdirs();
//            logger.info("Starting file export...");
//            DataExport.exportData(swacHomeConfig);
//        }

        boolean retVal = true;

        if (!swacWork.exists()) {
            logger.warn("SWAC_WORK directory does not exist:" + swacWorkDir);
            logger.info("Creating SWAC_WORK directory");
            swacWork.mkdirs();
            logger.info("SWAC_WORK directory created");
            retVal = false;
        }

        if (!containsSwacWorkFolders(swacWork.listFiles(), dataExtraction)) {
            if (retVal && !dataExtraction) {
                logger.warn("SWAC_WORK directory missing necessary folder(s); repairing...");
            }
            logger.info("Creating SWAC_WORK subdirectories");
            DataExport.createDirectoryStructure(swacWork);
            logger.info("SWAC_WORK subdirectories created");
            retVal = false;
        }

        String swacVersion = context.getSwacVersion();

        if (!validateVersionInfo(swacWork, swacVersion)) {
            if (retVal && !dataExtraction) {
                // Suppress this message if we're creating the whole SWAC_WORK directory
                logger.warn("SWAC_WORK contents are missing or out of date; updating... (overwrites old data)");
            }
            logger.info("Starting file export...");
            DataExport.exportData(swacWork, true); // TODO: verify business rules call for overwrite by default
            logger.info("File export finished");
            retVal = false;
        }

        if (!retVal) {
            versionStamp(swacWork, swacVersion);
        }

        return retVal;
    }
    private static final String VERSION_INFO_FILE = ".swac-version";

    private static boolean validateVersionInfo(File swacWork, String version) {
        File versionInfoFile = new File(swacWork, VERSION_INFO_FILE);
        if (!versionInfoFile.exists()) {
            return false;
        }

        try {
            String versionInfo = FileUtils.readFileToString(versionInfoFile).trim();
            return (version.equals(versionInfo));
        } catch (IOException e) {
            logger.error("Error reading version information.");
            return false;
        }
    }

    private static void versionStamp(File swacWork, String version) {
        File versionInfoFile = new File(swacWork, VERSION_INFO_FILE);
        try {
            FileUtils.writeStringToFile(versionInfoFile, version);
        } catch (IOException e) {
            logger.error("Error updating version information.");
        }
    }

    private static boolean containsSwacWorkFolders(File fileList[], boolean dataExtraction) {
        String swacWorkFolders[] = {"data", "scenarios", "temp"}; // log4j will always create the log directory.
        Set<String> fileSet = new TreeSet<String>();
        int matches = 0;

        for (File f : fileList) {
            if (f.exists() && f.isDirectory()) {
                fileSet.add(f.getName());
            }
        }
        
        for (int i = 0; i < swacWorkFolders.length; ++i) {
            String folderName = swacWorkFolders[i];
            if (fileSet.contains(folderName)) {
                ++matches;
            } else if (!dataExtraction && !folderName.contentEquals("temp")) {
                    logger.error("SWAC_WORK directory missing '" + folderName + "' folder.");
            }
        }

        return (matches == swacWorkFolders.length);

    }
}
