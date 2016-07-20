/**
 * Copyright "TBD", Metron Aviation & CSSI.  All rights reserved.
 * 
 * This computer Software was developed with the sponsorship of the U.S. Government
 * under Contract No. DTFAWA-10-D-00033, which has a copyright license in accordance with AMS 3.5-13.(c)(1).
 */

package gov.faa.ang.swac.common.geometry;


import gov.faa.ang.swac.common.datatypes.Latitude;
import gov.faa.ang.swac.common.datatypes.Longitude;
import gov.faa.ang.swac.common.geometry.SphericalUtilities.IntersectionType;

import java.util.Vector;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.mallardsoft.tuple.Quadruple;
import com.mallardsoft.tuple.Tuple;

public class GCEdgeTest
{
	private static Logger logger = LogManager.getLogger(GCEdgeTest.class);
	
	@Before
	public void setup()
	{
		// By default, swac-parent pom's surefire plugin configuration specifies
		// swac-data/src/test/resources/log4j-junit.xml to be logging configuration file, and that file logs levels
		// ERROR or higher.
		// Uncomment to manually setup logger for quick local run 
		// BasicConfigurator.configure();
	}
	
//	@Test
//	public void isLeft_isRight_isOn()
//	{
//		logger.info("================================================================================");
//		logger.info("Unit test for GCEdge.isLeft(), GCEdge.isRight(), GCEdge.isOn():");
//    	boolean pass = true;
//
//    	class Answers
//    	{
//    		public boolean isLeft;
//    		public boolean isRight;
//    		public boolean isOn;
//    		
//    		public Answers(boolean isLeft, boolean isRight, boolean isOn)
//    		{
//    			this.isLeft = isLeft;
//    			this.isRight = isRight;
//    			this.isOn = isOn;
//    		}
//    	}
//    	
//    	// Define the points for the great circle...
//		final GCEdge edge = new GCEdge(new GCPoint(new Vector3D( 1,  0,  0)), new GCPoint(new Vector3D( 0,  0,  1))/*, null*/);
//		final double sqrt2over2 = Math.sqrt(2.0)/2;
//		
//		// Define test points that are to the left of great circle x->z
//		Vector<Pair<GCPoint, Answers>> testPoints = new Vector<Pair<GCPoint, Answers>>();
//		testPoints.add( new Pair<GCPoint, Answers>(new GCPoint(new Vector3D( 0, -1,  0)),                 new Answers( true, false, false)));
//		testPoints.add( new Pair<GCPoint, Answers>(new GCPoint(new Vector3D(-1, -2, -3).normalize()),     new Answers( true, false, false)));
//		testPoints.add( new Pair<GCPoint, Answers>(new GCPoint(new Vector3D( 0,  1,  0)),                 new Answers(false,  true, false)));
//		testPoints.add( new Pair<GCPoint, Answers>(new GCPoint(new Vector3D( 1,  2,  3).normalize()),     new Answers(false,  true, false)));
//		testPoints.add( new Pair<GCPoint, Answers>(new GCPoint(new Vector3D(-1,  0,  0)),                 new Answers(false, false,  true)));
//		testPoints.add( new Pair<GCPoint, Answers>(new GCPoint(new Vector3D(sqrt2over2, 0, -sqrt2over2)), new Answers(false, false,  true)));
//		testPoints.add( new Pair<GCPoint, Answers>(new GCPoint(new Vector3D(sqrt2over2, 0,  sqrt2over2)), new Answers(false, false,  true)));
//
//		//------------------------------
//		// isLeft() tests...
//		//------------------------------
//		logger.info("Testing points in relation to great circle: " + edge.first() + " -> " + edge.second() + "...");
//		logger.info("--------------------------------------------------------------------------------");
//		for (Pair<GCPoint, Answers> testPoint : testPoints)
//		{
//			GCPoint point = Tuple.first(testPoint);
//			Answers answers = Tuple.second(testPoint);
//			boolean left  = answers.isLeft;
//			boolean right = answers.isRight;
//			boolean on    = answers.isOn;
//			boolean leftOrOn  = answers.isLeft || answers.isOn;
//			boolean rightOrOn = answers.isRight || answers.isOn;
//			
//			logger.info("Testing " + point + "...");
//
//			//--------------------
//			// isLeft()
//			boolean isLeft = false;
//			boolean isLeftException = false;
//			try { isLeft = edge.isLeft(point); } catch (InvalidInputException e) { isLeftException = true; }
//			
//			pass = (isLeftException == false && isLeft == left);
//			Assert.assertTrue(pass);
//			logger.info("   isLeft("+edge.first()+", "+edge.second()+", "+point+") should return " + left + ". It returned " + (isLeftException?"EXCEPTION":isLeft) + "..." + (pass ? "PASS":"FAIL"));
//			
//			//--------------------
//			// isRight()
//			boolean isRight = false;
//			boolean isRightException = false;
//			try { isRight = edge.isRight(point); } catch (InvalidInputException e) { isRightException = true; }
//			
//			pass = (isRightException == false && isRight == right);
//			Assert.assertTrue(pass);
//			logger.info("   isRight("+edge.first()+", "+edge.second()+", "+point+") should return " + right + ". It returned " + (isRightException?"EXCEPTION":isRight) + "..." + (pass ? "PASS":"FAIL"));
//			
//			//--------------------
//			// isOn()
//			boolean isOn = false;
//			boolean isOnException = false;
//			try { isOn = edge.isOn(point); } catch (InvalidInputException e) { isOnException = true; }
//			
//			pass = (isOnException == false && isOn == on);
//			Assert.assertTrue(pass);
//			logger.info("   isOn("+edge.first()+", "+edge.second()+", "+point+") should return " + on + ". It returned " + (isOnException?"EXCEPTION":isOn) + "..." + (pass ? "PASS":"FAIL"));			
//
//			//--------------------
//			// isLeftOrOn()
//			boolean isLeftOrOn = false;
//			boolean isLeftOrOnException = false;
//			try { isLeftOrOn = edge.isLeftOrOn(point); } catch (InvalidInputException e) { isLeftOrOnException = true; }
//			
//			pass = (isLeftOrOnException == false && isLeftOrOn == leftOrOn);
//			Assert.assertTrue(pass);
//			logger.info("   isLeftOrOn("+edge.first()+", "+edge.second()+", "+point+") should return " + leftOrOn + ". It returned " + (isLeftOrOnException?"EXCEPTION":isLeftOrOn) + "..." + (pass ? "PASS":"FAIL"));
//						
//			//--------------------
//			// isRightOrOn()
//			boolean isRightOrOn = false;
//			boolean isRightOrOnException = false;
//			try { isRightOrOn = edge.isRightOrOn(point); } catch (InvalidInputException e) { isRightOrOnException = true; }
//			
//			pass = (isRightOrOnException == false && isRightOrOn == rightOrOn);
//			Assert.assertTrue(pass);
//			logger.info("   isRightOrOn("+edge.first()+", "+edge.second()+", "+point+") should return " + rightOrOn + ". It returned " + (isRightOrOnException?"EXCEPTION":isRightOrOn) + "..." + (pass ? "PASS":"FAIL"));						
//		}
//	}

