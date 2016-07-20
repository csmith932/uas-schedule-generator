/**
 * Copyright 2011, Metron Aviation & CSSI.  All rights reserved.
 * 
 * This computer Software was developed with the sponsorship of the U.S. Government
 * under Contract No. DTFAWA-10-D-00033, which has a copyright license in accordance with AMS 3.5-13.(c)(1).
 */

package gov.faa.ang.swac.common.utilities;

import gov.faa.ang.swac.common.datatypes.Timestamp;
import gov.faa.ang.swac.datalayer.storage.fileio.HeaderUtils;
import gov.faa.ang.swac.datalayer.storage.fileio.TextSerializable;
import gov.faa.ang.swac.datalayer.storage.fileio.WithHeader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * The data object that will hold a Weather Control file row object
 * @author ikkurti
 *
 */
public class WxControlFileRecord implements Comparable<WxControlFileRecord>, TextSerializable, WithHeader
{
    private static final Logger logger = LogManager.getLogger(WxControlFileRecord.class);
    
    /** The timestamp of the weather event. */
	private Timestamp timeStamp;
	
	/** List of names of the files containing predicted Wx polygons, activated SUA polygons, etc. */
	private List<String> polygonFileNames = new ArrayList<String>();
	
	/**
     * Default constructor is needed for construction-by-reflection
     */
	public WxControlFileRecord() {   }
	
	public WxControlFileRecord(String timeStamp, List<String> fileNameList){
		this.timeStamp = Timestamp.myValueOf(timeStamp);
		this.polygonFileNames.addAll(fileNameList);
	}
	
	public Timestamp getTimeStamp(){
		return this.timeStamp;
	}
	
	public List<String> getPolygonFileNames(){
		return this.polygonFileNames;
	}
	
    // ---------------------------------------------------------------------
    // Comparable Interface Implementation
    // ---------------------------------------------------------------------	
	@Override
    public int compareTo(WxControlFileRecord otherRecord){
        return this.timeStamp.compareTo(otherRecord.timeStamp);
    }

	// ---------------------------------------------------------------------
    // TextSerializable Interface Implementation
    // ---------------------------------------------------------------------
    @Override
    public void readItem(BufferedReader reader) throws IOException{
    	logger.debug("Loading a record from schedule control file ...");
        String[] fields = reader.readLine().split(",");
        String datetimestr = fields[0].trim();
        this.timeStamp = Timestamp.myValueOf(datetimestr);
        for(int i=1; i<fields.length; i++){
            this.polygonFileNames.add(fields[i].trim());
        }
    }

    @Override
    public void writeItem(PrintWriter writer) throws IOException{
    	writer.println(this.toString());
    }

    // ---------------------------------------------------------------------
    // WithHeader Interface Implementation
    // ---------------------------------------------------------------------
    /**
     * Parses any file header comment lines beginning with '#' 
     */
    @Override
    public long readHeader(BufferedReader reader) throws IOException{
        HeaderUtils.readHeaderHashComment(reader);
        return -1;
    }

    @Override
    public void writeHeader(PrintWriter writer, long numRecords) throws IOException{
        throw new UnsupportedOperationException();
        
    }
}
