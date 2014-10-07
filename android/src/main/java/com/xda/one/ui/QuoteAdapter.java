package com.xda.one.ui;

import com.squareup.picasso.Picasso;
import com.xda.one.R;
import com.xda.one.model.augmented.AugmentedQuote;
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

public class QuoteAdapter extends RecyclerView.Adapter<QuoteAdapter.QuoteViewHolder> {

    private static final int MAX_STRING_LENGTH = 100;

    private final LayoutInflater mLayoutInflater;

    private final List<AugmentedQuote> mQuotes;

    private final View.OnClickListener mQuoteClickListener;

    private final View.OnClickListener mAvatarClickListener;

    private final Context mContext;

    public QuoteAdapter(final Context context, final View.OnClickListener quoteClickListener,
            final View.OnClickListener avatarClickListener) {
        mContext = context;
        mQuoteClickListener = quoteClickListener;
        mAvatarClickListener = avatarClickListener;

        mQuotes = new ArrayList<>();
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public QuoteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = mLayoutInflater.inflate(R.layout.quote_mention_list_item, parent, false);
        return new QuoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final QuoteViewHolder quoteHolder, final int position) {
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
        return mQuotes.size();
    }

    public void addAll(final List<AugmentedQuote> quotes) {
        if (Utils.isCollectionEmpty(quotes)) {
            return;
        }

        final int count = mQuotes.size();
        mQuotes.addAll(quotes);
        notifyItemRangeInserted(count, quotes.size());
    }

    public void remove(final AugmentedQuote item) {
        remove(indexOf(item));
    }

    void remove(final int position) {
        if (position < 0 || position >= mQuotes.size()) {
            // TODO - maybe throw an exception?
            return;
        }

        mQuotes.remove(position);
        notifyItemRemoved(position);
    }

    public void clear() {
        final int count = mQuotes.size();
        mQuotes.clear();
        notifyItemRangeRemoved(0, count - 1);
    }

    public int indexOf(final AugmentedQuote quote) {
        return mQuotes.indexOf(quote);
    }

    public void update(final AugmentedQuote quote) {
        final int position = indexOf(quote);
        mQuotes.set(position, quote);
        notifyItemChanged(position);
    }

    public void onSaveInstanceState(final Bundle outState) {
        final ArrayList<AugmentedQuote> quotes = new ArrayList<>(mQuotes);
        outState.putParcelableArrayList(QuoteFragment.SAVED_ADAPTER_STATE, quotes);
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
}