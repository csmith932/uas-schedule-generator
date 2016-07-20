/**
 * Copyright "TBD", Metron Aviation & CSSI.  All rights reserved.
 * 
 * This computer Software was developed with the sponsorship of the U.S. Government
 * under Contract No. DTFAWA-10-D-00033, which has a copyright license in accordance with AMS 3.5-13.(c)(1).
 */

package gov.faa.ang.swac.common.flightmodeling;

import java.io.Serializable;

import gov.faa.ang.swac.common.entities.Carrier;
import gov.faa.ang.swac.common.flightmodeling.fileio.BadaRecord;

/**
 * Represents an aircraft. It is primarily based on data contained in {@link ScheduleRecord}.
 * @author Jason Femino - CSSI, Inc.
 */
public class Aircraft implements Cloneable, Serializable
{
	public enum PhysicalClass
	{
		J { @Override public String description() { return "Jet"; } },
		T { @Override public String description() { return "Turboprop"; } },
		P { @Override public String description() { return "Piston"; } };
		
		public abstract String description();
	}

	//---------------------
	// Static class members
	//---------------------
	// toString related members
	private static final String SEP = ",";
	public static final String TEXT_RECORD_KEY = "AIRCRAFT: carrierId, badaAircraftType, etmsAircraftType, physicalClass, userClass, atoUserClass, turnAroundCategory, enrouteCategory, climbDescentCategory, EquipmentSuffix";
	
	//-----------------------
	// Instance class members
	//-----------------------
	private String carrierId = null;  // An aircraft can have a carrierId but not a carrier.
	private Carrier carrier = null;
    private String filedBadaAircraftType = null;
    private String filedEtmsAircraftType = null;
    private final PhysicalClass physicalClass;
    private final String userClass;
    private BadaRecord badaRecord = null;
    private String atoUserClass = null;
	private EquipmentSuffix equipmentSuffix = new EquipmentSuffix();
	

	/**
	 * Constructs an {@link Aircraft} from a {@link ScheduleRecord}.
	 * @param scheduleRecord
	 */
	public Aircraft(ScheduleRecord scheduleRecord)
	{
		setCarrierId(scheduleRecord.carrierId());
		this.filedBadaAircraftType = scheduleRecord.badaAircraftType;
		this.filedEtmsAircraftType = scheduleRecord.etmsAircraftType;
		this.physicalClass = scheduleRecord.physicalClass;
		this.userClass = scheduleRecord.userClass;
		this.atoUserClass = scheduleRecord.atoUserClass;
	}
	
	/**
	 * Copy constructor.
	 * @param aircraft
	 */
	public Aircraft(Aircraft aircraft)
	{
		this.carrierId = aircraft.carrierId;
		this.carrier = aircraft.carrier == null ? null : new Carrier(aircraft.carrier);
		this.filedBadaAircraftType = aircraft.filedBadaAircraftType;
		this.filedEtmsAircraftType = aircraft.filedEtmsAircraftType;
		this.physicalClass = aircraft.physicalClass;
		this.userClass = aircraft.userClass;
		this.badaRecord = aircraft.badaRecord;
		this.atoUserClass = aircraft.atoUserClass;
		this.equipmentSuffix = (aircraft.equipmentSuffix == null ? null : aircraft.equipmentSuffix.clone());
	}
	
	@Override
	public boolean equals(Object o)
	{
		if (o instanceof Aircraft)
		{
			Aircraft aircraft = (Aircraft)o;
			boolean carriersEqual = false;
			if (carrier == null) { 
				carriersEqual = (this.carrierId == null ? aircraft.carrierId == null : this.carrierId.equals(aircraft.carrierId));
				if (carriersEqual) assert( aircraft.carrier == null );
			} else {
				carriersEqual = (aircraft.carrier == null) ? false : carrier.equals(aircraft.carrier);				
			}
			if (carriersEqual &&
				(this.filedBadaAircraftType == null ? aircraft.filedBadaAircraftType == null : this.filedBadaAircraftType.equals(aircraft.filedBadaAircraftType)) &&
				(this.physicalClass == null ? aircraft.physicalClass == null : this.physicalClass.equals(aircraft.physicalClass)) &&
				(this.userClass == null ? aircraft.userClass == null : this.userClass.equals(aircraft.userClass)) &&
				(this.filedBadaAircraftType == null ? aircraft.filedBadaAircraftType == null : this.filedBadaAircraftType.equals(aircraft.filedBadaAircraftType)) &&
				(this.atoUserClass == null ? aircraft.atoUserClass == null : this.atoUserClass.equals(aircraft.atoUserClass)) &&
				(this.filedBadaAircraftType == null ? aircraft.filedBadaAircraftType == null : this.filedBadaAircraftType.equals(aircraft.filedBadaAircraftType)) &&
				(this.equipmentSuffix == null ? aircraft.equipmentSuffix == null : this.equipmentSuffix.equals(aircraft.equipmentSuffix)))
			{
			    return true;
			}
		}
		return false;
	}
	
