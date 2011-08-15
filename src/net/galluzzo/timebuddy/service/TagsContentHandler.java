/**
 * Created on Aug 13, 2011
 */
package net.galluzzo.timebuddy.service;

import java.util.ArrayList;
import java.util.List;

import net.galluzzo.timebuddy.model.Tag;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import android.graphics.Color;
import android.util.Log;

/**
 * SAX content handler for tags XML files.  These have a format similar to the
 * following:
 * 
 * <pre>
 * &lt;tags&gt;
 *   &lt;tag&gt;
 *     &lt;id&gt;8225c3e9002afb17&lt;/id&gt;
 *     &lt;label&gt;Administrative&lt;/label&gt;
 *     &lt;task&gt;false&lt;/task&gt;
 *   &lt;/tag&gt;
 *   &lt;tag&gt;
 *     &lt;id&gt;9a8329f283bc561e&lt;/id&gt;
 *     &lt;label&gt;Manhattan Project&lt;/label&gt;
 *     &lt;task&gt;true&lt;task&gt;
 *     &lt;color&gt;#FF007F&lt;/color&gt;
 *   &lt;/tag&gt;
 * &lt;/tags&gt;
 * </pre>
 * 
 * @author Eric Galluzzo, eric@galluzzo.net
 */
public class TagsContentHandler extends DefaultHandler
{
	private static final String TAG = TagsContentHandler.class.getSimpleName();
	
	private static final String TAG_ELEM = "tag";
	private static final String ID_ELEM = "id";
	private static final String LABEL_ELEM = "label";
	private static final String TASK_ELEM = "task";
	private static final String COLOR_ELEM = "color";
	
	private List<Tag> tags;
	private Tag tag;
	private String id;
	private StringBuilder stringBuilder;
	
	@Override
	public void startDocument() throws SAXException
	{
		tags = new ArrayList<Tag>();
		tag = new Tag();
		id = null;
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
		if ( TAG_ELEM.equals( inLocalName ) )
		{
			if ( id == null )
			{
				Log.w( TAG, "Not adding tag " + tag + ": no ID specified" );
			}
			else
			{
				Tag tagWithId = Tag.getTag( id );
				tagWithId.copyFrom( tag );
				tags.add( tagWithId );
			}
			tag = new Tag();
		}
		else if ( ID_ELEM.equals( inLocalName ) )
		{
			id = stringBuilder.toString().trim();
		}
		else if ( LABEL_ELEM.equals( inLocalName ) )
		{
			tag.setLabel( stringBuilder.toString().trim() );
		}
		else if ( TASK_ELEM.equals( inLocalName ) )
		{
			tag.setTask( Boolean.parseBoolean( stringBuilder.toString().trim() ) );
		}
		else if ( COLOR_ELEM.equals( inLocalName ) )
		{
			// TODO: This is currently Android-dependent.
			Log.d( TAG, "Parsing color: " + stringBuilder.toString().trim() );
			tag.setColor( Color.parseColor( stringBuilder.toString().trim() ) );
		}
		stringBuilder.setLength( 0 );
	}
	
	public List<Tag> getTags()
	{
		return tags;
	}
}
