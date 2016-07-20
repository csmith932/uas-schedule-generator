/**
 * Copyright "TBD", Metron Aviation & CSSI.  All rights reserved.
 * 
 * This computer Software was developed with the sponsorship of the U.S. Government
 * under Contract No. DTFAWA-10-D-00033, which has a copyright license in accordance with AMS 3.5-13.(c)(1).
 */

package gov.faa.ang.swac.common.flightmodeling;

import gov.faa.ang.swac.common.flightmodeling.EquipmentSuffix;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;

public class EquipmentSuffixTest
{
	private static int OLD_NUM_SUFFIXES_VALUE = 4;
	
	private static Logger logger = LogManager.getLogger(EquipmentSuffixTest.class);

	@Before
	public void setup()
	{
		// By default, swac-parent pom's surefire plugin configuration specifies
		// swac-data/src/test/resources/log4j-junit.xml to be logging configuration file, and that file logs levels
		// ERROR or higher.
		// Uncomment to manually setup logger for quick local run 
		// BasicConfigurator.configure();
	}
	
    protected static EquipmentSuffix dummy(int seed)
    {
    	EquipmentSuffix equipmentSuffix = new EquipmentSuffix();
    	for (int i=0; i< OLD_NUM_SUFFIXES_VALUE; i++)
    	{
    		equipmentSuffix.setSuffix(i, (seed + i) % 10);
    	}
    	
		return equipmentSuffix;
    }
    
	@Test
	public void toString_fromTextRecord_clone()
	{
		logger.info("================================================================================");
		logger.info("Unit tests for EquipmentSuffix.toString & EquipmentSuffix.fromTextRecord() & EquipmentSuffix.clone():");

		for (int i=0; i<5; i++)
		{
			EquipmentSuffix original = EquipmentSuffixTest.dummy(i); // Create a dummy object
			EquipmentSuffix copy = EquipmentSuffix.fromTextRecord(original.toString()); // Convert it to String and back
			EquipmentSuffix clone = original.clone();
			boolean pass = original.equals(copy) && original.equals(clone) && original != clone && original.getClass() == clone.getClass();

			logger.info("--------------------------------------------------------------------------------");
			logger.info("Comparing:");
			logger.info(original.toString());
			logger.info(copy.toString());
			logger.info(clone.toString());
			logger.info((pass ? "PASS":"FAIL"));
			Assert.assertTrue(pass);
		}
	}

   
	public static void main(String[] args)
	{
		org.junit.runner.JUnitCore.runClasses(EquipmentSuffixTest.class);
	}
}
