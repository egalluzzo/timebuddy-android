/**
 * Created on Aug 13, 2011
 */
package net.galluzzo.timebuddy.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Provides a mechanism for reading and writing private files.
 * 
 * @author Eric Galluzzo, eric@galluzzo.net
 */
public interface FileProvider
{
	/**
	 * Determines whether the given local file already exists or not.
	 * 
	 * @param fileName  The filename, without any path information
	 * 
	 * @return  <code>true</code> if the file exists, <code>false</code> if not
	 */
	boolean fileExists( String fileName );
	
	/**
	 * Opens an {@link InputStream} for reading a local file.
	 * 
	 * @param fileName  The filename, without any path information
	 * 
	 * @return  The input stream for reading the file
	 * 
	 * @throws IOException
	 *     If the file could not be opened
	 */
	InputStream readLocalFile( String fileName ) throws IOException;
	
	/**
	 * Opens an {@link OutputStream} for writing a local file.
	 * 
	 * @param fileName  The filename, without any path information
	 * 
	 * @return  The output stream for writing the file
	 * 
	 * @throws IOException
	 *     If the file could not be opened for writing
	 */
	OutputStream writeLocalFile( String fileName ) throws IOException;
}
