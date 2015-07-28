package com.xda.one.ui.thread;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.content.Loader;

import com.xda.one.loader.ParticipatedThreadLoader;
import com.xda.one.model.augmented.container.AugmentedUnifiedThreadContainer;

public class ParticipatedThreadLoaderStrategy implements ThreadLoaderStrategy {

    public static final Parcelable.Creator<ParticipatedThreadLoaderStrategy> CREATOR
            = new Parcelable.Creator<ParticipatedThreadLoaderStrategy>() {
        public ParticipatedThreadLoaderStrategy createFromParcel(Parcel in) {
            return new ParticipatedThreadLoaderStrategy();
        }

        public ParticipatedThreadLoaderStrategy[] newArray(int size) {
            return new ParticipatedThreadLoaderStrategy[size];
        }
    };

    @Override
    public Loader<AugmentedUnifiedThreadContainer> createLoader(final Context context,
                                                                final int forumId, final int currentPage) {
        return new ParticipatedThreadLoader(context, currentPage);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {

    }
}
