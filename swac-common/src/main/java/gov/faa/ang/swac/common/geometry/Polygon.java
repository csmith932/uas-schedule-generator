///**
// * Copyright "TBD", Metron Aviation & CSSI.  All rights reserved.
// * 
// * This computer Software was developed with the sponsorship of the U.S. Government
// * under Contract No. DTFAWA-10-D-00033, which has a copyright license in accordance with AMS 3.5-13.(c)(1).
// */
//
//package gov.faa.ang.swac.common.geometry;
//
//import java.io.Serializable;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Set;
//import java.util.TreeSet;
//
//import org.apache.log4j.LogManager;
//import org.apache.log4j.Logger;
//
//import gov.faa.ang.swac.common.datatypes.Latitude;
//import gov.faa.ang.swac.common.datatypes.Longitude;
//import gov.faa.ang.swac.common.datatypes.Vector3D;
//import gov.faa.ang.swac.common.geometry.SphericalUtilities.InvalidInputException;
//import java.util.Collections;
//
//// TODO KNOWN PROBLEMS:
//// 1. Polygon cannot handle polar vertices or polygons which contain a pole.
//// If this cannot be fixed, then it should be rejected in construction.
//
///**
// * Represents a polygon on a sphere with the option of using either Euclidean or spherical geometry
// * (see <code>setEuclidean()</code>). The main purpose is to determine if a test point is contained
// * within a {@link Polygon} by calling the <code>contains()</code> method.
// * 
// * @author Clifford Hall, et. al. - CSSI, Inc.
// * 
// * Updated by Sean Smitz March 2013: Added method to find all points of intersection with a line segment.
// */
//@Deprecated
//public class Polygon implements Serializable
//{
//	private static final Logger logger = LogManager.getLogger(Polygon.class);
//	
//    /**
//     * Determines if <code>true</code>, this <code>Polygon</code>'s edges are interpreted as line
//     * segments in a flat plane (Euclidean); otherwise, this <code>Polygon</code>'s edges are
//     * interpreted as arcs of great circles (Spherical).
//     * 
//     * @see #getEuclidean
//     * @see #setEuclidean
//     */
//    private boolean euclidean;
//
//    /**
//     * Holds the vertices of this <code>Polygon</code>. The internal representation is always
//     * closed: The last <code>GCPoint</code> is a reference to the first.
//     * 
//     * @see #getVertices
//     * @see #setVertices
//     */
//    private GCPoint[] vertices;
//
//    /**
//     * Contains data pertaining to the extreme values of the Polygon object: (1) the indices of the
//     * extremal vertices, and (2) the extreme north and south latitudes for spherical Polygons.
//     */
//    private class Extrema implements Serializable
//    {
//        /**
//         * Holds the index of the corresponding extreme vertex in this <code>Polygon</code> object.
//         */
//        private int iSouthExtreme, iNorthExtreme, iWestExtreme, iEastExtreme;
//
//        /**
//         * For spherical polygons, the northern & southern extreme latitudes can exceed those of the
//         * vertices themselves. If so, the corresponding value is stored here. Otherwise, null.
//         */
//        private Latitude northExtreme, southExtreme;
//
//        /**
//         * True if and only if this polygon wraps around the world from west to east
//         */
//        private boolean spansAllLongitude;
//
//        /**
//         * Constructs an <code>Extrema</code> object. Automatically sets this object's data members
//         * to correspond to the parent <code>Polygon</code> object.
//         */
//        private Extrema()
//        {
//            
//        }
//
//        /**
//         * The copy constructor. Only used by the <code>Polygon</code> copy constructor.
//         * 
//         * @param original The orignal <code>Extrema</code> object being copied.
//         */
//        private Extrema(Extrema original)
//        {
//            this.iSouthExtreme = original.iSouthExtreme;
//            this.iNorthExtreme = original.iNorthExtreme;
//            this.iWestExtreme = original.iWestExtreme;
//            this.iEastExtreme = original.iEastExtreme;
//
//            if (original.northExtreme != null)
//            {
//                this.northExtreme = new Latitude(original.northExtreme);
//            }
//
//            if (original.southExtreme != null)
//            {
//                this.southExtreme = new Latitude(original.southExtreme);
//            }
//        }
//
//        /**
//         * Checks to see if the point being passed in is located within the extrema
//         * 
//         * @param pt the test point being checked
//         * @return true if pt falls within the extrema; false, otherwise
//         */
//        private boolean contains(final GCPoint pt)
//        {
//            return pt.latitude().isBetween(getSouthExtreme(), getNorthExtreme()) &&
//                (this.spansAllLongitude || pt.longitude().isBetween(getWestExtreme(), getEastExtreme()));
//        }
//
//        /**
//         * Updates the extreme values for polygons whose edges are interpreted as line segments in a
//         * flat plane (Euclidean).
//         */
//        private void updateEuclidean()
//        {
//            // Get a list of lat/lon coordinates which are consistent with west-east
//            final double[] coords = getDenormalCoords();
//            if (coords == null) { return; }
//
//            // Find the extreme denormalized coordinate values
//            double minLat = coords[0];
//            double maxLat = coords[0];
//            double minLon = coords[1];
//            double maxLon = coords[1];
//
//            // Also, find the indices of the extreme vertices
//            this.iSouthExtreme = 0;
//            this.iNorthExtreme = 0;
//            this.iWestExtreme = 0;
//            this.iEastExtreme = 0;
//
//            // Loop over remaining vertices
//            // We can skip the last one, since the polygon should be closed.
//            for (int i = 1; i < getNumVertices(); ++i)
//            {
//                final double lat = coords[2 * i];
//                if (lat < minLat)
//                {
//                    minLat = lat;
//                    this.iSouthExtreme = i;
//                }
//                else if (maxLat < lat)
//                {
//                    maxLat = lat;
//                    this.iNorthExtreme = i;
//                }
//
//                final double lon = coords[2 * i + 1];
//                if (lon < minLon)
//                {
//                    minLon = lon;
//                    this.iWestExtreme = i;
//                }
//                else if (maxLon < lon)
//                {
//                    maxLon = lon;
//                    this.iEastExtreme = i;
//                }
//            }
//
//            this.northExtreme = null;
//            this.southExtreme = null;
//
//            this.spansAllLongitude = (360. <= getWidthLongitude());
//        }
//
//        /**
//         * Updates the extreme values for polygons whose edges are interpreted as arcs of great
//         * circles (Spherical).
//         */
//        private void updateSpherical()
//        {
//            // Start with the usual Euclidean extents
//            updateEuclidean();
//
//            // Include the great circle midpoints of the
//            // top and bottom of the Euclidean bounding box.
//            // The sides are meridians which are great circles.
//
//            final GCPoint nwExtreme = new GCPoint(getNorthExtreme(), getWestExtreme());
//            final GCPoint neExtreme = new GCPoint(getNorthExtreme(), getEastExtreme());
//            GCPoint newNorthExtreme = new GCPoint(Vector3D.getArcMidPoint(nwExtreme.vector(), neExtreme.vector()));
//            if (newNorthExtreme.latitude().isNorthOf(getNorthExtreme()))
//            {
//                this.northExtreme = newNorthExtreme.latitude();
//            }
//
//            final GCPoint swExtreme = new GCPoint(getSouthExtreme(), getWestExtreme());
//            final GCPoint seExtreme = new GCPoint(getSouthExtreme(), getEastExtreme());
//            GCPoint newSouthExtreme = new GCPoint(Vector3D.getArcMidPoint(swExtreme.vector(), seExtreme.vector()));
//            if (newSouthExtreme.latitude().isSouthOf(getSouthExtreme()))
//            {
//                this.southExtreme = newSouthExtreme.latitude();
//            }
//        }
//    }
//
//    /**
//     * Holds this <code>Polygon</code>'s extreme values.
//     */
//    private Extrema extrema;
//
//    /**
//     * The counter-clockwise (CCW) orientation is arbitrarily assigned the boolean value
//     * <code>true</code>.
//     * 
//     * @see #isOrientated()
//     */
//    public final static boolean CCW = true;
//
//    /**
//     * Constructs a Euclidean polygon.
//     * 
//     * @param vert Array of vertices in clockwise or counterclockwise order.
//     * @see #setEuclidean()
//     */
//    public Polygon(GCPoint[] vert)
//    {
//        this(true, vert);
//    }
//
//    /**
//     * Constructs a Euclidean polygon.
//     * 
//     * @param vert Array of vertices in clockwise or counterclockwise order.
//     * @see #setEuclidean()
//     */
//    public Polygon(List<GCPoint> vertices)
//    {
//        this(true, vertices);
//    }
//
//    /**
//     * Constructs a polygon.
//     * 
//     * @param euclidean <code>true</code> for Euclidean. <code>false</code> for spherical.
//     * @param vert Array of vertices in clockwise or counterclockwise order.
//     */
//    public Polygon(boolean euclidean, GCPoint[] vert)
//    {
//        this.euclidean = euclidean;
//
//        // If vertices are not provided at construction time,
//        // they may be provided later by setVertices()
//        if (vert != null)
//        {
//            this.setVertices(vert);
//        }
//    }
//
//    /**
//     * Constructs a polygon.
//     * 
//     * @param euclidean <code>true</code> for Euclidean. <code>false</code> for spherical.
//     * @param vert Array of vertices in clockwise or counterclockwise order.
//     */
//    public Polygon(boolean euclidean, List<GCPoint> vertices)
//    {
//        this(euclidean, vertices.toArray(new GCPoint[0]));
//    }
//
//    /**
//     * Constructs a Euclidean polygon.
//     * 
//     * @param latLongPts Array of vertices in clockwise or counterclockwise order. Each vertex is
//     *            represented as 2 <code>double</code>s: latitude first, longitude second.
//     */
//    public Polygon(double[] latLonPts)
//    {
//        this(true, latLonPts);
//    }
//
//    /**
//     * Constructs a polygon.
//     * 
//     * @param euclidean <code>true</code> for Euclidean. <code>false</code> for spherical.
//     * @param latLongPts Array of vertices in clockwise or counterclockwise order. Each vertex is
//     *            represented as 2 <code>double</code>s: latitude first, longitude second (all ordinates are in degrees, longitude is east-positive).
//     */
//    public Polygon(boolean euclidean, double[] latLonPts)
//    {
//        this.euclidean = euclidean;
//        this.setVertices(latLonPts);
//    }
//
//    /**
//     * Constructs a copy of another polygon by creating a deep copy.
//     * 
//     * @param poly The original polygon.
//     */
//    public Polygon(Polygon poly)
//    {
//        // Copy constructor: create new objects with equal values
//
//        this.euclidean = poly.euclidean;
//
//        this.vertices = new GCPoint[poly.vertices.length];
//        for (int i = 0; i < this.vertices.length; ++i)
//        {
//            this.vertices[i] = new GCPoint(poly.vertices[i]);
//        }
//
//        this.extrema = new Extrema(poly.extrema);
//    }
//
//    /**
//     * @return <code>true</code> if this <code>Polygon</code>'s edges are interpreted as line
//     *         segments in a flat plane (Euclidean), <code>false</code> if this <code>Polygon</code>
//     *         's edges are interpreted as arcs of great circles (spherical).
//     */
//    public boolean getEuclidean()
//    {
//        return this.euclidean;
//    }
//
//    /**
//     * @return An array of this <code>Polygon</code>'s vertices as {@link GCPoint}s. A copy of
//     *         vertices is returned to prevent users from manipulating the original vertices object.
//     */
//    public GCPoint[] getVertices()
//    {
//        // Perform a deep copy of the array & its elements
//
//        GCPoint[] copy = new GCPoint[this.vertices.length];
//        for (int i = 0; i < this.vertices.length; ++i)
//        {
//            copy[i] = new GCPoint(this.vertices[i]);
//        }
//        return copy;
//    }
//
//    /**
//     * @return The {@link Longitude} of this <code>Polygon</code>'s easternmost vertex.
//     */
//    public Longitude getEastExtreme()
//    {
//        return this.vertices[this.extrema.iEastExtreme].longitude().normalized();
//    }
//
//    /**
//     * @return The {@link Longitude} of this <code>Polygon</code>'s westernmost vertex.
//     */
//    public Longitude getWestExtreme()
//    {
//        return this.vertices[this.extrema.iWestExtreme].longitude().normalized();
//    }
//
//    /**
//     * @return The {@link Latitude} of this <code>Polygon</code>'s northernmost vertex, or (if
//     *         spherical) a value guaranteed to be at least as far north as any point on any edge.
//     */
//    public Latitude getNorthExtreme()
//    {
//        return this.extrema.northExtreme != null ? new Latitude(this.extrema.northExtreme) : new Latitude(this.vertices[this.extrema.iNorthExtreme].latitude());
//    }
//
//    /**
//     * @return The {@link Latitude} of this <code>Polygon</code>'s southernmost vertex, or (if
//     *         spherical) a value guaranteed to be at least as far south as any point on any edge.
//     */
//    public Latitude getSouthExtreme()
//    {
//        return this.extrema.southExtreme != null ? new Latitude(this.extrema.southExtreme) : new Latitude(this.vertices[this.extrema.iSouthExtreme].latitude());
//    }
//
//    /**
//     * @return The number of vertices in this polygon.
//     */
//    public int getNumVertices()
//    {
//        // Valid Polygons are closed; therefore,
//        // the array must have a least 4 points to be valid.
//        if (this.vertices == null || this.vertices.length < 4) { return 0; }
//
//        // Valid Polygons are closed; therefore,
//        // the # of vertices is 1 less than the array length.
//        return (this.vertices.length - 1);
//    }
//
//    /**
//     * @return An array of this polygon's vertices' latitude-longitude values in degrees such that
//     *         if vertex A is west of vertex B then the given longitude value of A is less than the
//     *         given longitude value for B by adding or subtracting multiples of 360 degrees to
//     *         succeeding vertices.
//     */
//    private double[] getDenormalCoords()
//    {
//        return getDenormalCoords(this.vertices);
//    }
//
//    /**
//     * Transforms normal coordinate values by the method explained below.
//     * 
//     * @param vertexArray array of {@link GCPoint}s
//     * @return An array of latitude-longitude values in degrees of the vertexArray points such that
//     *         if point A is west of point B then the given longitude value of A is less than the
//     *         given longitude value for B by adding or subtracting multiples of 360 degrees to
//     *         succeeding vertices.
//     */
//    private static double[] getDenormalCoords(GCPoint[] vertexArray)
//    {
//
//        // Return coordinate values are in degrees.
//
//        if (vertexArray == null || vertexArray.length < 1) { return null; }
//
//        final double[] result = new double[vertexArray.length * 2];
//
//        // The first point is NOT denormalized
//        result[0] = vertexArray[0].latitude().degrees();
//        result[1] = vertexArray[0].longitude().degrees();
//
//        // Keep track of the current longitude offset:
//        // the numerical # of full circles to add to the
//        // the normal value of longitude.
//        // It starts out as 0, and should end up as 0.
//        int nSeemCrossings = 0;
//
//        // Loop over the remaining vertices
//        for (int i = 1; i < vertexArray.length; ++i)
//        {
//            // Get the normal values in degrees
//            result[2 * i] = vertexArray[i].latitude().degrees();
//
//            final int iLonCurr = 2 * i + 1;
//            final int iLonPrev = iLonCurr - 2;
//
//            result[iLonCurr] = vertexArray[i].longitude().degrees() + nSeemCrossings * 360.0;
//
//            // Make ordering consistent with the concept of "west" in isWestOf()
//            if (vertexArray[i - 1].isWestOf(vertexArray[i]))
//            {
//                if (result[iLonCurr] < result[iLonPrev])
//                {
//                    // We must have crossed a seem going from west to east
//                    // So, we add 1 full circle of longitude
//                    result[iLonCurr] += 360;
//                    ++nSeemCrossings;
//                }
//            }
//            else if (vertexArray[i].isWestOf(vertexArray[i - 1]))
//            {
//                if (result[iLonPrev] < result[iLonCurr])
//                {
//                    // We must have crossed a seem going from east to west
//                    // So, we subtract 1 full circle of longitude
//                    result[iLonCurr] -= 360;
//                    --nSeemCrossings;
//                }
//            }
//            // No further correction should be needed
//            /*
//             * else // the edge is north-south { // Set this longitude equal to the previous
//             * longitude result[iLonCurr] = result[iLonPrev]; }
//             */
//        }
//
//        if (nSeemCrossings != 0) { throw new RuntimeException("Polygon contains a pole"); }
//
//        return result;
//    }
//
//    /**
//     * Determines if a polygon defined by the given array crosses itself.
//     * 
//     * @param vertexArray array of {@link GCPoint}s
//     * @return <code>true</code> if any edge intersects any non-adjacent edge; false otherwise.
//     */
//    private static boolean isSelfCrossingEuclidean(GCPoint[] theVertexArray)
//    {
//        // Return true iff any 2 sides intersect
//
//        // We assume that the vertex array is CLOSED!
//        GCPoint[] vertexArray = theVertexArray;
//        vertexArray = getClosedArray(vertexArray);
//
//        // Loop over all pairs of edges:
//        // The outer index, iEdge, can skip the last 2 edges because
//        // the (nEdges-2)th is adjacent to the (nEdges-1)th edge.
//        // Moreover, if there are only 3 edges (a triangle),
//        // then each edge is adjacent to the other 2 edges
//        final int nEdges = vertexArray.length - 1;
//        for (int iEdge = 0; iEdge < nEdges - 2; ++iEdge)
//        {
//            // Let:
//            // A = the first endpoint of the 1st edge
//            // B = the other endpoint of the 1st edge (translated)
//
//            double Ax = vertexArray[iEdge].longitude().degrees();
//            double Ay = vertexArray[iEdge].latitude().degrees();
//
//            // 1. Translate such that A is at origin.
//            double Bx = vertexArray[iEdge + 1].longitude().degrees() - Ax;
//            double By = vertexArray[iEdge + 1].latitude().degrees() - Ay;
//
//            // 2. Normalize the difference in longitudes
//            Bx = Longitude.valueOfDegrees(Bx).normalized().degrees();
//
//            // Get cosine & sine of line AB
//            // Default is an angle of 0 (no rotation)
//            double ABcos = 1;
//            double ABsin = 0;
//            final double ABlength = Math.sqrt(Bx * Bx + By * By);
//            if (0 < ABlength)
//            {
//                ABcos = Bx / ABlength;
//                ABsin = By / ABlength;
//            }
//
//            // The inner index, jEdge, can skip the last edge
//            // when the outer index is the first edge
//            for (int jEdge = iEdge + 2; jEdge < nEdges - (iEdge == 0 ? 1 : 0); ++jEdge)
//            {
//                // Do NOT compare adjacent edges
//                // They are supposed to intersect
//                final boolean areAdjacent = ((jEdge - iEdge) % nEdges == 1) || ((jEdge - iEdge) % nEdges == nEdges - 1);
//                if (areAdjacent)
//                {
//                    continue;
//                }
//
//                // Let:
//                // C = the first endpoint of the 2nd edge
//                // D = the other endpoint of the 2nd edge
//
//                // 1. Translate such that A is at origin.
//                double Cx = vertexArray[jEdge].longitude().degrees() - Ax;
//                double Cy = vertexArray[jEdge].latitude().degrees() - Ay;
//                double Dx = vertexArray[jEdge + 1].longitude().degrees() - Ax;
//                double Dy = vertexArray[jEdge + 1].latitude().degrees() - Ay;
//
//                // 2. Normalize the difference in longitudes
//                Cx = Longitude.valueOfDegrees(Cx).normalized().degrees();
//                Dx = Cx + Longitude.valueOfDegrees(Dx - Cx).normalized().degrees();
//
//                // 3. Rotate such that B is on the positive X-axis.
//                if (ABcos != 1)
//                {
//                    double newCx = Cx * ABcos + Cy * ABsin;
//                    Cy = Cy * ABcos - Cx * ABsin;
//                    Cx = newCx;
//
//                    double newDx = Dx * ABcos + Dy * ABsin;
//                    Dy = Dy * ABcos - Dx * ABsin;
//                    Dx = newDx;
//                }
//
//                // 4. Find where line CD crosses the X-axis
//                boolean CDcrossesXaxis = (Cy <= 0 && 0 <= Dy) || (Dy <= 0 && 0 <= Cy);
//                if (CDcrossesXaxis)
//                {
//                    if (Cy != Dy)
//                    {
//                        final double xIntercept = (Dx - Cx) * Cy / (Cy - Dy) + Cx;
//                        if (0 <= xIntercept && xIntercept <= ABlength) { return true; }
//                    }
//                    else
//                    // Cy == Dy == 0
//                    {
//                        // If one point is greater than zero, and
//                        // one point is less than the length, then
//                        // at least part of it coincides with AB
//                        if ((0 <= Cx || 0 <= Dx) && (Cx <= ABlength || Dx <= ABlength)) { return true; }
//                    }
//                }
//            }
//        }
//
//        return false;
//    }
//
//    /**
//     * Determines if a spherical polygon defined by the given array crosses itself.
//     * 
//     * @param vertexArray array of {@link GCPoint}s
//     * @return <code>true</code> if any edge intersects any non-adjacent edge; false otherwise.
//     */
//    private static boolean isSelfCrossingSpherical(GCPoint[] theVertexArray)
//    {
//        // Return true iff any 2 sides intersect
//
//        // We assume that the vertex array is CLOSED!
//        GCPoint[] vertexArray = theVertexArray;
//        vertexArray = getClosedArray(vertexArray);
//
//        // Loop over all pairs of edges:
//        // The outer index, iEdge, can skip the last 2 edges because
//        // the (nEdges-2)th is adjacent to the (nEdges-1)th edge.
//        // Moreover, if there are only 3 edges (a triangle),
//        // then each edge is adjacent to the other 2 edges
//        final int nEdges = vertexArray.length - 1;
//        for (int iEdge = 0; iEdge < nEdges - 2; ++iEdge)
//        {
//            // The inner index, jEdge, can skip the last edge
//            // when the outer index is the first edge
//            for (int jEdge = iEdge + 2; jEdge < nEdges - (iEdge == 0 ? 1 : 0); ++jEdge)
//            {
//                try
//                {
//                    // use spherical geometry
//                    // I am not sure when this is true and euclidean would be false
//                    Vector3D interVector = SphericalUtilities.intersection(vertexArray[iEdge].vector(), vertexArray[iEdge + 1].vector(), vertexArray[jEdge].vector(), vertexArray[jEdge + 1].vector(), SphericalUtilities.IntersectionType.NONSTRICT);
//                    if (interVector != null)
//                    {
//                        // throw new RuntimeException(
//                        // "Edge "+iEdge+" intersects edge "+jEdge+" at "+new GCPoint(interVector));
//
//                        return true;
//                    }
//                }
//                catch (InvalidInputException e)
//                {
//                }
//            }
//        }
//
//        return false;
//    }
//
//    /**
//     * Determines if the given array of vertices is closed.
//     * 
//     * @param vertexArray Array of vertices
//     * @return <code>true</code> if the last vertex of <code>vertexArray</code> equals the first.
//     * @see #close()
//     */
//    private static boolean isClosed(GCPoint[] vertexArray)
//    {
//        // Returns true if the first and last points refer to same object.
//        return vertexArray != null && 1 <= vertexArray.length && vertexArray[0] == vertexArray[vertexArray.length - 1];
//    }
//
//    /**
//     * Determines if the internal representation of this polygon is closed.
//     * 
//     * @return <code>true</code> if the last vertex in <code>vertices</code> equals the first.
//     * @see #close()
//     */
//    private boolean isClosed()
//    {
//        return isClosed(this.vertices);
//    }
//
//    /**
//     * Determines if the internal representation of this polygon is ordered CCW.
//     * 
//     * @return <code>true</code> if the vertices are in CCW order; otherwise, <code>false</code>.
//     */
//    private boolean isOrientated()
//    {
//        return isOrientated(CCW);
//    }
//
//    /**
//     * Determines if the internal representation of this polygon is ordered as the given argument.
//     * 
//     * @param desiredOrientationIsCcw <code>true</code> for CCW / <code>false</code> for CW
//     * @return <code>true</code> if the vertices' order are in the given order; otherwise,
//     *         <code>false</code>.
//     */
//    private boolean isOrientated(boolean desiredOrientationIsCcw)
//    {
//        // If there are no vertices, we can't orientate.
//        // If there are fewer than 3 vertices, we can't orientate.
//        if (this.vertices == null || getNumVertices() < 3) { return false; }
//
//        // In case the extrema are not up to date
//        updateExtrema();
//
//        // Use any extreme vertex - arbitrarily choose something on the western edge
//        // The angle at an extreme vertex is an interior angle
//        // The polygon is "locally convex" at that vertex
//        final int iExtremePt = this.extrema.iWestExtreme;
//        final int iPrev = (iExtremePt + getNumVertices() - 1) % getNumVertices();
//        final int iNext = (iExtremePt + 1) % getNumVertices();
//
//        // Use denormalized coordinates so we don't have to
//        // worry about crossing the 180 degrees longitude line
//        final double[] coords = getDenormalCoords();
//
//        // Use atan2 to compute Euclidean headings from the
//        // extreme point to the the next and previous vertices
//        final double directionForward = Math.atan2(coords[2 * iNext + 1] - coords[2 * iExtremePt + 1], coords[2 * iNext] - coords[2 * iExtremePt]);
//        final double directionBackward = Math.atan2(coords[2 * iPrev + 1] - coords[2 * iExtremePt + 1], coords[2 * iPrev] - coords[2 * iExtremePt]);
//
//        // Because we chose a western extreme vertex, we
//        // expect both of these directions to be within [0,+PI].
//
//        final boolean orienationIsCcw = (directionBackward < directionForward);
//
//        // The polygon is oriented if the desired orientation
//        // agrees with the current orientation.
//        return (desiredOrientationIsCcw == orienationIsCcw);
//    }
//
//    /**
//     * Determines if this {@link Polygon} is valid:
//     * <ul>
//     * <li>{@link Polygon} passes {{@link #verticesValid(boolean, GCPoint[])}</li>
//     * <li>Internal representation of the vertices {@link #isClosed()}</li>
//     * <li>The {@link Polygon} {@link #isOriented()} (i.e. ordered counter clockwise with respect to
//     * gravity)</li>
//     * </ul>
//     * 
//     * @return <code>true</code> if {@link Polygon} passes the aforementioned tests,
//     *         <code>false</code> otherwise
//     * @see #validate
//     */
//    public boolean isValid()
//    {
//        return verticesValid(this.euclidean, this.vertices) && isClosed() && isOrientated();
//    }
//
//    /**
//     * Validates the given edge interpretation and array of points to see if they can represent a
//     * polygon using the following checks:
//     * <ul>
//     * <li>There are no fewer than 3 unique vertices</li>
//     * <li>For Euclidean polygons, no edge spans 180 degrees of longitude</li>
//     * <li>For spherical polygons, no edge spans 180 degrees in length</li>
//     * </ul>
//     * 
//     * @param euclid how to interpret edges
//     * @param vertexArray array of points to consider
//     * @throws RuntimeException if inputs are invalid.
//     */
//    private static boolean verticesValid(boolean euclid, GCPoint[] vertexArray)
//    {
//        if (vertexArray == null || vertexArray.length < 3) { return false; }
//
//        // Ensure the array is sufficiently large
//        Set<GCPoint> vertexSet = new TreeSet<GCPoint>(); // Use set to ensure each point is unique
//        List<GCPoint> vertexList = new ArrayList<GCPoint>(vertexArray.length); // Use list to store
//                                                                               // unique points, in
//                                                                               // order
//        for (int i = 0; i < vertexArray.length - 1; ++i) // Check all vertices except the last
//                                                         // (which equals the first)
//        {
//            // Ensure each vertex is constructed
//            if (vertexArray[i] == null)
//            {
//                StringBuilder vertexString = new StringBuilder();
//                for (GCPoint point : vertexArray)
//                {
//                    vertexString.append(point.toString() + ", ");
//                }
//                logger.info("Vertex " + i + " in (" + vertexString.toString() + ") is null.");
//                return false;
//            }
//
//            // All vertices must be unique. The compareTo function used by the TreeSet
//            // might give a slightly different result than the equals function
//            // above, so we keep the above test to ensure no edge has 0 length.
//            final boolean unique = vertexSet.add(vertexArray[i]);
//            if (!unique)
//            {
//                StringBuilder vertexString = new StringBuilder();
//                for (GCPoint point : vertexArray)
//                {
//                    vertexString.append(point.toString() + ", ");
//                }
//                logger.info("Vertex " + i + " in " + vertexString.toString() + " is a duplicate.");
//                return false;
//            }
//
//            // Do not allow poles as vertices in Euclidean polygons
//            if (euclid)
//            {
//                if (Math.abs(vertexArray[i].latitude().degrees()) == 90)
//                {
//                    logger.info("Vertex " + i + " is a pole which is not supported for Euclidean polygons");
//                    return false;
//                }
//            }
//
//            // In Euclidean geometry, when longitudes are separated by exactly
//            // 180 degrees, we don't know if the edge should go east or west.
//            // To avoid ambiguity, consecutive points must be
//            // separated by less than 180 degrees of longitude.
//            //
//            // In spherical geometry, when longitudes are separated by exactly
//            // 180 degrees, the great circle edge must go through a pole.
//            // Spherical geometry does not have a problem with ambiguity
//            // unless the points are antipodes (180 degrees of longitude and
//            // opposite latitudes).
//
//            if (euclid)
//            {
//                if (vertexArray[i].longitude().isOppositeTo(vertexArray[i + 1].longitude()))
//                {
//                    logger.info("Vertex " + i + " and its successor are separated by exactly 180 degrees of longitude");
//                    return false;
//                }
//            }
//            else
//            // spherical geometry
//            {
//                if (vertexArray[i].isAntipodeTo(vertexArray[i + 1]))
//                {
//                    logger.info("Vertex " + i + " and its successor are antipodes making the edge ambiguous");
//                    return false;
//                }
//            }
//
//            vertexList.add(vertexArray[i]); // Only keep vertices that pass all tests
//        }
//
//        // Ensure the array is sufficiently large (again)
//        if (vertexArray.length < 4)
//        {
//            logger.info("Insufficient number of vertices");
//            return false;
//        }
//
//        // Check the polygon as a whole
//        // -------------------------------------------------------------------------------
//        if ((euclid && isSelfCrossingEuclidean(vertexArray)) || (!euclid && isSelfCrossingSpherical(vertexArray)))
//        {
//            logger.info("Vertex array crosses itself");
//            return false;
//        }
//
//        return true;
//    }
//
//    /**
//     * Validates the given edge interpretation and array of points to see if they can represent a
//     * polygon using the following checks:
//     * <ul>
//     * <li>There are no fewer than 3 unique vertices</li>
//     * <li>For Euclidean polygons, no edge spans 180 degrees of longitude</li>
//     * <li>For spherical polygons, no edge spans 180 degrees in length</li>
//     * </ul>
//     * 
//     * @param euclid how to interpret edges
//     * @param vertexArray array of points to consider
//     * @throws RuntimeException if inputs are invalid.
//     */
//    private static List<GCPoint> validVertices(boolean euclid, GCPoint[] theVertexArray)
//    {
//        GCPoint[] vertexArray = theVertexArray;
//        List<GCPoint> vertexList = null; // Use list to store unique points
//        
//        if (vertexArray != null && vertexArray.length > 0)
//        {
//            vertexList = new ArrayList<GCPoint>(vertexArray.length); // Use list to store unique points
//            
//            // Close the given array
//            vertexArray = getClosedArray(vertexArray);
//
//            // Ensure the array is sufficiently large
//            Set<GCPoint> vertexSet = new TreeSet<GCPoint>(); // Use set to ensure each point is unique
//            for (int i = 0; i < vertexArray.length - 1; ++i) // Check all vertices except the last (which equals the first)
//            {
//                // Ensure each vertex is constructed
//                if (vertexArray[i] == null)
//                {
//                    logger.warn("Vertex " + i + " in " + vertexArray + " is null. Skipping vertex.");
//                    continue;
//                }
//
//                // All vertices must be unique. The compareTo function used by the TreeSet
//                // might give a slightly different result than the equals function
//                // above, so we keep the above test to ensure no edge has 0 length.
//                final boolean unique = vertexSet.add(vertexArray[i]);
//                if (!unique)
//                {
//                    logger.warn("Vertex " + i + " in " + vertexArray + " is a duplicate. Skipping vertex.");
//                    continue;
//                }
//
//                // Do not allow poles as vertices in Euclidean polygons
//                if (euclid)
//                {
//                    if (Math.abs(vertexArray[i].latitude().degrees()) == 90) { throw new RuntimeException("Vertex " + i + " is a pole which is not supported for Euclidean polygons"); }
//                }
//
//                // In Euclidean geometry, when longitudes are separated by exactly
//                // 180 degrees, we don't know if the edge should go east or west.
//                // To avoid ambiguity, consecutive points must be
//                // separated by less than 180 degrees of longitude.
//                //
//                // In spherical geometry, when longitudes are separated by exactly
//                // 180 degrees, the great circle edge must go through a pole.
//                // Spherical geometry does not have a problem with ambiguity
//                // unless the points are antipodes (180 degrees of longitude and
//                // opposite latitudes).
//                if (euclid)
//                {
//                    if (vertexArray[i].longitude().isOppositeTo(vertexArray[i + 1].longitude())) { throw new RuntimeException("Vertex " + i + " and its successor are separated by exactly 180 degrees of longitude"); }
//                }
//                else
//                // Spherical geometry
//                {
//                    if (vertexArray[i].isAntipodeTo(vertexArray[i + 1])) { throw new RuntimeException("Vertex " + i + " and its successor are antipodes making the edge ambiguous"); }
//                }
//
//                vertexList.add(vertexArray[i]); // Only keep vertices that pass all tests
//            }
//        }
//
//        return vertexList; // Return a list of the points considered valid for a Polygon
//    }
//
//    /**
//     * Computes the difference between the maximum and minimum longitude values of the
//     * "denormalized" coordinates in degrees.
//     * 
//     * @return the number of degrees of longitude traversed by walking between this polygon's
//     *         easternmost and westernmost points.
//     * @see #getDenormalCoords()
//     */
//    public double getWidthLongitude()
//    {
//        double[] latLons = getDenormalCoords();
//        double minLon = latLons[1];
//        double maxLon = latLons[1];
//        for (int iLon = 3; iLon < latLons.length; iLon += 2)
//        {
//            if (latLons[iLon] < minLon)
//            {
//                minLon = latLons[iLon];
//            }
//
//            if (maxLon < latLons[iLon])
//            {
//                maxLon = latLons[iLon];
//            }
//        }
//
//        return (maxLon - minLon);
//    }
//
//    /**
//     * Determines if this polygon contains a given test point.
//     * 
//     * @param latitude The latitude in degrees of the test point.
//     * @param longitude The longitude in degrees of the test point.
//     * @return <code>true</code> if and only if the test point lies in the interior of the polygon,
//     *         on an edge or is a vertex.
//     */
//    public boolean contains(double latitude, double longitude)
//    {
//        return contains(new GCPoint(Latitude.valueOfDegrees(latitude), Longitude.valueOfDegrees(longitude)));
//    }
//
//    /**
//     * Determines if this polygon contains a given test point.
//     * 
//     * @param pointToCheck The given test point.
//     * @return <code>true</code> if and only if the test point lies in the interior of the polygon,
//     *         on an edge or is a vertex.
//     */
//    public boolean contains(GCPoint pointToCheck)
//    {
//        if (this.euclidean) { return containsEuclidean(pointToCheck); }
//
//        return containsSpherical(pointToCheck);
//    }
//
//    /**
//     * Determines if this polygon contains a given test point. This is only called for Euclidean
//     * polygons.
//     * 
//     * @param testPoint The given test point.
//     * @return <code>true</code> if and only if the test point lies in the interior of the polygon,
//     *         on an edge or is a vertex.
//     */
//    private boolean containsEuclidean(GCPoint testPoint)
//    {
//        // Check the bounding box for optimization
//        if (!this.extrema.contains(testPoint)) { return false; }
//
//        // Initially zero edges which is EVEN, not ODD
//        boolean pointIsInside = false;
//
//        final double testPtLatRads = testPoint.latitude().radians();
//        final double testPtLonRads = testPoint.longitude().radians();
//
//        // Loop over all edges
//        // ----------------------------------------------------------------------------------------
//        final int nEdges = getNumVertices();
//        for (int iEdge = 0; iEdge < nEdges; ++iEdge)
//        {
//            // Get references to the edge end points
//            // The edge include pt1, excludes pt2
//            final GCPoint pt1 = this.vertices[iEdge];
//            final GCPoint pt2 = this.vertices[iEdge + 1];
//
//            // Get the respective longitudes (in radians)
//            double pt1LonRads = pt1.longitude().radians();
//            double pt2LonRads = pt2.longitude().radians();
//
//            // Determine if test point longitude falls between the endpoint longitudes
//            // If the edge is NOT north-south, use testLonIsBetween
//            boolean testLonIsBetween = false;
//            if (pt1.isWestOf(pt2)) // Eastward edge
//            {
//                if (pt1LonRads < pt2LonRads)
//                {
//                    if (testPtLonRads == pt1LonRads)
//                    {
//                        // RARE CASE:
//                        // We must check to see that the current edge and the previous edge
//                        // are on opposite sides of this longitude; so,
//                        // The test point must be:
//                        // (a) EQUAL in latitude to the current point OR
//                        // (b) EQUAL in longitude to the previous point OR
//                        // (c) EAST of the previous point
//
//                        // If the test point also equals in latitude,
//                        // then it is equal to the vertex
//                        // and we may short circuit this logic
//                        if (testPtLatRads == pt1.latitude().radians()) { return true; }
//
//                        // Get the previous point
//                        final GCPoint pt0 = this.vertices[(iEdge + nEdges - 1) % nEdges];
//
//                        testLonIsBetween = testPtLonRads == pt0.longitude().normalized().radians() || testPoint.isEastOf(pt0);
//                    }
//                    else
//                    {
//                        testLonIsBetween = (pt1LonRads < testPtLonRads && testPtLonRads < pt2LonRads);
//                    }
//                }
//                else
//                // pt2LonRads < pt1LonRads (crosses seam)
//                {
//                    if (pt1LonRads <= testPtLonRads)
//                    {
//                        testLonIsBetween = true;
//                        pt2LonRads += 2 * Math.PI;
//                    }
//                    else if (testPtLonRads < pt2LonRads)
//                    {
//                        testLonIsBetween = true;
//                        pt1LonRads -= 2 * Math.PI;
//                    }
//                }
//            }
//            else if (pt2.isWestOf(pt1)) // Westward edge
//            {
//                if (pt2LonRads < pt1LonRads)
//                {
//                    testLonIsBetween = (pt2LonRads < testPtLonRads && testPtLonRads <= pt1LonRads);
//
//                    if (testPtLonRads == pt1LonRads)
//                    {
//                        // RARE CASE:
//                        // We must check to see that the current edge and the previous edge
//                        // are on opposite sides of this longitude; so,
//                        // The test point must be:
//                        // (a) EQUAL in latitude to the current point OR
//                        // (b) EQUAL in longitude to the previous point OR
//                        // (c) WEST of the previous point
//
//                        // If the test point also equals in latitude,
//                        // then it is equal to the vertex
//                        // and we may short circuit this logic
//                        if (testPtLatRads == pt1.latitude().radians()) { return true; }
//
//                        // Get the previous point
//                        final GCPoint pt0 = this.vertices[(iEdge + nEdges - 1) % nEdges];
//
//                        testLonIsBetween = testPtLonRads == pt0.longitude().normalized().radians() || testPoint.isWestOf(pt0);
//                    }
//                    else
//                    {
//                        testLonIsBetween = (pt2LonRads < testPtLonRads && testPtLonRads < pt1LonRads);
//                    }
//                }
//                else
//                // pt2LonRads < pt1LonRads
//                {
//                    if (pt2LonRads < testPtLonRads)
//                    {
//                        testLonIsBetween = true;
//                        pt1LonRads += 2 * Math.PI;
//                    }
//                    else if (testPtLonRads <= pt1LonRads)
//                    {
//                        testLonIsBetween = true;
//                        pt2LonRads -= 2 * Math.PI;
//                    }
//                }
//            }
//            else
//            // North-South Edge
//            {
//                final double pt1LatRads = pt1.latitude().radians();
//                final double pt2LatRads = pt2.latitude().radians();
//
//                // If the test point lies on the same longitude
//                if (testPtLonRads == pt1LonRads)
//                {
//                    // The test point is on a north-south edge if its latitude is between the
//                    // latitude end-points
//                    boolean isOnEdge = false;
//
//                    if (pt1LatRads < pt2LatRads) // Northward edge
//                    {
//                        isOnEdge = (pt1LatRads <= testPtLatRads && testPtLatRads < pt2LatRads);
//
//                    }
//                    else
//                    // pt2LatRads < pt1LatRads (southward edge)
//                    {
//                        isOnEdge = (pt2LatRads < testPtLatRads && testPtLatRads <= pt1LatRads);
//                    }
//
//                    if (isOnEdge) { return true; }
//
//                    // If the test point is not on the edge,
//                    // we count it if it is south of the test point
//                    if (pt1LatRads < testPtLatRads)
//                    {
//                        pointIsInside = !pointIsInside;
//                    }
//                }
//            }
//
//            if (testLonIsBetween) // this is only true for NON-north-south edges
//            {
//                // Compute the intersection latitude of the test point's longitude with the edge
//                final double pt1LatRads = pt1.latitude().radians();
//                final double pt2LatRads = pt2.latitude().radians();
//                double intersectLatRads = pt1LatRads + (testPtLonRads - pt1LonRads) / (pt2LonRads - pt1LonRads) * (pt2LatRads - pt1LatRads);
//
//                // If the intersection is at or south of the point, then count it
//                if (intersectLatRads < testPtLatRads)
//                {
//                    pointIsInside = !pointIsInside;
//                }
//                else if (intersectLatRads == testPtLatRads)
//                {
//                    // The test point is on the edge, return true
//                    return true;
//                }
//            }
//
//        }
//
//        return pointIsInside;
//    }
//
//    /**
//     * Determines if this polygon contains a given test point. This is only called for spherical
//     * polygons.
//     * 
//     * @param testPt The given test point.
//     * @return <code>true</code> if and only if the test point lies in the interior of the polygon,
//     *         on an edge or is a vertex.
//     */
//    private boolean containsSpherical(GCPoint testPt)
//    {
//        if (getNumVertices() < 3 || !isClosed() || !this.extrema.contains(testPt)) { return false; }
//
//        // Adapted point-in-polygon algorithm (http://alienryderflex.com/polygon/):
//        // Considering only edges with exactly one vertex west
//        // of the longitude of the given test point,
//        // count how many edges intersect a southward extended meridian.
//        // The count is odd for points within the polygon.
//
//        // Initially zero edges which is EVEN, not ODD
//        boolean oddEdges = false;
//
//        // Loop over all edges
//        // The polygon is closed, so the last vertex is a repeat of the first
//        final int nEdges = this.vertices.length - 1;
//        for (int iEdge = 0; iEdge < nEdges; ++iEdge)
//        {
//            // Assume eastward edge by default:
//            GCPoint westPt = this.vertices[iEdge];
//            GCPoint eastPt = this.vertices[iEdge + 1];
//
//            // Test the above direction assumption
//            // If we were wrong, swap and test again
//            if (!westPt.isWestOf(eastPt))
//            {
//                // Try assuming the edge is westward
//                eastPt = this.vertices[iEdge];
//                westPt = this.vertices[iEdge + 1];
//
//                // Test again. If wrong again,
//                // then the edge is neither eastward nor westward
//                // (it must be a north/south edge)
//                if (!westPt.isWestOf(eastPt))
//                {
//                    // Skip this edge
//                    continue;
//                }
//            }
//
//            // Determine if the test point's longitude is between
//            // the west and east points but NOT equal to the east point
//            boolean testLonIsBetween = testPt.longitude().isBetween(westPt.longitude(), eastPt.longitude()) && (testPt.longitude().radians() != eastPt.longitude().radians());
//
//            if (testLonIsBetween)
//            {
//                // The normal to the great circle from west to east
//                // points to somewhere in the northern hemisphere
//                final Vector3D gcNormal = westPt.vector().crossProduct(eastPt.vector());
//
//                // The point is south of the great circle if the dot product is
//                // negative or on the circle if the dot product is zero:
//                final boolean isSouthOfOrOnTheEdge = (testPt.vector().dotProduct(gcNormal) <= 0);
//
//                if (isSouthOfOrOnTheEdge)
//                {
//                    oddEdges = !oddEdges;
//                }
//            }
//        }
//
//        return oddEdges;
//    }
//
//    /**
//     * Sets how this <code>Polygon</code>'s edges are interpreted: as line segments in a flat plane
//     * (Euclidean) or as arcs of great circles (spherical).
//     * 
//     * @param euclid <code>true</code> for Euclidean / <code>false</code> for spherical.
//     * @see #getEuclidean
//     */
//    public void setEuclidean(boolean euclid)
//    {
//        this.euclidean = euclid;
//        updateExtrema();
//    }
//
//    /**
//     * Sets the vertices of this polygon. Validates the input array before setting. If the input is
//     * invalid, an exception is thrown.
//     * 
//     * @param gcPointArray Array of vertices in clockwise or counterclockwise order. Need not be
//     *            closed.
//     * @throws RuntimeException for invalid input
//     * @see #getVertices()
//     */
//    public void setVertices(GCPoint[] gcPointArray)
//    {
//        // Normalize all points in the array
//        for (int i = 0; i < gcPointArray.length; ++i)
//        {
//            gcPointArray[i].setLatitude(gcPointArray[i].latitude().normalized());
//            gcPointArray[i].setLongitude(gcPointArray[i].longitude().normalized());
//        }
//
//        List<GCPoint> validPoints = validVertices(this.euclidean, gcPointArray);
//
//        // Perform a deep copy of the array & its elements
//        this.vertices = new GCPoint[validPoints.size()];
//        for (int i = 0; i < validPoints.size(); i++)
//        {
//            this.vertices[i] = new GCPoint(validPoints.get(i));
//        }
//
//        // Nullify the extrema
//        // They are actually updated in orientate()!
//        nullifyExtrema();
//
//        // Update the internal state for consistency
//        // CALLING ORDER IS IMPORTANT HERE!
//        close();
//        orientate();
//        updateExtrema();
//    }
//
//    /**
//     * Sets the vertices of this polygon. Validates the input array before setting. If the input is
//     * invalid, an exception is thrown.
//     * 
//     * @param dataPoints Array of latitude and longitude values in degrees for each point in
//     *            clockwise or counterclockwise order. Need not be closed. Latitude is first, and
//     *            longitude is second: { lat0, lon0, lat1, lon1, ...}.
//     * @throws RuntimeException for invalid input
//     * @see #getVertices()
//     */
//    @Deprecated
//    public void setVertices(double[] dataPoints)
//    {
//        // dataPoints is list of lat/lon pairs:
//        // Latitude first, Longitude second
//        // datapoints = { lat0, lon0, lat1, lon1, ... }
//
//        // Validate Input
//        // ---------------------------------------------------------------------------------------------
//        if (dataPoints == null) { throw new RuntimeException("Array of point coordinates is null"); }
//
//        final boolean evenLength = (dataPoints.length % 2 == 0);
//        if (!evenLength) { throw new RuntimeException("Array is odd in length. Expected an even length list of doubles: lat,lon,..."); }
//
//        // Convert the lat/lon pairs to GCPoints
//        GCPoint[] newVertices = new GCPoint[dataPoints.length / 2];
//        for (int vert = 0; vert < dataPoints.length; vert += 2)
//        {
//            newVertices[vert / 2] = new GCPoint(Latitude.valueOfDegrees(dataPoints[vert]), Longitude.valueOfDegrees(dataPoints[vert + 1]));
//        }
//
//        newVertices = validVertices(this.euclidean, newVertices).toArray(new GCPoint[0]);
//
//        setVertices(newVertices);
//
//        if (!isValid()) { throw new RuntimeException("Polygon is not valid. Raising exception."); }
//    }
//
//    /**
//     * If the array is not closed, a new equivalent closed array is created; otherwise, a reference
//     * to the original array is returned.
//     * 
//     * @param vertexArray array of given {@link GCPoint}s
//     * @return
//     */
//    private static GCPoint[] getClosedArray(GCPoint[] theVertexArray)
//    {
//        GCPoint[] vertexArray = theVertexArray;
//        // If already closed, just return the original array
//        if (isClosed(vertexArray)) { return vertexArray; }
//
//        // If there are no vertices, we can't close.
//        // If there is only 1 vertex, it's already closed.
//        // And if it's already closed, we are done.
//        if (vertexArray == null || vertexArray.length <= 1) { return vertexArray; }
//
//        // If the first and last are not equal,
//        // then create a new array that is 1 element longer and
//        // copy the original values
//        if (!vertexArray[vertexArray.length - 1].equals(vertexArray[0]))
//        {
//            // Grow the vertices array by one
//            GCPoint[] tempVert = vertexArray;
//            vertexArray = new GCPoint[tempVert.length + 1];
//            for (int i = 0; i < tempVert.length; i++)
//            {
//                vertexArray[i] = tempVert[i];
//            }
//        }
//
//        // Make the last GCPoint the same reference to the first GCPoint
//        vertexArray[vertexArray.length - 1] = vertexArray[0];
//
//        return vertexArray;
//    }
//
//    /**
//     * Closes the internal representation of this polygon. The array of vertices is appended if
//     * necessary with a copy of a reference to the first vertex.
//     */
//    private void close()
//    {
//        this.vertices = getClosedArray(this.vertices);
//    }
//
//    /**
//     * Orients the internal representation of this polygon such that the vertices are in
//     * counter-clockwise order.
//     */
//    private void orientate()
//    {
//        orientate(CCW);
//    }
//
//    /**
//     * Orients the internal representation of this polygon such that the vertices are in the user's
//     * desired order.
//     * 
//     * @param desiredOrientationIsCcw <code>true</code> if the desired orientation is
//     *            counter-clockwise.
//     */
//    private void orientate(boolean desiredOrientationIsCcw)
//    {
//        // If already orientated, then we are done.
//        // If there are no vertices, we can't orientate.
//        // If there are fewer than 3 vertices, we can't orientate.
//        if (isOrientated(desiredOrientationIsCcw) || this.vertices == null || getNumVertices() < 3) { return; }
//
//        // Reversing the order of the vertices messes up the extrema indices,
//        // so we will update them after we are done here
//        nullifyExtrema();
//
//        // Reverse the order of the vertices array
//        for (int left = 0, right = this.vertices.length - 1; left < right; ++left, --right)
//        {
//            // Swap the left & the right
//            GCPoint temp = this.vertices[left];
//            this.vertices[left] = this.vertices[right];
//            this.vertices[right] = temp;
//        }
//
//        // Update the extrema
//        updateExtrema();
//    }
//
//    /**
//     * Resets this polygon's extrema before updating them.
//     * 
//     * @see #updateExtrema()
//     */
//    private void nullifyExtrema()
//    {
//        this.extrema = null;
//    }
//
//    /**
//     * Updates this polygon's extrema during construction or after <code>setEuclidean()</code> or
//     * <code>setVertices()</code> is called.
//     * 
//     * @see #nullifyExtrema()
//     */
//    private void updateExtrema()
//    {
//        // the extrema must first be nullified
//        // they are only nullified by setVertices
//        if (this.extrema != null) return;
//
//        if (this.vertices == null)
//        {
//            this.extrema = null;
//        }
//        else
//        {
//            this.extrema = new Extrema();
//            // Moved initialization code from constructor to avoid chicken-and-egg problem with references to parent polygon and back to Extrema
//            if (this.euclidean)
//            {
//                this.extrema.updateEuclidean();
//            }
//            else
//            {
//            	this.extrema.updateSpherical();
//            }
//        }
//    }
//    
//    public List<GCEdge> getEdges() {
//        List<GCEdge> edges = new ArrayList<GCEdge>();
//        
//        for (int i = 1; i < getNumVertices(); i++) {
//            edges.add(new GCEdge(this.vertices[i-1], this.vertices[i]));
//        }
//        
//        return Collections.unmodifiableList(edges);
//    }    /**
//     * 
//     * @param e
//     * @return  true if any portion of the GCEdge is contained by the polygon; 
//     * false otherwise.
//     */
//    public boolean containsEdge(GCEdge e) {
//        boolean rtn = contains(e.first) || contains(e.second);
//        
//        rtn |= intersections(e).size() > 1;
//        
//        return rtn;
//    }
//    
//    public List<GCPoint> intersections(GCEdge edge) {
//        List<GCPoint> intersections = new ArrayList<GCPoint>();		
//        GCPoint intersection = null;
//
//        for (GCEdge thisEdge : getEdges())
//        {
//            try
//            {
//                intersection = thisEdge.intersection(edge, SphericalUtilities.IntersectionType.NONSTRICT);
//            }
//            catch (InvalidInputException e)
//            {
//                logger.fatal("Polygon.intersections(GCEdge): Exception caught. Skipping this intersection. Chances are that either:\n" +
//                                "1) edge.first() ("+edge.first+") equals edge.second() ("+edge.second+")... or\n" +
//                                "2) thisEdge.first() ("+thisEdge.first+") equals thisEdge.second() ("+thisEdge.second+").");
//                logger.trace(e.getStackTrace());
//            }
//
//            if (intersection != null)
//            {
//                intersections.add(intersection);
//            }
//        }
//        return intersections;
//    }
//}