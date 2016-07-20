/**
 * Copyright "TBD", Metron Aviation & CSSI. All rights reserved.
 * 
 * This computer Software was developed with the sponsorship of the U.S. Government under Contract No.
 * DTFAWA-10-D-00033, which has a copyright license in accordance with AMS 3.5-13.(c)(1).
 */

package gov.faa.ang.swac.common.utilities;

import java.text.NumberFormat;
import java.util.Collection;
import java.util.Iterator;
import java.util.regex.Pattern;



/**
 * 
 */
public class ParseFormatUtils {
	
	private static Pattern commaPattern = Pattern.compile(",");
	private static ThreadLocal<NumberFormat> numberFormatRef = new ThreadLocal<NumberFormat>() { 
        @Override
        protected NumberFormat initialValue() {
        	NumberFormat numberFormat = NumberFormat.getInstance();
			numberFormat.setMaximumFractionDigits(2);
			numberFormat.setMinimumFractionDigits(2);
			numberFormat.setGroupingUsed(false);
            return numberFormat;
        }
	};
	
	
	public static String parseString(String text) {
		if (isBlank(text))
			return null;
		return text;
	}
	
	public static Integer parseInteger(String text) { 
		if (isBlank(text))
			return null;
		return Integer.valueOf(text);
	}
	
	public static int parseIntWithMinValueKludge(String text) {
		return isBlank(text) ? Integer.MIN_VALUE : Integer.parseInt(text);
	}

	public static Double parseDouble(String text) {
		if (isBlank(text))
			return null;
		return Double.valueOf(text);
	}
	
	public static double parseDoubleWithNAKludge(String text) {
		return isBlank(text) ? Double.NaN : Double.parseDouble(text);
	}
	
	public static Boolean parseBoolean(String text) {
		// 1 is true, 0 is false
		if (isBlank(text))
			return null;
		return "1te".indexOf(Character.toLowerCase(text.charAt(0))) >= 0;
	}
	
	/**
	 * Will parse the given String as a double, with special allowances for the String to represent infinity. val is
	 * considered infinite if val is equal (IgnoreCase) to infiniteCapacityStr or if val is negative and the
	 * negativeValuesAreInfinite flag is true.
	 * 
	 * @param val value to parse
	 * @param ifNullValue value to return if val is null
	 * @param infiniteCapacity value to return if val is deemed to be infinite
	 * @param infiniteCapacityStr String representation of infinity
	 * @param negativeValuesAreInfinite flag on whether to return infinite value if val is negative
	 * @return value parsed from val or ifNullValue (if val is negative), or infiniteCapacity (if val represents
	 *         infinity)
	 */
	public static Double parseDoubleOrInfinity(String val, double ifNullValue, double infiniteCapacity, String infiniteCapacityStr, boolean negativeValuesAreInfinite) { 
		double capacity = ifNullValue;
        if (val != null) {
        	val = val.trim();
        	boolean infinite = val.equalsIgnoreCase(infiniteCapacityStr);
        	if (! infinite) {
        		capacity = Double.parseDouble(val);
    			if (negativeValuesAreInfinite && capacity < 0) {
    				capacity = infiniteCapacity;
    			}
        	} 
        }
        return capacity;
	}
	
	public static String booleanToStringInt(Boolean bool) {
		if (bool == null)
			return "";
		return bool ? "1" : "0";
	}
	
	public static String blankIfNull(Object s) { 
		return s == null ? "" : s.toString();
	}

	public static String blankIfNull(Double s) {
		if (s == null || s.isNaN())
			return "";
		else
			return s.toString();
	}

	public static String blankIfMinValue(Integer i) {
		if (i == null)
			return "";
		return i == Integer.MIN_VALUE ? "" : i.toString();
	}

	public static Integer nullIfMinValue(int i) { 
		return i == Integer.MIN_VALUE ? null : i;
	}
	
	public static boolean isNullOrBlank(String val) {
		if (val == null)
			return true;
		
		if (val.isEmpty())
			return true;
		
		for (int i = 0; i < val.length(); i++) { 
			if (! Character.isWhitespace(val.charAt(i))) {
				return false;
			}
		}
		return true;
	}
	
	public static boolean isBlank(String val) {
		if (val.isEmpty())
			return true;
		
		for (int i = 0; i < val.length(); i++) { 
			if (! Character.isWhitespace(val.charAt(i))) {
				return false;
			}
		}
		return true;
	}
	
	public static String nullIfBlank(String s) {
		if (isBlank(s))
			return null;
		return s;
	}
	
	public static String nullIfBlankTrimOtherwise(String s) {
		if (s == null)
			return null;
		s = s.trim();
		if (s.isEmpty())
			return null;
		return s;
	}
	
	/**
	 * Splits line by comma.  Faster than String.split() because the pattern is already compiled.
	 * @param line
	 * @return
	 */
	public static String [] commaSplit(String line) { 
		return commaPattern.split(line); 
	}
	
	/**
	 * Splits line by comma.  Faster than String.split(limit) because the pattern is already compiled.
	 * @param line
	 * @return
	 */
	public static String [] commaSplit(String line, int limit) { 
		return commaPattern.split(line, limit); 
	}
	
	/**
	 * Replaces one or more consecutive whitespace characters with one space
	 * 
	 * @param str
	 * @return
	 */
	public static String trimConsecutiveWhiteSpace(String str) {
		if (str.isEmpty())
			return str;
		StringBuilder sb = new StringBuilder(str.length());
		boolean priorCharWhiteSpace = false;
		for (int i =0; i < str.length(); i++) {
			char c = str.charAt(i);
			if (Character.isWhitespace(c)) {
				if (! priorCharWhiteSpace) {
					priorCharWhiteSpace = true;
					sb.append(" ");
				} 
			} else { 
				sb.append(c);
				priorCharWhiteSpace = false;
			}
		}
		//System.out.printf("#<-%s\n#->%s\n", str, sb.toString());
		return sb.toString();
	}
	
	/**
	 * Returns a String consisting of each item toStringed separated by the given delimiter.
	 *  
	 * @param collection
	 * @param delimiter
	 * @return
	 */
	public static <T> String collectionToString(Collection<T> collection, String delimiter) {
		StringBuilder sb = new StringBuilder();
		Iterator<T> it = collection.iterator();
		if (it.hasNext()) 
			sb.append(it.next());
		while (it.hasNext()) {
			sb.append(delimiter);
			T item = it.next();
			sb.append(item);
		}
		return sb.toString();
	}
	
	public static String formatDoubleTwoDigits(Double number){
		NumberFormat numberFormat = numberFormatRef.get();
		return numberFormat.format(number);
	}
	
	public static String ZeroIfNull(Double s) {
		if (s == null || s.isNaN())
			return "0";
		else
			return s.toString();
	}
	
	public static void main(String[] args) {
		String ss = "hi  how \n how  you";
		String s ="If auto-detection of the number of processors is enabled, this provides an additional limiting " + 
			"\n constraint to the maximum number of processors. If too many processors are used, the JVM can" +
			"\nrun out of memory, causing SWAC to crash. This parameter sets the expected memory footprint per" +
			"\ninstance, and the number of processors is limited so that the total does not exceed available" +
			"\nmemory. Note that the maximum of for the JVM and not the system. The JVM maximum memory must be updated" +
			"\nseparately in the run script using the -Xmx parameter. Default is 3GB";
		trimConsecutiveWhiteSpace(s);
	}
}
