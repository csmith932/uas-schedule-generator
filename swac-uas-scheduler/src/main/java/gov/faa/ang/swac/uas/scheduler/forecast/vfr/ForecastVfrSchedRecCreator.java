package gov.faa.ang.swac.uas.scheduler.forecast.vfr;

import gov.faa.ang.swac.common.datatypes.Timestamp;
import gov.faa.ang.swac.uas.scheduler.forecast.trip_distribution.ForecastTripDistAirportData;
import gov.faa.ang.swac.uas.scheduler.vfr.VFRLocalTimeGenerator;
import gov.faa.ang.swac.uas.scheduler.vfr.VfrSchedRecCreator;

import java.util.List;

public class ForecastVfrSchedRecCreator extends VfrSchedRecCreator
{
    public ForecastVfrSchedRecCreator(
        Timestamp localDate,
        int firstIdNum,
        int increment,
        VFRLocalTimeGenerator vfrLocalTimeGenerator)
    {
        super(
            localDate, 
            firstIdNum, 
            increment, 
            vfrLocalTimeGenerator);
    }

    public void forecastPopulate(List<ForecastTripDistAirportData> airportList)
    {
        resetScheduleRecords();
        
        for (ForecastTripDistAirportData airport : airportList)
        {
            int nVfrToAdd = getVfrCount(airport);
            
            if (0 < nVfrToAdd)
            {
                schedRecList.addAll(
                    populateAirport(airport,nVfrToAdd));
            }
        }
    }

    private int getVfrCount(ForecastTripDistAirportData aprt)
    {
        return aprt.getVfrCountToAdd();
    }
}
