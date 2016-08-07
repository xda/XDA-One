package com.xda.one.ui;

import com.xda.one.R;
import com.xda.one.api.model.interfaces.Forum;
import com.xda.one.api.model.response.ResponseUserProfile;
import com.xda.one.loader.UserProfileLoader;
import com.xda.one.ui.widget.ObservableScrollView;
import com.xda.one.util.Utils;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

public class UserProfileAboutFragment extends Fragment implements ObservableScrollView.Callbacks,
        LoaderManager.LoaderCallbacks<ResponseUserProfile> {

    private static final String USER_ID_ARGUMENT = "user_id";

    protected View mEmptyView;

    private ObservableScrollView mScrollView;

    private Callback mCallback;

    private View mAboutView;

    private LayoutInflater mLayoutInflater;

    private String mUserId;

    private LinearLayout mItemContainer;

    private ResponseUserProfile mResponseUserProfile;

    public static UserProfileAboutFragment createInstance(final String userId) {
        final Bundle bundle = new Bundle();
        bundle.putString(USER_ID_ARGUMENT, userId);

        final UserProfileAboutFragment fragment = new UserProfileAboutFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onAttach(final Activity activity) {
        super.onAttach(activity);

        mCallback = (Callback) getParentFragment();
        mLayoutInflater = LayoutInflater.from(activity);
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mUserId = getArguments().getString(USER_ID_ARGUMENT);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container,
            @Nullable final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_profile_about, container, false);
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mEmptyView = view.findViewById(android.R.id.empty);

        mScrollView = (ObservableScrollView) view.findViewById(R.id.about_scroll_view);
        mScrollView.addCallbacks(this);

        mAboutView = view.findViewById(R.id.about_dummy);
        mAboutView.post(new Runnable() {
            @Override
            public void run() {
                mCallback.updateHeaderHeights(view.getHeight());
                mCallback.setupScrolling(mAboutView, UserProfileAboutFragment.this);
            }
        });

        mItemContainer = (LinearLayout) view.findViewById(R.id.about_item_container);
        getLoaderManager().initLoader(0, null, this);
    }

    private void onUserProfileLoaded() {
        mEmptyView.setVisibility(View.GONE);
        mCallback.updateHeader(mResponseUserProfile);

        mItemContainer.addView(getUserView(mItemContainer));
        mItemContainer.addView(getPostsView(mItemContainer));
    }

    public void onPageDisplayed() {
        // Reposition the header bar -- it's normally anchored to the top of the content,
        // but locks to the top of the screen on scroll
        final int scrollY = mScrollView.getScrollY();
        mCallback.onScrolled(scrollY);
    }

    @Override
    public void onScrollChanged(final int deltaX, final int deltaY) {
        // Reposition the header bar -- it's normally anchored to the top of the content,
        // but locks to the top of the screen on scroll
        final int scrollY = mScrollView.getScrollY();
        mCallback.onScrolled(scrollY);
    }

    private View getUserView(final ViewGroup container) {
        final View view = mLayoutInflater.inflate(R.layout.user_profile_about_user_list_item,
                container, false);

        final TextView emailTextView = (TextView) view.findViewById(R.id
                .user_profile_about_user_list_user_email);
        final String email = mResponseUserProfile.getEmail();
        if (TextUtils.isEmpty(email)) {
            emailTextView.setVisibility(View.GONE);
        } else {
            emailTextView.setText(String.format(getString(R.string.user_profile_email), email));
        }

        final TextView titleTextView = (TextView) view
                .findViewById(R.id.user_profile_about_user_list_user_title);
        final String title = mResponseUserProfile.getUserTitle();
        if (TextUtils.isEmpty(title)) {
            titleTextView.setVisibility(View.GONE);
        } else {
            titleTextView.setText(String.format(getString(R.string.user_profile_title), title));
        }

        final TextView signatureTextView = (TextView) view.findViewById(R.id
                .user_profile_about_user_list_signature);
        final CharSequence signature = mResponseUserProfile.getParsedSignature();
        if (TextUtils.isEmpty(signature)) {
            signatureTextView.setVisibility(View.GONE);
        } else {
            signatureTextView.setText(mResponseUserProfile.getParsedSignature());
            signatureTextView.setMovementMethod(LinkMovementMethod.getInstance());
        }

        final TextView devicesTextView = (TextView) view.findViewById(R.id
                .user_profile_about_user_list_devices);
        final List<? extends Forum> devices = mResponseUserProfile.getDevices();
        if (Utils.isCollectionEmpty(devices)) {
            devicesTextView.setVisibility(View.GONE);
        } else {
            final StringBuilder builder = new StringBuilder();
            for (int i = 0, size = devices.size(); i < size; i++) {
                final Forum device = devices.get(i);
                if (i != 0) {
                    builder.append(", ");
                }
                builder.append(device.getTitle());
            }
            devicesTextView.setText(getString(R.string.user_profile_devices, builder.toString()));
        }

        return view;
    }

    private View getPostsView(final ViewGroup container) {
        final View view = mLayoutInflater.inflate(R.layout.user_profile_about_posts_list_item,
                container, false);

        final TextView postCount = (TextView) view
                .findViewById(R.id.user_profile_about_posts_list_user_posts);
        postCount.setText(getString(R.string.user_profile_post_count,
                mResponseUserProfile.getPosts()));

        final TextView thankedPosts = (TextView) view
                .findViewById(R.id.user_profile_about_posts_list_user_thanked_posts);
        thankedPosts.setText(getString(R.string.user_profile_thanked_posts_count,
                mResponseUserProfile.getThankedPosts()));
        return view;
    }

    public void setTopPadding(final int topPadding) {
        final RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)
                mEmptyView.getLayoutParams();
        if (params.topMargin != topPadding + 16) {
            params.topMargin = topPadding + 16;
            mEmptyView.setLayoutParams(params);
        }
    }

    @Override
    public Loader<ResponseUserProfile> onCreateLoader(final int i, final Bundle bundle) {
        return new UserProfileLoader(getActivity(), mUserId);
    }

    @Override
    public void onLoadFinished(final Loader<ResponseUserProfile> responseUserProfileLoader,
            final ResponseUserProfile profile) {
        mResponseUserProfile = profile;
        onUserProfileLoaded();
    }

    @Override
    public void onLoaderReset(final Loader<ResponseUserProfile> responseUserProfileLoader) {
    }

    public interface Callback {

        void updateHeaderHeights(final int viewHeight);

        void onScrolled(int scrollY);

        void setupScrolling(View aboutView,
                            UserProfileAboutFragment userProfileAboutFragment);

        void updateHeader(ResponseUserProfile profile);
    }
}