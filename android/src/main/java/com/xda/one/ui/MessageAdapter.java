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

    private static final int NORMAL_VIEW_TYPE = 1;

    private static final int FOOTER_VIEW_TYPE = 2;

    private int mFooterItemCount = 0;

    private final Context mContext;

    private final LayoutInflater mLayoutInflater;

    private final List<Message> mMessages;

    private final View.OnClickListener mViewClickListener;

    private final View.OnClickListener mAvatarClickListener;

    public MessageAdapter(final Context context, final View.OnClickListener viewClickListener,
            final View.OnClickListener avatarClickListener) {
        mContext = context;
        mViewClickListener = viewClickListener;
        mAvatarClickListener = avatarClickListener;

        mMessages = new ArrayList<>();
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getItemViewType(final int position) {
        if (position == mMessages.size()) {
            return FOOTER_VIEW_TYPE;
        }
        return NORMAL_VIEW_TYPE;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        if (i == FOOTER_VIEW_TYPE) {
            final View view = mLayoutInflater
                    .inflate(R.layout.load_more_progress_bar_only, parent, false);
            return new FooterViewType(view);
        }
        final View view = mLayoutInflater.inflate(R.layout.message_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        if (getItemViewType(position) == FOOTER_VIEW_TYPE) {
            return;
        }

        final Message message = getMessage(position);

        // Set the view click listener
        holder.itemView.setOnClickListener(mViewClickListener);

        Picasso.with(mContext)
                .load(message.getAvatarUrl())
                .placeholder(R.drawable.account_circle)
                .error(R.drawable.account_circle)
                .into(holder.avatarView);
        holder.avatarView.setOnClickListener(mAvatarClickListener);
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
        return mMessages.size() + mFooterItemCount;
    }

    public void clear() {
        if (isEmpty()) {
            return;
        }

        final int count = mMessages.size();
        mMessages.clear();
        notifyItemRangeRemoved(0, count + mFooterItemCount--);
    }

    public void addAll(final List<? extends Message> messages) {
        if (messages == null) {
            return;
        }

        final int count = mMessages.size();
        mMessages.addAll(messages);
        if (count == 0) {
            // Add the footer in as well
            notifyItemRangeInserted(count, messages.size() + ++mFooterItemCount);
        } else {
            notifyItemRangeInserted(count, messages.size());
        }
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

    public boolean isEmpty() {
        return mMessages.isEmpty();
    }

    public void removeFooter() {
        mFooterItemCount = 0;
        notifyItemRemoved(mMessages.size());
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

    private static class FooterViewType extends ViewHolder {

        public FooterViewType(final View itemView) {
            super(itemView);
        }
    }
}
