package com.xda.one.ui.thread;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.content.Loader;

import com.xda.one.model.augmented.container.AugmentedUnifiedThreadContainer;

public interface ThreadLoaderStrategy extends Parcelable {

    Loader<AugmentedUnifiedThreadContainer> createLoader(final Context context,
                                                         final int forumId, final int currentPage);
}