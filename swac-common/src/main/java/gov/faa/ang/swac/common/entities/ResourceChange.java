/**
 * Copyright "TBD", Metron Aviation & CSSI.  All rights reserved.
 * 
 * This computer Software was developed with the sponsorship of the U.S. Government
 * under Contract No. DTFAWA-10-D-00033, which has a copyright license in accordance with AMS 3.5-13.(c)(1).
 */

package gov.faa.ang.swac.common.entities;

import gov.faa.ang.swac.common.datatypes.Timestamp;
import gov.faa.ang.swac.common.flightmodeling.IResourceInfo.ResourceType;

import java.io.Serializable;

public interface ResourceChange extends Comparable<ResourceChange>, Serializable {
	public long activationTime();
	public Timestamp activationTimestamp();
	public String resourceName();
	public ResourceType resourceType();
	public String parameter1();
	public String parameter2();
	public String parameter3();
}


