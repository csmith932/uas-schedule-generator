///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//package gov.faa.ang.swac.common.flightmodeling;
//
//import gov.faa.ang.swac.common.datatypes.*;
//import gov.faa.ang.swac.common.flightmodeling.IResourceInfo.ResourceType;
//import gov.faa.ang.swac.common.flightmodeling.Restriction.AffectedTraffic;
//import gov.faa.ang.swac.common.geometry.GCEdge;
//import gov.faa.ang.swac.common.geometry.GCPoint;
//
//import java.io.*;
//import java.util.ArrayList;
//import java.util.List;
//
//import org.apache.log4j.LogManager;
//import org.apache.log4j.Logger;
//import org.junit.*;
//
//import static org.junit.Assert.*;
//
///**
// *
// * @author ssmitz
// */
//public class RestrictionTest {
//    private Restriction instance;
//
//	private static Logger logger = LogManager.getLogger(RestrictionTest.class);
//
//	@Before
//	public void setup()
//	{
//		// By default, swac-parent pom's surefire plugin configuration specifies
//		// swac-data/src/test/resources/log4j-junit.xml to be logging configuration file, and that file logs levels
//		// ERROR or higher.
//		// Uncomment to manually setup logger for quick local run 
//		// BasicConfigurator.configure();
//	}
//	
//    public RestrictionTest() {
//    }
//
//    @BeforeClass
//    public static void setUpClass() throws Exception {
//    }
//
//    @AfterClass
//    public static void tearDownClass() throws Exception {
//    }
//    
//    @Before
//    public void setUp() {
//        instance = new Restriction();
//        instance.setName("test");
//        instance.setLabel("label");
//        instance.setFloor(new Altitude(1000.0, Altitude.Units.FEET));
//        instance.setCeiling(new Altitude(30000.0, Altitude.Units.FEET));
//        instance.setTrafficInterval(5.0);
//        instance.setAffectedTrafficFlag(AffectedTraffic.X);
//        instance.setTMI(false);
//        instance.setHeading1(new Angle(180.0, Angle.Units.DEGREES));
//        instance.setHeading2(new Angle(270.0, Angle.Units.DEGREES));
//        
//        List<GCPoint> points = new ArrayList<GCPoint>();
//        List<GCEdge> edges = new ArrayList<GCEdge>();
//        
//        GCPoint point = null;
//        GCPoint lastPoint = null;
//        
//        for (double i = 0; i < 10; i++) {
//            Latitude lat = new Latitude(10.0 + 7.5 * i, Angle.Units.DEGREES);
//            Longitude lon = new Longitude(45.0 + 3.75 * i, Angle.Units.DEGREES);
//            
//            lastPoint = point;
//            point = new GCPoint(lat, lon);
//            
//            points.add(point);
//            
//            if (lastPoint != null) {
//                GCEdge edge = new GCEdge(lastPoint, point);
//                edges.add(edge);
//            }
//        }
//        
//        instance.setPoints(points);
//        instance.setEdges(edges);
//        instance.addAirportPair(new String[]{"ABC","DEF"});
//        instance.addAirportPair(new String[]{"GHI","JKL"});
//        instance.addTimeInterval(new Timestamp[]{new Timestamp(86410000), new Timestamp(86420000)});
//        instance.addTimeInterval(new Timestamp[]{new Timestamp(87020000), new Timestamp(87030000)});
//        instance.setBoundingBox(36.0);
//    }
//    
//    @After
//    public void tearDown() {
//    }
//
//    /**
//     * Test of toString method, of class Restriction.
//     */
//    @Test
//    public void testToString() {
//        logger.info("testToString");
//        String expResult = "Restriction test (label):\n";
//        expResult += "\tFloor/Ceiling = 1000.0/30000.0 ft\n";
//        expResult += "\tInterval = 5.0 min\n";
//        expResult += "\tAffectedTrafficFlag = X, Heading1 = 180.0 deg, Heading2 = 270.0 deg\n";
//        expResult += "\tPoints:\n";
//        expResult += "\t\t 0: 10.000000000000000 N/45.000000000000000 E\n";
//        expResult += "\t\t 1: 17.500000000000000 N/48.750000000000000 E\n";
//        expResult += "\t\t 2: 25.000000000000000 N/52.500000000000010 E\n";
//        expResult += "\t\t 3: 32.500000000000000 N/56.250000000000000 E\n";
//        expResult += "\t\t 4: 40.000000000000000 N/59.999999999999990 E\n";
//        expResult += "\t\t 5: 47.500000000000000 N/63.750000000000010 E\n";
//        expResult += "\t\t 6: 55.000000000000000 N/67.500000000000000 E\n";
//        expResult += "\t\t 7: 62.500000000000010 N/71.250000000000000 E\n";
//        expResult += "\t\t 8: 70.000000000000000 N/75.000000000000000 E\n";
//        expResult += "\t\t 9: 77.500000000000000 N/78.750000000000000 E\n";
//        expResult += "\tAirportPairs:\n";
//        expResult += "\t\t 0: ABC DEF\n";
//        expResult += "\t\t 1: GHI JKL\n";
//        expResult += "\tTimeIntervals:\n";
//        expResult += "\t\t 0: 19700101 19:00:10 19700101 19:00:20\n";
//        expResult += "\t\t 1: 19700101 19:10:20 19700101 19:10:30\n";
//        expResult += "\tTMI: false\n";
//
//        logger.info(expResult);
//        logger.info(instance.toString());
//        
//        String result = instance.toString();
//        assertEquals(expResult, result);
//    }
//
//    /**
//     * Test of writeItem method, of class Restriction.
//     */
//    @Test
//    public void testReadWriteItem() throws Exception {
//        logger.info("testReadWriteItem");
//        StringWriter output = new StringWriter();
//        PrintWriter writer = new PrintWriter(output);
//        instance.writeItem(writer);
//        Restriction newInstance = new Restriction();
//        BufferedReader input = new BufferedReader(new StringReader(output.toString()));
//        newInstance.readItem(input);
//        logger.info(instance.toString());
//        logger.info(newInstance.toString());
//        assertEquals(instance.toString(), newInstance.toString());
//        
//    }
//}
