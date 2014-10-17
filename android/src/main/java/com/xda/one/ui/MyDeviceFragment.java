package com.xda.one.ui;

import com.squareup.picasso.Picasso;
import com.xda.one.R;
import com.xda.one.api.model.interfaces.Forum;
import com.xda.one.api.model.response.ResponseForum;
import com.xda.one.api.model.response.ResponseUserProfile;
import com.xda.one.loader.UserProfileLoader;
import com.xda.one.ui.helper.ActionModeHelper;
import com.xda.one.ui.widget.XDALinerLayoutManager;
import com.xda.one.util.FragmentUtils;
import com.xda.one.util.UIUtils;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

public class MyDeviceFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<ResponseUserProfile> {

    private ActionModeHelper mModeHelper;

    private RecyclerView mRecyclerView;

    private ForumAdapter<Forum> mAdapter;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mModeHelper = new ActionModeHelper(getActivity(),
                new ActionModeCallback(), new ClickListener(),
                ActionModeHelper.SelectionMode.SINGLE);
        mAdapter = new ForumAdapter<>(getActivity(), mModeHelper, mModeHelper, mModeHelper,
                new ImageViewDeviceDelegate(), new SubscribeButtonDelegate());
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container,
            @Nullable final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.my_device_fragment, container, false);
    }

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRecyclerView = (RecyclerView) view.findViewById(android.R.id.list);
        mRecyclerView.setLayoutManager(new XDALinerLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mAdapter);
        ViewCompat.setOverScrollMode(mRecyclerView, ViewCompat.OVER_SCROLL_NEVER);
        mModeHelper.setRecyclerView(mRecyclerView);

        final ActionBar bar = getActivity().getActionBar();
        bar.show();
        bar.setTitle(R.string.my_devices);
        bar.setSubtitle(null);

        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<ResponseUserProfile> onCreateLoader(final int i, final Bundle bundle) {
        return new UserProfileLoader(getActivity(), null);
    }

    @Override
    public void onLoadFinished(final Loader<ResponseUserProfile> responseUserProfileLoader,
            final ResponseUserProfile profile) {
        mAdapter.clear();

        if (profile != null) {
            mAdapter.addAll(profile.getDevices());
        }
        UIUtils.updateEmptyViewState(getView(), mRecyclerView, mAdapter.getItemCount());
    }

    @Override
    public void onLoaderReset(final Loader<ResponseUserProfile> responseUserProfileLoader) {
    }

    private class SubscribeButtonDelegate implements ForumAdapter.SubscribeButtonDelegate {

        @Override
        public void setupSubscribeButton(final ImageView subscribeButton, final Forum forum) {
            subscribeButton.setVisibility(View.GONE);
        }
    }

    private class ImageViewDeviceDelegate implements ForumAdapter.ImageViewDeviceDelegate {

        @Override
        public void setupImageViewDevice(final ImageView imageView, final Forum forum) {
            Picasso.with(getActivity())
                    .load(forum.getImageUrl())
                    .placeholder(R.drawable.phone)
                    .error(R.drawable.phone)
                    .into(imageView);
        }
    }

    private class ClickListener implements View.OnClickListener {

        @Override
        public void onClick(final View v) {
            final int position = mRecyclerView.getChildPosition(v);
            if (position == RecyclerView.NO_POSITION) {
                return;
            }
            final Forum forum = mAdapter.getForum(position);

            final List<String> hierarchy = new ArrayList<>();
            FragmentUtils.switchToForumContent(getFragmentManager(), null,
                    hierarchy, null, forum);
        }
    }

    private class ActionModeCallback extends ActionModeHelper.RecyclerViewActionModeCallback {

        @Override
        public boolean onCreateActionMode(final ActionMode actionMode, final Menu menu) {
            return true;
        }

        @Override
        public boolean onPrepareActionMode(final ActionMode actionMode, final Menu menu) {
            return true;
        }

        @Override
        public boolean onActionItemClicked(final ActionMode actionMode, final MenuItem menuItem) {
            return true;
        }

        @Override
        public void onCheckedStateChanged(final ActionMode actionMode, int position,
                boolean isNowChecked) {
        }
    }
}