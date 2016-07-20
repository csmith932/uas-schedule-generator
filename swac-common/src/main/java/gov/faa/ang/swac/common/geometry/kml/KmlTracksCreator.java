package gov.faa.ang.swac.common.geometry.kml;

import gov.faa.ang.swac.common.datatypes.Altitude;
import gov.faa.ang.swac.common.datatypes.Latitude;
import gov.faa.ang.swac.common.datatypes.Longitude;
import gov.faa.ang.swac.common.datatypes.Timestamp;
import gov.faa.ang.swac.common.datatypes.Angle.Units;
import gov.faa.ang.swac.common.entities.Airport;
import gov.faa.ang.swac.common.entities.AirportFix;
import gov.faa.ang.swac.common.entities.Fix;
import gov.faa.ang.swac.common.flightmodeling.FlightLeg;
import gov.faa.ang.swac.common.flightmodeling.Itinerary;
import gov.faa.ang.swac.common.flightmodeling.FlightLeg.FlightDirection;
import gov.faa.ang.swac.common.flightmodeling.FlightLeg.FlightRuleType;
import gov.faa.ang.swac.common.flightmodeling.TrajectoryPoint;
import gov.faa.ang.swac.common.flightmodeling.fileio.BadaRecord;
import gov.faa.ang.swac.common.geometry.GCPoint;
import gov.faa.ang.swac.common.geometry.GCPointAlt;
import gov.faa.ang.swac.common.geometry.GCUtilities;
import gov.faa.ang.swac.common.geometry.SimplePolygon;
import gov.faa.ang.swac.common.geometry.SphericalUtilities;
import gov.faa.ang.swac.common.geometry.SphericalUtilities.IntersectionType;
import gov.faa.ang.swac.common.geometry.SphericalUtilities.InvalidInputException;
import gov.faa.ang.swac.datalayer.AdHocDataAccess;
import gov.faa.ang.swac.datalayer.ResourceManager;

import java.io.Writer;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;

import de.micromata.opengis.kml.v_2_2_0.AltitudeMode;
import de.micromata.opengis.kml.v_2_2_0.Document;
import de.micromata.opengis.kml.v_2_2_0.Folder;
import de.micromata.opengis.kml.v_2_2_0.Kml;
import de.micromata.opengis.kml.v_2_2_0.KmlFactory;
import de.micromata.opengis.kml.v_2_2_0.LinearRing;
import de.micromata.opengis.kml.v_2_2_0.Placemark;
import de.micromata.opengis.kml.v_2_2_0.Style;
import de.micromata.opengis.kml.v_2_2_0.TimeSpan;
import de.micromata.opengis.kml.v_2_2_0.gx.Track;

/**
 * 
 * Creates KML Tracks from SWAC FlightLegs for playback in GoogleEarth (or other KML reader).  Most FlightLegs have their flight routes converted 
 * without any modification to the original route.  There are, however, two exceptions:
 * <BR><BR>
 * 1. As of build 7.1.2.2041 GoogleEarth has a bug where flights fly backwards in the wrong direction when the flight crosses the anti-meridian.  To remedy 
 * this flights crossing the anti-meridian have their tracks split into two separate tracks starting and ending on either side of the anti-meridian.  
 * <BR><BR>
 * 2. Flights with long distances between their waypoints, currently defined as 1,000 nmi, can appear to fly sideways during playback.  To correct this, additional 
 * coordinates are inserted to ensure there is at least 1 waypoint every 1,000 nmi so a reasonable heading can be calculated for the aircraft.
 *  
 * @author hkaing
 *
 */
public class KmlTracksCreator {
	
	private static org.apache.log4j.Logger logger = org.apache.log4j.LogManager.getLogger(KmlTracksCreator.class);
	
	private static DecimalFormat df2 = new DecimalFormat("#.##");
	private static DecimalFormat df6 = new DecimalFormat("#.######");
	private static String WORLD = "WORLD"; // used to identify user wants to generate KML for all airports.
	private static double LARGE_DISTANCE_NMI = 1000; // used to determine when to insert additional waypoints in flight route.
	
	// used to actually create the KML file.
	private final Kml kml;
	private final Document document;
	
	// used to determine if FlightLeg will be added to document.
	private Timestamp startTime;
	private Timestamp endTime;
	private String airportOfInterest;
	private Placemark currentLegPlacemark;
	private Track currentLegTrack;
	
	private Map<String,List<AirportTurnRecord>> airportTurns;
	private class AirportTurnRecord{
		public Timestamp start;
		public Timestamp end;
		public List<? extends TrajectoryPoint> runways;
	}
	
	public KmlTracksCreator(String documentName, Timestamp start, Timestamp end, String airport) {
		this.kml		= KmlFactory.createKml();
		this.document	= this.kml.createAndSetDocument().withName(documentName).withOpen(false);
		
		for (DocumentFolders folder : DocumentFolders.values()){
			this.document.createAndAddFolder().withName(folder.name()).withOpen(false);
		}
		
		Folder airportFolder = (Folder)this.document.getFeature().get(DocumentFolders.Airports.ordinal());
		for (AirportFolders folder : AirportFolders.values()){
			airportFolder.createAndAddFolder().withName(folder.name()).withOpen(false);
		}
		
		this.startTime	= start;
		this.endTime	= end;
		this.airportOfInterest = airport;
		this.airportTurns = new TreeMap<String,List<AirportTurnRecord>>();
		
		createStyleIDs();
	}
	
	private KmlTracksCreator(Document doc, Timestamp start, Timestamp end, String airportOfInterest, Map<String,List<AirportTurnRecord>> turns){
		this.kml = KmlFactory.createKml();
		this.kml.setFeature(doc);
		this.document = doc;
		
		this.startTime = start;
		this.endTime = end;
		this.airportOfInterest = airportOfInterest;
		this.airportTurns = turns;  // must remember to clear this after printing TrajMod and Sim results so we don't keep appending turns.
	}
	
