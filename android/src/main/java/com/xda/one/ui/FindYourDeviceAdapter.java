package com.xda.one.ui;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.xda.one.R;
import com.xda.one.api.model.response.ResponseForum;
import com.xda.one.db.ForumDbHelper;

public class FindYourDeviceAdapter
        extends RecyclerView.Adapter<FindYourDeviceAdapter.FindYourDeviceViewHolder> {

    protected final LayoutInflater mLayoutInflater;

    private final Context mContext;

    private final View.OnClickListener mOnClickListener;

    private Cursor mCursor;

    public FindYourDeviceAdapter(final Context context, final View.OnClickListener clickListener) {
        mContext = context;
        mOnClickListener = clickListener;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public FindYourDeviceViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        final View view = mLayoutInflater.inflate(R.layout.find_your_device_list_item, parent,
                false);
        return new FindYourDeviceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final FindYourDeviceViewHolder holder, final int position) {
        final ResponseForum forum = getItem(position);

        holder.itemView.setOnClickListener(mOnClickListener);

        holder.titleView.setText(getItem(position).getTitle());

        Picasso.with(mContext)
                .load(forum.getImageUrl())
                .placeholder(R.drawable.ic_nav_phone)
                .error(R.drawable.ic_nav_phone)
                .into(holder.imageView);
    }

    public ResponseForum getItem(int position) {
        mCursor.moveToPosition(position);
        return ForumDbHelper.getSuggestionFromCursor(mCursor);
    }

    @Override
    public int getItemCount() {
        return mCursor == null ? 0 : mCursor.getCount();
    }

    public void setCursor(final Cursor cursor) {
        mCursor = cursor;
        notifyDataSetChanged();
    }

    public static class FindYourDeviceViewHolder extends RecyclerView.ViewHolder {

        private final TextView titleView;

        private final ImageView imageView;

        public FindYourDeviceViewHolder(View itemView) {
            super(itemView);

            titleView = (TextView) itemView.findViewById(R.id.find_your_device_list_item_title);
            imageView = (ImageView) itemView.findViewById(R.id.find_your_device_list_item_image);
        }
    }
}