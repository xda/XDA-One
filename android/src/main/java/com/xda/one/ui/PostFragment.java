package com.xda.one.ui;

import com.xda.one.R;
import com.xda.one.api.inteface.PostClient;
import com.xda.one.api.misc.Consumer;
import com.xda.one.api.misc.Result;
import com.xda.one.api.model.interfaces.Post;
import com.xda.one.api.model.interfaces.UnifiedThread;
import com.xda.one.api.model.response.ResponseAttachment;
import com.xda.one.api.model.response.ResponseUnifiedThread;
import com.xda.one.api.model.response.container.ResponsePostContainer;
import com.xda.one.api.retrofit.RetrofitPostClient;
import com.xda.one.loader.PostLoader;
import com.xda.one.model.augmented.AugmentedPost;
import com.xda.one.model.augmented.AugmentedPostContainer;
import com.xda.one.ui.helper.ActionModeHelper;
import com.xda.one.ui.helper.CancellableCallbackHelper;
import com.xda.one.ui.listener.AvatarClickListener;
import com.xda.one.ui.widget.XDALinerLayoutManager;
import com.xda.one.util.UIUtils;
import com.xda.one.util.Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class PostFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<AugmentedPostContainer> {

    private static final String THREAD_ID_ARGUMENT = "thread_id";

    private static final String LIST_PADDING_SIZE_SAVED_STATE = "padding_size_saved_state";

    private static final String POSTS_SAVED_STATE = "posts_saved_state";

    private static final String PAGE_CONTAINER_ARGUMENT = "page_container";

    private static final int SCROLL_TO_LAST_LIST_ITEM = -1;

    private static final int SCROLL_TO_NONE = -2;

    private int mScrollToItem = SCROLL_TO_NONE;

    private final BroadcastReceiver mOnNotificationClick = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            // TODO - do something useful here
        }
    };

    private final BroadcastReceiver mOnComplete = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            final NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
            builder.build();
            Toast.makeText(context, "Finished downloading", Toast.LENGTH_LONG).show();
        }
    };

    private PostAdapter mAdapter;

    private int mPage;

    private String mThreadId;

    private Callback mCallback;

    private PostClient mPostClient;

    private RecyclerView mRecyclerView;

    private ActionModeHelper mModeHelper;

    private ResponsePostContainer mContainerArgument;

    private View mEmptyView;

    public static PostFragment getInstance(final UnifiedThread unifiedThread, final int page) {
        final Bundle bundle = new Bundle();
        bundle.putString(THREAD_ID_ARGUMENT, unifiedThread.getThreadId());
        bundle.putInt(PostPagerFragment.POST_PAGE_ARGUMENT, page);

        final PostFragment fragment = new PostFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    public static Fragment getInstance(final ResponsePostContainer containerArgument) {
        final Bundle bundle = new Bundle();
        bundle.putParcelable(PAGE_CONTAINER_ARGUMENT, containerArgument);

        final PostFragment fragment = new PostFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            // TODO - this may be an activity in the future - fix this
            mCallback = (Callback) getParentFragment();
        } catch (ClassCastException exc) {
            exc.printStackTrace();
        }
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mModeHelper = new ActionModeHelper(getActivity(),
                new PostFragmentActionMode(), null, ActionModeHelper.SelectionMode.MULTIPLE);

        mContainerArgument = getArguments().getParcelable(PAGE_CONTAINER_ARGUMENT);
        if (mContainerArgument == null) {
            mThreadId = getArguments().getString(THREAD_ID_ARGUMENT);
            mPage = getArguments().getInt(PostPagerFragment.POST_PAGE_ARGUMENT);
        } else {
            mPage = mContainerArgument.getCurrentPage();
        }

        getActivity().registerReceiver(mOnComplete, new IntentFilter(DownloadManager
                .ACTION_DOWNLOAD_COMPLETE));
        getActivity().registerReceiver(mOnNotificationClick, new IntentFilter(DownloadManager
                .ACTION_NOTIFICATION_CLICKED));

        // Get the client for sending thanks and the like to the server
        mPostClient = RetrofitPostClient.getClient(getActivity());

        // Create the adapter...
        // ... and set it up properly
        mAdapter = new PostAdapter(getActivity(),
                mModeHelper,
                new DownloadButtonClickListener(),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // TODO
                    }
                },
                new AvatarClickListener(getActivity()),
                new View.OnClickListener() {
                    @Override
                    public void onClick(final View view) {
                        final int position = (int) view.getTag();
                        final AugmentedPost post = mAdapter.getPost(position);
                        toggleThanks(position, post);
                    }
                },
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final int position = (int) view.getTag();
                        final AugmentedPost post = mAdapter.getPost(position);
                        quotePost(post);
                    }
                },
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final View itemView = (View) view.getTag();
                        multiQuotePost(itemView);
                    }
                },
                new PostAdapter.GoToQuoteListener() {
                    @Override
                    public void onClick(final String postId) {
                        final AlertDialog dialog = ProgressDialog.show(getActivity(),
                                "Finding post position", "Finding post position", true, true);

                        final PostClient client = RetrofitPostClient.getClient(getActivity());
                        GoToQuoteCallback callback = new GoToQuoteCallback(dialog);
                        client.getPostsById(postId, callback, callback);
                    }
                });
    }

    private void quotePost(final AugmentedPost... post) {
        mCallback.quotePost(post);
    }

    private void multiQuotePost(final View view) {
        mModeHelper.addViewToActionMode(view);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        getActivity().unregisterReceiver(mOnComplete);
        getActivity().unregisterReceiver(mOnNotificationClick);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
            final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.post_fragment, container, false);
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRecyclerView = (RecyclerView) view.findViewById(android.R.id.list);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new XDALinerLayoutManager(getActivity()));
        ViewCompat.setOverScrollMode(mRecyclerView, ViewCompat.OVER_SCROLL_NEVER);

        mEmptyView = view.findViewById(android.R.id.empty);

        mModeHelper.setRecyclerView(mRecyclerView);

        if (savedInstanceState == null) {
            mCallback.postPaddingToQuickReturn(mRecyclerView);
        } else {
            final int paddingSize = savedInstanceState.getInt(LIST_PADDING_SIZE_SAVED_STATE, -1);
            if (paddingSize != -1) {
                final int paddingLeft = mRecyclerView.getPaddingLeft();
                final int paddingRight = mRecyclerView.getPaddingRight();
                final int paddingBottom = mRecyclerView.getPaddingBottom();
                mRecyclerView.setPadding(paddingLeft, paddingSize, paddingRight, paddingBottom);
            }
        }

        // We need to do this rather than relying on the loader because the loader is not
        // preserved across page changes
        if (savedInstanceState == null) {
            getLoaderManager().initLoader(0, null, this);
        } else {
            final List<AugmentedPost> list = savedInstanceState
                    .getParcelableArrayList(POSTS_SAVED_STATE);
            if (Utils.isCollectionEmpty(list)) {
                getLoaderManager().initLoader(0, null, this);
            } else {
                UIUtils.updateEmptyViewState(getView(), mRecyclerView, list.size());
                mAdapter.addAll(list);
            }
        }
        mCallback.setQuickReturnListener(mRecyclerView, mPage - 1);
    }

    @Override
    public void onViewStateRestored(@Nullable final Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        mModeHelper.restoreInstanceState(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);

        final ArrayList<AugmentedPost> list = new ArrayList<>(mAdapter.getPosts());
        outState.putParcelableArrayList(POSTS_SAVED_STATE, list);

        mModeHelper.saveInstanceState(outState);

        if (getView() != null) {
            outState.putInt(LIST_PADDING_SIZE_SAVED_STATE, mRecyclerView.getPaddingTop());
        }
    }

    @Override
    public Loader<AugmentedPostContainer> onCreateLoader(int id, Bundle args) {
        if (mContainerArgument == null) {
            return new PostLoader(getActivity(), mThreadId, mPage);
        }
        mScrollToItem = mContainerArgument.getIndex();
        return new PostLoader(getActivity(), mContainerArgument);
    }

    @Override
    public void onLoadFinished(final Loader<AugmentedPostContainer> loader,
            final AugmentedPostContainer container) {
        if (container == null) {
            onItemsReceived(null);
        } else {
            onItemsReceived(container.getPosts());
            mCallback.onPageLoaded(container.getThread());
        }
    }

    private void onItemsReceived(final List<AugmentedPost> data) {
        if (data == null && mAdapter.getItemCount() != 0) {
            Toast.makeText(getActivity(), R.string.unable_to_refresh, Toast.LENGTH_LONG).show();
            return;
        } else if (data == null) {
            UIUtils.updateEmptyViewState(getView(), mRecyclerView, 0);
            return;
        }
        mAdapter.clear();

        UIUtils.updateEmptyViewState(getView(), mRecyclerView, data.size());
        mAdapter.addAll(data);

        // Scroll to the relevant position in the list
        if (mScrollToItem == SCROLL_TO_LAST_LIST_ITEM) {
            mRecyclerView.scrollToPosition(mAdapter.getItemCount() - 1);
        } else if (mScrollToItem != SCROLL_TO_NONE) {
            mRecyclerView.scrollToPosition(mScrollToItem);
        }
        mScrollToItem = SCROLL_TO_NONE;
    }

    @Override
    public void onLoaderReset(final Loader<AugmentedPostContainer> loader) {
    }

    private void toggleThanks(final int position, final Post post) {
        mPostClient.toggleThanksAsync(post, new Consumer<Result>() {
            @Override
            public void run(Result result) {
                Toast.makeText(getActivity(), "Thanks toggled", Toast.LENGTH_LONG).show();
                mAdapter.notifyItemChanged(position);
            }
        });
    }

    public void refreshPageAndScrollToBottom() {
        // We want to scroll to the bottom as the method name suggests
        mScrollToItem = SCROLL_TO_LAST_LIST_ITEM;
        // We also want to clear everything in the adapter
        mAdapter.clear();
        // And show the loading view
        UIUtils.showLoadingProgress(mRecyclerView, mEmptyView);
        // And then re-fetch the data from the server
        getLoaderManager().restartLoader(0, null, this);
    }

    public boolean isActionModeStarted() {
        return mModeHelper.isActionModeStarted();
    }

    public AugmentedPost[] getCheckedItems() {
        return mAdapter.getPosts(mModeHelper.getCheckedPositions());
    }

    public void finishActionMode() {
        if (mModeHelper != null) {
            mModeHelper.finish();
        }
    }

    public void scrollToPosition(final ResponsePostContainer data) {
        mRecyclerView.scrollToPosition(data.getIndex());
    }

    public static interface Callback {

        public void quotePost(final AugmentedPost... post);

        public void switchToFragment(final ResponsePostContainer container);

        public void setQuickReturnListener(final RecyclerView recyclerView, final int page);

        public void postPaddingToQuickReturn(final View content);

        public void onPageLoaded(final ResponseUnifiedThread thread);
    }

    private class DownloadButtonClickListener implements View.OnClickListener {

        @Override
        public void onClick(final View v) {
            final ResponseAttachment a = (ResponseAttachment) v.getTag();
            final DownloadManager manager = (DownloadManager) getActivity().getSystemService
                    (Context.DOWNLOAD_SERVICE);
            final DownloadManager.Request request = new DownloadManager
                    .Request(Uri.parse(a.getAttachmentUrl()))
                    .setAllowedOverRoaming(false)
                    .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS + "/XDA One/", a.getFileName());
            manager.enqueue(request);
        }
    }

    public class PostFragmentActionMode extends ActionModeHelper.RecyclerViewActionModeCallback {

        @Override
        public boolean onCreateActionMode(final ActionMode actionMode, final Menu menu) {
            actionMode.getMenuInflater().inflate(R.menu.post_fragment_cab, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            return true;
        }

        @Override
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.post_fragment_cab_quote:
                    final AugmentedPost[] posts = mAdapter
                            .getPosts(mModeHelper.getCheckedPositions());
                    quotePost(posts);
            }
            mModeHelper.finish();
            return true;
        }
    }

    public class GoToQuoteCallback extends CancellableCallbackHelper<ResponsePostContainer> {

        private final AlertDialog mDialog;

        public GoToQuoteCallback(final AlertDialog dialog) {
            super(dialog);

            mDialog = dialog;
        }

        @Override
        public void safeCallback(final ResponsePostContainer data) {
            mDialog.dismiss();

            if (data.getCurrentPage() == mPage) {
                scrollToPosition(data);
            } else {
                mCallback.switchToFragment(data);
            }
        }

        @Override
        public void run() {

        }
    }
}