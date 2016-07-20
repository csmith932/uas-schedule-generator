package gov.faa.ang.swac.common.interfaces;

import gov.faa.ang.swac.common.flightmodeling.FlightLeg;
import gov.faa.ang.swac.common.flightmodeling.Itinerary;
import gov.faa.ang.swac.common.flightmodeling.TrajectoryState;
import gov.faa.ang.swac.datalayer.DataAccessException;

/**
 *
 * @author ssmitz
 */
public interface Plugin extends Cloneable {
    public void modelItinerary(Itinerary itinerary) throws DataAccessException;
    public void modelFlightLeg(FlightLeg flightLeg) throws DataAccessException;
    
    /**
     * modelRemainingFlightLeg gives plugins the ability to perform modeling differently for flight legs that are considered to
     * be partially simulated already. The beginning of the flightLeg up to idxLastFlown should by convention be left untouched,
     * with appropriate modeling applied from that point forward in such a way that continuity of the flight leg is preserved.
     * 
     * @param flightLeg The flight leg to be modified in place
     * @param idxLastFlown Trajectory point index for the last invariant point
     * @throws DataAccessException
     */
    public void modelRemainingFlightLeg(FlightLeg flightLeg, int idxLastFlown) throws DataAccessException;
    
    /**
     * modelRemainingFlightLeg gives plugins the ability to perform modeling differently for flight legs that are considered to
     * be partially simulated already. The beginning of the flightLeg up to idxLastFlown should by convention be left untouched,
     * with appropriate modeling applied from that point forward in such a way that continuity of the flight leg is preserved.
     * 
     * @param flightLeg The flight leg to be modified in place
     * @param idxLastFlown Trajectory point index for the last invariant point
     * @param initialState Trajectory modeling metadata for overriding default behavior (e.g. climb, descend)
     * @throws DataAccessException
     */
    public void modelRemainingFlightLeg(FlightLeg flightLeg, int idxLastFlown, TrajectoryState initialState) throws DataAccessException;
    
    /**
     * modelRemainingFlightLeg gives plugins the ability to perform modeling differently for flight legs that are considered to
     * be completely simulated already. All capacitated resources are left untouched, and modeling proceeds only on the segments
     * between them. 
     * 
     * @param flightLeg The flight leg to be modified in place
     * @throws DataAccessException
     */
    public void modelFinalFlownFlightLeg(FlightLeg flightLeg) throws DataAccessException;
    
    public Plugin clone() throws CloneNotSupportedException;
}
