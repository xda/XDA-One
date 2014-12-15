package com.xda.one.ui;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.xda.one.R;
import com.xda.one.api.misc.Consumer;
import com.xda.one.model.misc.ForumType;
import com.xda.one.ui.helper.UrlParseHelper;
import com.xda.one.util.AccountUtils;
import com.xda.one.util.AnalyticsUtil;
import com.xda.one.util.CrashUtils;
import com.xda.one.util.UIUtils;
import com.xda.one.util.Utils;

import android.app.ActionBar;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Toast;
import com.newrelic.agent.android.NewRelic;

public class MainActivity extends BaseActivity
        implements NavigationDrawerFragment.Callback, SubscribedPagerFragment.Callback,
        ThreadFragment.Callback, PostPagerFragment.Callback, SearchFragment.Callback {

    private DrawerLayout mDrawerLayout;

    private NavigationDrawerFragment mNavigationDrawerFragment;

    private Runnable mLoginSuccessfulRunnable;

    private final String SCREEN_NAME = "XDA-One MainActivity";

    @Override
    public void onCreate(final Bundle bundle) {

        NewRelic.withApplicationToken(
                "AA31aa88f94b9a9db9fba799fdb1112f100438c79f"
        ).start(this.getApplication());

        requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        requestWindowFeature(Window.FEATURE_ACTION_MODE_OVERLAY);
        super.onCreate(bundle);

//        CrashUtils.startCrashlytics(this);

        setContentView(R.layout.main_activity);

        final ActionBar actionBar = getActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setIcon(R.drawable.ic_action_menu);

        startTracker(SCREEN_NAME);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View view, float v) {
            }

            @Override
            public void onDrawerOpened(View view) {
                actionBar.show();
            }

            @Override
            public void onDrawerClosed(View view) {
            }

            @Override
            public void onDrawerStateChanged(int i) {
            }
        });

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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                UIUtils.toggleDrawerLayout(mDrawerLayout);
                return true;
        }
        return false;
    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub

        super.onStart();
        GoogleAnalytics.getInstance(MainActivity.this).reportActivityStart(this);
        //Get an Analytics tracker to report app starts & uncaught exceptions etc.

    }

    private void initialReplaceFragment() {
        Intent intent = getIntent();
        if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            String url = intent.getDataString();
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
        }

        if (!mDrawerLayout.isDrawerOpen(Gravity.LEFT)
                && getSupportFragmentManager().getBackStackEntryCount() == 0) {
            mDrawerLayout.openDrawer(Gravity.LEFT);
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        //Stop the analytics tracking
        GoogleAnalytics.getInstance(MainActivity.this).reportActivityStop(this);
    }

    @Override
    public void closeNavigationDrawer() {
        mDrawerLayout.closeDrawer(Gravity.LEFT);
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
        final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (backStackAndAnimate) {
            transaction.addToBackStack(title)
                    .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left,
                            R.anim.slide_in_left, R.anim.slide_out_right);
        }
        transaction.replace(R.id.content_frame, fragment).commit();
    }

    @Override
    public void login(final Runnable runnable) {
        mLoginSuccessfulRunnable = runnable;
        mNavigationDrawerFragment.login();
    }

    private void startTracker(String screenName){
        // Get tracker.
        Tracker tracker = ((AnalyticsUtil) this.getApplication()).getTracker(
                AnalyticsUtil.TrackerName.APP_TRACKER);

        // Set screen name.
        // Where path is a String representing the screen name.
        tracker.setScreenName(screenName);

        // Send a screen view.
        tracker.send(new HitBuilders.AppViewBuilder().build());
    }
}