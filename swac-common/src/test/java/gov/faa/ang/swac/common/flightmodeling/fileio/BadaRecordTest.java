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

public class BadaRecordTest {
	
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
	public void testReadWriteItem() {
		BadaRecord br1 = null;
		BadaRecord br2 = null;
		
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(testInputFile)));
			
			PrintWriter writer = new PrintWriter(testOutputFile);
			
			RawBadaRecord rbd = new RawBadaRecord();
			
			rbd.readItem(reader);
			
			reader.close();
			
			br1 = new BadaRecord(rbd);
			
			br1.writeHeader(writer, 1);
			br1.writeItem(writer);
			br1.writeFooter(writer);
			
			writer.close();
			
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(testOutputFile)));
			
			br2 = new BadaRecord();
			
			br2.readItem(reader);
			
			reader.close();
			
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		
		assertEquals("Mar 11 2009", br2.getFileDate());
		assertEquals("B744", br2.getAircraftType());
		assertEquals("Dec 19 2008", br2.getSourceOpfDate());
		assertEquals("Mar 05 2009", br2.getSourceApfDate());
		assertEquals(casSpeed[0][0], br2.getCasSpeed()[0][0]);
		assertEquals(casSpeed[0][2], br2.getCasSpeed()[0][2]);
		assertEquals(casSpeed[1][0], br2.getCasSpeed()[1][0]);
		assertEquals(casSpeed[1][2], br2.getCasSpeed()[1][2]);
		assertEquals(casSpeed[2][0], br2.getCasSpeed()[2][0]);
		assertEquals(casSpeed[2][2], br2.getCasSpeed()[2][2]);
		assertEquals(new Double(machSpeed[0]), new Double(br2.getMachSpeed()[0]));
		assertEquals(new Double(machSpeed[1]), new Double(br2.getMachSpeed()[1]));
		assertEquals(new Double(machSpeed[2]), new Double(br2.getMachSpeed()[2]));
		assertEquals(massLevels[0], br2.getMassLevel()[0]);
		assertEquals(massLevels[1], br2.getMassLevel()[1]);
		assertEquals(massLevels[2], br2.getMassLevel()[2]);
		assertEquals("ISA", br2.getTemperature());
		assertEquals(maxAltitude, br2.getMaxAltitude());
		assertEquals(numRecords, br2.getFlightLevelRecords().size());
	}
}
