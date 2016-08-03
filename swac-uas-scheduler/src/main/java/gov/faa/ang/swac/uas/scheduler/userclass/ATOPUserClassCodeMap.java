package gov.faa.ang.swac.uas.scheduler.userclass;

import java.util.HashMap;

/**
 * A Class that maps user class names to user class code numbers
 * for the data that is used in {@link ATOPUserClass}.
 * 
 * @author James Bonn
 * @version 1.0
 */
public class ATOPUserClassCodeMap
{
    private static final String GA_NAME = "General Aviation";
    private static final String MIL_NAME = "Military";
    private static final String OTHER_NAME = "Other Miscellaneous";
    private static final String VFR_NAME = "VFR";
    
    private HashMap<Integer, String> codeToName;
    private HashMap<String, Integer> nameToCode;

    /**
     * Default Constructor.
     */
    public ATOPUserClassCodeMap()
    {
        codeToName = new HashMap<Integer, String>();
        nameToCode = new HashMap<String, Integer>();
    }

    /**
     * Add a user class name and code number to the mapping.
     * @param userClassCode
     * @param userClassName
     */
    public void addUserClassName(int userClassCode, String userClassName)
    {
        codeToName.put(userClassCode, userClassName);
        nameToCode.put(userClassName, userClassCode);
    }

    /**
     * Get the user class name given the code number.
     * @param userClassCode
     * @return the user class name mapped to the input code number
     */
    public String getUserClassName(int userClassCode)
    {
        return codeToName.get(userClassCode);
    }

    /**
     * Get the user class code number given the user class name.
     * @param userClassName
     * @return the user class code number mapped to the name
     */
    public int getUserClassCode(String userClassName)
    {
        int output = -1;
        if(nameToCode.containsKey(userClassName))
        {
            output = nameToCode.get(userClassName);
        }
        
        return output;
    }

    /**
     * @return the name of the GA user class
     */
    public static String getGAUserClassName()
    {
        return GA_NAME;
    }

    /**
     * @return the user class code number of the GA user class
     */
    public int getGAUserClassCode()
    {
        return getUserClassCode(GA_NAME);
    }

    /**
     * @return the user class name of the Military user class
     */
    public static String getMilitaryUserClassName()
    {
        return MIL_NAME;
    }

    /**
     * @return the user class code number of the Military user class
     */
    public int getMilitaryUserClassCode()
    {
        return getUserClassCode(MIL_NAME);
    }

    /**
     * @return the user class name of the "other" user class
     */
    public static String getOtherUserClassName()
    {
        return OTHER_NAME;
    }

    /**
     * @return the user class code number of the "other" user class
     */
    public int getOtherUserClassCode()
    {
        return getUserClassCode(OTHER_NAME);
    }

    /**
     * @return the user class name of the VFR user class
     */
    public static String getVFRUserClassName()
    {
        return VFR_NAME;
    }

    /**
     * @return the user class code number of the VFR user class
     */
    public static int getVFRUserClassCode()
    {
        return -1;
    }
}
