package gov.faa.ang.swac.uas.scheduler.forecast.airport_data;

import gov.faa.ang.swac.common.flightmodeling.ScheduleRecord;
import gov.faa.ang.swac.uas.scheduler.airport_data.AirportData;
import gov.faa.ang.swac.uas.scheduler.airport_data.AirportDataPair;
import gov.faa.ang.swac.uas.scheduler.forecast.trip_distribution.ForecastTripDistAirportDataCount;

import java.util.LinkedHashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * An extension of {@link CityPairAirports} that adds in flights and
 * counts needed for the Forecasting process.  It implements the {@link Comparable}
 * interface so that the city pairs can be sorted.  They are sorted by
 * the remainder of the calculated flights after the integer portions have
 * been assigned.
 * 
 * @author James Bonn
 * @version 1.0
 */
public class ForecastAirportDataPair extends AirportDataPair
    implements Comparable<ForecastAirportDataPair>
{
    private ForecastTripDistAirportDataCount countByClass;
    private double projectedFlightCountRaw;
    private double projectedFlightCountFinal;
    private Map<Integer,ScheduleRecord> flights;
    private int numClonesToMake;
    
    /**
     * Default Constructor.
     */
    private ForecastAirportDataPair()
    {
        countByClass = new ForecastTripDistAirportDataCount();
        flights = new LinkedHashMap<Integer,ScheduleRecord>();
    }

    /**
     * Constructor given a departure and an arrival airport.
     * @param origin
     * @param destin
     */
    public ForecastAirportDataPair(AirportData origin,AirportData destin)
    {
        super(origin,destin);
        countByClass = new ForecastTripDistAirportDataCount();
        flights = new LinkedHashMap<Integer,ScheduleRecord>();
    }

    /**
     * Constructor given departure and arrival airports and flight count data.
     * @param deptAprt
     * @param arrAprt
     * @param flightCount
     * @param projectedFlightCountRaw
     * @param projectedFlightCountFinal
     */
    public ForecastAirportDataPair(AirportData deptAprt, AirportData arrAprt,
        double flightCount, double projectedFlightCountRaw, double projectedFlightCountFinal)
    {
        super(deptAprt, arrAprt);
        this.projectedFlightCountRaw = projectedFlightCountRaw;
        this.projectedFlightCountFinal = projectedFlightCountFinal;
        
        countByClass = new ForecastTripDistAirportDataCount();
        countByClass.setNumOther(flightCount);
    }

    /**
     * @return the total number of flights in the baseline data
     */
    public double getFlightCount()
    {
        return countByClass.getTotal();
    }
    
    private void incrementFlightCount(char etmsUserClass)
    {
        countByClass.addFlight(etmsUserClass);
    }

    /**
     * Set the projected number of flights.
     * @param projectedFlightCountRaw
     */
    public void setProjectedFlightCountRaw(double projectedFlightCountRaw)
    {
        this.projectedFlightCountRaw = projectedFlightCountRaw;
    }

    /**
     * @return the projected number of flights
     */
    public double getProjectedFlightCountRaw()
    {
        return projectedFlightCountRaw;
    }

    /**
     * Set the final number of projected flights.  This is actually most likely
     * an integer and set after the Integerization process.
     * @param projectedFlightCountFinal
     */
    public void setProjectedFlightCountFinal(double projectedFlightCountFinal)
    {
        this.projectedFlightCountFinal = projectedFlightCountFinal;
    }

    /**
     *
     * @return the final projected number of flights
     */
    public double getProjectedFlightCountFinal()
    {
        return projectedFlightCountFinal;
    }

    /**
     * Increment the final projected flight count.
     */
    public void incrementProjectedFlightCountFinal()
    {
        projectedFlightCountFinal++;
    }

    /**
     * Set the initial flight count broken out by class.
     * @param countByClass
     */
    public void setCountByClass(ForecastTripDistAirportDataCount countByClass)
    {
        this.countByClass = countByClass;
    }

    /**
     * @return the initial flight count broken out by class
     */
    public ForecastTripDistAirportDataCount getCountByClass()
    {
        return countByClass;
    }

    public void setNumClonesToMake(int numClonesToMake) 
    {
		this.numClonesToMake = numClonesToMake;
	}
    
	public int getNumClonesToMake() 
    {
		return numClonesToMake;
	}

	public void decrementNumClonesToMake() 
    {
		--this.numClonesToMake;
	}
    
    /**
     * Add a {@link ScheduleRecord} to the city pair.
     * @param schedRec
     */
    public void addFlight(ScheduleRecord schedRec)
    {
        // Assumes that the schedule record IDs are unique 
        
        if (!flights.containsKey(schedRec.idNum))
        {
            flights.put(schedRec.idNum,schedRec);
            
            // Get the first character of the (ETMS) user class
            char etmsUserClass = ' ';
            if(schedRec.userClass != null)
            {
                etmsUserClass = schedRec.userClass.charAt(0);
            }
            
            incrementFlightCount(etmsUserClass);
        }
    }

    /**
     * @return a list of all of the flights on the city pair
     */
    public List<ScheduleRecord> getFlights()
    {
        ArrayList<ScheduleRecord> flightsList = 
            new ArrayList<ScheduleRecord>();
        flightsList.addAll(flights.values());
        return flightsList;
    }

    /**
     * @return the initial flight count
     */
    public double getInitialFlightCount()
    {
        return flights.size();
    }

    /**
     * @return the difference between the raw and final projected
     * flight counts
     */
    public double getRemainderProjectedRaw()
    {
        return (projectedFlightCountRaw - projectedFlightCountFinal);
    }

    /**
     * Used for sorting.  The order is descending by the output of
     * {@link #getRemainderProjectedRaw}.
     * @param o
     * @return see {@link Comparable}
     */
    @Override
    public int compareTo(ForecastAirportDataPair o)
    {
        // want sorting by remainder from large to small
        Double remainderThis = getRemainderProjectedRaw();
        Double remainderThat = o.getRemainderProjectedRaw();
        
        return -remainderThis.compareTo(remainderThat);
    }
    
    public void clear() 
    {
        countByClass = null;
        flights.clear();
    } 
    
}
