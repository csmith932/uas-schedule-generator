/**
 * Copyright "TBD", Metron Aviation & CSSI. All rights reserved.
 *
 * This computer Software was developed with the sponsorship of the U.S.
 * Government under Contract No. DTFAWA-10-D-00033, which has a copyright
 * license in accordance with AMS 3.5-13.(c)(1).
 */
package gov.faa.ang.swac.controller.core;

import gov.faa.ang.swac.common.interfaces.Plugin;
import gov.faa.ang.swac.common.utilities.Stopwatch;
import gov.faa.ang.swac.datalayer.storage.DataMarshaller;
import java.lang.management.ManagementFactory;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public abstract class AbstractTask implements Runnable {

    private static org.apache.log4j.Logger logger = org.apache.log4j.LogManager.getLogger(AbstractTask.class);
    private static final String STOPWATCH_FORMAT = "%s completed in %2$.2f seconds.";

    public enum VALIDATION_LEVEL {
        NONE,
        SHALLOW,
        DEEP
    }

    public String getTaskName() {
        return this.getClass().getSimpleName();
    }
    private boolean enabled = true;

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean val) {
        this.enabled = val;
    }
    private ScenarioExecution parent;

    public ScenarioExecution getParent() {
        return this.parent;
    }

    public final void setParent(ScenarioExecution val) {
        this.parent = val;
    }
    private int instanceId;

    public final int getInstanceId() {
        return this.instanceId;
    }

    public final void setInstanceId(int val) {
        this.instanceId = val;
    }
    private List<Plugin> plugins;

    public void setPlugins(List<Plugin> val) {
        this.plugins = val;
    }

    public List<Plugin> getPlugins() {
        return this.plugins;
    }
    
    // Tracks the number of Monte Carlo instances so that this information can be queried within a task if necessary 
    // (note: useful for validating all MC instances completed successfully)
    private int numInstances = 1;
    
    public int getNumInstances() {
    	return numInstances;
    }
    
    public void setNumInstances(int numInstances) {
    	this.numInstances = numInstances;
    }

//    @Override
//    protected Object clone() throws CloneNotSupportedException {
//        throw new CloneNotSupportedException();
//    }

    public abstract boolean validate(VALIDATION_LEVEL level);

    protected boolean validateFiles(DataMarshaller[] marshallers, VALIDATION_LEVEL level) {
        boolean retval = true;

        for (DataMarshaller marshaller : marshallers) {

            if (level == VALIDATION_LEVEL.SHALLOW) {

                retval &= marshaller.validateExistence();

            } else if (level == VALIDATION_LEVEL.DEEP) {

                retval &= marshaller.validateExistence();

                if (retval) {
                    retval &= marshaller.validateSchema();
                }

                if (retval) {
                    retval &= marshaller.validateData();
                }

            }

        }

        return retval;
    }
    public Exception abort;

    public final void abort(Exception arg) {
        abort = arg;
    }

    public void runWithCleanup() {
        //outputting memory usage when log4j-monte-carlo configuration loaded.
        logger.warn("START " + this.getTaskName() + " for scenario " + this.getParent().getScenarioName());
        //logger.debug("Memory usage at the beginning of " + this.getTaskName() + ": " + getMemoryUsage() + "MB");

        Stopwatch stopwatch = new Stopwatch().start();

        run();
        cleanup();

        logger.debug("Memory usage at the end of " + this.getTaskName() + ": " + getMemoryUsage() + " MB");
        logger.warn(String.format(STOPWATCH_FORMAT,
                this.getTaskName(),
                stopwatch.getElapsedSeconds()));
        logger.warn("END " + this.getTaskName());
    }

    /**
     *
     * @return memory used by JVM in Megabytes
     */
    private long getMemoryUsage() {
        return (ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed() / 1048576);
    }

    protected void cleanup() {
        Method[] methods = this.getClass().getMethods();
        for (Method method : methods) {
            Class<?>[] paramType = method.getParameterTypes();
            if (method.getName().startsWith("set") && paramType.length == 1) {
                if (DataMarshaller.class.isAssignableFrom(paramType[0])) {
                    // Single parameter is a DataMarshaller: set with null
                    try {
                        method.invoke(this, new Object[]{null});
                    } catch (IllegalArgumentException e) {
                        throw new IllegalStateException(e.getMessage(), e);
                    } catch (IllegalAccessException e) {
                        throw new IllegalStateException(e.getMessage(), e);
                    } catch (InvocationTargetException e) {
                        throw new IllegalStateException(e.getMessage(), e);
                    }
                }
            }
        }
    }

    @Override
    public String toString() {
        return getTaskName() + "#" + instanceId + "[enabled=" + enabled + "]";
    }
}
