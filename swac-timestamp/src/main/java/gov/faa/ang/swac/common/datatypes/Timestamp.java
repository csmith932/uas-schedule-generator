package gov.faa.ang.swac.common.datatypes;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * Extension of {@link java.util.Date} to include formatting in the forms:<br>
 * "<code>yyyymmdd hh24:mi:ss</code>" and "<code>mm/dd/yyyy hh24:mi:ss</code>".
 * <p>
 * Also contains methods to perform some math on {@link Timestamp}s.
 */
public class Timestamp implements Serializable, Cloneable, Comparable<Timestamp>
{
	private static Logger logger = LogManager.getLogger(Timestamp.class);
	
	private static final long serialVersionUID = 2186892858616607300L;
	/*
	 * public static final String TEXT_RECORD_PATTERN = "(" + Patterns.DATE_YEAR + ")(" +
	 * Patterns.DATE_MONTH + ")(" + Patterns.DATE_DAY + ") (" + Patterns.TIME_HOUR_24 + "):(" +
	 * Patterns.TIME_MIN + "):(" + Patterns.TIME_SEC + ")"; public static final String
	 * TEXT_RECORD_PATTERN_NON_CAPTURING = "(:?" + Patterns.DATE_YEAR + ")(:?" + Patterns.DATE_MONTH
	 * + ")(:?" + Patterns.DATE_DAY + ") (:?" + Patterns.TIME_HOUR_24 + "):(:?" + Patterns.TIME_MIN
	 * + "):(:?" + Patterns.TIME_SEC + ")";
	 */
	public static final long MILLISECS_DAY = 86400000L;
	public static final long MILLISECS_HOUR = 3600000L;
	public static final long MILLISECS_MIN = 60000L;
	public static final long MILLISECS_SEC = 1000L;
	
	private static final ThreadLocalDateFormat sqlDateFormatterRef = new ThreadLocalDateFormat("yyyy-MM-dd HH:mm:ss.S");
	private static final ThreadLocalDateFormat bonnDateFormatterRef = new ThreadLocalDateFormat("yyyyMMdd HH:mm:ss");
	private static final ThreadLocalDateFormat bonnDateOnlyFormatterRef = new ThreadLocalDateFormat("yyyyMMdd");
	private static final ThreadLocalDateFormat msAccessDateFormatterRef = new ThreadLocalDateFormat("MM/dd/yyyy HH:mm");
	private static final ThreadLocalDateFormat iso8601DateFormatterRef = new ThreadLocalDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
	private static final ThreadLocalDateFormat iso8601SubsecondsDateFormatterRef = new ThreadLocalDateFormat("yyyy-MM-dd'T'HH:mm:ss.S'Z'");
	private static final ThreadLocalDateFormat timeFormatterRef = new ThreadLocalDateFormat("HH:mm:ss");
	private static final ThreadLocalDateFormat bonnSubsecondsRef = new ThreadLocalDateFormat("yyyyMMdd HH:mm:ss.S");
	private static final ThreadLocalDateFormat trajectoryPointFormatterRef = new ThreadLocalDateFormat("HH:mm:ss.SSS");
	
	private Date date;
	/**
	 * @deprecated Use toSQLString() instead.
     */
	@Deprecated
	public static final DateFormat getSQLDateFormatter() { return sqlDateFormatterRef.get(); }
	/**
	 * @deprecated Use toBonnString() instead.
     */
	@Deprecated
	public static final DateFormat getBonnDateFormatter() { return bonnDateFormatterRef.get(); }
	/**
	 * @deprecated Use toBonnDateOnlyString() instead.
     */
	@Deprecated
	public static final DateFormat getBonnDateOnlyFormatter() { return bonnDateOnlyFormatterRef.get(); }
	/**
	 * @deprecated Use toMSAccessAccessString() instead
     */
	@Deprecated
	public static final DateFormat getMSAccessDateFormatter() { return msAccessDateFormatterRef.get(); }
	/**
	 * @deprecated Use toISO8601(false) instead
     */
	@Deprecated
	public static final DateFormat getISO8601DateFormatter() { return iso8601DateFormatterRef.get(); }
	/**
	 * @deprecated Use toISO8601(true) instead
	 */
	@Deprecated
	public static final DateFormat getISO8601SubsecondsDateFormatter() { return iso8601SubsecondsDateFormatterRef.get(); }
	/**
	 * @deprecated Use toTimeString() instead
	 */
	@Deprecated
	public static final DateFormat getTimeFormatter() { return timeFormatterRef.get(); }

