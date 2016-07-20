/**
 * Copyright "TBD", Metron Aviation & CSSI.  All rights reserved.
 * 
 * This computer Software was developed with the sponsorship of the U.S. Government
 * under Contract No. DTFAWA-10-D-00033, which has a copyright license in accordance with AMS 3.5-13.(c)(1).
 */

package gov.faa.ang.swac.common.flightmodeling.atmosphere;

import gov.faa.ang.swac.common.datatypes.Altitude;
import gov.faa.ang.swac.common.datatypes.Timestamp;
import gov.faa.ang.swac.common.geometry.GCPointAlt;
import gov.faa.ang.swac.common.geometry.GCPointAltTime;
import gov.faa.ang.swac.common.utilities.Mathematics;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import net.sourceforge.jgrib.GribPDSLevel;
import net.sourceforge.jgrib.GribRecord;
import net.sourceforge.jgrib.NoValidGribException;

/**
 * The WindData class returns the wind speed and heading for a given {@link GCPointAlt}.
 * <p>
 * Global gridded wind data is read from a specific GRIB file format. Only the "vgrd" and "ugrd" data types are retained, the rest is discarded.
 * <p>
 * Wind values for points not on the grid are obtained by 3D interpolation between grid points. (Wind data is NOT interpolated across time.)
 * All GRIB files from the specified location are processed as follows:<br>
 * - The file (or directory) specified is read<br>
 * - All NASPAC formatted GRIB files are checked to see if they contain wind data within the period "start date - stop date"<br>
 * - The wind data for those days is read and stored in chronological order<br>
 * <p>
 * <b>NOTE: An important assumption is that each GRIB file contains wind data for only one time stamp.</b><br>
 * @see http://www.nco.ncep.noaa.gov/pmb/docs/grib2/grib2_docs.shtml
 * @author Robert Lakatos
 */
public class WindData implements Cloneable
{
    private static org.apache.log4j.Logger logger = org.apache.log4j.LogManager.getLogger(WindData.class);

	private static final int levels[] = {1000, 925, 850, 700, 600, 500, 400, 300, 250, 200, 150, 100, 70, 50, 30, 20, 10};
	private static final int nx = 144;
	private static final int ny = 73;
	private static final int nz = 17;
	private static final double dx = 2.5;
	private static final double dy = -2.5;

	private static final double to_knots = 1.9438;
	private static final double degreesToRadians = Math.PI/180.0;
	
	private GribRecord[] vGrids[] = null;
	private GribRecord[] uGrids[] = null;
	private Timestamp gridDates[] = null;
	private boolean windDataLoaded = false;
	
	public WindData()
	{
	
	}
	
//	public WindData(String datapath, Timestamp startDateTime, Timestamp stopDateTime)
//    {
//		this( Arrays.asList(new String[] { datapath }),  startDateTime,  stopDateTime);
//    }
//	
	/**
	 *  If wind data is disabled it will always return 0 for wind speed and 0 for wind heading.
	 *  @param input is a single grib file name or a folder name containing grib files.
	 */
    public WindData(List<RawWindData> windFiles)
    {
    	// If wind directory not specified, don't bother trying to load data.
    	if (windFiles == null || windFiles.size() == 0)
    	{
    		logger.info("WindData: Wind database is initialized to return zero wind heading and magnitude.");
    	}
    	else
    	{
    	    this.windDataLoaded = parseGribFiles(windFiles);
    	}

		logger.debug("WindData class created successfully.");
	}

