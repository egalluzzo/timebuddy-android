/**
 * Created on Aug 9, 2011
 */
package net.galluzzo.timebuddy.service;

import java.util.Date;
import java.util.List;

import net.galluzzo.timebuddy.model.Tag;
import net.galluzzo.timebuddy.model.TimeEntry;

/**
 * The Time Buddy service, that performs persistence functions on
 * {@link TimeEntry}s and {@link Tag}s.
 * 
 * @author Eric Galluzzo, eric@galluzzo.net
 */
public interface TimeBuddyService
{
	void submit( TimeEntry timeEntry ) throws RuntimeException;

	Tag createNewTag( Tag tag );
	
	void saveTag( Tag tag );

	List<Tag> findAllTags();

	List<TimeEntry> findEntriesBetweenDates( Date startDate, Date endDate )
		throws RuntimeException;
}