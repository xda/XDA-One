package com.xda.one.db;

import android.app.SearchManager;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.provider.BaseColumns;
import android.text.TextUtils;

import com.xda.one.api.model.interfaces.Forum;
import com.xda.one.api.model.response.ResponseForum;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.xda.one.db.ForumDbHelper.ForumContract.ForumEntry;

public class ForumDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 3;

    public static final String DATABASE_NAME = "Forums.db";

    public static final String TABLE_NAME = "forumtable";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TABLE_NAME;

    private static final String TEXT_TYPE = " TEXT";

    private static final String INTEGER_TYPE = " INTEGER";

    private static final String COMMA_SEP = ",";

    private static final String UNIQUE = " UNIQUE";

    private static final String SQL_CREATE_FORUM_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    ForumEntry._ID + INTEGER_TYPE + " PRIMARY KEY" + COMMA_SEP +
                    ForumEntry.COLUMN_NAME_TITLE + TEXT_TYPE + COMMA_SEP +
                    ForumEntry.COLUMN_NAME_CONTENT + TEXT_TYPE + COMMA_SEP +
                    ForumEntry.COLUMN_NAME_FORUMID + INTEGER_TYPE + UNIQUE + COMMA_SEP +
                    ForumEntry.COLUMN_NAME_PARENTID + INTEGER_TYPE + COMMA_SEP +
                    ForumEntry.COLUMN_NAME_FORUMSLUG + TEXT_TYPE + COMMA_SEP +
                    ForumEntry.COLUMN_NAME_SUBSCRIBED + INTEGER_TYPE + COMMA_SEP +
                    ForumEntry.COLUMN_NAME_IMAGE + TEXT_TYPE + COMMA_SEP +
                    ForumEntry.COLUMN_NAME_SEARCHABLE + TEXT_TYPE + COMMA_SEP +
                    ForumEntry.COLUMN_NAME_CAN_CONTAIN_THREADS + INTEGER_TYPE + COMMA_SEP +
                    ForumEntry.COLUMN_NAME_WEB_URI + TEXT_TYPE + COMMA_SEP +
                    ForumEntry.COLUMN_NAME_CHILDREN_COUNT + INTEGER_TYPE +
                    " )";

    private static ForumDbHelper sForumDbHelper;

    private ForumDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static ForumDbHelper getInstance(Context context) {
        if (sForumDbHelper == null) {
            sForumDbHelper = new ForumDbHelper(context);
        }
        return sForumDbHelper;
    }

    private static ContentValues forumToContentValues(final ResponseForum forum) {
        final ContentValues values = new ContentValues();
        values.put(ForumEntry.COLUMN_NAME_TITLE, forum.getTitle());
        values.put(ForumEntry.COLUMN_NAME_FORUMID, forum.getForumId());
        values.put(ForumEntry.COLUMN_NAME_PARENTID, forum.getParentId());
        values.put(ForumEntry.COLUMN_NAME_FORUMSLUG, forum.getForumSlug());
        values.put(ForumEntry.COLUMN_NAME_SUBSCRIBED, forum.isSubscribed());
        values.put(ForumEntry.COLUMN_NAME_IMAGE, forum.getImageUrl());
        values.put(ForumEntry.COLUMN_NAME_SEARCHABLE, forum.getSearchable());
        values.put(ForumEntry.COLUMN_NAME_CAN_CONTAIN_THREADS, forum.canContainThreads());
        values.put(ForumEntry.COLUMN_NAME_WEB_URI, forum.getWebUri());
        values.put(ForumEntry.COLUMN_NAME_CHILDREN_COUNT, forum.getChildren().size());
        return values;
    }

    public static ResponseForum getForumFromCursor(final Cursor cursor) {
        return getForumWithTitleFromCursor(cursor, ForumEntry.COLUMN_NAME_TITLE);
    }

    public static ResponseForum getSuggestionFromCursor(final Cursor cursor) {
        return getForumWithTitleFromCursor(cursor, SearchManager.SUGGEST_COLUMN_TEXT_1);
    }

    public static ResponseForum getForumWithTitleFromCursor(final Cursor cursor,
                                                            final String titleColumn) {
        final ResponseForum forum = new ResponseForum();
        forum.setTitle(cursor.getString(cursor.getColumnIndex(titleColumn)));
        forum.setForumId(cursor.getInt(cursor.getColumnIndex(ForumEntry.COLUMN_NAME_FORUMID)));
        forum.setParentId(cursor.getInt(cursor.getColumnIndex(ForumEntry.COLUMN_NAME_PARENTID)));
        forum.setForumSlug(cursor.getString(cursor.getColumnIndex(ForumEntry
                .COLUMN_NAME_FORUMSLUG)));
        forum.setSubscribed(cursor.getInt(cursor.getColumnIndex(ForumEntry
                .COLUMN_NAME_SUBSCRIBED)) != 0);
        forum.setImageUrl(cursor.getString(cursor.getColumnIndex(ForumEntry.COLUMN_NAME_IMAGE)));
        forum.setSearchable(cursor.getString(cursor.getColumnIndex(ForumEntry
                .COLUMN_NAME_SEARCHABLE)));
        forum.setCanContainThreads(cursor.getInt(cursor.getColumnIndex(ForumEntry
                .COLUMN_NAME_CAN_CONTAIN_THREADS)) != 0);
        forum.setWebUri(cursor.getString(cursor.getColumnIndex(ForumEntry.COLUMN_NAME_WEB_URI)));
        forum.setChildrenCount(cursor.getInt(cursor.getColumnIndex(ForumEntry
                .COLUMN_NAME_CHILDREN_COUNT)));
        return forum;
    }

    private static Cursor getForumsChildren(final SQLiteDatabase database,
                                            final int forumId) {
        final String whereClause = ForumEntry.COLUMN_NAME_PARENTID + "=" + String.valueOf(forumId);
        return database.query(ForumDbHelper.TABLE_NAME, null, whereClause, null, null, null, null);
    }

    private static void addAllRecursive(final SQLiteDatabase database,
                                        final Collection<ResponseForum> list) {
        if (list == null) {
            return;
        }
        for (final ResponseForum forum : list) {
            database.insert(TABLE_NAME, null, forumToContentValues(forum));
            addAllRecursive(database, forum.getChildren());
        }
    }

    public static void updateForum(final SQLiteDatabase database, final ResponseForum forum) {
        final ContentValues values = forumToContentValues(forum);
        database.replace(TABLE_NAME, null, values);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_FORUM_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public Cursor searchForums(final String query) {
        if (TextUtils.isEmpty(query)) {
            return null;
        }
        final SQLiteDatabase database = getWritableDatabase();
        final String selection = ForumEntry.COLUMN_NAME_SEARCHABLE + " LIKE '%" + query + "%'";

        final Map<String, String> hashMap = new HashMap<>();
        hashMap.put(ForumEntry._ID, ForumEntry._ID);
        hashMap.put(ForumEntry.COLUMN_NAME_TITLE, ForumEntry.COLUMN_NAME_TITLE + " as " +
                SearchManager.SUGGEST_COLUMN_TEXT_1);
        hashMap.put(ForumEntry.COLUMN_NAME_CONTENT, ForumEntry.COLUMN_NAME_CONTENT);
        hashMap.put(ForumEntry.COLUMN_NAME_FORUMID, ForumEntry.COLUMN_NAME_FORUMID);
        hashMap.put(ForumEntry.COLUMN_NAME_PARENTID, ForumEntry.COLUMN_NAME_PARENTID);
        hashMap.put(ForumEntry.COLUMN_NAME_FORUMSLUG, ForumEntry.COLUMN_NAME_FORUMSLUG);
        hashMap.put(ForumEntry.COLUMN_NAME_SUBSCRIBED, ForumEntry.COLUMN_NAME_SUBSCRIBED);
        hashMap.put(ForumEntry.COLUMN_NAME_IMAGE, ForumEntry.COLUMN_NAME_IMAGE);
        hashMap.put(ForumEntry.COLUMN_NAME_SEARCHABLE, ForumEntry.COLUMN_NAME_SEARCHABLE);
        hashMap.put(ForumEntry.COLUMN_NAME_CAN_CONTAIN_THREADS,
                ForumEntry.COLUMN_NAME_CAN_CONTAIN_THREADS);
        hashMap.put(ForumEntry.COLUMN_NAME_WEB_URI, ForumEntry.COLUMN_NAME_WEB_URI);
        hashMap.put(ForumEntry.COLUMN_NAME_CHILDREN_COUNT, ForumEntry.COLUMN_NAME_CHILDREN_COUNT);

        final SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setProjectionMap(hashMap);
        builder.setTables(TABLE_NAME);

        return builder.query(database, null, selection, null, null, null, null);
    }

    public void addAllRecursive(final Collection<ResponseForum> list) {
        final SQLiteDatabase database = getWritableDatabase();
        database.beginTransaction();

        try {
            addAllRecursive(database, list);
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }
    }

    public void replaceRawForumResponse(final List<ResponseForum> list) {
        final SQLiteDatabase database = getWritableDatabase();
        database.delete(TABLE_NAME, null, null);
        addAllRecursive(list);
    }

    public List<ResponseForum> getTopLevelForums() {
        return getForumChildren(-1);
    }

    public List<ResponseForum> getForumChildren(final int forumId) {
        final SQLiteDatabase database = getWritableDatabase();
        final List<ResponseForum> forums = new ArrayList<>();
        final Cursor cursor = getForumsChildren(database, forumId);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                final ResponseForum forum = getForumFromCursor(cursor);
                forums.add(forum);
                cursor.moveToNext();
            }
        }
        cursor.close();
        return forums;
    }

    public void updateSubscribedFlag(final Forum forum, final boolean newSubscribedValue) {
        final SQLiteDatabase database = getWritableDatabase();

        final ContentValues contentValues = new ContentValues();
        contentValues.put(ForumEntry.COLUMN_NAME_SUBSCRIBED, newSubscribedValue);

        database.update(TABLE_NAME, contentValues, ForumEntry.COLUMN_NAME_FORUMID + "=" + forum
                .getForumId(), null);
    }

    public void updateForumCollection(final Collection<ResponseForum> list) {
        final SQLiteDatabase database = getWritableDatabase();
        database.beginTransaction();
        try {
            for (final ResponseForum responseForum : list) {
                updateForum(database, responseForum);
            }
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }
    }

    public Forum searchSlug(final int parentId, final String slug) {
        final SQLiteDatabase database = getWritableDatabase();
        final String selection = String.format("%s = '%s' AND %s = %d",
                ForumEntry.COLUMN_NAME_FORUMSLUG, slug,
                ForumEntry.COLUMN_NAME_PARENTID, parentId);

        final Cursor cursor = database.query(TABLE_NAME, null, selection, null, null, null, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            return getForumFromCursor(cursor);
        }
        return null;
    }

    public static final class ForumContract {

        // To prevent someone from accidentally instantiating the contract class,
        // give it an empty constructor.
        private ForumContract() {
        }

        /* Inner class that defines the table contents */
        public static abstract class ForumEntry implements BaseColumns {

            public static final String COLUMN_NAME_TITLE = "title";

            public static final String COLUMN_NAME_CONTENT = "content";

            public static final String COLUMN_NAME_FORUMID = "forumid";

            public static final String COLUMN_NAME_PARENTID = "parentid";

            public static final String COLUMN_NAME_FORUMSLUG = "forumslug";

            public static final String COLUMN_NAME_SUBSCRIBED = "subscribed";

            public static final String COLUMN_NAME_IMAGE = "image";

            public static final String COLUMN_NAME_SEARCHABLE = "searchable";

            public static final String COLUMN_NAME_CAN_CONTAIN_THREADS = "can_contain_threads";

            public static final String COLUMN_NAME_WEB_URI = "web_uri";

            public static final String COLUMN_NAME_CHILDREN_COUNT = "children_count";
        }
    }
}