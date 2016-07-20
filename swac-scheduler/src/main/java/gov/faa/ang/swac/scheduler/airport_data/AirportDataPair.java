package gov.faa.ang.swac.scheduler.airport_data;

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
}
