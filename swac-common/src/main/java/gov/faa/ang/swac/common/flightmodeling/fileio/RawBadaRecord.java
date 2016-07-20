/**
 * Copyright "TBD", Metron Aviation & CSSI.  All rights reserved.
 * 
 * This computer Software was developed with the sponsorship of the U.S. Government
 * under Contract No. DTFAWA-10-D-00033, which has a copyright license in accordance with AMS 3.5-13.(c)(1).
 */

package gov.faa.ang.swac.common.flightmodeling.fileio;

import gov.faa.ang.swac.common.datatypes.Altitude;
import gov.faa.ang.swac.common.datatypes.Patterns;
import gov.faa.ang.swac.datalayer.storage.fileio.TextSerializable;
import gov.faa.ang.swac.datalayer.storage.fileio.WithFooter;
import gov.faa.ang.swac.datalayer.storage.fileio.WithHeader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * An intermediate data structure for reading and writing BADA data files. 
 * Passed into a constructor in {@link BadaRecord} to generate XML.
 * 
 * @author ssmitz
 * 
 */
public class RawBadaRecord extends BadaRecord implements TextSerializable, WithHeader, WithFooter {
    // ---------------------
    // Static class members
    // ---------------------
    private static org.apache.log4j.Logger logger = org.apache.log4j.LogManager.getLogger(RawBadaRecord.class);

    // ToTextRecord related members
    public static final String DATE_PATTERN = "\\w+ \\d\\d \\d\\d\\d\\d";
    public static final String SEP = " ";
    public static final String TEXT_RECORD_KEY = "";
    // <seperator line>
    public static final String SEPARATOR_LINE_PATTERN = "^=====+.*$";
    // 1: fileDate
    public static final String HEADER_LINE_01 = "^[\\w|\\s|(|)|']+\\s(" + DATE_PATTERN + ")\\s*$";
    // 2: <blank line>
    public static final String HEADER_LINE_02 = "^\\s*$"; 
    // 3: aircraftType
    public static final String HEADER_LINE_03 = "^AC/Type:\\s*(" + Patterns.WORD + ")\\s*$"; 
    // 4: sourceOpfDate
    public static final String HEADER_LINE_04 = "^\\s*Source OPF File:\\s*(" + DATE_PATTERN + ")\\s*$"; 
    // 5: sourceAfpDate
    public static final String HEADER_LINE_05 = "^\\s*Source APF [fF]ile:\\s*(" + DATE_PATTERN + ")\\s*$"; 
    // 6: <blank line>
    public static final String HEADER_LINE_06 = "^\\s*$"; 
    // 7: Temperature
    public static final String HEADER_LINE_07 = "^\\s*Speeds:\\s+CAS\\(LO/HI\\)\\s+Mach\\s+Mass Levels \\[kg\\]\\s+Temperature:\\s+(.*)\\s*$"; 
    // 8: climb speed low, climb speed high, climb mach, mass level (low)
    public static final String HEADER_LINE_08 = "^\\s+climb\\s*-\\s*(" + Patterns.INTEGER + ")/\\s*(" + Patterns.INTEGER + ")\\s+(" + Patterns.FLOAT + ")\\s+low\\s*-\\s*(" + Patterns.INTEGER + ")\\s*$"; 
    // 9: cruise speed low, cruise speed high, cruise mach, mass level (nominal), max altitude (ft)
    public static final String HEADER_LINE_09 = "^\\s+cruise\\s*-\\s*(" + Patterns.INTEGER + ")/\\s*(" + Patterns.INTEGER + ")\\s+(" + Patterns.FLOAT + ")\\s+nominal\\s*-\\s*(" + Patterns.INTEGER + ")\\s*Max Alt. \\[ft\\]:\\s*(" + Patterns.INTEGER + ")\\s*$";
    // 10: descent speed low, descent speed high, descent mach, mass level (high)
    public static final String HEADER_LINE_10 = "^\\s+descent\\s*-\\s*(" + Patterns.INTEGER + ")/\\s*(" + Patterns.INTEGER + ")\\s+(" + Patterns.FLOAT + ")\\s+high\\s*-\\s*(" + Patterns.INTEGER + ")\\s*$";
    // 11: <separator line>
    public static final String HEADER_LINE_11 = SEPARATOR_LINE_PATTERN;
    // 12: <column header>
    public static final String HEADER_LINE_12 = "^.*$";
    // 13: <column header>
    public static final String HEADER_LINE_13 = "^.*$";
    // 14: <column header>
    public static final String HEADER_LINE_14 = "^.*$";
    // 15: <column header>
    public static final String HEADER_LINE_15 = "^.*$";
    // 16: <separator line>
    public static final String HEADER_LINE_16 = SEPARATOR_LINE_PATTERN;
    // flightLevel (100s of feet of altitude)
    public static final String TEXT_RECORD_PATTERN = "^\\s*(" + Patterns.INTEGER + ")\\s*\\|\\s*" + 
    "(" + Patterns.INTEGER + ")?\\s*" + // CRUISE - trueAirspeed (knots)
    "(" + Patterns.FLOAT + ")?\\s*" + // CRUISE - FUEL USAGE - low (kg/min)
    "(" + Patterns.FLOAT + ")?\\s*" + // CRUISE - FUEL USAGE - nominal (kg/min)
    "(" + Patterns.FLOAT + ")?\\s*\\|\\s*" + // CRUISE - FUEL USAGE - high
                                             // (kg/min)
    "(" + Patterns.INTEGER + ")?\\s*" + // CLIMB - trueAirspeed (knots)
    "(" + Patterns.INTEGER + ")?\\s*" + // CLIMB - ROCD - low (fpm)
    "(" + Patterns.INTEGER + ")?\\s*" + // CLIMB - ROCD - nominal (fpm)
    "(" + Patterns.INTEGER + ")?\\s*" + // CLIMB - ROCD - high (fpm)
    "(" + Patterns.FLOAT + ")?\\s*\\|\\s*" + // CLIMB - FUEL USAGE - nominal
                                             // (kg/min)
    "(" + Patterns.INTEGER + ")?\\s*" + // DESCENT - trueAirspeed (knots)
    "(" + Patterns.INTEGER + ")?\\s*" + // DESCENT - ROCD - nominal (fp)
    "(" + Patterns.FLOAT + ")?\\s*$"; // DESCENT - FUEL USAGE - nominal (kg/min)
    public static final String TEXT_RECORD_SEPARATOR_PATTERN = "^\\s+\\|\\s+\\|\\s+\\|\\s*$";

