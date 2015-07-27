package com.xda.one.ui.thread;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.xda.one.R;
import com.xda.one.model.augmented.AugmentedUnifiedThread;
import com.xda.one.util.FragmentUtils;

import java.util.ArrayList;
import java.util.List;

public class FirstThreadClickStrategy implements ThreadClickStrategy {

    public static final Parcelable.Creator<FirstThreadClickStrategy> CREATOR
            = new Parcelable.Creator<FirstThreadClickStrategy>() {
        public FirstThreadClickStrategy createFromParcel(Parcel in) {
            return new FirstThreadClickStrategy();
        }

        public FirstThreadClickStrategy[] newArray(int size) {
            return new FirstThreadClickStrategy[size];
        }
    };

    @Override
    public void onClick(final Fragment threadFragment, final List<String> hierarchy,
                        final AugmentedUnifiedThread thread) {
        final ArrayList<String> copyOfHierarchy = new ArrayList<>(hierarchy);
        final Fragment fragment = FragmentUtils.switchToPostList(thread, copyOfHierarchy);
        fragment.setTargetFragment(threadFragment, 101);

        final FragmentTransaction transaction = FragmentUtils
                .getDefaultTransaction(threadFragment.getFragmentManager());
        transaction.addToBackStack(thread.getTitle());
        transaction.replace(R.id.content_frame, fragment).commit();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }
}