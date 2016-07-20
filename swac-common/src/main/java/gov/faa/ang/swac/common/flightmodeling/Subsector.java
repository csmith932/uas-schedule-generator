/**
 * Copyright "TBD", Metron Aviation & CSSI.  All rights reserved.
 * 
 * This computer Software was developed with the sponsorship of the U.S. Government
 * under Contract No. DTFAWA-10-D-00033, which has a copyright license in accordance with AMS 3.5-13.(c)(1).
 */

package gov.faa.ang.swac.common.flightmodeling;

import gov.faa.ang.swac.common.datatypes.Altitude;
import gov.faa.ang.swac.common.datatypes.Angle;
import gov.faa.ang.swac.common.datatypes.Latitude;
import gov.faa.ang.swac.common.datatypes.Longitude;
import gov.faa.ang.swac.common.datatypes.Patterns;
import gov.faa.ang.swac.common.geometry.GCPoint;
import gov.faa.ang.swac.common.geometry.SimplePolygon;
import gov.faa.ang.swac.common.geometry.kml.KmlUtilities.Color;
import gov.faa.ang.swac.common.geometry.kml.KmlUtilities.Icon;
import gov.faa.ang.swac.common.utilities.Mathematics;
import gov.faa.ang.swac.datalayer.storage.fileio.TextSerializable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



/**
 * Represents a subsector. A {@link Subsector} is a region of airspace with a polygonal boundary capped by a 
 * {@link #floor()} and {@link #ceiling()} of constant {@link Altitude}.
 * {@link Subsector}s are grouped in collections known as {@link Sector}s.
 * @author Jason Femino - CSSI, Inc.
 * @see Sector
 */
public class Subsector implements TextSerializable
{
    private static org.apache.log4j.Logger logger = org.apache.log4j.LogManager.getLogger(Subsector.class);

	//---------------------
	// Static class members
	//---------------------	

	// toString related members
	public static final String SEP = ",";
	public static final String TEXT_RECORD_LABEL = "SUBSECTOR:";
	public static final String TEXT_RECORD_KEY = TEXT_RECORD_LABEL + " name" + SEP + "floor" + SEP + "ceiling\n" + "lat" + SEP + "lon...";

	protected static final String LAT_LON_RECORD_PATTERN               = "\\s*?("   + Patterns.FLOAT + ")\\s*?" + SEP + "\\s*?("   +  Patterns.FLOAT + ")\\s*?";
	protected static final String LAT_LON_RECORD_PATTERN_NON_CAPTURING = "\\s*?(:?" + Patterns.FLOAT + ")\\s*?" + SEP + "\\s*?(:?" +  Patterns.FLOAT + ")\\s*?";
	protected static final Pattern latLonRecordPattern = Pattern.compile(LAT_LON_RECORD_PATTERN);

	public static final String TEXT_RECORD_PATTERN = "^" + TEXT_RECORD_LABEL +
	    "\\s*?(\\S+?)\\s*?" + SEP +                            // Name
        "\\s*?(\\S+?)\\s*?" + SEP +                            // Center
        "\\s*?(\\S+?)\\s*?" + SEP +                            // Sector
        "\\s*?(\\S+?)\\s*?" + SEP +                            // FPA
        "\\s*?([MX]?)\\s*?" + SEP +                            // Module char
        "\\s*?(\\S+?)\\s*?" + SEP +                            // Module number
		"\\s*?(" + Patterns.FLOAT + ")\\s*?" + SEP +           // floor
		"\\s*?(" + Patterns.FLOAT + ")\\s*?$\\s?" +            // ceiling
		"((:?" + LAT_LON_RECORD_PATTERN_NON_CAPTURING + ")*)"; // lat/lon pairs
	public static final String TEXT_RECORD_PATTERN_NON_CAPTURING = "^" + TEXT_RECORD_LABEL +
	    "\\s*?(:?\\S+?)\\s*?" + SEP +                            // Name
        "\\s*?(:?\\S+?)\\s*?" + SEP +                            // Center
	    "\\s*?(:?\\S+?)\\s*?" + SEP +                            // Sector
	    "\\s*?(:?\\S+?)\\s*?" + SEP +                            // FPA
        "\\s*?(:?[MX]?)\\s*?" + SEP +                            // Module char
        "\\s*?(:?\\S+?)\\s*?" + SEP +                            // Module number
		"\\s*?(:?" + Patterns.FLOAT + ")\\s*?" + SEP +           // floor
		"\\s*?(:?" + Patterns.FLOAT + ")\\s*?$\\s?" +            // ceiling
		"(:?(:?" + LAT_LON_RECORD_PATTERN_NON_CAPTURING + ")*)"; // lat/lon pairs
	protected static final Pattern textRecordPattern = Pattern.compile(TEXT_RECORD_PATTERN, Pattern.MULTILINE);