	@Test
	public void intersection()	 
	{
		logger.info("================================================================================");
		logger.info("Unit tests for GCEdge.intersection():");
    	boolean pass = true;

    	
    	GCPoint nPole = new GCPoint(Latitude.valueOfDegrees(90), Longitude.valueOfDegrees(0));
    	GCPoint sPole = new GCPoint(Latitude.valueOfDegrees(-90), Longitude.valueOfDegrees(0));
    	GCPoint e0 = new GCPoint(Latitude.valueOfDegrees(0), Longitude.valueOfDegrees(0));
    	GCPoint e1 = new GCPoint(Latitude.valueOfDegrees(0), Longitude.valueOfDegrees(45));
    	GCPoint e2 = new GCPoint(Latitude.valueOfDegrees(0), Longitude.valueOfDegrees(90));
    	//GCPoint e3 = new GCPoint(Latitude.valueOfDegrees(0), Longitude.valueOfDegrees(135));
    	//GCPoint e4 = new GCPoint(Latitude.valueOfDegrees(0), Longitude.valueOfDegrees(180));
    	//GCPoint e5 = new GCPoint(Latitude.valueOfDegrees(0), Longitude.valueOfDegrees(-135));
    	//GCPoint e6 = new GCPoint(Latitude.valueOfDegrees(0), Longitude.valueOfDegrees(-90));
    	//GCPoint e7 = new GCPoint(Latitude.valueOfDegrees(0), Longitude.valueOfDegrees(-45));

    	Vector<Quadruple<GCEdge, GCEdge, GCPoint, GCPoint>> edgesAndPoints = new Vector<Quadruple<GCEdge, GCEdge, GCPoint, GCPoint>>();
    	//                                                                                                               Strict        Non-strict 
    	//                                                                      GCEdge1             GCEdge2              Intersection  Intersection
    	edgesAndPoints.add( new Quadruple<GCEdge, GCEdge, GCPoint, GCPoint>(new GCEdge(e0, e1), new GCEdge(e1, sPole),   null,         e1) );
    	edgesAndPoints.add( new Quadruple<GCEdge, GCEdge, GCPoint, GCPoint>(new GCEdge(e0, e1), new GCEdge(e1,    e2),   null,         e1) );
    	edgesAndPoints.add( new Quadruple<GCEdge, GCEdge, GCPoint, GCPoint>(new GCEdge(e0, e2), new GCEdge(e2, nPole),   null,         e2) );

    	for (Quadruple<GCEdge, GCEdge, GCPoint, GCPoint> edgesAndPoint : edgesAndPoints)
    	{
    		GCEdge edge1 = Tuple.first(edgesAndPoint);
    		GCEdge edge2 = Tuple.second(edgesAndPoint);
    		GCPoint pStrict = Tuple.third(edgesAndPoint);
    		GCPoint pNonStrict = Tuple.fourth(edgesAndPoint);
    		GCPoint vStrict = null;
    		GCPoint vNonStrict = null;
    		
    		try
    		{
    			vStrict = edge1.intersection(edge2, IntersectionType.STRICT);
        		vNonStrict = edge1.intersection(edge2, IntersectionType.NONSTRICT);
    		}
    		catch (Exception e)
    		{
    			Assert.assertTrue(false); // Exception not expected, fail this test
    		}
    		
    		if (pStrict == null)
    		{    			
        		pass = vStrict == null;
    			Assert.assertTrue(pass);
    		}
    		else
    		{
	    		pass = pStrict.compareTo(vStrict) == 0;
				Assert.assertTrue(pass);
    		}
	    	logger.info("    Strict Intersection of " + edge1 + " and " + edge2 + " is: " + vStrict + ". Should be: " + pStrict + "... " + (pass ? "PASS":"FAIL"));
    		
    		if (pNonStrict == null)
    		{    			
        		pass = vNonStrict == null;
    			Assert.assertTrue(pass);
    		}
    		else
    		{
	    		pass = pNonStrict.compareTo(vNonStrict) == 0;
				Assert.assertTrue(pass);
    		}
	    	logger.info("Non-Strict Intersection of " + edge1 + " and " + edge2 + " is: " + vNonStrict + ". Should be: " + pNonStrict + "... " + (pass ? "PASS":"FAIL"));
	    	logger.info("");
    	}
	}

	public static void main(String[] args)
	{
		org.junit.runner.JUnitCore.runClasses(GCEdgeTest.class);
	}
}