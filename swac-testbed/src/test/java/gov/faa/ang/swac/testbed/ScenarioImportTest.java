/**
 * Copyright 2016, Metron Aviation & CSSI.  All rights reserved.
 * 
 * This computer Software was developed with the sponsorship of the U.S. Government
 * under Contract No. DTFAWA-10-D-00033, which has a copyright license in accordance with AMS 3.5-13.(c)(1).
 */

package gov.faa.ang.swac.testbed;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;


/**
 * This test compares the windows and unix create scenario scripts to ensure that the embedded SWAC file/class links are
 * the same. Its very common to change the unix script and forget to change the windows script.
 * 
 * This test makes an obnoxious assumptions about the location of the script files. Ideally, this test would reside in
 * the project of where the create scripts are located, swac-assembly, but that isn't possible in this case because the
 * swac-assembly is a "pom" project and maven doesn't compile classes in pom projects. The kludgy workaround is to
 * locate this test in swac-testbed, a "jar" project, and presume that swac-testbed and swac-assembly are parallel
 * in the directory structure.
 */
public class ScenarioImportTest {
	@Test
	public void testClassAndFileLinks() throws Exception
	{
		// Find the swac-testbed directory.  Start from 'user.dir' and go up.
		File file = new File(System.getProperty("user.dir"));
		while (! file.getName().equals("swac-testbed")) {
			file = file.getParentFile();
			if (file == null)
				Assert.fail("Could not find create scenario scripts.  Started from " + System.getProperty("user.dir") + " and went up");
		}
		
		// Find swac-assembly dir
		File swacParent = file.getParentFile();
		if (swacParent == null)
			Assert.fail("Could not find create scenario scripts, no dir above swac-controller");
		
		File swacAssembly = new File(swacParent, "swac-assembly");
		if (swacAssembly == null || ! swacAssembly.exists())
			Assert.fail("Could not find create scenario scripts, swac-assembly not parallel to swac-controller");
		
		// Find windows and unix creat scenario scripts
		char sep = File.separatorChar; 
		File swacAssemblyBin = new File(swacAssembly.getAbsolutePath() + sep + "src" + sep + "main" + sep + "bin");
		if (swacAssemblyBin == null || ! swacAssemblyBin.exists())
			Assert.fail("Could not find create scenario scripts in swac-assembly/src/main/bin");
		
		File windowsScript = new File(swacAssemblyBin, "swac-create-scenario.bat");
		if (! windowsScript.exists())
			Assert.fail("Could not find windows create scenario script in swac-assembly/src/main/bin/swac-create-scenario.bat");
		
		File unixScript = new File(swacAssemblyBin, "new-swac-create-scenario.sh");
		if (! unixScript.exists())
			Assert.fail("Could not find unix create scenario script in swac-assembly/src/main/bin/new-swac-create-scenario.sh");


		// Read in windows create scenario script, parse out file and class lists.
		Set<String> winFilesSet = new LinkedHashSet<String>();
		parseWindowsScript("file_name_templates", windowsScript, winFilesSet);
		Set<String> winClassSet = new LinkedHashSet<String>();
		parseWindowsScript("file_classes", windowsScript, winClassSet);
		Assert.assertEquals("For windows create scenario script, num of files != num of classes, file " + winFilesSet.size() + ", classes " + winClassSet.size(),
							winFilesSet.size(), winClassSet.size());
		Map<String, String> winFileClassMap = new HashMap<String, String>();
		combineToMap(winFilesSet, winClassSet, winFileClassMap);
		
		// Read in unix create scenario script, parse out file and class lists.  
		// Adjust some values to align with windows style to allow apple to apples comparisons
		Set<String> unixFilesSet = new LinkedHashSet<String>();
		parseUnixScript("Files", unixScript, unixFilesSet);
		unixFilesSet = adjustUnixFiles(unixFilesSet, new LinkedHashSet<String>());
		Set<String> unixClassSet = new LinkedHashSet<String>();
		parseUnixScript("Classes", unixScript, unixClassSet);
		unixClassSet = adjustUnixClasses(unixClassSet,  new LinkedHashSet<String>());
		Assert.assertEquals("For unix create scenario script, num of files != num of classes, file " + unixFilesSet.size() + ", classes " + unixClassSet.size(),
				unixFilesSet.size(), unixClassSet.size());
		Map<String, String> unixFileClassMap = new HashMap<String, String>();
		combineToMap(unixFilesSet, unixClassSet, unixFileClassMap); 

		// Test if the classes in windows and unix scripts match up
		if (! winClassSet.equals(unixClassSet)) {
			LinkedHashSet<String> uniqueWindowClasses = new LinkedHashSet<String>(winClassSet);
			uniqueWindowClasses.removeAll(unixClassSet);
			
			LinkedHashSet<String> uniqueUnixClasses = new LinkedHashSet<String>(unixClassSet);
			uniqueUnixClasses.removeAll(winClassSet);
			
			StringBuilder sb = new StringBuilder();
			sb.append("Create scenario script mismatch error between windows and unix.  ");
			if(! uniqueWindowClasses.isEmpty()) 
				sb.append("Unique windows class(es): ").append(uniqueWindowClasses).append(". ");
			if(! uniqueUnixClasses.isEmpty()) 
				sb.append("Unique unix class(es): ").append(uniqueUnixClasses).append(". ");
			
			System.out.println(sb);
			Assert.fail(sb.toString());
		}
		
		// Test if the files in windows and unix scripts match up
		StringBuilder sb = null;
		for (Entry<String, String> winEntry : winFileClassMap.entrySet()) {
			String winFile = winEntry.getValue();
			String unixFile = unixFileClassMap.get(winEntry.getKey());
			if (! winFile.equals(unixFile)) {
				if (sb == null) {
					sb = new StringBuilder();
					sb.append("Create scenario script mismatch error between windows and unix.  ");
				}
				sb.append("Different files specified for class '" + winEntry.getKey() + "': windows: '" + winFile + "', unix: '" + unixFile + "'. ");
			}
		}
		if (sb != null)  {
			System.out.println(sb);
			Assert.fail(sb.toString());
		}
	}

	
	/**
	 * In the unix script, references to inner classes have to escape the $. Remove this escape so that style so that comparisons will be apples to apples
	 * Example: 
	 * 		"gov.faa.ang.swac.qroutes.trajectorymodifier.QRoutesCityPairs\$QRoutesCityPairsRecord" 
	 * becomes 
	 * 		"gov.faa.ang.swac.qroutes.trajectorymodifier.QRoutesCityPairs$QRoutesCityPairsRecord"
	 * @param oldSet
	 * @param newSet
	 * @return
	 */
	private <T extends Collection<String>> T adjustUnixClasses(Collection<String> oldSet, T newSet) {
		for(String str : oldSet) {
			//convert from "gov.faa.ang.swac.qroutes.trajectorymodifier.QRoutesCityPairs\$QRoutesCityPairsRecord" to "gov.faa.ang.swac.qroutes.trajectorymodifier.QRoutesCityPairs$QRoutesCityPairsRecord"
			int backslashDollarIndex = str.indexOf("\\$");
			while (backslashDollarIndex >= 0) {
				str = str.substring(0, backslashDollarIndex) + str.substring(backslashDollarIndex+1);
				backslashDollarIndex = str.indexOf("\\$");
			}
		
			newSet.add(str);
		}
		return newSet;
	}
	

