/**
 * Copyright "2014", Metron Aviation & CSSI.  All rights reserved.
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
public class EquipmentSuaThreshold implements TextSerializable, WithHeader, Serializable, Cloneable
{
	private static final long serialVersionUID = -8932802041676891227L;
	
	public int equipmentType;
	public double minLevel;
	public double maxLevel;

    public EquipmentSuaThreshold() {
        this.minLevel = 0.0;
        this.maxLevel = 0.0;
    }

    public EquipmentSuaThreshold(EquipmentSuaThreshold org) {
        this.equipmentType = org.equipmentType;
        this.minLevel = org.minLevel;
        this.maxLevel = org.maxLevel;
    }
	
	@Override
	public void readItem(BufferedReader reader) throws IOException {
		String line = reader.readLine();
		String[] fields = line.trim().split(",");
		
		this.equipmentType = Integer.valueOf(fields[0]);
		this.minLevel =  Double.valueOf(fields[1]);
		this.maxLevel =  Double.valueOf(fields[2]);

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
    public EquipmentSuaThreshold clone() {
        return new EquipmentSuaThreshold(this);
    }
}
