/**
 * Created on Aug 7, 2011
 */
package net.galluzzo.timebuddy.android;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * An activity to edit Time Buddy settings.
 * 
 * @author Eric Galluzzo, eric@galluzzo.net
 */
public class EditSettingsActivity extends PreferenceActivity
{
	@Override
	public void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		addPreferencesFromResource( R.xml.preferences );
	}
}
