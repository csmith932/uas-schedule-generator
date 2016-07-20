/**
 * Copyright "TBD", Metron Aviation & CSSI.  All rights reserved.
 * 
 * This computer Software was developed with the sponsorship of the U.S. Government
 * under Contract No. DTFAWA-10-D-00033, which has a copyright license in accordance with AMS 3.5-13.(c)(1).
 */

package gov.faa.ang.swac.common.entities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;


/**
 * This class test the AirportParetoCurve loading logic to ensure that it properly adds additional points to the pareto
 * curve so that the curve will include the 0 capacity departure point and the 0 capacity arrival point if it doesn't
 * contain those points already. The newly inserted points will be determined by extending the curve in a straight line
 * horizontally (for departures) or vertically (for arrivals).
 * 
 * @author cunningham
 * 
 */
public class AirportParetoCurveTest
{
	
	@Test
	public void testIntercepts() throws IOException {
		Double [] arrivals = new Double [] {  105.2, 105.2, 87.1, 73.1,  73.1,  45.1,   0. };
		Double [] departures = new Double [] {  0.,   70.,  87.2, 95.3, 109.6, 133.2, 133.2 };
		List<Double> arrivalsList = Arrays.asList(arrivals);
		List<Double> departuresList = Arrays.asList(departures);

		test4cases("ATL", "IMC", arrivalsList, departuresList);
		
		Collections.reverse(arrivalsList);
		Collections.reverse(departuresList);

		test4cases("ATL", "IMC", arrivalsList, departuresList);
	}	

	/**
	 * Given an airport code, weather, and pareto curve points, this method will run four tests on the
	 * AirportParetoCurve.
	 * 
	 * Test #1 Test whether the AirportParetoCurve can load up a normal pareto curve
	 * Test #2 Test whether the AirportParetoCurve can load up a pareto curve with the first point missing
	 * Test #3 Test whether the AirportParetoCurve can load up a pareto curve with the last point missing 
	 * Test #4 Test whether the AirportParetoCurve can load up a pareto curve with both the first and last points missing     
	 */ 
	private void test4cases(String code, String wx, List<Double> arrivalsList, List<Double> departuresList) throws IOException {
		testCase(code, wx, arrivalsList, departuresList, false, false);
		testCase(code, wx, arrivalsList, departuresList, true, false);
		testCase(code, wx, arrivalsList, departuresList, false, true);
		testCase(code, wx, arrivalsList, departuresList, true, true);
	}
	

	/**
	 * Will test the AirportParetoCurve loader using the given airport code, weather, pareto curve points, and
	 * removeFirstPoint and removeLastPoint flags.
	 * 
	 * @param code
	 * @param wx
	 * @param arrivalsList
	 * @param departuresList
	 * @param removeFirstPoint
	 * @param removeLastPoint
	 * @throws IOException
	 */
	private void testCase(String code, String wx, List<Double> arrivalsList, List<Double> departuresList,
			boolean removeFirstPoint, boolean removeLastPoint) throws IOException {
		
		String record = createCSVRecord(code, wx, arrivalsList, departuresList, removeFirstPoint, removeLastPoint);
		StringReader sr = new StringReader(record);
    	BufferedReader reader = new BufferedReader(sr);
    	
    	AirportParetoCurve apc = new AirportParetoCurve();
    	apc.readItem(reader);
    	
//    	System.out.println("expected arrivals : " + arrivalsList);
//    	System.out.println("actual arrivals   : " + Arrays.toString(apc.arrivalCapacities));
//    	System.out.println("expected departures : " + departuresList);
//    	System.out.println("actual departures   : " + Arrays.toString(apc.departureCapacities));
//    	System.out.println("");
    	
    	int testResult = compareNumbers(arrivalsList, apc.getArrivalCapacities()); 
    	Assert.assertTrue("Arrivals failed at test at index " + testResult, testResult < 0);
    	
    	testResult = compareNumbers(departuresList, apc.getDepartureCapacities());
    	Assert.assertTrue("Departures failed at test at index " + testResult, testResult < 0);
    	
	}


	/**
	 * Creates a csv delimited String pareto curve record, using the given airport code, wxtype, and pareto curve points.
	 *   
	 * @param code
	 * @param wx
	 * @param arrivalsList
	 * @param departuresList
	 * @param removeFirstPoint If true, will create the record without the first point in the curve
	 * @param removeLastPoint If true, will create the record without the last point in the curve
	 * @return
	 */
	private String createCSVRecord(String code, String wx, List<Double> arrivalsList, List<Double> departuresList, 
								boolean removeFirstPoint, boolean removeLastPoint) {
		int startIndex = 0;
		if (removeFirstPoint)
			startIndex = 1;
		
		int endIndex = departuresList.size();
		if (removeLastPoint)
			endIndex--;
		
		return createCSVRecord(code, wx, arrivalsList.subList(startIndex, endIndex), departuresList.subList(startIndex, endIndex));
	}

	/**
	 * Creates a csv delimited String pareto curve record, using the given airport code, wxtype, and pareto curve points.
	 */
	private String createCSVRecord(String code, String wx, List<Double> arrivals, List<Double> departures) {
		StringBuilder sb = new StringBuilder();
		sb.append(code);
		sb.append(',');
		sb.append(code).append('_').append(wx);
		for (int i = 0; i < arrivals.size(); i++) {
			sb.append(", ");
			sb.append(arrivals.get(i));
			sb.append(',');
			sb.append(departures.get(i));
		}
		return sb.toString();
	}
	

	/**
	 * Compares the list of numbers to the array of numbers and returns the first index where they differ, or -1 if they are equal.
	 * 
	 * @param list
	 * @param array
	 * @return
	 */
	private int compareNumbers(List<Double> list, double [] array) {
		int min = Math.min(list.size(), array.length);
		for (int i = 0; i < min; i++) {
			if (list.get(i) != array[i]) {
				return i;
			}
		}
		if (list.size() != array.length) 
			return min+1;
		return -1;
	}	
}