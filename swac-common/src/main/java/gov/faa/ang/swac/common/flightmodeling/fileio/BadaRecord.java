/**
 * Copyright "TBD", Metron Aviation & CSSI.  All rights reserved.
 * 
 * This computer Software was developed with the sponsorship of the U.S. Government
 * under Contract No. DTFAWA-10-D-00033, which has a copyright license in accordance with AMS 3.5-13.(c)(1).
 */

package gov.faa.ang.swac.common.flightmodeling.fileio;

import gov.faa.ang.swac.common.datatypes.Altitude;
import gov.faa.ang.swac.datalayer.storage.fileio.TextSerializable;
import gov.faa.ang.swac.datalayer.storage.fileio.WithFooter;
import gov.faa.ang.swac.datalayer.storage.fileio.WithHeader;

import com.mallardsoft.tuple.Triple;
import com.mallardsoft.tuple.Tuple;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * A data structure for reading and writing Bada data in XML format.
 * 
 * @author ssmitz
 * 
 */
public class BadaRecord implements TextSerializable, WithHeader, WithFooter, Serializable, Comparable<BadaRecord> {
    private static org.apache.log4j.Logger logger = org.apache.log4j.LogManager.getLogger(BadaRecord.class);
    private static String SCHEMA_FILE = "BADAData.xsd";

    private static final ThreadLocal<SimpleDateFormat> dateFormat1 = new ThreadLocal<SimpleDateFormat>() {
		   @Override
		   protected SimpleDateFormat initialValue() {
		    return new SimpleDateFormat("MMM dd yyyy");
		   }
	};
	
	private static final ThreadLocal<SimpleDateFormat> dateFormat2 = new ThreadLocal<SimpleDateFormat>() {
		   @Override
		   protected SimpleDateFormat initialValue() {
		    return new SimpleDateFormat("MMMM dd yyyy");
		   }
	};

	// maximum payload = High Mass - Empty Mass.
	private double maxPayload = Double.NaN;
	private double emptyMass = Double.NaN;
	
    public enum Range {
        LOW, NOMINAL, HIGH
    }

    public enum FlightStage {
        ASCENT, CRUISE, DESCENT
    }

    public static class FlightLevelRecord implements Comparable<FlightLevelRecord>, TextSerializable, Serializable, Cloneable {
        public Altitude altitude; // feet
        // [CLIMB, CRUISE, DESCENT] nmi/hr (knots)
        public int[] trueAirSpeed;
        // NOMINAL kg/min
        public double climbFuelUsage;
        // [LOW, NOMINAL, HIGH] kg/min
        public double[] cruiseFuelUsage;
        // NOMINAL kg/min
        public double descentFuelUsage;
        // [LOW, NOMINAL, HIGH] feet/min
        public int[] climbRocd;
        // NOMINAL feet/min
        public int descentRocd;

        public FlightLevelRecord() {
            this.altitude = null;
            this.trueAirSpeed = new int[] { Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE };
            this.climbFuelUsage = Double.NaN;
            this.cruiseFuelUsage = new double[] { Double.NaN,  Double.NaN,  Double.NaN };
            this.descentFuelUsage = Double.NaN;
            this.climbRocd = new int[] { Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE };
            this.descentRocd = Integer.MIN_VALUE;
        }
        
        public FlightLevelRecord(FlightLevelRecord org) {
            this.altitude = org.altitude;
            this.climbFuelUsage = org.climbFuelUsage;
            this.descentFuelUsage = org.descentFuelUsage;
            this.descentRocd = org.descentRocd;
            
            this.trueAirSpeed = new int[org.trueAirSpeed.length];
            
            for (int i = 0; i < org.trueAirSpeed.length; i++) {
                this.trueAirSpeed[i] = org.trueAirSpeed[i];
            }
            
            this.cruiseFuelUsage = new double[org.cruiseFuelUsage.length];
            
            for (int i = 0; i < org.cruiseFuelUsage.length; i++) {
                this.cruiseFuelUsage[i] = org.cruiseFuelUsage[i];
            }

            this.climbRocd = new int[org.climbRocd.length];
            
            for (int i = 0; i < org.climbRocd.length; i++) {
                this.climbRocd[i] = org.climbRocd[i];
            }
        }
        
        /**
         * Compare function for {@link FlightLevelRecord}. Ordered by altitude
         * (ascending).
         * 
         * @param b
         *            Object to compare to.
         * @return -1 if current {@link BadaFlightLevel} has a lower altitude
         *         than input, 0 if altitudes equal, 1 if current has a higher
         *         altitude.
         */
        public int compareTo(FlightLevelRecord b) {
            if (this.altitude.feet() < b.altitude.feet()) {
                return -1;
            } else if (this.altitude.feet() > b.altitude.feet()) {
                return 1;
            } else {
                logger.fatal("FlightLevelRecord.compareTo() error: FlightLevelRecords have same altitude!");
                throw new RuntimeException();

            }
        }

