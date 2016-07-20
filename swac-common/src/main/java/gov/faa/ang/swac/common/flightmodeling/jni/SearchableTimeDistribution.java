package gov.faa.ang.swac.common.flightmodeling.jni;

import java.io.Serializable;
import java.util.*;

/**
 * A collection of time distribution records for carriers, airports, and
 * equipment types.
 * Each distribution collection is composed of pushback, turnaround, taxi in,
 * taxi out, etc. times.
 * @author balan
 */
public class SearchableTimeDistribution {
	/** The distribution list data. */
	private TimeDataMap distributionList;
	
	/**
	 * Adds each record in the given list of look-up records to this
	 * distribution.
	 * Each record is recorded into a specialized key for quick look-up.
	 * If the record contains any wildcard entries, the record is added to the
	 * map's internal, ordered wildcard record list. When a record to be
     * looked-up does not match any records in the map, wildcard records are
     * used to try and find a match. This is done in the order wildcards were
     * added. 
	 * @param distributionList The list of look-up records to add.
	 */
	public SearchableTimeDistribution(List<? extends TimeDistributionLookupRecord> distributionList)
	{
		this.distributionList = new TimeDataMap();
		for (TimeDistributionLookupRecord record : distributionList) {
			TimeKey key = new TimeKey(record.getICAOCarrier(), record.getAirportCode(), record.getEquipmentCat());
			this.distributionList.put(key, record);
		}
	}
	
	/**
	 * Returns the TimeDistribution that is associated with the given carrier,
	 * airport code, and equipment types.
	 * The given data is recorded into a specialized key for quick look-up.
	 * @param carrier The carrier to look up.
	 * @param arptCat The airport code to look up.
	 * @param equipType The equipment type to look up.
	 * @return TimeDistribution
	 */
	public TimeDistribution getDistribution(String carrier, String arptCat, Integer equipType){
		TimeKey key = new TimeKey(carrier, arptCat, equipType);
		TimeDistributionLookupRecord record = this.distributionList.get(key);
		return (record != null) ? record.getTimeDistribution() : null;
	}
	
	/**
	 * Returns the RampTimeDistributionLookupRecord that is associated with the
	 * given carrier, airport code, and equipment types.
	 * The given data is recorded into a specialized key for quick look-up.
	 * @param carrier The carrier to look up.
	 * @param arptCat The airport code to look up.
	 * @param equipType The equipment type to look up.
	 * @return RampTimeDistributionLookupRecord
	 */
	public RampTimeDistributionLookupRecord getRampDistRecord(String carrier, String arptCat, Integer equipType)
	{
		TimeKey key = new TimeKey(carrier, arptCat, equipType);
		TimeDistributionLookupRecord record = this.distributionList.get(key);
		if (!(record instanceof RampTimeDistributionLookupRecord)) return null;
		return (RampTimeDistributionLookupRecord)record;		
	}
	
	/**
	 * A specialized key used for quick look-ups for TimeDistributions.
	 * The key is based on fields in a TimeDistributionLookupRecord and is
	 * composed of a carrier name, an airport code, and an equipment type.
	 * Wildcard entries are allowed. For carrier names and airport codes, the
	 * wildcard value is "-". For equipment types it is `null`.
	 * @author balan
	 */
	public static class TimeKey implements Serializable {
		/** The carrier name. */
		private String carrier;
		/** The airport code. */
		private String arptCat;
		/** The equipment type. */
		private Integer equipType;
		/** The hash code for this key, based on the above fields. */
		private int hashCode;
		
		/**
		 * Creates a new key for the given carrier, airport, and equipment type.
		 * @param carrier The carrier name from a TimeDistributionLookupRecord.
		 * @param arptCat The airport code from a TimeDistributionLookupRecord.
		 * @param equipType The equipment type from a
		 *   TimeDistributionLookupRecord.
		 */
		public TimeKey(String carrier, String arptCat, Integer equipType)
		{
			if (carrier == null) carrier = "";
			if (arptCat == null) arptCat = "";
			this.carrier = carrier;
			this.arptCat = arptCat;
			this.equipType = equipType;
			int prime = 31;
			hashCode = 1;
			hashCode = prime * hashCode + (carrier + arptCat).hashCode();
			hashCode = prime * hashCode + ((equipType != null) ? equipType : -1);
		}
		
		@Override
		/**
		 * The hash code for this key.
		 * It comprises the carrier, airport, and equipment type fields.
		 */
		public int hashCode() { return hashCode; }
		
		@Override
		/**
		 * Returns whether or not the given object is equivalent to this key.
		 * @param other The object to compare this key with.
		 */
		public boolean equals(Object other)
		{
			if (other == this) return true;
			if (other == null) return false;
			if (!(other instanceof TimeKey)) return false;
			TimeKey otherTimeKey = (TimeKey) other;
					
			return carrier.equals(otherTimeKey.getCarrier()) &&
					arptCat.equals(otherTimeKey.getArptCat()) &&
					((equipType == null) ? (otherTimeKey.getEquipType() == null) : (equipType.equals(otherTimeKey.getEquipType())));
		}
		
