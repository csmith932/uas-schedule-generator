/**
 * Copyright "TBD", Metron Aviation & CSSI. All rights reserved.
 *
 * This computer Software was developed with the sponsorship of the U.S.
 * Government under Contract No. DTFAWA-10-D-00033, which has a copyright
 * license in accordance with AMS 3.5-13.(c)(1).
 */
package gov.faa.ang.swac.controller.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import gov.faa.ang.swac.common.entities.Carrier;
import gov.faa.ang.swac.common.entities.CarrierLookup;
import gov.faa.ang.swac.common.entities.SubbingCarrierLookup;
import gov.faa.ang.swac.common.flightmodeling.FlightPlan;
import gov.faa.ang.swac.common.flightmodeling.ScheduleRecord;
import gov.faa.ang.swac.common.flightmodeling.ScheduleRecordToFlightPlanConverter;
import gov.faa.ang.swac.controller.ExitException;
import gov.faa.ang.swac.datalayer.DataAccessException;
import gov.faa.ang.swac.datalayer.storage.DataMarshaller;

/**
 * Trivial class for data format conversion. This needs to be separate from
 * RunScheduleGenerator so that RunScheduleGenerator can be disabled without
 * breaking the data dependency chain.
 *
 * @author csmith
 *
 */
public final class RunScheduleRecordToFlightPlan extends CloneableAbstractTask {

    private static final org.apache.log4j.Logger logger =
            org.apache.log4j.LogManager.getLogger(RunScheduleRecordToFlightPlan.class);
    public static final Class<?>[] OUTPUT_DATA_TYPES =
            new Class<?>[]{FlightPlan.class};
    // inputData:
    private DataMarshaller inputScheduleFile;
    private DataMarshaller inputCarriersFile;
    private DataMarshaller inputSubbingCarrierMappingsFile;
    // outputData:
    private DataMarshaller outputFlights;
    private DataMarshaller outputCarrierLookup;
    
    private boolean scheduleTimeOverride = false;
    
    
    public DataMarshaller getInputScheduleFile() {
        return this.inputScheduleFile;
    }

    public void setInputScheduleFile(DataMarshaller scheduleFile) {
        this.inputScheduleFile = scheduleFile;
    }

    
    public DataMarshaller getInputCarriersFile() {
        return this.inputCarriersFile;
    }

    public void setInputCarriersFile(DataMarshaller scheduleFile) {
        this.inputCarriersFile = scheduleFile;
    }
    
    public DataMarshaller getInputSubbingCarrierMappingsFile() {
        return this.inputSubbingCarrierMappingsFile;
    }

    public void setInputSubbingCarrierMappingsFile(DataMarshaller mappingsFile) {
        this.inputSubbingCarrierMappingsFile = mappingsFile;
    }
    
    
    public DataMarshaller getOutputFlights() {
        return outputFlights;
    }

    public void setOutputFlights(DataMarshaller outputFlights) {
        this.outputFlights = outputFlights;
    }

    public DataMarshaller getOutputCarrierLookup() {
        return outputCarrierLookup;
    }

    public void setOutputCarrierLookup(DataMarshaller outputCarrierLookup) {
        this.outputCarrierLookup = outputCarrierLookup;
    }
    
    
	public void setScheduleTimeOverride(boolean scheduleTimeOverride) {
		this.scheduleTimeOverride = scheduleTimeOverride;
	}

	
    public RunScheduleRecordToFlightPlan() {  }
    
    public RunScheduleRecordToFlightPlan(RunScheduleRecordToFlightPlan org) {
    	super(org);
    	this.scheduleTimeOverride = org.scheduleTimeOverride; 
    }
    
    @Override
    public void run() {
        try {
        	List<ScheduleRecord> schedRecList = new ArrayList<ScheduleRecord>();
            this.inputScheduleFile.load(schedRecList);
            
            List<CarrierLookup> carrierBuilderList = new ArrayList<CarrierLookup>();
            inputCarriersFile.load(carrierBuilderList);
            CarrierLookup carrierLookup = carrierBuilderList.get(0);
            
            List<SubbingCarrierLookup> subbingCarrierMappingsBuilderList = new ArrayList<SubbingCarrierLookup>();
            SubbingCarrierLookup.setCarrierLookup(carrierLookup); // important
            inputSubbingCarrierMappingsFile.load(subbingCarrierMappingsBuilderList);
            SubbingCarrierLookup subbingCarrierLookup = subbingCarrierMappingsBuilderList.get(0);
            
            Carrier.setSubbingCarrierLookup(subbingCarrierLookup);
            
            List<FlightPlan> flightPlanList = new ArrayList<FlightPlan>();
            
        	ScheduleRecordToFlightPlanConverter converter = new ScheduleRecordToFlightPlanConverter();
        	converter.convert(schedRecList, carrierLookup, subbingCarrierLookup, scheduleTimeOverride, flightPlanList);
            
            this.outputFlights.save(flightPlanList);
            this.outputCarrierLookup.save(Collections.<CarrierLookup>singletonList(carrierLookup));
        } catch (DataAccessException ex) {
            logger.trace(ex.getStackTrace());
            throw new ExitException("Fatal", ex);
        }
    }
    

    @Override
    public RunScheduleRecordToFlightPlan clone() {
        return new RunScheduleRecordToFlightPlan(this);
    }

    @Override
    public boolean validate(VALIDATION_LEVEL level) {
        boolean retval = false;

        retval = validateFiles(new DataMarshaller[]{inputScheduleFile}, level);

        return retval;
    }
}
