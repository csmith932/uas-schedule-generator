/**
 * Copyright "TBD", Metron Aviation & CSSI.  All rights reserved.
 * 
 * This computer Software was developed with the sponsorship of the U.S. Government
 * under Contract No. DTFAWA-10-D-00033, which has a copyright license in accordance with AMS 3.5-13.(c)(1).
 */

package gov.faa.ang.swac.common.flightmodeling;

import java.io.Serializable;

public interface IResourceInfo extends Serializable
{
	public enum ResourceType
	{
		SC, // Sector
		DF, // Departure Fix
		AF, // Arrival Fix
		RN, // Static Restriction
		AP, // Airport
		WP, // Filed or modeled waypoint that doesn't represent a resource crossing
		TC, // Top of Climb
		TD, // Top of Descent
		FH, // Freeze Horizon
		OE, // Oceanic entry
		OX, // Oceanic exit
		CC, // oceanic Conflict Check
		CR, // oceanic Climb Request
		AR; // Arc
	}
	
	public static final double DEFAULT_MODIFIER = 1;
	public static final String DEFAULT_MODIFIER_BASE = null;
	
	/**
	 * Name of the {@link IResourceInfo}.
	 */
	public String name();
	
	/**
	 * {@link ResourceType} of the {@link IResourceInfo}.
	 */
	public ResourceType resourceType();
	
	/**
	 * Time to cross {@link IResourceInfo} (based on external speed/distance calculations).
	 * @return
	 */
	public long crossingTime();
	
	public double modifier();
    
    public void setModifier(double modifier);
    
    public String modifierBase();
    
    public void setModifierBase(String base);
        
    public IResourceInfo clone() throws CloneNotSupportedException;
}