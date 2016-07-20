package gov.faa.ang.swac.controller.core;

import java.util.ArrayList;

public class BatchFiscalYear extends ArrayList<Integer>{
	
	//This class created simply to allow creation of a BatchFiscalYearEditor class to read in an 
	//ArrayList of Integer values to store a List of ForecastFiscalYears for Monte Carlo purposes.

    public BatchFiscalYear() {
        super();
    }
    
	@Override
	public String toString()
	{
		StringBuilder val = new StringBuilder("batchFiscalYear={");
		
		for (Integer fiscYear : this)
		{
			val.append(fiscYear.toString() + ";");
		}
		
		val.append("}");
		return val.toString();
	}	
	
}
