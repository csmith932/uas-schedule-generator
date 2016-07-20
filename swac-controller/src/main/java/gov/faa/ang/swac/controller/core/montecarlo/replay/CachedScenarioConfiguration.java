/**
 * Copyright "TBD", Metron Aviation & CSSI.  All rights reserved.
 *
 * This computer Software was developed with the sponsorship of the U.S. Government
 * under Contract No. DTFAWA-10-D-00033, which has a copyright license in accordance with AMS 3.5-13.(c)(1).
 */

package gov.faa.ang.swac.controller.core.montecarlo.replay;

import gov.faa.ang.swac.common.datatypes.Timestamp;

import java.util.SortedMap;
import java.util.TreeMap;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * This is a transitional mapping container for cached configurations: the CachedScenarioConfiguration
 * for a specific scenarioExecutionId contains an indexed collection of CachedTaskConfigurations, which
 * in turn contain the name-value property pairs. CachedScenarioConfiguration uses XML parsing and
 * rendering in conjunction with ConfigurationCache and CachedTaskConfiguration to implement StreamSerializable
 * 
 * @author csmith
 *
 */
public class CachedScenarioConfiguration
{
	
	public static final String ELEMENT_NAME = "scenario";
	public static final String ID_ATTRIBUTE_NAME = "scenarioExecutionId";
	private static final String BASE_DATE_ATTRIBUTE_NAME = "baseDate";
	private static final String FORECAST_FY_ATTRIBUTE_NAME = "forecastFiscalYear";
	private static final String CLASSIFIER_ATTRIBUTE_NAME = "classifier";
	
	private final Integer scenarioExecutionId;
	private Timestamp baseDate;
	private int forecastFiscalYear;
	private String classifier;
	private SortedMap<String,CachedTaskConfiguration> tasks = new TreeMap<String,CachedTaskConfiguration>();
	
	/**
	 * scenarioExecutionId is an immutable property
	 * @param scenarioExecutionId
	 */
	public CachedScenarioConfiguration(Integer scenarioExecutionId)
	{
		this.scenarioExecutionId = scenarioExecutionId;
	}
	
	/**
	 * 
	 * @return instance id
	 */
	public Integer getScenarioExecutionId()
	{
		return this.scenarioExecutionId;
	}
	
	
	public Timestamp getBaseDate() {
		return baseDate;
	}

	public void setBaseDate(Timestamp baseDate) {
		this.baseDate = baseDate;
	}

	public int getForecastFiscalYear() {
		return forecastFiscalYear;
	}

	public void setForecastFiscalYear(int forecastFiscalYear) {
		this.forecastFiscalYear = forecastFiscalYear;
	}

	public String getClassifier() {
		return classifier;
	}

	public void setClassifier(String classifier) {
		this.classifier = classifier;
	}

	/**
	 * Get-or-create behavior so calling code never explicitly needs to
	 * construct CachedTaskConfigurations
	 * @param taskId
	 * @return
	 */
	public CachedTaskConfiguration getTask(String taskId)
	{
		CachedTaskConfiguration retVal = this.tasks.get(taskId);
		if (retVal == null)
		{
			retVal = new CachedTaskConfiguration(taskId);
			this.tasks.put(taskId, retVal);
		}
		return retVal;
	}
	
	/**
	 * Flagged protected because calling code shouldn't need to use this method
	 * @param task
	 */
	protected void putTask(CachedTaskConfiguration task)
	{
		this.tasks.put(task.getTaskId(), task);
	}
	
	/**
	 * XML parsing. Sub-element parsing is delegated to CachedTaskConfiguration
	 * @param element
	 * @return
	 */
	protected static CachedScenarioConfiguration readElement(Element element) {
		CachedScenarioConfiguration scenario = new CachedScenarioConfiguration(Integer.parseInt(element.getAttribute(ID_ATTRIBUTE_NAME)));
		
		scenario.setBaseDate(Timestamp.myValueOf(element.getAttribute(BASE_DATE_ATTRIBUTE_NAME)));
		scenario.setForecastFiscalYear(Integer.parseInt(element.getAttribute(FORECAST_FY_ATTRIBUTE_NAME)));
		scenario.setClassifier(element.getAttribute(CLASSIFIER_ATTRIBUTE_NAME));
		
		NodeList taskList = element.getElementsByTagName(CachedTaskConfiguration.ELEMENT_NAME);
        for (int i = 0; i < taskList.getLength(); i++) {
            scenario.putTask(CachedTaskConfiguration.readElement((Element) taskList.item(i)));
        }
        
		return scenario;
    }
	
	/**
	 * XML formatting. Sub-element formatting is delegated to CachedTaskConfiguration
	 * @param d
	 * @param parent
	 * @param scenario
	 */
	protected static void writeElement(Document d, Element parent, CachedScenarioConfiguration scenario)
	{
		Element element = d.createElement(ELEMENT_NAME);
        parent.appendChild(element);

        element.setAttribute(ID_ATTRIBUTE_NAME, scenario.getScenarioExecutionId().toString());
        element.setAttribute(BASE_DATE_ATTRIBUTE_NAME, scenario.getBaseDate().toBonnDateOnlyString());
        element.setAttribute(FORECAST_FY_ATTRIBUTE_NAME, String.valueOf(scenario.getForecastFiscalYear()));
        element.setAttribute(CLASSIFIER_ATTRIBUTE_NAME, scenario.getClassifier());
        
        for (CachedTaskConfiguration task : scenario.tasks.values())
        {
        	CachedTaskConfiguration.writeElement(d, element, task);
        }
	}
}
