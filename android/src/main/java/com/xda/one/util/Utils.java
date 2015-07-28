package com.xda.one.util;

import android.content.Context;
import android.text.format.DateUtils;
import android.util.Log;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

import retrofit.RetrofitError;
import retrofit.mime.TypedInput;

public class Utils {

    public static String handleRetrofitErrorQuietly(final RetrofitError error) {
        error.printStackTrace();

        InputStream inputStream = null;
        try {
            if (error.isNetworkError()) {
                Log.e("XDA-ONE", "Network error happened.");
            } else {
                final TypedInput body = error.getResponse().getBody();
                if (body == null) {
                    Log.e("XDA-ONE", "Unable to retrieve body");
                    return null;
                }
                inputStream = body.in();

                final String result = IOUtils.toString(inputStream);
                Log.e("XDA-ONE", result);
                return result;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
        return null;
    }

    public static <T> int getCollectionSize(final Collection<T> collection) {
        return collection == null ? 0 : collection.size();
    }

    public static boolean isCollectionEmpty(final Collection collection) {
        return collection == null || collection.isEmpty();
    }

    public static CharSequence getRelativeDate(final Context context, final long dateline) {
        return DateUtils.getRelativeDateTimeString(context, dateline,
                DateUtils.SECOND_IN_MILLIS, DateUtils.WEEK_IN_MILLIS,
                DateUtils.FORMAT_NUMERIC_DATE);
    }
}