    private String name = null;
    private String center = null;
	private String sector = null;
	private String fpa = null;
	private Character moduleChar = null;
	private Integer module = null;
    private Altitude ceiling = Altitude.NULL;
    private Altitude floor = Altitude.NULL;
	private SimplePolygon polygon = null;
	
	public Subsector()
	{
	}

    @Override
    public boolean equals(Object o)
    {
    	if (!(o instanceof Subsector))
    	{
    		return false;
    	}
    	
    	Subsector subsector = (Subsector)o;
    	
        if ( (this.name == null && subsector.name != null) ||
             (this.name != null && subsector.name == null) ||
             (this.name != null && subsector.name != null && !this.name.equals(subsector.name)))
        {
            return false;
        }

        if ( (this.center == null && subsector.center != null) ||
             (this.center != null && subsector.center == null) ||
             (this.center != null && subsector.center != null && !this.center.equals(subsector.center)))
        {
            return false;
        }

        if ( (this.sector == null && subsector.sector != null) ||
             (this.sector != null && subsector.sector == null) ||
             (this.sector != null && subsector.sector != null && !this.sector.equals(subsector.sector)))
        {
            return false;
        }

        if ( (this.fpa == null && subsector.fpa != null) ||
             (this.fpa != null && subsector.fpa == null) ||
             (this.fpa != null && subsector.fpa != null && !this.fpa.equals(subsector.fpa)))
        {
            return false;
        }

        if ( (this.module == null && subsector.module != null) ||
             (this.module != null && subsector.module == null) ||
             (this.module != null && subsector.module != null && !this.module.equals(subsector.module)))
        {
            return false;
        }

        if ( (this.ceiling == null && subsector.ceiling != null) ||
             (this.ceiling != null && subsector.ceiling == null) ||
             (this.ceiling != null && subsector.ceiling != null && !Mathematics.equals(this.ceiling.feet(), subsector.ceiling.feet())))
        {
            return false;
        }

        if ( (this.floor == null && subsector.floor != null) ||
             (this.floor != null && subsector.floor == null) ||
             (this.floor != null && subsector.floor != null && !Mathematics.equals(this.floor.feet(), subsector.floor.feet())))
        {
            return false;
        }

    	if ( (this.polygon == null && subsector.polygon != null) ||
       		 (this.polygon != null && subsector.polygon == null) ||
       		 (this.polygon != null && subsector.polygon != null && !this.polygon.equals(subsector.polygon)))
       	{
       		return false;
       	}
    	
    	return true;
    }
    
    @Override
    public int hashCode()
    {
        return this.toString().hashCode();
    }
	