    public static final String TEXT_RECORD_PATTERN_NON_CAPTURING = "";
    private static Pattern textRecordPattern = Pattern.compile(TEXT_RECORD_PATTERN);
    private static Pattern textRecordSeparatorPattern = Pattern.compile(TEXT_RECORD_SEPARATOR_PATTERN);
    private static Pattern separatorLinePattern = Pattern.compile(SEPARATOR_LINE_PATTERN);
    private static Pattern headerLine01Pattern = Pattern.compile(HEADER_LINE_01);
    private static Pattern headerLine03Pattern = Pattern.compile(HEADER_LINE_03);
    private static Pattern headerLine04Pattern = Pattern.compile(HEADER_LINE_04);
    private static Pattern headerLine05Pattern = Pattern.compile(HEADER_LINE_05);
    private static Pattern headerLine07Pattern = Pattern.compile(HEADER_LINE_07);
    private static Pattern headerLine08Pattern = Pattern.compile(HEADER_LINE_08);
    private static Pattern headerLine09Pattern = Pattern.compile(HEADER_LINE_09);
    private static Pattern headerLine10Pattern = Pattern.compile(HEADER_LINE_10);

    public RawBadaRecord() {
        super();
    }

    /**
     * Parses a Bada data formatted text file.
     * 
     * @param reader 
     */
    public void readItem(BufferedReader reader) throws IOException {
        String line = null;
        try {
            // --------------------
            // Process file header
            // --------------------
            line = reader.readLine();
            Matcher matcher = headerLine01Pattern.matcher(line);

            if (matcher.find()) {
                this.setFileDate(matcher.group(1));
            } else {
                logger.error("BadaFileReader: Error reading PTF file: Expected \"" + headerLine01Pattern.pattern() + "\", got: \"" + line + "\".");
            }

            line = reader.readLine(); // Do nothing, line 2 blank
            line = reader.readLine();

            matcher = headerLine03Pattern.matcher(line);
            if (matcher.find()) {
                this.setAircraftType(matcher.group(1));
                if (this.getAircraftType().endsWith("_")) {
                    // Strip off trailing underscores ('_')
                    this.setAircraftType(this.getAircraftType().substring(0, this.getAircraftType().indexOf('_')));
                    logger.debug("Parsing data for aircraft: " + this.getAircraftType());
                }
            } else {
                logger.error("BadaFileReader error: Error reading PTF file: Expected " + headerLine03Pattern.pattern() + ", got: \"" + line + "\".");
            }

            line = reader.readLine();

            matcher = headerLine04Pattern.matcher(line);
            if (matcher.find()) {
                this.setSourceOpfDate(matcher.group(1));
            } else {
                logger.error("BadaFileReader error: Error reading PTF file: Expected " + headerLine04Pattern.pattern() + ", got: \"" + line + "\".");
            }

            line = reader.readLine();
            matcher = headerLine05Pattern.matcher(line);
            if (matcher.find()) {
                this.setSourceApfDate(matcher.group(1));
            } else {
                logger.error("BadaFileReader error: Error reading PTF file: Expected " + headerLine05Pattern.pattern() + ", got: \"" + line + "\".");
            }

            line = reader.readLine(); // Do nothing, line 6 blank

            line = reader.readLine();
            matcher = headerLine07Pattern.matcher(line);
            if (matcher.find()) {
                this.setTemperature(matcher.group(1));
            } else {
                logger.error("BadaFileReader error: Error reading PTF file: Expected " + headerLine07Pattern.pattern() + ", got: \"" + line + "\".");
            }

            line = reader.readLine();
            matcher = headerLine08Pattern.matcher(line);

            int[][] casArray = { { Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE }, { Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE }, { Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE } };
            double[] machArray = { Double.NaN, Double.NaN, Double.NaN };
            int[] massArray = { Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE };

            if (matcher.find()) {
                casArray[BadaRecord.FlightStage.ASCENT.ordinal()][BadaRecord.Range.LOW.ordinal()] = Integer.valueOf(matcher.group(1));
                casArray[BadaRecord.FlightStage.ASCENT.ordinal()][BadaRecord.Range.HIGH.ordinal()] = Integer.valueOf(matcher.group(2));
                machArray[BadaRecord.FlightStage.ASCENT.ordinal()] = Double.valueOf(matcher.group(3));
                massArray[BadaRecord.Range.LOW.ordinal()] = Integer.valueOf(matcher.group(4));
            } else {
                logger.error("BadaFileReader error: Error reading PTF file: Expected " + headerLine08Pattern.pattern() + ", got: \"" + line + "\".");
            }

            line = reader.readLine();
            matcher = headerLine09Pattern.matcher(line);
            if (matcher.find()) {
                casArray[BadaRecord.FlightStage.CRUISE.ordinal()][BadaRecord.Range.LOW.ordinal()] = Integer.valueOf(matcher.group(1));
                casArray[BadaRecord.FlightStage.CRUISE.ordinal()][BadaRecord.Range.HIGH.ordinal()] = Integer.valueOf(matcher.group(2));
                machArray[BadaRecord.FlightStage.CRUISE.ordinal()] = Double.valueOf(matcher.group(3));
                massArray[BadaRecord.Range.NOMINAL.ordinal()] = Integer.valueOf(matcher.group(4));
                this.setMaxAltitude(Integer.valueOf(matcher.group(5))); 
            } else {
                logger.error("BadaFileReader error: Error reading PTF file: Expected " + headerLine09Pattern.pattern() + ", got: \"" + line + "\".");
            }

            line = reader.readLine();
            matcher = headerLine10Pattern.matcher(line);
            if (matcher.find()) {
                casArray[BadaRecord.FlightStage.DESCENT.ordinal()][BadaRecord.Range.LOW.ordinal()] = Integer.valueOf(matcher.group(1));
                casArray[BadaRecord.FlightStage.DESCENT.ordinal()][BadaRecord.Range.HIGH.ordinal()] = Integer.valueOf(matcher.group(2));
                machArray[BadaRecord.FlightStage.DESCENT.ordinal()] = Double.valueOf(matcher.group(3));
                massArray[BadaRecord.Range.HIGH.ordinal()] = Integer.valueOf(matcher.group(4));
            } else {
                logger.error("BadaFileReader error: Error reading PTF file: Expected " + headerLine10Pattern.pattern() + ", got: \"" + line + "\".");
            }

            this.setCasSpeed(casArray);
            this.setMachSpeed(machArray);
            this.setMassLevel(massArray);

            line = reader.readLine(); // No nothing, line 11
            line = reader.readLine(); // No nothing, line 12
            line = reader.readLine(); // No nothing, line 13
            line = reader.readLine(); // No nothing, line 14
            line = reader.readLine(); // No nothing, line 15
            line = reader.readLine(); // No nothing, line 16

            // ------------------
            // Process file body
            // ------------------
            ArrayList<BadaRecord.FlightLevelRecord> flightLevelRecords = new ArrayList<BadaRecord.FlightLevelRecord>();
            BadaRecord.FlightLevelRecord flightLevelRecord = null;
            while (reader.ready()) {
                line = reader.readLine();

                matcher = textRecordPattern.matcher(line);
                if (matcher.find()) {
                    flightLevelRecord = new BadaRecord.FlightLevelRecord();
                    // Convert flight level to feet
                    flightLevelRecord.altitude = Altitude.valueOfFeet(Integer.valueOf(matcher.group(1)) * 100.0);
                    
                    if (matcher.group(2) != null) {
                        flightLevelRecord.trueAirSpeed[BadaRecord.FlightStage.CRUISE.ordinal()] = Integer.valueOf(matcher.group(2));
                    }
                    if (matcher.group(3) != null) {
                        flightLevelRecord.cruiseFuelUsage[BadaRecord.Range.LOW.ordinal()] = Double.valueOf(matcher.group(3));
                    }
                    if (matcher.group(4) != null) {
                        flightLevelRecord.cruiseFuelUsage[BadaRecord.Range.NOMINAL.ordinal()] = Double.valueOf(matcher.group(4));
                    }
                    if (matcher.group(5) != null) {
                        flightLevelRecord.cruiseFuelUsage[BadaRecord.Range.HIGH.ordinal()] = Double.valueOf(matcher.group(5));
                    }

                    if (matcher.group(6) != null) {
                        flightLevelRecord.trueAirSpeed[BadaRecord.FlightStage.ASCENT.ordinal()] = Integer.valueOf(matcher.group(6));
                    }
                    if (matcher.group(7) != null) {
                        flightLevelRecord.climbRocd[BadaRecord.Range.LOW.ordinal()] = Integer.valueOf(matcher.group(7));
                    }
                    if (matcher.group(8) != null) {
                        flightLevelRecord.climbRocd[BadaRecord.Range.NOMINAL.ordinal()] = Integer.valueOf(matcher.group(8));
                    }
                    if (matcher.group(9) != null) {
                        flightLevelRecord.climbRocd[BadaRecord.Range.HIGH.ordinal()] = Integer.valueOf(matcher.group(9));
                    }
                    if (matcher.group(10) != null) {
                        flightLevelRecord.climbFuelUsage = Double.valueOf(matcher.group(10));
                    }

                    if (matcher.group(11) != null) {
                        flightLevelRecord.trueAirSpeed[BadaRecord.FlightStage.DESCENT.ordinal()] = Integer.valueOf(matcher.group(11));
                    }
                    if (matcher.group(12) != null) {
                        flightLevelRecord.descentRocd = Integer.valueOf(matcher.group(12));
                    }
                    if (matcher.group(13) != null) {
                        flightLevelRecord.descentFuelUsage = Double.valueOf(matcher.group(13));
                    }

                    flightLevelRecords.add(flightLevelRecord);
                } else {
                    /* 
                     * Check if unmatched line is a record separator line (if it
                     * is, ignore it)
                     */
                    matcher = textRecordSeparatorPattern.matcher(line);
                    if (!matcher.find()) {
                        /* 
                         * Check if unmatched line is a record separator line (if it
                         * is, ignore it)
                         */
                        matcher = separatorLinePattern.matcher(line);
                        if (!matcher.find()) {
                        	logger.error("BadaFileReader error: Error reading PTF file: Unexpected line in PTF file: \"" + line + "\"");
                        }
                    }
                }
            }

            // Ensure that the flightLevelRecords are sorted
            flightLevelRecords.trimToSize();
            this.setFlightLevelRecords(flightLevelRecords);
        } catch (Exception ex) {
            logger.error("BadaFileReader: Error reading PTF file: Exception while processing line \"" + line + "\"!", ex);
        }
        return;
    }

