/**
 * Copyright "TBD", Metron Aviation & CSSI.  All rights reserved.
 * 
 * This computer Software was developed with the sponsorship of the U.S. Government
 * under Contract No. DTFAWA-10-D-00033, which has a copyright license in accordance with AMS 3.5-13.(c)(1).
 */

package gov.faa.ang.swac.common.flightmodeling.atmosphere;

import gov.faa.ang.swac.datalayer.storage.fileio.StreamSerializable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import net.sourceforge.jgrib.GribFile;
import net.sourceforge.jgrib.GribPDSLevel;
import net.sourceforge.jgrib.GribRecord;
import net.sourceforge.jgrib.GribRecordGDS;
import net.sourceforge.jgrib.NoValidGribException;
import net.sourceforge.jgrib.NotSupportedException;

public class RawWindData implements StreamSerializable
{
	private static final Logger logger = LogManager.getLogger(RawWindData.class);
	
	private static final int levels[] = {1000, 925, 850, 700, 600, 500, 400, 300, 250, 200, 150, 100, 70, 50, 30, 20, 10};
	private static final int nx = 144;
	private static final int ny = 73;
	private static final int nz = 17;
	private static final double dx = 2.5;
	private static final double dy = -2.5;
	
	private static final GribPDSLevel pdsLevels[] = initPdsLevels();
	private static GribPDSLevel[] initPdsLevels()
	{
		// create grib pressure levels
		GribPDSLevel pdsLevels[] = new GribPDSLevel[nz];
		for (int i = 0; i < nz; i++)
		{
			pdsLevels[i] = new GribPDSLevel(100, levels[i]>>8, levels[i]&255);
		}
		return pdsLevels;
	}
	
	public GribRecord[] tempVGrid;
	public GribRecord[] tempUGrid;
	public Date date;
	boolean validNASPACType = false;

	@Override
	public void readItem(InputStream in) throws IOException
	{
		GribFile aGribFile = null;
		GribRecordGDS gds = null;
		boolean validNASPACType = false;
		
		// from each grib file collect the required grids
		try
		{
			aGribFile = new GribFile(in);     // Create new GribFile
			gds = aGribFile.getRecord(1).getGDS(); // Extract record
	   		validNASPACType =                      // Check if record is valid for NASPAC
	   			gds.getGridType() == 0 && gds.getGridNX() == nx && gds.getGridNY() == ny &&  gds.getGridScanmode() == 0 &&
	   			gds.getGridDX() == dx && gds.getGridDY() == dy && gds.getGridLat1() == 90.0 && gds.getGridLon1() == 0.0 &&
	   			gds.getGridLat2() == -90.0 && gds.getGridLon2()== -2.5 && gds.isUVEastNorth() && gds.getGridMode() == 128;
		}
		catch (NotSupportedException nsex)
		{
			logger.fatal("File contains unsupported GRIB data: " + nsex.toString());
			nsex.printStackTrace();
			throw new RuntimeException();
		}
		catch (NoValidGribException nvgex)
		{
			logger.fatal("File does not contain valid GRIB data: " + nvgex.toString());
			nvgex.printStackTrace();
			throw new RuntimeException();
		}
		catch (Exception e)
		{
			logger.fatal("Exception while reading file: " + e.toString());
			e.printStackTrace();
			throw new RuntimeException(e);
		}

			if (!validNASPACType || aGribFile == null)
			{
				logger.warn("WindData: Skipping invalid GRIB file.");
				return; // Skip to the next file 
			}
			else
			{
				this.validNASPACType = true;
			}

		// get the grid time stamp
		Date dates[] = aGribFile.getDatesForTypeGridLevel("vgrd", gds, pdsLevels[0]);
		date = dates[0];

			try
			{
				// store the 3D grids for a given date and given pressure levels
				tempUGrid = aGribFile.get3dRecord("ugrd", gds, pdsLevels, date);
				tempVGrid = aGribFile.get3dRecord("vgrd", gds, pdsLevels, date);
			}
			catch (Exception e)
			{
				logger.fatal("WindData: Exception while reading file: " + e.toString());
				e.printStackTrace();
	    		throw new RuntimeException(e);
		}
	}
	
	@Override
	public void writeItem(OutputStream out) throws IOException
	{
		throw new UnsupportedOperationException();
	}
}
