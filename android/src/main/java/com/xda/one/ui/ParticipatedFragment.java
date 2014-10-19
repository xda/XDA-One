package com.xda.one.ui;

import com.xda.one.R;
import com.xda.one.api.inteface.ThreadClient;
import com.xda.one.api.retrofit.RetrofitThreadClient;
import com.xda.one.loader.ParticipatedThreadLoader;
import com.xda.one.model.augmented.AugmentedUnifiedThread;
import com.xda.one.model.augmented.container.AugmentedUnifiedThreadContainer;
import com.xda.one.ui.helper.ActionModeHelper;
import com.xda.one.ui.helper.ThreadEventHelper;
import com.xda.one.ui.helper.ThreadUnreadPostHelper;
import com.xda.one.ui.helper.UnifiedThreadFragmentActionModeHelper;
import com.xda.one.ui.listener.InfiniteRecyclerLoadHelper;
import com.xda.one.ui.widget.FloatingActionButton;
import com.xda.one.ui.widget.XDARefreshLayout;
import com.xda.one.util.UIUtils;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
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

public class ParticipatedFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<AugmentedUnifiedThreadContainer> {

    private static final String CURRENT_PAGE_LOADER_ARGUMENT = "current_page";

    private static final String THREADS_SAVED_STATE = "threads_saved_state";

    private UnifiedThreadAdapter mAdapter;

    private RecyclerView mRecyclerView;

    private XDARefreshLayout mRefreshLayout;

    private View mLoadMoreProgressContainer;

    private InfiniteRecyclerLoadHelper mInfiniteScrollListener;

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
            final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.partcipated_fragment, container, false);
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

        mRecyclerView = (RecyclerView) view.findViewById(android.R.id.list);
        mRecyclerView.setLayoutManager(new XDALinerLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mAdapter);
        ViewCompat.setOverScrollMode(mRecyclerView, ViewCompat.OVER_SCROLL_NEVER);
        mModeHelper.setRecyclerView(mRecyclerView);

        mRefreshLayout = (XDARefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        mRefreshLayout.setXDAColourScheme();
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                final Bundle bundle = new Bundle();
                bundle.putInt(CURRENT_PAGE_LOADER_ARGUMENT, 1);
                getLoaderManager().restartLoader(0, bundle, ParticipatedFragment.this);
            }
        });

        final ActionBar actionBar = UIUtils.getSupportActionBar(getActivity());
        actionBar.show();
        actionBar.setTitle(R.string.participated);
        actionBar.setSubtitle(null);

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
            getLoaderManager().initLoader(0, bundle, this);

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

        outState.putParcelableArrayList(THREADS_SAVED_STATE, new ArrayList<Parcelable>(mAdapter
                .getThreads()));
    }

    @Override
    public Loader<AugmentedUnifiedThreadContainer> onCreateLoader(final int loader,
            final Bundle bundle) {
        return new ParticipatedThreadLoader(getActivity(),
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
        mLoadMoreProgressContainer.setVisibility(View.GONE);
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

    private class InfiniteLoadCallback implements InfiniteRecyclerLoadHelper.Callback {

        @Override
        public void loadMoreData(final int page) {
            mLoadMoreProgressContainer.setVisibility(View.VISIBLE);

            final Bundle bundle = new Bundle();
            bundle.putInt(CURRENT_PAGE_LOADER_ARGUMENT, page);
            getLoaderManager().restartLoader(0, bundle, ParticipatedFragment.this);
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
                    getFragmentManager(), thread, progressDialog);
            postHelper.start();
        }
    }
}