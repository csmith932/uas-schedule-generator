/**
 * Copyright "TBD", Metron Aviation & CSSI.  All rights reserved.
 * 
 * This computer Software was developed with the sponsorship of the U.S. Government
 * under Contract No. DTFAWA-10-D-00033, which has a copyright license in accordance with AMS 3.5-13.(c)(1).
 */

package gov.faa.ang.swac.common.flightmodeling;

import gov.faa.ang.swac.common.datatypes.*;
import gov.faa.ang.swac.common.flightmodeling.Aircraft.PhysicalClass;
import gov.faa.ang.swac.datalayer.storage.fileio.HeaderUtils;
import gov.faa.ang.swac.datalayer.storage.fileio.TextSerializable;
import gov.faa.ang.swac.datalayer.storage.fileio.WithHeader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * A simple class whose public members represents the fields of a Schedule File.
 * The class also contains methods to extract more detailed information from the public members.<p>
 * Ostensibly, each {@link ScheduleRecord} represents a {@link FlightPlan}... that is an {@link Aircraft} and a {@link FlightLeg}.
 */
public class ScheduleRecord implements TextSerializable, WithHeader
{
	private static org.apache.log4j.Logger logger = org.apache.log4j.LogManager.getLogger(ScheduleRecord.class);
	public static final String SEP = ",";

	//private final static String aircraftIdPattern = "(\\D+)(\\S*)"; // Some number of NON-DIGITS followed by some number of DIGITS (possibly followed by more characters)
	public Integer idNum = null;  //IDs can be large negative values.  keep as Integer.
	public Timestamp actDate = null;
	public String aircraftId = null;
	public int flightIndex = Integer.MIN_VALUE;
	public String flightPlanType = null;
	public Timestamp gateOutTime = null;
	public String gateOutTimeFlag = null;
	public Timestamp runwayOffTime = null;
	public String runwayOffTimeFlag  = null;
	public Timestamp runwayOnTime = null;
	public String runwayOnTimeFlag = null;
	public Timestamp gateInTime = null;
	public Altitude filedCruiseAltitude = null;
	public double filedSpeed = Double.NaN;
	public String depAprtEtms = null;
	public String arrAprtEtms = null;
	public double depLatitude = Double.NaN;
	public double depLongitude = Double.NaN; 
	public int depElevation = Integer.MIN_VALUE;
	public int depAirportCountryCode = Integer.MIN_VALUE; //HK: merged_airport_data.txt shows no negative country codes. 
	public double arrLatitude = Double.NaN;
	public double arrLongitude = Double.NaN;
	public int arrElevation = Integer.MIN_VALUE;
	public int arrAirportCountryCode = Integer.MIN_VALUE;
	public String etmsAircraftType = null;
	public PhysicalClass physicalClass = null;
	public String userClass = null;
	public String flewFlag = null;
	public String airspaceCode = null;
	public String depAprtIcao = null;
	public String arrAprtIcao = null;
	public String atoUserClass = null;
	public String badaAircraftType = null;
	public String badaSource = null;
	public Boolean scheduledFlag = null;
	public Timestamp scheduledDepTime = null;
	public Timestamp scheduledArrTime = null;
    public String etmsFiledWaypointsRaw = null;
	public List<TrajectoryPoint> etmsFiledWaypoints = null; //PROCESSED VALUES - TO BE REMOVED
	public String field10 = null;
	
    public ScheduleRecord()
    {      
    }
    
