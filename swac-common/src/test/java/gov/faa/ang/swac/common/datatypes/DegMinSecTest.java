package gov.faa.ang.swac.common.datatypes;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;

import com.mallardsoft.tuple.Quintuple;
import com.mallardsoft.tuple.Sextuple;
import com.mallardsoft.tuple.Tuple;

import gov.faa.ang.swac.common.datatypes.DegMinSec;
import gov.faa.ang.swac.common.datatypes.DegMinSec.Format;
import gov.faa.ang.swac.common.utilities.Mathematics;

public class DegMinSecTest
{
	private static Logger logger = LogManager.getLogger(DegMinSecTest.class);

	@Before
	public void setup()
	{
		// By default, swac-parent pom's surefire plugin configuration specifies
		// swac-data/src/test/resources/log4j-junit.xml to be logging configuration file, and that file logs levels
		// ERROR or higher.
		// Uncomment to manually setup logger for quick local run 
		// BasicConfigurator.configure();
	}
	
	@Test
	public void normalize()
	{
		logger.info("================================================================================");
		logger.info("Unit tests for DegMinSec.normalize()");

		List<Sextuple<Integer, Integer, Double, Integer, Integer, Double>> tuples = new ArrayList<Sextuple<Integer, Integer, Double, Integer, Integer, Double>>();
		//                                                                             Original              Normalized
		//                                                                             deg   min     sec     deg   min      sec
		tuples.add( new Sextuple<Integer, Integer, Double, Integer, Integer, Double>(    0,    0,    0.0,      0,    0,     0.0) ); // Zero

		// Single normalization: seconds
		tuples.add( new Sextuple<Integer, Integer, Double, Integer, Integer, Double>(    0,    0,   59.9,      0,    0,    59.9) );
		tuples.add( new Sextuple<Integer, Integer, Double, Integer, Integer, Double>(    0,    0,   60.0,      0,    1,     0.0) );
		tuples.add( new Sextuple<Integer, Integer, Double, Integer, Integer, Double>(    0,    0,   60.1,      0,    1,     0.1) );
		tuples.add( new Sextuple<Integer, Integer, Double, Integer, Integer, Double>(    0,    0,  120.0,      0,    2,     0.0) );
		tuples.add( new Sextuple<Integer, Integer, Double, Integer, Integer, Double>(    0,    0,  400.0,      0,    6,    40.0) );
		tuples.add( new Sextuple<Integer, Integer, Double, Integer, Integer, Double>(    0,    0, 5000.0,      1,   23,    20.0) );

		// Single normalization: minutes
		tuples.add( new Sextuple<Integer, Integer, Double, Integer, Integer, Double>(    0,   59,    0.0,      0,    59,    0.0) );
		tuples.add( new Sextuple<Integer, Integer, Double, Integer, Integer, Double>(    0,   60,    0.6,      1,     0,    0.6) );
		tuples.add( new Sextuple<Integer, Integer, Double, Integer, Integer, Double>(    1,   61,    0.6,      2,     1,    0.6) );
		tuples.add( new Sextuple<Integer, Integer, Double, Integer, Integer, Double>(    0,  120,    0.0,      2,     0,    0.0) );
		tuples.add( new Sextuple<Integer, Integer, Double, Integer, Integer, Double>(    0,  400,    0.0,      6,    40,    0.0) );
		tuples.add( new Sextuple<Integer, Integer, Double, Integer, Integer, Double>(    0, 5000,    0.0,     83,    20,    0.0) );

		// Single normalization: degrees
		tuples.add( new Sextuple<Integer, Integer, Double, Integer, Integer, Double>(   59,    0,    0.0,     59,     0,    0.0) );
		tuples.add( new Sextuple<Integer, Integer, Double, Integer, Integer, Double>(   60,    0,    0.6,     60,     0,    0.6) );
		tuples.add( new Sextuple<Integer, Integer, Double, Integer, Integer, Double>(   61,    0,    0.0,     61,     0,    0.0) );
		tuples.add( new Sextuple<Integer, Integer, Double, Integer, Integer, Double>(  120,    0,    0.0,    120,     0,    0.0) );
		tuples.add( new Sextuple<Integer, Integer, Double, Integer, Integer, Double>(  400,    0,    0.0,    400,     0,    0.0) );
		tuples.add( new Sextuple<Integer, Integer, Double, Integer, Integer, Double>( 5000,    0,    0.0,   5000,     0,    0.0) );

		for (Sextuple<Integer, Integer, Double, Integer, Integer, Double> tuple : tuples)
		{
			int degOrig = Tuple.first(tuple);
			int minOrig = Tuple.second(tuple);
			double secOrig = Tuple.third(tuple);
			
			int degNorm = Tuple.fourth(tuple);
			int minNorm = Tuple.fifth(tuple);
			double secNorm = Tuple.sixth(tuple);
			
			DegMinSec degMinSec = new DegMinSec(true, degOrig, minOrig, secOrig).normalize();
    		boolean pass = degMinSec.deg() == degNorm &&
    		               degMinSec.min() == minNorm &&
    		               Mathematics.equals(degMinSec.sec(), secNorm);
    		
    		logger.info("DegMinSec(" + degOrig + ", " + minOrig + ", " + secOrig + ").normalize() = " + degMinSec.toString(Format.SYMBOL) + " (should be " + new DegMinSec(true, degNorm, minNorm, secNorm).toString(Format.SYMBOL) + ")... " + (pass ? "PASS":"FAIL"));
    		Assert.assertTrue(pass);
		}  
	}

