/**
 * Copyright "TBD", Metron Aviation & CSSI.  All rights reserved.
 * 
 * This computer Software was developed with the sponsorship of the U.S. Government
 * under Contract No. DTFAWA-10-D-00033, which has a copyright license in accordance with AMS 3.5-13.(c)(1).
 */

package gov.faa.ang.swac.datalayer;

/**
 * A catch-all Exception type for errors experienced interacting with the datalayer classes and/or underlying data store.
 * This is intended to be the only checked exception originating from datalayer classes.
 * @author csmith
 *
 */
public class DataAccessException extends Exception {

	/**
	 * Recommended for all Serializable classes
	 */
	private static final long serialVersionUID = -4887134713463174892L;
	
	/**
	 * Default constructor
	 */
	public DataAccessException() 
	{ 
        super("Unspecified data access exception."); 
    }  
    
	/**
	 * Includes a descriptive message
	 * @param message The message
	 */
    public DataAccessException(String message) 
    { 
        super(message); 
    }
    
    /**
     * Wraps an Exception or other Throwable
     * @param message The message
     * @param ex The inner Exception
     */
    public DataAccessException(String message, Throwable ex) {
		super(message, ex);
	}
    
    /**
     * Creates a {@code DataAccessException} with the specified cause and a detailed message of 
     * {@code (cause==null ? null : cause.toString())} (which typically contains the class and 
     * detail message of {@code cause}).
     * 
     * @param cause - the cause (which is saved for later retrieval by the 
     * {@link Throwable#getCause() Throwable.getCause()} method). (A null value is permitted, 
     * and indicates that the cause is nonexistent or unknown.)
     */
    public DataAccessException(Throwable cause) {
        super(cause);
    }
}
