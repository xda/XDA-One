package com.xda.one.ui.thread;

import com.xda.one.model.augmented.container.AugmentedUnifiedThreadContainer;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.content.Loader;

public interface ThreadLoaderStrategy extends Parcelable {

    Loader<AugmentedUnifiedThreadContainer> createLoader(final Context context,
            final int forumId, final int currentPage);
}