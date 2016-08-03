package gov.faa.ang.swac.uas.scheduler.userclass;

/**
 * A Class representing an ATO defined user class for flights.
 * 
 * @author James Bonn
 * @version 1.0
 */
public class ATOPUserClass
{
    private String faaCarrier;
    private String nationality;
    private int userClassCode;
    private String userClassName;
    private String businessModel;

    /**
     * Default Constructor.
     */
    public ATOPUserClass()
    {
    }

    /**
     * Constructor that sets all of the user class data.
     * @param faaCarrier
     * @param nationality
     * @param userClassCode
     * @param userClassName
     * @param businessModel
     */
    public ATOPUserClass(String faaCarrier, String nationality,
        int userClassCode, String userClassName, String businessModel)
    {
        this.faaCarrier = faaCarrier;
        this.nationality = nationality;
        this.userClassCode = userClassCode;
        this.userClassName = userClassName;
        this.businessModel = businessModel;
    }

    /**
     * Set the FAA carrier code.
     * @param faaCarrier
     */
    public void setFaaCarrier(String faaCarrier)
    {
        this.faaCarrier = faaCarrier;
    }

    /**
     * @return the FAA carrier code
     */
    public String getFaaCarrier()
    {
        return faaCarrier;
    }

    /**
     * Set the nationality flag. (For example "D" for domestic)
     * @param nationality
     */
    public void setNationality(String nationality)
    {
        this.nationality = nationality;
    }

    /**
     * @return the nationality flag
     */
    public String getNationality()
    {
        return nationality;
    }

    /**
     * Set the user class code.
     * @param userClassCode
     */
    public void setUserClassCode(int userClassCode)
    {
        this.userClassCode = userClassCode;
    }

    /**
     * @return the user class code
     */
    public int getUserClassCode()
    {
        return userClassCode;
    }

    /**
     * Set the user class name. (For example "Schedule Passenger Service")
     * @param userClassName
     */
    public void setUserClassName(String userClassName)
    {
        this.userClassName = userClassName;
    }

    /**
     * @return the user class name
     */
    public String getUserClassName()
    {
        return userClassName;
    }

    /**
     * Set the business model.  (For example "Mainline")
     * @param businessModel
     */
    public void setBusinessModel(String businessModel)
    {
        this.businessModel = businessModel;
    }

    /**
     * @return the business model
     */
    public String getBusinessModel()
    {
        return businessModel;
    }
}
