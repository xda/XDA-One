package com.xda.one.ui;

import com.xda.one.R;
import com.xda.one.ui.widget.TabLayout;
import com.xda.one.util.UIUtils;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class QuoteMentionFragment extends Fragment {

    private QuoteMentionFragmentPagerAdapter mAdapter;

    private TabLayout mSlidingTabLayout;

    public static QuoteMentionFragment getInstance() {
        return new QuoteMentionFragment();
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAdapter = new QuoteMentionFragmentPagerAdapter(getChildFragmentManager());
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
            final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.quote_mention_fragment, container, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final ViewPager viewPager = (ViewPager) view.findViewById(R.id.forum_view_pager);
        viewPager.setAdapter(mAdapter);
        viewPager.setOffscreenPageLimit(10);

        mSlidingTabLayout = (TabLayout) view.findViewById(R.id
                .fragment_sliding_tab_layout);
        mSlidingTabLayout.setSelectedIndicatorColors(getResources().getColor(android.R.color
                .white));
        mSlidingTabLayout.setViewPager(viewPager);

        final ActionBar bar = UIUtils.getSupportActionBar(getActivity());
        bar.show();
        bar.setTitle(R.string.quote_mentions);
        bar.setSubtitle(null);
    }

    private class QuoteMentionFragmentPagerAdapter extends FragmentPagerAdapter {

        public QuoteMentionFragmentPagerAdapter(final FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            switch (i) {
                case 0:
                    return new QuoteFragment();
                case 1:
                    return new MentionFragment();
            }
            return null;
        }

        @Override
        public CharSequence getPageTitle(final int position) {
            switch (position) {
                case 0:
                    return getString(R.string.quotes);
                case 1:
                    return getString(R.string.mentions);
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}