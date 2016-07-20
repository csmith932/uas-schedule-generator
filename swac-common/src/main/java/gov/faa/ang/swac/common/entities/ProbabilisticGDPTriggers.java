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
import java.util.ArrayList;
import java.util.List;


public class ProbabilisticGDPTriggers implements TextSerializable 
{
	public List<ProbabilisticGDPTriggerRecord> records = new ArrayList<ProbabilisticGDPTriggerRecord>();
	
	public class ProbabilisticGDPTriggerRecord
	{
		public String airport;
		public int min_queue_size;
		public int max_queue_size;
		public double probability;
	}
	
		@Override
	public void readItem(BufferedReader reader) throws IOException {
		reader.readLine();
		String header = reader.readLine().trim(); // this should be the
													// header
		String fields[] = header.split(",");

		String airport_headers[] = new String[fields.length];

		for (int i = 0; i < fields.length; i++) {
			if (fields[i].trim().startsWith("Queue"))
				continue;

			String airport_code = fields[i].trim();
			airport_headers[i - 1] = airport_code;
			airport_headers[i] = airport_code;
		}

		while (reader.ready()) {
			String line = reader.readLine().trim();

			fields = line.split(",");

			for (int i = 0; i < fields.length; i += 2) {
				String airport = airport_headers[i];

				String queue_bounds_str = fields[i].trim();

				if (queue_bounds_str.length() == 0)
					continue;

				int min_queue_size = 0;
				int max_queue_size = 0;

				if (queue_bounds_str.startsWith(">")) {
					min_queue_size = Integer.parseInt(queue_bounds_str.substring(1));
					max_queue_size = Integer.MAX_VALUE;
				} else {
					String queue_bounds_str_fields[] = queue_bounds_str.split("-");

					min_queue_size = Integer
							.parseInt(queue_bounds_str_fields[0].trim());
					max_queue_size = Integer
							.parseInt(queue_bounds_str_fields[1].trim());
				}

				double probability = Double.parseDouble(fields[i + 1].trim());

				ProbabilisticGDPTriggerRecord rec = new ProbabilisticGDPTriggerRecord();
				rec.airport = airport;
				rec.min_queue_size = min_queue_size;
				rec.max_queue_size = max_queue_size;
				rec.probability = probability;
			}
		}
	}

	@Override
	public void writeItem(PrintWriter writer) throws IOException {
		writer.println(this.toString());
	}
}
