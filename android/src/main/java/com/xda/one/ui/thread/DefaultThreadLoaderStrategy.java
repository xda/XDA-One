package com.xda.one.ui.thread;

import com.xda.one.loader.ThreadLoader;
import com.xda.one.model.augmented.container.AugmentedUnifiedThreadContainer;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.content.Loader;

public class DefaultThreadLoaderStrategy implements ThreadLoaderStrategy {

    public static final Parcelable.Creator<DefaultThreadLoaderStrategy> CREATOR
            = new Parcelable.Creator<DefaultThreadLoaderStrategy>() {
        public DefaultThreadLoaderStrategy createFromParcel(Parcel in) {
            return new DefaultThreadLoaderStrategy();
        }

        public DefaultThreadLoaderStrategy[] newArray(int size) {
            return new DefaultThreadLoaderStrategy[size];
        }
    };

    @Override
    public Loader<AugmentedUnifiedThreadContainer> createLoader(final Context context,
            final int forumId, final int currentPage) {
        return new ThreadLoader(context, forumId, currentPage);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {

    }
}