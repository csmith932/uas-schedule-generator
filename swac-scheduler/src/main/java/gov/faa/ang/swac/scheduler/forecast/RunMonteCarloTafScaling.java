/**
 * Copyright "TBD", Metron Aviation & CSSI. All rights reserved. This computer
 * Software was developed with the sponsorship of the U.S. Government under
 * Contract No. DTFAWA-10-D-00033, which has a copyright license in accordance
 * with AMS 3.5-13.(c)(1).
 */
package gov.faa.ang.swac.scheduler.forecast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.TreeMap;

import gov.faa.ang.swac.common.entities.TafAirportCorrelation;
import gov.faa.ang.swac.common.random.distributions.TriangularDistribution;
import gov.faa.ang.swac.common.random.multivariatedists.MVTriangular;
import gov.faa.ang.swac.controller.ExitException;
import gov.faa.ang.swac.controller.core.CloneableAbstractTask;
import gov.faa.ang.swac.datalayer.DataAccessException;
import gov.faa.ang.swac.datalayer.storage.DataMarshaller;
import gov.faa.ang.swac.scheduler.forecast.airport_data.ForecastAirportCountsRecordTaf;
import gov.faa.ang.swac.scheduler.forecast.trip_distribution.ForecastTripDistAirportDataCount;

public class RunMonteCarloTafScaling extends CloneableAbstractTask {

    private static final org.apache.log4j.Logger logger = org.apache.log4j.LogManager.getLogger(RunMonteCarloTafScaling.class);
    private DataMarshaller inputTafAopsFile;

    public DataMarshaller getInputTafAopsFile() {
        return this.inputTafAopsFile;
    }

    public void setInputTafAopsFile(DataMarshaller inputTafAopsFile) {
        this.inputTafAopsFile = inputTafAopsFile;
    }
    private DataMarshaller tafAirportCorrelationFile;

    public DataMarshaller getTafAirportCorrelationFile() {
        return tafAirportCorrelationFile;
    }

    public void setTafAirportCorrelationFile(DataMarshaller tafAirportCorrelationFile) {
        this.tafAirportCorrelationFile = tafAirportCorrelationFile;
    }
    private DataMarshaller outputTafAopsFile;

    public DataMarshaller getOutputTafAopsFile() {
        return this.outputTafAopsFile;
    }

    public void setOutputTafAopsFile(DataMarshaller outputTafAopsFile) {
        this.outputTafAopsFile = outputTafAopsFile;
    }
    private Double randomSeed;

    public Double getRandomSeed() {
        return randomSeed;
    }

    public void setRandomSeed(Double randomSeed) {
        this.randomSeed = randomSeed;
    }

    public void setRandomSeed(String randomSeed) {
        this.randomSeed = new Double(randomSeed);
    }
    
    private Double min, max, mode;

    public Double getMin() {
        return min;
    }

    public void setMin(Double min) {
        this.min = min;
    }

//    public void setMin(String min) {
//        setMin(Double.parseDouble(min));
//    }

    public Double getMax() {
        return max;
    }

    public void setMax(Double max) {
        this.max = max;
    }

//    public void setMax(String max) {
//        setMax(Double.parseDouble(max));
//    }

    public Double getMode() {
        return mode;
    }

    public void setMode(Double mode) {
        this.mode = mode;
    }

//    public void setMode(String mode) {
//        setMode(Double.parseDouble(mode));
//    }

    public RunMonteCarloTafScaling() {  }
    
    public RunMonteCarloTafScaling(RunMonteCarloTafScaling org) {
        super.setInstanceId(org.getInstanceId());
        super.setParent(org.getParent());

        this.max = (org.max == null ? null : org.max.doubleValue());
        this.min = (org.min == null ? null : org.min.doubleValue());
        this.mode = (org.mode == null ? null : org.mode.doubleValue());
        this.randomSeed = (org.randomSeed == null ? null : org.randomSeed.doubleValue());
    }
    
