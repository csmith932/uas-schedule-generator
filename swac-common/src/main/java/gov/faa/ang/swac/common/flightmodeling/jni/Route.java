/**
 * Copyright "TBD", Metron Aviation & CSSI.  All rights reserved.
 * 
 * This computer Software was developed with the sponsorship of the U.S. Government
 * under Contract No. DTFAWA-10-D-00033, which has a copyright license in accordance with AMS 3.5-13.(c)(1).
 */

package gov.faa.ang.swac.common.flightmodeling.jni;


import gov.faa.ang.swac.common.flightmodeling.TrajectoryPoint;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class Route
    extends ArrayList<TrajectoryPoint>
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 4616724663435138356L;

    public Route()
    {
    	super();
    }
    
    public Route(int initialCapacity)
    {
    	super(initialCapacity);
    }
    
    public Route(Collection<? extends TrajectoryPoint> route)
    {
    	super(route);
    }
    
    @Override
    public String toString()
    {
        Iterator<TrajectoryPoint> i = iterator();
    	if (! i.hasNext())
    	    return "WAY: ";

    	StringBuilder sb = new StringBuilder();
    	sb.append("WAY: ");
    	for (;;) {
    		TrajectoryPoint e = i.next();
    	    sb.append(e);
    	    if (! i.hasNext())
    		return sb.append("\n").toString();
    	    sb.append(", ");
    	}
    }
}
