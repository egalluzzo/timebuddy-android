/*
 * Copyright 2011 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.galluzzo.android.widget;

import net.galluzzo.timebuddy.android.R;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.text.format.DateUtils;
import android.widget.Button;

/**
 * Custom view that represents a block on a timeline, including its title and
 * time span that it occupies. Usually organized automatically by
 * {@link BlocksLayout} to match up against a {@link TimeRulerView} instance.
 * 
 * <p>Note: This file was copied and modified significantly from the version in
 * the Google I/O 2011 application.
 * 
 * @see http://code.google.com/p/iosched/
 */
public class BlockView extends Button {
    private static final int TIME_STRING_FLAGS = DateUtils.FORMAT_SHOW_DATE
            | DateUtils.FORMAT_SHOW_WEEKDAY | DateUtils.FORMAT_ABBREV_WEEKDAY |
            DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_ABBREV_TIME;

    private String mBlockId;
    private long mStartTime;
    private long mEndTime;
    private boolean mContainsStarred;
    private int mColumn;

    public BlockView(Context context, String blockId, String title, long startTime,
            long endTime, boolean containsStarred, int column, int accentColor) {
        super(context);

        mBlockId = blockId;
        mStartTime = startTime;
        mEndTime = endTime;
        mContainsStarred = containsStarred;
        mColumn = column;

        setText(title);

        // TODO: turn into color state list with layers?
        int textColor = Color.WHITE;

        LayerDrawable buttonDrawable = (LayerDrawable)
                context.getResources().getDrawable(R.drawable.btn_block);
        buttonDrawable.getDrawable(0).setColorFilter(accentColor, PorterDuff.Mode.SRC_ATOP);
        buttonDrawable.getDrawable(1).setAlpha(mContainsStarred ? 255 : 0);

        setTextColor(textColor);
        setBackgroundDrawable(buttonDrawable);
    }

    public String getBlockId() {
        return mBlockId;
    }

    public String getBlockTimeString() {
        return DateUtils.formatDateTime(getContext(), mStartTime, TIME_STRING_FLAGS);
    }

    public long getStartTime() {
        return mStartTime;
    }

    public long getEndTime() {
        return mEndTime;
    }

    public int getColumn() {
        return mColumn;
    }
    
    public void setStartTime(long startTime) {
    	mStartTime = startTime;
    	//invalidate();
    }
    
    public void setEndTime(long endTime) {
    	mEndTime = endTime;
    	//invalidate();
    }
    
    public void setColumn(int column) {
    	mColumn = column;
    	//invalidate();
    }
}