    /**
     * Writes a Bada data formatted text file.
     * 
     * @param writer
     */
    public void writeItem(PrintWriter writer) throws IOException {
        Integer line_length = 89;

        writer.println("BADA PERFORMANCE FILE                                        " + this.getFileDate()); 
        writer.println();
        writer.println("AC/Type: " + this.getAircraftType());
        writer.println("                              Source OPF File:               " + this.getSourceOpfDate());
        writer.println("                              Source APF File:               " + this.getSourceApfDate());
        writer.println();
        writer.println(" Speeds:   CAS(LO/HI)  Mach   Mass Levels [kg]         Temperature:  " + this.getTemperature());

        String casSpeedStr = String.format("%1$d/%2$d", this.casSpeed(FlightStage.ASCENT, Range.LOW), this.casSpeed(FlightStage.ASCENT, Range.HIGH));
        writer.format(" climb   - %1$-11s %2$-6f low     -  %3$-13d \n", casSpeedStr, this.machSpeed(FlightStage.ASCENT), this.massLevel(Range.LOW));

        casSpeedStr = String.format("%1$d/%2$d", this.casSpeed(FlightStage.CRUISE, Range.LOW), this.casSpeed(FlightStage.CRUISE, Range.HIGH));
        writer.format(" cruise  - %1$-11s %2$-6f nominal -  %3$-13d Max Alt. [ft]:  %4$d \n", casSpeedStr, this.machSpeed(FlightStage.CRUISE), this.massLevel(Range.NOMINAL), this.getMaxAltitude());

        casSpeedStr = String.format("%1$d/%2$d", this.casSpeed(FlightStage.DESCENT, Range.LOW), this.casSpeed(FlightStage.DESCENT, Range.HIGH));
        writer.format(" descent - %1$-11s %2$-6f high    -  %3$-13d \n", casSpeedStr, this.machSpeed(FlightStage.DESCENT), this.massLevel(Range.HIGH));

        for (int i = 0; i < line_length; i++)
            writer.print('=');

        writer.print('\n');
        writer.println(" FL |          CRUISE           |               CLIMB               |       DESCENT");
        writer.println("    |  TAS          fuel        |  TAS          ROCD         fuel   |  TAS  ROCD    fuel");
        writer.println("    | [kts]       [kg/min]      | [kts]        [fpm]       [kg/min] | [kts] [fpm] [kg/min]");
        writer.println("    |          lo   nom    hi   |         lo    nom    hi    nom    |        nom    nom");

        for (int i = 0; i < line_length; i++)
            writer.print('=');

        writer.print('\n');

        for (FlightLevelRecord record : this.getFlightLevelRecords()) {
            writer.format("%1$3s | %2$4s %3$7s %4$5s %5$5s  |  %6$4s %7$7s %8$5s %9$5s %10$7s  | %11$4s %12$6s %13$6s \n", record.altitude.flightLevel() == null ? "" : record.altitude.flightLevel(), record.trueAirSpeed(BadaRecord.FlightStage.CRUISE) == Integer.MIN_VALUE ? "" : record.trueAirSpeed(BadaRecord.FlightStage.CRUISE), Double.isNaN(record.fuelUsage(BadaRecord.FlightStage.CRUISE, BadaRecord.Range.LOW)) ? "" : record.fuelUsage(BadaRecord.FlightStage.CRUISE, BadaRecord.Range.LOW), Double.isNaN(record.fuelUsage(BadaRecord.FlightStage.CRUISE, BadaRecord.Range.NOMINAL)) ? "" : record.fuelUsage(BadaRecord.FlightStage.CRUISE, BadaRecord.Range.NOMINAL), Double.isNaN(record.fuelUsage(BadaRecord.FlightStage.CRUISE, BadaRecord.Range.HIGH)) ? "" : record.fuelUsage(BadaRecord.FlightStage.CRUISE, BadaRecord.Range.HIGH), record.trueAirSpeed(BadaRecord.FlightStage.ASCENT) == Integer.MIN_VALUE ? "" : record.trueAirSpeed(BadaRecord.FlightStage.ASCENT), record.rateofClimbDescent(BadaRecord.FlightStage.ASCENT, BadaRecord.Range.LOW) == Integer.MIN_VALUE ? "" : record.rateofClimbDescent(BadaRecord.FlightStage.ASCENT, BadaRecord.Range.LOW), record.rateofClimbDescent(BadaRecord.FlightStage.ASCENT, BadaRecord.Range.NOMINAL) == Integer.MIN_VALUE ? "" : record.rateofClimbDescent(BadaRecord.FlightStage.ASCENT, BadaRecord.Range.NOMINAL), record.rateofClimbDescent(BadaRecord.FlightStage.ASCENT, BadaRecord.Range.HIGH) == Integer.MIN_VALUE ? "" : record.rateofClimbDescent(BadaRecord.FlightStage.ASCENT, BadaRecord.Range.HIGH), Double.isNaN(record.fuelUsage(BadaRecord.FlightStage.ASCENT, BadaRecord.Range.NOMINAL)) ? "" : record.fuelUsage(BadaRecord.FlightStage.ASCENT, BadaRecord.Range.NOMINAL), record.trueAirSpeed(BadaRecord.FlightStage.DESCENT) == Integer.MIN_VALUE ? "" : record.trueAirSpeed(BadaRecord.FlightStage.DESCENT), record.rateofClimbDescent(BadaRecord.FlightStage.DESCENT, BadaRecord.Range.NOMINAL) == Integer.MIN_VALUE ? "" : record.rateofClimbDescent(BadaRecord.FlightStage.DESCENT, BadaRecord.Range.NOMINAL), Double.isNaN(record.fuelUsage(BadaRecord.FlightStage.DESCENT, BadaRecord.Range.NOMINAL)) ? "" : record.fuelUsage(BadaRecord.FlightStage.DESCENT, BadaRecord.Range.NOMINAL));
            writer.format("%1$3s | %2$4s %3$7s %4$5s %5$5s  |  %6$4s %7$7s %8$5s %9$5s %10$7s  | %11$4s %12$6s %13$6s \n", "", "", "", "", "", "", "", "", "", "", "", "", "");
        }

        for (int i = 0; i < line_length; i++)
            writer.print('=');

        writer.print('\n');
    }

    /**
     * Overrides BadaRecord method. Does nothing.
     * 
     * @param reader 
     */
    public long readHeader(BufferedReader reader) throws IOException {
        return -1;
    }

    /**
     * Overrides BadaRecord method. Does not write a header since raw Bada data
     * cannot be written to a single file. Does nothing.
     * 
     * @param writer
     */
    public void writeHeader(PrintWriter writer) throws IOException {

    }

    /**
     * Overrides BadaRecord method. Does nothing.
     * 
     * @param reader
     */
    public void readFooter(BufferedReader reader) throws IOException {

    }

    /**
     * Overrides BadaRecord method. Does not write a footer since raw Bada data
     * cannot be written to a single file. Does nothing.
     * 
     * @param writer
     */
    public void writeFooter(PrintWriter writer) throws IOException {

    }
}
