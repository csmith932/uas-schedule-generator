/**
 The information in this material is proprietary to, and the property of,
 Sensis Corporation. It may not be duplicated, used, or disclosed in whole
 or in part for any purpose without express written consent.

 Copyright (c) 2009 Sensis Corporation; All Rights Reserved
 */
package gov.faa.ang.swac.common;

import net.jcip.annotations.Immutable;

/**
 * A convenience data structure for returning two values
 * from a method
 * <p>
 * The class returns an immutable object
 *
 * @author Suresh Sitharaman
 * @version 1.0
 */
@Immutable
public class Pair<F, S> {

    /**
     * First object
     */
    private final F first;

    /**
     * Second object
     */
    private final S second;

    /**
     * private constructor
     *
     * @param first
     *        First object
     * @param second
     *        Second object
     */
    private Pair(F first, S second) {

        this.first = first;
        this.second = second;
    }

    /**
     *
     * @param first
     *        First object
     * @param second
     *        Second object
     *
     * @return Pair
     */
    public static <F, S> Pair<F, S> create(F first, S second) {
        return new Pair<F, S>(first, second);
    }

    /**
     * Returns the first object
     *
     * @return First object
     */
    public F getFirst() {
        return this.first;
    }

    /**
     * Returns the second object
     *
     * @return Second object
     */
    public S getSecond() {
        return this.second;
    }

    @Override
    public final boolean equals(Object o) {

        if(o == null) return false;

        if (!(o instanceof Pair)) {
            return false;
        }

        final Pair<?, ?> other = (Pair<?, ?>) o;
        return equal(getFirst(), other.getFirst()) && equal(getSecond(), other.getSecond());
    }

    @Override
    public int hashCode() {

        int hFirst  = (getFirst() == null ? 0 : getFirst().hashCode());
        int hSecond = (getSecond() == null ? 0 : getSecond().hashCode());

        return hFirst + (37 * hSecond);
    }

    public static boolean equal(Object o1, Object o2) {

        if (o1 == null) {
            return o2 == null;
        }
        return o1.equals(o2);
    }
    
    @Override
    public String toString(){
    	
    	return "[" + this.first + "," + this.second + "]"; 
    }
}