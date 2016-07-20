/**
 * Copyright "TBD", Metron Aviation & CSSI.  All rights reserved.
 * 
 * This computer Software was developed with the sponsorship of the U.S. Government
 * under Contract No. DTFAWA-10-D-00033, which has a copyright license in accordance with AMS 3.5-13.(c)(1).
 */

package gov.faa.ang.swac.datalayer.storage.db;



public interface FieldTypeConverter {
	
	String getIntType(); 
	
	String getIntType(long minValue, long maxValue);
	
	String getVarCharType();
	
	String getVarCharType(int size);

	String getDoubleType();
	
	String getDoubleType(int precision, int scale);

	String getBooleanType(String fieldName);
	
	//Implementation Hint: use class.getEnumConstants to get at the enums
	<T extends Enum<T>> String getEnumType(Class<T> anEnum);

	String getDateType(); 
	
	String getDateTimeType();

}