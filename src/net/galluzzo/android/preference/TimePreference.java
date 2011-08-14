/**
 * Created on Aug 7, 2011
 */
package net.galluzzo.android.preference;

import java.text.ParseException;

import net.galluzzo.datetime.Time;
import net.galluzzo.timebuddy.android.R;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.preference.Preference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TimePicker;

/**
 * A custom {@link Preference} that allows the user to select a time value from
 * the dropdown.
 * 
 * @author Eric Galluzzo, eric@galluzzo.net
 */
public class TimePreference extends DialogPreference
{
	private static final String TAG = TimePreference.class.getSimpleName();

	protected Time currentTime;
	protected TimePicker timePicker;

	public TimePreference( Context context, AttributeSet attrs )
	{
		super( context, attrs );
		setDialogLayoutResource( R.layout.time_picker );
	}

	public TimePreference( Context context, AttributeSet attrs, int defStyle )
	{
		super( context, attrs, defStyle );
		setDialogLayoutResource( R.layout.time_picker );
	}

	@Override
	protected void onDialogClosed( boolean inPositiveResult )
	{
		super.onDialogClosed( inPositiveResult );
		if ( inPositiveResult )
		{
			currentTime = new Time( timePicker.getCurrentHour(),
				timePicker.getCurrentMinute() );
			savePreference();
		}
	}

	protected void savePreference()
	{
		SharedPreferences.Editor editor = getEditor();
		editor.putString( getKey(), currentTime.toString() );
		editor.commit();
	}

	@Override
	protected void onBindDialogView( View inView )
	{
		super.onBindDialogView( inView );
		timePicker = (TimePicker) inView;
		timePicker.setCurrentHour( currentTime.getHour() );
		timePicker.setCurrentMinute( currentTime.getMinute() );
	}

	@Override
	protected Object onGetDefaultValue( TypedArray inArray, int inIndex )
	{
		String defaultValue = inArray.getString( inIndex );
		try
		{
			Time time = Time.parseTime( defaultValue );
			return time.toString();
		}
		catch ( ParseException pe )
		{
			Log.w( TAG, "Could not parse default time value: " + defaultValue );
			return Time.MIDNIGHT.toString();
		}
	}

	@Override
	protected void onSetInitialValue( boolean inRestorePersistedValue,
		Object inDefaultValue )
	{
		super.onSetInitialValue( inRestorePersistedValue, inDefaultValue );

		String timeStr;
		if ( inRestorePersistedValue )
		{
			timeStr = getPersistedString( Time.MIDNIGHT.toString() );
		}
		else
		{
			timeStr = (String) inDefaultValue;
		}

		try
		{
			currentTime = Time.parseTime( timeStr );
		}
		catch ( ParseException pe )
		{
			Log.w( TAG, "Could not parse initial time value: " + timeStr );
			currentTime = Time.MIDNIGHT;
		}
	}
}
