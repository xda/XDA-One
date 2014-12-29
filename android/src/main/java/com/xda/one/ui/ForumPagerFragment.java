package com.xda.one.ui;

import com.xda.one.R;
import com.xda.one.api.model.response.ResponseForum;
import com.xda.one.db.ForumDbHelper;
import com.xda.one.model.misc.ForumType;
import com.xda.one.ui.widget.TabLayout;
import com.xda.one.util.FragmentUtils;
import com.xda.one.util.UIUtils;

import android.app.SearchManager;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;

import java.util.ArrayList;

public class ForumPagerFragment extends Fragment {

    private ForumFragmentAdapter mFragmentAdapter;

    private MenuItem mSearchMenuItem;

    public static Fragment createInstance() {
        return new ForumPagerFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.forum_pager_fragment, container, false);
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFragmentAdapter = new ForumFragmentAdapter(getChildFragmentManager());
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);

        final ActionBar bar = UIUtils.getSupportActionBar(getActivity());
        bar.show();
        bar.setTitle(R.string.subscribed);
        bar.setSubtitle(null);
        bar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);

        final ViewPager pager = (ViewPager) view.findViewById(R.id.forum_view_pager);
        pager.setOffscreenPageLimit(3);
        pager.setAdapter(mFragmentAdapter);

        final TabLayout slidingTabLayout = (TabLayout) view
                .findViewById(R.id.pager_tab_strip);
        slidingTabLayout.setSelectedIndicatorColors(getResources().getColor(android.R.color
                .white));
        slidingTabLayout.setViewPager(pager);
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        inflater.inflate(R.menu.forum_pager_ab, menu);

        mSearchMenuItem = menu.findItem(R.id.find_your_device_search);

        // Get the SearchView and set the searchable configuration
        final SearchManager searchManager = (SearchManager) getActivity()
                .getSystemService(Context.SEARCH_SERVICE);

        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(mSearchMenuItem);

        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity()
                .getComponentName()));

        final SearchQueryListener listener = new SearchQueryListener(searchView);
        searchView.setOnQueryTextListener(listener);
        searchView.setOnSuggestionListener(listener);
    }

    public boolean onBackPressed() {
        final boolean isExpanded = MenuItemCompat.isActionViewExpanded(mSearchMenuItem);
        if (isExpanded) {
            MenuItemCompat.collapseActionView(mSearchMenuItem);
        }
        return isExpanded;
    }

    private class ForumFragmentAdapter extends FragmentPagerAdapter {

        private static final int TAB_COUNT = 4;

        private final ForumFragment mGeneralFragment;

        private ForumFragment mTopFragment;

        private ForumFragment mNewFragment;

        private ForumFragment mAllFragment;

        public ForumFragmentAdapter(final FragmentManager fm) {
            super(fm);

            mTopFragment = ForumFragment.createInstance(ForumType.TOP);
            mNewFragment = ForumFragment.createInstance(ForumType.NEWEST);
            mGeneralFragment = ForumFragment.createInstance(ForumType.GENERAL);
            mAllFragment = ForumFragment.createInstance(ForumType.ALL);
        }

        @Override
        public Fragment getItem(int i) {
            switch (i) {
                case 0:
                    return mTopFragment;
                case 1:
                    return mNewFragment;
                case 2:
                    return mGeneralFragment;
                case 3:
                    return mAllFragment;
            }
            throw new UnsupportedOperationException();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.forum_top_title);
                case 1:
                    return getString(R.string.forum_newest_title);
                case 2:
                    return getString(R.string.forum_general_title);
                case 3:
                    return getString(R.string.forum_all_title);
            }
            return super.getPageTitle(position);
        }

        @Override
        public int getCount() {
            return TAB_COUNT;
        }
    }

    private class SearchQueryListener implements SearchView.OnQueryTextListener,
            SearchView.OnSuggestionListener {

        private final SearchView mSearchView;

        public SearchQueryListener(final SearchView searchView) {
            mSearchView = searchView;
        }

        @Override
        public boolean onQueryTextSubmit(final String s) {
            if (TextUtils.isEmpty(s)) {
                return false;
            }
            MenuItemCompat.collapseActionView(mSearchMenuItem);

            final FragmentTransaction transaction = FragmentUtils
                    .getDefaultTransaction(getFragmentManager());
            final FindYourDeviceFragment fragment = FindYourDeviceFragment.createInstance(s);
            transaction.addToBackStack(null);
            transaction.replace(R.id.content_frame, fragment).commit();
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