        /**
         * 
         * @param flightStage
         * @return true airspeed in nmi/hr or {@code Integer.MIN_VALUE} if no value is available.
         */
        public int trueAirSpeed(FlightStage flightStage) {
            return this.trueAirSpeed[flightStage.ordinal()];
        }

        /**
         * 
         * @param flightStage
         * @param range
         * @return fuel usage in kg/min for applicable flightStage and range. {@code Double.NaN} otherwise.
         */
        public double fuelUsage(FlightStage flightStage, Range range) {
            double fuelUsage = Double.NaN;
            switch (flightStage) {
                case ASCENT:
                    // fuel rate only applicable for NOMINAL
                    if (range == Range.NOMINAL) {
                        fuelUsage = this.climbFuelUsage;
                    }
                    break;
                case CRUISE:
                    fuelUsage = this.cruiseFuelUsage[range.ordinal()];
                    break;
                case DESCENT:
                    // fuel rate only applicable for NOMINAL
                    if (range == Range.NOMINAL) {
                        fuelUsage = this.descentFuelUsage;
                    }
                    break;
                default:
                    logger.info("FlightLevelRecord.fuelUsage() error: Unsupported FlightStage: \"" + flightStage.name() + "\" = " + flightStage.ordinal() + "!");
                    break;
            }

            return fuelUsage;
        }

        /**
         * 
         * @param flightStage
         * @param range
         * @return ROCD in feet/min for applicable flightStage and range.  {@code Integer.MIN_VALUE} otherwise.
         */
        public int rateofClimbDescent(FlightStage flightStage, Range range) {
            int rocd = Integer.MIN_VALUE;
            switch (flightStage) {
                case ASCENT:
                    rocd = this.climbRocd[range.ordinal()];
                    break;
                case CRUISE: 
                    // ROCD not applicable for CRUISE
                    break;
                case DESCENT:
                    // ROCD only applicable for NOMINAL
                    if (range == Range.NOMINAL) {
                        rocd = this.descentRocd;
                    }
                    break;
                default:
                    logger.info("FlightLevelRecord.rocd() error: Unsupported FlightStage: \"" + flightStage.name() + "\" = " + flightStage.ordinal() + "!");
                    break;
            }

            return rocd;
        }

        // ---------------------------------------------------------------------
        // TextSerializable Implementation
        // ---------------------------------------------------------------------
        @Override
        public void readItem(BufferedReader reader) throws IOException {
            String line = reader.readLine().trim();

            if (line.contains("<flight_level_record>"))
                line = reader.readLine().trim();

            if (line.contains("<flight_level>")) {
                this.altitude = Altitude.valueOfFeet(Integer.parseInt(line.substring(line.indexOf(">") + 1, line.lastIndexOf("<"))) * 100);
                line = reader.readLine().trim();
            }

            if (line.contains("<cruise>")) {
                line = reader.readLine().trim();
                this.trueAirSpeed[BadaRecord.FlightStage.CRUISE.ordinal()] = Integer.decode(line.substring(line.indexOf(">") + 1, line.lastIndexOf("<")));
                line = reader.readLine().trim();
                line = reader.readLine().trim();

                this.cruiseFuelUsage[BadaRecord.Range.LOW.ordinal()] = Double.valueOf(line.substring(line.indexOf(">") + 1, line.lastIndexOf("<")));
                line = reader.readLine().trim();

                this.cruiseFuelUsage[BadaRecord.Range.NOMINAL.ordinal()] = Double.valueOf(line.substring(line.indexOf(">") + 1, line.lastIndexOf("<")));
                line = reader.readLine().trim();

                this.cruiseFuelUsage[BadaRecord.Range.HIGH.ordinal()] = Double.valueOf(line.substring(line.indexOf(">") + 1, line.lastIndexOf("<")));
                line = reader.readLine().trim();
                line = reader.readLine().trim();
                line = reader.readLine().trim();
            }
            if (line.contains("<climb>")) {
                line = reader.readLine().trim();
                this.trueAirSpeed[BadaRecord.FlightStage.ASCENT.ordinal()] = Integer.decode(line.substring(line.indexOf(">") + 1, line.lastIndexOf("<")));
                line = reader.readLine().trim();
                line = reader.readLine().trim();

                this.climbRocd[BadaRecord.Range.LOW.ordinal()] = Integer.decode(line.substring(line.indexOf(">") + 1, line.lastIndexOf("<")));
                line = reader.readLine().trim();

                this.climbRocd[BadaRecord.Range.NOMINAL.ordinal()] = Integer.decode(line.substring(line.indexOf(">") + 1, line.lastIndexOf("<")));
                line = reader.readLine().trim();

                this.climbRocd[BadaRecord.Range.HIGH.ordinal()] = Integer.decode(line.substring(line.indexOf(">") + 1, line.lastIndexOf("<")));
                line = reader.readLine().trim();
                line = reader.readLine().trim();
                line = reader.readLine().trim();

                this.climbFuelUsage = Double.valueOf(line.substring(line.indexOf(">") + 1, line.lastIndexOf("<")));
                line = reader.readLine().trim();
                line = reader.readLine().trim();
                line = reader.readLine().trim();
            }
            if (line.contains("<descent>")) {
                line = reader.readLine().trim();
                this.trueAirSpeed[BadaRecord.FlightStage.DESCENT.ordinal()] = Integer.decode(line.substring(line.indexOf(">") + 1, line.lastIndexOf("<")));
                line = reader.readLine().trim();
                line = reader.readLine().trim();

                this.descentRocd = Integer.decode(line.substring(line.indexOf(">") + 1, line.lastIndexOf("<")));
                line = reader.readLine().trim();
                line = reader.readLine().trim();
                line = reader.readLine().trim();

                this.descentFuelUsage = Double.valueOf(line.substring(line.indexOf(">") + 1, line.lastIndexOf("<")));
            }
        }

