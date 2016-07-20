/**
 * Copyright "TBD", Metron Aviation & CSSI.  All rights reserved.
 * 
 * This computer Software was developed with the sponsorship of the U.S. Government
 * under Contract No. DTFAWA-10-D-00033, which has a copyright license in accordance with AMS 3.5-13.(c)(1).
 */

package gov.faa.ang.swac.common.flightmodeling;

import gov.faa.ang.swac.common.datatypes.Altitude;
import gov.faa.ang.swac.common.flightmodeling.fileio.BadaRecord;
import gov.faa.ang.swac.common.flightmodeling.fileio.BadaRecord.FlightLevelRecord;
import gov.faa.ang.swac.common.flightmodeling.fileio.BadaRecord.FlightStage;
import gov.faa.ang.swac.common.flightmodeling.fileio.BadaRecord.Range;

import com.mallardsoft.tuple.Triple;
import com.mallardsoft.tuple.Tuple;

// TODO: Comments, exception handling,  validation

/**
 * BadaModel encapsulates the operation of a weight-based fuel burn model. It relies on PTF data
 * found in BadaRecord, and uses bilinear interpolation between altitude entries and
 * low/nominal/high weight thresholds. The model should be initialized with a starting altitude and
 * mass. From that point, calls to climb, cruise, or descend will advance the model by a specified
 * time increment, adjusting mass, altitude, and distance according to the BADA values for fuel
 * usage rate, rate of climb or descent, and nominal true air speed, respectively.
 */
public class BadaModel
{
	
    @SuppressWarnings("unused")
	private static org.apache.log4j.Logger logger = org.apache.log4j.LogManager.getLogger(BadaModel.class);


    // ------------------------------------------
    // Constants
    // ------------------------------------------

    /**
     * Simple conversion constant between minute and hours.
     */
    public static final double MINUTES_TO_HOURS = 1.0 / 60;

    // ------------------------------------------
    // Member variables
    // ------------------------------------------

    /**
     * BADA data associated with this model.
     */
    private BadaRecord bada;

    /**
     * The current altitude for the aircraft being modeled.
     */
    private Altitude altitude;

    /**
     * The current mass for the aircraft being modeled.
     */
    private double mass;

    /**
     * The low mass threshold extracted from the bada record for faster access.
     */
    private double loMass;

    /**
     * The nominal mass threshold extracted from the BADA record for faster access.
     */
    private double nomMass;

    /**
     * The high mass threshold extracted from the BADA record for faster access.
     */
    private double hiMass;

    /**
     * The lower altitude FlightLevelRecord bracketing the current altitude for this model,
     * extracted from the BADA record.
     */
    private FlightLevelRecord lowerFLR;

    /**
     * The upper altitude FlightLevelRecord bracketing the current altitude for this model,
     * extracted from the BADA record.
     */
    private FlightLevelRecord upperFLR;

    /**
     * The fraction of the distance between the altitude of lowerFLR and upperFLR represented by
     * altitude.
     */
    private double fraction;

    /**
     * Elapsed time, in minutes, modeled by successive climb/cruise/descend invocations since this
     * model was initialized or time was reset.
     */
    private double time = 0.0;

    /**
     * Traversed distance, in nautical miles, modeled by successive climb/cruise/descend invocations
     * since this model was initialized or distance was reset.
     */
    private double distance = 0.0;

    // ------------------------------------------
    // Constructors
    // ------------------------------------------

    /**
     * Extracts BADA data from an aircraft object. Does not check for nulls.
     */
    public BadaModel(Aircraft aircraft)
    {
        this(aircraft.badaRecord());
    }

    /**
     * Initializing with a BadaRecord sets starting altitude to 0 and starting mass to maximum mass
     * 
     * @param record
     */
    public BadaModel(BadaRecord record)
    {
        this.bada = record;

        this.loMass = Double.valueOf(this.bada.massLevel(Range.LOW));
        this.nomMass = Double.valueOf(this.bada.massLevel(Range.NOMINAL));
        this.hiMass = Double.valueOf(this.bada.massLevel(Range.HIGH));

        this.altitude = Altitude.valueOfFeet(0.0);
        this.setFlightLevelRecords();
        this.mass = this.hiMass;
    }

    // ------------------------------------------
    // Accessors
    // ------------------------------------------

    /**
     * Once initialized in the constructor, the BadaRecord for this model cannot be changed.
     */
    public BadaRecord getBadaRecord()
    {
        return this.bada;
    }

