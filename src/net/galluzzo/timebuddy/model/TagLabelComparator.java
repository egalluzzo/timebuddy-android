/**
 * Created on Aug 12, 2011
 */
package net.galluzzo.timebuddy.model;

import java.util.Comparator;

/**
 * A comparator that compares two {@link Tag}s by their labels.
 * 
 * @author Eric Galluzzo, eric@galluzzo.net
 */
public class TagLabelComparator implements Comparator<Tag>
{
	public int compare( Tag inTag1, Tag inTag2 )
	{
		String label1 = inTag1.getLabel() == null ? "" : inTag1.getLabel();
		String label2 = inTag2.getLabel() == null ? "" : inTag2.getLabel();
		return label1.compareTo( label2 );
	}
}