		/** Returns this key's carrier name. */
		public String getCarrier() { return carrier; }
		/** Returns this key's airport code. */
		public String getArptCat() { return arptCat; }
		/** Returns this key's equipment type. */
		public Integer getEquipType() { return equipType; }
		
		/**
		 * Returns a copy of this wildcard key with its non-wildcard entries
		 * replaced with the given key's respective entries.
		 * This method is used to determine whether or not the resultant
		 * wildcard record was originally added to this key's associated map.
		 * @param instance The key whose entries should replace this wildcard
		 *   key's wildcard entries.
		 * @return TimeKey
		 */
		public TimeKey replaceWildcards(TimeKey instance) {
			return new TimeKey(!carrier.equals("-") ? instance.getCarrier() : carrier,
			                   !arptCat.equals("-") ? instance.getArptCat() : arptCat,
			                   equipType != null ? instance.getEquipType() : equipType);
		}
		
		public String toString() { 
			return String.format("TimeKey [ carrier %s, arpt %s, equip %s ]", carrier, arptCat, equipType);
		}
	}
	
	/**
	 * A specialized map, keyed with TimeKeys, that holds look-up records for
	 * TimeDistributions.
	 * Acts as a normal look-up map for records unless the map contains wildcard
	 * keys. In that case, those wildcard keys are used to try and find a match.
	 * This is done in the order wildcards were added.
	 * @author balan
	 */
	public class TimeDataMap extends HashMap<TimeKey, TimeDistributionLookupRecord> {
		/**
		 * A map that indicates the order of wildcard records added to this map.
		 * This map is used to determine which record to ultimately return if
		 * multiple wildcards match a given key.
		 */
		private Map<TimeKey, Integer> wildcards;
		/**
		 * A map of generic wildcard keys to their wildcard maps.
		 * There are multiple wildcard maps associated with different wildcard
		 * entry combinations. In order to attempt to find a match for a key,
		 * that key needs to be converted to a generic wildcard key. If the
		 * resultant is contained within a wildcard map, then that resultant's
		 * record is a candidate for the looked-up record.
		 */
		private Map<TimeKey, Map<TimeKey, TimeDistributionLookupRecord>> wildcardsMapList;
		/** Map of records for keys having wildcard carrier names. */
		private Map<TimeKey, TimeDistributionLookupRecord> wildcardsCarrier;
		/** Map of records for keys having wildcard airport codes. */
		private Map<TimeKey, TimeDistributionLookupRecord> wildcardsArptCat;
		/** Map of records for keys having wildcard equipment types. */
		private Map<TimeKey, TimeDistributionLookupRecord> wildcardsEquipType;
		/** Map of records for keys having wildcard carrier names and airport codes. */
		private Map<TimeKey, TimeDistributionLookupRecord> wildcardsCarrierArptCat;
		/** Map of records for keys having wildcard airport codes and equipment types. */
		private Map<TimeKey, TimeDistributionLookupRecord> wildcardsArptCatEquipType;
		/** Map of records for keys having wildcard carrier names and equipment types. */
		private Map<TimeKey, TimeDistributionLookupRecord> wildcardsCarrierEquipType;
		/** Map of records for keys having wildcard carrier names, airport codes, and equipment types. */
		private Map<TimeKey, TimeDistributionLookupRecord> wildcardsAll;
		
		
		/** Creates a new map and empty wildcard data structures. */
		public TimeDataMap() {
			super();
			
			wildcards = new HashMap<TimeKey, Integer>();
			
			wildcardsMapList = new LinkedHashMap<TimeKey, Map<TimeKey, TimeDistributionLookupRecord>>();
			wildcardsCarrier = new HashMap<TimeKey, TimeDistributionLookupRecord>();
			wildcardsArptCat = new HashMap<TimeKey, TimeDistributionLookupRecord>();
			wildcardsEquipType = new HashMap<TimeKey, TimeDistributionLookupRecord>();
			wildcardsCarrierArptCat = new HashMap<TimeKey, TimeDistributionLookupRecord>();
			wildcardsArptCatEquipType = new HashMap<TimeKey, TimeDistributionLookupRecord>();
			wildcardsCarrierEquipType = new HashMap<TimeKey, TimeDistributionLookupRecord>();
			wildcardsAll = new HashMap<TimeKey, TimeDistributionLookupRecord>();
			
			// For each key below, non-"-" and non-null fields are replaced by
			// their respective fields in an instance key when trying to find
			// a wildcard match.
			wildcardsMapList.put(new TimeKey("-", "", 0), wildcardsCarrier);
			wildcardsMapList.put(new TimeKey("", "-", 0), wildcardsArptCat);
			wildcardsMapList.put(new TimeKey("", "", null), wildcardsEquipType);
			wildcardsMapList.put(new TimeKey("-", "-", 0), wildcardsCarrierArptCat);
			wildcardsMapList.put(new TimeKey("", "-", null), wildcardsArptCatEquipType);
			wildcardsMapList.put(new TimeKey("-", "", null), wildcardsCarrierEquipType);
			wildcardsMapList.put(new TimeKey("-", "-", null), wildcardsAll);
		}
		