    /**
     * @return Current altitude
     */
    public Altitude getAltitude()
    {
        return this.altitude;
    }

    /**
     * setAltitude should normally be invoked only at the beginning of this model's lifetime, to
     * avoid discontinuities in the implied trajectory.
     * 
     * @param v Altitude value
     */
    public void setAltitude(Altitude v)
    {
        this.altitude = v;
        this.setFlightLevelRecords();
    }

    /**
     * Retrieves BADA data for the bracketing flight levels and sets member variables for faster
     * access. This should be invoked by any method that modifies altitude.
     */
    private void setFlightLevelRecords()
    {
        Triple<FlightLevelRecord, FlightLevelRecord, Double> rec = this.bada.getFlightLevelRecords(this.altitude);
        this.lowerFLR = Tuple.first(rec);
        this.upperFLR = Tuple.second(rec);
        this.fraction = Tuple.third(rec);
    }

    /**
     * @return Current mass, in kilograms
     */
    public double getMass()
    {
        return this.mass;
    }

    /**
     * setMass should normally be invoked only at the beginning of this model's lifetime, to avoid
     * discontinuities in the implied trajectory.
     * 
     * @param v Double Mass value, in kilograms
     */
    public void setMass(double v)
    {
        this.mass = v;
    }

    /**
     * This allows the mass to be set as a fraction of the maximum fuel + payload, which corresponds
     * to the difference between loMass and hiMass.
     * 
     * @param load A fraction between 0 and 1. 0 indicates empty mass. 1 indicates maximum take off mass.
     */
    public void setMassByLoadFactor(double load)
    {
        this.mass = this.loMass + load * (this.hiMass - this.loMass);
    }

    /**
     * @return Time elapsed, in minutes
     */
    public double getTime()
    {
        return this.time;
    }

    /**
     * Time can be reset in order to act like a stopwatch. Setting time explicitly would serve to
     * corrupt the model.
     */
    public void resetTime()
    {
        this.time = 0.0;
    }

    /**
     * @return Distance traversed, in nm
     */
    public double getDistance()
    {
        return this.distance;
    }

    /**
     * Distance can be reset in order to act like a stopwatch. Setting distance explicitly would
     * serve to corrupt the model.
     */
    public void resetDistance()
    {
        this.distance = 0.0;
    }

    // ------------------------------------------
    // Modeling methods that do not modify data
    // ------------------------------------------

    /**
     * Returns the fuel usage rate based on this model's current member variable values.
     * 
     * @param stage Climb, Cruise, or Descent.
     * @return Fuel usage rate in kg/min or {@code Double.NaN} if not rate not found.
     */
    public double getFuelUsageRate(FlightStage stage)
    {
        return getFuelUsageRate(stage, this.lowerFLR, this.upperFLR, this.fraction, this.mass, this.loMass, this.nomMass, this.hiMass);
    }

    /**
     * Returns the fuel usage rate using this BADA model and a specified altitude - useful for
     * prediction fuel burn rates for decision making.
     * 
     * @param stage Climb, Cruise, or Descent.
     * @param alt The altitude for which fuel usage rate is desired.
     * @return Fuel usage rate in kg/min or {@code Double.NaN} if not rate not found.
     */
    public double getFuelUsageRate(FlightStage stage, Altitude alt)
    {
        return getFuelUsageRate(stage, alt, mass);
    }

    /**
     * Returns the fuel usage rate using this BADA model and a specified altitude - useful for
     * predicting fuel burn rates for decision making.
     * 
     * @param stage Climb, Cruise, or Descent.
     * @param alt The altitude for which fuel usage rate is desired.
     * @return Fuel usage rate in kg/min or {@code Double.NaN} if not rate not found.
     */
    public double getFuelUsageRate(FlightStage stage, Altitude alt, double mass)
    {
        Triple<FlightLevelRecord, FlightLevelRecord, Double> rec = this.bada.getFlightLevelRecords(alt);
        return getFuelUsageRate(stage, Tuple.first(rec), Tuple.second(rec), Tuple.third(rec), mass, this.loMass, this.nomMass, this.hiMass);
    }