        @Override
        public void writeItem(PrintWriter writer) throws IOException {
            writer.println("\t\t\t<flight_level_record>");
            writer.println("\t\t\t\t<flight_level>" + altitude.flightLevel() + "</flight_level>");

            if (this.trueAirSpeed[1] != Integer.MIN_VALUE) {
                writer.println("\t\t\t\t<cruise>");
                writer.println("\t\t\t\t\t<true_airspeed unit_of_measure=\"kts\">" + trueAirSpeed[1] + "</true_airspeed>");
                writer.println("\t\t\t\t\t<fuel unit_of_measure=\"kg/min\">");
                writer.println("\t\t\t\t\t\t<low>" + this.cruiseFuelUsage[0] + "</low>");
                writer.println("\t\t\t\t\t\t<nominal>" + this.cruiseFuelUsage[1] + "</nominal>");
                writer.println("\t\t\t\t\t\t<high>" + this.cruiseFuelUsage[2] + "</high>");
                writer.println("\t\t\t\t\t</fuel>");
                writer.println("\t\t\t\t</cruise>");
            }
            writer.println("\t\t\t\t<climb>");
            writer.println("\t\t\t\t\t<true_airspeed unit_of_measure=\"kts\">" + trueAirSpeed[0] + "</true_airspeed>");
            writer.println("\t\t\t\t\t<rate_of_climb_descent unit_of_measure=\"fpm\">");
            writer.println("\t\t\t\t\t\t<low>" + this.climbRocd[0] + "</low>");
            writer.println("\t\t\t\t\t\t<nominal>" + this.climbRocd[1] + "</nominal>");
            writer.println("\t\t\t\t\t\t<high>" + this.climbRocd[2] + "</high>");
            writer.println("\t\t\t\t\t</rate_of_climb_descent>");
            writer.println("\t\t\t\t\t<fuel unit_of_measure=\"kg/min\">");
            writer.println("\t\t\t\t\t\t<nominal>" + this.climbFuelUsage + "</nominal>");
            writer.println("\t\t\t\t\t</fuel>");
            writer.println("\t\t\t\t</climb>");
            writer.println("\t\t\t\t<descent>");
            writer.println("\t\t\t\t\t<true_airspeed unit_of_measure=\"kts\">" + trueAirSpeed[2] + "</true_airspeed>");
            writer.println("\t\t\t\t\t<rate_of_climb_descent unit_of_measure=\"fpm\">");
            writer.println("\t\t\t\t\t\t<nominal>" + this.descentRocd + "</nominal>");
            writer.println("\t\t\t\t\t</rate_of_climb_descent>");
            writer.println("\t\t\t\t\t<fuel unit_of_measure=\"kg/min\">");
            writer.println("\t\t\t\t\t\t<nominal>" + this.descentFuelUsage + "</nominal>");
            writer.println("\t\t\t\t\t</fuel>");
            writer.println("\t\t\t\t</descent>");
            writer.println("\t\t\t</flight_level_record>");
        }
        
        @Override
        public FlightLevelRecord clone() {
            return new FlightLevelRecord(this);
        }
    }

