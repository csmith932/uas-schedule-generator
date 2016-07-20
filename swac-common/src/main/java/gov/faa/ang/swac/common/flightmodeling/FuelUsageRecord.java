/**
 * Copyright "TBD", Metron Aviation & CSSI. All rights reserved. This computer Software was
 * developed with the sponsorship of the U.S. Government under Contract No. DTFAWA-10-D-00033, which
 * has a copyright license in accordance with AMS 3.5-13.(c)(1).
 */

package gov.faa.ang.swac.common.flightmodeling;

import gov.faa.ang.swac.common.flightmodeling.fileio.BadaRecord.FlightStage;

import java.io.Serializable;

/**
 * Primarily a wrapper for the FuelUsagePoint class.<BR><BR>
 * 
 * Used to track planned, nominal and delay fuel burn.  The trajectory modeled from the original flight plan provides the planned fuel usage.  The
 * nominal fuel usage is obtained from re-modeled trajectories due to rerouting.  If no rerouting has occurred the planned and nominal values will be identical.
 * The delay fuel is only incremented when there is delay observed at Resource crossings in the Sim.  A cruise usage rate is also stored by this class and should
 * be calculated when we calculate planned fuel use.
 * 
 * @author hkaing
 *
 */
public class FuelUsageRecord implements Serializable
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

    /**
     * Fuel burn rate for the highest altitude flight is scheduled to fly at in TrajectoryModeling.
     */
    private double cruiseUsageRate = Double.NaN;
    
    // planned and nominal fuel should be set by TrajectoryModeler after total fuel use is calculated at the ArrivalAirport.
    // delay fuel should only be modified by the Sim when we experience delay (SectorEntry, TBFM absorption events, ArrivalQueue, etc.)
    private FuelUsagePoint planned;
    private FuelUsagePoint nominal;
    private FuelUsagePoint delay;
    
    /**
     * Creates new FuelUsageRecord by setting both PLANNED and NOMINAL fuel to the plannedUsage value.  The plannedUsage
     * FuelUsagePoint should be calculated once a flight reaches the Arrival Airport.  The DELAY fuel associated with this 
     * FuelUsageRecord is initialized with zero fuel usage values for all FlightStages and FIR values.
     * 
     * @param plannedUsage FuelUsagePoint populated at Arrival Airport.
     * @param cruiseUsageRate fuel usage rate of kg per min
     * @return new FuelUsageRecord based to total fuel consumed at time of plannedUsage.
     */
    public static FuelUsageRecord newInstance(FuelUsagePoint plannedUsage, double cruiseUsageRate){
    	FuelUsageRecord record = new FuelUsageRecord();
    	record.cruiseUsageRate = cruiseUsageRate;
    	record.planned = plannedUsage;
    	record.nominal = plannedUsage;
    	record.delay = FuelUsagePoint.newInstance(null);
    	return record;
    }
    
    /**
     * 
     * @param original
     * @return new FuelUsageRecord with identical planned, nominal and delay FuelUsagePoints as the original
     */
    public static FuelUsageRecord newInstance(FuelUsageRecord original){
    	FuelUsageRecord record = new FuelUsageRecord();
    	record.cruiseUsageRate = original.cruiseUsageRate;
    	record.planned = original.planned.newInstance();
    	record.nominal = original.nominal.newInstance();
    	record.delay = original.delay.newInstance();
    	return record;
    }
    
    /**
     * Set cruise usage rate for highest cruise altitude observed.
     * 
     * @param rate
     */
    public void setCruiseUsageRate(double rate){
    	assert(Double.isNaN(this.cruiseUsageRate));
    	this.cruiseUsageRate = rate;
    }
    
    /**
     * Set original total fuel used at arrival airport. 
     * 
     * @param plannedFuel
     */
    public void setPlanned(FuelUsagePoint plannedFuel){
    	this.planned = plannedFuel;
    }
    
    /**
     * Set new total fuel used at arrival airport as a result of rerouting.
     * 
     * @param nominalFuel
     */
    public void setNominal(FuelUsagePoint nominalFuel){
    	assert (this.planned != null);
    	this.nominal = nominalFuel;
    }
    
    /**
     * Set the nominal FuelUsagePoint to the value from remodeled FlightLeg from TrajectoryModeler
     * 
     * @param flightLegCopy
     */
    public void updateForReroute(FuelUsageRecord flightLegCopy){
    	this.nominal = flightLegCopy.nominal;
    }
    
    /**
     * Adjust delay fuel used based on delay observed at last simulated point.
     * 
     * @param fuelUsed kg
     * @param minutes minutes of delay accrued
     * @param stage ASCENT, CRUISE or DESCENT
     * @param inFir last point in Flight Information Region?
     */
    public void addDelay(double fuelUsed, double minutes, FlightStage stage, boolean inFir) {
    	this.delay.adjustForDelays(fuelUsed, minutes, stage, inFir);
    }

    /**
     * 
     * @return fuel burn at highest cruise altitude observed.  kg/min.
     */
    public double getCruiseUsageRate(){
    	return this.cruiseUsageRate;
    }
    
 // ************ PLANNED FUEL GETTERS *****************

    /**
     * 
     * @return kg of fuel
     */
    public double getPlannedClimbUsage(){
    	return this.planned.climbFuel();
    }
    
    /**
     * 
     * @return kg of fuel
     */
    public double getPlannedCruiseUsage(){
    	return this.planned.cruiseFuel();
    }

    
    /**
     * 
     * @return kg of fuel
     */
    public double getPlannedDescentUsage(){
    	return this.planned.descentFuel();
    }
    
    /**
     * 
     * @return kg of fuel
     */
    public double getPlannedTotalFuelUsage() {
        return this.planned.getTotalFuelUsed();
    }

    // ************ NOMINAL FUEL GETTERS *****************    
    
    /**
     * 
     * @return kg of fuel
     */
    public double getNominalClimbUsage(){
    	return this.nominal.climbFuel();
    }
    
    /**
     * 
     * @return kg of fuel
     */
    public double getNominalCruiseUsage(){
    	return this.nominal.cruiseFuel();
    }
    
    /**
     * 
     * @return kg of fuel
     */
    public double getNominalDescentUsage(){
    	return this.nominal.descentFuel();
    }
    
    /**
     * 
     * @return kg of fuel
     */
    public double getNominalTotalFuelUsage() {
        return this.nominal.getTotalFuelUsed();
    }

    
	// ************ DELAY FUEL GETTERS *****************

    /**
     * 
     * @return kg of fuel
     */
    public double getDelayClimbUsage(){
    	return this.delay.climbFuel();
    }
    
    /**
     * 
     * @return kg of fuel
     */
    public double getDelayCruiseUsage(){
    	return this.delay.cruiseFuel();
    }
    
    /**
     * 
     * @return kg of fuel
     */
    public double getDelayDescentUsage(){
    	return this.delay.descentFuel();
    }
    
    /**
     * 
     * @return kg of fuel
     */
    public double getDelayTotalFuelUsage(){
    	return this.delay.getTotalFuelUsed();
    }

    
	// ************ TOTAL FUEL GETTERS *****************
    
    /**
     * nominal + delay climb fuel usage.
     * 
     * @return kg of fuel
     */
    public double getActualClimbFuelUsage() {
        return this.nominal.climbFuel() + this.delay.climbFuel();
    }
    
    /**
     * nominal + delay cruise fuel usage.
     * 
     * @return kg of fuel
     */
    public double getActualCruiseFuelUsage() {
        return this.nominal.cruiseFuel() + this.delay.cruiseFuel();
    }
    
    /**
     * nominal + delay descent fuel usage.
     * 
     * @return kg of fuel
     */
    public double getActualDescentFuelUsage() {
        return this.nominal.descentFuel() + this.delay.descentFuel();
    }
    
    
    /**
     * Total nominal fuel + Total delay fuel.
     * 
     * @return kg of fuel
     */
    public double getActualTotalFuelUsage() {
        return this.nominal.getTotalFuelUsed() + this.delay.getTotalFuelUsed();
    }
    

    
    /***************************************************************
     ********* U.S. Flight Information Region Getters **************
     ***************************************************************/
    
    /**
     * 
     * @return minutes inside FIR
     */
    public double getPlannedFirTime() {
    	return this.planned.firTime();
    }
    
    /**
     * 
     * @return kg of fuel
     */
    public double getPlannedFirFuel(){
    	return this.planned.firFuel();
    }
    
    /**
     * 
     * @return minutes inside FIR
     */
    public double getNominalFirTime() {
    	return this.nominal.firTime();
    }
    
    /**
     * 
     * @return kg of fuel
     */
    public double getNominalFirFuel(){
    	return this.nominal.firFuel();
    }
    
    /**
     * 
     * @return kg of fuel
     */
    public double getDelayFirTime(){
    	return this.delay.firTime();
    }
    
    /**
     * 
     * @return kg of fuel
     */
    public double getDelayFirFuel(){
    	return this.delay.firFuel();
    }
    
    
    /**
     * Total nominal time + Total delay time.
     * 
     * @return minutes inside FIR
     */
    public double getActualTotalFirTime(){
    	return this.nominal.firTime() + this.delay.firTime();
    }
    
    /**
     * Total nominal fuel + Total delay fuel.
     * 
     * @return kg of fuel
     */
    public double getActualTotalFirFuel(){
    	return this.nominal.firFuel() + this.delay.firFuel();
    }
}
