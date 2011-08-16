/**
 * Created on Aug 15, 2011
 */
package net.galluzzo.timebuddy.model;

import java.util.Comparator;

/**
 * A comparator that compares time entries by their timestamp.  Entries with no
 * timestamp come first.
 * 
 * @author Eric Galluzzo, eric@galluzzo.net
 */
public class TimeEntryTimestampComparator implements Comparator<TimeEntry>
{
	@Override
	public int compare( TimeEntry inObject1, TimeEntry inObject2 )
	{
		long millis1 = inObject1.getTimestamp() == null ? 0
			: inObject1.getTimestamp()
				.getTime();
		long millis2 = inObject2.getTimestamp() == null ? 0
			: inObject2.getTimestamp()
				.getTime();

		// I'm not using the "millis1 - millis2" trick, because I'm comparing
		// longs but returning an int, which may wrap over the MAX_INTEGER
		// boundary and return the wrong sign.

		if ( millis1 < millis2 )
		{
			return -1;
		}
		else if ( millis1 > millis2 )
		{
			return 1;
		}
		else
		{
			return 0;
		}
	}
}
