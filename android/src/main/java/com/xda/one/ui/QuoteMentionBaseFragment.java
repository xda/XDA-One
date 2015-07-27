package com.xda.one.ui;

import com.xda.one.R;
import com.xda.one.ui.listener.InfiniteRecyclerLoadHelper;
import com.xda.one.ui.widget.DividerItemDecoration;
import com.xda.one.ui.widget.XDARefreshLayout;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.XDALinerLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class QuoteMentionBaseFragment extends Fragment {

    public static final String SAVED_ADAPTER_STATE = "saved_adapter_state";

    protected static final String CURRENT_PAGE_LOADER_ARGUMENT = "current_page";

    // Infinite scrolling
    protected InfiniteRecyclerLoadHelper mInfiniteScrollListener;

    protected RecyclerView mRecyclerView;

    protected XDARefreshLayout mRefreshLayout;

    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container,
            @Nullable final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.quote_mention_fragment, container, false);
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRefreshLayout = (XDARefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        mRefreshLayout.setXDAColourScheme();

        mRecyclerView = (RecyclerView) view.findViewById(android.R.id.list);
        mRecyclerView.setLayoutManager(new XDALinerLayoutManager(getActivity()));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), XDALinerLayoutManager.VERTICAL));
        ViewCompat.setOverScrollMode(mRecyclerView, ViewCompat.OVER_SCROLL_NEVER);

        // If the listener already exists then tell it about the new recycler view
        if (mInfiniteScrollListener != null) {
            mInfiniteScrollListener.updateRecyclerView(mRecyclerView);
        }
    }
}
