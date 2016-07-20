/**
 * Copyright "TBD", Metron Aviation & CSSI.  All rights reserved.
 * 
 * This computer Software was developed with the sponsorship of the U.S. Government
 * under Contract No. DTFAWA-10-D-00033, which has a copyright license in accordance with AMS 3.5-13.(c)(1).
 */

package gov.faa.ang.swac.common.javascript;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import gov.faa.ang.swac.datalayer.storage.fileio.TextSerializable;

import javax.script.Invocable;
import javax.script.ScriptException;

public abstract class JavaScript implements TextSerializable
{
	private RhinoScriptManager engine;
	
	protected final void initEngine() { 
            engine = new RhinoScriptManager();
	 }
	
	public Invocable getEngine()
	{
		return (Invocable) engine;
	}
	
	@Override
	public void readItem(BufferedReader reader) throws IOException {
            try {
                    engine.eval(reader);
		} catch (ScriptException se) {
                    throw new IOException(se);
		}
	}
	
	@Override
	public void writeItem(PrintWriter writer) throws IOException {
		writer.println(this.toString());
	}	
}
