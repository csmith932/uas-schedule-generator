package gov.faa.ang.swac.common.entities;


import java.util.Collection;
import java.util.Collections;
import java.util.NavigableMap;
import java.util.TreeMap;

/**
 * This class encapsulates all the TBFM resources (arcs, freeze horizons) in a fix/airport arrival stream. 
 * 
 * @author cunningham
 */
public class TBFMArrivalStream  {
	private String fixName;
	private String airportCode;
	private NavigableMap<Double, FreezeHorizon> freezeHorizons; // comparable via milesFromFix
	private NavigableMap<Double, Arc> arcs; // comparable via milesFromFix
	private long traconDelayThreshold = 0;
	  
    public TBFMArrivalStream(String fixName, String airportCode) {
    	this.fixName = fixName;
    	this.airportCode = airportCode;
		
    	freezeHorizons = new TreeMap<Double, FreezeHorizon>();  
    	arcs = new TreeMap<Double, Arc>();
    }

	public String getFixName() {
		return fixName;
	}

	public String getAirportCode() {
		return airportCode;
	}

	/**
	 * Returns the tracon delay threshold in milliseconds.
	 */
    public long getTraconDelayThreshold() { 
    	return traconDelayThreshold;
    }
    
	/**
	 * Returns an unmodifiable collection of all freeze horizons in the arrival stream ordered by proximity to arrival
	 * fix. Closest ones first.
	 * 
	 * @return
	 */
    public Collection<FreezeHorizon> getFreezeHorizons() {  
    	return Collections.unmodifiableCollection(freezeHorizons.values());
    }
    
	/**
	 * Returns an unmodifiable collection of freeze horizons in the arrival stream whose distance to the fix are less
	 * than (or equal to if the given inclusive parameter is true) the given milesFromFix parameter, ordered by
	 * proximity to arrival fix. Closest ones first.
	 * 
	 * @return
	 */
    public Collection<FreezeHorizon> getFreezeHorizons(double milesFromFix, boolean inclusive) {  
    	return Collections.unmodifiableCollection(
    			freezeHorizons.headMap(milesFromFix, inclusive).values()
    	);
    }

    /**
     * Returns whether given freeze horizon is the first freeze horizon in the stream.
     *  
     * @param freezeHorizon
     */
	public boolean isFirstFreezeHorizon(FreezeHorizon freezeHorizon) {
		return freezeHorizons.headMap(freezeHorizon.getMilesFromFix(), false).isEmpty();
	}
	
	/**
	 * Returns an unmodifiable collection of all arcs in the arrival stream ordered by proximity to arrival fix. Closest
	 * ones first.
	 * 
	 * @return
	 */
    public Collection<Arc> getArcs(){
    	return Collections.unmodifiableCollection(arcs.values());
    }
    	
    
    /**
	 * Returns an unmodifiable collection of arcs in the arrival stream who are closer to the fix than the given freeze
	 * horizon parameter, ordered by proximity to arrival fix. Closest ones first.
	 * 
	 * @return
	 */
    public Collection<Arc> getDownstreamArcs(FreezeHorizon freezeHorizon) {  
    	return Collections.unmodifiableCollection(
    			arcs.headMap(freezeHorizon.getMilesFromFix(), true).values()
    	);
    	
    }

    /**
	 * Returns an unmodifiable collection of arcs in the arrival stream whose distance to the fix are less
	 * than (or equal to if the given inclusive parameter is true) the given milesFromFix parameter, ordered by
	 * proximity to arrival fix. Closest ones first.
	 * 
	 * @return
	 */
    public Collection<Arc> getDownstreamArcs(double milesFromFix, boolean inclusive) {  
    	return Collections.unmodifiableCollection(
    			arcs.headMap(milesFromFix, inclusive).values()
    	);
    }
    
    /**
	 * Returns an unmodifiable collection of arcs in the arrival stream who are downstream from the given
	 * upstreamFreezeHorizon parameter and upstream from the next downstream Freeze Horizon. Closest ones first.
	 * 
	 * @return
	 */
    public Collection<Arc> getArcsBetweenFreezeHorizons(FreezeHorizon upstreamFreezeHorizon) {
    	double toRangeExcl = 0;
		Collection<FreezeHorizon> downstreamFH = freezeHorizons.headMap(upstreamFreezeHorizon.getMilesFromFix(), false).values();
		if (! downstreamFH.isEmpty()) {
			FreezeHorizon nextFH = downstreamFH.iterator().next();
			toRangeExcl = nextFH.getMilesFromFix();
		}

		return Collections.unmodifiableCollection(
				arcs.subMap(upstreamFreezeHorizon.getMilesFromFix(), true, toRangeExcl, false).values()
    	);
    }
    
    
    void setTraconDelayThreshold(long value) {
    	this.traconDelayThreshold = value;
    }

	void addArc(Arc arc) {
		arcs.put(arc.getMilesFromFix(), arc);
	}
	
	void addFreezeHorizon(FreezeHorizon freezeHorizon) {
		freezeHorizons.put(freezeHorizon.getMilesFromFix(), freezeHorizon);
	}

	/**
	 * Signals to this class that no more state changes will be applied to class. Method will assign names to the arcs
	 * and freeze horizons based on the index of the entity in relation to its closeness to the fix.  Method will also
	 * perform some validation checks and return a String describing any validation failures, or null if the validation
	 * checks pass.
	 */
	String complete() {
		int i = 0;
		for (Arc arc : arcs.values()) { 
			arc.setName("AR_" + fixName + "_" + airportCode + "_" + i++);
		}
		
		i = 0;
		for (FreezeHorizon freezeHorizon : freezeHorizons.values()) { 
			freezeHorizon.setName("FH_" + fixName + "_" + airportCode + "_" + i++);
		}

		
		StringBuilder sb = new StringBuilder();
		if (freezeHorizons.isEmpty())
			sb.append("No freeze horizons defined. ");
		if (arcs.isEmpty())
			sb.append("No arcs defined. ");
		if (sb.length() > 0)
			return buildPrefix() + sb.toString();
		
		if (arcs.lastKey() > freezeHorizons.lastKey()) { 
			sb.append("Arc defined with no upstream freeze Horizon defined before it");
			return buildPrefix() + sb.toString();
		}
		return null;
	}
	
	private String buildPrefix() { return "For arrival stream with fix " + fixName + " and airport " + airportCode + ", "; }

 
}
