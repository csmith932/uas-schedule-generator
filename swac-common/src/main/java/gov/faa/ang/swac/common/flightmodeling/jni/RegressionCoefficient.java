package gov.faa.ang.swac.common.flightmodeling.jni;

import gov.faa.ang.swac.datalayer.storage.fileio.HeaderUtils;
import gov.faa.ang.swac.datalayer.storage.fileio.TextSerializable;
import gov.faa.ang.swac.datalayer.storage.fileio.WithHeader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;

public class RegressionCoefficient implements Serializable, TextSerializable, WithHeader{
	
	private String type;
	private  double intercept;
	private  double queueIn;
	private  double queueOut;
	private  double daylight;
	private  double nomTaxiIn;
	private  double nomTaxiOut;
	private  double numRunways;
	private  double imc;
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public double getIntercept() {
		return intercept;
	}
	public void setIntercept(double intercept) {
		this.intercept = intercept;
	}
	public double getQueueIn() {
		return queueIn;
	}
	public void setQueueIn(double queueIn) {
		this.queueIn = queueIn;
	}
	public double getQueueOut() {
		return queueOut;
	}
	public void setQueueOut(double queueOut) {
		this.queueOut = queueOut;
	}
	public double getDaylight() {
		return daylight;
	}
	public void setDaylight(double daylight) {
		this.daylight = daylight;
	}
	public double getNomTaxiIn() {
		return nomTaxiIn;
	}
	public void setNomTaxiIn(double nomTaxiIn) {
		this.nomTaxiIn = nomTaxiIn;
	}
	public double getNomTaxiOut() {
		return nomTaxiOut;
	}
	public void setNomTaxiOut(double nomTaxiOut) {
		this.nomTaxiOut = nomTaxiOut;
	}
	public double getNumRunways() {
		return numRunways;
	}
	public void setNumRunways(double numRunways) {
		this.numRunways = numRunways;
	}
	public double getImc() {
		return imc;
	}
	public void setImc(double imc) {
		this.imc = imc;
	}
	
	@Override
	public long readHeader(BufferedReader reader) throws IOException {
		HeaderUtils.readHeaderHashComment(reader);
        return -1;
	}

	@Override
	public void writeHeader(PrintWriter writer, long numRecords) throws IOException {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void readItem(BufferedReader reader) throws IOException {
		
		String line = reader.readLine();
		if(line == null || line.trim().length() == 0)
		{
			return;
		}
		String[] fields = line.trim().split(",");
		type = fields[0];
		intercept = Double.valueOf(fields[1]);
		queueIn = Double.valueOf(fields[2]);
		queueOut = Double.valueOf(fields[3]);
		daylight = Double.valueOf(fields[4]);
		if(type.equals("TAXI_IN"))
		{
			nomTaxiIn = Double.valueOf(fields[5]);
		}
		else
		{
			nomTaxiOut = Double.valueOf(fields[5]);
		}
		numRunways= Double.valueOf(fields[6]);
		imc= Double.valueOf(fields[7]);
	}
	
	public String toString()
	{
		return type+","+intercept+","+queueIn+","+queueOut+","+daylight+","+(type.equals("TAXI_IN")?nomTaxiIn:nomTaxiOut)+","+numRunways+","+imc;
	}
	
	@Override
	public void writeItem(PrintWriter writer) throws IOException {
		writer.println(this.toString());
	}
	
	

}
