/**
 * Copyright "TBD", Metron Aviation & CSSI.  All rights reserved.
 * 
 * This computer Software was developed with the sponsorship of the U.S. Government
 * under Contract No. DTFAWA-10-D-00033, which has a copyright license in accordance with AMS 3.5-13.(c)(1).
 */

package gov.faa.ang.swac.datalayer.storage.fileio;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import gov.faa.ang.swac.datalayer.DataAccessException;
import gov.faa.ang.swac.datalayer.storage.DataMarshallerBase;

/**
 * FileMarshaller moves data to/from files using the ResourceManager to obtain streams and custom XXSerializable
 * implementations to format/parse individual records. Multiple types of serialization are supported, with preference
 * being given first to TextSerializable, then to StreamSerializable, and finally to default Serializable. If none
 * of these interfaces is implemented, then conversion to/from file format is not well defined and exceptions will be thrown.
 * 
 * @author csmith
 *
 */
public class FileMarshaller extends DataMarshallerBase
{
	private static final Logger logger = LogManager.getLogger(FileMarshaller.class);
	
	private enum DATA_VALIDATION_STATE{
		NOT_VALIDATED,
		VALID,
		INVALID
	}
	
	private boolean withHeader;
	private boolean withFooter;
	private File file;
	private boolean faultTolerant;
	
	// These flags indicate which serializable interfaces are implemented
	private boolean text;
	private boolean stream;
	private boolean binary;
	private String schemaName;
	
	private DATA_VALIDATION_STATE existenceValidated=DATA_VALIDATION_STATE.NOT_VALIDATED;
	private DATA_VALIDATION_STATE schemaValidated=DATA_VALIDATION_STATE.NOT_VALIDATED;
	private DATA_VALIDATION_STATE dataValidated=DATA_VALIDATION_STATE.NOT_VALIDATED;
	
	public FileMarshaller(Class<?> clazz, File file)
	{
		this(clazz, file, false,null);
	}
	
	public FileMarshaller(Class<?> clazz, File file, boolean faultTolerant,String schemaName)
	{
		super(clazz, file.getName());
		this.file = file;
		this.faultTolerant = faultTolerant;
		this.schemaName=schemaName;
		// Throws exceptions for validation errors
		// Sets withHeader and withFooter fields
		validateClass();
	}
	
	private final void validateClass()
	{
		// Determine whether the the class is TextSerializable and also whether implements the WithHeader and/or WithFooter interfaces.
		// These mark the class for auxiliary processing at the beginning and end of serialization
		Class<?>[] interfaces = clazz.getInterfaces();
		boolean valid = false;
		for (Class<?> i : interfaces)
		{
			if (i.equals(StreamSerializable.class))
			{
				valid = true;
				this.stream = true;
			}
			
			if (i.equals(Serializable.class))
			{
				valid = true;
				this.binary = true;
			}
			
			if (i.equals(TextSerializable.class))
			{
				valid = true;
				this.text = true;
			}
			
			if (i.equals(WithHeader.class))
			{
				valid = true;
				this.withHeader = true;
			}
			
			if (i.equals(WithFooter.class))
			{
				valid = true;
				this.withHeader = true;
				this.withFooter = true;
				// Both interfaces implemented (WithFooter is a sub-interface of WithHeader)
			}
		}
		
		if (!valid)
		{
			throw new IllegalArgumentException("Error: Attempt to create FileMarshaller with incompatible class");
		}
	}

	
	private void validateSchemaInternal() throws DataAccessException{

		if(schemaName==null){
			return; //no schema, no problem...
		}
		//Verify that schema file exists...
		final String fullSchemaFilePath=this.file.getAbsolutePath(); //Hey look, it's truly OS-agnostic!
		
		String filePath = fullSchemaFilePath.substring(0,fullSchemaFilePath.lastIndexOf(File.separatorChar))+File.separatorChar+schemaName;		
		
		File schemaFile=new File(filePath);
		
		if(schemaFile.exists()){
			//Are we validating an XML file or a CSV file, or what?  Apply the appropriate validator to the named resource...
			logger.debug("Validating " + this.file.getName() + " against schema: " + this.schemaName);
			String name=file.getName();
			String extension=name.substring(name.lastIndexOf("."));
			
			if(extension.equalsIgnoreCase(".xml")){

				XmlValidator validator=new XmlValidator();

				try {
					validator.validate(filePath,file);
				} catch (DataAccessException e) {
					schemaValidated=DATA_VALIDATION_STATE.INVALID;
					throw e;
				}
				schemaValidated=DATA_VALIDATION_STATE.VALID;
				
			}else if(extension.equalsIgnoreCase(".csv")){
				//TODO CSV validation
			}else if(extension.equalsIgnoreCase(".txt")){
				//TODO TXT validation -- maybe?
			}
			
		}else{
			schemaValidated=DATA_VALIDATION_STATE.INVALID;
			throw new DataAccessException("Error: Schema file not found (\""+filePath+"\").");
		}


		
	}
	