    private String aircraftType = null;
    private Date fileDate = null;
    private Date sourceOpfDate = null;
    private Date sourceApfDate = null;
    private String temperature = null;
    // LOW, NOMINAL, HIGH
    private int massLevel[] = {Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE };
    // CLIMB, CRUISE, DESCENT
    private double machSpeed[] = { Double.NaN, Double.NaN, Double.NaN };
    // [CLIMB, CRUISE, DESCENT][LOW, NOMINAL, HIGH] (NOMINAL unused)
    private int casSpeed[][] = { { Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE }, { Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE }, { Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE } };
    private int maxAltitude = Integer.MIN_VALUE;
    private List<FlightLevelRecord> flightLevelRecords = null;

    /**
     * Default constructor
     */
    public BadaRecord() {
    }

    /**
     * Constructor used to convert {@link RawBadaRecord} so several text files 
     * can be converted to a single XML file.
     * 
     * @param record the {@link RawBadaRecord} to promote.
     */
    public BadaRecord(RawBadaRecord record) {
        this.aircraftType = record.getAircraftType();
        this.setFileDate(record.getFileDate());
        this.setSourceOpfDate(record.getSourceOpfDate());
        this.setSourceApfDate(record.getSourceApfDate());
        this.temperature = record.getTemperature();
        this.massLevel = record.getMassLevel();
        this.machSpeed = record.getMachSpeed();
        this.casSpeed = record.getCasSpeed();
        this.maxAltitude = record.getMaxAltitude();
        this.flightLevelRecords = record.getFlightLevelRecords();
    }

    public BadaRecord(BadaRecord org) {
    	throw new UnsupportedOperationException("BADA Records should not be cloned.");
    }
    
    public String getAircraftType() {
        return this.aircraftType;
    }

    public void setAircraftType(String type) {
        this.aircraftType = type;
    }

    public String getFileDate() {
        String date;

        if (fileDate == null)
            date = "";
        else
            date = dateFormat1.get().format(fileDate);

        return date;
    }

    public void setFileDate(Date date) {
        this.fileDate = date;
    }
    
    public void setFileDate(String date) {
        this.fileDate = parseDate(date);
    }

    public String getSourceOpfDate() {
        String date;

        if (sourceOpfDate == null)
            date = "";
        else
            date = dateFormat1.get().format(sourceOpfDate);

        return date;
    }
    
    public void setSourceOpfDate(Date date) {
        this.sourceOpfDate = date;
    }

    public void setSourceOpfDate(String date) {
        this.sourceOpfDate = parseDate(date);
    }

    public String getSourceApfDate() {
        String date;

        if (sourceApfDate == null)
            date = "";
        else
            date = dateFormat1.get().format(sourceApfDate);

        return date;
    }
    
    public void setSourceApfDate(Date date) {
        this.sourceApfDate = date;
    }

    public void setSourceApfDate(String date) {
        this.sourceApfDate = parseDate(date);
    }
    
    private Date parseDate(String date) {
        Date rtn = null;
        logger.debug("Parsing date: " + date);
        try {
            rtn = dateFormat1.get().parse(date);
            logger.debug("Parse succeded");
        } catch (ParseException ignore) {
            logger.debug("Malformed date...");
            try {
                rtn = dateFormat2.get().parse(date);
                logger.debug("Parse succeded");
            } catch (ParseException pe) {
                logger.debug(pe.getStackTrace());
            }
        }
        return rtn;
    }

    public String getTemperature() {
        return this.temperature;
    }

    public void setTemperature(String temp) {
        this.temperature = temp;
    }

    public int[] getMassLevel() {
        return massLevel.clone();
    }

    public void setMassLevel(int[] mass) {
        this.massLevel = mass.clone();
    }

    public double[] getMachSpeed() {
        return machSpeed.clone();
    }

    public void setMachSpeed(double[] mach) {
        this.machSpeed = mach.clone();
    }

    public int[][] getCasSpeed() {
        return casSpeed.clone();
    }

    public void setCasSpeed(int[][] speed) {
        this.casSpeed = speed.clone();
    }

    /**
     * Returns the maximum altitude associated with the BADA record file. 
     * @return
     */
    public int getMaxAltitude() {
        return maxAltitude;
    }

    public void setMaxAltitude(int alt) {
        this.maxAltitude = alt;
    }

    public List<FlightLevelRecord> getFlightLevelRecords() {
        return flightLevelRecords;
    }

    public void setFlightLevelRecords(List<FlightLevelRecord> flr) {
        this.flightLevelRecords = flr;
        Collections.sort(this.flightLevelRecords);
    }

    /**
     * Returns the altitude associated with the highest
     * {@link FlightLevelRecord}.<br>
     * NOTE: This is not necessarily equal to the "Max Alt." listed in the BADA
     * file header.
     * 
     * @return
     */
    public Altitude maxAltitude() {
        if (this.flightLevelRecords != null) {
            return this.flightLevelRecords.get(this.flightLevelRecords.size() - 1).altitude;
        }

        return null;
    }

