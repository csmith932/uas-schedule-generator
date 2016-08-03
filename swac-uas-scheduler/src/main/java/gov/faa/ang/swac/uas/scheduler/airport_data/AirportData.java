package gov.faa.ang.swac.uas.scheduler.airport_data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import gov.faa.ang.swac.datalayer.storage.fileio.TextSerializable;

/**
 * General Airport Data class.  Matches data from MERGED_AIRPORT_DATA table
 * on Metrics database.
 * 
 * @author James Bonn
 * @version 1.0
 */
public class AirportData implements TextSerializable
{
    private String faaCode;
    private String icaoCode;
    private double latitude;
    private double longitude;
    private int elevation;
    private int countryCode;
    private String country;
    private double utcDifference;  // from UTC to local
    private String center;
    
    /**
     * Default constructor
     */
    public AirportData()
    {
        faaCode = null;
        icaoCode = null;
        latitude = 0;
        longitude = 0;
        elevation = 0;
        countryCode = 0;
        utcDifference = 0;
        country = null;
        center = null;
    }
    
    /**
     * Constructor allowing all parameters to be set.
     * 
     * @param faaCode
     * @param icaoCode
     * @param latitude
     * @param longitude
     * @param elevation
     * @param countryCode
     * @param country
     * @param utcDifference
     * @param center
     */
    public AirportData(String faaCode, String icaoCode, double latitude,
        double longitude, int elevation, int countryCode, String country,
        double utcDifference, String center)
    {
        this.faaCode = (faaCode == null || faaCode.isEmpty() ? null : faaCode);
        this.icaoCode = (icaoCode == null || icaoCode.isEmpty() ? null : icaoCode);
        this.latitude = latitude;
        this.longitude = longitude;
        this.elevation = elevation;
        this.countryCode = countryCode;
        this.country = country;
        this.utcDifference = utcDifference;
        this.center = center;
    }
    
    /**
     * Constructor that copies the input Airport
     * 
     * @param aprt
     */
    public AirportData(AirportData aprt)
    {
        this(aprt.getFaaCode(), aprt.getIcaoCode(), aprt.getLatitude(),
            aprt.getLongitude(), aprt.getElevation(), aprt.getCountryCode(),
            aprt.getCountry(), aprt.getUtcDifference(), aprt.getCenter());
    }

	/**
	 * Set the FAA Code for the airport (most likely a 3 letter code)
	 * @param faaCode
	 */
	public void setFaaCode(String faaCode)
    {
        this.faaCode = faaCode;
    }

	/**
	 * Get the FAA Code for the airport
	 * @return FAA Code
	 */
	public String getFaaCode()
    {
        return faaCode;
    }

	/**
	 * Set the ICAO Code for the Airport (4 letter code)
	 * 
	 * @param icaoCode
	 */
	public void setIcaoCode(String icaoCode)
    {
        this.icaoCode = icaoCode;
    }

	/**
	 * Get the ICAO Code for the airport
	 * @return ICAO Code
	 */
	public String getIcaoCode()
    {
        return icaoCode;
    }
    
	/**
	 * Get the FAA Code for the airport.  If the FAA Code is null, get 
	 * the ICAO Code.
	 * 
	 * @return FAA Code, or ICAO Code if FAA Code is null
	 */
	public String getFaaIcaoCode()
    {
        if (faaCode == null)
        {
            return icaoCode;
        }
        return faaCode;
    }
    
	/**
	 * Get the ICAO Code for the airport.  If the ICAO Code is null, get
	 * the FAA Code.
	 * @return ICAO code, or FAA Code if ICAO Code is null
	 */
	public String getIcaoFaaCode()
    {
        if (icaoCode == null)
        {
            return faaCode;
        }
        return icaoCode;
    }
    
