package com.xda.one.ui;

import com.xda.one.R;
import com.xda.one.api.inteface.ThreadClient;
import com.xda.one.api.model.interfaces.Forum;
import com.xda.one.api.model.interfaces.UnifiedThread;
import com.xda.one.api.retrofit.RetrofitThreadClient;
import com.xda.one.loader.ThreadLoader;
import com.xda.one.model.augmented.AugmentedUnifiedThread;
import com.xda.one.model.augmented.container.AugmentedUnifiedThreadContainer;
import com.xda.one.model.misc.ForumType;
import com.xda.one.ui.helper.ActionModeHelper;
import com.xda.one.ui.helper.ThreadEventHelper;
import com.xda.one.ui.helper.UnifiedThreadFragmentActionModeHelper;
import com.xda.one.ui.listener.InfiniteRecyclerLoadHelper;
import com.xda.one.ui.widget.FloatingActionButton;
import com.xda.one.ui.widget.HierarchySpinnerAdapter;
import com.xda.one.ui.widget.XDARefreshLayout;
import com.xda.one.util.AccountUtils;
import com.xda.one.util.CompatUtils;
import com.xda.one.util.FragmentUtils;
import com.xda.one.util.UIUtils;
import com.xda.one.util.Utils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.XDALinerLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class ThreadFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<AugmentedUnifiedThreadContainer> {

    public static final String FORUM_ID_ARGUMENT = "sub_forum_id";

    public static final String FORUM_TITLE_ARGUMENT = "forum_title";

    public static final String PARENT_FORUM_TITLE_ARGUMENT = "parent_forum_title";

    public static final int CREATE_MESSAGE_REQUEST_CODE = 101;

    private static final String CURRENT_PAGE_LOADER_ARGUMENT = "current_page";

    private static final String THREADS_SAVED_STATE = "threads_saved_state";

    private static final String PAGES_SAVED_STATE = "pages_saved_state";

    private static final String FORUM_HIERARCHY_ARGUMENT = "hierarchy";

    private ForumType mForumType = ForumType.ALL;

    public static final String FORUM_TYPE = "forum_type";

    private int mForumId;

    // Infinite scrolling
    private InfiniteRecyclerLoadHelper mInfiniteScrollListener;

    private String mForumTitle;

    private String mParentForumTitle;

    private XDARefreshLayout mRefreshLayout;

    private RecyclerView mRecyclerView;

    private UnifiedThreadAdapter mAdapter;

    private View mLoadMoreProgressContainer;

    private ActionModeHelper mModeHelper;

    private List<String> mHierarchy;

    private HierarchySpinnerAdapter mSpinnerAdapter;

    private int mTotalPages;

    private ThreadClient mThreadClient;

    private ThreadEventHelper mThreadEventHelper;

    private Callback mCallback;

    public static ThreadFragment createInstance(final int forumId, final String forumTitle,
            final String parentForumTitle, final ArrayList<String> hierarchy) {
        final Bundle bundle = new Bundle();
        bundle.putInt(FORUM_ID_ARGUMENT, forumId);
        bundle.putString(FORUM_TITLE_ARGUMENT, forumTitle);
        bundle.putString(PARENT_FORUM_TITLE_ARGUMENT, parentForumTitle);
        bundle.putStringArrayList(FORUM_HIERARCHY_ARGUMENT, hierarchy);

        final ThreadFragment threadFragment = new ThreadFragment();
        threadFragment.setArguments(bundle);

        return threadFragment;
    }

    @Override
    public void onAttach(final Activity activity) {
        super.onAttach(activity);

        mCallback = (Callback) activity;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mThreadClient = RetrofitThreadClient.getClient(getActivity());

        final UnifiedThreadFragmentActionModeHelper helper =
                new UnifiedThreadFragmentActionModeHelper(getActivity(), mThreadClient);

        mModeHelper = new ActionModeHelper(getActivity(), helper,
                new ThreadClickListener(),
                ActionModeHelper.SelectionMode.SINGLE);
        mAdapter = new UnifiedThreadAdapter(getActivity(), mModeHelper, mModeHelper, mModeHelper);

        mThreadEventHelper = new ThreadEventHelper(getActivity(), mAdapter);

        helper.setAdapter(mAdapter);
        helper.setModeHelper(mModeHelper);

        mForumType = (ForumType) getArguments().getSerializable(FORUM_TYPE);
        mForumId = getArguments().getInt(FORUM_ID_ARGUMENT, 0);
        mForumTitle = getArguments().getString(FORUM_TITLE_ARGUMENT, null);
        mParentForumTitle = getArguments().getString(PARENT_FORUM_TITLE_ARGUMENT, null);
        mHierarchy = getArguments().getStringArrayList(FORUM_HIERARCHY_ARGUMENT);

        mSpinnerAdapter = new HierarchySpinnerAdapter(getActivity(), LayoutInflater.from(getActivity()),mHierarchy,getFragmentManager());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.thread_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);

        mThreadClient.getBus().register(mThreadEventHelper);

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
                reloadTheFirstPage();
            }
        });

        mRecyclerView = (RecyclerView) view.findViewById(android.R.id.list);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new XDALinerLayoutManager(getActivity()));
        ViewCompat.setOverScrollMode(mRecyclerView, ViewCompat.OVER_SCROLL_NEVER);

        mModeHelper.setRecyclerView(mRecyclerView);

        final FloatingActionButton button = (FloatingActionButton) view
                .findViewById(R.id.thread_fragment_create_thread);
        button.setOnClickListener(new CreateThreadListener());
        if (CompatUtils.hasLollipop()) {
            CompatUtils.setBackground(button, getResources()
                    .getDrawable(R.drawable.fab_background));
        } else {
            button.setBackgroundColor(getResources().getColor(R.color.fab_color));
        }

        final ActionBar actionBar = UIUtils.getSupportActionBar(getActivity());
        actionBar.setTitle(mForumTitle);
        actionBar.setSubtitle(mParentForumTitle);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        actionBar.setListNavigationCallbacks(mSpinnerAdapter, mSpinnerAdapter);
        actionBar.setSelectedNavigationItem(mSpinnerAdapter.getCount() - 1);

        // If the listener already exists then tell it about the new recycler view
        if (mInfiniteScrollListener != null) {
            mInfiniteScrollListener.updateRecyclerView(mRecyclerView);
        }

        if (mAdapter.getItemCount() != 0) {
            return;
        }

        if (savedInstanceState == null) {
            loadTheFirstPage();
        } else {
            final List<AugmentedUnifiedThread> threads = savedInstanceState
                    .getParcelableArrayList(THREADS_SAVED_STATE);
            if (Utils.isCollectionEmpty(threads)) {
                // threads being empty implies a save state call came through when we were loading
                // our data for the first time
                loadTheFirstPage();
            } else {
                // This should give a non-zero integer
                mTotalPages = savedInstanceState.getInt(PAGES_SAVED_STATE);
                if (mInfiniteScrollListener == null) {
                    mInfiniteScrollListener = createInfiniteScrollListener(mTotalPages);
                } else {
                    // TODO - Should never happen - investigate if it does
                }
                addDataToAdapter(threads);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        mThreadClient.getBus().unregister(mThreadEventHelper);
    }

    @Override
    public void onViewStateRestored(@Nullable final Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        mModeHelper.restoreInstanceState(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(final Bundle state) {
        super.onSaveInstanceState(state);

        mModeHelper.saveInstanceState(state);

        state.putParcelableArrayList(THREADS_SAVED_STATE, new ArrayList<>(mAdapter.getThreads()));
        state.putInt(PAGES_SAVED_STATE, mTotalPages);
    }

    private InfiniteRecyclerLoadHelper createInfiniteScrollListener(final int totalPages) {
        return new InfiniteRecyclerLoadHelper(mRecyclerView, new InfiniteLoadCallback(),
                totalPages, null);
    }

    private void loadTheFirstPage() {
        final Bundle bundle = new Bundle();
        bundle.putInt(CURRENT_PAGE_LOADER_ARGUMENT, 1);
        getLoaderManager().initLoader(0, bundle, this);

        mRefreshLayout.setRefreshing(true);
    }

    private void reloadTheFirstPage() {
        final Bundle bundle = new Bundle();
        bundle.putInt(CURRENT_PAGE_LOADER_ARGUMENT, 1);
        getLoaderManager().restartLoader(0, bundle, this);
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 1) {
                reloadTheFirstPage();
                mRefreshLayout.setRefreshing(true);
            } else if (requestCode == 101) {
                final UnifiedThread thread = data.getParcelableExtra("thread");
                updateThread(thread);
            }
        }
    }

    private void updateThread(final UnifiedThread thread) {
        final AugmentedUnifiedThread unifiedThread = new AugmentedUnifiedThread(thread,
                getActivity());
        final int position = mAdapter.indexOf(unifiedThread);
        mAdapter.updateThread(position, unifiedThread);
    }

    @Override
    public Loader<AugmentedUnifiedThreadContainer> onCreateLoader(int id, Bundle bundle) {
        return new ThreadLoader(getActivity(), mForumId,
                bundle.getInt(CURRENT_PAGE_LOADER_ARGUMENT));
    }

    @Override
    public void onLoadFinished(final Loader<AugmentedUnifiedThreadContainer> loader,
            final AugmentedUnifiedThreadContainer data) {
        if (data == null) {
            // TODO - we need to tailor this to lack of connection/other network issue
            addDataToAdapter(null);
            return;
        }

        final int count = mAdapter.getItemCount();
        mTotalPages = data.getTotalPages();
        mLoadMoreProgressContainer.setVisibility(View.GONE);
        if (data.getCurrentPage() == 1 && mRefreshLayout.isRefreshing()) {
            mAdapter.clear();
            mInfiniteScrollListener = createInfiniteScrollListener(data.getTotalPages());
        } else if (!mInfiniteScrollListener.isLoading() && count != 0) {
            // This may happen when we are coming back from posts fragment to threads. For some
            // reason loadFinished gets called. However, we may have new data about the thread -
            // don't disturb this data.
            UIUtils.updateEmptyViewState(getView(), mRecyclerView, count);
            mRecyclerView.setOnScrollListener(mInfiniteScrollListener);
            return;
        }
        mInfiniteScrollListener.onLoadFinished();
        addDataToAdapter(data.getThreads());
    }

    private void addDataToAdapter(final List<AugmentedUnifiedThread> data) {
        UIUtils.updateEmptyViewState(getView(), mRecyclerView, data == null ? 0 : data.size());

        // Let's actually add the items now
        mAdapter.addAll(data);
        mRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onLoaderReset(Loader<AugmentedUnifiedThreadContainer> loader) {
    }

    private void createNewThread() {
        final DialogFragment fragment = CreateThreadFragment.createInstance(mForumId);
        fragment.setTargetFragment(this, CREATE_MESSAGE_REQUEST_CODE);
        fragment.show(getFragmentManager(), "createThread");
    }

    public interface Callback {

        public void login(final Runnable runnable);
    }

    private class InfiniteLoadCallback implements InfiniteRecyclerLoadHelper.Callback {

        @Override
        public void loadMoreData(final int page) {
            mLoadMoreProgressContainer.setVisibility(View.VISIBLE);

            final Bundle bundle = new Bundle();
            bundle.putInt(CURRENT_PAGE_LOADER_ARGUMENT, page);
            getLoaderManager().restartLoader(0, bundle, ThreadFragment.this);
        }
    }

    private class ThreadClickListener implements View.OnClickListener {

        @Override
        public void onClick(final View view) {
            final int position = mRecyclerView.getChildPosition(view);
            if (position == RecyclerView.NO_POSITION) {
                return;
            }

            final AugmentedUnifiedThread thread = mAdapter.getThread(position);
            final Fragment fragment = FragmentUtils
                    .switchToPostList(thread, new ArrayList<>(mHierarchy));
            fragment.setTargetFragment(ThreadFragment.this, 101);
            final FragmentTransaction transaction = FragmentUtils.getDefaultTransaction(
                    getFragmentManager());
            transaction.addToBackStack(thread.getTitle());
            transaction.replace(R.id.content_frame, fragment).commit();
        }
    }

    private class CreateThreadListener implements View.OnClickListener {

        @Override
        public void onClick(final View v) {
            if (AccountUtils.isAccountAvailable(getActivity())) {
                createNewThread();
            } else {
                mCallback.login(new Runnable() {
                    @Override
                    public void run() {
                        createNewThread();
                    }
                });
            }
        }
    }
}