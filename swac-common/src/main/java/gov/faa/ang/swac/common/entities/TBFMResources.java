package gov.faa.ang.swac.common.entities;

import gov.faa.ang.swac.common.flightmodeling.IResourceInfo.ResourceType;
import gov.faa.ang.swac.common.interfaces.VisitorTwo;
import gov.faa.ang.swac.datalayer.storage.fileio.TextSerializable;
import gov.faa.ang.swac.datalayer.storage.fileio.WithHeader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections15.keyvalue.MultiKey;
import org.apache.commons.collections15.map.MultiKeyMap;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;



public class TBFMResources implements TextSerializable, WithHeader {
	protected static Logger logger = LogManager.getLogger(TBFMResources.class);

	/**
	 * Maps fix and airport to TBFMArrivalStream. TBFMArrivalStream contains all the TBFM resources for a particular
	 * fix/airport combo.
	 */
	private MultiKeyMapEx<String, TBFMArrivalStream> tbfmResourceMap;
	/**
	 * Maps arc name to Arc
	 */
	private Map<String, Arc> arcMap;
	/**
	 * Maps freeze horizon name to FreezeHorizon
	 */
	private Map<String, FreezeHorizon> freezeHorizonMap;
	
	/**
	 * Set of names of airports that participate in TBFM.
	 */
	private Set<String> tbfmAirportSet;
	
	/**
	 * Set of names of fixes that participate in TBFM.
	 */
	private Set<String> tbfmFixSet;
	
	
    public TBFMResources() {
    	tbfmResourceMap = new MultiKeyMapEx<String, TBFMArrivalStream>();
    	arcMap = new HashMap<String, Arc>();
    	freezeHorizonMap = new HashMap<String, FreezeHorizon>();
    	tbfmFixSet = new HashSet<String>();
    	tbfmAirportSet = new HashSet<String>();
    }

    
    public boolean isArrivalFixInTBFM(String arrivalFix) {
    	return tbfmFixSet.contains(arrivalFix);
    }
    
    public boolean isAirportInTBFM(String airport) {
    	return tbfmAirportSet.contains(airport);
    }
    
    public Arc getArc(String arcName) { 
    	return arcMap.get(arcName);
    }
    
    public FreezeHorizon getFreezeHorizon(String freezeHorizonName) { 
    	return freezeHorizonMap.get(freezeHorizonName);
    }
    
    public TBFMArrivalStream getTBFMArrivalStream(String arrivalFix, String airport) {
    	return tbfmResourceMap.get(arrivalFix, airport);
    }
    
    public TBFMArrivalStream getTBFMArrivalStream(FreezeHorizon freezeHorizon) {
    	return tbfmResourceMap.get(freezeHorizon.getFixName(), freezeHorizon.getAirportName());
    }
    
    public TBFMArrivalStream getTBFMArrivalStream(Arc arc) {
    	return tbfmResourceMap.get(arc.getFixName(), arc.getAirportName());
    }

	public int countOfStreamsToAirport(final String airportName) {
		// Something like this should work if we can ever use Java 8 for the sim.
		//return tbfmResourceMap.values().stream()
		//		.filter(s -> s.getAirportCode().equals(airportName))
		//		.count();
		int count = 0;
		for (TBFMArrivalStream arrivalStream : tbfmResourceMap.values()) {
			if (arrivalStream.getAirportCode().equals(airportName))
				count++;
		}
		return count;
	}
    
    /**
     * Tours the given visitee parameter through each fix/airport combination known in TBFM.  
     * 
     * Caller must provide VisitorTwo implementation 
     * 
     * tbfmResources.visit(new VisitorTwo<String, String>() {
     * 		@Override
     * 		public void visit(String fix, String airport) {
     *          // do something here
     *      }
     *  });
     *  
     * @param visitee
     */
    public void visitAllFixAirportCombos(VisitorTwo<String, String> visitee) {
    	Set<MultiKey<String>> keySet = tbfmResourceMap.keySet();
    	for (MultiKey<String> fixAirportKey : keySet) {
    		String fix = fixAirportKey.getKey(0);
    		String airport = fixAirportKey.getKey(1);
    		visitee.visit(fix, airport);
    	}
    }

    @Override
    public long readHeader(BufferedReader reader) throws IOException {
    	reader.readLine();
        return -1;
    }