	@Test
	public void roundToNearestSecond()
	{
		logger.info("================================================================================");
		logger.info("Unit tests for DegMinSec.roundToNearestSecond()");

		List<Sextuple<Integer, Integer, Double, Integer, Integer, Integer>> tuples = new ArrayList<Sextuple<Integer, Integer, Double, Integer, Integer, Integer>>();
		//                                                                                 Original       Rounded
		//                                                                              deg  min  sec   deg  min  sec
		tuples.add( new Sextuple<Integer, Integer, Double, Integer, Integer, Integer>(   0,  0,  0.0,    0,  0,  0) );
		tuples.add( new Sextuple<Integer, Integer, Double, Integer, Integer, Integer>(   1,  1,  0.4,    1,  1,  0) );
		tuples.add( new Sextuple<Integer, Integer, Double, Integer, Integer, Integer>(   1,  1,  0.5,    1,  1,  1) );
		tuples.add( new Sextuple<Integer, Integer, Double, Integer, Integer, Integer>(   1,  1,  0.6,    1,  1,  1) );
		
		tuples.add( new Sextuple<Integer, Integer, Double, Integer, Integer, Integer>(   0,  0,  32.9,   0,  0,  33) );
		tuples.add( new Sextuple<Integer, Integer, Double, Integer, Integer, Integer>(   0,  0,  33.0,   0,  0,  33) );
		tuples.add( new Sextuple<Integer, Integer, Double, Integer, Integer, Integer>(   0,  0,  33.1,   0,  0,  33) );
		tuples.add( new Sextuple<Integer, Integer, Double, Integer, Integer, Integer>(   0,  0,  33.2,   0,  0,  33) );
		tuples.add( new Sextuple<Integer, Integer, Double, Integer, Integer, Integer>(   0,  0,  33.3,   0,  0,  33) );
		tuples.add( new Sextuple<Integer, Integer, Double, Integer, Integer, Integer>(   0,  0,  33.4,   0,  0,  33) );
		tuples.add( new Sextuple<Integer, Integer, Double, Integer, Integer, Integer>(   0,  0,  33.5,   0,  0,  34) );
		tuples.add( new Sextuple<Integer, Integer, Double, Integer, Integer, Integer>(   0,  0,  33.6,   0,  0,  34) );
		tuples.add( new Sextuple<Integer, Integer, Double, Integer, Integer, Integer>(   0,  0,  33.7,   0,  0,  34) );
		tuples.add( new Sextuple<Integer, Integer, Double, Integer, Integer, Integer>(   0,  0,  33.8,   0,  0,  34) );
		tuples.add( new Sextuple<Integer, Integer, Double, Integer, Integer, Integer>(   0,  0,  33.9,   0,  0,  34) );
		tuples.add( new Sextuple<Integer, Integer, Double, Integer, Integer, Integer>(   0,  0,  34.0,   0,  0,  34) );
		
		tuples.add( new Sextuple<Integer, Integer, Double, Integer, Integer, Integer>(   0,  0,  59.9,   0,  0,  60) );
		tuples.add( new Sextuple<Integer, Integer, Double, Integer, Integer, Integer>(   0,  0,  60.0,   0,  0,  60) );
		tuples.add( new Sextuple<Integer, Integer, Double, Integer, Integer, Integer>(   0,  0,  60.1,   0,  0,  60) );

		for (Sextuple<Integer, Integer, Double, Integer, Integer, Integer> tuple : tuples)
		{
			int degOrig = Tuple.first(tuple);
			int minOrig = Tuple.second(tuple);
			double secOrig = Tuple.third(tuple);
			
			int degRound = Tuple.fourth(tuple);
			int minRound = Tuple.fifth(tuple);
			int secRound = Tuple.sixth(tuple);
			
			DegMinSec degMinSec = new DegMinSec(true, degOrig, minOrig, secOrig).roundToNearestSecond();
    		boolean pass = degMinSec.deg() == degRound &&
    		               degMinSec.min() == minRound &&
    		               degMinSec.sec() == secRound;
    		
    		logger.info("DegMinSec(" + degOrig + ", " + minOrig + ", " + secOrig + ").roundToNearestSecond() = " + degMinSec.toString(Format.SYMBOL) + " (should be " + new DegMinSec(true, degRound, minRound, secRound).toString(Format.SYMBOL) + ")... " + (pass ? "PASS":"FAIL"));
    		Assert.assertTrue(pass);
		}  
	}

