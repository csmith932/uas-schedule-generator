package gov.faa.ang.swac.uas.scheduler.userclass;

/**
 * A static class to create a string from an {@link ATOPUserClass} object.
 * 
 * @author James Bonn
 * @version 1.0
 */
public class ATOPUserClassWriter
{
    private static final String detailedUserClasses = "Scheduled Passenger Service";
    private static final String sep = " ";
    
    /**
     * @param userClass
     * @return a String version of the user class
     */
    public String toString(ATOPUserClass userClass)
    {
        StringBuilder output = new StringBuilder(userClass.getNationality());
        output.append(sep + userClass.getUserClassName());
        if(detailedUserClasses.equals(userClass.getUserClassName()))
        {
            output.append(sep + userClass.getBusinessModel());
        }
        
        return output.toString();
    }
}
