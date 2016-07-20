package gov.faa.ang.swac.common.geometry;

import com.mallardsoft.tuple.Pair;
import com.mallardsoft.tuple.Quadruple;
import com.mallardsoft.tuple.Quintuple;
import com.mallardsoft.tuple.Tuple;

import gov.faa.ang.swac.common.datatypes.Vector3D;
import gov.faa.ang.swac.common.geometry.SphericalUtilities;
import gov.faa.ang.swac.common.geometry.SphericalUtilities.InvalidInputException;
import gov.faa.ang.swac.common.utilities.Mathematics;

import java.util.ArrayList;
import java.util.Vector;

import org.junit.Test;
import org.junit.Assert;

public class SphericalUtilitiesUnitTests
{
	@Test
	public void angle()
	{
		System.out.println("================================================================================");
		System.out.println("Unit tests for SphericalUtilities.angle():");

    	//-----------------------------------------
    	// Loop around the equator
    	//----------------------------------------
		System.out.println("Loop test: East around the equator...");
		{
	    	int numSteps = 16;
	    	double angleIncrement = 2*Math.PI/numSteps;
	    	for (int i=0; i<=numSteps; i++)
	    	{
	        	double latRad1 = 0.0;
	        	double lonRad1 = 0.0;
	        	double latRad2 = 0.0;
	    		double lonRad2 = i * angleIncrement;
	    		
				double expectedAngle = lonRad2;
	    		if (lonRad2 > Math.PI)
	    		{
	    			expectedAngle = 2*Math.PI - lonRad2;
	    		}
	    		
				double computedAngle = SphericalUtilities.angle(latRad1, lonRad1, latRad2, lonRad2);
	
				boolean pass = Mathematics.equals(expectedAngle, computedAngle);
				System.out.println("     Spherical angle between (" + latRad1 + ", " + latRad2 + ") and (" + latRad2 + ", " + lonRad2 + ") should be " + expectedAngle + "... it is " + computedAngle + "..." + (pass ? "PASS":"FAIL"));
				Assert.assertTrue(pass);
	    	}
		}

    	//----------------------------------------
    	// Loop around the prime meridian
    	//----------------------------------------
		System.out.println("Loop test: From North pole to South along the prime meridian...");
		{
	    	int numSteps = 8;
	    	double angleIncrement = Math.PI/numSteps;
	    	for (int i=0; i<=numSteps; i++)
	    	{
	        	double latRad1 = Math.PI/2;
	        	double lonRad1 = 0.0;
	    		double latRad2 = Math.PI/2 - (i * angleIncrement);
	        	double lonRad2 = 0.0;
	
	        	double expectedAngle = (i * angleIncrement);
				double computedAngle = SphericalUtilities.angle(latRad1, lonRad1, latRad2, lonRad2);
	
				boolean pass = Mathematics.equals(expectedAngle, computedAngle);
				System.out.println("     Spherical angle between (" + latRad1 + ", " + latRad2 + ") and (" + latRad2 + ", " + lonRad2 + ") should be " + expectedAngle + "... it is " + computedAngle + "..." + (pass ? "PASS":"FAIL"));
				Assert.assertTrue(pass);
	    	}
		}
    	
		System.out.println("Loop test: From South pole to North along the international date line...");
		{
	    	int numSteps = 8;
	    	double angleIncrement = Math.PI/numSteps;
	    	for (int i=0; i<=numSteps; i++)
	    	{
	        	double latRad1 = -Math.PI/2;
	        	double lonRad1 = 0.0;
	    		double latRad2 = -Math.PI/2 + (i * angleIncrement);
	        	double lonRad2 = 0.0;
	
	        	double expectedAngle = (i * angleIncrement);
				double computedAngle = SphericalUtilities.angle(latRad1, lonRad1, latRad2, lonRad2);
	
				boolean pass = Mathematics.equals(expectedAngle, computedAngle);
				System.out.println("     Spherical angle between (" + latRad1 + ", " + latRad2 + ") and (" + latRad2 + ", " + lonRad2 + ") should be " + expectedAngle + "... it is " + computedAngle + "..." + (pass ? "PASS":"FAIL"));
				Assert.assertTrue(pass);
	    	}
		}
    	
    	//                 <  lat1,   lon1,   lat2,   lon2,  angle>   NOTE: ALL VALUES ARE IN RADIANS!!!
    	ArrayList<Quintuple<Double, Double, Double, Double, Double>> tuples = new ArrayList<Quintuple<Double, Double, Double, Double, Double>>();

    	//                                                                      lat1,       lon1,          lat2,       lon2,           angle
    	tuples.add(new Quintuple<Double, Double, Double, Double, Double>(        0.0,        0.0,           0.0,        0.0,           0.0));

    	tuples.add(new Quintuple<Double, Double, Double, Double, Double>(        0.0,        0.0,     Math.PI/2,        0.0,     Math.PI/2));
    	
    	tuples.add(new Quintuple<Double, Double, Double, Double, Double>(        0.0,        0.0,     Math.PI/2,        0.0,     Math.PI/2));
    	tuples.add(new Quintuple<Double, Double, Double, Double, Double>(        0.0,        0.0,    -Math.PI/2,        0.0,     Math.PI/2));
    	tuples.add(new Quintuple<Double, Double, Double, Double, Double>(  Math.PI/2,        0.0,    -Math.PI/2,        0.0,     Math.PI  ));
    	tuples.add(new Quintuple<Double, Double, Double, Double, Double>( -Math.PI/2,        0.0,     Math.PI/2,        0.0,     Math.PI  ));
    	
    	tuples.add(new Quintuple<Double, Double, Double, Double, Double>(        0.0,  Math.PI/8,           0.0,        0.0,     Math.PI/8));
    	tuples.add(new Quintuple<Double, Double, Double, Double, Double>(        0.0,  Math.PI/4,           0.0,        0.0,     Math.PI/4));
    	tuples.add(new Quintuple<Double, Double, Double, Double, Double>(        0.0,  Math.PI/2,           0.0,        0.0,     Math.PI/2));
    	tuples.add(new Quintuple<Double, Double, Double, Double, Double>(        0.0,  Math.PI  ,           0.0,        0.0,     Math.PI  ));
    	
    	tuples.add(new Quintuple<Double, Double, Double, Double, Double>(        0.0, -Math.PI/8,           0.0,        0.0,     Math.PI/8));
    	tuples.add(new Quintuple<Double, Double, Double, Double, Double>(        0.0, -Math.PI/4,           0.0,        0.0,     Math.PI/4));
    	tuples.add(new Quintuple<Double, Double, Double, Double, Double>(        0.0, -Math.PI/2,           0.0,        0.0,     Math.PI/2));
    	tuples.add(new Quintuple<Double, Double, Double, Double, Double>(        0.0, -Math.PI  ,           0.0,        0.0,     Math.PI  ));
    	
    	tuples.add(new Quintuple<Double, Double, Double, Double, Double>(  Math.PI/8,        0.0,     Math.PI/4,        0.0,     Math.PI/8));
    	tuples.add(new Quintuple<Double, Double, Double, Double, Double>(  Math.PI/8,        0.0,     Math.PI/2,        0.0,   3*Math.PI/8));
    	tuples.add(new Quintuple<Double, Double, Double, Double, Double>(  Math.PI/8,        0.0,     Math.PI  ,        0.0,   7*Math.PI/8));
    	
    	tuples.add(new Quintuple<Double, Double, Double, Double, Double>( -Math.PI/8,        0.0,     Math.PI/4,        0.0,   3*Math.PI/8));
    	tuples.add(new Quintuple<Double, Double, Double, Double, Double>( -Math.PI/8,        0.0,     Math.PI/2,        0.0,   5*Math.PI/8));
    	
    	tuples.add(new Quintuple<Double, Double, Double, Double, Double>(        0.0,  Math.PI/2,           0.0, -Math.PI/2,     Math.PI  ));

    	// Out-of-range Cases
    	tuples.add(new Quintuple<Double, Double, Double, Double, Double>(  3*Math.PI,        0.0,           0.0,        0.0,     Math.PI  ));
    	tuples.add(new Quintuple<Double, Double, Double, Double, Double>(        0.0,  3*Math.PI,           0.0,        0.0,     Math.PI  ));
    	tuples.add(new Quintuple<Double, Double, Double, Double, Double>(        0.0,        0.0,     3*Math.PI,        0.0,     Math.PI  ));
    	tuples.add(new Quintuple<Double, Double, Double, Double, Double>(        0.0,        0.0,           0.0,  3*Math.PI,     Math.PI  ));
    	tuples.add(new Quintuple<Double, Double, Double, Double, Double>(  4*Math.PI,        0.0,           0.0,        0.0,         0.0  ));
    	tuples.add(new Quintuple<Double, Double, Double, Double, Double>(        0.0,  4*Math.PI,           0.0,        0.0,         0.0  ));
    	tuples.add(new Quintuple<Double, Double, Double, Double, Double>(        0.0,        0.0,     4*Math.PI,        0.0,         0.0  ));
    	tuples.add(new Quintuple<Double, Double, Double, Double, Double>(        0.0,        0.0,           0.0,  4*Math.PI,         0.0  ));

    	for (Quintuple<Double, Double, Double, Double, Double> tuple : tuples)
    	{
    		double latRad1 = Tuple.first(tuple);
    		double lonRad1 = Tuple.second(tuple);
    		double latRad2 = Tuple.third(tuple);
    		double lonRad2 = Tuple.fourth(tuple);
    		double expectedAngle = Tuple.fifth(tuple);
    		
			double computedAngle = SphericalUtilities.angle(latRad1, lonRad1, latRad2, lonRad2);

			boolean pass = Mathematics.equals(expectedAngle, computedAngle);
			System.out.println("Spherical angle between (" + latRad1 + ", " + lonRad1 + ") and (" + latRad2 + ", " + lonRad2 + ") should be " + expectedAngle + "... it is " + computedAngle + "..." + (pass ? "PASS":"FAIL"));
			Assert.assertTrue(pass);
    	}
	}