	@Test
	public void asDegrees_asMinutes_asSeconds()
	{
		logger.info("================================================================================");
		logger.info("Unit tests for DegMinSec.asDegrees(), DegMinSec.asMinutes(), & DegMinSec.asSeconds()");

		List<Sextuple<Integer, Integer, Double, Double, Double, Double>> tuples = new ArrayList<Sextuple<Integer, Integer, Double, Double, Double, Double>>();
		//                                                                          deg     min   sec         degrees          minutes           seconds
		tuples.add( new Sextuple<Integer, Integer, Double, Double, Double, Double>(   0,    0,    0.1,    0.000027778,     0.001666667,      0.100000000) );
		tuples.add( new Sextuple<Integer, Integer, Double, Double, Double, Double>(   0,    0,    0.2,    0.000055556,     0.003333333,      0.200000000) );
		tuples.add( new Sextuple<Integer, Integer, Double, Double, Double, Double>(   0,    0,    0.3,    0.000083333,     0.005000000,      0.300000000) );
		tuples.add( new Sextuple<Integer, Integer, Double, Double, Double, Double>(   0,    0,    0.4,    0.000111111,     0.006666667,      0.400000000) );
		tuples.add( new Sextuple<Integer, Integer, Double, Double, Double, Double>(   0,    0,    0.5,    0.000138889,     0.008333333,      0.500000000) );
		tuples.add( new Sextuple<Integer, Integer, Double, Double, Double, Double>(   0,    0,    0.6,    0.000166667,     0.010000000,      0.600000000) );
		tuples.add( new Sextuple<Integer, Integer, Double, Double, Double, Double>(   0,    0,    0.7,    0.000194444,     0.011666667,      0.700000000) );
		tuples.add( new Sextuple<Integer, Integer, Double, Double, Double, Double>(   0,    0,    0.8,    0.000222222,     0.013333333,      0.800000000) );
		tuples.add( new Sextuple<Integer, Integer, Double, Double, Double, Double>(   0,    0,    0.9,    0.000250000,     0.015000000,      0.900000000) );
		tuples.add( new Sextuple<Integer, Integer, Double, Double, Double, Double>(   0,    1,    0.0,    0.016666667,     1.000000000,     60.000000000) );
		tuples.add( new Sextuple<Integer, Integer, Double, Double, Double, Double>(   0,    2,    0.0,    0.033333333,     2.000000000,    120.000000000) );
		tuples.add( new Sextuple<Integer, Integer, Double, Double, Double, Double>(   0,    3,    0.0,    0.050000000,     3.000000000,    180.000000000) );
		tuples.add( new Sextuple<Integer, Integer, Double, Double, Double, Double>(   0,    4,    0.0,    0.066666667,     4.000000000,    240.000000000) );
		tuples.add( new Sextuple<Integer, Integer, Double, Double, Double, Double>(   0,    5,    0.0,    0.083333333,     5.000000000,    300.000000000) );
		tuples.add( new Sextuple<Integer, Integer, Double, Double, Double, Double>(   0,    6,    0.0,    0.100000000,     6.000000000,    360.000000000) );
		tuples.add( new Sextuple<Integer, Integer, Double, Double, Double, Double>(   0,    7,    0.0,    0.116666667,     7.000000000,    420.000000000) );
		tuples.add( new Sextuple<Integer, Integer, Double, Double, Double, Double>(   0,    8,    0.0,    0.133333333,     8.000000000,    480.000000000) );
		tuples.add( new Sextuple<Integer, Integer, Double, Double, Double, Double>(   0,    9,    0.0,    0.150000000,     9.000000000,    540.000000000) );
		tuples.add( new Sextuple<Integer, Integer, Double, Double, Double, Double>(   0,   10,    0.0,    0.166666667,    10.000000000,    600.000000000) );
		tuples.add( new Sextuple<Integer, Integer, Double, Double, Double, Double>(   1,    0,    0.0,    1.000000000,    60.000000000,   3600.000000000) );
		tuples.add( new Sextuple<Integer, Integer, Double, Double, Double, Double>(   2,    0,    0.0,    2.000000000,   120.000000000,   7200.000000000) );
		tuples.add( new Sextuple<Integer, Integer, Double, Double, Double, Double>(   3,    0,    0.0,    3.000000000,   180.000000000,  10800.000000000) );
		tuples.add( new Sextuple<Integer, Integer, Double, Double, Double, Double>(   4,    0,    0.0,    4.000000000,   240.000000000,  14400.000000000) );
		tuples.add( new Sextuple<Integer, Integer, Double, Double, Double, Double>(   5,    0,    0.0,    5.000000000,   300.000000000,  18000.000000000) );
		tuples.add( new Sextuple<Integer, Integer, Double, Double, Double, Double>(   6,    0,    0.0,    6.000000000,   360.000000000,  21600.000000000) );
		tuples.add( new Sextuple<Integer, Integer, Double, Double, Double, Double>(   7,    0,    0.0,    7.000000000,   420.000000000,  25200.000000000) );
		tuples.add( new Sextuple<Integer, Integer, Double, Double, Double, Double>(   8,    0,    0.0,    8.000000000,   480.000000000,  28800.000000000) );
		tuples.add( new Sextuple<Integer, Integer, Double, Double, Double, Double>(   9,    0,    0.0,    9.000000000,   540.000000000,  32400.000000000) );
		tuples.add( new Sextuple<Integer, Integer, Double, Double, Double, Double>(  10,    0,    0.0,   10.000000000,   600.000000000,  36000.000000000) );

		for (Sextuple<Integer, Integer, Double, Double, Double, Double> tuple : tuples)
		{
			int degOrig = Tuple.first(tuple);
			int minOrig = Tuple.second(tuple);
			double secOrig = Tuple.third(tuple);
			DegMinSec degMinSec = new DegMinSec(true, degOrig, minOrig, secOrig);
			
			double degrees = Tuple.fourth(tuple);
			double minutes = Tuple.fifth(tuple);
			double seconds = Tuple.sixth(tuple);
			
    		boolean pass = Mathematics.equals(degMinSec.asDegrees(), degrees) && Mathematics.equals(degMinSec.asMinutes(), minutes) && Mathematics.equals(degMinSec.asSeconds(), seconds);

    		logger.info("DegMinSec(" + degOrig + ", " + minOrig + ", " + secOrig + ").asDegrees() = " + degMinSec.asDegrees() + " (should be " + degrees + "), " + 
	                   "asMinutes() = " + degMinSec.asMinutes() + " (should be " + minutes + "), " +
	                   "asSeconds() = " + degMinSec.asSeconds() + " (should be " + seconds + ")... " + (pass ? "PASS":"FAIL"));
    		Assert.assertTrue(pass);
		}  
	}

