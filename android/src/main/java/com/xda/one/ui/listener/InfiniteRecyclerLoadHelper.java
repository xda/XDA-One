package com.xda.one.ui.listener;

import android.support.v7.widget.RecyclerView;

public class InfiniteRecyclerLoadHelper extends RecyclerView.OnScrollListener
        implements RecyclerEndHelper.Callback {

    private final Callback mCallback;

    private final RecyclerEndHelper mRecyclerEndHelper;

    private final RecyclerView.OnScrollListener mScrollListener;

    // Pagination stuff
    private int mTotalPages;

    private int mLoadedPage = 1;

    private boolean mLoading = false;

    public InfiniteRecyclerLoadHelper(final RecyclerView recyclerView, final Callback callback,
            final int totalPages, final RecyclerView.OnScrollListener scrollListener) {
        mRecyclerEndHelper = new RecyclerEndHelper(recyclerView, this);
        mCallback = callback;
        mTotalPages = totalPages;

        mScrollListener = scrollListener;
        recyclerView.setOnScrollListener(this);
    }

    @Override
    public void onListEndReached() {
        if (mLoadedPage < mTotalPages && !mLoading) {
            mLoading = true;
            mCallback.loadMoreData(++mLoadedPage);
        }
    }

    /**
     * This method should be called when the loading is finished
     */
    public void onLoadFinished() {
        mLoading = false;
    }

    public boolean hasMoreData() {
        return mLoadedPage < mTotalPages;
    }

    @Override
    public void onScrollStateChanged(final RecyclerView recyclerView, final int newState) {
        if (mScrollListener != null) {
            mScrollListener.onScrollStateChanged(recyclerView, newState);
        }
        mRecyclerEndHelper.onScrollStateChanged(recyclerView, newState);
    }

    @Override
    public void onScrolled(final RecyclerView recyclerView, final int dx, final int dy) {
        if (mScrollListener != null) {
            mScrollListener.onScrolled(recyclerView, dx, dy);
        }
        mRecyclerEndHelper.onScrolled(recyclerView, dx, dy);
    }

    public void updateRecyclerView(RecyclerView recyclerView) {
        mRecyclerEndHelper.updateRecyclerView(recyclerView);
    }

    public boolean isLoading() {
        return mLoading;
    }

    public interface Callback {

        void loadMoreData(final int page);
    }
}