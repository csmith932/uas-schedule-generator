/**
 * Copyright "TBD", Metron Aviation & CSSI.  All rights reserved.
 * 
 * This computer Software was developed with the sponsorship of the U.S. Government
 * under Contract No. DTFAWA-10-D-00033, which has a copyright license in accordance with AMS 3.5-13.(c)(1).
 */

package gov.faa.ang.swac.common.geometry.kml;

import java.io.PrintWriter;
import java.util.List;

import gov.faa.ang.swac.common.datatypes.Altitude;
import gov.faa.ang.swac.common.geometry.GCEdge;
import gov.faa.ang.swac.common.geometry.GCPoint;
import gov.faa.ang.swac.common.geometry.GCPointAlt;
import gov.faa.ang.swac.common.geometry.GCPointAltTime;
import gov.faa.ang.swac.common.geometry.SimplePolygon;
import java.text.NumberFormat;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;


public class KmlUtilities
{
	private static final Logger logger = LogManager.getLogger(KmlUtilities.class);
	
    public enum GCClass {GCPOINT, GCEDGE, GCPOLYGON, LINE}
    public enum Icon { CIRCLE, SQUARE, TRIANGLE, PUSHPIN }
    public enum Color
    {
        RED     { 	@Override public String hex() { return "ff0000ff"; } 
        			@Override public String lineStyleName() { return "redLineStyle"; } },
        ORANGE  { 	@Override public String hex() { return "ff00a5ff"; } 
        			@Override public String lineStyleName() { return "orangeLineStyle"; } },
        GREEN   { 	@Override public String hex() { return "ff00ff00"; } 
        			@Override public String lineStyleName() { return "greenLineStyle"; } },
        BLUE    { 	@Override public String hex() { return "ffff0000"; } 
        			@Override public String lineStyleName() { return "blueLineStyle"; } },
        INDIGO  { 	@Override public String hex() { return "ff82004B"; } 
        			@Override public String lineStyleName() { return "indigoLineStyle"; } },
        VIOLET  { 	@Override public String hex() { return "ffee82ee"; } 
        			@Override public String lineStyleName() { return "violetLineStyle"; } },
        CYAN    { 	@Override public String hex() { return "ffffff00"; } 
        			@Override public String lineStyleName() { return "cyanLineStyle"; } },
        YELLOW  { 	@Override public String hex() { return "ff00ffff"; } 
        			@Override public String lineStyleName() { return "yellowLineStyle"; } },
        MAGENTA { 	@Override public String hex() { return "ffff00ff"; } 
        			@Override public String lineStyleName() { return "magentaLineStyle"; } },
        WHITE   { 	@Override public String hex() { return "ffffffff"; } 
        			@Override public String lineStyleName() { return "whiteLineStyle"; } },
        BLACK   { 	@Override public String hex() { return "00000000"; } 
        			@Override public String lineStyleName() { return "blackLineStyle"; } };
        /**
         * Returns a color string "<code>aaBBGGRR</code>" in hex where:<br>
         * <code>aa</code> = alpha channel intensity<br>
         * <code>BB</code> = blue channel intensity<br>
         * <code>GG</code> = green channel intensity<br>
         * <code>RR</code> = red channel intensity<br>
         * @return
         */
        abstract public String hex();
        abstract public String lineStyleName();
        public String lineStyleKml() {
        	return 	"\t<Style id=\"" + lineStyleName() + "\">\n" + 
            		"\t\t<LineStyle>\n" + 
            		"\t\t\t<color>"+ hex()+"</color>\n" + 
            		"\t\t</LineStyle>\n" + 
            		"\t</Style>\n";
        }
    }
    
    public enum AltitudeMode
    {
        CLAMPED_TO_GROUND    { @Override public String value() { return "clampToGround"; } },
        CLAMPED_TO_SEAFLOOR  { @Override public String value() { return "clampToSeaFloor"; } },
        RELATIVE_TO_GROUND   { @Override public String value() { return "relativeToGround"; } },
        RELATIVE_TO_SEAFLOOR { @Override public String value() { return "relativeToSeaFloor"; } },
        ABSOLUTE             { @Override public String value() { return "absolute"; } };

