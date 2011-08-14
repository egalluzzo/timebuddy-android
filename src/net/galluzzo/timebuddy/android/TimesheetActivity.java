/**
 * Created on Aug 13, 2011
 */
package net.galluzzo.timebuddy.android;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import net.galluzzo.android.widget.BlockView;
import net.galluzzo.android.widget.BlocksLayout;
import net.galluzzo.timebuddy.model.TimeEntry;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * An activity that shows submitted time entries in a calendar view.
 * 
 * @author Eric Galluzzo, eric@galluzzo.net
 */
public class TimesheetActivity extends BaseActivity
{
	private static final String TAG = TimesheetActivity.class.getSimpleName();
	
	private static final DateFormat START_OF_WEEK_DATE_FORMAT =
		DateFormat.getDateInstance( DateFormat.LONG );

	private Date startOfTimesheetView;
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
				scrollView.setVisibility( View.GONE );
				progressBarView.setVisibility( View.GONE );
				emptyTextView.setVisibility( View.VISIBLE );
				// FIXME: Use a resource string.
				emptyTextView.setText( "Error retrieving time entries" );
			}
			else if ( result.isEmpty() ) // no time entries in the given range
			{
				scrollView.setVisibility( View.GONE );
				progressBarView.setVisibility( View.GONE );
				emptyTextView.setVisibility( View.VISIBLE );
				// FIXME: Use a resource string.
				emptyTextView.setText( "No time entries" );
			}
			else
			{
				scrollView.setVisibility( View.VISIBLE );
				progressBarView.setVisibility( View.GONE );
				emptyTextView.setVisibility( View.GONE );
				createBlockViews( result );
			}
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

		scrollView.setVisibility( View.GONE );
		emptyTextView.setVisibility( View.GONE );
		progressBarView.setVisibility( View.VISIBLE );

		Calendar cal = Calendar.getInstance();
		cal.set( 2011, Calendar.JANUARY, 1, 0, 0, 0 );
		startOfTimesheetView = cal.getTime();
		weekTextView.setText( START_OF_WEEK_DATE_FORMAT.format( startOfTimesheetView ) );
		new LoadTimeEntriesTask().execute( startOfTimesheetView );
	}

	public void createBlockViews( List<TimeEntry> timeEntries )
	{
		blocksLayout.removeAllBlocks();
		for ( TimeEntry timeEntry : timeEntries )
		{
			long timeInMillis = timeEntry.getTimestamp()
				.getTime();
			int day = (int) ( ( timeInMillis - startOfTimesheetView.getTime() ) / ( 24L * 60L * 60L * 1000L ) );
			BlockView blockView = new BlockView( this, "",
				timeEntry.getMessage() + " "
					+ timeEntry.getCommaSeparatedTagLabels(), timeInMillis,
				timeInMillis + 3600000L, false, day, timeEntry.getColor() );
			blockView.setTextSize( TypedValue.COMPLEX_UNIT_DIP, 8 );
			blockView.setPadding( 5, 5, 5, 5 );
			blocksLayout.addBlock( blockView );
		}
	}
}
