package gov.faa.ang.swac.uas.scheduler.forecast.trip_distribution;

/**
 * A Class representing count values for different user class categories
 * necessary for matching up the TFMS to TAF data categories
 * in the forecasting process.  Different user classes are grown at
 * different rates, therefore the different category totals need to be tracked.
 * 
 * @author James Bonn
 * @version 1.0
 */
public class ForecastTripDistAirportDataCount
{
	public enum MissionType {
		BORDER_PATROL_SOUTHWEST ("U Border Patrol - Southwest Border Region"),
		BORDER_PATROL_NORTH ("U Border Patrol - Northern Border Region"),
		ENVIRONMNENTAL_SPILL_MONITORING ("U Environmental Spill Monitoring"),
		TRAFFIC_REPORTING ("U Traffic Reporting & News Gathering"),
		CARGO_DELIVERY ("U Cargo Delivery"),
		AIR_QUALITY_MONITORING ("U Air Quality Monitoring"),
		WILDFIRE_MONITORING ("U Wildfire Monitoring"),
		FLOOD_MAPPING ("U Flood Mapping"),
		LAW_ENFORCEMENT ("U Law Enforcement"),	
		EMISSION_MONITORING ("U Point-Source Emission Monitoring"),	
		WILDLIFE_MONITORING ("U Wildlife Monitoring"),
		GEOPHYSICAL_MONITORING ("U Geophysical Monitoring & Exploration"),	
		AIRBORNE_PATHOGEN_TRACKING ("U Airborne Pathogen Tracking"),
		WEATHER_DATA_COLLECTION ("U Weather Data Collection"),
		AIR_TAXI ("U On-Demand Air Taxi"),
		WAYPOINT_INSPECTION ("U FAA Waypoint Inspection"),	
		COMMUNICATION ("U Communication & Broadcast Relay"),	
		DISASTER_RESPONSE ("U Disaster Assessment & Response"),	
		MILITARY_TRAINING ("U Military Training"),
		MILITARY_TRANSPORT ("U Military Transport"),	
		PUBLIC_OTHER ("U Public Other"),
		CIVIL_OTHER ("U Civil Other");
		
		private final String userClass;
		
		MissionType(String userClass) {
			this.userClass = userClass;
		}
		
		public String userClass() {
			return this.userClass;
		}
		
		public static MissionType fromUserClass(String userClass) {
			for (MissionType mission : MissionType.values()) {
				if (mission.userClass.equals(userClass)) {
					return mission;
				}
			}
			throw new IllegalArgumentException();
		}
	}
	
	private int[] counts = new int[MissionType.values().length];

	public void setCount(MissionType mission, int count) {
		this.counts[mission.ordinal()] = count;
	}
	
	public int getCount(MissionType mission) {
		return this.counts[mission.ordinal()];
	}
	
    /**
     * Add the input data to the current data values.
     * @param addTo
     */
    public void addAllData(ForecastTripDistAirportDataCount addTo)
    {
        for (MissionType mission : MissionType.values()) {
        	setCount(mission, addTo.getCount(mission));
        }
    }

    /**
     * @return the total number of flights
     */
    public int getTotal()
    {
        int sum = 0;
        for (MissionType mission : MissionType.values()) {
        	sum += this.counts[mission.ordinal()];
        }
        return sum;
    }
}
