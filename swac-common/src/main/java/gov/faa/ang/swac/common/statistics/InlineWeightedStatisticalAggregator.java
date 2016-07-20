package gov.faa.ang.swac.common.statistics;

public class InlineWeightedStatisticalAggregator extends StatisticalAggregator {

    protected long n;
    protected double mean;
    protected double M2;
    protected double M3;
    protected double M4;
    protected double sumWeight;
    
    private Number min;
    private Number max;

    public InlineWeightedStatisticalAggregator() {

        this.n = 0;
        this.mean = 0;
        this.M2 = 0;
        this.M3 = 0;
        this.M4 = 0;
        this.sumWeight = 0;
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
        return Double.NaN;
    }

    @Override
    public double getSkewness() {
        return Double.NaN;
    }

    @Override
    public double getKurtosis() {
        return Double.NaN;
    }

    @Override
    public double getStandardDeviation() {
        return Double.NaN;
    }

    public void addWeightedDataNoIncrement(Number value, double weight) {
        this.mean = (this.mean * this.n + weight * value.doubleValue()) / this.n;
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

    @Override
    public void addDataPoint(Number value) {
        addWeightedDataPoint(value, 1.0);
    }

    @Override
    public void addWeightedDataPoint(Number value, double weight) {
        this.n++;

        this.mean = (this.mean * this.n + weight * value.doubleValue()) / this.n++;

        this.sumWeight += weight;
        
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
}
