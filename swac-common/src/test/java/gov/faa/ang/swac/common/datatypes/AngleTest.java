package gov.faa.ang.swac.common.datatypes;

import gov.faa.ang.swac.common.datatypes.Angle.Units;
import gov.faa.ang.swac.common.utilities.Mathematics;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.mallardsoft.tuple.Pair;
import com.mallardsoft.tuple.Tuple;

public class AngleTest
{
	private static Logger logger = LogManager.getLogger(AngleTest.class);

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
	public void normalize_and_isNormalized()
	{
		logger.info("================================================================================");
		logger.info("Unit tests for Angle.normalize() & Angle.isNormalized()");

		List<Pair<Double, Double>> degrees = new ArrayList<Pair<Double, Double>>();
		//                                     before  after
		degrees.add( new Pair<Double, Double>(   0.0,    0.0) );
		degrees.add( new Pair<Double, Double>(  10.0,   10.0) );
		degrees.add( new Pair<Double, Double>(  20.0,   20.0) );
		degrees.add( new Pair<Double, Double>(  30.0,   30.0) );
		degrees.add( new Pair<Double, Double>(  40.0,   40.0) );
		degrees.add( new Pair<Double, Double>(  50.0,   50.0) );
		degrees.add( new Pair<Double, Double>(  60.0,   60.0) );
		degrees.add( new Pair<Double, Double>(  70.0,   70.0) );
		degrees.add( new Pair<Double, Double>(  80.0,   80.0) );
		degrees.add( new Pair<Double, Double>(  90.0,   90.0) );
		degrees.add( new Pair<Double, Double>( 100.0,  100.0) );
		degrees.add( new Pair<Double, Double>( 110.0,  110.0) );
		degrees.add( new Pair<Double, Double>( 120.0,  120.0) );
		degrees.add( new Pair<Double, Double>( 130.0,  130.0) );
		degrees.add( new Pair<Double, Double>( 140.0,  140.0) );
		degrees.add( new Pair<Double, Double>( 150.0,  150.0) );
		degrees.add( new Pair<Double, Double>( 160.0,  160.0) );
		degrees.add( new Pair<Double, Double>( 170.0,  170.0) );
		degrees.add( new Pair<Double, Double>( 180.0,  180.0) );
		degrees.add( new Pair<Double, Double>( 190.0,  190.0) );
		degrees.add( new Pair<Double, Double>( 200.0,  200.0) );
		degrees.add( new Pair<Double, Double>( 210.0,  210.0) );
		degrees.add( new Pair<Double, Double>( 220.0,  220.0) );
		degrees.add( new Pair<Double, Double>( 230.0,  230.0) );
		degrees.add( new Pair<Double, Double>( 240.0,  240.0) );
		degrees.add( new Pair<Double, Double>( 250.0,  250.0) );
		degrees.add( new Pair<Double, Double>( 260.0,  260.0) );
		degrees.add( new Pair<Double, Double>( 270.0,  270.0) );
		degrees.add( new Pair<Double, Double>( 280.0,  280.0) );
		degrees.add( new Pair<Double, Double>( 290.0,  290.0) );
		degrees.add( new Pair<Double, Double>( 300.0,  300.0) );
		degrees.add( new Pair<Double, Double>( 310.0,  310.0) );
		degrees.add( new Pair<Double, Double>( 320.0,  320.0) );
		degrees.add( new Pair<Double, Double>( 330.0,  330.0) );
		degrees.add( new Pair<Double, Double>( 340.0,  340.0) );
		degrees.add( new Pair<Double, Double>( 350.0,  350.0) );
		degrees.add( new Pair<Double, Double>( 360.0,    0.0) );
		degrees.add( new Pair<Double, Double>( 370.0,   10.0) );
		degrees.add( new Pair<Double, Double>( 380.0,   20.0) );
		degrees.add( new Pair<Double, Double>( 390.0,   30.0) );
		degrees.add( new Pair<Double, Double>( 400.0,   40.0) );
		degrees.add( new Pair<Double, Double>( 410.0,   50.0) );
		degrees.add( new Pair<Double, Double>( 420.0,   60.0) );
		degrees.add( new Pair<Double, Double>( 430.0,   70.0) );
		degrees.add( new Pair<Double, Double>( 440.0,   80.0) );
		degrees.add( new Pair<Double, Double>( 450.0,   90.0) );
		degrees.add( new Pair<Double, Double>( 460.0,  100.0) );
		degrees.add( new Pair<Double, Double>( 470.0,  110.0) );
		degrees.add( new Pair<Double, Double>( 480.0,  120.0) );
		degrees.add( new Pair<Double, Double>( 490.0,  130.0) );
		degrees.add( new Pair<Double, Double>( 500.0,  140.0) );
		degrees.add( new Pair<Double, Double>( 510.0,  150.0) );
		degrees.add( new Pair<Double, Double>( 520.0,  160.0) );
		degrees.add( new Pair<Double, Double>( 530.0,  170.0) );
		degrees.add( new Pair<Double, Double>( 540.0,  180.0) );
		degrees.add( new Pair<Double, Double>( 550.0,  190.0) );
		degrees.add( new Pair<Double, Double>( 560.0,  200.0) );
		degrees.add( new Pair<Double, Double>( 570.0,  210.0) );
		degrees.add( new Pair<Double, Double>( 580.0,  220.0) );
		degrees.add( new Pair<Double, Double>( 590.0,  230.0) );
		degrees.add( new Pair<Double, Double>( 600.0,  240.0) );
		degrees.add( new Pair<Double, Double>( 610.0,  250.0) );
		degrees.add( new Pair<Double, Double>( 620.0,  260.0) );
		degrees.add( new Pair<Double, Double>( 630.0,  270.0) );
		degrees.add( new Pair<Double, Double>( 640.0,  280.0) );
		degrees.add( new Pair<Double, Double>( 650.0,  290.0) );
		degrees.add( new Pair<Double, Double>( 660.0,  300.0) );
		degrees.add( new Pair<Double, Double>( 670.0,  310.0) );
		degrees.add( new Pair<Double, Double>( 680.0,  320.0) );
		degrees.add( new Pair<Double, Double>( 690.0,  330.0) );
		degrees.add( new Pair<Double, Double>( 700.0,  340.0) );
		degrees.add( new Pair<Double, Double>( 710.0,  350.0) );
		degrees.add( new Pair<Double, Double>( 720.0,    0.0) );
		degrees.add( new Pair<Double, Double>(1440.0,    0.0) );

		degrees.add( new Pair<Double, Double>(   0.0,    0.0) );
		degrees.add( new Pair<Double, Double>( -10.0,  350.0) );
		degrees.add( new Pair<Double, Double>( -20.0,  340.0) );
		degrees.add( new Pair<Double, Double>( -30.0,  330.0) );
		degrees.add( new Pair<Double, Double>( -40.0,  320.0) );
		degrees.add( new Pair<Double, Double>( -50.0,  310.0) );
		degrees.add( new Pair<Double, Double>( -60.0,  300.0) );
		degrees.add( new Pair<Double, Double>( -70.0,  290.0) );
		degrees.add( new Pair<Double, Double>( -80.0,  280.0) );
		degrees.add( new Pair<Double, Double>( -90.0,  270.0) );
		degrees.add( new Pair<Double, Double>(-100.0,  260.0) );
		degrees.add( new Pair<Double, Double>(-110.0,  250.0) );
		degrees.add( new Pair<Double, Double>(-120.0,  240.0) );
		degrees.add( new Pair<Double, Double>(-130.0,  230.0) );
		degrees.add( new Pair<Double, Double>(-140.0,  220.0) );
		degrees.add( new Pair<Double, Double>(-150.0,  210.0) );
		degrees.add( new Pair<Double, Double>(-160.0,  200.0) );
		degrees.add( new Pair<Double, Double>(-170.0,  190.0) );
		degrees.add( new Pair<Double, Double>(-180.0,  180.0) );
		degrees.add( new Pair<Double, Double>(-190.0,  170.0) );
		degrees.add( new Pair<Double, Double>(-200.0,  160.0) );
		degrees.add( new Pair<Double, Double>(-210.0,  150.0) );
		degrees.add( new Pair<Double, Double>(-220.0,  140.0) );
		degrees.add( new Pair<Double, Double>(-230.0,  130.0) );
		degrees.add( new Pair<Double, Double>(-240.0,  120.0) );
		degrees.add( new Pair<Double, Double>(-250.0,  110.0) );
		degrees.add( new Pair<Double, Double>(-260.0,  100.0) );
		degrees.add( new Pair<Double, Double>(-270.0,   90.0) );
		degrees.add( new Pair<Double, Double>(-280.0,   80.0) );
		degrees.add( new Pair<Double, Double>(-290.0,   70.0) );
		degrees.add( new Pair<Double, Double>(-300.0,   60.0) );
		degrees.add( new Pair<Double, Double>(-310.0,   50.0) );
		degrees.add( new Pair<Double, Double>(-320.0,   40.0) );
		degrees.add( new Pair<Double, Double>(-330.0,   30.0) );
		degrees.add( new Pair<Double, Double>(-340.0,   20.0) );
		degrees.add( new Pair<Double, Double>(-350.0,   10.0) );
		degrees.add( new Pair<Double, Double>(-360.0,    0.0) );
		degrees.add( new Pair<Double, Double>(-370.0,  350.0) );
		degrees.add( new Pair<Double, Double>(-380.0,  340.0) );
		degrees.add( new Pair<Double, Double>(-390.0,  330.0) );
		degrees.add( new Pair<Double, Double>(-400.0,  320.0) );
		degrees.add( new Pair<Double, Double>(-410.0,  310.0) );
		degrees.add( new Pair<Double, Double>(-420.0,  300.0) );
		degrees.add( new Pair<Double, Double>(-430.0,  290.0) );
		degrees.add( new Pair<Double, Double>(-440.0,  280.0) );
		degrees.add( new Pair<Double, Double>(-450.0,  270.0) );
		degrees.add( new Pair<Double, Double>(-460.0,  260.0) );
		degrees.add( new Pair<Double, Double>(-470.0,  250.0) );
		degrees.add( new Pair<Double, Double>(-480.0,  240.0) );
		degrees.add( new Pair<Double, Double>(-490.0,  230.0) );
		degrees.add( new Pair<Double, Double>(-500.0,  220.0) );
		degrees.add( new Pair<Double, Double>(-510.0,  210.0) );
		degrees.add( new Pair<Double, Double>(-520.0,  200.0) );
		degrees.add( new Pair<Double, Double>(-530.0,  190.0) );
		degrees.add( new Pair<Double, Double>(-540.0,  180.0) );
		degrees.add( new Pair<Double, Double>(-550.0,  170.0) );
		degrees.add( new Pair<Double, Double>(-560.0,  160.0) );
		degrees.add( new Pair<Double, Double>(-570.0,  150.0) );
		degrees.add( new Pair<Double, Double>(-580.0,  140.0) );
		degrees.add( new Pair<Double, Double>(-590.0,  130.0) );
		degrees.add( new Pair<Double, Double>(-600.0,  120.0) );
		degrees.add( new Pair<Double, Double>(-610.0,  110.0) );
		degrees.add( new Pair<Double, Double>(-620.0,  100.0) );
		degrees.add( new Pair<Double, Double>(-630.0,   90.0) );
		degrees.add( new Pair<Double, Double>(-640.0,   80.0) );
		degrees.add( new Pair<Double, Double>(-650.0,   70.0) );
		degrees.add( new Pair<Double, Double>(-660.0,   60.0) );
		degrees.add( new Pair<Double, Double>(-670.0,   50.0) );
		degrees.add( new Pair<Double, Double>(-680.0,   40.0) );
		degrees.add( new Pair<Double, Double>(-690.0,   30.0) );
		degrees.add( new Pair<Double, Double>(-700.0,   20.0) );
		degrees.add( new Pair<Double, Double>(-710.0,   10.0) );
		degrees.add( new Pair<Double, Double>(-720.0,    0.0) );

		for (Pair<Double, Double> pair : degrees)
		{
			Double originalAngle = Tuple.first(pair);
			Double normalizedAngle = Tuple.second(pair);
			boolean wasNormalized = Mathematics.equals(originalAngle, normalizedAngle);
			
    		Angle angle = new Angle(originalAngle, Units.DEGREES);
    		boolean pass1 = angle.isNormalized() == wasNormalized;
            String init = "angle.isNormalized() = " + angle.isNormalized() + "... should be " + wasNormalized + "... ";

    		Angle normalized = angle.normalized();
    		boolean pass2 = Mathematics.equals(normalized.degrees(), normalizedAngle);
    		logger.info(init + "Angle(" + originalAngle + ", DEGREES).normalize() = " + normalized.degrees() + "... should be " + normalizedAngle + "... " + (pass1 && pass2 ? "PASS":"FAIL"));
    		Assert.assertTrue(pass1 && pass2);
		}    	
	}

