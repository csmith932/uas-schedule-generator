package gov.faa.ang.swac.controller.core.component;

import java.util.ArrayList;
import java.util.List;

import gov.faa.ang.swac.controller.core.CloneableAbstractTask;
import gov.faa.ang.swac.datalayer.AppendableDataSubscriber;
import gov.faa.ang.swac.datalayer.DataAccessException;
import gov.faa.ang.swac.datalayer.storage.DataMarshaller;
import gov.faa.ang.swac.datalayer.storage.fileio.FileMarshaller;

/**
 *  Used to replace some of the file output functionality of RunOutputGen
 * 
 * @author hkaing
 *
 */
public class GenericFileOutputBean extends CloneableAbstractTask implements AppendableDataSubscriber {

	private static org.apache.log4j.Logger logger = org.apache.log4j.LogManager.getLogger(GenericFileOutputBean.class);	
	
    private DataMarshaller inputData;
    public DataMarshaller getInputData() { return this.inputData; }
    public void setInputData(DataMarshaller val)
	{	
		if (this.inputData != null)
		{
			this.inputData.unsubscribe(this);
		}
		
		this.inputData = val;
		if (val != null)
		{
			val.subscribe(this);
		}
		
	}	
	
    private FileMarshaller outputFile;
    public FileMarshaller getOutputFile() { return this.outputFile; }
    public void setOutputFile(FileMarshaller val) { this.outputFile = val; }
    
	@Override
    public void run()
	{
		// Everything is done in the handler
		// TODO: Maybe this shouldn't be done with event hooking, but task ordering is difficult to resolve in a way that ensures timely
		// garbage collection and consistent behavior for Monte Carlo and non Monte Carlo outputs (e.g. global versus non-global)
		
		try {
			this.inputData.close();
			this.outputFile.close();
		} catch (DataAccessException e) { /* Ignore Exceptions closing marshallers */ }
	}
	
	@Override
	public void onLoad(Object source) {
		// Do nothing
	}
	
	// TODO: Not sure if using event hooking on DataMarshallers is wise
	@Override
	public void onSave(Object source) {
		if (source != inputData)
		{
			throw new IllegalStateException("GenericFileOutputBean should only be listening to events from inputData");
		}
		
		List<?> temp = new ArrayList();
		try {
			
            logger.debug("loading Intermediate Output from RunOutputGen...");
			this.inputData.load(temp);
			if (temp.size() > 0) // Avoid infinite loop
			{
				logger.debug("Saving file: " + this.outputFile.toString());
				this.outputFile.save(temp);
				
				// TODO: The following block doesn't work, and neither does unsubscribing in the handler (concurrent modification error)...we need better clean-up
//				// Now clean-up
//				temp.clear();
//				this.inputData.save(temp);
			}
		} catch (DataAccessException e) {
                    logger.trace(e.getStackTrace());
		}
	}
	@Override
	public boolean validate(VALIDATION_LEVEL level) {
		// Not sure anything can be validated in advance in this class.
		return true;
	}
	
	@Override
	public void onAppend(Object source) {
		if (source != inputData) {
			throw new IllegalStateException("GenericFileOutputBean should only be listening to events from inputData");
		}
		try {
			this.outputFile.append(this.inputData.read());
		} catch (DataAccessException e) {
			logger.trace(e.getStackTrace());
		}
	}
	@Override
	public CloneableAbstractTask clone() throws CloneNotSupportedException {
		// No cloneable properties
		return new GenericFileOutputBean();
	}
}
