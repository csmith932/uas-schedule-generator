/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.faa.ang.swac.controller;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 *
 * @author ssmitz
 */
public class SwacCopyScenario {
    private static final Logger logger = LogManager.getLogger(SwacCreateScenario.class);
    
    public static void main(String[] args) {
        int rval = copyScenario(args);
        System.exit(rval);
    }
    
    public static int copyScenario(String[] args) {
        String swacWork = System.getProperty("swac.work.dir");
        String baseScenario = args[0];
        File baseScenarioDir = new File(swacWork + File.separator + "scenarios" + File.separator + baseScenario);
        
        if (!baseScenario.contentEquals("scenario")) {
            logger.info("Preparing to copy scenario " + baseScenario + " to scenario " + args[1]);
        }
        
        if (!baseScenarioDir.canRead()) {
            logger.error("Unable to read: " + baseScenarioDir.toString());
            return 1;
        }
        
        File baseScenarioXml = new File(baseScenarioDir, baseScenario + ".xml");
        
        if (!baseScenarioXml.canRead()) {
            logger.error("Unable to read: " + baseScenarioXml.toString());
            return 1;
        }
        
        File baseScenarioProperties = new File(baseScenarioDir, baseScenario + ".properties");
        
        if (!baseScenarioProperties.canRead()) {
            logger.error("Unable to read: " + baseScenarioProperties.toString());
            return 1;
        }
        
        File baseScenarioItineraryView = new File(baseScenarioDir, "itineraryView.sql");
        File baseScenarioImports = new File(baseScenarioDir, "scenarioImports.csv");
        
        Collection<File> baseScenarioLogging = (Collection<File>) FileUtils.listFiles(baseScenarioDir, new WildcardFileFilter("log4j-*.xml"), null);
        
        String newScenario = args[1];
        File newScenarioDir = new File(swacWork + File.separator + "scenarios" + File.separator + newScenario);
        
        if (newScenarioDir.exists()) {
            logger.error("Scenario name already in use: " + newScenario);
            return 2;
        }
        
        newScenarioDir.mkdirs();
        
        // Create scenario sub-directories
        for (String dir : new String[] {"log", "outputs", "reports", "temp", "db", "cache"}) {
            new File(newScenarioDir, dir).mkdir();
        }
        
        File newScenarioXml = new File(newScenarioDir, newScenario + ".xml");
        File newScenarioProperties = new File(newScenarioDir, newScenario + ".properties");
        
        try {
            FileUtils.copyFile(baseScenarioXml, newScenarioXml);
            FileUtils.copyFile(baseScenarioProperties, newScenarioProperties);
            FileUtils.copyFile(baseScenarioItineraryView, new File(newScenarioDir, "itineraryView.sql"));
            
            for (File f : baseScenarioLogging) {
                FileUtils.copyFileToDirectory(f, newScenarioDir);
            }
            
            Collection<File> newScenarioLogging = (Collection<File>) FileUtils.listFiles(newScenarioDir, new WildcardFileFilter("log4j-*.xml"), null);
            for (File f : newScenarioLogging) {
                String file = FileUtils.readFileToString(f, "UTF-8");
                file = file.replace("${swac.work.dir}/scenarios/" + baseScenario, "${swac.work.dir}/scenarios/" + newScenario);
                FileUtils.writeStringToFile(f, file, "UTF-8");
            }
            
            if (baseScenarioImports.exists()) {
                FileUtils.copyFileToDirectory(baseScenarioImports, newScenarioDir);
            }
            
        } catch (IOException ioe) {
            logger.error("Error creating scenario files!");
            logger.debug(ioe.getLocalizedMessage());
            return 3;
        }
        return 0;
    }
}
