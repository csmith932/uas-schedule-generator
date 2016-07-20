package gov.faa.ang.swac.datalayer.storage.fileio;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import gov.faa.ang.swac.datalayer.DataAccessException;
import gov.faa.ang.swac.datalayer.ResourceManager;
import gov.faa.ang.swac.datalayer.ResourceManager.LOCATION;
import gov.faa.ang.swac.datalayer.identity.FileDataDescriptor;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

class DummyType implements StreamSerializable {
	
	public void readItem(InputStream inStream) throws IOException {
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(new InputSource(inStream));

		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void writeItem(OutputStream out) throws IOException {/*Not tested, not implemented.*/}
}

public class FileValidationTest {

	private FileDataDescriptor fdd;
	private ResourceManager rm;

	@Before
	public void setUp() throws Exception {
		fdd = new FileDataDescriptor();
		fdd.setFaultTolerant(false);
		fdd.setDataType(DummyType.class);
		fdd.setLocation(LOCATION.DATA);
		fdd.setReadOnly(false);

		rm = new ResourceManager();
		rm.setBatchName("");
		rm.setBinPath("");
		rm.setConfigPath("");
		rm.setDataDir("src"+File.separator+"test"+File.separator+"resources");
		rm.setOutputDir("");
		rm.setReportDir("");
		rm.setRootPath("");
		rm.setScenarioName("");
		rm.setTempDir("");
	}

	@Test
	public void validateValidXmlFile() {
		fdd.setSchemaName("EquipmentGroups_TEST.xsd");
		fdd.setResourceName("EquipmentGroups_TEST.xml");

		FileMarshaller fm = (FileMarshaller) fdd.createMarshaller(rm);
		List<Serializable> stuff = new ArrayList<Serializable>();

		try {
			fm.loadInternal(stuff); // This SHOULD succeed...
		} catch (DataAccessException e) {
			// If it doesn't succeed, then the test has failed.
			fail(e.getMessage());
		}
	}

	@Test
	public void validateInvalidXmlFile() {
		fdd.setSchemaName("EquipmentGroups_TEST.xsd");
		fdd.setResourceName("EquipmentGroups_INVALID_TEST.xml");

		FileMarshaller fm = (FileMarshaller) fdd.createMarshaller(rm);
		List<TextSerializable> stuff = new ArrayList<TextSerializable>();

		try {
			fm.loadInternal(stuff);// This SHOULD throw an exception...
			fail("The XML file is deliberately invalid, which should have been detected -- but wasn't.");
		} catch (DataAccessException e) {

			Throwable t = e.getCause();
			//File contains a value out of range for a what is declared as a percentage.  The invalid value is on line 2284 of the file.
			if(!t.getMessage().contains("cvc-maxInclusive-valid: Value '700000' is not facet-valid with respect to maxInclusive '1.0E2' for type 'PercentageType'.")){
				fail(t.getMessage());
			}
		}
	}

	@Test
	public void testMisingXmlFile() {

		fdd.setResourceName("MISSING_XML_FILE.xml"); // File actually doesn't exist... could be called anything here.

		FileMarshaller fm = (FileMarshaller) fdd.createMarshaller(rm);
		List<Serializable> stuff = new ArrayList<Serializable>();

		try {
			fm.loadInternal(stuff);// This SHOULD throw an exception...
			fail("This test should have caught an exception caused by the missing XML file, but no exception was caught at all.");
		} catch (DataAccessException e) {

			Throwable t = e.getCause();

			if (!(t instanceof java.io.FileNotFoundException)) {
				fail("This test should have caught an exception caused by the missing XML file, but instead we got: "+t.getMessage());
			}
		}
	}

	@Test
	public void testMisingSchemaFile() {

		fdd.setSchemaName("MISSING_SCHEMA_FILE.xsd"); // File actually doesn't exist... could be called anything here.
		fdd.setResourceName("EquipmentGroups.xml");

		FileMarshaller fm = (FileMarshaller) fdd.createMarshaller(rm);
		List<Serializable> stuff = new ArrayList<Serializable>();

		try {
			fm.loadInternal(stuff);// This SHOULD throw an exception...
			fail("This test should have caught an exception caused by a missing schema file, but no exception was caught at all.");
		} catch (DataAccessException e) {
			if (!e.getMessage().contains("Schema file not found")) {
				fail("This test should have caught an exception caused by a missing schema file.  Instead, we got: "+e.getMessage());
			}
		}
	}

}

