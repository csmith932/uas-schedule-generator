///**
// * Copyright "TBD", Metron Aviation & CSSI.  All rights reserved.
// * 
// * This computer Software was developed with the sponsorship of the U.S. Government
// * under Contract No. DTFAWA-10-D-00033, which has a copyright license in accordance with AMS 3.5-13.(c)(1).
// */
//
//package gov.faa.ang.swac.common.geometry;
//
//import static org.junit.Assert.*;
//
//
//import gov.faa.ang.swac.common.geometry.Polygon;
//import gov.faa.ang.swac.common.utilities.Mathematics;
//
//import org.junit.Before;
//import org.junit.Test;
//
///**
// * Tests for Polygon class using JUnit 4 Framework.
// * 
// * @author jagboola
// * @author chall
// */
//public class PolygonTest
//{
//    // PolygonFixtures
//    double[] oddVerticesLatLons, tooFewVerticesLatLons, selfcrosser;
//
//    /**
//     * Initializes the invalid fixtures prior to each test. Valid fixtures are in the
//     * <code>PolygonFixture</code> class.
//     * 
//     * @see PolygonFixture
//     */
//    @Before
//    public void setUp()
//    {
//        // array with odd numbered double vertices array
//        int randomOddInt = (int)Math.round(Math.random() * 10.0 + 1) * 2 + 1;
//        this.oddVerticesLatLons = new double[randomOddInt];
//        for (int i = 0; i < randomOddInt - 1; i += 2)
//        {
//            this.oddVerticesLatLons[i] = Math.random() * 178 - 89;
//            this.oddVerticesLatLons[i + 1] = Math.random() * 360 - 180;
//        }
//        this.oddVerticesLatLons[randomOddInt - 1] = Math.random() * 178 - 89;
//
//        // array with only 2 points
//        this.tooFewVerticesLatLons = new double[4];
//        this.tooFewVerticesLatLons[0] = Math.random() * 178 - 89;
//        this.tooFewVerticesLatLons[1] = Math.random() * 360 - 180;
//        this.tooFewVerticesLatLons[2] = Math.random() * 178 - 89;
//        this.tooFewVerticesLatLons[3] = Math.random() * 360 - 180;
//
//        // array that crosses itself
//        this.selfcrosser = new double[12];
//        this.selfcrosser[0] = 45;
//        this.selfcrosser[1] = -45;
//        this.selfcrosser[2] = 45;
//        this.selfcrosser[3] = 45;
//        this.selfcrosser[4] = 0;
//        this.selfcrosser[5] = 220;
//        this.selfcrosser[6] = -45;
//        this.selfcrosser[7] = 45;
//        this.selfcrosser[8] = -45;
//        this.selfcrosser[9] = -45;
//        this.selfcrosser[10] = 0;
//        this.selfcrosser[11] = -220;
//    }
//
//    @Test
//    public void constructors()
//    {
//        try
//        {
//            assertNull("Polygon object should not have been instantiated. Its double vertices array has odd length.", new Polygon(true, this.oddVerticesLatLons));
//        }
//        catch (RuntimeException e)
//        {
//            // The exception is supposed to be thrown since the Polygon
//            // has an odd number of elements in vertices double array.
//        }
//
//        try
//        {
//            assertNull("Polygon object should not have been instantiated. It only has 2 vertices.", new Polygon(true, this.tooFewVerticesLatLons));
//        }
//        catch (RuntimeException e)
//        {
//            // The exception is supposed to be thrown since the Polygon
//            // has an odd number of elements in vertices double array.
//        }
//
//        try
//        {
//            assertNull("Polygon object should not have been instantiated. It crosses itself.", new Polygon(true, this.selfcrosser));
//        }
//        catch (RuntimeException e)
//        {
//            // The exception is supposed to be thrown since the Polygon
//            // has an odd number of elements in vertices double array.
//        }
//
//        String polygonFixtureName = null;
//        int iInternalPolygonFixtureIndex = -1;
//        try
//        {
//            for (PolygonFixture fixture : PolygonFixture.values())
//            {
//                polygonFixtureName = fixture.toString();
//
//                for (int i = 0; i < 4; ++i)
//                {
//                    iInternalPolygonFixtureIndex = i;
//
//                    new Polygon(true, fixture.getLatLons(i));
//                }
//            }
//        }
//        catch (RuntimeException e)
//        {
//            // This is here so that the error message is displayed
//            // when a runtime exception is thrown from the Polygon constructor
//            // Convert exception into a failed assertion w/ message
//            fail(polygonFixtureName + " " + iInternalPolygonFixtureIndex + ":" + e.getMessage());
//        }
//    }
//
//    @Test
//    public void isValid()
//    {
//        for (PolygonFixture fixture : PolygonFixture.values())
//        {
//            for (int i = 0; i < 4; ++i)
//            {
//                Polygon polygon = new Polygon(fixture.getLatLons(i));
//                assertTrue(fixture.toString() + i, polygon.isValid());
//            }
//        }
//    }
//
//    @Test
//    public void getEuclidean()
//    {
//        for (PolygonFixture fixture : PolygonFixture.values())
//        {
//            for (int i = 0; i < 4; ++i)
//            {
//                Polygon polygon = new Polygon(fixture.getLatLons(i));
//                assertTrue(polygon.getEuclidean());
//            }
//        }
//    }
//
//    @Test
//    public void getExtremes()
//    {
//        String name;
//        for (int i = 0; i < 4; ++i)
//        {
//            final Polygon unitSquare = new Polygon(PolygonFixture.unitSquare.getLatLons(i));
//            name = PolygonFixture.unitSquare.toString() + " " + i;
//            assertTrue(name, unitSquare.getNorthExtreme().degrees() == 1);
//            assertTrue(name, unitSquare.getSouthExtreme().degrees() == 0);
//            assertTrue(name, unitSquare.getEastExtreme().degrees() == 1);
//            assertTrue(name, unitSquare.getWestExtreme().degrees() == 0);
//
//            final Polygon crossedSeamUnitSqr = new Polygon(PolygonFixture.crossedSeamUnitSqr.getLatLons(i));
//            name = PolygonFixture.crossedSeamUnitSqr.toString() + " " + i;
//            assertTrue(name, crossedSeamUnitSqr.getNorthExtreme().degrees() == .5);
//            assertTrue(name, crossedSeamUnitSqr.getSouthExtreme().degrees() == -.5);
//            assertTrue(name, crossedSeamUnitSqr.getEastExtreme().degrees() == -179.5);
//            assertTrue(name, crossedSeamUnitSqr.getWestExtreme().degrees() == 179.5);
//
//            final Polygon nonNormalSqr = new Polygon(PolygonFixture.nonNormalSqr.getLatLons(i));
//            name = PolygonFixture.nonNormalSqr.toString() + " " + i;
//            assertTrue(name, nonNormalSqr.getNorthExtreme().degrees() == 10);
//            assertTrue(name, nonNormalSqr.getSouthExtreme().degrees() == -10);
//            assertTrue(name, nonNormalSqr.getEastExtreme().degrees() == -170);
//            assertTrue(name, Mathematics.equals(nonNormalSqr.getWestExtreme().degrees(), 170, 1e-6));
//
//            final Polygon crosses90seam = new Polygon(PolygonFixture.crosses90seam.getLatLons(i));
//            name = PolygonFixture.crosses90seam.toString() + " " + i;
//            assertTrue(name, crosses90seam.getNorthExtreme().degrees() == 10);
//            assertTrue(name, crosses90seam.getSouthExtreme().degrees() == -10);
//            assertTrue(name, crosses90seam.getEastExtreme().degrees() == 100);
//            assertTrue(name, crosses90seam.getWestExtreme().degrees() == 80);
//
//            final Polygon bigDiamond = new Polygon(PolygonFixture.bigDiamond.getLatLons(i));
//            name = PolygonFixture.bigDiamond.toString() + " " + i;
//            assertTrue(name, bigDiamond.getNorthExtreme().degrees() == 45);
//            assertTrue(name, bigDiamond.getSouthExtreme().degrees() == -45);
//            assertTrue(name, bigDiamond.getEastExtreme().degrees() == 179);
//            assertTrue(name, bigDiamond.getWestExtreme().degrees() == -179);
//
//            final Polygon triangle = new Polygon(PolygonFixture.triangle.getLatLons(i));
//            name = PolygonFixture.triangle.toString() + " " + i;
//            assertTrue(name, triangle.getNorthExtreme().degrees() == 70);
//            assertTrue(name, triangle.getSouthExtreme().degrees() == -10);
//            assertTrue(name, triangle.getEastExtreme().degrees() == 80);
//            assertTrue(name, triangle.getWestExtreme().degrees() == -80);
//
//            final Polygon star = new Polygon(PolygonFixture.star.getLatLons(i));
//            name = PolygonFixture.star.toString() + " " + i;
//            assertTrue(name, star.getNorthExtreme().degrees() == 80);
//            assertTrue(name, star.getSouthExtreme().degrees() == -50);
//            assertTrue(name, Mathematics.equals(star.getEastExtreme().degrees(), 120, 1e-6));
//            assertTrue(name, Mathematics.equals(star.getWestExtreme().degrees(), -120, 1e-6));
//
//            final Polygon snake = new Polygon(PolygonFixture.snake.getLatLons(i));
//            name = PolygonFixture.snake.toString() + " " + i;
//            assertTrue(name, snake.getNorthExtreme().degrees() == 50);
//            assertTrue(name, snake.getSouthExtreme().degrees() == -10);
//            assertTrue(name, snake.getEastExtreme().degrees() == 0);
//            assertTrue(name, snake.getWestExtreme().degrees() == -90);
//        }
//    }
//
//    @Test
//    public void getNumVertices()
//    {
//        for (int i = 0; i < 4; ++i)
//        {
//            final Polygon unitSquare = new Polygon(PolygonFixture.unitSquare.getLatLons(i));
//            assertTrue(unitSquare.getNumVertices() == 4);
//
//            final Polygon crossedSeamUnitSqr = new Polygon(PolygonFixture.crossedSeamUnitSqr.getLatLons(i));
//            assertTrue(crossedSeamUnitSqr.getNumVertices() == 4);
//
//            final Polygon nonNormalSqr = new Polygon(PolygonFixture.nonNormalSqr.getLatLons(i));
//            assertTrue(nonNormalSqr.getNumVertices() == 4);
//
//            final Polygon crosses90seam = new Polygon(PolygonFixture.crosses90seam.getLatLons(i));
//            assertTrue(crosses90seam.getNumVertices() == 4);
//
//            final Polygon bigDiamond = new Polygon(PolygonFixture.bigDiamond.getLatLons(i));
//            assertTrue(bigDiamond.getNumVertices() == 4);
//
//            final Polygon triangle = new Polygon(PolygonFixture.triangle.getLatLons(i));
//            assertTrue(triangle.getNumVertices() == 3);
//
//            final Polygon star = new Polygon(PolygonFixture.star.getLatLons(i));
//            assertTrue(star.getNumVertices() == 10);
//
//            final Polygon snake = new Polygon(PolygonFixture.snake.getLatLons(i));
//            assertTrue(snake.getNumVertices() == 12);
//        }
//    }
//
//    @Test
//    public void contains()
//    {
//        for (int i = 0; i < 4; ++i)
//        {
//            final Polygon unitSqr = new Polygon(PolygonFixture.unitSquare.getLatLons(i));
//            assertTrue(unitSqr.contains(0.5, 0.5));
//            assertTrue(unitSqr.contains(0.5, 1));
//            assertTrue(unitSqr.contains(0, 0));
//            assertTrue(unitSqr.contains(1, 0));
//            assertTrue(unitSqr.contains(1, 1));
//            assertTrue(unitSqr.contains(0, 1));
//            assertFalse(unitSqr.contains(2, 0));
//            assertFalse(unitSqr.contains(-1, 0));
//            assertFalse(unitSqr.contains(2, 1));
//            assertFalse(unitSqr.contains(-1, 1));
//
//            final Polygon crossedSeamUnitSqr = new Polygon(PolygonFixture.crossedSeamUnitSqr.getLatLons(i));
//            assertTrue(crossedSeamUnitSqr.contains(0, 180));
//            assertTrue(crossedSeamUnitSqr.contains(0.4, -179.5));
//
//            final Polygon nonNormalSqr = new Polygon(PolygonFixture.nonNormalSqr.getLatLons(i));
//            assertTrue(nonNormalSqr.contains(0, 180));
//
//            final Polygon crosses90seam = new Polygon(PolygonFixture.crosses90seam.getLatLons(i));
//            assertTrue(crosses90seam.getNumVertices() == 4);
//
//            final Polygon bigDiamond = new Polygon(PolygonFixture.bigDiamond.getLatLons(i));
//            assertTrue(bigDiamond.contains(0, 0));
//            assertTrue(bigDiamond.contains(0, -179));
//            assertTrue(bigDiamond.contains(45, 0));
//            assertTrue(bigDiamond.contains(0, 179));
//            assertTrue(bigDiamond.contains(-45, 0));
//
//            final Polygon triangle = new Polygon(PolygonFixture.triangle.getLatLons(i));
//            assertTrue(triangle.contains(0, 0));
//            assertTrue(triangle.contains(-10, 5));
//            assertTrue(triangle.contains(-10, -80));
//            assertTrue(triangle.contains(70, 0));
//            assertTrue(triangle.contains(-10, 80));
//            assertFalse(triangle.contains(0, -80));
//            assertFalse(triangle.contains(-20, -80));
//            assertFalse(triangle.contains(0, 80));
//            assertFalse(triangle.contains(-20, 80));
//
//            final Polygon star = new Polygon(PolygonFixture.star.getLatLons(i));
//            assertTrue(star.contains(0, 0));
//            assertFalse(star.contains(-10, -100));
//            assertTrue(star.contains(-50, -120));
//            assertTrue(star.contains(25, -45));
//            assertTrue(star.contains(40, -120));
//            assertTrue(star.contains(40, -10));
//            assertTrue(star.contains(80, 0));
//            assertTrue(star.contains(40, 10));
//            assertTrue(star.contains(40, 20));
//            assertTrue(star.contains(25, 45));
//            assertTrue(star.contains(-40, -100));
//            assertTrue(star.contains(0, 0));
//
//            final Polygon snake = new Polygon(PolygonFixture.snake.getLatLons(i));
//            assertTrue(snake.contains(45, -90));
//            assertTrue(snake.contains(35, 0));
//            assertTrue(snake.contains(25, 90));
//            assertTrue(snake.contains(15, 180));
//            assertTrue(snake.contains(5, -90));
//            assertTrue(snake.contains(-5, 0));
//
//            // Casey's Practical Examples ---------------------------------------------------------
//            final Polygon ZLA083m = new Polygon(PolygonFixture.ZLA083m.getLatLons(i));
//            assertTrue(ZLA083m.contains(34.03333333333333, -118.76666666666667));
//
//            final Polygon ZLA084B = new Polygon(PolygonFixture.ZLA084B.getLatLons(i));
//            assertFalse(ZLA084B.contains(34.03333333333333, -118.76666666666667));
//
//            final Polygon ZBW016F = new Polygon(PolygonFixture.ZBW016F.getLatLons(i));
//            assertTrue(ZBW016F.contains(43.08333333333333, -70.83333333333333));
//        }
//    }
//
//    public static void main(String[] args)
//    {
//        org.junit.runner.JUnitCore.runClasses(PolygonTest.class);
//    }
//}