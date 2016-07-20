/**
 * Copyright "TBD", Metron Aviation & CSSI.  All rights reserved.
 * 
 * This computer Software was developed with the sponsorship of the U.S. Government
 * under Contract No. DTFAWA-10-D-00033, which has a copyright license in accordance with AMS 3.5-13.(c)(1).
 */

package gov.faa.ang.swac.common.flightmodeling;

import org.junit.Ignore;
import org.junit.Test;

public class ItineraryTest
{
//    protected static Itinerary dummy(int seed)
//    {
//        Itinerary itinerary = null;
//        Type type = Type.values()[seed % Type.values().length];
//        switch (type)
//        {
//            case IFR:
//                itinerary = new Itinerary(Itinerary.Type.IFR);
//                break;
//            case VFR_ARR:
//            case VFR_DEP:
//            case VFR_DEP_ARR:
//                itinerary = new Itinerary(type);
//                break;
//            default:
//                logger.error("ItineraryTest.dummy(): Unsupported Itinerary Type (" + type + ").");
//                break;
//        }
//
//        if (itinerary == null)
//        {
//            logger.error("ItineraryTest.dummy(): Exiting.");
//            throw new RuntimeException();
//        }
//        
//        // Create dummy Aircraft
//        itinerary.setAircraft(AircraftTest.dummy(seed));
//        
//        // Create dummy FlightLegs
//        List<FlightLeg> flightLegs = new ArrayList<FlightLeg>();
//        int numFlightLegs = (type == Type.IFR ? (seed == 0 ? 1 : seed) : 1);
//        for (int i=0; i<numFlightLegs; i++)
//        {
//            flightLegs.add(FlightLegTest.dummy(i));
//        }
//        itinerary.setFlightLegs(flightLegs);
//
//        return itinerary;
//    }
//
    @Ignore
    @Test
    public void toString_fromTextRecord()
    {
//        System.out.println("================================================================================");
//        System.out.println("Unit tests for Itinerary.toString & Itinerary.fromTextRecord():");
//
//        for (int i = 1; i < 5; i++)
//        {
//            System.out.println("--------------------------------------------------------------------------------");
//            System.out.println("Comparing:");
//            System.out.println("----------------------------------------");
//            Itinerary original = ItineraryTest.dummy(i); // Create a dummy object
//            System.out.println(original.toString());
//
//            System.out.println("----------------------------------------");
//            Itinerary copy = Itinerary.fromTextRecord(original.toString()); // Convert it to String and back
//            System.out.println(copy.toString());
//
//            boolean pass = original.equals(copy);
//            System.out.println((pass ? "PASS" : "FAIL"));
//            Assert.assertTrue(pass);
//        }
    }

    public static void main(String[] args)
    {
        org.junit.runner.JUnitCore.runClasses(ItineraryTest.class);
    }
}