        abstract public String value();
    }
   
    /**
     * 
     * @param gcClass
     * @return String containing kml Style configuration
     */
    public static String kmlStyles(GCClass gcClass)
    {
    	 
    	StringBuffer kmlStyles = new StringBuffer();

    	String styles = "";
        
        switch (gcClass)
        {
        case GCPOINT:   
                for (Color color : Color.values())
                {
                    kmlStyles.append(
                        "\t<Style id=\"style_gcpoint_circle_"+color.toString().toLowerCase()+"\">\n" + 
                        "\t\t<IconStyle>\n" + 
                        "\t\t\t<scale>1.2</scale>\n" + 
                        "\t\t\t<color>"+color.hex()+"</color>\n" +
                        "\t\t\t<Icon>\n" + 
                        "\t\t\t\t<href>http://maps.google.com/mapfiles/kml/shapes/placemark_circle.png</href>\n" + 
                        "\t\t\t</Icon>\n" + 
                        "\t\t</IconStyle>\n" + 
                        "\t\t<LabelStyle>\n" + 
                        "\t\t\t<scale>0.5</scale>\n" + 
                        "\t\t</LabelStyle>\n" + 
                        "\t</Style>\n" +
                        "\t<Style id=\"style_gcpoint_square_"+color.toString().toLowerCase()+"\">\n" + 
                        "\t\t<IconStyle>\n" + 
                        "\t\t\t<scale>1.2</scale>\n" + 
                        "\t\t\t<color>"+color.hex()+"</color>\n" +
                        "\t\t\t<Icon>\n" + 
                        "\t\t\t\t<href>http://maps.google.com/mapfiles/kml/shapes/placemark_square.png</href>\n" + 
                        "\t\t\t</Icon>\n" + 
                        "\t\t</IconStyle>\n" + 
                        "\t\t<LabelStyle>\n" + 
                        "\t\t\t<scale>0.5</scale>\n" + 
                        "\t\t</LabelStyle>\n" + 
                        "\t</Style>\n" +
                        "\t<Style id=\"style_gcpoint_triangle_"+color.toString().toLowerCase()+"\">\n" + 
                        "\t\t<IconStyle>\n" + 
                        "\t\t\t<scale>1.2</scale>\n" + 
                        "\t\t\t<color>"+color.hex()+"</color>\n" +
                        "\t\t\t<Icon>\n" + 
                        "\t\t\t\t<href>http://maps.google.com/mapfiles/kml/shapes/triangle.png</href>\n" + 
                        "\t\t\t</Icon>\n" + 
                        "\t\t</IconStyle>\n" + 
                        "\t\t<LabelStyle>\n" + 
                        "\t\t\t<scale>0.5</scale>\n" + 
                        "\t\t</LabelStyle>\n" + 
                        "\t</Style>\n" + 
                        "\t<Style id=\"style_gcpoint_pushpin_"+color.toString().toLowerCase()+"\">\n" +
                        "\t\t<IconStyle>\n" +
                        "\t\t\t<color>"+color.hex()+"</color>\n" +
                        "\t\t\t<scale>1.1</scale>\n" +
                        "\t\t\t<Icon>\n" +
                        "\t\t\t\t<href>http://maps.google.com/mapfiles/kml/pushpin/ylw-pushpin.png</href>\n" +
                        "\t\t\t</Icon>\n" +
                        "\t\t\t<hotSpot x=\"20\" y=\"2\" xunits=\"pixels\" yunits=\"pixels\"/>\n" +
                        "\t\t</IconStyle>\n" +
                        "\t</Style>\n");
                }
            styles = kmlStyles.toString();
            break;
            
        case GCEDGE:

                for (Color color : Color.values())
                {
                    kmlStyles.append(
                       "\t<Style id=\"style_gcedge_"+color.toString().toLowerCase()+"\">\n" + 
                        "\t\t<LineStyle>\n" + 
                        "\t\t\t<color>"+color.hex()+"</color>\n" + 
                        "\t\t\t<width>1</width>\n" +
                        "\t\t</LineStyle>\n" + 
                        "\t\t<PolyStyle>\n" + 
                        "\t\t\t<fill>0</fill>\n" + 
                        "\t\t</PolyStyle>\n" + 
                        "\t</Style>\n" + 
                        "\t<StyleMap id=\"stylemap_gcedge\">\n" + 
                        "\t\t<Pair>\n" + 
                        "\t\t\t<key>normal</key>\n" + 
                        "\t\t\t<styleUrl>#style_gcedge</styleUrl>\n" + 
                        "\t\t</Pair>\n" + 
                        "\t\t<Pair>\n" + 
                        "\t\t\t<key>highlight</key>\n" + 
                        "\t\t\t<styleUrl>#style_gcedge</styleUrl>\n" + 
                        "\t\t</Pair>\n" + 
                        "\t</StyleMap>\n");
                }
            styles = kmlStyles.toString();
            break;
            
        case GCPOLYGON:
                for (Color color : Color.values())
                {
                   kmlStyles.append(
                        "\t<Style id=\"style_gcpolygon_"+color.toString().toLowerCase()+"\">\n" + 
                        "\t\t<LineStyle>\n" + 
                        "\t\t\t<color>"+color.hex()+"</color>\n" + 
                        "\t\t</LineStyle>\n" + 
                        "\t\t<PolyStyle>\n" + 
                        "\t\t\t<fill>0</fill>\n" + 
                        "\t\t</PolyStyle>\n" + 
                        "\t</Style>\n");
                }
            styles = kmlStyles.toString();
            break;
            
        case LINE:
            for (Color color : Color.values())
            {
               kmlStyles.append(color.lineStyleKml());
            }
        styles = kmlStyles.toString();
        break;
            
        default:
            break;
        }
        
        return styles;
    }
    