	/**
	 * If US Airport, see {@link #getFaaIcaoCode()}.  If Foreign, see 
	 * {@link #getIcaoFaaCode()}
	 * @return if US, {@link #getFaaIcaoCode()}, else {@link #getIcaoFaaCode()}
	 */
	public String getMostLikelyCode()
    {
        if (this.isDomestic())
        {
            return getFaaIcaoCode();
        }
        
        return getIcaoFaaCode();
    }

	/**
	 * Set the latitude of the airport (in degrees)
	 * @param latitude
	 */
	public void setLatitude(double latitude)
    {
        this.latitude = latitude;
    }

	/**
	 * Get the latitude of the airport (in degrees)
	 * @return latitude
	 */
	public double getLatitude()
    {
        return latitude;
    }

	/**
	 * Set the longitude of the airport (in degrees)
	 * @param longitude
	 */
	public void setLongitude(double longitude)
    {
        this.longitude = longitude;
    }

	/**
	 * Get the longitude of the airport (in degrees)
	 * @return longitude
	 */
	public double getLongitude()
    {
        return longitude;
    }

	/**
	 * Set the elevation of the airport (in feet)
	 * @param elevation
	 */
	public void setElevation(int elevation)
    {
        this.elevation = elevation;
    }

	/**
	 * Get the elevation of the airport (in feet)
	 * @return elevation
	 */
	public int getElevation()
    {
        return elevation;
    }

	/**
	 * Set the country code of the airport (US == 1)
	 * @param countryCode
	 */
	public void setCountryCode(int countryCode)
    {
        this.countryCode = countryCode;
    }

	/**
	 * Get the country code of the airport (US == 1)
	 * @return country code
	 */
	public int getCountryCode()
    {
        return countryCode;
    }

    public boolean isDomestic()
    {
        return (countryCode == 1);
    }
    
    public boolean isForeign()
    {
        return (countryCode != 1);
    }
    
	/**
	 * Set the UTC time difference during standard time 
	 * (<0 is west of prime meridian, thus after UTC).
	 * @param utcDifference
	 */
	public void setUtcDifference(double utcDifference)
    {
        this.utcDifference = utcDifference;
    }

	/**
	 * Get the UTC time difference in hours during standard time 
	 * (<0 is west of prime meridian, thus after UTC).
	 * @return UTC difference
	 */
	public double getUtcDifference()
    {
        return utcDifference;
    }
    
	/**
	 * Returns true if UTC time difference < 0.
	 * @return true if UTC time difference < 0
	 */
	public boolean inWesternHemisphere()
    {
        return (utcDifference < 0);
    }

	/**
	 * Set the ARTCC name the airport lies under
	 * @param center
	 */
	public void setCenter(String center)
    {
        this.center = center;
    }

	/**
	 * Get the ARTCC name the airport lies under
	 * @return ARTCC name
	 */
	public String getCenter()
    {
        return center;
    }

	/**
	 * Set the country name the airport is in
	 * @param country
	 */
	public void setCountry(String country)
    {
        this.country = country;
    }

	/**
	 * Get the country name the airport is in
	 * @return country name
	 */
	public String getCountry()
    {
        return country;
    }

	@Override
	public void readItem(BufferedReader reader) throws IOException
	{
		String currentLine = reader.readLine();
		
		String[] values = currentLine.split(";");
		this.faaCode = values[0].isEmpty() ? null : values[0];
		this.icaoCode = values[1].isEmpty() ? null : values[1];
		this.latitude = Double.parseDouble(values[2]);
		this.longitude = Double.parseDouble(values[3]);
		this.elevation = Integer.parseInt(values[4]);
		this.countryCode = Integer.parseInt(values[5]);
		this.country = values[6];
		this.utcDifference = Double.parseDouble(values[7]);
		
		this.center = null;
		if (values.length >= 9)
		{
			this.center = values[8];
		}
	}

	@Override
	public void writeItem(PrintWriter writer) throws IOException
	{
		writer.println(this.toString());
	}
    
    
}
