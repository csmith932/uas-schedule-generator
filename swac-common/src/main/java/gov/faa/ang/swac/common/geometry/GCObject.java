/**
 * Copyright "TBD", Metron Aviation & CSSI.  All rights reserved.
 * 
 * This computer Software was developed with the sponsorship of the U.S. Government
 * under Contract No. DTFAWA-10-D-00033, which has a copyright license in accordance with AMS 3.5-13.(c)(1).
 */

package gov.faa.ang.swac.common.geometry;

import java.io.Serializable;

public class GCObject implements Serializable, Cloneable
{
	private static final long serialVersionUID = 3621073889157840783L;
	protected String name;

        public GCObject() {
            this.name = null;
        }
        
        public GCObject(GCObject org) {
            this.name = org.name;
        }
	public String name()
	{
		return this.name;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	/**
	 * Due to overabstraction, calling name() from 
	 * TrajectoryPoint may not gain access to GCObject.name
	 * 
	 */
	public String GCname(){
		return this.name;
	}
        
        @Override
        public GCObject clone() {
            return new GCObject(this);
        }
}