	/**
	 * Creates copy of original.
	 * 
	 * @param ktc
	 * @return
	 */
	public static KmlTracksCreator newInstance(KmlTracksCreator ktc){
		return new KmlTracksCreator(ktc.document.clone(), ktc.startTime, ktc.endTime, ktc.airportOfInterest, ktc.airportTurns);
	}
	
	private enum DocumentFolders{
		Airports,
		AirportTurns,
		Fixes,
		Sectors,
		PolyWx,
		GridWx,
		SUAs,
		FlightLegs;
	}
	
	private enum AirportFolders{
		Major,
		Minor;
	}
	
	private enum FixesFolders{
		Arrival,
		Departure;
	}
	
	public void changeDocName(String val){
		this.document.setName(val);
	}
	
	public Timestamp startTime(){
		return this.startTime;
	}
	
	public Timestamp endTime(){
		return this.endTime;
	}
	
	public String airportOfInterest(){
		return this.airportOfInterest;
	}
	
	public enum AirportStyle{
		MinorAirport,
		MajorAirport;
	}
	
	public enum FixStyle{
		ArrivalFix,
		DepartureFix;
	}
	
	public enum PolyType{
		Sector,
		Weather,
		Sua;
	}

	private enum SectorStyle{ // for sector definitions
		SECTOR1;
	}
	
	private enum WxStyle{ // for Wx polygon constraints
		WX1, // VIL1
		WX2,
		WX3,
		WX4,
		WX5,
		WX6; // VIL6
	}
	
	private enum SuaStyle{ // for SUA polygon constraints
		SUA1, // VIL1
		SUA2,
		SUA3,
		SUA4,
		SUA5,
		SUA6; // VIL6
	}
	
	private enum WakeStyle{ // for FlightLegs
		SUPER,
		HEAVY,
		LARGE,
		SMALL;
	}
	
	private enum RunwayStyle{
		ACTIVE,
		TURNING;
	}
	
	private enum MrmsWxStyle{
		MRMS1, // VIL1
		MRMS2,
		MRMS3,
		MRMS4,
		MRMS5,
		MRMS6; // VIL6
	}
	
	/**
	 * SUPER = BADA Type A380
	 * HEAVY >= 140,000 kg
	 * LARGE >=  19,000 kg
	 * SMALL <   19,000 kg or null BADA Type
	 * 
	 * Using user specified wake categories but special case handling
	 * for A380 aircraft (Super wake class).
	 * 
	 * @param badaRecord
	 * @return
	 */
	private WakeStyle getWakeCategory(BadaRecord badaRecord){
		
		if (badaRecord != null){
			Integer maxTOW = badaRecord.getMassLevel()[2]; //kg
			if (badaRecord.getAircraftType().equals("A380")){
				return WakeStyle.SUPER;
			}else if (maxTOW >= 140000){
				return WakeStyle.HEAVY;
			}else if (maxTOW > 19000){
				return WakeStyle.LARGE;
			}
		}
		
		return WakeStyle.SMALL;
	}
	
