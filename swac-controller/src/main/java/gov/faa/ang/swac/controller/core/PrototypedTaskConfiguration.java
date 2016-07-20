///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//package gov.faa.ang.swac.controller.core;
//
//import gov.faa.ang.swac.controller.ExitException;
//import gov.faa.ang.swac.controller.core.Batch.MonteCarloMode;
//import gov.faa.ang.swac.controller.core.montecarlo.replay.ConfigurationCache;
//import gov.faa.ang.swac.datalayer.DataAccessException;
//import gov.faa.ang.swac.datalayer.MappedDataAccess;
//import java.lang.reflect.InvocationTargetException;
//import java.lang.reflect.Method;
//import java.util.Map;
//import java.util.Map.Entry;
//import java.util.Random;
//import org.apache.log4j.LogManager;
//import org.apache.log4j.Logger;
//
///**
// *
// * @author ssmitz
// */
//public class PrototypedTaskConfiguration extends TaskConfiguration {
//
//    private static Logger logger = LogManager.getLogger(PrototypedTaskConfiguration.class);
//    private CloneableAbstractTask prototype;
//
//    public void setPrototype(CloneableAbstractTask val) {
//        this.prototype = val;
//    }
//
//    public CloneableAbstractTask getPrototype() {
//        return this.prototype;
//    }
//
//    @Override
//    public Class<? extends CloneableAbstractTask> getTarget() {
//        return this.prototype.getClass();
//    }
//
//    /**
//     * Instantiates the target task class and attempts to link data inputs and
//     * outputs to the rest of the execution context. Also sets configuration
//     * properties as defined in the TaskConfiguration.
//     *
//     * @param task
//     * @return
//     * @throws DataAccessException
//     */
//    @Override
//    public AbstractTask createTaskExecution(ScenarioExecution scenario, MappedDataAccess dao, int instanceId, Random random) throws DataAccessException {
//        try {
//            AbstractTask execution = this.prototype.clone();
//            execution.setInstanceId(instanceId);
//            execution.setEnabled(this.isEnabled());
//
//            return execution;
//        } catch (CloneNotSupportedException ex) {
//            return super.createTaskExecution(scenario, dao, instanceId, random);
//        }
//    }
//
//    @Override
//    public void initializeTaskExecution(AbstractTask execution, ScenarioExecution scenario, MappedDataAccess dao, int instanceId, Random random, ConfigurationCache cache,Map<String, Map<String, Double>> outerLoopValues, MonteCarloMode mode) throws DataAccessException {
//        super.initializeTaskExecution(execution, scenario, dao, instanceId, random, cache, outerLoopValues, mode);
//    }
//
//    @Override
//    public void initializeConfiguration(AbstractTask execution) throws DataAccessException {
//        for (Entry<String, Object> entry : super.getAllConfigurationParameters().entrySet()) {
//            Method setter = this.getSetter(entry.getKey(), entry.getValue().getClass(), execution);
//            if (setter == null) {
//                throw new ExitException("Invalid linkage between TaskConfiguration properties specification and target task");
//            }
//            logger.debug("Setting configuration property: task=" + execution.toString() + "; property=" + setter.getName() + "; marshaller=" + entry.getValue());
//
//            Object value = entry.getValue();
//
//            try {
//                Method clone = value.getClass().getMethod("clone", value.getClass());
//
//                invokeSetter(setter, execution, clone.invoke(value, null));
//            } catch (NoSuchMethodException snme) {
//                invokeSetter(setter, execution, value);
//            } catch (IllegalAccessException iae) {
//                throw new ExitException(iae);
//            } catch (InvocationTargetException ite) {
//                throw new ExitException(ite);
//            }
//        }
//    }
//    
//    @Override
//    public PrototypedTaskConfiguration clone() throws CloneNotSupportedException {
//        throw new CloneNotSupportedException("Not supported yet.");
//    }
//}