    /**
     * Returns the altitude at which nominal cruise fuel efficiency (kg/nm) is
     * optimal.<br>
     * 
     * @return
     */
    public Altitude cruiseAltitude() {
        if (this.flightLevelRecords != null) {
            Altitude alt = null;
            double fuel;
            int tas;
            double tempEfficiency;
            double efficiency = 0.0;

            for (FlightLevelRecord flr : this.flightLevelRecords) {
                fuel = flr.cruiseFuelUsage[Range.NOMINAL.ordinal()];
                tas = flr.trueAirSpeed[FlightStage.CRUISE.ordinal()];
                if (!Double.isNaN(fuel) && tas != Integer.MIN_VALUE) {
                    tempEfficiency = tas / (60 * fuel);
                    if (tempEfficiency > efficiency) {
                        efficiency = tempEfficiency;
                        alt = flr.altitude;
                    }
                }
            }
            return alt;
        }

        return null;
    }

    /**
     * Returns the maximum cruise altitude achievable based on the highest
     * flight level with a non-zero rate of climb/descent for the given
     * {@link Range}.<br>
     * NOTE:<br>
     * - Not necessarily equal to the "Max Alt." listed in the BADA file header.<br>
     * - Not to be confused with {@link #maxAltitude()}
     * 
     * @return
     */
    public Altitude maxCruiseAltitude(Range range) {
        for (int i = this.flightLevelRecords.size() - 1; i >= 0; i--) {
            if (this.flightLevelRecords.get(i).rateofClimbDescent(FlightStage.ASCENT, range) > 0) {
                return this.flightLevelRecords.get(i).altitude;
            }
        }

        return null;
    }

    public int massLevel(Range range) {
        return this.massLevel[range.ordinal()];
    }

    public double machSpeed(FlightStage flightStage) {
        return this.machSpeed[flightStage.ordinal()];
    }

    public int casSpeed(FlightStage flightStage, Range range) {
        return this.casSpeed[flightStage.ordinal()][range.ordinal()];
    }

    /**
     * Returns the FlightLevelRecords for the altitudes above and below
     * {@link altitude}.
     * <p>
     * {@link FlightLevelRecord}s are given as discrete altitudes. This method
     * fetches the {@link FlightLevelRecord}s that bound {@link altitude} so
     * they may be used to interpolate FlightLevel data for any altitude. Via
     * the following logic:
     * <p>
     * If {@link altitude} is below all {@link FlightLevelRecord}s, then the
     * records below and above will be the same (the lowest
     * {@link FlightLevelRecord}), and percentage will be 1.0. <br>
     * If {@link altitude} is above all {@link FlightLevelRecord}s, then the
     * records below and above will be the same (the highest
     * {@link FlightLevelRecord}), and percentage will be 1.0. <br>
     * If {@link altitude} is between any two {@link FlightLevelRecord}s, then
     * the records below and above will differ, and percentage will be between
     * 0.0 and 1.0. <br>
     * 
     * @return {@link Triple}<{@link FlightLevelRecord} below,
     *         {@link FlightLevelRecord} above, {@link Double} altitude's
     *         percentage between>
     */

    public Triple<FlightLevelRecord, FlightLevelRecord, Double> getFlightLevelRecords(Altitude altitude) {
        int indexBelow = 0; // Set to lowest index
        int indexAbove = this.flightLevelRecords.size() - 1; // Set to highest
        // index

        for (int i = 0; i < this.flightLevelRecords.size(); i++) {
            if (this.flightLevelRecords.get(i).altitude.feet() > altitude.feet()) {
                indexAbove = i;
                break;
            }

            indexBelow = i;
        }

        double percentage = 1.0;
        if (indexBelow != indexAbove) // protect against division by zero
        {
            percentage = (altitude.feet() - this.flightLevelRecords.get(indexBelow).altitude.feet()) / (this.flightLevelRecords.get(indexAbove).altitude.feet() - this.flightLevelRecords.get(indexBelow).altitude.feet());
        }

        return new Triple<FlightLevelRecord, FlightLevelRecord, Double>(this.flightLevelRecords.get(indexBelow), this.flightLevelRecords.get(indexAbove), percentage);
    }