    public ScheduleRecord(ScheduleRecord b)
    {
        idNum = new Integer(b.idNum);
        if (b.actDate != null)
            actDate = new Timestamp(b.actDate);
        aircraftId = b.aircraftId;
        flightIndex = b.flightIndex;
        flightPlanType = b.flightPlanType;
        if (b.gateOutTime != null)
            gateOutTime = new Timestamp(b.gateOutTime);
        gateOutTimeFlag = b.gateOutTimeFlag;
        if (b.runwayOffTime != null)
            runwayOffTime = new Timestamp(b.runwayOffTime);
        runwayOffTimeFlag = b.runwayOffTimeFlag;
        if (b.runwayOnTime != null)
            runwayOnTime = new Timestamp(b.runwayOnTime);
        runwayOnTimeFlag = b.runwayOnTimeFlag;
        if (b.gateInTime != null)
            gateInTime = new Timestamp(b.gateInTime);
        filedCruiseAltitude = b.filedCruiseAltitude;
        filedSpeed = b.filedSpeed;
        depAprtEtms = b.depAprtEtms;
        arrAprtEtms = b.arrAprtEtms;
        depLatitude = b.depLatitude;
        depLongitude = b.depLongitude;
        depElevation = b.depElevation;
        depAirportCountryCode = b.depAirportCountryCode;
        arrLatitude = b.arrLatitude;
        arrLongitude = b.arrLongitude;
        arrElevation = b.arrElevation;
        arrAirportCountryCode = b.arrAirportCountryCode;
        etmsAircraftType = b.etmsAircraftType;
        physicalClass = b.physicalClass;
        userClass = b.userClass;
        flewFlag = b.flewFlag;
        airspaceCode = b.airspaceCode;
        depAprtIcao = b.depAprtIcao;
        arrAprtIcao = b.arrAprtIcao;
        atoUserClass = b.atoUserClass;
        badaAircraftType = b.badaAircraftType;
        badaSource = b.badaSource;
        scheduledFlag = b.scheduledFlag;
        if (b.scheduledDepTime != null)
            scheduledDepTime = new Timestamp(b.scheduledDepTime);
        if (b.scheduledArrTime != null)
            scheduledArrTime = new Timestamp(b.scheduledArrTime);
        etmsFiledWaypointsRaw = b.etmsFiledWaypointsRaw;
        field10 = b.field10;
        
        if (this.etmsFiledWaypointsRaw != null &&
            !this.etmsFiledWaypointsRaw.isEmpty())
        {
            this.etmsFiledWaypoints = etmsFiledWaypoints(this.etmsFiledWaypointsRaw);
        }
    }
    
	public String carrierId()
	{
		String carrierId = null;

		if (this.aircraftId != null && !this.aircraftId.isEmpty())
		{
			// Handle general aviation acids like "N133HB" ("N" and "133HB")
			if ( (this.aircraftId.startsWith("N")  && this.aircraftId.charAt(1) >= '0' && this.aircraftId.charAt(1) <= '9') ||
				 (this.aircraftId.startsWith("C")  && this.aircraftId.charAt(1) >= '0' && this.aircraftId.charAt(1) <= '9'))
			{
				carrierId = this.aircraftId.substring(0,1);
			}
			// Handle aviation acids like "TN199Y"("TN" and "199Y")
			else if ( (this.aircraftId.startsWith("TN") && this.aircraftId.charAt(1) >= '0' && this.aircraftId.charAt(2) <= '9') ||
					  (this.aircraftId.startsWith("LN") && this.aircraftId.charAt(1) >= '0' && this.aircraftId.charAt(2) <= '9'))
			{
				carrierId = this.aircraftId.substring(0,2);
			}
			// Handle all other acids, especially commercial carrier flights like "AAL1234" ("AAL" and "1234")
			else if (this.aircraftId.length() >= 3)
			{
				carrierId = this.aircraftId.substring(0,3);
			}
			
			/* JLF TODO: The above parsing method is klunky and incomplete. Consider replacing with the regex above.
			Matcher matcher = Pattern.compile(aircraftIdPattern).matcher(aircraftId);
			if (matcher.matches())
			{
				carrierId = matcher.group(1);
			}
			*/
		}
		
		return carrierId;
	}
	
	public String flightId()
	{
		String flightId = null;

		if (this.aircraftId != null && !this.aircraftId.isEmpty())
		{
			// Handle general aviation acids like "N133HB" ("N" and "133HB")
			if ( (this.aircraftId.startsWith("N")  && this.aircraftId.charAt(1) >= '0' && this.aircraftId.charAt(1) <= '9') ||
				 (this.aircraftId.startsWith("C")  && this.aircraftId.charAt(1) >= '0' && this.aircraftId.charAt(1) <= '9'))
			{
				flightId = this.aircraftId.substring(1);
			}
			// Handle aviation acids like "TN199Y"("TN" and "199Y")
			else if ( (this.aircraftId.startsWith("TN") && this.aircraftId.charAt(1) >= '0' && this.aircraftId.charAt(2) <= '9') ||
					  (this.aircraftId.startsWith("LN") && this.aircraftId.charAt(1) >= '0' && this.aircraftId.charAt(2) <= '9'))
			{
				flightId = this.aircraftId.substring(2);
			}
			// Handle all other acids, especially commercial carrier flights like "AAL1234" ("AAL" and "1234")
			else if (this.aircraftId.length() >= 3)
			{
				flightId = this.aircraftId.substring(3);
			}

			/* JLF TODO: The above parsing method is klunky and incomplete. Consider replacing with the regex above.
			Matcher matcher = Pattern.compile(aircraftIdPattern).matcher(aircraftId);
			if (matcher.matches())
			{
				flightId = matcher.group(2);
			}
			*/
		}

		return flightId;
	}
    
