/**
 * Created on Aug 6, 2011
 */
package net.galluzzo.timebuddy.android;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

/**
 * An activity to submit a new time entry to the Time Buddy server.
 * 
 * @author Eric Galluzzo, eric@galluzzo.net
 */
public class ViewTimeEntryActivity extends BaseActivity
{
	public static final String EXTRA_DESCRIPTION = "description";
	public static final String EXTRA_START_TIME = "startTime";
	public static final String EXTRA_TAG_IDS = "tagIds";
	
	protected static final String SAVED_TAG_IDS = "tagIds";

	protected List<String> tagIds;
	protected TextView descriptionTextView;
	protected TextView startTimeTextView;

	@Override
	public void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.view_time_entry );
		
		descriptionTextView = (TextView) findViewById( R.id.timeEntryDescriptionText );
		startTimeTextView = (TextView) findViewById( R.id.timeEntryStartTimeText );

		Button tagsButton = (Button) findViewById( R.id.editTimeEntryTagsButton );
		tagsButton.setOnClickListener( new OnClickListener()
		{
			public void onClick( View inView )
			{
				Intent intent = new Intent( ViewTimeEntryActivity.this,
					ViewTagsActivity.class );
				intent.putExtra( ViewTagsActivity.EXTRA_TAG_IDS,
					tagIds.toArray( new String[tagIds.size()] ) );
				startActivity( intent );
			}
		} );

		Button cancelButton = (Button) findViewById( R.id.cancelTimeEntryButton );
		cancelButton.setOnClickListener( new OnClickListener()
		{
			public void onClick( View inView )
			{
				setResult( RESULT_CANCELED );
				finish();
			}
		} );

		String description = getIntent().getStringExtra( EXTRA_DESCRIPTION );
		long startTimeMillis = getIntent().getLongExtra( EXTRA_START_TIME, 0 );
		Date startTime = new Date( startTimeMillis );
		tagIds = Arrays.asList( getIntent().getStringArrayExtra( EXTRA_TAG_IDS ) );
		
		DateFormat dateFormat = DateFormat.getDateTimeInstance( DateFormat.LONG, DateFormat.MEDIUM );
		descriptionTextView.setText( description );
		startTimeTextView.setText( dateFormat.format( startTime ) );
	}
	
	@Override
	protected void onSaveInstanceState( Bundle inOutState )
	{
		super.onSaveInstanceState( inOutState );
		inOutState.putStringArrayList( SAVED_TAG_IDS, new ArrayList<String>( tagIds ) );
	}
	
	@Override
	protected void onRestoreInstanceState( Bundle inSavedInstanceState )
	{
		super.onRestoreInstanceState( inSavedInstanceState );
		tagIds = inSavedInstanceState.getStringArrayList( SAVED_TAG_IDS );
	}
}
