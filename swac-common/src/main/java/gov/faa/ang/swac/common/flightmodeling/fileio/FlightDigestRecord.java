package gov.faa.ang.swac.common.flightmodeling.fileio;

import gov.faa.ang.swac.common.FlightPhase;
import gov.faa.ang.swac.common.datatypes.Timestamp;
import gov.faa.ang.swac.common.flightmodeling.EquipmentSuffix;
import gov.faa.ang.swac.common.flightmodeling.FlightTurnImpact;
import gov.faa.ang.swac.common.utilities.ParseFormatUtils;
import gov.faa.ang.swac.datalayer.storage.db.PrintCSVHeaderQueryBuilder;
import gov.faa.ang.swac.datalayer.storage.db.PrintCSVQueryBinder;
import gov.faa.ang.swac.datalayer.storage.db.QueryBinder;
import gov.faa.ang.swac.datalayer.storage.db.QueryBuilder;
import gov.faa.ang.swac.datalayer.storage.db.UploadableRecord;
import gov.faa.ang.swac.datalayer.storage.fileio.OutputRecord;
import gov.faa.ang.swac.datalayer.storage.fileio.TextSerializable;
import gov.faa.ang.swac.datalayer.storage.fileio.WithHeader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.EnumMap;
import java.util.Map;


/**
 * This class encapsulate information about a flight and is used to: 
 * - read a record from the flight output report (method readItem)
 * - write a header record to the flight output report (method writeHeader)
 * - write a record to the flight output report (method writeItem)
 * - create a flight output table into the database (method describeFields)
 * - insert flight output record into a database (method bindFields
 * 
 * Several important developer notes when updating this class: 
 * - When adding or removing a field from FlightDigestRecord, be sure to update the field in the following methods:
 *   readItem, writeItem, writeHeader, describeFields, bindFields 
 * - Each method should work on the same fields and in the same order.
 * - The FlightDigestRecordTest is the junit test for this class. When updating FlightDigestRecord, copy a sample report record
 * from the flight output report and update the sampleReportLine static variable in this class. Failure to do so may cause 
 * FlightDigestRecordTest to generate a test failure, causing bamboo to invoke the dreaded email of shame.
 */
public class FlightDigestRecord implements TextSerializable, WithHeader, OutputRecord, UploadableRecord {
	/**
	 * Sample line of output from a recent flight output report. Replace this with another one after adding or removing a field from the report.
	 * This variable is used by the FlightDigestRecordTest junit class. Do not delete and keep updated!
	 */
	public static final String sampleReportLine = "testScenario,20130724,2013,N733M,UES,1,SDF,1,US,20130724 17:18:48,20130724 18:30:50,108864600,108864600,108864600,108864600,1,,FA50,FA50,FA50,FA50,FA50,,G,D General Aviation,0,1038.8,1038.8,,,,1038.8,1038.8,,,1038.8,1038.8,1038.8,1038.8,20130724 17:18:48,-8.95E-06,0,1038.8,1038.8,0,1038.8,1038.8,0,,0,1038.8,1048.9233,10.1233,0,10.1233,0,0,0,1,0,1048.9233,1048.9233,0,1048.9233,1048.9233,0,0,1048.9233,1048.9233,,1048.9233,1048.9233,20130724 17:28:55,,,0,,,,,1,1062.865333,35000,1048.9233,1100.788183,0,1083.085267,FALSE,,0,0,FRIZN1,100,,,,,H35LZ,1100.788183,1100.788183,SDF_VMC,20130724 18:20:47,1100.788183,1105.418183,4,4.63,0.63,0.63,1105.418183,1105.418183,0,1105.418183,1105.418183,0,SDF_ramp,0,1105.418183,1105.418183,20130724 18:25:25,72.03333333,51.86488333,0,51.86,0,0,NOT_AFFECTED,NOT_AFFECTED,1,50,ZNY999A,348.0761917,348.0761917,343.0761917,343.0761917,440.6,440.6,440.6,341.35,341.35,341.35,76.71,76.71,76.71,16.7,858.67,858.67,858.67,51.86,51.86,51.86,858.67,858.67,858.67,12345,54321,ZAU002 ZAU062 ZAU060 ZAU083 ZAU084 ZAU047 ZID089 ZID080 ZID075 ZID081 ZID018 ZID002";
	
    private static final char SEP = ',';
	private static final String DECIMAL_FORMAT = "%1$1.2f";

	// Reported values in order of report
	private String scenarioName;
	private static final String scenarioNameHeader = "scenario_name";
	private Timestamp startDate; // Only need date portion
	private static final String startDateHeader = "forecast_date";
	private int forecastFiscalYear;
	private static final String forecastFiscalYearHeader = "forecast_fiscal_year";
	private String filedAircraftId;
	private static final String filedAircraftIdHeader = "acid";
	private String filedDepAirport;
	private static final String filedDepAirportHeader = "departure_airport";
	private Integer filedDepAirportCountryCode;
	private static final String filedDepAirportCountryCodeHeader = "departure_country_code";
	private String filedArrAirport;
	private static final String filedArrAirportHeader = "arrival_airport";
	private Integer filedArrAirportCountryCode;
	private static final String filedArrAirportCountryCodeHeader = "arrival_country_code";
	private String region;
	private static final String regionHeader = "world_region";
	private Timestamp filedGateOutTime;
	private static final String filedGateOutTimeHeader = "gate_out_time_pln";
	private Timestamp filedGateInTime;
	private static final String filedGateInTimeHeader = "gate_in_time_pln";
	private Integer simFlightId;
	private static final String simFlightIdHeader = "flight_id_sim";
	private Integer simAirframeId;
	private static final String simAirframeIdHeader = "airframe_id";
	private Integer scheduleId;
	private static final String scheduleIdHeader = "flight_id_pln";
	private Integer itineraryNumber;
	private static final String itineraryNumberHeader = "itin_number";
	private Integer flightLegNumber;
	private static final String flightLegNumberHeader = "leg_num";
	private EquipmentSuffix simEquipment;
	private static final String simEquipmentHeader = "equipment";
	private String filedEtmsAircraftType;
	private static final String filedEtmsAircraftTypeHeader = "ac_type_itin_etms_original";
	private String filedBadaAircraftType;
	private static final String filedBadaAircraftTypeHeader = "ac_type_itin_bada_original";
	private String evolvedEtmsAircraftType;
	private static final String evolvedEtmsAircraftTypeHeader = "ac_type_itin_etms_evolved";
	private String evolvedBadaAircraftType;
	private static final String evolvedBadaAircraftTypeHeader = "ac_type_itin_bada_evolved";
	private String flownBadaAircraftType;
	private static final String flownBadaAircraftTypeHeader = "ac_type_itin_bada_flown";
	private String evolvedEtmsAircraftCategory;
	private static final String evolvedEtmsAircraftCategoryHeader = "ac_category_evolved";
	private String filedUserClass;
	private static final String filedUserClassHeader = "user_class_etms";
	private String filedAtoUserClass;
	private static final String filedAtoUserClassHeader = "user_class_FAA";
	private Boolean dayOverrideFlag;
	private static final String dayOverrideFlagHeader = "aircraft_day_override";

	private Map<FlightPhase, Double> flightPhaseStartTimes = new EnumMap<FlightPhase, Double>(FlightPhase.class);
	// One header per FlightPhase
	private static final String flightPhaseStartTimeDepGateHeader = "departure_gate_queue_start";
	private Map<FlightPhase, Double> flightPhaseEndTimes = new EnumMap<FlightPhase, Double>(FlightPhase.class);
	// One header per FlightPhase
	private static final String flightPhaseEndTimeDepGateHeader = "departure_gate_queue_end";
	private Double simDepartureTurnaroundDistributionValue;
	private static final String simDepartureTurnaroundDistributionValueHeader = "turnaround_duration";
	private static final String flightPhaseStartTimeDepTurnaroundHeader = "turnaround_start";
	private static final String flightPhaseEndTimeDepTurnaroundHeader = "turnaround_end";
	private static final String flightPhaseStartTimePushbackHeader = "idle_at_gate_start";
	private static final String flightPhaseEndTimePushbackHeader = "idle_at_gate_end";
	private Timestamp assignedEdct;
	private static final String assignedEdctHeader = "edct_assigned";
	private Timestamp appliedEdct;
	private static final String appliedEdctHeader = "edct_applied";
	private static final String flightPhaseStartTimeEdctGateHeader = "edct_gate_hold_start";
	private static final String flightPhaseEndTimeEdctGateHeader = "edct_gate_hold_end";
	private static final String flightPhaseStartTimeDepartureGateQueueHeader = "surface_congestion_hold_start";
	private static final String flightPhaseEndTimeDepartureGateQueueHeader = "surface_congestion_hold_end";
	private Timestamp simGateOutTime;
	private static final String simGateOutTimeHeader = "gate_out_time_sim";
	private Double simPushbackDistributionValue;
	private static final String simPushbackDistributionValueHeader = "gate_out_delay_random";
	private Double simGateDelay;
	private static final String simGateDelayHeader = "gate_out_delay";

	private static final String flightPhaseStartTimeDepartureRampQueueHeader = "departure_ramp_queue_start";
	private static final String flightPhaseEndTimeDepartureRampQueueHeader = "departure_ramp_queue_end";
	private Double departureRampQueuingDelay;
	private static final String departureRampQueuingDelayHeader = "departure_ramp_queue_delay";
	private static final String flightPhaseStartTimeDepartureRampServiceHeader = "departure_ramp_traversal_start";
	private static final String flightPhaseEndTimeDepartureRampServiceHeader = "departure_ramp_traversal_end";
	private Double departureRampServiceTime;
	private static final String departureRampServiceTimeHeader = "departure_ramp_duration";
	private String departureRampName;
	private static final String departureRampNameHeader = "departure_ramp_name";
	private Boolean departureRampBypassFlag;
	private static final String departureRampBypassFlagHeader = "departure_ramp_bypass_flag";
	private static final String flightPhaseStartTimeTaxiOutHeader = "taxi_out_start";
	private static final String flightPhaseEndTimeTaxiOutHeader = "taxi_out_end";
	private Double simNominalTaxiOutTime;
	private static final String simNominalTaxiOutTimeHeader = "taxi_out_nominal";
	private Double simTaxiOutDelay;
	private static final String simTaxiOutDelayHeader = "taxi_out_delay";
	private Double simActualTaxiOutTime;
	private static final String simActualTaxiOutTimeHeader = "taxi_out_duration";
	private Double simDepartureSurfaceDelay;
	private static final String simDepartureSurfaceDelayHeader = "departure_surface_delay";

