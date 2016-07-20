/**
 * Copyright "TBD", Metron Aviation & CSSI.  All rights reserved.
 * 
 * This computer Software was developed with the sponsorship of the U.S. Government
 * under Contract No. DTFAWA-10-D-00033, which has a copyright license in accordance with AMS 3.5-13.(c)(1).
 */

package gov.faa.ang.swac.datalayer.storage.db;


/**
 * Interface output report records must implement to be uploaded to the database.  
 *  
 * @author cunningham
 */
public interface UploadableRecord  {

	/**
	 * UploadableRecord implementations will describe their fields by calling the appropriate query builder add*Field()
	 * method. Order matters.
	 * 
	 * @param queryBuilder
	 */
	void describeFields(QueryBuilder queryBuilder);
	
	/**
	 * UploadableRecord implementations will bind their field values to the given query binder in the same field order
	 * as performed in describeFields method.
	 * 
	 * @param queryBuilder
	 */
	void bindFields(QueryBinder queryBinder) throws Exception;
	
}