	private void parseUnixScript(String match, File unixScript, Set<String> foundTokenList) throws IOException {
//		Files[0]="airport_pareto_curves_CLASSIFIER_FORECAST_FISCAL_YEAR.csv"
//		Files[1]="sector_capacities_CLASSIFIER_BASE_DATE_FORECAST_FISCAL_YEAR.csv"
		
		BufferedReader reader = new BufferedReader(new FileReader(unixScript));
		String line = reader.readLine();
		while (line != null) {
			int tokenIndex = line.lastIndexOf(match);
			if (tokenIndex >= 0) {
				int startIndex = -1;
				int filesEqualIndex = line.lastIndexOf(match);
				if (filesEqualIndex >= 0)
					filesEqualIndex = line.indexOf("]=\"", filesEqualIndex);
				if (filesEqualIndex >= 0)
					startIndex = filesEqualIndex + 3;
				int endIndex = line.lastIndexOf("\"");
				
				if (startIndex >= 0 && endIndex >= 0) {
					String token = line.substring(startIndex, endIndex);
					foundTokenList.add(token);
				}
			}
			line = reader.readLine();
		}
		reader.close();
			
	}

	private void parseWindowsScript(String match, File windowsScript, Collection<String> foundTokenList) throws IOException {
//		set file_name_templates="airport_pareto_curves_CLASSIFIER_FORECAST_FISCAL_YEAR.csv"
//		set "file_name_templates=%file_name_templates%,sector_capacities_CLASSIFIER_BASE_DATE_FORECAST_FISCAL_YEAR.csv"
//		set "file_name_templates=%file_name_templates%,pgb.f00Y2K_2DAY(00|06|12|18)"
//		set "file_name_templates=%file_name_templates%,airport_script_CLASSIFIER_FORECAST_FISCAL_YEAR.js"

//		set file_classes="gov.faa.ang.swac.common.entities.AirportParetoCurve"
//		set "file_classes=%file_classes%,gov.faa.ang.swac.changegenerator.SectorTimeCapacityDB"
		
		BufferedReader reader = new BufferedReader(new FileReader(windowsScript));
		String line = reader.readLine();
		while (line != null) {
			int tokenIndex = line.lastIndexOf(match);
			if (tokenIndex >= 0) {
				int startIndex = -1;
				int percent = line.lastIndexOf("%,");
				if (percent >= 0)
					startIndex = percent + 2;
				else {
					int equals = line.lastIndexOf("=\"");
					if (equals >= 0)
						startIndex = equals + 2;
				}
				int endIndex = line.lastIndexOf("\"");
				
				if (startIndex >= 0 && endIndex >= 0) {
					String token = line.substring(startIndex, endIndex);
					foundTokenList.add(token);
				}
					
			}
			line = reader.readLine();
		}
		reader.close();
		
	}
	
