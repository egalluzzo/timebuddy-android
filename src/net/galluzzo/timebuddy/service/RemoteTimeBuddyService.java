/**
 * Created on Aug 7, 2011
 */
package net.galluzzo.timebuddy.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import net.galluzzo.timebuddy.model.Tag;
import net.galluzzo.timebuddy.model.TimeEntry;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import android.graphics.Color;
import android.util.Log;
import android.util.Xml;
import android.util.Xml.Encoding;

/**
 * A service for sending requests to a remote Time Buddy server.
 * 
 * @author Eric Galluzzo, eric@galluzzo.net
 */
public class RemoteTimeBuddyService implements TimeBuddyService
{
	private static final String TAG = RemoteTimeBuddyService.class.getSimpleName();

	protected RemoteUrlProvider remoteUrlProvider;
	protected FileProvider fileProvider;
	protected Random random = new Random();
	protected List<Tag> tags;

	public RemoteTimeBuddyService( RemoteUrlProvider inRemoteUrlProvider,
		FileProvider inFileProvider )
	{
		remoteUrlProvider = inRemoteUrlProvider;
		fileProvider = inFileProvider;
	}

	/* (non-Javadoc)
	 * @see net.galluzzo.timebuddy.service.TimeBuddyService#submit(net.galluzzo.timebuddy.model.TimeEntry)
	 */
	@Override
	public void submit( TimeEntry timeEntry ) throws RuntimeException
	{
		String url = remoteUrlProvider.getTimeBuddyServerUrl();
		HttpPost request = new HttpPost( url );
		Log.i( TAG, "Posting to: " + url );
		if ( Log.isLoggable( TAG, Log.DEBUG ) )
		{
			Log.d( TAG, "Message: " + timeEntry.getMessage() );
			Log.d( TAG, "Tags: " + timeEntry.getCommaSeparatedTagLabels() );
		}

		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add( new BasicNameValuePair( "message",
			timeEntry.getMessage() ) );
		parameters.add( new BasicNameValuePair( "tags",
			timeEntry.getCommaSeparatedTagIds() ) );
		try
		{
			request.setEntity( new UrlEncodedFormEntity( parameters ) );
		}
		catch ( UnsupportedEncodingException ex )
		{
			Log.e( TAG, "Unsupported encoding while trying to post", ex );
			throw new RuntimeException(
				"Unsupported encoding for POST parameters: " + ex, ex );
		}

		executeRequest( request );
	}

	/* (non-Javadoc)
	 * @see net.galluzzo.timebuddy.service.TimeBuddyService#createNewTag(net.galluzzo.timebuddy.model.Tag)
	 */
	@Override
	public Tag createNewTag( Tag inTagData )
	{
		if ( tags == null )
		{
			tags = readTags();
		}

		Tag tag = Tag.getTag( createTagId() );
		tag.copyFrom( inTagData );
		tags.add( tag );

		writeTags();
		return tag;
	}

	protected String createTagId()
	{
		return Long.toHexString( ( ( System.currentTimeMillis() / 1000 ) << 16 )
			+ random.nextInt( 1 << 16 ) );
	}

	public void saveTag( Tag inTagData )
	{
		if ( tags == null )
		{
			tags = readTags();
		}

		writeTags();
	}

	/* (non-Javadoc)
	 * @see net.galluzzo.timebuddy.service.TimeBuddyService#findAllTags()
	 */
	@Override
	public List<Tag> findAllTags()
	{
		if ( tags == null )
		{
			tags = readTags();
		}
		return tags;
	}

	protected List<Tag> readTags()
	{
		if ( fileProvider.fileExists( "tags.xml" ) )
		{
			try
			{
				TagsContentHandler contentHandler = new TagsContentHandler();
				InputStream stream = fileProvider.readLocalFile( "tags.xml" );
				try
				{
					// TODO: Don't use Android-specific classes
					Xml.parse( stream, Encoding.UTF_8, contentHandler );
					return contentHandler.getTags();
				}
				finally
				{
					stream.close();
				}
			}
			catch ( Exception ex )
			{
				Log.e( TAG, "Could not read tags", ex );
				throw new RuntimeException( ex );
			}
		}
		else
		{
			return new ArrayList<Tag>();
		}
	}

