/**
 * Copyright "TBD", Metron Aviation & CSSI.  All rights reserved.
 * 
 * This computer Software was developed with the sponsorship of the U.S. Government
 * under Contract No. DTFAWA-10-D-00033, which has a copyright license in accordance with AMS 3.5-13.(c)(1).
 */

package gov.faa.ang.swac.datalayer.deprecated;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.zip.GZIPInputStream;

/**
 * @author sroberts
 */
@Deprecated
public class ResourceManager
{
    public static final String PROGRAM_NAME = "ResourceManager";
    public static final String VERSION = "2.0.alpha";

    private static org.apache.log4j.Logger logger = org.apache.log4j.LogManager.getLogger(ResourceManager.class);

     /** 
     * 
     */
    static private ResourceManager resMan;
    
    /**
     * Some default properties
     */
    @SuppressWarnings("unused")
	private String batchName;
    @SuppressWarnings("unused")
	private String scenarioName;
    private String dataDirectory;
    private String logDirectory;
    private String tempDirectory;
    private String testDirectory;
    private String outputDirectory;
    private String reportDirectory;
    private String homeDirectory;
    private String workDirectory;

    /*
     * Various constructors and creators
     */

    /**
     * Constructor
     * 
     * @param batchName
     * @param scenarioName
     */
    public ResourceManager(String newBatchName, String newScenarioName)
    {
        // set our scenario & batch name and our scenarioPath
        this.scenarioName = newScenarioName;
        this.batchName = newBatchName;
        
        // initialize our directories
        String defaultDir = System.getProperty("user.dir");
        this.dataDirectory = defaultDir;
        this.logDirectory = defaultDir;
        this.tempDirectory = defaultDir;
        this.testDirectory = defaultDir;
        this.outputDirectory = defaultDir;
        this.reportDirectory = defaultDir;
        this.homeDirectory = defaultDir;
        this.workDirectory = defaultDir;
    }
    

    /**
     * Create the resourceManager for a given scenario
     * This will create the resourceManager and initialize all the settings
     * 
     * @param batchName
     * @param scenarioName
     * @return instance of ResMan
     */
    public static ResourceManager create(String newBatchName, String newScenarioName)

    {
        resMan = new ResourceManager(newBatchName, newScenarioName);
        return resMan;
    }

    /**
     * Sets the current ResourceManager. Primarily used by the Scenario Engine to set a new scenario
     * 
     * @param resMan
     * @return
     */
    public static void setCurrent(ResourceManager resMan)
    {
        ResourceManager.resMan = resMan;
    }

    /**
     * get this resource manager
     * 
     * @return ResourceManager instance
     */
    public static ResourceManager getCurrent()
    {
        if (resMan == null)
        {
            resMan = new ResourceManager("", null);
        }
        return resMan;
    }
    
    /*
     * Basic existence checks
     */

    /**
     * Tests for file existence Helper function for validation methods
     * 
     * @param pathname A String containing the full pathname of the file to test for
     * @return true if the file exists 
     */
    public boolean rmcheckFileExists(String pathname)
    {
    	File f = this.rmgetFile(pathname);
    	return f == null ? false : f.exists();
    } // checkFileExists

    /*
     * Functions to get Readers
     */

    /**
     * Basic file getter
     * uses the following URIs to determine where to look for the file
     * 
     * PREFIX:PATH
     * where Prefix is one of the following and PATH is the path relative to the expanded path provided by the PREFIX
     * 
     * report:	look in NASPAC_WORK/report/BATCH_NAME/SCENARIO_NAME/
     * temp:	look in NASPAC_WORK/temp
     * output:	look in NASPAC_WORK/output/BATCH_NAME/SCENARIO_NAME/
     * log:		look in NASPAC_WORK/log/BATCH_NAME/SCENARIO_NAME/
     * test:	look in NASPAC_WORK/test/BATCH_NAME/SCENARIO_NAME/
     * data:	look in NASPAC_WORK/data
     * file:	treat the rest as a filepath
     * 
     * all other pathnames will be searched for normally     * 
     * 
     * 
     * 
     * @param pathname A String containing a URI or the full pathname of the file
     * @return the File
     */
    public File rmgetFile(String pathname)
    {
        URL url = this.rmGetURL(pathname);
        try 
		{
        	return url == null ? null : new File(url.toURI());
		} 
		catch (URISyntaxException ex) 
		{
			return null;
		}
    } // rmgetFile
	