    /**
     * Aircraft's true air speed.
     * 
     * @param altitude
     * @param {@link FlightStage}
     * @return true air speed (in knots) or {@code Double.NaN} if value not applicable
     *         for altitude/flightStage
     */
    public double trueAirSpeed(Altitude altitude, FlightStage flightStage) {
        Triple<FlightLevelRecord, FlightLevelRecord, Double> boundingRecords = getFlightLevelRecords(altitude);

        int tasBelow = Tuple.first(boundingRecords).trueAirSpeed(flightStage);
        int tasAbove = Tuple.second(boundingRecords).trueAirSpeed(flightStage);
        double percentage = Tuple.third(boundingRecords);

        if (tasBelow != Integer.MIN_VALUE && tasAbove != Integer.MIN_VALUE) {
            return tasBelow + percentage * (tasAbove - tasBelow);
        }

        return Double.NaN;
    }

    /**
     * Aircraft's fuel usage rate.
     * 
     * @param altitude
     * @param {@link FlightStage}
     * @param {@link Range}
     * @return fuel usage rate (in kg/min) or {@code Double.NaN} if value not
     *         applicable for altitude/flightStage/range
     */
    public double fuelUsageRate(Altitude altitude, FlightStage flightStage, Range range) {
        Triple<FlightLevelRecord, FlightLevelRecord, Double> boundingRecords = getFlightLevelRecords(altitude);

        double fuelRateBelow = Tuple.first(boundingRecords).fuelUsage(flightStage, range);
        double fuelRateAbove = Tuple.second(boundingRecords).fuelUsage(flightStage, range);
        double percentage = Tuple.third(boundingRecords);

        if (!Double.isNaN(fuelRateBelow) && !Double.isNaN(fuelRateAbove)) {
            return fuelRateBelow + percentage * (fuelRateAbove - fuelRateBelow);
        }

        return Double.NaN;
    }

    /**
     * Aircraft's rate of climb/descent.
     * 
     * @param altitude
     * @param {@link FlightStage}
     * @param {@link Range}
     * @return rate of climb/descent (in ft/min) or 0.0 if value not
     *         applicable for altitude/flightStage/range
     */
    public double rateofClimbDescent(Altitude altitude, FlightStage flightStage, Range range) {
        Triple<FlightLevelRecord, FlightLevelRecord, Double> boundingRecords = getFlightLevelRecords(altitude);

        int rocdBelow = Tuple.first(boundingRecords).rateofClimbDescent(flightStage, range);
        int rocdAbove = Tuple.second(boundingRecords).rateofClimbDescent(flightStage, range);
        double percentage = Tuple.third(boundingRecords);

        if (rocdBelow != Integer.MIN_VALUE && rocdAbove != Integer.MIN_VALUE) {
            return rocdBelow + percentage * (rocdAbove - rocdBelow);
        }

        return 0.0;
    }

    /**
     * empty = low mass / 1.2
     * 
     * @return
     */
    public double emptyMass(){
    	return this.emptyMass;
    }
    
    /**
     * max payload = high mass - empty mass.
     * @return
     */
    public double maxPayload(){
    	return this.maxPayload;
    }
    
