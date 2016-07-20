package gov.faa.ang.swac.scheduler.forecast.trip_distribution;

import gov.faa.ang.swac.common.flightmodeling.ScheduleRecord;
import gov.faa.ang.swac.scheduler.airport_data.AirportData;
import gov.faa.ang.swac.scheduler.forecast.airport_data.ForecastAirportDataPair;
import java.util.Map;
import java.util.Collection;
import java.util.LinkedHashMap;

public class ForecastTripDistAirportData extends AirportData
{
    private ForecastTripDistAirportDataCount 
        etmsDep, 
        etmsArr, 
        tafBase, 
        tafForecast, // part of projections
        opsnetBase;
    private double 
        initTotalDeps,
        initTotalArrs,
        initRemovedOps;
    
    // Projections
    private double 
        projectedTotalDep,
        projectedTotalArr;
    private double 
        calculatedTotalDep,
        calculatedTotalArr;
    private int 
        calcIntDep,
        calcIntArr;
    private double 
        oldDepCoeff,
        newDepCoeff,
        oldArrCoeff,
        newArrCoeff;
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
        opsnetBase      = new ForecastTripDistAirportDataCount();
        
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
        this.calculatedTotalDep = 0;
        this.calculatedTotalArr = 0;
        this.calcIntDep         = 0;
        this.calcIntArr         = 0;
        
        this.oldDepCoeff        = 1;
        this.newDepCoeff        = 1;
        this.oldArrCoeff        = 1;
        this.newArrCoeff        = 1;