	public String getFileName(){
		return this.file.getName();
	}
	
	@Override 
	public final boolean exists()
	{
		return this.file.exists() || this.faultTolerant;
	}
	
	@Override
	public final <T> void loadInternal(List<T> output) throws DataAccessException 
	{
		// This will throw a runtime exception if there is a type mismatch
		validateParameterizedType(output);

		if(schemaName!=null && schemaValidated==DATA_VALIDATION_STATE.NOT_VALIDATED) validateSchemaInternal();
	
		if (this.text)
		{
			loadText(output);
		}
		else if (this.stream)
		{
			loadStream(output);
		}
		else if (this.binary)
		{
			loadBinary(output);
		}
		else
		{
			throw new IllegalStateException("FileMarshaller has not initialized with a proper input format.");
		}
	}
	
	public final <T> void loadText(List<T> output) throws DataAccessException 
	{
		InputStream inStream = null;

		try
		{
			inStream = new FileInputStream(this.file);
			BufferedReader reader = new BufferedReader(new InputStreamReader(inStream));
			
			if (!reader.ready())
			{
				// Don't attempt to parse empty files
				return;
			}
			
			// Read the header, if it exists
			long maxRecords = Integer.MIN_VALUE;
			if (this.withHeader)
			{
				maxRecords = ((WithHeader)this.clazz.newInstance()).readHeader(reader);
			}
			
			// Read off the records one by one. If the header specified the number of records, read exactly that number.
			// Otherwise read to the end of the stream.
			long recordCount = 0;
			while (reader.ready() && ((maxRecords < 0) || (recordCount < maxRecords)))
            {
				recordCount++;
				try{
					TextSerializable item = (TextSerializable)clazz.newInstance();
					
					try
					{
						item.readItem(reader);
						
						// Type safety validated at beginning of method
						@SuppressWarnings("unchecked")
						T typedItem =(T)item;
						output.add(typedItem);
					}
					catch (EndOfFileException ex)
					{
						// This	represents routine termination of file reading
					}
				} 
				catch(IOException ioe) 
				{
					logger.debug("Parsing error for data type " + this.clazz.getName()+".  Error ocurred reading record #"+recordCount, ioe);
					if (!faultTolerant)
					{
						throw new DataAccessException("Parsing error for data type " + this.clazz.getName()+".  Error ocurred reading record #"+recordCount, ioe);
					}
				}catch(OutOfMemoryError e){
		            throw new OutOfMemoryError("Out of Memory loading data type: " + this.clazz.getName());
				}
				catch (Throwable t) {
					logger.fatal("Unknown error loading data type: " + this.clazz.getName());
					
					for (StackTraceElement ste : t.getStackTrace()) {
					    logger.debug(ste);
					}
					throw new DataAccessException(t);
				}
			}
			
			// Read the footer, if it exists
			if (this.withFooter)
			{
				((WithFooter)this.clazz.newInstance()).readFooter(reader);
			}
			
			// Validate the number of records if a number was specified by the header
			if (maxRecords >= 0)
			{
				if (recordCount != maxRecords)
				{
					throw new DataAccessException("Error: End of stream reached before expected number of records were loaded.");
				}
				if (reader.ready() && !this.withFooter)
				{ 
					throw new DataAccessException("Error: Expected number of records were loaded before end of stream was reached.");
				}
			}
		}
		catch (InstantiationException ex)
		{
			throw new DataAccessException("Error instantiating object for data loading", ex);
		}
		catch (IllegalAccessException ex)
		{
			throw new DataAccessException("Error reflecting object for data loading", ex);
		}
		catch (IOException ex)
		{
			throw new DataAccessException("Error reading data from file", ex);
		}
		finally
		{
			if (inStream != null)
			{
				try 
				{
					inStream.close();
				} 
				catch (IOException e) {}
			}
		}
	}
	
