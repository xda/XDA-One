package com.xda.one.ui.thread;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.ShareActionProvider;
import android.view.Menu;
import android.view.MenuItem;

import com.xda.one.R;
import com.xda.one.api.inteface.ThreadClient;
import com.xda.one.constants.XDAConstants;
import com.xda.one.model.augmented.AugmentedUnifiedThread;
import com.xda.one.ui.BaseActivity;
import com.xda.one.ui.ThreadAdapter;
import com.xda.one.ui.helper.ActionModeHelper;
import com.xda.one.util.AccountUtils;
import com.xda.one.util.UIUtils;

public class ThreadActionModeHelper extends ActionModeHelper.RecyclerViewActionModeCallback {

    private final Activity mActivity;

    private final ThreadClient mThreadClient;

    private ActionModeHelper mModeHelper;

    private ThreadAdapter mAdapter;

    private ShareActionProvider mShareActionProvider;

    private MenuItem mSubscribeItem;

    public ThreadActionModeHelper(final Activity activity,
                                  final ThreadClient threadClient) {
        mActivity = activity;
        mThreadClient = threadClient;
    }

    public void setAdapter(final ThreadAdapter adapter) {
        mAdapter = adapter;
    }

    @Override
    public boolean onCreateActionMode(final ActionMode actionMode, final Menu menu) {
        final BaseActivity baseActivity = UIUtils.getBaseActivity(mActivity);
        baseActivity.getMenuInflater().inflate(R.menu.thread_fragment_cab, menu);

        final MenuItem shareMenuItem = menu.findItem(R.id.thread_fragment_cab_share);

        // Fetch and store ShareActionProvider
        mShareActionProvider = (ShareActionProvider) MenuItemCompat
                .getActionProvider(shareMenuItem);

        // Get the subscribed menu item
        mSubscribeItem = menu.findItem(R.id.thread_fragment_cab_subscribe);

        return true;
    }

    @Override
    public boolean onPrepareActionMode(final ActionMode actionMode, final Menu menu) {
        if (mModeHelper.getCheckedItemCount() == 1) {
            updateShareIntent();

            final boolean visible = AccountUtils.isAccountAvailable(mActivity);
            mSubscribeItem.setVisible(visible);
            if (visible) {
                final boolean subscribed = getCheckedThread().isSubscribed();
                mSubscribeItem.setIcon(subscribed
                        ? R.drawable.ic_action_star
                        : R.drawable.ic_action_star_outline);
            }
        }
        return true;
    }

    private void updateShareIntent() {
        final Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, getCheckedThread().getTitle());
        sendIntent.putExtra(Intent.EXTRA_TEXT, XDAConstants.XDA_FORUM_URL +
                getCheckedThread().getWebUri());
        sendIntent.setType("text/plain");
        mShareActionProvider.setShareIntent(sendIntent);
    }

    public AugmentedUnifiedThread getCheckedThread() {
        return mAdapter.getThread(mModeHelper.getCheckedPositions().get(0));
    }

    @Override
    public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.thread_fragment_cab_subscribe:
                mThreadClient.toggleSubscribeAsync(getCheckedThread());
                break;
        }
        actionMode.finish();
        return true;
    }

    @Override
    public void onCheckedStateChanged(final ActionMode actionMode, final int position,
                                      final boolean isNowChecked) {
        actionMode.invalidate();
    }

    public void setModeHelper(final ActionModeHelper modeHelper) {
        mModeHelper = modeHelper;
    }
}
