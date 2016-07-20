/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.faa.ang.swac.common.geometry;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author ssmitz
 */
public class BoundedSimplePolygonTest {

    public BoundedSimplePolygonTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of get*ExtremeDegrees methods, of class SimplePolygon.
     */
    @Test
    public void testGetExtremes() {
        System.out.println("getNorthExtremeDegreesDegrees");
        String name;
        for (int i = 0; i < 4; ++i) {
            final BoundedSimplePolygon unitSquare = new BoundedSimplePolygon(PolygonFixture.unitSquare.getLatLons(i));
            name = PolygonFixture.unitSquare.toString() + " " + i;
            assertEquals(name, 1.0, unitSquare.getNorthExtremeDegrees(), 1e-6);
            assertEquals(name, 0.0, unitSquare.getSouthExtremeDegrees(), 1e-6);
            assertEquals(name, 1.0, unitSquare.getEastExtremeDegrees(), 1e-6);
            assertEquals(name, 0.0, unitSquare.getWestExtremeDegrees(), 1e-6);

            final BoundedSimplePolygon crossedSeamUnitSqr = new BoundedSimplePolygon(PolygonFixture.crossedSeamUnitSqr.getLatLons(i));
            name = PolygonFixture.crossedSeamUnitSqr.toString() + " " + i;
            assertEquals(name, 0.5, crossedSeamUnitSqr.getNorthExtremeDegrees(), 1e-6);
            assertEquals(name, -0.5, crossedSeamUnitSqr.getSouthExtremeDegrees(), 1e-6);
            assertEquals(name, -179.5, crossedSeamUnitSqr.getEastExtremeDegrees(), 1e-6);
            assertEquals(name, 179.5, crossedSeamUnitSqr.getWestExtremeDegrees(), 1e-6);

            final BoundedSimplePolygon nonNormalSqr = new BoundedSimplePolygon(PolygonFixture.nonNormalSqr.getLatLons(i));
            name = PolygonFixture.nonNormalSqr.toString() + " " + i;
            assertEquals(name, 10.0, nonNormalSqr.getNorthExtremeDegrees(), 1e-6);
            assertEquals(name, -10.0, nonNormalSqr.getSouthExtremeDegrees(), 1e-6);
            assertEquals(name, -170.0, nonNormalSqr.getEastExtremeDegrees(), 1e-6);
            assertEquals(name, 170.0, nonNormalSqr.getWestExtremeDegrees(), 1e-6);

            final BoundedSimplePolygon crosses90seam = new BoundedSimplePolygon(PolygonFixture.crosses90seam.getLatLons(i));
            name = PolygonFixture.crosses90seam.toString() + " " + i;
            assertEquals(name, 10.0, crosses90seam.getNorthExtremeDegrees(), 1e-6);
            assertEquals(name, -10.0, crosses90seam.getSouthExtremeDegrees(), 1e-6);
            assertEquals(name, 100.0, crosses90seam.getEastExtremeDegrees(), 1e-6);
            assertEquals(name, 80.0, crosses90seam.getWestExtremeDegrees(), 1e-6);

            final BoundedSimplePolygon bigDiamond = new BoundedSimplePolygon(PolygonFixture.bigDiamond.getLatLons(i));
            name = PolygonFixture.bigDiamond.toString() + " " + i;
            assertEquals(name, 45.0, bigDiamond.getNorthExtremeDegrees(), 1e-6);
            assertEquals(name, -45.0, bigDiamond.getSouthExtremeDegrees(), 1e-6);
            assertEquals(name, 179.0, bigDiamond.getEastExtremeDegrees(), 1e-6);
            assertEquals(name, -179.0, bigDiamond.getWestExtremeDegrees(), 1e-6);

            final BoundedSimplePolygon triangle = new BoundedSimplePolygon(PolygonFixture.triangle.getLatLons(i));
            name = PolygonFixture.triangle.toString() + " " + i;
            assertEquals(name, 70.0, triangle.getNorthExtremeDegrees(), 1e-6);
            assertEquals(name, -10.0, triangle.getSouthExtremeDegrees(), 1e-6);
            assertEquals(name, 80.0, triangle.getEastExtremeDegrees(), 1e-6);
            assertEquals(name, -80.0, triangle.getWestExtremeDegrees(), 1e-6);

            final BoundedSimplePolygon star = new BoundedSimplePolygon(PolygonFixture.star.getLatLons(i));
            name = PolygonFixture.star.toString() + " " + i;
            assertEquals(name, 80.0, star.getNorthExtremeDegrees(), 1e-6);
            assertEquals(name, -50.0, star.getSouthExtremeDegrees(), 1e-6);
            assertEquals(name, 120.0, star.getEastExtremeDegrees(), 1e-6);
            assertEquals(name, -120.0, star.getWestExtremeDegrees(), 1e-6);

            final BoundedSimplePolygon snake = new BoundedSimplePolygon(PolygonFixture.snake.getLatLons(i));
            name = PolygonFixture.snake.toString() + " " + i;
            assertEquals(name, 50.0, snake.getNorthExtremeDegrees(), 1e-6);
            assertEquals(name, -10.0, snake.getSouthExtremeDegrees(), 1e-6);
            assertEquals(name, 0.0, snake.getEastExtremeDegrees(), 1e-6);
            assertEquals(name, -90.0, snake.getWestExtremeDegrees(), 1e-6);
        }
    }
}