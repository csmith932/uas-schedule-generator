package gov.faa.ang.swac.common.entities;


public class Arc  {
	private String arcName;
    private String airportName;
    private String fixName;
    private double milesFromFix;
    private long delayAbsorption;

	public Arc(String airportName, String fixName, double milesFromFix, long delayAbsorption) {
		this.airportName = airportName;
		this.fixName = fixName;
		this.milesFromFix = milesFromFix;
		this.delayAbsorption = delayAbsorption;
	}

	public String getAirportName() {
		return airportName;
	}

	public String getFixName() {
		return fixName;
	}

	/**
	 * Returns the max amount of delay, in milliseconds, the arc can absorb. 
	 */
	public long getDelayAbsorption() {
		return delayAbsorption;
	}

	public double getMilesFromFix() {
		return milesFromFix;
	}

	public String getName() {
		return arcName;
	}

	void setName(String arcName) {
		this.arcName = arcName;
	}
	
	public String toString() { 
		return "[Arc " + arcName + " " + milesFromFix + "]";
	}

}
