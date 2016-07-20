/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.faa.ang.swac.common.geometry;

import gov.faa.ang.swac.common.datatypes.Longitude;

/**
 *
 * @author ssmitz
 */
public class BoundedSimplePolygon extends SimplePolygon {

    private static final int NORTH_EXTREME_LAT_INDEX = 0;
    private static final int SOUTH_EXTREME_LAT_INDEX = 1;
    private static final int EAST_EXTREME_LON_INDEX = 2;
    private static final int WEST_EXTREME_LON_INDEX = 3;
    private int[] extremaIndices;

    public BoundedSimplePolygon() {
        super();
        this.extremaIndices = new int[]{-1, -1, -1, -1};
    }

    public BoundedSimplePolygon(GCPoint[] vertices) {
        super(vertices);
        initExtrema();
    }

    public BoundedSimplePolygon(double[] latlons) {
        super(latlons);
        initExtrema();
    }

    public BoundedSimplePolygon(SimplePolygon org) {
        super(org);
        initExtrema();
    }

    private void initExtrema() {
        double[] denormal = getDenormalCoords();

        if (denormal == null) {
            return;
        }

        this.extremaIndices = new int[]{0, 0, 0, 0};

        // Find the extreme denormalized coordinate values
        double minLat = denormal[0];
        double maxLat = denormal[0];
        double minLon = denormal[1];
        double maxLon = denormal[1];

        // Loop over remaining vertices
        // We can skip the last one, since the polygon should be closed.
        for (int i = 1; i < this.coords.length; ++i) {
            final double lat = denormal[2 * i];

            if (lat < minLat) {
                minLat = lat;
                this.extremaIndices[SOUTH_EXTREME_LAT_INDEX] = i;
            } else if (maxLat < lat) {
                maxLat = lat;
                this.extremaIndices[NORTH_EXTREME_LAT_INDEX] = i;
            }

            final double lon = denormal[2 * i + 1];
            if (lon < minLon) {
                minLon = lon;
                this.extremaIndices[WEST_EXTREME_LON_INDEX] = i;
            } else if (maxLon < lon) {
                maxLon = lon;
                this.extremaIndices[EAST_EXTREME_LON_INDEX] = i;
            }
        }
    }

    private double[] getDenormalCoords() {

        // Return coordinate values are in degrees.

        if (this.coords == null || this.coords.length < 1) {
            return null;
        }

        final double[] result = new double[(this.coords.length + 1) * 2];

        // The first point is NOT denormalized
        result[0] = this.coords[0].getLat1();
        result[1] = this.coords[0].getLon1();

        // Keep track of the current longitude offset:
        // the numerical # of full circles to add to the
        // the normal value of longitude.
        // It starts out as 0, and should end up as 0.
        int nSeemCrossings = 0;

        // Loop over the remaining vertices
        int size = this.coords.length; // Algorithm is dependent on coords containing first point in last position also.
        for (int i = 1; i < size + 1; ++i) {
            int index = i % size;
            int prevIndex = index == 0 ? size - 1 : index - 1;

            // Get the normal values in degrees
            result[(2 * i)] = this.coords[index].getLat1();

            final int iLonCurr = (2 * i + 1);
            final int iLonPrev = (iLonCurr - 2);

            result[iLonCurr] = this.coords[index].getLon1() + nSeemCrossings * 360.0;

            // Make ordering consistent with the concept of "west" in isWestOf()
            if (Longitude.valueOfDegrees(this.coords[prevIndex].getLon1()).isWestOf(Longitude.valueOfDegrees(this.coords[index].getLon1()))) {
                if (result[iLonCurr] < result[iLonPrev]) {
                    // We must have crossed a seem going from west to east
                    // So, we add 1 full circle of longitude
                    result[iLonCurr] += 360;
                    ++nSeemCrossings;
                }
            } else if (Longitude.valueOfDegrees(this.coords[index].getLon1()).isWestOf(Longitude.valueOfDegrees(this.coords[prevIndex].getLon1()))) {
                if (result[iLonPrev] < result[iLonCurr]) {
                    // We must have crossed a seem going from east to west
                    // So, we subtract 1 full circle of longitude
                    result[iLonCurr] -= 360;
                    --nSeemCrossings;
                }
            }
        }

        if (nSeemCrossings != 0) {
            throw new RuntimeException("Polygon contains a pole");
        }

        return result;
    }

    /**
     * @return {@link Double.NaN} if polygon is uninitialized. Otherwise the
     * normalized degrees of longitude of this
     * <code>BoundedIndexedSimplePolygon</code>'s easternmost vertex.
     */
    public double getEastExtremeDegrees() {
        return this.extremaIndices[EAST_EXTREME_LON_INDEX] < 0 ? Double.NaN : this.coords[this.extremaIndices[EAST_EXTREME_LON_INDEX]].getLon1();
    }

    /**
     * @return {@link Double.NaN} if polygon is uninitialized. Otherwise the
     * normalized degrees of longitude of this
     * <code>BoundedIndexedSimplePolygon</code>'s westernmost vertex.
     */
    public double getWestExtremeDegrees() {
        return this.extremaIndices[WEST_EXTREME_LON_INDEX] < 0 ? Double.NaN : this.coords[this.extremaIndices[WEST_EXTREME_LON_INDEX]].getLon1();
    }

    /**
     * @return {@link Double.NaN} if polygon is uninitialized. Otherwise the
     * normalized degrees of latitude of this
     * <code>BoundedIndexedSimplePolygon</code>'s northernmost vertex.
     */
    public double getNorthExtremeDegrees() {
        return this.extremaIndices[NORTH_EXTREME_LAT_INDEX] < 0 ? Double.NaN : this.coords[this.extremaIndices[NORTH_EXTREME_LAT_INDEX]].getLat1();
    }

    /**
     * @return {@link Double.NaN} if polygon is uninitialized. Otherwise the
     * normalized degrees of latitude of this
     * <code>BoundedIndexedSimplePolygon</code>'s southernmost vertex.
     */
    public double getSouthExtremeDegrees() {
        return this.extremaIndices[SOUTH_EXTREME_LAT_INDEX] < 0 ? Double.NaN : this.coords[this.extremaIndices[SOUTH_EXTREME_LAT_INDEX]].getLat1();
    }
}
