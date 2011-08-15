/**
 * Created on Aug 13, 2011
 */
package net.galluzzo.timebuddy.service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import net.galluzzo.timebuddy.model.Tag;
import net.galluzzo.timebuddy.model.TimeEntry;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import android.util.Log;

/**
 * @author Eric Galluzzo, eric@galluzzo.net
 *
 */
public class EntriesContentHandler extends DefaultHandler
{
	private static final String TAG = EntriesContentHandler.class.getSimpleName();
	
	private static final String ENTRY_ELEM = "entries";
	private static final String START_TIME_ELEM = "startTime";
	private static final String DESCRIPTION_ELEM = "description";
	private static final String TAGS_ELEM = "commaSeparatedTagList";
	
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss'Z'" );
	static
	{
		DATE_FORMAT.setTimeZone( TimeZone.getTimeZone( "UTC" ) );
	}
	
	private List<TimeEntry> timeEntries;
	private TimeEntry timeEntry;
	private StringBuilder stringBuilder;
	
	@Override
	public void startDocument() throws SAXException
	{
		timeEntries = new ArrayList<TimeEntry>();
		timeEntry = new TimeEntry();
		stringBuilder = new StringBuilder();
	}
	
	@Override
	public void characters( char[] inCh, int inStart, int inLength )
		throws SAXException
	{
		stringBuilder.append( inCh, inStart, inLength );
	}

	@Override
	public void endElement( String inUri, String inLocalName, String inQName )
		throws SAXException
	{
		if ( ENTRY_ELEM.equals( inLocalName ) )
		{
			timeEntries.add( timeEntry );
			timeEntry = new TimeEntry();
		}
		else if ( START_TIME_ELEM.equals( inLocalName ) )
		{
			try
			{
				timeEntry.setTimestamp( DATE_FORMAT.parse( stringBuilder.toString().trim() ) );
			}
			catch ( ParseException e )
			{
				Log.w( TAG, "Could not parse date: " + stringBuilder.toString().trim() );
			}
		}
		else if ( DESCRIPTION_ELEM.equals( inLocalName ) )
		{
			timeEntry.setMessage( stringBuilder.toString().trim() );
		}
		else if ( TAGS_ELEM.equals( inLocalName ) )
		{
			addTags( stringBuilder.toString().trim() );
		}
		stringBuilder.setLength( 0 );
	}
	
	private void addTags( String inCommaSeparatedTagList )
	{
		String[] tagIds = inCommaSeparatedTagList.split( "," );
		for ( String tagId : tagIds )
		{
			tagId = tagId.trim();
			Tag tag;
			if ( Tag.doesTagIdExist( tagId ) )
			{
				tag = Tag.getTag( tagId );
			}
			else
			{
				// This is probably an old tag without an ID.  So create a
				// temporary tag without an ID.
				tag = new Tag();
				tag.setLabel( tagId );
			}
			timeEntry.addTag( tag );
		}
	}
	
	public List<TimeEntry> getTimeEntries()
	{
		return timeEntries;
	}
}
