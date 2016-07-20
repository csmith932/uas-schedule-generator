/**
 * Copyright "TBD", Metron Aviation & CSSI.  All rights reserved.
 * 
 * This computer Software was developed with the sponsorship of the U.S. Government
 * under Contract No. DTFAWA-10-D-00033, which has a copyright license in accordance with AMS 3.5-13.(c)(1).
 */

package gov.faa.ang.swac.common.flightmodeling;

import org.junit.Test;

public class FlightLegTest
{
//    public static FlightLeg dummy(int seed)
//    {
//		FlightLeg flightLeg = new FlightLeg();
//		flightLeg.setScheduleId(seed);
//		flightLeg.setFlightId("FlightID" + seed);
//		flightLeg.setFlightRuleType(FlightRuleType.VFR);
//		flightLeg.setFiledFlightPlanType("IFR");
//		flightLeg.setDeparture(TerminusDepartureTest.dummy(seed));
//		flightLeg.setArrival(TerminusArrivalTest.dummy(seed));
//		flightLeg.setWay(WayTest.dummy(seed));
//		flightLeg.setFiledAltitude(Altitude.valueOfFeet(10000.0 + seed));
//		flightLeg.setFiledAirspeed(600 + seed);
//		
//		return flightLeg;
//    }
//
	@Test
	public void toString_fromTextRecord_clone()
	{
//		System.out.println("================================================================================");
//		System.out.println("Unit tests for FlightLeg.toString & FlightLeg.fromTextRecord() & FlightLeg.clone():");
//
//		for (int i=0; i<5; i++)
//		{
//			FlightLeg original = FlightLegTest.dummy(i);                    // Create a dummy object
//			FlightLeg copy = FlightLeg.fromTextRecord(original.toString());  // Convert it to String and back
//			FlightLeg clone = original.clone();
//			boolean pass = original.equals(copy) && original.equals(clone) && original != clone && original.getClass() == clone.getClass();
//
//			System.out.println("--------------------------------------------------------------------------------");
//			System.out.println("Comparing:");
//			System.out.println("----------------------------------------");
//			System.out.println(original.toString());
//			System.out.println("----------------------------------------");
//			System.out.println(copy.toString());
//			System.out.println("----------------------------------------");
//			System.out.println(clone.toString());
//			System.out.println((pass ? "PASS":"FAIL"));
//			Assert.assertTrue(pass);
//		}
	}
//	
	public static void main(String[] args)
	{
		org.junit.runner.JUnitCore.runClasses(FlightLegTest.class);
	}
}