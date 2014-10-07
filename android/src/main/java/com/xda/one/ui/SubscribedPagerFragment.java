package com.xda.one.ui;

import com.xda.one.R;
import com.xda.one.ui.widget.TabLayout;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SubscribedPagerFragment extends Fragment implements SubscribedForumFragment.Callback {

    private Callback mCallback;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mCallback = (Callback) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.subscribed_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final ActionBar bar = getActivity().getActionBar();
        bar.show();
        bar.setTitle(R.string.subscribed);
        bar.setSubtitle(null);

        final SubscribeFragmentAdapter fragmentAdapter = new SubscribeFragmentAdapter
                (getChildFragmentManager());

        final ViewPager pager = (ViewPager) view.findViewById(R.id.subscribed_view_pager);
        pager.setAdapter(fragmentAdapter);

        final TabLayout tabLayout = (TabLayout) view
                .findViewById(R.id
                        .pager_tab_strip);
        tabLayout.setSelectedIndicatorColors(getResources().getColor(android.R.color
                .white));
        tabLayout.setViewPager(pager);
    }

    @Override
    public void switchCurrentlyDisplayedFragment(final Fragment fragment,
            final boolean backStackAndAnimate, final String title) {
        mCallback.switchCurrentlyDisplayedFragment(fragment, backStackAndAnimate, title);
    }

    public interface Callback {

        public void switchCurrentlyDisplayedFragment(final Fragment fragment,
                final boolean backStackAndAnimate, final String title);
    }

    private class SubscribeFragmentAdapter extends FragmentPagerAdapter {

        private static final int TAB_COUNT = 2;

        private SubscribedForumFragment mSubscribedForumFragment;

        private SubscribedThreadFragment mSubscribedThreadFragment;

        public SubscribeFragmentAdapter(final FragmentManager fm) {
            super(fm);

            mSubscribedForumFragment = new SubscribedForumFragment();
            mSubscribedThreadFragment = new SubscribedThreadFragment();
        }

        @Override
        public Fragment getItem(int i) {
            switch (i) {
                case 0:
                    return mSubscribedForumFragment;
                case 1:
                    return mSubscribedThreadFragment;
                default:
                    throw new UnsupportedOperationException();
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.forums);
                case 1:
                    return getString(R.string.threads);
                default:
                    return super.getPageTitle(position);
            }
        }

        @Override
        public int getCount() {
            return TAB_COUNT;
        }
    }
}