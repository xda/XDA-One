package com.xda.one.ui;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.xda.one.R;
import com.xda.one.api.inteface.PostClient;
import com.xda.one.api.model.response.container.ResponsePostContainer;
import com.xda.one.api.retrofit.RetrofitPostClient;
import com.xda.one.loader.MentionLoader;
import com.xda.one.model.augmented.AugmentedMention;
import com.xda.one.model.augmented.container.AugmentedMentionContainer;
import com.xda.one.ui.helper.CancellableCallbackHelper;
import com.xda.one.ui.listener.AvatarClickListener;
import com.xda.one.ui.listener.InfiniteRecyclerLoadHelper;
import com.xda.one.util.FragmentUtils;
import com.xda.one.util.UIUtils;
import com.xda.one.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class MentionFragment extends QuoteMentionBaseFragment
        implements LoaderManager.LoaderCallbacks<AugmentedMentionContainer> {

    private static final String PAGES_SAVED_STATE = "pages_saved_state";

    private MentionAdapter mAdapter;

    private int mTotalPages;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAdapter = new MentionAdapter(getActivity(), new MentionClickListener(),
                new AvatarClickListener(getActivity()));
    }

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle state) {
        super.onViewCreated(view, state);

        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                UIUtils.updateEmptyViewState(getView(), mRecyclerView, mAdapter.isEmpty());
                reloadTheFirstPage();
            }
        });
        mRecyclerView.setAdapter(mAdapter);

        if (!mAdapter.isEmpty()) {
            return;
        }
        if (state == null) {
            loadTheFirstPage();
        } else {
            final List<AugmentedMention> mentions = state
                    .getParcelableArrayList(SAVED_ADAPTER_STATE);
            if (Utils.isCollectionEmpty(mentions)) {
                loadTheFirstPage();
            } else {
                // This should give a non-zero integer
                mTotalPages = state.getInt(PAGES_SAVED_STATE);
                mInfiniteScrollListener = new InfiniteRecyclerLoadHelper(mRecyclerView,
                        new InfiniteLoadCallback(), mTotalPages, null);
                addDataToAdapter(mentions);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        final ArrayList<AugmentedMention> mentions = new ArrayList<>(mAdapter.getMentions());
        outState.putParcelableArrayList(MentionFragment.SAVED_ADAPTER_STATE, mentions);
        outState.putInt(PAGES_SAVED_STATE, mTotalPages);
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
    public Loader<AugmentedMentionContainer> onCreateLoader(int i, Bundle bundle) {
        return new MentionLoader(getActivity(), bundle.getInt(CURRENT_PAGE_LOADER_ARGUMENT));
    }

    @Override
    public void onLoadFinished(final Loader<AugmentedMentionContainer> loader,
                               final AugmentedMentionContainer data) {
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

        addDataToAdapter(data.getMentions());
        if (!mInfiniteScrollListener.hasMoreData()) {
            mAdapter.removeFooter();
        }
    }

    @Override
    public void onLoaderReset(Loader<AugmentedMentionContainer> loader) {
    }

    private void addDataToAdapter(final List<AugmentedMention> data) {
        UIUtils.updateEmptyViewState(getView(), mRecyclerView,
                data == null ? 0 : data.size());

        // Let's actually add the items now
        mAdapter.addAll(data);
        mRefreshLayout.setRefreshing(false);
    }

    private class InfiniteLoadCallback implements InfiniteRecyclerLoadHelper.Callback {

        @Override
        public void loadMoreData(final int page) {
            final Bundle bundle = new Bundle();
            bundle.putInt(CURRENT_PAGE_LOADER_ARGUMENT, page);
            getLoaderManager().restartLoader(0, bundle, MentionFragment.this);
        }
    }

    private class MentionCallback extends CancellableCallbackHelper<ResponsePostContainer> {

        private final AlertDialog mDialog;

        private final AugmentedMention mMention;

        public MentionCallback(final AlertDialog dialog, final AugmentedMention mention) {
            super(dialog);

            mDialog = dialog;
            mMention = mention;
        }

        @Override
        public void safeCallback(final ResponsePostContainer data) {
            mDialog.dismiss();

            final Fragment fragment = FragmentUtils.switchToPostList(mMention.getThread(),
                    new ArrayList<String>(), data);

            final FragmentTransaction transaction = FragmentUtils.getDefaultTransaction
                    (getParentFragment().getFragmentManager());
            transaction.addToBackStack(mMention.getThread().getTitle());
            transaction.replace(R.id.content_frame, fragment).commit();
        }

        @Override
        public void run() {
            Toast.makeText(mDialog.getContext(), R.string.something_went_wrong_request,
                    Toast.LENGTH_LONG).show();
        }
    }

    private class MentionClickListener implements View.OnClickListener {

        @Override
        public void onClick(final View v) {
            final int position = mRecyclerView.getChildPosition(v);
            if (position == RecyclerView.NO_POSITION) {
                return;
            }

            final AugmentedMention mention = mAdapter.getMention(position);
            final AlertDialog dialog = ProgressDialog.show(getActivity(),
                    "Finding post position", "Finding post position", true, true);

            final MentionCallback mentionCallback = new MentionCallback(dialog, mention);

            final PostClient client = RetrofitPostClient.getClient(getActivity());
            client.getPostsById(mention.getPostId(), mentionCallback, mentionCallback);
        }
    }
}
