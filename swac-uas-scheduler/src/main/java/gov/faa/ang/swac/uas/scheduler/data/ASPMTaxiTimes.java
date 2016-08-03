package gov.faa.ang.swac.uas.scheduler.data;

import gov.faa.ang.swac.datalayer.storage.fileio.TextSerializable;
import gov.faa.ang.swac.datalayer.storage.fileio.WithHeader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;

/**
 * A Class that loads in and stores the taxi times from the export of the table
 * ASPM_NOMINAL_TAXI_TIMES on the Metrics database.
 * 
 * @author James Bonn, Casey Smith
 * @version 2.0
 */
public class ASPMTaxiTimes
{
    private final HashMap<String,HashMap<String,double[]>> airports = new HashMap<String,HashMap<String,double[]>>(4000);
    private static final String DEFAULT_CARRIER = "ALL_FLTS";
    private static final double DEFAULT_TAXI_OUT = 11.1;
    private static final double DEFAULT_TAXI_IN = 4.9;
    
    public ASPMTaxiTimes(List<ASPMTaxiTimesRecord> recordSet, String yyyymm)
    {
    	HashMap<String,double[]> carrierHash = null;
        
    	for (ASPMTaxiTimesRecord rec : recordSet)
    	{
    		if (rec.yyyymm.equals(yyyymm))
    		{
    			if(this.airports.containsKey(rec.locid))
                {
                    carrierHash = this.airports.get(rec.locid);
                }
                else
                {
                    carrierHash = new HashMap<String,double[]>();
                    this.airports.put(rec.locid, carrierHash);
                }
                carrierHash.put(rec.carrier, rec.taxiTimes);
    		}
    	}
    }

    /**
     * Given an airport and a carrier, find the taxi out time.
     * @param locid
     * @param carrier
     * @return the taxi out time in minutes
     */
    public double getTaxiOutTime(String locid, String carrier)
    {
        return getTaxiTime(locid, carrier, "OUT");
    }

    /**
     * Given an airport and a carrier, find the taxi in time.
     * @param locid
     * @param carrier
     * @return the taxi in time in minutes
     */
    public double getTaxiInTime(String locid, String carrier)
    {
        return getTaxiTime(locid, carrier, "IN");
    }
    
    private double getTaxiTime(String locid, String carrier, String outIn)
    {
        double taxiTime = DEFAULT_TAXI_OUT;
        if(outIn.equals("IN"))
        {
            taxiTime = DEFAULT_TAXI_IN;
        }
        // done setting defaults
        carrier = carrier.substring(0,Math.min(3, carrier.length()));
        if(airports.containsKey(locid))
        {
        	HashMap<String,double[]> carrierHash = airports.get(locid);
            double [] taxiTimes = null;
            if(carrierHash.containsKey(carrier))
            {
                taxiTimes = (double []) carrierHash.get(carrier);
            }
            else
            {
                taxiTimes = (double []) carrierHash.get(DEFAULT_CARRIER);
            }
            if(taxiTimes != null)
            {
                taxiTime = taxiTimes[0];
                if(outIn.equals("IN"))
                {
                    taxiTime = taxiTimes[1];
                }
            }
        }
        return taxiTime;
    }

	public static class ASPMTaxiTimesRecord implements TextSerializable, WithHeader
	{
		private String yyyymm;
		private String locid;
		private String carrier;
		private double[] taxiTimes = new double[2];
		
		@Override
		public void readItem(BufferedReader reader) throws IOException
		{
			String currentLine = reader.readLine();
			String[] values = currentLine.split(",");

			yyyymm = values[0];
			locid = values[1];
            carrier = values[2];
            taxiTimes[0] = Double.parseDouble(values[3]);
            taxiTimes[1] = Double.parseDouble(values[4]);
		}

		@Override
		public void writeItem(PrintWriter writer) throws IOException
		{
			writer.println(this.toString());
		}
		
		@Override
		public long readHeader(BufferedReader reader) throws IOException
		{
			String currentLine = null;
			do
			{
				// Read ahead one line (buffering at least 1000 characters) to check for header tag (#)
				reader.mark(1000);
				currentLine = reader.readLine();
			}
			while (currentLine != null && currentLine.startsWith("#"));
			
			// Loop terminates when look-ahead line is not commented with a #: rewind to the beginning of the line
			// 
			reader.reset();
			
			return -1;
		}

		@Override
		public void writeHeader(PrintWriter writer, long numRecords) throws IOException
		{
			throw new UnsupportedOperationException();
		}
	}
}
