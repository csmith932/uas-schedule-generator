package gov.faa.ang.swac.common.flightmodeling;

import java.io.Serializable;

public class WaypointResourceInfo extends AbstractResourceInfo implements Serializable {
	private final ResourceType resourceType;
	private final String name;
	private boolean isHoldingFix;
	private boolean isIapPoint;
	
	public enum FinalFlownType{
		SPEEDCONTROL,
		VECTORING,
		PATHSTRETCH,
		HOLDING;
	}
	
	private WaypointResourceInfo(ResourceType resourceType, String name) {
		this.resourceType = resourceType;
		this.name = name;
	}
	
    private WaypointResourceInfo(WaypointResourceInfo org) {
        this.resourceType = org.resourceType;
        this.name = org.name;
        this.isHoldingFix = org.isHoldingFix;
        this.isIapPoint = org.isIapPoint;
    }
        
	public static WaypointResourceInfo createWaypoint(String name) {
		return new WaypointResourceInfo(ResourceType.WP, name);
	}

	public static WaypointResourceInfo createTopOfClimb(String name) {
		return new WaypointResourceInfo(ResourceType.TC, name);
	}

	public static WaypointResourceInfo createTopOfDescent(String name) {
		return new WaypointResourceInfo(ResourceType.TD, name);
	}
        
	public static WaypointResourceInfo createVectorManeuverPoint() {
		return new WaypointResourceInfo(ResourceType.WP, FinalFlownType.VECTORING.name());
	}
        
	public static WaypointResourceInfo createPathStretchingPoint() {
		return new WaypointResourceInfo(ResourceType.WP, FinalFlownType.PATHSTRETCH.name());
	}
        
	public static WaypointResourceInfo createHoldingManeuverPoint() {
		return new WaypointResourceInfo(ResourceType.WP, FinalFlownType.HOLDING.name());
	}
	
	public static WaypointResourceInfo createFreezeHorizonPoint(String name){
		return new WaypointResourceInfo(ResourceType.FH, name);
	}
	
	public static WaypointResourceInfo createMeteringArcPoint(String name){
		return new WaypointResourceInfo(ResourceType.AR, name);
	}

	public void setIsIapPoint(boolean val){
		this.isIapPoint = val;
	}
	
	public boolean isIapPoint(){
		return this.isIapPoint;
	}
	
	public void setIsHoldingFix(boolean val){
		this.isHoldingFix = val;
	}
	
	public boolean isHoldingFix(){
		return this.isHoldingFix;
	}
	
	public static FinalFlownType getFinalFlownType(String name){
		if (name == null)
			return null;
		
		for (FinalFlownType type : FinalFlownType.values()){
			if (name.startsWith(type.name()))
				return type;
		}
		
		return null;
	}
	
	@Override
	public String name() {
		return this.name;
	}

	@Override
	public ResourceType resourceType() {
		return this.resourceType;
	}

	@Override
	public long crossingTime() {
		return 0;
	}

	@Override
	public String toString() {
		return "WaypointResourceInfo [resourceType=" + resourceType + ", name="
				+ name + "]";
	}
        
    @Override
    public WaypointResourceInfo clone() {
        return new WaypointResourceInfo(this);
    }
}
