package com.xda.one.ui.helper;

import com.squareup.otto.Subscribe;
import com.xda.one.R;
import com.xda.one.event.thread.ThreadSubscriptionChangedEvent;
import com.xda.one.event.thread.ThreadSubscriptionChangingFailedEvent;
import com.xda.one.model.augmented.AugmentedUnifiedThread;
import com.xda.one.ui.UnifiedThreadAdapter;

import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

public class ThreadEventHelper {

    private final FragmentActivity mActivity;

    private UnifiedThreadAdapter mAdapter;

    public ThreadEventHelper(final FragmentActivity activity, final UnifiedThreadAdapter adapter) {
        mActivity = activity;
        mAdapter = adapter;
    }

    @Subscribe
    public void onThreadSubscriptionToggled(final ThreadSubscriptionChangedEvent event) {
        final AugmentedUnifiedThread thread = (AugmentedUnifiedThread) event.thread;
        mActivity.supportInvalidateOptionsMenu();

        if (event.isNowSubscribed) {
            Toast.makeText(mActivity, R.string.thread_subscription_subscribed,
                    Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(mActivity, R.string.thread_subscription_unsubscribed,
                    Toast.LENGTH_LONG).show();
        }

        // We would need to update the state of the subscribe button now
        final int position = mAdapter.indexOf(thread);
        final AugmentedUnifiedThread actualThread = mAdapter.getThread(position);
        actualThread.setSubscribedFlag(event.isNowSubscribed);
        mAdapter.notifyItemChanged(position);
    }

    @Subscribe
    public void onThreadSubscriptionToggleFailed(final ThreadSubscriptionChangingFailedEvent
            event) {
        Toast.makeText(mActivity, R.string.thread_subscription_toggle_failed,
                Toast.LENGTH_LONG).show();
    }
}
