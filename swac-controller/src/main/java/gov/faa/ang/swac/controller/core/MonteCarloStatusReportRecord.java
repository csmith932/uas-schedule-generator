package gov.faa.ang.swac.controller.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import gov.faa.ang.swac.datalayer.storage.fileio.TextSerializable;
import gov.faa.ang.swac.datalayer.storage.fileio.WithHeader;

public class MonteCarloStatusReportRecord implements TextSerializable, WithHeader {
	private int scenarioExecutionId;
	private double runTime;
	private boolean success;
	private String errorMessage;
	public int getScenarioExecutionId() {
		return scenarioExecutionId;
	}
	public void setScenarioExecutionId(int scenarioExecutionId) {
		this.scenarioExecutionId = scenarioExecutionId;
	}
	public double getRunTime() {
		return runTime;
	}
	public void setRunTime(double runTime) {
		this.runTime = runTime;
	}
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	@Override
	public String toString() {
		return scenarioExecutionId + "," + runTime + ","
				+ success + "," + errorMessage;
	}
	@Override
	public long readHeader(BufferedReader reader) throws IOException {
		throw new UnsupportedOperationException("Report data not intended for reading");
	}
	@Override
	public void writeHeader(PrintWriter writer, long numRecords)
			throws IOException {
		writer.println("scenarioExecutionId,runTime,success,errorMessage");
	}
	@Override
	public void readItem(BufferedReader reader) throws IOException {
		throw new UnsupportedOperationException("Report data not intended for reading");
	}
	@Override
	public void writeItem(PrintWriter writer) throws IOException {
		writer.println(this.toString());
	}
}
