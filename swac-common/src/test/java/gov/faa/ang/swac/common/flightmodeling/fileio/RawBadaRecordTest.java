/**
 * Copyright "TBD", Metron Aviation & CSSI.  All rights reserved.
 * 
 * This computer Software was developed with the sponsorship of the U.S. Government
 * under Contract No. DTFAWA-10-D-00033, which has a copyright license in accordance with AMS 3.5-13.(c)(1).
 */

package gov.faa.ang.swac.common.flightmodeling.fileio;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class RawBadaRecordTest {
	
	private int[][] casSpeed = { { 250, Integer.MIN_VALUE, 330 }, { 250, Integer.MIN_VALUE, 340 }, { 250, Integer.MIN_VALUE, 310 } };
	private double[] machSpeed = { 0.85, 0.84, 0.86 };
	private int[] massLevels = { 216528, 285700, 396800 };
	private int maxAltitude = 45000;
	private int numRecords = 28;
	
	private File testInputFile;
	private File testOutputFile;
	
	@Before
	public void setUp() throws Exception {
		testInputFile = new File("src" + File.separator + "test" + File.separator + "resources" + File.separator + "B744__.PTF");
		testOutputFile = new File("src" + File.separator + "test" + File.separator + "resources" + File.separator + "B744Test.xml");
	}
	
	@After
	public void tearDown() throws Exception {
		testOutputFile.delete();
	}
	
	@Test
	public void testReadItem() {
		RawBadaRecord rbd = new RawBadaRecord();
		
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(testInputFile)));
			
			rbd.readItem(reader);
			
			reader.close();
			
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		
		assertEquals("Mar 11 2009", rbd.getFileDate());
		assertEquals("B744", rbd.getAircraftType());
		assertEquals("Dec 19 2008", rbd.getSourceOpfDate());
		assertEquals("Mar 05 2009", rbd.getSourceApfDate());
		assertEquals(casSpeed[0][0], rbd.getCasSpeed()[0][0]);
		assertEquals(casSpeed[0][2], rbd.getCasSpeed()[0][2]);
		assertEquals(casSpeed[1][0], rbd.getCasSpeed()[1][0]);
		assertEquals(casSpeed[1][2], rbd.getCasSpeed()[1][2]);
		assertEquals(casSpeed[2][0], rbd.getCasSpeed()[2][0]);
		assertEquals(casSpeed[2][2], rbd.getCasSpeed()[2][2]);
		assertEquals(new Double(machSpeed[0]), new Double(rbd.getMachSpeed()[0]));
		assertEquals(new Double(machSpeed[1]), new Double(rbd.getMachSpeed()[1]));
		assertEquals(new Double(machSpeed[2]), new Double(rbd.getMachSpeed()[2]));
		assertEquals(massLevels[0], rbd.getMassLevel()[0]);
		assertEquals(massLevels[1], rbd.getMassLevel()[1]);
		assertEquals(massLevels[2], rbd.getMassLevel()[2]);
		assertEquals("ISA", rbd.getTemperature());
		assertEquals(maxAltitude, rbd.getMaxAltitude());
		assertEquals(numRecords, rbd.getFlightLevelRecords().size());
	}
	
	@Test
	public void testWriteItem() {
		RawBadaRecord testRBR = new RawBadaRecord();
		
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(testInputFile)));
			PrintWriter writer = new PrintWriter(testOutputFile);
			
			RawBadaRecord rbd = new RawBadaRecord();
			
			rbd.readItem(reader);
			
			reader.close();
			
			rbd.writeItem(writer);
			
			writer.close();
			
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(testOutputFile)));
			testRBR.readItem(reader);
			reader.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		assertEquals("Mar 11 2009", testRBR.getFileDate());
		assertEquals("B744", testRBR.getAircraftType());
		assertEquals("Dec 19 2008", testRBR.getSourceOpfDate());
		assertEquals("Mar 05 2009", testRBR.getSourceApfDate());
		assertEquals(casSpeed[0][0], testRBR.getCasSpeed()[0][0]);
		assertEquals(casSpeed[0][2], testRBR.getCasSpeed()[0][2]);
		assertEquals(casSpeed[1][0], testRBR.getCasSpeed()[1][0]);
		assertEquals(casSpeed[1][2], testRBR.getCasSpeed()[1][2]);
		assertEquals(casSpeed[2][0], testRBR.getCasSpeed()[2][0]);
		assertEquals(casSpeed[2][2], testRBR.getCasSpeed()[2][2]);
		assertEquals(new Double(machSpeed[0]), new Double(testRBR.getMachSpeed()[0]));
		assertEquals(new Double(machSpeed[1]), new Double(testRBR.getMachSpeed()[1]));
		assertEquals(new Double(machSpeed[2]), new Double(testRBR.getMachSpeed()[2]));
		assertEquals(massLevels[0], testRBR.getMassLevel()[0]);
		assertEquals(massLevels[1], testRBR.getMassLevel()[1]);
		assertEquals(massLevels[2], testRBR.getMassLevel()[2]);
		assertEquals("ISA", testRBR.getTemperature());
		assertEquals(maxAltitude, testRBR.getMaxAltitude());
		assertEquals(numRecords, testRBR.getFlightLevelRecords().size());
	}
}
