package com.xda.one.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.xda.one.R;
import com.xda.one.model.augmented.AugmentedMention;
import com.xda.one.util.StringUtils;
import com.xda.one.util.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MentionAdapter extends RecyclerView.Adapter<MentionAdapter.MentionViewHolder> {

    private static final int NORMAL_VIEW_TYPE = 1;

    private static final int FOOTER_VIEW_TYPE = 2;
    private static final int MAX_STRING_LENGTH = 100;
    private final LayoutInflater mLayoutInflater;
    private final List<AugmentedMention> mMentions;
    private final View.OnClickListener mViewClickListener;
    private final Context mContext;
    private final View.OnClickListener mAvatarClickListener;
    private int mFooterItemCount = 0;

    public MentionAdapter(final Context context, final View.OnClickListener viewClickListener,
                          final View.OnClickListener avatarClickListener) {
        mContext = context;
        mViewClickListener = viewClickListener;
        mAvatarClickListener = avatarClickListener;

        mLayoutInflater = LayoutInflater.from(context);
        mMentions = new ArrayList<>();
    }

    @Override
    public int getItemViewType(final int position) {
        if (position == mMentions.size()) {
            return FOOTER_VIEW_TYPE;
        }
        return NORMAL_VIEW_TYPE;
    }

    @Override
    public MentionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == FOOTER_VIEW_TYPE) {
            final View view = mLayoutInflater
                    .inflate(R.layout.load_more_progress_bar_only, parent, false);
            return new FooterViewType(view);
        }
        final View view = mLayoutInflater.inflate(R.layout.quote_mention_list_item, parent, false);
        return new MentionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MentionViewHolder quoteHolder, final int position) {
        if (getItemViewType(position) == FOOTER_VIEW_TYPE) {
            return;
        }

        final AugmentedMention mention = getMention(position);

        quoteHolder.itemView.setOnClickListener(mViewClickListener);

        Picasso.with(mContext)
                .load(mention.getAvatarUrl())
                .placeholder(R.drawable.account_circle)
                .error(R.drawable.account_circle)
                .into(quoteHolder.avatarImageView);
        quoteHolder.avatarImageView.setTag(mention.getUserId());
        quoteHolder.avatarImageView.setOnClickListener(mAvatarClickListener);

        quoteHolder.titleTextView.setText(mention.getThread().getTitle());

        quoteHolder.contentTextView.setText(StringUtils.trimCharSequence(
                mention.getCombinedUsernameContent(), MAX_STRING_LENGTH));
    }

    public AugmentedMention getMention(int position) {
        return mMentions.get(position);
    }

    @Override
    public int getItemCount() {
        return mMentions.size() + mFooterItemCount;
    }

    public void addAll(final List<AugmentedMention> mentions) {
        if (Utils.isCollectionEmpty(mentions)) {
            return;
        }

        final int count = mMentions.size();
        mMentions.addAll(mentions);
        if (count == 0) {
            // Add the footer in as well
            notifyItemRangeInserted(count, mentions.size() + ++mFooterItemCount);
        } else {
            notifyItemRangeInserted(count, mentions.size());
        }
    }

    public void clear() {
        if (isEmpty()) {
            return;
        }

        final int count = mMentions.size();
        mMentions.clear();
        notifyItemRangeRemoved(0, count + mFooterItemCount--);
    }

    public void update(final AugmentedMention quote) {
        final int position = mMentions.indexOf(quote);
        mMentions.set(position, quote);
        notifyItemChanged(position);
    }

    public void removeFooter() {
        mFooterItemCount = 0;
        notifyItemRemoved(mMentions.size());
    }

    public boolean isEmpty() {
        return mMentions.isEmpty();
    }

    public List<AugmentedMention> getMentions() {
        return Collections.unmodifiableList(mMentions);
    }

    public static class MentionViewHolder extends RecyclerView.ViewHolder {

        public final TextView titleTextView;

        private final ImageView avatarImageView;

        private final TextView contentTextView;

        public MentionViewHolder(View itemView) {
            super(itemView);

            avatarImageView = (ImageView) itemView.findViewById(R.id.avatar);
            titleTextView = (TextView) itemView.findViewById(R.id.user_profile_list_item_title);
            contentTextView = (TextView) itemView.findViewById(R.id.user_profile_list_item_content);
        }
    }

    private static class FooterViewType extends MentionViewHolder {

        public FooterViewType(final View itemView) {
            super(itemView);
        }
    }
}