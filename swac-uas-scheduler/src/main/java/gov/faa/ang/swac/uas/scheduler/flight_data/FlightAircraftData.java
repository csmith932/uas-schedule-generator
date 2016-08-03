package gov.faa.ang.swac.uas.scheduler.flight_data;

/**
 * A Class representing data useful for identifying the type of plane
 * for a flight.
 * 
 * @author James Bonn
 * @version 1.0
 */
public class FlightAircraftData
{
    private String etmsAcftType;
    private String badaAcftType;
    private String badaSourceType;
    private String physicalClass;
    
    /**
     * Default Constructor.
     */
    public FlightAircraftData()
    {
    }

    /**
     * Set the ETMS aircraft type.
     * @param etmsAcftType
     */
    public void setEtmsAcftType(String etmsAcftType)
    {
        this.etmsAcftType = etmsAcftType;
    }

    /**
     * @return the ETMS aircraft type
     */
    public String getEtmsAcftType()
    {
        return etmsAcftType;
    }

    /**
     * Set the BADA aircraft type.
     * @param badaAcftType
     */
    public void setBadaAcftType(String badaAcftType)
    {
        this.badaAcftType = badaAcftType;
    }

    /**
     * @return the BADA aircraft type
     */
    public String getBadaAcftType()
    {
        return badaAcftType;
    }

    /**
     * Set the source of the BADA aircraft type.
     * @param badaSourceType
     */
    public void setBadaSourceType(String badaSourceType)
    {
        this.badaSourceType = badaSourceType;
    }

    /**
     * @return the source of the BADA aircraft type
     */
    public String getBadaSourceType()
    {
        return badaSourceType;
    }

    /**
     * Set the ETMS physical class.
     * @param physicalClass
     */
    public void setPhysicalClass(String physicalClass)
    {
        this.physicalClass = physicalClass;
    }

    /**
     * @return the ETMS physical class
     */
    public String getPhysicalClass()
    {
        return physicalClass;
    }
}
