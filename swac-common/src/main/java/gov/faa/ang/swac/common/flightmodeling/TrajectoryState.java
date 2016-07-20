package gov.faa.ang.swac.common.flightmodeling;

import gov.faa.ang.swac.common.datatypes.Altitude;
import gov.faa.ang.swac.common.datatypes.Timestamp;
import gov.faa.ang.swac.common.flightmodeling.atmosphere.StandardAtmosphere;
import gov.faa.ang.swac.common.flightmodeling.atmosphere.WindData;
import gov.faa.ang.swac.common.flightmodeling.fileio.BadaRecord;
import gov.faa.ang.swac.common.flightmodeling.fileio.BadaRecord.FlightStage;
import gov.faa.ang.swac.common.geometry.GCPointAltTime;
import gov.faa.ang.swac.common.utilities.Mathematics;

/**
 * Simple structure to represent point-in-time status of a trajectory during interpolation that is not
 * tied to the trajectory point itself in a fundamental way.
 * 
 * Also contains logic ported over from the interpolator for looking up the appropriate rates of change
 * 
 * @author csmith
 *
 */
// TODO: hide public fields behind getters and setters
public class TrajectoryState {
	public double tas;
	public double rocd;
	public double fur;
	public double delayFur; //fuel usage rate to be used when a flight is delayed, sitting in a queue.
	public FlightStage flightStage;
	public boolean pastTopOfClimb;
	public boolean pastTopOfDescent;
	
	// For oceanic
	public Timestamp timeToConflictResolution = new Timestamp(0); // Default to min value so before() checks don't also have to do null checks 
	public Altitude climbTo;
	public Altitude descendTo;
	
	/**
     * Calculates the rate of change data for the point. Calculated data includes airspeed,
     * fuel usage, climb rate, wind data.
     *
     * @param p {@link TrajectoryPoint} the new point (just changing data, not
     * an entirely "new" point)
     * @param p2 {@link GCPointAltTime} the point with the 4-D data
     * @param windData {@link WindDataClass} the wind field
     * @param flightStage int value for the stage of flight
     */
    public void updateRateOfChangeParameters(
            BadaRecord badaRecord,
            Altitude perceivedAlt,
            BadaModel model,
            Altitude altitudeTransition,
            WindData windData,
            boolean useBadaRocd) {
        if (perceivedAlt.feet() >= altitudeTransition.feet()) {
            perceivedAlt = Altitude.valueOfFeet(Mathematics.round(StandardAtmosphere.findAltitude(windData.getPressureLevel(perceivedAlt)), 0));
        }

        // If aircraft mass is provided, use BadaModel figures, otherwise use BadaRecord figures
        if (model == null) {
        	this.tas = badaRecord.trueAirSpeed(perceivedAlt, flightStage);
        	if (!Double.isNaN(this.tas)){
	        	this.fur = badaRecord.fuelUsageRate(perceivedAlt, flightStage, BadaRecord.Range.NOMINAL);
	        	this.delayFur = badaRecord.fuelUsageRate(perceivedAlt, FlightStage.CRUISE, BadaRecord.Range.NOMINAL); //when a flight is delayed in a queue, we will assume it's cruising.
	        	if (useBadaRocd) this.rocd = badaRecord.rateofClimbDescent(perceivedAlt, flightStage, BadaRecord.Range.NOMINAL);
        	}
        } else {
        	this.tas = model.getTrueAirSpeed(flightStage, perceivedAlt);
        	if (!Double.isNaN(this.tas)){
	        	this.fur = model.getFuelUsageRate(flightStage, perceivedAlt);
	        	this.delayFur = model.getFuelUsageRate(FlightStage.CRUISE, perceivedAlt); //when a flight is delayed in a queue, we will assume it's cruising.
	        	if (useBadaRocd) this.rocd = model.getRateOfClimbDescent(flightStage, perceivedAlt);
        	}
        }

        // BADA tables don't have CRUISE data below certain altitudes (i.e. Double.isNaN(this.tas)).
        // If attempting to cruise too low, use climb data for that altitude (and log a message).
        // (Manual check for too-low-cruise also performed in NASPACTrajectoryUtilities.fuelUsage().)
        if (Double.isNaN(this.tas) && flightStage == BadaRecord.FlightStage.CRUISE) {
        	this.tas = badaRecord.trueAirSpeed(perceivedAlt, BadaRecord.FlightStage.ASCENT);
        	this.fur = badaRecord.fuelUsageRate(perceivedAlt, BadaRecord.FlightStage.ASCENT, BadaRecord.Range.NOMINAL);
        	this.delayFur = this.fur; // no cruise altitude available, just us ASCENT like fur.
        	if (useBadaRocd) this.rocd = 0.0;
        }
        
        assert (!Double.isNaN(this.tas));
        
        // final check to make sure delay fuel usage rate is valid.
        if (Double.isNaN(this.delayFur)){
            this.delayFur = this.tas;
        }
    }
}
