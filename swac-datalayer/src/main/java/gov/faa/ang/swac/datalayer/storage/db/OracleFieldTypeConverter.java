/**
 * Copyright "TBD", Metron Aviation & CSSI.  All rights reserved.
 * 
 * This computer Software was developed with the sponsorship of the U.S. Government
 * under Contract No. DTFAWA-10-D-00033, which has a copyright license in accordance with AMS 3.5-13.(c)(1).
 */

package gov.faa.ang.swac.datalayer.storage.db;



public class OracleFieldTypeConverter implements FieldTypeConverter  {

	@Override
	public String getIntType() {
		return "NUMBER(38,0)";
	}

	@Override
	public String getIntType(long minValue, long maxValue) {
		// from http://docs.oracle.com/cd/B19306_01/olap.102/b14346/dml_datatypes002.htm
		if (minValue >= -32768L && maxValue <= 32767L)
			return "SMALLINT";
		else if (minValue >= -2147483648L && maxValue <= 2147483647L)
			return "INTEGER";
		else 
			return "LONGINTEGER";
	}

	@Override
	public String getVarCharType() {
		return "VARCHAR2(255)";
	}

	@Override
	public String getVarCharType(int maxSize) {
		return "VARCHAR2(" + maxSize +")";
	}

	@Override
	public String getDoubleType() {
		return "NUMBER";
	}

	@Override
	public String getDoubleType(int precision, int scale) {
		return "NUMBER(" + precision + "," + scale +")";
	}

	@Override
	public String getBooleanType(String fieldName) {
		//see http://stackoverflow.com/questions/30062/boolean-field-in-oracle
		// Binding prepared statements ala 'preparedStatement.setBoolean(parameterIndex++, field)' causes the char field
		// to be set to 0 and 1. This is an oracle jdbc driver decision, not a swac developer decision.   
		return "char check (" + fieldName + " in ('0','1'))";
	}

	@Override
	public <T extends Enum<T>> String getEnumType(Class<T> anEnum) {
		return getVarCharType();
	}
	
	public String getDateType() { 
		return "DATE";
	}
		
	public String getDateTimeType() {
		return "TIMESTAMP";
	}

}