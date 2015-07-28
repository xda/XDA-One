package com.xda.one.search;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import com.xda.one.db.ForumDbHelper;

import org.apache.commons.lang3.NotImplementedException;

public class SearchContentProvider extends ContentProvider {

    private ForumDbHelper mForumDbHelper;

    public SearchContentProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        throw new NotImplementedException("Not yet implemented");
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new NotImplementedException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // TODO: Implement this to handle requests to insert a new row.
        throw new NotImplementedException("Not yet implemented");
    }

    @Override
    public boolean onCreate() {
        mForumDbHelper = ForumDbHelper.getInstance(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        final String query = uri.getLastPathSegment().toLowerCase();
        return mForumDbHelper.searchForums(query);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new NotImplementedException("Not yet implemented");
    }
}
