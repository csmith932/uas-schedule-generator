package gov.faa.ang.swac.common.flightmodeling;

import gov.faa.ang.swac.common.datatypes.Timestamp;
import gov.faa.ang.swac.common.flightmodeling.IResourceInfo.ResourceType;

public class ArrivalRouteShorteningReportRecord {
	
	private static final String SEPARATOR = "/";
	private static final String COMMA = ", ";
	public static final String HEADER = "flightId, departure, arrival, badaType, TOD time, oldArrivalTime, newArrivalTime, equipSuffix, wxCondition, apsProbability, apsSelected, newCurve, distSavings, oldStar, newStar, success";
	
	// Report fields
	private int flightId;
	private String departure;
	private String arrival;
	private String badaType;
	private Timestamp todTime;
	private Timestamp oldArrivalTime;
	private Timestamp newArrivalTime;
	private String equipSuffix;
	private String wxCondition;
	private double apsProbability;
	private boolean apsSelected;
	private String newCurve;
	private double distSavings;
	private String oldStar;
	private String newStar;
	private boolean success;
	
	// Internal fields
	private double oldDist;
	
	public void setWxCondition(String wxCondition) {
		this.wxCondition = wxCondition;
	}

	public void setApsProbability(double apsProbability) {
		this.apsProbability = apsProbability;
	}

	public void setApsSelected(boolean apsSelected) {
		this.apsSelected = apsSelected;
	}

	public void setNewCurve(String newCurve) {
		this.newCurve = newCurve;
	}
	
	public double getDistSavings() {
		return this.distSavings;
	}

	public void setInitialValues(FlightLeg leg) {
		this.flightId = leg.flightId();
		this.departure = leg.departureAirport();
		this.arrival = leg.arrivalAirport();
		this.badaType = leg.filedBadaAircraftType();
		this.todTime = getTodTime(leg);
		this.oldArrivalTime = leg.computedArrivalTime();
		// newArrivalTime is set once the APS is complete
		this.equipSuffix = leg.aircraft().equipmentSuffix().toString();
		// wxCondition is set when we do the APS inquiry
		// apsProbability is set when we do the APS inquiry
		// apsSelected is set when we do the APS inquiry
		// newCurve is ??? TODO
		// distSavings is set once the APS is complete
		this.oldStar = getStar(leg);
		// newStar is set once the APS is complete
		
		this.oldDist = leg.getTotalRouteDistance();
	}
	
	public void setFinalValues(FlightLeg leg) {
		this.newArrivalTime = leg.computedArrivalTime();
		this.distSavings = this.oldDist - leg.getTotalRouteDistance();
		this.newStar = getStar(leg);
	}
	
	public void setSuccess(boolean success) {
		this.success = success;
	}

	private Timestamp getTodTime(FlightLeg leg) {
		for (TrajectoryPoint p : leg.flightRoute()) {
			if (ResourceType.TD.equals(p.resourceType())) {
				return p.timestamp();
			}
		}
		// XXX: I don't want to worry about null checks but this may not be an appropriate default 
		return new Timestamp(0);
	}
	
	private String getStar(FlightLeg leg) {
		StringBuilder retVal = new StringBuilder();
		boolean flag = false;
		String lastResourceName = null;
		for (TrajectoryPoint p : leg.flightRoute()) {
			if (ResourceType.AF.equals(p.resourceType())) {
				flag = true;
			}
			if (flag) {
				String name = p.GCname();
				if (name == null) {
					IResourceInfo r = p.resourceInfo();
					if (r != null) {
						name = r.name();
					}
				}
				if (name != null && !(name.equals(lastResourceName))) {
					retVal.append(SEPARATOR).append(name);
					lastResourceName = name;
				}
			}
		}
		return retVal.toString();
	}
	
	@Override
	public String toString() {
		return this.flightId + COMMA +
			this.departure + COMMA +
			this.arrival + COMMA +
			this.badaType + COMMA +
			this.todTime + COMMA +
			this.oldArrivalTime + COMMA +
			this.newArrivalTime + COMMA +
			this.equipSuffix + COMMA +
			this.wxCondition + COMMA +
			this.apsProbability + COMMA +
			this.apsSelected + COMMA +
			this.newCurve + COMMA +
			this.distSavings + COMMA +
			this.oldStar + COMMA +
			this.newStar + COMMA +
			this.success;
	}
}
