package gov.faa.ang.swac.common.tbfm;

import gov.faa.ang.swac.common.datatypes.Timestamp;
import gov.faa.ang.swac.common.entities.FreezeHorizon;

import java.util.HashMap;
import java.util.Map;

/**
 * Contains the arc and dumping ground ETAs for a flight under TBFM control.
 */
public class TBFMFlightSchedule {
	private int flightId;
	private FreezeHorizon freezeHorizon;
	private Map<String, Timestamp> etaMap;
	private Map<String, Long> delayMap;
	private Timestamp dumpingGroundETA;
	private long dumpingGroundDelay;

	private TBFMFlightSchedule(int flightId, FreezeHorizon freezeHorizon, Map<String, Timestamp> etaMap, Map<String, Long> delayMap, Timestamp dumpingGroundETA, long dumpingGroundDelay) {
		this.flightId = flightId;
		this.freezeHorizon = freezeHorizon;
		this.etaMap = etaMap;
		this.delayMap = delayMap;
		this.dumpingGroundETA = dumpingGroundETA;
		this.dumpingGroundDelay = dumpingGroundDelay;
	}

	public int getFlightId() { return flightId; }
	
	public FreezeHorizon getFreezeHorizon() { return freezeHorizon; } 
	
	/**
	 * Returns TBFM the max delay the arc could absorb for this flight as determined by the TBFM TFM. This is NOT the
	 * same value as the Arc's configured max delay absorption, not is it necessarily the delay the flight could receive
	 * at the arc.
	 * 
	 * For example - arc has max 5 minutes delay absorption. TBFM assigns flight 3 minutes of delay at the arc. Flight
	 * arrives at the arc 1 minutes later than expected, and flight receives 2 minutes of delay at the arc.  For such a 
	 * setup, this method would return 3.
	 */
	public long getEstimatedArcDelay(String arcName) {
		return delayMap.get(arcName);
	}
	
	public Timestamp getArcETA(String arcName) {
		return etaMap.get(arcName);
	}

	public Timestamp getDumpingGroundETA() {
		return dumpingGroundETA;
	}
	
	public long getDumpingGroundDelay() { 
		return dumpingGroundDelay;
	}

	@Override
	public String toString() {
		return "TBFMFlightSchedule [flightId=" + flightId + ", freezeHorizon=" + freezeHorizon + ", etaMap=" + etaMap
				+ ", delayMap=" + delayMap + ", dumpingGroundETA=" + dumpingGroundETA + ", dumpingGroundDelay="
				+ dumpingGroundDelay + "]";
	}
	public static class Builder { 
		private int flightId;
		private FreezeHorizon freezeHorizon;
		private Map<String, Timestamp> etaMap;
		private Map<String, Long> delayMap;
		private Timestamp dumpingGroundETA;
		private long dumpingGroundDelay;
		
		public Builder(int flightId, FreezeHorizon freezeHorizon) {
			this.flightId = flightId;
			this.freezeHorizon = freezeHorizon;
			this.etaMap = new HashMap<String, Timestamp>();
			this.delayMap = new HashMap<String, Long>();
		}

		public void addArcEta(String arcName, Timestamp timestamp, long delayMs) {
			etaMap.put(arcName, timestamp);
			delayMap.put(arcName, delayMs);
		}
		
		public void setDumpingGroundETA(Timestamp dumpingGroundETA, long delayMs) {
			this.dumpingGroundETA = dumpingGroundETA;
			this.dumpingGroundDelay = delayMs;
		}
		
		public TBFMFlightSchedule create() { 
			return new TBFMFlightSchedule(flightId, freezeHorizon, etaMap, delayMap, dumpingGroundETA, dumpingGroundDelay);
		}
	}
}
