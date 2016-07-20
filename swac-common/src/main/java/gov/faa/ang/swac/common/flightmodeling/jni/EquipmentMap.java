/**
 * Copyright "TBD", Metron Aviation & CSSI.  All rights reserved.
 * 
 * This computer Software was developed with the sponsorship of the U.S. Government
 * under Contract No. DTFAWA-10-D-00033, which has a copyright license in accordance with AMS 3.5-13.(c)(1).
 */

package gov.faa.ang.swac.common.flightmodeling.jni;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gov.faa.ang.swac.common.entities.EquipmentMapRecord;

public class EquipmentMap {
	private final Map<String, Integer> turnAroundCategoryMap;
	private final Map<String, Integer> pushbackCategoryMap;
	private final Map<String, Integer> taxiOutCategoryMap;
	private final Map<String, Integer> taxiInCategoryMap;
	private final Map<String, Integer> rerouteClearanceCategoryMap;
	private final Map<String, Integer> rampCategoryMap;

	public int getTurnAroundCategory(String actype) {
		Integer retVal = this.turnAroundCategoryMap.get(actype);
		return retVal == null ? 0 : retVal;
	}

	public int getPushbackCategory(String actype) {
		Integer retVal = this.pushbackCategoryMap.get(actype);
		return retVal == null ? 0 : retVal;
	}

	public int getTaxiOutCategory(String actype) {
		Integer retVal = this.taxiOutCategoryMap.get(actype);
		return retVal == null ? 0 : retVal;
	}

	public int getTaxiInCategory(String actype) {
		Integer retVal = this.taxiInCategoryMap.get(actype);
		return retVal == null ? 0 : retVal;
	}
	
	public int getRerouteClearanceCategory(String actype) {
		Integer retVal = this.rerouteClearanceCategoryMap.get(actype);
		return retVal == null ? 0 : retVal;
	}
	
	public int getRampCategory(String actype) { 
		Integer retVal = this.rampCategoryMap.get(actype);
		return retVal == null ? 0 : retVal;
	}

	/**
	 * 
	 * @param equipmentMapRecords
	 */
	public EquipmentMap(List<EquipmentMapRecord> equipmentMapRecords) {

		this.turnAroundCategoryMap = new HashMap<String, Integer>();
		this.pushbackCategoryMap = new HashMap<String, Integer>();
		this.taxiOutCategoryMap = new HashMap<String, Integer>();
		this.taxiInCategoryMap = new HashMap<String, Integer>();
		this.rerouteClearanceCategoryMap = new HashMap<String, Integer>();
		this.rampCategoryMap = new HashMap<String, Integer>();
		
		for (EquipmentMapRecord record : equipmentMapRecords) {
			this.turnAroundCategoryMap.put(record.aircraft_type,
					record.turnaround_cat);
			this.pushbackCategoryMap.put(record.aircraft_type,
					record.pushback_cat);
			this.taxiOutCategoryMap.put(record.aircraft_type,
					record.taxi_out_cat);
			this.taxiInCategoryMap
					.put(record.aircraft_type, record.taxi_in_cat);
			this.rerouteClearanceCategoryMap.put(record.aircraft_type, record.rerouteClearanceCat);
			this.rampCategoryMap.put(record.aircraft_type, record.rampCat);
		}
	}
}
