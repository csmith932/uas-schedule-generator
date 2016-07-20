package gov.faa.ang.swac.common.utilities;

/**
 * Class with various mathematical static methods that don't fit nicely in other packages.
 * 
 * @author James Bonn
 */
public class Mathematics
{
	/**
	 * For floating-point computations, this value is used as the maximum threshold for equivalency with absolute zero.
	 * <p>
	 * In other words, a floating point number is considered to be zero, if it is <b>less than or equal to</b> this value.
	 */
	static final double ZERO_TOLERANCE = 0.00000000001;
	
	/**
	 * The minimum numerical difference between floating-point values for them to be considered different in certain numerical comparisons.
	 * <p>
	 * In other words, if the difference between two floating point values is <b>less than
	 * or equal to</b> this, then the values are considered identical for some computations.  
	 */
	static final double NUMERICAL_TOLERANCE = 0.000000001;
	
    private Mathematics()
    {
    }

	/**
	 * Returns the numerical tolerance for equality with zero in floating-point comparison.
	 * When using this numerical tolerance, two floating-point values are considered equal
	 * if one is within +/- the numerical tolerance (inclusive) of the other.<br>
	 * <code>a = b if (b >= a - tolerance) && (b <= a + tolerance)</code>
	 * @return numerical tolerance value
	 * @see #ZERO_TOLERANCE
	 */
	public static double zeroTolerance()
	{
		return ZERO_TOLERANCE;
	}
	
	
	
	/**
	 * Returns the current numerical tolerance for equality in floating-point comparison.
	 * When using this numerical tolerance, two floating-point values are considered equal
	 * if one is within +/- the numerical tolerance (inclusive) of the other.<br>
	 * <code>a = b if (b >= a - tolerance) && (b <= a + tolerance)</code>
	 * @return numerical tolerance value
	 * @see #NUMERICAL_TOLERANCE
	 */
	public static double numericalTolerance()
	{
		return NUMERICAL_TOLERANCE;
	}
	
	
	
	/**
	 * A floating-point equality check.
	 * @param a
	 * @param b
	 * @return true if (b >= a - tolerance) && (b <= a + tolerance)... false otherwise
	 * @see #NUMERICAL_TOLERANCE
	 */
	public static boolean equals(double a, double b)
	{
		if (b >= a-NUMERICAL_TOLERANCE  && b <= a+NUMERICAL_TOLERANCE)
		{
			return true;
		}
		return false;
	}
	
	/**
	 * A floating-point equality check.
	 * @param a
	 * @param b
	 * @param tolerance
	 * @return true if (b >= a - tolerance) && (b <= a + tolerance)... false otherwise
	 * @see #NUMERICAL_TOLERANCE
	 */
	public static boolean equals(double a, double b, double tolerance)
	{
		if (b >= a-tolerance  && b <= a+tolerance)
		{
			return true;
		}
		return false;
	}
	
	/**
	 * A floating-point less-than check.
	 * @param a
	 * @param b
	 * @return true if (b < a - tolerance)... false otherwise
	 */
	// XXX: This seems to be backward and as such is 100% equivalent to greaterThan
	public static boolean lessThan(double a, double b)
	{
		if (b < a-NUMERICAL_TOLERANCE)
		{
			return true;
		}
		return false;
	}
	
	/**
	 * A floating-point greater-than check.
	 * @param a
	 * @param b
	 * @return true if (a > b + tolerance)... false otherwise
	 */
	public static boolean greaterThan(double a, double b)
	{
		if (a > b+NUMERICAL_TOLERANCE)
		{
			return true;
		}
		return false;
	}
	
	/**
	 * A floating-point greater-than check.
	 * @param a
	 * @param b
	 * @return true if (a > b + tolerance)... false otherwise
	 */
	public static boolean greaterThan(double a, double b, double tolerance)
	{
		if (a > b+tolerance)
		{
			return true;
		}
		return false;
	}
	
	/**
     * Find (a mod b) = (a - b*floor(a/b)).
     * 
     * @param a double value of number to mod
     * @param b double value of number to mod by
     * @return double value (a mod b).
     * @throws ArithmeticException when b=0.
     */
    public static double mod(double a, double b) throws ArithmeticException
    {
        if(b != 0)
        {
            double tempInt = Math.floor(a/b);
            return (a-b*tempInt);
        }
        
        throw new ArithmeticException("Trying to Mod by 0");
    }
    
    /**
     * Round input a to n decimal places.  n < 0 gives positions left of the decimal point.
     * <p>
     * Example: round(12.5465,2) = 12.55
     * <p>
     * Example: round(12.5433,-1) = 10
     * 
     * @param a double input
     * @param n int number of decimals
     * @return double a rounded to n decimals
     */
    public static double round(double a, int n)
    {
        return Math.round(a*Math.pow(10,n))/Math.pow(10,n);
    }
    
    /**
     * Floor input a to n decimal places.  n < 0 gives positions left of the decimal point.
     * <P>
     * Example: floor(12.5465,2) = 12.54
     * <P>
     * Example: floor(12.5465,-1) = 10
     * 
     * @param a double input
     * @param n int number of decimals
     * @return double a floored to n decimals
     */
    public static double floor(double a, int n)
    {
        return Math.floor(a*Math.pow(10,n))/Math.pow(10,n);
    }
    
}