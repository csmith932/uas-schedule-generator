package gov.faa.ang.swac.common.geometry.kml;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import gov.faa.ang.swac.common.flightmodeling.Aircraft;
import gov.faa.ang.swac.common.flightmodeling.FlightLeg;
import gov.faa.ang.swac.common.geometry.GCPointAltTime;
import gov.faa.ang.swac.common.geometry.kml.KmlUtilities.AltitudeMode;
import gov.faa.ang.swac.common.geometry.kml.KmlUtilities.Color;
import gov.faa.ang.swac.common.geometry.kml.KmlUtilities.GCClass;
import gov.faa.ang.swac.datalayer.storage.fileio.TextSerializable;
import gov.faa.ang.swac.datalayer.storage.fileio.WithFooter;
import gov.faa.ang.swac.datalayer.storage.fileio.WithHeader;

// TODO: This can be compacted to a smaller footprint if needed
public class TrajectoryKmlRecord implements TextSerializable, WithHeader, WithFooter {
	private static final String DOCUMENT_NAME = "Trajectory Paths";
	private static final String NULL = "NULL";
	
	private final String name;
	private final List<GCPointAltTime> trajectory;
	
	// Need a default constructor for reflection
	public TrajectoryKmlRecord() {
		name = "NULL";
		trajectory = new ArrayList<GCPointAltTime>(1);
	}
	
	public TrajectoryKmlRecord(String name, List<? extends GCPointAltTime> trajectory) {
		this.name = name;
		this.trajectory = new ArrayList<GCPointAltTime>(trajectory.size());
		for (GCPointAltTime p : trajectory) {
			this.trajectory.add(new GCPointAltTime(p));
		}
	}

	public TrajectoryKmlRecord(FlightLeg flightLeg) {
		this(getName(flightLeg), flightLeg.flightRoute());
	}
	
	private static String getName(FlightLeg flightLeg) {
		Integer flightId = flightLeg.flightId();
		Aircraft aircraft = flightLeg.aircraft();
		String carrierId = NULL;
		String acType = NULL;
		if (aircraft != null) {
			carrierId = aircraft.carrierId();
			if (aircraft.badaRecord() != null) {
				acType = aircraft.badaRecord().getAircraftType();
			}
		}
		String dep = flightLeg.departure() == null ? NULL : flightLeg.departure().airportName();
		String arr = flightLeg.arrival() == null ? NULL : flightLeg.arrival().airportName();
		return flightId + ": " + carrierId + " " + acType + " " + dep + "->" + arr;
	}
	
	@Override
	public long readHeader(BufferedReader reader) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void writeHeader(PrintWriter writer, long numRecords)
			throws IOException {
		writer.write(	"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
                		"<kml xmlns=\"http://earth.google.com/kml/2.2\">\n" +
                		"<Document>\n" +
                			"\t<name>" + DOCUMENT_NAME + "</name>\n" +
			     			"\t<open>0</open>\n");
		writer.write(KmlUtilities.kmlStyles(GCClass.LINE));
	}
	
	@Override
	public void readFooter(BufferedReader reader) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void writeFooter(PrintWriter writer) throws IOException {
		writer.write(	"</Document>\n" +
			 			"</kml>\n");
	}

	@Override
	public void readItem(BufferedReader reader) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void writeItem(PrintWriter writer) throws IOException {
		String kml = KmlUtilities.toKml(trajectory, name, Color.RED, AltitudeMode.ABSOLUTE);
		if (kml != null) {
			writer.write(kml);
		}
	}
}
