/**
 * Created on Aug 9, 2011
 */
package net.galluzzo.timebuddy.model;

import java.text.MessageFormat;
import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * An RGB color.
 * 
 * @author Eric Galluzzo, eric@galluzzo.net
 */
public class Color
{
	private static final Pattern COLOR_PATTERN = Pattern.compile( "^#([0-9A-Fa-f]{2})([0-9A-Fa-f]{2})([0-9A-Fa-f]{2})$" );

	private int red, green, blue;

	public Color( int red, int green, int blue )
	{
		if ( red < 0 || red > 255 || green < 0 || green > 255 || blue < 0
			|| blue > 255 )
		{
			throw new IllegalArgumentException(
				"Red, green and blue values must be between 0 and 255" );
		}
		this.red = red;
		this.green = green;
		this.blue = blue;
	}
	
	public int toARGBColor()
	{
		return 0xFF000000 | (red << 16) | (green << 8) | blue;
	}

	public String toString()
	{
		return MessageFormat.format( "#%02x%02x%02x", red, green, blue );
	}
	
	public static Color fromARGBColor( int inRGBAColor )
	{
		return new Color( ( inRGBAColor >> 16 ) & 0xFF,
			( inRGBAColor >> 8 ) & 0xFF,
			inRGBAColor & 0xFF );
	}

	public static Color parseColor( String inColor ) throws ParseException
	{
		if ( inColor == null )
		{
			throw new IllegalArgumentException( "Color may not be null" );
		}

		Matcher matcher = COLOR_PATTERN.matcher( inColor );
		if ( matcher.matches() )
		{
			return new Color( parseHexString( matcher.group( 1 ) ),
				parseHexString( matcher.group( 2 ) ),
				parseHexString( matcher.group( 3 ) ) );
		}
		else
		{
			// We don't know where the error occurred, so we'll use location 0.
			throw new ParseException(
				"Invalid color string, must be in the form #xxxxxx: " + inColor,
				0 );
		}
	}

	protected static int parseHexString( String inHexString )
	{
		return Integer.parseInt( inHexString, 16 );
	}
}
