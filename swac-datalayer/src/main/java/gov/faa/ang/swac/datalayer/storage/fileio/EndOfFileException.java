/**
 * Copyright "TBD", Metron Aviation & CSSI.  All rights reserved.
 * 
 * This computer Software was developed with the sponsorship of the U.S. Government
 * under Contract No. DTFAWA-10-D-00033, which has a copyright license in accordance with AMS 3.5-13.(c)(1).
 */

package gov.faa.ang.swac.datalayer.storage.fileio;

import java.io.IOException;

/**
 * Provides a means for a TextSerializable to abort parsing if the remaining contents of the reader cannot be properly interpreted as a record
 * of the appropriate type - usually trailing white space. This exception is to be interpreted as successful termination of file parsing, with
 * the last uninitialized record to be discarded. Use other exception types to indicate parsing errors.
 * 
 * @author csmith
 *
 */
public class EndOfFileException extends IOException
{
	private static final long serialVersionUID = 6240568593870461629L;
}
