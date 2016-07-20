package gov.faa.ang.swac.scheduler.vfr;

/**
 * A Class that keeps a basic count of OPSNET data and ETMS data and determines
 * how many VFR flights are needed.  OPSNET airport codes are always the three
 * letter FAA code, so do not try to match data on ICAO codes.
 * 
 * @author James Bonn
 * @version 1.0
 */
public class VFROpsnetAirportData
{
    private String faaCode;
    private int opsnetCount;
    private int etmsCount;

    /**
     * Default Constructor.
     */
    public VFROpsnetAirportData()
    {
    }

    /**
     * Constructor that sets the airport code.
     * @param faaCode
     */
    public VFROpsnetAirportData(String faaCode)
    {
        this.faaCode = faaCode;
        opsnetCount = 0;
        etmsCount = 0;
    }

    /**
     * @return the "key" for mapping
     */
    public String getKey()
    {
        return faaCode;
    }

    /**
     * Set the airport FAA code.
     * @param faaCode
     */
    public void setFaaCode(String faaCode)
    {
        this.faaCode = faaCode;
    }

    /**
     * @return the airport FAA code
     */
    public String getFaaCode()
    {
        return faaCode;
    }

    /**
     * Set the OPSNET count.
     * @param opsnetCount
     */
    public void setOpsnetCount(int opsnetCount)
    {
        this.opsnetCount = opsnetCount;
    }

    /**
     * @return the OPSNET count
     */
    public int getOpsnetCount()
    {
        return opsnetCount;
    }

    /**
     * Set the ETMS count.
     * @param etmsCount
     */
    public void setEtmsCount(int etmsCount)
    {
        this.etmsCount = etmsCount;
    }

    /**
     * @return the ETMS count
     */
    public int getEtmsCount()
    {
        return etmsCount;
    }

    /**
     * Add a flight to the ETMS count.
     */
    public void incrementEtmsCount()
    {
        etmsCount++;
    }

    /**
     * @return the number of VFR flights as the difference between
     * the OPSNET count and the ETMS count
     */
    public int getNumVFR()
    {
        return Math.max(0, opsnetCount - etmsCount);
    }
}