    @Override
    public void readItem(BufferedReader reader) throws IOException {
        String line = reader.readLine();
        List<Arc> arcList = new ArrayList<Arc>(200);
        List<FreezeHorizon> freezeHorizonList = new ArrayList<FreezeHorizon>(200);
        while (line != null) {
        	int q = 0;
	        String[] fields = line.split(",");
	        if (fields.length < 5) {
	        	logger.error("Invalid line in TBFM file: " + line);
	        	line = reader.readLine();
	        	continue;
	        }
	        String typeStr = fields[q++].trim();
	        String airportCode = fields[q++].trim();
	        String fixName = fields[q++].trim();
	        String milesFromFixStr = fields[q++].trim();
	        String delayAbsortionStr = fields[q++].trim();

	        TBFMArrivalStream arrivalStream = tbfmResourceMap.get(fixName, airportCode);
	        if (arrivalStream == null) {
	        	arrivalStream = new TBFMArrivalStream(fixName, airportCode);
	        	tbfmResourceMap.put(fixName, airportCode, arrivalStream);
	        	
	        	tbfmFixSet.add(fixName);
	        	tbfmAirportSet.add(airportCode);
	        }
	        
	        long delayAbsorption = Math.round(Double.parseDouble(delayAbsortionStr) * 1000 * 60);
	        
	        ResourceType type = null;
	        double milesFromFix = 0.;
	        if (typeStr.equalsIgnoreCase("TR")) {
	        	//With tracon boundary threshold's, we are only concerned with the delay absorption value 
	        	arrivalStream.setTraconDelayThreshold(delayAbsorption);
	        } else { 
	        	type = ResourceType.valueOf(typeStr);
	        	milesFromFix = Double.parseDouble(milesFromFixStr);
	        
		        switch(type) {
				case AR:
					Arc arc = new Arc(airportCode, fixName, milesFromFix, delayAbsorption);
					arrivalStream.addArc(arc);
					arcList.add(arc);
					break;
				case FH:
					FreezeHorizon freezeHorizon = new FreezeHorizon(airportCode, fixName, milesFromFix);
					freezeHorizonList.add(freezeHorizon);
					arrivalStream.addFreezeHorizon(freezeHorizon);
					break;
				default:
					throw new IllegalStateException("Unknown resource type " + type);
		        }
	        }
	        line = reader.readLine();
        }
        
        // Arc and FreezeHorizon names are derived from fix/airport combo + plus the index the entity is in the arrival stream.
        // So given airport "ARPT" and fix "FIX", two arcs might have names "AR_ARPT_FIX_0" and "AR_ARPT_FIX_1".  We haven't  
		// been able to determine the entity names until now, after all the resources have been read in, because we
		// didn't know the index order of the entities (they are ordered by proximity to arrival fix).   
        for (TBFMArrivalStream stream : tbfmResourceMap.values()) { 
        	String err = stream.complete();  // inform stream no more entity adds, this will trigger the stream class to name the entities.
        	if (err != null)
        		throw new IOException("Invalid TBFM configuration: " + err);
        }
        
        // With TBFM entities now named, build a lookup map of names to arcs and name to freeze horizons
        for (Arc arc : arcList) { 
        	arcMap.put(arc.getName(), arc);
        }
        for (FreezeHorizon freezeHorizon : freezeHorizonList) { 
        	freezeHorizonMap.put(freezeHorizon.getName(), freezeHorizon);
        }
    }

    @Override
    public void writeHeader(PrintWriter writer, long numRecords) throws IOException {
    	throw new UnsupportedOperationException("");
    }

    @Override
    public void writeItem(PrintWriter writer) throws IOException {
        throw new UnsupportedOperationException("");
    }
    

    /**
	 * Extend the Apache MultiKeyMap to override a method that inexplicably doesn't provide generic friendly return type
	 * 
	 * @author cunningham
	 * 
	 * @param <K>
	 * @param <V>
	 */
    private static class MultiKeyMapEx<K, V> extends MultiKeyMap<K, V> {
		private static final long serialVersionUID = 6865124961676268819L;

		public MultiKeyMapEx() { super(); } 
    	
		// super class values() is inexplicably not generified
	    public Set<MultiKey<K>> keySet() {
	        return map.keySet();
	    }
	    
    	// super class values() is inexplicably not generified
	    public Collection<V> values() {
	        return map.values();
	    }
    }
 }
