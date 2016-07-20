package gov.faa.ang.swac.common.entities;

import gov.faa.ang.swac.common.datatypes.Timestamp;
import gov.faa.ang.swac.datalayer.storage.fileio.HeaderUtils;
import gov.faa.ang.swac.datalayer.storage.fileio.TextSerializable;
import gov.faa.ang.swac.datalayer.storage.fileio.WithHeader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Creates an association between a particular {@link gov.faa.ang.swac.common.entities.Airport Airport}
 * and a particular {@link gov.faa.ang.swac.common.entities.Fix Fix}
 *
 * @author ssmitz
 *
 */
public class AirportFix implements TextSerializable, WithHeader, Comparable<AirportFix> {

    public static enum FixType {

        ARRIVAL, DEPARTURE
    };
    /*
     * Instance variables...
     */
    private String aptName;                 // The FAA airport code of the airport for which this association is valid. 
    private String fixName;                 // The name of the fix for which this association is valid.
    private FixType type;                   // Whether the fix is used for arrivals or departures.
    private Double sharedSeparationTime;    // The local rate the the airport will use for this fix in minutes.
    private List<String> exceptions;        // The airports which are exempted by this airport for this fix.

    /*
     * Constructors
     */
    /**
     * This should only be used by the Data Access Layer!
     */
    public AirportFix() {
        this.aptName = null;
        this.fixName = null;
        this.type = null;
        this.sharedSeparationTime = null;
        this.exceptions = new ArrayList<String>();
    }

    /**
     * @param aptName The FAA abbreviated name of the airport for which this
     * association is valid.
     * @param fixName The name of the fix for which this association is valid.
     *
     * @throws IllegalArgumentException if {@code fixName} or {@code aptName} is {@code null}
     * or if either are an empty {@link java.lang.String String} after
     * whitespace has been removed.
     */
    public AirportFix(String aptName, String fixName) throws IllegalArgumentException {
        if (aptName == null || aptName.trim().isEmpty() || fixName == null || fixName.trim().isEmpty()) {
            throw new IllegalArgumentException("Attemted to create an improperly associated AirportFixes!");
        } else {
            this.aptName = aptName;
            this.fixName = fixName;
            this.type = null;
            this.sharedSeparationTime = null;
            this.exceptions = new ArrayList<String>();
        }
    }

    /**
     * @param aptName The FAA abbreviated name of the airport for which this
     * association is valid.
     * @param fixName The name of the fix for which this association is valid.
     * @param type Whether the fix is used for arrivals or departures.
     * @param sharedSeparationTime The local rate the the airport will use for
     * this fix in minutes.
     * @param exceptions The airports which are exempted by this airport.
     *
     * @throws IllegalArgumentException if {@code fixName} or {@code aptName} is {@code null}
     * or if either are an empty {@link java.lang.String String} after
     * whitespace has been removed.
     */
    public AirportFix(String aptName,
            String fixName,
            String type,
            Double sharedSeparationTime,
            String exceptions)
            throws IllegalArgumentException {
        if (aptName == null || aptName.trim().contentEquals("") || fixName == null || fixName.trim().contentEquals("")) {
            throw new IllegalArgumentException("Attemted to create an improperly associated AirportFixes!");
        } else {
            this.aptName = aptName;
            this.fixName = fixName;
            this.type = (type == null ? null : FixType.valueOf(type));
            this.sharedSeparationTime = sharedSeparationTime;
            this.exceptions = new ArrayList<String>();

            setExceptions(exceptions);
        }
    }

    /*
     * Getters and Setters
     */
    /**
     * @return The FAA abbreviated name of the airport for which this
     * association is valid.
     */
    public String getAptName() {
        return aptName;
    }

    /**
     * @param aptName The FAA abbreviated name of the airport for which this
     * association is valid.
     *
     * @throws IllegalArgumentException if {@code fixName} or {@code aptName} is {@code null}
     * or if either are an empty {@link java.lang.String String} after
     * whitespace has been removed.
     */
    public void setAptName(String aptName) throws IllegalArgumentException {
        if (aptName == null || aptName.trim().contentEquals("")) {
            throw new IllegalArgumentException("Attemted to unassociated AirportFixes from Airport!");
        } else {
            this.aptName = aptName;
        }
    }

