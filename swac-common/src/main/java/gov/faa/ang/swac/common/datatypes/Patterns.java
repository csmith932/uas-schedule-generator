package gov.faa.ang.swac.common.datatypes;

public class Patterns
{
	public static final String DATE_YEAR    = "(?:19[0-9][0-9]|2[0-9][0-9][0-9])";     // "YYYY" range = 1900-2999 (inclusive), non-capturing
	public static final String DATE_MONTH   = "(?:0[1-9]|1[0-2])";                     // "MM"   range = 01-12     (inclusive), non-capturing
	public static final String DATE_DAY     = "(?:0[1-9]|[1-2][0-9]|3[01])";           // "DD"   range = 01-31     (inclusive), non-capturing
	public static final String DATE         = DATE_YEAR+DATE_MONTH+DATE_DAY;           // YYYYMMDD
	public static final String TIME_HOUR_12 = "(?:[0][0-9]|1[0-2])";                   // "hh" range = 00-12 (inclusive), non-capturing
	public static final String TIME_HOUR_24 = "(?:[01][0-9]|2[0-4])";                  // "hh" range = 00-24 (inclusive), non-capturing
	public static final String TIME_MIN     = "(?:[0-5][0-9])";                        // "mm" range = 00-59 (inclusive), non-capturing
	public static final String TIME_SEC     = TIME_MIN;                                // "ss" range = 00-59 (inclusive), non-capturing
	public static final String TIME_24HR    = TIME_HOUR_24+":"+TIME_MIN+":"+TIME_SEC;  // hh:mm:ss
	public static final String DATE_TIME    = DATE+" "+ TIME_24HR;                     // YYYYMMDD hh:mm:ss
	
	public static final String INTEGER = "-?\\d+";                     // Digits (preceded by optional negative sign)
	public static final String FLOAT   = "-?(?:\\d+\\.?\\d*|\\.\\d+)"; // Digits followed by possible decimal point (and possibly more digits) (and possibly preceded by a negative sign) "[-]d[.d]" OR "[-].d"
	public static final String CHAR    = "\\S";                        // Non-whitespace character
	public static final String WORD    = "\\S+";                       // Non-whitespace characters
	public static final String STRING  = ".*";	                       // Any sequence of characters
	public static final String BOOLEAN = "(?i)(?:true|false)";         // true/false ("(?i)" = cAsE iN-SeNsItIvE)
}