	/**
	 * Converts a time interval in minutes to a time interval in milliseconds
	 * 
	 * @param valueInMin time interval in minutes
	 * @return time interval in milliseconds
	 */
	public static long minutesToMillis(double valueInMin) {
		return Math.round(valueInMin * Timestamp.MILLISECS_MIN);
	}

	/**
	 * Converts a time interval in milliseconds to a time interval in minutes
	 * 
	 * @param valueInMillis time interval in milliseconds
	 * @return time interval in minutes
	 */
	public static double millisToMin(long valueInMillis) {
		return (double) valueInMillis / (double) Timestamp.MILLISECS_MIN;
	}
		     
	/**
	 * Default constructor. Same as {@link #Timestamp(long)} with the input of
	 * 0.
	 * 
	 * @see Timestamp(long)
	 */
	public Timestamp()
	{
		this(0L);
	}
	
	/**
	 * Same as {@link java.util.Date#Date(long)}
	 * 
	 * @param time
	 *            Long integer. See {@link java.util.Date#Date(long)}
	 * @see java.util.Date#Date(long)
	 */
	public Timestamp(long time)
	{
		date = new Date(time);
	}
	
	public Timestamp(Timestamp timestamp)
	{
		this(timestamp.date.getTime());
	}
	
	/**
	 * Returns a Timestamp representing the current time 
	 *  
	 * @return A Timestamp representing the current time
	 */
	public static Timestamp now() { 
		return new Timestamp(System.currentTimeMillis());
	}
	
	/**
	 * Same as {@link java.text.DateFormat#parse(String)} except the String is
	 * of format " <code>yyyymmdd hh24:mi:ss</code>" or "
	 * <code>mm/dd/yyyy hh24:mi:ss</code>" where the time data is optional (for
	 * example, can input a date in the format of "<code>yyyymmdd</code>" with
	 * no time and the time will be set to midnight).
	 * 
	 * @param s
	 *            Date/Time String in format of "
	 *            <code>yyyymmdd hh24:mi:ss</code>"
	 * @return New {@link Timestamp} variable.
	 * @see java.text.DateFormat#parse(String)
	 */
	public static Timestamp myValueOf(String s)
	{
		if (s == null) return null;
		s = s.trim();
		if (s.isEmpty()) return null;
		
		try
		{
			return myValueOf(Timestamp.bonnDateFormatterRef, s);
		} catch (ParseException pe1)
		{
			try
			{
				return myValueOf(Timestamp.bonnDateOnlyFormatterRef, s);
			} catch (ParseException pe2)
			{
				logger.warn(pe2);
			}
		}
		return null;
	}
	
	/**
	 * Given a data String and a DateFormat parser, returns a Timestamp parsed
	 * from the given String using the given date parser.
	 * 
	 * @param dateFormatParser
	 * 			  Date/Time parser
	 * @param dateStr
	 *            Date/Time String
	 * @return New {@link Timestamp} variable.
	 * 
	 */
	public static Timestamp myValueOf(DateFormat dateFormatParser, String dateStr) throws ParseException {
		return new Timestamp(dateFormatParser.parse(dateStr).getTime());
	}
	
	public static Timestamp fromBonnSubsecondsString(String dateStr) throws ParseException {
		return myValueOf(bonnSubsecondsRef, dateStr);
	}
	
	public static Timestamp fromBonnDateOnlyString(String dateStr) throws ParseException  {
		return myValueOf(bonnDateOnlyFormatterRef, dateStr);
	}
	
	public static Timestamp fromBonnString(String dateStr) throws ParseException  {
		return myValueOf(bonnDateFormatterRef, dateStr);
	}
	
