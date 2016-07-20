/**
 * Copyright "TBD", Metron Aviation & CSSI.  All rights reserved.
 * 
 * This computer Software was developed with the sponsorship of the U.S. Government
 * under Contract No. DTFAWA-10-D-00033, which has a copyright license in accordance with AMS 3.5-13.(c)(1).
 */

package gov.faa.ang.swac.datalayer.deprecated;

import gov.faa.ang.swac.datalayer.deprecated.ResourceManager;


public class ResourceManagerFixture {
	public static final String DEFAULT_DIR = "target/test-classes";
	
	public static void initializeResourceManager() {
		ResourceManager resMan = new ResourceManager("SAMPLE_SCENARIO", null);
		
		resMan.setDataDirectory(DEFAULT_DIR);
		resMan.setHomeDirectory(DEFAULT_DIR);
		resMan.setLogDirectory(DEFAULT_DIR);
		resMan.setOutputDirectory(DEFAULT_DIR);
		resMan.setReportDirectory(DEFAULT_DIR);
		resMan.setTempDirectory(DEFAULT_DIR);
		resMan.setWorkDirectory(DEFAULT_DIR);
		resMan.setTestDirectory(DEFAULT_DIR);
		
		ResourceManager.setCurrent(resMan);
	}
	
}