    public WindData(WindData org) {
        this.gridDates = new Timestamp[org.gridDates.length];
        System.arraycopy(org.gridDates, 0, this.gridDates, 0, org.gridDates.length);
        
        int max = 0;
        for (GribRecord[] a : org.uGrids) {
            max = Math.max(a.length, max);
        }
        
        this.uGrids = new GribRecord[org.uGrids.length][max];
        
        int i = 0;
        for (GribRecord[] a : org.uGrids) {
            System.arraycopy(a, 0, this.uGrids[i++], 0, a.length);
        }
        
        max = 0;
        for (GribRecord[] a : org.vGrids) {
            max = Math.max(a.length, max);
        }
        
        this.vGrids = new GribRecord[org.vGrids.length][max];
        
        i = 0;
        for (GribRecord[] a : org.vGrids) {
            System.arraycopy(a, 0, this.vGrids[i++], 0, a.length);
        }
        this.windDataLoaded = org.windDataLoaded;
    }
//    /**
//     * Returns the {@link GribRecordGDS} for the chronologically-first grid.
//     */
//    public GribRecordGDS getGDS() {
//		if (this.windDataLoaded)
//		{
//			return this.vGrids[0][0].getGDS();
//		}
//
//		return null;
//	}
//
//    /**
//     * Shifts the original grid {@link Timestamp}s so that wind data from any time
//     * period can be used on any arbitrary day.
//     * <p>
//     * e.g. If the wind data starts at 1/1/2007 12:30, and contains subsequent entries at
//     * 4 hours, 6 hours, and 10 hours after start... then calling setStartDate() with a
//     * {@link Timestamp} of 10/31/2008 15:00 will shift all of the wind data {@link Timestamp}s to begin
//     * at 10/31/2008 15:00, with the subsequent entries still being 4 hours, 6 hours, and
//     * 10 hours after the start.
//     * @param startDate
//     */
//    public void shiftTimestamps(Timestamp timestamp)
//    {
//    	if (this.windDataLoaded)
//    	{
//        	// replace the original grid time stamps to start from the simulation/schedule start date
//        	long currT;
//	        long timeShift = timestamp.getTime() - this.gridDates[0].getTime();
//        	for (int i = 0; i< this.gridDates.length; i++)
//        	{
//            	currT = this.gridDates[i].getTime();
//            	this.gridDates[i].setTime(currT + timeShift);
//        	}
//        }
//    }
//   
//   /**
//     * Returns all grid timestamps within the current dataset. 
//     */
//	public Timestamp[] getDates()
//	{
//		if (this.windDataLoaded)
//		{
//			return this.gridDates;
//		}
//		return null;
//	}
//	
//	/**
//	 * Returns the pressure levels at which wind grid data is evaluated.
//	 */
//	public int[] getLevels()
//	{
//		return levels;
//	}
//
//	/**
//	 * Returns the appropriate altitude for a given pressure level.
//	 */
//	public double getAltitude(double pressureLevel)
//    {
//        return Mathematics.round(StandardAtmosphere.findAltitude(pressureLevel),0);
//    }
	
    /**
	 * Returns the appropriate pressure level for the altitude of the given point.
	 */
    public double getPressureLevel(Altitude alt)
    {
        return Mathematics.round(StandardAtmosphere.findPressure(
            (int) Mathematics.round(alt.feet(),0)), 1);
    }
    
	/**
	 * Returns the appropriate pressure level for the altitude of the given point.
	 */
    public double getPressureLevel(GCPointAlt p1)
    {
        return Mathematics.round(StandardAtmosphere.findPressure(
            (int) Mathematics.round(p1.altitude().feet(),0)), 1);
    }
	
