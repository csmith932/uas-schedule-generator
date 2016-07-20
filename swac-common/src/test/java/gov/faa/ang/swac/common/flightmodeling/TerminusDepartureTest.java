/**
 * Copyright "TBD", Metron Aviation & CSSI.  All rights reserved.
 * 
 * This computer Software was developed with the sponsorship of the U.S. Government
 * under Contract No. DTFAWA-10-D-00033, which has a copyright license in accordance with AMS 3.5-13.(c)(1).
 */

package gov.faa.ang.swac.common.flightmodeling;

import static org.junit.Assert.assertTrue;
import gov.faa.ang.swac.common.datatypes.Altitude;
import gov.faa.ang.swac.common.datatypes.Latitude;
import gov.faa.ang.swac.common.datatypes.Longitude;
import gov.faa.ang.swac.common.datatypes.Timestamp;
import gov.faa.ang.swac.common.geometry.GCPointAlt;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class TerminusDepartureTest
{
    public static TerminusDeparture dummy(int i)
    {
		TerminusDeparture terminus = new TerminusDeparture();
		char letter = (char)i;
		letter += 'A';
		int time = 1000000 * i;

		terminus.setAirportName( String.valueOf(letter) + String.valueOf(letter) + String.valueOf(letter++));
		terminus.setScheduledFlag(true);
		terminus.setScheduledDateTime( new Timestamp(time + 10000) );
		terminus.setGateDateTime( new Timestamp(time + 10000) );
		terminus.setGateDateTimeFlag( "DG_Flag_" + String.valueOf(i) );
		terminus.setRunwayDateTime( new Timestamp(time + 20000) );
		terminus.setRunwayDateTimeFlag( "DR_Flag_" + String.valueOf(i) );
		terminus.setAirportLocation(new GCPointAlt(
				Latitude.valueOfDegrees(10.0+i),
				Longitude.valueOfDegrees(20.0+i),
				Altitude.valueOfFeet(i)));


		return terminus;
    }

    @Ignore
	@Test
	public void toString_fromTextRecord_clone()
	{
		System.out.println("================================================================================");
		System.out.println("Unit tests for TerminusDeparture.toString & TerminusDeparture.fromTextRecord() & TerminusDeparture.clone():");

		for (int i=0; i<5; i++)
		{
			TerminusDeparture original = TerminusDepartureTest.dummy(i);                         // Create a dummy object
			TerminusDeparture copy = TerminusDeparture.fromTextRecord(original.toString());  // Convert it to String and back
			TerminusDeparture clone = original.clone();
			boolean pass = original.equals(copy) && original.equals(clone) && original != clone && original.getClass() == clone.getClass();

			System.out.println("--------------------------------------------------------------------------------");
			System.out.println("Comparing:");
			System.out.println("----------------------------------------");
			System.out.println(original.toString());
			System.out.println("----------------------------------------");
			System.out.println(copy.toString());
			System.out.println("----------------------------------------");
			System.out.println(clone.toString());
			System.out.println((pass ? "PASS":"FAIL"));
			Assert.assertTrue(pass);
		}
	}
	
	@Test
	public void equalsTest()
	{
		TerminusDeparture a = dummy(42);
		TerminusDeparture b = new TerminusDeparture(a);
		TerminusDeparture c = a.clone();
		assertTrue(a.equals(b));
		assertTrue(b.equals(c));
		assertTrue(a.equals(c));
	}
	
	public static void main(String[] args)
	{
		org.junit.runner.JUnitCore.runClasses(TerminusDepartureTest.class);
	}
}
