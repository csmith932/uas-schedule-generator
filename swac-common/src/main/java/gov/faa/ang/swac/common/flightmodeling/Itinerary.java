/**
 * Copyright "TBD", Metron Aviation & CSSI.  All rights reserved.
 * 
 * This computer Software was developed with the sponsorship of the U.S. Government
 * under Contract No. DTFAWA-10-D-00033, which has a copyright license in accordance with AMS 3.5-13.(c)(1).
 */

package gov.faa.ang.swac.common.flightmodeling;

import gov.faa.ang.swac.common.datatypes.REGION;
import gov.faa.ang.swac.common.datatypes.Timestamp;
import gov.faa.ang.swac.common.entities.Carrier;
import gov.faa.ang.swac.common.flightmodeling.FlightLeg.ModeledState;
import gov.faa.ang.swac.common.flightmodeling.jni.Airframe;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;



/**
 * Represents an IFR (Instrument Flight Rules) itinerary, that is... an {@link Aircraft} and a list of IFR {@link FlightLeg}s.<p>
 * For an itinerary with a single VFR (Visual Flight Rules) flight plan, see {@link ItineraryVFR}.
 * @see ItineraryVFR
 * @author Jason Femino - CSSI, Inc.
 */
public class Itinerary implements Airframe, Serializable, Cloneable 
{
    public enum Type { IFR, VFR_DEP, VFR_ARR, VFR_DEP_ARR }

    //---------------------
    // Static class members
    //---------------------
    
    // toString related members
    public static final String SEP = ",";
    public static final String TEXT_RECORD_KEY = "ITINERARY: Itinerary Type, NASPAC Itinerary Number, ID, Flight Plan Count\n" +
        Aircraft.TEXT_RECORD_KEY + "\n" +
        FlightLeg.TEXT_RECORD_KEY + "\n" +
        "...";
    
    //-----------------------
    // Instance class members
    //-----------------------
    public Aircraft aircraft = null;
    public Type type = null;
    private REGION region;
    


	public Itinerary(Aircraft aircraft, Type type)
    {
    	super();
    	this.type = type;
    	this.flightList = new ArrayList<FlightLeg>();
    	this.setAircraft(new Aircraft(aircraft));
    }

    public Itinerary(Aircraft aircraft, FlightLeg flightLeg, Type type)
    {
    	this(aircraft, type);
    	this.addFlightLeg(new FlightLeg(flightLeg));
    }
    
    public Itinerary(Itinerary org) {
        this.aircraft = (org.aircraft == null ? null : org.aircraft.clone());
        this.dayOverrideFlag = org.dayOverrideFlag;
        this.evolvedEtmsAircraftCategory = org.evolvedEtmsAircraftCategory;
        this.filedEtmsAircraftCategory = org.filedEtmsAircraftCategory;
        this.gateDesignGroupMask = org.gateDesignGroupMask;
        this.pushbackCategory = (org.pushbackCategory == null ? null : org.pushbackCategory.intValue());
        this.region = org.region;
        this.rerouteClearanceCategory = (org.rerouteClearanceCategory == null ? null : org.rerouteClearanceCategory.intValue());
        this.taxiInCategory = (org.taxiInCategory == null ? null : org.taxiInCategory.intValue());
        this.taxiOutCategory = (org.taxiOutCategory == null ? null : org.taxiOutCategory.intValue());
        this.turnAroundCategory = (org.turnAroundCategory == null ? null : org.turnAroundCategory.intValue());
        this.rampCategory = (org.rampCategory == null ? null : org.rampCategory.intValue());  
        this.type = org.type;
        
        this.flightList = new ArrayList<FlightLeg>(org.flightList.size());
        
        for (FlightLeg fl : org.flightList) {
            this.flightList.add(fl.clone());
        }
        
    }
    
    public void addFlightLeg(FlightLeg flightLeg)
    {
    	this.flightList.add(flightLeg);
    	flightLeg.setParentAirframe(this);
    }

    public void removeFlightLeg(int index)
    {
    	FlightLeg flightLeg = this.flightList.remove(index);
    	flightLeg.setParentAirframe(null);
    }

