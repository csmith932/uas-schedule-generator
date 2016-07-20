/**
 * Copyright "TBD", Metron Aviation & CSSI.  All rights reserved.
 * 
 * This computer Software was developed with the sponsorship of the U.S. Government
 * under Contract No. DTFAWA-10-D-00033, which has a copyright license in accordance with AMS 3.5-13.(c)(1).
 */

package gov.faa.ang.swac.common.flightmodeling;

import gov.faa.ang.swac.common.datatypes.Altitude;
import gov.faa.ang.swac.common.datatypes.Angle;
import gov.faa.ang.swac.common.datatypes.Angle.Format;
import gov.faa.ang.swac.common.datatypes.Latitude;
import gov.faa.ang.swac.common.datatypes.Longitude;
import gov.faa.ang.swac.common.datatypes.Timestamp;
import gov.faa.ang.swac.common.geometry.GCEdge;
import gov.faa.ang.swac.common.geometry.GCPoint;
import gov.faa.ang.swac.datalayer.storage.fileio.HeaderUtils;
import gov.faa.ang.swac.datalayer.storage.fileio.TextSerializable;
import gov.faa.ang.swac.datalayer.storage.fileio.WithHeader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Restriction extends AbstractResourceInfo implements IGriddableResource, TextSerializable, WithHeader, Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -9000945134528695543L;

	public enum AffectedTraffic
	{
		O, // Only traffic traveling between listed O/D airport pairs are assigned the restriction
		X, // All traffic except traffic traveling between listed O/D airport pairs are assigned the restriction
		A  // All traffic traveling in the direction bounded by Headings 1 & 2 are assigned the restriction
	}
	
	private String name = null;
	private String label = null;
	private Altitude floor = null;
	private Altitude ceiling = null;
	private Double trafficInterval = null;
	private AffectedTraffic affectedTrafficFlag = null;
	private boolean isTMI;
	
	private Angle heading1 = null;
	private Angle heading2 = null;

	private List<GCPoint> points = null;
	private List<String[]> airportPairs = null;
	private List<Timestamp[]> timeIntervals = null;
	
	// Bounding box related
	private GCPoint northEastPoint;
	private GCPoint southWestPoint;
	
        public Restriction() {
            this.name = null;
            this.label = null;
            this.floor = null;
            this.ceiling = null;
            this.trafficInterval = null;
            this.affectedTrafficFlag = null;
            this.heading1 = null;
            this.heading2 = null;
            this.points = null;
            this.airportPairs = null;
            this.timeIntervals = null;
        }
        
        public Restriction(Restriction org) {
            this.name = org.name;
            this.label = org.label;
            this.floor = org.floor;
            this.ceiling = org.ceiling;
            this.trafficInterval = (org.trafficInterval == null ? null : org.trafficInterval.doubleValue());
            this.affectedTrafficFlag = org.affectedTrafficFlag;
            this.isTMI = org.isTMI;
            this.heading1 = (org.heading1 == null ? null : org.heading1.clone());
            this.heading2 = (org.heading2 == null ? null : org.heading2.clone());
            this.northEastPoint = (org.northEastPoint == null ? null : org.northEastPoint.clone());
            this.southWestPoint = (org.southWestPoint == null ? null : org.southWestPoint.clone());
            
            this.points = new ArrayList<GCPoint>();
            
            for (GCPoint gcp : org.points) {
                this.points.add((gcp == null ? null : gcp.clone()));
            }
            
            this.airportPairs = new ArrayList<String[]>();
            
            for (String[] stra : org.airportPairs) {
                String[] nstra = new String[stra.length];
                System.arraycopy(stra, 0, nstra, 0, stra.length);
                this.airportPairs.add(nstra);
            }
            
            this.timeIntervals = new ArrayList<Timestamp[]>();
            
            for (Timestamp[] tsa : org.timeIntervals) {
                Timestamp[] ntsa = new Timestamp[tsa.length];
                for (int i = 0; i < tsa.length; i++) {
                    ntsa[i] = (tsa[i] == null ? null : tsa[i].clone());
                }
                this.timeIntervals.add(ntsa);
            }
        }
        
	@Override
	public String name()
	{
		return this.name;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public String label()
	{
		return this.label;
	}
	
	public void setLabel(String label)
	{
		this.label = label;
	}
	
	public Altitude floor()
	{
		return this.floor;
	}
	
	public void setFloor(Altitude altitude)
	{
		this.floor = altitude;
	}
	
	public Altitude ceiling()
	{
		return this.ceiling;
	}
	
	public void setCeiling(Altitude altitude)
	{
		this.ceiling = altitude;
	}
	
	public Double trafficInterval()
	{
		return this.trafficInterval;
	}
	
	public void setTrafficInterval(Double minutes)
	{
		this.trafficInterval = minutes;
	}
	
	public AffectedTraffic affectedTrafficFlag()
	{
		return this.affectedTrafficFlag;
	}
	
	public void setAffectedTrafficFlag(AffectedTraffic affectedTrafficFlag)
	{
		this.affectedTrafficFlag = affectedTrafficFlag;
	}
	
	public boolean isTMI() {
		return isTMI;
	}

	public void setTMI(boolean isTMI) {
		this.isTMI = isTMI;
	}
	
	public Angle heading1()
	{
		return this.heading1;
	}
	
	public void setHeading1(Angle angle)
	{
		this.heading1 = angle;
	}

	public Angle heading2()
	{
		return this.heading2;
	}
	
	public void setHeading2(Angle angle)
	{
		this.heading2 = angle;
	}

	private List<GCPoint> points()
	{
		return this.points;
	}
	
	public void setPoints(List<GCPoint> points)
	{
		this.points = points;
		
		this.setBoundingBox();
	}
	
	/**
	 * Returns the list of {@link GCPoint}s for this {@link Restriction} as a list of {@link GCEdge}.
	 * <p>
	 * <b>NOTE: </b> This method creates a new set of {@link GCEdge}s each time it is called.
	 */
	public List<GCEdge> edges()
	{
		if (this.points == null || this.points.size() <= 1)
		{
			return null;
		}
		
		List<GCEdge> edges = new ArrayList<GCEdge>(this.points.size() - 1);
		for (int i=0; i<this.points.size()-1; i++)
		{
			edges.add( new GCEdge(this.points.get(i), this.points.get(i+1)) );
		}
		return edges;
	}
	
	/**
	 * Sets the {@link #points} by converting the {@link List} of {@link GCEdge}s into a {@link List} of {@link GCPoint}s, and storing.
	 */
	public void setEdges(List<GCEdge> edges)
	{
		List<GCPoint> points = new ArrayList<GCPoint>(edges.size() + 1);
		points.add(edges.get(0).first());
		for (GCEdge edge : edges)
		{
			points.add(edge.second());
		}
		
		this.setPoints(points);
	}
	
	
	private List<String[]> airportPairs()
	{
		return this.airportPairs;
	}
	
	public void setAirportPairs(List<String[]> airportPairs)
	{
		this.airportPairs = airportPairs;
	}

	public void addAirportPair(String[] airportPair)
	{
		if (this.airportPairs == null)
		{
			this.airportPairs = new ArrayList<String[]>();
		}
		
		this.airportPairs.add(airportPair);
	}

	public List<Timestamp[]> timeIntervals()
	{
		return this.timeIntervals;
	}
	
	public void setTimeIntervals(List<Timestamp[]> timeIntervals)
	{
		this.timeIntervals = timeIntervals;
	}
	
	public void addTimeInterval(Timestamp[] timeInterval)
	{
		if (this.timeIntervals == null)
		{
			this.timeIntervals = new ArrayList<Timestamp[]>();
		}
		
		this.timeIntervals.add(timeInterval);
	}
	
	/**
	 * Calls {@link #setBoundingBox(double)} with a padding of zero.
	 * @see {@link #setBoundingBox(double)}
	 */
	private void setBoundingBox()
	{
		setBoundingBox(0);
	}
	
	/**
	 * Assesses the maximal latitude and longitude extents of the coordinates in this restriction,
	 * and sets the minimum and maximum points for the {@link IGriddableResource} interface. This is called by
	 * {@link #setEdges(List)} when the restriction coordinates are originally assigned, and should be called manually
	 * if the collection changes, or if different padding is desired in the bounding box.
	 * NOTE: Latitude and longitude values are not validated.
	 * @param padding Decimal degrees to extend the bounding box beyond the points in the restriction.
	 */
	public void setBoundingBox(double padding)
	{
		if (this.points != null && this.points.size() >= 1)
		{
			// Extract latitude and longitude from the points in the first segment
			// Local variables are used to reduce unnecessary use of property accessors in repeated min-max comparisons.
			GCPoint point = this.points.get(0);
			double minLatDegrees = point.latitude().normalized().degrees();
			double maxLatDegrees = point.latitude().normalized().degrees();
			double minLonDegrees = point.longitude().normalized().degrees();
			double maxLonDegrees = point.longitude().normalized().degrees();
			
			for (int i = 1; i < this.points.size(); i++)
			{
				point = this.points.get(i);
				minLatDegrees = Math.min(minLatDegrees, point.latitude().normalized().degrees());
				minLonDegrees = Math.min(minLonDegrees, point.longitude().normalized().degrees());
				maxLatDegrees = Math.max(maxLatDegrees, point.latitude().normalized().degrees());
				maxLonDegrees = Math.max(maxLonDegrees, point.longitude().normalized().degrees());
			}
			
			this.southWestPoint = new GCPoint(Latitude.valueOfDegrees(minLatDegrees - padding), Longitude.valueOfDegrees(minLonDegrees - padding));
			this.northEastPoint = new GCPoint(Latitude.valueOfDegrees(maxLatDegrees + padding), Longitude.valueOfDegrees(maxLonDegrees + padding));
		}
		else
		{
			this.southWestPoint = null;
			this.northEastPoint = null;
		}
	}

	@Override
	public GCPoint northeastPoint()
	{
		return this.northEastPoint;
	}

	@Override
	public GCPoint southwestPoint()
	{
		return this.southWestPoint;
	}

	@Override
	/**
	 * Required member for {@link IResourceInfo}.
	 * @return Restrictions are 1-dimensional so this is always zero.
	 */
	public long crossingTime()
	{
		return 0;
	}

	@Override
	/**
	 * Required member for {@link IResourceInfo}.
	 * @return Restrictions are denoted in the {@link ResourceType} enumeration as RN.
	 */
	public ResourceType resourceType()
	{
		return ResourceType.RN;
	}
	
	@Override
	public String toString()
	{
		StringBuilder stringBuilder = new StringBuilder();
		
		stringBuilder.append("Restriction " + this.name + " (" + this.label + "):\n");
		stringBuilder.append("\tFloor/Ceiling = " + (this.floor == null ? "null" : this.floor.feet()) + "/" + (this.ceiling == null ? "null" : this.ceiling.feet() + " ft\n"));
		stringBuilder.append("\tInterval = " + (this.trafficInterval == null ? "null" : this.trafficInterval) + " min\n");
		stringBuilder.append("\tAffectedTrafficFlag = " + (this.affectedTrafficFlag == null ? "null" : this.affectedTrafficFlag.toString()) +
			", Heading1 = " + (this.heading1 == null ? "null" : this.heading1.degrees()) + " deg" +
			", Heading2 = " + (this.heading2 == null ? "null" : this.heading2.degrees()) + " deg\n");
		
		stringBuilder.append("\tPoints:\n");
		if (this.points != null)
		{
			for (int i=0; i<this.points.size(); i++)
			{
				stringBuilder.append(String.format("\t\t%1$2d: %2$s\n", i, this.points.get(i).toString(Format.DECIMAL_NSEW)));
			}
		}
		
		stringBuilder.append("\tAirportPairs:\n");
		if (this.airportPairs != null)
		{
			for (int i=0; i<this.airportPairs.size(); i++)
			{
				stringBuilder.append(String.format("\t\t%1$2d: %2$s %3$s\n", i, this.airportPairs.get(i)[0], this.airportPairs.get(i)[1]));
			}
		}
		
		stringBuilder.append("\tTimeIntervals:\n");
		if (this.timeIntervals != null)
		{
			for (int i=0; i<this.timeIntervals.size(); i++)
			{
				stringBuilder.append(String.format("\t\t%1$2d: %2$s %3$s\n", i, this.timeIntervals.get(i)[0].toBonnString(), this.timeIntervals.get(i)[1].toBonnString()));
			}
		}
		
		stringBuilder.append("\tTMI: "+isTMI+"\n");
		
		return stringBuilder.toString();
	}
	
	//------------------------------------------------------------------------------------------------------
	// TextSerializable implementaiton
	//------------------------------------------------------------------------------------------------------
	
	//---------------------
	// Static class members
	//---------------------
    //private static final String AFFECTED_TRAFFIC_FLAG_PATTERN = generateAffectedTrafficFlagPattern();
		
	/*private static final String RESTRICTION_HEADER_PATTERN =
		"^(.{6})" +                                     // Restriction ID (6-chars)
		"(.{21})" +                                     // Restriction label (21-chars)
		"(" + AFFECTED_TRAFFIC_FLAG_PATTERN + ")\\s+" + // Affected traffic flag (1-char)
		"(" + Patterns.FLOAT + ")\\s+" +                // Traffic interval
		"(" + Patterns.INTEGER + ")\\s+" +              // Heading 1
		"(" + Patterns.INTEGER + ")\\s+" +              // Heading 2
		"(" + Patterns.INTEGER + ")\\s+" +              // Floor (100s of feet)
		"(" + Patterns.INTEGER + ")\\s+" +              // Ceiling (100s of feet)
		"(" + Patterns.INTEGER + ")\\s+" +              // Number of points
		"(" + Patterns.INTEGER + ")\\s+" +              // Number of exception pairs
		"(" + Patterns.INTEGER + ")\\s*$";              // Number of time-of-day intervals*/

	/*private static final String POINT_RECORD_PATTERN =
		"^\\s*(" + Patterns.INTEGER + ")\\s+" + // Latitude ("DDMMSS")
		"(" + Patterns.INTEGER + ")\\s*$";      // Longitude ("DDDMMSS")*/

	/*private static final String AIRPORT_PAIR_RECORD_PATTERN =
		"^\\s*(\\S{3}|\\*{5})\\s+" + // Origin airport
		"(\\S{3}|\\*{5})\\s*$";      // Destination airport*/

	/*private static final String TIME_INTERVAL_RECORD_PATTERN =
		"^\\s*(" + Patterns.TIME_24HR  + ")" + // Time interval start ("HH:MM:SS")
		"(?:[ -])" +                                                  // separator (either space or dash)
		"(" + Patterns.TIME_24HR + ")\\s*$";  // Time interval end("HH:MM:SS")*/
	/*private static final String TIME_INTERVAL_RECORD_PATTERN =
            "^\\s*((?:[0-9][0-9]):(?:[0-5][0-9]):(?:[0-5][0-9]))" + // Time interval start ("HH:MM:SS")
            "(?:[ -])" +                                                  // separator (either space or dash)
            "((?:[0-9][0-9]):(?:[0-5][0-9]):(?:[0-5][0-9]))\\s*$";  // Time interval end("HH:MM:SS")*/

	/*private static final Pattern restrictionHeaderPattern = Pattern.compile(RESTRICTION_HEADER_PATTERN);
	private static final Pattern pointRecordPattern = Pattern.compile(POINT_RECORD_PATTERN);
	private static final Pattern airportPairRecordPattern = Pattern.compile(AIRPORT_PAIR_RECORD_PATTERN);
	private static final Pattern timeIntervalRecordPattern = Pattern.compile(TIME_INTERVAL_RECORD_PATTERN);

	private static final String RESTRICTION_HEADER_FORMAT = "%1$-5s, %2$-20s, %3$1s, %4$6.3f, %5$4d, %6$5d, %7$3d, %8$3d, %9$2d, %10$2d, %11$2d\n";
	private static final String POINT_RECORD_FORMAT         = " %1$06d %2$07d\n";
	private static final String AIRPORT_PAIR_RECORD_FORMAT  = " %1$s %2$s\n";
	private static final String TIME_INTERVAL_RECORD_FORMAT = " %1$04d %2$04d\n";*/
	
	/*private static String generateAffectedTrafficFlagPattern()
	{
		String affectedTrafficFlagPattern = "";
		AffectedTraffic[] affectedTrafficValues = AffectedTraffic.values();
		for (int i=0; i<affectedTrafficValues.length; i++)
		{
			if (i != 0)
			{
				affectedTrafficFlagPattern += "|";
			}
			
			affectedTrafficFlagPattern += affectedTrafficValues[i];
		}
		
		return affectedTrafficFlagPattern;
	}*/

    public static final boolean STANDARD_CONVENTION = true;
	public static final boolean NASPAC_CONVENTION = false;
	
	/**
	 * Converting latitude values from DDMMSS or DDMM to dd.dddd
	 * (e.g., when reading data from files).
	 * @param lat input latitude in DDMM or DDMMSS format
	 * @return the value of the input latitude in dd.dddd format
	 * @throws BadDataException
	 */
	private static double convertLatitude (String lat)
	{
		// do the latitude conversion from DDMMSS to dd.dddd
		if (lat.length()<=4) lat = lat + "00"; // add 00 for seconds if needed
		int latValue = Integer.parseInt(lat);
		int DDlat = latValue / 10000;
		latValue = latValue % 10000;
		int MMlat = latValue / 100;
		latValue = latValue % 100;
		int SSlat = latValue;
		double dlat = DDlat + (MMlat / 60.0) + (SSlat / 3600.0);
		if (dlat < -90.0 || dlat > 90.0) throw new IllegalArgumentException();
		return dlat;
	}

	/**
	 * Converting longitude values from DDDMMSS or DDDMM to ddd.dddd
	 * (e.g., when reading data from files).
	 * @param lon input longitude in DDDMM or DDDMMSS format
	 * @return the value of the input longitude in dd.dddd format
	 * @throws BadDataException
	 */
	private static double convertLongitude(String lon, boolean standard_convention)
	{
		// the boolean argument indicates the convention of the input longitude
		// we always convert to standard convention - east longitude positive!
		// do the longitude conversion from DDDMMSS to ddd.dddd
		if (lon.length()<=5) lon = lon + "00"; // add 00 for seconds if needed
		int lonValue = Integer.parseInt(lon);
		int DDDlon = lonValue / 10000;
		lonValue = lonValue % 10000;
		int MMlon = lonValue / 100;
		lonValue = lonValue % 100;
		int SSlon = lonValue;
		double dlon = DDDlon + (MMlon / 60.0) + (SSlon / 3600.0);
		if (!standard_convention)
		{
			dlon = - dlon;
		}
		if (dlon < -180.0 || dlon > 180.0) throw new IllegalArgumentException();
		return dlon;
	}
	
	@Override
	public void readItem(BufferedReader reader) throws IOException
	{
		try
		{
			String line = reader.readLine();
			String[] fields = line.trim().split(",");
			
			this.setName(fields[0].trim());
			this.setLabel(fields[1].trim());
			this.setAffectedTrafficFlag(AffectedTraffic.valueOf(fields[2].trim()));
			this.setTrafficInterval( Double.valueOf(fields[3].trim()) );
			this.setHeading1( Angle.valueOfDegrees(Double.valueOf(fields[4].trim())) );
			this.setHeading2( Angle.valueOfDegrees(Double.valueOf(fields[5].trim())) );
			this.setFloor( Altitude.valueOfFeet(Integer.valueOf(fields[6].trim()) * 100.0) );
			this.setCeiling( Altitude.valueOfFeet(Integer.valueOf(fields[7].trim()) * 100.0) );
			
			// Process points
			List<GCPoint> points = new ArrayList<GCPoint>();
			String latlongsList[] = fields[8].trim().split(";");
			for(String latlon : latlongsList)
			{
				String[] latlons = latlon.trim().split(" ");
				double parse;
				try {
					parse = Double.parseDouble(latlons[0].trim());
				} catch (NumberFormatException ex) {
					parse = convertLatitude(latlons[0].trim());
				}
				Latitude lat = Latitude.valueOfDegrees( parse );
				try {
					parse = Double.parseDouble(latlons[1].trim());
				} catch (NumberFormatException ex) {
					parse = convertLongitude(latlons[1].trim(), false);
				}
				Longitude lon = Longitude.valueOfDegrees( parse );
				points.add(new GCPoint(lat, lon));
			}
			this.setPoints(points);
			
			// Process airport pairs
			List<String[]> airportPairs = new ArrayList<String[]>();
			String arpPairsList[] = fields[9].split(";");
			for(String arpPair : arpPairsList)
			{
				airportPairs.add(arpPair.trim().split(" "));
	
			}
			this.setAirportPairs(airportPairs);
	
			// Process time intervals
			String[] timeIntPairList = fields[10].trim().split(";");
			List<Timestamp[]> timeIntervals = new ArrayList<Timestamp[]>(timeIntPairList.length);
			for(String timeIntPair : timeIntPairList)
			{
				try
				{
					String[] pairs = timeIntPair.split(" ");
				
					String[] tokens = pairs[0].split(":");
					
					Timestamp t1 = new Timestamp(0).hourAdd(Double.valueOf(tokens[0])).minuteAdd(Double.valueOf(tokens[1])).secondAdd(Double.valueOf(tokens[2]));;
					
					tokens = pairs[1].split(":");
					Timestamp t2 = new Timestamp(0).hourAdd(Double.valueOf(tokens[0])).minuteAdd(Double.valueOf(tokens[1])).secondAdd(Double.valueOf(tokens[2]));
					
					timeIntervals.add(new Timestamp[] { t1, t2 });
				}
				catch (Exception e)
				{
					throw new IOException("Expected time interval pair while processing Restriction \"" + this.name() + "\".", e);
				}
			}
			this.setTimeIntervals(timeIntervals);
			this.setTMI(new Boolean(fields[11]));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void writeItem(PrintWriter writer) throws IOException
	{
            StringBuilder sb = new StringBuilder();
            sb.append(this.name).append(", ");
            sb.append(this.label).append(", ");
            sb.append(this.affectedTrafficFlag.name()).append(", ");
            sb.append(this.trafficInterval).append(", ");
            sb.append(Math.round(this.heading1.degrees())).append(", ");
            sb.append(Math.round(this.heading2.degrees())).append(", ");
            sb.append(Math.round(this.floor.feet()/100)).append(", ");
            sb.append(Math.round(this.ceiling.feet()/100)).append(", ");
            
            for (GCPoint pt : this.points) {
//                Latitude lat = Latitude.valueOfDegrees( convertLatitude(pt.latitude().toString(Angle.Format.COMPACT)) );
//                Longitude lon = Longitude.valueOfDegrees( convertLongitude(pt.longitude().toString(Angle.Format.COMPACT), false) );
//
//                sb.append(lat.toString(Angle.Format.COMPACT)).append(" ");
//                sb.append(lon.toString(Angle.Format.COMPACT)).append(";");
            	sb.append(pt.latitude().toString(Angle.Format.DECIMAL)).append(" ");
                sb.append(pt.longitude().toString(Angle.Format.DECIMAL)).append(";");
            }
            
            sb.append(", ");
            
            for (String[] aptPair : this.airportPairs) {
                sb.append(aptPair[0]).append(" ").append(aptPair[1]).append(";");
            }
            
            sb.append(", ");
            
            
            
            for (Timestamp[] timeInt : this.timeIntervals) {
            	
                sb.append(buildTimeOffsetString(timeInt[0], new Timestamp(0)));
                sb.append(" ");
                sb.append(buildTimeOffsetString(timeInt[1], new Timestamp(0))).append(";");
            }
            
            sb.append(", ");
            sb.append(this.isTMI);
            
            String out = sb.toString().replaceAll(";,", ",");
            writer.write(out);
	}
	
	private String buildTimeOffsetString(Timestamp target, Timestamp base) {
    	long millisDiff = target.getTime() - base.getTime();
    	long hours = millisDiff / Timestamp.MILLISECS_HOUR;
    	millisDiff = millisDiff - (hours * Timestamp.MILLISECS_HOUR);
    	long mins = millisDiff / Timestamp.MILLISECS_MIN;
    	millisDiff = millisDiff - (mins * Timestamp.MILLISECS_MIN);
    	long secs = millisDiff / Timestamp.MILLISECS_SEC;
    	
    	StringBuilder sb = new StringBuilder();
    	sb.append((hours < 10 ? "0" : "") + hours);
    	sb.append((mins < 10 ? ":0" : ":") + mins);
    	sb.append((secs < 10 ? ":0" : ":") + secs);
    	
    	return sb.toString();
	}
	
	@Override
	public long readHeader(BufferedReader reader) throws IOException
	{
		HeaderUtils.readHeaderHashComment(reader);
		return -1;
	}

	@Override
	public void writeHeader(PrintWriter writer, long numRecords) throws IOException
	{
		
	}

    @Override
    public Restriction clone() {
        return new Restriction(this);
    }
}