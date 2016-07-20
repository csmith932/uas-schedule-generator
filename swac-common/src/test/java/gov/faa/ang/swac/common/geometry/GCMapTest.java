/**
 * Copyright "TBD", Metron Aviation & CSSI.  All rights reserved.
 * 
 * This computer Software was developed with the sponsorship of the U.S. Government
 * under Contract No. DTFAWA-10-D-00033, which has a copyright license in accordance with AMS 3.5-13.(c)(1).
 */

package gov.faa.ang.swac.common.geometry;

import static org.junit.Assert.*;
import gov.faa.ang.swac.common.datatypes.Latitude;
import gov.faa.ang.swac.common.datatypes.Longitude;

import org.junit.Before;
import org.junit.Test;

public class GCMapTest
{
    GCMap<Integer> map;
    SimplePolygon poly1;
    SimplePolygon poly2;
    Integer i1;
    Integer i2;
    GCPoint p1;
    GCPoint p2;

    @Before
    public void setUp()
    {
        this.map = new GCMap<Integer>();
        this.i1 = Integer.valueOf(100);
        this.i2 = Integer.valueOf(200);
        this.poly1 = new IndexedSimplePolygon(new double[] {10, 10, 10, 20, 20, 20, 20, 10});
        this.poly2 = new IndexedSimplePolygon(new double[] {30, 30, 30, 40, 40, 40, 40, 30});
        this.p1 = new GCPoint(Latitude.valueOfDegrees(15), Longitude.valueOfDegrees(15));
        this.p2 = new GCPoint(Latitude.valueOfDegrees(35), Longitude.valueOfDegrees(35));
        this.map.put(this.poly1, this.i1);
        this.map.put(this.poly2, this.i2);
    }

    @Test
    public void classCastException()
    {
        boolean exception = false;
        try
        {
            this.map.get(new Object());
        }
        catch (Exception ex)
        {
            assertTrue(ex instanceof ClassCastException);
            exception = true;
        }
        assertTrue(exception);
    }

    @Test
    public void getByPolygon()
    {
        Integer i = this.map.get(this.poly1);
        assertNotNull(i);
        assertEquals(i, this.i1);
        Integer j = this.map.get(this.poly2);
        assertNotNull(j);
        assertEquals(j, this.i2);
    }

    @Test
    public void getByPoint()
    {
        Integer i = this.map.get(this.p1);
        assertNotNull(i);
        assertEquals(i, this.i1);
        Integer j = this.map.get(this.p2);
        assertNotNull(j);
        assertEquals(j, this.i2);
    }
}
