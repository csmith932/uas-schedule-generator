/**
 * Copyright "TBD", Metron Aviation & CSSI. All rights reserved.
 *
 * This computer Software was developed with the sponsorship of the U.S.
 * Government under Contract No. DTFAWA-10-D-00033, which has a copyright
 * license in accordance with AMS 3.5-13.(c)(1).
 */
package gov.faa.ang.swac.common.databasescript;

import java.io.Serializable;

/**
 *
 * @author ssmitz
 */
public abstract class SqlScript implements Serializable {
    /*
     * All script files are read from the file system when the DB is initialized.
     * extend this class with another unique empty class to allow for parameterization
     * of the script files.
     */
}
