package gov.faa.ang.swac.uas.scheduler.input;

import java.util.ArrayList;
import java.util.List;

import gov.faa.ang.swac.common.datatypes.Latitude;
import gov.faa.ang.swac.common.datatypes.Longitude;
import gov.faa.ang.swac.common.geometry.GCPoint;

public class Waypoint extends GCPoint {
	
	public Waypoint(Latitude lat, Longitude lon) {
		super(lat, lon);
	}
	
	@Override
	public String toString() {
		return ((int)(60 * this.latitude.degrees())) + "/" + ((int)(-60 * this.longitude.degrees())); 
	}
	
	public static List<Waypoint> fromWaypointString(String waypointString)
    {
        List<Waypoint> waypoints = new ArrayList<Waypoint>();
        
        if (waypointString != null)
        {
            String[] waypointPairs = waypointString.split(" "); // Split up waypoints into "lat/lon" pairs
            double lat, lon;
            
            for (String waypointPair : waypointPairs)
            {
                String[] latLonPair = waypointPair.split("/");  // Split up waypoint into "lat" and "lon"
                
                // Convert minutes of angle into degrees
                lat =   Double.valueOf(latLonPair[0]) / 60.0;
                lon = -Double.valueOf(latLonPair[1]) / 60.0; // Schedule file defines West as negative longitude... convert to standard East-positive
                waypoints.add(new Waypoint(Latitude.valueOfDegrees(lat), Longitude.valueOfDegrees(lon)));
            }
        }
        
        return waypoints;
    }
	
	public static String toWaypointString(List<Waypoint> waypoints, int multiple) {
		StringBuilder str = new StringBuilder();
		for (int i = 0; i < waypoints.size() * multiple; i++) {
			str.append(waypoints.get(i % waypoints.size()).toString() + " ");
		}
		
		return str.toString().trim();
	}
}
