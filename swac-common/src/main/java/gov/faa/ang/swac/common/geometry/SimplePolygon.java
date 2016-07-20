/**
 * Copyright "TBD", Metron Aviation & CSSI.  All rights reserved.
 * 
 * This computer Software was developed with the sponsorship of the U.S. Government
 * under Contract No. DTFAWA-10-D-00033, which has a copyright license in accordance with AMS 3.5-13.(c)(1).
 */

package gov.faa.ang.swac.common.geometry;

import gov.faa.ang.swac.common.datatypes.Angle;
import gov.faa.ang.swac.common.datatypes.Latitude;
import gov.faa.ang.swac.common.datatypes.Longitude;
import gov.faa.ang.swac.common.datatypes.Patterns;
import gov.faa.ang.swac.datalayer.storage.fileio.TextSerializable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * Provides basic, performance optimized point-in-poly testing based on
 * Euclidean geometry and the even-odd point containment algorithm.
 * 
 * @author csmith
 *
 */
public class SimplePolygon implements TextSerializable
{    
    protected static final String LAT_LON_PATTERN = "^\\s*?(" + Patterns.FLOAT + ")\\s*?,\\s*?(" + Patterns.FLOAT + ")\\s*?$";
    
    private static int INSTANCE_COUNTER = 0;
    private static Logger logger = LogManager.getLogger(SimplePolygon.class);

    public int instanceId;
    protected SimpleEdge[] coords;
    
    public SimplePolygon() {
        this.instanceId = SimplePolygon.INSTANCE_COUNTER++;
    }
	
	/**
     * Initializes a collection of directed edges, using the assumption that each adjacent pair of points
     * in the vertices list represents an edge (closing from the last point to the first point).
     * The endpoints of the edge are recorded as well as pre-computed azimuth and elevation deltas from
     * the first endpoint to the second.
     * @param vertices
     */
    public SimplePolygon(GCPoint[] vertices)
    {
        this.instanceId = INSTANCE_COUNTER++;
    	init(vertices);
    }
    
    public SimplePolygon(double[] latlons) {
        this.instanceId = INSTANCE_COUNTER++;
    	if (latlons.length % 2 != 0) {
    		throw new IllegalArgumentException("Invalid number of coordinates for polygon specification.");
    	}
    	int l = latlons.length / 2;
    	GCPoint[] vertices = new GCPoint[l];
    	for (int i = 0; i < l; i++) {
    		Latitude lat = Latitude.valueOfDegrees(latlons[2 * i]).normalized();
    		Longitude lon = Longitude.valueOfDegrees(latlons[2 * i + 1]).normalized();
    		vertices[i] = new GCPoint(lat, lon);
    	}
    	init(vertices);
    }
    
    public SimplePolygon(SimplePolygon org) {
        this.instanceId = INSTANCE_COUNTER++;
        this.init(org.points().toArray(new GCPoint[org.points().size()]));
    }
    
    protected void init(GCPoint[] vertices) {
    	int numVertices = vertices.length;
        
    	if (vertices[0].equals(vertices[vertices.length-1]))
    	{
    		// Polygon is already closed. Do not duplicate the start/end point
    		// since this would create a zero length edge (and divide by zero error)
    		numVertices--;
    	}
    	
        if (numVertices < 3) {
            throw new IllegalArgumentException("Invalid number of coordinates for polygon specification.");
    	}
        
    	coords = new SimpleEdge[numVertices];
        
    	int i = 0;
    	int j = 0;
    	while (numVertices > (i = j++))
    	{
    		GCPoint iPt = vertices[i];
    		// Use % to handle the last point wrapping to the first
    		GCPoint jPt = vertices[j % numVertices];
                
    		SimpleEdge edge = new SimpleEdge(iPt, jPt);
    		coords[i] = edge;
    	}
    }
    
