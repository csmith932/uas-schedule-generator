package gov.faa.ang.swac.uas.scheduler.vfr;

import gov.faa.ang.swac.common.datatypes.Timestamp;
import gov.faa.ang.swac.common.flightmodeling.ScheduleRecord;
import gov.faa.ang.swac.uas.scheduler.airport_data.AirportData;
import gov.faa.ang.swac.uas.scheduler.forecast.MissionAirportPairKey;

import java.util.ArrayList;
import java.util.List;

public class VfrSchedRecCreator
{
    // Input
    protected VFRLocalTimeGenerator vfrLocalTimeGenerator;    
    protected Timestamp localDate;    
    private int nextIdNum;
    protected int idNumInc;
    protected double nominalTaxiTimeMinutes;
    
    protected VfrSchedRecCreator()
    {
    }

    public VfrSchedRecCreator(
        Timestamp localDate,
        int firstIdNum,
        int increment,
        double nominalTaxiTimeMinutes,
        VFRLocalTimeGenerator vfrLocalTimeGenerator)
    {
        this.vfrLocalTimeGenerator = vfrLocalTimeGenerator;
        this.localDate = localDate;
        this.nextIdNum = firstIdNum;
        this.idNumInc = increment;
        this.nominalTaxiTimeMinutes = nominalTaxiTimeMinutes;
    }
    
    public void setLocalDate(Timestamp localDate)
    {
        this.localDate = localDate;
    }

    public Timestamp getLocalDate()
    {
        return localDate;
    }
    
    public List<ScheduleRecord> populateMissionAirportPair(
        MissionAirportPairKey missionAirportPair,
        int nVfrToAdd)
    {
        List<ScheduleRecord> results = new ArrayList<ScheduleRecord>();

        boolean dep = true;
        for (int i = 0; i < nVfrToAdd * 2; ++i)
        {
            ScheduleRecord schedRec = createScheduleRecord(missionAirportPair,this.nextIdNum, dep);
            
            results.add(schedRec);
            
            this.nextIdNum += this.idNumInc;
            dep = !dep;
        }

        return results;
    }

    protected ScheduleRecord createScheduleRecord(
    		MissionAirportPairKey missionAirportPair,
        int iVfr, boolean departure)
    {
        // Odd indices = arrival, Even = departures
        AirportData airport;
        if (departure) {
        	airport = missionAirportPair.airportPair.getOrigin();
        } else {
        	airport = missionAirportPair.airportPair.getDestination();
        }

        final ScheduleRecord schedRec = new ScheduleRecord();
        
        // Identification fields ------------------------------------------------------------------
        schedRec.idNum = iVfr;
        schedRec.actDate = this.localDate;
        schedRec.aircraftId = "V_" + airport.getMostLikelyCode() + "_" + (iVfr+1);
        schedRec.flightIndex = (iVfr+1);
        schedRec.flightPlanType = "VFR";

        // Time fields ----------------------------------------------------------------------------
        Timestamp localTime = 
            this.vfrLocalTimeGenerator.createLocalVfrTime(this.localDate);
        Timestamp etmsTime  = 
            localTime.hourAdd(-airport.getUtcDifference());        
        if (departure)
        {
            double taxiTimeSecs = 60 * this.nominalTaxiTimeMinutes;
            
            schedRec.gateOutTime = etmsTime.secondAdd(-taxiTimeSecs);
            schedRec.gateOutTimeFlag = "CREATED";
            schedRec.runwayOffTime = etmsTime;
            schedRec.runwayOffTimeFlag = "CREATED";            
        }
        else
        {
            double taxiTimeSecs = 60 * this.nominalTaxiTimeMinutes;
            
            schedRec.runwayOnTime = etmsTime;
            schedRec.runwayOnTimeFlag = "CREATED";
            schedRec.gateInTime = etmsTime.secondAdd(taxiTimeSecs);
        }

        // User-class fields ----------------------------------------------------------------------
        schedRec.userClass = "U";
        schedRec.atoUserClass = missionAirportPair.mission.userClass();
        
        // Airspace fields ------------------------------------------------------------------------
        //schedRec.flewFlag = null; // previous code did not set it?
        schedRec.airspaceCode = "10";
        // TODO: Center gives us the opportunity to create a VFR loiter at a random location in the center
        String center = airport.getCenter();
        if (center != null)
        {
            if (center.equals("ZAN"))
            { // Alaska
                schedRec.airspaceCode = "3";
            }
            else if (center.equals("ZHN"))
            { // Hawaii
                schedRec.airspaceCode = "1";
            }
            else if (center.equals("ZLB"))
            { // not real, seems to be Panama
                schedRec.airspaceCode = "-1";
            }
            else if (center.equals("ZSU"))
            { // San Juan
                schedRec.airspaceCode = "5";
            }
            else if (center.equals("ZUA"))
            { // Guam
                schedRec.airspaceCode = "1";
            }
        }

        // Airport --------------------------------------------------------------------------------
        if (departure)
        {
            schedRec.depAprtEtms = airport.getFaaCode();
            schedRec.depAprtIcao = airport.getIcaoCode();
            
            schedRec.depLatitude = airport.getLatitude();
            schedRec.depLongitude = airport.getLongitude();
            schedRec.depElevation = airport.getElevation();
            schedRec.depAirportCountryCode = airport.getCountryCode();
        }
        else
        {
            schedRec.arrAprtEtms = airport.getFaaCode(); 
            schedRec.arrAprtIcao = airport.getIcaoCode();    
            
            schedRec.arrLatitude = airport.getLatitude();
            schedRec.arrLongitude = airport.getLongitude();
            schedRec.arrElevation = airport.getElevation();
            schedRec.arrAirportCountryCode = airport.getCountryCode();           
        }

        // I don't know what to set the remaining fields to.
        // Is null sufficient for VF?
        // TODO: Based on our mission we can populate additional fields for loiters, esp. altitude, location, and duration
        schedRec.filedCruiseAltitude = null;
        schedRec.filedSpeed = Double.NaN;
       
        schedRec.etmsAircraftType = null;
        schedRec.physicalClass = null;
        schedRec.badaAircraftType = null;
        schedRec.badaSource = null;
        
        schedRec.scheduledFlag = false;
        schedRec.scheduledDepTime = null;
        schedRec.scheduledArrTime = null;
        
        schedRec.etmsFiledWaypointsRaw = "";
        schedRec.field10 = "";
        
        return schedRec;
    }
}
