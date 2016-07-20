package gov.faa.ang.swac.common.statistics;

import gov.faa.ang.swac.common.random.distributions.StudentDistribution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SimpleStatisticalAggregator extends InlineStatisticalAggregator {

    private final List<Number> dataPoints = new ArrayList<Number>();

    @Override
    public void addDataPoint(Number value) {
    	super.addDataPoint(value);
        dataPoints.add(value);
    }
    
	@Override
	public Number getMedian() {
		
		return getPercentile(0.5);
		
	}

	@Override
	public Number getPercentile(double fraction) {
		
	     if (dataPoints.isEmpty()) {
	            return null;
	        }
	       
	     Collections.sort(dataPoints, new TComparator());
	       
	       int index = (int) Math.ceil(fraction * dataPoints.size());
	        
	        if (index >= dataPoints.size()) {
	            return dataPoints.get(dataPoints.size()-1);
	        }
	        return dataPoints.get(index);
	}

	@Override
	public double getHalfWidth(double confidenceLevel) {
    	
		if (dataPoints.size() < 2) {
    		return 0d;
    	}
    	double t = StudentDistribution.inverseF((int)dataPoints.size() - 1, 0.5*(1.0 + confidenceLevel));
    	return t*getStandardDeviation()/Math.sqrt(dataPoints.size());

	}
}
