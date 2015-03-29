package com.xda.one.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.otto.Subscribe;
import com.xda.one.R;
import com.xda.one.api.inteface.ForumClient;
import com.xda.one.api.model.interfaces.Forum;
import com.xda.one.api.model.response.ResponseForum;
import com.xda.one.api.retrofit.RetrofitForumClient;
import com.xda.one.event.forum.ForumSubscriptionChangedEvent;
import com.xda.one.event.forum.ForumSubscriptionChangingFailedEvent;
import com.xda.one.loader.SubscribedForumLoader;
import com.xda.one.ui.widget.XDARefreshLayout;

import java.util.ArrayList;
import java.util.List;

import static com.xda.one.util.UIUtils.updateEmptyViewState;

public class SubscribedForumFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<List<ResponseForum>> {

    private static final int FORUM_LOADER = 2;

    private final EventHandler mEventHandler = new EventHandler();

    private Callback mCallback;

    private RecyclerView mRecyclerView;

    private ForumAdapter<Forum> mAdapter;

    private XDARefreshLayout mRefreshLayout;

    private ForumClient mForumClient;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mCallback = (Callback) getParentFragment();
        } catch (ClassCastException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mForumClient = RetrofitForumClient.getClient(getActivity());
        mAdapter = new ForumAdapter<>(getActivity(), new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                final int position = mRecyclerView.getChildPosition(view);
                switchToThreadFragment(position);
            }
        }, null, null, new ForumAdapter.ImageViewDeviceDelegate() {
            @Override
            public void setupImageViewDevice(ImageView imageView, Forum forum) {
                // Simply do nothing - we don't need to load images here
            }
        }, new ForumAdapter.SubscribeButtonDelegate() {
            @Override
            public void setupSubscribeButton(final ImageView subscribeButton,
                    final Forum forum) {
                subscribeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View view) {
                        unsubscribeRequested(forum);
                        updateEmptyViewState(getView(), mRecyclerView, mAdapter.getItemCount());
                    }
                });
            }
        });
    }

    private void unsubscribeRequested(final Forum forum) {
        mForumClient.unsubscribeAsync(forum);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
            final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.subscribed_forum_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mForumClient.getBus().register(mEventHandler);

        mRefreshLayout = (XDARefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        mRefreshLayout.setXDAColourScheme();
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getLoaderManager().restartLoader(0, null, SubscribedForumFragment.this);
            }
        });

        mRecyclerView = (RecyclerView) view.findViewById(android.R.id.list);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        ViewCompat.setOverScrollMode(mRecyclerView, ViewCompat.OVER_SCROLL_NEVER);

        if (mAdapter.getItemCount() == 0) {
            mRefreshLayout.setRefreshing(true);
            getLoaderManager().initLoader(FORUM_LOADER, /* bundle */ null, this);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        mForumClient.getBus().unregister(mEventHandler);
    }

    private void switchToThreadFragment(final int position) {
        final Forum forum = mAdapter.getForum(position);

        final ArrayList<String> hierarchy = new ArrayList<>();
        hierarchy.add(forum.getTitle());

        final Fragment fragment = ThreadFragment.createDefault(forum.getForumId(),
                forum.getTitle(), /* parentForum */ null, hierarchy);
        mCallback.switchCurrentlyDisplayedFragment(fragment, true, forum.getTitle());
    }

    @Override
    public Loader<List<ResponseForum>> onCreateLoader(int id, Bundle args) {
        return new SubscribedForumLoader(getActivity());
    }

    @Override
    public void onLoadFinished(final Loader<List<ResponseForum>> loader,
            final List<ResponseForum> list) {
        // Remove the list if the adapter is not empty
        if (!mAdapter.isEmpty()) {
            mAdapter.clear();
        }

        mRefreshLayout.setRefreshing(false);
        updateEmptyViewState(getView(), mRecyclerView, list == null ? 0 : list.size());
        mAdapter.addAll(list);
    }

    @Override
    public void onLoaderReset(Loader<List<ResponseForum>> loader) {
    }

    public interface Callback {

        public void switchCurrentlyDisplayedFragment(final Fragment fragment,
                final boolean backStackAndAnimate, final String backstackTitle);
    }

    public class EventHandler {

        @Subscribe
        public void onUnsubscribed(final ForumSubscriptionChangedEvent event) {
            Toast.makeText(getActivity(), R.string.forum_subscription_unsubscribed,
                    Toast.LENGTH_LONG).show();

            final int position = mAdapter.indexOf(event.forum);
            mAdapter.remove(position);
        }

        @Subscribe
        public void onForumSubscribingFailed(final ForumSubscriptionChangingFailedEvent event) {
            // TODO - show a snackbar
            Toast.makeText(getActivity(), R.string.forum_subscription_toggle_failed,
                    Toast.LENGTH_LONG).show();
        }
    }
}