    public enum SCHEME { report, temp, output, log, test, data, work }
    
    public URL rmGetURL(String pathname)
    {
    	URL url = null;
    	
    	// First try to resolve it directly to the file system, which will fail if it is a URI
    	url = this.getURLFromFilePath(pathname);
    	// If it works, return immediately
    	if (url != null) { return url; }
    	
    	// Parse the path as a simple URI - "location:resourceName"
		URI path;
		String schemeStr = "";
		String resourceName = "";
		try 
		{
			path = new URI(pathname);
			schemeStr = path.getScheme();
			if (schemeStr == null) 
			{ 
				// Default to NASPAC_WORK when no scheme is specified
				schemeStr = "work"; 
			}
			else if (schemeStr.equals("file"))
			{
				// This is already an absolute file URI path. Do not resolve further
				try 
				{
					return path.toURL();
				} 
				catch (MalformedURLException ex) 
				{
					// A corrupt file system URI is not recoverable
					logger.info("Invalid request for Resource - malformed URL: " + pathname);
					return null;
				}
			}
			resourceName = path.getSchemeSpecificPart();
			if (resourceName == null) 
			{ 
				resourceName = ""; 
			}
		} 
		catch (URISyntaxException ex) 
		{
			// Bad URI naming. Try to recover for known exceptions or return null if not possible
			
			int separator = pathname.indexOf(":");
			
			if (separator >= 0 && separator == pathname.length() - 1)
			{
				// Scheme only but no resource name represents the root directory path of a named location
				// Example: "temp:"
				schemeStr = pathname.substring(0, separator);
			}
			else
			{
				logger.info("Invalid request for Resource - URI Syntax Error: " + pathname);
				return null;
			}
		}
		
		String rootPath = "";
		
		// Resolve scheme to an acceptable enumeration value
		SCHEME scheme = null;
		try
		{
			scheme = SCHEME.valueOf(schemeStr);
		}
		catch (IllegalArgumentException ex)
		{
			// not in the list = invalid
			logger.info("Invalid request for Resource: " + pathname);
			return null;
		}
	
		// Resolve scheme to path string
		switch (scheme)
		{
			case report:
				rootPath = this.reportDirectory;
				break;
			case temp:
				rootPath = this.tempDirectory;
				break;
			case output:
				rootPath = this.outputDirectory;
				break;
			case log:
				rootPath = this.logDirectory;
				break;
			case test:
				rootPath = this.testDirectory;
				break;
			case data:
				rootPath = this.dataDirectory;
				break;
			case work:
				rootPath = this.workDirectory;
				break;
		}
		
		// Assemble full resource path
		String truePath = rootPath.isEmpty() ? resourceName : rootPath + File.separator + resourceName;
		
		// Try resolution from relative location in classpath (this only works when rootPath is a relative location on the classpath (e.g. "data")
		ClassLoader loader = ResourceManager.class.getClassLoader();
		url = loader.getResource(truePath);
		
		// Try resolution from file system (this works for absolute file paths)
		if (url == null)
		{
			url = getURLFromFilePath(truePath, true);
		}
		
		return url;
    } // rmGetURL
    
    /**
     * If pathname represents an absolute file path, then it is converted into a URL. Otherwise return null.
     * @param pathname
     * @return
     */
    private URL getURLFromFilePath(String pathname)
    {
    	return getURLFromFilePath(pathname, false);
    }
   
