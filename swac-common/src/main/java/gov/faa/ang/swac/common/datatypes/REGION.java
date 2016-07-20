package gov.faa.ang.swac.common.datatypes;

public enum REGION 
{
	// these should be in "Comparable" order
	US(false), PACIFIC, ATLANTIC, LATIN_AMERICA, CANADA;
	
	private boolean isInternational = false;
	
	REGION() {
		this(true);
	}
	
	REGION(boolean isInternational) {
		this.isInternational = isInternational;
	}
	
	public boolean isDomestic() { return ! isInternational; }
	public boolean isInternational() { return isInternational; }
}
