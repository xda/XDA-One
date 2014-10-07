package com.xda.one.ui.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.TextView;

public class SlidingTabLayout extends TabLayout {

    private static final int TAB_VIEW_TEXT_SIZE_SP = 14;

    private static final int TAB_VIEW_PADDING_DIPS = 16;

    public SlidingTabLayout(final Context context) {
        super(context);
    }

    public SlidingTabLayout(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public SlidingTabLayout(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
    }


    /**
     * Create a default view to be used for tabs. This is called if a custom tab view is not set
     * via
     * {@link #setCustomTabView(int, int)}.
     */
    @Override
    protected TextView createDefaultTabView(Context context) {
        TextView textView = new TextView(context);
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, TAB_VIEW_TEXT_SIZE_SP);
        textView.setTypeface(Typeface.DEFAULT_BOLD);
        textView.setTextColor(Color.WHITE);

        // If we're running on Honeycomb or newer, then we can use the Theme's
        // selectableItemBackground to ensure that the View has a pressed state
        TypedValue outValue = new TypedValue();
        getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground,
                outValue, true);
        textView.setBackgroundResource(outValue.resourceId);

        // If we're running on ICS or newer, enable all-caps to match the Action Bar tab style
        textView.setAllCaps(true);

        int padding = (int) (TAB_VIEW_PADDING_DIPS * getResources().getDisplayMetrics().density);
        textView.setPadding(padding, padding, padding, padding);

        return textView;
    }
}