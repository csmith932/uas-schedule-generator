/**
 * Copyright "TBD", Metron Aviation & CSSI.  All rights reserved.
 *
 * This computer Software was developed with the sponsorship of the U.S. Government
 * under Contract No. DTFAWA-10-D-00033, which has a copyright license in accordance with AMS 3.5-13.(c)(1).
 */

package gov.faa.ang.swac.datalayer.storage.db;

public class MariaDBFieldTypeConverter implements FieldTypeConverter  {

	@Override
	public String getIntType() {
		return "INT";
	}

	@Override
	public String getIntType(long minValue, long maxValue) {
		if (minValue >= -128L && maxValue < 127L)
			return "TINYINT";
		else if (minValue >= -32768L && maxValue < 32767L)
			return "SMALLINT";
		else if (minValue >= -8388608L && maxValue < 8388607L)
			return "MEDIUMINT";
		else if (minValue >= -9223372036854775808L && maxValue < 9223372036854775807L)
			return "BIGINT";
		else
			return "INT";
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
		return "DOUBLE";
	}

	@Override
	public String getDoubleType(int precision, int scale) {
		return "DOUBLE(5,2)";
	}

	@Override
	public String getBooleanType(String fieldName) {
		return "BOOLEAN";
	}

	@Override
	public <T extends Enum<T>> String getEnumType(Class<T> anEnum) {
		//ENUM('red', 'pink', 'blue')
		T[] enumValues = anEnum.getEnumConstants();
		StringBuilder sb = new StringBuilder(6 + enumValues.length * 15);
		sb.append("ENUM('");
		sb.append(enumValues[0].name());
		for (int i = 1; i < enumValues.length; i++) {
			sb.append("', '");
			sb.append(enumValues[i].name());
		}
		sb.append("')");
		return sb.toString();
	}

	public String getDateType() { 
		return "DATE";
	}
		
	public String getDateTimeType() {
		return "DATETIME";
	}

}
