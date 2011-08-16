/**
 * Created on Aug 13, 2011
 */
package net.galluzzo.timebuddy.android;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import net.galluzzo.android.widget.BlockView;
import net.galluzzo.android.widget.BlocksLayout;
import net.galluzzo.timebuddy.model.TimeEntry;
import net.galluzzo.timebuddy.model.TimeEntryTimestampComparator;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * An activity that shows submitted time entries in a calendar view.
 * 
 * @author Eric Galluzzo, eric@galluzzo.net
 */
public class TimesheetActivity extends BaseActivity
{
	protected static final long MILLIS_PER_HOUR = 60L * 60L * 1000L;
	protected static final long MILLIS_PER_DAY = 24L * MILLIS_PER_HOUR;
	protected static final DateFormat START_OF_WEEK_DATE_FORMAT = DateFormat.getDateInstance( DateFormat.LONG );
	protected static final String SAVED_START_OF_WEEK = "startOfWeek";

	private static final String TAG = TimesheetActivity.class.getSimpleName();

	private Calendar startOfTimesheetView;
	private LoadTimeEntriesTask loadTask;

	private ScrollView scrollView;
	private BlocksLayout blocksLayout;
	private TextView weekTextView;
	private TextView emptyTextView;
	private View progressBarView;

	private class LoadTimeEntriesTask extends
		AsyncTask<Date, Void, List<TimeEntry>>
	{
		protected List<TimeEntry> doInBackground( Date... inParams )
		{
			Calendar cal = Calendar.getInstance();
			cal.setTime( inParams[0] );
			cal.add( Calendar.WEEK_OF_YEAR, 1 );
			Date endDate = cal.getTime();
			List<TimeEntry> timeEntries = null;
			try
			{
				timeEntries = getTimeBuddyService().findEntriesBetweenDates(
					inParams[0], endDate );
			}
			catch ( Exception e )
			{
				Log.e( TAG, "Error retrieving time entries", e );
				// FIXME: Handle exception
			}
			return timeEntries;
		}

		protected void onPostExecute( List<TimeEntry> result )
		{
			if ( result == null ) // we got an exception
			{
				makeEmptyTextViewVisible();
				// FIXME: Use a resource string.
				emptyTextView.setText( "Error retrieving time entries" );
			}
			else if ( result.isEmpty() ) // no time entries in the given range
			{
				makeEmptyTextViewVisible();
				// FIXME: Use a resource string.
				emptyTextView.setText( "No time entries" );
			}
			else
			{
				makeTimesheetVisible();
				createBlockViews( result );
			}
			loadTask = null;
		}
	}

	@Override
	protected void onCreate( Bundle inSavedInstanceState )
	{
		super.onCreate( inSavedInstanceState );
		setContentView( R.layout.timesheet );

		scrollView = (ScrollView) findViewById( R.id.blocks_scroll );
		blocksLayout = (BlocksLayout) findViewById( R.id.blocks );
		weekTextView = (TextView) findViewById( R.id.weekTextView );
		emptyTextView = (TextView) findViewById( android.R.id.empty );
		progressBarView = findViewById( android.R.id.progress );

		Button selectViewButton = (Button) findViewById( R.id.selectWeekButton );
		selectViewButton.setOnClickListener( new OnClickListener()
		{
			public void onClick( View inView )
			{
				int year = startOfTimesheetView.get( Calendar.YEAR );
				int month = startOfTimesheetView.get( Calendar.MONTH );
				int day = startOfTimesheetView.get( Calendar.DAY_OF_MONTH );
				new DatePickerDialog( TimesheetActivity.this,
					new OnDateSetListener()
					{
						public void onDateSet( DatePicker inView, int inYear,
							int inMonthOfYear, int inDayOfMonth )
						{
							startOfTimesheetView.set( Calendar.YEAR, inYear );
							startOfTimesheetView.set( Calendar.MONTH,
								inMonthOfYear );
							startOfTimesheetView.set( Calendar.DAY_OF_MONTH,
								inDayOfMonth );
							refreshTimesheet();
						}
					}, year, month, day ).show();
			}
		} );
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		if ( startOfTimesheetView == null )
		{
			startOfTimesheetView = getStartOfCurrentWeek();
		}
		refreshTimesheet();
	}

