package gov.faa.ang.swac.common.entities;

import gov.faa.ang.swac.datalayer.storage.fileio.HeaderUtils;
import gov.faa.ang.swac.datalayer.storage.fileio.TextSerializable;
import gov.faa.ang.swac.datalayer.storage.fileio.WithHeader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Lookup class for determining a flight's prime carrier based on that flight's
 * flight number.
 * This class also reads a given CSV file in order to construct its lookup
 * table.
 */
public class SubbingCarrierLookup implements TextSerializable, WithHeader, Serializable {
	private static final long serialVersionUID = 952317235422453728L;
	/**
	 * This class' lookup table by carrier and flight number range.
	 * Each carrier is a flight's current carrier mapped to potential prime
	 * carriers, ordered by flight number ranges.
	 */
	private Map<Carrier, List<SubbingCarrierRecord>> carrierMap;
	/**
	 * Comparator for sorting a current carrier's flight number ranges for more
	 * efficient lookups.
	 */
	private static Comparator<SubbingCarrierRecord>
	FLIGHT_NUMBER_COMPARATOR = new Comparator<SubbingCarrierRecord>() {
		@Override
		public int compare(SubbingCarrierRecord r1, SubbingCarrierRecord r2) {
			return r1.flightNumberLB.compareTo(r2.flightNumberLB);
		}
	};
	
	public SubbingCarrierLookup() {
		carrierMap = new HashMap<Carrier, List<SubbingCarrierRecord>>();
	}
	
	/**
	 * Sets the lookup for carrier IDs to carrier objects.
	 * This method MUST be called before loading any CSV data.
	 * @param carrier_lookup An existing CarrierLookup
	 */
	public static void setCarrierLookup(CarrierLookup carrier_lookup) {
		SubbingCarrierRecord.carrierLookup = carrier_lookup;
	}
	
	/**
	 * Returns the prime carrier for the given flight Id and current carrier.
	 * @param flight_number The flight number string (e.g. "ASH606").
	 * @param carrier The flight's current carrier (e.g. ASH).
	 * @return prime carrier (e.g. USA from the above inputs)
	 */
	public Carrier getPrimeCarrier(String flight_number, Carrier carrier) {
		List<SubbingCarrierRecord> records = carrierMap.get(carrier);
		if (records == null) {
			return carrier;
		}
		for (SubbingCarrierRecord record : records) {
			if (flight_number.compareTo(record.flightNumberLB) < 0) {
				return carrier;
			} else if (flight_number.compareTo(record.flightNumberUB) <= 0) {
				return record.primeCarrier;
			}
		}
		return carrier; // prime not found
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
		String line = reader.readLine();
		// Load the subbing carrier records.
		while (line != null) { 
			SubbingCarrierRecord record = SubbingCarrierRecord.fromCSVLine(line); 
			if (record != null && record.carrier != null && record.primeCarrier != null) {
				if (!carrierMap.containsKey(record.carrier)) {
					carrierMap.put(record.carrier, new ArrayList<SubbingCarrierRecord>());
				}
				if (!record.flightNumberLB.equals(record.carrier.getCarrierId() + "1") ||
				    !record.flightNumberUB.equals(record.carrier.getCarrierId() + "9999") ||
				    !record.carrier.equals(record.primeCarrier)) {
					// Ignore redundant records like "AAL 1 9999 AAL".
					carrierMap.get(record.carrier).add(record);
				}
			}
			line = reader.readLine();
		}
		// Sort the resultant per-carrier records by flight number.
		for (List<SubbingCarrierRecord> records : carrierMap.values()) {
			Collections.sort(records, FLIGHT_NUMBER_COMPARATOR);
		}
	}
	
	@Override
	public void writeItem(PrintWriter writer) throws IOException
	{
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Subclass for CSV data used to determine the prime carrier for a flight
	 * Id number.
	 */
	private static class SubbingCarrierRecord {
		/** Lookup for carrier names to carriers. */
		private static CarrierLookup carrierLookup = null;
		/** A flight's current carrier. */
		private final Carrier carrier;
		/** A flight's prime carrier for a given flight number range. */
		private final Carrier primeCarrier;
		/** The lower bound for the prime carrier's flight number range. */
		private final String flightNumberLB;
		/** The upper bound for the prime carrier's flight number range. */
		private final String flightNumberUB;
		
		/**
		 * Creates a new SubbingCarrierRecord from the given line of CSV data.
		 * @param line CSV data.
		 * @return SubbingCarrierRecord
		 */
		public static SubbingCarrierRecord fromCSVLine(String line)  {
			try { 
				String[] fields = line.trim().split(",", -1);
				if (fields.length >= 5) {
					return new SubbingCarrierRecord(fields);
				}
			} catch (NumberFormatException ex) { 
				ex.printStackTrace();
				System.out.println("for line: " + line);
				throw ex;
			}
			return null;
		}
		
		/**
		 * Creates a new SubbingCarrierRecord from the given CSV data fields.
		 * @param fields CSV data fields: Carrier, Flight_Number_Lower_Bound,
		 *   Flight_Number Upper_Bound, and Subbing_Carrier.
		 */
		private SubbingCarrierRecord (String [] fields)  {
			//CARRIER, FLIGHT_NUMBER_LB, FLIGHT_NUMBER_UB, SUBBING_CARRIER
			int i = 0;
			this.carrier = carrierLookup.getCarrierById(fields[i++].trim());
			this.flightNumberLB = String.valueOf(Integer.parseInt(fields[i++].trim()));
			this.flightNumberUB = String.valueOf(Integer.parseInt(fields[i++].trim()));
			this.primeCarrier = carrierLookup.getCarrierById(fields[i++].trim());
		}
	}
}