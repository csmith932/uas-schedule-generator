/**
 * Copyright "TBD", Metron Aviation & CSSI. All rights reserved.
 *
 * This computer Software was developed with the sponsorship of the U.S.
 * Government under Contract No. DTFAWA-10-D-00033, which has a copyright
 * license in accordance with AMS 3.5-13.(c)(1).
 */
package gov.faa.ang.swac.controller;

import gov.faa.ang.swac.controller.core.Batch;
import java.io.File;
import java.io.IOException;
import java.util.TimeZone;

/**
 * Primary command line interface accepts a command name followed by a variable
 * parameter list.
 *
 * @author csmith
 *
 */
public final class Main {

    private static org.apache.log4j.Logger logger = org.apache.log4j.LogManager.getLogger(Main.class);

    private Main() { /* Static only: do not instantiate */ }

    /**
     * Main entry point
     *
     * @param args command line argument list, consisting of a command name
     * followed by a parameter list
     */
    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("GMT"));

        try {
            for (String scenarioName : args) {
                ScenarioApplicationContext ctx = new ScenarioApplicationContext(scenarioName);

                // Locate scenario configuration directory
                File scenarioDir = new File(ctx.getSwacScenarioDir());
                // validate config file existence
                File configFile = new File(scenarioDir, scenarioName + ".xml");
                if (scenarioName == null || scenarioName.isEmpty() || !configFile.exists() || !configFile.isFile()) {
                    logger.error("Configuration file \"" + scenarioName + ".xml\" does not exist. Skipping...");
                    continue;
                }
                try {
                    run(scenarioName, ctx);
                } catch (ExitException ex) {
                    logger = org.apache.log4j.LogManager.getLogger(Main.class);
                    ex.printStackTrace();
                    
                    logger.fatal("Unrecoverable error running scenario \"" + scenarioName + "\". Skipping...");
                    logger.debug(ex.getLocalizedMessage());
                    for (StackTraceElement ste : ex.getStackTrace()) {
                        logger.trace(ste);
                    }
                    continue;
                }
            }
        } catch (Exception ex) {
            logger.fatal(ex.getMessage(), ex);
            logger.trace(ex.getStackTrace());
        }
    }

    /**
     * Loads a batch configuration and runs through all instances in the batch
     *
     * @param batchName Root name for batch configuration files in
     * SWAC_WORK/scenario
     */
    public static void run(String scenarioName, ScenarioApplicationContext ctx) {
        try {
            Batch b = BatchManager.create(scenarioName, ctx);
            b.run(ctx);
        } catch (IOException ex) {
            throw new ExitException("Error initializing scenario", ex);
        }
    }

    /**
     * Print usage instructions
     */
    public static void usage() {
        System.out.println("Usage:");
        System.out.println("$ swac.sh <Scenario Configuration File Name> ...");
        System.out.println("(Runs one or more scenarios)");
        System.out.println("(Scenario Names: " + BatchManager.listAll() + ")");
    }
}
