/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.faa.ang.swac.common.geometry;

import gov.faa.ang.swac.datalayer.storage.fileio.TextSerializable;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 *
 * @author ssmitz
 */
public class IndexedSimplePolygon extends SimplePolygon implements TextSerializable {

    private static final int NUM_BINS = 360;
    private static final double DEGREES_PER_BIN = 360 / ((double) NUM_BINS);
    private static Logger logger = LogManager.getLogger(IndexedSimplePolygon.class);
    /**
     * Edges are indexed into bins of equal longitude. This reduces the number
     * of edges that need to be checked for crossing during each containment
     * lookup.
     */
    private List<List<Integer>> index;

    public IndexedSimplePolygon() {
        super();
        this.index = new ArrayList<List<Integer>>(NUM_BINS);
    }

    public IndexedSimplePolygon(GCPoint[] vertices) {
        super(vertices);
        this.index = new ArrayList<List<Integer>>(NUM_BINS);
        buildIndex();
    }

    public IndexedSimplePolygon(double[] latlons) {
        super(latlons);
        this.index = new ArrayList<List<Integer>>(NUM_BINS);
        buildIndex();
    }
    
    public IndexedSimplePolygon(SimplePolygon org) {
        super(org);
        this.index = new ArrayList<List<Integer>>(NUM_BINS);
        buildIndex();
    }

    /**
     * Assigns each edge to one or more longitudinal bins, based on whether any
     * part of the edge is contained in the bin. Logically, this is done by
     * looping through the set of bins between the ones containing each
     * endpoint, inclusive. Antemeridian crossing requires special handling
     */
    private void buildIndex() {
        // Initialize sub-lists
        for (int i = 0; i < NUM_BINS; i++) {
            this.index.add(new ArrayList<Integer>());
        }

        for (int i = 0; i < coords.length; i++) {
            SimpleEdge p = coords[i];

            int idx1 = getIdx(p.getLon1());
            int idx2 = getIdx(p.getLon2());

            // Easy case: edge is entirely contained within one bin
            if (idx1 == idx2) {
                this.index.get(idx1).add(i);
                continue;
            }

            // Add i to the span of bins between idx1 and idx2

            // First determine the min and max for well defined iteration
            int minIdx = Math.min(idx1, idx2);
            int maxIdx = Math.max(idx1, idx2);

            // If the antemeridian is crossed, the span goes from 0 to minIdx and maxIdx to the end of the index.
            // If not, the span goes from minIdx to maxIdx (inclusive
            if (p.isCross()) {
                for (int t = 0; t <= minIdx; t++) {
                    this.index.get(t).add(i);
                }
                for (int t = maxIdx; t < this.index.size(); t++) {
                    this.index.get(t).add(i);
                }
            } else {
                for (int t = minIdx; t <= maxIdx; t++) {
                    this.index.get(t).add(i);
                }
            }
        }
    }

    /**
     * Clamp each point to the lower bound of the longitude range.
     *
     * @param lon
     * @return
     */
    private int getIdx(double lon) {
        return (int) ((lon + 179.9999999) / DEGREES_PER_BIN);
    }

    /**
     * Basic even-odd algorithm for testing polygon containment. A ray is
     * dropped along the meridian from the test point to the south pole and the
     * number of edges crossed is counted: odd=inside and even=outside. This is
     * not well defined for polygons containing the south pole. Edges are
     * considered to be in the Euclidean plane (wrapping at the antemeridian)
     * for geometric purposes
     *
     * @param testPoint
     * @return
     */
    @Override
    public boolean contains(GCPoint testPoint) {
        final double testLat = testPoint.latitude.normalized().degrees();
        final double testLon = testPoint.longitude.normalized().degrees();

        boolean inside = false;

        // Loop through the set of vertices indexed to this bin and check for crossings
        for (int i : this.index.get(getIdx(testLon))) {
            SimpleEdge edge = coords[i];
            /* Ignore edges whose longitudes are both on the same side of the test point 
             * (reverse the test if the antemeridian is crossed by the edge)
             * Vertices are considered to be infinitesimally shifted to the east of their
             * longitudes, such that edges of constant longitude and vertex interactions can
             * be ignored.
             */
            if (((edge.getLon1() < testLon) == (edge.getLon2() < testLon)) == edge.isCross()) {
                // We are dropping a ray to the south pole, so if the edge latitude (interpolated to test longitude)
                // is lower than the test latitude, then the edge is crossed 
                if (edge.getLat1() + (SimpleEdge.azimuth(edge.getLon1(), testLon) / edge.getAzimuth()) * (edge.getElevation()) < testLat) {
                    inside = !inside;
                }
            }
        }

        return inside;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof SimplePolygon) {
            boolean rtn = true;
            SimplePolygon sp = (SimplePolygon) o;

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
    public String toString() {
        return "Indexed" + super.toString();
    }

    @Override
    public void readItem(BufferedReader reader) throws IOException {
        super.readItem(reader);
        buildIndex();
    }

    @Override
    public void writeItem(PrintWriter writer) throws IOException {
        writer.println(this.toString());
    }
}
