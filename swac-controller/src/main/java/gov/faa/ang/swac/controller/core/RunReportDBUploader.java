/**
 * Copyright "TBD", Metron Aviation & CSSI.  All rights reserved.
 * 
 * This computer Software was developed with the sponsorship of the U.S. Government
 * under Contract No. DTFAWA-10-D-00033, which has a copyright license in accordance with AMS 3.5-13.(c)(1).
 */
package gov.faa.ang.swac.controller.core;

import gov.faa.ang.swac.controller.ExitException;
import gov.faa.ang.swac.datalayer.DataAccessException;
import gov.faa.ang.swac.datalayer.storage.DataMarshaller;
import gov.faa.ang.swac.datalayer.storage.db.DataAccessObject;
import gov.faa.ang.swac.datalayer.storage.db.UploadableRecord;

import java.util.ArrayList;
import java.util.List;

public final class RunReportDBUploader extends CloneableAbstractTask {
	
	private static org.apache.log4j.Logger logger = org.apache.log4j.LogManager.getLogger(RunReportDBUploader.class);

    private DataMarshaller inputRecords;
    private DataAccessObject<UploadableRecord> dao;
    
    
    public RunReportDBUploader() {  }
        
    public RunReportDBUploader(RunReportDBUploader org) {
    	super(org);
    }
        
    public void setInputRecords(DataMarshaller inputRecords) {
        this.inputRecords = inputRecords;
    }

    public void setUploadDao(DataAccessObject<UploadableRecord> dao) {
        this.dao = dao;
    }
    
	@Override
	public void run() {
		try {
			List<UploadableRecord> uploadableRecords = new ArrayList<UploadableRecord>(1100);
			this.inputRecords.load(uploadableRecords);
			//System.out.printf("### uploading for dao %s with %s records, instance id %s\n", dao, uploadableRecords.size(), getInstanceId());
			dao.insertAll(uploadableRecords);
		} catch (DataAccessException e) {
//			logger.trace(e.getStackTrace());
			logger.fatal("Failure in " + getClass().getSimpleName(), e);
			throw new ExitException("Data access exception.", e);
		}
	}
		
	@Override
	public boolean validate(VALIDATION_LEVEL level) { return true; }

    @Override
    public RunReportDBUploader clone() {
        return new RunReportDBUploader(this);
    }
}
