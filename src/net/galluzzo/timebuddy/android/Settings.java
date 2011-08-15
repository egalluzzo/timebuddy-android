/**
 * Created on Aug 7, 2011
 */
package net.galluzzo.timebuddy.android;

import java.text.ParseException;
import java.util.Calendar;

import net.galluzzo.datetime.Time;
import net.galluzzo.timebuddy.service.RemoteUrlProvider;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * A class that provides access to the local Time Buddy settings.
 * 
 * <p>FIXME: These defaults and keys are duplicated from preferences.xml.  How
 * do I use those values instead?  Should I just use values in strings.xml for
 * the keys and default values?
 * 
 * @author Eric Galluzzo, eric@galluzzo.net
 */
public class Settings implements RemoteUrlProvider
{
	private static final String TAG = Settings.class.getSimpleName();

	private SharedPreferences sharedPreferences;

	public Settings( SharedPreferences inSharedPreferences )
	{
		sharedPreferences = inSharedPreferences;
	}

	/**
	 * Returns the email address with which the user signs into the Time Buddy
	 * App Engine server.
	 * 
	 * @return  The Time Buddy email address
	 */
	public String getTimeBuddyEmail()
	{
		return sharedPreferences.getString( "timeBuddyEmail", null );
	}

	/**
	 * Returns the API key (generated token) in the Time Buddy App Engine app.
	 * 
	 * @return  The API key
	 */
	public String getApiKey()
	{
		return sharedPreferences.getString( "apiKey", null );
	}

	/**
	 * Determines whether the Time Buddy email and the API key are not null or
	 * blank, so that we have a chance of connecting to the server.
	 * 
	 * @return  <code>true</code> if connectivity information is set
	 */
	public boolean isConnectivityInformationSet()
	{
		return getTimeBuddyEmail() != null && getApiKey() != null
			&& !"".equals( getTimeBuddyEmail().trim() )
			&& !"".equals( getApiKey().trim() );
	}

	public String getTimeBuddyServerUrl()
	{
		return "http://time-buddy.appspot.com/api/" + getTimeBuddyEmail() + ":"
			+ getApiKey() + "/entries/";
	}

	/**
	 * Returns whether this application should poll the user periodically for
	 * new time entries.
	 * 
	 * @return  <code>true</code> if polling is enabled, <code>false</code>
	 *          otherwise
	 */
	public boolean isPolling()
	{
		return sharedPreferences.getBoolean( "poll", true );
	}

	/**
	 * Returns the day of the week at which polling should start -- 0 for Sunday
	 * up to 6 for Saturday.
	 * 
	 * @return  An integer between 0 and 6
	 */
	public int getWorkWeekStart()
	{
		return Integer.parseInt( sharedPreferences.getString( "workWeekStart",
			"1" ) );
	}

	/**
	 * Returns the day of the week at which polling should stop -- 0 for Sunday
	 * up to 6 for Saturday.
	 * 
	 * @return  An integer between 0 and 6
	 */
	public int getWorkWeekEnd()
	{
		return Integer.parseInt( sharedPreferences.getString( "workWeekEnd",
			"5" ) );
	}

	/**
	 * Returns the time at which polling should start on each workday.
	 * 
	 * @return  The time at which polling should start
	 */
	public Time getWorkdayStart()
	{
		try
		{
			return Time.parseTime( sharedPreferences.getString( "workdayStart",
				"8:00" ) );
		}
		catch ( ParseException e )
		{
			Log.w( TAG, "Could not parse workdayStart preference: ", e );
			return Time.MIDNIGHT;
		}
	}

	/**
	 * Returns the time at which polling should stop on each workday.
	 * 
	 * @return  The time at which polling should stop
	 */
	public Time getWorkdayEnd()
	{
		try
		{
			return Time.parseTime( sharedPreferences.getString( "workdayEnd",
				"17:00" ) );
		}
		catch ( ParseException e )
		{
			Log.w( TAG, "Could not parse workdayStart preference: ", e );
			return Time.MIDNIGHT;
		}
	}

	/**
	 * Returns the interval at which the user should be polled for new time
	 * entries, between the times of {@link #getWorkdayStart()} and
	 * {@link #getWorkdayEnd()} during the work week (from
	 * {@link #getWorkWeekStart()} to {@link #getWorkWeekEnd()}.
	 * 
	 * @return  The polling interval, in minutes
	 */
	public int getPollingIntervalInMinutes()
	{
		return Integer.parseInt( sharedPreferences.getString(
			"pollingInterval", "60" ) );
	}

	/**
	 * Finds the next time at which the user should be polled, assuming they
	 * were just polled.
	 * 
	 * @return  The next time at which the user should be polled, as a number
	 *          of milliseconds (c.f. System.currentTimeMillis()).
	 */
	public long findNextPollTime()
	{
		return System.currentTimeMillis()
			+ ( ( (long) getPollingIntervalInMinutes() ) * 60L * 1000L );
	}

	/**
	 * Determines whether polling is enabled, the current day is within the work
	 * week, and the current time is within the workday.
	 * 
	 * @return  <code>true</code> if so, <code>false</code> if not
	 */
	public boolean shouldNotifyNow()
	{
		return isPolling() && isCurrentDayInWorkWeek() && isCurrentTimeInWorkDay();
	}
	
	protected boolean isCurrentDayInWorkWeek()
	{
		Calendar cal = Calendar.getInstance();
		int dayOfWeek = cal.get( Calendar.DAY_OF_WEEK ) - Calendar.SUNDAY;
		if ( getWorkWeekStart() <= getWorkWeekEnd() )
		{
			return ( dayOfWeek >= getWorkWeekStart() ) && ( dayOfWeek <= getWorkWeekEnd() );
		}
		else
		{
			return ( dayOfWeek >= getWorkWeekStart() ) || ( dayOfWeek <= getWorkWeekEnd() );
		}
	}
	
	protected boolean isCurrentTimeInWorkDay()
	{
		return Time.now().isInRange( getWorkdayStart(), getWorkdayEnd() );
	}
}