    @Override
    public boolean equals(Object o)
    {
        if (!(o instanceof Itinerary))
        {
            return false;
        }
        
        Itinerary itinerary = (Itinerary)o;
        if (this.type != itinerary.type)
        {
            return false;
        }
        

        // Compare List objects
        if ((this.flightList == null && itinerary.flightList != null) ||
            (this.flightList != null && itinerary.flightList == null) ||
            (this.flightList != null && itinerary.flightList != null && this.flightList.size() != itinerary.flightList.size()) )
        {
            return false;
        }
        
        // Both lists are null (or non-null, and have the same number of objects)
        if (this.flightList != null)
        {
            // Compare lists, object-by-object
            for (int i=0; i<this.flightLegs().size(); i++)
            {
                if (!this.flightList.get(i).equals(itinerary.flightList.get(i)))
                {
                    return false;
                }
            }
        }
        
        return true;
    }

    /**
     * Gets the {@link Type} of this {@link ItineraryVFR}'s {@link FlightLeg}.<p>
     * @see {@link FlightLeg#vfrType(FlightLeg)}
     */
    public Type type()
    {
        return this.type;
    }
    
    
    /**
     * Is this a VFR Or not?
     * 
     */
    public boolean isVFR() {
    	return (this.type != Type.IFR);
    }


    /**
     * Gets the unique serial number given to each {@link Itinerary} object upon creation.<p>
     * <b>Not to be confused with NASPAC {@link #itineraryNumber}.</b>
     */
    public Integer id()
    {
        return this.flightList.get(0).flightId();
    }

    /**
     * Sets the NASPAC itinerary number.
     */
    public List<FlightLeg> flightLegs()
    {
        return Collections.unmodifiableList(this.flightList);
    }
    
    public Aircraft aircraft()
    {
        return this.aircraft;
    }
    
    public void setAircraft(Aircraft aircraft)
    {
        this.aircraft = aircraft;
    }
    
	public REGION getRegion() {
		return region;
	}

	public void setRegion(REGION region) {
		this.region = region;
	}
    
    /**
     * Sets the {@link FlightLeg}s for this {@link ItineraryIFR}.
     */
    public void setFlightLegs(List<FlightLeg> flightLegs)
    {
        if (this.flightList != null)
        {
            this.flightList.clear();
        }
        else
        {
            this.flightList = new ArrayList<FlightLeg>(flightLegs.size());
        }

        this.flightList.addAll(flightLegs);
        for (FlightLeg flightLeg : flightLegs)
        {
        	flightLeg.setParentAirframe(this);
        }
    }
    
    @Override
    public String toString()
    {
        StringBuilder output = new StringBuilder();
        output.append("ITINERARY: " +
                (this.type == null ? "" : this.type) + SEP + " " +
                 this.id() + SEP +  " " +
                (this.flightList == null ? "0" : this.flightList.size()) + "\n" +
                (this.aircraft == null ? "" : this.aircraft.toString()) + "\n");
        
        if ( this.flightList != null )
        {
            for (int i=0; i<this.flightList.size(); i++)
            {
                output.append(this.flightList.get(i).toString());
                
                // Don't append a newline for the last line
                if ( i < this.flightList.size()-1 )
                {
                    output.append("\n");
                }
            }
        }
        
        return output.toString();
    }

    /**
     * Splits {@link Itinerary} into two, returning a new object similar to the original...
     * except that the {@link FlightLeg}s before "flightLegIndex" will remain with the
     * original object, and {@link FlightLeg}s at and after "flightLegIndex" will belong to the new object.<p>
     * This method will have no effect (return <code>null</code>) if the original {@link Itinerary} has 0 {@link FlightLeg}s.
     * @param flightLegIndex
     * @return new {@link Itinerary} (or <code>null</code>)
     */
    public Itinerary split(int flightLegIndex)
    {
    	// Only valid for IFR itineraries
    	if (this.type != Type.IFR) {
    		return null;
    	}
    	
    	if (this.flightList.size() > 0 && flightLegIndex >= 0 && flightLegIndex < this.flightList.size())
    	{
        	
    		// Copy all FlightLegs from index to the end
    		List<FlightLeg> newFlightLegs = new ArrayList<FlightLeg>( this.flightList.size() - flightLegIndex + 1 );

    		int originalFlightLegCount = this.flightList.size();
    		for (int i=flightLegIndex; i<originalFlightLegCount; i++)
    		{
    			newFlightLegs.add( this.flightList.get(flightLegIndex) );
    			this.flightList.remove(flightLegIndex);
    		}
    		
    		// Create a new Itinerary
    		Itinerary newItinerary = new Itinerary(this.aircraft, Type.IFR);
    		newItinerary.setFlightLegs(newFlightLegs);
    		return newItinerary;
    	}
    	
    	return null;
    }
    
