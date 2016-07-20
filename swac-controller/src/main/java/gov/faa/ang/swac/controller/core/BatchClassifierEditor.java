package gov.faa.ang.swac.controller.core;

import java.beans.PropertyEditorSupport;

public class BatchClassifierEditor extends PropertyEditorSupport{
	
	public String getAsText() 
	{
		return getValue() == null ? null : ((BatchClassifier) getValue()).toString();
	}

    public void setAsText(String text) 
    {
    	String buffer[] = text.split(",");
    	BatchClassifier batchClassifierList = new BatchClassifier();
    	for (int i = 0; i < buffer.length; ++i)
    	{
   			batchClassifierList.add(buffer[i].trim());
    	}
    	
        /*
         * When running with dataprocessor.xml instead of scenario.xml we 
         * do not require a classifier value but we cannot have an empty list.
         * 
         * Add dummy value if list is empty.
         */
    	if (batchClassifierList.isEmpty())
    	{
    		batchClassifierList.add("dummyValue");
    	}
    	
    	setValue(batchClassifierList);
    }	
}