    /**
     * Static helper performs the work for other getFuelUsageRate method signatures. Returns the
     * fuel usage rate based on all supplied values, using bilinear interpolation
     * 
     * @param stage Climb, Cruise, or Descent.
     * @param lowFLR The FlightLevelRecord bracketing the chosen altitude from below.
     * @param highFLR The FlightLevelRecord bracketing the chosen altitude from above.
     * @param fraction The fraction of the distance between lowFLR and highFLR for which altitude is interpolated.
     * @param mass The aircraft mass used to interpolate between cruise fuel usage rates.
     * @param loMass The BADA model's minimum mass, used to interpolate between cruise fuel usage rates.
     * @param nomMass The BADA model's nominal mass, used to interpolate between cruise fuel usage rates.
     * @param hiMass The BADA model's maximum mass, used to interpolate between cruise fuel usage rates.
     * @return Fuel usage rate in kg/min or {@code Double.NaN} if not rate not found.
     */
    private static double getFuelUsageRate(FlightStage stage, FlightLevelRecord lowFLR, FlightLevelRecord highFLR, Double fraction, Double mass, Double loMass, Double nomMass, Double hiMass)
    {
        double massFraction = Double.NaN;
        double loFuel = Double.NaN;
        double hiFuel = Double.NaN;

        switch (stage)
        {
            // During ascent, fuel usage is the same for all weight levels: interpolate over altitude only
            case ASCENT:
                loFuel = Double.valueOf(lowFLR.climbFuelUsage);
                hiFuel = Double.valueOf(highFLR.climbFuelUsage);
                break;
            // In the cruise segment, we must interpolate based on mass. Choose whether mass lies between aircraft mimimum and nominal values,
            // or between nominal and maximum values in order to choose the interpolation limits.
            case CRUISE:
                if (mass >= nomMass)
                {
                    massFraction = hiMass.equals(nomMass) ? 0 : (mass - nomMass) / (hiMass - nomMass);

                    double loNomFuel = lowFLR.cruiseFuelUsage[Range.NOMINAL.ordinal()];
                    double loHiFuel = lowFLR.cruiseFuelUsage[Range.HIGH.ordinal()];
                    double hiNomFuel = highFLR.cruiseFuelUsage[Range.NOMINAL.ordinal()];
                    double hiHiFuel = highFLR.cruiseFuelUsage[Range.HIGH.ordinal()];

                    // Some fuel usage entries may be null. Leave loFuel and hiFuel null if that is the case.
                    if (!Double.isNaN(loNomFuel) && !Double.isNaN(loHiFuel))
                    {
                        loFuel = loNomFuel + massFraction * (loHiFuel - loNomFuel);
                    }
                    if (!Double.isNaN(hiNomFuel) && !Double.isNaN(hiHiFuel))
                    {
                        hiFuel = hiNomFuel + massFraction * (hiHiFuel - hiNomFuel);
                    }
                }
                else
                {
                    massFraction = nomMass.equals(loMass) ? 0 : (mass - loMass) / (nomMass - loMass);

                    double loNomFuel = lowFLR.cruiseFuelUsage[Range.NOMINAL.ordinal()];
                    double loLoFuel = lowFLR.cruiseFuelUsage[Range.LOW.ordinal()];
                    double hiNomFuel = highFLR.cruiseFuelUsage[Range.NOMINAL.ordinal()];
                    double hiLoFuel = highFLR.cruiseFuelUsage[Range.LOW.ordinal()];

                    // Some fuel usage entries may be null. Leave loFuel and hiFuel null if that is the case.
                    if (!Double.isNaN(loNomFuel) && !Double.isNaN(loLoFuel))
                    {
                        loFuel = loLoFuel + massFraction * (loNomFuel - loLoFuel);
                    }
                    if (!Double.isNaN(hiNomFuel) && !Double.isNaN(hiLoFuel))
                    {
                        hiFuel = hiLoFuel + massFraction * (hiNomFuel - hiLoFuel);
                    }
                }
                break;
            // During descent, fuel usage is the same for all weight levels: interpolate over altitude only
            case DESCENT:
                loFuel = lowFLR.descentFuelUsage;
                hiFuel = highFLR.descentFuelUsage;
                break;
        }
        // Perform linear interpolation over altitude. If loFuel or hiFuel is null, return null.
        if (Double.isNaN(loFuel))
        {
            return hiFuel;
        }
        else if (Double.isNaN(hiFuel))
        {
            return loFuel;
        }
        else
        {
            return loFuel + fraction * (hiFuel - loFuel);
        }
    }

    /**
     * Returns the true air speed based on this model's current member variable values.
     * 
     * @param stage Climb, Cruise, or Descent.
     * @return TAS in nm/hr or {@code Double.NaN} if stage is not applicable.
     */
    public double getTrueAirSpeed(FlightStage stage)
    {
        return getTrueAirSpeed(stage, this.altitude);
    }

