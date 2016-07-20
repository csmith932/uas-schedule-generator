package gov.faa.ang.swac.controller.core;

import java.util.ArrayList;

public class BatchClassifier extends ArrayList<String>{
	
	//This class created simply to allow creation of a BatchClassifierEditor class to read in an 
	//ArrayList of String values to store a List of Classifier values (e.g. "base") for Monte Carlo purposes.

    public BatchClassifier() {
        super();
    }
	@Override
	public String toString()
	{
		StringBuilder val = new StringBuilder("batchClassifier={");
		
		for (String classifier : this)
		{
			val.append(classifier + ";");
		}
		
		val.append("}");
		return val.toString();
	}		
	
}