	@Test
	public void gcInteriorAngle()
	{
		System.out.println("================================================================================");
		System.out.println("Unit tests for SphericalUtilities.gcInteriorAngle():");

    	Vector3D fPole = new Vector3D( 1,  0,  0);
    	Vector3D rPole = new Vector3D(-1,  0,  0);
    	Vector3D nPole = new Vector3D( 0,  0,  1);
    	Vector3D sPole = new Vector3D( 0,  0, -1);
    	Vector3D ePole = new Vector3D( 0,  1,  0);
    	Vector3D wPole = new Vector3D( 0, -1,  1);
    	
    	ArrayList<Quintuple<Vector3D, Vector3D, Vector3D, Boolean, Double>> tuples = new ArrayList<Quintuple<Vector3D, Vector3D, Vector3D, Boolean, Double>>();
        //                                                                                                          expect      expected
    	//                                                                               v1,       v2,       v3,    exception?  answer
    	tuples.add(new Quintuple<Vector3D, Vector3D, Vector3D, Boolean, Double>(      fPole,    nPole,    wPole,    false,      Math.PI/2));
    	tuples.add(new Quintuple<Vector3D, Vector3D, Vector3D, Boolean, Double>(      fPole,    nPole,    ePole,    false,    3*Math.PI/2));
    	tuples.add(new Quintuple<Vector3D, Vector3D, Vector3D, Boolean, Double>(      fPole,    nPole,    fPole,    false,         0.0   ));
    	tuples.add(new Quintuple<Vector3D, Vector3D, Vector3D, Boolean, Double>(      rPole,    sPole,    fPole,    false,      Math.PI  ));

    	tuples.add(new Quintuple<Vector3D, Vector3D, Vector3D, Boolean, Double>(      rPole,    rPole,    fPole,    true,       null  ));

    	for (Quintuple<Vector3D, Vector3D, Vector3D, Boolean, Double> tuple : tuples)
    	{
    		Vector3D v1 = Tuple.first(tuple);
    		Vector3D v2 = Tuple.second(tuple);
    		Vector3D v3 = Tuple.third(tuple);
    		Boolean exceptionExpected = Tuple.fourth(tuple);
    		Double expectedAngle = Tuple.fifth(tuple);
    		
    		boolean exceptionCaught = false;
			Double computedAngle = null;
			try
			{
				computedAngle = SphericalUtilities.gcInteriorAngle(v1, v2, v3);
			}
			catch (InvalidInputException e)
			{
				exceptionCaught = true;
			}

			boolean pass = exceptionCaught == exceptionExpected;
			Assert.assertTrue(pass);

			if (!exceptionCaught)
			{
				pass = Mathematics.equals(expectedAngle, computedAngle);				
			}
			
			System.out.println("Spherical interior angle between v1: "+v1+", v2:"+v2+", v3:"+v3+" should be " + (exceptionExpected?"exception":expectedAngle) + "... it is " + (exceptionCaught?"exception":computedAngle) + "..." + (pass ? "PASS":"FAIL"));
			Assert.assertTrue(pass);
    	}
	}

