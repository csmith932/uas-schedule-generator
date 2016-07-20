package gov.faa.ang.swac.common.flightmodeling;

public abstract class AbstractResourceInfo implements IResourceInfo {
	private double modifier = 1;
    private String modifierBase = null;
	
    public AbstractResourceInfo() {
    	
    }
    
	public AbstractResourceInfo(AbstractResourceInfo org) {
        this.modifier = org.modifier;
        this.modifierBase = org.modifierBase;
	}
	
	public double modifier() {
        return this.modifier;
    }
    
    public void setModifier(double modifier) {
        this.modifier = modifier;
    }
    
    public String modifierBase() {
        return this.modifierBase;
    }
    
    public void setModifierBase(String base) {
        if (base.equalsIgnoreCase("D") || base.equalsIgnoreCase("T"))
            this.modifierBase = base.toUpperCase();
    }
    
    public abstract IResourceInfo clone() throws CloneNotSupportedException;
}
