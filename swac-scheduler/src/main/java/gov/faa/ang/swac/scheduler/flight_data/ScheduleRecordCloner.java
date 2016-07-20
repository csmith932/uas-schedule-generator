package gov.faa.ang.swac.scheduler.flight_data;

import gov.faa.ang.swac.common.datatypes.Timestamp;
import gov.faa.ang.swac.common.flightmodeling.ScheduleRecord;
import gov.faa.ang.swac.scheduler.mathematics.statistics.HQRandom;
import java.util.ArrayList;
import java.util.List;

public class ScheduleRecordCloner
{
    private HQRandom hqr = null;

    public ScheduleRecordCloner(HQRandom generator)
    {
        this.hqr = generator;
    }

    public List<ScheduleRecord> cloneScheduleRecord(
        ScheduleRecord schedRecIn, 
        int nClones,
        double timeShiftSigmaMins)
    {
        List<ScheduleRecord> cloneList = new ArrayList<ScheduleRecord>(nClones);

        for (int i = 0; i < nClones; ++i)
        {
            cloneList.add(
                newClonedScheduleRecord(
                    schedRecIn,
                    i,
                    timeShiftSigmaMins));
        }

        return cloneList;
    }

    private ScheduleRecord newClonedScheduleRecord(
        ScheduleRecord schedRec, 
        int iClone,
        double timeShiftSigmaMins)
    {
        ScheduleRecord schedRecClone = new ScheduleRecord(schedRec);

        // Update the ID of the clone
        schedRecClone.idNum = schedRec.idNum + (iClone+1);
        schedRecClone.flightPlanType = "CLONE_"+schedRec.idNum;
        
        // Select a time shift in minutes from a normal (Gaussian) 
        // distribution with mean=0, standard deviation given:
        final double timeShiftMins = 
            this.hqr.randomNormal(0,timeShiftSigmaMins);

        // Shift all timestamps in the clone
        schedRecClone.gateOutTime       = shift(schedRec.gateOutTime,timeShiftMins);
        schedRecClone.runwayOffTime     = shift(schedRec.runwayOffTime,timeShiftMins);
        schedRecClone.runwayOnTime      = shift(schedRec.runwayOnTime,timeShiftMins);
        schedRecClone.gateInTime        = shift(schedRec.gateInTime,timeShiftMins);
        schedRecClone.scheduledDepTime  = shift(schedRec.scheduledDepTime,timeShiftMins);
        schedRecClone.scheduledArrTime  = shift(schedRec.scheduledArrTime,timeShiftMins);
                
        return schedRecClone;
    }

    private static Timestamp shift(
        Timestamp time, 
        double shift)
    { 
        if (time != null)
        {
            return time.minuteAdd(shift);
        }
        
        return null;
    }


}