	@Test
	public void isLeftRightOn()
	{
		System.out.println("================================================================================");
		System.out.println("Unit tests for SphericalUtilities.isLeftRightOn():");

    	class Answers
    	{
    		public Boolean isLeft;
    		public boolean leftException;
    		public Boolean isRight;
    		public boolean rightException;
    		public Boolean isOn;
    		public boolean onException;
    		public boolean leftOrOnException;    		
    		public boolean rightOrOnException;
    		
    		public Answers(Boolean isLeft, boolean leftException, Boolean isRight, boolean rightException, Boolean isOn, boolean onException, boolean leftOrOnException, boolean rightOrOnException)
    		{
    			this.isLeft = isLeft;
    			this.leftException = leftException;
    			this.isRight = isRight;
    			this.rightException = rightException;
    			this.isOn = isOn;
    			this.onException = onException;
    			this.leftOrOnException = leftOrOnException;
    			this.rightOrOnException = rightOrOnException;
    		}
    	}
    	
    	// Define the points for the great circle...
		final Vector3D unitX  = new Vector3D( 1,  0,  0);
		final Vector3D unitZ  = new Vector3D( 0,  0,  1);
		final double sqrt2over2 = Math.sqrt(2.0)/2;
		
		// Define test points that are to the left of great circle x->z
		Vector<Pair<Vector3D, Answers>> testPoints = new Vector<Pair<Vector3D, Answers>>();
		testPoints.add( new Pair<Vector3D, Answers>(new Vector3D( 1,  0,  0),                 new Answers(false, false,     false, false,      true, false,       false, false)));
		testPoints.add( new Pair<Vector3D, Answers>(new Vector3D( 0,  0,  1),                 new Answers(false, false,     false, false,      true,  true,       false, false)));
		testPoints.add( new Pair<Vector3D, Answers>(new Vector3D( 0, -1,  0),                 new Answers( true, false,     false, false,     false, false,       false, false)));
		testPoints.add( new Pair<Vector3D, Answers>(new Vector3D(-1, -2, -3).normalize(),     new Answers( true, false,     false, false,     false, false,       false, false)));
		testPoints.add( new Pair<Vector3D, Answers>(new Vector3D( 0,  1,  0),                 new Answers(false, false,      true, false,     false, false,       false, false)));
		testPoints.add( new Pair<Vector3D, Answers>(new Vector3D( 1,  2,  3).normalize(),     new Answers(false, false,      true, false,     false, false,       false, false)));
		testPoints.add( new Pair<Vector3D, Answers>(new Vector3D(-1,  0,  0),                 new Answers(false, false,     false, false,      true, false,       false, false)));
		testPoints.add( new Pair<Vector3D, Answers>(new Vector3D(sqrt2over2, 0, -sqrt2over2), new Answers(false, false,     false, false,      true, false,       false, false)));
		testPoints.add( new Pair<Vector3D, Answers>(new Vector3D(sqrt2over2, 0,  sqrt2over2), new Answers(false, false,     false, false,      true, false,       false, false)));

		//------------------------------
		// isLeft() tests...
		//------------------------------
		System.out.println("Testing points in relation to great circle: " + unitX + " -> " + unitZ + "...");
		System.out.println("--------------------------------------------------------------------------------");
		for (Pair<Vector3D, Answers> testPoint : testPoints)
		{
			Vector3D vector = Tuple.first(testPoint);
			Answers answers = Tuple.second(testPoint);
			boolean left  = answers.isLeft;
			boolean leftException = answers.leftException;
			boolean right = answers.isRight;
			boolean rightException = answers.rightException;
			boolean on    = answers.isOn;
			boolean onException = answers.onException;
			boolean leftOrOn  = answers.isLeft || answers.isOn;
			boolean rightOrOn = answers.isRight || answers.isOn;
			boolean leftOrOnException = answers.leftOrOnException;
			boolean rightOrOnException = answers.rightOrOnException;

			System.out.println("Testing " + vector + "...");

			//--------------------
			// isLeft()
			boolean isLeft = false;
			boolean isLeftExceptionCaught = false;
			try { isLeft = SphericalUtilities.isLeft(unitX, unitZ, vector); }
			catch (SphericalUtilities.InvalidInputException e) { isLeftExceptionCaught = true; }

			boolean pass = (isLeftExceptionCaught == leftException);
			if (!leftException) pass = isLeft == left;
			System.out.println("   isLeft("+unitX+", "+unitZ+", "+vector+") should return " + (leftException?"EXCEPTION":left) + ". It returned " + (isLeftExceptionCaught?"EXCEPTION":isLeft) + "..." + (pass ? "PASS":"FAIL"));
			Assert.assertTrue(pass);
			
			//--------------------
			// isRight()
			boolean isRight = false;
			boolean isRightExceptionCaught = false;
			try { isRight = SphericalUtilities.isRight(unitX, unitZ, vector); }
			catch (SphericalUtilities.InvalidInputException e) { isRightExceptionCaught = true; }

			pass = (isRightExceptionCaught == rightException);
			if (!rightException) pass = isRight == right;
			System.out.println("   isRight("+unitX+", "+unitZ+", "+vector+") should return " + (rightException?"EXCEPTION":right) + ". It returned " + (isRightExceptionCaught?"EXCEPTION":isRight) + "..." + (pass ? "PASS":"FAIL"));
			Assert.assertTrue(pass);

			//--------------------
			// isOn()
			boolean isOn = false;
			boolean isOnExceptionCaught = false;
			try { isOn = SphericalUtilities.isOn(unitX, unitZ, vector); }
			catch (SphericalUtilities.InvalidInputException e) { isOnExceptionCaught = true; }
			
			pass = (isOnExceptionCaught == onException);
			if (!onException) pass = isOn == on;
			System.out.println("   isOn("+unitX+", "+unitZ+", "+vector+") should return " + (onException?"EXCEPTION":on) + ". It returned " + (isOnExceptionCaught?"EXCEPTION":isOn) + "..." + (pass ? "PASS":"FAIL"));
			Assert.assertTrue(pass);

			//--------------------
			// isLeftOrOn()
			boolean isLeftOrOn = false;
			boolean isLeftOrOnExceptionCaught = false;
			try { isLeftOrOn = SphericalUtilities.isLeftOrOn(unitX, unitZ, vector); }
			catch (SphericalUtilities.InvalidInputException e) { isLeftOrOnExceptionCaught = true; }
			
			pass = (isLeftOrOnExceptionCaught == leftOrOnException);
			if (!leftOrOnException) pass = isLeftOrOn == leftOrOn;
			System.out.println("   isLeftOrOn("+unitX+", "+unitZ+", "+vector+") should return " + (leftOrOnException?"EXCEPTION":leftOrOn) + ". It returned " + (isLeftOrOnExceptionCaught?"EXCEPTION":isLeftOrOn) + "..." + (pass ? "PASS":"FAIL"));
			Assert.assertTrue(pass);
						
			//--------------------
			// isRightOrOn()
			boolean isRightOrOn = false;
			boolean isRightOrOnExceptionCaught = false;
			try { isRightOrOn = SphericalUtilities.isRightOrOn(unitX, unitZ, vector); }
			catch (SphericalUtilities.InvalidInputException e) { isRightOrOnExceptionCaught = true; }
			
			pass = (isRightOrOnExceptionCaught == rightOrOnException);
			if (!rightOrOnException) pass = isRightOrOn == rightOrOn;
			System.out.println("   isRightOrOn("+unitX+", "+unitZ+", "+vector+") should return " + (rightOrOnException?"EXCEPTION":rightOrOn) + ". It returned " + (isRightOrOnExceptionCaught?"EXCEPTION":isRightOrOn) + "..." + (pass ? "PASS":"FAIL"));
			Assert.assertTrue(pass);
		}
	}

