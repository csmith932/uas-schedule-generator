/**
 * Copyright "TBD", Metron Aviation & CSSI.  All rights reserved.
 * 
 * This computer Software was developed with the sponsorship of the U.S. Government
 * under Contract No. DTFAWA-10-D-00033, which has a copyright license in accordance with AMS 3.5-13.(c)(1).
 */

package gov.faa.ang.swac.common.geometry;

import gov.faa.ang.swac.common.datatypes.Vector3D;
import gov.faa.ang.swac.common.utilities.Mathematics;

import java.util.Vector;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * This class of utility functions is meant to make Great Circle (GC) calculations very simple.
 * 
 * CONVENTIONS:
 * - These algorithms assume the Earth to be a perfect sphere.
 *   (The equatorial radius is actually about 30km greater than the Polar.)
 * - East Longitude is positive, West Longitude is negative
 * - Polygons on the surface of a sphere have their points defined in a counter-clockwise direction.
 *   
 * @author Jason Femino - CSSI, Inc.
 */
public final class GCUtilitiesTest
{
	private static Logger logger = LogManager.getLogger(GCUtilitiesTest.class);
	
	@Before
	public void setup()
	{
		// By default, swac-parent pom's surefire plugin configuration specifies
		// swac-data/src/test/resources/log4j-junit.xml to be logging configuration file, and that file logs levels
		// ERROR or higher.
		// Uncomment to manually setup logger to run locally 
		// BasicConfigurator.configure();
	}
	
	@Test
	public void gcInteriorAngle()
	{
		logger.info("================================================================================");
		logger.info("Unit tests for GCUtilities.gcInteriorAngle():");
    	boolean pass = true;
    	Vector3D z, u, v;
    	double angle = 0;
    	boolean exceptionCaught = false;
	
		final Vector3D unitX  = new Vector3D( 1,  0,  0);
		final Vector3D unitY  = new Vector3D( 0,  1,  0);
		final Vector3D unitZ  = new Vector3D( 0,  0,  1);
		final Vector3D unitZp = new Vector3D( 0,  0, -1);
		final double sqrt2over2 = Math.sqrt(2.0)/2;

		double expectedAnswer = 3*Math.PI/2.0f;
		u = unitX; z = unitZ; v = unitY;
		exceptionCaught = false;
		try { angle = SphericalUtilities.gcInteriorAngle(u, z, v); } catch (SphericalUtilities.InvalidInputException e) { exceptionCaught = true; }
		pass = exceptionCaught == false && Mathematics.equals(angle, expectedAnswer);
		Assert.assertTrue(pass);
		logger.info("gcAngle( " + u + ", " + z + ", " + v + " ) should be " + expectedAnswer + ". It is " + (exceptionCaught?"EXCEPTION":angle) + " ..." +(pass ? "PASS":"FAIL"));

		expectedAnswer = Math.PI/2.0f;
		u = unitX; z = unitZp; v = unitY;
		exceptionCaught = false;
		try { angle = SphericalUtilities.gcInteriorAngle(u, z, v); } catch (SphericalUtilities.InvalidInputException e) { exceptionCaught = true; }
		pass = exceptionCaught == false && Mathematics.equals(angle, expectedAnswer);
		Assert.assertTrue(pass);
		logger.info("gcAngle( " + u + ", " + z + ", " + v + " ) should be " + expectedAnswer + ". It is " + (exceptionCaught?"EXCEPTION":angle) + " ..." +(pass ? "PASS":"FAIL"));

		//----------------------------------------
		// Test xz planar rotations...
		final int numIncrements = 8;
		logger.info("Rotating the x-axis around the y-axis in " + numIncrements + " increments:");
		Vector<Vector3D> xzVectors = new Vector<Vector3D>(numIncrements);
		xzVectors.add(new Vector3D(           1,  0,           0));
		xzVectors.add(new Vector3D(  sqrt2over2,  0,  sqrt2over2));
		xzVectors.add(new Vector3D(           0,  0,           1));
		xzVectors.add(new Vector3D( -sqrt2over2,  0,  sqrt2over2));
		xzVectors.add(new Vector3D(          -1,  0,           0));
		xzVectors.add(new Vector3D( -sqrt2over2,  0, -sqrt2over2));
		xzVectors.add(new Vector3D(           0,  0,          -1));
		xzVectors.add(new Vector3D(  sqrt2over2,  0, -sqrt2over2));
		for (int i=0; i<numIncrements; i++)
		{
			u = unitX; z = unitY; v = xzVectors.get(i);
			exceptionCaught = false;
			try {  angle = SphericalUtilities.gcInteriorAngle(u, z, v); } catch (SphericalUtilities.InvalidInputException e) { exceptionCaught = true; }
			expectedAnswer = i*(2*Math.PI/numIncrements);
			pass = exceptionCaught == false && Mathematics.equals(angle, expectedAnswer);
			Assert.assertTrue(pass);
			logger.info("gcAngle( " + u + ", " + z + ", " + v + " ) should be " + expectedAnswer + ". It is " + (exceptionCaught?"EXCEPTION":angle) + " ..." +(pass ? "PASS":"FAIL"));
		}

		//----------------------------------------
		// Test special situations...
		u = unitY; z = unitX; v = unitY;
		exceptionCaught = false;
		try { angle = SphericalUtilities.gcInteriorAngle(u, z, v); } catch (SphericalUtilities.InvalidInputException e) { exceptionCaught = true; } // u & v are the same... so angle should be 0.
		pass = exceptionCaught == false && Mathematics.equals(angle, 0);
		Assert.assertTrue(pass);
		logger.info("gcAngle( " + u + ", " + z + ", " + v + " ) = " + (exceptionCaught?"EXCEPTION":angle) + "... " + (pass ? "PASS":"FAIL"));
		
		u = unitY; z = unitX; v = new Vector3D(sqrt2over2, sqrt2over2, 0);
		exceptionCaught = false;
		try { angle = SphericalUtilities.gcInteriorAngle(u, z, v); }
		catch (SphericalUtilities.InvalidInputException e) { exceptionCaught = true; } // u & v are on the same side of z... so angle should be 0
		pass = exceptionCaught == false && Mathematics.equals(angle, 0);
		Assert.assertTrue(pass);
		logger.info("gcAngle( " + u + ", " + z + ", " + v + " ) = " + (exceptionCaught?"EXCEPTION":angle) + "... " + (pass ? "PASS":"FAIL"));
	}