    /**
     * Returns the {@link WindPoint} corresponding to the given{@link GCPointAltTime}.
     * <p>
     * If the point p1 is not on the 3D grid for that date/time we interpolate the wind 
     * value using the nearest 3D grid points. Wind is not interpolated between two different
     * points in time.
     */
    public PointWind getWindPoint(GCPointAltTime p1)
    {
        // NOTE: Wind velocity components are interpolated before conversion to heading/speed
        // because heading is a discontinuous field hence tricky for interpolation.

    	if (!this.windDataLoaded)
    	{
    		return new PointWind();
    	}

    	double u = 0.0;
		double v = 0.0;
    	// prepare parameters for interpolation
    	double x = p1.longitude().degrees();
    	x = Mathematics.mod(x, 360.0);
		int x1i =  (int) (x / dx) % nx;
		int x2i = (x1i + 1) % nx;
		double x1 = x1i * dx;
		
		double y = p1.latitude().degrees();
		double absdy = Math.abs(dy);
		y = -y + 90.0;
		int y1i = (int) (y / absdy) % ny;
		int y2i = (y1i + 1) % ny;
		double y1 = y1i * absdy;
		
    	double z = getPressureLevel(p1); // pressure in millibars (i.e. hPa) - NOTE, pressure decreases with altitude!
		int zIndexes[] = getZIndicies(z);
		int z1i = zIndexes[0];
		int z2i = zIndexes[1];
		double z1 = levels[z1i];
		double z2 = levels[z2i];
		
		Timestamp t = p1.timestamp();
    	int ti = getTIndex(t);
    	
    	// interpolate u and v components of the wind vector one dimension at a time
    	try 
    	{
    		double rx = (x - x1) / dx;
        	double ry = (y - y1) / absdy;
    		// at level z1
    		double ux12y1z1 = (1 - rx) * getU(x1i, y1i, z1i, ti) + rx * getU(x2i, y1i, z1i, ti);
    		double ux12y2z1 = (1 - rx) * getU(x1i, y2i, z1i, ti) + rx * getU(x2i, y2i, z1i, ti);
    		double uxy12z1 = (1 - ry) * ux12y1z1 + ry * ux12y2z1;
    		double vx12y1z1 = (1 - rx) * getV(x1i, y1i, z1i, ti) + rx * getV(x2i, y1i, z1i, ti);
    		double vx12y2z1 = (1 - rx) * getV(x1i, y2i, z1i, ti) + rx * getV(x2i, y2i, z1i, ti);
    		double vxy12z1 = (1 - ry) * vx12y1z1 + ry * vx12y2z1;
    		if (z1i == z2i)
    		{
    			// there is only one level, so we are done
    			v = vxy12z1;
        		u = uxy12z1;
    		}
    		else
    		{
    			// we have one more level to deal with, z2
    			double rz = (z - z2) / (z1 - z2);
    			double vx12y1z2 = (1 - rx) * getV(x1i, y1i, z2i, ti) + rx * getV(x2i, y1i, z2i, ti);
        		double vx12y2z2 = (1 - rx) * getV(x1i, y2i, z2i, ti) + rx * getV(x2i, y2i, z2i, ti);
        		double vxy12z2 = (1 - ry) * vx12y1z2 + ry * vx12y2z2;
        		double vxyz12 = (1 - rz) * vxy12z2 + rz * vxy12z1;
        		v = vxyz12;
        		double ux12y1z2 = (1 - rx) * getU(x1i, y1i, z2i, ti) + rx * getU(x2i, y1i, z2i, ti);
        		double ux12y2z2 = (1 - rx) * getU(x1i, y2i, z2i, ti) + rx * getU(x2i, y2i, z2i, ti);
        		double uxy12z2 = (1 - ry) * ux12y1z2 + ry * ux12y2z2;
        		double uxyz12 = (1 - rz) * uxy12z2 + rz * uxy12z1;
        		u = uxyz12;
    		}
    	}
    	catch (Exception e)
    	{
            logger.fatal("Error during wind interpolation "+ e.getMessage() + ". Caught " + e + " attempting to access GRIB grid data.");
            e.printStackTrace();
    		throw new RuntimeException(e);
    	}
    	// now compute speed and heading
        double speed = to_knots * Math.sqrt(v*v + u*u);
        double heading = (Math.PI/2.0 - Math.atan2(v, u)) / degreesToRadians;
        return new PointWind(speed, heading);
    }
    
    /**
     * Reads all specified GRIB files as follows:<br>
     * - Each file is checked to see if it is a valid NASPAC GRIB file. If not, we exit.<br>
     * - Each valid file is checked to make sure that the data is between startDateTime and endDateTime. If not, the file is skipped.
     * - After all files are read, and the data is sorted chronologically.
     * @return true if no errors encountered, false otherwise.
     */
    private boolean parseGribFiles(List<RawWindData> windFiles)
    {
    	List<GribRecord[]> tempVGrids = new ArrayList<GribRecord[]>();
    	List<GribRecord[]> tempUGrids = new ArrayList<GribRecord[]>();
		HashMap<Date, Integer> dateToIndexMap = new HashMap<Date, Integer>();
		
		// create grib pressure levels
		GribPDSLevel pdsLevels[] = new GribPDSLevel[nz];
		for (int i = 0; i < nz; i++)
		{
			pdsLevels[i] = new GribPDSLevel(100, levels[i]>>8, levels[i]&255);
		}
		
		int validGribFileCount = 0;
		for (RawWindData wind : windFiles)
		{
			if (!wind.validNASPACType)
	   		{
	   			logger.warn("WindData: Skipping invalid GRIB file.");
	   			continue; // Skip to the next file 
	   		}

			dateToIndexMap.put(wind.date, validGribFileCount++);

			// store the 3D grids for a given date and given pressure levels
			tempUGrids.add(wind.tempUGrid);
			tempVGrids.add(wind.tempVGrid);
		}
		
		// If no wind data found... exit
		if (dateToIndexMap.size() == 0)
		{
			logger.fatal("No wind data found for simulation time range. Exiting...");
			throw new RuntimeException();
		}

		// reorder 3D grids in order of increasing time and store them in member variables
		// (but keep them in the compressed grib format)
		Date tempDates[] = new Date[dateToIndexMap.size()];
		tempDates = dateToIndexMap.keySet().toArray(tempDates);
		int m = tempDates.length;
		Arrays.sort(tempDates);
		this.uGrids = new GribRecord[m][];
		this.vGrids = new GribRecord[m][];
		this.gridDates = new Timestamp[m]; 
		for (int i = 0; i < m; i++)
		{
			this.gridDates[i] = new Timestamp(tempDates[i].getTime());
			this.uGrids[i] = tempUGrids.get(dateToIndexMap.get(tempDates[i]));
			this.vGrids[i] = tempVGrids.get(dateToIndexMap.get(tempDates[i]));
		}
		
		return true;
    }
    
