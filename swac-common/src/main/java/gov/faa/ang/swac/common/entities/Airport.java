package gov.faa.ang.swac.common.entities;

import gov.faa.ang.swac.common.datatypes.Latitude;
import gov.faa.ang.swac.common.datatypes.Longitude;
import gov.faa.ang.swac.common.datatypes.Angle.Units;
import gov.faa.ang.swac.common.datatypes.Timestamp;
import gov.faa.ang.swac.datalayer.storage.fileio.TextSerializable;
import gov.faa.ang.swac.datalayer.storage.fileio.WithHeader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;

public class Airport implements TextSerializable, WithHeader, Comparable<Airport> {
	private static final int infiniteCapacity = 999999;
	private static final long DEFAULT_ARR_Q_THRESHOLD = 2700000; // 2700000 millis = 45 minutes
	
    private enum TimeZone {

        GMT(0), UTC(0), WET(0),
        BST(1), CET(1), DFT(1), IrST(1), WAT(1), WEDT(1), WEST(1),
        CAT(2), CEDT(2), CEST(2), EET(2), IST(2), SAST(2),
        AbST(3), EAT(3), EEDT(3), EEST(3), MSK(3), IRST(3.5),
        AMT(4), AbnST(4), AZT(4), GET(4), MSD(4), MUT(4), RET(4), SAMT(4), SCT(4), AFT(4.5),
        AMST(5), HMT(5), PKT(5), YEKT(5), InST(5.5), SLT(5.5), NPT(5.75),
        BIOT(6), BaST(6), BTT(6), OMST(6), CCT(6.5), MyST(6.5),
        CXT(7), ICT(7), KRAT(7), THA(7),
        ACT(8), AWST(8), BDT(8), CnST(8), HKT(8), IRKT(8), MaST(8), PhST(8), SST(8),
        AWDT(9), JST(9), KST(9), YAKT(9), ACST(9.5),
        AEST(10), ChST(10), VLAT(10), ACDT(10.5), LHST(10.5),
        AEDT(11), MAGT(11), SBT(11), NFT(11.5),
        FJT(12), GILT(12), NZST(12), PETT(12), CHAST(12.75),
        NZDT(13), PHOT(13), CHADT(13.75),
        LINT(14),
        AZOST(-1), CVT(-1),
        GST(-2), UYST(-2), NDT(-2.5),
        ADT(-3), ART(-3), BRT(-3), CLST(-3), FKST(-3), GFT(-3), UYT(-3), NST(3.5), NT(-3.5),
        AST(-4), BOT(-4), CLT(-4), COST(-4), ECT(-4), FKT(-4), GYT(-4), VET(-4.5),
        COT(-5), EqCT(-5), EST(-5), ET(-5),
        CST(-6), EAST(-6), GALT(-6), CT(-6),
        MST(-7), MT(-7),
        CIST(-8), PST(-8), PT(-8),
        AKST(-9), GIT(-9), AT(-9), MIT(-9.5),
        CKT(-10), HAST(-10), HST(-10), TAHT(-10), HT(-10),
        SaST(-11),
        BIT(-12),;
        private final double utcOffset;

        TimeZone(double offset) {
            this.utcOffset = offset;
        }

        double getUtcOffset() {
            return utcOffset;
        }
    }
    public static Double DEFAULT_TAXI_OUT_TIME = null;
    public static Double DEFAULT_TAXI_IN_TIME = null;
    public static Double DEFAULT_TAXI_OUT_DELAY = null;
    public static Double DEFAULT_TAXI_IN_DELAY = null;
    public static Double DEFAULT_DEPARTURE_QUEUE_THRESHOLD = null;
    /*
     * Instance variables...
     */
    private String name;            // FAA abbreviated name of the airport.
    private Boolean modeled;        // Whether the airport should be modeled by the simulator.
    private String state;           // The state (or country) the airport is in.
    private Boolean major;            // Whether the airport is an OEP airport.
    private Latitude latitude;      // The approximate center latitude of the airport.
    private Longitude longitude;    // The approximate center longitude of the airport.
    private Integer numRunways;     // The number of runways available at this airport.
    private TimeZone timeZone;      // The time zone the airport resides in.
    private boolean dst;            // Whether daylight savings time is observed. 
    private Double taxiOutTime;     // The mean taxi out time in minutes.
    private Double taxiInTime;      // The mean taxi in time in minutes.
    private Double taxiOutDelay;     // The mean taxi out delay in minutes.
    private Double taxiInDelay;      // The mean taxi in delay in minutes.
    private Double eteCorrection;   // The mean estimated time enroute correction in minutes.
    private boolean enableDepartureQueueThreshold;	// Whether departure queue throttling is enabled at the airport.
    private Double departureQueueThreshold;		// The departure queue length threshold before flights are held at the gate.
    private boolean enableRerouteClearance;
    private Double rerouteClearanceCapacity;	// Resource clearance handling resource capacity.
	private int defaultRampCapacity;  // Default ramp capacity
	private boolean enableArrivalQueueModeling;  // What arrival queue mgmt is enabled for this airport
	private long maxArrFixQueueWaitTimeMs; // Maximum arrival fix queue waiting time
	private long lastCapResourceTransitTimeThresholdMs; // Max transit time between a resource and arr aprt to be eligible as last capacitated resource


