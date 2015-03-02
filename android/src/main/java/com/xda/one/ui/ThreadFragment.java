package com.xda.one.ui;

import com.xda.one.R;
import com.xda.one.api.inteface.ThreadClient;
import com.xda.one.api.model.interfaces.UnifiedThread;
import com.xda.one.api.retrofit.RetrofitThreadClient;
import com.xda.one.model.augmented.AugmentedUnifiedThread;
import com.xda.one.model.augmented.container.AugmentedUnifiedThreadContainer;
import com.xda.one.ui.helper.ActionModeHelper;
import com.xda.one.ui.listener.InfiniteRecyclerLoadHelper;
import com.xda.one.ui.thread.DefaultThreadLoaderStrategy;
import com.xda.one.ui.thread.FirstThreadClickStrategy;
import com.xda.one.ui.thread.ParticipatedThreadLoaderStrategy;
import com.xda.one.ui.thread.SubscribedThreadLoaderStrategy;
import com.xda.one.ui.thread.ThreadActionModeHelper;
import com.xda.one.ui.thread.ThreadClickStrategy;
import com.xda.one.ui.thread.ThreadEventHelper;
import com.xda.one.ui.thread.ThreadLoaderStrategy;
import com.xda.one.ui.thread.UnreadThreadClickStrategy;
import com.xda.one.ui.widget.FloatingActionButton;
import com.xda.one.ui.widget.HierarchySpinnerAdapter;
import com.xda.one.ui.widget.XDARefreshLayout;
import com.xda.one.util.AccountUtils;
import com.xda.one.util.CompatUtils;
import com.xda.one.util.UIUtils;
import com.xda.one.util.Utils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
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

public class ThreadFragment extends Fragment {

    // Request codes
    public static final int CREATE_THREAD_REQUEST_CODE = 101;

    // Argument keys
    public static final String FORUM_ID_ARGUMENT = "sub_forum_id";

    private static final String FORUM_TITLE_ARGUMENT = "forum_title";

    private static final String PARENT_FORUM_TITLE_ARGUMENT = "parent_forum_title";

    private static final String ADD_EXTRA_DECOR_ARGUMENT = "add_extra_decor";

    private static final String THREAD_LOAD_STRATEGY_ARGUMENT = "thread_load_strategy";

    private static final String THREAD_CLICK_STRATEGY_ARGUMENT = "thread_click_strategy";

    private static final String CURRENT_PAGE_LOADER_ARGUMENT = "current_page";

    private static final String FORUM_HIERARCHY_ARGUMENT = "hierarchy";

    // Saved state keys
    private static final String THREADS_SAVED_STATE = "threads_saved_state";

    private static final String PAGES_SAVED_STATE = "pages_saved_state";

    // Callbacks
    private final LoaderCallbacks mLoaderCallbacks = new LoaderCallbacks();

    private Callback mCallback;

    // Useful objects
    private ThreadClient mThreadClient;

    // Internal arguments
    private boolean mAddExtraDecor;

    private ThreadLoaderStrategy mThreadLoadStrategy;

    private ThreadClickStrategy mThreadClickStrategy;

    // External Arguments
    private int mForumId;

    private String mForumTitle;

    private String mParentForumTitle;

    private List<String> mHierarchy;

    // Views
    private XDARefreshLayout mRefreshLayout;

    private RecyclerView mRecyclerView;

    // View helpers
    private ActionModeHelper mModeHelper;

    private ThreadEventHelper mThreadEventHelper;

    private InfiniteRecyclerLoadHelper mInfiniteScrollListener;

    // Adapters
    private ThreadAdapter mAdapter;

    private HierarchySpinnerAdapter mSpinnerAdapter;

    // Data
    private int mTotalPages;

    public static ThreadFragment createDefault(final int forumId, final String forumTitle,
            final String parentForumTitle, final ArrayList<String> hierarchy) {
        final Bundle bundle = new Bundle();

        // Internal use
        bundle.putBoolean(ADD_EXTRA_DECOR_ARGUMENT, true);
        bundle.putParcelable(THREAD_LOAD_STRATEGY_ARGUMENT, new DefaultThreadLoaderStrategy());
        bundle.putParcelable(THREAD_CLICK_STRATEGY_ARGUMENT, new FirstThreadClickStrategy());

        // From external
        bundle.putInt(FORUM_ID_ARGUMENT, forumId);
        bundle.putString(FORUM_TITLE_ARGUMENT, forumTitle);
        bundle.putString(PARENT_FORUM_TITLE_ARGUMENT, parentForumTitle);
        bundle.putStringArrayList(FORUM_HIERARCHY_ARGUMENT, hierarchy);

        final ThreadFragment threadFragment = new ThreadFragment();
        threadFragment.setArguments(bundle);

        return threadFragment;
    }

