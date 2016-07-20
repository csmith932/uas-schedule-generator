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

public class BootstrapTest extends NoExitTest {

	@Before
	public void setUp() 
	{
		ProjectFixture.initSystemProperties("VERBOSE", "NORMAL", "FALSE");
	}
	
	@Ignore
	@Test
	public void test() throws Exception
	{
		Bootstrap.main(new String[] { "test" });

		// this test should verify that NASPAC_WORK has been created successfully, including exported data files. Main should throw an exception for an invalid command name.
	}
}
