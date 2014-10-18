package com.xda.one.ui;

import com.squareup.picasso.Picasso;
import com.xda.one.R;
import com.xda.one.model.augmented.AugmentedMention;
import com.xda.one.util.StringUtils;
import com.xda.one.util.Utils;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MentionAdapter extends RecyclerView.Adapter<MentionAdapter.MentionViewHolder> {

    private static final int MAX_STRING_LENGTH = 100;

    private final LayoutInflater mLayoutInflater;

    private final List<AugmentedMention> mMentions;

    private final View.OnClickListener mViewClickListener;

    private final Context mContext;

    private final View.OnClickListener mAvatarClickListener;

    public MentionAdapter(final Context context, final View.OnClickListener viewClickListener,
            final View.OnClickListener avatarClickListener) {
        mContext = context;
        mViewClickListener = viewClickListener;
        mAvatarClickListener = avatarClickListener;

        mLayoutInflater = LayoutInflater.from(context);
        mMentions = new ArrayList<>();
    }

    @Override
    public MentionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = mLayoutInflater.inflate(R.layout.quote_mention_list_item, parent, false);
        return new MentionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MentionViewHolder quoteHolder, final int position) {
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
        return mMentions.size();
    }

    public void addAll(final List<AugmentedMention> quotes) {
        if (Utils.isCollectionEmpty(quotes)) {
            return;
        }

        final int count = mMentions.size();
        mMentions.addAll(quotes);
        notifyItemRangeInserted(count, quotes.size());
    }

    public void remove(final AugmentedMention item) {
        remove(indexOf(item));
    }

    void remove(final int position) {
        if (position < 0 || position >= mMentions.size()) {
            // TODO - maybe throw an exception?
            return;
        }

        mMentions.remove(position);
        notifyItemRemoved(position);
    }

    public void clear() {
        final int count = mMentions.size();
        mMentions.clear();
        notifyItemRangeRemoved(0, count);
    }

    public int indexOf(final AugmentedMention quote) {
        return mMentions.indexOf(quote);
    }

    public void update(final AugmentedMention quote) {
        final int position = indexOf(quote);
        mMentions.set(position, quote);
        notifyItemChanged(position);
    }

    public void onSaveInstanceState(final Bundle outState) {
        final ArrayList<AugmentedMention> quotes = new ArrayList<>(mMentions);
        outState.putParcelableArrayList(MentionFragment.SAVED_ADAPTER_STATE, quotes);
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
}