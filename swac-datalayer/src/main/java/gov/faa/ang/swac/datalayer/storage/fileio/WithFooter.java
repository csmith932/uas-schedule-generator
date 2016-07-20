/**
 * Copyright "TBD", Metron Aviation & CSSI.  All rights reserved.
 * 
 * This computer Software was developed with the sponsorship of the U.S. Government
 * under Contract No. DTFAWA-10-D-00033, which has a copyright license in accordance with AMS 3.5-13.(c)(1).
 */

package gov.faa.ang.swac.datalayer.storage.fileio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * WithFooter indicates that a particular data type is associated with a text file format that has a footer. The footer text
 * may or may not contain useful information. If it does, then it should be stored statically: FileMarshaller reflects a local 
 * empty instance so non-static information would be missing.
 * NOTE: WithHeader implementation MUST return a valid number of records, or else FileMarshaller will attempt to read records
 * to the end of the stream without stopping to read the footer. This will likely cause parsing errors in readItem.
 * @author csmith
 *
 */
public interface WithFooter extends WithHeader
{
	/**
	 * Invoked by FileMarshaller after all records have been read. If readFooter fails to advance the reader to the end
	 * of stream, then FileMarshaller will throw an exception for invalid file size. Implementers should not open/close
	 * the reader and should not handle their own exceptions. Any useful data should be stored statically, since the
	 * reflected instance on which readFooter is transient.
	 * @param reader An open BufferedReader representing a stream of text
	 * @throws IOException
	 */
	public void readFooter(BufferedReader reader) throws IOException;
	
	/**
	 * Invoked by FileMarshaller after all records have been written. If useful information is to be written to the stream
	 * then it should exist statically.
	 * @param writer
	 * @throws IOException
	 */
	public void writeFooter(PrintWriter writer) throws IOException;
}