	private Boolean rerouteFlag;
	private static final String rerouteFlagHeader = "rerouted";
	private Boolean preDepartureRerouteFlag;
	private static final String preDepartureRerouteFlagHeader = "rerouted_predeparture";
	private Boolean oceanicRerouteFlag;
	private static final String oceanicRerouteFlagHeader = "rerouted_oceanic";
	private Boolean rerouteClearanceFlag;
	private static final String rerouteClearanceFlagHeader = "reroute_clearance_required";
	private static final String flightPhaseStartTimeRerouteClearanceHeader = "reroute_clearance_queue_start";
	private Double rerouteClearanceQueueEnd;
	private static final String rerouteClearanceQueueEndHeader = "reroute_clearance_queue_end";
	private Double simRerouteClearanceQueuingDelay;
	private static final String simRerouteClearanceQueuingDelayHeader = "reroute_clearance_queuing_dly";
	private static final String rerouteClearanceQueueEndHeader2 = "reroute_clearance_start";
	private static final String flightPhaseEndTimeRerouteClearanceHeader = "reroute_clearance_end";
	private Double simRerouteClearanceServiceTime;
	private static final String simRerouteClearanceServiceTimeHeader = "reroute_clearance_duration";
	private Double simRerouteClearanceTotalDelay;
	private static final String simRerouteClearanceTotalDelayHeader = "reroute_clearance_total_delay";

	private static final String flightPhaseStartTimeEdctSurfaceHeader = "edct_surface_hold_start";
	private static final String flightPhaseEndTimeEdctSurfaceHeader = "edct_surface_hold_end";
	private static final String simDepartureParetoCurveHeader = "departure_pareto_curve";
	private String simDepartureParetoCurve;
	private static final String flightPhaseStartTimeDepartureHeader = "departure_queue_start";
	private static final String flightPhaseEndTimeDepartureHeader = "departure_queue_end";
	private Timestamp simWheelsOffTime;
	private static final String simWheelsOffTimeHeader = "wheels_off_time";

	private Double simTotalRestrictionDelay;
	private static final String simTotalRestrictionDelayHeader = "restriction_delay_total";
	private String sidSelected;
	private static final String sidSelectedHeader = "sid_selected";
	private Integer sidFitness;
	private static final String sidFitnessHeader = "sid_fitness";
	private String simDepartureFix;
	private static final String simDepartureFixHeader = "departure_fix";
	private static final String flightPhaseStartTimeDepartureFixHeader = "departure_fix_queue_start";
	private static final String flightPhaseEndTimeDepartureFixHeader = "departure_fix_queue_end";
	private Double simDepartureFixDelay;
	private static final String simDepartureFixDelayHeader = "departure_fix_queue_delay";
	private Boolean stepClimbEligible;
	private static final String stepClimbEligibleHeader = "step_climb_eligible";
	private Double topOfClimbMins;
	private static final String topOfClimbMinsHeader = "top_of_climb";

	private Double filedCruiseAltitude;
	private static final String filedCruiseAltitudeHeader = "cruise_altitude";
	private static final String flightPhaseStartTimeSectorQueueHeader = "enroute_sectors_start";
	private static final String flightPhaseEndTimeSectorQueueHeader = "enroute_sectors_end";
	private Double simSectorDelay;
	private static final String simSectorDelayHeader = "sector_queue_delay";
	private Double topOfDescentMins;
	private static final String topOfDescentMinsHeader = "top_of_descent";

	private boolean shortenedArrivalRoute;
	private static final String shortenedArrivalHeader = "shortened_arrival";
	private String cdaCurveName;
	private static final String cdaCurveNameHeader = "cda_curve_name";
	private Double cdaProbability;
	private static final String cdaProbabilityHeader = "cda_probability";
	private Boolean cdaSelected;
	private static final String cdaSelectedHeader = "cda_selected";
	private String starSelected;
	private static final String starSelectedHeader = "star_selected";
	private Integer starFitness;
	private static final String starFitnessHeader = "star_fitness";
	private String simArrivalFix;
	private static final String simArrivalFixHeader = "arrival_fix";
	private static final String flightPhaseStartTimeArrivalFixHeader = "arrival_fix_queue_start";
	private static final String flightPhaseEndTimeArrivalFixHeader = "arrival_fix_queue_end";
	private Double simArrivalFixDelay;
	private static final String simArrivalFixDelayHeader = "arrival_fix_queue_delay";
	private String iapSelected;
	private static final String iapSelectedHeader = "iap";
	private static final String flightPhaseStartTimeArrivalHeader = "arrival_queue_start";
	private static final String flightPhaseEndTimeArrivalHeader = "arrival_queue_end";
	private static final String simArrivalParetoCurveHeader = "arrival_pareto_curve";
	private String simArrivalParetoCurve;
	private Timestamp simWheelsOnTime;
	private static final String simWheelsOnTimeHeader = "wheels_on_time";
	private static final String flightPhaseStartTimeTaxiInHeader = "taxi_in_start";
	private static final String flightPhaseEndTimeTaxiInHeader = "taxi_in_end";
	private Double simNominalTaxiInTime;
	private static final String simNominalTaxiInTimeHeader = "taxi_in_nominal";
	private Double simActualTaxiInTime;
	private static final String simActualTaxiInTimeHeader = "taxi_in_duration";
	private Double simTaxiInDelay;
	private static final String simTaxiInDelayHeader = "taxi_in_delay";
	private Double simArrivalSurfaceDelay;
	private static final String simArrivalSurfaceDelayHeader = "arrival_surface_delay";
	private static final String flightPhaseStartTimeArrivalRampQueueHeader = "arrival_ramp_queue_start";
	private static final String flightPhaseEndTimeArrivalRampQueueHeader = "arrival_ramp_queue_end";
	private Double arrivalRampQueuingDelay;
	private static final String arrivalRampQueuingDelayHeader = "arrival_ramp_queue_delay";
	private static final String flightPhaseStartTimeArrivalRampServiceHeader = "arrival_ramp_traversal_start";
	private static final String flightPhaseEndTimeArrivalRampServiceHeader = "arrival_ramp_traversal_end";
	private Double arrivalRampServiceTime;
	private static final String arrivalRampServiceTimeHeader = "arrival_ramp_duration";
	private String arrivalRampName;
	private static final String arrivalRampNameHeader = "arrival_ramp_name";
	private Boolean arrivalRampBypassFlag;
	private static final String arrivalRampBypassFlagHeader = "arrival_ramp_bypass_flag";
	private static final String flightPhaseStartTimeArrGateHeader = "arrival_gate_queue_start";
	private static final String flightPhaseEndTimeArrGateHeader = "arrival_gate_queue_end";
	private Timestamp simGateInTime;
	private static final String simGateInTimeHeader = "gate_in_time_sim";
	private Double blockTime;
	private static final String blockTimeHeader = "block_time_pln";
	private Double fcEnrouteTime;
	private static final String fcEnrouteTimeHeader = "airborne_duration_unimpeded";
	
	private Double rerouteEnrouteTime;
	private static final String rerouteEnrouteTimeHeader = "reroute_airborne_duration_unim";
	
	private Double simulatedAirborneTime;
	private static final String simulatedAirborneTimeHeader = "airborne_duration_actual";
	private Double simulatedAirborneDelay;
	private static final String simulatedAirborneDelayHeader = "airborne_delay";
	private Double totalTbfmDelay;
	private static final String totalTbfmDelayHeader = "tbfm_delay";
	
	private FlightTurnImpact departureTurnImpact;
	private static final String departureTurnImpactHeader = "departure_turn_impact";
	private FlightTurnImpact arrivalTurnImpact;
	private static final String arrivalTurnImpactHeader = "arrival_turn_impact";
	
	private boolean oceanicFlag;
	private static final String oceanicFlagHeader = "oceanic_flag";
	private double oceanicDelay;
	private static final String oceanicDelayHeader = "oceanic_delay";
	private String oceanicRegionsTraversed;
	private static final String oceanicRegionsTraversedHeader = "oceanic_regions";
	
	private double plannedRouteDistance;
	private static final String plannedRouteDistHeader = "route_dist_pln";
	private double actualRouteDistance;
	private static final String actualRouteDistHeader = "route_dist_sim";
	
	private double plannedFirDistance;
	private static final String plannedFirRouteDistHeader = "USFIR_route_dist_pln";
	private double actualFirDistance;
	private static final String actualFirRouteDistHeader = "USFIR_route_dist_sim";

	private Double plannedClimbUsage;
	private static final String plannedClimbUsageHeader = "fuel_use_climb_pln";
	private Double nominalClimbUsage;
	private static final String nominalClimbUsageHeader = "fuel_use_climb_nom";
	private Double actualClimbUsage;
	private static final String actualClimbUsageHeader = "fuel_use_climb_sim";
	
	private Double plannedCruiseUsage;
	private static final String plannedCruiseUsageHeader = "fuel_use_cruise_pln";
	private Double nominalCruiseUsage;
	private static final String nominalCruiseUsageHeader = "fuel_use_cruise_nom";
	private Double actualCruiseUsage;
	private static final String actualCruiseUsageHeader = "fuel_use_cruise_sim";
	
	private Double plannedDescentUsage;
	private static final String plannedDescentUsageHeader = "fuel_use_descent_pln";
	private Double nominalDescentUsage;
	private static final String nominalDescentUsageHeader = "fuel_use_descent_nom";
	private Double actualDescentUsage;
	private static final String actualDescentUsageHeader = "fuel_use_descent_sim";
	
	private Double cruiseUsageRate;
	private static final String cruiseUsageRateHeader = "fuel_rate_cruise";
	