	@Test
	public void toStringTest()
	{
		logger.info("================================================================================");
		logger.info("Unit tests for DegMinSec.toString()");

		List<Quintuple<DegMinSec, String, String, String, String>> tuples = new ArrayList<Quintuple<DegMinSec, String, String, String, String>>();
		//                                                                        DegMinSec                              COMPACT               SHORT                        LONG                  SYMBOL
		tuples.add( new Quintuple<DegMinSec, String, String, String, String>( new DegMinSec(),                          "000000",      "00.00.00.00",    "0 deg 0 min 0.0000 sec",      "0" + DegMinSec.DEGREE_SYMBOL + " 0' 0.0000\"") );
		tuples.add( new Quintuple<DegMinSec, String, String, String, String>( new DegMinSec( true, 1, 2, 3.0),          "010203",      "01.02.03.00",    "1 deg 2 min 3.0000 sec",      "1" + DegMinSec.DEGREE_SYMBOL + " 2' 3.0000\"") );
		tuples.add( new Quintuple<DegMinSec, String, String, String, String>( new DegMinSec(false, 1, 2, 3.0),         "-010203",     "-01.02.03.00",   "-1 deg 2 min 3.0000 sec",     "-1" + DegMinSec.DEGREE_SYMBOL + " 2' 3.0000\"") );

		tuples.add( new Quintuple<DegMinSec, String, String, String, String>( new DegMinSec(false, 1, 2, 3.1),         "-010203",     "-01.02.03.10",   "-1 deg 2 min 3.1000 sec",      "-1" + DegMinSec.DEGREE_SYMBOL + " 2' 3.1000\"") );
		tuples.add( new Quintuple<DegMinSec, String, String, String, String>( new DegMinSec(false, 1, 2, 3.9),         "-010204",     "-01.02.03.90",   "-1 deg 2 min 3.9000 sec",      "-1" + DegMinSec.DEGREE_SYMBOL + " 2' 3.9000\"") );

		tuples.add( new Quintuple<DegMinSec, String, String, String, String>( new DegMinSec(false, 179, 15, 4.5678),  "-1791505",    "-179.15.04.57", "-179 deg 15 min 4.5678 sec",  "-179" + DegMinSec.DEGREE_SYMBOL + " 15' 4.5678\"") );
		tuples.add( new Quintuple<DegMinSec, String, String, String, String>( new DegMinSec(false, 179, 15, 4.56789), "-1791505",    "-179.15.04.57", "-179 deg 15 min 4.5679 sec",  "-179" + DegMinSec.DEGREE_SYMBOL + " 15' 4.5679\"") );


		for (Quintuple<DegMinSec, String, String, String, String> tuple : tuples)
		{
			DegMinSec degMinSec  = Tuple.first(tuple);
			String stringCompact = Tuple.second(tuple);
			String stringShort   = Tuple.third(tuple);
			String stringLong    = Tuple.fourth(tuple);
			String stringSymbol  = Tuple.fifth(tuple);

    		boolean pass = degMinSec.toString(Format.COMPACT).equals(stringCompact) &&
    					   degMinSec.toString(Format.SHORT).equals(stringShort) &&
    					   degMinSec.toString(Format.LONG).equals(stringLong) &&
    					   degMinSec.toString(Format.SYMBOL).equals(stringSymbol);

    		logger.info("degMinSec.toString(Format.COMPACT) = \"" + degMinSec.toString(Format.COMPACT) + "\" (should be \"" + stringCompact + "\")\n" + 
 		                       "         .toString(Format.SHORT) = \"" + degMinSec.toString(Format.SHORT) + "\" (should be \"" + stringShort + "\")\n" + 
 		                       "         .toString(Format.LONG) = \"" + degMinSec.toString(Format.LONG) + "\" (should be \"" + stringLong + "\")\n" + 
 		                       "         .toString(Format.SYMBOL) = \"" + degMinSec.toString(Format.SYMBOL) + "\" (should be \"" + stringSymbol + "\")... " + (pass ? "PASS":"FAIL"));
    		Assert.assertTrue(pass);
		}  
	}
	
	public static void main(String[] args)
	{
		org.junit.runner.JUnitCore.runClasses(DegMinSecTest.class);
	}
}