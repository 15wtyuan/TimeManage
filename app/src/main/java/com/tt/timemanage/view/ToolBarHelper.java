package com.tt.timemanage.view;

/**
 * Created by bill on 2017/9/23.
 */

import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import com.tt.timemanage.R;

public class ToolBarHelper {
    private Toolbar mToolbar;

    public ToolBarHelper(Toolbar toolbar) {
        this.mToolbar = toolbar;
    }

    public Toolbar getToolbar() {
        mToolbar.setTitle("");
        return mToolbar;
    }

    public void setTitle(String title) {
        TextView titleTV = (TextView) mToolbar.findViewById(R.id.tv_title);
        titleTV.setText(title);
    }

    public void setTitlesColor(int color){
        TextView titleTV = (TextView) mToolbar.findViewById(R.id.tv_title);
        titleTV.setTextColor(color);
    }

}
