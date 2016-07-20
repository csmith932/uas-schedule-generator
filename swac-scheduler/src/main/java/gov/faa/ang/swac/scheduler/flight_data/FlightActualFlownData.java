package gov.faa.ang.swac.scheduler.flight_data;

/**
 * A Class representing data associated with the "actual" flown version
 * of a specific flight
 * 
 * @author James Bonn
 * @version 1.0
 */
public class FlightActualFlownData
{
    private int flewFlag;
    private int airspaceFlag;
    
    /**
     * Default Constructor.
     */
    public FlightActualFlownData()
    {
    }

    /**
     * Set the ETMS flew flag.  If <=0, flight is not considered to have flown.
     * If >0, flight is considered to have flown.
     * @param flewFlag
     */
    public void setFlewFlag(int flewFlag)
    {
        this.flewFlag = flewFlag;
    }

    /**
     *
     * @return ETMS flew flag
     */
    public int getFlewFlag()
    {
        return flewFlag;
    }

    /**
     * Value representing what airspace the flight touched.  
     * See {@link DemandFlight} for a full description.
     * @param airspaceFlag
     */
    public void setAirspaceFlag(int airspaceFlag)
    {
        this.airspaceFlag = airspaceFlag;
    }

    /**
     *
     * @return value representing what airspace the flight touched
     */
    public int getAirspaceFlag()
    {
        return airspaceFlag;
    }
}
