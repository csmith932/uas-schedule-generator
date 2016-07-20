/**
 * Copyright "TBD", Metron Aviation & CSSI.  All rights reserved.
 * 
 * This computer Software was developed with the sponsorship of the U.S. Government
 * under Contract No. DTFAWA-10-D-00033, which has a copyright license in accordance with AMS 3.5-13.(c)(1).
 */

package gov.faa.ang.swac.common.flightmodeling;

import java.io.Serializable;

public class Sector extends AbstractResourceInfo implements Serializable, Cloneable
{
	private String name;
	
	public Sector(String name)
	{
		this.name = name;
	}
	
    public Sector(Sector org) {
        this.name = org.name;
    }
        
	@Override
	public String name() {
		return this.name;
	}

	@Override
	public ResourceType resourceType() {
		return ResourceType.SC;
	}

	@Override
	public long crossingTime() {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Sector [name()=");
		builder.append(name());
		builder.append(", resourceType()=");
		builder.append(resourceType());
		builder.append("]");
		return builder.toString();
	}
        
    @Override
    public Sector clone() {
        return new Sector(this);
    }
}
