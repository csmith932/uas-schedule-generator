package gov.faa.ang.swac.controller.core;

import gov.faa.ang.swac.common.datatypes.Timestamp;

import java.util.ArrayList;

public class BatchTimestamp extends ArrayList<Timestamp>{
	
	//This class created simply to allow creation of a BatchTimestampEditor class to read in an 
	//ArrayList of Timestamp values to store a list of BaseDates for Monte Carlo purposes.

    public BatchTimestamp() {
        super();
    }
	@Override
	public String toString()
	{
		StringBuilder val = new StringBuilder("batchTimestamp={");
		
		for (Timestamp ts : this)
		{
			val.append(ts.toString() + ";");
		}
		
		val.append("}");
		return val.toString();
	}
	
}
