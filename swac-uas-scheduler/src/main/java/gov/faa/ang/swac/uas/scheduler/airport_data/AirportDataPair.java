package gov.faa.ang.swac.uas.scheduler.airport_data;

/**
 * A class representing an AirportData {@link AirportData} 
 * origin-destination pair.
 * 
 * @author Clifford Hall
 * @version 1.2
 */
public class AirportDataPair
{
	private AirportData 
        origin, 
        destin;
    
    /**
     * Default Constructor, departure and arrival airports are null.
     */
    public AirportDataPair()
    {
    }
    
    /**
     * Constructor where departure and arrival airports are given
     * @param origin
     * @param destin
     */
    public AirportDataPair(
        AirportData origin, 
        AirportData destin)
    {
        this.origin = origin;
        this.destin = destin;
    }

    /**
     * Set the departure airport
     * @param depAprt
     */
    public void setOrigin(AirportData depAprt)
    {
        this.origin = depAprt;
    }

    /**
     * Get the departure airport
     * @return departure airport
     */
    public AirportData getOrigin()
    {
        return origin;
    }

    /**
     * Set the arrival airport
     * @param arrAprt
     */
    public void setDestination(AirportData arrAprt)
    {
        this.destin = arrAprt;
    }

    /**
     * Get the arrival airport
     * @return arrival airport
     */
    public AirportData getDestination()
    {
        return destin;
    }

	@Override
	public String toString() {
		return "AirportDataPair [origin=" + origin + ", destin=" + destin + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((destin == null) ? 0 : destin.hashCode());
		result = prime * result + ((origin == null) ? 0 : origin.hashCode());
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
		AirportDataPair other = (AirportDataPair) obj;
		if (destin == null) {
			if (other.destin != null)
				return false;
		} else if (!destin.equals(other.destin))
			return false;
		if (origin == null) {
			if (other.origin != null)
				return false;
		} else if (!origin.equals(other.origin))
			return false;
		return true;
	}
}
