/**
 * Copyright "TBD", Metron Aviation & CSSI.  All rights reserved.
 * 
 * This computer Software was developed with the sponsorship of the U.S. Government
 * under Contract No. DTFAWA-10-D-00033, which has a copyright license in accordance with AMS 3.5-13.(c)(1).
 */

package gov.faa.ang.swac.common.entities;


import gov.faa.ang.swac.datalayer.storage.fileio.HeaderUtils;
import gov.faa.ang.swac.datalayer.storage.fileio.TextSerializable;
import gov.faa.ang.swac.datalayer.storage.fileio.WithHeader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

/**
 * @author anwari
 *
 */
@Deprecated
public class AircraftTypeData extends HashMap<String, Integer> implements TextSerializable, WithHeader
{

    /**
	 * 
	 */
	private static final long serialVersionUID = 6914155735684502143L;
	private static final org.apache.log4j.Logger logger = org.apache.log4j.LogManager.getLogger(AircraftTypeData.class);
    public static final String SEPARATOR = ",";
	
    @Override
	public void readItem(BufferedReader reader) throws IOException {
		 	while (reader.ready()) {
        		String[] fields = reader.readLine().split(SEPARATOR);
        		
        		if (fields.length != 2)
        			continue;
        		
        		String aircraft_type = fields[0].trim();
        		Integer priority = Integer.MAX_VALUE;
        		try
        		{
        			priority = Integer.parseInt(fields[1].trim());
        		}
        		catch (NumberFormatException nfe)
        		{
        			// priority initialized to zero if parsing error encountered
        			logger.warn("AircraftTypeFileReader: Priority not parsed for aircraft type: " + aircraft_type + ", setting priority to 0.", nfe);
        		}
        		this.put(aircraft_type, priority);
		 	}
	}

	@Override
	public void writeItem(PrintWriter writer) throws IOException {
		writer.println(this.toString());
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

}
