/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.faa.ang.swac.common.geometry;

import gov.faa.ang.swac.common.datatypes.Latitude;
import gov.faa.ang.swac.common.datatypes.Longitude;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;

/**
 *
 * @author ssmitz
 */
public class SimplePolygonTest {
    // PolygonFixtures

    double[] oddVerticesLatLons, tooFewVerticesLatLons;

    public SimplePolygonTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        // array with odd numbered double vertices array
        int randomOddInt = (int) Math.round(Math.random() * 10.0 + 1) * 2 + 1;
        this.oddVerticesLatLons = new double[randomOddInt];
        for (int i = 0; i < randomOddInt - 1; i += 2) {
            this.oddVerticesLatLons[i] = Math.random() * 178 - 89;
            this.oddVerticesLatLons[i + 1] = Math.random() * 360 - 180;
        }
        this.oddVerticesLatLons[randomOddInt - 1] = Math.random() * 178 - 89;

        // array with only 2 points
        this.tooFewVerticesLatLons = new double[4];
        this.tooFewVerticesLatLons[0] = Math.random() * 178 - 89;
        this.tooFewVerticesLatLons[1] = Math.random() * 360 - 180;
        this.tooFewVerticesLatLons[2] = Math.random() * 178 - 89;
        this.tooFewVerticesLatLons[3] = Math.random() * 360 - 180;
    }

    @After
    public void tearDown() {
    }

    @Test
    public void constructors() {
        try {
            assertNull("Polygon object should not have been instantiated. Its double vertices array has odd length.", new SimplePolygon(this.oddVerticesLatLons));
        } catch (IllegalArgumentException e) {
            // The exception is supposed to be thrown since the Polygon
            // has an odd number of elements in vertices double array.
        }

        try {
            assertNull("Polygon object should not have been instantiated. It only has 2 vertices.", new SimplePolygon(this.tooFewVerticesLatLons));
        } catch (IllegalArgumentException e) {
            // The exception is supposed to be thrown since the Polygon
            // has an odd number of elements in vertices double array.
        }
    }

    /**
     * Test of points method, of class SimplePolygon.
     */
    @Test
    @Ignore
    public void testPoints() {
        System.out.println("points");
        SimplePolygon instance = new SimplePolygon();
        List expResult = null;
        List result = instance.points();
        assertEquals(expResult, result);
        fail("The test case is a prototype.");
    }

    @Test
    public void testContains() {
        for (int i = 0; i < 4; ++i) {
            final SimplePolygon unitSqr = new SimplePolygon(PolygonFixture.unitSquare.getLatLons(i));
            GCPoint p = new GCPoint(Latitude.valueOfDegrees(0.5), Longitude.valueOfDegrees(0.5));
            assertTrue(unitSqr.contains(p));
            p = new GCPoint(Latitude.valueOfDegrees(0.5), Longitude.valueOfDegrees(1));
            assertTrue(unitSqr.contains(p));
//            p = new GCPoint(Latitude.valueOfDegrees(0), Longitude.valueOfDegrees(0));
//            assertTrue(unitSqr.contains(p));
//            p = new GCPoint(Latitude.valueOfDegrees(1), Longitude.valueOfDegrees(0));
//            assertTrue(unitSqr.contains(p));
            p = new GCPoint(Latitude.valueOfDegrees(1), Longitude.valueOfDegrees(1));
            assertTrue(unitSqr.contains(p));
//            p = new GCPoint(Latitude.valueOfDegrees(0), Longitude.valueOfDegrees(1));
//            assertTrue(unitSqr.contains(p));
            p = new GCPoint(Latitude.valueOfDegrees(2), Longitude.valueOfDegrees(0));
            assertFalse(unitSqr.contains(p));
            p = new GCPoint(Latitude.valueOfDegrees(-1), Longitude.valueOfDegrees(0));
            assertFalse(unitSqr.contains(p));
            p = new GCPoint(Latitude.valueOfDegrees(2), Longitude.valueOfDegrees(1));
            assertFalse(unitSqr.contains(p));
            p = new GCPoint(Latitude.valueOfDegrees(-1), Longitude.valueOfDegrees(1));
            assertFalse(unitSqr.contains(p));

            final SimplePolygon bigDiamond = new SimplePolygon(PolygonFixture.bigDiamond.getLatLons(i));
            p = new GCPoint(Latitude.valueOfDegrees(0), Longitude.valueOfDegrees(0));
            assertTrue(bigDiamond.contains(p));
//            p = new GCPoint(Latitude.valueOfDegrees(0), Longitude.valueOfDegrees(-179));
//            assertTrue(bigDiamond.contains(p));
            p = new GCPoint(Latitude.valueOfDegrees(45), Longitude.valueOfDegrees(0));
            assertTrue(bigDiamond.contains(p));
//            p = new GCPoint(Latitude.valueOfDegrees(0), Longitude.valueOfDegrees(179));
//            assertTrue(bigDiamond.contains(p));
//            p = new GCPoint(Latitude.valueOfDegrees(-45), Longitude.valueOfDegrees(0));
//            assertTrue(bigDiamond.contains(p));

            final SimplePolygon triangle = new SimplePolygon(PolygonFixture.triangle.getLatLons(i));
            p = new GCPoint(Latitude.valueOfDegrees(0), Longitude.valueOfDegrees(0));
            assertTrue(triangle.contains(p));
//            p = new GCPoint(Latitude.valueOfDegrees(-10), Longitude.valueOfDegrees(5));
//            assertTrue(triangle.contains(p));
//            p = new GCPoint(Latitude.valueOfDegrees(-10), Longitude.valueOfDegrees(-80));
//            assertTrue(triangle.contains(p));
            p = new GCPoint(Latitude.valueOfDegrees(70), Longitude.valueOfDegrees(0));
            assertTrue(triangle.contains(p));
//            p = new GCPoint(Latitude.valueOfDegrees(-10), Longitude.valueOfDegrees(80));
//            assertTrue(triangle.contains(p));
            p = new GCPoint(Latitude.valueOfDegrees(0), Longitude.valueOfDegrees(-80));
            assertFalse(triangle.contains(p));
            p = new GCPoint(Latitude.valueOfDegrees(-20), Longitude.valueOfDegrees(-80));
            assertFalse(triangle.contains(p));
            p = new GCPoint(Latitude.valueOfDegrees(0), Longitude.valueOfDegrees(80));
            assertFalse(triangle.contains(p));
            p = new GCPoint(Latitude.valueOfDegrees(-20), Longitude.valueOfDegrees(80));
            assertFalse(triangle.contains(p));

            final SimplePolygon star = new SimplePolygon(PolygonFixture.star.getLatLons(i));
//            p = new GCPoint(Latitude.valueOfDegrees(0), Longitude.valueOfDegrees(0));
//            assertTrue(star.contains(p));
            p = new GCPoint(Latitude.valueOfDegrees(-10), Longitude.valueOfDegrees(-100));
            assertFalse(star.contains(p));
//            p = new GCPoint(Latitude.valueOfDegrees(-50), Longitude.valueOfDegrees(-120));
//            assertTrue(star.contains(p));
            p = new GCPoint(Latitude.valueOfDegrees(25), Longitude.valueOfDegrees(-45));
            assertTrue(star.contains(p));
//            p = new GCPoint(Latitude.valueOfDegrees(40), Longitude.valueOfDegrees(-120));
//            assertTrue(star.contains(p));
            p = new GCPoint(Latitude.valueOfDegrees(40), Longitude.valueOfDegrees(-10));
            assertTrue(star.contains(p));
            p = new GCPoint(Latitude.valueOfDegrees(80), Longitude.valueOfDegrees(0));
            assertTrue(star.contains(p));
            p = new GCPoint(Latitude.valueOfDegrees(40), Longitude.valueOfDegrees(10));
            assertTrue(star.contains(p));
            p = new GCPoint(Latitude.valueOfDegrees(40), Longitude.valueOfDegrees(20));
            assertTrue(star.contains(p));
            p = new GCPoint(Latitude.valueOfDegrees(25), Longitude.valueOfDegrees(45));
            assertTrue(star.contains(p));
            p = new GCPoint(Latitude.valueOfDegrees(-40), Longitude.valueOfDegrees(-100));
            assertTrue(star.contains(p));

            final SimplePolygon snake = new SimplePolygon(PolygonFixture.snake.getLatLons(i));
//            p = new GCPoint(Latitude.valueOfDegrees(45), Longitude.valueOfDegrees(-90));
//            assertTrue(snake.contains(p));
            p = new GCPoint(Latitude.valueOfDegrees(35), Longitude.valueOfDegrees(0));
            assertTrue(snake.contains(p));
            p = new GCPoint(Latitude.valueOfDegrees(25), Longitude.valueOfDegrees(90));
            assertTrue(snake.contains(p));
            p = new GCPoint(Latitude.valueOfDegrees(15), Longitude.valueOfDegrees(180));
            assertTrue(snake.contains(p));
            p = new GCPoint(Latitude.valueOfDegrees(5), Longitude.valueOfDegrees(-90));
            assertTrue(snake.contains(p));
            p = new GCPoint(Latitude.valueOfDegrees(-5), Longitude.valueOfDegrees(0));
            assertTrue(snake.contains(p));

            // Casey's Practical Examples ---------------------------------------------------------
            final SimplePolygon ZLA083m = new SimplePolygon(PolygonFixture.ZLA083m.getLatLons(i));
            p = new GCPoint(Latitude.valueOfDegrees(34.03333333333333), Longitude.valueOfDegrees(-118.76666666666667));
            assertTrue(ZLA083m.contains(p));

            final SimplePolygon ZLA084B = new SimplePolygon(PolygonFixture.ZLA084B.getLatLons(i));
            p = new GCPoint(Latitude.valueOfDegrees(34.03333333333333), Longitude.valueOfDegrees(-118.76666666666667));
            assertFalse(ZLA084B.contains(p));

            final SimplePolygon ZBW016F = new SimplePolygon(PolygonFixture.ZBW016F.getLatLons(i));
            p = new GCPoint(Latitude.valueOfDegrees(43.08333333333333), Longitude.valueOfDegrees(-70.83333333333333));
            assertTrue(ZBW016F.contains(p));
        }
    }

    /**
     * Test of equals method, of class SimplePolygon.
     */
    @Test
    @Ignore
    public void testEquals() {
        System.out.println("equals");
        Object o = null;
        SimplePolygon instance = new SimplePolygon();
        boolean expResult = false;
        boolean result = instance.equals(o);
        assertEquals(expResult, result);
        fail("The test case is a prototype.");
    }

    /**
     * Test of toString method, of class SimplePolygon.
     */
    @Test
    @Ignore
    public void testToString() {
        System.out.println("toString");
        SimplePolygon instance = new SimplePolygon();
        String expResult = "";
        String result = instance.toString();
        assertEquals(expResult, result);
        fail("The test case is a prototype.");
    }

    /**
     * Test of readItem method, of class SimplePolygon.
     */
    @Test
    @Ignore
    public void testReadItem() throws Exception {
        BufferedReader reader = null;
        SimplePolygon instance = new SimplePolygon();
        instance.readItem(reader);
        fail("The test case is a prototype.");
    }

    /**
     * Test of writeItem method, of class SimplePolygon.
     */
    @Test
    @Ignore
    public void testWriteItem() throws Exception {
        PrintWriter writer = null;
        SimplePolygon instance = new SimplePolygon();
        instance.writeItem(writer);
        fail("The test case is a prototype.");
    }
}