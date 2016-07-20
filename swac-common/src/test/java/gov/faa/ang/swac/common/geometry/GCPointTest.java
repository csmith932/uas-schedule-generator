/**
 * Copyright "TBD", Metron Aviation & CSSI.  All rights reserved.
 * 
 * This computer Software was developed with the sponsorship of the U.S. Government
 * under Contract No. DTFAWA-10-D-00033, which has a copyright license in accordance with AMS 3.5-13.(c)(1).
 */

package gov.faa.ang.swac.common.geometry;

import gov.faa.ang.swac.common.datatypes.Latitude;
import gov.faa.ang.swac.common.datatypes.Longitude;
import gov.faa.ang.swac.common.datatypes.Vector3D;
import gov.faa.ang.swac.common.utilities.Mathematics;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.mallardsoft.tuple.Pair;
import com.mallardsoft.tuple.Triple;
import com.mallardsoft.tuple.Tuple;

public class GCPointTest
{
	private static Logger logger = LogManager.getLogger(GCPointTest.class);
	
	@Before
	public void setup()
	{
		// By default, swac-parent pom's surefire plugin configuration specifies
		// swac-data/src/test/resources/log4j-junit.xml to be logging configuration file, and that file logs levels
		// ERROR or higher.
		// Uncomment to manually setup logger to run locally 
		// BasicConfigurator.configure();
	}
	
	public static GCPoint dummy(int seed)
	{
		return new GCPoint(Latitude.valueOfDegrees(seed+1), Longitude.valueOfDegrees(seed+2));
	}
	
	@Test
	public void poles()
	{
		logger.info("================================================================================");
		logger.info("Unit tests to determine proper orientation of the coordinate system (the six \"poles\"):");
    	boolean pass = false;

		ArrayList<Triple<String, GCPoint, Vector3D>> poles = new ArrayList<Triple<String, GCPoint, Vector3D>>(); 
    	poles.add( new Triple<String, GCPoint, Vector3D>("nPole", new GCPoint(Latitude.valueOfDegrees( 90), Longitude.valueOfDegrees(   0)), new Vector3D( 0, 0, 1) ));
    	poles.add( new Triple<String, GCPoint, Vector3D>("sPole", new GCPoint(Latitude.valueOfDegrees(-90), Longitude.valueOfDegrees(   0)), new Vector3D( 0, 0,-1) ));
    	poles.add( new Triple<String, GCPoint, Vector3D>("ePole", new GCPoint(Latitude.valueOfDegrees(  0), Longitude.valueOfDegrees(  90)), new Vector3D( 0, 1, 0) ));
    	poles.add( new Triple<String, GCPoint, Vector3D>("wPole", new GCPoint(Latitude.valueOfDegrees(  0), Longitude.valueOfDegrees( -90)), new Vector3D( 0,-1, 0) ));
    	poles.add( new Triple<String, GCPoint, Vector3D>("fPole", new GCPoint(Latitude.valueOfDegrees(  0), Longitude.valueOfDegrees(   0)), new Vector3D( 1, 0, 0) ));
    	poles.add( new Triple<String, GCPoint, Vector3D>("rPole", new GCPoint(Latitude.valueOfDegrees(  0), Longitude.valueOfDegrees( 180)), new Vector3D(-1, 0, 0) ));
    	
    	for (Triple<String, GCPoint, Vector3D> pole : poles)
    	{
    		String name = Tuple.first(pole);
    		GCPoint point = Tuple.second(pole);
    		Vector3D vector = Tuple.third(pole);
    		pass = point.vector().equals(vector);
    		Assert.assertTrue(pass);
    		logger.info(name + " = GCPoint: " + point + " point.vector() = " + point.vector() + "... should be: " + vector + "... " + (pass ? "PASS":"FAIL"));
    	}
	}
	
	@Test
	public void GCPoint()
	{
		logger.info("================================================================================");
		logger.info("Unit tests for GCPoint(Latitude, Longitude):");
    	boolean pass = false;
		
    	List<Pair<Double, Double>> latLonPairs = new ArrayList<Pair<Double, Double>>();
    	latLonPairs.add(new Pair<Double, Double>(new Double(  0), new Double(  0)));
    	latLonPairs.add(new Pair<Double, Double>(new Double( 10), new Double( 10)));
    	latLonPairs.add(new Pair<Double, Double>(new Double( 45), new Double( 45)));
    	latLonPairs.add(new Pair<Double, Double>(new Double( 90), new Double( 90)));
    	latLonPairs.add(new Pair<Double, Double>(new Double(-10), new Double(-10)));
    	latLonPairs.add(new Pair<Double, Double>(new Double(-45), new Double(-45)));
    	latLonPairs.add(new Pair<Double, Double>(new Double(-90), new Double(-90)));

    	for (Pair<Double, Double> latLonPair : latLonPairs)
    	{
    		Latitude lat = Latitude.valueOfDegrees(Tuple.first(latLonPair));
    		Longitude lon = Longitude.valueOfDegrees(Tuple.second(latLonPair));
	    	GCPoint testPoint = new GCPoint(lat, lon);
	    	logger.info("testPoint = new GCPoint("+lat+", "+lon+")...");
	    	
	    	pass = Mathematics.equals(testPoint.latitude().degrees(), lat.degrees());
    		Assert.assertTrue(pass);
	    	logger.info("testPoint.lat() should equal " + lat + ". It is: " + testPoint.latitude() + " ..." + (pass ? "PASS":"FAIL"));
	    	
	    	pass = Mathematics.equals(testPoint.longitude().degrees(), lon.degrees());
    		Assert.assertTrue(pass);
	    	logger.info("testPoint.lon() should equal " + lon + ". It is: " + testPoint.longitude() + " ..." + (pass ? "PASS":"FAIL"));
	    	
	    	pass = Mathematics.equals(testPoint.vector().length(), 1);
    		Assert.assertTrue(pass);
	    	logger.info("testPoint.vector().length() should equal " + 1 + ". It is: " + testPoint.vector().length() + " ..." + (pass ? "PASS":"FAIL"));	    	
    	}
	}
	
	public static void main(String[] args)
	{
		org.junit.runner.JUnitCore.runClasses(GCEdgeTest.class);
	}
}