    /**
     * Returns the u-component of wind for the point specified.
     * @param i input index of the grid in the x direction
	 * @param j input index of the grid in y direction
	 * @param z input index of the grid pressure level
	 * @param d input index of the grid time stamp
	 * @return the u-component of the wind at a given grid point
	 * @throws NoValidGribException
     */
	private double getU(int i, int j, int z, int d) throws NoValidGribException
	{
		return this.uGrids[d][z].getValue(i, j);
	}
	
	/**
     * Returns the v-component of wind for the point specified.
	 * @param i input index of the grid in the x direction
	 * @param j input index of the grid in y direction
	 * @param z input index of the grid pressure level
	 * @param d input index of the grid time stamp
	 * @return the v-component of the wind at a given grid point
	 * @throws NoValidGribException
	 */
	private double getV(int i, int j, int z, int d) throws NoValidGribException
	{
		return this.vGrids[d][z].getValue(i, j);
	}
	
	/**
	 * Returns the index of the last grid whose timestamp is <= the input timestamp.
	 * <p>
	 * @return Index of the last grid whose timestamp is <= the input timestamp.<br>
	 * If input time stamp occurs before first grid time, returns index of first grid.<br>
	 * If input time stamp occurs after last grid time, returns index of last grid.
	 */
	private int getTIndex(Timestamp t)
	{
    	int tIndex;
    	int first = 0;
    	int last = this.gridDates.length - 1;
    	if (t.before(this.gridDates[first]) || t.equals(this.gridDates[first]))
    	{
    		tIndex = first;
    	}
    	else if (this.gridDates[last].before(t) || t.equals(this.gridDates[last]))
    	{
    		tIndex = last;
    	}
    	else
    	{
    		int k = first;
    		while (t.after(this.gridDates[k])) { k++; }
    		if (t.equals(this.gridDates[k]))
    		{
    			tIndex = k;
    		}
    		else 
    		{
    			tIndex = k-1;
    		}
    	}
		return tIndex;
	}

	/**
	 * Returns the two pressure levels bounding the specified pressure level.
	 * <p>
     * NOTE: Pressure values are in decreasing order (pressure decreases with altitude)!
	 * @return Array containing the two pressure levels bounding the specified pressure level.<br>
	 * If specified pressure level is below the lowest pressure level, returns lowest pressure level (twice).<br>
	 * If specified pressure level is above the highest pressure level, returns highest pressure level (twice).<br>
	 */
	private int[] getZIndicies(double z)
	{
		int zIndex1, zIndex2;
		int highest = 0;
		int lowest = nz - 1;
    	if (z >= levels[highest])
    	{
    		zIndex1 = zIndex2 = highest;
    	}
    	else if (z <= levels[lowest])
    	{
    		zIndex1 = zIndex2 = lowest;
    	}
    	else
    	{
    		int k = highest;
    		while (z < levels[k]) { k++; }
    		if (z == levels[k]) // JLF TODO: Potentially dangerous double to int equality?
    		{
    			zIndex1 = zIndex2 = k;
    		}
    		else 
    		{
    			zIndex1 = k - 1;
    			zIndex2 = k;
    		}
    	}
    	
		return new int[] {zIndex1, zIndex2};
	}
        
        @Override
        public WindData clone() {
            return new WindData(this);
        }
}