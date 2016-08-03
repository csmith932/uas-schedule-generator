package gov.faa.ang.swac.uas.scheduler.forecast.trip_distribution;

import gov.faa.ang.swac.common.flightmodeling.ScheduleRecord;
import gov.faa.ang.swac.uas.scheduler.airport_data.AirportData;
import gov.faa.ang.swac.uas.scheduler.forecast.airport_data.ForecastAirportDataPair;

import java.util.Map;
import java.util.Collection;
import java.util.LinkedHashMap;

public class ForecastTripDistAirportData extends AirportData
{
    private ForecastTripDistAirportDataCount 
        etmsDep, 
        etmsArr, 
        tafBase, 
        tafForecast;
    private double 
        initTotalDeps,
        initTotalArrs,
        initRemovedOps;
    
    // Projections
    private double 
        projectedTotalDep,
        projectedTotalArr;
    private double 
        windowZ9Arrs,
        windowZ9Deps;
    
    // Windowed Flight Counters  
    private int
        windowedArrs,
        windowedDeps;

    private Map<String,ForecastAirportDataPair> 
        comingFrom, // "coming from" another airport to here
        goingTo;    // from here "going to" another airport   
    
    public ForecastTripDistAirportData()
    {
        initialize();
    }

    public ForecastTripDistAirportData(AirportData airportData)
    {
        super(airportData);
        initialize();
    }

    private void initialize()
    {
        etmsDep         = new ForecastTripDistAirportDataCount();
        etmsArr         = new ForecastTripDistAirportDataCount();
        tafBase         = new ForecastTripDistAirportDataCount();
        
        initTotalDeps   = 0;
        initTotalArrs   = 0;
        initRemovedOps  = 0;

        resetProjections();
        resetWindowedFlightCounters();      
        
        comingFrom      = new LinkedHashMap<String,ForecastAirportDataPair>();
        goingTo         = new LinkedHashMap<String,ForecastAirportDataPair>();
    }
    
    public void resetProjections()
    {
        this.tafForecast = new ForecastTripDistAirportDataCount();
        
        this.projectedTotalDep  = 0;
        this.projectedTotalArr  = 0;
        
        this.windowZ9Arrs       = 0;
        this.windowZ9Deps       = 0;
    }
    
    public void resetWindowedFlightCounters() 
    {
    	this.windowedArrs   = 0;
    	this.windowedDeps   = 0;
    }
    
    public ForecastTripDistAirportDataCount getEtmsDep()
    {
        return etmsDep;
    }

    public ForecastTripDistAirportDataCount getEtmsArr()
    {
        return etmsArr;
    }
    
    public void setTafBase(ForecastTripDistAirportDataCount tafBase)
    {
        this.tafBase = tafBase;
    }

    public ForecastTripDistAirportDataCount getTafBase()
    {
        return tafBase;
    }

    public void setTafForecast(ForecastTripDistAirportDataCount tafForecast)
    {
        this.tafForecast = tafForecast;
    }

    public ForecastTripDistAirportDataCount getTafForecast()
    {
        return tafForecast;
    }

    public void setInitialTotalDep(double initTotalDeps)
    {
        this.initTotalDeps = initTotalDeps;
    }

    public double getInitialTotalDep()
    {
        return initTotalDeps;
    }

    public void setInitialTotalArr(double initTotalArrs)
    {
        this.initTotalArrs = initTotalArrs;
    }

    public double getInitialTotalArr()
    {
        return initTotalArrs;
    }   

    public double getInitialRemovedOperations()
    {
        return initRemovedOps;
    }
    
    public void setProjectedTotalDep(double projectedTotalDep)
    {
        this.projectedTotalDep = projectedTotalDep;
    }

    public double getProjectedTotalDep()
    {
        return projectedTotalDep;
    }

    public void setProjectedTotalArr(double projectedTotalArr)
    {
        this.projectedTotalArr = projectedTotalArr;
    }

