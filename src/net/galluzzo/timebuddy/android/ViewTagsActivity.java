/**
 * Created on Sep 3, 2011
 */
package net.galluzzo.timebuddy.android;

import java.util.ArrayList;
import java.util.List;

import net.galluzzo.timebuddy.model.Tag;
import android.os.Bundle;

/**
 * An activity that shows a read-only list of tags.
 * 
 * @author Eric Galluzzo, eric@galluzzo.net
 */
public class ViewTagsActivity extends BaseListActivity
{
	public static final String EXTRA_TAG_IDS = "tagIds";
	
	@Override
	protected void onCreate( Bundle inSavedInstanceState )
	{
		super.onCreate( inSavedInstanceState );
		setContentView( R.layout.view_tags );
		
		String[] tagIds = getIntent().getStringArrayExtra( EXTRA_TAG_IDS );
		List<Tag> tags = new ArrayList<Tag>();
		for ( String tagId : tagIds )
		{
			if ( Tag.doesTagIdExist( tagId ) )
			{
				tags.add( Tag.getTag( tagId ) );
			}
		}
		
		TagAdapter tagAdapter = new TagAdapter( this, R.layout.list_item_tag, tags );
		setListAdapter( tagAdapter );
	}
}
