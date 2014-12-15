package com.xda.one.ui;

import com.xda.one.R;

import android.app.ActionBar;
import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.SearchView;

public class SearchFragment extends Fragment {

    private WebView mWebView;

    private MenuItem mSearchMenuItem;

    private Callback mCallback;

    private String mCurrentUrl;

    public static Fragment createInstance() {
        return new SearchFragment();
    }

    @Override
    public void onAttach(final Activity activity) {
        super.onAttach(activity);

        mCallback = (Callback) activity;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container,
            @Nullable final Bundle savedInstanceState) {
        mWebView = new WebView(getActivity());
        return mWebView;
    }

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);

        final ActionBar actionBar = getActivity().getActionBar();
        actionBar.show();
        actionBar.setTitle(R.string.search);
        actionBar.setSubtitle(null);

        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(final WebView view, final String url) {
                final Uri uri = Uri.parse(url);
                if ("forum.one-developers.com".equals(uri.getAuthority())) {
                    mCallback.parseAndDisplayForumUrl(url, false);
                } else {
                    // Update the url so that if we come back, we come back to this page
                    mCurrentUrl = url;
                    view.loadUrl(url);
                }
                return true;
            }

            @Override
            public void onPageFinished(WebView view, final String url) {
            }
        });

        if (savedInstanceState != null) {
            mWebView.restoreState(savedInstanceState);
        } else if (mCurrentUrl != null) {
            mWebView.loadUrl(mCurrentUrl);
        }
    }

    @Override
    public void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);

        mWebView.saveState(outState);
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        inflater.inflate(R.menu.search_ab, menu);

        mSearchMenuItem = menu.findItem(R.id.search_ab_search);

        final SearchView searchView = (SearchView) mSearchMenuItem.getActionView();
        final SearchQueryListener listener = new SearchQueryListener();
        searchView.setOnQueryTextListener(listener);

        // TODO - stop this from constantly happening when you go to a item and then come back
        searchView.post(new Runnable() {
            @Override
            public void run() {
                MenuItemCompat.expandActionView(mSearchMenuItem);
            }
        });
    }

    @Override
    public void onPrepareOptionsMenu(final Menu menu) {

    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        return false;
    }

    public boolean onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        }
        return false;
    }

    public interface Callback {

        public void parseAndDisplayForumUrl(final String url, final boolean fromExternal);
    }

    private class SearchQueryListener implements SearchView.OnQueryTextListener {

        @Override
        public boolean onQueryTextSubmit(final String query) {
            if (TextUtils.isEmpty(query)) {
                return false;
            }
            MenuItemCompat.collapseActionView(mSearchMenuItem);

            final String queryFinal = String.format("%s site:forum.one-developers.com/", query);
            final Uri uri = new Uri.Builder().scheme("https").authority("www.googleplus.com")
                    .appendPath("search")
                    .appendQueryParameter("q", queryFinal).build();
            mCurrentUrl = uri.toString();
            mWebView.setVisibility(View.VISIBLE);
            mWebView.loadUrl(mCurrentUrl);
            return true;
        }

        @Override
        public boolean onQueryTextChange(final String query) {
            return false;
        }
    }
}