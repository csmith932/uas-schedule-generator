package gov.faa.ang.swac.controller.core;

import java.util.concurrent.ExecutorService;

/**
 * Simple factory interface to allow ExecutorServices to be configurable via Spring and made available to Batch for scheduling work.
 * 
 * @author csmith
 *
 */
public interface ExecutorServiceFactory {
	public ExecutorService create();
}
