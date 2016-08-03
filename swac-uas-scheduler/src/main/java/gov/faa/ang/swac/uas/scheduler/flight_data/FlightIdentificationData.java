package gov.faa.ang.swac.uas.scheduler.flight_data;

import gov.faa.ang.swac.common.datatypes.Timestamp;

/**
 * A Class representing the data used to identify a specific flight.
 * 
 * @author James Bonn
 * @version 1.0
 */
public class FlightIdentificationData
{
    private int idNum;
    private Timestamp actDate;
    private String acid;
    private int etmsFlightIndex;
    private String sourceType;

    /**
     * Default Constructor.
     */
    public FlightIdentificationData()
    {
    }

    /**
     * Set the ID Number. Meant to be a unique flight identifier.
     * 
     * @param idNum
     */
    public void setIdNum(int idNum)
    {
        this.idNum = idNum;
    }

    /**
     * @return the ID Number
     */
    public int getIdNum()
    {
        return idNum;
    }

    /**
     * Set the ATALAB defined ACT_DATE.
     * 
     * @param actDate
     */
    public void setActDate(Timestamp actDate)
    {
        this.actDate = actDate;
    }

    /**
     * @return the ATALAB defined ACT_DATE
     */
    public Timestamp getActDate()
    {
        return actDate;
    }

    /**
     * Set the ETMS ACID.
     * 
     * @param acid
     */
    public void setAcid(String acid)
    {
        this.acid = acid;
    }

    /**
     * @return the ETMS ACID
     */
    public String getAcid()
    {
        return acid;
    }

    /**
     * Set the ATALAB defined FLIGHT_INDEX.
     * 
     * @param etmsFlightIndex
     */
    public void setEtmsFlightIndex(int etmsFlightIndex)
    {
        this.etmsFlightIndex = etmsFlightIndex;
    }

    /**
     * @return the ATALAB defined FLIGHT_INDEX
     */
    public int getEtmsFlightIndex()
    {
        return etmsFlightIndex;
    }

    /**
     * Get the first three characters of the ACID as a proxy for the carrier.
     * 
     * @return the first three characters of the ACID
     */
    public String getCarrier()
    {
        String carrier = acid;
        if (acid.length() > 3)
        {
            carrier = acid.substring(0, 3);
        }

        return carrier;
    }

    /**
     * Set the source type of the flight data. For example, "ORIGINAL_FLIGHT" for a baseline ETMS
     * flight, "CLONE" for a cloned flight, "VFR" for a created VFR flight.
     * 
     * @param sourceType
     */
    public void setSourceType(String sourceType)
    {
        this.sourceType = sourceType;
    }

    /**
     * @return the source type of the flight data
     */
    public String getSourceType()
    {
        return sourceType;
    }
}
