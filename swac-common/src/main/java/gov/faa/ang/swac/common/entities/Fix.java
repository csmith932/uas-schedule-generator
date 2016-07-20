package gov.faa.ang.swac.common.entities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import gov.faa.ang.swac.common.datatypes.Latitude;
import gov.faa.ang.swac.common.datatypes.Longitude;
import gov.faa.ang.swac.common.datatypes.Timestamp;
import gov.faa.ang.swac.common.datatypes.Angle.Units;
import gov.faa.ang.swac.datalayer.storage.fileio.HeaderUtils;
import gov.faa.ang.swac.datalayer.storage.fileio.TextSerializable;
import gov.faa.ang.swac.datalayer.storage.fileio.WithHeader;

public class Fix implements TextSerializable, WithHeader, Comparable<Fix> {
    /*
     * Instance variables...
     */

    private String name;            // The name of the fix
    private Latitude latitude;      // The latitude of the fix.
    private Longitude longitude;    // The longitude of the fix.
    private Double separationTime;  // The global separation time of the fix in minutes.

    /*
     * Constructors
     */
    /**
     * This should only be used by the Data Access Layer!
     */
    public Fix() {
        this.name = null;
        this.latitude = null;
        this.longitude = null;
        this.separationTime = null;
    }

    /**
     * @param name The name of the fix.
     *
     * @throws IllegalArgumentException if {@code fixName} is {@code null} or an
     * empty {@link java.lang.String String} after whitespace has been removed.
     */
    public Fix(String name) throws IllegalArgumentException {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Attemted to create unnamed Fix!");
        } else {
            this.name = name;
            this.latitude = null;
            this.longitude = null;
            this.separationTime = null;
        }
    }

    /**
     * @param name The name of the fix.
     * @param latitude The latitude of the fix.
     * @param longitude the longitude of the fix.
     * @param seperationTime The global separation time of the fix in minutes.
     *
     * @throws IllegalArgumentException if {@code fixName} is {@code null} or an
     * empty {@link java.lang.String String} after whitespace has been removed.
     */
    public Fix(String name, Latitude latitude, Longitude longitude, Double seperationTime) throws IllegalArgumentException {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Attemted to create unnamed Fix!");
        } else {
            this.name = name;
            this.latitude = latitude;
            this.longitude = longitude;
            this.separationTime = seperationTime;
        }
    }

    /*
     * Getters and Setters
     */
    /**
     * @return the name of the fix.
     */
    public String getName() {
        return name;
    }

    /**
     * @param name The name of the fix.
     *
     * @throws IllegalArgumentException if {@code name} is {@code null} or an
     * empty {@link java.lang.String String} after whitespace has been removed.
     */
    public void setName(String name) throws IllegalArgumentException {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Attemted to unname Fix!");
        } else {
            this.name = name;
        }
    }

    /**
     * @return The latitude of the fix or null if unset.
     */
    public Latitude getLatitude() {
        return latitude;
    }

    /**
     * @param latitude The latitude of the fix or null if unset.
     */
    public void setLatitude(Latitude latitude) {
        this.latitude = latitude;
    }

    /**
     * @return The longitude of the fix or null if unset.
     */
    public Longitude getLongitude() {
        return longitude;
    }

    /**
     * @param longitude The longitude of the fix or null if unset.
     */
    public void setLongitude(Longitude longitude) {
        this.longitude = longitude;
    }

    /**
     * @return The global separation time of the fix in minutes or null if unset
     */
    public Double getSeparationTime() {
        return separationTime;
    }

    /**
     * @param separationTime The global separation time for the fix in minutes.
     */
    public void setSeparationTime(double separationTime) {
        this.separationTime = separationTime;
    }

    @Override
    public int compareTo(Fix f) {
        return this.name.compareTo(f.name);
    }

    @Override
    public long readHeader(BufferedReader reader) throws IOException {
    	HeaderUtils.readHeaderHashComment(reader);
        return -1;
    }

    @Override
    public void writeHeader(PrintWriter writer, long numRecords) throws IOException {
        writer.println("#Fix Name, Latitude, Longitude, SeparationTime (mins)");
    }

    @Override
    public void readItem(BufferedReader reader) throws IOException {
        String line = reader.readLine();
        String[] fields = line.split(",");

        name = fields[0].trim();
        latitude = new Latitude(Double.valueOf(fields[1].trim()), Units.DEGREES);
        longitude = new Longitude(Double.valueOf(fields[2].trim()), Units.DEGREES);
        separationTime = fields[3] == null || fields[3].trim().isEmpty() ? null : Double.valueOf(fields[3].trim()) * Timestamp.MILLISECS_MIN;
    }

    @Override
    public void writeItem(PrintWriter writer) throws IOException {
        char sep = ',';
        DecimalFormat sdf = new DecimalFormat("0.00");
        DecimalFormat ldf = new DecimalFormat("0.0000000000");

        writer.write(name + sep);
        writer.write(ldf.format(latitude.degrees()) + sep);
        writer.write(ldf.format(longitude.degrees()) + sep);
        writer.write((separationTime == null ? "" : sdf.format(separationTime)));

        writer.write("\n");
    }
}
