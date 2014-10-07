package com.xda.one.ui.listener;

import android.support.v7.widget.RecyclerView;

public class InfiniteRecyclerLoadHelper implements RecyclerEndHelper.Callback,
        RecyclerView.OnScrollListener {

    private final Callback mCallback;

    private final RecyclerEndHelper mRecyclerEndHelper;

    private final RecyclerView.OnScrollListener mScrollListener;

    // Pagination stuff
    private int mTotalPages;

    private int mLoadedPage = 1;

    private boolean mLoading = false;

    public InfiniteRecyclerLoadHelper(final RecyclerView recyclerView, final Callback callback,
            final int totalPages, RecyclerView.OnScrollListener scrollListener) {
        mRecyclerEndHelper = new RecyclerEndHelper(recyclerView, this);
        mCallback = callback;
        mTotalPages = totalPages;

        mScrollListener = scrollListener;
        recyclerView.setOnScrollListener(this);
    }

    @Override
    public void onListEndReached() {
        if (mLoadedPage < mTotalPages && !mLoading) {
            ++mLoadedPage;
            mLoading = true;
            mCallback.loadMoreData(mLoadedPage);
        }
    }

    /**
     * This method should be called when the loading is finished
     */
    public void onLoadFinished() {
        mLoading = false;
    }

    @Override
    public void onScrollStateChanged(int newState) {
        if (mScrollListener != null) {
            mScrollListener.onScrollStateChanged(newState);
        }
        mRecyclerEndHelper.onScrollStateChanged(newState);
    }

    @Override
    public void onScrolled(int dx, int dy) {
        if (mScrollListener != null) {
            mScrollListener.onScrolled(dx, dy);
        }
        mRecyclerEndHelper.onScrolled(dx, dy);
    }

    public void updateRecyclerView(RecyclerView recyclerView) {
        mRecyclerEndHelper.updateRecyclerView(recyclerView);
    }

    public boolean isLoading() {
        return mLoading;
    }

    public interface Callback {

        public void loadMoreData(final int page);
    }
}