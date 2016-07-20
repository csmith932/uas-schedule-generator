package gov.faa.ang.swac.controller.core;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import gov.faa.ang.swac.common.interfaces.Plugin;
import gov.faa.ang.swac.common.random.distributions.Distribution;
import gov.faa.ang.swac.controller.ExitException;
import gov.faa.ang.swac.controller.core.Batch.MonteCarloMode;
import gov.faa.ang.swac.controller.core.montecarlo.replay.ConfigurationCache;
import gov.faa.ang.swac.datalayer.DataAccessException;
import gov.faa.ang.swac.datalayer.MappedDataAccess;
import gov.faa.ang.swac.datalayer.identity.DataAccessObjectDescriptor;
import gov.faa.ang.swac.datalayer.identity.DataDescriptor;
import gov.faa.ang.swac.datalayer.identity.FileDataDescriptor;
import gov.faa.ang.swac.datalayer.identity.IntermediateDataDescriptor;
import gov.faa.ang.swac.datalayer.identity.ParameterizedDataDescriptor;
import gov.faa.ang.swac.datalayer.storage.DataMarshaller;
import gov.faa.ang.swac.datalayer.storage.db.DataAccessObject;

import java.util.List;

public class TaskConfiguration implements Comparable<TaskConfiguration>
{
	private static final Logger logger = LogManager.getLogger(TaskConfiguration.class);
	
	private boolean enabled;
	private boolean innerLoop;
	public boolean isInnerLoop() {
		return innerLoop;
	}
	public void setInnerLoop(boolean innerLoop) {
		this.innerLoop = innerLoop;
	}

	private Map<String,DataDescriptor> inputData = new TreeMap<String, DataDescriptor>();
	private Map<String,DataAccessObjectDescriptor> dataAccessObjects = new TreeMap<String, DataAccessObjectDescriptor>();
	private Map<String,Class<?>> outputData = new TreeMap<String, Class<?>>();
	private SortedMap<String,Distribution> randomizedConfiguration = new TreeMap<String, Distribution>(); 
	private Map<String,Object> innerLoopVariables = new TreeMap<String, Object>();
	

