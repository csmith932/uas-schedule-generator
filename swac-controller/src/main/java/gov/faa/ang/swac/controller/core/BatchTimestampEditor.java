package gov.faa.ang.swac.controller.core;

import gov.faa.ang.swac.common.datatypes.Timestamp;

import java.beans.PropertyEditorSupport;

public class BatchTimestampEditor extends PropertyEditorSupport {
	
	public String getAsText() 
	{
		return getValue() == null ? null : ((BatchTimestamp) getValue()).toString();
	}

    public void setAsText(String text) 
    {
    	String buffer[] = text.split(",");
    	BatchTimestamp batchTimestampList = new BatchTimestamp();
    	
    	
    	for (int i = 0; i < buffer.length; ++i)
    	{
    		Timestamp ts = Timestamp.myValueOf(buffer[i].trim());

    		if (ts != null)
    		{
    			batchTimestampList.add(ts);
    		}
    	}
    	
        /*
         * When running with dataprocessor.xml instead of scenario.xml we 
         * do not require a baseDate value but we cannot have an empty list.
         * 
         * Add dummy value if list is empty.
         */
    	if (batchTimestampList.isEmpty())
    	{
    		batchTimestampList.add(Timestamp.myValueOf("19800117"));
    	}
    	
    	setValue(batchTimestampList);
    }	

}
