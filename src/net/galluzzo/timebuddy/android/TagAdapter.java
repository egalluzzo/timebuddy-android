/**
 * Created on Aug 11, 2011
 */
package net.galluzzo.timebuddy.android;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import net.galluzzo.timebuddy.model.Tag;
import net.galluzzo.timebuddy.model.TagLabelComparator;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * A list adapter that shows tags with their colors.
 * 
 * @author Eric Galluzzo, eric@galluzzo.net
 */
public class TagAdapter extends ArrayAdapter<Tag>
{
	private int resourceId;
	private Comparator<Tag> comparator;

	private class ViewWrapper
	{
		private TextView textView;
		private ImageView imageView;

		public ViewWrapper( TextView textView, ImageView imageView )
		{
			this.textView = textView;
			this.imageView = imageView;
		}

		public void showTag( Tag tag )
		{
			textView.setText( tag.getLabel() );
			if ( tag.isTask() )
			{
				imageView.setVisibility( View.VISIBLE );
				imageView.setImageDrawable( new ColorDrawable( tag.getColor() ) );
				//imageView.setImageDrawable( new ColorDrawable( tag.getColor().toRGBAColor() ) );
			}
			else
			{
				imageView.setVisibility( View.INVISIBLE );
			}
		}
	}

	public TagAdapter( Context context, int resourceId, List<Tag> tags )
	{
		super( context, resourceId, new ArrayList<Tag>( tags ) );
		this.resourceId = resourceId;
		this.comparator = new TagLabelComparator();
		sort( comparator );
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView( int inPosition, View inConvertView, ViewGroup inParent )
	{
		View tagView = inConvertView;
		if ( tagView == null )
		{
			LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(
				Context.LAYOUT_INFLATER_SERVICE );
			tagView = layoutInflater.inflate( resourceId, null );
			tagView.setTag( new ViewWrapper(
				(TextView) tagView.findViewById( android.R.id.text1 ),
				(ImageView) tagView.findViewById( android.R.id.icon1 ) ) );
		}

		Tag tag = (Tag) getItem( inPosition );
		ViewWrapper wrapper = (ViewWrapper) tagView.getTag();
		wrapper.showTag( tag );

		return tagView;
	}

	public void addTag( Tag tag )
	{
		// I'm too lazy to use Collections.binarySearch.
		add( tag );
		sort( comparator );
		notifyDataSetChanged();
	}

	public void editTag( Tag tag )
	{
		sort( comparator );
		notifyDataSetChanged();
	}
}