    @Override
	public String toString()
	{
		return (this.idNum == null ? "" : this.idNum) + SEP +
				(this.actDate == null ? "" : this.actDate.toString().substring(0, 8)) + SEP +
				(this.aircraftId == null ? "" : this.aircraftId) + SEP +
				(this.flightIndex == Integer.MIN_VALUE ? "" : this.flightIndex) + SEP +
				(this.flightPlanType == null ? "" : this.flightPlanType) + SEP +
				(this.gateOutTime == null ? "" : this.gateOutTime.toString()) + SEP +
				(this.gateOutTimeFlag == null ? "" : this.gateOutTimeFlag) + SEP +
				(this.runwayOffTime == null ? "" : this.runwayOffTime.toString()) + SEP +
				(this.runwayOffTimeFlag == null ? "" : this.runwayOffTimeFlag ) + SEP +
				(this.runwayOnTime == null ? "" : this.runwayOnTime.toString()) + SEP +
				(this.runwayOnTimeFlag == null ? "" : this.runwayOnTimeFlag) + SEP +
				(this.gateInTime == null ? "" : this.gateInTime.toString()) + SEP +
				(this.filedCruiseAltitude == null ? "" : this.filedCruiseAltitude.flightLevel().doubleValue()) + SEP +
				(Double.isNaN(this.filedSpeed) ? "" : this.filedSpeed) + SEP +
				(this.depAprtEtms == null ? "" : this.depAprtEtms) + SEP +
				(this.arrAprtEtms == null ? "" : this.arrAprtEtms) + SEP +
				(Double.isNaN(this.depLatitude) ? "" : this.depLatitude) + SEP +
				(Double.isNaN(this.depLongitude) ? "" : this.depLongitude) + SEP +
				(this.depElevation == Integer.MIN_VALUE ? "" : this.depElevation) + SEP +
				(this.depAirportCountryCode == Integer.MIN_VALUE ? "" : this.depAirportCountryCode) + SEP +
				(Double.isNaN(this.arrLatitude) ? "" : this.arrLatitude) + SEP +
				(Double.isNaN(this.arrLongitude) ? "" : this.arrLongitude) + SEP +
				(this.arrElevation == Integer.MIN_VALUE ? "" : this.arrElevation) + SEP +
				(this.arrAirportCountryCode == Integer.MIN_VALUE ? "" : this.arrAirportCountryCode) + SEP +
				(this.etmsAircraftType == null ? "" : this.etmsAircraftType) + SEP +
				(this.physicalClass == null ? "-" : this.physicalClass) + SEP +
				(this.userClass == null ? "" : this.userClass) + SEP +
				(this.flewFlag == null ? "" : this.flewFlag) + SEP +
				(this.airspaceCode == null ? "" : this.airspaceCode) + SEP +
				(this.depAprtIcao == null ? "" : this.depAprtIcao) + SEP +
				(this.arrAprtIcao == null ? "" : this.arrAprtIcao) + SEP +
				(this.atoUserClass == null ? "" : this.atoUserClass) + SEP +
				(this.badaAircraftType == null ? "" : this.badaAircraftType) + SEP +
				(this.badaSource == null ? "" : this.badaSource) + SEP +
				(this.scheduledFlag == null ? "" : (this.scheduledFlag ? "Y":"N")) + SEP +
                (this.scheduledDepTime==null ? "":this.scheduledDepTime.toString())+ SEP +
                (this.scheduledArrTime==null ? "":this.scheduledArrTime.toString()) + SEP +
                (this.etmsFiledWaypointsRaw == null ? "" : this.etmsFiledWaypointsRaw) + //SEP (No comma if no field10.)
                (this.field10 == null || this.field10.isEmpty() ? "" : SEP + this.field10);
	}

