package gov.faa.ang.swac.uas.scheduler.forecast.airport_data;

import gov.faa.ang.swac.datalayer.storage.fileio.TextSerializable;
import gov.faa.ang.swac.datalayer.storage.fileio.WithHeader;
import gov.faa.ang.swac.uas.scheduler.forecast.trip_distribution.ForecastTripDistAirportDataCount;

/**
 * TODO: This is a workaround for data flow ambiguity
 * @author csmith
 *
 */
public class ForecastAirportCountsRecordTaf extends ForecastAirportCountsRecord implements TextSerializable, WithHeader
{
	/**
	 * Need default constructor to reflect in data layer
	 */
	public ForecastAirportCountsRecordTaf()
	{
	}
	
	/**
	 * Copy constructor
	 * @param val
	 */
	public ForecastAirportCountsRecordTaf(ForecastAirportCountsRecordTaf val)
	{
		this.airportName = val.airportName;
		this.yyyymmdd = val.yyyymmdd;
		this.count = new ForecastTripDistAirportDataCount(val.count);
	}
}