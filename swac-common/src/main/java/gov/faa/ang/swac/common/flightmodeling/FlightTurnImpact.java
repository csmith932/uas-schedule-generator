package gov.faa.ang.swac.common.flightmodeling;

/**
 * Enum for airport reconfiguration effects on flights.
 */
public enum FlightTurnImpact {
	/** Flight not affected by an airport configuration change. */
	NOT_AFFECTED,
	/**
	 * Flight affected by an airport configuration change.
	 * If it was departure flight, it was in the airport's departure queue at
	 * the time of the configuration change.
	 * If it was an arrival flight, it was inside its arrival fix at the time of
	 * the configuration change.
	 */
	JUST_BEFORE_TURN_START,
	/**
	 * Flight adversely affected by an airport configuration change.
	 * If it was a departure flight, it was in the airport's departure queue at
	 * some point during the configuration change.
	 * If it was an arrival flight, it was inside of its arrival fix's queue
	 * at some point during the configuration change.
	 */
	ADVERSELY_AFFECTED,
	/**
	 * Flight latently affected by an airport configuration change.
	 * If it was a departure flight, it entered the airport's departure queue
	 * after the configuration change, but may have been latently affected by
	 * extra queue delay because the departure queue had not emptied since the
	 * end of the change.
	 * If it was an arrival flight, it entered the airport's arrival queue after
	 * the configuration change, but may have been latently affected by extra
	 * queue delay because the arrival queue had not emptied since the end of
	 * the change.
	 */
	LATENTLY_AFFECTED
}
