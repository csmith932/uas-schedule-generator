/**
 * Copyright "TBD", Metron Aviation & CSSI.  All rights reserved.
 * 
 * This computer Software was developed with the sponsorship of the U.S. Government
 * under Contract No. DTFAWA-10-D-00033, which has a copyright license in accordance with AMS 3.5-13.(c)(1).
 */

package gov.faa.ang.swac.controller;

public final class ExitException extends SecurityException
{
    private static final long serialVersionUID = -1982617086752946683L; 
 
    public ExitException() { 
        super("Fatal error: aborting..."); 
    }  
    
    public ExitException(String message) { 
        super(message); 
    }
    
    public ExitException(String message, Throwable ex) {
		super(message, ex);
	}
    
    public ExitException(Throwable cause) {
        super(cause);
    }
}