    /**
     * Converts <code>GCPoint<code> to String containing kml
     * @param point
     * @return
     */
    public static String toKml(GCPoint point)
    {
        if (point instanceof GCPointAlt)
        {
            return toKml(point, point.toString(), Icon.CIRCLE, Color.WHITE, AltitudeMode.ABSOLUTE, null);
        }

        return toKml(new GCPointAlt(point.latitude(), point.longitude(), Altitude.valueOfFeet(0.0)), point.toString(), Icon.CIRCLE, Color.WHITE, AltitudeMode.CLAMPED_TO_GROUND, null);            
    }

    /**
     * Converts <code>GCPoint<code> to String containing kml.
     * @param point
     * @param name
     * @param icon
     * @param color
     * @param altitudeMode
     * @param comment
     * @return
     */
    public static String toKml(GCPoint point, String name, Icon icon, Color color, AltitudeMode altitudeMode, String comment)
    {
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMinimumFractionDigits(4);
        nf.setMaximumFractionDigits(4);

        GCPointAlt pointAlt = null;
        if (point instanceof GCPointAlt)
        {
            pointAlt = (GCPointAlt)point;
        }
        else
        {
            pointAlt = new GCPointAlt(point.latitude(), point.longitude(), Altitude.valueOfFeet(0.0));
        }
        
        StringBuilder output = new StringBuilder();

        // Write out as a KML polygon
        output.append("\t<Placemark>\n" + 
                     "\t\t<name>" + (name == null ? "" : name) + "</name>\n" + 
                     "\t\t<visibility>0</visibility>\n");

        output.append("\t\t<LookAt>\n" +
                "\t\t\t<TimeStamp>\n" + 
                "\t\t\t\t<when>");
        
        if (point instanceof GCPointAltTime) {
            output.append(((GCPointAltTime)point).timestamp().toISO8601(false));
        }
        
        output.append("</when>\n" + 
                "\t\t\t</TimeStamp>\n" +
                "\t\t\t<longitude>"+nf.format(pointAlt.longitude().degrees())+"</longitude>\n" +
                "\t\t\t<latitude>"+nf.format(pointAlt.latitude().degrees())+"</latitude>\n" +
                "\t\t\t<altitude>"+pointAlt.altitude().meters()+"</altitude>\n" +
                "\t\t\t<range>25000.0</range>\n" +
                "\t\t\t<tilt>0</tilt>\n" +
                "\t\t\t<heading>0</heading>\n" +
                "\t\t</LookAt>\n");
        
        output.append("\t\t<styleUrl>#style_gcpoint_"+icon.toString().toLowerCase()+"_"+color.toString().toLowerCase()+"</styleUrl>\n");            
                
        output.append("\t\t<Point>\n" + 
                "\t\t\t<altitudeMode>" + altitudeMode.value() + "</altitudeMode>\n" +
                "\t\t\t<coordinates>" + nf.format(pointAlt.longitude().degrees()) + "," + nf.format(pointAlt.latitude().degrees()) + "," + pointAlt.altitude().meters() + " </coordinates>\n" +
                "\t\t</Point>\n" + 
                "\t</Placemark>\n\n\n");
        return output.toString();
    }
    
