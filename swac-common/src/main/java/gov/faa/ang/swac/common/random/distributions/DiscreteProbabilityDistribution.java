/**
 * Copyright 2012, Metron Aviation & CSSI.  All rights reserved.
 * 
 * This computer Software was developed with the sponsorship of the U.S. Government
 * under Contract No. DTFAWA-10-D-00033, which has a copyright license in accordance with AMS 3.5-13.(c)(1).
 */
package gov.faa.ang.swac.common.random.distributions;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DiscreteProbabilityDistribution extends DiscreteDistribution
{
	public DiscreteProbabilityDistribution(double[] values, double[] probs, int size)
	{
		super(values,probs,size);
	}

	@Override
	public double nextDouble(Random random)
	{
		double rand = random.nextDouble();
		int index = 0;
		while(index < this.getN() && rand > this.cdf(this.getValue(index))){
				++index;
		}
		return this.getValue(index);
	}
	
	@Override
	public DistributionType getDistributionType(){
		return DistributionType.DISCRETE;
	}
	
	@Override
	public String getDistributionDescription(){
		return "Variable Distribution curve based on input CDF files";
	}
	
	@Override
	public List<String> getConfigurabeFields(){
		List<String> fields  = new ArrayList<String>();
		return fields;
	}
}
