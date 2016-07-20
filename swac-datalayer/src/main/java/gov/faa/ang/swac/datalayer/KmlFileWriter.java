/**
 * Copyright "TBD", Metron Aviation & CSSI.  All rights reserved.
 * 
 * This computer Software was developed with the sponsorship of the U.S. Government
 * under Contract No. DTFAWA-10-D-00033, which has a copyright license in accordance with AMS 3.5-13.(c)(1).
 */

package gov.faa.ang.swac.datalayer;

import java.io.PrintWriter;

public class KmlFileWriter
{
	private PrintWriter output;
	
	public KmlFileWriter(String documentName, PrintWriter writer)
	{
		this.output = writer;
        this.output.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
                         "<kml xmlns=\"http://earth.google.com/kml/2.2\">\n" +
                         "<Document>\n" +
                         "       <name>" + documentName + "</name>\n" +
					     "       <open>1</open>\n"); 
	}
	
	public void write(String str)
	{
		this.output.write(str);
	}

	public void flush()
	{
		this.output.flush();
	}

	public void folderOpen(String name)
	{
		write("       <Folder>\n" +
			  "              <name>" + name + "</name>\n");
	}

	public void folderClose()
	{
		write("       </Folder>\n");
	}

	public void close()
	{
		this.output.write("</Document>\n" +
		  			 "</kml>\n");
		this.output.flush();
		
		this.output.close();
	}
	

}