	private Double plannedFirFuel;
	private static final String plannedFirFuelHeader = "fuel_use_USFIR_pln";
	private Double nominalFirFuel;
	private static final String nominalFirFuelHeader = "fuel_use_USFIR_nom";
	private Double actualFirFuel;
	private static final String actualFirFuelHeader = "fuel_use_USFIR_sim";
	
	private Double plannedFirTime;
	private static final String plannedFirTimeHeader = "USFIR_duration_pln";
	private Double nominalFirTime;
	private static final String nominalFirTimeHeader = "USFIR_duration_nom";
	private Double actualFirTime;
	private static final String actualFirTimeHeader = "USFIR_duration_sim";
	
	private Double plannedTotalFuelUsage;
	private static final String plannedTotalFuelUsageHeader = "fuel_use_filed_route_unimpeded";
	private Double nominalTotalFuelUsage;
	private static final String nominalTotalFuelUsageHeader = "fuel_use_unimpeded_route";
	private Double actualTotalFuelUsage;
	private static final String actualTotalFuelUsageHeader = "fuel_use_actual_route";
	
	private Double nominalOceanicFuelUsage;
	private static final String nominalOceanicFuelUsageHeader = "fuel_use_oceanic_nom";
	private Double actualOceanicFuelUsage;
	private static final String actualOceanicFuelUsageHeader = "fuel_use_oceanic_sim";

	private String simSectorTrace;
	private static final String simSectorTraceHeader = "sectors";

	// Unused...
	private Double simArrivalTurnaroundDistributionValue;

    @Override
    public long readHeader(BufferedReader reader) throws IOException {
        reader.readLine(); 
        return -1;
    }

	@Override
	public void writeHeader(PrintWriter pw, long numRecords) throws IOException {
		//--------------------
		// Write out header (with totals data)
		//--------------------
		QueryBuilder csvBuilder = new PrintCSVHeaderQueryBuilder(pw);
        describeFields(csvBuilder);
		pw.print("\n");
	}

	@Override
	public void readItem(BufferedReader reader) throws IOException {
		String line = reader.readLine();
		if (line == null) return;

		String [] fields = line.split(",", -1);

		int i = 0;
		scenarioName = fields[i++];
		startDate = Timestamp.myValueOf(fields[i++]);
		forecastFiscalYear = ParseFormatUtils.parseInteger(fields[i++]);
		filedAircraftId = fields[i++];
		filedDepAirport = fields[i++];
		filedDepAirportCountryCode = ParseFormatUtils.parseInteger(fields[i++]);
		filedArrAirport = fields[i++];
		filedArrAirportCountryCode = ParseFormatUtils.parseInteger(fields[i++]);
		region = ParseFormatUtils.nullIfBlank(fields[i++]);
		filedGateOutTime = Timestamp.myValueOf(fields[i++]);
		filedGateInTime = Timestamp.myValueOf(fields[i++]);
		simFlightId = ParseFormatUtils.parseInteger(fields[i++]);
		simAirframeId = ParseFormatUtils.parseInteger(fields[i++]);
		scheduleId = ParseFormatUtils.parseInteger(fields[i++]);
		itineraryNumber = ParseFormatUtils.parseInteger(fields[i++]);
		flightLegNumber = ParseFormatUtils.parseInteger(fields[i++]);
		simEquipment = EquipmentSuffix.fromTextRecord(fields[i++]);
		filedEtmsAircraftType = ParseFormatUtils.nullIfBlank(fields[i++]);
		filedBadaAircraftType = ParseFormatUtils.nullIfBlank(fields[i++]);
		evolvedEtmsAircraftType = ParseFormatUtils.nullIfBlank(fields[i++]);
		evolvedBadaAircraftType = ParseFormatUtils.nullIfBlank(fields[i++]);
		flownBadaAircraftType = fields[i++];
		evolvedEtmsAircraftCategory = fields[i++];
		filedUserClass = fields[i++];
		filedAtoUserClass = ParseFormatUtils.nullIfBlank(fields[i++]);
		dayOverrideFlag = ParseFormatUtils.parseBoolean(fields[i++]);

		flightPhaseStartTimes.put(FlightPhase.DEP_GATE, ParseFormatUtils.parseDouble(fields[i++]));
		flightPhaseEndTimes.put(FlightPhase.DEP_GATE, ParseFormatUtils.parseDouble(fields[i++]));
		simDepartureTurnaroundDistributionValue = ParseFormatUtils.parseDouble(fields[i++]);
		flightPhaseStartTimes.put(FlightPhase.DEP_TURNAROUND, ParseFormatUtils.parseDouble(fields[i++]));
		flightPhaseEndTimes.put(FlightPhase.DEP_TURNAROUND, ParseFormatUtils.parseDouble(fields[i++]));
		//simArrivalTurnaroundDistributionValue = fields[i++];
		flightPhaseStartTimes.put(FlightPhase.PUSHBACK, ParseFormatUtils.parseDouble(fields[i++]));
		flightPhaseEndTimes.put(FlightPhase.PUSHBACK, ParseFormatUtils.parseDouble(fields[i++]));
		assignedEdct = Timestamp.myValueOf(fields[i++]);
		appliedEdct = Timestamp.myValueOf(fields[i++]); 
		flightPhaseStartTimes.put(FlightPhase.EDCT_GATE, ParseFormatUtils.parseDouble(fields[i++]));
		flightPhaseEndTimes.put(FlightPhase.EDCT_GATE, ParseFormatUtils.parseDouble(fields[i++]));
		flightPhaseStartTimes.put(FlightPhase.DEPARTURE_GATE_QUEUE, ParseFormatUtils.parseDouble(fields[i++]));
		flightPhaseEndTimes.put(FlightPhase.DEPARTURE_GATE_QUEUE, ParseFormatUtils.parseDouble(fields[i++]));
		simGateOutTime = Timestamp.myValueOf(fields[i++]);
		simPushbackDistributionValue = ParseFormatUtils.parseDouble(fields[i++]);
		simGateDelay = ParseFormatUtils.parseDouble(fields[i++]);
		flightPhaseStartTimes.put(FlightPhase.DEPARTURE_RAMP_QUEUE, ParseFormatUtils.parseDouble(fields[i++]));
		flightPhaseEndTimes.put(FlightPhase.DEPARTURE_RAMP_QUEUE, ParseFormatUtils.parseDouble(fields[i++]));
		departureRampQueuingDelay = ParseFormatUtils.parseDouble(fields[i++]);
		flightPhaseStartTimes.put(FlightPhase.DEPARTURE_RAMP_SERVICE, ParseFormatUtils.parseDouble(fields[i++]));
		flightPhaseEndTimes.put(FlightPhase.DEPARTURE_RAMP_SERVICE, ParseFormatUtils.parseDouble(fields[i++]));
		departureRampServiceTime = ParseFormatUtils.parseDouble(fields[i++]);
		departureRampName = ParseFormatUtils.nullIfBlank(fields[i++]);
		departureRampBypassFlag = ParseFormatUtils.parseBoolean(fields[i++]);
		flightPhaseStartTimes.put(FlightPhase.TAXI_OUT, ParseFormatUtils.parseDouble(fields[i++]));
		flightPhaseEndTimes.put(FlightPhase.TAXI_OUT, ParseFormatUtils.parseDouble(fields[i++]));
		simNominalTaxiOutTime = ParseFormatUtils.parseDouble(fields[i++]);
		simTaxiOutDelay = ParseFormatUtils.parseDouble(fields[i++]);
		simActualTaxiOutTime = ParseFormatUtils.parseDouble(fields[i++]);
		simDepartureSurfaceDelay = ParseFormatUtils.parseDouble(fields[i++]);

		rerouteFlag = ParseFormatUtils.parseBoolean(fields[i++]);
		preDepartureRerouteFlag = ParseFormatUtils.parseBoolean(fields[i++]);
		oceanicRerouteFlag = ParseFormatUtils.parseBoolean(fields[i++]);
		rerouteClearanceFlag = ParseFormatUtils.parseBoolean(fields[i++]);
		flightPhaseStartTimes.put(FlightPhase.REROUTE_CLEARANCE, ParseFormatUtils.parseDouble(fields[i++]));
		rerouteClearanceQueueEnd = ParseFormatUtils.parseDouble(fields[i++]);
		simRerouteClearanceQueuingDelay = ParseFormatUtils.parseDouble(fields[i++]);
		i++; // reroute_clearance_start is same as rerouteClearanceQueueEnd
		flightPhaseEndTimes.put(FlightPhase.REROUTE_CLEARANCE, ParseFormatUtils.parseDouble(fields[i++]));
		simRerouteClearanceServiceTime = ParseFormatUtils.parseDouble(fields[i++]);
		simRerouteClearanceTotalDelay = ParseFormatUtils.parseDouble(fields[i++]);
		flightPhaseStartTimes.put(FlightPhase.EDCT_SURFACE, ParseFormatUtils.parseDouble(fields[i++]));
		flightPhaseEndTimes.put(FlightPhase.EDCT_SURFACE, ParseFormatUtils.parseDouble(fields[i++]));
		simDepartureParetoCurve = ParseFormatUtils.nullIfBlank(fields[i++]);
		flightPhaseStartTimes.put(FlightPhase.DEPARTURE, ParseFormatUtils.parseDouble(fields[i++]));
		flightPhaseEndTimes.put(FlightPhase.DEPARTURE, ParseFormatUtils.parseDouble(fields[i++]));
		simWheelsOffTime = Timestamp.myValueOf(fields[i++]);

		simTotalRestrictionDelay = ParseFormatUtils.parseDouble(fields[i++]);
		sidSelected = ParseFormatUtils.nullIfBlank(fields[i++]);
		sidFitness = ParseFormatUtils.parseInteger(fields[i++]);
		simDepartureFix = ParseFormatUtils.nullIfBlank(fields[i++]);
		flightPhaseStartTimes.put(FlightPhase.DEPARTURE_FIX, ParseFormatUtils.parseDouble(fields[i++]));
		flightPhaseEndTimes.put(FlightPhase.DEPARTURE_FIX, ParseFormatUtils.parseDouble(fields[i++]));
		simDepartureFixDelay = ParseFormatUtils.parseDouble(fields[i++]);
		stepClimbEligible = ParseFormatUtils.parseBoolean(fields[i++]);
		topOfClimbMins = ParseFormatUtils.parseDouble(fields[i++]);

		filedCruiseAltitude = ParseFormatUtils.parseDouble(fields[i++]);
		flightPhaseStartTimes.put(FlightPhase.SECTOR_QUEUE, ParseFormatUtils.parseDouble(fields[i++]));
		flightPhaseEndTimes.put(FlightPhase.SECTOR_QUEUE, ParseFormatUtils.parseDouble(fields[i++]));
		simSectorDelay = ParseFormatUtils.parseDouble(fields[i++]);
		topOfDescentMins = ParseFormatUtils.parseDouble(fields[i++]);

		shortenedArrivalRoute = ParseFormatUtils.parseBoolean(fields[i++]);
		cdaCurveName = ParseFormatUtils.nullIfBlank(fields[i++]);
		cdaProbability = ParseFormatUtils.parseDouble(fields[i++]);
		cdaSelected = ParseFormatUtils.parseBoolean(fields[i++]);
		starSelected = ParseFormatUtils.nullIfBlank(fields[i++]);
		starFitness = ParseFormatUtils.parseInteger(fields[i++]);
		simArrivalFix = ParseFormatUtils.nullIfBlank(fields[i++]);
		flightPhaseStartTimes.put(FlightPhase.ARRIVAL_FIX, ParseFormatUtils.parseDouble(fields[i++]));
		flightPhaseEndTimes.put(FlightPhase.ARRIVAL_FIX, ParseFormatUtils.parseDouble(fields[i++]));
		simArrivalFixDelay = ParseFormatUtils.parseDouble(fields[i++]);
		iapSelected = ParseFormatUtils.nullIfBlank(fields[i++]);
		flightPhaseStartTimes.put(FlightPhase.ARRIVAL, ParseFormatUtils.parseDouble(fields[i++]));
		flightPhaseEndTimes.put(FlightPhase.ARRIVAL, ParseFormatUtils.parseDouble(fields[i++]));
		simArrivalParetoCurve = ParseFormatUtils.nullIfBlank(fields[i++]);
		simWheelsOnTime = Timestamp.myValueOf(fields[i++]);
		flightPhaseStartTimes.put(FlightPhase.TAXI_IN, ParseFormatUtils.parseDouble(fields[i++]));
		flightPhaseEndTimes.put(FlightPhase.TAXI_IN, ParseFormatUtils.parseDouble(fields[i++]));
		simNominalTaxiInTime = ParseFormatUtils.parseDouble(fields[i++]);
		simActualTaxiInTime = ParseFormatUtils.parseDouble(fields[i++]);
		simTaxiInDelay = ParseFormatUtils.parseDouble(fields[i++]);
		simArrivalSurfaceDelay = ParseFormatUtils.parseDouble(fields[i++]);
		flightPhaseStartTimes.put(FlightPhase.ARRIVAL_RAMP_QUEUE, ParseFormatUtils.parseDouble(fields[i++]));
		flightPhaseEndTimes.put(FlightPhase.ARRIVAL_RAMP_QUEUE, ParseFormatUtils.parseDouble(fields[i++]));
		arrivalRampQueuingDelay = ParseFormatUtils.parseDouble(fields[i++]);
		flightPhaseStartTimes.put(FlightPhase.ARRIVAL_RAMP_SERVICE, ParseFormatUtils.parseDouble(fields[i++]));
		flightPhaseEndTimes.put(FlightPhase.ARRIVAL_RAMP_SERVICE, ParseFormatUtils.parseDouble(fields[i++]));
		arrivalRampServiceTime = ParseFormatUtils.parseDouble(fields[i++]);
		arrivalRampName = ParseFormatUtils.nullIfBlank(fields[i++]);
		arrivalRampBypassFlag = ParseFormatUtils.parseBoolean(fields[i++]);
		flightPhaseStartTimes.put(FlightPhase.ARR_GATE, ParseFormatUtils.parseDouble(fields[i++]));
		flightPhaseEndTimes.put(FlightPhase.ARR_GATE, ParseFormatUtils.parseDouble(fields[i++]));
		simGateInTime = Timestamp.myValueOf(fields[i++]);
		blockTime = ParseFormatUtils.parseDouble(fields[i++]);
		fcEnrouteTime = ParseFormatUtils.parseDouble(fields[i++]);
		this.rerouteEnrouteTime = ParseFormatUtils.parseDouble(fields[i++]);
		simulatedAirborneTime = ParseFormatUtils.parseDouble(fields[i++]);
		simulatedAirborneDelay = ParseFormatUtils.parseDouble(fields[i++]);
		totalTbfmDelay = ParseFormatUtils.parseDouble(fields[i++]); 
		departureTurnImpact = FlightTurnImpact.valueOf(fields[i++]);
		arrivalTurnImpact = FlightTurnImpact.valueOf(fields[i++]);
		
		oceanicFlag = ParseFormatUtils.parseBoolean(fields[i++]);
		oceanicDelay = ParseFormatUtils.parseDouble(fields[i++]);
		oceanicRegionsTraversed = ParseFormatUtils.parseString(fields[i++]);
		
		this.plannedRouteDistance = ParseFormatUtils.parseDouble(fields[i++]);
		this.actualRouteDistance = ParseFormatUtils.parseDouble(fields[i++]);
		
		this.plannedFirDistance = ParseFormatUtils.parseDouble(fields[i++]);
		this.actualFirDistance = ParseFormatUtils.parseDouble(fields[i++]);

		plannedClimbUsage = ParseFormatUtils.parseDouble(fields[i++]);
		nominalClimbUsage = ParseFormatUtils.parseDouble(fields[i++]);
		actualClimbUsage = ParseFormatUtils.parseDouble(fields[i++]);
		
		plannedCruiseUsage = ParseFormatUtils.parseDouble(fields[i++]);
		nominalCruiseUsage = ParseFormatUtils.parseDouble(fields[i++]);
		actualCruiseUsage = ParseFormatUtils.parseDouble(fields[i++]);
		
		plannedDescentUsage = ParseFormatUtils.parseDouble(fields[i++]);
		nominalDescentUsage = ParseFormatUtils.parseDouble(fields[i++]);
		actualDescentUsage = ParseFormatUtils.parseDouble(fields[i++]);
		
		cruiseUsageRate = ParseFormatUtils.parseDouble(fields[i++]);
		
		plannedFirFuel = ParseFormatUtils.parseDouble(fields[i++]);
		nominalFirFuel = ParseFormatUtils.parseDouble(fields[i++]);
		actualFirFuel = ParseFormatUtils.parseDouble(fields[i++]);
		
		plannedFirTime = ParseFormatUtils.parseDouble(fields[i++]);
		nominalFirTime = ParseFormatUtils.parseDouble(fields[i++]);
		actualFirTime = ParseFormatUtils.parseDouble(fields[i++]);
		
		plannedTotalFuelUsage = ParseFormatUtils.parseDouble(fields[i++]);
		nominalTotalFuelUsage = ParseFormatUtils.parseDouble(fields[i++]);
		actualTotalFuelUsage = ParseFormatUtils.parseDouble(fields[i++]);
		
		nominalOceanicFuelUsage = ParseFormatUtils.parseDouble(fields[i++]);
		actualOceanicFuelUsage = ParseFormatUtils.parseDouble(fields[i++]);

		simSectorTrace = ParseFormatUtils.nullIfBlank(fields[i++]);

	}
    
