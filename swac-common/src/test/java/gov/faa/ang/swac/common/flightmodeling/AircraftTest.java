/**
 * Copyright "TBD", Metron Aviation & CSSI.  All rights reserved.
 * 
 * This computer Software was developed with the sponsorship of the U.S. Government
 * under Contract No. DTFAWA-10-D-00033, which has a copyright license in accordance with AMS 3.5-13.(c)(1).
 */

package gov.faa.ang.swac.common.flightmodeling;

import org.junit.Ignore;
import org.junit.Test;

public class AircraftTest
{
//	public static Aircraft dummy(int seed)
//	{
//		char letter = (char) seed;
//		letter += 'A';
//		
//		PhysicalClass[] physicalClasses = { PhysicalClass.J, PhysicalClass.J, PhysicalClass.J }; 
//		
//		Aircraft aircraft = new Aircraft();
//		aircraft.setCarrierId( String.valueOf(letter) + String.valueOf(letter) + String.valueOf(letter) );
//		aircraft.setFiledBadaAircraftType( "BADA_" + String.valueOf(seed) );
//		aircraft.setFiledEtmsAircraftType( "ETMS_" + String.valueOf(seed) );
//		aircraft.setPhysicalClass( physicalClasses[seed % 3] );
//		aircraft.setAtoUserClass( String.valueOf(letter) + String.valueOf(letter) + String.valueOf(letter) + "Class" );
//		aircraft.setTurnAroundTimeCategory( 6 + seed);
//		aircraft.setEnrouteTimeCategory( 7 + seed );
//		aircraft.setClimbDecentCategory( 8 + seed );
//		aircraft.setEquipmentSuffix(EquipmentSuffixTest.dummy(seed));
//		
//		return aircraft;
//	}
	
	@Ignore
	@Test
	public void toTextRecord_fromTextRecord()
	{
//		System.out.println("================================================================================");
//		System.out.println("Unit tests for Aircraft.toTextRecord & Aircraft.fromTextRecord():");
//
//		for (int i=0; i<5; i++)
//		{
//			Aircraft original = AircraftTest.dummy(i);                    // Create a dummy object
//			Aircraft copy = Aircraft.fromTextRecord(original.toTextRecord());  // Convert it to String and back
//			boolean pass = original.equals(copy);
//
//			System.out.println("--------------------------------------------------------------------------------");
//			System.out.println("Comparing:");
//			System.out.println("----------------------------------------");
//			System.out.println(original.toTextRecord());
//			System.out.println("----------------------------------------");
//			System.out.println(copy.toTextRecord());
//			System.out.println((pass ? "PASS":"FAIL"));
//			Assert.assertTrue(pass);
//			Assert.assertTrue(original.equals(original));
//			Assert.assertTrue(original.equals(copy));
//			Assert.assertTrue(copy.equals(original));
//			Assert.assertFalse(original.equals(null));
//		}
	}
	
	public static void main(String[] args)
	{
		org.junit.runner.JUnitCore.runClasses(AircraftTest.class);
	}
}