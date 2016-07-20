/**
 * Copyright "TBD", Metron Aviation & CSSI.  All rights reserved.
 * 
 * This computer Software was developed with the sponsorship of the U.S. Government
 * under Contract No. DTFAWA-10-D-00033, which has a copyright license in accordance with AMS 3.5-13.(c)(1).
 */

package gov.faa.ang.swac.common.flightmodeling.jni;

import java.io.Serializable;

public class AirportSeparation implements Serializable {

	private static final long serialVersionUID = 1455749192443861419L;
	
	private final String airportCode;
	private final double separationTime;

	public AirportSeparation(String airportCode, Double separationTime) {
		this.airportCode = airportCode;
		this.separationTime = separationTime;
	}

	public String getAirportCode() {
		return airportCode;
	}

	public double getSeparationTime() {
		return separationTime;
	}

	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		return str.append("Airport: ").append(airportCode).append("; sepTime:").append(separationTime).toString();
	}
}