    /**
     * Returns the true air speed using this BADA model and a specified altitude - useful for
     * prediction fuel efficiency for decision making.
     * 
     * @param stage Climb, Cruise, or Descent.
     * @param alt The altitude for which true air speed is desired.
     * @return TAS in nm/hr or {@code Double.NaN} if stage or alt is not applicable
     */
    public double getTrueAirSpeed(FlightStage stage, Altitude alt)
    {
        return this.bada.trueAirSpeed(alt, stage);
    }

    /**
     * Returns the fuel efficiency based on this model's current member variable values.
     * 
     * @param stage Climb, Cruise, or Descent.
     * @return Fuel efficiency in nm/kg or {@code Double.NaN} if rate not found.
     */
    public double getFuelEfficiency(FlightStage stage)
    {
        double tas = this.getTrueAirSpeed(stage);
        double fuel = this.getFuelUsageRate(stage);
        return Double.isNaN(tas) || Double.isNaN(fuel) ? Double.NaN : tas / fuel * MINUTES_TO_HOURS;
    }

    /**
     * Returns the fuel efficiency using this BADA model and a specified altitude - useful for
     * prediction fuel efficiency for decision making.
     * 
     * @param stage Climb, Cruise, or Descent.
     * @return Fuel efficiency in nm/kg or {@code Double.NaN} if rate not found.
     */
    public double getFuelEfficiency(FlightStage stage, Altitude alt)
    {
        double tas = this.getTrueAirSpeed(stage, alt);
        double fuel = this.getFuelUsageRate(stage, alt);
        return Double.isNaN(tas) || Double.isNaN(fuel) ? Double.NaN : tas / fuel * MINUTES_TO_HOURS;
    }

    /**
     * Returns the rate of climb or descent based on this model's current member variable values,
     * using bilinear interpolation over mass and altitude.
     * 
     * @param stage Climb, Cruise, or Descent.
     * @return Rate of climb or descent in ft/min or {@code Double.NaN} if ROCD not found.
     */
    public double getRateOfClimbDescent(FlightStage stage)
    {
        return getRateOfClimbDescent(stage, this.lowerFLR, this.upperFLR, this.fraction, this.mass, this.loMass, this.nomMass, this.hiMass);
    }

    /**
     * Returns the rate of climb or descent based on this model's current member variable values,
     * using bilinear interpolation over mass and altitude.
     * 
     * @param stage Climb, Cruise, or Descent.
     * @return Rate of climb or descent in ft/min or {@code Double.NaN} if ROCD not found.
     */
    public double getRateOfClimbDescent(FlightStage stage, Altitude alt)
    {
        Triple<FlightLevelRecord, FlightLevelRecord, Double> rec = this.bada.getFlightLevelRecords(alt);
        return getRateOfClimbDescent(stage, Tuple.first(rec), Tuple.second(rec), Tuple.third(rec), this.mass, this.loMass, this.nomMass, this.hiMass);
    }

