/**
 * Copyright "TBD", Metron Aviation & CSSI.  All rights reserved.
 * 
 * This computer Software was developed with the sponsorship of the U.S. Government
 * under Contract No. DTFAWA-10-D-00033, which has a copyright license in accordance with AMS 3.5-13.(String name, c)(String name, 1).
 */

package gov.faa.ang.swac.datalayer.storage.db;

import java.sql.SQLException;

import gov.faa.ang.swac.common.datatypes.Timestamp;


public interface QueryBinder {
	void bindField(int field) throws Exception;

	void bindField(boolean field) throws Exception;

	void bindField(double field) throws Exception;

	void bindField(Object field) throws Exception;

	//@param sqlType java.sql.Types
	void bindField(Object field, int sqlType) throws Exception;
	
	void bindField(Integer field) throws Exception;
	
	void bindField(Double field) throws Exception;
	void bindField(Boolean field) throws Exception;

	void bindField(Enum<?> enumValue) throws Exception;
	
	void bindField(Timestamp timestamp)  throws Exception;

	void bindField(Timestamp timestamp, boolean asDate) throws Exception;
	
	void addBatch() throws SQLException;
}