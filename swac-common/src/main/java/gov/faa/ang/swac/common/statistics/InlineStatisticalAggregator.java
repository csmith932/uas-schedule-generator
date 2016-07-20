package gov.faa.ang.swac.common.statistics;

/**
 * Class for computing statistical aggregates on the fly without maintaining 
 * individual data points. The algorithm for the variance is from Knuth's The 
 * Art of Computer Programming and is attributed to Welford 
 * (ref <a href="http://en.wikipedia.org/wiki/Algorithms_for_calculating_variance#On-line_algorithm">Wikipedia article</a>)
 * 
 * @author ssmitz
 *
 * @param <T> Any class which extends {@link java.lang.Number} as the 
 * {@link Number#doubleValue()} method is required to compute running 
 * variance.
 */

public class InlineStatisticalAggregator extends StatisticalAggregator {

    protected long n;
    protected double mean;
    protected double M2;
    protected double M3;
    protected double M4;
    
    protected Number min;
    protected Number max;

    /**
     * Initializes the aggregator.
     */
    public InlineStatisticalAggregator() {
        this.n = 0;
        this.mean = 0;
        this.M2 = 0;
        this.M3 = 0;
        this.M4 = 0;
    }

    @Override
    public long getNumberOfDataPoints() {
        return this.n;
    }

    @Override
    public Number getMinimum() {
        return this.min;
    }

    @Override
    public Number getMaximum() {
        return this.max;
    }

    /**
     * 
     * @return the sum of all the data points received so (computed from the mean).
     */
    @Override
    public double getSum() {
        return this.mean * this.n;
    }

    @Override
    public double getMean() {
        return this.mean;
    }
    
    @Override
    public double getVariance() {
        if (this.n > 1) {
            return this.M2 / (this.n-1);
        } else {
            return 0;
        }
    }

    /**
     * The results from this function are not currently valid.
     * 
     * @return the coefficient of skewness
     */
    @Override
    public double getSkewness() {
        double denominator = Math.sqrt(this.M2 * this.M2 * this.M2);

        if (denominator != 0) {
            return (Math.sqrt(this.n) * this.M3) / denominator;
        } else {
            return 0;
        }
    }


    /**
     * The results from this function are not currently valid.
     * 
     * @return the coefficient of kurtosis
     */
    @Override
    public double getKurtosis() {
        double denominator = this.M2 * this.M2;

        if (denominator != 0) {
            return (this.n * this.M4) / denominator - 3;
        } else {
            return 0;
        }
    }
    
    @Override
    public double getStandardDeviation() {
        return Math.sqrt(this.getVariance());
    }

    /**
     * Takes in a data point and updates the statistical aggregate data. Actually 
     * implements the algorithm referenced by 
     * <a href="http://en.wikipedia.org/wiki/Algorithms_for_calculating_variance#On-line_algorithm">Wikipedia article</a>
     * 
     * @param value the next data point to add to the aggregate data.
     */
    @Override
    public void addDataPoint(Number value) {
        this.n++;

        long n1 = this.n - 1;

        double delta = value.doubleValue() - this.mean;
        double deltaN = delta / this.n;
        double deltaN2 = deltaN * deltaN;
        double term1 = delta * deltaN * n1;

        this.mean += deltaN;
        this.M4 += term1 * deltaN2 * (this.n * this.n - 3 * this.n + 3) + 6 * deltaN2 * this.M2 - 4 * deltaN * this.M3;
        this.M3 += term1 * deltaN * (this.n - 2) - 3 * deltaN * M2;
        this.M2 += term1;
        
        TComparator comparator = new TComparator();
        
        if (this.min == null) {
            this.min = value;
            this.max = value;
        } else if (comparator.compare(min, value) > 0) {
            this.min = value;
        } else if (comparator.compare(max, value) < 0) {
            this.max = value;
        }
    }

    @Override
    public void addWeightedDataPoint(Number value, double weight) {
    	throw new UnsupportedOperationException("Not a valid operation for unweighted inline processing.");
    }
    
    @Override
    public Number getMedian() {
        throw new UnsupportedOperationException("Not a valid operation for inline processing.");
    }

    @Override
    public Number getPercentile(double fraction) {
        throw new UnsupportedOperationException("Not a valid operation for inline processing.");
    }

    @Override
    public double getHalfWidth(double confidenceLevel) {
        throw new UnsupportedOperationException("Not a valid operation for inline processing.");
    }

}
