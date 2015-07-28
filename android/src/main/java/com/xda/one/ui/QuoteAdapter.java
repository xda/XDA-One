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
import com.xda.one.model.augmented.AugmentedQuote;
import com.xda.one.util.StringUtils;
import com.xda.one.util.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuoteAdapter extends RecyclerView.Adapter<QuoteAdapter.QuoteViewHolder> {

    private static final int NORMAL_VIEW_TYPE = 1;

    private static final int FOOTER_VIEW_TYPE = 2;
    private static final int MAX_STRING_LENGTH = 100;
    private final LayoutInflater mLayoutInflater;
    private final List<AugmentedQuote> mQuotes;
    private final View.OnClickListener mQuoteClickListener;
    private final View.OnClickListener mAvatarClickListener;
    private final Context mContext;
    private int mFooterItemCount = 0;

    public QuoteAdapter(final Context context, final View.OnClickListener quoteClickListener,
                        final View.OnClickListener avatarClickListener) {
        mContext = context;
        mQuoteClickListener = quoteClickListener;
        mAvatarClickListener = avatarClickListener;

        mQuotes = new ArrayList<>();
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getItemViewType(final int position) {
        if (position == mQuotes.size()) {
            return FOOTER_VIEW_TYPE;
        }
        return NORMAL_VIEW_TYPE;
    }

    @Override
    public QuoteViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        if (i == FOOTER_VIEW_TYPE) {
            final View view = mLayoutInflater
                    .inflate(R.layout.load_more_progress_bar_only, parent, false);
            return new FooterViewType(view);
        }
        final View view = mLayoutInflater.inflate(R.layout.quote_mention_list_item, parent, false);
        return new QuoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final QuoteViewHolder quoteHolder, final int position) {
        if (getItemViewType(position) == FOOTER_VIEW_TYPE) {
            return;
        }

        final AugmentedQuote quote = getQuote(position);

        Picasso.with(mContext)
                .load(quote.getAvatarUrl())
                .placeholder(R.drawable.account_circle)
                .error(R.drawable.account_circle)
                .into(quoteHolder.avatarImageView);
        quoteHolder.avatarImageView.setOnClickListener(mAvatarClickListener);
        quoteHolder.avatarImageView.setTag(quote.getUserId());

        quoteHolder.itemView.setOnClickListener(mQuoteClickListener);
        quoteHolder.titleTextView.setText(quote.getThread().getTitle());
        quoteHolder.contentTextView.setText(StringUtils.trimCharSequence(
                quote.getCombinedUsernameContent(), MAX_STRING_LENGTH));
    }

    public AugmentedQuote getQuote(int position) {
        return mQuotes.get(position);
    }

    @Override
    public int getItemCount() {
        return mQuotes.size() + mFooterItemCount;
    }

    public void addAll(final List<AugmentedQuote> quotes) {
        if (Utils.isCollectionEmpty(quotes)) {
            return;
        }

        final int count = mQuotes.size();
        mQuotes.addAll(quotes);

        if (count == 0) {
            // Add the footer in as well
            notifyItemRangeInserted(count, quotes.size() + ++mFooterItemCount);
        } else {
            notifyItemRangeInserted(count, quotes.size());
        }
    }

    public void clear() {
        if (isEmpty()) {
            return;
        }

        final int count = mQuotes.size();
        mQuotes.clear();
        notifyItemRangeRemoved(0, count + mFooterItemCount--);
    }

    public void update(final AugmentedQuote quote) {
        final int position = mQuotes.indexOf(quote);
        mQuotes.set(position, quote);
        notifyItemChanged(position);
    }

    public void removeFooter() {
        mFooterItemCount = 0;
        notifyItemRemoved(mQuotes.size());
    }

    public boolean isEmpty() {
        return mQuotes.isEmpty();
    }

    public List<AugmentedQuote> getQuotes() {
        return Collections.unmodifiableList(mQuotes);
    }

    public static class QuoteViewHolder extends RecyclerView.ViewHolder {

        public final TextView titleTextView;

        private final ImageView avatarImageView;

        private final TextView contentTextView;

        public QuoteViewHolder(View itemView) {
            super(itemView);

            avatarImageView = (ImageView) itemView.findViewById(R.id.avatar);
            titleTextView = (TextView) itemView.findViewById(R.id.user_profile_list_item_title);
            contentTextView = (TextView) itemView.findViewById(R.id.user_profile_list_item_content);
        }
    }

    private static class FooterViewType extends QuoteViewHolder {

        public FooterViewType(final View itemView) {
            super(itemView);
        }
    }
}