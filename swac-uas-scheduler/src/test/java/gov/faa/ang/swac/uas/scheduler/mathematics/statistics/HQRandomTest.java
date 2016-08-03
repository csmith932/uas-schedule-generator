/**
 * Copyright "TBD", Metron Aviation & CSSI.  All rights reserved.
 * 
 * This computer Software was developed with the sponsorship of the U.S. Government
 * under Contract No. DTFAWA-10-D-00033, which has a copyright license in accordance with AMS 3.5-13.(c)(1).
 */

package gov.faa.ang.swac.uas.scheduler.mathematics.statistics;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import gov.faa.ang.swac.uas.scheduler.mathematics.statistics.HQRandom;

public class HQRandomTest {

	private static Logger logger = LogManager.getLogger(HQRandomTest.class);
	
	@Before
	public void setup()
	{
		// By default, swac-parent pom's surefire plugin configuration specifies
		// swac-data/src/test/resources/log4j-junit.xml to be logging configuration file, and that file logs levels
		// ERROR or higher.
		// Uncomment to manually setup logger to run locally 
		// BasicConfigurator.configure();
	}
	
    @Test
    public void main() {
		HQRandom hqRandom = new HQRandom();
		logger.info("set seed for random number stream 1");
		logger.info("seed: "+hqRandom.staticSeed);
		HQRandom randomS1 = new HQRandom(123456789L);
		logger.info("seed: "+hqRandom.staticSeed);
		HQRandom randomS2 = new HQRandom();
		logger.info("seed: "+hqRandom.staticSeed);
		HQRandom randomS3 = new HQRandom(123456789L);
		logger.info("seed: "+hqRandom.staticSeed);
		HQRandom randomS4 = new HQRandom();
		logger.info("seed: "+hqRandom.staticSeed);
		double r1, r2, r3, r4;
		logger.info("r1\tr2\tr3\tr4");
		for (int i=0; i<50; i++) {
			r1=randomS1.nextDouble();
			r2=randomS2.nextDouble();
			r3=randomS3.nextDouble();
			r4=randomS4.nextDouble();
			logger.info(r1+"\t"+r2+"\t"+r3+"\t"+r4);
			if (i == 25) {
				logger.info("resetting streams");
				randomS1.resetStreamToLastSavedState();
				randomS2.resetStreamToLastSavedState();
				randomS3.resetStreamToLastSavedState();
				randomS4.resetStreamToLastSavedState();
			}
		}
	}
}
