/**
 * Copyright "TBD", Metron Aviation & CSSI.  All rights reserved.
 * 
 * This computer Software was developed with the sponsorship of the U.S. Government
 * under Contract No. DTFAWA-10-D-00033, which has a copyright license in accordance with AMS 3.5-13.(c)(1).
 */

package gov.faa.ang.swac.common.flightmodeling;

import java.io.Serializable;

import gov.faa.ang.swac.common.entities.AirportFix.FixType;
import gov.faa.ang.swac.common.geometry.GCPoint;

/**
 * Simple record that stores {@link Fix} information useful for {@link FlightLeg} and {@link FlightLeg} modeling.
 * 
 * @author Robert Lakatos
 */
public class Fix extends AbstractResourceInfo implements Serializable
{
	/**
	 * Denotes if this {@link Fix} is an "Arrival" or "Departure" fix. 
	 */
	public enum Type
	{
		Arrival,
		Departure,
	}
	
	private String name;
	private Type type;
	private boolean isRequired;
	private GCPoint point;
	
	public Fix(String name, Type type, boolean isRequired, GCPoint point)
	{
		this.name = name;
		this.type = type;
		this.isRequired = isRequired;
		this.point = point;
	}
	
	public Fix(gov.faa.ang.swac.common.entities.Fix entityFix,
			   gov.faa.ang.swac.common.entities.AirportFix.FixType entityType,
			   Boolean isRequired){

		this.name		= entityFix.getName();
		this.point		= new TrajectoryPoint(entityFix.getLatitude(), entityFix.getLongitude());
		this.isRequired = isRequired;
		
		if (entityType.equals(FixType.DEPARTURE))
			this.type = gov.faa.ang.swac.common.flightmodeling.Fix.Type.Departure;
		else
			this.type = gov.faa.ang.swac.common.flightmodeling.Fix.Type.Arrival;
	 }
	
        public Fix(Fix org) {
            this.point = (org.point == null ? null : org.point.clone());
            this.name = org.name;
            this.type = org.type;
            this.isRequired = org.isRequired;
        }
        
	/**
	 * Returns the name of this {@link Fix}.
	 */
	public String name()
	{
		return this.name;
	}
	
	/**
	 * Set the {@link Type} of this {@link Fix}.
	 */
	public void type(Type type)
	{
		this.type = type;
	}
	
	/**
	 * Returns the {@link Type} of this {@link Fix}.
	 */
	public Type type()
	{
		return this.type;
	}
	
	/**
	 * Returns the "isRequired" attribute of this {@link Fix}.
	 * @return <code>true</code> if this {@link Fix} is required, <code>false</code> otherwise.
	 */
	public boolean isRequired()
	{
		return this.isRequired;
	}
	
	/**
	 * Returns the {@link GCPoint} of this {@link Fix}.
	 */
	public GCPoint point()
	{
		return new GCPoint(this.point);
	}
	
	@Override
	public String toString()
	{
		return " Fix: name=" + this.name + ", type=" + this.type + ", required=" + this.isRequired;
	}

	public long crossingTime() {
		return 0;
	}

	public ResourceType resourceType()
	{
		if (this.type == Type.Arrival)
		{
			return ResourceType.AF;			
		}
		
		return ResourceType.DF;
	}
        
        @Override
        public Fix clone() {
            return new Fix(this);
        }
}