	/**
	 * The Y2K_2DAY template has a slightly different syntax in windows and unix. Convert to windows style so that
	 * comparisons will be apples to apples
	 * Example: 
	 * 		"pgb.f00Y2K_2DAY{00,06,12,18}" 
	 * becomes 
	 * 		"pgb.f00Y2K_2DAY(00|06|12|18)"
	 * 
	 * @param oldSet
	 * @param newSet
	 * @return
	 */
	private <T extends Collection<String>> T adjustUnixFiles(Collection<String> oldSet, T newSet) {
		for(String str : oldSet) {
			//convert from pgb.f00Y2K_2DAY{00,06,12,18} to pgb.f00Y2K_2DAY(00|06|12|18)
			int y2kIndex = str.indexOf("Y2K_2DAY");
			if (y2kIndex >=0) {
				int endIndex = str.indexOf("}", y2kIndex);
				String y2kStr = str.substring(y2kIndex, endIndex) + ")";
				y2kStr = y2kStr.replace("{", "(");
				y2kStr = y2kStr.replace(",", "|");
				str = str.substring(0, y2kIndex) + y2kStr; 
			}
			newSet.add(str);
		}
		return newSet;
	}

	private void combineToMap(Collection<String> filesList, Collection<String> classList, Map<String, String> fileClassMap) {
		Iterator<String> fileIt = filesList.iterator();
		Iterator<String> classIt = classList.iterator();
		while (fileIt.hasNext()) { 
			fileClassMap.put(classIt.next(), fileIt.next());
		}
	}
}