    /**
     * Static helper performs the work for other getRateOfClimbDescent method signatures. Returns
     * the rate of climb or descent based on all supplied values, using bilinear interpolation
     * 
     * @param stage Climb, Cruise, or Descent.
     * @param lowFLR The FlightLevelRecord bracketing the chosen altitude from below.
     * @param highFLR The FlightLevelRecord bracketing the chosen altitude from above.
     * @param fraction The fraction of the distance between lowFLR and highFLR for which altitude is interpolated.
     * @param mass The aircraft mass used to interpolate between cruise fuel usage rates.
     * @param loMass The BADA model's minimum mass, used to interpolate between cruise fuel usage rates.
     * @param nomMass The BADA model's nominal mass, used to interpolate between cruise fuel usage rates.
     * @param hiMass The BADA model's maximum mass, used to interpolate between cruise fuel usage rates.
     * @return Rate of climb or descent in ft/min or {@code Double.NaN} if ROCD not found.
     */
    private static double getRateOfClimbDescent(FlightStage stage, FlightLevelRecord lowFLR, FlightLevelRecord highFLR, Double fraction, Double mass, Double loMass, Double nomMass, Double hiMass)
    {
        Double loRocd = null;
        Double hiRocd = null;
        Double massFraction = null;

        switch (stage)
        {
            // During ascent, the rocd depends on mass. Choose whether mass lies between aircraft mimimum and nominal values,
            // or between nominal and maximum values in order to choose the interpolation limits.
            case ASCENT:
                if (mass >= nomMass)
                {
                    massFraction = hiMass.equals(nomMass) ? 0 : (mass - nomMass) / (hiMass - nomMass);

                    Double loNomRocd = lowFLR.climbRocd[Range.NOMINAL.ordinal()] != Integer.MIN_VALUE ? Double.valueOf(lowFLR.climbRocd[Range.NOMINAL.ordinal()]) : null;
                    Double loHiRocd = lowFLR.climbRocd[Range.HIGH.ordinal()] != Integer.MIN_VALUE ? Double.valueOf(lowFLR.climbRocd[Range.HIGH.ordinal()]) : null;
                    Double hiNomRocd = highFLR.climbRocd[Range.NOMINAL.ordinal()] != Integer.MIN_VALUE ? Double.valueOf(highFLR.climbRocd[Range.NOMINAL.ordinal()]) : null;
                    Double hiHiRocd = highFLR.climbRocd[Range.HIGH.ordinal()] != Integer.MIN_VALUE ? Double.valueOf(highFLR.climbRocd[Range.HIGH.ordinal()]) : null;

                    // Some rocd entries may be null. Leave loFuel and hiFuel null if that is the case.
                    if (loNomRocd != null && loHiRocd != null && hiNomRocd != null && hiHiRocd != null)
                    {
                        loRocd = loNomRocd + massFraction * (loHiRocd - loNomRocd);
                        hiRocd = hiNomRocd + massFraction * (hiHiRocd - hiNomRocd);
                    }
                }
                else
                {
                    massFraction = nomMass.equals(loMass) ? 0 : (mass - loMass) / (nomMass - loMass);

                    Double loNomRocd = lowFLR.climbRocd[Range.NOMINAL.ordinal()] != Integer.MIN_VALUE ? Double.valueOf(lowFLR.climbRocd[Range.NOMINAL.ordinal()]) : null;
                    Double loLoRocd = lowFLR.climbRocd[Range.LOW.ordinal()] != Integer.MIN_VALUE ? Double.valueOf(lowFLR.climbRocd[Range.LOW.ordinal()]) : null;
                    Double hiNomRocd = highFLR.climbRocd[Range.NOMINAL.ordinal()] != Integer.MIN_VALUE ? Double.valueOf(highFLR.climbRocd[Range.NOMINAL.ordinal()]) : null;
                    Double hiLoRocd = highFLR.climbRocd[Range.LOW.ordinal()] != Integer.MIN_VALUE ? Double.valueOf(highFLR.climbRocd[Range.LOW.ordinal()]) : null;

                    // Some rocd entries may be null. Leave loFuel and hiFuel null if that is the case.
                    if (loNomRocd != null && loLoRocd != null && hiNomRocd != null && hiLoRocd != null)
                    {
                        loRocd = loLoRocd + massFraction * (loNomRocd - loLoRocd);
                        hiRocd = hiLoRocd + massFraction * (hiNomRocd - hiLoRocd);
                    }
                }
                break;
            // Rocd is trivially zero during cruise
            case CRUISE:
                return 0.0;
            // Rocd does not vary based on weight for descent: interpolate over altitude only.
            case DESCENT:
                loRocd = Double.valueOf(lowFLR.descentRocd);
                hiRocd = Double.valueOf(highFLR.descentRocd);
                break;
        }
        // Perform linear interpolation over altitude. If loRocd or hiRocd is null, return null.
        return loRocd == null || hiRocd == null ? Double.NaN : loRocd + fraction * (hiRocd - loRocd);
    }

    // ---------------------------------------------------------------------------------------
    // Modeling methods that modify data - representing actions taken by the modeled aircraft
    // ---------------------------------------------------------------------------------------

    /**
     * Executes 1 minute of climb, modifying altitude, mass, and distance based on model values for
     * rocd, fuel usage rate, and nominal speed.
     * 
     * @return The distance traversed during this interval, in nm.
     */
    public double climb()
    {
        return climb(Double.valueOf(1));
    }

    /**
     * Executes 1 minute of cruise, modifying mass and distance based on model values for fuel usage
     * rate and nominal speed.
     * 
     * @return The distance traversed during this interval, in nm.
     */
    public double cruise()
    {
        return cruise(Double.valueOf(1));
    }

    /**
     * Executes 1 minute of descent, modifying altitude, mass, and distance based on model values
     * for rocd, fuel usage rate, and nominal speed.
     * 
     * @return The distance traversed during this interval, in nm.
     */
    public Double descend()
    {
        return descend(Double.valueOf(1));
    }

