/**
 * Created on Aug 6, 2011
 */
package net.galluzzo.timebuddy.android;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.galluzzo.timebuddy.model.Tag;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.TextView;

/**
 * An activity that allows the user to select from a list of available tags.
 * 
 * @author Eric Galluzzo, eric@galluzzo.net
 */
public class SelectTagsActivity extends BaseListActivity
{
	public static final String EXTRA_TAG_IDS = "tagIds";
	
	private static final String TAG = SelectTagsActivity.class.getSimpleName();

	protected TagAdapter tagAdapter;

	@Override
	public void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.select_tags );

		Button selectTagsButton = (Button) findViewById( R.id.selectTagsButton );
		selectTagsButton.setOnClickListener( new OnClickListener()
		{
			public void onClick( View v )
			{
				List<Tag> selectedTags = getSelectedTags();
				String[] selectedTagIds = getTagIds( selectedTags );

				Intent intent = new Intent();
				intent.putExtra( EXTRA_TAG_IDS, selectedTagIds );
				setResult( RESULT_OK, intent );

				finish();
			}
		} );

		Button cancelButton = (Button) findViewById( R.id.cancelButton );
		cancelButton.setOnClickListener( new OnClickListener()
		{
			public void onClick( View v )
			{
				setResult( RESULT_CANCELED );
				finish();
			}
		} );

		getListView().setOnItemClickListener( new OnItemClickListener()
		{
			private boolean inMethod = false;

			public void onItemClick( AdapterView<?> inParent, View inView,
				final int inPosition, long inId )
			{
				// Prevent re-entry
				if ( !inMethod )
				{
					inMethod = true;
					try
					{
						if ( getListView().isItemChecked( inPosition ) )
						{
							deselectTasksExceptFor( inPosition );
						}
					}
					finally
					{
						inMethod = false;
					}
				}
			}
		} );

		// TODO: Do this asynchronously, with a ProgressBar somewhere.
		List<Tag> tags;
		try
		{
			tags = getTimeBuddyService().findAllTags();
		}
		catch ( Exception ex )
		{
			Log.e( TAG, "Could not read tags", ex );
			// FIXME: Use a resource ID
			( (TextView) findViewById( android.R.id.empty ) ).setText( "Could not read tags" );
			tags = Collections.emptyList();
		}
		tagAdapter = new TagAdapter( this, R.layout.list_item_tag_checked, tags );
		setListAdapter( tagAdapter );
	}

	@Override
	protected void onResume()
	{
		super.onResume();

		String[] selectedTagIds = getIntent().getStringArrayExtra(
			EXTRA_TAG_IDS );
		selectTagLabels( selectedTagIds );
	}

	/**
	 * @param extraTagLabels
	 */
	private void selectTagLabels( String[] selectedTagIds )
	{
		Set<String> tagIdsToSelect = new HashSet<String>();
		if ( selectedTagIds != null )
		{
			tagIdsToSelect.addAll( Arrays.asList( selectedTagIds ) );
		}

		for ( int i = 0; i < tagAdapter.getCount(); i++ )
		{
			boolean select = tagIdsToSelect.contains( ( (Tag) tagAdapter.getItem( i ) ).getId() );
			getListView().setItemChecked( i, select );
			i++;
		}

		// TODO: Could add any that aren't in the list to the master list
	}

	/**
	 * Returns all the tags that the user has checked in the list.
	 * 
	 * @return  The list of selected tags
	 */
	private List<Tag> getSelectedTags()
	{
		List<Tag> selectedTags = new ArrayList<Tag>();
		SparseBooleanArray checkedItems = getListView().getCheckedItemPositions();
		for ( int i = 0; i < checkedItems.size(); i++ )
		{
			if ( checkedItems.get( i ) )
			{
				selectedTags.add( (Tag) tagAdapter.getItem( i ) );
			}
		}
		return selectedTags;
	}

	private String[] getTagIds( List<Tag> tags )
	{
		String[] tagIds = new String[tags.size()];
		for ( int i = 0; i < tagIds.length; i++ )
		{
			tagIds[i] = tags.get( i )
				.getId();
		}
		return tagIds;
	}

	/**
	 * Unchecks all tasks (not tags) except for the one at the given position.
	 * 
	 * @param inPosition  The position of the task to leave checked
	 */
	private void deselectTasksExceptFor( int inPosition )
	{
		Tag selectedTag = (Tag) tagAdapter.getItem( inPosition );
		if ( selectedTag.isTask() )
		{
			for ( int i = 0; i < tagAdapter.getCount(); i++ )
			{
				Tag tag = (Tag) tagAdapter.getItem( i );
				if ( tag.isTask() && i != inPosition )
				{
					getListView().setItemChecked( i, false );
				}
			}
		}
	}
}
