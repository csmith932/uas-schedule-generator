package gov.faa.ang.swac.uas.scheduler.forecast;

import gov.faa.ang.swac.uas.scheduler.airport_data.AirportDataPair;
import gov.faa.ang.swac.uas.scheduler.forecast.trip_distribution.ForecastTripDistAirportDataCount.MissionType;

public class MissionAirportPairKey {
	public final AirportDataPair airportPair;
	public final MissionType mission;
	
	public MissionAirportPairKey(MissionType mission, AirportDataPair airportPair) {
		this.mission = mission;
		this.airportPair = airportPair;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((airportPair == null) ? 0 : airportPair.getOrigin().getMostLikelyCode().hashCode());
		result = prime * result + ((airportPair == null) ? 0 : airportPair.getDestination().getMostLikelyCode().hashCode());
		result = prime * result + ((mission == null) ? 0 : mission.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MissionAirportPairKey other = (MissionAirportPairKey) obj;
		if (airportPair == null) {
			if (other.airportPair != null)
				return false;
		} else if (!airportPair.equals(other.airportPair))
			return false;
		if (mission != other.mission)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "MissionAirportPairKey [airportPair=" + airportPair + ", mission=" + mission + "]";
	}
}
