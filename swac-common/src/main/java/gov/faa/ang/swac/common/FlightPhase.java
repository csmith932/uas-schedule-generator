/**
 * 
 */
package gov.faa.ang.swac.common;

public enum FlightPhase
{
 	 DEP_GATE                    { @Override public String phase() { return "DEPGATE"; } },
     DEP_TURNAROUND              { @Override public String phase() { return "DEP TURNAROUND"; } },
     PUSHBACK                    { @Override public String phase() { return "PUSHBACK"; } },
     EDCT_GATE                   { @Override public String phase() { return "EDCT GATE"; } },
     DEPARTURE_GATE_QUEUE        { @Override public String phase() { return "DEP GATE QUEUE"; } },
     DEPARTURE_RAMP_QUEUE        { @Override public String phase() { return "DEP RAMP QUEUE"; } },
     DEPARTURE_RAMP_SERVICE      { @Override public String phase() { return "DEP RAMP SERVICE"; } },
     TAXI_OUT                    { @Override public String phase() { return "TAXIOUT"; } },
     REROUTE_CLEARANCE			 { @Override public String phase() { return "REROUTE CLEARANCE"; } },
     EDCT_SURFACE                { @Override public String phase() { return "EDCT SURFACE"; } },
     DEPARTURE                   { @Override public String phase() { return "DEPARTURE"; } },
     DEPARTURE_FIX               { @Override public String phase() { return "DEP FIX"; } },
     SECTOR_QUEUE                { @Override public String phase() { return "SEC QUEUE"; } },
     SECTOR_CROSS                { @Override public String phase() { return "SEC CROSS"; } },
     RESTRICTION                 { @Override public String phase() { return "RESTRICTION"; } },
     OCEANIC_TRAVERSAL			 { @Override public String phase() { return "OCEANIC TRAVERSAL"; } },
     ARRIVAL_FIX                 { @Override public String phase() { return "ARR FIX"; } },
     ARRIVAL                     { @Override public String phase() { return "ARRIVAL"; } },
     TAXI_IN                     { @Override public String phase() { return "TAXIIN"; } },
     ARRIVAL_RAMP_QUEUE          { @Override public String phase() { return "ARR RAMP QUEUE"; } },
     ARRIVAL_RAMP_SERVICE        { @Override public String phase() { return "ARR RAMP SERVICE"; } },
     ARR_GATE                    { @Override public String phase() { return "ARRGATE"; } },
     ARR_TURNAROUND				 { @Override public String phase() { return "ARR_TURNAROUND"; } };

     public abstract String phase();
 }