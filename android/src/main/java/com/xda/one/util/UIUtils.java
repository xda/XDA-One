package com.xda.one.util;

import com.xda.one.R;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class UIUtils {

    private static final int[] RES_IDS_ACTION_BAR_SIZE = {android.R.attr.actionBarSize};

    public static int calculateActionBarSize(final Context context) {
        if (context == null) {
            return 0;
        }

        Resources.Theme curTheme = context.getTheme();
        if (curTheme == null) {
            return 0;
        }

        TypedArray att = curTheme.obtainStyledAttributes(RES_IDS_ACTION_BAR_SIZE);
        if (att == null) {
            return 0;
        }

        float size = att.getDimension(0, 0);
        att.recycle();
        return (int) size;
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
}