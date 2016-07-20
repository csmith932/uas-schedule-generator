/**
 * Copyright "TBD", Metron Aviation & CSSI.  All rights reserved.
 * 
 * This computer Software was developed with the sponsorship of the U.S. Government
 * under Contract No. DTFAWA-10-D-00033, which has a copyright license in accordance with AMS 3.5-13.(String name, c)(String name, 1).
 */

package gov.faa.ang.swac.datalayer.storage.db;



public interface QueryBuilder {
	
	void addIntField(String name); 
	
	void addIntField(String name, long minValue, long maxValue);
	
	void addVarCharField(String name);
	
	void addVarCharField(String name, int size);

	void addDoubleField(String name);
	
	void addDoubleField(String name, int precision, int scale);

	void addBooleanField(String name);
	
	//Implementation Hint: use class.getEnumConstants to get at the enums
	<T extends Enum<T>> void addEnumField(String name, Class<T> anEnum);

	/**
	 * Finalizes the query and returns the String. Caller should not invoke any add*Field methods after calling this
	 * method.
	 * 
	 * @return
	 */
	String toQueryString();

	void addDateField(String name);

	void addDateTimeField(String name);
	
	void addFieldWithUnits(String name, String units);

}