package com.xda.one.ui;

import com.xda.one.R;
import com.xda.one.ui.widget.FloatingActionButton;
import com.xda.one.ui.widget.TabLayout;
import com.xda.one.util.CompatUtils;
import com.xda.one.util.UIUtils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MessagePagerFragment extends Fragment {

    public static final int CREATE_MESSAGE_REQUEST_CODE = 101;

    private ViewPager mViewPager;

    private MessageFragmentAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAdapter = new MessageFragmentAdapter(getActivity(), getChildFragmentManager());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.message_pager_fragment, container, false);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final ActionBar actionBar = UIUtils.getSupportActionBar(getActivity());
        actionBar.show();
        actionBar.setTitle(R.string.private_messages);

        mViewPager = (ViewPager) view.findViewById(R.id.message_view_pager);
        mViewPager.setAdapter(mAdapter);

        final TabLayout tabLayout = (TabLayout) view
                .findViewById(R.id.pager_tab_strip);
        tabLayout.setSelectedIndicatorColors(getResources().getColor(android.R.color.white));
        tabLayout.setViewPager(mViewPager);

        final FloatingActionButton button = (FloatingActionButton) view
                .findViewById(R.id.message_fragment_action_create);

        final Fragment holder = this;

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final DialogFragment fragment = CreateMessageFragment.createInstance();
                fragment.setTargetFragment(holder, CREATE_MESSAGE_REQUEST_CODE);
                fragment.show(getFragmentManager(), "createMessage");
            }
        });

        if (CompatUtils.hasLollipop()) {
            CompatUtils.setBackground(button,
                    getResources().getDrawable(R.drawable.fab_background));
        } else {
            button.setBackgroundColor(getResources().getColor(R.color.fab_color));
        }
    }

    public MessageFragment getCurrentFragment() {
        return (MessageFragment) mAdapter.instantiateItem(mViewPager, mViewPager.getCurrentItem());
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        getCurrentFragment().onActivityResult(requestCode, resultCode, data);
    }

    public enum MessageContainerType {
        INBOX,
        OUTBOX
    }

    public static class MessageFragmentAdapter extends FragmentPagerAdapter {

        private final static int TAB_COUNT = 2;

        private final Context mContext;

        private MessageFragment mInboxFragment;

        private MessageFragment mSentFragment;

        public MessageFragmentAdapter(final Context context, final FragmentManager fm) {
            super(fm);
            mContext = context;

            mInboxFragment = MessageFragment.getInstance(MessageContainerType.INBOX);
            mSentFragment = MessageFragment.getInstance(MessageContainerType.OUTBOX);
        }

        @Override
        public MessageFragment getItem(final int i) {
            switch (i) {
                case 0:
                    return mInboxFragment;
                case 1:
                    return mSentFragment;
            }
            throw new UnsupportedOperationException();
        }

        @Override
        public CharSequence getPageTitle(final int position) {
            switch (position) {
                case 0:
                    return mContext.getString(R.string.inbox_title);
                case 1:
                    return mContext.getString(R.string.sent_items_title);
            }
            throw new UnsupportedOperationException();
        }

        @Override
        public int getCount() {
            return TAB_COUNT;
        }
    }
}