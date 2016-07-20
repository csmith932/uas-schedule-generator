/**
 * Copyright "TBD", Metron Aviation & CSSI. All rights reserved.
 *
 * This computer Software was developed with the sponsorship of the U.S.
 * Government under Contract No. DTFAWA-10-D-00033, which has a copyright
 * license in accordance with AMS 3.5-13.(c)(1).
 */
package gov.faa.ang.swac.datalayer.storage.db;

/**
 * These are wrapper subclasses for UploadReportsDao. The reason for the wrapper subclasses is to distinguish the
 * various DAOS in scenario.xml.
 * 
 * The data descriptors used in the xml (JDBCDataDescriptor) are distinguished by database name and type, and since all
 * the daos refer to the same database, they need a different type.  
 * 
 * @author cunningham
 */
public class NamedUploadReportsDaos  
{
	public static class ScheduleOutputReportDao extends UploadReportsDao {}
	public static class ScheduleSummaryReportDao extends UploadReportsDao {}
	public static class FlightOutputReportDao extends UploadReportsDao {}
	public static class AirportOutputReportDao extends UploadReportsDao {}
	public static class SectorTimeOutputReportDao extends UploadReportsDao {}
	public static class SectorSummaryOutputReportDao extends UploadReportsDao {}
	public static class FlightSummaryReportDao extends UploadReportsDao {}
	public static class FixOutputReportDao extends UploadReportsDao {}
	public static class OceanicRegionOutputReportDao extends UploadReportsDao {}
	public static class PfmPassengerDelayReportDao extends UploadReportsDao {}
	
	public static class MonteCarloFlightOutputStatisticsReportDao extends UploadReportsDao {}
	public static class MonteCarloFlightOutputDistributionReportDao extends UploadReportsDao {}
	public static class MonteCarloAirportOutputDistributionReportDao extends UploadReportsDao {}
	public static class MonteCarloAirportOutputStatisticsReportDao extends UploadReportsDao {}
}