	private void createStyleIDs(){
		
		// hex color codes order is: ALPHA (transparency), BLUE, GREEN, RED
		
		// ---- Airport Styles -----------------------------
		Style minorStyle = this.document.createAndAddStyle().withId(AirportStyle.MinorAirport.name());
		minorStyle.createAndSetIconStyle().withColor("FF00CCFF").withScale(0.6)
		.createAndSetIcon().setHref("http://maps.google.com/mapfiles/kml/shapes/placemark_circle.png");
		minorStyle.createAndSetLabelStyle().withColor("FF00CCFF").setScale(0.6);
		
		Style majorStyle = this.document.createAndAddStyle().withId(AirportStyle.MajorAirport.name());
		majorStyle.createAndSetIconStyle().withColor("FF00FFFF").withScale(0.8)
		.createAndSetIcon().setHref("http://maps.google.com/mapfiles/kml/shapes/placemark_circle.png");
		majorStyle.createAndSetLabelStyle().withColor("FF00FFFF").setScale(0.8);
		
		// ---- Fix Styles -----------------------------
		Style arrivalStyle = this.document.createAndAddStyle().withId(FixStyle.ArrivalFix.name());
		arrivalStyle.createAndSetIconStyle().withColor("FF003399").withScale(0.4)
		.createAndSetIcon().setHref("http://maps.google.com/mapfiles/kml/shapes/road_shield3.png");
		arrivalStyle.createAndSetLabelStyle().withColor("FF003399").setScale(0.6);
		
		Style departureStyle = this.document.createAndAddStyle().withId(FixStyle.DepartureFix.name());
		departureStyle.createAndSetIconStyle().withColor("FF006699").withScale(0.4)
		.createAndSetIcon().setHref("http://maps.google.com/mapfiles/kml/shapes/road_shield3.png");
		departureStyle.createAndSetLabelStyle().withColor("FF006699").setScale(0.6);

		// ---- Sector Polygon Constraint Styles -----------
		Style sectorStyle = this.document.createAndAddStyle().withId(SectorStyle.SECTOR1.name());
		sectorStyle.createAndSetLineStyle().withColor("FF0000FF").withWidth(1.0);
		sectorStyle.createAndSetPolyStyle().withColor("00000000").withFill(false);
		
		// ---- Wx Polygon Constraint Styles -----------
		Style wxStyle1 = this.document.createAndAddStyle().withId(WxStyle.WX1.name());
		wxStyle1.createAndSetLineStyle().withColor("FFFFFFFF").withWidth(1.0);
		wxStyle1.createAndSetPolyStyle().withColor("7080FF7D").withFill(true);
		
		Style wxStyle2 = this.document.createAndAddStyle().withId(WxStyle.WX2.name());
		wxStyle2.createAndSetLineStyle().withColor("FFFFFFFF").withWidth(1.0);
		wxStyle2.createAndSetPolyStyle().withColor("7000FF00").withFill(true);
		
		Style wxStyle3 = this.document.createAndAddStyle().withId(WxStyle.WX3.name());
		wxStyle3.createAndSetLineStyle().withColor("FFFFFFFF").withWidth(1.0);
		wxStyle3.createAndSetPolyStyle().withColor("7000FFFF").withFill(true);
		
		Style wxStyle4 = this.document.createAndAddStyle().withId(WxStyle.WX4.name());
		wxStyle4.createAndSetLineStyle().withColor("FFFFFFFF").withWidth(1.0);
		wxStyle4.createAndSetPolyStyle().withColor("70009FFF").withFill(true);
		
		Style wxStyle5 = this.document.createAndAddStyle().withId(WxStyle.WX5.name());
		wxStyle5.createAndSetLineStyle().withColor("FFFFFFFF").withWidth(1.0);
		wxStyle5.createAndSetPolyStyle().withColor("701665BE").withFill(true);
		
		Style wxStyle6 = this.document.createAndAddStyle().withId(WxStyle.WX6.name());
		wxStyle6.createAndSetLineStyle().withColor("FFFFFFFF").withWidth(1.0);
		wxStyle6.createAndSetPolyStyle().withColor("700000FF").withFill(true);
		
		// ---- Sua Polygon Constraint Styles -----------
		Style suaStyle1 = this.document.createAndAddStyle().withId(SuaStyle.SUA1.name());
		suaStyle1.createAndSetLineStyle().withColor("FFFFFFFF").withWidth(1.0);
		suaStyle1.createAndSetPolyStyle().withColor("70CCFFCC").withFill(true);
		
		Style suaStyle2 = this.document.createAndAddStyle().withId(SuaStyle.SUA2.name());
		suaStyle2.createAndSetLineStyle().withColor("FFFFFFFF").withWidth(1.0);
		suaStyle2.createAndSetPolyStyle().withColor("70FFFFCC").withFill(true);
		
		Style suaStyle3 = this.document.createAndAddStyle().withId(SuaStyle.SUA3.name());
		suaStyle3.createAndSetLineStyle().withColor("FFFFFFFF").withWidth(1.0);
		suaStyle3.createAndSetPolyStyle().withColor("70FFCC66").withFill(true);
		
		Style suaStyle4 = this.document.createAndAddStyle().withId(SuaStyle.SUA4.name());
		suaStyle4.createAndSetLineStyle().withColor("FFFFFFFF").withWidth(1.0);
		suaStyle4.createAndSetPolyStyle().withColor("70FF9933").withFill(true);
		
		Style suaStyle5 = this.document.createAndAddStyle().withId(SuaStyle.SUA5.name());
		suaStyle5.createAndSetLineStyle().withColor("FFFFFFFF").withWidth(1.0);
		suaStyle5.createAndSetPolyStyle().withColor("70FF6600").withFill(true);
		
		Style suaStyle6 = this.document.createAndAddStyle().withId(SuaStyle.SUA6.name());
		suaStyle6.createAndSetLineStyle().withColor("FFFFFFFF").withWidth(1.0);
		suaStyle6.createAndSetPolyStyle().withColor("70FF0000").withFill(true);

		
		// -------- FlightLeg Track Styles --------------
		Style smallStyle = this.document.createAndAddStyle().withId(WakeStyle.SMALL.name());
		smallStyle.createAndSetIconStyle().withColor("FFD7DF01").withScale(0.5)
		.createAndSetIcon().setHref("http://maps.google.com/mapfiles/kml/shapes/airports.png");
		smallStyle.createAndSetLabelStyle().setScale(0.0); // don't display label
		smallStyle.createAndSetLineStyle().setWidth(0.0); // don't display line
		
		Style largeStyle = this.document.createAndAddStyle().withId(WakeStyle.LARGE.name());
		largeStyle.createAndSetIconStyle().withColor("FF01FFD7").withScale(0.7)
		.createAndSetIcon().setHref("http://maps.google.com/mapfiles/kml/shapes/airports.png");
		largeStyle.createAndSetLabelStyle().setScale(0.0); // don't display label
		
		Style heavyStyle = this.document.createAndAddStyle().withId(WakeStyle.HEAVY.name());
		heavyStyle.createAndSetIconStyle().withColor("FF0174DF").withScale(0.9)
		.createAndSetIcon().setHref("http://maps.google.com/mapfiles/kml/shapes/airports.png");
		heavyStyle.createAndSetLabelStyle().setScale(0.0); // don't display label
		
		Style superStyle = this.document.createAndAddStyle().withId(WakeStyle.SUPER.name());
		superStyle.createAndSetIconStyle().withColor("FF0101DF").withScale(1.0)
		.createAndSetIcon().setHref("http://maps.google.com/mapfiles/kml/shapes/airports.png");
		superStyle.createAndSetLabelStyle().setScale(0.0); // don't display label
		
		// -------------------- Runway Configuration Styles -----------------------
		Style activeStyle = this.document.createAndAddStyle().withId(RunwayStyle.ACTIVE.name());
		activeStyle.createAndSetIconStyle().withColor("FF14F000").withScale(0.6)
		.createAndSetIcon().setHref("http://maps.google.com/mapfiles/kml/shapes/shaded_dot.png");
		activeStyle.createAndSetLabelStyle().setScale(0.6);
		
		Style turningStyle = this.document.createAndAddStyle().withId(RunwayStyle.TURNING.name());
		turningStyle.createAndSetIconStyle().withColor("FF14F0FF").withScale(0.6)
		.createAndSetIcon().setHref("http://maps.google.com/mapfiles/kml/shapes/shaded_dot.png");
		turningStyle.createAndSetLabelStyle().setScale(0.6);
		
		// ------------------- MRMS Weather Styles --------------------------------
		Style mrmsWxStyle1 = this.document.createAndAddStyle().withId(MrmsWxStyle.MRMS1.name());
		mrmsWxStyle1.createAndSetIconStyle().withColor("7080FF7D").withScale(0.9)
		.createAndSetIcon().setHref("http://maps.google.com/mapfiles/kml/shapes/shaded_dot.png");
		mrmsWxStyle1.createAndSetLabelStyle().setScale(0.0); // don't display intensity label
		
		Style mrmsWxStyle2 = this.document.createAndAddStyle().withId(MrmsWxStyle.MRMS2.name());
		mrmsWxStyle2.createAndSetIconStyle().withColor("7000FF00").withScale(0.9)
		.createAndSetIcon().setHref("http://maps.google.com/mapfiles/kml/shapes/shaded_dot.png");
		mrmsWxStyle2.createAndSetLabelStyle().setScale(0.0); // don't display intensity label
		
		Style mrmsWxStyle3 = this.document.createAndAddStyle().withId(MrmsWxStyle.MRMS3.name());
		mrmsWxStyle3.createAndSetIconStyle().withColor("7000FFFF").withScale(0.9)
		.createAndSetIcon().setHref("http://maps.google.com/mapfiles/kml/shapes/shaded_dot.png");
		mrmsWxStyle3.createAndSetLabelStyle().setScale(0.0); // don't display intensity label
		
		Style mrmsWxStyle4 = this.document.createAndAddStyle().withId(MrmsWxStyle.MRMS4.name());
		mrmsWxStyle4.createAndSetIconStyle().withColor("70009FFF").withScale(0.9)
		.createAndSetIcon().setHref("http://maps.google.com/mapfiles/kml/shapes/shaded_dot.png");
		mrmsWxStyle4.createAndSetLabelStyle().setScale(0.0); // don't display intensity label
		
		Style mrmsWxStyle5 = this.document.createAndAddStyle().withId(MrmsWxStyle.MRMS5.name());
		mrmsWxStyle5.createAndSetIconStyle().withColor("701665BE").withScale(0.9)
		.createAndSetIcon().setHref("http://maps.google.com/mapfiles/kml/shapes/shaded_dot.png");
		mrmsWxStyle5.createAndSetLabelStyle().setScale(0.0); // don't display intensity label
		
		Style mrmsWxStyle6 = this.document.createAndAddStyle().withId(MrmsWxStyle.MRMS6.name());
		mrmsWxStyle6.createAndSetIconStyle().withColor("700000FF").withScale(0.9)
		.createAndSetIcon().setHref("http://maps.google.com/mapfiles/kml/shapes/shaded_dot.png");
		mrmsWxStyle6.createAndSetLabelStyle().setScale(0.0); // don't display intensity label
		
	}