    /**
     * Executes a specified duration of climb, modifying altitude, mass, and distance based on model
     * values for rocd, fuel usage rate, and nominal speed. Minutes should generally be set to a
     * value less than 1. This method is technically approximating solutions for differential
     * equations in terms of dMass/dt and dAltitude/dt: mass and altitude are the lookup dimensions
     * of the PTF tables, and fuel usage rate (dMass/dt) and rocd (dAltitude/dt) are the values. At
     * short intervals, the limits of bilinear interpolation between table values mask the
     * inaccuracies of linear approximation of differential equations. At larger intervals this
     * weakness will dominate.
     * 
     * @param minutes The time interval over which climb is approximated in the model.
     * @return The fuel burned during this interval, in kg.
     */
    public double climb(double minutes)
    {
        // Trivial case
        if (minutes == 0) return 0.0;

        // Get model values for fuel burn, rocd, and tas
        Double fuelBurn = this.getFuelUsageRate(FlightStage.ASCENT);
        Double rateOfClimb = this.getRateOfClimbDescent(FlightStage.ASCENT);
        Double trueAirSpeed = this.getTrueAirSpeed(FlightStage.ASCENT);

        // Update member values
        this.mass -= minutes * fuelBurn;
        this.altitude = Altitude.valueOfFeet(this.altitude.feet() + minutes * rateOfClimb);
        this.setFlightLevelRecords();
        this.time += minutes;
        this.distance += minutes * MINUTES_TO_HOURS * trueAirSpeed;

        return fuelBurn;
    }

    /**
     * Executes a specified duration of cruise, modifying altitude and distance based on model
     * values for rocd and nominal speed. Minutes should generally be set to a value less than 1.
     * This method is technically approximating solutions for differential equations in terms of
     * dMass/dt and dAltitude/dt: mass and altitude are the lookup dimensions of the PTF tables, and
     * fuel usage rate (dMass/dt) and rocd (dAltitude/dt) are the values. At short intervals, the
     * limits of bilinear interpolation between table values mask the inaccuracies of linear
     * approximation of differential equations. At larger intervals this weakness will dominate.
     * 
     * @param minutes The time interval over which climb is approximated in the model.
     * @return The fuel burned during this interval, in kg.
     */
    public double cruise(double minutes)
    {
        // Trivial case
        if (minutes == 0) return 0.0;

        // Get model values for fuel burn and tas
        double fuelBurn = this.getFuelUsageRate(FlightStage.CRUISE);
        if (Double.isNaN(fuelBurn))
        {
            fuelBurn = this.getFuelUsageRate(FlightStage.ASCENT);
        }
        double trueAirSpeed = this.getTrueAirSpeed(FlightStage.CRUISE);
        if (Double.isNaN(trueAirSpeed))
        {
            trueAirSpeed = this.getTrueAirSpeed(FlightStage.ASCENT);
        }

        // Update member values
        this.mass -= minutes * fuelBurn;
        this.time += minutes;
        this.distance += minutes * MINUTES_TO_HOURS * trueAirSpeed;

        return fuelBurn;
    }

    /**
     * Executes a specified duration of descent, modifying altitude, mass, and distance based on
     * model values for rocd, fuel usage rate, and nominal speed. Minutes should generally be set to
     * a value less than 1. This method is technically approximating solutions for differential
     * equations in terms of dMass/dt and dAltitude/dt: mass and altitude are the lookup dimensions
     * of the PTF tables, and fuel usage rate (dMass/dt) and rocd (dAltitude/dt) are the values. At
     * short intervals, the limits of bilinear interpolation between table values mask the
     * inaccuracies of linear approximation of differential equations. At larger intervals this
     * weakness will dominate.
     * 
     * @param minutes The time interval over which climb is approximated in the model.
     * @return The fuel burned during this interval, in kg.
     */
    public double descend(double minutes)
    {
        // Trivial case
        if (minutes == 0) return 0.0;

        // Get model values for fuel burn, rocd, and tas
        Double fuelBurn = this.getFuelUsageRate(FlightStage.DESCENT);
        Double rateOfClimb = this.getRateOfClimbDescent(FlightStage.DESCENT);
        Double trueAirSpeed = this.getTrueAirSpeed(FlightStage.DESCENT);

        // Update member values
        this.mass -= minutes * fuelBurn;
        this.altitude = Altitude.valueOfFeet(this.altitude.feet() - minutes * rateOfClimb);
        this.setFlightLevelRecords();
        this.time += minutes;
        this.distance += minutes * MINUTES_TO_HOURS * trueAirSpeed;

        return fuelBurn;
    }
}
