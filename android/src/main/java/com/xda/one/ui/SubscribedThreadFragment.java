package com.xda.one.ui;

import com.xda.one.R;
import com.xda.one.api.inteface.ThreadClient;
import com.xda.one.api.retrofit.RetrofitThreadClient;
import com.xda.one.loader.SubscribedThreadLoader;
import com.xda.one.model.augmented.AugmentedUnifiedThread;
import com.xda.one.model.augmented.container.AugmentedUnifiedThreadContainer;
import com.xda.one.ui.helper.ActionModeHelper;
import com.xda.one.ui.helper.ThreadEventHelper;
import com.xda.one.ui.helper.ThreadUnreadPostHelper;
import com.xda.one.ui.helper.UnifiedThreadFragmentActionModeHelper;
import com.xda.one.ui.listener.InfiniteRecyclerLoadHelper;
import com.xda.one.ui.widget.XDALinerLayoutManager;
import com.xda.one.ui.widget.XDARefreshLayout;
import com.xda.one.util.UIUtils;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

public class SubscribedThreadFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<AugmentedUnifiedThreadContainer> {

    private static final String CURRENT_PAGE_LOADER_ARGUMENT = "current_page";

    private static final int THREAD_LOADER = 2;

    private static final String THREADS_SAVED_STATE = "threads_saved_state";

    private UnifiedThreadAdapter mAdapter;

    // Infinite scrolling
    private InfiniteRecyclerLoadHelper mInfiniteScrollListener;

    private XDARefreshLayout mRefreshLayout;

    private ProgressBar mLoadMoreProgressBar;

    private RecyclerView mRecyclerView;

    private ActionModeHelper mModeHelper;

    private ThreadClient mThreadClient;

    private ThreadEventHelper mThreadEventHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
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
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.subscribed_thread_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);

        mThreadClient.getBus().register(mThreadEventHelper);

        mLoadMoreProgressBar = (ProgressBar) view.findViewById(R.id
                .subscribed_thread_fragment_load_more_progress_bar);

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
        mModeHelper.setRecyclerView(mRecyclerView);

        // If the listener already exists then tell it about the new recycler view
        if (mInfiniteScrollListener != null) {
            mInfiniteScrollListener.updateRecyclerView(mRecyclerView);
        }

        if (mAdapter.getItemCount() != 0) {
            return;
        }
        if (savedInstanceState == null) {
            final Bundle bundle = new Bundle();
            bundle.putInt(CURRENT_PAGE_LOADER_ARGUMENT, 1);
            getLoaderManager().initLoader(THREAD_LOADER, bundle, this);

            mRefreshLayout.setRefreshing(true);
        } else {
            final ArrayList<AugmentedUnifiedThread> threads = savedInstanceState
                    .getParcelableArrayList(THREADS_SAVED_STATE);
            addDataToAdapter(threads);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        mThreadClient.getBus().unregister(mThreadEventHelper);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelableArrayList(THREADS_SAVED_STATE, new ArrayList<>(mAdapter
                .getThreads()));
    }

    @Override
    public Loader<AugmentedUnifiedThreadContainer> onCreateLoader(int id, Bundle bundle) {
        return new SubscribedThreadLoader(getActivity(),
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

        if (data.getCurrentPage() == 1) {
            mAdapter.clear();
            mInfiniteScrollListener = new InfiniteRecyclerLoadHelper(mRecyclerView,
                    new InfiniteLoadCallback(), data.getTotalPages(), null);
        }
        mLoadMoreProgressBar.setVisibility(View.GONE);
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
    public void onLoaderReset(final Loader<AugmentedUnifiedThreadContainer> loader) {
    }

    private void loadTheFirstPage() {
        final Bundle bundle = new Bundle();
        bundle.putInt(CURRENT_PAGE_LOADER_ARGUMENT, 1);
        getLoaderManager().restartLoader(THREAD_LOADER, bundle, this);
    }

    private class InfiniteLoadCallback implements InfiniteRecyclerLoadHelper.Callback {

        @Override
        public void loadMoreData(final int page) {
            mLoadMoreProgressBar.setVisibility(View.VISIBLE);

            final Bundle bundle = new Bundle();
            bundle.putInt(CURRENT_PAGE_LOADER_ARGUMENT, page);
            getLoaderManager().restartLoader(0, bundle, SubscribedThreadFragment.this);
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
            final ProgressDialog progressDialog = ProgressDialog.show(getActivity(),
                    "Finding post position", "Finding post position", true, true);

            final ThreadUnreadPostHelper postHelper = new ThreadUnreadPostHelper(getActivity(),
                    getParentFragment().getFragmentManager(), thread, progressDialog);
            postHelper.start();
        }
    }
}