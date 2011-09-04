package net.galluzzo.timebuddy.android;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

public class TimeBuddyAppWidget extends AppWidgetProvider
{
	private static final String TAG = TimeBuddyAppWidget.class.getSimpleName();
	
	@Override
	public void onEnabled( Context context )
	{
		super.onEnabled( context );
		NotificationBroadcastReceiver.startNotificationServiceIfNecessary(
				context.getApplicationContext() );
	}

	@Override
	public void onUpdate( Context context, AppWidgetManager appWidgetManager,
		int[] appWidgetIds )
	{
		super.onUpdate( context, appWidgetManager, appWidgetIds );

		Settings settings = ServiceFactory.getSettings( context.getApplicationContext() );

		for ( int i = 0; i < appWidgetIds.length; i++ )
		{
			int appWidgetId = appWidgetIds[i];

			RemoteViews remoteViews = new RemoteViews( context.getPackageName(),
				R.layout.timebuddy_appwidget );
			
			updateDoNotDisturbStatus( context, settings, remoteViews );
			attachTimeBuddyMainActivityListener( context, remoteViews );
			attachNewTimeEntryListener( context, remoteViews );
			attachDoNotDisturbListener( context, remoteViews );

			appWidgetManager.updateAppWidget( appWidgetId, remoteViews );
		}
	}
	
	protected void updateDoNotDisturbStatus( Context inContext, Settings inSettings,
		RemoteViews inRemoteViews )
	{
		String text;
		int doNotDisturbImageResource;
		if ( inSettings.isPolling() )
		{
			Log.i( TAG, "Currently polling" );
			text = inContext.getString( R.string.appwidget_timebuddy_is_on );
			doNotDisturbImageResource = R.drawable.appwidget_disable;
		}
		else
		{
			Log.i( TAG, "Not currently polling" );
			text = inContext.getString( R.string.appwidget_timebuddy_is_off );
			doNotDisturbImageResource = R.drawable.appwidget_enable;
		}
		inRemoteViews.setTextViewText( R.id.appwidgetPollingStatusTextView,
			text );
		inRemoteViews.setImageViewResource( R.id.appwidgetDoNotDisturbButton,
			doNotDisturbImageResource );
	}
	
	protected void attachTimeBuddyMainActivityListener( Context inContext,
		RemoteViews inRemoteViews )
	{
		Intent intent = new Intent( inContext, TimeBuddyMainActivity.class );
		PendingIntent pendingIntent = PendingIntent.getActivity( inContext, 0,
			intent, 0 );
		inRemoteViews.setOnClickPendingIntent( R.id.appwidgetTimeBuddyButton,
			pendingIntent );
	}
	
	protected void attachNewTimeEntryListener( Context inContext,
		RemoteViews inRemoteViews )
	{
		Intent intent = new Intent( inContext, NewTimeEntryActivity.class );
		PendingIntent pendingIntent = PendingIntent.getActivity( inContext, 0,
			intent, Intent.FLAG_ACTIVITY_NEW_TASK );
		inRemoteViews.setOnClickPendingIntent( R.id.appwidgetAddNewButton,
			pendingIntent );
	}
	
	protected void attachDoNotDisturbListener( Context inContext,
		RemoteViews inRemoteViews )
	{
		Intent intent = new Intent( inContext,
			DoNotDisturbBroadcastReceiver.class );
		PendingIntent pendingIntent = PendingIntent.getBroadcast( inContext, 0,
			intent, 0 );
		inRemoteViews.setOnClickPendingIntent(R.id.appwidgetDoNotDisturbButton,
			pendingIntent);
	}
}
