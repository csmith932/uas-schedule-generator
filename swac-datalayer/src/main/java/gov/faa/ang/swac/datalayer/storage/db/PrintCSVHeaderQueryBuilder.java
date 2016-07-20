package gov.faa.ang.swac.datalayer.storage.db;

import java.io.PrintWriter;

public class PrintCSVHeaderQueryBuilder implements QueryBuilder {
	private static final char SEP = ',';
	private int fieldCount;
	private boolean first = true;
	private PrintWriter writer;
	
	public PrintCSVHeaderQueryBuilder(PrintWriter pw)  {
		writer = pw;
    }
	
	@Override
	public void addIntField(String name) {
		addClause(name);
	}

	@Override
	public void addIntField(String name, long minValue, long maxValue) {
		addClause(name);
	}

	@Override
	public void addVarCharField(String name) {
		addClause(name);
	}

	@Override
	public void addVarCharField(String name, int size) {
		addClause(name);
	}

	@Override
	public void addDoubleField(String name) {
		addClause(name);
	}

	@Override
	public void addDoubleField(String name, int precision, int scale) {
		addClause(name);
	}

	@Override
	public void addBooleanField(String name) {
		addClause(name);
	}

	@Override
	public <T extends Enum<T>> void addEnumField(String name, Class<T> anEnum) {
		addClause(name);
	}

	@Override
	public String toQueryString() {
		return null;
	}

	@Override
	public void addDateField(String name) {
		addClause(name);
	}

	@Override
	public void addDateTimeField(String name) {
		addClause(name);
	}
	
	@Override
	public void addFieldWithUnits(String name, String units) {
		addClause(name + " " + units);	
	}

	private void addClause(String fieldName) {
		if (first){
			first = false;
		}
		else{
			writer.print(SEP);
		}
		writer.print(fieldName);
		fieldCount++;
	}
}