    /**
     * Parses an XML formatted file into a BadaRecord object.
     */
    @Override
    public void readItem(BufferedReader reader) throws IOException {
        String line = reader.readLine().trim();

        while (reader.ready() && !line.contains("<bada_record>"))
            line = reader.readLine().trim();

        if (line.contains("<bada_record>")) {
            line = reader.readLine().trim();

            if (line.contains("<bada_header>")) {
                line = reader.readLine().trim();

                try {
                    if (line.contains("<bada_date>")) {
                        this.fileDate = dateFormat1.get().parse(line.substring(line.indexOf(">") + 1, line.lastIndexOf("<")));
                        line = reader.readLine().trim();
                    }
                } catch (ParseException pe) {
                    logger.warn("Malformed date in " + this.aircraftType + " PTF file. Received: " + line);
                    line = reader.readLine().trim();
                }

                if (line.contains("<aircraft_type>")) {
                    this.aircraftType = line.substring(line.indexOf(">") + 1, line.lastIndexOf("<"));
                    line = reader.readLine().trim();
                }

                try {
                    if (line.contains("<src_opf_date>")) {
                        this.sourceOpfDate = dateFormat1.get().parse(line.substring(line.indexOf(">") + 1, line.lastIndexOf("<")));
                        line = reader.readLine().trim();
                    }
                } catch (ParseException pe) {
                    logger.warn("Malformed date in " + this.aircraftType + " PTF file. Received: " + line);
                    line = reader.readLine().trim();
                }
                try {
                    if (line.contains("<src_apf_date>")) {
                        this.sourceApfDate = dateFormat1.get().parse(line.substring(line.indexOf(">") + 1, line.lastIndexOf("<")));
                        line = reader.readLine().trim();
                    }
                } catch (ParseException pe) {
                    logger.warn("Malformed date in " + this.aircraftType + " PTF file. Received: " + line);
                    line = reader.readLine().trim();
                }
                if (line.contains("<cas_speeds>")) {
                    line = reader.readLine().trim();
                    line = reader.readLine().trim();
                    line = reader.readLine().trim();

                    this.casSpeed[BadaRecord.FlightStage.ASCENT.ordinal()][BadaRecord.Range.LOW.ordinal()] = Integer.decode(line.substring(line.indexOf(">") + 1, line.lastIndexOf("<")));
                    line = reader.readLine().trim();

                    this.casSpeed[BadaRecord.FlightStage.ASCENT.ordinal()][BadaRecord.Range.HIGH.ordinal()] = Integer.decode(line.substring(line.indexOf(">") + 1, line.lastIndexOf("<")));
                    line = reader.readLine().trim();
                    line = reader.readLine().trim();

                    this.machSpeed[BadaRecord.FlightStage.ASCENT.ordinal()] = Double.valueOf(line.substring(line.indexOf(">") + 1, line.lastIndexOf("<")));
                    line = reader.readLine().trim();
                    line = reader.readLine().trim();
                    line = reader.readLine().trim();
                    line = reader.readLine().trim();

                    this.casSpeed[BadaRecord.FlightStage.CRUISE.ordinal()][BadaRecord.Range.LOW.ordinal()] = Integer.decode(line.substring(line.indexOf(">") + 1, line.lastIndexOf("<")));
                    line = reader.readLine().trim();

                    this.casSpeed[BadaRecord.FlightStage.CRUISE.ordinal()][BadaRecord.Range.HIGH.ordinal()] = Integer.decode(line.substring(line.indexOf(">") + 1, line.lastIndexOf("<")));
                    line = reader.readLine().trim();
                    line = reader.readLine().trim();

                    this.machSpeed[BadaRecord.FlightStage.CRUISE.ordinal()] = Double.valueOf(line.substring(line.indexOf(">") + 1, line.lastIndexOf("<")));
                    line = reader.readLine().trim();
                    line = reader.readLine().trim();
                    line = reader.readLine().trim();
                    line = reader.readLine().trim();

                    this.casSpeed[BadaRecord.FlightStage.DESCENT.ordinal()][BadaRecord.Range.LOW.ordinal()] = Integer.decode(line.substring(line.indexOf(">") + 1, line.lastIndexOf("<")));
                    line = reader.readLine().trim();

                    this.casSpeed[BadaRecord.FlightStage.DESCENT.ordinal()][BadaRecord.Range.HIGH.ordinal()] = Integer.decode(line.substring(line.indexOf(">") + 1, line.lastIndexOf("<")));
                    line = reader.readLine().trim();
                    line = reader.readLine().trim();

                    this.machSpeed[BadaRecord.FlightStage.DESCENT.ordinal()] = Double.valueOf(line.substring(line.indexOf(">") + 1, line.lastIndexOf("<")));
                    line = reader.readLine().trim();
                    line = reader.readLine().trim();
                    line = reader.readLine().trim();
                }
                if (line.contains("<mass_levels>")) {
                    line = reader.readLine().trim();
                    this.massLevel[BadaRecord.Range.LOW.ordinal()] = Integer.decode(line.substring(line.indexOf(">") + 1, line.lastIndexOf("<")));

                    line = reader.readLine().trim();
                    this.massLevel[BadaRecord.Range.NOMINAL.ordinal()] = Integer.decode(line.substring(line.indexOf(">") + 1, line.lastIndexOf("<")));

                    line = reader.readLine().trim();
                    this.massLevel[BadaRecord.Range.HIGH.ordinal()] = Integer.decode(line.substring(line.indexOf(">") + 1, line.lastIndexOf("<")));
                }
                line = reader.readLine().trim();
                line = reader.readLine().trim();
                this.temperature = line.substring(line.indexOf(">") + 1, line.lastIndexOf("<"));

                line = reader.readLine().trim();
                this.maxAltitude = Integer.decode(line.substring(line.indexOf(">") + 1, line.lastIndexOf("<")));

                while (!line.contains("<data>"))
                    line = reader.readLine().trim();
            }
            if (this.flightLevelRecords == null) {
                this.flightLevelRecords = new ArrayList<FlightLevelRecord>();
            }

            while (!line.contains("</data>")) {
                while (!line.contains("<flight_level_record>") && !line.contains("</data>"))
                    line = reader.readLine().trim();

                if (line.contains("<flight_level_record>")) {
                    FlightLevelRecord tmp = new FlightLevelRecord();
                    tmp.readItem(reader);
                    this.flightLevelRecords.add(tmp);
                    line = reader.readLine().trim();
                }
            }
        }
        
        this.emptyMass = this.massLevel[0] / 1.2;
        this.maxPayload = this.massLevel[2] - this.emptyMass;
    }