    @Override
    public void run() {
        try {
            Random rng = new Random(Math.round(randomSeed));
            TriangularDistribution d = new TriangularDistribution(min, max, mode);
            List<ForecastAirportCountsRecordTaf> tafAopsFile = new ArrayList<ForecastAirportCountsRecordTaf>();
            List<ForecastAirportCountsRecordTaf> tafAopsOutFile = new ArrayList<ForecastAirportCountsRecordTaf>();

            List<TafAirportCorrelation> tafArpCorList = new ArrayList<TafAirportCorrelation>();
            logger.debug("loading forecast airport counts...");
            this.inputTafAopsFile.load(tafAopsFile);

            logger.debug("loading taf airport correlations...");
            this.tafAirportCorrelationFile.load(tafArpCorList);
            TreeMap<Integer, TreeMap<String, Double>> arpCorrTree = new TreeMap<Integer, TreeMap<String, Double>>();
            for (TafAirportCorrelation corrRecord : tafArpCorList) {
                if (!arpCorrTree.containsKey(corrRecord.getGroup())) {
                    arpCorrTree.put(corrRecord.getGroup(), new TreeMap<String, Double>());
                }
                arpCorrTree.get(corrRecord.getGroup()).put(corrRecord.getAirport1() + "," + corrRecord.getAirport2(), new Double(corrRecord.getTafMulCorr()));
            }
            TreeMap<String, Double[][]> myCorrCoefMap = new TreeMap<String, Double[][]>();
            TreeMap<String, Double> arpCorrRandomMultiplier = new TreeMap<String, Double>();
            List<String> allCorrArpList = new ArrayList<String>();
            for (Integer grp : arpCorrTree.keySet()) {
                List<String> arpList = new ArrayList<String>();
                String arpListString = null;
                for (String arpCouple : arpCorrTree.get(grp).keySet()) {
                    String arps[] = arpCouple.split("[,]");
                    for (int i = 0; i < arps.length; i++) {
                        if (!arpList.contains(arps[i])) {
                            arpList.add(arps[i]);
                            if (arpListString == null) {
                                arpListString = arps[i];
                            } else {
                                arpListString = arpListString + "," + arps[i];
                            }
                            allCorrArpList.add(arps[i]);
                        }
                    }
                }
                Double[][] myCorrCoefMatrix = new Double[arpList.size()][arpList.size()];
                for (int i = 0; i <= arpList.size() - 1; i++) {
                    myCorrCoefMatrix[i][i] = 1.0;
                }
                for (String arpCouple : arpCorrTree.get(grp).keySet()) {
                    String arps[] = arpCouple.split("[,]");
                    myCorrCoefMatrix[arpList.indexOf(arps[0])][arpList.indexOf(arps[1])] = arpCorrTree.get(grp).get(arpCouple);
                    myCorrCoefMatrix[arpList.indexOf(arps[1])][arpList.indexOf(arps[0])] = arpCorrTree.get(grp).get(arpCouple);
                }
                myCorrCoefMap.put(arpListString, myCorrCoefMatrix);
            }
            for (String arpListString : myCorrCoefMap.keySet()) {
                MVTriangular mvD = new MVTriangular(min, max, mode, myCorrCoefMap.get(arpListString));
                if (mvD.getNotPD()) {
                    logger.debug("The correlation matrix defined for these airports is not Positive Definite " + arpListString + ". No Correlation will be imposed");
                }
                Double corrRandomMultiplier[] = mvD.mvNextDouble(rng);
                String arps[] = arpListString.split("[,]");
                for (int i = 0; i <= arps.length - 1; i++) {
                    arpCorrRandomMultiplier.put(arps[i], corrRandomMultiplier[i]);
                }
            }

            for (ForecastAirportCountsRecordTaf oldRecord : tafAopsFile) {
                // Clone the original
                ForecastAirportCountsRecordTaf newRecord = new ForecastAirportCountsRecordTaf(oldRecord);
                tafAopsOutFile.add(newRecord);

                // Only update counts for the forecast year, since the schedule generator 
                // uses the ratio to the base year for traffic scaling and we don't want to 
                // scale both the numerator and denominator 
                if (newRecord.getYear() == this.getParent().getForecastFiscalYear() && !allCorrArpList.contains(newRecord.airportName)) {

                    double sampledMultiplier = d.nextDouble(rng);

                    // Update the count values
                    ForecastTripDistAirportDataCount record = newRecord.count;
                    record.setNumGA(record.getNumGA() * sampledMultiplier);
                    record.setNumMil(record.getNumMil() * sampledMultiplier);
                    record.setNumOther(record.getNumOther() * sampledMultiplier);
                } else if (newRecord.getYear() == this.getParent().getForecastFiscalYear() && allCorrArpList.contains(newRecord.airportName)) {
                    // Update the count values
                    //System.out.println("AirportName: " + newRecord.airportName + " " + record.getNumGA() + " " + arpCorrRandomMultiplier.get(newRecord.airportName));
                    ForecastTripDistAirportDataCount record = newRecord.count;
                    record.setNumGA(record.getNumGA() * arpCorrRandomMultiplier.get(newRecord.airportName));
                    record.setNumMil(record.getNumMil() * arpCorrRandomMultiplier.get(newRecord.airportName));
                    record.setNumOther(record.getNumOther() * arpCorrRandomMultiplier.get(newRecord.airportName));
                }
            }
            logger.debug("saving forecast airport counts...");
            this.outputTafAopsFile.save(tafAopsOutFile);
		}
		catch (DataAccessException ex)
		{
                        logger.trace(ex.getStackTrace());
			throw new ExitException("Fatal", ex);
		}
	}
	@Override
	public boolean validate(VALIDATION_LEVEL level) {

        boolean retval = true;

		retval=validateFiles(new DataMarshaller[]{inputTafAopsFile,tafAirportCorrelationFile},level);

		return retval; 	

	}

    @Override
    public RunMonteCarloTafScaling clone() {
        return new RunMonteCarloTafScaling(this);
    }
}
