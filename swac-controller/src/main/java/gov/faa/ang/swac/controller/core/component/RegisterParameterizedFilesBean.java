/**
 * Copyright "2014", Metron Aviation & CSSI.  All rights reserved.
 * 
 * This computer Software was developed with the sponsorship of the U.S. Government
 * under Contract No. DTFAWA-10-D-00033, which has a copyright license in accordance with AMS 3.5-13.(c)(1).
 */

package gov.faa.ang.swac.controller.core.component;

import java.io.File;
import gov.faa.ang.swac.datalayer.MappedDataAccess;
import gov.faa.ang.swac.datalayer.DataAccessException;

//import java.io.File;
//import java.io.FilenameFilter;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Iterator;
//import java.util.LinkedHashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Map.Entry;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//import org.apache.log4j.LogManager;
//import org.apache.log4j.Logger;
//
//import gov.faa.ang.swac.changegenerator.SectorTimeCapacityDB;
//import gov.faa.ang.swac.changegenerator.datatypes.ForecastWeatherEvent;
//import gov.faa.ang.swac.changegenerator.datatypes.WeatherEvent;
//import gov.faa.ang.swac.common.datatypes.Patterns;
//import gov.faa.ang.swac.common.datatypes.Timestamp;
//import gov.faa.ang.swac.common.entities.AirportParetoCurve;
//import gov.faa.ang.swac.common.flightmodeling.ScheduleRecord;
//import gov.faa.ang.swac.common.flightmodeling.atmosphere.RawWindData;
//import gov.faa.ang.swac.common.javascript.JavaScript;
//import gov.faa.ang.swac.common.utilities.WxControlFileRecord;
//import gov.faa.ang.swac.congestion.terminalarea.TerminalAreaCongestionAirportRecord;
//import gov.faa.ang.swac.controller.core.component.TemplateImporter;
//import gov.faa.ang.swac.datalayer.DataAccessException;
//import gov.faa.ang.swac.datalayer.MappedDataAccess;
//import gov.faa.ang.swac.datalayer.ResourceManager.LOCATION;
//import gov.faa.ang.swac.datalayer.identity.FileDataDescriptor;
//import gov.faa.ang.swac.datalayer.identity.FileSetDescriptor;
//import gov.faa.ang.swac.datalayer.identity.ParameterizedDataDescriptor;
//import gov.faa.ang.swac.qroutes.trajectorymodifier.QRoutesCityPairs.QRoutesCityPairsRecord;
//import gov.faa.ang.swac.tam.ProcedureRecord;
//import gov.faa.ang.swac.weather.SuaControlFileRecord;

/**
 * This class has been commented out.
 * 
 * Finding a project home that depends on everything this class requires
 * depending on was problematic. As this class is not currently in use, it is
 * seems wiser to punt the problem for now rather than rearrange project
 * structures to accommodate an unused class.
 * 
 */