	public final <T> void loadStream(List<T> output) throws DataAccessException 
	{
		InputStream inStream = null;
	
		try
		{
			inStream = new FileInputStream(this.file);
			
			while (validStream(inStream))
			{
				try{
					StreamSerializable item = (StreamSerializable)clazz.newInstance();
					item.readItem(inStream);
					
					// Type safety validated at beginning of method
					@SuppressWarnings("unchecked")
					T typedItem =(T)item;
					output.add(typedItem);
				} 
				catch (InstantiationException ex)
				{
					throw new DataAccessException("Error instantiating object for data loading", ex);
				}
				catch (IllegalAccessException ex)
				{
					throw new DataAccessException("Error reflecting object for data loading", ex);
				}
				catch(Exception e) 
				{
					logger.debug(e.getMessage() + " (ignoring)");
					if (!faultTolerant)
					{
						throw new DataAccessException("Parsing error for data type " + this.clazz.getName(), e);
					}
				}
			}
		}
		catch(Exception e) 
		{
			logger.debug(e.getMessage() + " (ignoring)");
			if (!faultTolerant)
			{
				throw new DataAccessException("Error opening file for input [" + this.toString() + "]", e);
			}
		}
		finally
		{
			if (inStream != null)
			{
				try 
				{
					inStream.close();
				} 
				catch (IOException e) {}
			}
		}
	}
	
	private final boolean validStream(InputStream inStream)
	{
		try
		{
			return inStream != null && inStream.available() > 0;
		}
		catch (IOException ex)
		{
			// This is routing for readers/parsers that close the stream when they're finished
			return false;
		}
	}
	
	private final <T> void loadBinary(List<T> output) throws DataAccessException 
	{
		InputStream inStream = null;
		try
		{
			inStream = new FileInputStream(this.file);
			ObjectInputStream reader = new ObjectInputStream(inStream);
			
			while (reader.available() > 0)
			{
				Object item = reader.readObject();
				
				// Type safety validated at beginning of method
				@SuppressWarnings("unchecked")
				T typedItem =(T)item;
				output.add(typedItem);
			}
		}
		catch (ClassNotFoundException ex)
		{
			throw new DataAccessException("Error reflecting object for data loading", ex);
		}
		catch (IOException ex)
		{
			throw new DataAccessException("Error reading data from file", ex);
		}
		finally
		{
			if (inStream != null)
			{
				try 
				{
					inStream.close();
				} 
				catch (IOException e) {}
			}
		}
	}
	
	
	
	@Override
	public final <T> void saveInternal(List<T> data) throws DataAccessException 
	{
		// Verify that clazz is the same class as the parameterized list
		validateParameterizedType(data);
		
		if (this.text)
		{
			saveText(data);
		}
		else if (this.stream)
		{
			saveStream(data);
		}
		else if (this.binary)
		{
			saveBinary(data);
		}
		else
		{
			throw new IllegalStateException("FileMarshaller has not initialized with a proper input format.");
		}
	}
	
	public final <T> void saveText(List<T> data) throws DataAccessException 
	{		
		OutputStream outStream = null;
		PrintWriter writer = null;
		try
		{
			outStream = new FileOutputStream(this.file);
			writer = new PrintWriter(outStream);
			
			// Write the header, if it exists
			if (this.withHeader)
			{
				// Reflection is used instead of a collection member in case the collection is empty
				((WithHeader)this.clazz.newInstance()).writeHeader(writer, data.size());
			}
			
			for (T item : data)
			{
				((TextSerializable)item).writeItem(writer);
			}
			
			// Write the footer, if it exists
			if (this.withFooter)
			{
				// Reflection is used instead of a collection member in case the collection is empty
				((WithFooter)this.clazz.newInstance()).writeFooter(writer);
			}
		}
		catch (InstantiationException ex)
		{
			throw new DataAccessException("Error reflecting data for output", ex);
		}
		catch (IllegalAccessException ex)
		{
			throw new DataAccessException("Error reflecting data for output", ex);
		}
		catch (IOException ex)
		{
			throw new DataAccessException("Error writing data to file", ex);
		}
		finally
		{
			if (writer != null)
			{
				writer.flush();
				writer.close();
			}
		}
	}

	public final <T> void saveStream(List<T> data) throws DataAccessException 
	{		
		OutputStream outStream = null;
		try
		{
			outStream = new FileOutputStream(this.file);
			
			for (T item : data)
			{
				((StreamSerializable)item).writeItem(outStream);
			}
		}
		catch (IOException ex)
		{
			throw new DataAccessException("Error writing data to file", ex);
		}
		finally
		{
			if (outStream != null)
			{
				try 
				{
					outStream.close();
				} 
				catch (IOException e) {}
			}
		}
	}
	
