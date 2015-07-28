package com.xda.one.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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

import com.xda.one.R;
import com.xda.one.api.model.response.ResponseNews;
import com.xda.one.api.model.response.container.ResponseNewsContainer;
import com.xda.one.loader.NewsLoader;
import com.xda.one.ui.listener.InfiniteRecyclerLoadHelper;
import com.xda.one.ui.widget.XDARefreshLayout;
import com.xda.one.util.UIUtils;
import com.xda.one.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class NewsFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<ResponseNewsContainer> {

    private static final String CURRENT_PAGE_LOADER_ARGUMENT = "current_page";

    private static final String NEWS_SAVED_STATE = "news_saved_state";

    private static final String PAGES_SAVED_STATE = "pages_saved_state";

    // Views
    private XDARefreshLayout mRefreshLayout;

    private RecyclerView mRecyclerView;

    // View helpers
    private InfiniteRecyclerLoadHelper mInfiniteScrollListener;

    // Adapters
    private NewsAdapter mAdapter;

    // Data
    private int mTotalPages;

    public static NewsFragment createInstance() {
        return new NewsFragment();
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAdapter = new NewsAdapter(getActivity(), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getTag() != null && v.getTag() instanceof String) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse((String) v.getTag()));
                    startActivity(i);
                }
            }
        });
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.news_fragment, container, false);
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final ActionBar actionBar = UIUtils.getSupportActionBar(getActivity());
        actionBar.setTitle(R.string.xda_news);
        actionBar.setSubtitle(null);

        mRefreshLayout = (XDARefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        mRefreshLayout.setXDAColourScheme();
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                UIUtils.updateEmptyViewState(getView(), mRecyclerView, mAdapter.isEmpty());
                reloadTheFirstPage();
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

        // Start loading data if necessary
        if (!mAdapter.isEmpty()) {
            return;
        }
        if (savedInstanceState == null) {
            loadTheFirstPage();
        } else {
            final List<ResponseNews> news = savedInstanceState.getParcelableArrayList(
                    NEWS_SAVED_STATE);
            if (Utils.isCollectionEmpty(news)) {
                // news being empty implies a save state call came through when we were loading
                // our data for the first time
                loadTheFirstPage();
            } else {
                // This should give a non-zero integer
                mTotalPages = savedInstanceState.getInt(PAGES_SAVED_STATE);
                mInfiniteScrollListener = new InfiniteRecyclerLoadHelper(mRecyclerView,
                        new InfiniteLoadCallback(), mTotalPages, null);
                addDataToAdapter(news);
            }
        }
    }

    @Override
    public void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);

        final ArrayList<ResponseNews> list = new ArrayList<>(mAdapter.getNews());
        outState.putParcelableArrayList(NEWS_SAVED_STATE, list);
    }

    private void loadTheFirstPage() {
        final Bundle bundle = new Bundle();
        bundle.putInt(CURRENT_PAGE_LOADER_ARGUMENT, 1);
        getLoaderManager().initLoader(0, bundle, this);
    }

    private void reloadTheFirstPage() {
        final Bundle bundle = new Bundle();
        bundle.putInt(CURRENT_PAGE_LOADER_ARGUMENT, 1);
        getLoaderManager().restartLoader(0, bundle, this);
    }

    @Override
    public Loader<ResponseNewsContainer> onCreateLoader(int id, Bundle bundle) {
        return new NewsLoader(getActivity(),
                bundle.getInt(CURRENT_PAGE_LOADER_ARGUMENT));
    }

    @Override
    public void onLoadFinished(final Loader<ResponseNewsContainer> loader,
                               final ResponseNewsContainer data) {
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

        addDataToAdapter(data.getNewsItems());
        if (!mInfiniteScrollListener.hasMoreData()) {
            mAdapter.removeFooter();
        }
    }

    private void addDataToAdapter(final List<ResponseNews> data) {
        UIUtils.updateEmptyViewState(getView(), mRecyclerView, data == null ? 0 : data.size());

        // Let's actually add the items now
        mAdapter.addAll(data);
        mRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onLoaderReset(Loader<ResponseNewsContainer> loader) {
    }

    private class InfiniteLoadCallback implements InfiniteRecyclerLoadHelper.Callback {

        @Override
        public void loadMoreData(final int page) {
            final Bundle bundle = new Bundle();
            bundle.putInt(CURRENT_PAGE_LOADER_ARGUMENT, page);
            getLoaderManager().restartLoader(0, bundle, NewsFragment.this);
        }
    }
}