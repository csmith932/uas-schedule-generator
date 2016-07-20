package gov.faa.ang.swac.common.entities;

public interface ParetoCurve {
	public String getParetoCurveName();

	public boolean isEmpty();
	public int numberOfPoints();

	public double getMaximumDepartureCapacity();
	public double getDepartureCapacity(int index);

	public double getMaximumArrivalCapacity();
	public double getArrivalCapacity(int index);
}