public class RegisterParameterizedFilesBean implements TemplateImporter 
{
	public void run(File dataDir, MappedDataAccess dao) throws DataAccessException {
		
	}
	
//	private static final Logger logger = LogManager.getLogger(RegisterParameterizedFilesBean.class);
//	
//	private static final String WILDCARD_TEMPLATE_PATTERN = "WILDCARD";
//	private static final String Y2K_DATE_TEMPLATE_PATTERN = "Y2K_DATE";
//	private static final String BASE_DATE_TEMPLATE_PATTERN = "BASE_DATE";
//	private static final String FORECAST_FISCAL_YEAR_TEMPLATE_PATTERN = "FORECAST_FISCAL_YEAR";
//	private static final String CLASSIFIER_TEMPLATE_PATTERN = "CLASSIFIER";
//	
//	private static final String WILDCARD_REGEX_PATTERN = Patterns.STRING;
//	private static final String Y2K_DATE_REGEX_PATTERN = "((?:[0-9][0-9])" + Patterns.DATE_MONTH + Patterns.DATE_DAY + ")";
//	private static final String BASE_DATE_REGEX_PATTERN = "(" + Patterns.DATE + ")";
//	private static final String FORECAST_FISCAL_YEAR_REGEX_PATTERN = "(" + Patterns.DATE_YEAR + ")";
//	private static final String CLASSIFIER_REGEX_PATTERN = "(" + Patterns.STRING + ")";
//	
//	private static final Pattern Y2K_DATE_REGEX = Pattern.compile(Y2K_DATE_REGEX_PATTERN);
//	private static final Pattern BASE_DATE_REGEX = Pattern.compile(BASE_DATE_REGEX_PATTERN);
//	private static final Pattern FORECAST_FISCAL_YEAR_REGEX = Pattern.compile(FORECAST_FISCAL_YEAR_REGEX_PATTERN);
//	private static final Pattern CLASSIFIER_REGEX = Pattern.compile(CLASSIFIER_REGEX_PATTERN);
//	
//	private String paretoCurvesFilenameTemplate;
//	private String sectorCapacityMatrixFilenameTemplate;
//	private String airportWeatherFilenameTemplate;
//	private String airportWeatherForecastFilenameTemplate;
//	private String baseScheduleFilenameTemplate;
//	private String sidStarDatabaseFilenameTemplate;
//	private String windFilenameTemplate;
//	
//	private String airportScriptFilenameTemplate;
//	private String qRoutesCityPairsFilenameTemplate;
//	private String weatherPredictionScheduleFilenameTemplate;
//	private String suaScheduleFilenameTemplate;
//	private String terminalAreaCongestionParametersFilenameTemplate;
//	private String airportWxParetoLookupFilenameTemplate;
//	
//	public String getParetoCurvesFilenameTemplate() {
//		return paretoCurvesFilenameTemplate;
//	}
//	public void setParetoCurvesFilenameTemplate(String paretoCurvesFilenameTemplate) {
//		this.paretoCurvesFilenameTemplate = paretoCurvesFilenameTemplate;
//	}
//	public String getSectorCapacityMatrixFilenameTemplate() {
//		return sectorCapacityMatrixFilenameTemplate;
//	}
//	public void setSectorCapacityMatrixFilenameTemplate(
//			String sectorCapacityMatrixFilenameTemplate) {
//		this.sectorCapacityMatrixFilenameTemplate = sectorCapacityMatrixFilenameTemplate;
//	}
//	public String getAirportWeatherFilenameTemplate() {
//		return airportWeatherFilenameTemplate;
//	}
//	public void setAirportWeatherFilenameTemplate(
//			String airportWeatherFilenameTemplate) {
//		this.airportWeatherFilenameTemplate = airportWeatherFilenameTemplate;
//	}
//	public String getAirportWeatherForecastFilenameTemplate() {
//		return airportWeatherForecastFilenameTemplate;
//	}
//	public void setAirportWeatherForecastFilenameTemplate(
//			String airportWeatherForecastFilenameTemplate) {
//		this.airportWeatherForecastFilenameTemplate = airportWeatherForecastFilenameTemplate;
//	}
//	public String getBaseScheduleFilenameTemplate() {
//		return baseScheduleFilenameTemplate;
//	}
//	public void setBaseScheduleFilenameTemplate(String baseScheduleFilenameTemplate) {
//		this.baseScheduleFilenameTemplate = baseScheduleFilenameTemplate;
//	}
//	public String getSidStarDatabaseFilenameTemplate() {
//		return sidStarDatabaseFilenameTemplate;
//	}
//	public void setSidStarDatabaseFilenameTemplate(
//			String sidStarDatabaseFilenameTemplate) {
//		this.sidStarDatabaseFilenameTemplate = sidStarDatabaseFilenameTemplate;
//	}
//	public String getWindFilenameTemplate() {
//		return windFilenameTemplate;
//	}
//	public void setWindFilenameTemplate(String windFilenameTemplate) {
//		this.windFilenameTemplate = windFilenameTemplate;
//	}
//	
//	public String getAirportScriptFilenameTemplate() {
//		return airportScriptFilenameTemplate;
//	}
//	public void setAirportScriptFilenameTemplate(
//			String airportScriptFilenameTemplate) {
//		this.airportScriptFilenameTemplate = airportScriptFilenameTemplate;
//	}
//	public String getqRoutesCityPairsFilenameTemplate() {
//		return qRoutesCityPairsFilenameTemplate;
//	}
//	public void setqRoutesCityPairsFilenameTemplate(
//			String qRoutesCityPairsFilenameTemplate) {
//		this.qRoutesCityPairsFilenameTemplate = qRoutesCityPairsFilenameTemplate;
//	}
//	public String getWeatherPredictionScheduleFilenameTemplate() {
//		return weatherPredictionScheduleFilenameTemplate;
//	}
//	public void setWeatherPredictionScheduleFilenameTemplate(String weatherPredictionScheduleFilenameTemplate) {
//		this.weatherPredictionScheduleFilenameTemplate = weatherPredictionScheduleFilenameTemplate;
//	}
//	public String getSuaScheduleFilenameTemplate() {
//		return suaScheduleFilenameTemplate;
//	}
//	public void setSuaScheduleFilenameTemplate(String suaScheduleFilenameTemplate) {
//		this.suaScheduleFilenameTemplate = suaScheduleFilenameTemplate;
//	}
//	public String getTerminalAreaCongestionParametersFilenameTemplate() {
//		return terminalAreaCongestionParametersFilenameTemplate;
//	}
//	public void setTerminalAreaCongestionParametersFilenameTemplate(String terminalAreaCongestionParametersFilenameTemplate) {
//		this.terminalAreaCongestionParametersFilenameTemplate = terminalAreaCongestionParametersFilenameTemplate;
//	}
//	public String getAirportWxParetoLookupFilenameTemplate() {
//		return this.airportWxParetoLookupFilenameTemplate;
//	}
//	public void setAirportWxParetoLookupFilenameTemplate(String airportWxParetoLookupFilenameTemplate) {
//		this.airportWxParetoLookupFilenameTemplate = airportWxParetoLookupFilenameTemplate;
//	}
//	
//	/* (non-Javadoc)
//	 * @see gov.faa.ang.swac.controller.core.component.TemplateImporter#run(java.io.File, gov.faa.ang.swac.datalayer.MappedDataAccess)
//	 */
//	@Override
//	public void run(File dataDir, MappedDataAccess dao) throws DataAccessException
//	{
//		List<FileMetadata> files = filter(dataDir, paretoCurvesFilenameTemplate);
//        registerGenericFiles(files, AirportParetoCurve.class, dao);
//        registerGenericFiles(files, gov.faa.ang.swac.common.entities.AirportParetoCurve.class, dao);
//		
//		files = filter(dataDir, sectorCapacityMatrixFilenameTemplate);
//		registerGenericFiles(files, SectorTimeCapacityDB.class, dao);
//		
//		files = filter(dataDir, airportWeatherFilenameTemplate);
//		registerGenericFiles(files, WeatherEvent.class, dao);
//		
//		files = filter(dataDir, airportWeatherForecastFilenameTemplate);
//		registerGenericFiles(files, ForecastWeatherEvent.class, dao);
//		
//		files = filter(dataDir, baseScheduleFilenameTemplate);
//		registerGenericFiles(files, ScheduleRecord.class, dao);
//		
//		files = filter(dataDir, sidStarDatabaseFilenameTemplate);
//		registerSidStarFiles(files, dao);
//		
//		files = filter(dataDir, windFilenameTemplate);
//		registerWindFiles(files, dao);
//		
//		files = filter(dataDir, airportScriptFilenameTemplate);
//		registerGenericFiles(files, JavaScript.class, dao);
//		
//		files = filter(dataDir, qRoutesCityPairsFilenameTemplate);
//		registerGenericFiles(files, QRoutesCityPairsRecord.class, dao);
//		
//		files = filter(dataDir, weatherPredictionScheduleFilenameTemplate);
//		registerGenericFiles(files, WxControlFileRecord.class, dao);
//		
//		files = filter(dataDir, suaScheduleFilenameTemplate);
//		registerGenericFiles(files, SuaControlFileRecord.class, dao);
//		
//		files = filter(dataDir, terminalAreaCongestionParametersFilenameTemplate);
//		registerGenericFiles(files, TerminalAreaCongestionAirportRecord.class, dao);
//	}
//	
//	/**
//	 * When a single pattern match is intended to be associated with a single input file, call registerGenericFiles to generate 
//	 * DataDescriptors from the metadata and register with the dao for future querying.
//	 * @param files
//	 * @param dataType
//	 * @param dao
//	 * @throws DataAccessException
//	 */
//	private void registerGenericFiles(List<FileMetadata> files, Class<?> dataType, MappedDataAccess dao) throws DataAccessException
//	{
//		for (FileMetadata meta : files)
//		{
//			FileDataDescriptor f = createFileDataDescriptor(meta, dataType);
//			ParameterizedDataDescriptor d = createParameterizedDataDescriptor(meta, f);
//			dao.getMarshaller(d);
//		}
//	}
//	
//	/**
//	 * SID/STAR files are a special case, where the date in the file is not intended to match the base date for the scenario. Instead,
//	 * they partition the year. Each scenario base date is associated with the latest-dated SID/STAR file before the scenario date (or equal).
//	 * To handle the cardinality mismatch, additional metadata is generated for every day and registered with the appropriate SID/STAR file.
//	 * @param files
//	 * @param dao
//	 * @throws DataAccessException
//	 */
//	private void registerSidStarFiles(List<FileMetadata> files, MappedDataAccess dao) throws DataAccessException
//	{
//		Class<?> dataType = ProcedureRecord.class;
//		
//		// Sort the data in order to define partition
//		Collections.sort(files);
//		
//		Iterator<FileMetadata> iter = files.iterator(); 
//		FileMetadata lastMeta = null;
//		FileMetadata meta = null;
//		while (iter.hasNext())
//		{
//			lastMeta = meta;
//			meta = iter.next();
//			if (lastMeta != null)
//			{
//				// Valid date pair: register lastMeta metadata to every date in-between 
//				for (Timestamp t = lastMeta.baseDate; t.before(meta.baseDate); t = t.dayAdd(1))
//				{
//					FileMetadata newMeta = new FileMetadata(lastMeta);
//					newMeta.baseDate = t;
//					FileDataDescriptor f = createFileDataDescriptor(newMeta, dataType);
//					ParameterizedDataDescriptor d = createParameterizedDataDescriptor(newMeta, f);
//					dao.getMarshaller(d);
//				}
//			}
//			if (!iter.hasNext())
//			{
//				// Last SID/STAR file: apply this to the 28 day period following
//				for (int i = 0; i < 28; i++)
//				{
//					FileMetadata newMeta = new FileMetadata(meta);
//					newMeta.baseDate = meta.baseDate.dayAdd(i);
//					FileDataDescriptor f = createFileDataDescriptor(newMeta, dataType);
//					ParameterizedDataDescriptor d = createParameterizedDataDescriptor(newMeta, f);
//					dao.getMarshaller(d);
//				}
//			}
//		}
//	}
//	
//	/**
//	 * Sorts wind files into date-coded buckets and registers each file set according to date, since there is a cardinality mismatch whereby a
//	 * single base date may be associated with more than one file. Contiguous segments of days may refer to the same base date, so these occurences
//	 * are searched for and merged.
//	 * 
//	 * TODO: does not account for variations in classifier or forecast fiscal year
//	 * 
//	 * @param files
//	 * @param dao
//	 * @throws DataAccessException
//	 */
//	private void registerWindFiles(List<FileMetadata> files, MappedDataAccess dao) throws DataAccessException
//	{
//		Class<?> dataType = RawWindData.class;
//		
//		// Sort the input data chronologically to make merging into adjacent days easier to handle
//		Collections.sort(files);
//		
//		Map<Timestamp,FileSetDescriptor> fileSets = new LinkedHashMap<Timestamp,FileSetDescriptor>();
//		for (FileMetadata meta : files)
//		{
//			if (!fileSets.containsKey(meta.baseDate))
//			{
//				FileSetDescriptor descriptor = new FileSetDescriptor();
//				descriptor.setDataType(dataType);
//				descriptor.setLocation(LOCATION.DATA);
//				descriptor.setReadOnly(true);
//				
//				// If adjacent to previous entry, merge files forward
//				FileSetDescriptor preDesc = fileSets.get(meta.baseDate.dayAdd(-1));
//				if (preDesc != null)
//				{
//					descriptor.setResourceNames(new ArrayList<String>(preDesc.getResourceNames()));
//				}
//				else
//				{
//					descriptor.setResourceNames(new ArrayList<String>());
//				}
//				
//				fileSets.put(meta.baseDate, descriptor);
//			}
//			fileSets.get(meta.baseDate).getResourceNames().add(meta.fileName);
//			
//			// Merge each new entry backward to contiguous prior dates
//			Timestamp preDate = meta.baseDate;
//			FileSetDescriptor desc = null;
//			while ((desc = fileSets.get((preDate = preDate.dayAdd(-1)))) != null)
//			{
//				desc.getResourceNames().add(meta.fileName);
//			}
//		}
//				
//		logger.debug("All contiguous dates should have the same number of wind files...");
//		for (Entry<Timestamp,FileSetDescriptor> entry : fileSets.entrySet())
//		{
//			logger.debug(entry.getKey().toString() + ": " + entry.getValue().getResourceNames().size());
//			
//			ParameterizedDataDescriptor d = createParameterizedDataDescriptor(new FileMetadata(entry.getKey(), null, null, null), entry.getValue());
//			dao.getMarshaller(d);
//		}
//	}
//		
//	/**
//	 * Wraps a FileDataDescriptor with a ParameterizedDataDescriptor, and applies query parameters from FileMetadata
//	 * @param meta
//	 * @param f
//	 * @return
//	 */
//	private static ParameterizedDataDescriptor createParameterizedDataDescriptor(FileMetadata meta, FileDataDescriptor f)
//	{
//		ParameterizedDataDescriptor p = new ParameterizedDataDescriptor();
//		p.setBaseDescriptor(f);
//		p.setDataType(f.getDataType());
//		p.setBaseDate(meta.baseDate);
//		p.setForecastFiscalYear(meta.forecastFiscalYear);
//		p.setClassifier(meta.classifier);
//		
//		return p;
//	}
//	
//	/**
//	 * Translates metadata into a FileDataDescriptor
//	 * @param meta
//	 * @param dataType
//	 * @return
//	 */
//	private static FileDataDescriptor createFileDataDescriptor(FileMetadata meta, Class<?> dataType)
//	{
//		FileDataDescriptor f = new FileDataDescriptor();
//		f.setReadOnly(true);
//		f.setDataType(dataType);
//		f.setLocation(LOCATION.DATA);
//		f.setResourceName(meta.fileName);
//		
//		return f;
//	}
//	
//	/**
//	 * Simple container class for common query metadata to be associated with files 
//	 *
//	 */
//	private static class FileMetadata implements Comparable<FileMetadata>
//	{
//		private Timestamp baseDate;
//		private Integer forecastFiscalYear;
//		private String classifier;
//		private String fileName;
//		
//		private FileMetadata()
//		{
//			super();
//		}
//				
//		private FileMetadata(Timestamp baseDate, Integer forecastFiscalYear,
//				String classifier, String fileName) {
//			super();
//			this.baseDate = baseDate;
//			this.forecastFiscalYear = forecastFiscalYear;
//			this.classifier = classifier;
//			this.fileName = fileName;
//		}
//
//		private FileMetadata(FileMetadata source) {
//			this(source.baseDate, source.forecastFiscalYear, source.classifier, source.fileName);
//		}
//
//		@Override
//		public int compareTo(FileMetadata o) {
//			return baseDate.compareTo(o.baseDate);
//		}
//	}
//	
//	/**
//	 * Translates a user-friendly text pattern into a regex pattern, and filters for matching files in the directory.
//	 * Metadata from the patterns are assembled and associated with the file name
//	 * @param dataDir
//	 * @param template
//	 * @return
//	 */
//	private static List<FileMetadata> filter(File dataDir, String template)
//	{
//		List<FileMetadata> files = new ArrayList<FileMetadata>();
//		
//		String regexStr = template.replace(Y2K_DATE_TEMPLATE_PATTERN, Y2K_DATE_REGEX_PATTERN)
//										.replace(BASE_DATE_TEMPLATE_PATTERN, BASE_DATE_REGEX_PATTERN)
//										.replace(FORECAST_FISCAL_YEAR_TEMPLATE_PATTERN, FORECAST_FISCAL_YEAR_REGEX_PATTERN)
//										.replace(CLASSIFIER_TEMPLATE_PATTERN, CLASSIFIER_REGEX_PATTERN)
//										.replace(WILDCARD_TEMPLATE_PATTERN, WILDCARD_REGEX_PATTERN);
//		final Pattern regex = Pattern.compile(regexStr);
//		
//		FilenameFilter templateFilter = new FilenameFilter() {
//			public boolean accept(File dir, String name) {
//				return regex.matcher(name).matches();
//			}
//		};
//		
//		File[] matches = dataDir.listFiles(templateFilter);
//		if (matches.length > 0)
//		{
//			for (File f : matches)
//			{
//				FileMetadata meta = new FileMetadata();
//				meta.fileName = f.getName();
//				
//				Matcher m = regex.matcher(meta.fileName);
//				if (!m.matches())
//				{
//					throw new RuntimeException("Error filtering files: " + meta.fileName + " passed the FileName filter but its metadata could not be extracted.");
//				}
//				
//				boolean containsY2kDate = template.contains(Y2K_DATE_TEMPLATE_PATTERN);
//				boolean containsBaseDate = template.contains(BASE_DATE_TEMPLATE_PATTERN);
//				boolean containsForecastFy = template.contains(FORECAST_FISCAL_YEAR_TEMPLATE_PATTERN);
//				boolean containsClassifier = template.contains(CLASSIFIER_TEMPLATE_PATTERN);
//				
//				for (int i = 1; i <= m.groupCount(); i++)
//				{
//					String group = m.group(i);
//					// Match on the most restrictive patterns first, and only capture zero or once
//					if (containsBaseDate && BASE_DATE_REGEX.matcher(group).matches())
//					{
//						meta.baseDate = Timestamp.myValueOf(group);
//						containsBaseDate = false;
//					}
//					else if (containsY2kDate && Y2K_DATE_REGEX.matcher(group).matches())
//					{
//						meta.baseDate = Timestamp.myValueOf("20" + group);
//						containsY2kDate = false;
//					}
//					else if (containsForecastFy && FORECAST_FISCAL_YEAR_REGEX.matcher(group).matches())
//					{
//						meta.forecastFiscalYear = Integer.parseInt(group);
//						containsForecastFy = false;
//					}
//					else if (containsClassifier && CLASSIFIER_REGEX.matcher(group).matches())
//					{
//						meta.classifier = group;
//						containsClassifier = false;
//					}
//				}
//				
//				files.add(meta);
//			}
//		}
//		
//		return files;
//	}
}
