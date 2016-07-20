/**
 * Copyright "TBD", Metron Aviation & CSSI.  All rights reserved.
 * 
 * This computer Software was developed with the sponsorship of the U.S. Government
 * under Contract No. DTFAWA-10-D-00033, which has a copyright license in accordance with AMS 3.5-13.(c)(1).
 */

package gov.faa.ang.swac.controller;

import static org.junit.Assert.fail;

import gov.faa.ang.swac.controller.BatchManager;
import gov.faa.ang.swac.controller.core.Batch;

import java.io.IOException;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class BatchManagerTest 
{
	private static final String BATCH_NAME = "test.xml";
	
	@Before
	public void setUp()
	{
		ProjectFixture.initSystemProperties("VERBOSE", "NORMAL", "FALSE");
	}
	
	@Ignore
	@Test
	public void createTest()
	{
		try 
		{
                    ScenarioApplicationContext sac = new ScenarioApplicationContext(BATCH_NAME);
			Batch b = BatchManager.create(BATCH_NAME,sac);
			
			b.run(sac);

		} 
		catch (IOException e) 
		{
			fail();
			e.printStackTrace();
		}
	}
}
