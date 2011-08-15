package net.galluzzo.timebuddy.android;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * The main activity that shows the four initial buttons.
 * 
 * @author Eric Galluzzo, eric@galluzzo.net
 */
public class TimeBuddyMainActivity extends BaseActivity
{
	@Override
	public void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.main );
		
		Button newTimeEntryButton = (Button) findViewById( R.id.newTimeEntryButton );
		newTimeEntryButton.setOnClickListener( new OnClickListener()
		{
			public void onClick( View inView )
			{
				Intent intent = new Intent( TimeBuddyMainActivity.this, NewTimeEntryActivity.class );
				startActivity( intent );
			}
		} );
		
		Button timesheetButton = (Button) findViewById( R.id.viewTimesheetButton );
		timesheetButton.setOnClickListener( new OnClickListener()
		{
			public void onClick( View inView )
			{
				Intent intent = new Intent( TimeBuddyMainActivity.this, TimesheetActivity.class );
				startActivity( intent );
			}
		} );
		
		Button tagsButton = (Button) findViewById( R.id.tagsButton );
		tagsButton.setOnClickListener( new OnClickListener()
		{
			public void onClick( View inView )
			{
				Intent intent = new Intent( TimeBuddyMainActivity.this, EditTagsActivity.class );
				startActivity( intent );
			}
		} );
		
		Button settingsButton = (Button) findViewById( R.id.settingsButton );
		settingsButton.setOnClickListener( new OnClickListener()
		{
			public void onClick( View inView )
			{
				Intent intent = new Intent( TimeBuddyMainActivity.this, EditSettingsActivity.class );
				startActivity( intent );
			}
		} );
		
		NotificationBroadcastReceiver.startNotificationServiceIfNecessary( getApplicationContext() );
	}
}