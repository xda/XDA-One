package com.xda.one.ui;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.xda.one.R;
import com.xda.one.model.augmented.AugmentedUnifiedThread;
import com.xda.one.ui.helper.ActionModeHelper;
import com.xda.one.util.StringUtils;
import com.xda.one.util.Utils;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class ThreadAdapter
        extends RecyclerView.Adapter<ThreadAdapter.NormalThreadViewHolder> {

    private static final int NORMAL_VIEW_TYPE = 1;

    private static final int FOOTER_VIEW_TYPE = 2;

    private int mFooterItemCount = 0;

    private final Context mContext;

    private final LayoutInflater mLayoutInflater;

    private final List<AugmentedUnifiedThread> mThreads;

    private final View.OnClickListener mViewClickListener;

    private final View.OnLongClickListener mLongClickListener;

    private final ActionModeHelper mActionModeHelper;

    private NumberFormat mNumberFormat;

    public ThreadAdapter(final Context context, final View.OnClickListener
            onClickListener, final View.OnLongClickListener longClickListener,
            final ActionModeHelper helper) {
        mLongClickListener = longClickListener;
        mActionModeHelper = helper;
        mNumberFormat = NumberFormat.getInstance(Locale.getDefault());
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mThreads = new ArrayList<>();
        mViewClickListener = onClickListener;
    }

    @Override
    public int getItemViewType(final int position) {
        if (position == mThreads.size()) {
            return FOOTER_VIEW_TYPE;
        }
        return NORMAL_VIEW_TYPE;
    }

    @Override
    public NormalThreadViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        if (i == FOOTER_VIEW_TYPE) {
            final View view = mLayoutInflater
                    .inflate(R.layout.load_more_progress_bar_only, viewGroup, false);
            return new FooterViewType(view);
        }
        //final View view = mLayoutInflater.inflate(R.layout.thread_list_item, viewGroup, false);
        final View view = mLayoutInflater.inflate(R.layout.thread_list_item, viewGroup, false);
        return new NormalThreadViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final NormalThreadViewHolder holder, final int position) {
        if (getItemViewType(position) == FOOTER_VIEW_TYPE) {
            return;
        }

        final AugmentedUnifiedThread thread = getThread(position);

        // Set the click listener
        holder.container.setOnClickListener(mViewClickListener);
        holder.container.setOnLongClickListener(mLongClickListener);
        mActionModeHelper.updateActivatedState(holder.container, position);

        Picasso.with(mContext)
                .load(thread.getAvatarUrl())
                .placeholder(R.drawable.account_circle)
                .error(R.drawable.account_circle)
                .into(holder.avatarView);

        holder.userNameView.setText(thread.getPostUsername());

        holder.stickyView.setVisibility(thread.isSticky() ? View.VISIBLE : View.GONE);
        holder.lockedView.setVisibility(thread.isOpen() ? View.GONE : View.VISIBLE);

        holder.titleView.setText(Html.fromHtml(thread.getTitle()));
        holder.titleView.setTypeface(null, thread.isUnread() ? Typeface.BOLD : Typeface.NORMAL);

        holder.textView.setText(StringUtils.removeWhiteSpaces(thread.getSubPageText()));

        holder.replyCount.setText(mNumberFormat.format(thread.getReplyCount()));
        holder.lastPostTimeView
                .setText(Utils.getRelativeDate(mContext, thread.getLastPost()));
    }

    @Override
    public int getItemCount() {
        return mThreads.size() + mFooterItemCount;
    }

    public void addAll(final List<AugmentedUnifiedThread> threads) {
        if (Utils.isCollectionEmpty(threads)) {
            return;
        }

        final int count = mThreads.size();
        mThreads.addAll(threads);

        if (count == 0) {
            // Add the footer in as well
            notifyItemRangeInserted(count, threads.size() + ++mFooterItemCount);
        } else {
            notifyItemRangeInserted(count, threads.size());
        }
    }

    public void clear() {
        if (isEmpty()) {
            return;
        }
        final int count = mThreads.size();
        mThreads.clear();
        notifyItemRangeRemoved(0, count + mFooterItemCount--);
    }

    public AugmentedUnifiedThread getThread(final int position) {
        return mThreads.get(position);
    }

    public List<AugmentedUnifiedThread> getThreads() {
        return Collections.unmodifiableList(mThreads);
    }

    public int indexOf(final AugmentedUnifiedThread thread) {
        return mThreads.indexOf(thread);
    }

    public void updateThread(final int position, final AugmentedUnifiedThread unifiedThread) {
        mThreads.set(position, unifiedThread);
        notifyItemChanged(position);
    }

    public void removeFooter() {
        mFooterItemCount = 0;
        notifyItemRemoved(mThreads.size());
    }

    public boolean isEmpty() {
        return mThreads.isEmpty();
    }

    public static class NormalThreadViewHolder extends RecyclerView.ViewHolder {

        public final View container;

        public final TextView titleView;

        public final TextView textView;

        public final TextView replyCount;

        public final TextView userNameView;

        public final ImageView avatarView;

        public final TextView lastPostTimeView;

        public final ImageView stickyView;

        public final ImageView lockedView;

        public NormalThreadViewHolder(View itemView) {
            super(itemView);

            container = itemView.findViewById(R.id.thread_list_item_container);
            titleView = (TextView) itemView.findViewById(R.id.thread_title);
            textView = (TextView) itemView.findViewById(R.id.thread_list_item_content);
            replyCount = (TextView) itemView.findViewById(R.id.reply_count);
            userNameView = (TextView) itemView.findViewById(R.id.poster_user_name);
            avatarView = (ImageView) itemView.findViewById(R.id.avatar);
            stickyView = (ImageView) itemView.findViewById(R.id.thread_list_item_sticky);
            lockedView = (ImageView) itemView.findViewById(R.id.thread_list_item_locked);
            lastPostTimeView = (TextView) itemView.findViewById(R.id.last_post);
        }
    }

    private static class FooterViewType extends NormalThreadViewHolder {

        public FooterViewType(final View itemView) {
            super(itemView);
        }
    }
}