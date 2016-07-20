/**
 * Copyright "TBD", Metron Aviation & CSSI.  All rights reserved.
 * 
 * This computer Software was developed with the sponsorship of the U.S. Government
 * under Contract No. DTFAWA-10-D-00033, which has a copyright license in accordance with AMS 3.5-13.(c)(1).
 */

package gov.faa.ang.swac.common.flightmodeling.equipage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;

import gov.faa.ang.swac.datalayer.storage.fileio.HeaderUtils;
import gov.faa.ang.swac.datalayer.storage.fileio.TextSerializable;
import gov.faa.ang.swac.datalayer.storage.fileio.WithHeader;

/**
 * 
 * Replaced with gov.faa.ang.swac.reroute.VilAssignmentScript in SWAC 2.1
 *
 */
@Deprecated
public class EquipmentWeatherThreshold implements TextSerializable, WithHeader, Serializable, Cloneable
{
	private static final long serialVersionUID = -8932802041676891227L;
	
	public int equipmentType;
	public double minConvection;
	public double maxConvection;

    public EquipmentWeatherThreshold() {
        this.minConvection = 1.0;
        this.maxConvection = 4.0;
    }

    public EquipmentWeatherThreshold(EquipmentWeatherThreshold org) {
        this.equipmentType = org.equipmentType;
        this.minConvection = org.minConvection;
        this.maxConvection = org.maxConvection;
    }
	
	@Override
	public void readItem(BufferedReader reader) throws IOException {
		String line = reader.readLine();
		String[] fields = line.trim().split(",");
		
		this.equipmentType = Integer.valueOf(fields[0]);
		this.minConvection =  Double.valueOf(fields[1]);
		this.maxConvection =  Double.valueOf(fields[2]);

	}
	
	@Override
	public void writeItem(PrintWriter writer) throws IOException {
		writer.println(this.toString());
	}
	
	@Override
	public long readHeader(BufferedReader reader) throws IOException {
		HeaderUtils.readHeaderHashComment(reader);
		return -1;
	}
	
	@Override
	public void writeHeader(PrintWriter arg0, long arg1) throws IOException {
		throw new UnsupportedOperationException();
	}
        
    @Override
    public EquipmentWeatherThreshold clone() {
        return new EquipmentWeatherThreshold(this);
    }
}
