package gov.faa.ang.swac.common.utilities;

public class SolarData {
	
	double sunrise;
	double sunset;
	

	
	public SolarData(double sunrise, double sunset) {
		super();
		this.sunrise = sunrise;
		this.sunset = sunset;
	}

	public double getSunrise() {
		return sunrise;
	}

	public void setSunrise(double sunrise) {
		this.sunrise = sunrise;
	}

	public double getSunset() {
		return sunset;
	}

	public void setSunset(double sunset) {
		this.sunset = sunset;
	}

	public int getSunriseHour() 
	{ 
		return getHour(sunrise); 
	}
	public int getSunriseMinAfterHour() 
	{ 
		return getMinute(sunrise); 
	}
	public int getSunsetHour() 
	{ 
		return getHour(sunset); 
	}
	public int getSunsetMinAfterHour() 
	{ 
		return getMinute(sunset); 
	}
	
	private int getHour(double dayFraction)  {
		return (int) (Math.floor(dayFraction*24));
	}

	private int getMinute(double dayFraction) {
		double exactMinute = dayFraction*24*60 - getHour(dayFraction)*60;
		return (int) (Math.floor(exactMinute));
	}

}
