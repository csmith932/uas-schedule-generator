/**
 * Copyright "TBD", Metron Aviation & CSSI.  All rights reserved.
 * 
 * This computer Software was developed with the sponsorship of the U.S. Government
 * under Contract No. DTFAWA-10-D-00033, which has a copyright license in accordance with AMS 3.5-13.(c)(1).
 */

package gov.faa.ang.swac.common.random;

import gov.faa.ang.swac.common.flightmodeling.jni.TimeDistribution;
import gov.faa.ang.swac.common.random.distributions.NormalDistribution;
import gov.faa.ang.swac.common.random.distributions.UniformDistribution;

import java.util.Random;


/**
 * This class produces generating uniform and normal numbers. It delegates to java Random and swac common classes.
 * 
 * @author cunningham
 */
public class RandomStreamJSim implements RandomStream {
	private Random random = new Random();
		
	public RandomStreamJSim() { 
		random = new Random();
	}
	
	public RandomStreamJSim(long seed) {
		random = new Random(seed);
	}
	
	@Override
	public void seed(long seed) {
		random.setSeed(seed);
	}
	
	@Override
	public double uniform(double min, double max) {
		return UniformDistribution.inverseF(min, max, random.nextDouble());
		//return random.nextDouble() * (max - min) + min;
	}
	
	@Override
	public double normal(double mean, double stdDev) {
		return NormalDistribution.inverseF(mean, stdDev, random.nextDouble());
		
		// http://www.javamex.com/tutorials/random_numbers/gaussian_distribution_2.shtml
		//return random.nextGaussian() * stdDev + mean;
	}
		
	public double getDistributionHoldTime(TimeDistribution td)
	{
		return td.normal(random.nextDouble());
	}
	
	public static void main(String[] args) {
		System.out.println("start");
		
		RandomStreamJSim random = new RandomStreamJSim();
		//random.init();
		random.seed(5L);
		for (int i = 0; i< 15; i++) {
			double d = random.uniform(5., 10.);
			System.out.println("uniform: " + d);
		}
		for (int i = 0; i< 15; i++) {
			double normal = random.normal(500., 5.);
			System.out.println("normal: " + normal);
		}
		
//		System.out.println("----------");
		
//		double [] uniformArray = new double[5];
//		random.uniforms(5., 10., uniformArray);
//		for (int i = 0; i< uniformArray.length; i++) {
//			System.out.println("uniform: " + uniformArray[i]);
//		}
//
//		double [] normalArray = new double[25];
//		random.normals(100., 50., normalArray);
//		for (int i = 0; i< normalArray.length; i++) {
//			System.out.println("normal: " + normalArray[i]);
//		}
		
		System.out.println("done");
	}
}