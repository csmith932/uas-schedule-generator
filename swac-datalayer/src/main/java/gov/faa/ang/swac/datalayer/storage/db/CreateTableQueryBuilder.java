/**
 * Copyright "TBD", Metron Aviation & CSSI.  All rights reserved.
 * 
 * This computer Software was developed with the sponsorship of the U.S. Government
 * under Contract No. DTFAWA-10-D-00033, which has a copyright license in accordance with AMS 3.5-13.(c)(1).
 */

package gov.faa.ang.swac.datalayer.storage.db;




public class CreateTableQueryBuilder implements QueryBuilder {

	private FieldTypeConverter converter;
	private StringBuilder sb;
	private boolean first = true;
	private boolean finalized = false;

	public CreateTableQueryBuilder(String tableName, FieldTypeConverter converter) {
		this.converter = converter;
		sb = new StringBuilder(500);
		sb.append("CREATE TABLE ").append(tableName);
		sb.append(" (");
	}

	public String toQueryString() { 
		if (! finalized) {
			sb.append(" )");
			finalized = true;
		}
		return sb.toString();
	}
	
	@Override
	public void addIntField(String name) {
		addTableCreateClause(sb, name, converter.getIntType());
	}
	
	@Override
	public void addIntField(String name, long minValue, long maxValue) {
		addTableCreateClause(sb, name, converter.getIntType(minValue, maxValue));
	}

	@Override
	public void addVarCharField(String name) {
		addTableCreateClause(sb, name, converter.getVarCharType());
	}

	@Override
	public void addVarCharField(String name, int size) {
		addTableCreateClause(sb, name, converter.getVarCharType(size));		
	}

	@Override
	public void addDoubleField(String name) {
		addTableCreateClause(sb, name, converter.getDoubleType());
	}

	@Override
	public void addDoubleField(String name, int precision, int scale) {
		addTableCreateClause(sb, name, converter.getDoubleType(precision, scale));
	}

	@Override
	public void addBooleanField(String name) {
		addTableCreateClause(sb, name, converter.getBooleanType(name));
	}

	@Override
	public <T extends Enum<T>> void addEnumField(String name, Class<T> anEnum) {
		addTableCreateClause(sb, name, converter.getEnumType(anEnum));
	}
	
	@Override
	public void addDateField(String name) {
		addTableCreateClause(sb, name, converter.getDateType());
	}
	
	@Override
	public void addDateTimeField(String name) {
		addTableCreateClause(sb, name, converter.getDateTimeType());
	}
	
	private void addTableCreateClause(StringBuilder sb, String fieldName, String type) {
		if (first) 
			first = false;
		else 
			sb.append(", ");
		sb.append(fieldName).append(' ').append(type);
	}

	@Override
	public void addFieldWithUnits(String name, String units) {
		addDoubleField(name);	
	}
}
	