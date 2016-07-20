/**
 * Copyright "TBD", Metron Aviation & CSSI.  All rights reserved.
 * 
 * This computer Software was developed with the sponsorship of the U.S. Government
 * under Contract No. DTFAWA-10-D-00033, which has a copyright license in accordance with AMS 3.5-13.(c)(1).
 */

package gov.faa.ang.swac.common.random;

import gov.faa.ang.swac.common.flightmodeling.jni.TimeDistribution;


public interface RandomStream {

	void seed(long seed);
	
	double normal(double mean, double stdDev);
	
	double uniform(double min, double max);
	
	double getDistributionHoldTime(TimeDistribution td);

}