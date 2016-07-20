package gov.faa.ang.swac.common.flightmodeling.jni;

import gov.faa.ang.swac.datalayer.storage.fileio.HeaderUtils;
import gov.faa.ang.swac.datalayer.storage.fileio.TextSerializable;
import gov.faa.ang.swac.datalayer.storage.fileio.WithHeader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;

public class GateDesignGroupMaskData implements Serializable, TextSerializable, WithHeader{
	
	private String etmsType;
	private String designGroupMask;
	
	
	@Override
	public long readHeader(BufferedReader reader) throws IOException {
		HeaderUtils.readHeaderHashComment(reader);
        return -1;
	}

	public String getEtmsType() {
		return etmsType;
	}

	public String getDesignGroupMask() {
		return designGroupMask;
	}


	@Override
	public void writeHeader(PrintWriter writer, long numRecords) throws IOException {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void readItem(BufferedReader reader) throws IOException {
		//System.out.println("GateDesignGroupMaskData::readItem");
		String line = reader.readLine();
		//System.out.println("=>" + line);
		
		if(line == null)
			return;
		
		line = line.trim();
		if (line.isEmpty())
			return;
		
		int q = 0;
		String[] fields = line.trim().split(",");
		etmsType = fields[q++];
		designGroupMask = fields[q++];
	}
	
	@Override
	public String toString()
	{
		return etmsType+","+designGroupMask;
	}
	
	@Override
	public void writeItem(PrintWriter writer) throws IOException {
		writer.println(this.toString());
	}
}
