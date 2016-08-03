package gov.faa.ang.swac.uas.scheduler.vfr;

import gov.faa.ang.swac.common.datatypes.Timestamp;
import gov.faa.ang.swac.common.flightmodeling.Aircraft;
import gov.faa.ang.swac.common.flightmodeling.ScheduleRecord;
import gov.faa.ang.swac.uas.scheduler.airport_data.AirportData;
import gov.faa.ang.swac.uas.scheduler.airport_data.AirportDataMap;
import gov.faa.ang.swac.uas.scheduler.airport_data.AirportDataPair;
import gov.faa.ang.swac.uas.scheduler.data.ASPMTaxiTimes;
import gov.faa.ang.swac.uas.scheduler.flight_data.*;
import gov.faa.ang.swac.uas.scheduler.userclass.ATOPUserClassFinder;

import java.util.ArrayList;
import java.util.List;

public class VfrSchedRecCreator
{
    // Input
    private VFRLocalTimeGenerator vfrLocalTimeGenerator;    
    private Timestamp localDate;    
    private int nextIdNum;
    private int idNumInc; 
    private AirportDataMap airportMap;
    private ASPMTaxiTimes taxiTimes;
    protected VFRHelicopterMap helicopterMap;
    
    // Output
    protected ArrayList<ScheduleRecord> schedRecList;   

    protected VfrSchedRecCreator()
    {
    }

    public VfrSchedRecCreator(
        Timestamp localDate,
        int firstIdNum,
        int increment,
        VFRLocalTimeGenerator vfrLocalTimeGenerator)
    {
        this.vfrLocalTimeGenerator = vfrLocalTimeGenerator;
        this.localDate = localDate;
        this.nextIdNum = firstIdNum;
        this.idNumInc = increment;

        this.schedRecList = new ArrayList<ScheduleRecord>();
    }
    
    public void setAirportMap(AirportDataMap airportMap)
    {
        this.airportMap = airportMap;
    }

    public AirportDataMap getAirportMap()
    {
        return airportMap;
    }

    public void setTaxiTimes(ASPMTaxiTimes taxiTimes)
    {
        this.taxiTimes = taxiTimes;
    }

    public ASPMTaxiTimes getTaxiTimes()
    {
        return taxiTimes;
    }
    
    public void setHelicopterMap(VFRHelicopterMap helicopterMap)
    {
        this.helicopterMap = helicopterMap;
    }

    public VFRHelicopterMap getHelicopterMap()
    {
        return helicopterMap;
    }

    public void setLocalDate(Timestamp localDate)
    {
        this.localDate = localDate;
    }

    public Timestamp getLocalDate()
    {
        return localDate;
    }

    protected void resetScheduleRecords()
    {
        this.schedRecList = new ArrayList<ScheduleRecord>();
    }   
    
    public void populate(List<VFROpsnetAirportData> airports)
    {
        for (VFROpsnetAirportData vfrAirport : airports)
        {
            AirportData airport = 
                this.airportMap.getAirport(vfrAirport.getFaaCode());
            
            int nVfrToAdd = getVfrCount(airport, vfrAirport); 
            if (0 < nVfrToAdd)
            {
                if (airport != null)
                {
                    this.schedRecList.addAll(
                        populateAirport(airport,nVfrToAdd));
                }
            }
        }
    }

    private int getVfrCount(AirportData airport, VFROpsnetAirportData vfrAirport)
    {
        int nVfrToAdd = vfrAirport.getNumVFR();
        
        if (0 < nVfrToAdd && this.helicopterMap != null)
        {
            nVfrToAdd = this.helicopterMap.getNumberVfrOperations(airport,nVfrToAdd);
        }

        return nVfrToAdd;
    }

    public List<ScheduleRecord> populateAirport(
        AirportData airport,
        int nVfrToAdd)
    {
        List<ScheduleRecord> results = new ArrayList<ScheduleRecord>();

        for (int i = 0; i < nVfrToAdd; ++i)
        {
            ScheduleRecord schedRec = createScheduleRecord(airport,i);
            
            results.add(schedRec);
        }

        return results;
    }

    private ScheduleRecord createScheduleRecord(
        AirportData airport,
        int iVfr)
    {
        // Odd indices = arrival, Even = departures
        final boolean departure = (iVfr%2 == 0);        

        final ScheduleRecord schedRec = new ScheduleRecord();
        
        // Identification fields ------------------------------------------------------------------
        schedRec.idNum = this.nextIdNum;
        this.nextIdNum += this.idNumInc;
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
            double taxiTimeSecs = 60
                *taxiTimes.getTaxiOutTime(
                    airport.getFaaCode(),
                    schedRec.aircraftId);
            
            schedRec.gateOutTime = etmsTime.secondAdd(-taxiTimeSecs);
            schedRec.gateOutTimeFlag = "CREATED";
            schedRec.runwayOffTime = etmsTime;
            schedRec.runwayOffTimeFlag = "CREATED";            
        }
        else
        {
            double taxiTimeSecs = 60
                *taxiTimes.getTaxiInTime(
                    airport.getFaaCode(),
                    schedRec.aircraftId);
            
            schedRec.runwayOnTime = etmsTime;
            schedRec.runwayOnTimeFlag = "CREATED";
            schedRec.gateInTime = etmsTime.secondAdd(taxiTimeSecs);
        }

        // User-class fields ----------------------------------------------------------------------
        schedRec.userClass = "G";
        schedRec.atoUserClass = "D VFR";
        
        // Airspace fields ------------------------------------------------------------------------
        //schedRec.flewFlag = null; // previous code did not set it?
        schedRec.airspaceCode = "10";
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

    public ArrayList<ScheduleRecord> getScheduleRecordList()
    {
        return schedRecList;
    }
}
