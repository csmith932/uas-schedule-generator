package gov.faa.ang.swac.scheduler.flight_data;

import gov.faa.ang.swac.scheduler.userclass.ATOPUserClass;

/**
 * A Class representing the type of user class for the flight.
 * 
 * @author James Bonn
 * @version 1.0
 */
public class FlightUserClassData
{
    private String etmsUserClass;
    private ATOPUserClass atopUserClass;
    private String atopUserClassStr;

    /**
     * Default Constructor.
     */
    public FlightUserClassData()
    {
    }

    /**
     * Set the ETMS user class.
     * @param etmsUserClass
     */
    public void setEtmsUserClass(String etmsUserClass)
    {
        this.etmsUserClass = etmsUserClass;
    }

    /**
     * @return the ETMS user class
     */
    public String getEtmsUserClass()
    {
        return etmsUserClass;
    }

    /**
     * Set the ATO User Class.
     * @param atopUserClass
     */
    public void setAtopUserClass(ATOPUserClass atopUserClass)
    {
        this.atopUserClass = atopUserClass;
    }

    /**
     * @return the ATO User Class
     */
    public ATOPUserClass getAtopUserClass()
    {
        return atopUserClass;
    }

    /**
     * Set a string value for the ATO user class.
     * @param atopUserClassStr
     */
    public void setAtopUserClassStr(String atopUserClassStr)
    {
        this.atopUserClassStr = atopUserClassStr;
    }

    /**
     * @return string value for the ATO user class
     */
    public String getAtopUserClassStr()
    {
        return atopUserClassStr;
    }
}
