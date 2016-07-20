package gov.faa.ang.swac.common.flightmodeling.jni;

import gov.faa.ang.swac.common.datatypes.Timestamp;
import gov.faa.ang.swac.datalayer.storage.fileio.TextSerializable;
import gov.faa.ang.swac.datalayer.storage.fileio.WithHeader;

public class RampTimeDistributionLookupRecord extends TimeDistributionLookupRecord implements TextSerializable, WithHeader
{
	private long minimumServiceTime;
	
	public long getMinimumServiceTime() { return minimumServiceTime; } 
	
	@Override
	protected int expectedFieldCount() { 
		return 7;
	}
	
	@Override
	protected void parseFields(String [] fields) {
		super.parseFields(fields);
	
		String minimumServiceTimeStr = fields[6].trim();
        if (minimumServiceTimeStr.equals("-")) 
        	minimumServiceTime = 0; 
        else 
        	minimumServiceTime = (long) (Double.parseDouble(minimumServiceTimeStr) * (double) Timestamp.MILLISECS_MIN);
	}
}
