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

public class ConstantProbabilityDistribution implements Distribution
{
	private double value;
	
	public ConstantProbabilityDistribution(double value)
	{
		this.value = value;
	}

	public double getValue()
	{
		return value;
	}

	public void setValue(double value)
	{
		this.value = value;
	}

	@Override
	public double cdf (double x){
		if (x >= value)
			return 1;
		else
			return 0;
	}
	
	@Override
	public double barF (double x){
		if (x >= value)
			return 0;
		else
			return 1;
	}
	
	@Override
	public double nextDouble(Random random){
		return value;
	}

	@Override
	public DistributionType getDistributionType(){
		return DistributionType.CONSTANT;
	}
	
	@Override
	public String getDistributionDescription(){
		return "Always returns: " + value;
	}
	
	@Override
	public List<String> getConfigurabeFields(){
		List<String> fields  = new ArrayList<String>();
		fields.add("value");
		return fields;
	}
}
