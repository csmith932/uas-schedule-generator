package gov.faa.ang.swac.common.interfaces;

import gov.faa.ang.swac.common.datatypes.Timestamp;

/**
 * Specifies that a class contains a timestamp that can be access with a getTimestamp() method.
 * 
 * @author ssmitz
 */
public interface HasTimestamp
{
    /**
     * @return The associated {@link Timestamp}.
     */
    public Timestamp getTimestamp();
}
