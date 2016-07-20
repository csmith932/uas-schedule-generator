package gov.faa.ang.swac.common.flightmodeling.jni;

import java.io.Serializable;
import java.util.Random;

import gov.faa.ang.swac.common.datatypes.Timestamp;
import gov.faa.ang.swac.common.random.distributions.NormalDistribution;

public class TimeDistribution implements Serializable {
	
	private static final long serialVersionUID = -2629674602623601619L;
	
	/** The mean time. */
	private double mean;
	/** The time distribution. */
	private double stdDev;
	
	public TimeDistribution(double mean, double stdDev){
		this.mean = mean;
		this.stdDev = stdDev;
	}

	public double getStdDev() { return stdDev; }

	public double getMean() { return mean; }

	public void setStdDev(double stdDev) { this.stdDev = stdDev; }

	public void setMean(double mean) { this.mean = mean; }

	public double normal(double random) {
		if(stdDev == 0) {
			return mean;
		} else { 
			return NormalDistribution.inverseF(mean, stdDev, random);
		}
		// http://www.javamex.com/tutorials/random_numbers/gaussian_distribution_2.shtml
		//return random.nextGaussian() * stdDev + mean;
	}
	
	public String toString() { return "TimeDistribution [ mean = " + mean + "; stdDev = " + stdDev + "]"; } 

}
