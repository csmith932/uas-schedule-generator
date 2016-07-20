/**
 * Copyright "TBD", Metron Aviation & CSSI.  All rights reserved.
 *
 * This computer Software was developed with the sponsorship of the U.S. Government
 * under Contract No. DTFAWA-10-D-00033, which has a copyright license in accordance with AMS 3.5-13.(c)(1).
 */

package gov.faa.ang.swac.controller.core.montecarlo.replay;

import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * This is a container for configuration properties of a TaskConfiguration, excluding data
 * flow information and specific behaviors (e.g. MonteCarloMapTaskConfiguration). 
 * CachedTaskConfiguration has XML parsing/formatting so it can be cached to disk in
 * XML and used to override the Spring-loaded and especially Monte Carlo random sampled
 * properties in a scenario replay.
 * @author csmith
 *
 */
public class CachedTaskConfiguration {

	public static final String ELEMENT_NAME = "task";
	public static final String ID_ATTRIBUTE_NAME = "taskId";
	public static final String PROPERTY_ELEMENT_NAME = "property";
	public static final String PROPERTY_KEY_ATTRIBUTE_NAME = "name";
	public static final String PROPERTY_VALUE_ATTRIBUTE_NAME = "value";
	
	private final String taskId;
	public final SortedMap<String,Object> configuration = new TreeMap<String, Object>();
	
	/**
	 * Immutable id property
	 * @param taskId
	 */
	public CachedTaskConfiguration(String taskId) {
		this.taskId = taskId;
	}

	public String getTaskId() {
		return taskId;
	}

	/**
	 * XML parsing
	 * @param element
	 * @return
	 */
	protected static CachedTaskConfiguration readElement(Element element) {
		CachedTaskConfiguration task = new CachedTaskConfiguration(element.getAttribute(ID_ATTRIBUTE_NAME));
		
		NodeList propertyList = element.getElementsByTagName(PROPERTY_ELEMENT_NAME);
        for (int i = 0; i < propertyList.getLength(); i++) {
        	Element property = (Element) propertyList.item(i);
        	String name = property.getAttribute(PROPERTY_KEY_ATTRIBUTE_NAME);
        	String value = property.getAttribute(PROPERTY_VALUE_ATTRIBUTE_NAME);
            
        	task.configuration.put(name, value);
        }
        
		return task;
    }
	
	/**
	 * XML formatting
	 * @param d
	 * @param parent
	 * @param task
	 */
	protected static void writeElement(Document d, Element parent, CachedTaskConfiguration task)
	{
		Element element = d.createElement(ELEMENT_NAME);
        parent.appendChild(element);
        
        element.setAttribute(ID_ATTRIBUTE_NAME, task.getTaskId());

        for (Entry<String,Object> property : task.configuration.entrySet()) {
        	writePropertyElement(d, element, property);
        }
	}
	
	private static void writePropertyElement(Document d, Element parent, Entry<String,Object> property)
	{
		Element propElement = d.createElement(PROPERTY_ELEMENT_NAME);
        parent.appendChild(propElement);

        propElement.setAttribute(PROPERTY_KEY_ATTRIBUTE_NAME, property.getKey());
        propElement.setAttribute(PROPERTY_VALUE_ATTRIBUTE_NAME, property.getValue().toString());
	}
}
