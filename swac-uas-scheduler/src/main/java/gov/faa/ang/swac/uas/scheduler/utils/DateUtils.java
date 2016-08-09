package gov.faa.ang.swac.uas.scheduler.utils;

import java.util.Calendar;
import java.util.GregorianCalendar;

import gov.faa.ang.swac.common.datatypes.Timestamp;

public class DateUtils {
	public static String getQuarter(Timestamp date) {
		Calendar c = new GregorianCalendar();
		c.setTimeInMillis(date.getTime());
		switch (c.get(Calendar.MONTH)) {
			case 0:
			case 1:
			case 2:
				return "Q2";
			case 3:
			case 4:
			case 5:
				return "Q3";
			case 6:
			case 7:
			case 8:
				return "Q4";
			case 9:
			case 10:
			case 11:
				return "Q1";
			default:
				throw new IllegalArgumentException();
		}
	}
}