    /*
     * Constructors
     */
    /**
     * This should only be used by the Data Access Layer!
     */
    public Airport() {
        this.name = null;
        this.modeled = false;
        this.state = null;
        this.major = false;
        this.latitude = null;
        this.longitude = null;
        this.numRunways = null;
        this.timeZone = null;
        this.dst = false;
        this.taxiOutTime = null;
        this.taxiInTime = null;
        this.taxiOutDelay = null;
        this.taxiInDelay = null;
        this.eteCorrection = null;
        this.enableDepartureQueueThreshold = false;
        this.departureQueueThreshold = null;
        this.enableRerouteClearance = false;
        this.rerouteClearanceCapacity = null;
        this.defaultRampCapacity = infiniteCapacity;
        this.enableArrivalQueueModeling = false;
    }

    /**
     * @param name The FAA abbreviated name of the airport.
     *
     * @throws IllegalArgumentException if {@code name} is {@code null} or an
     * empty
     * {@link java.lang.String String} after whitespace has been removed.
     */
    public Airport(String name) throws IllegalArgumentException {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Attemted to create unnamed Airport!");
        } else {
            this.name = name;
            this.modeled = false;
            this.state = null;
            this.major = false;
            this.latitude = null;
            this.longitude = null;
            this.numRunways = null;
            this.timeZone = null;
            this.dst = false;
            this.taxiOutTime = null;
            this.taxiInTime = null;
            this.taxiOutDelay = null;
            this.taxiInDelay = null;
            this.eteCorrection = null;
            this.enableDepartureQueueThreshold = false;
            this.departureQueueThreshold = null;
            this.enableRerouteClearance = false;
            this.rerouteClearanceCapacity = null;
            this.defaultRampCapacity = infiniteCapacity;
            this.enableArrivalQueueModeling = false;
        }
    }

    /**
     * @param name The FAA abbreviated name of the airport.
     * @param modeled If the airport should be modeled by the simulator.
     * @param state The state or region of the airport.
     * @param latitude The approximate center latitude of the airport.
     * @param longitude The approximate center longitude of the airport.
     * @param numRunways The number of runways at this airport.
     * @param timeZone The abbreviated time zone name (e.g. ET - Eastern Time).
     * @param dst Whether the airport uses daylight savings time.
     * @param taxiOutTime The average time to taxi to the runway from the gate
     * in minutes.
     * @param taxiInTime The average time to taxi to the gate from the runway in
     * minutes.
     * @param eteCorrection The estimated time enroute correction in minutes.
     *
     * @throws IllegalArgumentException if {@code name} is {@code null} or an
     * empty
     * {@link java.lang.String String} after whitespace has been removed.
     */
    public Airport(String name,
            boolean modeled,
            String state,
            boolean major,
            Latitude latitude,
            Longitude longitude,
            int numRunways,
            String timeZone,
            boolean dst,
            double taxiOutTime,
            double taxiInTime,
            double taxiOutDelay,
            double taxiInDelay,
            double eteCorrection,
            boolean enableDepartureQueueThreshold,
            double departureQueueThreshold,
            boolean enableRerouteClearance,
            double rerouteClearanceHandlingCapacity, 
            int defaultRampCapacity,
            boolean enableArrivalQueueManagement, 
			long maxArrFixQueueWaitTime,
			long lastCapResourceTransitTimeThreshold)
            throws IllegalArgumentException {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Attemted to create unnamed Airport!");
        } else {
            this.name = name;
            this.modeled = modeled;
            this.state = state;
            this.major = major;
            this.latitude = latitude;
            this.longitude = longitude;
            this.numRunways = numRunways;
            this.timeZone = TimeZone.valueOf(timeZone);
            this.dst = dst;
            this.taxiOutTime = taxiOutTime;
            this.taxiInTime = taxiInTime;
            this.taxiOutDelay = taxiOutDelay;
            this.taxiInDelay = taxiInDelay;
            this.eteCorrection = eteCorrection;
            this.enableDepartureQueueThreshold = enableDepartureQueueThreshold;
            this.departureQueueThreshold = departureQueueThreshold;
            this.enableRerouteClearance = enableRerouteClearance;
            this.rerouteClearanceCapacity = rerouteClearanceHandlingCapacity;
            this.defaultRampCapacity = defaultRampCapacity;
            this.enableArrivalQueueModeling = enableArrivalQueueManagement;
            this.maxArrFixQueueWaitTimeMs = maxArrFixQueueWaitTime;
            this.lastCapResourceTransitTimeThresholdMs = lastCapResourceTransitTimeThreshold;
        }
    }

    /*
     * Getters and Setters
     */
    /**
     * @return The FAA abbreviated name of the airport
     */
    public String getName() {
        return name;
    }

    /**
     * @param name The FAA abbreviated name of the airport.
     *
     * @throws IllegalArgumentException if {@code name} is {@code null} or an
     * empty
     * {@link java.lang.String String} after whitespace has been removed.
     */
    public void setName(String name) throws IllegalArgumentException {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Attemted to unnamed Airport!");
        } else {
            this.name = name;
        }
    }

    /**
     * @return {@code true} if the airport is to be modeled in the simulation,
     * {@code false} otherwise.
     */
    public boolean isModeled() {
        return modeled;
    }

    /**
     * @param modeled If the airport is to be modeled in the simulation.
     */
    public void setModeled(boolean modeled) {
        this.modeled = modeled;
    }

    /**
     * @return The state or region of the airport or {@code null} if unset.
     */
    public String getState() {
        return state;
    }

    /**
     * @param state - The state or region of the airport
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     * @return {@code true} if the airport is a major airport,
     * {@code false} otherwise.
     */
    public boolean isMajor() {
        return major;
    }

    /**
     * @param major If the airport is an OEP airport.
     */
    public void setMajor(boolean major) {
        this.major = major;
    }

    /**
     * @return The latitude of the approximate center of the airport or {@code null}
     * if unset.
     */
    public Latitude getLatitude() {
        return latitude;
    }

    /**
     * @param latitude The latitude of the approximate center of the airport.
     */
    public void setLatitude(Latitude latitude) {
        this.latitude = latitude;
    }

    /**
     * @return The longitude of the approximate center of the airport or {@code null}
     * if unset.
     */
    public Longitude getLongitude() {
        return longitude;
    }

    /**
     * @param longitude The longitude of the approximate center of the airport.
     */
    public void setLongitude(Longitude longitude) {
        this.longitude = longitude;
    }

    /**
     * @return The number of runways at this airport or {@code null} if unset.
     */
    public Integer getNumRunways() {
        return numRunways;
    }

    /**
     * @param numRunways The number of runways at this airport.
     */
    public void setNumRunways(int numRunways) {
        this.numRunways = numRunways;
    }

    /**
     * @return The abbreviated time zone name (e.g. ET - Eastern Time) or {@code null}
     * if unset.
     */
    public String getTimeZone() {
        return (timeZone == null ? null : timeZone.toString());
    }

    /**
     * @return The UTC offset for the airport or {@code null} if unset.
     */
    public Double getUtcOffset() {
        return (timeZone == null ? null : timeZone.getUtcOffset());
    }

    /**
     * @param timeZone The abbreviated time zone name (e.g. ET - Eastern Time).
     *
     * @return {@code true} if {@code timeZone} is a valid time zone
     * abbreviation and {@code false} otherwise.
     */
    public boolean setTimeZone(String timeZone) {
        try {
            this.timeZone = (timeZone == null ? null : TimeZone.valueOf(timeZone));
        } catch (IllegalArgumentException iae) {
            return false;
        }
        return true;
    }

    /**
     * @return {@code true} if the airport uses daylight savings time and {@code false}
     * if it is unset or otherwise.
     */
    public boolean isDst() {
        return dst;
    }

    /**
     * @param dst Whether the airport uses daylight savings time.
     */
    public void setDst(boolean dst) {
        this.dst = dst;
    }

    /**
     * @return The mean time to taxi from the gate to the runway in minutes or {@code null}
     * if unset.
     */
    public Double getTaxiOutTime() {
        return taxiOutTime;
    }

    /**
     * @param taxiOutTime The mean time to taxi from the gate to the runway in
     * minutes.
     */
    public void setTaxiOutTime(double taxiOutTime) {
        this.taxiOutTime = taxiOutTime;
    }

    /**
     * @return The mean time to taxi to the gate from the runway in minutes or {@code null}
     * if unset.
     */
    public Double getTaxiInTime() {
        return taxiInTime;
    }

    /**
     * @param taxiInTime The mean time to taxi to the gate from the runway in
     * minutes.
     */
    public void setTaxiInTime(double taxiInTime) {
        this.taxiInTime = taxiInTime;
    }

    /**
     * @return The mean Delay to taxi from the gate to the runway in minutes or {@code null}
     * if unset.
     */
    public Double getTaxiOutDelay() {
        return taxiOutDelay;
    }

    /**
     * @param taxiOutDelay The mean Delay to taxi from the gate to the runway in
     * minutes.
     */
    public void setTaxiOutDelay(double taxiOutDelay) {
        this.taxiOutDelay = taxiOutDelay;
    }

    /**
     * @return The mean Delay to taxi to the gate from the runway in minutes or {@code null}
     * if unset.
     */
    public Double getTaxiInDelay() {
        return taxiInDelay;
    }

    /**
     * @param taxiInDelay The mean Delay to taxi to the gate from the runway in
     * minutes.
     */
    public void setTaxiInDelay(double taxiInDelay) {
        this.taxiInDelay = taxiInDelay;
    }

    /**
     * TODO delete eteCorrection
     * @return The mean estimated time enroute correction in minutes or {@code null}
     * if unset.
     */
    public Double getEteCorrection() {
        return eteCorrection;
    }

    /**
     * @param eteCorrection The mean estimated time enroute correction in
     * minutes.
     */
    public void setEteCorrection(double eteCorrection) {
        this.eteCorrection = eteCorrection;
    }
    
    /**
     * @return whether departure queue throttling is enabled at the airport.
     */
    public boolean getEnableDepartureQueueThreshold() {
        return this.enableDepartureQueueThreshold;
    }

    /**
     * @param enableDepartureQueueThreshold Whether departure queue throttling
     * is enabled at the airport.
     */
    public void setEnableDepartureQueueThreshold(boolean enableDepartureQueueThreshold) {
        this.enableDepartureQueueThreshold = enableDepartureQueueThreshold;
    }
    
    /**
     * @return the departure queue length threshold at the airport before 
     * flights are held at the departure gate.
     */
    public Double getDepartureQueueThreshold() {
        return departureQueueThreshold;
    }

    /**
     * @param departureQueueThreshold The departure queue length threshold 
     * at the airport before flights are held at the departure gate.
     */
    public void setDepartureQueueThreshold(double departureQueueThreshold) {
        this.departureQueueThreshold = departureQueueThreshold;
    }
    
    /**
     * @return whether re-route clearance is enabled at the airport.
     */
    public boolean getEnableRerouteClearance() {
        return this.enableRerouteClearance;
    }

    /**
     * @param flag Whether re-route clearance is enabled at the airport.
     */
    public void setEnableRerouteClearance(boolean flag) {
        this.enableRerouteClearance = flag;
    }
    
   /**
    * @return the reroute clearance handling resource capacity at the airport.
    */
    public Double getRerouteClearanceHandlingCapacity() {
        return this.rerouteClearanceCapacity;
    }

   /**
    * @param rerouteClearanceHandlingCapacity The reroute clearance handling resource capacity.
    */
    public void setRerouteClearanceHandlingCapacity(double rerouteClearanceHandlingCapacity) {
        this.rerouteClearanceCapacity = rerouteClearanceHandlingCapacity;
    }

   /**
    * @return the default ramp capacity at the airport.  Used when no gate groups have been configured for the airport
    */
    public int getDefaultRampCapacity() { 
    	return this.defaultRampCapacity;
    }
   
   /**
    * @param rerouteClearanceHandlingCapacity The reroute clearance handling resource capacity.
    */
    public void setDefaultRampCapacity(int defaultRampCapacity) {
    	this.defaultRampCapacity = defaultRampCapacity;
    }

    public boolean getEnableArrivalQueueModeling() {
		return enableArrivalQueueModeling;
	}

	public void setEnableArrivalQueueManagement(boolean enableArrivalQueueModeling) {
		this.enableArrivalQueueModeling = enableArrivalQueueModeling;
	}

	public long getMaxArrFixQueueWaitTimeMs() {
		return maxArrFixQueueWaitTimeMs;
	}

	public void setMaxArrFixQueueWaitTime(long maxArrFixQueueWaitTime) {
		this.maxArrFixQueueWaitTimeMs = maxArrFixQueueWaitTime;
	}

	public long getLastCapResourceTransitTimeThresholdMs() {
		return lastCapResourceTransitTimeThresholdMs;
	}

	public void setLastCapResourceTransitTimeThresholdMs(long lastCapResourceTransitTimeThreshold) {
		this.lastCapResourceTransitTimeThresholdMs = lastCapResourceTransitTimeThreshold;
	}


    /**
     * for use in
     * gov.faa.ang.swac.simengine.data.jni.ResourceData.loadSurfaceModelingFromList()
     */
    public boolean isValid() {
        if (this.name == null || this.state == null || this.latitude == null || this.longitude == null || this.numRunways == null
                || this.timeZone == null || this.taxiOutTime == null || this.taxiInTime == null || this.eteCorrection == null) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public int compareTo(Airport a) {
        return this.name.compareTo(a.name);
    }

    @Override
    public long readHeader(BufferedReader reader) throws IOException {
    	reader.readLine();
		return -1;
    }

    @Override
    public void writeHeader(PrintWriter writer, long numRecords) throws IOException {
//        writer.println("################################################################################################################");
//        writer.println("# Number of Airports: " + numRecords);
//        writer.println("# ");
//        writer.println("# DEFAULT_TAXI_OUT_TIME = " + Airport.DEFAULT_TAXI_OUT_TIME);
//        writer.println("# DEFAULT_TAXI_IN_TIME = " + Airport.DEFAULT_TAXI_IN_TIME);
//        writer.println("# DEFAULT_TAXI_OUT_DELAY = " + Airport.DEFAULT_TAXI_OUT_DELAY);
//        writer.println("# DEFAULT_TAXI_IN_DELAY = " + Airport.DEFAULT_TAXI_IN_DELAY);
//        writer.println("# DEFAULT_DEPARTURE_QUEUE_THRESHOLD = " + Airport.DEFAULT_DEPARTURE_QUEUE_THRESHOLD);
//        writer.println("################################################################################################################");
        writer.println("FAA Code, Modeled Flag, State, Latitude, Longitude, Number Runways, Time Zone, DST Flag, Taxi Out Time mins, Taxi In Time mins, ETE Correction mins, Taxi Out Delay mins, Taxi In Delay mins, EnableDepQueueThreshold, DepQueueThreshold, RereouteClearanceFlag, RerouteClearanceCapacity,DefaultRampCapacity,EnableArrQModeling,MaxArrFixQueueWaitTime,LastCapResourceTransitTimeThreshold");
    }

    @Override
    public void readItem(BufferedReader reader) throws IOException {
        String line = reader.readLine();

        while (line.startsWith("#")) {
            line = reader.readLine();
        }

        String[] fields = line.split(",");

        name            = fields[0].trim();
        modeled         = fields[1].trim().equalsIgnoreCase("T") ? true : false;
        state           = fields[2].trim();
        major           = fields[3].trim().equalsIgnoreCase("T") ? true : false;
        latitude        = new Latitude(Double.valueOf(fields[4].trim()), Units.DEGREES);
        longitude       = new Longitude(Double.valueOf(fields[5].trim()), Units.DEGREES);
        numRunways      = fields[6] == null  || fields[6].trim().isEmpty() ? null : Integer.valueOf(fields[6].trim());
        timeZone        = fields[7] == null  || fields[7].trim().isEmpty() ? null : TimeZone.valueOf(fields[7].trim());
        dst             = fields[8] == null  || fields[8].trim().equalsIgnoreCase("T") ? true : false;
        taxiOutTime     = fields[9] == null  || fields[9].trim().isEmpty() ? Airport.DEFAULT_TAXI_OUT_TIME : Double.valueOf(fields[9].trim());
        taxiInTime      = fields[10] == null || fields[10].trim().isEmpty() ? Airport.DEFAULT_TAXI_IN_TIME : Double.valueOf(fields[10].trim());
        eteCorrection   = fields[11] == null || fields[11].trim().isEmpty() ? null : Double.valueOf(fields[11].trim());
        taxiOutDelay    = fields[12] == null || fields[12].trim().isEmpty() ? Airport.DEFAULT_TAXI_OUT_DELAY : Double.valueOf(fields[12].trim());
        taxiInDelay     = fields[13] == null || fields[13].trim().isEmpty() ? Airport.DEFAULT_TAXI_IN_DELAY : Double.valueOf(fields[13].trim());
        enableDepartureQueueThreshold = fields[14].trim().equalsIgnoreCase("T") ? true : false;
        departureQueueThreshold = fields[15] == null || fields[15].trim().isEmpty() ? Airport.DEFAULT_DEPARTURE_QUEUE_THRESHOLD : Double.valueOf(fields[15].trim());
        enableRerouteClearance = fields[16].trim().equalsIgnoreCase("T") ? true : false;
        rerouteClearanceCapacity = fields[17] == null || fields[17].trim().isEmpty() ? null : Double.valueOf(fields[17].trim());
        
        defaultRampCapacity = infiniteCapacity;
        if (fields[18] != null) {
        	fields[18] = fields[18].trim();
        	boolean infinite = fields[18].isEmpty() || fields[18].equalsIgnoreCase("inf");
        	if (! infinite) {
    			defaultRampCapacity = Integer.parseInt(fields[18]);
    			if (defaultRampCapacity < 0)
    				defaultRampCapacity = infiniteCapacity;
        	}
        }
        enableArrivalQueueModeling = fields[19] == null || fields[19].trim().equalsIgnoreCase("T") ? true : false;
    	maxArrFixQueueWaitTimeMs = fields[20] == null || fields[20].trim().isEmpty() ? DEFAULT_ARR_Q_THRESHOLD : Timestamp.minutesToMillis(Double.valueOf(fields[20].trim()));
    	lastCapResourceTransitTimeThresholdMs = fields[21] == null || fields[21].trim().isEmpty() ? DEFAULT_ARR_Q_THRESHOLD : Timestamp.minutesToMillis(Double.valueOf(fields[21].trim()));
    }

    @Override
    public void writeItem(PrintWriter writer) throws IOException {
        char sep = ',';
        DecimalFormat sdf = new DecimalFormat("0.00");
        DecimalFormat ldf = new DecimalFormat("0.0000000000");

        writer.write(name + sep);
        writer.write((modeled ? "T" : "F") + sep);
        writer.write(state + sep);
        writer.write((major ? "T" : "F") + sep);
        writer.write(ldf.format(latitude.degrees()) + sep);
        writer.write(ldf.format(longitude.degrees()) + sep);
        writer.write((numRunways == null ? "" : Integer.toString(numRunways)) + sep);
        writer.write((timeZone == null ? "" : timeZone.toString() + sep));
        writer.write((dst ? "T" : "F") + sep);
        writer.write((taxiOutTime == null ? "" : sdf.format(taxiOutTime)) + sep);
        writer.write((taxiInTime == null ? "" : sdf.format(taxiInTime)) + sep);
        writer.write((eteCorrection == null ? "" : sdf.format(eteCorrection)) + sep);
        writer.write((taxiOutDelay == null ? "" : sdf.format(taxiOutDelay)) + sep);
        writer.write((taxiInDelay == null ? "" : sdf.format(taxiInDelay)) + sep);
        writer.write((enableDepartureQueueThreshold ? "T" : "F") + sep);
        writer.write((departureQueueThreshold == null ? "" : sdf.format(departureQueueThreshold)) + sep);
        writer.write((enableRerouteClearance? "T" : "F") + sep);
        writer.write((rerouteClearanceCapacity == null ? "" : sdf.format(rerouteClearanceCapacity)) + sep);
        writer.write("" + defaultRampCapacity);
        writer.write(sep);
        
        writer.write(enableArrivalQueueModeling? "T" : "F");
        writer.write(sep);
        
        writer.write("" + Timestamp.millisToMin(maxArrFixQueueWaitTimeMs));
        writer.write(sep);
        
        writer.write("" + Timestamp.millisToMin(lastCapResourceTransitTimeThresholdMs));
        writer.write(sep);
        
        writer.write("\n");
    }
}
