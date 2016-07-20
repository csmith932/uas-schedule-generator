package gov.faa.ang.swac.common.utilities;

import java.util.Calendar;
import java.util.Date;



public class SolarUtility {
	
	static double Deg2Rad(double degree) {
		return Math.PI*degree/180;
	}
	static double Rad2Deg(double radian) {
		return radian*180/Math.PI;
	}
	static double between(double minVal, double val, double maxVal) {
		double diff = maxVal - minVal;
		while (val < minVal) {
			val += diff;
		}
		while (val > maxVal) {
			val -= diff;
		}
		return val;
	}
	
	public static SolarData calcSunriseSet(Date date, double lat, double lon)
	{
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH) + 1;
		int day = cal.get(Calendar.DAY_OF_MONTH);
		double n1 = Math.floor(275*month/9);
		double n2 = Math.floor((month+9)/12);
		double n3 = 1 + Math.floor((year-4*Math.floor(year/4)+2)/3);
		double n = n1-n2*n3+day-30;
		double lngHour = lon/15;
		double tRise = n+((6-lngHour)/24);
		double tSet = n+((18-lngHour)/24);
		double mRise = 0.9856*tRise-3.289;
		double mSet = 0.9856*tSet-3.289;
		double lRise = mRise + (  1.916*Math.sin(Deg2Rad(mRise)) ) + ( 0.02*Math.sin(Deg2Rad(2*mRise)) ) + 282.634;
		lRise = between(0., lRise, 360.);
		double lSet = mSet + (1.916*Math.sin(Deg2Rad(mSet))) + (0.02*Math.sin(Deg2Rad(2*mSet))) + 282.634;
		lSet = between(0., lSet, 360.);
		double raRise = Rad2Deg(Math.atan(0.91764*Math.tan(Deg2Rad(lRise))));
		raRise = between(0., raRise, 360.);
		double raSet = Rad2Deg(Math.atan(0.91764*Math.tan(Deg2Rad(lSet))));
		raSet = between(0., raSet, 360.);
		double lQuadRise = Math.floor(lRise/90)*90.;
		double lQuadSet =  Math.floor(lSet/90)*90.;
		double raQuadRise =  Math.floor(raRise/90)*90.;
		double raQuadSet =  Math.floor(raSet/90)*90.;
		double raHoursRise = (raRise+lQuadRise-raQuadRise)/15.;
		double raHoursSet = (raSet+lQuadSet-raQuadSet)/15.;
		double sinDecRise = 0.39782* Math.sin(Deg2Rad(lRise));
		double sinDecSet = 0.39782* Math.sin(Deg2Rad(lSet));
		double cosDecRise = Math.cos(Math.asin(sinDecRise));
		double cosDecSet = Math.cos(Math.asin(sinDecSet));
		double cosHRise = (Math.cos(Deg2Rad(90.8333))-(sinDecRise*Math.sin(Deg2Rad(lat)))) / (cosDecRise*Math.cos(Deg2Rad(lat)));
		double cosHSet = (Math.cos(Deg2Rad(90.8333))-(sinDecSet*Math.sin(Deg2Rad(lat)))) / (cosDecSet*Math.cos(Deg2Rad(lat)));
		double hRise = (360-Rad2Deg(Math.acos(cosHRise)))/15.;
		double hSet = (Rad2Deg(Math.acos(cosHSet)))/15.;
		double timeRise = hRise+raHoursRise-(0.06571*tRise)-6.622;
		double timeSet = hSet+raHoursSet-(0.06571*tSet)-6.622;
		double utRise = timeRise - lngHour;
		double utSet = timeSet - lngHour;
		double offset = 0;
		double tSunrise = (utRise+offset)/24;
		if (tSunrise > 1.0) {
			tSunrise -= 1.0;
		}
		double tSunset;
		if (utSet+offset < 0) {
			tSunset = (utSet+offset+24)/24;
		}
		else {
			tSunset = (utSet+offset)/24.;
		}
		return new SolarData(tSunrise, tSunset);
	}
	
	public static boolean isDayLight(SolarData solarData, Date currDate) // compares just hour and min.
	{
		Calendar date = Calendar.getInstance();
		date.setTime(currDate);
		if( solarData.getSunsetHour() > solarData.getSunriseHour() )  { // sunrise and sunset hour different..
			if(date.get(Calendar.HOUR_OF_DAY) > solarData.getSunriseHour() && date.get(Calendar.HOUR_OF_DAY) < solarData.getSunsetHour() ) return true; // current hour between sunrise and sunset hours
			if( date.get(Calendar.HOUR_OF_DAY) == solarData.getSunriseHour() && date.get(Calendar.MINUTE) >= solarData.getSunriseMinAfterHour() ) return true;
			if(date.get(Calendar.HOUR_OF_DAY) == solarData.getSunsetHour() && date.get(Calendar.MINUTE) <= solarData.getSunsetMinAfterHour() ) return true;
		}
		else if( solarData.getSunsetHour() < solarData.getSunriseHour() ) { // sunset pushed to next day
			if( date.get(Calendar.HOUR_OF_DAY) > solarData.getSunriseHour() && date.get(Calendar.HOUR_OF_DAY) < 24.00 ) return true; // current hour between sunrise and sunset hours
			if( date.get(Calendar.HOUR_OF_DAY) < solarData.getSunriseHour() && date.get(Calendar.MINUTE) < solarData.getSunriseMinAfterHour() ) return true;
			if( date.get(Calendar.HOUR_OF_DAY) == solarData.getSunriseHour() && date.get(Calendar.MINUTE) >= solarData.getSunriseMinAfterHour() ) return true;
			if( date.get(Calendar.HOUR_OF_DAY) == solarData.getSunsetHour() && date.get(Calendar.MINUTE) <= solarData.getSunsetMinAfterHour() ) return true;
		}

		return false;
	}
	
	

}
