package gov.faa.ang.swac.controller.core.component.trimmingsupport;

//import gov.faa.ajg61.trimsmooth.ScheduleTrimmerSmoother;
//import gov.faa.ajg61.trimsmooth.capacity.AirportCapacityDb;
//import gov.faa.ajg61.trimsmooth.capacity.CapacityDataLoader;
//import gov.faa.ajg61.trimsmooth.capacity.IAirportCapacityCurveData;
//import gov.faa.ajg61.trimsmooth.capacity.SymmetricAirportCapacityDb;
//import gov.faa.ajg61.trimsmooth.params.Parameters;
//import gov.faa.ajg61.trimsmooth.params.ParametersLoader;
//import gov.faa.ang.swac.common.flightmodeling.FlightPlan;
//
//import java.io.File;
//import java.io.IOException;
//import java.sql.Timestamp;
//import java.util.ArrayList;
//import java.util.Collection;
//
//import org.apache.log4j.Level;
//import org.apache.log4j.LogManager;
//import org.apache.log4j.Logger;
//
//import com.mallardsoft.tuple.Pair;

public class TrimAndSmoothWithParamFile {

//	private static Logger logger = LogManager.getLogger(TrimAndSmoothWithParamFile.class);
//	
//	// fields initialized with default values
//	private static String paramFilePath = 
//			"resources" + File.separator + "trim_smooth_parameters.txt";
//			//".." + File.separator + "config" + File.separator + "trim_smooth_parameters.txt";
//		
//	
//	public static void main(String[] args) {
//		System.out.println(System.getProperty("user.dir", "err"));
//		
//		// locate parameters file
//		if  (args.length == 0) {
//			logger.log(Level.INFO, "No parameter file specified.  " + 
//					"Using default parameter file at " + paramFilePath); 
//		} else {
//			paramFilePath = args[0];
//		}
//		
//		// read parameters from file
//		Parameters params = ParametersLoader.loadFromFile(paramFilePath);
//
//		// initialize trace files, if needed
//		if (params.getGenerateTraceFiles()) { params.setDefaultTraceWriters(params.getOutputDirectory());}
//		
//		// read/load capacity curves
//		logger.log(Level.INFO, "Loading airport capacity curves...");
//		File capacityFile = new File(params.getCapacityFilePath());
//		Collection<IAirportCapacityCurveData> capacityCurves = new CapacityDataLoader().loadData(capacityFile);
//		AirportCapacityDb capacityDb = null;
//		if (params.getUseSymmetricCapacities()) {
//			capacityDb = new SymmetricAirportCapacityDb(capacityCurves);
//		} else {
//			capacityDb = new AirportCapacityDb(capacityCurves);
//		}
//		
//		// get valid IFR flight plans
//		logger.log(Level.INFO, "Reading input schedule...");
//		Pair<Collection<FlightPlan>, Timestamp> scheduleData;
//		scheduleData = null; //ScheduleFilter.filterSchedule(params.getScheduleFilePath(), null, null, null, null);
//		Timestamp startDate = Pair.second(scheduleData);
//		Collection<FlightPlan> flightPlans = Pair.first(scheduleData);
//		// transform FlightPlan into SwacTSFlightPlan
//		ArrayList<SwacTSFlightPlan> wrappedFlightPlans = new ArrayList<SwacTSFlightPlan>(flightPlans.size());
//		for (FlightPlan flightPlan : flightPlans) {
//			wrappedFlightPlans.add(new SwacTSFlightPlan(flightPlan));
//		}
//		flightPlans.clear();
//		flightPlans = null;
//		
//		// start of trimming and smoothing
//		logger.log(Level.INFO, "Smoothing/trimming the schedule...");
//		ScheduleTrimmerSmoother<SwacTSFlightPlan> trimmer = new ScheduleTrimmerSmoother<SwacTSFlightPlan>();
//		trimmer.setParameters(params);
//		trimmer.setTaskData(wrappedFlightPlans, capacityDb, startDate);
//		trimmer.runAlgorithm();
//		
//		// create change file
//		logger.log(Level.INFO, "Creating change file...");
//		String fileName = params.getOutputDirectory() + File.separator + "ts_change_file.csv";
//		//TSResultsWriter tsResultsWriter = new TSResultsWriter(fileName);
//		try {
//			if (1==0) throw new IOException("hi");
//			//#tsResultsWriter.writeChangedFlights(wrappedFlightPlans, startDate);
//			//tsResultsWriter.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//			System.exit(-1);
//		}
//				
//		logger.log(Level.INFO, "Program complete!");
//		System.exit(0);
//	} 
}
