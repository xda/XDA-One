package com.xda.one.ui.thread;

import com.xda.one.loader.SubscribedThreadLoader;
import com.xda.one.model.augmented.container.AugmentedUnifiedThreadContainer;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.content.Loader;

public class SubscribedThreadLoaderStrategy implements ThreadLoaderStrategy {

    public static final Parcelable.Creator<SubscribedThreadLoaderStrategy> CREATOR
            = new Parcelable.Creator<SubscribedThreadLoaderStrategy>() {
        public SubscribedThreadLoaderStrategy createFromParcel(Parcel in) {
            return new SubscribedThreadLoaderStrategy();
        }

        public SubscribedThreadLoaderStrategy[] newArray(int size) {
            return new SubscribedThreadLoaderStrategy[size];
        }
    };

    @Override
    public Loader<AugmentedUnifiedThreadContainer> createLoader(final Context context,
            final int forumId, final int currentPage) {
        return new SubscribedThreadLoader(context, currentPage);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {

    }
}