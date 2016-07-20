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

public class AirportDepartureCenterExceptionRecord implements TextSerializable, WithHeader
{
	public static final long default_delay_threshold = Long.MAX_VALUE;
	
	public String airport_code;
	public String center;
	public double default_non_exempt_ratio;
	public long[] delay_threshold;
	public double[] non_exempt_ratio;
	
	@Override
	public void readItem(BufferedReader reader) throws IOException 
	{
		String fields[] = reader.readLine().split(",");

		airport_code = fields[0].trim();
		center = fields[1].trim();

		// define final exemption ratio thresholds
		int final_idx = fields.length - 1;
		int num = (fields.length - 3)/2;
		
		delay_threshold = new long[num];
		non_exempt_ratio = new double[num];
		
		default_non_exempt_ratio = Double.parseDouble(fields[final_idx].trim());
		
		for (int i = 0; i < num; i++) {
			int idx = 2 + 2*i;
			delay_threshold[i] = Long.parseLong(fields[idx].trim());
			non_exempt_ratio[i] = Double.parseDouble(fields[idx + 1].trim());
		}
	}

	@Override
	public void writeItem(PrintWriter writer) throws IOException
	{
		writer.println(this.toString());
	}

	@Override
	public long readHeader(BufferedReader reader) throws IOException 
	{
		reader.readLine();
		return -1;
	}

	@Override
	public void writeHeader(PrintWriter writer, long numRecords) throws IOException 
	{
		throw new UnsupportedOperationException();
	}
}