	private final <T> void saveBinary(List<T> data) throws DataAccessException 
	{
		OutputStream outStream = null;
		try
		{
			outStream = new FileOutputStream(this.file);
			ObjectOutputStream writer = new ObjectOutputStream(outStream);
			
			for (T item : data)
			{
				writer.writeObject(item);
			}
		}
		catch (IOException ex)
		{
			throw new DataAccessException("Error writing data to file", ex);
		}
		finally
		{
			if (outStream != null)
			{
				try 
				{
					outStream.close();
				} 
				catch (IOException e) {}
			}
		}
	}


	@Override
	public boolean validateExistence() {

		boolean retval=false;
		
		if(existenceValidated==DATA_VALIDATION_STATE.NOT_VALIDATED){
			retval=this.exists();
			existenceValidated=retval?DATA_VALIDATION_STATE.VALID:DATA_VALIDATION_STATE.INVALID;
			if(existenceValidated==DATA_VALIDATION_STATE.INVALID){
				logger.fatal("ERROR validating file "+file.getAbsolutePath()+" -- File was not found.");
			}
		}else{
			retval=existenceValidated==DATA_VALIDATION_STATE.VALID;
		}
		return retval;
	}
	
	@Override
	public boolean validateSchema() {

		boolean retval=false;
		
		if(schemaValidated==DATA_VALIDATION_STATE.NOT_VALIDATED){
			try {
				this.validateSchemaInternal();
				schemaValidated=DATA_VALIDATION_STATE.VALID;
				retval=true;
			} catch (DataAccessException e) {
				schemaValidated=DATA_VALIDATION_STATE.INVALID;
				logger.fatal("ERROR validating file "+file.getAbsolutePath()+" -- Invalid data format: ("+e.getMessage()+")");
				retval=false;
	            logger.debug(e.getLocalizedMessage());
	            
	            for (StackTraceElement ste : e.getStackTrace()) {
	                logger.trace(ste);
	            }				
			}
		}else{
			retval=schemaValidated==DATA_VALIDATION_STATE.VALID;		
		}		

		return retval;
	}	
	
	@Override
	public boolean validateData() {
		boolean retval=false;

		if(dataValidated==DATA_VALIDATION_STATE.NOT_VALIDATED){

			List<?> temp=null;
			
			if(this.stream){
				temp=new ArrayList<StreamSerializable>();
			}else if(this.text){
				temp=new ArrayList<TextSerializable>();
			}else if(this.binary){
				temp=new ArrayList<Serializable>();
			}

			try {
				try {
					this.loadInternal(temp);
				} catch (Exception e) {
					throw new DataAccessException(e);
				}
				dataValidated = DATA_VALIDATION_STATE.VALID;
				retval = true;
			} catch (DataAccessException e) {
				retval = false;
				dataValidated = DATA_VALIDATION_STATE.INVALID;
				logger.fatal("ERROR validating file " + file.getAbsolutePath() + " -- " + e.getMessage());

				logger.debug(e.getLocalizedMessage());

				for (StackTraceElement ste : e.getStackTrace()) {
					logger.trace(ste);
				}

			}
			
		}else{
			retval=dataValidated==DATA_VALIDATION_STATE.VALID;
		}			
		
		return retval;
	}
	
	private PrintWriter appender;
	
	
	 
	protected <T> void appendInternal(T data) throws DataAccessException {
		if (appender == null) {
			initAppender();
		}
		try {
			((TextSerializable)data).writeItem(appender);
			appender.flush();
		} catch (IOException ex) {
			throw new DataAccessException("Error writing data to file", ex);
		}
	}
	
	private void initAppender() throws DataAccessException {
		try
		{
			appender = new PrintWriter(new FileOutputStream(this.file));
			
			// Write the header, if it exists
			if (this.withHeader)
			{
				// Reflection is used instead of a collection member in case the collection is empty
				((WithHeader)this.clazz.newInstance()).writeHeader(appender, 0);
			}
		}
		catch (InstantiationException ex)
		{
			throw new DataAccessException("Error reflecting data for output", ex);
		}
		catch (IllegalAccessException ex)
		{
			throw new DataAccessException("Error reflecting data for output", ex);
		}
		catch (IOException ex)
		{
			throw new DataAccessException("Error writing data to file", ex);
		}
	}
	
	public void close() throws DataAccessException {
		if (appender != null)
		{
			try {
				// Write the footer, if it exists
				if (this.withFooter)
				{
					// Reflection is used instead of a collection member in case the collection is empty
					((WithFooter)this.clazz.newInstance()).writeFooter(appender);
				}
			} catch (Exception e) {
				// Fail the footer silently
			}
			finally
			{
				if (appender != null)
				{
					appender.flush();
					appender.close();
				}
			}
		}
	}
}
