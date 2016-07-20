/**
 * Copyright "TBD", Metron Aviation & CSSI.  All rights reserved.
 * 
 * This computer Software was developed with the sponsorship of the U.S. Government
 * under Contract No. DTFAWA-10-D-00033, which has a copyright license in accordance with AMS 3.5-13.(c)(1).
 */

package gov.faa.ang.swac.common.flightmodeling;

import gov.faa.ang.swac.common.datatypes.Altitude;
import gov.faa.ang.swac.common.flightmodeling.AltitudeRestriction.AltitudeType;
import gov.faa.ang.swac.common.flightmodeling.AltitudeRestriction.RestrictionType;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.mallardsoft.tuple.Triple;
import com.mallardsoft.tuple.Tuple;

public class AltitudeRestrictionTest
{
	private static Logger logger = LogManager.getLogger(AltitudeRestrictionTest.class);
	
	@Before
	public void setup()
	{
		// By default, swac-parent pom's surefire plugin configuration specifies
		// swac-data/src/test/resources/log4j-junit.xml to be logging configuration file, and that file logs levels
		// ERROR or higher.
		// Uncomment to manually setup logger for quick local run 
		// BasicConfigurator.configure();
	}
	
	
	public static AltitudeRestriction dummy(int seed)
	{
		int restrictionTypeIndex = seed % (RestrictionType.values().length);
		RestrictionType restrictionType = RestrictionType.values()[restrictionTypeIndex];
		
		int altitudeTypeIndex = (seed+1) % (AltitudeType.values().length);
		AltitudeType altitudeType = AltitudeType.values()[altitudeTypeIndex];
		Altitude altitude = Altitude.valueOfFeet(10000.0 + seed);
		
		return new AltitudeRestriction(restrictionType, altitudeType, altitude);
	}
	
	@Test
	public void toFromTextRecord()
	{
		logger.info("================================================================================");
		logger.info("Unit tests for: AltitudeRestriction.toString() & AltitudeRestriction.fromTextRecord()");
		
		List<Triple<RestrictionType, AltitudeType, Altitude>> tuples = new ArrayList<Triple<RestrictionType, AltitudeType, Altitude>>();
		tuples.add( new Triple<RestrictionType, AltitudeType, Altitude>( RestrictionType.SID, AltitudeType.AT, Altitude.valueOfFeet(1000.0)) );
		tuples.add( new Triple<RestrictionType, AltitudeType, Altitude>( RestrictionType.STAR, AltitudeType.AT_OR_ABOVE, Altitude.valueOfFeet(250.0)) );
		tuples.add( new Triple<RestrictionType, AltitudeType, Altitude>( RestrictionType.IAP, AltitudeType.AT_OR_BELOW, Altitude.valueOfFeet(9999.0)) );

		for (Triple<RestrictionType, AltitudeType, Altitude> tuple : tuples)
		{
			RestrictionType restrictionType = Tuple.first(tuple);
			AltitudeType altitudeType = Tuple.second(tuple);
			Altitude altitude = Tuple.third(tuple);
			
    		AltitudeRestriction altitudeRestrictionOrig = new AltitudeRestriction(restrictionType, altitudeType, altitude);
    		
    		AltitudeRestriction altitudeRestrictionNew = AltitudeRestriction.fromTextRecord(altitudeRestrictionOrig.toString());
    		boolean pass = altitudeRestrictionOrig.equals(altitudeRestrictionNew);
    		logger.info("----------------------------------------");
    		logger.info("Creating original AltitudeRestriction: \"" + altitudeRestrictionOrig.toString() + "\"");
    		logger.info("       fromTextRecord(toString()): \"" + altitudeRestrictionNew.toString() + "\"... " + (pass ? "PASS":"FAIL"));
    		Assert.assertTrue(pass);
		}

	}

	public static void main(String[] args)
	{
		org.junit.runner.JUnitCore.runClasses(AltitudeRestrictionTest.class);
	}
}