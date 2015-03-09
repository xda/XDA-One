package com.xda.one.ui;

import com.xda.one.R;
import com.xda.one.api.model.interfaces.Forum;
import com.xda.one.api.model.response.ResponseForum;
import com.xda.one.db.ForumDbHelper;
import com.xda.one.loader.FindYouDeviceLoader;
import com.xda.one.util.FragmentUtils;
import com.xda.one.util.UIUtils;

import android.app.SearchManager;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.XDALinerLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class FindYourDeviceFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String QUERY_ARGUMENT = "query_argument";

    private String mQuery;

    private RecyclerView mRecyclerView;

    private FindYourDeviceAdapter mAdapter;

    private View mEmptyView;

    private MenuItem mSearchMenuItem;

    public static FindYourDeviceFragment createInstance() {
        return new FindYourDeviceFragment();
    }

    public static FindYourDeviceFragment createInstance(final String query) {
        final Bundle bundle = new Bundle();
        bundle.putString(QUERY_ARGUMENT, query);

        final FindYourDeviceFragment fragment = new FindYourDeviceFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mQuery = getArguments().getString(QUERY_ARGUMENT);
        }

        mAdapter = new FindYourDeviceAdapter(getActivity(), new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                final int position = mRecyclerView.getChildPosition(view);
                if (position == RecyclerView.NO_POSITION) {
                    return;
                }

                final Forum forum = mAdapter.getItem(position);
                final FragmentTransaction transaction = FragmentUtils.getDefaultTransaction
                        (getFragmentManager());
                // TODO - what should be done here?
                transaction.addToBackStack(null);

                final ForumFragment fragment = ForumFragment.createInstance(forum, null,
                        new ArrayList<String>());
                transaction.replace(R.id.content_frame, fragment).commit();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.find_your_device_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);

        mRecyclerView = (RecyclerView) view.findViewById(android.R.id.list);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new XDALinerLayoutManager(getActivity()));
        ViewCompat.setOverScrollMode(mRecyclerView, ViewCompat.OVER_SCROLL_NEVER);

        // Set empty view to gone
        mEmptyView = view.findViewById(android.R.id.empty);
        mEmptyView.setVisibility(View.GONE);

        final ActionBar actionBar = UIUtils.getSupportActionBar(getActivity());
        actionBar.setSubtitle(null);

        if (mQuery == null) {
            actionBar.setTitle(R.string.find_your_device);
        } else {
            actionBar.setTitle(mQuery);
            if (mAdapter.getItemCount() == 0) {
                // Show the loading view
                UIUtils.showLoadingProgress(mRecyclerView, mEmptyView);
                getLoaderManager().initLoader(0, null, FindYourDeviceFragment.this);
            }
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new FindYouDeviceLoader(getActivity(), mQuery);
    }

    @Override
    public void onLoadFinished(final Loader<Cursor> ResponseSearchDevice,
            final Cursor data) {
        mAdapter.setCursor(data);
        UIUtils.updateEmptyViewState(getView(), mRecyclerView, mAdapter.getItemCount());
    }

    @Override
    public void onLoaderReset(final Loader<Cursor> loader) {
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        inflater.inflate(R.menu.find_device_ab, menu);

        mSearchMenuItem = menu.findItem(R.id.find_your_device_search);

        // Get the SearchView and set the searchable configuration
        final SearchManager searchManager = (SearchManager) getActivity()
                .getSystemService(Context.SEARCH_SERVICE);

        final SearchView searchView = (SearchView) mSearchMenuItem.getActionView();
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity()
                .getComponentName()));

        final SearchQueryListener listener = new SearchQueryListener(searchView);
        searchView.setOnQueryTextListener(listener);
        searchView.setOnSuggestionListener(listener);

        searchView.post(new Runnable() {
            @Override
            public void run() {
                if (mQuery == null) {
                    MenuItemCompat.expandActionView(mSearchMenuItem);
                } else {
                    MenuItemCompat.collapseActionView(mSearchMenuItem);
                }
            }
        });
    }

    public boolean onBackPressed() {
        final boolean isExpanded = MenuItemCompat.isActionViewExpanded(mSearchMenuItem);
        if (isExpanded) {
            MenuItemCompat.collapseActionView(mSearchMenuItem);
        }
        return isExpanded;
    }

    private class SearchQueryListener implements SearchView.OnQueryTextListener,
            SearchView.OnSuggestionListener {

        private final SearchView mSearchView;

        public SearchQueryListener(final SearchView searchView) {
            mSearchView = searchView;
        }

        @Override
        public boolean onQueryTextSubmit(final String query) {
            if (TextUtils.isEmpty(query)) {
                return false;
            }
            MenuItemCompat.collapseActionView(mSearchMenuItem);

            mQuery = query;
            final ActionBar actionBar = ((BaseActivity) getActivity()).getSupportActionBar();
            actionBar.setTitle(mQuery);
            getLoaderManager().restartLoader(0, null, FindYourDeviceFragment.this);

            return true;
        }

        @Override
        public boolean onQueryTextChange(final String query) {
            return false;
        }

        @Override
        public boolean onSuggestionSelect(final int position) {
            return false;
        }

        @Override
        public boolean onSuggestionClick(final int position) {
            final Cursor cursor = mSearchView.getSuggestionsAdapter().getCursor();
            final ResponseForum forum = ForumDbHelper.getSuggestionFromCursor(cursor);
            FragmentUtils.switchToForumContent(getFragmentManager(), null,
                    new ArrayList<String>(), null, forum);
            return true;
        }
    }
}