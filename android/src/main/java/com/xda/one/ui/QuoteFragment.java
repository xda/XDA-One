package com.xda.one.ui;

import com.xda.one.R;
import com.xda.one.api.inteface.PostClient;
import com.xda.one.api.model.response.container.ResponsePostContainer;
import com.xda.one.api.retrofit.RetrofitPostClient;
import com.xda.one.loader.QuoteLoader;
import com.xda.one.model.augmented.AugmentedQuote;
import com.xda.one.model.augmented.container.AugmentedQuoteContainer;
import com.xda.one.ui.helper.CancellableCallbackHelper;
import com.xda.one.ui.listener.AvatarClickListener;
import com.xda.one.ui.listener.InfiniteRecyclerLoadHelper;
import com.xda.one.util.FragmentUtils;
import com.xda.one.util.UIUtils;
import com.xda.one.util.Utils;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class QuoteFragment extends QuoteMentionBaseFragment
        implements LoaderManager.LoaderCallbacks<AugmentedQuoteContainer> {

    private static final String PAGES_SAVED_STATE = "pages_saved_state";

    private QuoteAdapter mAdapter;

    private int mTotalPages;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAdapter = new QuoteAdapter(getActivity(), new QuoteClickListener(),
                new AvatarClickListener(getActivity()));
    }

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle state) {
        super.onViewCreated(view, state);

        mRecyclerView.setAdapter(mAdapter);

        if (mAdapter.getItemCount() > 0) {
            return;
        }

        if (state == null) {
            loadTheFirstPage();
        } else {
            final List<AugmentedQuote> quotes = state.getParcelableArrayList(SAVED_ADAPTER_STATE);
            if (Utils.isCollectionEmpty(quotes)) {
                loadTheFirstPage();
            } else {
                // This should give a non-zero integer
                mTotalPages = state.getInt(PAGES_SAVED_STATE);
                if (mLoadHelper == null) {
                    mLoadHelper = createInfiniteScrollListener(mTotalPages);
                } else {
                    // TODO - Should never happen - investigate if it does
                }
                addDataToAdapter(quotes);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        mAdapter.onSaveInstanceState(outState);
        outState.putInt(PAGES_SAVED_STATE, mTotalPages);
    }

    private void loadTheFirstPage() {
        final Bundle bundle = new Bundle();
        bundle.putInt(CURRENT_PAGE_LOADER_ARGUMENT, 1);
        getLoaderManager().initLoader(0, bundle, this);
    }

    @Override
    public Loader<AugmentedQuoteContainer> onCreateLoader(int i, Bundle bundle) {
        return new QuoteLoader(getActivity(), bundle.getInt(CURRENT_PAGE_LOADER_ARGUMENT));
    }

    @Override
    public void onLoadFinished(final Loader<AugmentedQuoteContainer> loader,
            final AugmentedQuoteContainer data) {
        if (data == null) {
            // TODO - we need to tailor this to lack of connection/other network issue
            addDataToAdapter(null);
            return;
        }

        mTotalPages = data.getTotalPages();

        if (data.getCurrentPage() == 1) {
            mAdapter.clear();
            mLoadHelper = createInfiniteScrollListener(data.getTotalPages());
        }

        mLoadMoreProgressContainer.setVisibility(View.GONE);
        mLoadHelper.onLoadFinished();
        addDataToAdapter(data.getQuotes());
    }

    @Override
    public void onLoaderReset(Loader<AugmentedQuoteContainer> loader) {
    }

    private InfiniteRecyclerLoadHelper createInfiniteScrollListener(final int totalPages) {
        return new InfiniteRecyclerLoadHelper(mRecyclerView, new InfiniteLoadCallback(),
                totalPages, null);
    }

    private void addDataToAdapter(final List<AugmentedQuote> data) {
        UIUtils.updateEmptyViewState(getView(), mRecyclerView,
                data == null ? 0 : data.size());

        // Let's actually add the items now
        mAdapter.addAll(data);
    }

    private class InfiniteLoadCallback implements InfiniteRecyclerLoadHelper.Callback {

        @Override
        public void loadMoreData(final int page) {
            mLoadMoreProgressContainer.setVisibility(View.VISIBLE);

            final Bundle bundle = new Bundle();
            bundle.putInt(CURRENT_PAGE_LOADER_ARGUMENT, page);
            getLoaderManager().restartLoader(0, bundle, QuoteFragment.this);
        }
    }

    private class QuoteCallback extends CancellableCallbackHelper<ResponsePostContainer> {

        private final AlertDialog mDialog;

        private final AugmentedQuote mQuote;

        public QuoteCallback(final AlertDialog dialog, final AugmentedQuote quote) {
            super(dialog);

            mDialog = dialog;
            mQuote = quote;
        }

        @Override
        public void safeCallback(final ResponsePostContainer data) {
            mDialog.dismiss();

            final Fragment fragment = FragmentUtils.switchToPostList(mQuote.getThread(),
                    new ArrayList<String>(), data);

            final FragmentTransaction transaction = FragmentUtils.getDefaultTransaction
                    (getParentFragment().getFragmentManager());
            transaction.addToBackStack(mQuote.getThread().getTitle());
            transaction.replace(R.id.content_frame, fragment).commit();
        }

        @Override
        public void run() {
            Toast.makeText(mDialog.getContext(), R.string.something_went_wrong_request,
                    Toast.LENGTH_LONG).show();
            mDialog.dismiss();
        }
    }

    private class QuoteClickListener implements View.OnClickListener {

        @Override
        public void onClick(final View v) {
            final int position = mRecyclerView.getChildPosition(v);
            if (position == RecyclerView.NO_POSITION) {
                return;
            }

            final AugmentedQuote quote = mAdapter.getQuote(position);
            final AlertDialog dialog = ProgressDialog.show(getActivity(),
                    "Finding post position", "Finding post position", true, true);

            final QuoteCallback callback = new QuoteCallback(dialog, quote);
            final PostClient client = RetrofitPostClient.getClient(getActivity());
            client.getPostsById(quote.getPostId(), callback, callback);
        }
    }
}