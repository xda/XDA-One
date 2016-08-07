package com.xda.one.ui.thread;

import android.app.ProgressDialog;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.xda.one.model.augmented.AugmentedUnifiedThread;

import java.util.List;

public class UnreadThreadClickStrategy implements ThreadClickStrategy {

    public static final Parcelable.Creator<UnreadThreadClickStrategy> CREATOR
            = new Parcelable.Creator<UnreadThreadClickStrategy>() {
        public UnreadThreadClickStrategy createFromParcel(Parcel in) {
            return new UnreadThreadClickStrategy();
        }

        public UnreadThreadClickStrategy[] newArray(int size) {
            return new UnreadThreadClickStrategy[size];
        }
    };

    @Override
    public void onClick(final Fragment threadFragment, final List<String> hierarchy,
                        final AugmentedUnifiedThread thread) {
        final ProgressDialog progressDialog = ProgressDialog.show(threadFragment.getActivity(),
                "Finding post position", "Finding position of most recently read post",
                true, true);

        final Fragment parentFragment = threadFragment.getParentFragment();
        final FragmentManager fragmentManager = parentFragment == null
                ? threadFragment.getFragmentManager()
                : parentFragment.getFragmentManager();

        final ThreadUnreadPostHelper postHelper = new ThreadUnreadPostHelper(threadFragment,
                fragmentManager, thread, progressDialog);
        postHelper.start();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
    }
}