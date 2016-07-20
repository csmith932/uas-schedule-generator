/**
 * Copyright "TBD", Metron Aviation & CSSI.  All rights reserved.
 * 
 * This computer Software was developed with the sponsorship of the U.S. Government
 * under Contract No. DTFAWA-10-D-00033, which has a copyright license in accordance with AMS 3.5-13.(c)(1).
 */

package gov.faa.ang.swac.common;

import gov.faa.ang.swac.common.flightmodeling.FlightPlan;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.Level;

/**
 * This is the interface for logging information on "bad" {@link FlightPlan}s.
 * @author Jason Femino - CSSI, Inc.
 */
public class FlightPlanMessageLogger
{
	private static final Logger logger = LogManager.getLogger(FlightPlanMessageLogger.class);
	
	/**
	 * Constructor.
	 */
	private FlightPlanMessageLogger()
	{
	}
	
	/**
	 * Comments about {@link FlightPlan}s.
	 */
	public enum Reason
	{
		DUPLICATE_SCHEDULE_ID		{ @Override public Level logLevel() { return Level.DEBUG; } @Override public String message() { return "has a duplicate schedule ID"; } },
		HELICOPTER                  { @Override public Level logLevel() { return Level.DEBUG; } @Override public String message() { return "is a helicopter flight"; } },
		MISSING_AIRPORTS            { @Override public Level logLevel() { return Level.INFO;  } @Override public String message() { return "missing departure and arrival airports"; } },
		MISSING_AIRPORT_COORDINATES { @Override public Level logLevel() { return Level.INFO;  } @Override public String message() { return "missing departure and arrival airport coordinates"; } },
		MISSING_DEP_ARR_TIMES       { @Override public Level logLevel() { return Level.INFO;  } @Override public String message() { return "missing departure and arrival times"; } },
		DEP_TIME_INVALID            { @Override public Level logLevel() { return Level.DEBUG; } @Override public String message() { return "has departure time outside simulation range"; } },
		ARR_TIME_INVALID            { @Override public Level logLevel() { return Level.DEBUG; } @Override public String message() { return "has arrival time outside simulation range"; } },
		ENROUTE_TIME_INVALID        { @Override public Level logLevel() { return Level.DEBUG; } @Override public String message() { return "has enroute time duration outside simulation range"; } },
		TRIMMED                     { @Override public Level logLevel() { return Level.DEBUG; } @Override public String message() { return "was trimmed from the schedule"; } },
		OUTSIDE_BOX                 { @Override public Level logLevel() { return Level.DEBUG; } @Override public String message() { return "originates and terminates outside the box"; } },
		SIM_ENGINE_ITINERARY_LIMIT  { @Override public Level logLevel() { return Level.INFO;  } @Override public String message() { return "SimEngine Itinerary input limit exceeded"; } },
		VFR_NO_DEP                  { @Override public Level logLevel() { return Level.DEBUG; } @Override public String message() { return "VFR departure information missing"; } },
		VFR_NO_ARR                  { @Override public Level logLevel() { return Level.DEBUG; } @Override public String message() { return "VFR arrival information missing"; } },
		VFR_NO_DEP_AIRPORT          { @Override public Level logLevel() { return Level.DEBUG; } @Override public String message() { return "VFR departure airport not listed"; } },
		VFR_NO_ARR_AIRPORT          { @Override public Level logLevel() { return Level.DEBUG; } @Override public String message() { return "VFR arrival airport not listed"; } },
		VFR_DEP_TIME_MISSING        { @Override public Level logLevel() { return Level.DEBUG; } @Override public String message() { return "VFR missing departure time"; } },
		VFR_ARR_TIME_MISSING        { @Override public Level logLevel() { return Level.DEBUG; } @Override public String message() { return "VFR missing arrival time"; } },
		VFR_DEP_AIRPORT_NOT_ALLOWED { @Override public Level logLevel() { return Level.TRACE; } @Override public String message() { return "VFR departure airport not allowed"; } },
		VFR_ARR_AIRPORT_NOT_ALLOWED { @Override public Level logLevel() { return Level.TRACE; } @Override public String message() { return "VFR arrival airport not allowed"; } },
		VFR_DEP_CREATED             { @Override public Level logLevel() { return Level.TRACE; } @Override public String message() { return "VFR departure created"; } },
		VFR_ARR_CREATED             { @Override public Level logLevel() { return Level.TRACE; } @Override public String message() { return "VFR arrival created"; } },
		VFR_REMOVED                 { @Override public Level logLevel() { return Level.TRACE; } @Override public String message() { return "VFR flight removed"; } },
		IFR_MISSING_DEP_AIRPORT     { @Override public Level logLevel() { return Level.DEBUG; } @Override public String message() { return "IFR has no departure airport"; } },
		IFR_MISSING_ARR_AIRPORT     { @Override public Level logLevel() { return Level.DEBUG; } @Override public String message() { return "IFR has no arrival airport"; } },
		IFR_MISSING_DEP_LOCATION    { @Override public Level logLevel() { return Level.DEBUG; } @Override public String message() { return "IFR has no departure airport location"; } },
		IFR_MISSING_ARR_LOCATION    { @Override public Level logLevel() { return Level.DEBUG; } @Override public String message() { return "IFR has no arrival airport location"; } },
                IFR_MISSING_DEP_DATE_TIMES  { @Override public Level logLevel() { return Level.DEBUG; } @Override public String message() { return "IFR has no departure date/times"; } },
		IFR_MISSING_BADA            { @Override public Level logLevel() { return Level.DEBUG; } @Override public String message() { return "IFR has no valid BADA type"; } },
		IFR_SHORT_FLIGHT            { @Override public Level logLevel() { return Level.INFO;  } @Override public String message() { return "IFR is a short flight"; } },
		IFR_ROUND_ROBIN             { @Override public Level logLevel() { return Level.INFO;  } @Override public String message() { return "IFR is a round-robin flight"; } },
		IFR_INVALID_FILED_ALT       { @Override public Level logLevel() { return Level.INFO;  } @Override public String message() { return "IFR filed altitude invalid"; } },
                IFR_INVALID_FLIGHT_PATH     { @Override public Level logLevel() { return Level.INFO;  } @Override public String message() { return "IFR has invalid flight path"; } },
		IFR_TO_VFR_DEP              { @Override public Level logLevel() { return Level.INFO;  } @Override public String message() { return "IFR flight converted to VFR departure"; } },
		IFR_TO_VFR_ARR              { @Override public Level logLevel() { return Level.INFO;  } @Override public String message() { return "IFR flight converted to VFR arrival"; } },
		IFR_TO_VFR_FAIL             { @Override public Level logLevel() { return Level.INFO;  } @Override public String message() { return "IFR flight could not be converted to VFR"; } },
		IFR_REMOVED                 { @Override public Level logLevel() { return Level.INFO;  } @Override public String message() { return "IFR flight removed"; } };

                public abstract Level logLevel();
		
		public abstract String message();
	}
	
	/**
	 * Write id to file with a message.
	 */
	public static void logReason(FlightRecord flightRecord, Reason reason)
	{
		logReason(flightRecord, reason, false);
	}	
	
	public static void logReason(FlightRecord flightRecord, Reason reason, boolean isStillValid)
	{
		// Log it
		String msg = flightRecord.getScheduleId() + ": " + reason.message();

                switch(reason.logLevel().toInt()) {
                    case Level.FATAL_INT:
                        logger.fatal(msg);
                        break;
                    case Level.ERROR_INT:
                        logger.error(msg);
                        break;
                    case Level.WARN_INT:
                        logger.warn(msg);
                        break;
                    case Level.INFO_INT:
                        logger.info(msg);
                        break;
                    case Level.DEBUG_INT:
                        logger.debug(msg);
                        break;
                    default:
                    case Level.TRACE_INT:
                        logger.trace(msg);
                        break;
                }
		
		// Set flags for postprocessing
		flightRecord.setFlag(reason, true);
		flightRecord.setValidFlight(flightRecord.isValidFlight() && isStillValid);
	}	
}