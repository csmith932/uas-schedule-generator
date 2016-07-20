/**
 * Copyright "TBD", Metron Aviation & CSSI.  All rights reserved.
 * 
 * This computer Software was developed with the sponsorship of the U.S. Government
 * under Contract No. DTFAWA-10-D-00033, which has a copyright license in accordance with AMS 3.5-13.(c)(1).
 */

package gov.faa.ang.swac.data;

import gov.faa.ang.swac.data.DataExport;

import java.io.File;
import java.io.UnsupportedEncodingException;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class DataFixture {
public static final String DEFAULT_DIR = "target/test-classes";

	public static final Logger logger = LogManager.getLogger(DataFixture.class);
	
	public static void initializeData() throws UnsupportedEncodingException
	{
		initializeData(DEFAULT_DIR);
	}

	private static void initializeData(String dir) throws UnsupportedEncodingException
	{
		DataExport.exportData(new File(dir));
	}
	
	
}