    //================================================================================
    // GCPoint paths
    //================================================================================

    @Deprecated
    public static String toKml(List<? extends GCPoint> points, String name)
    {
        return toKml(points, name, Color.WHITE, AltitudeMode.CLAMPED_TO_GROUND);
    }

    /**
     * Prints a KML LineString with the list of {@link GCPoint}s. If any points are also {@link GCPointAlt} or {@link GCPointAltTime},
     * then {@link Altitude} and/or {@link Timestamp} information is also represented in KML. 
     */
    public static String toKml(List<? extends GCPoint> points, String name, Color color, AltitudeMode altitudeMode)
    {
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMinimumFractionDigits(4);
        nf.setMaximumFractionDigits(4);
        
        StringBuilder output = new StringBuilder();

        // Write out as a KML path (and a set of points)
        output.append(
                "\t<Placemark>\n" + 
                "\t\t<name>" + name + "</name>\n" + 
                "\t\t<styleUrl>#" + color.lineStyleName() + "</styleUrl>\n");
        
        GCPoint start = points.get(0);
        GCPoint end = points.get(points.size()-1);
        if (start instanceof GCPointAltTime && end instanceof GCPointAltTime) { 
            output.append("\t\t<timespan>\n\t\t\t<begin>" + ((GCPointAltTime)start).timestamp().toISO8601(false) + "</begin>\n"); 
            output.append("\t\t\t<end>" + ((GCPointAltTime)end).timestamp().toISO8601(false) + "</end>\n\t\t</timespan>\n");
        }
        
        // Write KML path object
        output.append(
                "\t\t<LineString>\n" +
                "\t\t\t<extrude>1</extrude>\n" +
                "\t\t\t<tessellate>1</tessellate>\n" +
                "\t\t\t<altitudeMode>" + altitudeMode.value() + "</altitudeMode>\n" +
                "\t\t\t<coordinates>");            
        
        boolean first = true;
        for (GCPoint point : points)
        {
        	try {
        		if (first) {
        			first = false;
        		} else {
        			output.append(" ");
        		}
        		output.append(nf.format(point.longitude().degrees()) + "," + nf.format(point.latitude().degrees()) + "," + (point instanceof GCPointAlt ? Math.round(((GCPointAlt)point).altitude().meters()) : "0"));
        	} catch (Exception ex) {
        		logger.error("Invalid trajectory point for KML output: " + point.toString());
        		return null;
        	}
        }
        
        output.append("</coordinates>\n" +
                "\t\t</LineString>\n" + 
                "\t</Placemark>\n");
        return output.toString();
    }

    //================================================================================
    // GCEdge
    //================================================================================
    @Deprecated
	public static String toKml(GCEdge edge)
    {
        return toKml(edge, "GCEdge #"+edge.instanceId, Color.RED, null);
    }
    
