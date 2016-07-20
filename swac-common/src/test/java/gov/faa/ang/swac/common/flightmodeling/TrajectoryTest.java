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
//public class TrajectoryTest
//{
//	public static Trajectory dummy(int seed)
//	{
//		List<TrajectoryPoint> points = new ArrayList<TrajectoryPoint>();
//
//		// Create trajectory points and fill with dummy data
//		for (int i=0; i<seed; i++)
//		{
//			points.add(TrajectoryPointTest.dummy(i));
//		}
//
//		Trajectory trajectory = new Trajectory();
//		trajectory.setTrajectoryPoints(points);
//		return trajectory;
//	}
//
//	@Test
//	public void toString_fromTextRecord_clone()
//	{
//		System.out.println("================================================================================");
//		System.out.println("Unit tests for Trajectory.toString & Trajectory.fromTextRecord() & Trajectory.clone():");
//
//		for (int i=0; i<5; i++)
//		{
//			Trajectory original = TrajectoryTest.dummy(i);                         // Create a dummy object
//			Trajectory copy = Trajectory.fromTextRecord(original.toString());  // Convert it to String and back
//			Trajectory clone = original.clone();
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
//
//	public static void main(String[] args)
//	{
//		org.junit.runner.JUnitCore.runClasses(TrajectoryTest.class);
//	}
//}