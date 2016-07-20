package gov.faa.ang.swac.scheduler.userclass;

import java.util.HashMap;

/**
 * A Class that maps from an FAA Carrier code to an {@link ATOPUserClass} object.
 * 
 * @author James Bonn
 * @version 1.0
 */
public class ATOPAirlineUserClassMap
{
    private static final int DEFAULT_SIZE = 1000;
        
    HashMap<String, ATOPUserClass> userClassMap;

    /**
     * Default Constructor.
     */
    public ATOPAirlineUserClassMap()
    {
        this.userClassMap = new HashMap<String, ATOPUserClass>(DEFAULT_SIZE);
    }

    /**
     * Add an {@link ATOPUserClass} object to the map.
     * @param uc
     */
    public void addUserClass(ATOPUserClass uc)
    {
        userClassMap.put(uc.getFaaCarrier(), uc);
    }

    /**
     * Given a carrier, find the mapped user class.
     * @param carrier
     * @return an {@link ATOPUserClass} object mapped to the input carrer
     */
    public ATOPUserClass getUserClass(String carrier)
    {
        return userClassMap.get(carrier);
    }
}
