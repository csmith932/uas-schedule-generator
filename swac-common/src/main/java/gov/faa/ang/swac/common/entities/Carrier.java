/**
 * Copyright 2014, Metron Aviation & CSSI.  All rights reserved.
 * 
 * This computer Software was developed with the sponsorship of the U.S. Government
 * under Contract No. DTFAWA-10-D-00033, which has a copyright license in accordance with AMS 3.5-13.(c)(1).
 */
package gov.faa.ang.swac.common.entities;

import java.io.Serializable;

/**
 * A carrier airline.
 */
public class Carrier implements Serializable { 
	
	private static final long serialVersionUID = 2479464098404611061L;
	
	/** 
	 * ICAO carried id.  
	 */
	private final String icaoId;
	/** 
	 * IATA carried id. 
	 */
	private final String iataId;
	/** 
	 * Whether or not this carrier is a major carrier. 
	 */
	private final boolean isMajorCarrier;
	/** 
	 * Whether or not this carrier is a sub-carrier.
	 */ 
	private final boolean isSubCarrier;
	/** 
	 * True if domestic, false if international
	 */
	private final boolean isDomestic;
	/** 
	 * True if carrier should be an agent in airline response
	 */
	private final boolean useInAirlineResponse;
	/** 
	 * Carrier that bought out this carrier or the carrier that bought this one or ... 
	 */
	private final Carrier latestAcquiringCarrier;
	
	/**
	 * Lookup for determining the prime carrier from a flight's flightId range.
	 */
	private static SubbingCarrierLookup subbingCarrierLookup = null;
	
	/**
	 * Sets the lookup class for determining the prime carrier from a flight's
	 * flightId range.
	 * @param lookup The lookup class.
	 */
	public static void setSubbingCarrierLookup(SubbingCarrierLookup lookup) {
		subbingCarrierLookup = lookup;
	}
		
	/** 
	 * Initializes a new carrier with the given state 
	 */
	public Carrier(String name, boolean isMajorCarrier, boolean isSubCarrier, boolean isDomestic, boolean useInAirlineResponse) {
		this(name, null, isMajorCarrier, isSubCarrier, isDomestic, useInAirlineResponse, null);
	}
	
	/** 
	 * Initializes a new carrier with the given state 
	 */
	public Carrier(String icaoId, String iataId, boolean isMajorCarrier, boolean isSubCarrier, boolean isDomestic, boolean useInAirlineResponse, Carrier latestAcquiringCarrier) {
		this.icaoId = icaoId;
		this.iataId = iataId;
		this.isMajorCarrier = isMajorCarrier;
		this.isSubCarrier = isSubCarrier;
		this.isDomestic = isDomestic;
		this.useInAirlineResponse = useInAirlineResponse;
		this.latestAcquiringCarrier = latestAcquiringCarrier;
	}

	/** 
	 * Copy constructor
	 */
	public Carrier(Carrier carrier) {
		this.icaoId = carrier.icaoId;
		this.iataId = carrier.iataId;
		this.isMajorCarrier = carrier.isMajorCarrier;
		this.isSubCarrier = carrier.isSubCarrier;
		this.isDomestic = carrier.isDomestic;
		this.useInAirlineResponse = carrier.useInAirlineResponse;
		this.latestAcquiringCarrier = carrier.latestAcquiringCarrier;
	}

	/** 
	 * Returns this carrier's icao id. 
	 */
	public String getCarrierId() { return getICAOCarrierId(); }

	/** 
	 * Returns this carrier's icao id. 
	 */
	public String getICAOCarrierId() { return icaoId; }
	
	/** 
	 * Returns this carrier's iata id. 
	 */
	public String getIATACarrierId() { return iataId; }
	
	/** 
	 * Returns whether or not this carrier is a major carrier. 
	 */
	public boolean isMajorCarrier() { return isMajorCarrier; }

	/** 
	 * Returns whether or not this carrier is a sub-carrier. 
	 */
	public boolean isSubCarrier() { return isSubCarrier; }

	/** 
	 * Returns true if carrier is a domestic, false if carrier is international 
	 */
	public boolean isDomestic() { return isDomestic; }
	
	/** 
	 * Returns true if carrier is a domestic, false if carrier is international 
	 */
	public boolean useInAirlineResponse() { return useInAirlineResponse; }
	
	/**
	 * Returns the Carrier that owns the assets now. For example, if this carrier was bought out by carrier B, and carrier
	 * B was later bought out by carrier C, then this method would return carrier C.
	 */
	public Carrier getLatestAcquiringCarrier() { return latestAcquiringCarrier; }
	
	/**
	 * Returns the prime carrier based on this carrier and a flight number.
	 * @param flight_number The flight number.
	 * @return prime carrier (which may be the current one)
	 */
	public Carrier getPrimeCarrier(String flight_number) {
		if (subbingCarrierLookup == null) {
			return this;
		} else {
			return subbingCarrierLookup.getPrimeCarrier(flight_number, this);
		}
	}
	
	@Override
	public String toString() {
		return "Carrier [name: " + icaoId + ", iata: " + iataId +"; isMajorCarrier: " + isMajorCarrier + ", isSubCarrier: " + isSubCarrier
				 + ", isDomestic: " + isDomestic + ", useInAirlineResponse: " + useInAirlineResponse
				+ ( latestAcquiringCarrier == null ? "" : ", latestAcquiringCarrier: " +  latestAcquiringCarrier.getCarrierId() ) + "]";
	}

	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((icaoId == null) ? 0 : icaoId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Carrier other = (Carrier) obj;
		if (icaoId == null) {
			if (other.icaoId != null)
				return false;
		} else if (!icaoId.equals(other.icaoId))
			return false;
		return true;
	}
	
	
}
