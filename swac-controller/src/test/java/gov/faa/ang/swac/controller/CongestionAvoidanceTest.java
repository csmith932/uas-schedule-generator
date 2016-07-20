/**
 * Copyright 2012, Metron Aviation & CSSI.  All rights reserved.
 * 
 * This computer Software was developed with the sponsorship of the U.S. Government
 * under Contract No. DTFAWA-10-D-00033, which has a copyright license in accordance with AMS 3.5-13.(c)(1).
 */

package gov.faa.ang.swac.controller;

import gov.faa.ang.swac.common.Pair;

import java.util.ArrayList;
import java.util.List;

public class CongestionAvoidanceTest {
	public static void main(String[] args) {
		ProjectFixture.initSystemProperties("VERBOSE", "NORMAL", "FALSE");
		ProjectFixture.cleanWorkDir();
		ProjectFixture.initWorkDir();
		
		List<Pair<String, String>> rewrites = new ArrayList<Pair<String, String>>();
		
		rewrites.add(Pair.<String, String>create("LOG_LEVEL=VERBOSE", "LOG_LEVEL=DEBUG"));
		rewrites.add(Pair.<String, String>create("ENABLE_CONGESTION_AVOIDANCE=FALSE", "ENABLE_CONGESTION_AVOIDANCE=TRUE"));
		
		try {
			ProjectFixture.createScenario("mitScenario", "20101104", "2011", "base", rewrites);
			Bootstrap.main(new String[] { "mitScenario" });
			
		} catch (Exception e) { // Doesn't really matter what kind; we can't do much about it anyway.
			e.printStackTrace();
		}
	}
}
