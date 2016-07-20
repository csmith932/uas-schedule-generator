package gov.faa.ang.swac.common.entities;

import gov.faa.ang.swac.datalayer.storage.fileio.HeaderUtils;
import gov.faa.ang.swac.datalayer.storage.fileio.TextSerializable;
import gov.faa.ang.swac.datalayer.storage.fileio.WithHeader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class CarrierLookup implements TextSerializable, WithHeader, Serializable {
	private static final long serialVersionUID = 952317235422453727L;
	
	private Map<String, Carrier> carrierMap;
	
	public CarrierLookup() { }
	
	/**
	 * 
	 * @param id ICAO name.
	 * @return
	 */
	public Carrier getCarrierById(String id) {
		return carrierMap.get(id);
	}
	
	/**
	 * Same thing as getCarrierById() but it loops over carrierMap instead of doing a direct key lookup.  
	 * Slower, but currently this method currently only used by BtsPayloadMap class during payload file generation.
	 * 
	 * @param id IATA name.
	 * @return first Carrier with matching IATA as there is a many-to-one relationship for some carriers.
	 */
	public Carrier getCarrierByIata(String id){
		for (Carrier carrier : this.carrierMap.values()){
			if (carrier.getIATACarrierId().equals(id))
				return carrier;
		}
		
		return null;
	}
	
	@Override
	public long readHeader(BufferedReader reader) throws IOException {
		HeaderUtils.readHeaderHashComment(reader);
		return -1;
	}
	
	@Override
	public void writeHeader(PrintWriter writer, long numRecords) throws IOException {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void readItem(BufferedReader reader) throws IOException {
		Map<String, CarrierRecord> carrierRecordMap = new HashMap<String, CarrierRecord>();
		String line = reader.readLine();
		// load the carrier records from the file 
		while (line != null) { 
			CarrierRecord carrierRecord = CarrierRecord.fromCSVLine(line); 
			if (carrierRecord != null) {
				Object shouldBeNull = carrierRecordMap.put(carrierRecord.icaoId, carrierRecord);
				if (shouldBeNull != null)
					throw new IllegalStateException("Duplicate carrier name in carriers file");
			}
			line = reader.readLine();
		}
		
		// fill in the initial acquiring carrier links
		for (CarrierRecord record : carrierRecordMap.values()) { 
			if (record.acquiringCarrierId != null && ! record.acquiringCarrierId.isEmpty()) {
				CarrierRecord mergerRecord = carrierRecordMap.get(record.acquiringCarrierId);
				if (mergerRecord != null)
					record.latestAcquiringCarrierRecord = mergerRecord;
			}
		}
		
		// For each carrier, find its "latest" acquiring carrier. For example, if carrier A was bought out by carrier
		// B, and carrier B was later bought out by carrier C, then the latest acquiring carrier for carrier A is
		// carrier C. 
		for (CarrierRecord record : carrierRecordMap.values()) {
			CarrierRecord acquiringCarrierRecord = record.latestAcquiringCarrierRecord; 
			if (acquiringCarrierRecord != null) {
				CarrierRecord nextAcquiringCarrierRecord = acquiringCarrierRecord;
				while (nextAcquiringCarrierRecord != null) {
					acquiringCarrierRecord = nextAcquiringCarrierRecord;
	        		nextAcquiringCarrierRecord = nextAcquiringCarrierRecord.latestAcquiringCarrierRecord;
	        	}
				record.latestAcquiringCarrierRecord = acquiringCarrierRecord;
			}
		}
		
		// convert CarrierRecords into Carrier
		carrierMap = new HashMap<String, Carrier>(carrierRecordMap.size());
		for (CarrierRecord record : carrierRecordMap.values()) {
			carrierMap.put(record.icaoId, record.getOrCreateCarrier());
		}
	}
	
	@Override
	public void writeItem(PrintWriter writer) throws IOException
	{
		throw new UnsupportedOperationException();
	}
	
	private static class CarrierRecord {
		private final String icaoId;
		private final boolean isMajorCarrier;
		private final boolean isSubCarrier;
		private final boolean isDomestic;
		private final boolean useInAirlineResponse;
		private final String acquiringCarrierId;
		private CarrierRecord latestAcquiringCarrierRecord;
		private Carrier acquiringCarrier;
		private String iataId; 
		
		public static CarrierRecord fromCSVLine(String line)  {
			try { 
				String[] fields = line.trim().split(",", -1);
				if (fields.length >= 7) {
					return new CarrierRecord(fields);
				}
			} catch (NumberFormatException ex) { 
				ex.printStackTrace();
				System.out.println("for line: " + line);
				throw ex;
			}
			return null;
		}
		
		private CarrierRecord (String [] fields)  {
			//ICAO, IATA, MAJOR_CARRIER, SUB_CARRIER, IS_DOMESTIC, USE_IN_AIRLINE_RESPONSE, ACQUIRING_CARRIER
			int i = 0;
			icaoId = fields[i++].trim();
			iataId = fields[i++].trim();
			isMajorCarrier = Integer.parseInt(fields[i++].trim()) == 1;
			isSubCarrier = Integer.parseInt(fields[i++].trim()) == 1;
			isDomestic = Integer.parseInt(fields[i++].trim()) == 1;
			useInAirlineResponse = Integer.parseInt(fields[i++].trim()) == 1;
			acquiringCarrierId = fields[i++].trim();
		}
		
		public Carrier getOrCreateCarrier() {
			if (acquiringCarrier == null) {
				acquiringCarrier = new Carrier(icaoId, iataId, isMajorCarrier, isSubCarrier, isDomestic, useInAirlineResponse, latestAcquiringCarrierRecord == null ? null : latestAcquiringCarrierRecord.getOrCreateCarrier());
			}
			return acquiringCarrier;
		}
	}
}