	public void addAirportAndFixes(Airport airport, Map<AirportFix,Fix> arrivalFixes, Map<AirportFix,Fix> departureFixes){
		String coord = df6.format(airport.getLongitude().degrees()) + "," + df6.format(airport.getLatitude().degrees()) + ",0"; // use zero altitude for airports.
		Folder airportFolder = (Folder)this.document.getFeature().get(DocumentFolders.Airports.ordinal());
		if (airport.isMajor()){
			Folder majorFolder = (Folder)airportFolder.getFeature().get(AirportFolders.Major.ordinal());
			Placemark placemark = majorFolder.createAndAddPlacemark().withName(airport.getName()).withVisibility(false);
			placemark.createAndSetPoint().addToCoordinates(coord);
			placemark.setStyleUrl(AirportStyle.MajorAirport.name());
			
		}else{
			Folder minorFolder = (Folder)airportFolder.getFeature().get(AirportFolders.Minor.ordinal());
			Placemark placemark = minorFolder.createAndAddPlacemark().withName(airport.getName()).withVisibility(false);
			placemark.createAndSetPoint().addToCoordinates(coord);
			placemark.setStyleUrl(AirportStyle.MinorAirport.name());
		}
		
		Folder fixesFolder = (Folder)this.document.getFeature().get(DocumentFolders.Fixes.ordinal());
		boolean createArrFixFolder = arrivalFixes != null && !arrivalFixes.values().isEmpty();
		boolean createDepFixFolder = departureFixes != null && !departureFixes.values().isEmpty();
		if (createArrFixFolder || createDepFixFolder){ 
			Folder airportFixFolder = fixesFolder.createAndAddFolder().withName(airport.getName()).withVisibility(false);
			if (createArrFixFolder){
				Folder arrivalFixFolder = airportFixFolder.createAndAddFolder().withName(FixesFolders.Arrival.name()).withVisibility(false);
				for (Fix fix : arrivalFixes.values()){
					addFix(arrivalFixFolder, fix, true);
				}
			}
			
			if (createDepFixFolder){
				Folder departureFixFolder = airportFixFolder.createAndAddFolder().withName(FixesFolders.Departure.name()).withVisibility(false);
				for (Fix fix : departureFixes.values()){
					addFix(departureFixFolder, fix, false);
				}
			}
		}
	}
	
	private void addFix(Folder parent, Fix fix, boolean isArrivalFix){
		Placemark placemark = null;
		if (isArrivalFix){
			placemark = parent.createAndAddPlacemark().withName(fix.getName()).withVisibility(false);
			placemark.setStyleUrl(FixStyle.ArrivalFix.name());
		}else{
			placemark = parent.createAndAddPlacemark().withName(fix.getName()).withVisibility(false);
			placemark.setStyleUrl(FixStyle.DepartureFix.name());
		}
		String coord = df6.format(fix.getLongitude().degrees()) + "," + df6.format(fix.getLatitude().degrees()) + ",0"; // use zero altitude for fixes.
		placemark.createAndSetPoint().addToCoordinates(coord);
	}
	