	public Map<String, Object> getInnerLoopVariables() {
		return innerLoopVariables;
	}
	public void setInnerLoopVariables(Map<String, Object> innerLoopVariables) {
		this.innerLoopVariables = innerLoopVariables;
	}
	public boolean isEnabled() {
		return enabled;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	public Map<String, DataDescriptor> getInputData() {
		return inputData;
	}
	public void setInputData(Map<String, DataDescriptor> inputData) {
		this.inputData = inputData;
	}
	public Map<String, DataAccessObjectDescriptor> getDataAccessObjects() {
		return this.dataAccessObjects;
	}
	public void setDataAccessObjects(Map<String, DataAccessObjectDescriptor> dataAccessObjects) {
		this.dataAccessObjects = dataAccessObjects;
	}
	public Map<String,Class<?>> getOutputData() {
		return outputData;
	}
	public void setOutputData(Map<String,Class<?>> outputData) {
		this.outputData = outputData;
	}
	
	public SortedMap<String,Distribution> getRandomizedConfiguration() {
		return randomizedConfiguration;
	}
	
	public void setRandomizedConfiguration(SortedMap<String,Distribution> configuration) {
		this.randomizedConfiguration = configuration;
	}
	
	private boolean global;
	public boolean isGlobal() { return this.global; }
	public void setGlobal(boolean val) { this.global = val; }
	
	private Integer globalPriority = Integer.MAX_VALUE; // Used to sort global tasks.  Higher priority values execute later.
	public Integer getGlobalPriority(){
		return this.globalPriority;
	}
	public void setGlobalPriority(Integer val){
		this.globalPriority = val;
	}
	
	public void validateConfiguration() {
		//validateInputs();
		//validateOutputs();
//		validateConfig();
	}
/*	
	private void validateInputs() {
		for (String name : this.inputData.keySet())
		{
			Method setter = getSetter(name, DataMarshaller.class, null);
			if (setter == null)
			{
				// Invalid input data property
				throw new ExitException("Invalid task configuration: Property \"" + name + 
						"\" does not have a setter method to link a DataMarshaller to the input");
			}
		}
	}
	
	private void validateOutputs() {
		for (String name : this.outputData.keySet())
		{
			Method setter = getSetter(name, DataMarshaller.class, null);
			if (setter == null)
			{
				// Invalid input data property
				throw new ExitException("Invalid task configuration: Property \"" + name + 
						"\" in task \"" + this.getTarget().getSimpleName() + "\" does not have a setter method to link a DataMarshaller to the output");
			}
		}
	}
*/	
//	private void validateConfig() {
//		for (String name : innerLoopVariables.keySet())
//		{
//			Method setter = getSetter(name, innerLoopVariables.get(name).getClass(), null);
//			if (setter == null)
//			{
//				// Invalid input data property
//				throw new ExitException("Invalid task configuration: Property \"" + name + 
//						"\" does not have a setter method");
//			}
//		}
//	}
	

	public Method getSetter(String name, Class<?> clazz, AbstractTask execution)
	{
		return getSetter(this.getTarget(), name, clazz, execution);
	}
	
	public static Method getSetter(Class<?> targetClazz, String name, Class<?> clazz, AbstractTask execution)
	{
		return getPrefixedMethod(targetClazz, name, clazz, "set", execution);
	}
	
	private static Method getPrefixedMethod(Class<?> target, String name, Class<?> clazz, String prefix, AbstractTask execution)
	{
		if (clazz == null)
		{
			return null;
		}
		
		Method setter = null;
		try {
			setter = target.getMethod(prefix + name.substring(0,1).toUpperCase() + name.substring(1), clazz);
		} catch (SecurityException e) {
                        logger.debug(e.getStackTrace());
			throw e;
		} catch (NoSuchMethodException e) {
			try {
				setter = target.getMethod(prefix + name.substring(0,1).toLowerCase() + name.substring(1), clazz);
			} catch (SecurityException e1) {
                                logger.debug(e1.getStackTrace());
				throw e1;
			} catch (NoSuchMethodException e1) {
                            if (execution != null) {
                                logger.debug("Setter: " + prefix + name.substring(0,1).toUpperCase() + name.substring(1) + " not found. Checking for plugins...");
                                List<Plugin> plugins = execution.getPlugins();
                                if (plugins != null) {
                                    for (Plugin plugin : plugins) {
                                        setter = getPrefixedMethod(plugin.getClass(), name, clazz, prefix, null);
                                        if (setter != null) {
                                            logger.debug("Setter found: " + setter.getName());
                                            return setter;
                                        }
                                    }
                                }
                            }
                            // This may mean that a superclass of the parameter class is declared. Recursively try to match to a more general type
                            return getPrefixedMethod(target, name, clazz.getSuperclass(), prefix, execution);

			}
			
		}
		return setter;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(this.getTarget().getSimpleName());
		builder.append("#");
		builder.append(this.hashCode());
		return builder.toString();
	}
	
	public void initializeTaskExecution(AbstractTask execution,
			ScenarioExecution scenario, 
			MappedDataAccess dao, 
			int instanceId,
			Random random,
			ConfigurationCache cache,
			Map<String, Map<String, Double>> outerLoopValues,
			MonteCarloMode mode) throws DataAccessException
	{
		logger.debug("Initializing task execution");
		
		initializeConfiguration(execution, this.innerLoopVariables);
		
		linkInputs(execution, scenario, dao, instanceId);
		
		// Initialize the DAO's
		initDao(execution, dao, instanceId);
                
		// Do the same with output data
		linkOutputs(execution, dao);
		
		if(!mode.equals(MonteCarloMode.OFF))
		{
			String cacheTaskId = this.getPrototype().getClass().toString();
						
			SortedMap<String,Object> config = cache.getScenario(instanceId).getTask(cacheTaskId).configuration;
			if (mode.equals(MonteCarloMode.REPLAY))
			{
				initializeConfiguration(execution, config);
			}
			else if (mode.equals(MonteCarloMode.ON))
			{			
				setRandomizedConfiguration(execution, random, config, outerLoopValues);	
			}
		}
	}
	
	private void setRandomizedConfiguration(AbstractTask execution, Random random, SortedMap<String,Object> sampledConfiguration, Map<String, Map<String, Double>> outerLoopValues) throws DataAccessException 
	{
		SortedMap<String,Object> currentRandomizedValues = new TreeMap<String,Object>();
		for (Entry<String,Distribution> property : this.randomizedConfiguration.entrySet())
		{
			String propertyName = property.getKey();
			Double sample = null;
			if(this.innerLoopVariables.keySet().contains(propertyName))//If property is a random seed, get new random value every time
			{
				sample = property.getValue().nextDouble(random);
			}
			else// If property is not random seed, get a new value only once per outer loop
			{
				String taskName = this.getPrototype().getClass().toString();
				
				Map<String, Double> thisTaskValues = outerLoopValues.get(taskName);
				if(thisTaskValues == null)
				{
					thisTaskValues = new HashMap<String, Double>();
					outerLoopValues.put(taskName, thisTaskValues);
				}
				sample = thisTaskValues.get(propertyName);
				if(sample == null)
				{
					sample = property.getValue().nextDouble(random);
					thisTaskValues.put(propertyName, sample);
				}
			}
			logger.debug("Setting randomized parameter: task=" + execution.toString() + "; property=" + property.getKey() + "; value=" + sample);
			sampledConfiguration.put(property.getKey(), sample);
			currentRandomizedValues.put(property.getKey(), sample);
		}
		initializeConfiguration(execution, currentRandomizedValues);
	}
	
	public void linkInputs(AbstractTask execution,
			ScenarioExecution scenario, 
			MappedDataAccess dao, 
			int instanceId) throws DataAccessException
	{
		// Then we loop through the input data property map and attempt to create proper linkage
		for (Entry<String,DataDescriptor> entry : this.getInputData().entrySet())
		{
			// Retrieve the JavaBean property setter for this property name. TaskConfiguration should automatically resolve the type to DataMarshaller
			Method setter = this.getSetter(entry.getKey(), DataMarshaller.class, execution);
			if (setter == null)
			{
				throw new ExitException("Invalid linkage between TaskConfiguration input data specification and target task: "
						+ "task=" + execution.getTaskName()
						+ "; property=" + entry.getKey()
						+ "; data=" + entry.getValue().toString());
			}
			
			DataDescriptor descriptor = entry.getValue();
			
			DataMarshaller marshaller = null;
			
			// Handle the DataDescriptor and associated DataMarshaller in a case-specific way 
			if (descriptor instanceof FileDataDescriptor)
			{
				// Load or link to static data in Batch dao
				marshaller = scenario.getStaticMarshaller((FileDataDescriptor)descriptor);
			}
			else if (descriptor instanceof ParameterizedDataDescriptor)
			{
				// Parameterized data should already have metadata registered; retrieve marshaller
				marshaller = scenario.getParameterizedMarshaller((ParameterizedDataDescriptor)descriptor);
			}
			else if (descriptor instanceof IntermediateDataDescriptor)
			{
				// Trace linkage and connect to execution
				IntermediateDataDescriptor intermediate = new IntermediateDataDescriptor((IntermediateDataDescriptor)descriptor); // Copy to avoid tampering with configuration
				intermediate.setInstanceId(instanceId);

				marshaller = dao.getMarshaller(intermediate); // Register the linkage with the data layer
			}
			else
			{
				throw new ExitException("Invalid data descriptor type");
			}
			
			if (marshaller == null)
			{
				throw new ExitException("Error retrieving data marshaller for descriptor " + descriptor.toString());
			}
			
			// Now that we have located and/or created our DataMarshaller, assign it to the task's bean property so the task may load from or save to it 
			logger.debug("Setting input data marshaller: task=" + execution.toString() + "; property=" + setter.getName() + "; marshaller=" + marshaller.toString());
			invokeSetter(setter, execution, marshaller);
		}
	}
	
	/**
	 * Initializes DAO's, which have a more robust interface than DataMarshallers for executing queries against a data source
	 * TODO: DAO's can use scenarioId and such to use scenario specific database views and/or schema 
	 * @param execution
	 * @param dao
	 * @throws DataAccessException
	 */
	private void initDao(AbstractTask execution, MappedDataAccess dao, int instanceId) throws DataAccessException {
		// Loop through the DAO property map and attempt to create proper linkage
		for (Entry<String,DataAccessObjectDescriptor> entry : this.getDataAccessObjects().entrySet())
		{
			DataAccessObjectDescriptor descriptor = entry.getValue().clone();
                        
            descriptor.setInstanceId(Integer.toString(instanceId));
			
			DataAccessObject myDao = dao.getDataAccessObject(descriptor);
                        
			Method setter = this.getSetter(entry.getKey(), DataAccessObject.class, execution);
                        
			if (setter == null)
			{
				throw new ExitException("Invalid linkage between TaskConfiguration DAO specification and target task");
			}
			
			// Now that we have located and/or created our DAO, assign it to the task's bean property so the task may query it 
			logger.debug("Setting data access object: task=" + execution.toString() + "; property=" + setter.getName() + "; DAO=" + myDao.toString());
			invokeSetter(setter, execution, myDao);
		}
	}
	
	public void linkOutputs(AbstractTask execution,
			MappedDataAccess dao) throws DataAccessException
	{
		for (Entry<String,Class<?>> entry : this.getOutputData().entrySet())
		{
			// Retrieve the JavaBean property setter for this property name. TaskConfiguration should automatically resolve the type to DataMarshaller
			Method setter = this.getSetter(entry.getKey(), DataMarshaller.class, execution);
			if (setter == null)
			{
				throw new ExitException("Invalid linkage between TaskConfiguration output data specification and target task: class=" + execution.getClass().getSimpleName() + "; propertyName=" + entry.getKey());
			}
			
			Class<?> clazz = entry.getValue();
			
			// Note: output data refers only to raw data to be consumed downstream in the processing pipeline, so each output data class
			// maps neatly to an IntermediateDataDescriptor. Final postprocessor outputs and reports should be handled individually by
			// the tasks, using FileDataDescriptors as necessary
			IntermediateDataDescriptor descriptor = new IntermediateDataDescriptor();
			descriptor.setDataType(clazz);
			descriptor.setDataSource(this);
			descriptor.setInstanceId(execution.getInstanceId());
			descriptor.setPersistent(false);
			
			DataMarshaller marshaller = dao.getMarshaller(descriptor);
			
			logger.debug("Setting output data marshaller: task=" + execution.toString() + "; property=" + setter.getName() + "; marshaller=" + marshaller.toString());
			invokeSetter(setter, execution, marshaller);
		}
	}
	
	public void initializeConfiguration(AbstractTask execution, Map<String,Object> properties) throws DataAccessException
	{
		for (Entry<String, Object> entry : properties.entrySet()) {
            try {
            	Object value = entry.getValue();
            	Object valueClone = null;
            	try {
            		Method clone = value.getClass().getMethod("clone", value.getClass());
            		valueClone = clone.invoke(value, new Object[] {});
            	} catch (NoSuchMethodException snme) {
            		logger.debug("Property " + value.getClass().getSimpleName() + " could not be cloned during initialization. Using original value. This is a risk for Monte Carlo mode");
                    valueClone = value;
            	}
                logger.debug("Setting configuration property: task=" + execution.toString() + "; property=" + entry.getKey() + "; value=" + entry.getValue());
                invokeSetter(entry.getKey(), entry.getValue().getClass(), execution, valueClone);
            } catch (IllegalAccessException iae) {
                throw new ExitException(iae);
            } catch (InvocationTargetException ite) {
                throw new ExitException(ite);
            }
        }
	}
	
	public static void invokeSetter(Method setter, AbstractTask execution, Object marshaller)
	{
		try {
			setter.invoke(execution, new Object[] { marshaller });
		} catch (Exception ex) {
			// TODO: This is supposed to allow randomized configuration values to propagate into plugins but it probably doesn't work
                    List<Plugin> plugins = execution.getPlugins();
                    
                    if (plugins != null) {
                        for (Plugin plugin : plugins) {
                            try {
                                setter.invoke(plugin, new Object[] { marshaller });
                                assert false : "This should be unreachable code in TaskConfiguration";
                                return;
                            } catch (Exception ex2) {}
                        }
                    }
                    for (StackTraceElement ste : ex.getStackTrace())
                        logger.trace(ste.toString());
                    
                    throw new ExitException("Invalid task configuration", ex);
                }
	}
	
	/**
	 * This overload combines finding and invoking the setter, making it easier to search through the object graph and find a sub-property
	 * XXX: If not found on the top level object, search order through children is undetermined; The only way to verify that the right property is
	 * being set is to make sure the object graph has a unique combination of property name and value type.
	 * 
	 * @param name
	 * @param clazz
	 * @param execution
	 * @param value
	 */
	public static void invokeSetter(String name, Class<?> propertyClazz, AbstractTask execution, Object value) {
		assert name != null && propertyClazz != null && execution!= null && value != null : "Invalid parameters for reflection"; 
		
		List<Object> nodes = new LinkedList<Object>();
		nodes.add(execution);
		List<Class<?>> classes = new LinkedList<Class<?>>();
		classes.add(execution.getClass());
		Set<Class<?>> exemptList = new HashSet<Class<?>>();
		exemptList.add(Object.class);
		exemptList.add(Class.class);
		exemptList.add(TaskConfiguration.class);
		exemptList.add(Batch.class);
		String prefix = "set";
		Method setter = null;
		Class<?> primitiveClazz = getPrimitiveClass(propertyClazz);
		while (!nodes.isEmpty()) {
			Object obj = nodes.remove(0);
			Class<?> targetClazz = classes.remove(0);
			if (!exemptList.contains(targetClazz)) {
				exemptList.add(targetClazz);
				try {
					setter = targetClazz.getMethod(prefix + name.substring(0,1).toUpperCase() + name.substring(1), propertyClazz);
				} catch (NoSuchMethodException e) {}
				if (setter == null) {
					try {
						setter = targetClazz.getMethod(prefix + name.substring(0,1).toLowerCase() + name.substring(1), propertyClazz);
					} catch (NoSuchMethodException e1) {}
				}
				if (setter == null && primitiveClazz != null) {
					try {
						setter = targetClazz.getMethod(prefix + name.substring(0,1).toUpperCase() + name.substring(1), primitiveClazz);
					} catch (NoSuchMethodException e1) {}
				}
				if (setter == null && primitiveClazz != null) {
					try {
						setter = targetClazz.getMethod(prefix + name.substring(0,1).toLowerCase() + name.substring(1), primitiveClazz);
					} catch (NoSuchMethodException e1) {}
				}
				if (setter == null) {
				    logger.debug("Setter: " + prefix + name.substring(0,1).toUpperCase() + name.substring(1) + " not found. Checking superclass and children");
	                 // This may mean that a superclass of the parameter class is declared. Recursively try to match to a more general type
	                Class<?> parentClazz = targetClazz.getSuperclass();
	                while (!parentClazz.equals(Object.class)){
	                	nodes.add(obj);
	                	classes.add(parentClazz);
	                	parentClazz = parentClazz.getSuperclass();
	                }
                    List<Object> children = getProperties(obj, exemptList);
                    for (Object child : children) {
                    	nodes.add(child);
                    	classes.add(child.getClass());
                    }
				} else {
					try {
						setter.invoke(obj, new Object[] { value });
						return;
					} catch (Exception e) {
						throw new ExitException("Invalid task configuration", e);
					}
				}
			}
		}
		// This should be unreachable if a valid value is found
		throw new ExitException("Invalid task configuration: property name=" + name + " does not exist for task=" + execution.getClass().getSimpleName() + " or its children.");
	}
	
	private static Class<?> getPrimitiveClass(Class<?> objectClass) {
		if (Boolean.class.equals(objectClass)) {
			return Boolean.TYPE;
		} else if (Byte.class.equals(objectClass)) {
			return Byte.TYPE;
		} else if (Character.class.equals(objectClass)) {
			return Character.TYPE;
		} else if (Short.class.equals(objectClass)) {
			return Short.TYPE;
		} else if (Integer.class.equals(objectClass)) {
			return Integer.TYPE;
		} else if (Long.class.equals(objectClass)) {
			return Long.TYPE;
		} else if (Float.class.equals(objectClass)) {
			return Float.TYPE;
		} else if (Double.class.equals(objectClass)) {
			return Double.TYPE;
		} else {
			return null;
		}
	}
	
	/**
	 * Reflects all of the 
	 * @param obj
	 * @return
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 */
	private static List<Object> getProperties(Object obj, Set<Class<?>> exemptList) {
		List<Object> retVal = new ArrayList<Object>();
		Class<?> clazz = obj.getClass();
		Field[] fields = clazz.getDeclaredFields();
		for (Field f : fields) {
			Class<?> type = f.getType();
			if (!type.isPrimitive() && 
					!exemptList.contains(type) &&
					type.getName().startsWith("gov.faa.ang.swac") &&
					!type.getName().startsWith("gov.faa.ang.swac.datalayer")){
				try {
					f.setAccessible(true);
					Object child = f.get(obj);
					if (child != null) {
						if (child instanceof Collection) {
							retVal.addAll((Collection)child);
						} else {
							retVal.add(child);
						}
					}
				} catch (Exception ex) {
					logger.debug("Error reflecting child properties. Recoverable.");
				}
			}
		}
		return retVal;
	}
	
	///
	/// Integrated from PrototypedTaskConfiguration
	///
	
	private CloneableAbstractTask prototype;

    public void setPrototype(CloneableAbstractTask val) {
        this.prototype = val;
    }

    public CloneableAbstractTask getPrototype() {
        return this.prototype;
    }

//    @Override
    public Class<? extends CloneableAbstractTask> getTarget() {
        return this.prototype.getClass();
    }

    /**
     * Instantiates the target task class and attempts to link data inputs and
     * outputs to the rest of the execution context. Also sets configuration
     * properties as defined in the TaskConfiguration.
     *
     * @param task
     * @return
     * @throws DataAccessException
     */
//    @Override
    public AbstractTask createTaskExecution(ScenarioExecution scenario, MappedDataAccess dao, int instanceId, Random random) throws DataAccessException {
        try {
            AbstractTask execution = this.prototype.clone();
            execution.setInstanceId(instanceId);
            execution.setEnabled(this.isEnabled());

            return execution;
        } catch (CloneNotSupportedException ex) {
//            return super.createTaskExecution(scenario, dao, instanceId, random);
        	logger.fatal("Task " + this.prototype.getClass().getSimpleName() + " does not support cloning, which makes scenario initialization impossible.");
        	throw new ExitException(ex);
        }
    }

    @Override
    public TaskConfiguration clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException("Not supported yet.");
    }
	@Override
	public int compareTo(TaskConfiguration o) {
		return this.globalPriority.compareTo(o.globalPriority);
	}
}
