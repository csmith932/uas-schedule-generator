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
 * Implementation of this interface indicates that a text representation of a collection of records contains a single instance
 * of header text. The implementing class is responsible for moving the reader to the starting position of the first record,
 * and optionally returning a fixed record count. A record count is necessary when a footer is present to make termination of
 * a list of records well defined. If a footer is not present, an implementing class may return any negative number, which
 * is interpreted to mean that the list of records is terminated by the end of stream.
 * 
 * @author csmith
 *
 */
public interface WithHeader extends TextSerializable
{
	/**
	 * Reads the header from the beginning of the text stream, advancing the Reader to the first record.
	 * @param reader
	 * @return The number of records in the file specified by the header. If no record count is specified in the header then a negative number should be returned. WithFooter cannot be implemented properly with an unspecified number of records returned by readHeader.
	 * @throws IOException
	 */
	public long readHeader(BufferedReader reader) throws IOException;
	
	/**
	 * Optionally write some header text. This is applied to an anonymous record, so beyond the provided record count only static state 
	 * can reliably be included in the header.
	 * @param writer
	 * @param numRecords
	 * @throws IOException
	 */
	public void writeHeader(PrintWriter writer, long numRecords) throws IOException;
}
