/**
 * Created on Aug 9, 2011
 */
package net.galluzzo.timebuddy.service;

/**
 * A class that can provide the URL for a remote Time Buddy REST server.
 * 
 * @author Eric Galluzzo, eric@galluzzo.net
 */
public interface RemoteUrlProvider
{
	/**
	 * Provides the URL of the remote Time Buddy REST server.
	 * 
	 * @return  The URL of the remote Time Buddy server
	 */
	String getTimeBuddyServerUrl();
}
