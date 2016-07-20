/**
 * Copyright "TBD", Metron Aviation & CSSI.  All rights reserved.
 * 
 * This computer Software was developed with the sponsorship of the U.S. Government
 * under Contract No. DTFAWA-10-D-00033, which has a copyright license in accordance with AMS 3.5-13.(c)(1).
 */

package gov.faa.ang.swac.datalayer.storage.fileio;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * This interface provides a fallback for structured data types that cannot be plausibly (binary) or 
 * conveniently (xml) translated to/from text. TextSerializable provides a more convenient interface
 * using PrintWriter and BufferegReader for easy translation of text, and is generally preferable to implementing
 * this interface with manually created reader/writers.
 * 
 * @author csmith
 *
 */
public interface StreamSerializable
{
	/**
	 * Implementing classes should use this method to reconstitute their internal state from the contents
	 * of the stream without closing it. Exceptions should be allowed to propagate, with parsing errors
	 * repackaged as IOExceptions. The stream is only guaranteed to be in the ready state for the first 
	 * read operation: additional checks should be made if more reads are necessary for a single item. 
	 * 
	 * @param in
	 * @throws IOException
	 */
	public void readItem(InputStream in) throws IOException;
	
	/**
	 * Implementing classes should use this method to dump their internal state to stream in a format that is reversible
	 * in readItem. The writer should not be closed, and exceptions should be allowed to propagate.
	 * 
	 * @param writer
	 * @throws IOException
	 */public void writeItem(OutputStream out) throws IOException;
}
