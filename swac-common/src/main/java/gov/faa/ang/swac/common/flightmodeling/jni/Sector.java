/**
 * Copyright "TBD", Metron Aviation & CSSI.  All rights reserved.
 * 
 * This computer Software was developed with the sponsorship of the U.S. Government
 * under Contract No. DTFAWA-10-D-00033, which has a copyright license in accordance with AMS 3.5-13.(c)(1).
 */

package gov.faa.ang.swac.common.flightmodeling.jni;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;

import gov.faa.ang.swac.common.utilities.ParseFormatUtils;
import gov.faa.ang.swac.datalayer.storage.fileio.TextSerializable;
import gov.faa.ang.swac.datalayer.storage.fileio.WithHeader;


public class Sector implements TextSerializable, WithHeader, Comparable<Sector>, Serializable
{
	private static final long serialVersionUID = 396857033877898776L;

	public static final double infiniteCapacity = 999999.;
	
    private String sectorName;
    private double maxIAC;

    public Sector() { }
    
    public Sector(String sectorName) { 
    	this.sectorName = sectorName;
    }
    
	public String getName()
    {
        return this.sectorName;
    }

    public double getMaxIAC()
    {
        return this.maxIAC;
    }

    public boolean isCapacitated() { 
    	return maxIAC != infiniteCapacity;
    }
    
    public void setInfiniteMaxIAC() {
    	this.maxIAC = infiniteCapacity;
    }

    @Override
	public void readItem(BufferedReader reader) throws IOException {
		String line = reader.readLine();
	    String[] fields = line.split(",");
	    
        this.sectorName = fields[0];
        this.maxIAC = ParseFormatUtils.parseDoubleOrInfinity(fields[1], infiniteCapacity, infiniteCapacity, "inf", true);
	}
    
	@Override
	public void writeItem(PrintWriter writer) throws IOException
	{
		writer.println(this.toString());
	}

	@Override
	public long readHeader(BufferedReader reader) throws IOException {
		reader.readLine();
        return -1;
	}

	@Override
	public void writeHeader(PrintWriter writer, long arg1) throws IOException {
		writer.println("WARNING: ouput for reporting purposes only - not suitable for parsing");
	}

	@Override
	public int compareTo(Sector o) {
		return this.sectorName.compareTo(o.sectorName);
	}
	
	@Override
	public String toString() {
		return "Sector [sectorName=" + sectorName + ", maxIAC=" + maxIAC + "]"; 
	}
}
