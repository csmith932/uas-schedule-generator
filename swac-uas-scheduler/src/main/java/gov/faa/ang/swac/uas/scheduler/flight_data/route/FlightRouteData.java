package gov.faa.ang.swac.uas.scheduler.flight_data.route;

/**
 * A Class representing a route, altitude, speed, and bndxing source(intersect_flag) for a flight.
 * 
 * @author James Bonn
 * @version 1.0
 */
public class FlightRouteData
{
    private double flightLevel;
    private double speed;
    private String waypoints;

    /**
     * Default Constructor.
     */
    public FlightRouteData()
    {
        flightLevel = 0;
        speed = 0;
        waypoints = null;
        //intersectFlag = 0;
    }

    /**
     * Constructor.
     * 
     * @param flightLevel
     * @param speed
     * @param waypoints
     * @param intersectFlag
     */
    public FlightRouteData(double flightLevel, double speed, String waypoints)
    {
        this.flightLevel = flightLevel;
        this.speed = speed;
        this.waypoints = waypoints;
    }

    /**
     * Set the flight level.
     * @param flightLevel
     */
    public void setFlightLevel(double flightLevel)
    {
        this.flightLevel = flightLevel;
    }

    /**
     * @return the flight level
     */
    public double getFlightLevel()
    {
        return flightLevel;
    }

    /**
     * Set the ground speed.
     * @param speed
     */
    public void setSpeed(double speed)
    {
        this.speed = speed;
    }

    /**
     * @return the ground speed
     */
    public double getSpeed()
    {
        return speed;
    }

    /**
     * Set the String list of waypoints.
     * @param waypoints
     */
    public void setWaypoints(String waypoints)
    {
        this.waypoints = waypoints;
    }

    /**
     * @return the String list of waypoints
     */
    public String getWaypoints()
    {
        return waypoints;
    }
    
    public String getEtmsWaypoints() {
        StringBuilder ret = new StringBuilder("");
        
        if (waypoints == null)
            return null;
        
        String[] points = waypoints.split(",");
        
        for (String way : points) {
            String[] tmp = way.split("/");
            ret.append(Math.round(Double.parseDouble(tmp[0]) * 60) + "/" + Math.round(Double.parseDouble(tmp[1]) * -60) + " ");
        }
        
        return ret.toString();
    }
}