	@Test
	public void latLonToVectorToLatLon()
	{
		System.out.println("================================================================================");
		System.out.println("Unit tests for SphericalUtilities.latLonToVectorToLatLon():");

    	for (double lat=-Math.PI/2; lat<=Math.PI/2; lat+=Math.PI/4)
    	{
        	for (double lon=-Math.PI; lon<=Math.PI; lon+=Math.PI/4)
        	{
        		Vector3D v = SphericalUtilities.latLonToVector(lat, lon);
        		double[] latLon = SphericalUtilities.vectorToLatLon(v);
        		
				boolean pass = Mathematics.equals(latLon[0], lat) && Mathematics.equals(latLon[1], lon);
				System.out.println("lat/lon: ("+lat+", "+lon+") -> vector "+v+" -> lat/lon: (" + latLon[0]+ ", " + latLon[1] + ")..." + (pass ? "PASS":"FAIL"));
				Assert.assertTrue(pass);
        	}
    	}
	}

	@Test
	public void vectorToLatLon()
	{
		System.out.println("================================================================================");
		System.out.println("Unit tests for SphericalUtilities.vectorToLatLon():");

    	Vector3D nPole = new Vector3D( 0,  0,  1);
    	Vector3D sPole = new Vector3D( 0,  0, -1);
    	Vector3D ePole = new Vector3D( 0,  1,  0);
    	Vector3D wPole = new Vector3D( 0, -1,  0);
    	Vector3D fPole = new Vector3D( 1,  0,  0);
    	Vector3D rPole = new Vector3D(-1,  0,  0);
    	
    	ArrayList<Quadruple<String, Vector3D, Double, Double>> tuples = new ArrayList<Quadruple<String, Vector3D, Double, Double>>();
    	//                                                             name,      v,       latRad,       lonRad
    	tuples.add(new Quadruple<String, Vector3D, Double, Double>( "nPole",  nPole,    Math.PI/2,          0.0));
    	tuples.add(new Quadruple<String, Vector3D, Double, Double>( "sPole",  sPole,   -Math.PI/2,          0.0));
    	tuples.add(new Quadruple<String, Vector3D, Double, Double>( "ePole",  ePole,          0.0,    Math.PI/2));
    	tuples.add(new Quadruple<String, Vector3D, Double, Double>( "wPole",  wPole,          0.0,   -Math.PI/2));
    	tuples.add(new Quadruple<String, Vector3D, Double, Double>( "fPole",  fPole,          0.0,          0.0));
    	tuples.add(new Quadruple<String, Vector3D, Double, Double>( "rPole",  rPole,          0.0,      Math.PI));

    	for (Quadruple<String, Vector3D, Double, Double> tuple : tuples)
    	{
    		String name = Tuple.first(tuple);
    		Vector3D vector = Tuple.second(tuple);
    		double latRad = Tuple.third(tuple);
    		double lonRad = Tuple.fourth(tuple);

    		double[] latLon = SphericalUtilities.vectorToLatLon(vector);
    		// TODO:  Fix the conditional below, as the Mathematics.equals is called twice for the same test
			boolean pass = Mathematics.equals(latLon[0], latRad); // && Mathematics.equals(latLon[0], latRad);
			
			System.out.println("Vector "+name+" "+vector+" should be (" + latRad + ", " + lonRad + ") in radians. It is ("+latLon[0]+", "+latLon[1]+")... " + (pass ? "PASS":"FAIL"));
			Assert.assertTrue(pass);
    	}
	}

