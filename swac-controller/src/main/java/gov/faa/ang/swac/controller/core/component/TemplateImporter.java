package gov.faa.ang.swac.controller.core.component;

import java.io.File;

import gov.faa.ang.swac.datalayer.DataAccessException;
import gov.faa.ang.swac.datalayer.MappedDataAccess;


public interface TemplateImporter {

	void run(File dataDir, MappedDataAccess dao) throws DataAccessException;

}