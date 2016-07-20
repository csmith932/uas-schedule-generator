/**
 * Copyright "TBD", Metron Aviation & CSSI.  All rights reserved.
 * 
 * This computer Software was developed with the sponsorship of the U.S. Government
 * under Contract No. DTFAWA-10-D-00033, which has a copyright license in accordance with AMS 3.5-13.(c)(1).
 */

package gov.faa.ang.swac.common.entities;

import gov.faa.ang.swac.datalayer.storage.fileio.TextSerializable;
import gov.faa.ang.swac.datalayer.storage.fileio.WithHeader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class AirportParetoCurve implements TextSerializable, WithHeader, Comparable<AirportParetoCurve>, ParetoCurve
{
	private static final AirportParetoCurve airportInadequateParetoCurve;
	
	static
	{
		airportInadequateParetoCurve = new AirportParetoCurve();
		airportInadequateParetoCurve.airportName = "NONE";
	}

	public static AirportParetoCurve getInadequateAirportParetoCurve() { return airportInadequateParetoCurve; }

	private String airportName;
    private String paretoCurveName;
    
    private double[] arrivalCapacities;
    private double[] departureCapacities;
    
    /**
     * Default constructor required for instantiation via reflection (for data layer interface)
     */
    public AirportParetoCurve()
    {
    	this.airportName = null;
    	this.paretoCurveName = null;
    	this.arrivalCapacities = null;
    	this.departureCapacities =null;
    }
    
    public AirportParetoCurve(String aptName, String name, double[] departure_caps, double[] arrival_caps)
    {
    	this.airportName = aptName;
    	this.paretoCurveName = name;
        
        if (arrival_caps != null)
        {
        	this.arrivalCapacities = new double[arrival_caps.length];
            for (int i=0; i<arrival_caps.length; i++)
            {
            	this.arrivalCapacities[i] = arrival_caps[i];
            }
            assert(arrivalCapacities[0] >= arrivalCapacities[arrivalCapacities.length -1]);
        }
        
        if (departure_caps != null)
        {
        	this.departureCapacities = new double[departure_caps.length];
            for (int i=0; i<departure_caps.length; i++)
            {
            	this.departureCapacities[i] = departure_caps[i];
            }
        }
    }

    @Override
	public boolean isEmpty() {
		return numberOfPoints() == 0;
	}
    
    @Override
	public int numberOfPoints() {
    	assert(departureCapacities.length == arrivalCapacities.length);
    	return Math.min(departureCapacities.length, arrivalCapacities.length);
    }

    /**
     * @return the airport name.
     */
    public String getAirportName()
    {
        return this.airportName;
    }
    
    /**
     * @return the pareto curve name.
     */
    @Override
	public String getParetoCurveName()
    {
        return this.paretoCurveName;
    }
    
    
    public double[] getArrivalCapacities()
    {
    	return Arrays.copyOf(this.arrivalCapacities, this.arrivalCapacities.length);
    }
    
    public double[] getDepartureCapacities()
    {
    	return Arrays.copyOf(this.departureCapacities, this.departureCapacities.length);
    }
    
    
        
    @Override
	public double getDepartureCapacity(int index) { 
    	return departureCapacities[index];
    }

    @Override
	public double getArrivalCapacity(int index) { 
    	return arrivalCapacities[index];
    }
    
    public double getMaximumArrivalCapacity()
    {
        if (this.arrivalCapacities == null) return 0;
        
        return this.arrivalCapacities[0];
    }
    
    public double getMaximumDepartureCapacity()
    {
        if (this.departureCapacities == null) return 0;
        
        return this.departureCapacities[this.departureCapacities.length-1];
    }
    
    public double getMediumArrivalCapacity()
    {
        if (this.arrivalCapacities == null) return 0;
        
        int max_idx = -1;
        double max_capacity_sum = 0;
        for (int i=0; i<this.arrivalCapacities.length; i++)
        {
            double capacity_sum = this.arrivalCapacities[i] + this.departureCapacities[i];
            
            if (capacity_sum > max_capacity_sum)
            {
                max_idx = i;
                max_capacity_sum = capacity_sum;
            }
        }
        
        if (max_idx == -1) return 0;
        
        return this.arrivalCapacities[max_idx];
    }
    
    public double getMediumDepartureCapacity()
    {
        if (this.departureCapacities == null) return 0;
        
        int max_idx = -1;
        double max_capacity_sum = 0;
        for (int i=0; i<this.departureCapacities.length; i++)
        {
            double capacity_sum = this.departureCapacities[i] + this.arrivalCapacities[i];
            
            if (capacity_sum > max_capacity_sum)
            {
                max_idx = i;
                max_capacity_sum = capacity_sum;
            }
        }
        
        if (max_idx == -1) return 0;
        
        return this.departureCapacities[max_idx];
    }
    
    /**
     * Calculates a balanced capacity point, where arrival capacity
     * becomes equal to departure capacity.
     * @return the balanced arrival/departure capacity
     */
    public double getBalancedCapacity()
    {
        int sign_change_index = -1;
        for (int i=0; i<this.arrivalCapacities.length; i++)
        {
            double arr_cap = this.arrivalCapacities[i];
            double dep_cap = this.departureCapacities[i];
            
            double diff = dep_cap - arr_cap;
            
            if (diff == 0)
                return this.arrivalCapacities[i];
            
            if (diff > 0) // arrival capacity has become less than departure capacity
            {
                sign_change_index = i;
                break;
            }
        }
        
        // pareto curve is not built correctly, just return the max
        if (sign_change_index <= 0)
            return getMaximumArrivalCapacity();
        
        // calculate the balanced arrival/departure capacity
        int n1 = sign_change_index - 1;
        int n2 = sign_change_index;
        
        double a1 = this.arrivalCapacities[n1];
        double d1 = this.departureCapacities[n1];
        
        double a2 = this.arrivalCapacities[n2];
        double d2 = this.departureCapacities[n2];
        
        double delta_a = a1 - a2;
        if (delta_a == 0) return a1;
        
        double delta_d = d1 - d2;
        if (delta_d == 0) return d1;
        
        double a_balanced = (d1 - a1 * delta_d / delta_a)/(1.0 - delta_d / delta_a);
        
        return a_balanced;
    }
    
    @Override
    public String toString()
    {
        StringBuilder output = new StringBuilder();
        
        output.append("paretoCurveName    = " + this.paretoCurveName + "\n");
        
        output.append("arrivalCapacities  =");
        if (this.arrivalCapacities != null)
        {
            for (int i=0; i<this.arrivalCapacities.length; i++)
                output.append(" " + this.arrivalCapacities[i]);
        }
        output.append("\n");
        
        output.append("departureCapacities  =");
        if (this.departureCapacities != null)
        {
            for (int i=0; i<this.departureCapacities.length; i++)
                output.append(" " + this.departureCapacities[i]);
        }
        output.append("\n");
        
        return output.toString();
    }
    
    public static final int DEP_IDX = 0;
    public static final int ARR_IDX = 1;
    
    // TODO Delete this method
    public Collection<double[]> getCurveValues()
    {
    	List<double[]> retval = new ArrayList<double[]>();
    	
    	for (int i = 0; i < this.departureCapacities.length; i++)
    	{
    		retval.add(new double[] { this.departureCapacities[i], this.arrivalCapacities[i] });
    	}
    	
    	return retval;
    }
    

    @Override
	public void readItem(BufferedReader reader) throws IOException {
		String[] tokens = reader.readLine().split(",");
		
    	this.airportName = tokens[0];
		this.paretoCurveName = tokens[1];
		
		if (tokens.length < 4) {
			this.arrivalCapacities = new double[0];
			this.departureCapacities = new double[0];
			return;
		}
		
		// This is not as complicated as it initially may appear. We simply want to read in the points from a number
		// stream that includes both arrival/departure dimensions, and write the points out onto two separate single
		// dimension number streams - one for deps, the other for arrivals.
		
		// The complicating factor is that the pareto curve must include one point where the departure capacity is zero
		// and another point where arrival capacity is 0. If one or both of those points are missing, than we need to
		// add those points. The coordinates for the missing points are determined by extending the curve in a straight
		// line horizontally (for departures) or vertically (for arrivals). So, for example, if the pareto curve starts
		// at point (10, 2) (10 arrivals, 2 departures), than the point (10, 0) must be inserted in the curve before the
		// point (10, 2).
		double firstArrival = Double.parseDouble(tokens[2]);
		double firstDeparture = Double.parseDouble(tokens[3]);
		
		if (tokens.length < 6) {
			// single point curve, is this legal or viable? 
			this.arrivalCapacities = new double[] { firstArrival };
			this.departureCapacities = new double[] { firstDeparture};
			return;
		}
		
		double lastArrival = Double.parseDouble(tokens[tokens.length - 2]);
		double lastDeparture = Double.parseDouble(tokens[tokens.length - 1]);
		
		boolean haveZeroCapacityDepartures = firstDeparture == 0 || lastDeparture == 0;
		boolean haveZeroCapacityArrivals = firstArrival == 0 || lastArrival == 0;
				
		int numOfCurvePoints = tokens.length / 2 -1;
		if (! haveZeroCapacityDepartures) numOfCurvePoints++; // need to insert at zero departure capacity 
		if (! haveZeroCapacityArrivals) numOfCurvePoints++; // need to insert at zero arrival capacity
		
		double[] arrivals = new double[numOfCurvePoints];
		double[] departures = new double[numOfCurvePoints];
		
		boolean clockwise = (lastDeparture - firstDeparture) >= 0;
		
		int destinationIndex = 0;
		
		if (clockwise) {
			if (! haveZeroCapacityDepartures) {
				arrivals[destinationIndex] = firstArrival;
				departures[destinationIndex] = 0;
				destinationIndex++;
			} 
		} else {
			if (! haveZeroCapacityArrivals) {
				arrivals[destinationIndex] = 0;
				departures[destinationIndex] = firstDeparture;
				destinationIndex++;
			}
		}
		
		for (int sourceIndex = 2; sourceIndex < tokens.length - 1; sourceIndex += 2) {
			arrivals[destinationIndex] = Double.parseDouble(tokens[sourceIndex]);
			departures[destinationIndex] = Double.parseDouble(tokens[sourceIndex + 1]);
			destinationIndex++;
		}

		if (clockwise) {
			if (! haveZeroCapacityArrivals) {
				arrivals[destinationIndex] = 0;
				departures[destinationIndex] = lastDeparture;
				destinationIndex++;
			}
			 
		} else {
			if (! haveZeroCapacityDepartures) {
				arrivals[destinationIndex] = lastArrival;
				departures[destinationIndex] = 0;
				destinationIndex++;
			}
		}
		
		this.arrivalCapacities = arrivals;
		this.departureCapacities = departures;
	}

	@Override
	public void writeItem(PrintWriter writer) throws IOException {
		writer.println(this.toString());
	}

	@Override
	public long readHeader(BufferedReader reader) throws IOException {
		reader.readLine();     // header
		return -1;
	}

	@Override
	public void writeHeader(PrintWriter arg0, long arg1) throws IOException {
		throw new UnsupportedOperationException();
	}

    @Override
    public int compareTo(AirportParetoCurve o)
    {
        if (this.paretoCurveName.contentEquals(o.paretoCurveName)) {
            return this.airportName.compareTo(o.airportName);
        } else {
            return this.paretoCurveName.compareTo(o.paretoCurveName);
        }
    }
}
