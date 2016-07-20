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
//import gov.faa.ang.swac.common.datatypes.Altitude;
//import gov.faa.ang.swac.common.datatypes.Latitude;
//import gov.faa.ang.swac.common.datatypes.Longitude;
//import gov.faa.ang.swac.common.datatypes.Timestamp;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import org.junit.Test;
//import org.junit.Assert;
//
//public class TrajectoryPointTest
//{
//	public static TrajectoryPoint dummy(int seed)
//	{
//		char letter = (char)(seed + 'A');
//		String name = (seed == 0 ? null : String.valueOf(letter) + String.valueOf(letter) + String.valueOf(letter));
//		
//		List<AltitudeRestriction> altitudeRestrictions = new ArrayList<AltitudeRestriction>();
//		for (int i=0; i<seed; i++)
//		{
//			altitudeRestrictions.add(AltitudeRestrictionTest.dummy(i));
//		}
//
//		TrajectoryPoint point = new TrajectoryPoint(Latitude.valueOfDegrees(seed), Longitude.valueOfDegrees(seed+10.0), Altitude.valueOfFeet(1000.0 + seed + seed), Timestamp.myValueOf("20000101").secondAdd(seed));
//		point.setName(name);
//		point.setIsWayPoint((seed % 2 == 0 ? true : false));
//		point.setIsRoutePoint((seed+1 % 2 == 0 ? true : false));
//		point.setAltitudeRestrictions(altitudeRestrictions);
//		
//		return point;
//	}
//	
//	@Test
//	public void toString_fromTextRecord_clone()
//	{
//		System.out.println("================================================================================");
//		System.out.println("Unit tests for TrajectoryPoint.toString & TrajectoryPoint.fromTextRecord() & TrajectoryPoint.clone():");
//
//		for (int i=0; i<5; i++)
//		{
//			TrajectoryPoint original = TrajectoryPointTest.dummy(i); // Create a dummy object
//			TrajectoryPoint copy = TrajectoryPoint.fromTextRecord(original.toString()); // Convert it to String and back
//			TrajectoryPoint clone = original.clone();
//			boolean pass = original.equals(copy) && original.equals(clone) && original != clone && original.getClass() == clone.getClass();
//
//			System.out.println("--------------------------------------------------------------------------------");
//			System.out.println("Comparing:");
//			System.out.println(original.toString());
//			System.out.println(copy.toString());
//			System.out.println(clone.toString());
//			System.out.println((pass ? "PASS":"FAIL"));
//			Assert.assertTrue(pass);
//		}
//	}
//	
//	public static void main(String[] args)
//	{
//		org.junit.runner.JUnitCore.runClasses(TrajectoryPointTest.class);
//	}
//}