package gov.faa.ang.swac.controller.core;

import java.beans.PropertyEditorSupport;

public class BatchFiscalYearEditor extends PropertyEditorSupport{

	public String getAsText() 
	{
		return getValue() == null ? null : ((BatchFiscalYear) getValue()).toString();
	}

    public void setAsText(String text) 
    {
    	String buffer[] = text.split(",");
    	BatchFiscalYear batchFiscalYearList = new BatchFiscalYear();
    	for (int i = 0; i < buffer.length; ++i)
    	{
    		Integer fiscYear = null;
    		
    		try
    		{
    			fiscYear = Integer.valueOf(buffer[i].trim());
    			batchFiscalYearList.add(fiscYear);
    		}
    		catch (NumberFormatException e)
    		{
    			//do nothing.  ignore String value.
    		}
    	}
    	
        /*
         * When running with dataprocessor.xml instead of scenario.xml we 
         * do not require a forecastFiscalYear value but we cannot have an empty list.
         * 
         * Add dummy value if list is empty.
         */
    	if (batchFiscalYearList.isEmpty())
    	{
    		batchFiscalYearList.add(1980);
    	}    	
    	
    	setValue(batchFiscalYearList);
    }	

}
