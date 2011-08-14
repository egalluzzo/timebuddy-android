/**
 * Created on Aug 7, 2011
 */
package net.galluzzo.datetime;

import java.text.ParseException;

/**
 * A class that represents a certain time of day, such as 5:30.
 * 
 * @author Eric Galluzzo, eric@galluzzo.net
 */
public class Time implements Comparable<Time>
{
	/**
	 * The {@link Time} representing midnight (hour and minute are both zero).
	 */
	public static final Time MIDNIGHT = new Time( 0, 0 );
	
	private int hour;
	private int minute;
	
	/**
	 * Creates a new {@link Time}.
	 * 
	 * @param inHour    An integer between 0 and 23
	 * @param inMinute  A minute between 0 and 59
	 * 
	 * @throws IllegalArgumentException
	 *     If the hour or minute is out of the valid range
	 */
	public Time( int inHour, int inMinute )
	{
		if ( inHour < 0 || inHour > 23 )
		{
			throw new IllegalArgumentException( "Hour must be between 0 and 23" );
		}
		
		if ( inMinute < 0 || inMinute > 59 )
		{
			throw new IllegalArgumentException( "Minute must be between 0 and 59" );
		}
		
		hour = inHour;
		minute = inMinute;
	}
	
	/**
	 * Returns the hour, between 0 and 23.
	 * 
	 * @return  The hour
	 */
	public int getHour()
	{
		return hour;
	}
	
	/**
	 * Returns the minute, between 0 and 59.
	 * 
	 * @return  The minute
	 */
	public int getMinute()
	{
		return minute;
	}
	
	@Override
	public boolean equals( Object inOther )
	{
		if ( !( inOther instanceof Time ) )
		{
			return false;
		}
		
		Time other = (Time) inOther;
		return ( hour == other.hour && minute == other.minute );
	}
	
	@Override
	public int hashCode()
	{
		return hour * 60 + minute;
	}
	
	/**
	 * Returns the time as a string of the form "hh:mm", where "hh" may be one
	 * or two digits, and "mm" will always be two digits.  For example:
	 * <ul>
	 *   <li>0:00
	 *   <li>8:30
	 *   <li>17:05
	 *   <li>23:59
	 * </ul>
	 * 
	 * @return  The string representation of this {@link Time}
	 */
	@Override
	public String toString()
	{
		String minuteStr;
		if ( minute < 10 )
		{
			minuteStr = "0" + minute;
		}
		else
		{
			minuteStr = String.valueOf( minute );
		}
		
		return hour + ":" + minuteStr;
	}
	
	@Override
	public int compareTo( Time inAnother )
	{
		int comparison = hour - inAnother.hour;
		if ( comparison == 0 )
		{
			comparison = minute - inAnother.minute;
		}
		return comparison;
	}

	/**
	 * Parses the given string as a {@link Time} of the form "hh:mm", where
	 * h represents an hour between 0 and 23 and m represents a minute between
	 * 0 and 59.
	 * 
	 * @param inTime  A time string of the form "hh:mm"
	 * 
	 * @return  The {@link Time} representing the given string
	 * 
	 * @throws ParseException  If the time string is not in the correct format
	 */
	public static Time parseTime( String inTime ) throws ParseException
	{
		int colonPos = inTime.indexOf( ':' );
		if ( colonPos >= 0 )
		{
			try
			{
				String hourStr = inTime.substring( 0, colonPos );
				int hour = Integer.parseInt( hourStr );
				String minuteStr = inTime.substring( colonPos + 1 );
				int minute = Integer.parseInt( minuteStr );
				if ( hour >= 0 && hour <= 23 && minute >= 0 && minute <= 59 )
				{
					return new Time( hour, minute );
				}
			}
			catch ( NumberFormatException nfe )
			{
				// Not valid, so fall through to the exception below.
			}
		}
		
		// Don't really know what the position of the failure is, so we'll
		// use 0 for now.
		throw new ParseException( "Invalid time string: " + inTime, 0 );
	}
}