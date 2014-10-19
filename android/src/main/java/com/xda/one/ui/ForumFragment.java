package com.xda.one.ui;

import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;
import com.xda.one.R;
import com.xda.one.api.inteface.ForumClient;
import com.xda.one.api.model.interfaces.Forum;
import com.xda.one.api.model.response.ResponseForum;
import com.xda.one.api.retrofit.RetrofitClient;
import com.xda.one.api.retrofit.RetrofitForumClient;
import com.xda.one.auth.XDAAccount;
import com.xda.one.constants.XDAConstants;
import com.xda.one.event.forum.ForumSubscriptionChangedEvent;
import com.xda.one.event.forum.ForumSubscriptionChangingFailedEvent;
import com.xda.one.loader.ForumLoader;
import com.xda.one.model.misc.ForumType;
import com.xda.one.ui.helper.ActionModeHelper;
import com.xda.one.ui.widget.XDARefreshLayout;
import com.xda.one.util.AccountUtils;
import com.xda.one.util.FragmentUtils;
import com.xda.one.util.UIUtils;
import com.xda.one.util.Utils;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.XDALinerLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ForumFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<List<ResponseForum>> {

    public static final String FORUM_TYPE = "forum_type";

    private static final String PARENT_FORUM_TITLE = "parent_title";

    private static final String FORUM_HIERARCHY = "forum_hierarchy";

    private static final String FORUM = "forum";

    private final EventHandler mEventHandler = new EventHandler();

    private List<String> mHierarchy;

    private String mForumTitle;

    private String mParentForumTitle;

    private ForumType mForumType = ForumType.ALL;

    // Adapter for ListView
    private ForumAdapter<Forum> mAdapter;

    private XDARefreshLayout mRefreshLayout;

    private RecyclerView mRecyclerView;

    private ActionModeHelper mModeHelper;

    private Forum mForum;

    private ForumClient mClient;

    public static ForumFragment createInstance(final ForumType forumType) {
        final Bundle bundle = new Bundle();
        bundle.putSerializable(FORUM_TYPE, forumType);

        final ForumFragment fragment = new ForumFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    public static ForumFragment createInstance(final Forum forum, final String parentTitle,
            final ArrayList<String> hierarchy) {
        final Bundle bundle = new Bundle();
        bundle.putSerializable(FORUM_TYPE, ForumType.CHILD);
        bundle.putParcelable(FORUM, forum);
        bundle.putString(PARENT_FORUM_TITLE, parentTitle);
        bundle.putStringArrayList(FORUM_HIERARCHY, hierarchy);

        final ForumFragment fragment = new ForumFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mClient = RetrofitForumClient.getClient(getActivity());

        mModeHelper = new ActionModeHelper(getActivity(),
                new ForumFragmentActionMode(),
                new View.OnClickListener() {
                    @Override
                    public void onClick(final View view) {
                        final int position = mRecyclerView.getChildPosition(view);
                        if (position == RecyclerView.NO_POSITION) {
                            return;
                        }
                        final Forum responseForum = mAdapter.getForum(position);
                        onListItemClicked(responseForum);
                    }
                },
                ActionModeHelper.SelectionMode.SINGLE);

        mForumType = (ForumType) getArguments().getSerializable(FORUM_TYPE);
        if (mForumType == ForumType.CHILD) {
            mForum = getArguments().getParcelable(FORUM);
            mForumTitle = mForum.getTitle();
            mParentForumTitle = getArguments().getString(PARENT_FORUM_TITLE);
            mHierarchy = getArguments().getStringArrayList(FORUM_HIERARCHY);
        } else {
            mForumTitle = getString(mForumType.getStringTitleId());
            mHierarchy = Collections.emptyList();
        }

        mAdapter = new ForumAdapter<>(getActivity(), mModeHelper, mModeHelper, mModeHelper,
                new ForumAdapter.ImageViewDeviceDelegate() {
                    @Override
                    public void setupImageViewDevice(ImageView imageView, Forum forum) {
                        onSetupImageViewListItem(imageView, forum);
                    }
                },
                new ForumAdapter.SubscribeButtonDelegate() {
                    @Override
                    public void setupSubscribeButton(ImageView subscribeButton,
                            final Forum forum) {
                        // Subscribe button
                        onSetupSubscribeButton(subscribeButton, forum);
                    }
                }
        );
    }

    private void onSetupSubscribeButton(final ImageView subscribeButton, final Forum forum) {
        final XDAAccount selectedAccount = AccountUtils.getAccount(getActivity());
        if (forum.hasChildren() || selectedAccount == null) {
            subscribeButton.setVisibility(View.GONE);
        } else {
            subscribeButton.setVisibility(View.VISIBLE);
            subscribeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    onSubscribeToggleRequested(forum);
                }
            });
            subscribeButton.setImageResource(forum.isSubscribed()
                    ? R.drawable.ic_star_light
                    : R.drawable.ic_star_outline_light);
        }
    }

    private void onSetupImageViewListItem(final ImageView imageView, final Forum responseForum) {
        Picasso.with(getActivity())
                .load(responseForum.getImageUrl())
                .placeholder(R.drawable.phone)
                .error(R.drawable.phone)
                .into(imageView);
    }

    private void onListItemClicked(final Forum forum) {
        FragmentUtils.switchToForumContent(getFragmentManager(), getParentFragment(), mHierarchy,
                mForumTitle, forum);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
            final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.forum_fragment, container, false);
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRecyclerView = (RecyclerView) view.findViewById(android.R.id.list);
        mRecyclerView.setLayoutManager(new XDALinerLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mAdapter);
        ViewCompat.setOverScrollMode(mRecyclerView, ViewCompat.OVER_SCROLL_NEVER);

        mRefreshLayout = (XDARefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        mRefreshLayout.setXDAColourScheme();
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                final Bundle bundle = new Bundle();
                bundle.putBoolean(RetrofitClient.FORCE_RELOAD, true);
                getLoaderManager().restartLoader(0, bundle, ForumFragment.this);
            }
        });

        mClient.getBus().register(mEventHandler);

        mModeHelper.setRecyclerView(mRecyclerView);

        final ActionBar actionBar = UIUtils.getSupportActionBar(getActivity());
        actionBar.show();
        actionBar.setTitle(mForumTitle);
        actionBar.setSubtitle(mParentForumTitle);

        if (mAdapter.getItemCount() == 0) {
            getLoaderManager().initLoader(0, null, this);
            mRefreshLayout.setRefreshing(true);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        mClient.getBus().unregister(mEventHandler);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mAdapter = null;
    }

    @Override
    public Loader<List<ResponseForum>> onCreateLoader(final int id, final Bundle bundle) {
        boolean forceReload = bundle != null && bundle.getBoolean(RetrofitClient.FORCE_RELOAD,
                false);
        return new ForumLoader(getActivity(), mForumType, mForum, forceReload);
    }

    @Override
    public void onLoadFinished(final Loader<List<ResponseForum>> loader,
            final List<ResponseForum> responseForums) {
        // Remove the old data if the adapter is not empty
        if (mAdapter.getItemCount() != 0) {
            mAdapter.clear();
        }

        mRefreshLayout.setRefreshing(false);
        UIUtils.updateEmptyViewState(getView(), mRecyclerView,
                Utils.getCollectionSize(responseForums));
        mAdapter.addAll(responseForums);
    }

    @Override
    public void onLoaderReset(final Loader<List<ResponseForum>> loader) {
    }

    public ForumType getForumType() {
        return mForumType;
    }

    private void onSubscribeToggleRequested(final Forum forum) {
        mClient.toggleForumSubscriptionAsync(forum);
    }

    private class ForumFragmentActionMode
            extends ActionModeHelper.RecyclerViewActionModeCallback {

        private ShareActionProvider mShareActionProvider;

        private MenuItem mShareMenuItem;

        private MenuItem mSubscribeItem;

        @Override
        public boolean onCreateActionMode(final ActionMode actionMode, final Menu menu) {
            actionMode.getMenuInflater().inflate(R.menu.forum_fragment_cab, menu);

            // Locate MenuItem with ShareActionProvider
            mShareMenuItem = menu.findItem(R.id.forum_fragment_cab_share);

            // Fetch and store ShareActionProvider
            mShareActionProvider = (ShareActionProvider) MenuItemCompat
                    .getActionProvider(mShareMenuItem);

            // Get the subscribed menu item
            mSubscribeItem = menu.findItem(R.id.forum_fragment_cab_subscribe);

            return true;
        }

        @Override
        public boolean onPrepareActionMode(final ActionMode actionMode, final Menu menu) {
            if (mModeHelper.getCheckedItemCount() == 1) {
                updateShareIntent();

                final Forum forum = getCheckedForum();
                final XDAAccount selectedAccount = AccountUtils.getAccount(getActivity());
                final boolean visible = !(forum.hasChildren() || selectedAccount == null);
                mSubscribeItem.setVisible(visible);

                if (visible) {
                    final boolean subscribed = forum.isSubscribed();
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
            sendIntent.putExtra(Intent.EXTRA_SUBJECT, getCheckedForum().getTitle());
            sendIntent.putExtra(Intent.EXTRA_TEXT, XDAConstants.XDA_FORUM_URL +
                    getCheckedForum().getWebUri());
            sendIntent.setType("text/plain");
            mShareActionProvider.setShareIntent(sendIntent);
        }

        public Forum getCheckedForum() {
            // TODO - fix this ugly hack
            return mAdapter.getForum(mModeHelper.getCheckedPositions().get(0));
        }

        @Override
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.forum_fragment_cab_subscribe:
                    mClient.toggleForumSubscriptionAsync(getCheckedForum());
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
    }

    private final class EventHandler {

        @Subscribe
        public void onForumSubscribed(final ForumSubscriptionChangedEvent event) {
            // TODO - show a snackbar
            if (event.isNowSubscribed) {
                Toast.makeText(getActivity(), R.string.forum_subscription_subscribed,
                        Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getActivity(), R.string.forum_subscription_unsubscribed,
                        Toast.LENGTH_LONG).show();
            }

            // We would need to update the state of the subscribe button now
            final int position = mAdapter.indexOf(event.forum);
            final Forum forum = mAdapter.getForum(position);
            forum.setSubscribed(event.isNowSubscribed);
            mAdapter.notifyItemChanged(position);
        }

        @Subscribe
        public void onForumSubscribingFailed(final ForumSubscriptionChangingFailedEvent event) {
            // TODO - show a snackbar
            Toast.makeText(getActivity(), R.string.forum_subscription_toggle_failed,
                    Toast.LENGTH_LONG).show();
        }
    }
}
