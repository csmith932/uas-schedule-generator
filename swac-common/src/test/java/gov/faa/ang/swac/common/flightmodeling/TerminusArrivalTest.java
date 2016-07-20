/**
 * Copyright "TBD", Metron Aviation & CSSI.  All rights reserved.
 * 
 * This computer Software was developed with the sponsorship of the U.S. Government
 * under Contract No. DTFAWA-10-D-00033, which has a copyright license in accordance with AMS 3.5-13.(c)(1).
 */

package gov.faa.ang.swac.common.flightmodeling;

import gov.faa.ang.swac.common.datatypes.Altitude;
import gov.faa.ang.swac.common.datatypes.Latitude;
import gov.faa.ang.swac.common.datatypes.Longitude;
import gov.faa.ang.swac.common.datatypes.Timestamp;
import gov.faa.ang.swac.common.geometry.GCPointAlt;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class TerminusArrivalTest
{
    public static TerminusArrival dummy(int i)
    {
		TerminusArrival terminus = new TerminusArrival();
		char letter = (char)i;
		letter += 'A';
		int time = 1000000 * i;

		terminus.setAirportName( String.valueOf(letter) + String.valueOf(letter) + String.valueOf(letter++));
		terminus.setAirportLocation(new GCPointAlt(
				Latitude.valueOfDegrees(10.0+i),
				Longitude.valueOfDegrees(20.0+i),
				Altitude.valueOfFeet(i)));
		terminus.setScheduledDateTime( new Timestamp(time + 20000) );
		terminus.setScheduledFlag(true);
		//terminus.setRunwayDateTime( new FaaTimestamp(time + 30000) );
		terminus.setRunwayDateTimeFlag( "DR_Flag_" + String.valueOf(i) );
		terminus.setGateDateTime( new Timestamp(time + 40000) );
		
		return terminus;
    }

	@Ignore
    @Test
	public void toString_fromTextRecord_clone()
	{
		System.out.println("================================================================================");
		System.out.println("Unit tests for TerminusArrival.toString & TerminusArrival.fromTextRecord() & TerminusArrival.clone():");

		for (int i=0; i<5; i++)
		{
			TerminusArrival original = TerminusArrivalTest.dummy(i);                    // Create a dummy object
			TerminusArrival copy = TerminusArrival.fromTextRecord(original.toString());  // Convert it to String and back
			TerminusArrival clone = original.clone();
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
	
	public static void main(String[] args)
	{
		org.junit.runner.JUnitCore.runClasses(TerminusArrivalTest.class);
	}
}