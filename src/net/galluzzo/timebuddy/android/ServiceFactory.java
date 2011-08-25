/**
 * Created on Aug 9, 2011
 */
package net.galluzzo.timebuddy.android;

import net.galluzzo.timebuddy.service.RemoteTimeBuddyService;
import net.galluzzo.timebuddy.service.TimeBuddyService;
import android.content.Context;

/**
 * @author Eric Galluzzo, eric@galluzzo.net
 */
public class ServiceFactory
{
	private static Settings settings;
	private static TimeBuddyService timeBuddyService;

	public static TimeBuddyService getTimeBuddyService( Context inContext )
	{
		if ( timeBuddyService == null )
		{
			timeBuddyService = new RemoteTimeBuddyService(
				getSettings( inContext ), new ContextFileProvider( inContext ) );
		}
		return timeBuddyService;
	}

	public static Settings getSettings( Context inContext )
	{
		if ( settings == null )
		{
			settings = new Settings( inContext );
		}
		return settings;
	}

	public static void setSettings( Settings inSettings )
	{
		settings = inSettings;
	}

	public static void setTimeBuddyService( TimeBuddyService inTimeBuddyService )
	{
		timeBuddyService = inTimeBuddyService;
	}
}