    public double getProjectedTotalArr()
    {
        return projectedTotalArr;
    }

    public double getWindowZ9Arrs()
    {
        return windowZ9Arrs;
    }

    public double getWindowZ9Deps()
    {
        return windowZ9Deps;
    }
    
    public int getWindowedArrivals() 
    {
    	return this.windowedArrs;
    }
    
    public int getWindowedDepartures() 
    {
    	return this.windowedDeps;
    }

    public Collection<ForecastAirportDataPair> getComingFrom()
    {
        return comingFrom.values();
    }

    public Collection<ForecastAirportDataPair> getGoingTo()
    {
        return goingTo.values();
    }

    private String createKey(AirportData origin, AirportData destin)
    {
        return 
            origin.getMostLikelyCode()
            +"_" 
            +destin.getMostLikelyCode();
    }
    
    public void addDepartureGoingTo(
        ForecastTripDistAirportData destin,
        ScheduleRecord schedRec)
    {
        String key = createKey(this,destin);
        
        ForecastAirportDataPair pair;
        if (goingTo.containsKey(key))
        {
            pair = goingTo.get(key);
        }
        else
        {
            if (destin.comingFrom.containsKey(key))
            {
                pair = destin.comingFrom.get(key);
            }
            else
            {
                pair = new ForecastAirportDataPair(this,destin);
            }
            goingTo.put(key, pair);
        }
        
        pair.addFlight(schedRec);
    }

    public void addArrivalComingFrom(
        ForecastTripDistAirportData origin,
        ScheduleRecord schedRec)
    {
        String key = createKey(origin,this);
        
        ForecastAirportDataPair pair;
        if (comingFrom.containsKey(key))
        {
            pair = comingFrom.get(key);
        }
        else
        {
            if (origin.goingTo.containsKey(key))
            {
                pair = origin.goingTo.get(key);
            }
            else
            {
                pair = new ForecastAirportDataPair(origin,this);
            }
            comingFrom.put(key,pair);
        }
        
        pair.addFlight(schedRec);
    }

    public void calculateInitialEtmsCounts()
    {
        for(ForecastAirportDataPair pair : comingFrom.values())
        {
            etmsArr.addAllData(pair.getCountByClass());
        }
        
        for(ForecastAirportDataPair pair : goingTo.values())
        {
            etmsDep.addAllData(pair.getCountByClass());
        }
    }

    public double getProjectedOpsnet()
    {
    	// This used to multiply the OPSNET actuals by the ratio of the forecast to base TAF. Instead we're treating the forecast as golden 
       return tafForecast.getTotal();
    }

    public void addToWindowedArrivals(int count) 
    {
    	this.windowedArrs += count;
    }
    
    public void addToWindowedDepartures(int count) 
    {
    	this.windowedDeps += count;
    }       

    public int getVfrCountToAdd()
    {
        double cloneCount =
            (getProjectedTotalDep()+getProjectedTotalArr())
            -(getInitialTotalArr()+getInitialTotalDep());
        
        double z9Counts = 
            getWindowZ9Deps() 
            +getWindowZ9Arrs();
        
        int result = (int)Math.round(
            getProjectedOpsnet() 
            -cloneCount 
            -z9Counts 
            -getInitialRemovedOperations());
        result = Math.max(0,result);
        
        return result;
    }
    
    public int getWindowedVfrCountToAdd()
    {
        int result = (int)Math.round(
            getProjectedOpsnet() 
            -getWindowedArrivals() 
            -getWindowedDepartures());
        
        result = Math.max(0,result);
        
        return result;
    }
    
    public void clear() 
    {
    	for (ForecastAirportDataPair pair : goingTo.values()) 
        {
    		pair.clear();
    	}
    	goingTo.clear();
        
    	for (ForecastAirportDataPair pair : comingFrom.values()) 
        {
    		pair.clear();
    	} 
    	comingFrom.clear();
    }
}