    /**
     * Writes a BadaRecord object out to an XML formatted file. Multiple 
     * BadaRecords can be stored in a single file.
     */
    @Override
    public void writeItem(PrintWriter writer) throws IOException {
        writer.println("\t<bada_record>");
        writer.println("\t\t<bada_header>");
        writer.println("\t\t\t<bada_date>" + this.getFileDate() + "</bada_date>");
        writer.println("\t\t\t<aircraft_type>" + this.aircraftType + "</aircraft_type>");
        writer.println("\t\t\t<src_opf_date>" + this.getSourceOpfDate() + "</src_opf_date>");
        writer.println("\t\t\t<src_apf_date>" + this.getSourceApfDate() + "</src_apf_date>");
        writer.println("\t\t\t<cas_speeds>");
        writer.println("\t\t\t\t<climb>");
        writer.println("\t\t\t\t\t<calibrated_airspeed>");
        writer.println("\t\t\t\t\t\t<low>" + this.casSpeed[0][0] + "</low>");
        writer.println("\t\t\t\t\t\t<high>" + this.casSpeed[0][2] + "</high>");
        writer.println("\t\t\t\t\t</calibrated_airspeed>");
        writer.println("\t\t\t\t\t<mach>" + this.machSpeed[0] + "</mach>");
        writer.println("\t\t\t\t</climb>");
        writer.println("\t\t\t\t<cruise>");
        writer.println("\t\t\t\t\t<calibrated_airspeed>");
        writer.println("\t\t\t\t\t\t<low>" + this.casSpeed[1][0] + "</low>");
        writer.println("\t\t\t\t\t\t<high>" + this.casSpeed[1][2] + "</high>");
        writer.println("\t\t\t\t\t</calibrated_airspeed>");
        writer.println("\t\t\t\t\t<mach>" + this.machSpeed[1] + "</mach>");
        writer.println("\t\t\t\t</cruise>");
        writer.println("\t\t\t\t<descent>");
        writer.println("\t\t\t\t<calibrated_airspeed>");
        writer.println("\t\t\t\t<low>" + this.casSpeed[2][0] + "</low>");
        writer.println("\t\t\t\t<high>" + this.casSpeed[2][2] + "</high>");
        writer.println("\t\t\t\t</calibrated_airspeed>");
        writer.println("\t\t\t\t<mach>" + this.machSpeed[2] + "</mach>");
        writer.println("\t\t\t\t</descent>");
        writer.println("\t\t\t</cas_speeds>");
        writer.println("\t\t\t<mass_levels>");
        writer.println("\t\t\t\t<low>" + this.massLevel[0] + "</low>");
        writer.println("\t\t\t\t<nominal>" + this.massLevel[1] + "</nominal>");
        writer.println("\t\t\t\t<high>" + this.massLevel[2] + "</high>");
        writer.println("\t\t\t</mass_levels>");
        writer.println("\t\t\t<temperature>" + this.temperature + "</temperature>");
        writer.println("\t\t\t<maximum_altitude>" + this.maxAltitude + "</maximum_altitude>");
        writer.println("\t\t</bada_header>");
        writer.println("\t\t<data>");

        for (FlightLevelRecord flr : this.flightLevelRecords) {
            flr.writeItem(writer);
        }

        writer.println("\t\t</data>");
        writer.println("\t</bada_record>");
    }

    /**
     * Parses the number of BadaRecords contained in the file (Used by the data
     * layer API). 
     */
    @Override
    public long readHeader(BufferedReader reader) throws IOException {
        String line = reader.readLine().trim();
        line = reader.readLine().trim();
        String tmp = line.substring(line.indexOf("number_of_records=\"") + "number_of_records=\"".length());

        return Long.decode(tmp.substring(0, tmp.indexOf('"')));
    }

    /**
     * Writes the header for the Bada data XML file.
     */
    @Override
    public void writeHeader(PrintWriter writer, long numRecords) throws IOException {
        URL schema = this.getClass().getResource(SCHEMA_FILE);

        writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        if (schema != null)
            writer.println("<bada_records number_of_records=\"" + numRecords + "\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"" + schema.getFile() + "\">");
        else
            writer.println("<bada_records number_of_records=\"" + numRecords + "\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">");

    }

    /**
     * Does nothing. 
     */
    @Override
    public void readFooter(BufferedReader reader) throws IOException {
    }

    /**
     * Writes the closing tag to the XML file. 
     */
    @Override
    public void writeFooter(PrintWriter writer) throws IOException {
        writer.println("</bada_records>");
    }
    
	@Override
	public int compareTo(BadaRecord o) {
		return this.aircraftType.compareTo(o.aircraftType);
	}
}