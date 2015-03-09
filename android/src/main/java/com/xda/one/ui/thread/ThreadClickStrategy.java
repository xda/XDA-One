package com.xda.one.ui.thread;

import com.xda.one.model.augmented.AugmentedUnifiedThread;

import android.os.Parcelable;
import android.support.v4.app.Fragment;

import java.util.List;

public interface ThreadClickStrategy extends Parcelable {

    void onClick(final Fragment fragment, final List<String> hierarchy,
            final AugmentedUnifiedThread unifiedThread);
}