	/**
     * Basic even-odd algorithm for testing polygon containment. A ray is dropped along the meridian from the test point
     * to the south pole and the number of edges crossed is counted: odd=inside and even=outside. This is not well defined
     * for polygons containing the south pole. Edges are considered to be in the Euclidean plane 
     * (wrapping at the antemeridian) for geometric purposes
     * @param testPoint
     * @return
     */
    public boolean contains(GCPoint testPoint)
	{
		final double testLat = testPoint.latitude.normalized().degrees();
		final double testLon = testPoint.longitude.normalized().degrees();
		
		boolean inside = false;
		
		// Loop through the set of vertices indexed to this bin and check for crossings
		for (SimpleEdge edge : this.coords)
    	{
			/* Ignore edges whose longitudes are both on the same side of the test point 
			 * (reverse the test if the antemeridian is crossed by the edge)
			 * Vertices are considered to be infinitesimally shifted to the east of their
			 * longitudes, such that edges of constant longitude and vertex interactions can
			 * be ignored.
			 */ 
    		if (((edge.getLon1() < testLon) == (edge.getLon2() < testLon)) == edge.isCross()) 
    		{
    			// We are dropping a ray to the south pole, so if the edge latitude (interpolated to test longitude)
    			// is lower than the test latitude, then the edge is crossed 
    			if (edge.getLat1() + (SimpleEdge.azimuth(edge.getLon1(), testLon) / edge.getAzimuth()) * (edge.getElevation()) < testLat) 
    			{
    				inside = !inside;
    			}
    		}
    	}
		
		return inside;
	}
    
    /**
     *
     * @return A list of
     * @GCPoints which define the shape of the polygon.
     */
    public List<GCPoint> points() {
        List<GCPoint> points = new ArrayList<GCPoint>();

        for (SimpleEdge se : this.coords) {
            Latitude lat = new Latitude(se.getLat1(), Angle.Units.DEGREES);
            Longitude lon = new Longitude(se.getLon1(), Angle.Units.DEGREES);
            points.add(new GCPoint(lat, lon));
        }
        
        points.add(points.get(0).clone());
        
        return points;
    }
    
    @Override
    public boolean equals(Object o) {
        if (o instanceof SimplePolygon) {
            boolean rtn = true;
            SimplePolygon sp = (SimplePolygon)o;
            
            if (this.coords.length != sp.coords.length) {
                rtn = false;
            }
            
            for (int i = 0; i < this.coords.length && rtn; i++) {
                rtn &= this.coords[i].equals(sp.coords[i]);
            }
            
            return rtn;
        } else {
            return false;
        }
    }
    
    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(coords);
		result = prime * result + instanceId;
		return result;
	}    

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("SimplePolygon: ");
        for (GCPoint point : this.points()) {
            stringBuilder.append(point.toString()).append(" -> ");
        }
        stringBuilder.delete(stringBuilder.length() - 2, stringBuilder.length() - 1);
        return stringBuilder.toString();
    }
    
    @Override
    public void readItem(BufferedReader reader) throws IOException {
        List<GCPoint> points = new ArrayList<GCPoint>();
        Pattern latLonPattern = Pattern.compile(LAT_LON_PATTERN);

        while (reader.ready()) {
            String line = reader.readLine();
            Matcher matcher = latLonPattern.matcher(line);

            if (matcher.find()) {
                Latitude lat = Latitude.valueOfDegrees(Double.valueOf(matcher.group(1))).normalized();
                Longitude lon = Longitude.valueOfDegrees(Double.valueOf(matcher.group(2))).normalized();
                GCPoint point = new GCPoint(lat, lon);
                points.add(point);
            } else {
                logger.fatal("SimplePolygon.readPolygonFromFile() error: Expected lat/lon pair, got: \"" + line + "\". Exiting...");
                throw new RuntimeException();
            }
        }
        this.init(points.toArray(new GCPoint[points.size()]));
    }

    @Override
    public void writeItem(PrintWriter writer) throws IOException {
        writer.println(this.toString());
    }
}
