/**
 * Created on Aug 6, 2011
 */
package net.galluzzo.timebuddy.android;

import java.util.List;

import net.galluzzo.timebuddy.model.Tag;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;

/**
 * An activity that allows the user to select from a list of available tags.
 * 
 * @author Eric Galluzzo, eric@galluzzo.net
 */
public class EditTagsActivity extends BaseListActivity
{
	protected static final int NEW_TAG_REQUEST_CODE = 1;
	protected static final int EDIT_TAG_REQUEST_CODE = 2;

	protected Tag editedTag;
	protected TagAdapter tagAdapter;

	@Override
	public void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.edit_tags );

		Button newTagButton = (Button) findViewById( R.id.newTagButton );
		newTagButton.setOnClickListener( new OnClickListener()
		{
			public void onClick( View v )
			{
				Intent intent = new Intent( EditTagsActivity.this,
					EditTagActivity.class );
				startActivityForResult( intent, NEW_TAG_REQUEST_CODE );
			}
		} );

		getListView().setOnItemClickListener( new OnItemClickListener()
		{
			@Override
			public void onItemClick( AdapterView<?> inParent, View inView,
				int inPosition, long inId )
			{
				editedTag = (Tag) tagAdapter.getItem( inPosition );
				Intent intent = new Intent( EditTagsActivity.this,
					EditTagActivity.class );
				populateIntentFromTag( intent, editedTag );
				startActivityForResult( intent, EDIT_TAG_REQUEST_CODE );
			}
		} );

		// TODO: Do this asynchronously, with a ProgressBar somewhere.
		List<Tag> tags = getTimeBuddyService().findAllTags();
		tagAdapter = new TagAdapter( this, R.layout.list_item_tag, tags );
		setListAdapter( tagAdapter );
//		listAdapter = new ArrayAdapter<Tag>( this,
//			android.R.layout.simple_list_item_checked, tags );
//		setListAdapter( listAdapter );
	}

	@Override
	protected void onActivityResult( int inRequestCode, int inResultCode,
		Intent inData )
	{
		super.onActivityResult( inRequestCode, inResultCode, inData );
		switch ( inRequestCode )
		{
			case NEW_TAG_REQUEST_CODE:
				if ( inResultCode == RESULT_OK )
				{
					addTag( inData );
				}
				break;

			case EDIT_TAG_REQUEST_CODE:
				if ( inResultCode == RESULT_OK )
				{
					editTag( inData );
				}
				break;

			default:
				throw new IllegalStateException( "Unknown request code "
					+ inRequestCode );
		}
	}

	protected void addTag( Intent inData )
	{
		Tag tag = new Tag();
		populateTagFromIntent( tag, inData );
		// FIXME: Make this asynchronous
		tag = getTimeBuddyService().createNewTag( tag );
		tagAdapter.addTag( tag );
	}

	protected void editTag( Intent inData )
	{
		populateTagFromIntent( editedTag, inData );
		// FIXME: Make this asynchronous
		getTimeBuddyService().saveTag( editedTag );
		tagAdapter.editTag( editedTag );
		editedTag = null;
	}

	protected void populateTagFromIntent( Tag inTag, Intent inData )
	{
		inTag.setLabel( inData.getStringExtra( EditTagActivity.EXTRA_LABEL ) );
		inTag.setTask( inData.getBooleanExtra( EditTagActivity.EXTRA_TASK,
			false ) );
//		inTag.setColor( Color.fromRGBAColor( inData.getIntExtra(
//			EditTagActivity.EXTRA_COLOR, 0 ) ) );
		inTag.setColor( inData.getIntExtra( EditTagActivity.EXTRA_COLOR, 0xFF0000FF ) );
	}

	protected void populateIntentFromTag( Intent inData, Tag inTag )
	{
		inData.putExtra( EditTagActivity.EXTRA_LABEL, inTag.getLabel() );
		inData.putExtra( EditTagActivity.EXTRA_TASK, inTag.isTask() );
		inData.putExtra( EditTagActivity.EXTRA_COLOR, inTag.getColor() );
//		if ( inTag.getColor() != null )
//		{
//			inData.putExtra( EditTagActivity.EXTRA_COLOR, inTag.getColor()
//				.toRGBAColor() );
//		}
	}
}
