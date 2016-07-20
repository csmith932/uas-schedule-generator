package gov.faa.ang.swac.common.datatypes;

import com.mallardsoft.tuple.Pair;
import com.mallardsoft.tuple.Tuple;

import gov.faa.ang.swac.common.datatypes.Latitude;
import gov.faa.ang.swac.common.datatypes.Angle.Units;
import gov.faa.ang.swac.common.utilities.Mathematics;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;

public class LatitudeTest
{
	private static Logger logger = LogManager.getLogger(LatitudeTest.class);

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
		logger.info("Unit tests for: Latitude.normalize()");
		
		List<Pair<Double, Double>> pairs = new ArrayList<Pair<Double, Double>>();
		pairs.add( new Pair<Double, Double>(   0.0,    0.0) );
		pairs.add( new Pair<Double, Double>(  10.0,   10.0) );
		pairs.add( new Pair<Double, Double>(  20.0,   20.0) );
		pairs.add( new Pair<Double, Double>(  30.0,   30.0) );
		pairs.add( new Pair<Double, Double>(  40.0,   40.0) );
		pairs.add( new Pair<Double, Double>(  50.0,   50.0) );
		pairs.add( new Pair<Double, Double>(  60.0,   60.0) );
		pairs.add( new Pair<Double, Double>(  70.0,   70.0) );
		pairs.add( new Pair<Double, Double>(  80.0,   80.0) );
		pairs.add( new Pair<Double, Double>(  90.0,   90.0) );
		pairs.add( new Pair<Double, Double>( 100.0,   80.0) );
		pairs.add( new Pair<Double, Double>( 110.0,   70.0) );
		pairs.add( new Pair<Double, Double>( 120.0,   60.0) );
		pairs.add( new Pair<Double, Double>( 130.0,   50.0) );
		pairs.add( new Pair<Double, Double>( 140.0,   40.0) );
		pairs.add( new Pair<Double, Double>( 150.0,   30.0) );
		pairs.add( new Pair<Double, Double>( 160.0,   20.0) );
		pairs.add( new Pair<Double, Double>( 170.0,   10.0) );
		pairs.add( new Pair<Double, Double>( 180.0,    0.0) );
		pairs.add( new Pair<Double, Double>( 190.0,  -10.0) );
		pairs.add( new Pair<Double, Double>( 200.0,  -20.0) );
		pairs.add( new Pair<Double, Double>( 210.0,  -30.0) );
		pairs.add( new Pair<Double, Double>( 220.0,  -40.0) );
		pairs.add( new Pair<Double, Double>( 230.0,  -50.0) );
		pairs.add( new Pair<Double, Double>( 240.0,  -60.0) );
		pairs.add( new Pair<Double, Double>( 250.0,  -70.0) );
		pairs.add( new Pair<Double, Double>( 260.0,  -80.0) );
		pairs.add( new Pair<Double, Double>( 270.0,  -90.0) );
		pairs.add( new Pair<Double, Double>( 280.0,  -80.0) );
		pairs.add( new Pair<Double, Double>( 290.0,  -70.0) );
		pairs.add( new Pair<Double, Double>( 300.0,  -60.0) );
		pairs.add( new Pair<Double, Double>( 310.0,  -50.0) );
		pairs.add( new Pair<Double, Double>( 320.0,  -40.0) );
		pairs.add( new Pair<Double, Double>( 330.0,  -30.0) );
		pairs.add( new Pair<Double, Double>( 340.0,  -20.0) );
		pairs.add( new Pair<Double, Double>( 350.0,  -10.0) );
		pairs.add( new Pair<Double, Double>( 360.0,    0.0) );
		pairs.add( new Pair<Double, Double>( 370.0,   10.0) );
		pairs.add( new Pair<Double, Double>( 380.0,   20.0) );
		pairs.add( new Pair<Double, Double>( 390.0,   30.0) );
		pairs.add( new Pair<Double, Double>( 400.0,   40.0) );
		pairs.add( new Pair<Double, Double>( 410.0,   50.0) );
		pairs.add( new Pair<Double, Double>( 420.0,   60.0) );
		pairs.add( new Pair<Double, Double>( 430.0,   70.0) );
		pairs.add( new Pair<Double, Double>( 440.0,   80.0) );
		pairs.add( new Pair<Double, Double>( 450.0,   90.0) );
		pairs.add( new Pair<Double, Double>( 460.0,   80.0) );
		pairs.add( new Pair<Double, Double>( 470.0,   70.0) );
		pairs.add( new Pair<Double, Double>( 480.0,   60.0) );
		pairs.add( new Pair<Double, Double>( 490.0,   50.0) );
		pairs.add( new Pair<Double, Double>( 500.0,   40.0) );
		pairs.add( new Pair<Double, Double>( 510.0,   30.0) );
		pairs.add( new Pair<Double, Double>( 520.0,   20.0) );
		pairs.add( new Pair<Double, Double>( 530.0,   10.0) );
		pairs.add( new Pair<Double, Double>( 540.0,    0.0) );
		pairs.add( new Pair<Double, Double>( 550.0,  -10.0) );
		pairs.add( new Pair<Double, Double>( 560.0,  -20.0) );
		pairs.add( new Pair<Double, Double>( 570.0,  -30.0) );
		pairs.add( new Pair<Double, Double>( 580.0,  -40.0) );
		pairs.add( new Pair<Double, Double>( 590.0,  -50.0) );
		pairs.add( new Pair<Double, Double>( 600.0,  -60.0) );
		pairs.add( new Pair<Double, Double>( 610.0,  -70.0) );
		pairs.add( new Pair<Double, Double>( 620.0,  -80.0) );
		pairs.add( new Pair<Double, Double>( 630.0,  -90.0) );
		pairs.add( new Pair<Double, Double>( 640.0,  -80.0) );
		pairs.add( new Pair<Double, Double>( 650.0,  -70.0) );
		pairs.add( new Pair<Double, Double>( 660.0,  -60.0) );
		pairs.add( new Pair<Double, Double>( 670.0,  -50.0) );
		pairs.add( new Pair<Double, Double>( 680.0,  -40.0) );
		pairs.add( new Pair<Double, Double>( 690.0,  -30.0) );
		pairs.add( new Pair<Double, Double>( 700.0,  -20.0) );
		pairs.add( new Pair<Double, Double>( 710.0,  -10.0) );
		pairs.add( new Pair<Double, Double>( 720.0,    0.0) );