    /**
     * If pathname represents a file path, then it is converted into a URL. Otherwise return null.
     * @param pathname
     * @param allowRelative
     * @return
     */
    private URL getURLFromFilePath(String pathname, boolean allowRelative)
    {
    	try
    	{
    		File f = new File(pathname);
    		// If the path is not absolute, there is no guarantee that it was intended to be a file path and not a URI
    		if (allowRelative || f.isAbsolute())
    		{
    			return f.toURI().toURL();
    		}
    	}
    	catch (MalformedURLException ex) 
		{

		}
    	
		return null;
    }
    
	/**
	 * Returns a BuffererdReader for a given filename if pathname ends with .gz,
	 * assume the file is compressed and uncompress it
	 * 
	 * @param pathname
	 *            A String containing the full pathname of the file
	 * @return BuffererdReader for the file
	 */
    public BufferedReader rmgetBufferedReader(String pathname) throws IOException
    {
    	BufferedReader bufferedReader =null;
        if (pathname.endsWith(".gz") || pathname.endsWith(".GZ"))
        {
            bufferedReader = rmgetBufferedReader(new GZIPInputStream(rmGetURL(pathname).openStream()));
        } else {
            bufferedReader = rmgetBufferedReader(rmGetURL(pathname).openStream());
        }
        return bufferedReader;    
    } // getBufferedReader

    /**
     * Returns a BuffererdReader for a given inputStream
     * 
     * @param pathname A String containing the full pathname of the file
     * @return BuffererdReader for the file
     */
    private BufferedReader rmgetBufferedReader(InputStream stream)
    {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));

        return bufferedReader;
    } // getBufferedReader

    /*
     * Writers
     */

    /**
     * Returns a PrintWriter for a given filename
     * 
     * @param pathname A String containing the full pathname of the file
     * @return PrintWriter for the file
     */
    public PrintWriter rmgetPrintWriter(String pathname) throws IOException
    {
        return rmgetPrintWriter(pathname, false);
    } // getPrintWriter

    /**
     * Returns a PrintWriter for a given filename with append option
     * autoflushing is enabled
     * 
     * @param pathname A String containing the full pathname of the file
     * @param append Boolean Append? Y/N
     * @return PrintWriter for the file
     */
    public PrintWriter rmgetPrintWriter(String pathname, Boolean append) throws IOException
    {
    	File f = rmgetFile(pathname);
    	File dir = f.getParentFile();
    	if (dir != null)
    	{
    		dir.mkdirs();
    	}
        PrintWriter printWriter = new PrintWriter(new BufferedWriter(new FileWriter(f,
                append)), true);

        return printWriter;
    } // getPrintWriter

    /*
     * Getters and setters
     */
    public void setDataDirectory(String dataDirectory)
    {
        this.dataDirectory = dataDirectory;
    }
    public String getDataDirectory()
    {
        return this.dataDirectory;
    }

    public void setLogDirectory(String logDirectory)
    {
        this.logDirectory = logDirectory;
    }
    public String getLogDirectory()
    {
        return this.logDirectory;
    }

    public void setOutputDirectory(String outputDirectory)
    {
        this.outputDirectory = outputDirectory;
    }
    public String getOutputDirectory()
    {
        return this.outputDirectory;
    }

    public void setReportDirectory(String reportDirectory)
    {
        this.reportDirectory = reportDirectory;
    }
    public String getReportDirectory()
    {
        return this.reportDirectory;
    }

    public void setTempDirectory(String tempDirectory)
    {
        this.tempDirectory = tempDirectory;
    }
    public String getTempDirectory()
    {
        return this.tempDirectory;
    }

    public void setTestDirectory(String testDirectory)
    {
        this.testDirectory = testDirectory;
    }
    public String getTestDirectory()
    {
        return this.testDirectory;
    }

    public void setHomeDirectory(String homeDirectory)
    {
        this.homeDirectory = homeDirectory;
    }

    public String getHomeDirectory()
    {
        return this.homeDirectory;
    }

    public void setWorkDirectory(String workDirectory)
    {
        this.workDirectory = workDirectory;
    }

    public String getWorkDirectory()
    {
        return this.workDirectory;
    }

} // class ResourceManager
