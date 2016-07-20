package gov.faa.ang.swac.common.geometry;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import gov.faa.ang.swac.common.geometry.SphericalUtilities.IntersectionType;
import gov.faa.ang.swac.common.geometry.SphericalUtilities.InvalidInputException;

/**
 * An extension of SimplePolygon that supports the operation of checking whether any part of a line segment
 * is contained in the polygon, i.e. that it crosses the polygon at some point. Faster bounding box and 
 * point containment checks are applied first, before the slower check against all edges. This 
 * 
 * @author csmith
 *
 */
public class CrossingSimplePolygon extends SimplePolygon {
	private static final Logger logger = LogManager.getLogger(CrossingSimplePolygon.class);
	
	// TODO: Simplified bounding box logic here should be integrated with BoundedSimplePolygon, but BSP seems a little overkill if nothing crosses the antemeridian
	/** Bounding box coordinates; note: not valid for polygons crossing the antemeridian; */
	private double minLat;
	private double maxLat;
	private double minLon;
	private double maxLon;
	
	/** This is used for the full, slow check of intersections; redundant with SimpleEdge boundary definition but saves time */
	List<GCEdge> gcEdgeCache;
	
	public CrossingSimplePolygon(GCPoint[] array) {
		super(array);
		// NOTE: see init() override for CrossingSimplePoylgon-specific construction logic
	}

	/**
	 * Note: This overrides all of the code in SimplePolygon.init and inserts its own. This must be updated alongside 
	 * SimplePolygon if anything important changes
	 */
	@Override
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
    	gcEdgeCache = new ArrayList<GCEdge>(numVertices);
        
    	minLat = 90;
    	maxLat = -90;
    	minLon = 180;
    	maxLon = -180;
    	
    	int i = 0;
    	int j = 0;
    	while (numVertices > (i = j++))
    	{
    		GCPoint iPt = vertices[i];
    		
    		minLat = Math.min(iPt.latitude().normalized().degrees(), minLat);
    		maxLat = Math.max(iPt.latitude().normalized().degrees(), maxLat);
    		minLon = Math.min(iPt.longitude().normalized().degrees(), minLon);
    		maxLon = Math.max(iPt.longitude().normalized().degrees(), maxLon);
    		
    		// Use % to handle the last point wrapping to the first
    		GCPoint jPt = vertices[j % numVertices];
                
    		SimpleEdge edge = new SimpleEdge(iPt, jPt);
    		coords[i] = edge;
    		
    		if (!iPt.equals(jPt)) {
    			GCEdge gcEdge = new GCEdge(iPt, jPt);
    			gcEdgeCache.add(gcEdge); 
    		}
    	}
    }

	/**
     * Extension of the containment algorithm, for determining whether any part of line segment a-b is contained in the polygon. This proceeds in 3 layers
     * of checks. The first is a very fast bounding box overlap check. The second is a fast containment check for either of the endpoints. Finally, if crossing
     * is still indeterminate, a slow intersection check is made with each of the edges.  
     *  
     * @param a
     * @param b
     * @return
     */
    public boolean crosses(GCPoint a, GCPoint b) {
    	double aLat = a.latitude().normalized().degrees();
    	double aLon = a.longitude().normalized().degrees();
    	double bLat = b.latitude().normalized().degrees();
    	double bLon = b.longitude().normalized().degrees();
    	
    	double minLat = Math.min(aLat, bLat);
    	double maxLat = Math.max(aLat, bLat);
    	double minLon = Math.min(aLon, bLon);
    	double maxLon = Math.max(aLon, bLon);
    	
    	boolean crossLat = (maxLat >= this.minLat && minLat <= this.maxLat);
    	boolean crossLon = (maxLon >= this.minLon && minLon <= this.maxLon);
    	if (crossLat && crossLon) {
    		// Bounding boxes overlap; move on to next test
    		if (contains(a) || contains(b)) { // If an endpoint is contained then the segment crosses
    			return true;
    		} else if (!a.equals(b)){ // Trivial segments only cross if they're contained, so skip intersection checking if length = 0
    			// Doesn't contain the endpoints; need to execute the full edge-crossing algorithm
    			// There may be more efficient algorithms, but for now we simply check to see whether any 
    			// edge crosses the segment, which returns true.
    			GCEdge segment = new GCEdge(a,b);
    			for (GCEdge edge : gcEdgeCache) {
    				if (intersectsEuclidean(segment, edge)) {
    					return true;
    				}
    				// TODO: can we get away with Euclidean for performance's sake?
//    				if (intersectsGC(segment, edge)) {
//    					return true;
//    				}
    			}
    		}
    	} 
    	return false;
    }
    
    public boolean intersectsEuclidean(GCEdge a, GCEdge b) {
    	return  getLineIntersection(
    			a.first().latitude().degrees(), 
    			a.first().longitude().degrees(),
    			a.second().latitude().degrees(),
    			a.second().longitude().degrees(),
    			b.first().latitude().degrees(), 
    			b.first().longitude().degrees(),
    			b.second().latitude().degrees(),
    			b.second().longitude().degrees());
    }
	
    // NOTE: taken without validation from http://stackoverflow.com/questions/563198/how-do-you-detect-where-two-line-segments-intersect
    // Returns 1 if the lines intersect, otherwise 0. In addition, if the lines 
	// intersect the intersection point may be stored in the floats i_x and i_y.
	boolean getLineIntersection(double p0_x, double p0_y, double p1_x, double p1_y, 
			double p2_x, double p2_y, double p3_x, double p3_y)
	{
		double s1_x, s1_y, s2_x, s2_y;
	    s1_x = p1_x - p0_x;     s1_y = p1_y - p0_y;
	    s2_x = p3_x - p2_x;     s2_y = p3_y - p2_y;
	    
	    double denom = (-s2_x * s1_y + s1_x * s2_y);
	    if (denom == 0) {
	    	return false;
	    }

	    double s, t;
	    s = (-s1_y * (p0_x - p2_x) + s1_x * (p0_y - p2_y)) / denom;
	    t = ( s2_x * (p0_y - p2_y) - s2_y * (p0_x - p2_x)) / denom;

	    return (s >= 0 && s <= 1 && t >= 0 && t <= 1);
	}
    
    public boolean intersectsGC(GCEdge a, GCEdge b) {
    	try {
			if (a.intersection(b, IntersectionType.NONSTRICT) != null) {
				return true;
			}
		} catch (InvalidInputException e) {
			logger.error("Error occurred when checking for polygon edge intersection", e);
			// TODO: What should we do? Skip and go to the next edge? I think this is unreachable code
		}
		return false;
    }
}
