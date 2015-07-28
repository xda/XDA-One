package com.xda.one.ui.thread;

import android.os.Parcelable;
import android.support.v4.app.Fragment;

import com.xda.one.model.augmented.AugmentedUnifiedThread;

import java.util.List;

public interface ThreadClickStrategy extends Parcelable {

    void onClick(final Fragment fragment, final List<String> hierarchy,
                 final AugmentedUnifiedThread unifiedThread);
}