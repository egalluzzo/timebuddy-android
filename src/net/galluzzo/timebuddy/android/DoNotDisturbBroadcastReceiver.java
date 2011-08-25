package net.galluzzo.timebuddy.android;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

public class DoNotDisturbBroadcastReceiver extends BroadcastReceiver
{
	@Override
	public void onReceive( Context inContext, Intent inIntent )
	{
		Context appContext = inContext.getApplicationContext();
		Settings settings = ServiceFactory.getSettings( appContext );
		settings.togglePolling();
		
		RemoteViews remoteViews = new RemoteViews( appContext.getPackageName(),
			R.layout.timebuddy_appwidget );
		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance( appContext );
		ComponentName appWidgetComponentName = new ComponentName( appContext,
			TimeBuddyAppWidget.class );
		appWidgetManager.updateAppWidget( appWidgetComponentName, remoteViews );
	}
}
