/**
 * Copyright "TBD", Metron Aviation & CSSI.  All rights reserved.
 * 
 * This computer Software was developed with the sponsorship of the U.S. Government
 * under Contract No. DTFAWA-10-D-00033, which has a copyright license in accordance with AMS 3.5-13.(c)(1).
 */

package gov.faa.ang.swac.common.flightmodeling.jni;

import gov.faa.ang.swac.common.datatypes.Timestamp;
import gov.faa.ang.swac.common.flightmodeling.EquipmentSuffix;
import gov.faa.ang.swac.common.flightmodeling.FlightLeg;
import gov.faa.ang.swac.datalayer.storage.fileio.TextSerializable;

import java.util.List;

public interface Airframe extends Comparable<Airframe>, TextSerializable
{
	///
	/// JNI fields (Do not change without C++ synchronization
	///
	
    public Integer airframeId();
    public Timestamp activationTimestamp();
    public Integer tailNumber();
    public String airlineIndicator(); // "ml"= military, "ga"= general aviation
    public String aircraftType();
    
    public EquipmentSuffix equipmentSuffix();
    public String equipmentSuffixString(); // this is string representation of equipmentSuffix, for jni translation
                                                // eg. "3201" in string. Default 0.
    public Integer turnAroundCategory();
    public Integer pushbackCategory();
    public Integer taxiOutCategory();
    public Integer taxiInCategory();
    
    public boolean dayOverrideFlag();
    
    public List<FlightLeg> flightList();
}
