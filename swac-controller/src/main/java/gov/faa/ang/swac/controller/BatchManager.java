/**
 * Copyright "TBD", Metron Aviation & CSSI. All rights reserved.
 *
 * This computer Software was developed with the sponsorship of the U.S.
 * Government under Contract No. DTFAWA-10-D-00033, which has a copyright
 * license in accordance with AMS 3.5-13.(c)(1).
 */
package gov.faa.ang.swac.controller;

import gov.faa.ang.swac.common.datatypes.Timestamp;
import gov.faa.ang.swac.controller.core.Batch;
import gov.faa.ang.swac.datalayer.AdHocDataAccess.LogLevel;
import gov.faa.ang.swac.datalayer.MappedDataAccess;
import gov.faa.ang.swac.datalayer.ResourceManager;
import gov.faa.ang.swac.datalayer.identity.FileDataDescriptor;
import gov.faa.ang.swac.datalayer.identity.FileSetDescriptor;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.support.FileSystemXmlApplicationContext;

/**
 * Supports loading batch configurations to run NASPAC. Each configuration has
 * three file schemas with their own naming conventions: <batchName>.xml is the
 * required Spring configuration file, which must contain exactly one bean
 * definition of type Batch and any number of sub-beans. <batchName>.properties
 * contains all property values with placeholders in <batchName>.xml. This file
 * is optional. <batchName>.<scenarioName>.properties are zero or more
 * alternative property placeholder configurations to be iterated over.
 * Properties to be batched over must be omitted from <batchName>.properties and
 * defined separately in each of these files. The BatchManager returned by a
 * call to the create(batchName) factory method is an iterator over a sequence
 * of instances of the Batch bean defined in <batchName>.xml, each one using the
 * same property placeholders from <batchName>.properties and a separate set of
 * property placeholders from the specific instance of
 * <batchName>.<scenarioName>.properties.
 *
 * @author csmith
 *
 */
public class BatchManager {

    private static final String SCENARIO_IMPORTS_FILE = "scenarioImports.csv";
    private static Logger logger = LogManager.getLogger(BatchManager.class);
    // Constants
    public static final String SCENARIO_ID_PROPERTY = "SCENARIO_ID";

    /**
     * Utility method for use by the UI in describing usage options to the user
     *
     * @return A concatenated list of all batch names that exist in the scenario
     * folder, representing the potential valid arguments for create(batchName)
     */
    public static String listAll() {
        File scenariosDir = new File(GlobalApplicationContext.getInstance().getSwacScenariosDir());

        // Load batch configuration files
        SuffixFilenameFilter filter = new SuffixFilenameFilter("scenario");
        File[] batchFiles = scenariosDir.listFiles(filter);
        if (batchFiles == null || batchFiles.length == 0) {
            // Trivial case
            return "";
        } else {
            Arrays.sort(batchFiles);
            StringBuilder str = new StringBuilder();
            boolean first = true;
            for (File f : batchFiles) {
                if (first) {
                    first = false;
                } else {
                    str.append(", ");
                }
                String name = f.getName();
                str.append(filter.getScenarioName(name));
            }
            return str.toString();
        }
    }

    /**
     * Main factory method for constructing a BatchManager and populating it
     * with the Spring context and Properties collections necessary to fetch the
     * Batch bean over a series of property configurations
     *
     * @param batchName Identifies the naming root for all configuration files
     * in the scenario directory that must be loaded to generate the batch job
     * @return A BatchManager that can be used to iterate over a sequence of
     * configurations for a specific Batch bean
     * @throws IOException BatchManager interacts directly with the file system
     * during object construction, running the risk of I/O errors
     */
    public static Batch create(String scenarioName, ScenarioApplicationContext ctx) throws IOException {
        // Locate scenario configuration directory
        File scenarioDir = new File(ctx.getSwacScenarioDir());
        File batchFile = new File(scenarioDir, scenarioName + ".xml");
        File propsFile = new File(scenarioDir, scenarioName + ".properties");
        Properties props = new Properties();

        // Load properties if they exist
        if (propsFile.getName().endsWith(".properties") && propsFile.exists()) {
            FileInputStream propsFileStream = new FileInputStream(propsFile);
            props.load(propsFileStream);
            propsFileStream.close();
            String rel = props.getProperty("SWAC_RELEASE");
            String ver = ctx.getSwacVersion();
            if (ctx.getSwacVersion().contains("-")) {
            	ver = ctx.getSwacVersion().substring(0, ctx.getSwacVersion().indexOf("-"));
            }

            if (!ver.contentEquals(rel)) {
				// The following kludge is utilized when a bundle is finalized and then a change needs to occur.
            	boolean exception = false; //(rel.equals("2.0") && ver.equals("2.0a"));
            	if (!exception) { 
	                logger.warn("SWAC version in properties file does not match! "
	                        + "SWAC version: " + ver + " Properties "
	                        + "file version: " + rel);
            	} 
            }
        }

        // Load configuration into context. FileSystemXmlApplicationContext is used for the ability to swap out PropertyPlaceholderConfigurer settings
        String configPath = "file:" + batchFile.getAbsolutePath();
        String defaultImportsPath = "file:" + new File(ctx.getSwacDefaultImportsFile()).toString();
        String defaultExportsPath = "file:" + new File(ctx.getSwacDefaultExportsFile()).toString();

        FileSystemXmlApplicationContext context = new FileSystemXmlApplicationContext(new String[]{configPath, defaultImportsPath, defaultExportsPath}, false);

        // Attach PropertyPlaceholderConfigurer
        PropertyPlaceholderConfigurer mainProps = new PropertyPlaceholderConfigurer();
        mainProps.setIgnoreUnresolvablePlaceholders(true);
        mainProps.setProperties(props);
        context.addBeanFactoryPostProcessor(mainProps);

        // Refresh the ApplicationContext with the new set of Properties and retrieve the Batch bean
        try {
            context.refresh();
        } catch(BeanCreationException bce) {
            for (StackTraceElement ste : bce.getStackTrace()) {
                logger.trace(ste.toString());
            }
            throw new ExitException(bce.getLocalizedMessage(),bce);
        }
        Batch retVal = context.getBean(Batch.class);

        // Set properties derived from configuration file names
        retVal.setBatchName(scenarioName);
        retVal.setLogLevel(LogLevel.valueOf(System.getProperty("swac.log.level")));
        // Create master DAO
        MappedDataAccess mda = ctx.createDao(scenarioName, "");

        loadScenarioFiles(mda);

        retVal.setDao(mda);

        return retVal;
    }

