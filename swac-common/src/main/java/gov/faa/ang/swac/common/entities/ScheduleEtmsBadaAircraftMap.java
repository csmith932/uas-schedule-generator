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
public class ScheduleEtmsBadaAircraftMap extends HashMap<String, String> implements TextSerializable {
   
	/**
	 * 
	 */
	private static final long serialVersionUID = -5802307578306779306L;

	@Override
	public void readItem(BufferedReader reader) throws IOException 
	{
		String line = "";

		while ( (line = reader.readLine()) != null )
		{
			if (line.startsWith("#")) {
				continue;
			}
			String fields[] = line.split(",");

			String etms = fields[0];
			String bada = fields[1];

			this.put(etms, bada);
		}

	}

	@Override
	public void writeItem(PrintWriter writer) throws IOException {
		writer.println(this.toString());
	}

}