	private static enum Field
    {
        ID_NUM,                   // 0
        ACT_DATE,
        AIRCRAFT_ID,
        FLIGHT_INDEX,
        FLIGHT_TYPE_PLAN,
        GATE_OUT_TIME,
        GATE_OUT_TIME_FLAG,
        RUNWAY_OFF_TIME,
        RUNWAY_OFF_TIME_FLAG,
        RUNWAY_ON_TIME,
        RUNWAY_ON_TIME_FLAG,      // 10
        GATE_IN_TIME,
        FILED_FLIGHT_LEVEL,
        FILED_SPEED,
        DEP_AIRPORT,
        ARR_AIRPORT,
        DEP_LATITUDE,
        DEP_LONGITUDE,
        DEP_ELEVATION,
        DEP_AIRPORT_COUNTRY_CODE,
        ARR_LATITUDE,             // 20
        ARR_LONGITUDE,
        ARR_ELEVATION,
        ARR_AIRPORT_COUNTRY_CODE,
        ETMS_AIRCRAFT_TYPE,
        PHYSICAL_CLASS,
        USER_CLASS,
        FLEW_FLAG,
        AIRSPACE_CODE,
        DEP_ICAO,
        ARR_ICAO,                 // 30
        ATO_USER_CLASS,
        BADA_AIRCRAFT_TYPE,
        BADA_AIRCRAFT_TYPE_SOURCE,
        SCHEDULED_FLAG,
        SCHEDULED_DEP_TIME,
        SCHEDULED_ARR_TIME,
        ETMS_FILED_WAYPOINTS,
        FIELD_10;                 // 39
    }
	
	private static final Pattern[] patterns = compilePatterns();
	
    private static Pattern[] compilePatterns()
    {
    	Pattern[] retVal = new Pattern[Field.values().length];

        retVal[Field.ID_NUM.ordinal()] = Pattern.compile(Patterns.INTEGER);
        retVal[Field.ACT_DATE.ordinal()] = Pattern.compile(Patterns.DATE);
        retVal[Field.AIRCRAFT_ID.ordinal()] = Pattern.compile(Patterns.STRING);
        retVal[Field.FLIGHT_INDEX.ordinal()] = Pattern.compile(Patterns.INTEGER);
        retVal[Field.FLIGHT_TYPE_PLAN.ordinal()] = Pattern.compile(Patterns.STRING);
        retVal[Field.GATE_OUT_TIME.ordinal()] = Pattern.compile(Patterns.DATE_TIME);
        retVal[Field.GATE_OUT_TIME_FLAG.ordinal()] = Pattern.compile(Patterns.STRING);
        retVal[Field.RUNWAY_OFF_TIME.ordinal()] = Pattern.compile(Patterns.DATE_TIME);
        retVal[Field.RUNWAY_OFF_TIME_FLAG.ordinal()] = Pattern.compile(Patterns.STRING);
        retVal[Field.RUNWAY_ON_TIME.ordinal()] = Pattern.compile(Patterns.DATE_TIME);
        retVal[Field.RUNWAY_ON_TIME_FLAG.ordinal()] = Pattern.compile(Patterns.STRING);
        retVal[Field.GATE_IN_TIME.ordinal()] = Pattern.compile(Patterns.DATE_TIME);
        retVal[Field.FILED_FLIGHT_LEVEL.ordinal()] = Pattern.compile(Patterns.FLOAT); // Logically should be an integer, but printed as a decimal in the schedule file (converted to integer below)
        retVal[Field.FILED_SPEED.ordinal()] = Pattern.compile(Patterns.FLOAT); // Logically should be an integer, but printed as a decimal in the schedule file (converted to integer below)
        retVal[Field.DEP_AIRPORT.ordinal()] = Pattern.compile("(\\S+)");
        retVal[Field.ARR_AIRPORT.ordinal()] = Pattern.compile("(\\S+)");
        retVal[Field.DEP_LATITUDE.ordinal()] = Pattern.compile(Patterns.FLOAT);
        retVal[Field.DEP_LONGITUDE.ordinal()] = Pattern.compile(Patterns.FLOAT);
        retVal[Field.DEP_ELEVATION.ordinal()] = Pattern.compile(Patterns.INTEGER);
        retVal[Field.DEP_AIRPORT_COUNTRY_CODE.ordinal()] = Pattern.compile(Patterns.INTEGER);
        retVal[Field.ARR_LATITUDE.ordinal()] = Pattern.compile(Patterns.FLOAT);
        retVal[Field.ARR_LONGITUDE.ordinal()] = Pattern.compile(Patterns.FLOAT);
        retVal[Field.ARR_ELEVATION.ordinal()] = Pattern.compile(Patterns.INTEGER);
        retVal[Field.ARR_AIRPORT_COUNTRY_CODE.ordinal()] = Pattern.compile(Patterns.INTEGER);
        retVal[Field.ETMS_AIRCRAFT_TYPE.ordinal()] = Pattern.compile(Patterns.WORD);
        retVal[Field.PHYSICAL_CLASS.ordinal()] = Pattern.compile("(\\S)");
        retVal[Field.USER_CLASS.ordinal()] = Pattern.compile(Patterns.STRING);
        retVal[Field.FLEW_FLAG.ordinal()] = Pattern.compile(Patterns.STRING);
        retVal[Field.AIRSPACE_CODE.ordinal()] = Pattern.compile(Patterns.STRING);
        retVal[Field.DEP_ICAO.ordinal()] = Pattern.compile(Patterns.WORD);
        retVal[Field.ARR_ICAO.ordinal()] = Pattern.compile(Patterns.WORD);
        retVal[Field.ATO_USER_CLASS.ordinal()] = Pattern.compile(Patterns.STRING);
        retVal[Field.BADA_AIRCRAFT_TYPE.ordinal()] = Pattern.compile(Patterns.WORD);
        retVal[Field.BADA_AIRCRAFT_TYPE_SOURCE.ordinal()] = Pattern.compile(Patterns.STRING);
        retVal[Field.SCHEDULED_FLAG.ordinal()] = Pattern.compile(Patterns.STRING);
        retVal[Field.SCHEDULED_DEP_TIME.ordinal()] = Pattern.compile(Patterns.DATE_TIME);
        retVal[Field.SCHEDULED_ARR_TIME.ordinal()] = Pattern.compile(Patterns.DATE_TIME);
        retVal[Field.ETMS_FILED_WAYPOINTS.ordinal()] = Pattern.compile("(-?\\d+/-?\\d+ *)*");
        retVal[Field.FIELD_10.ordinal()] = Pattern.compile(Patterns.STRING);

        return retVal;
    }
	
