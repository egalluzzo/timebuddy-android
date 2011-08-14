/**
 * Created on Aug 9, 2011
 */
package net.galluzzo.timebuddy.model;

import java.util.HashMap;
import java.util.Map;

/**
 * A tag that can be applied to a time entry.
 * 
 * @author Eric Galluzzo, eric@galluzzo.net
 */
public class Tag
{
	private static Map<String, Tag> idToTagMap = new HashMap<String, Tag>();

	private String id;
	private String label;
	private boolean task;
//	private Color color;
	private int color = 0xFFFFFFFF; // white
	
	/**
	 * This constructor is useful for making tags without an ID, to use as
	 * templates for a real tag with an ID.
	 */
	public Tag()
	{
	}

	protected Tag( String id )
	{
		this.id = id;
	}

	public String getId()
	{
		return id;
	}

	public String getLabel()
	{
		return label;
	}

	public void setLabel( String inLabel )
	{
		label = inLabel;
	}
	
	public boolean isTask()
	{
		return task;
	}
	
	public void setTask( boolean inTask )
	{
		task = inTask;
	}

//	public Color getColor()
//	{
//		return color;
//	}
//
//	public void setColor( Color inColor )
//	{
//		color = inColor;
//	}
	
	public int getColor()
	{
		return color;
	}
	
	public void setColor( int inColor )
	{
		color = inColor;
	}

	@Override
	public String toString()
	{
		return "Tag [id = " + id + ", label = " + label + ", color = " + color
			+ "]";
	}
	
	/**
	 * Copies the non-ID attributes from the other tag to this one.
	 * 
	 * @param inOther  The tag to copy from
	 */
	public void copyFrom( Tag inOther )
	{
		label = inOther.getLabel();
		task = inOther.isTask();
		color = inOther.getColor();
	}

	public static Tag getTag( String inId )
	{
		Tag tag = idToTagMap.get( inId );
		if ( tag == null )
		{
			tag = new Tag( inId );
			idToTagMap.put( inId, tag );
		}
		return tag;
	}
	
	public static boolean doesTagIdExist( String inId )
	{
		return idToTagMap.containsKey( inId );
	}
}
