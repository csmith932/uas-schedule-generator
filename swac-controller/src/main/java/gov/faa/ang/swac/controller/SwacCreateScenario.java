/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.faa.ang.swac.controller;

import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 *
 * @author ssmitz
 */
public class SwacCreateScenario {
    private static final Logger logger = LogManager.getLogger(SwacCreateScenario.class);
    
    public static void main(String args[]) {
        String swacWork = System.getProperty("swac.work.dir");
        String swacHome = System.getenv("SWAC_HOME");
        
        String scenarioName = args[0];
        
        logger.info("Preparing to create scenario: " + scenarioName);
        
        int returnValue = SwacCopyScenario.copyScenario(new String[] { "scenario", scenarioName });
        if (returnValue != 0) { 
        	System.exit(returnValue);
            return;
        }
        
        logger.info("Customizing scenario files...");
        
        File newScenarioDir = new File(swacWork + File.separator + "scenarios" + File.separator + scenarioName);
        File newScenarioProperties = new File(newScenarioDir, scenarioName + ".properties");
        
        String baseDates = args[1];
        String forecastFiscalYears = args[2];
        String classifiers = args[3];
        
        try {
            String file = FileUtils.readFileToString(newScenarioProperties, "UTF-8");
            file = file.replace("BASE_DATE=BASE_DATE", "BASE_DATE=" + baseDates);
            file = file.replace("FORECAST_FISCAL_YEAR=FORECAST_FISCAL_YEAR", "FORECAST_FISCAL_YEAR=" + forecastFiscalYears);
            file = file.replace("CLASSIFIER=CLASSIFIER", "CLASSIFIER=" + classifiers);
            FileUtils.writeStringToFile(newScenarioProperties, file, "UTF-8");
            ScenarioImportsFileGenerator.writeScenarioImportsFile(swacHome, swacWork, newScenarioDir,
            		baseDates.split(","), forecastFiscalYears.split(","), classifiers.split(","));
            logger.info("Finished creating scenario " + scenarioName);
        } catch (IOException ioe) {
            System.err.println("Error writing scenarioImports.csv");
            System.err.print(ioe.getLocalizedMessage());
            System.exit(3);
        }
    }
}
