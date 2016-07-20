package gov.faa.ang.swac.controller.core;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class ThreadPoolExecutorServiceFactory implements ExecutorServiceFactory {
	private static final Logger logger = LogManager.getLogger(ThreadPoolExecutorServiceFactory.class);
	
 	private int numProcessors;

    public int getNumProcessors() {
        return this.numProcessors;
    }

    public void setNumProcessors(int val) {
        this.numProcessors = val;
    }
    
    private long memoryPerInstance;

    public long getMemoryPerInstance() {
        return this.memoryPerInstance;
    }

    public void setMemoryPerInstance(long val) {
        this.memoryPerInstance = val;
    }
	    
	@Override
	public ExecutorService create() {
		int maxThreads = this.numProcessors;
		if (maxThreads <= 0) {
			// auto-detect
			long cpus = Runtime.getRuntime().availableProcessors();
			// Use the total number of CPUs
			// XXX: This might not be a good heuristic. More threads than cores might be preferable if it allows for other Jobs to continue executing
			// while I/O or other operations are waiting, even though more threads than cores causes unnecessary context switching.
			long systemMemory = Runtime.getRuntime().maxMemory();
			systemMemory = systemMemory/memoryPerInstance;
			maxThreads = (int)Math.max(Math.min(cpus, systemMemory), 1);
			logger.info("ExecutorService auto-configuration has selected " + maxThreads + " concurrent threads for Job execution.");
		}
		
		ExecutorService executor = Executors.newFixedThreadPool(maxThreads);
		
		return executor;
	}

}
