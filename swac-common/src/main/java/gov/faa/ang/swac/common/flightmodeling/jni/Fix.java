/**
 * Copyright "TBD", Metron Aviation & CSSI.  All rights reserved.
 * 
 * This computer Software was developed with the sponsorship of the U.S. Government
 * under Contract No. DTFAWA-10-D-00033, which has a copyright license in accordance with AMS 3.5-13.(c)(1).
 */

package gov.faa.ang.swac.common.flightmodeling.jni;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Fix implements Serializable
{
	private static final long serialVersionUID = 6725876990482288988L;
	public String name;
    public double separationTime;
    public boolean isShared;
    public List<AirportSeparation> airportSeparationList;
    
	public Fix()
	{
		airportSeparationList = new ArrayList<AirportSeparation>();
	}
	
    public Fix(gov.faa.ang.swac.common.entities.Fix fix) {
        this.name = fix.getName();
        this.separationTime = (fix.getSeparationTime() == null ? Double.NaN : fix.getSeparationTime());
        airportSeparationList = new ArrayList<AirportSeparation>();
    }
	
	public double getSeparationTimeForAirport(String airportCode) {
		for (AirportSeparation airportSep: airportSeparationList) {
			if (airportSep.getAirportCode().equals(airportCode))
				return airportSep.getSeparationTime();
		}
		
		return 0.;
	}
	
	public void addAirportSeparation(AirportSeparation airportSeparation) {
		airportSeparationList.add(airportSeparation);
	}

	public void setShared() { 
		isShared = true;
	}
			
	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		str.append(name).append("; sepTime:").append(separationTime).append("; isShared: ").append(isShared).append(" - ");
		for (AirportSeparation sep: airportSeparationList) {
			str.append('[').append(sep.toString()).append(']');
		}
		return str.toString();
	}
}