    /**
     * @return The name of the fix for which this association is valid.
     */
    public String getFixName() {
        return fixName;
    }

    /**
     * @param fixName The name of the fix for which this association is valid.
     *
     * @throws IllegalArgumentException if {@code fixName} is {@code null} or an
     * empty {@link java.lang.String String} after whitespace has been removed.
     */
    public void setFixName(String fixName) throws IllegalArgumentException {
        if (fixName == null || fixName.trim().contentEquals("")) {
            throw new IllegalArgumentException("Attemted to unassociated AirportFixes from Fix!");
        } else {
            this.fixName = fixName;
        }
    }

    /**
     * @return Whether this is an ARRIVAL or DEPARTURE fix for this airport.
     */
    public String getType() {
        return type.toString();
    }

    /**
     * @param type Whether this is an ARRIVAL or DEPARTURE fix for this airport.
     */
    public void setType(String type) {
        this.type = FixType.valueOf(type);
    }

    /**
     * @return The local rate the the airport will use for this fix in minutes
     * or null if unset.
     */
    public Double getSharedSeparationTime() {
        return sharedSeparationTime;
    }

    /**
     * @param sharedSeparationTime The local rate the the airport will use for
     * this fix in minutes.
     */
    public void setSharedSeparationTime(Double sharedSeparationTime) {
        this.sharedSeparationTime = sharedSeparationTime;
    }

    /**
     * @return The airports which are exempted by this airport for this fix as a
     * comma-delineated list.
     */
    public String getExceptions() {
        String rtn = exceptions.toString();
        rtn = rtn.replace("[", "").replace("]", "").replace(" ", ",");
        return rtn;
    }

    /**
     * @param exceptions The airports which are exempted by this airport for
     * this fix as a comma-delineated list.
     */
    public void setExceptions(String exceptions) {
        if (exceptions == null) {
            this.exceptions = new ArrayList<String>();
        } else {
            for (String ex : exceptions.split(",")) {
                this.exceptions.add(ex.trim());
            }
        }
    }

    @Override
    public int compareTo(AirportFix af) {
        if (this.aptName.contentEquals(af.aptName)) {
            return this.fixName.compareTo(af.fixName);
        } else {
            return this.aptName.compareTo(af.aptName);
        }
    }

    @Override
    public long readHeader(BufferedReader reader) throws IOException {
    	HeaderUtils.readHeaderHashComment(reader);
        return -1;
    }

    @Override
    public void writeHeader(PrintWriter writer, long numRecords) throws IOException {
    	writer.println("# Airport, Fix Name, Fix Type, Separation Time (mins), Exceptions List");
    }

    @Override
    public void readItem(BufferedReader reader) throws IOException {
        String line = reader.readLine();

        while (line.matches("^(#.*)|(\\s+)$")) {
            line = reader.readLine();
        }

        String[] fields = line.split(",");

        aptName = fields[0].trim();
        fixName = fields[1].trim();

        String fixType = fields[2].trim();
        
        if (fixType.contentEquals("A")) {
            type = FixType.ARRIVAL;
        } else if (fixType.contentEquals("D")) {
            type = FixType.DEPARTURE;
        } else {
            type = null;
        }
        
        String time = "";
        
        if (fields[3] != null)
            time = fields[3].trim();

        sharedSeparationTime = time.isEmpty() ? null : Double.valueOf(time) * Timestamp.MILLISECS_MIN;

        if (exceptions == null) {
            exceptions = new ArrayList<String>();
        }

        for (int i = 4; i < fields.length; ++i) {
            if (fields[i] != null) {
                String exception = fields[i].trim();
                if (!exception.isEmpty()) {
                    exceptions.add(exception);
                }
            }
        }
    }

    @Override
    public void writeItem(PrintWriter writer) throws IOException {
        char sep = ',';
        DecimalFormat sdf = new DecimalFormat("0.00");

        writer.write(aptName + sep);
        writer.write(fixName + sep);

        if (type == FixType.ARRIVAL) {
            writer.write("A" + sep);
        } else if (type == FixType.DEPARTURE) {
            writer.write("D" + sep);
        } else {
            writer.write(sep);
        }

        writer.write((sharedSeparationTime == null ? "" : sdf.format(sharedSeparationTime)) + sep);
        writer.write(getExceptions());
        writer.write("\n");
    }
}
