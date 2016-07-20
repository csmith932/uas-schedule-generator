/**
 * Copyright "TBD", Metron Aviation & CSSI.  All rights reserved.
 * 
 * This computer Software was developed with the sponsorship of the U.S. Government
 * under Contract No. DTFAWA-10-D-00033, which has a copyright license in accordance with AMS 3.5-13.(c)(1).
 */

package gov.faa.ang.swac.datalayer.storage.db;



public class InsertTableQueryBuilder implements QueryBuilder {
	private StringBuilder sb;
	private int fieldCount;
	private boolean first = true;
	private boolean finalized = false;

	public InsertTableQueryBuilder(String tableName) {
		sb = new StringBuilder(500);
		sb.append("INSERT INTO ").append(tableName);
		sb.append(" (");
		fieldCount = 0;
	}
	
	public String toQueryString() {
		if (! finalized) {
			finalized = true;
			sb.append(" ) VALUES(");
			sb.append("?");
			for (int i = 1; i < fieldCount; i++)
				sb.append(", ?");
		}
		sb.append(") ");
		return sb.toString();
	}

	@Override
	public void addIntField(String name) {
		addClause(sb, name);
	}
	
	@Override
	public void addIntField(String name, long minValue, long maxValue) {
		addClause(sb, name);
	}

	@Override
	public void addVarCharField(String name) {
		addClause(sb, name);
	}

	@Override
	public void addVarCharField(String name, int size) {
		addClause(sb, name);		
	}

	@Override
	public void addDoubleField(String name) {
		addClause(sb, name);
	}

	@Override
	public void addDoubleField(String name, int precision, int scale) {
		addClause(sb, name);
	}

	@Override
	public void addBooleanField(String name) {
		addClause(sb, name);
	}

	@Override
	public <T extends Enum<T>> void addEnumField(String name, Class<T> anEnum) {
		addClause(sb, name);
	}

	@Override
	public void addDateField(String name) {
		addClause(sb, name);
	}

	@Override
	public void addDateTimeField(String name) {
		addClause(sb, name);
	}
	
	private void addClause(StringBuilder sb, String fieldName) {
		if (first) 
			first = false;
		else
			sb.append(", ");
		sb.append(fieldName);
		fieldCount++;
	}

	@Override
	public void addFieldWithUnits(String name, String units) {
		addDoubleField(name);
	}


}
	