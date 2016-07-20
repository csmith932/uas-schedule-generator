///**
// * Copyright "TBD", Metron Aviation & CSSI. All rights reserved. This computer Software was
// * developed with the sponsorship of the U.S. Government under Contract No. DTFAWA-10-D-00033, which
// * has a copyright license in accordance with AMS 3.5-13.(c)(1).
// */
//
//package gov.faa.ang.swac.controller.core.montecarlo;
//
//import gov.faa.ang.swac.controller.core.Batch.MonteCarloMode;
//import gov.faa.ang.swac.common.random.distributions.Distribution;
//import gov.faa.ang.swac.controller.core.montecarlo.replay.ConfigurationCache;
//
//import java.util.Map.Entry;
//import java.util.Map;
//import java.util.Random;
//import java.util.SortedMap;
//import java.util.TreeMap;
//
//import org.apache.log4j.LogManager;
//import org.apache.log4j.Logger;
//
//import gov.faa.ang.swac.controller.core.AbstractTask;
//import gov.faa.ang.swac.controller.core.ScenarioExecution;
//import gov.faa.ang.swac.controller.core.PrototypedTaskConfiguration;
//import gov.faa.ang.swac.datalayer.DataAccessException;
//import gov.faa.ang.swac.datalayer.MappedDataAccess;
//
//public class MonteCarloMapTaskConfiguration extends PrototypedTaskConfiguration 
//{
//	private static final Logger logger = LogManager.getLogger(MonteCarloMapTaskConfiguration.class);
//	
//	private SortedMap<String,Distribution> randomizedConfiguration = new TreeMap<String, Distribution>(); 
//	
//	public SortedMap<String,Distribution> getRandomizedConfiguration() {
//		return randomizedConfiguration;
//	}
//	
//	public void setRandomizedConfiguration(SortedMap<String,Distribution> configuration) {
//		this.randomizedConfiguration = configuration;
//	}
//	
//	// TODO: property validation for randomized config
//	
//	@Override
//	public void initializeTaskExecution(AbstractTask execution, ScenarioExecution scenario, 
//			MappedDataAccess dao, 
//			int instanceId,
//			Random random,
//			ConfigurationCache cache,
//			Map<String, Map<String, Double>> outerLoopValues,
//			MonteCarloMode mode) throws DataAccessException
//	{
//		logger.debug("Initializing Monte Carlo map task execution: " + execution.toString());
//		super.initializeTaskExecution(execution, scenario, dao, instanceId, random, cache, outerLoopValues, mode);
//		
//		SortedMap<String,Object> config = cache.getScenario(instanceId).getTask(this.target.getName()).configuration;
//		
//		if (mode.equals(MonteCarloMode.REPLAY))
//		{
//			super.initializeConfiguration(execution, config);
//		}
//		else if (mode.equals(MonteCarloMode.ON))
//		{
//			setRandomizedConfiguration(execution, random, config);	
//		}
//		else
//		{
//			throw new IllegalStateException("Error: Monte Carlo map task should not be processed when Monte Carlo mode is off");
//		}
//	}
//
//	private void setRandomizedConfiguration(AbstractTask execution, Random random, SortedMap<String,Object> sampledConfiguration) throws DataAccessException 
//	{
//		SortedMap<String,Object> currentRandomizedValues = new TreeMap<String,Object>();
//		for (Entry<String,Distribution> property : this.randomizedConfiguration.entrySet())
//		{
//			Double sample = property.getValue().nextDouble(random);
//			logger.debug("Setting randomized parameter: task=" + execution.toString() + "; property=" + property.getKey() + "; value=" + sample);
//			sampledConfiguration.put(property.getKey(), sample);
//			currentRandomizedValues.put(property.getKey(), sample);
//		}
//		
//		super.initializeConfiguration(execution, currentRandomizedValues);
//	}
//}
