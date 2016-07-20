package gov.faa.ang.swac.datalayer.identity;

import gov.faa.ang.swac.datalayer.DataAccessException;
import gov.faa.ang.swac.datalayer.ResourceManager;
import gov.faa.ang.swac.datalayer.storage.db.DataAccessObject;

/**
 * Marker superclass uses a strongly typed override of createMarshaller
 * @author csmith
 *
 */
public abstract class DataAccessObjectDescriptor extends DataDescriptor implements Cloneable {
    protected String instanceId;
    
    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }
    
    public String getInstanceId() {
        return this.instanceId;
    }
    
	@Override
	public abstract DataAccessObject createMarshaller(ResourceManager resMan) throws DataAccessException;
        
    @Override
    public abstract DataAccessObjectDescriptor clone();
    
    @Override
    public abstract int hashCode();
}
