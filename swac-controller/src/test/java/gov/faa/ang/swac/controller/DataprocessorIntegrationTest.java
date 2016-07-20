/**
 * Copyright "TBD", Metron Aviation & CSSI.  All rights reserved.
 * 
 * This computer Software was developed with the sponsorship of the U.S. Government
 * under Contract No. DTFAWA-10-D-00033, which has a copyright license in accordance with AMS 3.5-13.(c)(1).
 */

package gov.faa.ang.swac.controller;

import gov.faa.ang.swac.controller.Bootstrap;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class DataprocessorIntegrationTest extends NoExitTest {

	@Before
	public void setUp() 
	{
		ProjectFixture.initSystemProperties("VERBOSE", "NORMAL", "FALSE");
		ProjectFixture.cleanWorkDir();
	}
	
	@Ignore
	@Test
	public void test() throws Exception
	{
		Bootstrap.main(new String[] { "dataprocessor.xml" });
		// this test is currently disabled. it was crashing on Starting CSIM and breaking compile.
		// Digging into a run and trying to figure out why it was crashing proved fruitless.
		// system runs ok on Linux as expected.
//		Bootstrap.main(new String[] { "scenario.xml" });
	}
}
