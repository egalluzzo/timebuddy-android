/**
 * Created on Aug 12, 2011
 */
package net.galluzzo.timebuddy.android;

import net.galluzzo.android.widget.ColorPickerDialog;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;

/**
 * An activity that allows the user to create a new tag.
 * 
 * @author Eric Galluzzo, eric@galluzzo.net
 */
public class EditTagActivity extends BaseActivity
{
	public static final String EXTRA_TASK = "task";
	public static final String EXTRA_LABEL = "label";
	public static final String EXTRA_COLOR = "color";
	
	protected int color = 0xFFFFFFFF; // white
	protected EditText labelTextField;
	protected CheckBox taskCheckBox;
	protected Button colorButton;
	
	@Override
	protected void onCreate( Bundle inSavedInstanceState )
	{
		super.onCreate( inSavedInstanceState );
		setContentView( R.layout.edit_tag );
		
		labelTextField = (EditText) findViewById( R.id.editTagLabelText );
		taskCheckBox = (CheckBox) findViewById( R.id.editTagTaskCheckBox );
		colorButton = (Button) findViewById( R.id.editTagColorButton );
		
		if ( getIntent() != null )
		{
			populateFieldsFromIntent();
		}
		
		taskCheckBox.setOnCheckedChangeListener( new OnCheckedChangeListener()
		{
			public void onCheckedChanged( CompoundButton inButtonView,
				boolean inIsChecked )
			{
				colorButton.setEnabled( inIsChecked );
			}
		} );
		
		colorButton.setEnabled( taskCheckBox.isChecked() );
		colorButton.setOnClickListener( new OnClickListener()
		{
			public void onClick( View inV )
			{
				new ColorPickerDialog( EditTagActivity.this,
					new ColorPickerDialog.OnColorChangedListener()
					{
						public void colorChanged( int inColor )
						{
							setColor( inColor );
						}
					}, color ).show();
			}
		} );
		
		Button saveButton = (Button) findViewById( R.id.saveTagButton );
		saveButton.setOnClickListener( new OnClickListener()
		{
			public void onClick( View inV )
			{
				Intent intent = new Intent();
				intent.putExtra( EXTRA_LABEL, labelTextField.getText().toString() );
				intent.putExtra( EXTRA_TASK, taskCheckBox.isChecked() );
				intent.putExtra( EXTRA_COLOR, color );
				setResult( RESULT_OK, intent );
				finish();
			}
		} );
		
		Button cancelButton = (Button) findViewById( R.id.cancelButton );
		cancelButton.setOnClickListener( new OnClickListener()
		{
			public void onClick( View inV )
			{
				setResult( RESULT_CANCELED );
				finish();
			}
		} );
	}

	/**
	 * 
	 */
	private void populateFieldsFromIntent()
	{
		String label = getIntent().getStringExtra( EXTRA_LABEL );
		if ( label != null )
		{
			labelTextField.setText( label );
		}
		
		boolean isTask = getIntent().getBooleanExtra( EXTRA_TASK, false );
		taskCheckBox.setChecked( isTask );
		
		setColor( getIntent().getIntExtra( EXTRA_COLOR, 0xFFFFFFFF ) );
	}
	
	@Override
	protected void onSaveInstanceState( Bundle inOutState )
	{
		super.onSaveInstanceState( inOutState );
		inOutState.putInt( EXTRA_COLOR, color );
	}
	
	@Override
	protected void onRestoreInstanceState( Bundle inSavedInstanceState )
	{
		super.onRestoreInstanceState( inSavedInstanceState );
		color = inSavedInstanceState.getInt( EXTRA_COLOR );
	}

	protected void setColor( int inColor )
	{
		color = inColor;
		colorButton.getBackground().setColorFilter( inColor, PorterDuff.Mode.MULTIPLY );
	}
}
