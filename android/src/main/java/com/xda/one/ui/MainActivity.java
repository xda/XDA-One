package com.xda.one.ui;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import com.xda.one.R;
import com.xda.one.api.misc.Consumer;
import com.xda.one.model.misc.ForumType;
import com.xda.one.ui.helper.UrlParseHelper;
import com.xda.one.util.AccountUtils;
import com.xda.one.util.FragmentUtils;
import com.xda.one.util.OneApplication;

import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends BaseActivity
        implements NavigationDrawerFragment.Callback, SubscribedPagerFragment.Callback,
        ThreadFragment.Callback, PostPagerFragment.Callback, SearchFragment.Callback {

    private static final String SCREEN_NAME = "XDA-One MainActivity";

    private DrawerLayout mDrawerLayout;

    private NavigationDrawerFragment mNavigationDrawerFragment;

    private Runnable mLoginSuccessfulRunnable;

    private ActionBarDrawerToggle mDrawerToggle;

    private Toolbar mToolbar;

    @Override
    public void onCreate(final Bundle bundle) {
        /*NewRelic.withApplicationToken(
                "AA31aa88f94b9a9db9fba799fdb1112f100438c79f"
        ).start(getApplication());*/
        super.onCreate(bundle);

        setContentView(R.layout.main_activity);
        // startTracker(SCREEN_NAME);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setStatusBarBackgroundColor(getResources().getColor(R.color.colorPrimary));

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mDrawerToggle = new MainActionBarToggle(mToolbar);
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (bundle == null) {
            mNavigationDrawerFragment = new NavigationDrawerFragment();
            final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.navigation_drawer_frame, mNavigationDrawerFragment).commit();

            initialReplaceFragment();
        } else {
            mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.navigation_drawer_frame);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // GoogleAnalytics.getInstance(MainActivity.this).reportActivityStart(this);
    }

    private void initialReplaceFragment() {
        if (Intent.ACTION_VIEW.equals(getIntent().getAction())) {
            String url = getIntent().getDataString();
            if (url != null) {
                parseAndDisplayForumUrl(url, true);
                return;
            }
        }
        // Make forum fragment visible
        switchCurrentlyDisplayedFragment(ForumPagerFragment.createInstance());
    }

    @Override
    public void parseAndDisplayForumUrl(final String url, final boolean fromExternal) {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setIndeterminate(true);
        dialog.setTitle("Parsing URL");
        dialog.setMessage("Parsing URL and getting the page.");
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();

        final Uri parsed = Uri.parse(url);
        UrlParseHelper.parseUrl(this, parsed, new Consumer<Fragment>() {
            @Override
            public void run(final Fragment fragment) {
                dialog.dismiss();

                // Fragment can be null if we opened an activity instead of doing this
                if (fragment == null && fromExternal) {
                    switchCurrentlyDisplayedFragment(ForumPagerFragment.createInstance());
                } else if (fragment != null) {
                    switchCurrentlyDisplayedFragment(fragment, !fromExternal, null);
                }
            }
        }, new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
                Toast.makeText(MainActivity.this, R.string.failed_to_get_forum,
                        Toast.LENGTH_LONG).show();
                if (fromExternal) {
                    switchCurrentlyDisplayedFragment(ForumPagerFragment.createInstance());
                }
            }
        });
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();

        if (mLoginSuccessfulRunnable == null) {
            return;
        }
        final Runnable runnable = mLoginSuccessfulRunnable;
        mLoginSuccessfulRunnable = null;
        if (AccountUtils.getAccount(this) != null) {
            runnable.run();
        }
    }

    @Override
    public void onBackPressed() {
        final Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);
        // TODO - fix this hack
        if (fragment instanceof SearchFragment) {
            final SearchFragment searchFragment = (SearchFragment) fragment;
            if (searchFragment.onBackPressed()) {
                return;
            }
        } else if (fragment instanceof ForumPagerFragment) {
            final ForumPagerFragment pagerFragment = (ForumPagerFragment) fragment;
            if (pagerFragment.onBackPressed()) {
                return;
            }
        }

        if (!mDrawerLayout.isDrawerOpen(Gravity.START)
                && getSupportFragmentManager().getBackStackEntryCount() == 0) {
            mDrawerLayout.openDrawer(Gravity.START);
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onStop() {
        super.onStop();

        // GoogleAnalytics.getInstance(MainActivity.this).reportActivityStop(this);
    }

    @Override
    public void closeNavigationDrawer() {
        mDrawerLayout.closeDrawer(Gravity.START);
    }

    @Override
    public void onNavigationItemClicked(final Fragment fragment) {
        final Fragment oldFragment = getSupportFragmentManager()
                .findFragmentById(R.id.content_frame);
        if (oldFragment == null || !oldFragment.getClass().equals(fragment.getClass())) {
            switchCurrentlyDisplayedFragment(fragment);
        } else if (oldFragment instanceof ForumFragment) {
            final ForumFragment currentFragment = (ForumFragment) oldFragment;
            final ForumFragment newFragment = (ForumFragment) fragment;
            final ForumType newType = (ForumType) newFragment.getArguments()
                    .getSerializable(ForumFragment.FORUM_TYPE);
            if (!currentFragment.getForumType().equals(newType)) {
                switchCurrentlyDisplayedFragment(fragment);
            }
        }
    }

    private void switchCurrentlyDisplayedFragment(final Fragment fragment) {
        switchCurrentlyDisplayedFragment(fragment, false, null);
    }

    @Override
    public void switchCurrentlyDisplayedFragment(final Fragment fragment,
            final boolean backStackAndAnimate, final String title) {
        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        final FragmentTransaction transaction = FragmentUtils
                .getDefaultTransaction(getSupportFragmentManager());
        if (backStackAndAnimate) {
            transaction.addToBackStack(title);
        }
        transaction.replace(R.id.content_frame, fragment).commit();
    }

    @Override
    public Toolbar getToolbar() {
        return mToolbar;
    }

    @Override
    public void login(final Runnable runnable) {
        mLoginSuccessfulRunnable = runnable;
        mNavigationDrawerFragment.login();
    }

    private void startTracker(String screenName) {
        final Tracker tracker = getOneApplication()
                .getTracker(OneApplication.TrackerName.APP_TRACKER);
        tracker.setScreenName(screenName);
        tracker.send(new HitBuilders.AppViewBuilder().build());
    }

    private class MainActionBarToggle extends ActionBarDrawerToggle {

        public MainActionBarToggle(final Toolbar toolBar) {
            super(MainActivity.this, mDrawerLayout, toolBar, R.string.drawer_open,
                    R.string.drawer_close);
        }

        @Override
        public void onDrawerOpened(final View drawerView) {
            getSupportActionBar().show();
        }

        @Override
        public void onDrawerClosed(final View drawerView) {
        }

        @Override
        public void onDrawerSlide(final View drawerView, final float slideOffset) {
        }
    }
}