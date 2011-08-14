/**
 * Created on Aug 6, 2011
 */
package net.galluzzo.timebuddy.android;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.galluzzo.timebuddy.model.Tag;
import net.galluzzo.timebuddy.model.TimeEntry;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * An activity to submit a new time entry to the Time Buddy server.
 * 
 * @author Eric Galluzzo, eric@galluzzo.net
 */
public class NewTimeEntryActivity extends BaseActivity
{
	protected static final int SELECT_TAGS_REQUEST_CODE = 1;
	protected static final String SAVED_TAG_IDS = "tagIds";

	protected List<String> tagIds;
	protected EditText descriptionTextField;

	/**
	 * An asynchronous task that submits a new time entry to the Time Buddy
	 * server.
	 * 
	 * @author Eric Galluzzo, eric@galluzzo.net
	 */
	private final class SubmitTimeEntryTask extends
		AsyncTask<TimeEntry, Void, Exception>
	{
		@Override
		protected void onPostExecute( Exception inResult )
		{
			if ( inResult == null )
			{
				Toast.makeText( getApplicationContext(),
					R.string.time_entry_submitted, Toast.LENGTH_SHORT )
					.show();
			}
			else
			{
				Toast.makeText(
					getApplicationContext(),
					"Could not submit time entry: "
						+ inResult.getLocalizedMessage(), Toast.LENGTH_SHORT )
					.show();
			}
		}

		@Override
		protected Exception doInBackground( TimeEntry... inParams )
		{
			try
			{
				getTimeBuddyService().submit( inParams[0] );
				return null;
			}
			catch ( Exception ex )
			{
				return ex;
			}
		}
	}

	@Override
	public void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.new_time_entry );

		tagIds = new ArrayList<String>();
		descriptionTextField = (EditText) findViewById( R.id.timeEntryDescriptionText );

		Button tagsButton = (Button) findViewById( R.id.editTimeEntryTagsButton );
		tagsButton.setOnClickListener( new OnClickListener()
		{
			public void onClick( View inView )
			{
				Intent intent = new Intent( NewTimeEntryActivity.this,
					SelectTagsActivity.class );
				intent.putExtra( SelectTagsActivity.EXTRA_TAG_IDS,
					tagIds.toArray( new String[tagIds.size()] ) );
				startActivityForResult( intent, SELECT_TAGS_REQUEST_CODE );
			}
		} );

		Button submitButton = (Button) findViewById( R.id.submitTimeEntryButton );
		submitButton.setOnClickListener( new OnClickListener()
		{
			public void onClick( View inView )
			{
				submitTimeEntry();
				setResult( RESULT_OK );
				finish();
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

	@Override
	protected void onActivityResult( int inRequestCode, int inResultCode,
		Intent inData )
	{
		super.onActivityResult( inRequestCode, inResultCode, inData );
		if ( inRequestCode == SELECT_TAGS_REQUEST_CODE )
		{
			if ( inResultCode == RESULT_OK )
			{
				tagIds = Arrays.asList( inData.getStringArrayExtra( SelectTagsActivity.EXTRA_TAG_IDS ) );
			}
		}
	}

	protected void submitTimeEntry()
	{
		TimeEntry timeEntry = new TimeEntry();
		timeEntry.setMessage( descriptionTextField.getText()
			.toString() );
		for ( String tagId : tagIds )
		{
			timeEntry.addTag( Tag.getTag( tagId ) );
		}
		new SubmitTimeEntryTask().execute( timeEntry );
	}
}