    public static ThreadFragment createSubscribed() {
        final Bundle bundle = new Bundle();

        // All are from internal
        bundle.putBoolean(ADD_EXTRA_DECOR_ARGUMENT, false);
        bundle.putParcelable(THREAD_LOAD_STRATEGY_ARGUMENT, new SubscribedThreadLoaderStrategy());
        bundle.putParcelable(THREAD_CLICK_STRATEGY_ARGUMENT, new UnreadThreadClickStrategy());

        bundle.putStringArrayList(FORUM_HIERARCHY_ARGUMENT, new ArrayList<String>());

        final ThreadFragment threadFragment = new ThreadFragment();
        threadFragment.setArguments(bundle);

        return threadFragment;
    }

    public static ThreadFragment createParticipated() {
        final Bundle bundle = new Bundle();

        // All are from internal
        bundle.putBoolean(ADD_EXTRA_DECOR_ARGUMENT, false);
        bundle.putParcelable(THREAD_LOAD_STRATEGY_ARGUMENT, new ParticipatedThreadLoaderStrategy());
        bundle.putParcelable(THREAD_CLICK_STRATEGY_ARGUMENT, new UnreadThreadClickStrategy());

        bundle.putString(FORUM_TITLE_ARGUMENT, "Participated");
        bundle.putStringArrayList(FORUM_HIERARCHY_ARGUMENT, new ArrayList<String>());

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

        // Retrieve the arguments from the given bundle
        // Internal arguments
        mAddExtraDecor = getArguments().getBoolean(ADD_EXTRA_DECOR_ARGUMENT, true);
        mThreadLoadStrategy = getArguments().getParcelable(THREAD_LOAD_STRATEGY_ARGUMENT);
        mThreadClickStrategy = getArguments().getParcelable(THREAD_CLICK_STRATEGY_ARGUMENT);

        // From external
        mForumId = getArguments().getInt(FORUM_ID_ARGUMENT, 0);
        mForumTitle = getArguments().getString(FORUM_TITLE_ARGUMENT, null);
        mParentForumTitle = getArguments().getString(PARENT_FORUM_TITLE_ARGUMENT, null);
        mHierarchy = getArguments().getStringArrayList(FORUM_HIERARCHY_ARGUMENT);

        final ThreadActionModeHelper helper =
                new ThreadActionModeHelper(getActivity(), mThreadClient);

        mModeHelper = new ActionModeHelper(getActivity(), helper,
                new ThreadClickListener(), ActionModeHelper.SelectionMode.SINGLE);
        mAdapter = new ThreadAdapter(getActivity(), mModeHelper, mModeHelper, mModeHelper);
        mSpinnerAdapter = new HierarchySpinnerAdapter(getActivity(),
                LayoutInflater.from(getActivity()), mHierarchy, getFragmentManager());

        mThreadEventHelper = new ThreadEventHelper(getActivity(), mAdapter);
        helper.setAdapter(mAdapter);
        helper.setModeHelper(mModeHelper);
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

        // Setup views
        setupRefreshLayout(view);
        setupRecyclerView(view);
        setupAddThreadButton(view);
        setupActionBar();

        // Tell helpers about RecyclerView
        mModeHelper.setRecyclerView(mRecyclerView);
        if (mInfiniteScrollListener != null) {
            mInfiniteScrollListener.updateRecyclerView(mRecyclerView);
        }

        // Start loading data if necessary
        if (!mAdapter.isEmpty()) {
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
                mInfiniteScrollListener = new InfiniteRecyclerLoadHelper(mRecyclerView,
                        new InfiniteLoadCallback(),
                        mTotalPages, null);
                addDataToAdapter(threads);
            }
        }
    }

    private void setupRefreshLayout(final View view) {
        mRefreshLayout = (XDARefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        mRefreshLayout.setXDAColourScheme();
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                UIUtils.updateEmptyViewState(getView(), mRecyclerView, mAdapter.isEmpty());
                reloadTheFirstPage();
            }
        });
    }

    private void setupRecyclerView(final View view) {
        mRecyclerView = (RecyclerView) view.findViewById(android.R.id.list);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new XDALinerLayoutManager(getActivity()));
        ViewCompat.setOverScrollMode(mRecyclerView, ViewCompat.OVER_SCROLL_NEVER);
    }

    private void setupAddThreadButton(final View view) {
        final FloatingActionButton button = (FloatingActionButton) view
                .findViewById(R.id.thread_fragment_create_thread);
        if (!mAddExtraDecor) {
            button.setVisibility(View.GONE);
            return;
        }

        button.setOnClickListener(new CreateThreadListener());
        if (CompatUtils.hasLollipop()) {
            final Drawable drawable = getResources().getDrawable(R.drawable.fab_background);
            CompatUtils.setBackground(button, drawable);
        } else {
            final int color = getResources().getColor(R.color.fab_color);
            button.setBackgroundColor(color);
        }
    }

    private void setupActionBar() {
        final ActionBar actionBar = UIUtils.getSupportActionBar(getActivity());
        if (mForumTitle != null) {
            actionBar.setTitle(mForumTitle.isEmpty() ? null : mForumTitle);
        }
        if (mParentForumTitle != null) {
            actionBar.setSubtitle(mParentForumTitle.isEmpty() ? null : mParentForumTitle);
        }

        if (!mAddExtraDecor) {
            return;
        }
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        actionBar.setListNavigationCallbacks(mSpinnerAdapter, mSpinnerAdapter);
        actionBar.setSelectedNavigationItem(mSpinnerAdapter.getCount() - 1);
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

    private void loadTheFirstPage() {
        final Bundle bundle = new Bundle();
        bundle.putInt(CURRENT_PAGE_LOADER_ARGUMENT, 1);
        getLoaderManager().initLoader(0, bundle, mLoaderCallbacks);
    }

    private void reloadTheFirstPage() {
        final Bundle bundle = new Bundle();
        bundle.putInt(CURRENT_PAGE_LOADER_ARGUMENT, 1);
        getLoaderManager().restartLoader(0, bundle, mLoaderCallbacks);
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 1) {
                UIUtils.updateEmptyViewState(getView(), mRecyclerView, true);
                reloadTheFirstPage();
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

    private void addDataToAdapter(final List<AugmentedUnifiedThread> data) {
        UIUtils.updateEmptyViewState(getView(), mRecyclerView, data == null ? 0 : data.size());

        // Let's actually add the items now
        mAdapter.addAll(data);
        mRefreshLayout.setRefreshing(false);
    }

    public interface Callback {

        public void login(final Runnable runnable);
    }

    private class InfiniteLoadCallback implements InfiniteRecyclerLoadHelper.Callback {

        @Override
        public void loadMoreData(final int page) {
            final Bundle bundle = new Bundle();
            bundle.putInt(CURRENT_PAGE_LOADER_ARGUMENT, page);
            getLoaderManager().restartLoader(0, bundle, mLoaderCallbacks);
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
            mThreadClickStrategy.onClick(ThreadFragment.this, mHierarchy, thread);
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

        private void createNewThread() {
            final DialogFragment fragment = CreateThreadFragment.createInstance(mForumId);
            fragment.setTargetFragment(ThreadFragment.this, CREATE_THREAD_REQUEST_CODE);
            fragment.show(getFragmentManager(), "createThread");
        }
    }

    private class LoaderCallbacks
            implements LoaderManager.LoaderCallbacks<AugmentedUnifiedThreadContainer> {

        @Override
        public Loader<AugmentedUnifiedThreadContainer> onCreateLoader(int id, Bundle bundle) {
            return mThreadLoadStrategy.createLoader(getActivity(), mForumId,
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

            if (mInfiniteScrollListener != null && !mInfiniteScrollListener.isLoading()
                    && !mRefreshLayout.isRefreshing()) {
                // This may happen when we are coming back from posts fragment to threads. For some
                // reason loadFinished gets called. However, we may have new data about the thread -
                // don't disturb this data.
                UIUtils.updateEmptyViewState(getView(), mRecyclerView, false);
                mRecyclerView.setOnScrollListener(mInfiniteScrollListener);
                return;
            } else if (data.getCurrentPage() == 1 || mInfiniteScrollListener == null) {
                mAdapter.clear();

                mTotalPages = data.getTotalPages();
                mInfiniteScrollListener = new InfiniteRecyclerLoadHelper(mRecyclerView,
                        new InfiniteLoadCallback(), mTotalPages, null);
            }
            mInfiniteScrollListener.onLoadFinished();

            addDataToAdapter(data.getThreads());
            if (!mInfiniteScrollListener.hasMoreData()) {
                mAdapter.removeFooter();
            }
        }

        @Override
        public void onLoaderReset(Loader<AugmentedUnifiedThreadContainer> loader) {
        }
    }
}