	@Override
	public void writeItem(PrintWriter pw) throws IOException {
		QueryBinder csvBuilder = new PrintCSVQueryBinder(pw);
        try{
        	bindFields(csvBuilder);
        	pw.println();
        }
        catch(Exception e){
        	throw new IOException(e);
        }	
	}
    
	public void setScenarioName(String scenarioName) {
		this.scenarioName = scenarioName;
	}

	public void setForecastFiscalYear(int forecastFiscalYear) {
		this.forecastFiscalYear = forecastFiscalYear;
	}
	
	public Integer getItineraryNumber() {
		return itineraryNumber;
	}
	
	public void setItineraryNumber(Integer itineraryNumber) {
		this.itineraryNumber = itineraryNumber;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public Integer getFlightLegNumber() {
		return flightLegNumber;
	}

	public void setFlightLegNumber(Integer flightLegNumber) {
		this.flightLegNumber = flightLegNumber;
	}

	public Integer getScheduleId() {
		return scheduleId;
	}

	public void setScheduleId(Integer scheduleId) {
		this.scheduleId = scheduleId;
	}

	public void setSimAirframeId(Integer simAirframeId) {
		this.simAirframeId = simAirframeId;
	}

	public Integer getSimAirframeId() {
		return simAirframeId;
	}

	public Integer getSimFlightId() {
		return simFlightId;
	}

	public void setSimFlightId(Integer simFlightId) {
		this.simFlightId = simFlightId;
	}

	public void setFiledAircraftId(String filedAircraftId) {
		this.filedAircraftId = filedAircraftId;
	}

	public void setFiledEtmsAircraftType(String filedEtmsAircraftType) {
		this.filedEtmsAircraftType = filedEtmsAircraftType;
	}

	public void setFiledBadaAircraftType(String filedBadaAircraftType) {
		this.filedBadaAircraftType = filedBadaAircraftType;
	}

	public void setEvolvedEtmsAircraftType(String evolvedEtmsAircraftType) {
		this.evolvedEtmsAircraftType = evolvedEtmsAircraftType;
	}

	public void setEvolvedBadaAircraftType(String evolvedBadaAircraftType) {
		this.evolvedBadaAircraftType = evolvedBadaAircraftType;
	}

	public void setFlownBadaAircraftType(String flownBadaAircraftType) {
		this.flownBadaAircraftType = flownBadaAircraftType;
	}

	public void setSimEquipment(EquipmentSuffix simEquipment) {
		this.simEquipment = simEquipment;
	}

	public void setDayOverrideFlag(Boolean dayOverrideFlag) {
		this.dayOverrideFlag = dayOverrideFlag;
	}

	public void setRerouteClearanceFlag(Boolean rerouteClearanceFlag) {
		this.rerouteClearanceFlag = rerouteClearanceFlag;
	}

	public void setPreDepartureRerouteFlag(Boolean preDepartureRerouteFlag) {
		this.preDepartureRerouteFlag = preDepartureRerouteFlag;
	}

	public void setRerouteFlag(Boolean rerouteFlag) {
		this.rerouteFlag = rerouteFlag;
	}

	public void setEvolvedEtmsAircraftCategory(String evolvedEtmsAircraftCategory) {
		this.evolvedEtmsAircraftCategory = evolvedEtmsAircraftCategory;
	}

	public void setFiledUserClass(String filedUserClass) {
		this.filedUserClass = filedUserClass;
	}

	public void setFiledAtoUserClass(String filedAtoUserClass) {
		this.filedAtoUserClass = filedAtoUserClass;
	}

	public String getFiledDepAirport() {
		return filedDepAirport;
	}

	public void setFiledDepAirport(String filedDepAirport) {
		this.filedDepAirport = filedDepAirport;
	}

	public void setFiledDepAirportCountryCode(Integer filedDepAirportCountryCode) {
		this.filedDepAirportCountryCode = filedDepAirportCountryCode;
	}

	public String getFiledArrAirport() {
		return filedArrAirport;
	}

	public void setFiledArrAirport(String filedArrAirport) {
		this.filedArrAirport = filedArrAirport;
	}

	public void setFiledArrAirportCountryCode(Integer filedArrAirportCountryCode) {
		this.filedArrAirportCountryCode = filedArrAirportCountryCode;
	}

	public void setStartDate(Timestamp startDate) {
		this.startDate = startDate;
	}

	public void setFiledGateOutTime(Timestamp filedGateOutTime) {
		this.filedGateOutTime = filedGateOutTime;
	}

	public void setFiledGateInTime(Timestamp filedGateInTime) {
		this.filedGateInTime = filedGateInTime;
	}

	public void setAssignedEdct(Timestamp assignedEdct) {
		this.assignedEdct = assignedEdct;
	}
	
	public void setAppliedEdct(Timestamp appliedEdct) { 
		this.appliedEdct = appliedEdct;
	}

	public void setSimGateOutTime(Timestamp simGateOutTime) {
		this.simGateOutTime = simGateOutTime;
	}

	public void setSimWheelsOffTime(Timestamp simWheelsOffTime) {
		this.simWheelsOffTime = simWheelsOffTime;
	}

	public void setSimWheelsOnTime(Timestamp simWheelsOnTime) {
		this.simWheelsOnTime = simWheelsOnTime;
	}

	public void setSimGateInTime(Timestamp simGateInTime) {
		this.simGateInTime = simGateInTime;
	}

	public void setBlockTime(Double blockTime) {
		this.blockTime = blockTime;
	}
	
	public void setFcEnrouteTime(Double fcEnrouteTime) {
		this.fcEnrouteTime = fcEnrouteTime;
	}
	
	public void setRerouteEnrouteTime(Double rerouteEnrouteTime) {
		this.rerouteEnrouteTime = rerouteEnrouteTime;
	}

	public void setSimulatedAirborneTime(Double simulatedAirborneTime) {
		this.simulatedAirborneTime = simulatedAirborneTime;
	}

	public void setSimulatedAirborneDelay(Double simulatedAirborneDelay) {
		this.simulatedAirborneDelay = simulatedAirborneDelay;
	}
	
	public void setTbfmDelay(Double simulatedTbfmDelay) {
		this.totalTbfmDelay = simulatedTbfmDelay;
	}
	
	public void setDepartureTurnImpact(FlightTurnImpact impact) {
		this.departureTurnImpact = impact;
	}
	
	public void setArrivalTurnImpact(FlightTurnImpact impact) {
		this.arrivalTurnImpact = impact;
	}

	public Double getSimIdleEndTime() {
		return flightPhaseEndTimes.get(FlightPhase.PUSHBACK);
	}
	
	public Double getSimArrivalGateQueueEndTime() {
		return flightPhaseEndTimes.get(FlightPhase.ARR_GATE);
	}

	public void setFlightPhaseStartAndEndTime(FlightPhase flightPhase, Double startTime, Double endTime) {
		flightPhaseStartTimes.put(flightPhase, startTime);
		flightPhaseEndTimes.put(flightPhase, endTime);
	}

	public void setSimGateDelay(Double simGateDelay) {
		this.simGateDelay = simGateDelay;
	}
	
	public void setSimDepartureSurfaceDelay(Double simDepartureSurfaceDelay) {
		this.simDepartureSurfaceDelay = simDepartureSurfaceDelay;
	}
	
	public void setSimArrivalSurfaceDelay(Double simArrivalSurfaceDelay) {
		this.simArrivalSurfaceDelay = simArrivalSurfaceDelay;
	}
	
	public void setSimSectorDelay(Double simSectorDelay) {
		this.simSectorDelay = simSectorDelay;
	}
	
	public Double getSimDepartureTurnaroundDistributionValue() {
		return simDepartureTurnaroundDistributionValue;
	}
	
	public void setSimDepartureTurnaroundDistributionValue(Double simDepartureTurnaroundDistributionValue) {
		this.simDepartureTurnaroundDistributionValue = simDepartureTurnaroundDistributionValue;
	}
	
	public Double getSimArrivalTurnaroundDistributionValue() {
		return simArrivalTurnaroundDistributionValue;
	}
	
	public void setSimArrivalTurnaroundDistributionValue(Double simArrivalTurnaroundDistributionValue) {
		this.simArrivalTurnaroundDistributionValue = simArrivalTurnaroundDistributionValue;
	}
	
	public void setSimPushbackDistributionValue(Double simPushbackDistributionValue) {
		this.simPushbackDistributionValue = simPushbackDistributionValue;
	}
	
	public void setDepartureRampName(String departureRampName) {
		this.departureRampName = departureRampName;
	}
	
	public void setDepartureRampQueuingDelay(Double departureRampQueuingDelay) {
		this.departureRampQueuingDelay = departureRampQueuingDelay;
	}
	
	public void setDepartureRampServiceTime(Double departureRampServiceTime) {
		this.departureRampServiceTime = departureRampServiceTime;
	}
	
	public void setDepartureRampBypassFlag(Boolean departureRampBypassFlag) {
		this.departureRampBypassFlag = departureRampBypassFlag;
	}
	
	public void setTopOfClimb(Double topOfClimbMins) {
		this.topOfClimbMins = topOfClimbMins;
	}

	public void setTopOfDescent(Double topOfDescentMins) {
		this.topOfDescentMins = topOfDescentMins;
	}

	public void setArrivalRampName(String arrivalRampName) {
		this.arrivalRampName = arrivalRampName;
	}
	
	public void setArrivalRampQueuingDelay(Double arrivalRampQueuingDelay) {
		this.arrivalRampQueuingDelay = arrivalRampQueuingDelay;
	}
	
	public void setArrivalRampServiceTime(Double arrivalRampServiceTime) {
		this.arrivalRampServiceTime = arrivalRampServiceTime;
	}
	
	public void setArrivalRampBypassFlag(Boolean arrivalRampBypassFlag) {
		this.arrivalRampBypassFlag = arrivalRampBypassFlag;
	}
	
	public void setSimNominalTaxiOutTime(Double simNominalTaxiOutTime) {
		this.simNominalTaxiOutTime = simNominalTaxiOutTime;
	}
	
	public void setSimTaxiOutDelay(Double simTaxiOutDelay) {
		this.simTaxiOutDelay = simTaxiOutDelay;
	}

	public void setSimActualTaxiOutTime(Double simActualTaxiOutTime) {
		this.simActualTaxiOutTime = simActualTaxiOutTime;
	}
	
	public void setSimNominalTaxiInTime(Double simNominalTaxiInTime) {
		this.simNominalTaxiInTime = simNominalTaxiInTime;
	}
	
	public void setSimActualTaxiInTime(Double simActualTaxiInTime) {
		this.simActualTaxiInTime = simActualTaxiInTime;
	}
	
	public void setSimTaxiInDelay(Double simTaxiInDelay) {
		this.simTaxiInDelay = simTaxiInDelay;
	}
	
	public void setSimRerouteClearanceQueuingDelay(Double simRerouteClearanceQueuingDelay) {
		this.simRerouteClearanceQueuingDelay = simRerouteClearanceQueuingDelay;
	}
	
	public void setSimRerouteClearanceServiceTime(Double simRerouteClearanceServiceTime) {
		this.simRerouteClearanceServiceTime = simRerouteClearanceServiceTime;
	}
	
	public void setSimRerouteClearanceTotalDelay(Double simRerouteClearanceTotalDelay) {
		this.simRerouteClearanceTotalDelay = simRerouteClearanceTotalDelay;
	}
	
	public void setRerouteClearanceQueueEnd(Double rerouteClearanceQueueEnd) {
		this.rerouteClearanceQueueEnd = rerouteClearanceQueueEnd; 
	}

	public void setSimDepartureFix(String simDepartureFix) {
		this.simDepartureFix = simDepartureFix;
	}
	
	public void setSimDepartureFixDelay(Double simDepartureFixDelay) {
		this.simDepartureFixDelay = simDepartureFixDelay;
	}
	
	public void setSimArrivalFix(String simArrivalFix) {
		this.simArrivalFix = simArrivalFix;
	}
	
	public void setSimArrivalFixDelay(Double simArrivalFixDelay) {
		this.simArrivalFixDelay = simArrivalFixDelay;
	}
	
	public void setSimTotalRestrictionDelay(Double simTotalRestrictionDelay) {
		this.simTotalRestrictionDelay = simTotalRestrictionDelay;
	}
	
	public void setPlannedRouteDist(Double plannedRouteDist){
		this.plannedRouteDistance = plannedRouteDist;
	}
	
	public void setActualRouteDist(Double actualRouteDist){
		this.actualRouteDistance = actualRouteDist;
	}
	
	public void setPlannedFirDist(Double plannedFirDist){
		this.plannedFirDistance = plannedFirDist;
	}
	
	public void setActualFirDist(Double actualFirDist){
		this.actualFirDistance = actualFirDist;
	}
	
	public void setPlannedClimbUsage(Double plannedClimbUsage) {
		this.plannedClimbUsage = plannedClimbUsage;
	}

	public void setNominalClimbUsage(Double nominalClimbUsage) {
		this.nominalClimbUsage = nominalClimbUsage;
	}
	
	public void setActualClimbUsage(Double actualClimbUsage) {
		this.actualClimbUsage = actualClimbUsage;
	}

	public void setPlannedCruiseUsage(Double plannedCruiseUsage) {
		this.plannedCruiseUsage = plannedCruiseUsage;
	}

	public void setNominalCruiseUsage(Double nominalCruiseUsage) {
		this.nominalCruiseUsage = nominalCruiseUsage;
	}
	
	public void setActualCruiseUsage(Double actualCruiseUsage){
		this.actualCruiseUsage = actualCruiseUsage;
	}

	public void setPlannedDescentUsage(Double plannedDescentUsage) {
		this.plannedDescentUsage = plannedDescentUsage;
	}

	public void setNominalDescentUsage(Double nominalDescentUsage) {
		this.nominalDescentUsage = nominalDescentUsage;
	}
	
	public void setActualDescentUsage(Double actualDescentUsage){
		this.actualDescentUsage = actualDescentUsage;
	}

	public void setCruiseUsageRate(Double cruiseUsageRate) {
		this.cruiseUsageRate = cruiseUsageRate;
	}

	public void setPlannedFirFuel(Double plannedFirFuel) {
		this.plannedFirFuel = plannedFirFuel;
	}

	public void setNominalFirFuel(Double nominalFirFuel) {
		this.nominalFirFuel = nominalFirFuel;
	}
	
	public void setActualFirFuel(Double actualFirFuel){
		this.actualFirFuel = actualFirFuel;
	}

	public void setPlannedFirTime(Double plannedFirTime) {
		this.plannedFirTime = plannedFirTime;
	}

	public void setNominalFirTime(Double nominalFirTime) {
		this.nominalFirTime = nominalFirTime;
	}
	
	public void setActualFirTime(Double actualFirTime) {
		this.actualFirTime = actualFirTime;
	}

	public void setPlannedTotalFuelUsage(Double plannedTotalFuelUsage) {
		this.plannedTotalFuelUsage = plannedTotalFuelUsage;
	}

	public void setNominalTotalFuelUsage(Double nominalTotalFuelUsage) {
		this.nominalTotalFuelUsage = nominalTotalFuelUsage;
	}

	public void setActualTotalFuelUsage(Double actualTotalFuelUsage) {
		this.actualTotalFuelUsage = actualTotalFuelUsage;
	}

	public void setStepClimbEligible(Boolean stepClimbEligible) {
		this.stepClimbEligible = stepClimbEligible;
	}
	
	public void setShortenedArrival(boolean shortenedArrival){
		this.shortenedArrivalRoute = shortenedArrival;
	}

	public void setCdaCurveName(String cdaCurveName) {
		this.cdaCurveName = cdaCurveName;
	}

	public void setCdaProbability(Double cdaProbability) {
		this.cdaProbability = cdaProbability;
	}

	public void setCdaSelected(Boolean cdaSelected) {
		this.cdaSelected = cdaSelected;
	}

	public void setSidSelected(String sidSelected) {
		this.sidSelected = sidSelected;
	}

	public void setSidFitness(Integer sidFitness) {
		this.sidFitness = sidFitness;
	}

	public void setStarSelected(String starSelected) {
		this.starSelected = starSelected;
	}

	public void setStarFitness(Integer starFitness) {
		this.starFitness = starFitness;
	}

	public void setIapSelected(String iapSelected) {
		this.iapSelected = iapSelected;
	}

	public void setFiledCruiseAltitude(Double filedCruiseAltitude) {
		this.filedCruiseAltitude = filedCruiseAltitude;
	}

	public void setSimSectorTrace(String simSectorTrace) {
		this.simSectorTrace = simSectorTrace;
	}
	
	public String getSimSectorTrace() {
		return simSectorTrace;
	}

	public void setFiledEtmsAircraftCategory(String filedEtmsAircraftCategory) {
	}

	public void setSimWxEquipmentArrivalQueueDelay(Double simWxEquipmentArrivalQueueDelay) {
	}
	
	@Override
	public void describeFields(QueryBuilder queryBuilder) {
		queryBuilder.addVarCharField(scenarioNameHeader);
		queryBuilder.addDateField(startDateHeader);
		queryBuilder.addIntField(forecastFiscalYearHeader);
		queryBuilder.addVarCharField(filedAircraftIdHeader);
		queryBuilder.addVarCharField(filedDepAirportHeader);
		queryBuilder.addIntField(filedDepAirportCountryCodeHeader);
		queryBuilder.addVarCharField(filedArrAirportHeader);
		queryBuilder.addIntField(filedArrAirportCountryCodeHeader);
		queryBuilder.addVarCharField(regionHeader);
		queryBuilder.addDateTimeField(filedGateOutTimeHeader);
		queryBuilder.addDateTimeField(filedGateInTimeHeader);
		queryBuilder.addIntField(simFlightIdHeader);
		queryBuilder.addIntField(simAirframeIdHeader);
		queryBuilder.addIntField(scheduleIdHeader);
		queryBuilder.addIntField(itineraryNumberHeader);
		queryBuilder.addIntField(flightLegNumberHeader);
		queryBuilder.addVarCharField(simEquipmentHeader); // Instead of normalizing the list in another table, just use toString()...
		queryBuilder.addVarCharField(filedEtmsAircraftTypeHeader);
		queryBuilder.addVarCharField(filedBadaAircraftTypeHeader);
		queryBuilder.addVarCharField(evolvedEtmsAircraftTypeHeader);
		queryBuilder.addVarCharField(evolvedBadaAircraftTypeHeader);
		queryBuilder.addVarCharField(flownBadaAircraftTypeHeader);
		queryBuilder.addVarCharField(evolvedEtmsAircraftCategoryHeader);
		queryBuilder.addVarCharField(filedUserClassHeader);
		queryBuilder.addVarCharField(filedAtoUserClassHeader);
		queryBuilder.addBooleanField(dayOverrideFlagHeader);

		queryBuilder.addDoubleField(flightPhaseStartTimeDepGateHeader);
		queryBuilder.addDoubleField(flightPhaseEndTimeDepGateHeader);
		queryBuilder.addDoubleField(simDepartureTurnaroundDistributionValueHeader);
		queryBuilder.addDoubleField(flightPhaseStartTimeDepTurnaroundHeader);
		queryBuilder.addDoubleField(flightPhaseEndTimeDepTurnaroundHeader);
		queryBuilder.addDoubleField(flightPhaseStartTimePushbackHeader);
		queryBuilder.addDoubleField(flightPhaseEndTimePushbackHeader);
		queryBuilder.addDateTimeField(assignedEdctHeader);
		queryBuilder.addDateTimeField(appliedEdctHeader);
		queryBuilder.addDoubleField(flightPhaseStartTimeEdctGateHeader);
		queryBuilder.addDoubleField(flightPhaseEndTimeEdctGateHeader);
		queryBuilder.addDoubleField(flightPhaseStartTimeDepartureGateQueueHeader);
		queryBuilder.addDoubleField(flightPhaseEndTimeDepartureGateQueueHeader);
		queryBuilder.addDateTimeField(simGateOutTimeHeader);
		queryBuilder.addDoubleField(simPushbackDistributionValueHeader);
		queryBuilder.addDoubleField(simGateDelayHeader);

		queryBuilder.addDoubleField(flightPhaseStartTimeDepartureRampQueueHeader);
		queryBuilder.addDoubleField(flightPhaseEndTimeDepartureRampQueueHeader);
		queryBuilder.addDoubleField(departureRampQueuingDelayHeader);
		queryBuilder.addDoubleField(flightPhaseStartTimeDepartureRampServiceHeader);
		queryBuilder.addDoubleField(flightPhaseEndTimeDepartureRampServiceHeader);
		queryBuilder.addDoubleField(departureRampServiceTimeHeader);
		queryBuilder.addVarCharField(departureRampNameHeader);
		queryBuilder.addBooleanField(departureRampBypassFlagHeader);
		queryBuilder.addDoubleField(flightPhaseStartTimeTaxiOutHeader);
		queryBuilder.addDoubleField(flightPhaseEndTimeTaxiOutHeader);
		queryBuilder.addDoubleField(simNominalTaxiOutTimeHeader);
		queryBuilder.addDoubleField(simTaxiOutDelayHeader);
		queryBuilder.addDoubleField(simActualTaxiOutTimeHeader);
		queryBuilder.addDoubleField(simDepartureSurfaceDelayHeader);

		queryBuilder.addBooleanField(rerouteFlagHeader);
		queryBuilder.addBooleanField(preDepartureRerouteFlagHeader);
		queryBuilder.addBooleanField(oceanicRerouteFlagHeader);
		queryBuilder.addBooleanField(rerouteClearanceFlagHeader);
		queryBuilder.addDoubleField(flightPhaseStartTimeRerouteClearanceHeader);
		queryBuilder.addDoubleField(rerouteClearanceQueueEndHeader);
		queryBuilder.addDoubleField(simRerouteClearanceQueuingDelayHeader);
		queryBuilder.addDoubleField(rerouteClearanceQueueEndHeader2); // May not be needed
		queryBuilder.addDoubleField(flightPhaseEndTimeRerouteClearanceHeader);
		queryBuilder.addDoubleField(simRerouteClearanceServiceTimeHeader);
		queryBuilder.addDoubleField(simRerouteClearanceTotalDelayHeader);

		queryBuilder.addDoubleField(flightPhaseStartTimeEdctSurfaceHeader);
		queryBuilder.addDoubleField(flightPhaseEndTimeEdctSurfaceHeader);
		queryBuilder.addVarCharField(simDepartureParetoCurveHeader);
		queryBuilder.addDoubleField(flightPhaseStartTimeDepartureHeader);
		queryBuilder.addDoubleField(flightPhaseEndTimeDepartureHeader);
		queryBuilder.addDateTimeField(simWheelsOffTimeHeader);

		queryBuilder.addDoubleField(simTotalRestrictionDelayHeader);
		queryBuilder.addVarCharField(sidSelectedHeader);
		queryBuilder.addIntField(sidFitnessHeader);
		queryBuilder.addVarCharField(simDepartureFixHeader);
		queryBuilder.addDoubleField(flightPhaseStartTimeDepartureFixHeader);
		queryBuilder.addDoubleField(flightPhaseEndTimeDepartureFixHeader);
		queryBuilder.addDoubleField(simDepartureFixDelayHeader);
		queryBuilder.addBooleanField(stepClimbEligibleHeader);
		queryBuilder.addDoubleField(topOfClimbMinsHeader);

		queryBuilder.addDoubleField(filedCruiseAltitudeHeader);
		queryBuilder.addDoubleField(flightPhaseStartTimeSectorQueueHeader);
		queryBuilder.addDoubleField(flightPhaseEndTimeSectorQueueHeader);
		queryBuilder.addDoubleField(simSectorDelayHeader);
		queryBuilder.addDoubleField(topOfDescentMinsHeader);

		queryBuilder.addBooleanField(shortenedArrivalHeader);
		queryBuilder.addVarCharField(cdaCurveNameHeader);
		queryBuilder.addDoubleField(cdaProbabilityHeader);
		queryBuilder.addBooleanField(cdaSelectedHeader);
		queryBuilder.addVarCharField(starSelectedHeader);
		queryBuilder.addIntField(starFitnessHeader);
		queryBuilder.addVarCharField(simArrivalFixHeader);
		queryBuilder.addDoubleField(flightPhaseStartTimeArrivalFixHeader);
		queryBuilder.addDoubleField(flightPhaseEndTimeArrivalFixHeader);
		queryBuilder.addDoubleField(simArrivalFixDelayHeader);
		queryBuilder.addVarCharField(iapSelectedHeader);
		queryBuilder.addDoubleField(flightPhaseStartTimeArrivalHeader);
		queryBuilder.addDoubleField(flightPhaseEndTimeArrivalHeader);
		queryBuilder.addVarCharField(simArrivalParetoCurveHeader);
		queryBuilder.addDateTimeField(simWheelsOnTimeHeader);
		queryBuilder.addDoubleField(flightPhaseStartTimeTaxiInHeader);
		queryBuilder.addDoubleField(flightPhaseEndTimeTaxiInHeader);
		queryBuilder.addDoubleField(simNominalTaxiInTimeHeader);
		queryBuilder.addDoubleField(simActualTaxiInTimeHeader);
		queryBuilder.addDoubleField(simTaxiInDelayHeader);
		queryBuilder.addDoubleField(simArrivalSurfaceDelayHeader);
		queryBuilder.addDoubleField(flightPhaseStartTimeArrivalRampQueueHeader);
		queryBuilder.addDoubleField(flightPhaseEndTimeArrivalRampQueueHeader);
		queryBuilder.addDoubleField(arrivalRampQueuingDelayHeader);
		queryBuilder.addDoubleField(flightPhaseStartTimeArrivalRampServiceHeader);
		queryBuilder.addDoubleField(flightPhaseEndTimeArrivalRampServiceHeader);
		queryBuilder.addDoubleField(arrivalRampServiceTimeHeader);
		queryBuilder.addVarCharField(arrivalRampNameHeader);
		queryBuilder.addBooleanField(arrivalRampBypassFlagHeader);
		queryBuilder.addDoubleField(flightPhaseStartTimeArrGateHeader);
		queryBuilder.addDoubleField(flightPhaseEndTimeArrGateHeader);
		queryBuilder.addDateTimeField(simGateInTimeHeader);
		queryBuilder.addDoubleField(blockTimeHeader);
		queryBuilder.addDoubleField(fcEnrouteTimeHeader);
		queryBuilder.addDoubleField(rerouteEnrouteTimeHeader);
		queryBuilder.addDoubleField(simulatedAirborneTimeHeader);
		queryBuilder.addDoubleField(simulatedAirborneDelayHeader);
		queryBuilder.addDoubleField(totalTbfmDelayHeader);
		queryBuilder.addEnumField(departureTurnImpactHeader, FlightTurnImpact.class);
		queryBuilder.addEnumField(arrivalTurnImpactHeader, FlightTurnImpact.class);
		
		queryBuilder.addBooleanField(oceanicFlagHeader);
		queryBuilder.addDoubleField(oceanicDelayHeader);
		queryBuilder.addVarCharField(oceanicRegionsTraversedHeader);

		queryBuilder.addDoubleField(plannedRouteDistHeader);
		queryBuilder.addDoubleField(actualRouteDistHeader);
		
		queryBuilder.addDoubleField(plannedFirRouteDistHeader);
		queryBuilder.addDoubleField(actualFirRouteDistHeader);
		
		queryBuilder.addDoubleField(plannedClimbUsageHeader);
		queryBuilder.addDoubleField(nominalClimbUsageHeader);
		queryBuilder.addDoubleField(actualClimbUsageHeader);
		
		queryBuilder.addDoubleField(plannedCruiseUsageHeader);
		queryBuilder.addDoubleField(nominalCruiseUsageHeader);
		queryBuilder.addDoubleField(actualCruiseUsageHeader);
		
		queryBuilder.addDoubleField(plannedDescentUsageHeader);
		queryBuilder.addDoubleField(nominalDescentUsageHeader);
		queryBuilder.addDoubleField(actualDescentUsageHeader);
		
		queryBuilder.addDoubleField(cruiseUsageRateHeader);
		
		queryBuilder.addDoubleField(plannedFirFuelHeader);
		queryBuilder.addDoubleField(nominalFirFuelHeader);
		queryBuilder.addDoubleField(actualFirFuelHeader);
		
		queryBuilder.addDoubleField(plannedFirTimeHeader);
		queryBuilder.addDoubleField(nominalFirTimeHeader);
		queryBuilder.addDoubleField(actualFirTimeHeader);
		
		queryBuilder.addDoubleField(plannedTotalFuelUsageHeader);
		queryBuilder.addDoubleField(nominalTotalFuelUsageHeader);
		queryBuilder.addDoubleField(actualTotalFuelUsageHeader);
		
		queryBuilder.addDoubleField(nominalOceanicFuelUsageHeader);
		queryBuilder.addDoubleField(actualOceanicFuelUsageHeader);

		queryBuilder.addVarCharField(simSectorTraceHeader, 2048);
	}

	@Override
	public void bindFields(QueryBinder queryBinder) throws Exception {
		// For some of the double fields below, we bind a String value to a field of type Double. This is a hack so that
		// when this method is called for outputting a record to a csv file (ie stream output), the double value can be
		// formatted in a specific way, yet when this method is called for updating a record to the db, the field can
		// still be bound as a double. Worst yet, if the value is null, we binding a an empty String to Double field.
		// How does that get translated in jdbc? Turns out it all works in Oracle. Concerned that it might not work for
		// other dbs.
		queryBinder.bindField(scenarioName);
		queryBinder.bindField(startDate, true); // Only date portion
		queryBinder.bindField(forecastFiscalYear);
		queryBinder.bindField(filedAircraftId);
		queryBinder.bindField(filedDepAirport);
		queryBinder.bindField(filedDepAirportCountryCode);
		queryBinder.bindField(filedArrAirport);
		queryBinder.bindField(filedArrAirportCountryCode);
		queryBinder.bindField(region);
		queryBinder.bindField(filedGateOutTime);
		queryBinder.bindField(filedGateInTime);

		queryBinder.bindField(simFlightId);
		queryBinder.bindField(simAirframeId);
		queryBinder.bindField(scheduleId);
		queryBinder.bindField(itineraryNumber);
		queryBinder.bindField(flightLegNumber);
		queryBinder.bindField(simEquipment.toString());
		queryBinder.bindField(filedEtmsAircraftType);
		queryBinder.bindField(filedBadaAircraftType);
		queryBinder.bindField(evolvedEtmsAircraftType);
		queryBinder.bindField(evolvedBadaAircraftType);
		queryBinder.bindField(flownBadaAircraftType);

		queryBinder.bindField(evolvedEtmsAircraftCategory);
		queryBinder.bindField(filedUserClass);
		queryBinder.bindField(filedAtoUserClass);
		queryBinder.bindField(dayOverrideFlag);

		queryBinder.bindField(flightPhaseStartTimes.get(FlightPhase.DEP_GATE));
		queryBinder.bindField(flightPhaseEndTimes.get(FlightPhase.DEP_GATE));
		queryBinder.bindField(simDepartureTurnaroundDistributionValue);
		queryBinder.bindField(flightPhaseStartTimes.get(FlightPhase.DEP_TURNAROUND));
		queryBinder.bindField(flightPhaseEndTimes.get(FlightPhase.DEP_TURNAROUND));
		queryBinder.bindField(flightPhaseStartTimes.get(FlightPhase.PUSHBACK));
		queryBinder.bindField(flightPhaseEndTimes.get(FlightPhase.PUSHBACK));
		queryBinder.bindField(assignedEdct);
		queryBinder.bindField(appliedEdct);
		queryBinder.bindField(flightPhaseStartTimes.get(FlightPhase.EDCT_GATE));
		queryBinder.bindField(flightPhaseEndTimes.get(FlightPhase.EDCT_GATE));
		queryBinder.bindField(flightPhaseStartTimes.get(FlightPhase.DEPARTURE_GATE_QUEUE));
		queryBinder.bindField(flightPhaseEndTimes.get(FlightPhase.DEPARTURE_GATE_QUEUE));
		queryBinder.bindField(simGateOutTime);
		queryBinder.bindField(simPushbackDistributionValue);
		queryBinder.bindField(simGateDelay);
		queryBinder.bindField(flightPhaseStartTimes.get(FlightPhase.DEPARTURE_RAMP_QUEUE));
		queryBinder.bindField(flightPhaseEndTimes.get(FlightPhase.DEPARTURE_RAMP_QUEUE));
		queryBinder.bindField(departureRampQueuingDelay);
		queryBinder.bindField(flightPhaseStartTimes.get(FlightPhase.DEPARTURE_RAMP_SERVICE));
		queryBinder.bindField(flightPhaseEndTimes.get(FlightPhase.DEPARTURE_RAMP_SERVICE));
		queryBinder.bindField(departureRampServiceTime);
		queryBinder.bindField(departureRampName);
		queryBinder.bindField(departureRampBypassFlag);
		queryBinder.bindField(flightPhaseStartTimes.get(FlightPhase.TAXI_OUT));
		queryBinder.bindField(flightPhaseEndTimes.get(FlightPhase.TAXI_OUT));
		queryBinder.bindField(simNominalTaxiOutTime);
		queryBinder.bindField(simTaxiOutDelay);
		queryBinder.bindField(simActualTaxiOutTime);
		queryBinder.bindField(simDepartureSurfaceDelay);
		queryBinder.bindField(rerouteFlag);
		queryBinder.bindField(preDepartureRerouteFlag);
		queryBinder.bindField(oceanicRerouteFlag);
		queryBinder.bindField(rerouteClearanceFlag);
		queryBinder.bindField(flightPhaseStartTimes.get(FlightPhase.REROUTE_CLEARANCE));
		queryBinder.bindField(rerouteClearanceQueueEnd);
		queryBinder.bindField(simRerouteClearanceQueuingDelay);
		queryBinder.bindField(rerouteClearanceQueueEnd); // May not need this a second time if we can build a view
		queryBinder.bindField(flightPhaseEndTimes.get(FlightPhase.REROUTE_CLEARANCE));
		queryBinder.bindField(simRerouteClearanceServiceTime);
		queryBinder.bindField(simRerouteClearanceTotalDelay);
		queryBinder.bindField(flightPhaseStartTimes.get(FlightPhase.EDCT_SURFACE));
		queryBinder.bindField(flightPhaseEndTimes.get(FlightPhase.EDCT_SURFACE));
		queryBinder.bindField(simDepartureParetoCurve);
		queryBinder.bindField(flightPhaseStartTimes.get(FlightPhase.DEPARTURE));
		queryBinder.bindField(flightPhaseEndTimes.get(FlightPhase.DEPARTURE));
		queryBinder.bindField(simWheelsOffTime);
		queryBinder.bindField(simTotalRestrictionDelay);
		queryBinder.bindField(sidSelected);
		queryBinder.bindField(sidFitness);
		queryBinder.bindField(simDepartureFix);
		queryBinder.bindField(flightPhaseStartTimes.get(FlightPhase.DEPARTURE_FIX));
		queryBinder.bindField(flightPhaseEndTimes.get(FlightPhase.DEPARTURE_FIX));
		queryBinder.bindField(simDepartureFixDelay);
		queryBinder.bindField(stepClimbEligible);
		queryBinder.bindField(topOfClimbMins);
		queryBinder.bindField(filedCruiseAltitude);
		queryBinder.bindField(flightPhaseStartTimes.get(FlightPhase.SECTOR_QUEUE));
		queryBinder.bindField(flightPhaseEndTimes.get(FlightPhase.SECTOR_QUEUE));
		queryBinder.bindField(simSectorDelay);
		queryBinder.bindField(topOfDescentMins);
		queryBinder.bindField(shortenedArrivalRoute);
		queryBinder.bindField(cdaCurveName);
		queryBinder.bindField(formatDouble(cdaProbability));
		queryBinder.bindField(cdaSelected);
		queryBinder.bindField(starSelected);
		queryBinder.bindField(starFitness);
		queryBinder.bindField(simArrivalFix);

		queryBinder.bindField(flightPhaseStartTimes.get(FlightPhase.ARRIVAL_FIX));
		queryBinder.bindField(flightPhaseEndTimes.get(FlightPhase.ARRIVAL_FIX));
		queryBinder.bindField(simArrivalFixDelay);
		queryBinder.bindField(iapSelected);
		queryBinder.bindField(flightPhaseStartTimes.get(FlightPhase.ARRIVAL));
		queryBinder.bindField(flightPhaseEndTimes.get(FlightPhase.ARRIVAL));
		queryBinder.bindField(simArrivalParetoCurve);
		queryBinder.bindField(simWheelsOnTime);
		queryBinder.bindField(flightPhaseStartTimes.get(FlightPhase.TAXI_IN));
		queryBinder.bindField(flightPhaseEndTimes.get(FlightPhase.TAXI_IN));
		queryBinder.bindField(simNominalTaxiInTime);
		queryBinder.bindField(simActualTaxiInTime);
		queryBinder.bindField(simTaxiInDelay);
		queryBinder.bindField(simArrivalSurfaceDelay);
		queryBinder.bindField(flightPhaseStartTimes.get(FlightPhase.ARRIVAL_RAMP_QUEUE));
		queryBinder.bindField(flightPhaseEndTimes.get(FlightPhase.ARRIVAL_RAMP_QUEUE));
		queryBinder.bindField(arrivalRampQueuingDelay);
		queryBinder.bindField(flightPhaseStartTimes.get(FlightPhase.ARRIVAL_RAMP_SERVICE));
		queryBinder.bindField(flightPhaseEndTimes.get(FlightPhase.ARRIVAL_RAMP_SERVICE));
		queryBinder.bindField(arrivalRampServiceTime);
		queryBinder.bindField(arrivalRampName);
		queryBinder.bindField(arrivalRampBypassFlag);
		queryBinder.bindField(flightPhaseStartTimes.get(FlightPhase.ARR_GATE));
		queryBinder.bindField(flightPhaseEndTimes.get(FlightPhase.ARR_GATE));
		queryBinder.bindField(simGateInTime);
		queryBinder.bindField(blockTime);
		queryBinder.bindField(fcEnrouteTime);
		queryBinder.bindField(this.rerouteEnrouteTime);
		queryBinder.bindField(formatDouble(simulatedAirborneTime));
		queryBinder.bindField(formatDouble(simulatedAirborneDelay));
		queryBinder.bindField(formatDouble(totalTbfmDelay));
		queryBinder.bindField(departureTurnImpact);
		queryBinder.bindField(arrivalTurnImpact);
		
		queryBinder.bindField(oceanicFlag);
		queryBinder.bindField(oceanicDelay);
		queryBinder.bindField(oceanicRegionsTraversed);

		queryBinder.bindField(plannedRouteDistance);
		queryBinder.bindField(actualRouteDistance);
		
		queryBinder.bindField(plannedFirDistance);
		queryBinder.bindField(actualFirDistance);
		
		queryBinder.bindField(formatDouble(plannedClimbUsage));
		queryBinder.bindField(formatDouble(nominalClimbUsage));
		queryBinder.bindField(formatDouble(actualClimbUsage));
		
		queryBinder.bindField(formatDouble(plannedCruiseUsage));
		queryBinder.bindField(formatDouble(nominalCruiseUsage));
		queryBinder.bindField(formatDouble(actualCruiseUsage));
		
		queryBinder.bindField(formatDouble(plannedDescentUsage));
		queryBinder.bindField(formatDouble(nominalDescentUsage));
		queryBinder.bindField(formatDouble(actualDescentUsage));
		
		queryBinder.bindField(formatDouble(cruiseUsageRate));
		
		queryBinder.bindField(formatDouble(plannedFirFuel));
		queryBinder.bindField(formatDouble(nominalFirFuel));
		queryBinder.bindField(formatDouble(actualFirFuel));
		
		queryBinder.bindField(formatDouble(plannedFirTime));
		queryBinder.bindField(formatDouble(nominalFirTime));
		queryBinder.bindField(formatDouble(actualFirTime));
		
		queryBinder.bindField(formatDouble(plannedTotalFuelUsage));
		queryBinder.bindField(formatDouble(nominalTotalFuelUsage));
		queryBinder.bindField(formatDouble(actualTotalFuelUsage));
		
		queryBinder.bindField(formatDouble(nominalOceanicFuelUsage));
		queryBinder.bindField(formatDouble(actualOceanicFuelUsage));

		// Truncate to 2048 characters...
		//queryBinder.bindField(simSectorTrace.substring(0, Math.min(simSectorTrace.length(), 2048)));
		queryBinder.bindField(simSectorTrace);

		queryBinder.addBatch();
	}

	public String getSimDepartureParetoCurve() {
		return simDepartureParetoCurve;
	}

	public void setSimDepartureParetoCurve(String simDepartureParetoCurve) {
		this.simDepartureParetoCurve = simDepartureParetoCurve;
	}

	public String getSimArrivalParetoCurve() {
		return simArrivalParetoCurve;
	}

	public void setSimArrivalParetoCurve(String simArrivalParetoCurve) {
		this.simArrivalParetoCurve = simArrivalParetoCurve;
	}
	
	public boolean isOceanicFlight() {
		return oceanicFlag;
	}
	
	public void setOceanicFlag(boolean flag) {
		oceanicFlag = flag;
	}
	
	public boolean isOceanicReroute() {
		return oceanicRerouteFlag;
	}
	
	public void setOceanicRerouteFlag(boolean flag) {
		oceanicRerouteFlag = flag;
	}
	
	public double getOceanicDelay() {
		return oceanicDelay;
	}
	
	public void setOceanicDelay(double delay) {
		oceanicDelay = delay;
	}
	
	public String getOceanicRegionsTraversed() {
		return oceanicRegionsTraversed;
	}
	
	public void setOceanicRegionsTraversed(String oceanicRegions) {
		oceanicRegionsTraversed = oceanicRegions;
		if (oceanicRegionsTraversed == null) {
			oceanicRegionsTraversed = "";
		}
	}
	
	public double getNominalOceanicFuelUsage() {
		return nominalOceanicFuelUsage;
	}
	
	public void setNominalOceanicFuelUsage(double fuel) {
		nominalOceanicFuelUsage = fuel;
	}
	
	public double getActualOceanicFuelUsage() {
		return actualOceanicFuelUsage;
	}
	
	public void setActualOceanicFuelUsage(double fuel) {
		actualOceanicFuelUsage = fuel;
	}
	
	private String formatDouble(Double s){
		if(s == null){
			return "";
		}
		return String.format(DECIMAL_FORMAT, s);
	}
}
