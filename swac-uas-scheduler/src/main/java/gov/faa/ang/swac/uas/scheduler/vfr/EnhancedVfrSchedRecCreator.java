package gov.faa.ang.swac.uas.scheduler.vfr;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import gov.faa.ang.swac.common.datatypes.Altitude;
import gov.faa.ang.swac.common.datatypes.Latitude;
import gov.faa.ang.swac.common.datatypes.Longitude;
import gov.faa.ang.swac.common.datatypes.Timestamp;
import gov.faa.ang.swac.common.flightmodeling.ScheduleRecord;
import gov.faa.ang.swac.common.geometry.GCPoint;
import gov.faa.ang.swac.common.geometry.GCUtilities;
import gov.faa.ang.swac.common.flightmodeling.Aircraft.PhysicalClass;
import gov.faa.ang.swac.uas.scheduler.airport_data.AirportData;
import gov.faa.ang.swac.uas.scheduler.forecast.MissionAirportPairKey;
import gov.faa.ang.swac.uas.scheduler.input.UasVfrRecord;
import gov.faa.ang.swac.uas.scheduler.input.Waypoint;
import gov.faa.ang.swac.uas.scheduler.mathematics.statistics.HQRandom;

public class EnhancedVfrSchedRecCreator extends VfrSchedRecCreator {
	private static final Logger logger = LogManager.getLogger(EnhancedVfrSchedRecCreator.class);
	
	private Map<MissionAirportPairKey, List<UasVfrRecord>> vfrMap;
	private long samplingRandomSeed;
	private int idCounter;
	
	public EnhancedVfrSchedRecCreator(Timestamp startTime, int i, int j, double nominalTaxiTimeMinutes,
			VFRLocalTimeGenerator vfrLocalTimeGenerator, Map<MissionAirportPairKey, List<UasVfrRecord>> vfrMap, int idCounter, long samplingRandomSeed) {
		super(startTime, i, j, nominalTaxiTimeMinutes, vfrLocalTimeGenerator);
		this.vfrMap = vfrMap;
		this.idCounter = idCounter;
		this.samplingRandomSeed = samplingRandomSeed;
	}
	
	@Override
	public List<ScheduleRecord> populateMissionAirportPair(
        MissionAirportPairKey missionAirportPair,
        int nVfrToAdd)
    {
		List<UasVfrRecord> list = this.vfrMap.get(missionAirportPair);
		if (list == null || list.isEmpty()) {
			return super.populateMissionAirportPair(missionAirportPair, nVfrToAdd);
		}
		
		try {
			List<ScheduleRecord> results = new ArrayList<ScheduleRecord>();
			Sampler sampler = new Sampler(list, samplingRandomSeed);

	        for (int i = 0; i < nVfrToAdd; ++i)
	        {
	        	UasVfrRecord vfrRec = sampler.sample();
	            ScheduleRecord schedRec = createScheduleRecord(missionAirportPair, idCounter, vfrRec); // always departure so we populate arrival info
	            idCounter += this.idNumInc;
	            
	            results.add(schedRec);
	        }
	
	        return results;
		} catch (Exception ex) {
			logger.error("Error trying to populate enhanced VFR records. Defaulting to normal VFR.");
			return super.populateMissionAirportPair(missionAirportPair, nVfrToAdd);
		}
    }

	private class Sampler {
		private List<UasVfrRecord> list;
		private double[] cumDistr;
		private HQRandom rand;
		
		public Sampler(List<UasVfrRecord> list, long seed) {
			this.list = list;
			this.cumDistr = new double[list.size()];
			this.rand = new HQRandom(seed);
			
			cumDistr[0] = list.get(0).fraction;
			for (int i = 1; i < cumDistr.length; i++) {
				cumDistr[i] = cumDistr[i-1] + list.get(i).fraction;
			}
		}
		
		public UasVfrRecord sample() {
			double draw = rand.nextDouble();
			for (int i = 0; i < cumDistr.length; i++) {
				if (draw < cumDistr[i]) {
					return list.get(i);
				}
			}
			// Whammy...return the first one by default
			return list.get(0);
		}
	}
	
