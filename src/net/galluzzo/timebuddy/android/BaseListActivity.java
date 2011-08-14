/**
 * Created on Aug 9, 2011
 */
package net.galluzzo.timebuddy.android;

import net.galluzzo.timebuddy.service.TimeBuddyService;
import android.app.ListActivity;

/**
 * The base class for all Time Buddy activities.
 * 
 * @author Eric Galluzzo, eric@galluzzo.net
 */
public class BaseListActivity extends ListActivity
{
	protected TimeBuddyService getTimeBuddyService()
	{
		return ServiceFactory.getTimeBuddyService( getApplicationContext() );
	}
	
	protected Settings getSettings()
	{
		return ServiceFactory.getSettings( getApplicationContext() );
	}
}