	/**
	 * 
	 * @param polygon Sector polygon definition.
	 * @param start constraint active start time
	 * @param end constraint active end time
	 * @param description KML Polygon description
	 * @param polyType should always be Sector
	 */
	public void addSectorPolygon(SimplePolygon polygon, Timestamp start, Timestamp end, String name, String description, PolyType polyType){
		Placemark placemark = null;
		if (polyType.equals(PolyType.Sector)){
			Folder sectorFolder = (Folder)this.document.getFeature().get(DocumentFolders.Sectors.ordinal());
			placemark = sectorFolder.createAndAddPlacemark().withName(name).withDescription(description).withVisibility(false);
			placemark.setStyleUrl(SectorStyle.values()[0].name());
			
		}
		
		TimeSpan timeSpan = placemark.createAndSetTimeSpan();
		timeSpan.setBegin(start.toISO8601(false));
		timeSpan.setEnd(end.toISO8601(false));

		LinearRing linearRing = placemark.createAndSetPolygon().createAndSetOuterBoundaryIs().createAndSetLinearRing();
		for (GCPoint point : polygon.points()){
			linearRing.addToCoordinates(point.longitude().degrees(), point.latitude().degrees());
		}
	}
	
	List<Timestamp> wxPolyLoadTimes = new ArrayList<Timestamp>();
	List<Timestamp> suaPolyLoadTimes = new ArrayList<Timestamp>();
	
	public void createPolygonTimeFolder(Timestamp loadTime, PolyType polyType){
		if (polyType.equals(PolyType.Weather)){
			Folder wxFolder = (Folder)this.document.getFeature().get(DocumentFolders.PolyWx.ordinal());
			wxFolder.createAndAddFolder().withName(loadTime.toString()).withVisibility(false);
			this.wxPolyLoadTimes.add(loadTime);
		}else if (polyType.equals(PolyType.Sua)){
			Folder suaFolder = (Folder)this.document.getFeature().get(DocumentFolders.SUAs.ordinal());
			suaFolder.createAndAddFolder().withName(loadTime.toString()).withVisibility(false);
			this.suaPolyLoadTimes.add(loadTime);
		}
	}
	

	/**
	 * 
	 * @param polygon Wx or SUA polygon definition.
	 * @param intensity constraint intensity (1 through 6)
	 * @param loadTime when this polygon becomes known to the world.
	 * @param start constraint active start time
	 * @param end constraint active end time
	 * @param name
	 * @param description KML Polygon description
	 * @param polyType polyType Sector Definition, Weather Constraint or SUA Constraint?
	 */
	public void addPolygonConstraint(SimplePolygon polygon, double intensity, Timestamp loadTime, Timestamp start, Timestamp end, String name, String description, PolyType polyType){
		
		int correctIntensity = (int)Math.max(1, Math.min(6, intensity));
		
		Placemark placemark = null;
		if (polyType.equals(PolyType.Weather)){
			Folder wxFolder = (Folder)this.document.getFeature().get(DocumentFolders.PolyWx.ordinal());
			Folder timeFolder = (Folder)wxFolder.getFeature().get(this.wxPolyLoadTimes.size()-1);
			placemark = timeFolder.createAndAddPlacemark().withName(name).withDescription(description).withVisibility(false);
			placemark.setStyleUrl(WxStyle.values()[correctIntensity-1].name());
			
		}else if (polyType.equals(PolyType.Sua)){
			Folder suaFolder = (Folder)this.document.getFeature().get(DocumentFolders.SUAs.ordinal());
			Folder timeFolder = (Folder)suaFolder.getFeature().get(this.suaPolyLoadTimes.size()-1);
			placemark = timeFolder.createAndAddPlacemark().withName(name).withDescription(description).withVisibility(false);
			placemark.setStyleUrl(SuaStyle.values()[correctIntensity-1].name());
		}
		
		TimeSpan timeSpan = placemark.createAndSetTimeSpan();
		timeSpan.setBegin(start.toISO8601(false));
		timeSpan.setEnd(end.toISO8601(false));

		LinearRing linearRing = placemark.createAndSetPolygon().createAndSetOuterBoundaryIs().createAndSetLinearRing();
		for (GCPoint point : polygon.points()){
			linearRing.addToCoordinates(point.longitude().degrees(), point.latitude().degrees());
		}
	}
	
