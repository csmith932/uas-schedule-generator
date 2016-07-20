/**
 * Copyright "TBD", Metron Aviation & CSSI.  All rights reserved.
 * 
 * This computer Software was developed with the sponsorship of the U.S. Government
 * under Contract No. DTFAWA-10-D-00033, which has a copyright license in accordance with AMS 3.5-13.(c)(1).
 */

package gov.faa.ang.swac.datalayer.storage.db;

// https://help.sap.com/saphelp_nw70ehp1/helpdata/en/16/9dc9ac8bc72a48b80e639abaa2e497/content.htm?frameset=/en/57/1177611c11cd418564cdbc1488ce33/frameset.htm&current_toc=/en/43/05e450d1cd6e95e10000000a11466f/plain.htm&node_id=1115&show_children=false
// http://etutorials.org/SQL/SQL+Bible+Oracle/Part+I+SQL+Basic+Concepts+and+Principles/Chapter+3+SQL+Data+Types/In+Numbers+Strength/
public class SQL99FieldTypeConverter implements FieldTypeConverter  {

	@Override
	public String getIntType() {
		return "INTEGER";
	}

	@Override
	public String getIntType(long minValue, long maxValue) {
		// from http://docs.oracle.com/cd/B19306_01/olap.102/b14346/dml_datatypes002.htm
		if (minValue >= -32768L && maxValue <= 32767L)
			return "SMALLINT";
		else if (minValue >= -2147483648L && maxValue <= 2147483647L)
			return "INTEGER";
		else 
			return "BIGINT";
	}

	@Override
	public String getVarCharType() {
		return "VARCHAR(255)";
	}

	@Override
	public String getVarCharType(int maxSize) {
		return "VARCHAR(" + maxSize +")";
	}

	@Override
	public String getDoubleType() {
		return "NUMERIC";
	}

	@Override
	public String getDoubleType(int precision, int scale) {
		return "NUMERIC(" + precision + "," + scale +")";
	}

	@Override
	public String getBooleanType(String fieldName) {
		return "VARCHAR(1)";
	}

	@Override
	public <T extends Enum<T>> String getEnumType(Class<T> anEnum) {
		return getVarCharType();
	}

	public String getDateType() { 
		return "DATE";
	}
	
	public String addTimeType() {
		return "TIME";
	}
	
	public String getDateTimeType() {
		return "DATETIME";
	}
}