/**
 * Copyright "TBD", Metron Aviation & CSSI.  All rights reserved.
 * 
 * This computer Software was developed with the sponsorship of the U.S. Government
 * under Contract No. DTFAWA-10-D-00033, which has a copyright license in accordance with AMS 3.5-13.(c)(1).
 */

package gov.faa.ang.swac.common.flightmodeling;

import gov.faa.ang.swac.common.geometry.GCPoint;

/**
 *	Exposes a bounding box for insertion of the resource into a geographical search grid. By convention it is assumed
 *  that the edges of the bounding box are lines of equal latitude and longitude.
 *  <p>
 *	NOTE: This approach to defining a bounding box is ill-suited for some applications, including:<br>
 *  a) Boxes that overlap the international date-line (or other datum used for normalizing longitude),<br>
 *  b) Boxes at or near the poles.<br>
 *  c) Extremely large boxes, especially those that enclose over 180 degrees of longitude.
 *  
 * @author Casey Smith
 */
public interface IGriddableResource extends IResourceInfo
{
	/**
	 * @return South-West-most bounding-box point 
	 */
	public GCPoint southwestPoint();
	
	/**
	 * @return North-East-most bounding-box point
	 */
	public GCPoint northeastPoint();
}