	public void addItineraries(List<Itinerary> itineraries){
		
		Folder legsFolder = (Folder)this.document.getFeature().get(DocumentFolders.FlightLegs.ordinal());
		
        for (Itinerary itin : itineraries) {
        	for (FlightLeg leg : itin.flightLegs()) {
        		// IFR check isn't powerful enough...some of our VFR conversions pass the test even though they have invalid routes
        		if (FlightRuleType.IFR.equals(leg.flightRuleType()) && leg.flightRoute() != null && leg.flightRoute().size() >= 2) {		
					if (this.startTime.beforeOrEqualTo(leg.departure().gateDateTime()) && this.endTime.afterOrEqualTo(leg.departure().gateDateTime()) || // depart inside time window
						this.startTime.beforeOrEqualTo(leg.arrival().gateDateTime()) && this.endTime.afterOrEqualTo(leg.arrival().gateDateTime()) || // arrive inside time window
						this.startTime.after(leg.departure().gateDateTime()) && this.endTime.before(leg.arrival().gateDateTime())) // in air during entire time window
					{ 
						if (WORLD.equals(this.airportOfInterest) || leg.departureAirport().equals(this.airportOfInterest) || leg.arrivalAirport().equals(this.airportOfInterest)){
					
							String badaType = leg.aircraft().badaRecord().getAircraftType();
							String vilLevels = "Wx: " + df2.format(leg.maxConvection()) + " Sua: " + df2.format(leg.maxSua());
							String flightInfo = leg.carrierId() + leg.flightNumber() + "." + leg.departureAirport() + "." + leg.arrivalAirport() + "\n" + 
												badaType + " " + vilLevels + "\n" + leg.aircraft().equipmentSuffix().toString();
							String rerouteType = "";
							if (leg.isRerouted()){
								rerouteType = "Rerouted";
							}else if (leg.failedReroute()){
								rerouteType = "RerouteFailed";
							}
							
							WakeStyle wakeStyle = getWakeCategory(leg.aircraft().badaRecord());
							
							this.currentLegPlacemark = legsFolder.createAndAddPlacemark().withDescription(flightInfo).withVisibility(false);
							this.currentLegPlacemark.setName(leg.flightId().toString() + " " + rerouteType);
							this.currentLegPlacemark.setStyleUrl(wakeStyle.name());
							this.currentLegTrack = this.currentLegPlacemark.createAndSetTrack();
							this.currentLegTrack.setAltitudeMode(AltitudeMode.ABSOLUTE);
							for (int i=0; i < leg.way().size(); ++i){
								TrajectoryPoint current = leg.way().get(i);
								TrajectoryPoint next = i < leg.way().size()-2 ? leg.way().get(i+1) : current;
								
								String coord = df6.format(current.longitude().degrees()) + " " + df6.format(current.latitude().degrees()) + " " + current.altitude().meters().intValue();
								this.currentLegTrack.addToCoord(coord);
								this.currentLegTrack.addToWhen(current.timestamp().toISO8601(false));
								
								// Headings get skewed if the distance between two points is too large.  
								// Insert additional points to a track so we don't get flights flying sideways.
								double distToNext = GCUtilities.gcDistance(current, next);
								while (distToNext > LARGE_DISTANCE_NMI){
									TrajectoryPoint newPoint = new TrajectoryPoint(GCUtilities.findPoint(current, next, LARGE_DISTANCE_NMI / SphericalUtilities.RADIANS_TO_NMI));
									double percentChange = LARGE_DISTANCE_NMI / distToNext;
									double altChange = percentChange * (next.altitude().feet() - current.altitude().feet());
									long milliChange = (long)(percentChange * next.timestamp().milliDifference(current.timestamp()));
									newPoint.setAltitude(new Altitude(current.altitude().feet() + altChange, Altitude.Units.FEET));
									newPoint.setTimestamp(current.timestamp().milliAdd(milliChange));
									
									antiMeridianCheck(leg, current, newPoint, distToNext, legsFolder, wakeStyle.name(), flightInfo);
									
									coord = df6.format(newPoint.longitude().degrees()) + " " + df6.format(newPoint.latitude().degrees()) + " " + newPoint.altitude().meters().intValue();
									this.currentLegTrack.addToCoord(coord);
									this.currentLegTrack.addToWhen(newPoint.timestamp().toISO8601(false));
									
									current = newPoint;
									distToNext = GCUtilities.gcDistance(current, next);
								}
								
								antiMeridianCheck(leg, current, next, distToNext, legsFolder, wakeStyle.name(), flightInfo);
							}
						}
					}
        		}
        	}
        }
					
		this.currentLegPlacemark = null;
		this.currentLegTrack = null;
	}
	
	public void recordTurnStart(String airport, Timestamp startT, List<? extends TrajectoryPoint> runways){
		List<AirportTurnRecord> turns = this.airportTurns.get(airport);
		if (turns == null){
			turns = new ArrayList<AirportTurnRecord>();
			this.airportTurns.put(airport, turns);
		}
		AirportTurnRecord newTurn = new AirportTurnRecord();
		newTurn.start = startT;
		newTurn.runways = runways;
		
		if (!turns.isEmpty() && turns.get(turns.size()-1).end == null){
			logger.warn(airport + " starting new turn before previous turn completes. Previous turn start: "  + turns.get(turns.size()-1).start + " New turn start: " + startT);
			turns.get(turns.size()-1).end = startT; // if new turn is starting, old turn automatically ends.
		}
		
		turns.add(newTurn);
	}
	
	public void recordTurnEnd(String airport, Timestamp endT, List<? extends TrajectoryPoint> runways){
		List<AirportTurnRecord> turns = this.airportTurns.get(airport);
		AirportTurnRecord newTurn = turns.get(turns.size()-1);
		newTurn.end = endT;
		assert(newTurn.runways.equals(runways)) : "Runways don't match for airport turn!";
	}
	
	public void recordGriddedWeather(NavigableMap<Timestamp,Collection<GCPointAlt>> griddedWxMap, float binInterval){
		Folder gridWxFolder = (Folder)this.document.getFeature().get(DocumentFolders.GridWx.ordinal());
		
		// show actual weather an hour before and after flights are recorded.
		Timestamp alteredStart = this.startTime.minuteSubtract(60.0); 
		Timestamp alteredEnd = this.endTime.minuteAdd(60.0);
		
		for (Map.Entry<Timestamp,Collection<GCPointAlt>> gridEntry : griddedWxMap.entrySet()){
			Timestamp wxSnapshotT = gridEntry.getKey();
			if (wxSnapshotT.afterOrEqualTo(alteredStart)){
				Folder timeFolder = gridWxFolder.createAndAddFolder().withName(wxSnapshotT.toString()).withVisibility(false);
				
				// group points by altitude for easier filtering
				for (int i=1; i <= 10; ++i){
					int maxAltitude = 5000 * i;
					timeFolder.createAndAddFolder().withName(String.valueOf(maxAltitude)).withVisibility(false);
				}
				timeFolder.createAndAddFolder().withName("50000+").withVisibility(false);
				
				for (GCPointAlt wxPoint : gridEntry.getValue()){
					
					int echoTop = wxPoint.altitude().feet().intValue() / 1000;
					int wxIntensity =  (int)Math.max(1, Math.min(6, Double.valueOf(wxPoint.name()).intValue()));
					
					int folderIdx = Math.min(wxPoint.altitude().feet().intValue() / 5000, 10);
					Folder altFolder = (Folder)timeFolder.getFeature().get(folderIdx);
					Placemark dataPoint = altFolder.createAndAddPlacemark().withName(String.valueOf(echoTop)).withVisibility(false);
					String coord = df6.format(wxPoint.longitude().degrees()) + "," + df6.format(wxPoint.latitude().degrees()) + ",0"; 
					dataPoint.createAndSetPoint().addToCoordinates(coord);
					dataPoint.setStyleUrl(MrmsWxStyle.values()[wxIntensity-1].name());
					
					TimeSpan timeSpan = dataPoint.createAndSetTimeSpan();
					timeSpan.setBegin(wxSnapshotT.toISO8601(false));
					timeSpan.setEnd(wxSnapshotT.minuteAdd(binInterval).toISO8601(false));
				}
			}
			
			if (wxSnapshotT.afterOrEqualTo(alteredEnd))
				break;
		}
	}
	
