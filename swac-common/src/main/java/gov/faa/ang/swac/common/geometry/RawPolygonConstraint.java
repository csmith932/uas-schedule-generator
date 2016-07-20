/**
 * Copyright 2014, Metron Aviation & CSSI.  All rights reserved.
 * 
 * This computer Software was developed with the sponsorship of the U.S. Government
 * under Contract No. DTFAWA-10-D-00033, which has a copyright license in accordance with AMS 3.5-13.(c)(1).
 */

package gov.faa.ang.swac.common.geometry;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.log4j.LogManager;

import de.micromata.opengis.kml.v_2_2_0.Coordinate;
import de.micromata.opengis.kml.v_2_2_0.Data;
import de.micromata.opengis.kml.v_2_2_0.Document;
import de.micromata.opengis.kml.v_2_2_0.Feature;
import de.micromata.opengis.kml.v_2_2_0.Folder;
import de.micromata.opengis.kml.v_2_2_0.Kml;
import de.micromata.opengis.kml.v_2_2_0.Placemark;
import de.micromata.opengis.kml.v_2_2_0.Polygon;
import de.micromata.opengis.kml.v_2_2_0.TimeSpan;
import gov.faa.ang.swac.common.datatypes.Altitude;
import gov.faa.ang.swac.common.datatypes.Latitude;
import gov.faa.ang.swac.common.datatypes.Longitude;
import gov.faa.ang.swac.datalayer.storage.fileio.StreamSerializable;

public class RawPolygonConstraint implements StreamSerializable {
	private static final org.apache.log4j.Logger logger = LogManager.getLogger(RawPolygonConstraint.class);
	
	private static double getIntensity(String str) {
		if ("VIL Level 0".equals(str)) {
			return 0.0;
		} else if ("VIL Level 1".equals(str)) {
			return 1.0;
		} else if ("VIL Level 2".equals(str)) {
			return 2.0;
		} else if ("VIL Level 3".equals(str)) {
			return 3.0;
		} else if ("VIL Level 4".equals(str)) {
			return 4.0;
		} else if ("VIL Level 5".equals(str)) {
			return 5.0;
		} else if ("VIL Level 6".equals(str)) {
			return 6.0;
		} else {
			try {
				return Double.parseDouble(str);
			} catch (NumberFormatException ex) {
				return 7.0;
			}
		}
	}
	
	public List<RawPolygonConstraintRecord> records;
	
	@Override
	public void readItem(InputStream in) throws IOException {
		List<RawPolygonConstraintRecord> constraints = new ArrayList<RawPolygonConstraintRecord>();
		
		DateFormat kmlDateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		final Kml kml = Kml.unmarshal(in);
		final Document document = (Document)kml.getFeature();
		List<Feature> folders = document.getFeature();
		for(Feature folder : folders)
		{
			List<Feature> placeMarks = ((Folder)folder).getFeature();
			double intensity = getIntensity(folder.getName());

			for(Feature placeMarkFeature : placeMarks)
			{
				Placemark placemark = (Placemark)placeMarkFeature;
				Date startDate = null, endDate = null;
				logger.debug("Loading weather polygon named " + placemark.getName() + "...");
				TimeSpan timeSpan = (TimeSpan)placemark.getTimePrimitive();
				try
				{
					startDate = new Date(kmlDateFormatter.parse(timeSpan.getBegin()).getTime());
					endDate = new Date(kmlDateFormatter.parse(timeSpan.getEnd()).getTime());
				}
				catch(ParseException e)
				{
					e.printStackTrace();
					// JRC: should we return here or throw an IOException? endDate and possibly startDate are null. Code
					// below will inevitably generate null pointer.
				}
				logger.debug("  polygon time bounds: " + startDate + ", " + endDate);
				
				double minAlt = 0;
				double maxAlt = Double.MAX_VALUE;
				if (placemark.getExtendedData() != null){
					for (Data data : placemark.getExtendedData().getData()){
						if (data.getName().equals("minAltitude")){
							minAlt = Double.valueOf(data.getValue());
						}
						else if (data.getName().equals("maxAltitude")){
							maxAlt = Double.valueOf(data.getValue());
						}
					}
				}
				else{
					logger.info("No ExtendedData information.  Constraint altitudes will be set to: minAlt=0, maxAlt=Double.MAX_VALUE");					
				}

				Altitude floor = Altitude.valueOfFeet(minAlt);
				Altitude ceil = Altitude.valueOfFeet(maxAlt);				
				
				Polygon polygon = (Polygon) placemark.getGeometry();
				List<Coordinate> coordinates = polygon.getOuterBoundaryIs().getLinearRing().getCoordinates();
				List<GCPoint> coordList = new ArrayList<GCPoint>();
				for (Coordinate coordinate : coordinates) {
					double lat = coordinate.getLatitude();
					double lon = coordinate.getLongitude();
					coordList.add(new GCPoint(Latitude.valueOfDegrees(lat), Longitude.valueOfDegrees(lon))); 
				}
				
				String name = in.toString() + startDate.getTime() + endDate.getTime() + floor + ceil + coordList.hashCode(); // Just needs to be unique enough for HashMap lookup by name
				
				RawPolygonConstraintRecord record = new RawPolygonConstraintRecord(name, intensity, coordList, startDate, endDate, floor, ceil);
				constraints.add(record);
			}
		}
		records = Collections.unmodifiableList(constraints);
	}

	@Override
	public void writeItem(OutputStream out) throws IOException {
		throw new UnsupportedOperationException();
	}

	public static class RawPolygonConstraintRecord {
		public final String name;
		public final double intensity;
		public final List<GCPoint> coordList;
		public final Date startDate;
		public final Date endDate;
		public Altitude floor;
		public Altitude ceil;
		
		public RawPolygonConstraintRecord(String name, double intensity, List<GCPoint> coordList, Date startDate, Date endDate, Altitude floor, Altitude ceil) {
			this.name = name;
			this.intensity = intensity;
			this.coordList = Collections.unmodifiableList(coordList);
			this.startDate = startDate;
			this.endDate = endDate;
			this.floor = floor;
			this.ceil = ceil;
		}
	}
}
