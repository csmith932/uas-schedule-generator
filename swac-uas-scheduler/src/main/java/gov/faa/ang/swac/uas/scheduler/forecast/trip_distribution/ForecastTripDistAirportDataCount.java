package gov.faa.ang.swac.uas.scheduler.forecast.trip_distribution;

/**
 * A Class representing count values for different user class categories
 * necessary for matching up the TFMS to TAF data categories
 * in the forecasting process.  Different user classes are grown at
 * different rates, therefore the different category totals need to be tracked.
 * 
 * @author James Bonn
 * @version 1.0
 */
public class ForecastTripDistAirportDataCount
{
	
    private double numGA;
    private double numMil;
    private double numOther;

    /**
     * Use this to add a GA flight.
     */
    public final static char ETMS_TYPE_GA = 'G';

    /**
     * Use this to add a Military flight.
     */
    public final static char ETMS_TYPE_MIL = 'M';

    /**
     * Default Constructor.
     */
    public ForecastTripDistAirportDataCount()
    {
    }

    /**
     * Constructor with given starting values.
     * @param numGA
     * @param numMil
     * @param numOther
     */
    public ForecastTripDistAirportDataCount(double numGA, double numMil, double numOther)
    {
        this.numGA = numGA;
        this.numMil = numMil;
        this.numOther = numOther;
    }

    public ForecastTripDistAirportDataCount(
			ForecastTripDistAirportDataCount count) {
    	this.numGA = count.numGA;
        this.numMil = count.numMil;
        this.numOther = count.numOther;
	}

	/**
     * Add a flight based on the ETMS user class.
     * @param etmsUserClass
     */
    public void addFlight(char etmsUserClass)
    {
        switch(etmsUserClass)
        {
            case ETMS_TYPE_GA:
                numGA++;
                break;
            case ETMS_TYPE_MIL:
                numMil++;
                break;
            default:
                numOther++;
                break;
        }
    }

    /**
     * Add the input data to the current data values.
     * @param addTo
     */
    public void addAllData(ForecastTripDistAirportDataCount addTo)
    {
        numGA += addTo.getNumGA();
        numMil += addTo.getNumMil();
        numOther += addTo.getNumOther();
    }

    /**
     * Add the input data to the current data values.
     * @param numGA
     * @param numMil
     * @param numOther
     */
    public void addAllData(double numGA, double numMil, double numOther)
    {
        this.numGA += numGA;
        this.numMil += numMil;
        this.numOther += numOther;
    }

    /**
     * Given another {@link ForecastTripDistAirportDataCount}, 
     * remove those flight totals from this (useful when removing citypairs
     * while closing the airport system).
     * @param subFrom
     */
    public void subtractAllData(ForecastTripDistAirportDataCount subFrom)
    {
        numGA -= subFrom.getNumGA();
        numMil -= subFrom.getNumMil();
        numOther -= subFrom.getNumOther();
    }

    /**
     * @return the total number of flights
     */
    public double getTotal()
    {
        return numGA + numMil + numOther;
    }

    /**
     * Set the number of GA flights.
     * @param numGA
     */
    public void setNumGA(double numGA)
    {
        this.numGA = numGA;
    }

    /**
     * @return the number of GA flights
     */
    public double getNumGA()
    {
        return numGA;
    }

    /**
     * Set the number of Military flights.
     * @param numMil
     */
    public void setNumMil(double numMil)
    {
        this.numMil = numMil;
    }

    /**
     * @return the number of Military flights
     */
    public double getNumMil()
    {
        return numMil;
    }

    /**
     * Set the number of non-GA, non-Military flights.
     * @param numOther
     */
    public void setNumOther(double numOther)
    {
        this.numOther = numOther;
    }

    /**
     * @return the number of non-GA, non-Military flights
     */
    public double getNumOther()
    {
        return numOther;
    }

	@Override
	public String toString() {
		return "ForecastTripDistAirportDataCount [numGA=" + numGA + ", numMil="
				+ numMil + ", numOther=" + numOther + "]";
	}
}
