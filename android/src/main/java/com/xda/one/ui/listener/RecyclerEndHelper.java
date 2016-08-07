package com.xda.one.ui.listener;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.XDALinerLayoutManager;

public class RecyclerEndHelper extends RecyclerView.OnScrollListener {

    private final Callback mCallback;

    private XDALinerLayoutManager mLayoutManager;

    private boolean mListEnd;

    private int mCurrentScrollState;

    public RecyclerEndHelper(final RecyclerView recyclerView, final Callback callback) {
        mCallback = callback;
        updateRecyclerView(recyclerView);
    }

    private void isScrollCompleted() {
        if (mCurrentScrollState == RecyclerView.SCROLL_STATE_IDLE && mListEnd) {
            mCallback.onListEndReached();
        }
    }

    @Override
    public void onScrolled(final RecyclerView recyclerView, final int dx, final int dy) {
        mListEnd = mLayoutManager.isListEnd();
    }

    @Override
    public void onScrollStateChanged(final RecyclerView recyclerView, final int newState) {
        mCurrentScrollState = newState;
        isScrollCompleted();
    }

    public void updateRecyclerView(final RecyclerView recyclerView) {
        mLayoutManager = (XDALinerLayoutManager) recyclerView.getLayoutManager();
    }

    public interface Callback {

        void onListEndReached();
    }
}