    @Test
	public void normalize()
	{
		logger.info("================================================================================");
		logger.info("Unit tests for Angle.normalize()");

		List<Pair<Double, Double>> degrees = new ArrayList<Pair<Double, Double>>();
		//                                   original  normalized
		degrees.add( new Pair<Double, Double>(   0.0,    0.0) );
		degrees.add( new Pair<Double, Double>( -10.0,  350.0) );
		degrees.add( new Pair<Double, Double>( 400.0,   40.0) );

    	boolean pass = false;
		for (Pair<Double, Double> pair : degrees)
		{
			Double angleOriginal = Tuple.first(pair);
			Double angleNormalized = Tuple.second(pair);
			
			Angle original = new Angle(angleOriginal, Units.DEGREES);
    		Angle normalized = original.normalized();
    		
    		// Ensure that calling normalized() does not change the original Angle object
    		pass = Mathematics.equals(original.degrees(), angleOriginal) && Mathematics.equals(normalized.degrees(), angleNormalized);
    		logger.info("original.degrees()   = " + original.degrees() + " (should be " + angleOriginal +"), " +
    				           "normalized.degrees() = " + normalized.degrees() + " (should be " + angleNormalized +")... " + (pass ? "PASS":"FAIL"));

    		Assert.assertTrue(pass);
		}
	}

	public static void main(String[] args)
	{
		org.junit.runner.JUnitCore.runClasses(AngleTest.class);
	}
}