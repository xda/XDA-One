package com.xda.one.ui;

import com.xda.one.R;
import com.xda.one.api.model.response.ResponseNews;
import com.xda.one.api.model.response.container.ResponseNewsContainer;
import com.xda.one.loader.NewsLoader;
import com.xda.one.ui.listener.InfiniteRecyclerLoadHelper;

import android.graphics.Color;
import android.support.v7.widget.XDALinerLayoutManager;

import com.xda.one.ui.widget.FloatingActionButton;
import com.xda.one.util.UIUtils;
import com.xda.one.util.Utils;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class NewsFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<ResponseNewsContainer> {

    private static final String CURRENT_PAGE_LOADER_ARGUMENT = "current_page";

    private static final String NEWS_SAVED_STATE = "news_saved_state";

    private static final String PAGES_SAVED_STATE = "pages_saved_state";

    // Infinite scrolling
    private InfiniteRecyclerLoadHelper mInfiniteScrollListener;

    private NewsAdapter mAdapter;

    private RecyclerView mRecyclerView;

    private int mTotalPages;

    private View mLoadMoreProgressContainer;

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

        final FloatingActionButton loadMoreBackground = (FloatingActionButton) view
                .findViewById(R.id.load_more_progress_bar_background);
        loadMoreBackground.setBackgroundColor(Color.WHITE);
        mLoadMoreProgressContainer = view.findViewById(R.id.load_more_progress_container);

        final ActionBar actionBar = UIUtils.getSupportActionBar(getActivity());
        actionBar.show();
        actionBar.setTitle(R.string.xda_news);
        actionBar.setSubtitle(null);

        mRecyclerView = (RecyclerView) view.findViewById(android.R.id.list);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new XDALinerLayoutManager(getActivity()));
        ViewCompat.setOverScrollMode(mRecyclerView, ViewCompat.OVER_SCROLL_NEVER);

        // If the listener already exists then tell it about the new recycler view
        if (mInfiniteScrollListener != null) {
            mInfiniteScrollListener.updateRecyclerView(mRecyclerView);
        }

        if (savedInstanceState == null) {
            loadTheFirstPage();
        } else {
            final List<ResponseNews> list = savedInstanceState
                    .getParcelableArrayList(NEWS_SAVED_STATE);
            if (Utils.isCollectionEmpty(list)) {
                loadTheFirstPage();
            } else {
                // This should give a non-zero integer
                mTotalPages = savedInstanceState.getInt(PAGES_SAVED_STATE);
                if (mInfiniteScrollListener == null) {
                    mInfiniteScrollListener = createInfiniteScrollListener(mTotalPages);
                } else {
                    // TODO - Should never happen - investigate if it does
                }
                addDataToAdapter(list);
            }
        }
    }

    private InfiniteRecyclerLoadHelper createInfiniteScrollListener(final int totalPages) {
        return new InfiniteRecyclerLoadHelper(mRecyclerView,
                new InfiniteLoadCallback(), totalPages, null);
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

        mTotalPages = data.getTotalPages();

        if (data.getCurrentPage() == 1) {
            mAdapter.clear();
            mInfiniteScrollListener = createInfiniteScrollListener(data.getTotalPages());
        }
        mLoadMoreProgressContainer.setVisibility(View.GONE);
        mInfiniteScrollListener.onLoadFinished();
        addDataToAdapter(data.getNewsItems());
    }

    private void addDataToAdapter(final List<ResponseNews> data) {
        UIUtils.updateEmptyViewState(getView(), mRecyclerView, data == null ? 0 : data.size());

        // Let's actually add the items now
        mAdapter.addAll(data);
    }

    @Override
    public void onLoaderReset(Loader<ResponseNewsContainer> loader) {
    }

    private class InfiniteLoadCallback implements InfiniteRecyclerLoadHelper.Callback {

        @Override
        public void loadMoreData(final int page) {
            mLoadMoreProgressContainer.setVisibility(View.VISIBLE);

            final Bundle bundle = new Bundle();
            bundle.putInt(CURRENT_PAGE_LOADER_ARGUMENT, page);
            getLoaderManager().restartLoader(0, bundle, NewsFragment.this);
        }
    }
}