/**
 * Copyright "TBD", Metron Aviation & CSSI.  All rights reserved.
 * 
 * This computer Software was developed with the sponsorship of the U.S. Government
 * under Contract No. DTFAWA-10-D-00033, which has a copyright license in accordance with AMS 3.5-13.(c)(1).
 */

package gov.faa.ang.swac.common.entities;

import gov.faa.ang.swac.datalayer.storage.fileio.TextSerializable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

public class AirportCenterMap extends HashMap<String, String> implements TextSerializable 
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void readItem(BufferedReader reader) throws IOException {
		reader.readLine(); // header
	
		while (reader.ready()) {
				String line = reader.readLine();
	
				String fields[] = line.split(",");
	
				String faa_airport_code = fields[0].trim();
				String artcc_code = fields[2].trim();
	
				this.put(faa_airport_code, artcc_code);
		}
	}

	@Override
	public void writeItem(PrintWriter writer) throws IOException {
		writer.println(this.toString());
	}
}
