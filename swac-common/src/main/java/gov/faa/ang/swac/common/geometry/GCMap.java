/**
 * Copyright "TBD", Metron Aviation & CSSI. All rights reserved.
 *
 * This computer Software was developed with the sponsorship of the U.S.
 * Government under Contract No. DTFAWA-10-D-00033, which has a copyright
 * license in accordance with AMS 3.5-13.(c)(1).
 */
package gov.faa.ang.swac.common.geometry;

import java.util.HashMap;

/**
 * A generalization of the Map data structure that allows geographical lookup by
 * containment of a GCPoint parameter in a SimplePolygon key of the Map. Overlapping
 * SimplePolygons create the possibility for violation of the uniqueness assumption
 * for key-value mappings. This is disambiguated by returning the first match in
 * a default iteration over keys. Retrieval by GCPoint runs O(n), so this
 * structure is not well suited for large mappings. An indexing scheme (e.g.
 * grid-based partitioning of candidate keys) is recommended for large maps.
 *
 * @author csmith
 *
 * @param <T>
 */
public class GCMap<T> extends HashMap<SimplePolygon, T> {

    /**
     * Required for Serializable interface
     */
    private static final long serialVersionUID = 729588313234312302L;

    /**
     * Override for default map accessor allows GCPoint to be used as a key
     * rather than just SimplePolygon. When a GCPoint is the parameter, the first
     * SimplePolygon found in an iteration over keys that contains the point is used
     * as the key for normal retrieval.
     *
     * @param key A SimplePolygon or GCPoint
     * @return T The strongly typed value mapped to key
     */
    @Override
    public T get(Object key) {
        if (key instanceof SimplePolygon) {
            return super.get(key);
        } else if (key instanceof GCPoint) //TODO: STS This should be separated out into a different function (public SimplePolygon getContainingSimplePolygon(GCPoint p)).
        {
            return super.get(this.getKey((GCPoint) key));
        } else {
            throw new ClassCastException();
        }
    }

    /**
     * Key lookup by GCPoint containment.
     *
     * @param key A GCPoint
     * @return The first SimplePolygon in the keySet collection that contains key
     */
    private SimplePolygon getKey(GCPoint key) {
        for (SimplePolygon p : this.keySet()) {
            if (p.contains(key)) {
                return p;
            }
        }
        return null;
    }
}
