/**
 * Copyright "TBD", Metron Aviation & CSSI.  All rights reserved.
 * 
 * This computer Software was developed with the sponsorship of the U.S. Government
 * under Contract No. DTFAWA-10-D-00033, which has a copyright license in accordance with AMS 3.5-13.(c)(1).
 */

package gov.faa.ang.swac.common.entities;


import gov.faa.ang.swac.common.Pair;
import gov.faa.ang.swac.datalayer.storage.fileio.TextSerializable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;


/**
 * A class to get minimum turn around times for itineraries.
 * Turn around times are mapped by Carrier (String) & Aircraft Type (String).
 * <p>
 * Turn around times reads from NASPAC Turn Around Times file to extract the minimum 
 * aircraft turn around times. Minimum turn around time is defined as the minimum time an 
 * aircraft must remain at a gate (after arriving) before it may depart that gate.
 * <p>
 * The NASPAC Turn Around Times file entries are expected to conform to the following format:
 * <code>&lt;Carrier&gt;, &lt;Aircraft BADA Type&gt;, &lt;Min. Turn Time (minutes)&gt; </code><p>
 * The file no longer uses an optional header line to define a default turn around time. 
 * <p>
 * The default minimum time can be set by calling setDefaultTurnaroundMinutes.   
 * <p>
 * Blank lines and comment lines (lines beginning with <code>#</code>) are ignored.
 * 
 * @author Jason Femino - CSSI Inc.
 */
public class TurnAroundTimeData implements TextSerializable
{
	private static final long serialVersionUID = 4475023444496337260L;

	private static org.apache.log4j.Logger logger = org.apache.log4j.LogManager.getLogger(TurnAroundTimeData.class);
	
	private static final Pattern textRecordPattern = Pattern.compile(",");
    private static final int DEFAULT_TURN_AROUND_MINUTES = 60;
    
    
	private int defaultTurnAroundMinutes = DEFAULT_TURN_AROUND_MINUTES;
	private Map<Pair<String, String>, Integer> turnAroundMap = new HashMap<Pair<String, String>, Integer>(); 

	/**
	 * Sets the default turnaround time. This turnaround will be used on a call to getTurnAroundSeconds if no turnaround
	 * time exists for a given carrier and badaType.
	 * 
	 * @param _defaultTurnAroundMinutes
	 */
	public void setDefaultTurnaroundMinutes(Integer _defaultTurnAroundMinutes) {
		if (_defaultTurnAroundMinutes != null) {
			this.defaultTurnAroundMinutes = _defaultTurnAroundMinutes.intValue();
		}
	}
	
    /**
     * Given an airport, carrier code, and aircraft type, return the minimum turn around time in seconds.
     * 
     * @param carrier String value of the carrier code.
     * @param badaType String value of the aircraft type.
     * @return integer number of seconds for the minimum turn around time.
     */
    public int getTurnAroundSeconds(String carrier, String badaType)
    {
    	int turnAroundTime = this.defaultTurnAroundMinutes * 60;
    	
		Integer turnAroundTimeInteger = turnAroundMap.get(Pair.<String, String>create(carrier, badaType));
		if (turnAroundTimeInteger != null) { 
			turnAroundTime = turnAroundTimeInteger.intValue();
		}
    	
    	return turnAroundTime;
    }
    

	@Override
	public void readItem(BufferedReader reader) throws IOException {
		// Read each record from file
		String currentLine = nextDataLine(reader);
    	while (currentLine != null)
    	{
    		String [] fields = textRecordPattern.split(currentLine); //currentLine.split(",");
    		if (fields.length == 3) { 
            	String carrier = fields[0].trim();
            	String badaType = fields[1].trim();
            	Integer minutes = Integer.valueOf( fields[2].trim() );
            	
            	// Insert turn around time into map
        		Pair<String, String> keyPair = Pair.<String, String>create(carrier, badaType);
        		turnAroundMap.put(keyPair, (minutes * 60));
            }
            else
            {
            	logger.warn("TurnAroundFileReader error: Input \"" + currentLine + "\" does not match pattern \"" + textRecordPattern.pattern() + "\"! Returning null!");
            }

    		currentLine = nextDataLine(reader);
    	}
	}
	
	private String nextDataLine(BufferedReader reader) throws IOException { 
		String currentLine = reader.readLine();
        while (currentLine != null && (currentLine.startsWith("#") || currentLine.trim().length() == 0))
        {
	        currentLine = reader.readLine();
        }
        return currentLine;
	}

	@Override
	public void writeItem(PrintWriter writer) throws IOException {
		writer.println(this.toString());
	}
}