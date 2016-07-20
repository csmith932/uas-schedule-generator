/**
 * Copyright "TBD", Metron Aviation & CSSI.  All rights reserved.
 * 
 * This computer Software was developed with the sponsorship of the U.S. Government
 * under Contract No. DTFAWA-10-D-00033, which has a copyright license in accordance with AMS 3.5-13.(c)(1).
 */

package gov.faa.ang.swac.controller;

import gov.faa.ang.swac.controller.Main;

import java.io.File;


import org.junit.Before;



import org.junit.Test;
import org.junit.Ignore;

public class MainTest extends NoExitTest {

	public static final String INPUT_SCHEDULE = ProjectFixture.DEFAULT_SWAC_HOME_DIR + File.separator + "sample-schedule-inputs" + File.separator + "forecast_sample_20081108_2009_2009.txt";
	public static final String INPUT_SMOOTHING = ProjectFixture.DEFAULT_SWAC_HOME_DIR + File.separator + "sample-schedule-inputs" + File.separator + "empty.txt";
	public static final String BASE_FISCAL_YEAR = "2009";
	public static final String FORECAST_FISCAL_YEAR = "2009";
	public static final String DATE = "1108";
	public static final String[] ARGS = new String[] { INPUT_SCHEDULE, INPUT_SMOOTHING, BASE_FISCAL_YEAR, FORECAST_FISCAL_YEAR, DATE };
	

	@Before
	public void setUp() 
	{
		ProjectFixture.initSystemProperties("VERBOSE", "NORMAL", "FALSE");

	}
	
	@Ignore
	@Test
	public void test() throws Exception
	{
		Main.main(new String[] { "create-scenario", ProjectFixture.DEFAULT_SWAC_SCENARIO_NAME, INPUT_SCHEDULE, INPUT_SMOOTHING, BASE_FISCAL_YEAR, FORECAST_FISCAL_YEAR, DATE });
		Main.main(new String[] { "run-preprocessor", ProjectFixture.DEFAULT_SWAC_SCENARIO_NAME });
		Main.main(new String[] { "clean-scenario", ProjectFixture.DEFAULT_SWAC_SCENARIO_NAME });



	}

}
