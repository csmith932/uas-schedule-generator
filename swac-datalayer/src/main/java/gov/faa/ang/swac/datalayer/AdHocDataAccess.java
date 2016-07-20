/**
 * Copyright "TBD", Metron Aviation & CSSI.  All rights reserved.
 * 
 * This computer Software was developed with the sponsorship of the U.S. Government
 * under Contract No. DTFAWA-10-D-00033, which has a copyright license in accordance with AMS 3.5-13.(c)(1).
 */

package gov.faa.ang.swac.datalayer;

import gov.faa.ang.swac.datalayer.ResourceManager.LOCATION;
import gov.faa.ang.swac.datalayer.storage.fileio.OutputRecord;
import gov.faa.ang.swac.datalayer.storage.fileio.TextSerializable;
import gov.faa.ang.swac.datalayer.storage.fileio.WithHeader;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * Provides static methods for "emergency" access to the file system, such as debug data dumps or logging. This should not be used
 * for normal data access needs
 * 
 * @author csmith
 *
 */
public class AdHocDataAccess
{
	private static final Logger logger = LogManager.getLogger(AdHocDataAccess.class);
	
	private static ResourceManager resMan;
	private static LogLevel logLevel = LogLevel.NONE;

	public enum LogLevel { NONE, VERBOSE, DEBUG}
	
	/*
	 * This is not a recommended way to access the ResourceManager.
	 * Preserved for debugging purposes.
	 */
	@Deprecated
	public static ResourceManager getResourceManager()
	{
		return resMan;
	}

	public static void setResourceManager(ResourceManager resMan)
	{
		AdHocDataAccess.resMan = resMan;
	}
	
	public static LogLevel getLogLevel() {
		return logLevel;
	}

	public static void setLogLevel(LogLevel logLevel) {
		AdHocDataAccess.logLevel = logLevel;
	}

	public static void printReport(String reportName, String reportContent)
	{
		printReport(reportName, reportContent, false);
	}
	
	public static void printReport(String reportName, String reportContent, boolean append)
	{
		PrintWriter writer = null;
		try
		{
			writer = getWriter(LOCATION.REPORT, reportName, append);
                        
			if (writer == null) { return; }
                        
			writer.write(reportContent);
		} 
		catch (Exception ex)
		{
			// Failure to dump an ad hoc report should not interfere with normal program operation.
		}
		finally
		{
			try
			{
                if (writer != null) {
					writer.flush();
					writer.close();
                }
			} catch (Exception ex) {}
		}
	}
	
	public static PrintWriter getWriter(String reportName)
	{
		return getWriter(LOCATION.REPORT, reportName);
	}

	public static PrintWriter getWriter(LOCATION location, String reportName)
	{
		return getWriter(location, reportName, false);
	}

	/**
	 * Always returns a PrintWriter, never returns null.  If SWAC reporting is not enabled, a dummy/do nothing writer will be returned.
	 * 
	 * @return
	 */
	public static PrintWriter getWriterAlways(LOCATION location, String reportName)
	{
		PrintWriter pw = getWriter(location, reportName, false, !logLevel.equals(LogLevel.NONE));
		if (pw == null)
			pw = new PrintWriter(new DummyWriter());
		return pw;
	}
	
	public static PrintWriter getWriter(LOCATION location, String reportName, boolean append) {
		return getWriter(location, reportName, append, !logLevel.equals(LogLevel.NONE));
	}
	
	/**
	 * Always returns a PrintWriter, never returns null.  If SWAC reporting is not enabled, a dummy/do nothing writer will be returned.
	 * 
	 * @return
	 */
	public static PrintWriter getWriterAlways(LOCATION location, String reportName, boolean append)
	{
		PrintWriter pw = getWriter(location, reportName, append, !logLevel.equals(LogLevel.NONE));
		if (pw == null)
			pw = new PrintWriter(new DummyWriter());
		return pw;
	}
	
	public static PrintWriter getWriter(LOCATION location, String reportName, boolean append, boolean overrideLogLevel) {
		if (!overrideLogLevel)
			return null;
		
		PrintWriter writer = null;
		try
		{
			writer = new PrintWriter(resMan.openOutput(location, reportName, append), append);
			return writer;
		} 
		catch (Exception ex)
		{
			// Failure to dump an ad hoc report should not interfere with normal program operation.
            logger.warn("Failed to open file: " + location + File.separator + reportName);
			return null;
		}
	}
	
	public static void dumpData(String source, List<?> data)
	{
		dumpData(source, data, false);
	}
	
	public static void dumpData(String source, List<?> data, boolean overrideLogLevel)
	{
		if (data == null || data.size() == 0)
		{
			return;
		}
		if(data.get(0) instanceof OutputRecord)
		{
			return;// Avoid creating output record files in report folder 
		}
		
		Class<?> clazz = data.get(0).getClass();
		String dumpFileName = source + "_" + clazz.getSimpleName() + ".dmp";
		PrintWriter writer = null;
		try
		{
			writer = getWriter(LOCATION.REPORT, dumpFileName, false, overrideLogLevel);
                        
            if (writer == null) { return; }
                        
            if (data.get(0) instanceof WithHeader) {
            	((WithHeader)data.get(0)).writeHeader(writer, data.size());
            }
			for (Object item : data)
			{
				if (item instanceof TextSerializable)
				{
					((TextSerializable)item).writeItem(writer);
				}
				else
				{
					writer.println(item.toString());
				}
			}
		} 
		catch (Exception ex)
		{
			// Failure to dump should not interfere with normal program operation.
			logger.debug("Data dump failure", ex);
		}
		finally
		{
			try {
				if (writer != null) {
					writer.flush();
					writer.close();
				}
			} catch (Exception ex) {
			}
		}
	}
	
	private static class DummyWriter extends Writer {
		public void write(char[] cbuf, int off, int len) throws IOException {
		}

		public void flush() throws IOException {
		}

		public void close() throws IOException {
		}
	}
}
