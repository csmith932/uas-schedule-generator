package gov.faa.ang.swac.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;
import java.util.TreeMap;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * This class is used to create scenario imports file.
 * The file is a mapping between data files and
 * the corresponding classes.
 * 
 * <p>
 * 
 * 
 * @author Bingyi Xu - Metron Aviation 2015
 *
 */
public class ScenarioImportsFileGenerator {
	private static final Logger logger = LogManager.getLogger(ScenarioImportsFileGenerator.class);

	public static void main(String args[]) {
		String swacWork = System.getProperty("swac.work.dir");
		String swacHome = System.getenv("SWAC_HOME");
		String scenarioName = args[0];
		String baseDates = args[1];
		String forecastFiscalYears = args[2];
		String classifiers = args[3];
		File newScenarioDir = new File(swacWork + File.separator + "scenarios" + File.separator + scenarioName);

		try {
			writeScenarioImportsFile(swacHome, swacWork, newScenarioDir,
					baseDates.split(","), forecastFiscalYears.split(","), classifiers.split(","));
		} catch (IOException ioe) {
			System.err.println("Error writing scenarioImports.csv");
			System.err.print(ioe.getLocalizedMessage());
			System.exit(3);
		}
	}

	public static void writeScenarioImportsFile(String swacHome, String swacWork, File newScenarioDir, 
			String[] baseDates, String[] forecastFiscalYears, String[] classifiers) throws IOException {
		DateFormat y2kDf = new SimpleDateFormat("yyMMdd");
		List<String> lines = new ArrayList<String>();
		File swacDataDir = new File(swacWork + File.separator + "data");

        logger.info("Load data file to class mapping...");
        
        File d2c_mapping = new File(swacHome + File.separator + "config" + File.separator + "data_class_mapping.csv");
        List<Data2Class> data2Class = loadData2ClassMapping(d2c_mapping);
		
		logger.info("Writing scenarioImports.csv...");

		for (String date : baseDates) {
			Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("Coordinated Universal Time"));
			{ // Just want to reduce the scope of these temporary variables.
				int year = Integer.valueOf(date.substring(0, 4));
				int month = Integer.valueOf(date.substring(4, 6)) - 1;
				int day = Integer.valueOf(date.substring(6));

				cal.set(year, month, day);
			}

			y2kDf.setCalendar(cal);

			String y2kDate = y2kDf.format(cal.getTime());
			cal.add(Calendar.DAY_OF_MONTH, 1);
			String y2kNext = y2kDf.format(cal.getTime());
			StringBuilder tmp = new StringBuilder();

			if (y2kDate.substring(2, 4).contentEquals(y2kNext.substring(2, 4))) {
				tmp.append(y2kDate.substring(0, 4));
			} else {
				tmp.append(y2kDate.substring(0, 2));

				if (y2kDate.substring(2, 3).contentEquals(y2kNext.substring(2, 3))) {
					tmp.append(y2kDate.substring(2, 3));
				} else {
					tmp.append("[" + y2kDate.substring(2, 3) + y2kNext.substring(2, 3) + "]");
				}
				tmp.append("[" + y2kDate.substring(3, 4) + y2kNext.substring(3, 4) + "]");
			}

			if (y2kDate.substring(4, 5).contentEquals(y2kNext.substring(4, 5))) {
				tmp.append(y2kDate.substring(4, 5));
			} else {
				tmp.append("[" + y2kDate.substring(4, 5) + y2kNext.substring(4, 5) + "]");
			}
			tmp.append("[" + y2kDate.substring(5) + y2kNext.substring(5) + "]");

			String y2k2Day = tmp.toString();

			for (String year : forecastFiscalYears) {
				for (String classifier : classifiers) {
					for (Data2Class dc : data2Class) {
						String oldName = dc.fileName;
						String newName = oldName.replace("BASE_DATE", date);

						boolean subBd = !oldName.contentEquals(newName);

						oldName = newName.toString();
						newName = oldName.replace("FORECAST_FISCAL_YEAR", year);

						boolean subFfy = !oldName.contentEquals(newName);

						oldName = newName.toString();
						newName = oldName.replace("CLASSIFIER", classifier);

						boolean subCl = !oldName.contentEquals(newName);
						oldName = newName.toString();

						newName = oldName.replace("Y2K_DATE", y2kDate);
						subBd = (subBd | !oldName.contentEquals(newName));
						oldName = newName.toString();

						newName = oldName.replace("Y2K_2DAY", y2k2Day);
						subBd = (subBd | !oldName.contentEquals(newName));

						boolean dataDir = true;
						StringBuilder line = new StringBuilder();
						Iterator<File> it = FileUtils.iterateFiles(swacDataDir, new RegexFileFilter(newName),
								TrueFileFilter.INSTANCE);

						if (!it.hasNext()) {
							dataDir = false;
							it = FileUtils.iterateFiles(newScenarioDir, new RegexFileFilter(newName),
									TrueFileFilter.INSTANCE);
						}
						
						File file;
						TreeMap<String, File> files_map = new TreeMap<String, File>();
						while (it.hasNext()) {
							file = it.next();
							files_map.put(file.getName(), file);
						}

						boolean first = true;
						String curDir = "";
						for (File name : files_map.values()) {
							if (first) {
								first = false;
								if (dataDir) {
									curDir = name.getParent().replace(swacDataDir.getAbsolutePath(), "");
								} else {
									curDir = name.getParent().replace(newScenarioDir.getAbsolutePath(), "");
								}

								if (subBd) {
									line.append(date + ",");
								} else {
									line.append(",");
								}
								if (subFfy) {
									line.append(year + ",");
								} else {
									line.append(",");
								}
								if (subCl) {
									line.append(classifier + ",");
								} else {
									line.append(",");
								}
								line.append(dc.className + ",");
							} else {
								String newDir;
								if (dataDir) {
									newDir = name.getParent().replace(swacDataDir.getAbsolutePath(), "");
								} else {
									newDir = name.getParent().replace(newScenarioDir.getAbsolutePath(), "");
								}
								if (!curDir.contentEquals(newDir)) {
									continue;
								}
								line.append(",,,,");
							}
							if (curDir.isEmpty()) {
								line.append(name.getName() + "\n");
							} else {
								line.append(curDir + File.separator + name.getName() + "\n");
							}
						}
						if (line.toString().isEmpty()) {
							logger.warn("File not found: " + newName);
							if (subBd) {
								line.append(date + ",");
							} else {
								line.append(",");
							}
							if (subFfy) {
								line.append(year + ",");
							} else {
								line.append(",");
							}
							if (subCl) {
								line.append(classifier + ",");
							} else {
								line.append(",");
							}
							line.append(dc.className + "," + newName + "\n");
						}
						lines.add(line.toString());
					}
				}
			}
		}
		FileWriter newScenarioImports = new FileWriter(new File(newScenarioDir, "scenarioImports.csv"));
		newScenarioImports.append("#Base Date,Fiscal Year,Classifier,Data Type,Resource Name\n");
		for (String line : lines) {
			newScenarioImports.append(line);
		}

		newScenarioImports.flush();
		newScenarioImports.close();
	}

	private static List<Data2Class> loadData2ClassMapping(File mapping) {
		List<Data2Class> d2c = new ArrayList<Data2Class>();

		BufferedReader br = null;

		try {

			String line;

			br = new BufferedReader(new FileReader(mapping));

			while ((line = br.readLine()) != null) {
				processLine(line, d2c);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}

		return d2c;
	}

	private static void processLine(String line, List<Data2Class> d2c) {
		line = line.trim();
		if (line.length() == 0 || line.startsWith("#"))
			return;

		String[] parts = line.split(",");
		if (parts.length != 2) {
			logger.error("Unexpected data to class mapping entry: " + line);
			System.exit(1);
		}

		Data2Class value = new Data2Class();
		value.fileName = parts[0].trim();
		value.className = parts[1].trim();
		d2c.add(value);
	}
	   
    static class Data2Class
    {
    	public String fileName;
    	public String className;
    }
}
