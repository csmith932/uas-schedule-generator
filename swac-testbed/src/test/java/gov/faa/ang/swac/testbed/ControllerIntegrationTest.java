/**
 * Copyright 2012, Metron Aviation & CSSI.  All rights reserved.
 * 
 * This computer Software was developed with the sponsorship of the U.S. Government
 * under Contract No. DTFAWA-10-D-00033, which has a copyright license in accordance with AMS 3.5-13.(c)(1).
 */

package gov.faa.ang.swac.testbed;

import gov.faa.ang.swac.controller.Bootstrap;
import gov.faa.ang.swac.controller.ProjectFixture;

public class ControllerIntegrationTest {
	
	public static void main(String[] args) {
		ProjectFixture.initSystemProperties("DEBUG", "NORMAL", "FALSE");
		ProjectFixture.cleanWorkDir();
		ProjectFixture.initWorkDir();
		
		try {
			ProjectFixture.createScenario("uasOutput", "20130724", "2013", "base");
			Bootstrap.main(new String[] { "uasOutput" });
			
		} catch (Exception e) { // Doesn't really matter what kind; we can't do much about it anyway.
			e.printStackTrace();
		}
	}
}
