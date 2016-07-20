package gov.faa.ang.swac.common.statistics;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Comparator;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 *
 * @author ssmitz
 */
public abstract class StatisticalAggregator<T extends Number> {
    /**
     * 
     * @return the number of data points received so far.
     */
    public abstract long getNumberOfDataPoints();

    /**
     * 
     * @return the minimum value received thus far or null if no values
     * have been received.
     */
    public abstract T getMinimum();

    /**
     * 
     * @return the maximum value received thus far or null if no values
     * have been received.
     */
    public abstract T getMaximum();

    /**
     * 
     * @return the sum of all the data points received.
     */
    public abstract double getSum();

    /**
     * 
     * @return the mean.
     */
    public abstract double getMean();

    /**
     * 
     * @return the variance.
     */
    public abstract double getVariance();

    /**
     * 
     * @return the standard deviation. Computed as sqrt(variance).
     */
    public abstract double getStandardDeviation();

    /**
     * 
     * @return the coefficient of skewness.
     */
    public abstract double getSkewness();

    /**
     * 
     * @return the coefficient of kurtosis. 
     */
    public abstract double getKurtosis();
    
    /**
     * 
     * @return the value which is exactly in position ceil(n/2) when the data 
     * points are sorted.
     */
    public abstract T getMedian();
    
    /**
     * 
     * @return the value which is exactly in position ceil(n * fraction) when 
     * the data points are sorted.
     */
    public abstract T getPercentile(double fraction);
    
    /**
     * @return the half-width.
     */
    public abstract double getHalfWidth(double confidenceLevel);
    
    /**
     * Takes in a () data point.
     * @param value the next data point to add to the aggregate data.
     */
    public abstract void addDataPoint(T value);
    
    /**
     * Takes in a () data point and the weight to apply to the data point.
     * @param value the next data point to add.
     * @param weight The weight to apply to the value.
     */
    public abstract void addWeightedDataPoint(T value, double weight);
    
    protected class TComparator implements Comparator<T>, Serializable {
        @Override
        public int compare(T o1, T o2) {
            int rtn;
            
            if (o1 instanceof AtomicInteger) {
                Integer i = Integer.valueOf(o1.intValue());
                rtn = i.compareTo(Integer.valueOf(o2.intValue()));
            } else if (o1 instanceof AtomicLong) {
                Long l = Long.valueOf(o1.longValue());
                rtn = l.compareTo(Long.valueOf(o2.longValue()));
            } else if (o1 instanceof BigDecimal) {
                BigDecimal bd1 = (BigDecimal)o1;
                rtn = bd1.compareTo((BigDecimal)o2);
            } else if (o1 instanceof BigInteger) {
                BigInteger bi1 = (BigInteger)o1;
                rtn = bi1.compareTo((BigInteger)o2);
            } else if (o1 instanceof Byte) {
                Byte b = Byte.valueOf(o1.byteValue());
                rtn = b.compareTo(Byte.valueOf(o2.byteValue()));
            } else if (o1 instanceof Double) {
                Double d = Double.valueOf(o1.doubleValue());
                rtn = d.compareTo(Double.valueOf(o2.doubleValue()));
            } else if (o1 instanceof Float) {
                Float f = Float.valueOf(o1.floatValue());
                rtn = f.compareTo(Float.valueOf(o2.floatValue()));
            } else if (o1 instanceof Integer) {
                Integer i = Integer.valueOf(o1.intValue());
                rtn = i.compareTo(Integer.valueOf(o2.intValue()));
            } else if (o1 instanceof Long) {
                Long l = Long.valueOf(o1.longValue());
                rtn = l.compareTo(Long.valueOf(o2.longValue()));
            } else if (o1 instanceof Short) {
                Short s = Short.valueOf(o1.shortValue());
                rtn = s.compareTo(Short.valueOf(o2.shortValue()));
            } else {
                Double d1 = Double.valueOf(o1.doubleValue());
                Double d2 = Double.valueOf(o2.doubleValue());
                rtn = d1.compareTo(d2);
            }
            
            return rtn;
        }
    }
}