	@Test
	public void latLonToVector()
	{
		System.out.println("================================================================================");
		System.out.println("Unit tests for SphericalUtilities.latLonToVector():");

    	Vector3D nPole = new Vector3D( 0,  0,  1);
    	Vector3D sPole = new Vector3D( 0,  0, -1);
    	Vector3D ePole = new Vector3D( 0,  1,  0);
    	Vector3D wPole = new Vector3D( 0, -1,  0);
    	Vector3D fPole = new Vector3D( 1,  0,  0);
    	Vector3D rPole = new Vector3D(-1,  0,  0);
    	
    	ArrayList<Quadruple<String, Double, Double, Vector3D>> tuples = new ArrayList<Quadruple<String, Double, Double, Vector3D>>();
    	//                                                             name,      latRad,      lonRad,   vector
    	tuples.add(new Quadruple<String, Double, Double, Vector3D>( "nPole",   Math.PI/2,         0.0,   nPole));
    	tuples.add(new Quadruple<String, Double, Double, Vector3D>( "sPole",  -Math.PI/2,         0.0,   sPole));
    	tuples.add(new Quadruple<String, Double, Double, Vector3D>( "ePole",         0.0,   Math.PI/2,   ePole));
    	tuples.add(new Quadruple<String, Double, Double, Vector3D>( "wPole",         0.0,  -Math.PI/2,   wPole));
    	tuples.add(new Quadruple<String, Double, Double, Vector3D>( "fPole",         0.0,         0.0,   fPole));
    	tuples.add(new Quadruple<String, Double, Double, Vector3D>( "rPole",         0.0,     Math.PI,   rPole));

    	for (Quadruple<String, Double, Double, Vector3D> tuple : tuples)
    	{
    		String name = Tuple.first(tuple);
    		double latRad = Tuple.second(tuple);
    		double lonRad = Tuple.third(tuple);
    		Vector3D expectedVector = Tuple.fourth(tuple);

    		Vector3D computedVector = SphericalUtilities.latLonToVector(latRad, lonRad);
			boolean pass = Vector3D.equals(expectedVector, computedVector);
			
			System.out.println(name + ": (" + latRad + ", " + lonRad + ") in radians. Should be equal to "+expectedVector+". It is "+computedVector+"... " + (pass ? "PASS":"FAIL"));
			Assert.assertTrue(pass);
    	}
	}