	protected void writeTags()
	{
		try
		{
			OutputStream stream = fileProvider.writeLocalFile( "tags.xml" );
			try
			{
				OutputStreamWriter writer = new OutputStreamWriter( stream, "UTF-8" );
				writer.write( "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" );
				writer.write( "<tags>\n" );
				for ( Tag tag : tags )
				{
					writer.write( "  <tag>\n" );
					writer.write( "    <id>" );
					writer.write( escapeXml( tag.getId() ) );
					writer.write( "</id>\n" );
					writer.write( "    <label>" );
					writer.write( escapeXml( tag.getLabel() ) );
					writer.write( "</label>\n" );
					writer.write( "    <task>" );
					writer.write( String.valueOf( tag.isTask() ) );
					writer.write( "</task>\n" );
					if ( tag.isTask() )
					{
						writer.write( "    <color>" );
						// TODO: Don't use Android-specific classes
						writer.write( String.format( "#%02x%02x%02x",
							Color.red( tag.getColor() ),
							Color.green( tag.getColor() ),
							Color.blue( tag.getColor() ) ) );
						writer.write( "</color>\n" );
					}
					writer.write( "  </tag>\n" );
				}
				writer.write( "</tags>" );
			}
			finally
			{
				stream.close();
			}
		}
		catch ( IOException ioe )
		{
			Log.e( TAG, "Could not write tags", ioe );
			throw new RuntimeException( ioe );
		}
	}
	
	protected String escapeXml( String text )
	{
		StringBuilder result = new StringBuilder();
		for ( int i = 0; i < text.length(); i++ )
		{
			char c = text.charAt( i );
			switch ( c )
			{
				case '&':
					result.append( "&amp;" );
					break;
				case '<':
					result.append( "&lt;" );
					break;
				case '>':
					result.append( "&gt;" );
					break;
				case '"':
					result.append( "&quot;" );
					break;
				case '\'':
					result.append( "&apos;" );
					break;
				default:
					result.append( c );
			}
		}
		return result.toString();
	}

	/* (non-Javadoc)
	 * @see net.galluzzo.timebuddy.service.TimeBuddyService#findEntriesBetweenDates(java.util.Date, java.util.Date, java.lang.String)
	 */
	@Override
	public List<TimeEntry> findEntriesBetweenDates( Date startDate, Date endDate )
		throws RuntimeException
	{
		SimpleDateFormat format = new SimpleDateFormat( "yyyy-MM-dd" );
		String from = "";
		String to = "";

		try
		{
			from = format.format( startDate );
			to = format.format( endDate );
		}
		catch ( Exception e )
		{
		}

		String url = remoteUrlProvider.getTimeBuddyServerUrl();
		Log.i( TAG, "Querying time entries between " + from + " and " + to );
		HttpGet request = new HttpGet( url + "?from=" + from + "&to=" + to );

		HttpResponse response = executeRequest( request );

		HttpEntity entity = response.getEntity();
		try
		{
			InputStream contentStream = entity.getContent();
			try
			{
				EntriesContentHandler contentHandler = new EntriesContentHandler();
				Xml.parse( contentStream, Encoding.UTF_8, contentHandler );
				return contentHandler.getTimeEntries();
			}
			finally
			{
				contentStream.close();
			}
		}
		catch ( Exception ex )
		{
			Log.e( TAG, "Could not read time entries", ex );
			throw new RuntimeException( ex );
		}
	}

	/**
	 * Executes the given request, throwing a {@link RuntimeException} if the
	 * HTTP status code was not in the "OK" range (200-299), or if the request
	 * could not be executed in the first place.
	 * 
	 * @param request
	 * @return
	 */
	private HttpResponse executeRequest( HttpUriRequest request )
	{
		String url = remoteUrlProvider.getTimeBuddyServerUrl();
		HttpClient client = new DefaultHttpClient();
		HttpResponse response;
		try
		{
			response = client.execute( request );
		}
		catch ( ClientProtocolException ex )
		{
			Log.e( TAG,
				"Unable to communicate with server: " + ex.getMessage(), ex );
			throw new RuntimeException( "Unable to communicate with server: "
				+ url, ex );
		}
		catch ( IOException ex )
		{
			Log.e(
				TAG,
				"IO exception during data transfer with server: "
					+ ex.getMessage(), ex );
			throw new RuntimeException(
				"Data IO error communicating with server: " + url, ex );
		}

		int statusCode = response.getStatusLine()
			.getStatusCode();
		String message = response.getStatusLine()
			.getReasonPhrase();

		Log.i( TAG, "Received " + statusCode + " " + message );

		if ( statusCode < 200 || statusCode >= 300 )
		{
			throw new RuntimeException( "Received error from server: "
				+ statusCode + " " + message );
		}
		return response;
	}
}