        this.windowZ9Arrs       = 0;
        this.windowZ9Deps       = 0;
    }
    
    public void resetWindowedFlightCounters() 
    {
    	this.windowedArrs   = 0;
    	this.windowedDeps   = 0;
    }
    
    public void setEtmsDep(ForecastTripDistAirportDataCount etmsDep)
    {
        this.etmsDep = etmsDep;
    }

    public ForecastTripDistAirportDataCount getEtmsDep()
    {
        return etmsDep;
    }

    public void setEtmsArr(ForecastTripDistAirportDataCount etmsArr)
    {
        this.etmsArr = etmsArr;
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

    public void setOpsnetBase(ForecastTripDistAirportDataCount opsnetBase)
    {
        this.opsnetBase = opsnetBase;
    }

    public ForecastTripDistAirportDataCount getOpsnetBase()
    {
        return opsnetBase;
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

    public void setInitialRemovedOperations(double initRemovedOps)
    {
        this.initRemovedOps = initRemovedOps;
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

    public void setCalculatedTotalDep(double calculatedTotalDep)
    {
        this.calculatedTotalDep = calculatedTotalDep;
    }

    public double getCalculatedTotalDep()
    {
        return calculatedTotalDep;
    }

    public void setCalculatedTotalArr(double calculatedTotalArr)
    {
        this.calculatedTotalArr = calculatedTotalArr;
    }

    public double getCalculatedTotalArr()
    {
        return calculatedTotalArr;
    }

    public void setCalcIntDep(int calcIntDep)
    {
        this.calcIntDep = calcIntDep;
    }

    public int getCalcIntDep()
    {
        return calcIntDep;
    }

    public void setCalcIntArr(int calcIntArr)
    {
        this.calcIntArr = calcIntArr;
    }
    public int getCalcIntArr()
    {
        return calcIntArr;
    }

    public void setOldDepCoeff(double oldDepCoeff)
    {
        this.oldDepCoeff = oldDepCoeff;
    }

    public double getOldDepCoeff()
    {
        return oldDepCoeff;
    }

    public void setNewDepCoeff(double newDepCoeff)
    {
        this.newDepCoeff = newDepCoeff;
    }

    public double getNewDepCoeff()
    {
        return newDepCoeff;
    }

    public void setOldArrCoeff(double oldArrCoeff)
    {
        this.oldArrCoeff = oldArrCoeff;
    }

    public double getOldArrCoeff()
    {
        return oldArrCoeff;
    }

    public void setNewArrCoeff(double newArrCoeff)
    {
        this.newArrCoeff = newArrCoeff;
    }

    public double getNewArrCoeff()
    {
        return newArrCoeff;
    }   
    
    public void setWindowZ9Arrs(double windowZ9Arrs)
    {
        this.windowZ9Arrs = windowZ9Arrs;
    }

    public double getWindowZ9Arrs()
    {
        return windowZ9Arrs;
    }

    public void setWindowZ9Deps(double windowZ9Deps)
    {
        this.windowZ9Arrs = windowZ9Deps;
    }

    public double getWindowZ9Deps()
    {
        return windowZ9Deps;
    }
    
    public void setWindowedArrivals(int windowedArrs)
    {
        this.windowedArrs = windowedArrs;
    }
    
    public int getWindowedArrivals() 
    {
    	return this.windowedArrs;
    }
    
    public void setWindowedDepartures(int windowedDeps)
    {
        this.windowedDeps = windowedDeps;
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
    
    private String createKey(ForecastAirportDataPair pair)
    {
        return createKey(pair.getOrigin(),pair.getDestination());
    }

    public void addCityPair(ForecastAirportDataPair pair)
    {
        String key = createKey(pair);
        
        if (this == pair.getOrigin())
        {
            if(goingTo.containsKey(key))
            {
                goingTo.get(key).getCountByClass().addAllData(
                    pair.getCountByClass());
            }
            else
            {
                goingTo.put(key,pair);
            }
        }
        
        if (this == pair.getDestination())
        {
            if(comingFrom.containsKey(key))
            {
                comingFrom.get(key).getCountByClass().addAllData(
                    pair.getCountByClass());
            }
            else
            {
                comingFrom.put(key,pair);
            }
        }
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

    public boolean isSource()
    {
        return comingFrom.isEmpty();
    }
   
    public boolean isSink()
    {
        return goingTo.isEmpty();
    }

    public boolean isSourceOrSink()
    {
        return isSource() || isSink();
    }

    /*public boolean isSourceAndSink()
    {
        return isSource() && isSink();
    }*/

    public void removeConnectedPairs()
    {
        for(ForecastAirportDataPair pair : comingFrom.values())
        {
            ForecastTripDistAirportData airport = 
                (ForecastTripDistAirportData)pair.getOrigin();
            boolean removed = airport.removePair(pair);
            if(!removed)
            {
                System.out.println(
                    "Didn't remove " + airport.getMostLikelyCode() +
                    " to " + getMostLikelyCode());
            }
        }
        
        for(ForecastAirportDataPair pair : goingTo.values())
        {
            ForecastTripDistAirportData airport = 
                (ForecastTripDistAirportData)pair.getDestination();
            boolean removed = airport.removePair(pair);
            if(!removed)
            {
                System.out.println(
                    "didn't remove " + getMostLikelyCode() + 
                    " to " + airport.getMostLikelyCode());
            }
        }
    }
    
    private boolean removePair(ForecastAirportDataPair pair)
    {
        String key = createKey(pair);      
        
        ForecastAirportDataPair removedPair = null;
        
        if (this == pair.getOrigin())
        {
            removedPair = goingTo.remove(key);
        }
        
        if (this == pair.getDestination())
        {
            removedPair = comingFrom.remove(key);
        }

        if (removedPair != null)
        {
            initRemovedOps += removedPair.getInitialFlightCount();
            return true;
        }         
        
        return false;
    }

    public double getProjectedOpsnet()
    {
        double projectedOpsnet = opsnetBase.getTotal();
        
        if (0 < projectedOpsnet && 0 < tafBase.getTotal())
        {
            projectedOpsnet *= 
                tafForecast.getTotal()
                /tafBase.getTotal();
        }
        
        return projectedOpsnet;
    }

    public void addZ9Arrivals(double addZ9ArrCount)
    {
        this.windowZ9Arrs += addZ9ArrCount;
    }

    public void addZ9Departures(double addZ9DepCount)
    {
        this.windowZ9Deps += addZ9DepCount;
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
            (getCalcIntDep()+getCalcIntArr())
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

	public int getDepCountToClone() 
    {
		int n = 0;
		for (ForecastAirportDataPair pair : goingTo.values()) 
        {
    		n += java.lang.Math.max(0,pair.getNumClonesToMake());
    	}
		return n;
	}

	public int getArrCountToClone()
    {
		int n = 0;
		for (ForecastAirportDataPair pair : comingFrom.values()) 
        {
    		n += java.lang.Math.max(0,pair.getNumClonesToMake());
    	}
		return n;
	}
}
