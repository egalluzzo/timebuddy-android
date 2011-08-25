/**
 * Created on Aug 7, 2011
 */
package net.galluzzo.timebuddy.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * @author Eric Galluzzo, eric@galluzzo.net
 *
 */
public class TimeEntry
{
	private String message;
	private List<Tag> tags = new ArrayList<Tag>( 2 );
	private Date timestamp;

	public String getMessage()
	{
		return message;
	}

	public void setMessage( String inMessage )
	{
		message = inMessage;
	}

	public List<Tag> getTags()
	{
		return tags;
	}

	public void addTag( Tag inTag )
	{
		tags.add( inTag );
	}
	
	public void addAllTags( Collection<Tag> inTags )
	{
		tags.addAll( inTags );
	}
	
	public void clearTags()
	{
		tags.clear();
	}
	
	@Override
	public String toString()
	{
		return "TimeEntry[timestamp = " + timestamp + ", message = " + message
				+ ", tags = " + getCommaSeparatedTagLabels() + "]";
	}
	
	public String getCommaSeparatedTagLabels()
	{
		StringBuilder sb = new StringBuilder();
		for ( Iterator<Tag> tagIter = tags.iterator(); tagIter.hasNext(); )
		{
			Tag tag = tagIter.next();
			sb.append( tag.getLabel() );
			if ( tagIter.hasNext() )
			{
				sb.append( ", " );
			}
		}
		
		return sb.toString();
	}
	
	public String getCommaSeparatedTagIds()
	{
		StringBuilder sb = new StringBuilder();
		for ( Iterator<Tag> tagIter = tags.iterator(); tagIter.hasNext(); )
		{
			Tag tag = tagIter.next();
			sb.append( tag.getId() );
			if ( tagIter.hasNext() )
			{
				sb.append( ", " );
			}
		}
		
		return sb.toString();
	}

	public Date getTimestamp()
	{
		return timestamp;
	}

	public void setTimestamp( Date inTimestamp )
	{
		timestamp = inTimestamp;
	}

	/**
	 * Returns the color of the first task assigned to this time entry, if any,
	 * or gray if there is none.
	 * 
	 * @return  The color that this time entry should be rendered
	 */
	public int getColor()
	{
		for ( Tag tag : tags )
		{
			if ( tag.isTask() )
			{
				return tag.getColor();
			}
		}
		return 0xFF808080;
	}
}
