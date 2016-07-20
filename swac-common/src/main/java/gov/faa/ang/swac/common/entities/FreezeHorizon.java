package gov.faa.ang.swac.common.entities;


public class FreezeHorizon  {
	private String freezeHorizonName;
    private String airportName;
    private String fixName;
    private double milesFromFix;
    
	public FreezeHorizon(String airportName, String fixName, double milesFromFix) {
		this.airportName = airportName;
		this.fixName = fixName;
		this.milesFromFix = milesFromFix;
	}

	public String getAirportName() {
		return airportName;
	}

	public String getFixName() {
		return fixName;
	}

	public double getMilesFromFix() {
		return milesFromFix;
	}

	public String getName() {
		return freezeHorizonName;
	}
	
	void setName(String freezeHorizonName) {
		this.freezeHorizonName = freezeHorizonName;
	}
	
	public String toString() { 
		return "[FreezeHorizon " + freezeHorizonName + " " + milesFromFix + "]";
	}


}
