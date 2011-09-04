package net.galluzzo.timebuddy.android;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class DoNotDisturbBroadcastReceiver extends BroadcastReceiver
{
	private static final String TAG = DoNotDisturbBroadcastReceiver.class.getSimpleName();

	@Override
	public void onReceive( Context inContext, Intent inIntent )
	{
		Context appContext = inContext.getApplicationContext();
		Settings settings = ServiceFactory.getSettings( appContext );
		settings.togglePolling();

		updateAllWidgets( inContext, TimeBuddyAppWidget.class );
	}

	protected void updateAllWidgets( Context inContext,
		Class<? extends AppWidgetProvider> inAppWidgetClass )
	{
		Context appContext = inContext.getApplicationContext();
		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance( appContext );
		int[] appWidgetIds = appWidgetManager.getAppWidgetIds( new ComponentName(
			appContext, inAppWidgetClass ) );
		if ( appWidgetIds.length > 0 )
		{
			try
			{
				inAppWidgetClass.newInstance()
					.onUpdate( appContext, appWidgetManager, appWidgetIds );
			}
			catch ( IllegalAccessException e )
			{
				Log.e( TAG, "Could not update " + inAppWidgetClass.getName()
					+ ": " + e, e );
				throw new RuntimeException( e );
			}
			catch ( InstantiationException e )
			{
				Log.e( TAG, "Could not update " + inAppWidgetClass.getName()
					+ ": " + e, e );
				throw new RuntimeException( e );
			}
		}
	}
}
