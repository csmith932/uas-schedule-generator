package gov.faa.ang.swac.common.flightmodeling.fileio;

import gov.faa.ang.swac.common.datatypes.Timestamp;
import gov.faa.ang.swac.common.flightmodeling.fileio.FlightDigestRecord;
import gov.faa.ang.swac.datalayer.storage.db.QueryBinder;
import gov.faa.ang.swac.datalayer.storage.db.QueryBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.sql.SQLException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


/**
 * Tests for ensuring FlightDigestRecords reading and writing is consistent when fields are added or removed.
 */
public class FlightDigestRecordTest 
{
	/** The number of fields in 'sampleReportLine'. */
	private final int numFields = FlightDigestRecord.sampleReportLine.split(",").length;
	/** The flight to test against. */
	private FlightDigestRecord flight;
	
	/** Reads a flight output record line into the flight to test against. */
	@Before
	public void setUp() throws IOException {
		flight = new FlightDigestRecord();
		BufferedReader reader = new BufferedReader(new StringReader(FlightDigestRecord.sampleReportLine));
		flight.readItem(reader);
		reader.close();
	}
	
	/**
	 * Verifies that the number of fields written in the header matches the
	 * number of fields read.
	 */
	@Test
	public void testHeaderWrite() throws IOException {
		StringWriter writer = new StringWriter();
		flight.writeHeader(new PrintWriter(writer), 0);
		int expected = numFields;
		int actual = writer.toString().split(",").length;
		Assert.assertEquals("The number of fields written by 'writeHeader' is different from the number of fields read.",
				expected, actual);
	}
	
	/**
	 * Verifies that the number of fields written matches the number of fields
	 * read.
	 */
	@Test
	public void testFieldWrite() throws IOException {
		StringWriter writer = new StringWriter();
		flight.writeItem(new PrintWriter(writer));
		int expected = numFields;
		int actual = writer.toString().split(",").length;
		Assert.assertEquals("The number of fields written by 'writeItem' is different from the number of fields read.",
				expected, actual);
	}
	
	/**
	 * Verifies that the number of fields added to a query matches the number of
	 * fields read.
	 */
	@Test
	public void testQueryBuilder() {
		final StringBuilder fields = new StringBuilder();
		flight.describeFields(new QueryBuilder() {
			@Override public String toQueryString() { return null; }
			@Override public void addVarCharField(String name, int size) { fields.append(" "); }
			@Override public void addVarCharField(String name) { fields.append(" "); }
			@Override public void addIntField(String name, long minValue, long maxValue) { fields.append(" "); }
			@Override public void addIntField(String name) { fields.append(" "); }
			@Override public <T extends Enum<T>> void addEnumField(String name, Class<T> anEnum) { fields.append(" "); }
			@Override public void addDoubleField(String name, int precision, int scale) { fields.append(" "); }
			@Override public void addDoubleField(String name) { fields.append(" "); }
			@Override public void addDateTimeField(String name) { fields.append(" "); }
			@Override public void addDateField(String name) { fields.append(" "); }
			@Override public void addBooleanField(String name) { fields.append(" "); }
			@Override public void addFieldWithUnits(String name, String units) { fields.append(" "); }
		});
		int expected = numFields;
		int actual = fields.length();
		Assert.assertEquals("The number of fields produced by 'describeFields' is different from the number of fields read.",
				expected, actual);
	}
	
	/**
	 * Verifies that the number of fields added to a binder matches the number
	 * of fields read.
	 */
	@Test
	public void testQueryBinder() throws Exception {
		final StringBuilder fields = new StringBuilder();
		flight.bindFields(new QueryBinder() {
			@Override public void bindField(Timestamp timestamp, boolean asDate) throws Exception { fields.append(" "); } 
			@Override public void bindField(Timestamp timestamp) throws Exception { fields.append(" "); } 
			@Override public void bindField(Enum<?> enumValue) throws Exception { fields.append(" "); } 
			@Override public void bindField(Boolean field) throws Exception { fields.append(" "); } 
			@Override public void bindField(Double field) throws Exception { fields.append(" "); } 
			@Override public void bindField(Integer field) throws Exception { fields.append(" "); } 
			@Override public void bindField(Object field, int sqlType) throws Exception { fields.append(" "); } 
			@Override public void bindField(Object field) throws Exception { fields.append(" "); } 
			@Override public void bindField(double field) throws Exception { fields.append(" "); } 
			@Override public void bindField(boolean field) throws Exception { fields.append(" "); } 
			@Override public void bindField(int field) throws Exception { fields.append(" "); } 
			@Override public void addBatch() throws SQLException {}
		});
		int expected = numFields;
		int actual = fields.length();
		Assert.assertEquals("The number of fields produced by 'bindFields' is different from the number of fields read.",
				expected, actual);
	}
}