	/**
	 * GoogleEarth can't handle flying over anti-meridian. If current and next TrajectoryPoints cross over anti-meridian, 
	 * create new Placemark that will break up the flight into two separate tracks so flight doesn't fly backwards around the globe. 
	 * We choose a Placemark over a MultiTrack so we can retain ability to view Elevation Profiles in GoogleEarth for the two tracks.
	 * 
	 * @param leg FlightLeg
	 * @param placemark current Placemark
	 * @param track current Track that coordinates are being added to.
	 * @param current TrajectoryPoint that is already added to Track
	 * @param next TrajectoryPoint being considered for addition to Track
	 * @param distToNext distance between current and next
	 * @param styleUrl used if a new Placemark is created.
	 * @param flightInfo used if a new Placemark is crated.
	 */
	private void antiMeridianCheck(FlightLeg leg, TrajectoryPoint current, TrajectoryPoint next, double distToNext, Folder legsFolder, String styleUrl, String flightInfo){
		if ((leg.getDirection().equals(FlightDirection.EASTBOUND) && current.longitude().degrees() > 0 && (next.longitude().degrees() < 0) || next.longitude().degrees() == 180.0) ||
				(leg.getDirection().equals(FlightDirection.WESTBOUND) && current.longitude().degrees() < 0 && (next.longitude().degrees() > 0) || next.longitude().degrees() == -180.0)){ 
				
			TrajectoryPoint newPoint = findAntiMeridianIntersection(current, next);
			if (newPoint != null){ // intersection not found or error in calculating intersection, skip
				logger.debug("FlightId: " + leg.flightId() + " " + leg.departureAirport() + "." + leg.arrivalAirport() + " crossing anti-merdian.");
				assert(Math.abs(newPoint.longitude().degrees()) == 180.0);
				
				// we want newPoint on the same side of the anti-meridian as the current point
				if ((newPoint.longitude().degrees() < 0 && current.longitude().degrees() > 0) ||
					(newPoint.longitude().degrees() > 0 && current.longitude().degrees() < 0)){
					newPoint.setLongitude(new Longitude(-1*newPoint.longitude().degrees(),Units.DEGREES));
				}
				
				// TODO: the way we set the time is based on percentage of distance flown with no regard to any acceleration/deceleration so large spikes 
				// in speed can be observed in KML output, meaning speeds at the anti-meridian can't be totally trusted.  revisit this in future.
				double distToNew = GCUtilities.gcDistance(current, newPoint);
				double percentChange = distToNew / distToNext;
				double altChange = percentChange * (next.altitude().feet() - current.altitude().feet());
				long milliChange = (long)(percentChange * next.timestamp().milliDifference(current.timestamp()));
				
				newPoint.setAltitude(new Altitude(current.altitude().feet() + altChange, Altitude.Units.FEET));
				newPoint.setTimestamp(current.timestamp().milliAdd(milliChange));
				
				// add newPoint as the end point in old track.
				String coord = df6.format(newPoint.longitude().degrees()) + " " + df6.format(newPoint.latitude().degrees()) + " " + newPoint.altitude().meters().intValue();
				this.currentLegTrack.addToCoord(coord);
				this.currentLegTrack.addToWhen(newPoint.timestamp().toISO8601(false));
				
				// create new track on other side of anti-meridian.
				this.currentLegPlacemark = legsFolder.createAndAddPlacemark().withDescription(flightInfo).withVisibility(false);
				this.currentLegPlacemark.setName(leg.flightId().toString() + ".p2");
				this.currentLegPlacemark.setStyleUrl(styleUrl/*wakeStyle.name()*/);
				this.currentLegTrack = this.currentLegPlacemark.createAndSetTrack();
				this.currentLegTrack.setAltitudeMode(AltitudeMode.ABSOLUTE);
				
				// reverse sign of newPoint Longitude since it's on other side of anti-meridian
				newPoint.setLongitude(new Longitude(-1*newPoint.longitude().degrees(),Units.DEGREES));
				coord = df6.format(newPoint.longitude().degrees()) + " " + df6.format(newPoint.latitude().degrees()) + " " + newPoint.altitude().meters().intValue();
				this.currentLegTrack.addToCoord(coord);
				this.currentLegTrack.addToWhen(newPoint.timestamp().toISO8601(false));
				
				// finally, make sure next point is on same side of anti-meridian as new point.
				if ((next.longitude().degrees() < 0 && newPoint.longitude().degrees() > 0) ||
					(next.longitude().degrees() > 0 && newPoint.longitude().degrees() < 0)){
					next.setLongitude(new Longitude(-1*next.longitude().degrees(),Units.DEGREES));
				}
			}
		}
	}
	