	@Override
	public void readItem(BufferedReader reader) throws IOException
	{
		// Create a new ScheduleRecord from the current "line"...
        String[] currentFields = reader.readLine().split("\\s*,\\s*"); // Split will also strip whitespace from the ends of fields
        
        if (!isEmpty(Field.ID_NUM, currentFields)                    && getField(Field.ID_NUM, currentFields).length() > 0)                   { this.idNum                 =             Integer.valueOf( getField(Field.ID_NUM, currentFields) ); }
        if (!isEmpty(Field.ACT_DATE, currentFields)                  && getField(Field.ACT_DATE, currentFields).length() > 0)                 { this.actDate               =         Timestamp.myValueOf( getField(Field.ACT_DATE, currentFields) ); }
        if (!isEmpty(Field.AIRCRAFT_ID, currentFields)               && getField(Field.AIRCRAFT_ID, currentFields).length() > 0)              { this.aircraftId            =                              getField(Field.AIRCRAFT_ID, currentFields); }
        if (!isEmpty(Field.FLIGHT_INDEX, currentFields)              && getField(Field.FLIGHT_INDEX, currentFields).length() > 0)             { this.flightIndex           =             Integer.valueOf( getField(Field.FLIGHT_INDEX, currentFields) ); }
        if (!isEmpty(Field.FLIGHT_TYPE_PLAN, currentFields)          && getField(Field.FLIGHT_TYPE_PLAN, currentFields).length() > 0)         { this.flightPlanType        =                              getField(Field.FLIGHT_TYPE_PLAN, currentFields); }
        if (!isEmpty(Field.GATE_OUT_TIME, currentFields)             && getField(Field.GATE_OUT_TIME, currentFields).length() > 0)            { this.gateOutTime           =         Timestamp.myValueOf( getField(Field.GATE_OUT_TIME, currentFields) ); }
        if (!isEmpty(Field.GATE_OUT_TIME_FLAG, currentFields)        && getField(Field.GATE_OUT_TIME_FLAG, currentFields).length() > 0)       { this.gateOutTimeFlag       =                              getField(Field.GATE_OUT_TIME_FLAG, currentFields); }
        if (!isEmpty(Field.RUNWAY_OFF_TIME, currentFields)           && getField(Field.RUNWAY_OFF_TIME, currentFields).length() > 0)          { this.runwayOffTime         =         Timestamp.myValueOf( getField(Field.RUNWAY_OFF_TIME, currentFields) ); }
        if (!isEmpty(Field.RUNWAY_OFF_TIME_FLAG, currentFields)      && getField(Field.RUNWAY_OFF_TIME_FLAG, currentFields).length() > 0)     { this.runwayOffTimeFlag     =                              getField(Field.RUNWAY_OFF_TIME_FLAG, currentFields); }
        if (!isEmpty(Field.RUNWAY_ON_TIME, currentFields)            && getField(Field.RUNWAY_ON_TIME, currentFields).length() > 0)           { this.runwayOnTime          =         Timestamp.myValueOf( getField(Field.RUNWAY_ON_TIME, currentFields) ); }
        if (!isEmpty(Field.RUNWAY_ON_TIME_FLAG, currentFields)       && getField(Field.RUNWAY_ON_TIME_FLAG, currentFields).length() > 0)      { this.runwayOnTimeFlag      =                              getField(Field.RUNWAY_ON_TIME_FLAG, currentFields); }
        if (!isEmpty(Field.GATE_IN_TIME, currentFields)              && getField(Field.GATE_IN_TIME, currentFields).length() > 0)             { this.gateInTime            =         Timestamp.myValueOf( getField(Field.GATE_IN_TIME, currentFields) ); }
        if (!isEmpty(Field.FILED_FLIGHT_LEVEL, currentFields)        && getField(Field.FILED_FLIGHT_LEVEL, currentFields).length() > 0)       { this.filedCruiseAltitude   = Altitude.valueOfFeet(Double.valueOf( getField(Field.FILED_FLIGHT_LEVEL, currentFields) ) * 100.0); } // Convert flight level to altitude
        if (!isEmpty(Field.FILED_SPEED, currentFields)               && getField(Field.FILED_SPEED, currentFields).length() > 0)              { this.filedSpeed            =              Double.valueOf( getField(Field.FILED_SPEED, currentFields) ); }
        if (!isEmpty(Field.DEP_AIRPORT, currentFields)               && getField(Field.DEP_AIRPORT, currentFields).length() > 0)              { this.depAprtEtms            =                              getField(Field.DEP_AIRPORT, currentFields); }
        if (!isEmpty(Field.ARR_AIRPORT, currentFields)               && getField(Field.ARR_AIRPORT, currentFields).length() > 0)              { this.arrAprtEtms            =                              getField(Field.ARR_AIRPORT, currentFields); }
        if (!isEmpty(Field.DEP_LATITUDE, currentFields)              && getField(Field.DEP_LATITUDE, currentFields).length() > 0)             { this.depLatitude           =              Double.valueOf( getField(Field.DEP_LATITUDE, currentFields) ); }
        if (!isEmpty(Field.DEP_LONGITUDE, currentFields)             && getField(Field.DEP_LONGITUDE, currentFields).length() > 0)            { this.depLongitude          =              Double.valueOf( getField(Field.DEP_LONGITUDE, currentFields) ); }
        if (!isEmpty(Field.DEP_ELEVATION, currentFields)             && getField(Field.DEP_ELEVATION, currentFields).length() > 0)            { this.depElevation          =             Integer.valueOf( getField(Field.DEP_ELEVATION, currentFields) ); }
        if (!isEmpty(Field.DEP_AIRPORT_COUNTRY_CODE, currentFields)  && getField(Field.DEP_AIRPORT_COUNTRY_CODE, currentFields).length() > 0) { this.depAirportCountryCode =             Integer.valueOf( getField(Field.DEP_AIRPORT_COUNTRY_CODE, currentFields) ); }
        if (!isEmpty(Field.ARR_LATITUDE, currentFields)              && getField(Field.ARR_LATITUDE, currentFields).length() > 0)             { this.arrLatitude           =              Double.valueOf( getField(Field.ARR_LATITUDE, currentFields) ); }
        if (!isEmpty(Field.ARR_LONGITUDE, currentFields)             && getField(Field.ARR_LONGITUDE, currentFields).length() > 0)            { this.arrLongitude          =              Double.valueOf( getField(Field.ARR_LONGITUDE, currentFields) ); }
        if (!isEmpty(Field.ARR_ELEVATION, currentFields)             && getField(Field.ARR_ELEVATION, currentFields).length() > 0)            { this.arrElevation          =             Integer.valueOf( getField(Field.ARR_ELEVATION, currentFields) ); }
        if (!isEmpty(Field.ARR_AIRPORT_COUNTRY_CODE, currentFields)  && getField(Field.ARR_AIRPORT_COUNTRY_CODE, currentFields).length() > 0) { this.arrAirportCountryCode =             Integer.valueOf( getField(Field.ARR_AIRPORT_COUNTRY_CODE, currentFields) ); }
        if (!isEmpty(Field.ETMS_AIRCRAFT_TYPE, currentFields)        && getField(Field.ETMS_AIRCRAFT_TYPE, currentFields).length() > 0)       { this.etmsAircraftType      =                              getField(Field.ETMS_AIRCRAFT_TYPE, currentFields); }
        if (!isEmpty(Field.PHYSICAL_CLASS, currentFields)) { String s = getField(Field.PHYSICAL_CLASS, currentFields); if (s.length() > 0 && !s.equals("-")){ this.physicalClass         =               PhysicalClass.valueOf( s ); }}
        if (!isEmpty(Field.USER_CLASS, currentFields)                && getField(Field.USER_CLASS, currentFields).length() > 0)               { this.userClass             =                              getField(Field.USER_CLASS, currentFields); }
        if (!isEmpty(Field.FLEW_FLAG, currentFields)                 && getField(Field.FLEW_FLAG, currentFields).length() > 0)                { this.flewFlag              =                              getField(Field.FLEW_FLAG, currentFields); }
        if (!isEmpty(Field.AIRSPACE_CODE, currentFields)             && getField(Field.AIRSPACE_CODE, currentFields).length() > 0)            { this.airspaceCode          =                              getField(Field.AIRSPACE_CODE, currentFields); }
        if (!isEmpty(Field.DEP_ICAO, currentFields)                  && getField(Field.DEP_ICAO, currentFields).length() > 0)                 { this.depAprtIcao               =                              getField(Field.DEP_ICAO, currentFields); }
        if (!isEmpty(Field.ARR_ICAO, currentFields)                  && getField(Field.ARR_ICAO, currentFields).length() > 0)                 { this.arrAprtIcao               =                              getField(Field.ARR_ICAO, currentFields); }
        if (!isEmpty(Field.ATO_USER_CLASS, currentFields)            && getField(Field.ATO_USER_CLASS, currentFields).length() > 0)           { this.atoUserClass          =                              getField(Field.ATO_USER_CLASS, currentFields); }
        if (!isEmpty(Field.BADA_AIRCRAFT_TYPE, currentFields)        && getField(Field.BADA_AIRCRAFT_TYPE, currentFields).length() > 0)       { this.badaAircraftType      =                              getField(Field.BADA_AIRCRAFT_TYPE, currentFields); }
        if (!isEmpty(Field.BADA_AIRCRAFT_TYPE_SOURCE, currentFields) && getField(Field.BADA_AIRCRAFT_TYPE_SOURCE, currentFields).length() > 0){ this.badaSource            =                              getField(Field.BADA_AIRCRAFT_TYPE_SOURCE, currentFields); }
        if (!isEmpty(Field.SCHEDULED_FLAG, currentFields)            && getField(Field.SCHEDULED_FLAG, currentFields).length() > 0)           { this.scheduledFlag         =                              getField(Field.SCHEDULED_FLAG, currentFields).compareTo("Y") == 0 ? true:false;  }
        if (!isEmpty(Field.SCHEDULED_DEP_TIME, currentFields)        && getField(Field.SCHEDULED_DEP_TIME, currentFields).length() > 0)       { this.scheduledDepTime      =         Timestamp.myValueOf( getField(Field.SCHEDULED_DEP_TIME, currentFields) ); }
        if (!isEmpty(Field.SCHEDULED_ARR_TIME, currentFields)        && getField(Field.SCHEDULED_ARR_TIME, currentFields).length() > 0)       { this.scheduledArrTime      =         Timestamp.myValueOf( getField(Field.SCHEDULED_ARR_TIME, currentFields) ); }
        if (!isEmpty(Field.ETMS_FILED_WAYPOINTS, currentFields)      && getField(Field.ETMS_FILED_WAYPOINTS, currentFields).length() > 0)     { this.etmsFiledWaypointsRaw =                              getField(Field.ETMS_FILED_WAYPOINTS, currentFields); }
        if (!isEmpty(Field.FIELD_10, currentFields)                  && getField(Field.FIELD_10, currentFields).length() > 0)                 { this.field10               =                              getField(Field.FIELD_10, currentFields); }

        if (this.etmsFiledWaypointsRaw != null &&
            !this.etmsFiledWaypointsRaw.isEmpty())
        {
            this.etmsFiledWaypoints = etmsFiledWaypoints(this.etmsFiledWaypointsRaw);
        }
	}
	