    @Deprecated
	public static String toKml(GCEdge edge, String name, Color color, String comment)
    {
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMinimumFractionDigits(4);
        nf.setMaximumFractionDigits(4);
        
        StringBuilder output = new StringBuilder();

        // Write out as a KML polygon
        output.append("\t<Placemark>\n" + 
                     "\t\t<name>" + name + "</name>\n" + 
                     "\t\t<visibility>0</visibility>\n" + 
                     "\t\t<timespan>\n");
        if (edge.first() instanceof GCPointAltTime) {
            GCPointAltTime p = (GCPointAltTime)edge.first();
            output.append("\t\t\t<begin>" + p.timestamp().toISO8601(false) + "</begin>\n");
        }
        if (edge.second() instanceof GCPointAltTime) {
            GCPointAltTime p = (GCPointAltTime)edge.second();
            output.append("\t\t\t<end>" + p.timestamp().toISO8601(false) + "</end>\n");
        }
        output.append("\t\t</timespan>\n" +
                    "\t\t<styleUrl>#stylemap_gcedge_"+color.toString().toLowerCase()+"</styleUrl>\n" +
                    "\t\t<LineString>\n" + 
                    "\t\t\t<tessellate>1</tessellate>\n" + 
                    "\t\t\t<coordinates>" + nf.format(edge.first().longitude().degrees()) + "," + nf.format(edge.first().latitude().degrees()) + ",0 " +
                    nf.format(edge.second().longitude().degrees()) + "," + nf.format(edge.second().latitude().degrees()) + ",0 " + "</coordinates>\n" +
                    "\t\t</LineString>\n" + 
                    "\t</Placemark>\n");
        
        return output.toString();
    }
    
    //================================================================================
    // GCPolygon
    //================================================================================
    @Deprecated
	public static String toKml(SimplePolygon polygon)
    {
        return toKml(polygon, "SimplePolygon #" + polygon.instanceId, Color.RED);
    }
    
    @Deprecated
	public static String toKml(SimplePolygon polygon, String name, Color color)
    {
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMinimumFractionDigits(4);
        nf.setMaximumFractionDigits(4);
        
        StringBuilder output = new StringBuilder();

        // Write out as a KML polygon
        output.append("\t<Placemark>\n" + 
                     "\t\t<name>" + name + "</name>\n" + 
                     "\t\t<styleUrl>#style_gcpolygon_" + color.toString().toLowerCase() + "</styleUrl>\n" +
                     "\t\t<LineString>\n" + 
                     "\t\t\t<tessellate>1</tessellate>\n" + 
                     "\t\t\t<coordinates>");
        
        for (GCPoint point : polygon.points())
        {
            output.append("\t\t\t\t" + nf.format(point.longitude().degrees()) + "," + nf.format(point.latitude().degrees()) + ",0\n");
        }
        
        output.append("\t\t\t</coordinates>\n" +
                      "\t\t</LineString>\n" + 
                      "\t</Placemark>\n");
        
        return output.toString();
    }
    
 // ------------------------------------------------------
	// TODO: csmith 2/1/2011: static helper methods allow classes
	// that formerly used this as a writer to instead implement
	// TextSerializable to dump their state to kml
	// ------------------------------------------------------
	
    @Deprecated
	public static void open(PrintWriter writer, String documentName)
	{

		writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
                         "<kml xmlns=\"http://earth.google.com/kml/2.2\">\n" +
                         "<Document>\n" +
                         "\t<name>" + documentName + "</name>\n" +
					     "\t<open>1</open>\n"); 
	}
	
	@Deprecated
	public static void write(PrintWriter writer, String str)
	{
		writer.write(str);
	}

	@Deprecated
	public static void folderOpen(PrintWriter writer, String name)
	{
		writer.write("\t<Folder>\n" +
			  "\t\t<name>" + name + "</name>\n");
	}

	@Deprecated
	public static void folderClose(PrintWriter writer)
	{
		writer.write("\t</Folder>\n");
	}

	@Deprecated
	public static void close(PrintWriter writer)
	{
		writer.write("</Document>\n" +
		  			 "</kml>\n");
	}

}