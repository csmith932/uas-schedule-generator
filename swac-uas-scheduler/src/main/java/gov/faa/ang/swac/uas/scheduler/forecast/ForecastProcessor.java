package gov.faa.ang.swac.uas.scheduler.forecast;

import gov.faa.ang.swac.common.datatypes.Timestamp;
import gov.faa.ang.swac.common.flightmodeling.ScheduleRecord;
import gov.faa.ang.swac.uas.scheduler.forecast.clone.ForecastCloner;
import gov.faa.ang.swac.uas.scheduler.vfr.VfrSchedRecCreator;

import java.util.*;

public class ForecastProcessor
{
	private Map<MissionAirportPairKey,List<ScheduleRecord>> scheduleMap;
    private Map<MissionAirportPairKey,Integer> tafData;
    private VfrSchedRecCreator vfrCreator;
    private ForecastCloner cloner;

    public ForecastProcessor(
    		Map<MissionAirportPairKey,List<ScheduleRecord>> scheduleMap,
        Map<MissionAirportPairKey,Integer> tafData,
        ForecastCloner cloner,       
        VfrSchedRecCreator vfrCreator)
    {
        this.scheduleMap    	= scheduleMap;
        this.tafData            = tafData;
        this.cloner             = cloner;       
        this.vfrCreator         = vfrCreator;        
    }

    public List<ScheduleRecord> process(
        int baseFiscalYear, 
        int forecastFiscalYear,
        Timestamp startTime, 
        Timestamp endTime)
    {
        List<ScheduleRecord> resultList = new ArrayList<ScheduleRecord>();
        
        for (MissionAirportPairKey missionAirportPair : this.tafData.keySet())
        {
        	int nForecastFlights = this.tafData.get(missionAirportPair);
        	List<ScheduleRecord> baseFlights = this.scheduleMap.get(missionAirportPair);
        	if (baseFlights == null) {
        		baseFlights = new ArrayList<ScheduleRecord>();
        	}
        	
            // Clone flights
        	List<ScheduleRecord> forecastFlights = this.cloneFlights(
                baseFlights, 
                nForecastFlights); 
        	
            resultList.addAll(forecastFlights);
            
            // Pad VFRs to make up the gap
            if (forecastFlights.size() < nForecastFlights) {
            	// Create VFR Flights 
            	List<ScheduleRecord> vfrFlights = this.createVfrFlights(
            			missionAirportPair,
                        forecastFlights, 
                        nForecastFlights); 
            	
                resultList.addAll(vfrFlights);
            }
        }

        return resultList;
    }

	private List<ScheduleRecord> cloneFlights(List<ScheduleRecord> baseFlights, int nForecastFlights) {
		if (baseFlights.size() == 0) {
			return new ArrayList<ScheduleRecord>();
		}
		
		cloner.clearFlightLists();
        cloner.cloneFlights(baseFlights, nForecastFlights);
        
        List<ScheduleRecord> forecastFlights = new ArrayList<ScheduleRecord>();
        forecastFlights.addAll(cloner.getClonedFlights());
        if (cloner.getRemovedFlights().size() == 0) {
        	forecastFlights.addAll(baseFlights);
        } else {
        	for (ScheduleRecord baseFlight : baseFlights) {
        		if (!cloner.hasRemoved(baseFlight)) {
        			forecastFlights.add(baseFlight);
        		}
        	}
        }
        return forecastFlights;
	}
	
	public List<ScheduleRecord> createVfrFlights(MissionAirportPairKey missionAirportPair, List<ScheduleRecord> forecastFlights, int nForecastFlights) 
    {
		List<ScheduleRecord> schedRecList = new ArrayList<ScheduleRecord>();
		
		// Double to make sure we have a departure and arrival
		// TODO: Triple if we add a VFR loiter as well
        int nVfrToAdd = 2 * (nForecastFlights - forecastFlights.size());
        if (0 < nVfrToAdd) 
        {
        	schedRecList.addAll(this.vfrCreator.populateMissionAirportPair(missionAirportPair,nVfrToAdd));
        }
        
        return schedRecList;
    }
}
