package gov.faa.ang.swac.common.passenger;

import gov.faa.ang.swac.datalayer.storage.fileio.HeaderUtils;
import gov.faa.ang.swac.datalayer.storage.fileio.TextSerializable;
import gov.faa.ang.swac.datalayer.storage.fileio.WithHeader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;

/**
 * Count of total passengers for an Origin/Destination (OD) pair. 
 * 
 * An OD pair is defined as the start and end point, respectively, for a specific passenger,
 * regardless of the route (and layovers) needed to get from O to D.
 * 
 * We define the natural ordering as decreasing with passenger count.
 * @author James Bonn
 *
 */
public class OriginDestinationData implements Comparable<OriginDestinationData>, TextSerializable, WithHeader, Serializable
{
	private String originCode;
	private String destinationCode;
	private double count;

	public OriginDestinationData()
	{
	}

	public String getOriginCode()
	{
		return originCode;
	}

	public void setOriginCode(String originCode)
	{
		this.originCode = originCode;
	}

	public String getDestinationCode()
	{
		return destinationCode;
	}

	public void setDestinationCode(String destinationCode)
	{
		this.destinationCode = destinationCode;
	}

	public double getCount()
	{
		return count;
	}

	public void setCount(double count)
	{
		this.count = count;
	}

	/**
	 * Key is "originCode,destinationCode"
	 * @return
	 */
	public String getKey()
	{
		return originCode + "," + destinationCode;
	}

	/**
	 * Ordering is decreasing with count.
	 */
	@Override
	public int compareTo(OriginDestinationData arg0)
	{
		Double count1 = this.count;
		Double count2 = arg0.count;
		
		int compare = -count1.compareTo(count2);
		
		if(compare == 0)
		{
			compare = this.getKey().compareTo(arg0.getKey());
		}
		
		return compare;
	}
	
	public String toString() { 
		return String.format("OriginDestinationData: " + originCode + " to " + destinationCode + " - " + count);
	}

	@Override
	public long readHeader(BufferedReader reader) throws IOException {
		HeaderUtils.readHeaderHashComment(reader);
		return -1;
	}

	@Override
	public void writeHeader(PrintWriter writer, long numRecords)
			throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void readItem(BufferedReader reader) throws IOException 
	{
		String line = reader.readLine();
		String [] lineData = line.split(",");
		
		this.originCode = lineData[0].trim();
		this.destinationCode = lineData[1].trim();
		this.count = Double.valueOf(lineData[2].trim());		
	}

	@Override
	public void writeItem(PrintWriter writer) throws IOException {
		// TODO Auto-generated method stub
		
	}
}

