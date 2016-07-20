/**
 * Copyright "TBD", Metron Aviation & CSSI.  All rights reserved.
 *
 * This computer Software was developed with the sponsorship of the U.S. Government
 * under Contract No. DTFAWA-10-D-00033, which has a copyright license in accordance with AMS 3.5-13.(c)(1).
 */

package gov.faa.ang.swac.controller.core.montecarlo.replay;

import gov.faa.ang.swac.datalayer.storage.fileio.StreamSerializable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Container for cached configuration properties, keyed by scenarioExecutionId, taskId, and property name.
 * StreamSerializable to XML in conjunction with CachedScenarioConfiguration and CachedTaskConfiguration.
 * @author csmith
 *
 */
public class ConfigurationCache implements StreamSerializable {
	private static final Logger logger = LogManager.getLogger(ConfigurationCache.class); 

	public static final String DOCUMENT_NAME = "ConfigurationCache";
	
	private SortedMap<Integer,CachedScenarioConfiguration> scenarios = new TreeMap<Integer,CachedScenarioConfiguration>();
	
	/**
	 * Get-or-create behavior so calling code never explicitly needs to 
	 * construct CachedScenarioConfiguration
	 * @param taskId
	 * @return
	 */
	public CachedScenarioConfiguration getScenario(Integer scenarioExecutionId)
	{
		CachedScenarioConfiguration retVal = this.scenarios.get(scenarioExecutionId);
		if (retVal == null)
		{
			retVal = new CachedScenarioConfiguration(scenarioExecutionId);
			this.scenarios.put(scenarioExecutionId, retVal);
		}
		return retVal;
	}
	
	public boolean contains(Integer scenarioExecutionId){
		return this.scenarios.containsKey(scenarioExecutionId);
	}
	
	/**
	 * Flagged protected because calling code shouldn't need to use this method
	 * @param scenario
	 */
	protected void putScenario(CachedScenarioConfiguration scenario)
	{
		this.scenarios.put(scenario.getScenarioExecutionId(), scenario);
	}
	
	/**
	 * Using this to clear the cache and then saving it will overwrite the existing cache with a blank file
	 */
	public void clearCache()
	{
		logger.warn("All cached configurations are being deleted");
		this.scenarios.clear();
	}
	
	/**
	 * XML parsing. Sub-element parsing is delegated to CachedScenarioConfiguration
	 */
	@Override
    public void readItem(InputStream inStream) throws IOException {
        try
        {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder parser = dbf.newDocumentBuilder();

            Document d = parser.parse(new InputSource(inStream));

            NodeList list = d.getDocumentElement().getElementsByTagName(CachedScenarioConfiguration.ELEMENT_NAME);
            for (int i = 0; i < list.getLength(); i++) {
            	CachedScenarioConfiguration scenario = CachedScenarioConfiguration.readElement((Element) list.item(i));
            	this.putScenario(scenario);
            }
        }
        catch (SAXException ex)
        {
            throw new IOException(ex);
        } 
        catch (ParserConfigurationException ex) 
        {
            throw new IOException(ex);
        }
    }
	
	/**
	 * XML formatting. Sub-element formatting is delegated to CachedScenarioConfiguration
	 */
	@Override
    public void writeItem(OutputStream outStream) throws IOException {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = dbf.newDocumentBuilder();
            DOMImplementation impl = builder.getDOMImplementation();
            Document d = impl.createDocument(null, DOCUMENT_NAME, null);

            for (CachedScenarioConfiguration scenario : this.scenarios.values()) {
            	CachedScenarioConfiguration.writeElement(d, d.getDocumentElement(), scenario);
            }

            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

            transformer.transform(new DOMSource(d), new StreamResult(outStream));
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            return;
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
            return;
        } catch (TransformerException e) {
            e.printStackTrace();
            return;
        }
    }
}