    protected String filedEtmsAircraftCategory;
    protected String evolvedEtmsAircraftCategory;

	public String getFiledEtmsAircraftCategory() {
		return filedEtmsAircraftCategory;
	}


	public void setFiledEtmsAircraftCategory(String filedEtmsAircraftCategory) {
		this.filedEtmsAircraftCategory = filedEtmsAircraftCategory;
	}


	public String getEvolvedEtmsAircraftCategory() {
		return evolvedEtmsAircraftCategory;
	}


	public void setEvolvedEtmsAircraftCategory(String evolvedEtmsAircraftCategory) {
		this.evolvedEtmsAircraftCategory = evolvedEtmsAircraftCategory;
	}

    public Itinerary(Itinerary itineraryVfr, Type printType)
    {
		this(itineraryVfr.aircraft(), itineraryVfr.type());
		
		if ( itineraryVfr.flightLegs() == null || itineraryVfr.flightLegs().size() == 0 )
		{
			throw new IllegalStateException();
		}
		
		// Copy and modify the original flight leg and itinerary
		FlightLeg leg = new FlightLeg(itineraryVfr.flightList.get(0));
		this.addFlightLeg(leg);
		
		String airline = null;
 		if (this.aircraft().atoUserClass().toLowerCase().contains("military"))
    	{
 			airline = "ml ";
    	}
    	else
    	{
    		airline = "ga ";
    	}
        
 		this.aircraft().setCarrierId(airline);
 		this.aircraft().setEquipmentSuffix(new EquipmentSuffix());
        
        TrajectoryPoint tp = new TrajectoryPoint();
 		tp.setTimeToNextResource(meanFlyingTimeMins * 60 * 1000);
 		tp.setResourceInfo(leg.departure());
 		
 		if ( (printType == Type.VFR_DEP || printType == Type.VFR_DEP_ARR) &&
        	 (this.type() == Type.VFR_DEP || this.type() == Type.VFR_DEP_ARR) )
        {
        	Terminus departure = this.flightLegs().get(0).departure();
       		Timestamp depTime = departure.runwayDateTime();

            leg.departure().setGateDateTime(depTime); // TODO: is this right? do VFR's skip surface modeling? IFR's use gate time and VFR's use runway?
            leg.arrival().setRunwayDateTime(depTime.minuteAdd(depArrTimeDiffMins));
            leg.arrival().setGateDateTime(depTime.minuteAdd(depArrTimeDiffMins));
            leg.arrival().setAirportName("????");
        }

    	if ( (printType == Type.VFR_ARR || printType == Type.VFR_DEP_ARR) &&
        	 (this.type() == Type.VFR_ARR || this.type() == Type.VFR_DEP_ARR) )
        {
    		leg.arrival().setGateDateTime(leg.arrival().runwayDateTime());
            leg.departure().setGateDateTime(leg.arrival().runwayDateTime().minuteAdd(-1 * depArrTimeDiffMins));
            leg.departure().setRunwayDateTime(leg.arrival().runwayDateTime().minuteAdd(-1 * depArrTimeDiffMins));
            leg.departure().setAirportName("????");
        }
    	
    	tp.setTimestamp(leg.departure().gateDateTime());
        
    	leg.setCrossings(Arrays.asList(new TrajectoryPoint[] { tp }), false);
    	leg.setModeledState(ModeledState.CROSSINGS);
    }
	
	public static final int depArrTimeDiffMins = 13; // difference between dep and arr times in minutes
	public static final int meanFlyingTimeMins = 8;

    private Integer turnAroundCategory = null;
    private Integer pushbackCategory = null;
    private Integer taxiOutCategory = null;
    private Integer taxiInCategory = null;
    private Integer rerouteClearanceCategory = null;
    private Integer rampCategory = null;
    
    private boolean dayOverrideFlag = false;
    
	private List<FlightLeg> flightList = null;