	private static boolean isEmpty(Field index, String[] currentFields)
    {
        if (index.ordinal() < currentFields.length)
        { 
            return currentFields[index.ordinal()].isEmpty(); 
        }
        return true;
    }

    private static String getField(Field index, String[] currentFields)
    {
        if (index.ordinal() < currentFields.length)
        {
            String field = currentFields[index.ordinal()];
            Matcher fieldMatcher = patterns[index.ordinal()].matcher(field);
            if (!fieldMatcher.matches())
            {
                logger.fatal("Schedule ID " + currentFields[Field.ID_NUM.ordinal()] + ": field " + index.name() + " \"" + field + "\" doesn't match pattern \"" + patterns[index.ordinal()].toString() + "\"!");
                throw new RuntimeException();
            }

            return fieldMatcher.group().trim();
        }
        
        return "";
    }

    /**
     * Reads this {@link ScheduleRecord}'s ETMS {@link TrajectoryPoint} {@link String} and returns a {@link List} of {@link TrajectoryPoint}s. 
     * <p>
     * Each {@link TrajectoryPoint} entry is separated by whitespace. {@link TrajectoryPoint} entries are themselves a series of lat/lon fields, each separated by "<code>/</code>":
     * <table border="1">
     * <tr> <td><b>Field</b></td>                <td><b>Occurrence</b></td>  <td><b>Units</b></td>     <td><b>Format</b></td>                                        <td><b>Description</b></td></tr>
     * <tr> <td>Latitude</td>                    <td>Required</td>           <td>{@link integer}</td>  <td>minutes of angle, North-positive</td>                     <td></td> </tr>
     * <tr> <td>Longitude</td>                   <td>Required</td>           <td>{@link integer}</td>  <td>minutes of angle, West-positive (NASPAC convention)</td>  <td></td> </tr>
     * </table>
     * <p>
     * <b>e.g.</b> "<code>1706/4879 1713/4880 1746/4883</code>" is equivalent to:
     * <ol>
     *    <li> {@link TrajectoryPoint} at 28.433&deg; N, 81.316&deg W</li>
     *    <li> {@link TrajectoryPoint} at 28.550&deg; N, 81.333&deg W</li>
     *    <li> {@link TrajectoryPoint} at 29.100&deg; N, 81.383&deg W</li>
     * </ol>
     */
    public static List<TrajectoryPoint> etmsFiledWaypoints(String etmsFiledWaypoints)
    {
        List<TrajectoryPoint> waypoints = new ArrayList<TrajectoryPoint>();
        
        if (etmsFiledWaypoints != null)
        {
            String[] waypointPairs = etmsFiledWaypoints.split(" "); // Split up waypoints into "lat/lon" pairs
            double lat, lon;
            
            for (String waypointPair : waypointPairs)
            {
                String[] latLonPair = waypointPair.split("/");  // Split up waypoint into "lat" and "lon"
                
                // Convert minutes of angle into degrees
                lat =   Double.valueOf(latLonPair[0]) / 60.0;
                lon = -Double.valueOf(latLonPair[1]) / 60.0; // Schedule file defines West as negative longitude... convert to standard East-positive
                waypoints.add(new TrajectoryPoint(Latitude.valueOfDegrees(lat), Longitude.valueOfDegrees(lon)));
            }
        }
        
        return waypoints;
    }
    
	@Override
	public void writeItem(PrintWriter writer) throws IOException
	{
		writer.println(this.toString());
	}

	@Override
	public long readHeader(BufferedReader reader) throws IOException
	{
		HeaderUtils.readHeaderHashComment(reader);
		return -1;
	}

	@Override
	public void writeHeader(PrintWriter writer, long numRecords) throws IOException
	{
        writer.println("#SWAC-Generated Schedule Record File");
		writer.println("#id_num,act_date,acid,flight_index,flight_plan_type,out_time,out_time_flag,off_time,off_time_flag,on_time,on_time_flag,in_time,filed_altitude,filed_airspeed,etms_departure_airport,etms_arrival_airport,dept_lat,dept_lon,dept_elev,dept_cntry_code,arr_lat,arr_lon,arr_elev,arr_cntry_code,etms_aircraft_type,physical_class,etms_user_class,flew_flag,airspace_code,dept_icao_code,arr_icao_code,ato_user_class,bada_type,bada_type_source,sched_flag,sched_dep_time,sched_arr_time,waypoints");        
	}
}
