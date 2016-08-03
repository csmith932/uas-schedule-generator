package gov.faa.ang.swac.uas.scheduler.flight_data;

/**
 * A Class giving the taxi times for a specific flight.  Taxi times are
 * given in seconds.
 * 
 * @author James Bonn
 * @version 1.0
 */
public class FlightTaxiTimesData
{
    private int taxiOutTime;
    private int taxiInTime;

    /**
     * Default Constructor.
     */
    public FlightTaxiTimesData()
    {
    }

    /**
     * Set the taxi out time.
     * @param taxiOutTime
     */
    public void setTaxiOutTime(int taxiOutTime)
    {
        this.taxiOutTime = taxiOutTime;
    }

    /**
     * @return the taxi out time
     */
    public int getTaxiOutTime()
    {
        return taxiOutTime;
    }

    /**
     * Set the taxi in time.
     * @param taxiInTime
     */
    public void setTaxiInTime(int taxiInTime)
    {
        this.taxiInTime = taxiInTime;
    }

    /**
     * @return the taxi in time
     */
    public int getTaxiInTime()
    {
        return taxiInTime;
    }
}