	/**
	 * Convert a {@link java.util.Date} to a new {@link Timestamp}
	 * 
	 * @param t
	 *            {@link java.util.Date}
	 * @return new {@link Timestamp} with same value as t
	 */
	public static Timestamp myValueOf(Date t)
	{
		if (t == null) {
			return null;
		}
		
		return new Timestamp(t.getTime());
	}

	/**
	 * Returns the maximum of the two given Timestamps.
	 * @param timestamp1
	 * @param timestamp2
	 * @return
	 */
	public static Timestamp max(Timestamp timestamp1, Timestamp timestamp2) {
		return (timestamp1.date.before(timestamp2.date)) ? timestamp2 : timestamp1;
	}
	
	/**
	 * Returns the minimum of the two given Timestamps.
	 * @param timestamp1
	 * @param timestamp2
	 * @return
	 */
	public static Timestamp min(Timestamp timestamp1, Timestamp timestamp2) {
		return (timestamp1.date.before(timestamp2.date)) ? timestamp1 : timestamp2;
	}
	
	/**
	 * Tests if this Timestamp is equal to or after the specified timestamp.
	 * 
	 * @param timestamp
	 * @return
	 */
	public boolean beforeOrEqualTo(Timestamp timestamp) {
		return ! this.date.after(timestamp.date);
	}
	
	/**
	 * Tests if this Timestamp is equal to or before the specified timestamp.
	 * 
	 * @param timestamp
	 * @return
	 */
	public boolean afterOrEqualTo(Timestamp timestamp) {
		return ! this.date.before(timestamp.date);
	}
	
	/**
	 * Convert this {@link Timestamp} to a {@link java.util.Date}
	 * 
	 * @return new {@link java.sql.Timestamp} with same value as current
	 *         {@link Timestamp}
	 */
	public java.sql.Timestamp toTimestamp()
	{
		return new java.sql.Timestamp(date.getTime());
	}
	
	
	/**
	 * Returns true if Timestamp value is greater than or equal to the given start range value and less than or equal to
	 * the given end range value.
	 * 
	 * @param startRange
	 * @param endRange
	 * @return
	 */
	public boolean inRange(Timestamp startRange, Timestamp endRange) { 
		return inRange(startRange, endRange, true, true);
	}
	
	/**
	 * Returns true if Timestamp value is greater than given start range value, or equal to it if startIncl is true, and
	 * less than the given end range value, or equal to it if endIncl is true.
	 * 
	 * @param startRange
	 * @param endRange
	 * @return
	 */
	public boolean inRange(Timestamp startRange, Timestamp endRange, boolean startIncl, boolean endIncl) {
		boolean beforeStart = false;
		if (startIncl) 
			beforeStart = this.date.before(startRange.date);
		else
			beforeStart = this.beforeOrEqualTo(startRange);
		if (beforeStart)
			return false;
		
		boolean afterEnd = false;
		if (endIncl) 
			afterEnd = this.date.after(startRange.date);
		else
			afterEnd = this.afterOrEqualTo(startRange);
		if (afterEnd)
			return false;
		
		return true;
	}
	
	/**
	 * Add a number of days to the current {@link Timestamp} and return a new
	 * {@link Timestamp}.
	 * 
	 * @param days
	 *            double number of days to add to current {@link Timestamp}.
	 * @return new {@link Timestamp} with value of current plus 'days' number of
	 *         days.
	 */
	public Timestamp dayAdd(double days)
	{
		return (new Timestamp(date.getTime() + Math.round(days * MILLISECS_DAY)));
	}
	
	/**
	 * Find the number of days from input baseTimestamp to current value. Can be
	 * decimal for fraction of days. Output is equivalent to current minus
	 * baseTimestamp
	 * 
	 * @param baseTimestamp
	 *            {@link Timestamp} value to be subtracted from current.
	 * @return double number of days between {@link Timestamp} values, can be
	 *         fractional.
	 */
	public double dayDifference(Timestamp baseTimestamp)
	{
		return milliDifferenceInDouble(baseTimestamp) / MILLISECS_DAY;
	}
	
