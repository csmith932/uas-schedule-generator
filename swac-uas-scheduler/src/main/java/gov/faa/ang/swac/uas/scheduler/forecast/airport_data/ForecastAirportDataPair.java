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
    private double projectedFlightCountFinal;
    private Map<Integer,ScheduleRecord> flights;
    
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
     * Set the final number of projected flights.  This is actually most likely
     * an integer and set after the Integerization process.
     * @param projectedFlightCountFinal
     */
    // TODO: CSS This needs to be set as the sum of operations from projectedTotalDep and/or projectedTotalArr, which would have been done implicity by Fratar if the coefficients and ops were already balanced
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
     * @return the initial flight count broken out by class
     */
    public ForecastTripDistAirportDataCount getCountByClass()
    {
        return countByClass;
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
     * Used for sorting.  The order is descending by the output of
     * {@link #getRemainderProjectedRaw}.
     * @param o
     * @return see {@link Comparable}
     */
    @Override
    public int compareTo(ForecastAirportDataPair o)
    {
        return Double.compare(this.projectedFlightCountFinal, o.projectedFlightCountFinal);
    }
    
    public void clear() 
    {
        countByClass = null;
        flights.clear();
    } 
    
}
