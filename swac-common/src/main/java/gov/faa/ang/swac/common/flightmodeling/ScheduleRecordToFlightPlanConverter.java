/**
 * Copyright "TBD", Metron Aviation & CSSI. All rights reserved.
 *
 * This computer Software was developed with the sponsorship of the U.S.
 * Government under Contract No. DTFAWA-10-D-00033, which has a copyright
 * license in accordance with AMS 3.5-13.(c)(1).
 */
package gov.faa.ang.swac.common.flightmodeling;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import gov.faa.ang.swac.common.entities.Carrier;
import gov.faa.ang.swac.common.entities.CarrierLookup;
import gov.faa.ang.swac.common.entities.SubbingCarrierLookup;

/**
 * Trivial class for data format conversion. This needs to be separate from
 * RunScheduleGenerator so that RunScheduleGenerator can be disabled without
 * breaking the data dependency chain.
 *
 * @author csmith
 *
 */
public final class ScheduleRecordToFlightPlanConverter  {

	public ScheduleRecordToFlightPlanConverter() {
	}
	
    //scheduleTimeOverride = false
	public void convert(List<ScheduleRecord> schedRecList, CarrierLookup carrierLookup,
			SubbingCarrierLookup subbingCarrierLookup, boolean scheduleTimeOverride, List<FlightPlan> flightPlanList) {
        for (ScheduleRecord schedRec : schedRecList) {
            flightPlanList.add(new FlightPlan(schedRec, scheduleTimeOverride));
        }
        
        // should be done before this method?
        SubbingCarrierLookup.setCarrierLookup(carrierLookup); // important
        Carrier.setSubbingCarrierLookup(subbingCarrierLookup);
		
        for (FlightPlan flightPlan : flightPlanList) { 
        	String carrierId = flightPlan.aircraft().carrierId();
        	Carrier carrier = carrierLookup.getCarrierById(carrierId);
        	if (carrier != null) { 
        		flightPlan.aircraft().setCarrier(carrier);
        	}
        }
        
//        this.outputFlights.save(flightPlanList);
//        this.outputCarrierLookup.save(Collections.<CarrierLookup>singletonList(carrierLookup));
    }
}
        