	private static GCPoint antiMeridianNorth = new GCPoint(new Latitude(89.99,Units.DEGREES), new Longitude(180.0,Units.DEGREES));
	private static GCPoint antiMeridianSouth = new GCPoint(new Latitude(-89.99,Units.DEGREES), new Longitude(180.0,Units.DEGREES));
	private static TrajectoryPoint findAntiMeridianIntersection(GCPoint current, GCPoint next){
		try{
			GCPoint intersection = new GCPoint(SphericalUtilities.intersection(current.vector(), next.vector(), antiMeridianNorth.vector(), antiMeridianSouth.vector(), IntersectionType.NONSTRICT));
			if (intersection != null){
				return new TrajectoryPoint(intersection);
			}
		}catch (InvalidInputException e){
			logger.error("Invalid GCPoints for intersection calculation: current->" + current.toString() + " next->" + next.toString());
		}catch (NullPointerException e){
			logger.error("GCPoint to Vector3D conversion failure: current->" + current.toString() + " next->" + next.toString());
		}

		return null;
	}
	
	/**
	 * Call this after we are sure no more airport turns will be performed.  Publishes the turns to the KML document.
	 */
	private void recordAirportTurns(){
		if (this.airportTurns != null && !this.airportTurns.isEmpty()){
			Folder rootFolder = (Folder)this.document.getFeature().get(DocumentFolders.AirportTurns.ordinal());
			for (String airport : this.airportTurns.keySet()){
				Folder airportFolder = rootFolder.createAndAddFolder().withName(airport).withVisibility(false);
				
				List<AirportTurnRecord> turns = this.airportTurns.get(airport);
				
				Timestamp turnEndTime = null;
				List<? extends TrajectoryPoint> lastRwySet = null;
				for (AirportTurnRecord currentTurn : turns){
					
					// the last set of runways are active from last turn end until this new turn completes.
					if (turnEndTime != null){
						Folder activeFolder = airportFolder.createAndAddFolder().withName("Active  " + turnEndTime.toString());
						for (TrajectoryPoint runway : lastRwySet){
							Placemark runwayPoint = activeFolder.createAndAddPlacemark().withName(runway.name().substring(2)).withVisibility(false);
							String coord = df6.format(runway.longitude().degrees()) + "," + df6.format(runway.latitude().degrees()) + ",0"; 
							runwayPoint.createAndSetPoint().addToCoordinates(coord);
							runwayPoint.setStyleUrl(RunwayStyle.ACTIVE.name());
							TimeSpan timeSpan = runwayPoint.createAndSetTimeSpan();
							timeSpan.setBegin(turnEndTime.toISO8601(false));
							timeSpan.setEnd(currentTurn.end.toISO8601(false));
						}
					}
					
					// the current set of runways won't be active until the turn ends.
					Folder turnFolder = airportFolder.createAndAddFolder().withName("Turning " + currentTurn.start.toString());
					for (TrajectoryPoint runway : currentTurn.runways){
						Placemark runwayPoint = turnFolder.createAndAddPlacemark().withName(runway.name().substring(2)).withVisibility(false);
						String coord = df6.format(runway.longitude().degrees()) + "," + df6.format(runway.latitude().degrees()) + ",0"; 
						runwayPoint.createAndSetPoint().addToCoordinates(coord);
						runwayPoint.setStyleUrl(RunwayStyle.TURNING.name());
						TimeSpan timeSpan = runwayPoint.createAndSetTimeSpan();
						timeSpan.setBegin(currentTurn.start.toISO8601(false));
						timeSpan.setEnd(currentTurn.end.toISO8601(false));
					}
					
					turnEndTime = currentTurn.end;
					lastRwySet = currentTurn.runways;
				}
				
				// finally, make last runway set active until end of report period.
				Folder activeFolder = airportFolder.createAndAddFolder().withName("Active  " + turnEndTime.toString());
				for (TrajectoryPoint runway : lastRwySet){
					Placemark runwayPoint = activeFolder.createAndAddPlacemark().withName(runway.name().substring(2)).withVisibility(false);
					String coord = df6.format(runway.longitude().degrees()) + "," + df6.format(runway.latitude().degrees()) + ",0"; 
					runwayPoint.createAndSetPoint().addToCoordinates(coord);
					runwayPoint.setStyleUrl(RunwayStyle.ACTIVE.name());
					TimeSpan timeSpan = runwayPoint.createAndSetTimeSpan();
					timeSpan.setBegin(turnEndTime.toISO8601(false));
					Timestamp endT = turnEndTime.after(this.endTime) ? turnEndTime : this.endTime;
					timeSpan.setEnd(endT.toISO8601(false));
				}
			}
		}		
	}
	
	/**
	 * Creates tracks file in REPORT folder.
	 * 
	 * @param filename without extension.
	 * @param kmz zip KML output file?
	 */
	public void createTracksFile(String filename, boolean kmz) {
		
	    // TODO: We still need a better way to dump reports/logs to disk 
		try{
			recordAirportTurns();
		    if (kmz){
		    	filename += ".kmz";
		    	String fullPath = AdHocDataAccess.getResourceManager().getReportDir() + "/" + filename;
		    	this.kml.marshalAsKmz(fullPath, new Kml());	    	
		    }else{
		    	filename += ".kml";
				Writer writer = null;
		    	writer = AdHocDataAccess.getWriter(ResourceManager.LOCATION.REPORT, filename);
		    	//null check, KZ
		    	if (writer!=null){
		    		this.kml.marshal(writer);
		    		writer.close();
		    	}
		    }
		    this.airportTurns.clear();
		}catch (Exception ex) {
			if (this.airportTurns != null){
				this.airportTurns.clear();
			}
			logger.error("Couldn't create: " + filename + " KML Tracks file.");
    	}
	}
}

