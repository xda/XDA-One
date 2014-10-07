package com.xda.one.loader;

import com.xda.one.db.ForumDbHelper;

import android.content.Context;
import android.database.Cursor;

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
