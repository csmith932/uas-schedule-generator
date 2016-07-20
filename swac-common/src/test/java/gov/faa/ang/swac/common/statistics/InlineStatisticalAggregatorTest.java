package gov.faa.ang.swac.common.statistics;

import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import gov.faa.ang.swac.common.statistics.InlineStatisticalAggregator;

public class InlineStatisticalAggregatorTest
{
    private int[] salaries = {
            900000,   300000, 10100000,  5500000,   750000, 11428571,
            2200000,  6000000, 15600000,   364100,  5000000,   400000,
             550000,  6000000, 13000000, 12000000,  2400000, 11500000,
            8000000, 10500000,   800000,  2500000,  5000000,  4150000,
            3250000, 12357143,   700000,  1500000,  5350000, 11500000,
             302500,   325000,   425000,  7250000,  1000000,   725000,
            7250000,  2270000,   315000,  1425000,   320000,  2266667,
             925000,  7833333,   300000,   312500,  9900000,  1425000,
            8166667,   305000,  4250000,  3875000,   375000,   302500,
             337500,  5500000,  7500000,  3000000,   324500,   500000,
           11000000,  2000000,   300000,   407500,  1700000,   625000,
            3625000,   309500, 15500000,  2900000,  2000000,   805000,
            2100000,  4000000,  1250000,   300000, 20000000,  1850000,
            4700000,  4000000,  3450000,   300000,  1500000,  5125000,
             302100,  1000000,   330000,   314300,   303000,  7166667,
             301100,   900000,  3916667,   302200,   400000,  6750000,
             300900,   314400,   500000,   307500,   300900,   314000,
             302400,  1100000,   303200,   600000,   325000,   600000,
             300000,  5500000,   305500,  6000000,  2600000,   700000,
            1000000,  2200000,   700000, 18700000,  3900000,   310000,
            3825000,   302000,   600000,   313000,   313000,  5350000,
             350000,   300000,   305000,   800000,   320000,   845000,
            6200000,  1000000,   600000,   425000,   520000,   300000,
             316000,   775000,  6400000,   325000,   300000, 13000000,
            4250000,  3100000,  1000000,  3000000,  7030000,  3500000,
             375000,   330000,  3000000,  1550000,  1000000,  1200000,
            2900000,   350000,   305000,  1200000,   325000,   900000,
            1725000,   300000,  4250000,  3500000,   762500,  7000000,
            1000000,   300000,   600000,   300000,   300000,   300000,
             300000,   300000,  5500000,   300000,   325000,   325000,
             300000,   500000,   300000,   300000,  6500000,   400000,
             400000,   300000,   300000,   300000,   300000,   300000,
             300000,   300000,   313000,   314000,  6000000,   304000,
             302000,   309500,   304500,   300000,   625000,   775000,
             303500,  2000000,   300000,   305500,  3000000,   300000,
            1500000,   300000,   301000,   450000,  2750000,  4500000,
             900000, 11000000,  2750000,   311000,   302500,   500000,
             330000,   500000,  2700000,  2525000,  3000000,  1000000,
            4750000,  2750000,   325000,  3400000,   312500,   330000,
            4150000,  1750000,  6000000,   315000,   365000,   450000,
            8750000,  8000000,   340000,  2000000,   325000,   335000,
             700000,   445000,  8250000,   315000,   450000,   375000,
             330000,  1400000,   675000,   345000,  4250000,  6250000,
            4200000,   500000,   330000,   300000,  9000000,   325000,
             450000,   320000,   300000,  5000000,  5000000,   600000,
             325000,   575000,  3200000,   314000,   325000,   300000,
             303000,   300000,  2150000, 11850000,   300000,   315000,
             340000,   305000,   300000,   309000,  1700000,  8500000,
            2625000,  2500000,   310000,   300000,   307000,  4500000,
             305000,   400000,   360000,  6750000,   300000,  8000000,
             500000,  7416667,   400000,  6725000,   750000,  1000000,
             425000,  6875000,  2500000,  1800000,   600000,  4000000,
             302500,  3150000,   325000,  6500000,  3983333,  7700000,
             440000,  3500000,  8000000,   300000,  4666667,  3500000,
            3300000,   304000,   331000,   300000,  3675000,  1065000,
           11666667,   307500,   316000,  6000000,   350000,   750000,
            1750000,  1887500,  2700000,   500000,   335000,  2175000,
            3216667,   334500,  2650000,   300000,   322000,  1700000,
            1200000,  5125000,  1000000,   302500,   300000,   900000,
            1837500,  9150000,   600000,   300000,  1000000, 13000000,
             750000,  7000000,   750000,   440000,   302500,   327500,
             550000,  9000000, 13000000,  1300000,  3250000, 22000000,
             600000,   750000,  1300000,  4500000,  2500000,  2500000,
            1500000,   415000,  3366667      
        };
    
    private InlineStatisticalAggregator salaryAggregator;
    // TODO: Need to determine a better check for the accuracy of the higher-order statistics
    private Double tolerance = new Double(0.00000000001);
    
    @BeforeClass
    public static void setUpBeforeClass() throws Exception
    {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception
    {
    }

    @Before
    public void setUp() throws Exception
    {
        salaryAggregator = new InlineStatisticalAggregator();
        
        for (int salary : salaries)
        {
            salaryAggregator.addDataPoint(salary);
        }
    }

    @After
    public void tearDown() throws Exception
    {
    }

    @Test
    public void testGetNumberOfDataPoints()
    {
        assertTrue(this.salaryAggregator.getNumberOfDataPoints() == salaries.length);
    }

    @Test
    public void testGetMinimum()
    {
        assertTrue(this.salaryAggregator.getMinimum().intValue() == 300000);
    }

    @Test
    public void testGetMaximum()
    {
        assertTrue(this.salaryAggregator.getMaximum().intValue() == 22000000);
    }

    @Test
    public void testGetSum()
    {
        Double actual = 962021983.0;
        Double sum = this.salaryAggregator.getSum();
        Double delta = Math.abs((actual - sum) / actual);
        
        assertTrue(delta < this.tolerance);
    }

    @Test
    public void testGetMean()
    {
        Double actual = 2524992.081364830000000;
        Double mean = this.salaryAggregator.getMean();
        Double delta = Math.abs((actual - mean) / actual);

        assertTrue(delta < this.tolerance);
    }

    @Test
    public void testGetVariance()
    {
        Double actual = 12161920735179.9;
        Double variance = this.salaryAggregator.getVariance();
        Double delta = Math.abs((actual - variance) / actual);
        
        assertTrue(delta < this.tolerance);
    }
    @Ignore
    @Test
    public void testGetSkewness()
    {
        Double actual = 2.285630309420570;
        Double skewness = this.salaryAggregator.getSkewness();
        Double delta = Math.abs((actual - skewness) / actual);
        
        assertTrue(delta == this.tolerance);
    }
    @Ignore
    @Test
    public void testGetKurtosis()
    {
        Double actual = 6.210618855480070;
        Double kurtosis = this.salaryAggregator.getKurtosis();
        Double delta = Math.abs((actual - kurtosis) / actual);
        
        assertTrue(delta < this.tolerance);
    }

    @Test
    public void testGetStandardDeviation()
    {
        Double actual = 3487394.54825231;
        Double standardDeviation = this.salaryAggregator.getStandardDeviation();
        Double delta = Math.abs((actual - standardDeviation) / actual);
        
        assertTrue(delta < this.tolerance);
    }
}
