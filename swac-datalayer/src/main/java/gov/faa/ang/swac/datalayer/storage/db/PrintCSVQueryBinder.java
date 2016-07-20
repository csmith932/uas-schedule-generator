package gov.faa.ang.swac.datalayer.storage.db;

import gov.faa.ang.swac.common.datatypes.Timestamp;

import java.io.PrintWriter;
import java.sql.SQLException;

public class PrintCSVQueryBinder implements QueryBinder {
	private static final char SEP = ',';
	private int fieldCount;
	private boolean first = true;
	private PrintWriter writer;
	
	
	public PrintCSVQueryBinder(PrintWriter pw) {
		writer = pw;
    }
		
	@Override
	public void bindField(int field) throws Exception {
		addSeparator();
		if(field == Integer.MIN_VALUE){
			writer.print("null");
		}
		else{
			writer.print(field);
		}
	}

	@Override
	public void bindField(boolean field) throws Exception {
		addSeparator();
		writer.print(field ? "1" : "0");
	}

	@Override
	public void bindField(double field) throws Exception {
		addSeparator();
		writer.print(field);
	}

	@Override
	public void bindField(Object field) throws Exception {
		addSeparator();
		if(field == null){
			writer.print("");
		}
		else{
			writer.print(field.toString());
		}
	}

	@Override
	public void bindField(Object field, int sqlType) throws Exception {
	
	}

	@Override
	public void bindField(Integer field) throws Exception {
		addSeparator();
		if((field == null) || (field == Integer.MIN_VALUE)){
			writer.print("");
		}
		else{
			writer.print(field.toString());
		}
	}

	@Override
	public void bindField(Double field) throws Exception {
		addSeparator();
		if((field == null) || (field.isNaN())){
			writer.print("");
		}
		else{
			writer.print(field.toString());
		}
	}

	@Override
	public void bindField(Boolean field) throws Exception {
		addSeparator();
		if(field == null) {
			writer.print("");
		}
		else{
			writer.print(field ? "1" : "0");
		}
	}

	@Override
	public void bindField(Enum<?> field) throws Exception {
		addSeparator();
		if(field == null) {
			writer.print("");
		}
		else{
			writer.print(field.name());
		}
	}

	@Override
	public void bindField(Timestamp timestamp) throws Exception {
		addSeparator();
		if(timestamp == null){
			writer.print("");
		}
		else{
			writer.print(timestamp.toBonnString());
		}
	}

	@Override
	public void bindField(Timestamp timestamp, boolean asDate) throws Exception {
		addSeparator();
		if(asDate){
			if(timestamp == null){
				writer.print("");
			}
			else{
				writer.print(timestamp.toBonnDateOnlyString());
			}
		}
		else{
			bindField(timestamp);
		}
	}

	@Override
	public void addBatch() throws SQLException {
		
	}
	
	private void addSeparator() {
		if (first){
			first = false;
		}
		else{
			writer.print(SEP);
		}
		fieldCount++;
	}

}
