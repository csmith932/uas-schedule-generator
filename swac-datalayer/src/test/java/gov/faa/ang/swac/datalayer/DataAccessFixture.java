/**
 * Copyright "TBD", Metron Aviation & CSSI.  All rights reserved.
 * 
 * This computer Software was developed with the sponsorship of the U.S. Government
 * under Contract No. DTFAWA-10-D-00033, which has a copyright license in accordance with AMS 3.5-13.(c)(1).
 */

package gov.faa.ang.swac.datalayer;

import gov.faa.ang.swac.datalayer.AdHocDataAccess;
import gov.faa.ang.swac.datalayer.MappedDataAccess;


public class DataAccessFixture {
	public static final String DEFAULT_DIR = "target/test-classes";
	
	public static MappedDataAccess initializeDataAccess() {
		
		MappedDataAccess retVal = new MappedDataAccess();
		
		retVal.setBatchName("TEST_BATCH");
		retVal.setScenarioName("TEST_SCENARIO");
		retVal.setBinPath(DEFAULT_DIR);
		retVal.setRootPath(DEFAULT_DIR);
		retVal.setDataDir(DEFAULT_DIR);
		retVal.setOutputDir(DEFAULT_DIR);
		retVal.setReportDir(DEFAULT_DIR);
		retVal.setTempDir(DEFAULT_DIR);
		
		AdHocDataAccess.setResourceManager(retVal);
		
		return retVal;
	}
	
}
