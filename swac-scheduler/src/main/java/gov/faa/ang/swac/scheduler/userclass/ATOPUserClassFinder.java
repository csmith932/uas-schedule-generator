package gov.faa.ang.swac.scheduler.userclass;

/**
 * A Class that maps ETMS data to {@link ATOPUserClass} objects.
 * 
 * @author James Bonn
 * @version 1.0
 */
public class ATOPUserClassFinder
{
    private final static String ETMS_MILITARY = "M";
    private final static String ETMS_GA = "G";
    private final static String ETMS_OTHER = "O";
    private final static String DOMESTIC = "D";
    
    private ATOPAirlineUserClassMap airlineUserClassMap;
    private ATOPUserClassCodeMap userClassCodeMap;

    /**
     * Default Constructor.
     */
    public ATOPUserClassFinder()
    {
    }

    /**
     * Constructor that sets the {@link ATOPAirlineUserClassMap} and the
     * {@link ATOPUserClassCodeMap} maps.
     * @param airlineUserClassMap
     * @param userClassCodeMap
     */
    public ATOPUserClassFinder(ATOPAirlineUserClassMap airlineUserClassMap,
        ATOPUserClassCodeMap userClassCodeMap)
    {
        this.airlineUserClassMap = airlineUserClassMap;
        this.userClassCodeMap = userClassCodeMap;
    }

    /**
     * Given an FAA carrier and an ETMS user class, find the 
     * {@link ATOPUserClass} associated with it.
     * @param carrier
     * @param etmsUserClass
     * @return the {@link ATOPUserClass} associated with the provided
     * FAA carrier and ETMS user class
     */
    public ATOPUserClass find(String carrier, String etmsUserClass)
    {
        ATOPUserClass uc = null;
        if(etmsUserClass != null)
        {
            if(etmsUserClass.equals(ETMS_MILITARY))
            {
                // create a military user class
                uc = createMilitary();
            }
            if(etmsUserClass.equals(ETMS_GA))
            {
                uc = createGA();
            }
            if(etmsUserClass.equals(ETMS_OTHER))
            {
                uc = createOther();
            }
        }
        
        if(uc == null)
        {
            uc = airlineUserClassMap.getUserClass(carrier);
        }
        
        if(uc == null)
        {
            uc = createOther();
        }
        
        return uc;
    }

    /**
     * 
     * @return a new Military {@link ATOPUserClass} object
     */
    public ATOPUserClass createMilitary()
    {
        ATOPUserClass uc = new ATOPUserClass();
        
        uc.setNationality(DOMESTIC);
        uc.setUserClassCode(userClassCodeMap.getMilitaryUserClassCode());
        uc.setUserClassName(ATOPUserClassCodeMap.getMilitaryUserClassName());
        uc.setBusinessModel(ATOPUserClassCodeMap.getMilitaryUserClassName());
        
        return uc;
    }

    /**
     * 
     * @return a new GA {@link ATOPUserClass} object
     */
    public ATOPUserClass createGA()
    {
        ATOPUserClass uc = new ATOPUserClass();
        
        uc.setNationality(DOMESTIC);
        uc.setUserClassCode(userClassCodeMap.getGAUserClassCode());
        uc.setUserClassName(ATOPUserClassCodeMap.getGAUserClassName());
        uc.setBusinessModel(ATOPUserClassCodeMap.getGAUserClassName());
        
        return uc;
    }

    /**
     * @return a new "other" {@link ATOPUserClass} object
     */
    public ATOPUserClass createOther()
    {
        ATOPUserClass uc = new ATOPUserClass();
        
        uc.setNationality(DOMESTIC);
        uc.setUserClassCode(userClassCodeMap.getOtherUserClassCode());
        uc.setUserClassName(ATOPUserClassCodeMap.getOtherUserClassName());
        uc.setBusinessModel(ATOPUserClassCodeMap.getOtherUserClassName());
        
        return uc;
    }

    /**
     * @return a new VFR {@link ATOPUserClass} object
     */
    public static ATOPUserClass createVFR()
    {
        ATOPUserClass uc = new ATOPUserClass();
        
        uc.setNationality(DOMESTIC);
        uc.setUserClassCode(ATOPUserClassCodeMap.getVFRUserClassCode());
        uc.setUserClassName(ATOPUserClassCodeMap.getVFRUserClassName());
        uc.setBusinessModel(ATOPUserClassCodeMap.getVFRUserClassName());
        
        return uc;
    }

    /**
     * Set the {@link ATOPAirlineUserClassMap} map.
     * @param airlineUserClassMap
     */
    public void setAirlineUserClassMap(ATOPAirlineUserClassMap airlineUserClassMap)
    {
        this.airlineUserClassMap = airlineUserClassMap;
    }

    /**
     * @return the {@link ATOPAirlineUserClassMap} map
     */
    public ATOPAirlineUserClassMap getAirlineUserClassMap()
    {
        return airlineUserClassMap;
    }

    /**
     * Set the {@link ATOPUserClassCodeMap} map.
     * @param userClassCodeMap
     */
    public void setUserClassCodeMap(ATOPUserClassCodeMap userClassCodeMap)
    {
        this.userClassCodeMap = userClassCodeMap;
    }

    /**
     * @return the {@link ATOPUserClassCodeMap} map
     */
    public ATOPUserClassCodeMap getUserClassCodeMap()
    {
        return userClassCodeMap;
    }
}
