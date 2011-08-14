/**
 * Created on Aug 13, 2011
 */
package net.galluzzo.timebuddy.android;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import net.galluzzo.timebuddy.service.FileProvider;
import android.content.Context;

/**
 * @author Eric Galluzzo, eric@galluzzo.net
 *
 */
public class ContextFileProvider implements FileProvider
{
	private Context context;
	
	public ContextFileProvider( Context inContext )
	{
		context = inContext;
	}
	
	public boolean fileExists( String inFileName )
	{
		return context.getFileStreamPath( inFileName ).exists();
	}
	
	public InputStream readLocalFile( String inFileName ) throws IOException
	{
		return context.openFileInput( inFileName );
	}
	
	public OutputStream writeLocalFile( String inFileName ) throws IOException
	{
		return context.openFileOutput( inFileName, Context.MODE_PRIVATE );
	}
}