	protected ScheduleRecord createScheduleRecord(
    		MissionAirportPairKey missionAirportPair,
        int iVfr, UasVfrRecord vfrRec)
    {
        // Odd indices = arrival, Even = departures
        AirportData departureAirport = missionAirportPair.airportPair.getOrigin();
        AirportData arrivalAirport = missionAirportPair.airportPair.getDestination();

        final ScheduleRecord schedRec = new ScheduleRecord();
        
        // Identification fields ------------------------------------------------------------------
        schedRec.idNum = iVfr;
        schedRec.actDate = this.localDate;
        schedRec.aircraftId = "V_" + departureAirport.getMostLikelyCode() + "_" + arrivalAirport.getMostLikelyCode() + "_" + iVfr;
        schedRec.flightIndex = iVfr;
        schedRec.flightPlanType = "VFR";

        // Time fields ----------------------------------------------------------------------------
        Timestamp localTime = 
            this.vfrLocalTimeGenerator.createLocalVfrTime(this.localDate);
        Timestamp etmsTime  = localTime.hourAdd(-departureAirport.getUtcDifference());        
        double taxiTimeSecs = 60 * this.nominalTaxiTimeMinutes;
        
        schedRec.gateOutTime = etmsTime.secondAdd(-taxiTimeSecs);
        schedRec.gateOutTimeFlag = "CREATED";
        schedRec.runwayOffTime = etmsTime;
        schedRec.runwayOffTimeFlag = "CREATED";            
        schedRec.runwayOnTime = schedRec.runwayOffTime.hourAdd(vfrRec.duration);
        schedRec.runwayOnTimeFlag = "CREATED";
        schedRec.gateInTime = schedRec.runwayOnTime.secondAdd(taxiTimeSecs);
        

        // User-class fields ----------------------------------------------------------------------
        schedRec.userClass = "U";
        schedRec.atoUserClass = missionAirportPair.mission.userClass();
        
        // Airspace fields ------------------------------------------------------------------------
        //schedRec.flewFlag = null; // previous code did not set it?
        schedRec.airspaceCode = "10";
        // TODO: Center gives us the opportunity to create a VFR loiter at a random location in the center
        // TODO: not necessarily fair to bias toward the departure but most are round robin anyway
        String center = departureAirport.getCenter();
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
        
        schedRec.depAprtEtms = departureAirport.getFaaCode();
        schedRec.depAprtIcao = departureAirport.getIcaoCode();
        
        schedRec.depLatitude = departureAirport.getLatitude();
        schedRec.depLongitude = departureAirport.getLongitude();
        schedRec.depElevation = departureAirport.getElevation();
        schedRec.depAirportCountryCode = departureAirport.getCountryCode();
        
        schedRec.arrAprtEtms = arrivalAirport.getFaaCode(); 
        schedRec.arrAprtIcao = arrivalAirport.getIcaoCode();    
        
        schedRec.arrLatitude = arrivalAirport.getLatitude();
        schedRec.arrLongitude = arrivalAirport.getLongitude();
        schedRec.arrElevation = arrivalAirport.getElevation();
        schedRec.arrAirportCountryCode = arrivalAirport.getCountryCode();           

        // I don't know what to set the remaining fields to.
        // Is null sufficient for VF?

        // Performance info
        schedRec.filedCruiseAltitude = Altitude.valueOfFeet(vfrRec.cruiseAltitude);
        schedRec.filedSpeed = vfrRec.cruiseTas;
       
        schedRec.etmsAircraftType = vfrRec.badaType;
        schedRec.physicalClass = PhysicalClass.valueOf("T"); // TODO: not all will be T
        schedRec.badaAircraftType = vfrRec.badaType;
        schedRec.badaSource = "ATOP";
        
        schedRec.scheduledFlag = false;
        schedRec.scheduledDepTime = null;
        schedRec.scheduledArrTime = null;
        
        // Route Info
        schedRec.etmsFiledWaypointsRaw = Waypoint.toWaypointString(vfrRec.waypoints, calculateRepeatCount(vfrRec, departureAirport, arrivalAirport));
        
        schedRec.field10 = "";
        
        return schedRec;
    }

	public int calculateRepeatCount(UasVfrRecord rec, AirportData departureAirport, AirportData arrivalAirport) {
		if (rec.waypoints.size() < 2) {
			return 1;
		}
		
		double maxDistance = rec.cruiseTas * rec.duration; // nm
		
		GCPoint departureLocation = new GCPoint(Latitude.valueOfDegrees(departureAirport.getLatitude()), Longitude.valueOfDegrees(departureAirport.getLongitude()));
		GCPoint arrivalLocation = new GCPoint(Latitude.valueOfDegrees(arrivalAirport.getLatitude()), Longitude.valueOfDegrees(arrivalAirport.getLongitude()));
		
		double distanceToRendezvous = GCUtilities.gcDistance(departureLocation, rec.waypoints.get(0));
		double distanceFromRendezvous = GCUtilities.gcDistance(rec.waypoints.get(rec.waypoints.size()-1), arrivalLocation);
		
		double loiterDistance = maxDistance - distanceToRendezvous - distanceFromRendezvous;
		if (loiterDistance < 0) {
			return 1;
		}
		
		double circuitDistance = GCUtilities.gcDistance(rec.waypoints.get(0), rec.waypoints.get(1));
		for (int i = 0; i < rec.waypoints.size() - 1; i++) {
			circuitDistance += GCUtilities.gcDistance(rec.waypoints.get(i), rec.waypoints.get(i+1));
		}
		
		return (int)Math.max(1, loiterDistance / circuitDistance);
	}
}
