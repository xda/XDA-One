package com.xda.one.ui;

import com.squareup.picasso.Picasso;
import com.xda.one.R;
import com.xda.one.api.model.interfaces.Message;
import com.xda.one.util.Utils;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private final Context mContext;

    private final LayoutInflater mLayoutInflater;

    private final List<Message> mMessages;

    private final View.OnClickListener mViewClickListener;

    private final View.OnClickListener mAvatarClickListner;

    public MessageAdapter(final Context context, final View.OnClickListener viewClickListener,
            final View.OnClickListener avatarClickListner) {
        mContext = context;
        mViewClickListener = viewClickListener;
        mAvatarClickListner = avatarClickListner;

        mMessages = new ArrayList<>();
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        final View view = mLayoutInflater.inflate(R.layout.message_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Message message = getMessage(position);

        // Set the view click listener
        holder.itemView.setOnClickListener(mViewClickListener);

        Picasso.with(mContext)
                .load(message.getAvatarUrl())
                .placeholder(R.drawable.account_circle)
                .error(R.drawable.account_circle)
                .into(holder.avatarView);
        holder.avatarView.setOnClickListener(mAvatarClickListner);
        holder.avatarView.setTag(message.getFromUserId());

        holder.userName.setText(message.getFromUserName());

        holder.dateView.setText(Utils.getRelativeDate(mContext, message.getDate()));

        holder.titleView.setText(message.getTitle());
        // If the message is unread make it bold
        holder.titleView.setTypeface(message.isMessageUnread()
                ? Typeface.DEFAULT_BOLD
                : Typeface.DEFAULT);

        holder.messageView.setText(message.getSubMessage());
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    public void clear() {
        final int count = mMessages.size();
        mMessages.clear();
        notifyItemRangeRemoved(0, count);
    }

    public void addAll(final List<? extends Message> messages) {
        if (messages == null) {
            return;
        }

        final int count = mMessages.size();
        mMessages.addAll(messages);
        notifyItemRangeInserted(count, messages.size());
    }

    public Message getMessage(int position) {
        return mMessages.get(position);
    }

    public void update(final Message message) {
        final int position = mMessages.indexOf(message);
        if (position == -1) {
            return;
        }
        mMessages.set(position, message);
        notifyItemChanged(position);
    }

    public void remove(final Message message) {
        final int position = mMessages.indexOf(message);
        if (position == -1) {
            return;
        }
        mMessages.remove(position);
        notifyItemRemoved(position);
    }

    public List<Message> getMessages() {
        return Collections.unmodifiableList(mMessages);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        public final TextView userName;

        public final TextView dateView;

        public final TextView titleView;

        public final TextView messageView;

        public final ImageView avatarView;

        public ViewHolder(final View itemView) {
            super(itemView);

            userName = (TextView) itemView.findViewById(R.id.messsage_list_item_user_name);
            dateView = (TextView) itemView.findViewById(R.id.messsage_list_item_last_post);
            titleView = (TextView) itemView.findViewById(R.id.messsage_list_item_title);
            messageView = (TextView) itemView.findViewById(R.id.messsage_list_item_content);
            avatarView = (ImageView) itemView.findViewById(R.id.messsage_list_item_avatar);
        }
    }
}
