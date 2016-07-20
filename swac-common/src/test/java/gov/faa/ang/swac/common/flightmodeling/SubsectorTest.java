/**
 * Copyright "TBD", Metron Aviation & CSSI.  All rights reserved.
 * 
 * This computer Software was developed with the sponsorship of the U.S. Government
 * under Contract No. DTFAWA-10-D-00033, which has a copyright license in accordance with AMS 3.5-13.(c)(1).
 */

package gov.faa.ang.swac.common.flightmodeling;

import gov.faa.ang.swac.common.datatypes.Altitude;
import gov.faa.ang.swac.common.datatypes.Latitude;
import gov.faa.ang.swac.common.datatypes.Longitude;
import gov.faa.ang.swac.common.geometry.GCPoint;
import gov.faa.ang.swac.common.geometry.SimplePolygon;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;

public class SubsectorTest
{
	private static Logger logger = LogManager.getLogger(SubsectorTest.class);
	
	@Before
	public void setup()
	{
		// By default, swac-parent pom's surefire plugin configuration specifies
		// swac-data/src/test/resources/log4j-junit.xml to be logging configuration file, and that file logs levels
		// ERROR or higher.
		// Uncomment to manually setup logger for quick local run 
		// BasicConfigurator.configure();
	}
	
	public static GCPoint dummy_GCPoint(int seed)
	{
		return new GCPoint(Latitude.valueOfDegrees(seed+1), Longitude.valueOfDegrees(seed+2));
	}
	
	public static SimplePolygon dummy_SimplePolygon(int seed)
	{
		List<GCPoint> points = new ArrayList<GCPoint>();
		for (int i=0; i<seed; i++)
		{
			points.add(dummy_GCPoint(seed + i));
		}
		return new SimplePolygon(points.toArray(new GCPoint[points.size()]));		
	}
	
	public static Subsector dummy(int seed)
	{
		char letter = (char) seed;
		letter += 'A';
		
		Subsector subsector = new Subsector();
        subsector.setName( String.valueOf(letter) + String.valueOf(letter) + String.valueOf(letter) );
        subsector.setCenter( String.valueOf(letter+1) + String.valueOf(letter+1) + String.valueOf(letter+1) );
        subsector.setSector( String.valueOf(letter+2) + String.valueOf(letter+2) );
        subsector.setFpa( String.valueOf(letter+3) + String.valueOf(letter+3) );
        
        Character moduleChar = null;
        switch (seed % 2)
        {
            case 2:
                moduleChar = 'M';
                break;
            case 1:
                moduleChar = 'X';
                break;
            default:
            case 0: // Do nothing (leave moduleChar == null)
                break;
        }
        subsector.setModuleChar(moduleChar);
        
        Integer moduleNumber = seed % 5;
        if (moduleNumber == 5)
        {
            moduleNumber = null;
        }
        subsector.setModule(moduleNumber);
        
		subsector.setCeiling(Altitude.valueOfFeet(1000.0 + seed));
		subsector.setFloor(Altitude.valueOfFeet(2000.0 + seed));
		subsector.setPolygon(dummy_SimplePolygon(seed + 3));

		return subsector;
	}
		
	@Test
	public void toString_fromTextRecord()
	{
		logger.info("================================================================================");
		logger.info("Unit tests for Subsector.toString & Subsector.fromTextRecord():");

		for (int i=0; i<5; i++)
		{
			Subsector original = SubsectorTest.dummy(i); // Create a dummy object
			Subsector copy = Subsector.fromTextRecord(original.toString());  // Convert it to String and back
			boolean pass = original.equals(copy);

			logger.info("--------------------------------------------------------------------------------");
			logger.info("Comparing:");
			logger.info("----------------------------------------");
			logger.info(original.toString());
			logger.info("----------------------------------------");
			logger.info(copy.toString());
			logger.info((pass ? "PASS":"FAIL"));
			Assert.assertTrue(pass);
		}
	}


	public static void main(String[] args)
	{
		org.junit.runner.JUnitCore.runClasses(SubsectorTest.class);
	}
}