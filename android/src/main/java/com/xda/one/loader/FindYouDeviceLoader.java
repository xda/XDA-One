package com.xda.one.loader;

import android.content.Context;
import android.database.Cursor;

import com.xda.one.db.ForumDbHelper;

public class FindYouDeviceLoader extends AsyncLoader<Cursor> {

    private final ForumDbHelper mForumDbHelper;

    private final String mQuery;

    public FindYouDeviceLoader(final Context context, final String query) {
        super(context);

        mForumDbHelper = ForumDbHelper.getInstance(context);
        mQuery = query;
    }

    @Override
    public void releaseResources(final Cursor cursor) {
    }

    @Override
    public Cursor loadInBackground() {
        return mForumDbHelper.searchForums(mQuery);
    }
}