	@Test
	public void rotate()
	{
		System.out.println("================================================================================");
		System.out.println("Unit tests for SphericalUtilities.rotate():");
    	boolean pass = true;
		
		final Vector3D unitX  = new Vector3D( 1,  0,  0);
		final Vector3D unitY  = new Vector3D( 0,  1,  0);
		final Vector3D unitZ  = new Vector3D( 0,  0,  1);
		final double rightAngle = Math.PI/2;
		
		Vector3D v;
		Vector3D vPrime;
		Vector3D u;
		double a;
		
		v = unitX; u = unitZ; a = rightAngle; vPrime = SphericalUtilities.rotate(v, u, a);
    	pass = vPrime.equals(unitY);
		System.out.println("Transform x-axis into y-axis: SphericalUtilities.rotate( " + v + ", " + u + ", " + a + " ) = " + vPrime + "... " + (pass ? "PASS":"FAIL"));
		Assert.assertTrue(pass);
		
		v = unitY; u = unitZ; a = -rightAngle; vPrime = SphericalUtilities.rotate(v, u, a);
    	pass = vPrime.equals(unitX);
		System.out.println("Transform y-axis into x-axis: SphericalUtilities.rotate( " + v + ", " + u + ", " + a + " ) = " + vPrime + "... " + (pass ? "PASS":"FAIL"));
		Assert.assertTrue(pass);
		
		v = unitX; u = unitY; a = -rightAngle; vPrime = SphericalUtilities.rotate(v, u, a);
    	pass = vPrime.equals(unitZ);
		System.out.println("Transform x-axis into z-axis: SphericalUtilities.rotate( " + v + ", " + u + ", " + a + " ) = " + vPrime + "... " + (pass ? "PASS":"FAIL"));
		Assert.assertTrue(pass);
		
		v = vPrime; u = unitY; a = rightAngle; vPrime = SphericalUtilities.rotate(v, u, a);
    	pass = vPrime.equals(unitX);
		System.out.println("Transform z-axis into x-axis: SphericalUtilities.rotate( " + v + ", " + u + ", " + a + " ) = " + vPrime + "... " + (pass ? "PASS":"FAIL"));
		Assert.assertTrue(pass);

		v = unitY; u = unitX; a = rightAngle; vPrime = SphericalUtilities.rotate(v, u, a);
    	pass = vPrime.equals(unitZ);
		System.out.println("Transform y-axis into z-axis: SphericalUtilities.rotate( " + v + ", " + u + ", " + a + " ) = " + vPrime + "... " + (pass ? "PASS":"FAIL"));
		Assert.assertTrue(pass);
		
		v = vPrime; u = unitX; a = -rightAngle; vPrime = SphericalUtilities.rotate(v, u, a);
    	pass = vPrime.equals(unitY);
		System.out.println("Transform z-axis into y-axis: SphericalUtilities.rotate( " + v + ", " + u + ", " + a + " ) = " + vPrime + "... " + (pass ? "PASS":"FAIL"));
		Assert.assertTrue(pass);

		
		
		int numIncrements = 8; 		a = (Math.PI*2) / numIncrements;		double sqrt2o2 = Math.sqrt(2.0)/2;
		//----------------------------------------
		// Testing xy planar rotations...
		System.out.println("Rotating the x-axis around the z-axis in " + numIncrements + " increments:");
		v = unitX;		u = unitZ;
		
		Vector<Vector3D> xyVectors = new Vector<Vector3D>(numIncrements);
		xyVectors.add(new Vector3D(  sqrt2o2,  sqrt2o2, 0));
		xyVectors.add(new Vector3D(        0,        1, 0));
		xyVectors.add(new Vector3D( -sqrt2o2,  sqrt2o2, 0));
		xyVectors.add(new Vector3D(       -1,        0, 0));
		xyVectors.add(new Vector3D( -sqrt2o2, -sqrt2o2, 0));
		xyVectors.add(new Vector3D(        0,       -1, 0));
		xyVectors.add(new Vector3D(  sqrt2o2, -sqrt2o2, 0));
		xyVectors.add(new Vector3D(        1,        0, 0));
		for (int i=0; i<numIncrements; i++)
		{
			vPrime = SphericalUtilities.rotate(v, u, a);
	    	pass = vPrime.equals(xyVectors.get(i));
			System.out.println("     " + i + ": SphericalUtilities.rotate( " + v + ", " + u + ", " + a + " ) = " + vPrime + "... " + (pass ? "PASS":"FAIL"));
			v = vPrime;
			Assert.assertTrue(pass);
		}
		
		//----------------------------------------
		// Test yz planar rotations...
		System.out.println("Rotating the y-axis around the x-axis in " + numIncrements + " increments:");
		v = unitY;		u = unitX;

		Vector<Vector3D> yzVectors = new Vector<Vector3D>(numIncrements);
		yzVectors.add(new Vector3D( 0,   sqrt2o2,  sqrt2o2));
		yzVectors.add(new Vector3D( 0,         0,        1));
		yzVectors.add(new Vector3D( 0,  -sqrt2o2,  sqrt2o2));
		yzVectors.add(new Vector3D( 0,        -1,        0));
		yzVectors.add(new Vector3D( 0,  -sqrt2o2, -sqrt2o2));
		yzVectors.add(new Vector3D( 0,         0,       -1));
		yzVectors.add(new Vector3D( 0,   sqrt2o2, -sqrt2o2));
		yzVectors.add(new Vector3D( 0,         1,        0));
		for (int i=0; i<numIncrements; i++)
		{
			vPrime = SphericalUtilities.rotate(v, u, a);
	    	pass = vPrime.equals(yzVectors.get(i));
			System.out.println("     " + i + ": SphericalUtilities.rotate( " + v + ", " + u + ", " + a + " ) = " + vPrime + "... " + (pass ? "PASS":"FAIL"));
			v = vPrime;
			Assert.assertTrue(pass);
		}
				
		//----------------------------------------
		// Testing xz planar rotations...
		System.out.println("Rotating the x-axis around the y-axis in " + numIncrements + " increments:");
		v = unitX;		u = unitY;
		
		Vector<Vector3D> xzVectors = new Vector<Vector3D>(numIncrements);
		xzVectors.add(new Vector3D(  sqrt2o2,  0, -sqrt2o2));
		xzVectors.add(new Vector3D(        0,  0,       -1));
		xzVectors.add(new Vector3D( -sqrt2o2,  0, -sqrt2o2));
		xzVectors.add(new Vector3D(       -1,  0,        0));
		xzVectors.add(new Vector3D( -sqrt2o2,  0,  sqrt2o2));
		xzVectors.add(new Vector3D(        0,  0,        1));
		xzVectors.add(new Vector3D(  sqrt2o2,  0,  sqrt2o2));
		xzVectors.add(new Vector3D(        1,  0,        0));
		for (int i=0; i<numIncrements; i++)
		{
			vPrime = SphericalUtilities.rotate(v, u, a);
	    	pass = vPrime.equals(xzVectors.get(i));
			System.out.println("     " + i + ": SphericalUtilities.rotate( " + v + ", " + u + ", " + a + " ) = " + vPrime + "... " + (pass ? "PASS":"FAIL"));
			v = vPrime;
			Assert.assertTrue(pass);
		}
	}

	public static void main(String[] args)
	{
		org.junit.runner.JUnitCore.runClasses(SphericalUtilitiesUnitTests.class);
	}
}