	private String gateDesignGroupMask;
		
	
	@Override
	public Integer airframeId() {
    	return this.id();
	}

	@Override
	public Timestamp activationTimestamp() {
		return this.flightList.get(0).departure().gateDateTime();
	}

	@Override
	public Integer tailNumber() {
		return this.id();
	}

	@Override
	public String airlineIndicator() {
		return this.aircraft().carrierId();
	}
	
	public Carrier getCarrier() {
		return this.aircraft().getCarrier();
	}
	
	public void setCarrier(Carrier carrier) {
		this.aircraft.setCarrier(carrier);
	}

	@Override
	public String aircraftType() {
		String acTypeStr = this.aircraft().filedBadaAircraftType(); // XXX: CSS 7/6/2011 Minor business rule change (sim always uses BADA AC type). May have impact on regression testing
		return acTypeStr == null || acTypeStr.isEmpty() || acTypeStr.equals("HELI") ? "----" : acTypeStr;
	}

	@Override
	public EquipmentSuffix equipmentSuffix() {
		return this.aircraft().equipmentSuffix();
	}

	@Override
	public String equipmentSuffixString() {
		return this.equipmentSuffix().toString();
	}

	@Override
	public Integer turnAroundCategory() {
		return turnAroundCategory;
	}

	public void setTurnAroundCategory(Integer turnAroundCategory) {
		this.turnAroundCategory = turnAroundCategory;
	}

	@Override
	public Integer pushbackCategory() {
		return pushbackCategory;
	}

	public void setPushbackCategory(Integer pushbackCategory) {
		this.pushbackCategory = pushbackCategory;
	}

	@Override
	public Integer taxiOutCategory() {
		return taxiOutCategory;
	}

	public void setTaxiOutCategory(Integer taxiOutCategory) {
		this.taxiOutCategory = taxiOutCategory;
	}

	@Override
	public Integer taxiInCategory() {
		return taxiInCategory;
	}

	public void setTaxiInCategory(Integer taxiInCategory) {
		this.taxiInCategory = taxiInCategory;
	}
	
	public Integer rerouteClearanceCategory() {
		return rerouteClearanceCategory;
	}

	public void setRerouteClearanceCategory(Integer rerouteClearanceCategory) {
		this.rerouteClearanceCategory = rerouteClearanceCategory;
	}
	
	public Integer rampCategory() {
		return rampCategory;
	}
	
	public void setRampCategory(Integer rampCategory) { 
		this.rampCategory = rampCategory;
	}
	
	@Override
	public boolean dayOverrideFlag() {
		return dayOverrideFlag;
	}
	
	public void setDayOverrideFlag(boolean flag) {
		dayOverrideFlag = flag;
	}
	
	@Override
	public List<FlightLeg> flightList() {
		return Collections.unmodifiableList(flightList);
	}

	public String getGateDesignGroupMask() {
		return gateDesignGroupMask;
	}
	
	public void setGateDesignGroupMask(String gateDesignGroupMask) {
		this.gateDesignGroupMask = gateDesignGroupMask;
	}
	
	@Override
	public int compareTo(Airframe o) {
		int retVal = this.activationTimestamp().compareTo(o.activationTimestamp());
		if (retVal == 0)
		{
			return this.id().compareTo(o.airframeId());
		}
		return retVal;
	}

//	@Override
//	public String toString() {
//		return "Airframe [airframeId=" + airframeId() + ", activationTime="
//				+ activationTime() + ", activationTimestamp="
//				+ activationTimestamp() + ", tailNumber=" + tailNumber()
//				+ ", airlineIndicator=" + airlineIndicator() + ", aircraftType="
//				+ aircraftType() + ", equipmentSuffix=" + equipmentSuffix()
//				+ ", equipmentSuffixString=" + equipmentSuffixString()
//				+ ", turnAroundCategory=" + turnAroundCategory
//				+ ", pushbackCategory=" + pushbackCategory
//				+ ", taxiOutCategory=" + taxiOutCategory + ", taxiInCategory="
//				+ taxiInCategory + ", flightList=" + flightList
//				+ "]";
//	}

	@Override
	public void readItem(BufferedReader reader) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void writeItem(PrintWriter writer) throws IOException {
		writer.println(this.toString());
	}

    @Override
    public Itinerary clone() {
        return new Itinerary(this);
    }
}