		pairs.add( new Pair<Double, Double>(   0.0,    0.0) );
		pairs.add( new Pair<Double, Double>( -10.0,  -10.0) );
		pairs.add( new Pair<Double, Double>( -20.0,  -20.0) );
		pairs.add( new Pair<Double, Double>( -30.0,  -30.0) );
		pairs.add( new Pair<Double, Double>( -40.0,  -40.0) );
		pairs.add( new Pair<Double, Double>( -50.0,  -50.0) );
		pairs.add( new Pair<Double, Double>( -60.0,  -60.0) );
		pairs.add( new Pair<Double, Double>( -70.0,  -70.0) );
		pairs.add( new Pair<Double, Double>( -80.0,  -80.0) );
		pairs.add( new Pair<Double, Double>( -90.0,  -90.0) );
		pairs.add( new Pair<Double, Double>(-100.0,  -80.0) );
		pairs.add( new Pair<Double, Double>(-110.0,  -70.0) );
		pairs.add( new Pair<Double, Double>(-120.0,  -60.0) );
		pairs.add( new Pair<Double, Double>(-130.0,  -50.0) );
		pairs.add( new Pair<Double, Double>(-140.0,  -40.0) );
		pairs.add( new Pair<Double, Double>(-150.0,  -30.0) );
		pairs.add( new Pair<Double, Double>(-160.0,  -20.0) );
		pairs.add( new Pair<Double, Double>(-170.0,  -10.0) );
		pairs.add( new Pair<Double, Double>(-180.0,    0.0) );
		pairs.add( new Pair<Double, Double>(-190.0,   10.0) );
		pairs.add( new Pair<Double, Double>(-200.0,   20.0) );
		pairs.add( new Pair<Double, Double>(-210.0,   30.0) );
		pairs.add( new Pair<Double, Double>(-220.0,   40.0) );
		pairs.add( new Pair<Double, Double>(-230.0,   50.0) );
		pairs.add( new Pair<Double, Double>(-240.0,   60.0) );
		pairs.add( new Pair<Double, Double>(-250.0,   70.0) );
		pairs.add( new Pair<Double, Double>(-260.0,   80.0) );
		pairs.add( new Pair<Double, Double>(-270.0,   90.0) );
		pairs.add( new Pair<Double, Double>(-280.0,   80.0) );
		pairs.add( new Pair<Double, Double>(-290.0,   70.0) );
		pairs.add( new Pair<Double, Double>(-300.0,   60.0) );
		pairs.add( new Pair<Double, Double>(-310.0,   50.0) );
		pairs.add( new Pair<Double, Double>(-320.0,   40.0) );
		pairs.add( new Pair<Double, Double>(-330.0,   30.0) );
		pairs.add( new Pair<Double, Double>(-340.0,   20.0) );
		pairs.add( new Pair<Double, Double>(-350.0,   10.0) );
		pairs.add( new Pair<Double, Double>(-360.0,    0.0) );
		pairs.add( new Pair<Double, Double>(-370.0,  -10.0) );
		pairs.add( new Pair<Double, Double>(-380.0,  -20.0) );
		pairs.add( new Pair<Double, Double>(-390.0,  -30.0) );
		pairs.add( new Pair<Double, Double>(-400.0,  -40.0) );
		pairs.add( new Pair<Double, Double>(-410.0,  -50.0) );
		pairs.add( new Pair<Double, Double>(-420.0,  -60.0) );
		pairs.add( new Pair<Double, Double>(-430.0,  -70.0) );
		pairs.add( new Pair<Double, Double>(-440.0,  -80.0) );
		pairs.add( new Pair<Double, Double>(-450.0,  -90.0) );
		pairs.add( new Pair<Double, Double>(-460.0,  -80.0) );
		pairs.add( new Pair<Double, Double>(-470.0,  -70.0) );
		pairs.add( new Pair<Double, Double>(-480.0,  -60.0) );
		pairs.add( new Pair<Double, Double>(-490.0,  -50.0) );
		pairs.add( new Pair<Double, Double>(-500.0,  -40.0) );
		pairs.add( new Pair<Double, Double>(-510.0,  -30.0) );
		pairs.add( new Pair<Double, Double>(-520.0,  -20.0) );
		pairs.add( new Pair<Double, Double>(-530.0,  -10.0) );
		pairs.add( new Pair<Double, Double>(-540.0,    0.0) );
		pairs.add( new Pair<Double, Double>(-550.0,   10.0) );
		pairs.add( new Pair<Double, Double>(-560.0,   20.0) );
		pairs.add( new Pair<Double, Double>(-570.0,   30.0) );
		pairs.add( new Pair<Double, Double>(-580.0,   40.0) );
		pairs.add( new Pair<Double, Double>(-590.0,   50.0) );
		pairs.add( new Pair<Double, Double>(-600.0,   60.0) );
		pairs.add( new Pair<Double, Double>(-610.0,   70.0) );
		pairs.add( new Pair<Double, Double>(-620.0,   80.0) );
		pairs.add( new Pair<Double, Double>(-630.0,   90.0) );
		pairs.add( new Pair<Double, Double>(-640.0,   80.0) );
		pairs.add( new Pair<Double, Double>(-650.0,   70.0) );
		pairs.add( new Pair<Double, Double>(-660.0,   60.0) );
		pairs.add( new Pair<Double, Double>(-670.0,   50.0) );
		pairs.add( new Pair<Double, Double>(-680.0,   40.0) );
		pairs.add( new Pair<Double, Double>(-690.0,   30.0) );
		pairs.add( new Pair<Double, Double>(-700.0,   20.0) );
		pairs.add( new Pair<Double, Double>(-710.0,   10.0) );
		pairs.add( new Pair<Double, Double>(-720.0,    0.0) );

		for (Pair<Double, Double> pair : pairs)
		{
			Double angleOriginal = Tuple.first(pair);
			Double angleNormalized = Tuple.second(pair);

    		Latitude latitudeOriginal = new Latitude(angleOriginal, Units.DEGREES);
    		Latitude latitudeNormalized = latitudeOriginal.normalized();
    		Assert.assertNotNull(latitudeNormalized);
    		boolean pass = Mathematics.equals(latitudeNormalized.degrees(), angleNormalized);
    		logger.info("Latitude(" + angleOriginal + ", DEGREES).normalize() = " + latitudeNormalized.degrees() + "... should be " + latitudeNormalized +"... " + (pass ? "PASS":"FAIL"));
    		Assert.assertTrue(pass);
		}    	
	}
	
	public static void main(String[] args)
	{
		org.junit.runner.JUnitCore.runClasses(LatitudeTest.class);
	}
}