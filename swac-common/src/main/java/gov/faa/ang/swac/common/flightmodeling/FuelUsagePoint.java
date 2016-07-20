package gov.faa.ang.swac.common.flightmodeling;

import java.io.Serializable;

import gov.faa.ang.swac.common.flightmodeling.fileio.BadaRecord.FlightStage;

public class FuelUsagePoint implements Serializable{
	
	private TrajectoryPoint parent; // point in flight route where the FuelUsagePoint was calculated
	
	private double climbFuelUsed; // kg
	private double cruiseFuelUsed; // kg
	private double descentFuelUsed; // kg
	
	private double firFuelUsed; // kg
	private double firTransitTime; // minutes
	
	private FuelUsagePoint(TrajectoryPoint parent){
		this.parent = parent;
		this.climbFuelUsed = 0;
		this.cruiseFuelUsed = 0;
		this.descentFuelUsed = 0;
		
		this.firFuelUsed = 0;
		this.firTransitTime = 0;
	}
	
	private FuelUsagePoint(FuelUsagePoint original){
		this.climbFuelUsed = original.climbFuelUsed;
		this.cruiseFuelUsed = original.cruiseFuelUsed;
		this.descentFuelUsed = original.descentFuelUsed;
		
		this.firFuelUsed = original.firFuelUsed;
		this.firTransitTime = original.firTransitTime;
	}
	
	/**
	 * 
	 * @param parent 
	 * @return new FuelUsagePoint with zero for all fuel usage values.
	 */
	public static FuelUsagePoint newInstance(TrajectoryPoint parent){
		return new FuelUsagePoint(parent);
	}
	
	/**
	 * 
	 * @return a copy of this FuelUsagePoint
	 */
	public FuelUsagePoint newInstance(){
		return new FuelUsagePoint(this);
	}
	
	/**
	 * Manually adjust the fuel usage amounts and FIR transit time for delays experienced during Sim execution.
	 * 
	 * @param fuelUsed
	 * @param minutes
	 * @param stage
	 * @param inFirRegion
	 */
	public void adjustForDelays(double fuelUsed, double minutes, FlightStage stage, boolean inFirRegion){
		if (FlightStage.ASCENT.equals(stage)){
			this.climbFuelUsed += fuelUsed;
		}else if (FlightStage.CRUISE.equals(stage)){
			this.cruiseFuelUsed += fuelUsed;
		}else if (FlightStage.DESCENT.equals(stage)){
			this.descentFuelUsed += fuelUsed;
		}
		
		if (inFirRegion){
			this.firFuelUsed += fuelUsed;
			this.firTransitTime += minutes;
		}
	}
	
	/**
	 * Adjust amount of fuel used based on the time between the current TrajectoryPoint and
	 * the anticipated time to reach the nextPoint.  
	 * 
	 * @param nextPoint next TrajectoryPoint in the flight route.
	 * @param inFirRegion should we consider the nextPoint to be inside Flight Information Region?
	 * @return new FuelUsagePoint with updated FlightStage and FIR fuel usage values and updated FIR transit time.
	 */
	public FuelUsagePoint updateParameters(TrajectoryPoint nextPoint, boolean inFirRegion){
		
		FuelUsagePoint nextFuelPoint = new FuelUsagePoint(this);
		nextFuelPoint.parent = nextPoint;
		
		double elapsedMinutes = nextPoint.timestamp().minDifference(this.parent.timestamp());
		double fuelBurn = elapsedMinutes * this.parent.instantaneousFuelRate();
		
		if (FlightStage.ASCENT.equals(this.parent.stage())){
			nextFuelPoint.climbFuelUsed += fuelBurn;
		}else if (FlightStage.CRUISE.equals(this.parent.stage())){
			nextFuelPoint.cruiseFuelUsed += fuelBurn;
		}else if (FlightStage.DESCENT.equals(this.parent.stage())){
			nextFuelPoint.descentFuelUsed += fuelBurn;
		}else{
			return null;
		}
		
		if (inFirRegion){
			nextFuelPoint.firFuelUsed += fuelBurn;
			nextFuelPoint.firTransitTime += elapsedMinutes;
		}
		
		return nextFuelPoint;
	}
	
	/**
	 * 
	 * @return Kg fuel used during CLIMB up until this point
	 */
	public double climbFuel(){
		return this.climbFuelUsed;
	}
	
	/**
	 * 
	 * @return Kg fuel used during CRUISE up until this point
	 */
	public double cruiseFuel(){
		return this.cruiseFuelUsed;
	}
	
	/**
	 * 
	 * @return Kg fuel used during DESCENT up until this point
	 */
	public double descentFuel(){
		return this.descentFuelUsed;
	}
	
	/**
	 * 
	 * @return Kg fuel used while in FIR region up until this point
	 */
	public double firFuel(){
		return this.firFuelUsed;
	}
	
	/**
	 * 
	 * @return minutes spent in FIR region up until this point.
	 */
	public double firTime(){
		return this.firTransitTime;
	}
	
	/**
	 * 
	 * @return Sum of CLIMB, CRUISE and DESCNET fuel used.
	 */
	public double getTotalFuelUsed(){
		return this.climbFuelUsed + this.cruiseFuelUsed + this.descentFuelUsed;
	}

}
