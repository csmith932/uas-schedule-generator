/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.faa.ang.swac.controller.core;

/**
 *
 * @author ssmitz
 */
public abstract class CloneableAbstractTask extends AbstractTask implements Cloneable {

	public CloneableAbstractTask() {
		super();
    }    
    
    public CloneableAbstractTask(AbstractTask org) {
    	super();
    	setInstanceId(org.getInstanceId());
        setParent(org.getParent());
    }

    @Override
    public abstract CloneableAbstractTask clone() throws CloneNotSupportedException;
    
}
