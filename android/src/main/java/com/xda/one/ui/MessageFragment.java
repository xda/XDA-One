package com.xda.one.ui;

import com.squareup.otto.Subscribe;
import com.xda.one.R;
import com.xda.one.api.inteface.PrivateMessageClient;
import com.xda.one.api.model.interfaces.Message;
import com.xda.one.api.retrofit.RetrofitPrivateMessageClient;
import com.xda.one.event.message.MessageDeletedEvent;
import com.xda.one.event.message.MessageSentEvent;
import com.xda.one.event.message.MessageStatusToggledEvent;
import com.xda.one.loader.MessageLoader;
import com.xda.one.model.augmented.AugmentedMessage;
import com.xda.one.model.augmented.container.AugmentedMessageContainer;
import com.xda.one.ui.listener.AvatarClickListener;
import com.xda.one.ui.listener.InfiniteRecyclerLoadHelper;
import com.xda.one.ui.widget.FloatingActionButton;
import com.xda.one.ui.widget.XDARefreshLayout;
import com.xda.one.util.UIUtils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.XDALinerLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static com.xda.one.ui.ViewMessageFragment.ViewMessageResult;

public class MessageFragment extends Fragment implements LoaderManager
        .LoaderCallbacks<AugmentedMessageContainer> {

    private static final String CURRENT_PAGE_LOADER_ARGUMENT = "current_page";

    private static final String MESSAGE_FRAGMENT_TYPE = "message_fragment_type";

    private static final String MESSAGES_SAVED_STATE = "messages_saved_state";

    private static final int VIEW_MESSAGE_REQUEST_CODE = 100;

    private final Object mEventHandler = new Object() {
        @Subscribe
        public void onMessageDeleted(final MessageDeletedEvent event) {
            Toast.makeText(getActivity(), R.string.message_delete_successful, Toast.LENGTH_LONG)
                    .show();
            mAdapter.remove(event.message);
        }

        @Subscribe
        public void onMessageSent(final MessageSentEvent event) {
            loadTheFirstPage();
        }

        @Subscribe
        public void onMessageStatusToggled(final MessageStatusToggledEvent event) {
            mRefreshLayout.setRefreshing(true);
            mAdapter.update(event.message);
        }
    };

    private RecyclerView mRecyclerView;

    private MessageAdapter mAdapter;

    private MessagePagerFragment.MessageContainerType mMessageContainerType;

    // Infinite scrolling
    private InfiniteRecyclerLoadHelper mInfiniteScrollListener;

    private XDARefreshLayout mRefreshLayout;

    private PrivateMessageClient mPrivateMessageClient;

    private View mLoadMoreProgressContainer;

    public static MessageFragment getInstance(
            final MessagePagerFragment.MessageContainerType type) {
        final Bundle bundle = new Bundle();
        bundle.putSerializable(MESSAGE_FRAGMENT_TYPE, type);

        final MessageFragment fragment = new MessageFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPrivateMessageClient = RetrofitPrivateMessageClient.getClient(getActivity());

        // Get the arguments
        mMessageContainerType = (MessagePagerFragment.MessageContainerType) getArguments()
                .getSerializable(MESSAGE_FRAGMENT_TYPE);

        // Create the adapter
        mAdapter = new MessageAdapter(getActivity(), new MessageClickListener(),
                new AvatarClickListener(getActivity()));
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
            final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.message_fragment, container, false);
    }

    @Override
    public void onViewCreated(final View view, final Bundle bundle) {
        super.onViewCreated(view, bundle);

        final FloatingActionButton loadMoreBackground = (FloatingActionButton) view
                .findViewById(R.id.load_more_progress_bar_background);
        loadMoreBackground.setBackgroundColor(Color.WHITE);
        mLoadMoreProgressContainer = view.findViewById(R.id.load_more_progress_container);

        mRefreshLayout = (XDARefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        mRefreshLayout.setXDAColourScheme();
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                UIUtils.updateEmptyViewState(getView(), mRecyclerView, mAdapter.getItemCount());
                loadTheFirstPage();
            }
        });

        mRecyclerView = (RecyclerView) view.findViewById(android.R.id.list);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new XDALinerLayoutManager(getActivity()));
        ViewCompat.setOverScrollMode(mRecyclerView, ViewCompat.OVER_SCROLL_NEVER);

        // If the listener already exists then tell it about the new recycler view
        if (mInfiniteScrollListener != null) {
            mInfiniteScrollListener.updateRecyclerView(mRecyclerView);
        }

        if (mAdapter.getItemCount() != 0) {
            return;
        }
        if (bundle == null) {
            final Bundle loader = new Bundle();
            loader.putInt(CURRENT_PAGE_LOADER_ARGUMENT, 1);
            getLoaderManager().initLoader(0, loader, this);

            mRefreshLayout.setRefreshing(true);
        } else {
            final ArrayList<Message> threads = bundle.getParcelableArrayList(MESSAGES_SAVED_STATE);
            addDataToAdapter(threads);
        }
    }

    @Override
    public void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);

        final ArrayList<Parcelable> list = new ArrayList<Parcelable>(mAdapter.getMessages());
        outState.putParcelableArrayList(MESSAGES_SAVED_STATE, list);
    }

    @Override
    public void onResume() {
        super.onResume();

        mPrivateMessageClient.getBus().register(mEventHandler);
    }

    @Override
    public void onPause() {
        super.onPause();

        mPrivateMessageClient.getBus().unregister(mEventHandler);
    }

    @Override
    public Loader<AugmentedMessageContainer> onCreateLoader(final int id, final Bundle args) {
        final int page = args.getInt(CURRENT_PAGE_LOADER_ARGUMENT);
        return new MessageLoader(getActivity(), page, mMessageContainerType);
    }

    @Override
    public void onLoadFinished(final Loader<AugmentedMessageContainer> loader,
            final AugmentedMessageContainer data) {
        if (data == null) {
            // TODO - we need to tailor this to lack of connection/other network issue
            addDataToAdapter(null);
            return;
        }

        if (data.getCurrentPage() == 1) {
            mAdapter.clear();
            mInfiniteScrollListener = new InfiniteRecyclerLoadHelper(mRecyclerView,
                    new InfiniteLoadCallback(), data.getTotalPages(), null);
        }
        mLoadMoreProgressContainer.setVisibility(View.GONE);
        mInfiniteScrollListener.onLoadFinished();
        addDataToAdapter(data.getMessages());
    }

    @Override
    public void onLoaderReset(final Loader<AugmentedMessageContainer> loader) {
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == VIEW_MESSAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            final Bundle arguments = data.getBundleExtra(ViewMessageFragment
                    .ACTIVITY_RESULT_BUNDLE_ARGUMENTS);
            final AugmentedMessage message = arguments
                    .getParcelable(ViewMessageFragment.MESSAGE_ARGUMENT);
            ViewMessageResult result = (ViewMessageResult) arguments
                    .getSerializable(ViewMessageFragment.ACTIVITY_RESULT_BUNDLE_ARGUMENTS_RESULT);
            if (result == null) {
                result = ViewMessageResult.CHANGED;
            }
            switch (result) {
                case CHANGED:
                    mAdapter.update(message);
                    break;
                case DELETED:
                    mAdapter.remove(message);
                    break;
            }
        }
    }

    private void addDataToAdapter(final List<? extends Message> data) {
        UIUtils.updateEmptyViewState(getView(), mRecyclerView, data == null ? 0 : data.size());

        // Let's actually add the items now
        mAdapter.addAll(data);
        mRefreshLayout.setRefreshing(false);
    }

    private void loadTheFirstPage() {
        final Bundle bundle = new Bundle();
        bundle.putInt(CURRENT_PAGE_LOADER_ARGUMENT, 1);
        getLoaderManager().restartLoader(0, bundle, MessageFragment.this);
    }

    private class InfiniteLoadCallback implements InfiniteRecyclerLoadHelper.Callback {

        @Override
        public void loadMoreData(final int page) {
            mLoadMoreProgressContainer.setVisibility(View.VISIBLE);

            final Bundle bundle = new Bundle();
            bundle.putInt(CURRENT_PAGE_LOADER_ARGUMENT, page);
            getLoaderManager().restartLoader(0, bundle, MessageFragment.this);
        }
    }

    private class MessageClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            final int position = mRecyclerView.getChildPosition(view);
            if (position == RecyclerView.NO_POSITION) {
                return;
            }
            final Message message = mAdapter.getMessage(position);
            final Intent intent = new Intent(getActivity(), ViewMessageActivity.class);
            intent.putExtra(ViewMessageFragment.MESSAGE_ARGUMENT, message);
            getParentFragment().startActivityForResult(intent, VIEW_MESSAGE_REQUEST_CODE);
        }
    }
}