	/**
	 * Find the number of hours from input baseTimestamp to current value. Can
	 * be decimal for fraction of hours. Output is equivalent to current minus
	 * baseTimestamp
	 * 
	 * @param baseTimestamp
	 *            {@link Timestamp} value to be subtracted from current.
	 * @return double number of hours between {@link Timestamp} values, can be
	 *         fractional.
	 */
	public double hourDifference(Timestamp baseTimestamp)
	{
		return milliDifferenceInDouble(baseTimestamp) / MILLISECS_HOUR;
	}
	
	/**
	 * Find the number of minutes from input baseTimestamp to current value. Can
	 * be decimal for fraction of minutes. Output is equivalent to current minus
	 * baseTimestamp
	 * 
	 * @param baseTimestamp
	 *            {@link Timestamp} value to be subtracted from current.
	 * @return double number of minutes between {@link Timestamp} values, can be
	 *         fractional.
	 */
	public double minDifference(Timestamp baseTimestamp)
	{
		return milliDifferenceInDouble(baseTimestamp) / MILLISECS_MIN;
	}
	
	/**
	 * Find the number of seconds between the current {@link Timestamp} and
	 * baseTimestamp
	 * 
	 * @param baseTimestamp
	 *            {@link Timestamp} value to be subtracted from the current.
	 * @return double number of seconds, current value minus baseTimestamp.
	 */
	public double secDifference(Timestamp baseTimestamp)
	{
		return milliDifferenceInDouble(baseTimestamp) / MILLISECS_SEC;
	}
	
	/**
	 * Find the number of milliseconds between the current {@link Timestamp} and
	 * baseTimestamp
	 * 
	 * @param baseTimestamp
	 *            {@link Timestamp} value to be subtracted from the current.
	 * @return double number of milliseconds, current value minus baseTimestamp.
	 */
	public long milliDifference(Timestamp baseTimestamp) {
		return (date.getTime() - baseTimestamp.date.getTime());
	}
	
	
	private double milliDifferenceInDouble(Timestamp baseTimestamp)
	{
		return milliDifference(baseTimestamp);
	}
	
	/**
	 * Add a number of hours to the current {@link Timestamp} and return a new
	 * {@link Timestamp}.
	 * 
	 * @param hours
	 *            double number of hours to add to current {@link Timestamp}.
	 * @return new {@link Timestamp} with value of current plus 'hours' number
	 *         of hours.
	 */
	public Timestamp hourAdd(double hours)
	{
		return (new Timestamp(date.getTime() + Math.round(hours * MILLISECS_HOUR)));
	}
	
	/**
	 * Add a number of seconds to the current {@link Timestamp} and return a new
	 * {@link Timestamp}.
	 * 
	 * @param secs
	 *            double number of seconds to add to current {@link Timestamp}.
	 * @return new {@link Timestamp} with value of current plus 'secs' number of
	 *         seconds.
	 */
	public Timestamp secondAdd(double secs)
	{
		return (new Timestamp(date.getTime() + Math.round(secs * MILLISECS_SEC)));
	}
	
	/**
	 * Add a number of minutes to the current {@link Timestamp} and return a new
	 * {@link Timestamp}.
	 * 
	 * @param mins
	 *            double number of minutes to add to current {@link Timestamp}.
	 * @return new {@link Timestamp} with value of current plus 'mins' number of
	 *         minutes.
	 */
	public Timestamp minuteAdd(double mins)
	{
		return (new Timestamp(date.getTime() + Math.round(mins * MILLISECS_MIN)));
	}
		
	/**
	 * Remove a number of minutes from the current {@link Timestamp} and return
	 * a new {@link Timestamp}.
	 * 
	 * @param mins
	 *            double number of minutes to remove from current
	 *            {@link Timestamp}.
	 * @return new {@link Timestamp} with value of current minus 'mins' number
	 *         of minutes. NOTE: returns original time if 'mins' can't be
	 *         subtracted safely.
	 */
	public Timestamp minuteSubtract(double mins)
	{
		// TODO (JP) - Fix this logic. It is perfectly legal to subtract minutes to a negative value, as Timestamp will interpret it reasonably anyway.
		//you can't subtract 'mins' if it gives you a negative value
		if (date.getTime() - Math.round(mins * MILLISECS_MIN) >= 0) {
			return (new Timestamp(date.getTime() - Math.round(mins * MILLISECS_MIN)));
		} else {
			return new Timestamp(date.getTime());
		}
	}
	
