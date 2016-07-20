/**
 * Copyright "TBD", Metron Aviation & CSSI.  All rights reserved.
 * 
 * This computer Software was developed with the sponsorship of the U.S. Government
 * under Contract No. DTFAWA-10-D-00033, which has a copyright license in accordance with AMS 3.5-13.(c)(1).
 */

package gov.faa.ang.swac.controller;

import gov.faa.ang.swac.controller.ExitException;

import java.security.Permission;

import org.junit.After;
import org.junit.Before;

public abstract class NoExitTest {
    
    
	protected static class NoExitSecurityManager extends SecurityManager { 
		@Override 
	    public void checkPermission(Permission perm) { 
	        // allow anything. 
	    } 
	 
	    @Override 
	    public void checkPermission(Permission perm, Object context) { 
	        // allow anything. 
	    } 
	 
	    @Override 
	    public void checkExit(int status) { 
	        super.checkExit(status); 
	        throw new ExitException(); 
	    } 
	} 
	 
	private SecurityManager securityManager; 
	 
	@Before 
	public void setUpSecurityManager() { 
	    this.securityManager = System.getSecurityManager(); 
	    System.setSecurityManager(new NoExitSecurityManager()); 
	} 
	 
	@After 
	public void tearDownSecurityManager() { 
	    System.setSecurityManager(this.securityManager); 
	} 
	
	
}
