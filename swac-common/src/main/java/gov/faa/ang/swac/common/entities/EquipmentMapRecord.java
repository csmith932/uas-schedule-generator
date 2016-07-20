/**
 * Copyright "TBD", Metron Aviation & CSSI.  All rights reserved.
 * 
 * This computer Software was developed with the sponsorship of the U.S. Government
 * under Contract No. DTFAWA-10-D-00033, which has a copyright license in accordance with AMS 3.5-13.(c)(1).
 */

package gov.faa.ang.swac.common.entities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import gov.faa.ang.swac.datalayer.storage.fileio.TextSerializable;
import gov.faa.ang.swac.datalayer.storage.fileio.WithHeader;

public class EquipmentMapRecord implements TextSerializable, WithHeader
{
	public String aircraft_type;
    public Integer turnaround_cat;
    public Integer pushback_cat;
    public Integer taxi_out_cat;
    public Integer taxi_in_cat;
    public Integer rerouteClearanceCat;
	public Integer rampCat;

	@Override
	public void readItem(BufferedReader reader) throws IOException {
	    String line = reader.readLine();
        
        String fields[] = line.split(",");
        
        aircraft_type = fields[0].trim();
        turnaround_cat = Integer.parseInt(fields[1].trim());
        pushback_cat = Integer.parseInt(fields[2].trim());
        taxi_out_cat = Integer.parseInt(fields[3].trim());
        taxi_in_cat = Integer.parseInt(fields[4].trim());
        rerouteClearanceCat = Integer.parseInt(fields[5].trim());
        rampCat = Integer.parseInt(fields[6].trim());
	}

	@Override
	public void writeItem(PrintWriter writer) throws IOException {
		writer.println(this.toString());
	}

	@Override
	public long readHeader(BufferedReader reader) throws IOException {
		reader.readLine();      // header
		
		return -1;
	}

	@Override
	public void writeHeader(PrintWriter writer, long numRecords)
			throws IOException {
		throw new UnsupportedOperationException();
	}
}
