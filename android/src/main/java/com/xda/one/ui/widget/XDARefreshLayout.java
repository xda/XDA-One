package com.xda.one.ui.widget;

import com.xda.one.R;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;

public class XDARefreshLayout extends SwipeRefreshLayout {

    public XDARefreshLayout(Context context) {
        super(context);
    }

    public XDARefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setXDAColourScheme() {
        setColorSchemeResources(R.color.colorPrimary, R.color.colorAccent,
                R.color.colorPrimaryDark, android.R.color.white);
    }
}