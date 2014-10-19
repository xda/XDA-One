package com.xda.one.util;

import com.xda.one.R;
import com.xda.one.ui.BaseActivity;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class UIUtils {

    public static int calculateActionBarSize(final Context context) {
        final TypedValue tv = new TypedValue();
        context.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true);
        return TypedValue.complexToDimensionPixelSize(tv.data,
                context.getResources().getDisplayMetrics());
    }

    public static void updateEmptyViewState(final View view, final RecyclerView recyclerView,
            int itemCount) {
        // Find the empty view from the main view - it's using the android id for it
        final View emptyView = view.findViewById(android.R.id.empty);

        // If we don't have any threads then simply tell the user this and quit
        if (itemCount == 0) {
            // Toggle what's happening with the view
            showEmptyText(recyclerView, emptyView);

            // TODO - find if there's a better way to do this
            // For now simply show the empty view ...
            emptyView.setVisibility(View.VISIBLE);
            // ... and manually remove the recyler view
            recyclerView.setVisibility(View.GONE);

            return;
        }

        // TODO - find if there's a better way to do this
        // For now simply show the recycler view ...
        recyclerView.setVisibility(View.VISIBLE);
        // ... and manually remove the loading view
        emptyView.setVisibility(View.GONE);
    }

    public static void showEmptyText(final RecyclerView recyclerView, final View emptyView) {
        recyclerView.setVisibility(GONE);
        emptyView.setVisibility(VISIBLE);

        final View textView = emptyView.findViewById(R.id.empty_view_text_view);
        final View progressBar = emptyView.findViewById(R.id.empty_view_progress_bar);

        textView.setVisibility(VISIBLE);
        progressBar.setVisibility(GONE);
    }

    public static void showLoadingProgress(final RecyclerView recyclerView, final View emptyView) {
        recyclerView.setVisibility(GONE);
        emptyView.setVisibility(VISIBLE);

        final View textView = emptyView.findViewById(R.id.empty_view_text_view);
        final View progressBar = emptyView.findViewById(R.id.empty_view_progress_bar);

        textView.setVisibility(GONE);
        progressBar.setVisibility(VISIBLE);
    }

    public static ActionBar getSupportActionBar(final Activity activity) {
        return ((BaseActivity) activity).getSupportActionBar();
    }

    public static BaseActivity getBaseActivity(final Activity activity) {
        return (BaseActivity) activity;
    }
}