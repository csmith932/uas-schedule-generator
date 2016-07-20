package gov.faa.ang.swac.datalayer.storage.fileio;

import gov.faa.ang.swac.datalayer.DataAccessException;

import java.io.File;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.xml.sax.SAXParseException;

public class XmlValidator {


	public void validate(String schemaFileName,File xmlFile) throws DataAccessException{
		
		Source xmlFileSource = new StreamSource(xmlFile);
		SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		Schema schema=null;

		try {
			
			schema = schemaFactory.newSchema(new File(schemaFileName));
			
		} catch (Exception e1) {
			
			throw new DataAccessException(e1.getMessage(),e1);
			
		}

		Validator validator = schema.newValidator();
		
		try {
			
		  validator.validate(xmlFileSource);
		  
		} catch (SAXParseException e) {
			
			int lineNumber=e.getLineNumber();
			int columnNumber=e.getColumnNumber();
			
			String msg="ERROR in file \""+xmlFile.getName()+"\", line "+lineNumber+", column "+columnNumber+". "+e.getMessage();
			
			throw new DataAccessException(msg,e);
			
		}catch(Exception e){
			
			throw new DataAccessException(e.getMessage(),e);
			
		}

	}
	
	

}