    public String name()
    {
        return this.name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public String center()
    {
        return this.center;
    }
    
    public void setCenter(String center)
    {
        this.center = center;
    }
    
    public String sector()
    {
        return this.sector;
    }
    
    public void setSector(String sector)
    {
        this.sector = sector;
    }
    
    public String fpa()
    {
        return this.fpa;
    }
    
    public void setFpa(String fpa)
    {
        this.fpa = fpa;
    }
    
    public Character moduleChar()
    {
        return this.moduleChar;
    }
    
    public void setModuleChar(Character moduleChar)
    {
        this.moduleChar = moduleChar;
    }
    
    public Integer module()
    {
        return this.module;
    }
    
    public void setModule(Integer module)
    {
        this.module = module;
    }
    
    public Altitude floor()
    {
        return this.floor;
    }
    
    public void setFloor(Altitude floor)
    {
        this.floor = floor;
    }
    
	public Altitude ceiling()
	{
		return this.ceiling;
	}
	
    public void setCeiling(Altitude ceiling)
    {
        this.ceiling = ceiling;
    }
    
	public SimplePolygon polygon()
	{
		return this.polygon;
	}
	
	public void setPolygon(SimplePolygon polygon)
	{
		this.polygon = polygon;
	}
	
	@Override
	public String toString()
	{
		StringBuilder output = new StringBuilder();
		
	    output.append( "SUBSECTOR: " +
                this.name + SEP + " " +
                this.center + SEP + " " +
                this.sector + SEP + " " +
                (this.fpa == null ? "" : this.fpa) + SEP + " " +
                (this.moduleChar == null ? "" : this.moduleChar) + SEP + " " +
                (this.module == null ? "" : this.module) + SEP + " " +
	            this.floor.feet() + SEP + " " +
	            this.ceiling.feet() + "\n");
	    
	    // Print out SUBSECTOR LatLon info
		for (GCPoint point : this.polygon.points())
		{
			output.append(String.format("   %1$1.6f%2$s %3$1.6f\n", point.latitude().degrees(), SEP, point.longitude().degrees()));
		}
	    
	    return output.toString();
	}
	
    public static Subsector fromTextRecord(String str)
    {
    	Subsector subsector = null;

		// If this string matches the pattern, initialize the object
		Matcher matcher = textRecordPattern.matcher(str);
		if (!matcher.find())
		{
			logger.warn("Subsector error: Input \"" + str + "\", does not match pattern \"" + textRecordPattern.pattern() + "\"! Returning null.");
			return null;
		}
		
		try
		{
			// Note: No need to use trim() with any of the matcher.group() calls... the pattern should strip them out
			subsector = new Subsector();			
            subsector.setName( matcher.group(1) );
            subsector.setCenter( matcher.group(2) );
            subsector.setSector( matcher.group(3) );
            if (matcher.group(4) != null && matcher.group(4).length() > 0 ) { subsector.setFpa( matcher.group(4) ); }
            if (matcher.group(5) != null && matcher.group(5).length() > 0 ) { subsector.setModuleChar( matcher.group(5).charAt(0) ); }
            subsector.setModule( Integer.valueOf(matcher.group(6)) );
			subsector.floor = Altitude.valueOfFeet( Double.valueOf(matcher.group(7)) );
			subsector.ceiling = Altitude.valueOfFeet( Double.valueOf(matcher.group(8)) );
			
			String latLonPairs = matcher.group(9);
			String[] latLons = latLonPairs.split("\n");
                        double[] dLatLons = new double[2 * latLons.length];
			for (int i = 0; i < latLons.length; i++)
			{
                            String latLon = latLons[i];
				if (latLon.trim().length() > 0)
				{
					Matcher latLonMatcher = latLonRecordPattern.matcher(latLon);
					if (!latLonMatcher.find())
					{
						logger.warn("Subsector error: Expected lat/lon line, got \"" + latLon + "\", skipping line...");
					}
					else
					{
						double lat = Double.valueOf(latLonMatcher.group(1));
						double lon = Double.valueOf(latLonMatcher.group(2));
                                                dLatLons[2 * i] = lat;
                                                dLatLons[2 * i + 1] = lon;
					}
				}
			}
			SimplePolygon polygon = new SimplePolygon(dLatLons);
			
			subsector.setPolygon(polygon);
			return subsector;
		}
		catch (Exception e)
		{
			logger.warn("Subsector: Exception raised parsing Subsector string \"" + str + "\", returning null!");
			return null;
		}
    }

	public String toKml()
	{
		StringBuilder output = new StringBuilder();

		// Write out as a KML polygon
	    output.append("       <Placemark>\n" + 
			     	"               <name>" + this.name + "</name>\n" + 
			     	"               <description>Floor = " + this.floor + " feet\n" +
			     	"Ceiling = " + this.ceiling + " feet\n" + 
			     	"Vertices:\n");
	    int i=1;
	    for ( GCPoint point : this.polygon.points() )
	    {
	    	output.append("     " + (i++) + ":  " + point.toString() + "\n");
	    }

	    output.append("</description>\n" + 
				    "               <styleUrl>#stylemap_sector</styleUrl>\n" + // Style defined in KMZ file header 
				    "               <Polygon>\n" + 
				    "                       <tessellate>1</tessellate>\n" + 
				    "                       <outerBoundaryIs>\n" + 
				    "                               <LinearRing>\n" + 
				    "                                       <coordinates>");
			
	    // Write out each point's coordinates in the following format: "<lon>,<lat>,<alt>" (spaces are only between coordinates)
	    for (GCPoint point : this.polygon.points())
		{
	    	output.append(point.longitude().degrees() + "," + point.latitude().degrees() + ",0 " );
		}
			
		output.append("</coordinates>\n" +
				    "                               </LinearRing>\n" + 
				    "                       </outerBoundaryIs>\n" + 
				    "               </Polygon>\n" + 
				    "       </Placemark>\n\n\n");
		
		// Write out a folder of vertex points
		output.append("<Folder>\n" +
				"        <name>Vertices</name>");

		for (GCPoint point : this.polygon().points())
		{
		    output.append("       <Placemark>\n" + 
			     		"               <name>" + point.toString() + "</name>\n" + 
				     	"               <visibility>0</visibility>\n" + 
				     	"               <description>" + point.latitude().degrees() + ", " + point.longitude().degrees() + "\n" +
				     	Latitude.toString(point.latitude().degrees(), Angle.Format.LONG_NSEW) + "   " + Longitude.toString(point.longitude().degrees(), Angle.Format.LONG_NSEW) +
				     	"</description>\n");
		    
	    	output.append("              <styleUrl>#style_gcpoint_"+Icon.CIRCLE.toString().toLowerCase()+"_"+Color.WHITE.toString().toLowerCase()+"</styleUrl>\n");	    	
		    		
		    output.append("              <Point>\n" + 
					"                      <coordinates>" + point.longitude().degrees() + "," + point.latitude().degrees() + ",0 " + "</coordinates>\n" +
					"              </Point>\n" + 
					"       </Placemark>\n\n\n");
		}
		output.append("</Folder>\n");
		
		return output.toString();
	}
		
	public String toKml3D()
	{
		StringBuilder output = new StringBuilder();
		List<GCPoint> vertices = this.polygon.points();
		final double FEET_PER_METERS = 3.208399;

		// Write out as KML polygons

		//--------------------------------------------------------------------------------
		// "Create" Subsector folder
	    output.append("       <Folder>\n" + 
			     	"               <name>" + this.name + "</name>\n" + 
			     	"               <description>Floor = " + this.floor.feet() + " feet\n" +
			     	"Ceiling = " + this.ceiling.feet() + " feet\n" + 
			     	"Vertices:\n");
	    int i=0;
	    for ( GCPoint point : vertices )
	    {
	    	output.append("     " + (++i) + ":  " + Latitude.toString(point.latitude().degrees(), Angle.Format.LONG_NSEW) + "   " + Longitude.toString(point.longitude().degrees(), Angle.Format.LONG_NSEW) + "\n");
	    }

	    output.append("</description>\n");
	    
	    
		//--------------------------------------------------------------------------------
		// Subsector floor & ceiling
	    for (int j=0; j<=1; j++) // j=0 for "Floor" polygon, j=1 for "Ceiling" polygon
	    {
	    	String name = "Floor";
	    	int altitude = (int)Math.round(this.floor.feet());
	    	if (j==1)
	    	{
		    	name = "Ceiling";
		    	altitude = (int)Math.round(this.ceiling.feet());
	    	}
		    output.append("       <Placemark>\n" + 
			     	"               <name>" + name + "</name>\n" + 
			     	"               <styleUrl>#stylemap_sector_3d</styleUrl>\n" + 
					"               <Polygon>\n" + 
					"                       <tessellate>1</tessellate>\n" + 
					"                       <altitudeMode>absolute</altitudeMode>\n" +
					"                       <outerBoundaryIs>\n" + 
					"                               <LinearRing>\n" + 
		    	    "                                       <coordinates>");
		    // Write out each point's coordinates in the following format: "<lon>,<lat>,<alt>" (spaces are only between coordinates)
		    for (int count=0; count<=vertices.size(); count++)
			{
		    	int index = count % vertices.size();
		    	GCPoint point = vertices.get(index);
		    	output.append(point.longitude() + "," + point.latitude() + "," + (int)(altitude/FEET_PER_METERS) + " ");
			}
			output.append("</coordinates>\n" +
					    "                               </LinearRing>\n" + 
					    "                       </outerBoundaryIs>\n" + 
					    "               </Polygon>\n" +
					    "        </Placemark>\n");
	    }
				
		//--------------------------------------------------------------------------------
		// Subsector sides
		for (int side=0; side<vertices.size(); side++)
		{
			int next = (side + 1) % vertices.size(); // Index of next point in Vector
			
		    output.append("       <Placemark>\n" + 
			     	"               <name>Side " + side + "</name>\n" + 
			     	"               <styleUrl>#stylemap_sector_3d</styleUrl>\n" + 
					"               <Polygon>\n" + 
					"                       <tessellate>1</tessellate>\n" + 
					"                       <altitudeMode>absolute</altitudeMode>\n" +
					"                       <outerBoundaryIs>\n" + 
					"                               <LinearRing>\n" + 
		    	    "                                       <coordinates>" +
		    	    vertices.get(side).longitude() + "," + vertices.get(side).latitude() + "," + (int)(this.floor.feet()/FEET_PER_METERS) + " " +
		    	    vertices.get(next).longitude() + "," + vertices.get(next).latitude() + "," + (int)(this.floor.feet()/FEET_PER_METERS) + " " +
		    	    vertices.get(next).longitude() + "," + vertices.get(next).latitude() + "," + (int)(this.ceiling.feet()/FEET_PER_METERS) + " " +
		    	    vertices.get(side).longitude() + "," + vertices.get(side).latitude() + "," + (int)(this.ceiling.feet()/FEET_PER_METERS) + " " +
		    	    vertices.get(side).longitude() + "," + vertices.get(side).latitude() + "," + (int)(this.floor.feet()/FEET_PER_METERS) + " </coordinates>\n" +
				    "                               </LinearRing>\n" + 
				    "                       </outerBoundaryIs>\n" + 
				    "               </Polygon>\n" +
		    		"        </Placemark>\n");
		}		
		
		output.append("       </Folder>\n\n\n"); // "Close" folder
		
		return output.toString();
	}
	
	//-----------------------------------------------------------------------
	// TextSerializable implementation (originally from ACESSSectorFileReader
	//-----------------------------------------------------------------------

	public static final String MODULE_NAME_PATTERN_1 = "([a-zA-Z]{3})" + // Center
	// name
"(\\S{2})" + // Sector number (could occasionally be letters instead
// of numbers)
"(\\S{2})" + // FPA number (could occasionally be letters instead of
// numbers)
"([MX])" + // Module character
"(\\d{1,2})"; // Module number

public static final String MODULE_NAME_PATTERN_2 = "([a-zA-Z]{3})" + // Center
	// name
"(\\S{2})"; // Sector number (could occasionally be letters instead
// of numbers)

public static final String MODULE_HEADER_PATTERN = "^\\.(\\S+)\\s+" + // Initial
	// period,
	// Center
	// name,
	// Subsector
	// #,
	// FPA
	// #,
	// Module
"(\\d+)\\s+" + // Floor altitude (hundreds of feet)
"(\\d+)\\s*"; // Ceiling altitude (hundreds of feet)
// NOTE: The rest of the header line may contain other information such as
// FPA-associated fixes, and tracon area names.

public static final String VERTEX_RECORD_PATTERN = "^\\s*("
+ Patterns.FLOAT + ")" + // Latitude (decimal degrees)
"\\s+(" + Patterns.FLOAT + ")\\s*$"; // Longitude (decimal degrees,
// west-positive)

// -----------------------
// Instance class members
// -----------------------
protected static final Pattern moduleNamePattern1 = Pattern
.compile(MODULE_NAME_PATTERN_1);
protected static final Pattern moduleNamePattern2 = Pattern
.compile(MODULE_NAME_PATTERN_2);
protected static final Pattern moduleHeaderPattern = Pattern
.compile(MODULE_HEADER_PATTERN);
protected static final Pattern vertexRecordPattern = Pattern
.compile(VERTEX_RECORD_PATTERN);

	@Override
	public void readItem(BufferedReader reader) throws IOException {
		String currentLine = reader.readLine();
		
		Matcher matcher = moduleHeaderPattern.matcher(currentLine);
		if (matcher.find()) {
			this.setName(matcher.group(1));
			this.setFloor(Altitude.valueOfFeet(Integer
					.valueOf(matcher.group(2)) * 100.0)); // Convert
															// Flight
															// Level to
															// feet
			this.setCeiling(Altitude.valueOfFeet(Integer
					.valueOf(matcher.group(3)) * 100.0)); // Convert
															// Flight
															// Level to
															// feet

			// Attempt to break up the module name into Center, Sector,
			// FPA, & Module...
			// NOTE: Not all names are formatted properly to allow this
			// division
			Matcher nameMatcher1 = moduleNamePattern1.matcher(this.name());
			if (nameMatcher1.find()) {
				this.setCenter(nameMatcher1.group(1));
				this.setSector(nameMatcher1.group(2));
				this.setFpa(nameMatcher1.group(3));
				this.setModuleChar(nameMatcher1.group(4).charAt(0));
				this.setModule(Integer.valueOf(nameMatcher1
						.group(5)));
			} else // If the module name cannot be broken up... assume
					// the first three chars of the name is the center
			{
				Matcher nameMatcher2 = moduleNamePattern2
						.matcher(this.name());
				if (nameMatcher2.find()) {
					this.setCenter(nameMatcher2.group(1));
					this.setSector(nameMatcher2.group(2));
					logger.warn("ACESSectorFileReader.getRecord(): Module name \""
							+ this.name()
							+ "\" does not match name pattern 1. Setting center to \""
							+ this.center()
							+ "\" and sector to \""
							+ this.sector()
							+ "\"... (fpa #, module char, & module # will be left null)");
				} else {
					this.setCenter(this.name()
							.substring(0, 3));
					logger.warn("ACESSectorFileReader.getRecord(): Module name \""
							+ this.name()
							+ "\" does not match name pattern 2. Setting center to \""
							+ this.center()
							+ "\"... (sector #, fpa #, module char, & module # will be left null)");
				}
			}

			// ---------------------
			// Process flight nodes
			// ---------------------
			currentLine = reader.readLine();
			List<GCPoint> vertices = new ArrayList<GCPoint>();
			while (currentLine != null
					&& !currentLine.startsWith(".")) {
				matcher = vertexRecordPattern.matcher(currentLine);
				if (matcher.find()) {
					double lat = Double.valueOf(matcher.group(1));
					double lon = -Double.valueOf(matcher.group(2)); // Convert
																	// west-positive
																	// to
																	// east-positive
					vertices.add(new GCPoint(Latitude
							.valueOfDegrees(lat), Longitude
							.valueOfDegrees(lon)));
				} else {
					logger.fatal("ACESSubsectorFileReader error: Expected vertex line. Got: \""
							+ currentLine + "\"");
					throw new RuntimeException();

				}
				
				// Need to mark and rewind
				reader.mark(8000);
				currentLine = reader.readLine();
			}
			if (currentLine != null)
			{
				reader.reset();
			}
			
			this.setPolygon(new SimplePolygon(vertices.toArray(new GCPoint[vertices.size()])));
		}
	}

	@Override
	public void writeItem(PrintWriter writer) throws IOException 
	{
		writer.println(this.toKml());
	}
}