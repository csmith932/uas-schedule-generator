package gov.faa.ang.swac.common.flightmodeling;
///**
// * Copyright "TBD", Metron Aviation & CSSI.  All rights reserved.
// * 
// * This computer Software was developed with the sponsorship of the U.S. Government
// * under Contract No. DTFAWA-10-D-00033, which has a copyright license in accordance with AMS 3.5-13.(c)(1).
// */
//
//package gov.faa.ang.swac.common.flightmodeling;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import org.junit.Test;
//import org.junit.Assert;
//
//public class WayTest
//{
//	public static Way dummy(int seed)
//	{
//		List<WayPoint> points = new ArrayList<WayPoint>();
//
//		// Create Way and fill with dummy data
//		for (int i=0; i<=seed; i++)
//		{
//			points.add(WayPointTest.dummy(seed + i));
//		}
//
//		Way way = new Way();
//		way.setWayPoints(points);
//		return way;
//	}
//	
//	@Test
//	public void toString_fromTextRecord_clone()
//	{
////		System.out.println("================================================================================");
////		System.out.println("Unit tests for Way.toString & Way.fromTextRecord() & Way.clone():");
////
////		for (int i=0; i<5; i++)
////		{
////			Way original = WayTest.dummy(i);                    // Create a dummy object
////			Way copy = Way.fromTextRecord(original.toString());  // Convert it to String and back
////			Way clone = original.clone();
////			boolean pass = original.equals(copy) && original.equals(clone) && original != clone && original.getClass() == clone.getClass();
////
////			System.out.println("--------------------------------------------------------------------------------");
////			System.out.println("Comparing:");
////			System.out.println(original.toString());
////			System.out.println(copy.toString());
////			System.out.println(clone.toString());
////			System.out.println((pass ? "PASS":"FAIL"));
////			Assert.assertTrue(pass);
////		}
//	}
//	
//	public static void main(String[] args)
//	{
//		org.junit.runner.JUnitCore.runClasses(WayTest.class);
//	}
//}