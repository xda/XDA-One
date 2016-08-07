package com.xda.one.ui;

import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.xda.one.R;
import com.xda.one.api.model.response.ResponseUserProfile;
import com.xda.one.ui.widget.TabLayout;
import com.xda.one.util.CompatUtils;
import com.xda.one.util.UIUtils;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class UserProfileFragment extends Fragment
        implements UserProfileAboutFragment.Callback {

    private static final String USER_ID_ARGUMENT = "user_id";

    private static final float PHOTO_ASPECT_RATIO = 5f;

    private View mPhotoViewContainer;

    private ImageView mPhotoView;

    private View mHeaderBackground;

    private View mHeaderContent;

    private int mActionBarSize;

    private int mPhotoHeightPixels;

    private int mHeaderHeightPixels;

    private UserProfileFragmentPagerAdapter mAdapter;

    private TabLayout mSlidingTabLayout;

    private int mSlidingTabLayoutHeightPixels;

    private ImageView mAvatar;

    private String mUserId;

    private TextView mUsernameTextView;

    private TextView mTagTextView;

    public static UserProfileFragment createInstance(final String userId) {
        final Bundle bundle = new Bundle();
        bundle.putString(USER_ID_ARGUMENT, userId);

        UserProfileFragment fragment = new UserProfileFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mUserId = getArguments().getString(USER_ID_ARGUMENT);
        mAdapter = new UserProfileFragmentPagerAdapter(getChildFragmentManager());
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final @Nullable ViewGroup container,
                             final @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.user_profile_fragment, container, false);
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);

        mHeaderBackground = view.findViewById(R.id.user_profile_header_background);

        mUsernameTextView = (TextView) view.findViewById(R.id.header_user_name);
        mTagTextView = (TextView) view.findViewById(R.id.header_user_stuff);

        mAvatar = (ImageView) view.findViewById(R.id.avatar);
        CompatUtils.setBackground(mAvatar, new ColorDrawable(getResources()
                .getColor(android.R.color.white)));

        mPhotoViewContainer = view.findViewById(R.id.session_photo_container);
        mPhotoView = (ImageView) view.findViewById(R.id.session_photo);

        mHeaderContent = view.findViewById(R.id.header_user_profile_content);

        final ViewPager viewPager = (ViewPager) view.findViewById(R.id.forum_view_pager);
        viewPager.setAdapter(mAdapter);
        viewPager.setOffscreenPageLimit(10);

        // final ActionBar actionBar = UIUtils.getSupportActionBar(getActivity());
        // actionBar.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        mSlidingTabLayout = (TabLayout) view.findViewById(R.id.fragment_sliding_tab_layout);
        mSlidingTabLayout.setSelectedIndicatorColors(Color.WHITE);
        mSlidingTabLayout.setViewPager(viewPager);
        mSlidingTabLayout.setOnPageChangeListener(new PageChangeListener(viewPager));
    }

    @Override
    public void updateHeaderHeights(final int viewHeight) {
        mActionBarSize = UIUtils.calculateActionBarSize(getActivity());
        mHeaderHeightPixels = mHeaderContent.getHeight();

        mPhotoHeightPixels = (int) (mPhotoView.getWidth() / PHOTO_ASPECT_RATIO);
        mPhotoHeightPixels = Math.min(mPhotoHeightPixels, viewHeight * 2 / 3);

        mSlidingTabLayoutHeightPixels = mSlidingTabLayout.getHeight();

        final ViewGroup.LayoutParams lp;
        lp = mPhotoView.getLayoutParams();
        if (lp.height != mPhotoHeightPixels) {
            lp.height = mPhotoHeightPixels;
            mPhotoView.setLayoutParams(lp);
        }
    }

    @Override
    public void onScrolled(final int scrollY) {
        final float newTop = Math.max(mActionBarSize - mHeaderHeightPixels
                + mSlidingTabLayoutHeightPixels, mPhotoHeightPixels - scrollY);
        mHeaderContent.setTranslationY(newTop);
        mAvatar.setTranslationY(newTop);

        final float alpha = (float) Math.min(scrollY, mPhotoHeightPixels - mActionBarSize) /
                (float) (mPhotoHeightPixels - mActionBarSize);
        mHeaderBackground.setAlpha(alpha);

        // Move background photo (parallax effect)
        mPhotoViewContainer.setTranslationY(-scrollY * 0.5f);
    }

    @Override
    public void setupScrolling(final View itemView, final UserProfileAboutFragment fragment) {
        final ViewGroup.LayoutParams params = itemView.getLayoutParams();
        if (params.height != mPhotoHeightPixels + mHeaderHeightPixels) {
            params.height = mPhotoHeightPixels + mHeaderHeightPixels;
            itemView.setLayoutParams(params);
        }

        fragment.setTopPadding(mPhotoHeightPixels + mHeaderHeightPixels);
        fragment.onScrollChanged(0, 0); // trigger scroll handling
    }

    @Override
    public void updateHeader(final ResponseUserProfile profile) {
        mUsernameTextView.setText(profile.getUserName());

        mTagTextView.setText(String.format("Thanked count: %d",
                profile.getThankedTimes()));

        Picasso.with(getActivity())
                .load(profile.getAvatarUrl())
                .placeholder(R.drawable.account_circle)
                .error(R.drawable.account_circle)
                .into(mAvatar);
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        inflater.inflate(R.menu.user_profile_ab, menu);
    }

    @Override
    public void onPrepareOptionsMenu(final Menu menu) {
        final MenuItem item = menu.findItem(R.id.user_profile_ab_pm);
        item.setVisible(mUserId != null);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.user_profile_ab_pm:
                final String username = mUsernameTextView.getText().toString();
                final DialogFragment fragment = CreateMessageFragment.createInstance(username);
                fragment.show(getFragmentManager(), "createMessage");
                return true;
        }
        return false;
    }

    private class UserProfileFragmentPagerAdapter extends FragmentPagerAdapter {

        public UserProfileFragmentPagerAdapter(final FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            switch (i) {
                case 0:
                    return UserProfileAboutFragment.createInstance(mUserId);
            }
            return null;
        }

        @Override
        public CharSequence getPageTitle(final int position) {
            switch (position) {
                case 0:
                    return getString(R.string.about);
            }
            return null;
        }

        @Override
        public int getCount() {
            return 1;
        }
    }

    private class PageChangeListener extends ViewPager.SimpleOnPageChangeListener {

        private final ViewPager mViewPager;

        public PageChangeListener(final ViewPager viewPager) {
            mViewPager = viewPager;
        }

        @Override
        public void onPageSelected(int position) {
            final UserProfileAboutFragment fragment = (UserProfileAboutFragment) mAdapter
                    .instantiateItem(mViewPager, position);
            fragment.onPageDisplayed();
        }
    }
}