	/**
	 * Add a number of milliseconds to the current {@link Timestamp} and return a new
	 * {@link Timestamp}.
	 * 
	 * @param millis
	 *            long number of millis to add to current {@link Timestamp}.
	 * @return new {@link Timestamp} with value of current plus 'millis' number of
	 *         milliseconds.
	 */
	public Timestamp milliAdd(long millis)
	{
		return new Timestamp(date.getTime() + millis);
	}
	
	/**
	 * Add the number of milliseconds from both parameters to the current {@link Timestamp} and return a new
	 * {@link Timestamp}.
	 * 
	 * @param millis 
	 * 			long number of millis to add to current {@link Timestamp}.
	 * @return new {@link Timestamp} with value of current plus 'millis' number of milliseconds.
	 */
	public Timestamp milliAdd(long milli1, long milli2)
	{
		return new Timestamp(date.getTime() + milli1 + milli2);
	}
	
	/**
	 * Add a number of cumulative milliseconds from given millis array to the current {@link Timestamp} and return a new
	 * {@link Timestamp}.
	 * 
	 * @param millis long number of millis to add to current {@link Timestamp}.
	 * @return new {@link Timestamp} with value of current plus 'millis' number of milliseconds.
	 */
	public Timestamp milliAdd(long... millis)
	{
		long totalMillis = 0;
		for (int i = 0; i < millis.length; i++) 
			totalMillis += millis[i];
		return new Timestamp(date.getTime() + totalMillis);
	}
	
	/**
	 * Remove a number of milliseconds from the current {@link Timestamp} and return
	 * a new {@link Timestamp}.
	 * 
	 * @param millis
	 *            long number of millis to remove from current
	 *            {@link Timestamp}.
	 * @return new {@link Timestamp} with value of current minus 'millis' number
	 *         of milliseconds.
	 */
	public Timestamp milliSubtract(long millis)
	{
		return new Timestamp(date.getTime() - millis);
	}
	
	/**
	 * Returns true if the current {@link Timestamp} and the input
	 * {@link Timestamp} have the same value, not just if they are the same
	 * reference.
	 * 
	 * @param t
	 *            {@link Timestamp} input to compare to the current
	 *            {@link Timestamp}
	 * @return true if values are equal, false if values differ.
	 */
	public boolean equalValue(Timestamp t)
	{
		if (t == null) {
			return false;
		}
		
		return (date.getTime() == t.date.getTime());
	}
	
	/**
	 * Truncate the current {@link Timestamp} to midnight.
	 */
	public Timestamp truncateToDay()
	{
		Calendar c = new GregorianCalendar();
		c.setTimeInMillis(date.getTime());
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		return new Timestamp(c.getTimeInMillis());
	}
	
	public Timestamp getTruncatedToHour() {
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		
		calendar.setTime(this.date);
		
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		
		return new Timestamp(calendar.getTime().getTime());
	}
	
    /**
	 * Returns a new Date with the same date value as the Timestamp.
	 * 
	 * Timestamp extends Date, so why bother? That might change at some point. Use of this method will be more robust to
	 * future change.
	 * 
	 * @return
	 */
    public Date toDate() { 
    	return new Date(date.getTime());
    }
    
	/**
	 * Returns string value of Timestamp in format "<code>yyyymmdd hh24:mi:ss</code>"
	 * 
	 * @return String in format "<code>yyyymmdd hh24:mi:ss</code>"
	 */
	@Override
	public String toString()
	{
		return toBonnString();
	}
	
	/**
	 * Returns string value of Timestamp using the given dateFormat
	 * 
	 * @return String in format given by dateFormat
	 */
	public String toString(DateFormat dateFormat)
	{
		return dateFormat.format(this.date);
	}

