/**
 * Copyright "TBD", Metron Aviation & CSSI.  All rights reserved.
 * 
 * This computer Software was developed with the sponsorship of the U.S. Government
 * under Contract No. DTFAWA-10-D-00033, which has a copyright license in accordance with AMS 3.5-13.(c)(1).
 */

package gov.faa.ang.swac.common.entities;

import gov.faa.ang.swac.datalayer.storage.fileio.TextSerializable;
import gov.faa.ang.swac.datalayer.storage.fileio.WithHeader;

/**
 * A do nothing inheritance class solely created to give AirportParetoCurve a separate name so that it can be referred to
 * separately in scenarioImports and defaultImports.
 */
public class AirportParetoCurveAlt extends AirportParetoCurve  implements TextSerializable, WithHeader, Comparable<AirportParetoCurve>, ParetoCurve
{
}