	/**
	 * Returns a date at midnight at the start of the current week.
	 * 
	 * @return  The start of the current week
	 */
	protected Calendar getStartOfCurrentWeek()
	{
		Calendar cal = Calendar.getInstance();
		int year = cal.get( Calendar.YEAR );
		int weekOfYear = cal.get( Calendar.WEEK_OF_YEAR );
		cal.clear();
		cal.set( Calendar.YEAR, year );
		cal.set( Calendar.WEEK_OF_YEAR, weekOfYear );
		//cal.set( 2011, Calendar.JANUARY, 1, 0, 0, 0 );
		return cal;
	}

	/**
	 * Requests the time entries for the currently shown week from the service.
	 */
	protected void refreshTimesheet()
	{
		scrollView.setVisibility( View.GONE );
		emptyTextView.setVisibility( View.GONE );
		progressBarView.setVisibility( View.VISIBLE );

		weekTextView.setText( START_OF_WEEK_DATE_FORMAT.format( startOfTimesheetView.getTime() ) );

		if ( loadTask != null )
		{
			loadTask.cancel( true );
		}
		loadTask = new LoadTimeEntriesTask();
		loadTask.execute( startOfTimesheetView.getTime() );
	}

	public void createBlockViews( List<TimeEntry> timeEntries )
	{
		List<TimeEntry> sortedTimeEntries = new ArrayList<TimeEntry>(
			timeEntries );
		Collections.sort( sortedTimeEntries, new TimeEntryTimestampComparator() );
		
		int previousDay = -1;
		BlockView previousBlock = null;
		blocksLayout.removeAllBlocks();
		for ( TimeEntry timeEntry : sortedTimeEntries )
		{
			long startTime = timeEntry.getTimestamp()
				.getTime();
			int day = (int) ( ( startTime - startOfTimesheetView.getTimeInMillis() ) / MILLIS_PER_DAY );
			BlockView blockView = new BlockView( this, "",
				timeEntry.getMessage() + "\n"
					+ timeEntry.getCommaSeparatedTagLabels(), startTime,
					startTime + MILLIS_PER_HOUR, false, day,
				timeEntry.getColor() );
			blockView.setTextSize( TypedValue.COMPLEX_UNIT_DIP, 8 );
			blockView.setPadding( 5, 5, 5, 5 );
			
			if ( previousBlock != null )
			{
				// If the new block is on the same day as the old one, end the
				// previous block right above the new one.
				if ( previousDay == day )
				{
					Log.w("TimesheetActivity", "Setting end time to " + startTime );
					previousBlock.setEndTime( startTime - 1L );
				}
				blocksLayout.addBlock( previousBlock );
			}
			previousBlock = blockView;
			previousDay = day;
		}
		
		if ( previousBlock != null )
		{
			blocksLayout.addBlock( previousBlock );
		}
	}

	@Override
	protected void onSaveInstanceState( Bundle inOutState )
	{
		super.onSaveInstanceState( inOutState );
		inOutState.putLong( SAVED_START_OF_WEEK,
			startOfTimesheetView.getTimeInMillis() );
	}

	@Override
	protected void onRestoreInstanceState( Bundle inSavedInstanceState )
	{
		super.onRestoreInstanceState( inSavedInstanceState );
		startOfTimesheetView = Calendar.getInstance();
		startOfTimesheetView.setTimeInMillis( inSavedInstanceState.getLong( SAVED_START_OF_WEEK ) );
	}

	protected void makeTimesheetVisible()
	{
		scrollView.setVisibility( View.VISIBLE );
		progressBarView.setVisibility( View.GONE );
		emptyTextView.setVisibility( View.GONE );
	}

	protected void makeEmptyTextViewVisible()
	{
		scrollView.setVisibility( View.GONE );
		progressBarView.setVisibility( View.GONE );
		emptyTextView.setVisibility( View.VISIBLE );
	}
}
