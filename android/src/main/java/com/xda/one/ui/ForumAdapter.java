package com.xda.one.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xda.one.R;
import com.xda.one.api.model.interfaces.Forum;
import com.xda.one.ui.helper.ActionModeHelper;
import com.xda.one.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class ForumAdapter<T extends Forum>
        extends RecyclerView.Adapter<ForumAdapter.ForumViewHolder> {

    private final LayoutInflater mLayoutInflater;

    private final List<T> mForums;

    private final View.OnClickListener mListener;

    private final View.OnLongClickListener mLongClickListener;

    private final ActionModeHelper mModeHelper;

    private final ImageViewDeviceDelegate mImageViewDeviceDelegate;

    private final SubscribeButtonDelegate mSubscribeButtonDelegate;

    public ForumAdapter(final Context context, final View.OnClickListener listener,
                        final View.OnLongClickListener longClickListener,
                        final ActionModeHelper modeHelper,
                        final ImageViewDeviceDelegate imageViewDeviceDelegate,
                        final SubscribeButtonDelegate buttonDelegate) {
        mLayoutInflater = LayoutInflater.from(context);
        mForums = new ArrayList<>();
        mListener = listener;
        mLongClickListener = longClickListener;
        mModeHelper = modeHelper;
        mImageViewDeviceDelegate = imageViewDeviceDelegate;
        mSubscribeButtonDelegate = buttonDelegate;
    }

    @Override
    public ForumViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        final View view = mLayoutInflater.inflate(R.layout.forum_list_item, viewGroup, false);
        return new ForumViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ForumViewHolder forumHolder, final int position) {
        final T forum = getForum(position);

        // The full item
        forumHolder.itemView.setOnClickListener(mListener);
        forumHolder.itemView.setOnLongClickListener(mLongClickListener);

        if (mModeHelper != null) {
            // Update the activated state of the item
            mModeHelper.updateActivatedState(forumHolder.itemView, position);
        }

        // Setup the device view
        mImageViewDeviceDelegate.setupImageViewDevice(forumHolder.deviceImageView, forum);

        // Title of forum
        forumHolder.titleTextView.setText(forum.getTitle());

        // Setup the subscribe button
        mSubscribeButtonDelegate.setupSubscribeButton(forumHolder.subscribeButton, forum);
    }

    @Override
    public int getItemCount() {
        return mForums.size();
    }

    public T getForum(final int position) {
        return mForums.get(position);
    }

    public void addAll(final List<? extends T> forums) {
        if (Utils.isCollectionEmpty(forums)) {
            return;
        }

        final int count = mForums.size();
        mForums.addAll(forums);
        notifyItemRangeInserted(count, forums.size());
    }

    public void remove(final T item) {
        remove(indexOf(item));
    }

    void remove(final int position) {
        if (position < 0 || position >= mForums.size()) {
            // TODO - maybe throw an exception?
            return;
        }

        mForums.remove(position);
        notifyItemRemoved(position);
    }

    public void clear() {
        final int count = mForums.size();
        mForums.clear();
        notifyItemRangeRemoved(0, count);
    }

    public int indexOf(T forum) {
        return mForums.indexOf(forum);
    }

    public boolean isEmpty() {
        return mForums.isEmpty();
    }

    public static interface ImageViewDeviceDelegate {

        public void setupImageViewDevice(final ImageView imageView, final Forum forum);
    }

    public static interface SubscribeButtonDelegate {

        public void setupSubscribeButton(final ImageView subscribeButton, final Forum forum);
    }

    static class ForumViewHolder extends RecyclerView.ViewHolder {

        public ImageView deviceImageView;

        public TextView titleTextView;

        public ImageView subscribeButton;

        public ForumViewHolder(final View itemView) {
            super(itemView);

            deviceImageView = (ImageView) itemView.findViewById(R.id.forum_list_item_device_image);
            titleTextView = (TextView) itemView.findViewById(R.id.forum_list_item_name);
            subscribeButton = (ImageView) itemView.findViewById(R.id.forum_list_item_subscribe);
        }
    }
}