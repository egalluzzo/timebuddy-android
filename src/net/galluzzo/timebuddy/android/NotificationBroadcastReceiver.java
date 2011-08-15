/**
 * Created on Aug 14, 2011
 */
package net.galluzzo.timebuddy.android;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * A notification broadcast receiver that prompts the user to enter their time.
 * 
 * @author Eric Galluzzo, eric@galluzzo.net
 */
public class NotificationBroadcastReceiver extends BroadcastReceiver
{
	public static final int NEW_TIME_ENTRY_NOTIFICATION_ID = 1;

	private static boolean notificationServiceStarted;

	public static void startNotificationServiceIfNecessary( Context inContext )
	{
		if ( !notificationServiceStarted )
		{
			createNextBroadcastAlarm( inContext );
			notificationServiceStarted = true;
		}
	}

	public static void createNextBroadcastAlarm( Context inContext )
	{
		Settings settings = ServiceFactory.getSettings( inContext );
		AlarmManager alarmManager = (AlarmManager) inContext.getSystemService( Context.ALARM_SERVICE );
		long triggerTime = settings.findNextPollTime();
		PendingIntent pendingIntent = createPendingNotificationBroadcastIntent( inContext );
		alarmManager.set( AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent );
	}

	/**
	 * @param inContext
	 * @return
	 */
	protected static PendingIntent createPendingNotificationBroadcastIntent(
		Context inContext )
	{
		Intent broadcastIntent = new Intent( inContext,
			NotificationBroadcastReceiver.class );
		PendingIntent pendingIntent = PendingIntent.getBroadcast( inContext, 0,
			broadcastIntent, 0 );
		return pendingIntent;
	}

	@Override
	public void onReceive( Context inContext, Intent inIntent )
	{
		Settings settings = ServiceFactory.getSettings( inContext );
		if ( settings.shouldNotifyNow() )
		{
			NotificationManager notificationManager = (NotificationManager) inContext.getSystemService( Context.NOTIFICATION_SERVICE );
			Notification notification = createNotification( inContext );
			notificationManager.notify( NEW_TIME_ENTRY_NOTIFICATION_ID,
				notification );
		}
		createNextBroadcastAlarm( inContext );
	}

	/**
	 * @param inContext
	 * @return
	 */
	protected Notification createNotification( Context inContext )
	{
		Notification notification = new Notification(
			R.drawable.notification_icon, "What are you doing right now?",
			System.currentTimeMillis() );
		notification.defaults |= Notification.DEFAULT_SOUND;
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		notification.setLatestEventInfo( inContext, "Time Buddy",
			"What are you doing right now?",
			createPendingIntentForNewTimeEntry( inContext ) );
		return notification;
	}

	protected PendingIntent createPendingIntentForNewTimeEntry(
		Context inContext )
	{
		Intent intent = new Intent( inContext, NewTimeEntryActivity.class );
		PendingIntent pendingIntent = PendingIntent.getActivity( inContext, 0,
			intent, Intent.FLAG_ACTIVITY_NEW_TASK );
		return pendingIntent;
	}
}
