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
 * This interface mimics the Externalizable interface used for custom serialization. However, instead of furnishing
 * ObjectInput and ObjectOutput it uses BufferedReader and PrintWriter. Implementing classes should use these to
 * parse/format text from/to these text-friendly readers and writers. If a text representation of the data is not
 * feasible, the lower level StreamSerializable interface can be used instead.
 * 
 * @author csmith
 *
 */
public interface TextSerializable
{
	/**
	 * Implementing classes should use this method to reconstitute their internal state from the contents
	 * of the reader without closing it. Exceptions should be allowed to propagate, with parsing errors
	 * repackaged as IOExceptions. Multiple lines of text are permitted, but the assumption should be that
	 * the reader is only guaranteed to be in the ready state for the first read operation: additional checks
	 * should be made if more reads are necessary for a single item. The reader should be left at the immediate
	 * end position of the item being parsed. If additional reading is necessary to detect termination of a record
	 * of indeterminate length, the reader should be marked and rewound to the end position of the item 
	 * being parsed.
	 * 
	 * @param reader
	 * @throws IOException
	 */
	public void readItem(BufferedReader reader) throws IOException;
	
	/**
	 * Implementing classes should use this method to dump their internal state to text in a format that is reversible
	 * in readItem and/or human-readable. The writer should not be closed, and exceptions should be allowed to propagate.
	 * 
	 * @param writer
	 * @throws IOException
	 */
	public void writeItem(PrintWriter writer) throws IOException;
}
