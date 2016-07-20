/**
 * Copyright "TBD", Metron Aviation & CSSI.  All rights reserved.
 * 
 * This computer Software was developed with the sponsorship of the U.S. Government
 * under Contract No. DTFAWA-10-D-00033, which has a copyright license in accordance with AMS 3.5-13.(c)(1).
 */

package gov.faa.ang.swac.datalayer.storage.fileio;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * Provides a set of static helper methods for reading off standard header formats
 * @author csmith
 *
 */
public class HeaderUtils
{
	/**
	 * Reads off a header on the assumption that all lines beginning with '#' at the beginning of the
	 * file header comment lines, with the first record beginning immediately after.
	 * NOTE: '#' comments interspersed with data must be handled by the TextSerializable.readItem implementation
	 * @throws IOException 
	 */
	public static void readHeaderHashComment(BufferedReader reader) throws IOException
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
	}
}