    private static void loadScenarioFiles(MappedDataAccess mda) {
        String importsPath = File.separator + "scenarios" + File.separator + mda.getBatchName() + File.separator + SCENARIO_IMPORTS_FILE;
        File scenarioImports = mda.getFile(ResourceManager.LOCATION.ROOT, importsPath);

        try {
            if (scenarioImports.canRead()) {
                BufferedReader br = new BufferedReader(new FileReader(scenarioImports));
                String dataType;
                String baseDate;
                String fiscalYear;
                String classifier;

                String line = br.readLine();

                while (line != null) {
                    String next = br.readLine();
                    String[] fields = line.split(",");

                    if (fields.length != 5 || line.startsWith("#")) {
                        line = next;
                        continue;
                    }

                    baseDate = fields[0].trim();
                    fiscalYear = fields[1].trim();
                    classifier = fields[2].trim();
                    dataType = fields[3].trim();

                    String nextClass = null;

                    if (next != null) {
                        nextClass = next.split(",")[3].trim();
                    }

                    if (next != null && nextClass.length() == 0) {
                        FileSetDescriptor fsdd = new FileSetDescriptor();

                        fsdd.setLocation(ResourceManager.LOCATION.DATA);

                        if (dataType != null && !dataType.isEmpty()) {
                            fsdd.setDataType(Class.forName(dataType));
                        }
                        if (baseDate != null && !baseDate.isEmpty()) {
                            fsdd.setBaseDate(Timestamp.myValueOf(baseDate));
                        }
                        if (fiscalYear != null && !fiscalYear.isEmpty()) {
                            fsdd.setForecastFiscalYear(Integer.parseInt(fiscalYear));
                        }

                        fsdd.setClassifier(classifier);
                        fsdd.setFaultTolerant(false);
                        fsdd.setReadOnly(true);
                        fsdd.setResourceName(fields[4].trim());
                        fsdd.getResourceNames().add(fields[4].trim());

                        while (next != null && nextClass.length() == 0) {
                            line = next;
                            next = br.readLine();

                            if (next != null && !next.isEmpty()) {
                                nextClass = next.split(",")[3].trim();
                            } else {
                                next = null;
                            }
                            fields = line.split(",");

                            if (fields.length != 5) {
                                continue;
                            }
                            fsdd.getResourceNames().add(fields[4].trim());
                        }
                        mda.loadScenarioFileDataDescriptor(fsdd);
                    } else {
                        FileDataDescriptor fdd = new FileDataDescriptor();

                        fdd.setLocation(ResourceManager.LOCATION.DATA);

                        if (dataType != null && !dataType.isEmpty()) {
                            fdd.setDataType(Class.forName(dataType));
                        }
                        if (baseDate != null && !baseDate.isEmpty()) {
                            fdd.setBaseDate(Timestamp.myValueOf(baseDate));
                        }
                        if (fiscalYear != null && !fiscalYear.isEmpty()) {
                            fdd.setForecastFiscalYear(Integer.parseInt(fiscalYear));
                        }

                        fdd.setClassifier(classifier);
                        fdd.setFaultTolerant(false);
                        fdd.setReadOnly(true);
                        fdd.setResourceName(fields[4].trim());

                        mda.loadScenarioFileDataDescriptor(fdd);
                    }
                    line = next;
                }
            }
        } catch (FileNotFoundException fnfe) {
            logger.error(fnfe.getLocalizedMessage());
            throw new ExitException(fnfe);
        } catch (IOException ioe) {
            logger.error(ioe.getLocalizedMessage());
            throw new ExitException(ioe);
        } catch (ClassNotFoundException cnfe) {
            logger.error(cnfe.getLocalizedMessage());
            throw new ExitException(cnfe);
        }
    }

    /**
     * FilenameFilter for identifying fileName.suffix files and extracting
     * fileName
     */
    private static class SuffixFilenameFilter implements FilenameFilter {

        private Pattern pattern;

        public SuffixFilenameFilter(String suffix) {
            String patternStr = "^" + suffix + "$";
            pattern = Pattern.compile(patternStr);
        }

        @Override
        public boolean accept(File dir, String name) {
            return pattern.matcher(name).matches();
        }

        public String getScenarioName(String name) {
            Matcher m = pattern.matcher(name);
            if (m.matches()) {
                return m.group(1);
            }
            return null;
        }
    }
}