	/**
	 * Returns string value of Timestamp in one of two different formats
	 * corresponding to ISO8601 standards depending on value of input parameter.
	 * 
	 * @param includeDecimalSeconds
	 *            boolean, if true, output is in format "
	 *            <code>YYYY-MM-DDThh:mm:ss.sZ</code>",<br>
	 *            if false, output is in format "
	 *            <code>YYYY-MM-DDThh:mm:ssZ</code>" where T and Z are literal.
	 * @return string value of Timestamp in ISO8601 format
	 */
	public String toISO8601(boolean includeDecimalSeconds)
	{
		if (includeDecimalSeconds) {
			return toString(iso8601SubsecondsDateFormatterRef);
		} else {
			return toString(iso8601DateFormatterRef);
		}
	}

	/**
	 * Returns string value of Timestamp in format "<code>"yyyy-MM-dd HH:mm:ss.S"</code>"
	 * 
	 * @return String in format "<code>"yyyy-MM-dd HH:mm:ss.S"</code>"
	 */
	public String toSQLString() {
		return toString(sqlDateFormatterRef);
	}
	
	/**
	 * Returns string value of Timestamp in format "<code>"yyyyMMdd HH:mm:ss"</code>"
	 * 
	 * @return String in format "<code>"yyyyMMdd HH:mm:ss"</code>"
	 */
	public String toBonnString() {
		return toString(bonnDateFormatterRef);
	}
	
	/**
	 * Returns string value of the given date in format "<code>"yyyyMMdd HH:mm:ss"</code>"
	 * 
	 * @return String in format "<code>"yyyyMMdd HH:mm:ss"</code>"
	 */
	public static String toBonnString(Date date) {
		return bonnDateFormatterRef.get().format(date);
	}
	
	/**
	 * Returns string value of Timestamp in format "<code>"yyyyMMdd"</code>"
	 * 
	 * @return String in format "<code>"yyyyMMdd"</code>"
	 */
	public String toBonnDateOnlyString() {
		return toString(bonnDateOnlyFormatterRef);
	}
	
	/**
	 * Returns string value of the given date in format "<code>"yyyyMMdd"</code>"
	 * 
	 * @return String in format "<code>"yyyyMMdd"</code>"
	 */
	public static String toBonnDateOnlyString(Timestamp time) {
		return bonnDateOnlyFormatterRef.get().format(time.date);
	}
	
	/**
	 * Returns string value of Timestamp in format "<code>yyyymmdd hh24:mi:ss</code>"
	 * 
	 * @return String in format "<code>yyyymmdd hh24:mi:ss</code>"
	 */
	public String toBonnSubsecondsString() {
		return this.toString(bonnSubsecondsRef);
	}
	
	/**
	 * Returns string value of the given date in format "<code>yyyymmdd hh24:mi:ss</code>"
	 * 
	 * @return String in format "<code>yyyymmdd hh24:mi:ss</code>"
	 */
	public static String toBonnSubsecondsString(Date date) {
		return bonnSubsecondsRef.get().format(date);
	}
	
	/**
	 * This format was previously a static SimpleDateFormat in TrajectoryPoint.  Moved here for Thread safety.
	 * 
	 * @return String in format "<code>HH:mm:ss.SSS</code>"
	 */
	public String toTrajectoryPointString(){
		return this.toString(trajectoryPointFormatterRef);
	}
	
	
	/**
	 * Returns string value of the Timestamp in format "<code>HH:mm:ss</code>"
	 * 
	 * @return String in format "<code>HH:mm:ss</code>"
	 */
	public String toTimeString() {
		return this.toString(timeFormatterRef);
	}
	
	/**
	 * Returns string value of the given date in format "<code>HH:mm:ss</code>"
	 * 
	 * @return String in format "<code>HH:mm:ss</code>"
	 */
	public static String toTimeString(Date date) {
		return timeFormatterRef.get().format(date);
	}
	