		@Override
		/**
		 * If the given key does not contain wildcard entries, puts the key and
		 * its given value into the map.
		 * Otherwise, adds the key to an internal, ordered wildcard record list,
		 * and places the value in the appropriate wildcard record map.
		 */
		public TimeDistributionLookupRecord put(TimeKey key, TimeDistributionLookupRecord record)
		{
			boolean wildcardCarrier = key.getCarrier().equals("-");
			boolean wildcardArptCat = key.getArptCat().equals("-");
			boolean wildcardEquipType = key.getEquipType() == null;
			if (wildcardCarrier || wildcardArptCat || wildcardEquipType) {
				// Add this wildcard key to the ordered list of keys added.
				wildcards.put(key, wildcards.size());
				// Add the key and its value to the proper wildcard map.
				Map<TimeKey, TimeDistributionLookupRecord> map = null;
				if (wildcardCarrier && wildcardArptCat && wildcardEquipType) {
					map = wildcardsAll;
				} else if (wildcardCarrier && wildcardArptCat) {
					map = wildcardsCarrierArptCat;
				} else if (wildcardArptCat && wildcardEquipType) {
					map = wildcardsArptCatEquipType;
				} else if (wildcardCarrier && wildcardEquipType) {
					map = wildcardsCarrierEquipType;
				} else if (wildcardCarrier) {
					map = wildcardsCarrier;
				} else if (wildcardArptCat) {
					map = wildcardsArptCat;
				} else {
					map = wildcardsEquipType;
				}
				map.put(key, record);
				return null;
			} else {
				return super.put(key, record);
			}
		}
		
		@Override
		/**
		 * Returns the record associated with the given key in this map.
		 * If no record exists, loops through the map's wildcard keys, attemping
		 * to find a match.
		 * Returns null if there was ultimately no match.
		 */
		public TimeDistributionLookupRecord get(Object key)
		{
			if (!(key instanceof TimeKey)) return null;
			if (containsKey(key)) {
				return super.get(key);
			} else {
				// Try wildcards.
				
				// As an example, suppose the following (simplified) data set:
				// #CARRIER AIRPORT EQUIP RECORD
				// AAL      ABQ     null  foo
				// -        ABQ     null  bar
				// -        -       null  baz
				// After loading the data, the "wildcardsEquipType" map has the
				// first entry, the "wildcardsCarrierEquip" map has the second,
				// and the "wildcardsAll" has the third.
				//
				// Assume we have a record: ("SWA", "ABQ", 1) to look-up.
				//
				// Loop through each map along with its generic key.
				// The iteration is unordered, so suppose "wildcardsAll" comes
				// up first.
				// Generic key: ("-", "-", null).
				// Since the generic key doesn't have any non-"-" and non-null
				// fields, no transformation happens and looking up that key
				// in the "wildcardsAll" map yields a result. Consider "baz" a
				// candidate to return.
				//
				// Suppose the next iteration is for "wildcardsEquipType".
				// Generic key: ("", "", null).
				// The generic key has two non-"-" fields so the transformation
				// using the given record yields: ("SWA", "ABQ", null).
				// Looking up that key in "wildcardsEquipType" yields nothing.
				//
				// Suppose the next iteration is for "wildcardsCarrierEquip".
				// Generic key: ("-", "", null).
				// Transformation: ("-", "ABQ", null).
				// Looking up that transformed key in "wildcardsCarrierEquip"
				// yields a result. Since we already have a result ("baz", from
				// "wildcardsAll"), check to see which wildcard record was added
				// first. In this case, this record was added first. Therefore,
				// consider "bar" the new candidate to return, forgetting "baz".
				//
				// All other iterations yield nothing, so "bar" is returned.
				
				int currentKey = Integer.MAX_VALUE;
				TimeDistributionLookupRecord currentRecord = null;
				for (Map.Entry<TimeKey, Map<TimeKey, TimeDistributionLookupRecord>> entry : wildcardsMapList.entrySet()) {
					// Get the generic wildcard key for this wildcard map and
					// replaces its non-"-" and non-null fields with the given
					// key's respective fields.
					TimeKey wildcard = entry.getKey().replaceWildcards((TimeKey)key);
					// Now get the record associated with the resultant key.
					TimeDistributionLookupRecord record = entry.getValue().get(wildcard);
					if (record != null) {
						// Even if a record was found, there may be multiple
						// matches for multiple wildcard maps. In that case,
						// ensure the record returned was the one that matches
						// the first wildcard record originally added.
						int i = wildcards.get(wildcard); 
						if (i < currentKey) {
							currentKey = i;
							currentRecord = record;
						}
					}
				}
				return currentRecord;
			}
		}
	}
}