	@Test
	public void rotate()
	{
		logger.info("================================================================================");
		logger.info("Unit tests for GCUtilities.rotate():");
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
    	pass = Vector3D.equals(vPrime, unitY);
		Assert.assertTrue(pass);
		logger.info("Transform x-axis into y-axis: rotate( " + v + ", " + u + ", " + a + " ) = " + vPrime + "... " + (pass ? "PASS":"FAIL"));
		
		v = unitY; u = unitZ; a = -rightAngle; vPrime = SphericalUtilities.rotate(v, u, a);
    	pass = Vector3D.equals(vPrime, unitX);
		Assert.assertTrue(pass);
		logger.info("Transform y-axis into x-axis: rotate( " + v + ", " + u + ", " + a + " ) = " + vPrime + "... " + (pass ? "PASS":"FAIL"));
		
		v = unitX; u = unitY; a = -rightAngle; vPrime = SphericalUtilities.rotate(v, u, a);
    	pass = Vector3D.equals(vPrime, unitZ);
		Assert.assertTrue(pass);
		logger.info("Transform x-axis into z-axis: rotate( " + v + ", " + u + ", " + a + " ) = " + vPrime + "... " + (pass ? "PASS":"FAIL"));
		
		v = vPrime; u = unitY; a = rightAngle; vPrime = SphericalUtilities.rotate(v, u, a);
    	pass = Vector3D.equals(vPrime, unitX);
		Assert.assertTrue(pass);
		logger.info("Transform z-axis into x-axis: rotate( " + v + ", " + u + ", " + a + " ) = " + vPrime + "... " + (pass ? "PASS":"FAIL"));

		v = unitY; u = unitX; a = rightAngle; vPrime = SphericalUtilities.rotate(v, u, a);
    	pass = Vector3D.equals(vPrime, unitZ);
		Assert.assertTrue(pass);
		logger.info("Transform y-axis into z-axis: rotate( " + v + ", " + u + ", " + a + " ) = " + vPrime + "... " + (pass ? "PASS":"FAIL"));
		
		v = vPrime; u = unitX; a = -rightAngle; vPrime = SphericalUtilities.rotate(v, u, a);
    	pass = Vector3D.equals(vPrime, unitY);
		Assert.assertTrue(pass);
		logger.info("Transform z-axis into y-axis: rotate( " + v + ", " + u + ", " + a + " ) = " + vPrime + "... " + (pass ? "PASS":"FAIL"));

		
		
		int numIncrements = 8; 		a = (Math.PI*2) / numIncrements;		double sqrt2o2 = Math.sqrt(2.0)/2;
		//----------------------------------------
		// Testing xy planar rotations...
		logger.info("Rotating the x-axis around the z-axis in " + numIncrements + " increments:");
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
	    	pass = Vector3D.equals(vPrime, xyVectors.get(i));
	    	Assert.assertTrue(pass);
			logger.info("     " + i + ": rotate( " + v + ", " + u + ", " + a + " ) = " + vPrime + "... " + (pass ? "PASS":"FAIL"));
			v = vPrime;
		}
		
		//----------------------------------------
		// Test yz planar rotations...
		logger.info("Rotating the y-axis around the x-axis in " + numIncrements + " increments:");
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
	    	pass = Vector3D.equals(vPrime, yzVectors.get(i));
	    	Assert.assertTrue(pass);
			logger.info("     " + i + ": rotate( " + v + ", " + u + ", " + a + " ) = " + vPrime + "... " + (pass ? "PASS":"FAIL"));
			v = vPrime;
		}
				
		//----------------------------------------
		// Testing xz planar rotations...
		logger.info("Rotating the x-axis around the y-axis in " + numIncrements + " increments:");
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
	    	pass = Vector3D.equals(vPrime, xzVectors.get(i));
	    	Assert.assertTrue(pass);
			logger.info("     " + i + ": rotate( " + v + ", " + u + ", " + a + " ) = " + vPrime + "... " + (pass ? "PASS":"FAIL"));
			v = vPrime;
		}
	}
	
	public static void main(String[] args)
	{
		org.junit.runner.JUnitCore.runClasses(GCUtilitiesTest.class);
	}
}