	/**
	 * Gets the name of the {@link Aircraft} operator.
	 */
	public String carrierId()
	{
		return carrierId;
	}

	/**
	 * Sets the name of the {@link Aircraft} operator.
	 * @param carrierId
	 */
	public void setCarrierId(String carrierId)
	{
		this.carrierId = carrierId;
		this.carrier = null;
	}

	/**
	 * Returns the operator 
	 * @param carrier
	 */
	public Carrier getCarrier() {
		return carrier;
	}
	
	/**
	 * Sets the operator 
	 * @param carrier
	 */
	public void setCarrier(Carrier carrier) {
		this.carrier = carrier;
		this.carrierId = carrier.getCarrierId();
	}

	/**
	 * Gets the name of the BADA (Base of Aircraft DAta) performance database entry.
	 */
	public String filedBadaAircraftType()
	{
		return this.filedBadaAircraftType;
	}

	/**
	 * Sets the name of the BADA (Base of Aircraft DAta) performance database entry.
	 */
	public void setFiledBadaAircraftType(String badaAircraftType)
	{
		this.filedBadaAircraftType = badaAircraftType;
	}

	/**
	 * Gets the name of the ETMS (Enhanced Traffic Management System) aircraft type.
	 */
	public String filedEtmsAircraftType()
	{
		return this.filedEtmsAircraftType;
	}

	/**
	 * Sets the name of the ETMS (Enhanced Traffic Management System) aircraft type.
	 */
	public void setFiledEtmsAircraftType(String etmsAircraftType)
	{
		this.filedEtmsAircraftType = etmsAircraftType;
	}

	/**
	 * Gets the aircraft engine type. 
	 */
	public PhysicalClass physicalClass()
	{
		return this.physicalClass;
	}

	/**
	 * Gets the aircraft engine type. 
	 */
	public String userClass()
	{
		return this.userClass;
	}

	/**
	 * Gets the BADA (Base of Aircraft DAta) performance database entry.
	 */
	public BadaRecord badaRecord()
	{
		return this.badaRecord;
	}

	/**
	 * Sets the BADA (Base of Aircraft DAta) performance database entry.
	 */
	public void setBadaRecord(BadaRecord badaRecord)
	{
		this.badaRecord = badaRecord;
	}

	/**
	 * @return the equipmentSuffix
	 */
	public EquipmentSuffix equipmentSuffix()
	{
		return this.equipmentSuffix;
	}

	/**
	 * @param equipmentSuffix the equipmentSuffix to set
	 */
	public void setEquipmentSuffix(EquipmentSuffix equipmentSuffix)
	{
		this.equipmentSuffix = equipmentSuffix;
	}

	/**
	 * Gets the ATO user class.
	 */
	public String atoUserClass()
	{
		return this.atoUserClass;
	}

	/**
	 * Sets the ATO user class.
	 */
	public void setAtoUserClass(String newUserClass)
	{
		this.atoUserClass = newUserClass;
	}
	
	@Override
	public String toString()
	{
	    return "AIRCRAFT:" +
	    	" " + (this.carrier              == null ? "" : this.carrier) + SEP +
	    	" " + (this.filedBadaAircraftType  == null ? "" : this.filedBadaAircraftType) + SEP +
	    	" " + (this.filedEtmsAircraftType  == null ? "" : this.filedEtmsAircraftType) + SEP +
	    	" " + (this.physicalClass          == null ? "" : this.physicalClass) + SEP +
	    	" " + (this.atoUserClass           == null ? "" : this.atoUserClass) + SEP +
	    	" " + (this.equipmentSuffix		   == null ? "" : this.equipmentSuffix.toString());
	}

	@Override
	public Aircraft clone()
	{
		return new Aircraft(this);
	}
}