	/**
	 * Returns string value of Timestamp in format "<code>""MM/dd/yyyy HH:mm""</code>"
	 * 
	 * @return String in format "<code>"MM/dd/yyyy HH:mm"</code>"
	 */
	public String toMSAccessString() {
		return toString(msAccessDateFormatterRef);
	}
    
    @Override
    public Timestamp clone() {
        return new Timestamp(this);
    }
	
	public int getFiscalYear()
	{
		Calendar cal = new GregorianCalendar();
		cal.setTime(this.date);
		
		if (cal.get(Calendar.MONTH) > Calendar.SEPTEMBER) {
			return cal.get(Calendar.YEAR) + 1;
		} else {
			return cal.get(Calendar.YEAR);
		}
	}
	
	@Override
	public int compareTo(Timestamp o) {
		return this.date.compareTo(o.date);
	}
	
	public long getTime(){
		return date.getTime();
	}
	
	public void setTime(long time) {
        date.setTime(time);
    }
	
	public boolean after(Timestamp other){
		return this.date.after(other.date);
	}
	
	public boolean after(Date other){
		return this.date.after(other);
	}
	
	public boolean before(Timestamp other){
		return this.date.before(other.date);
	}
	
	public boolean before(Date other){
		return this.date.before(other);
	}
	
	@Deprecated
	public int getHours() {
		return date.getHours();
	}
	
	@Deprecated
	public int getMinutes(){
		return date.getHours();
	}
	
	@Deprecated
	public int getSeconds(){
		return date.getSeconds();
	}
	
	public boolean equals(Object obj) {
        return (obj instanceof Timestamp) &&
        		(this.date.equals(((Timestamp)obj).date));
    }
	
	public int hashCode() {
        return date.hashCode();
    }
	
	public static Timestamp fromTextRecord(String str)
	{
		return Timestamp.myValueOf(str);
	}

	private String toString(ThreadLocal<DateFormat> dateFormatRef) {
		return toString(dateFormatRef.get());
	}

	private static Timestamp myValueOf(ThreadLocal<DateFormat> dateFormatParser, String s) throws ParseException {
		return myValueOf(dateFormatParser.get(), s);
	}

	private static class ThreadLocalDateFormat extends ThreadLocal<DateFormat> {
		private String pattern;
		
		public ThreadLocalDateFormat(String pattern) {
			this.pattern = pattern;
		}

		@Override
        protected DateFormat initialValue() {
			return new SimpleDateFormat(pattern);
        }
	}
	
    // TODO: Translate some of these tests into junit tests
	public static void main(String[] args) throws ParseException {
		/*
		if (args.length < 1) {
			System.out.println("USAGE: java Timestamp _time_stamp_str or millis_since_epoch");
			System.exit(0);
		}
				
		String dateStr = args[0];
		
		if (args.length > 1) {
			StringBuilder sb = new StringBuilder(50);
			for (String arg: args) {
				sb.append(arg);
				sb.append(' ');
			}
			dateStr = sb.toString();
		}
		dateStr = dateStr.trim();
		*/
		//String dateStr = "20101104 15:00:18.569";
//		String [] dateStrs = new String [] {
//			"20101104 13:40:58.466",
//			"20101104 13:59:20.868",
//			"20101104 14:12:49.395",
//			"20101104 14:28:11.797"};

		String [] dateStrs = new String [] {
				"20101104 14:42:22.94",
				"20101104 14:55:50.621"
		};
		
		for (String dateStr : dateStrs) {
		
			boolean isTimestampStr = false;
			if (dateStr.indexOf(' ') >= 0) {
				isTimestampStr = true;
			} else if (dateStr.indexOf(':') >= 0) {
				isTimestampStr = true;
			} else if (dateStr.indexOf('.') >= 0) {
				isTimestampStr = true;
			}
			
			if (isTimestampStr) {
				Timestamp t = Timestamp.myValueOf(Timestamp.bonnSubsecondsRef, dateStr);
				System.out.println("from " + dateStr + " to " + t.getTime());
			} else {
				Timestamp t= new Timestamp(Long.parseLong(dateStr));
				System.out.println("from " + t.getTime() + " to " + t.toString());
			}
		}
	}
	
}
