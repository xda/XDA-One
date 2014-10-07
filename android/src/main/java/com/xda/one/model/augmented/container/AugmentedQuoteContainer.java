package com.xda.one.model.augmented.container;

import com.xda.one.R;
import com.xda.one.api.model.interfaces.Quote;
import com.xda.one.api.model.interfaces.container.QuoteContainer;
import com.xda.one.model.augmented.AugmentedQuote;
import com.xda.one.parser.ContentParser;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;

import java.util.ArrayList;
import java.util.List;

public class AugmentedQuoteContainer implements QuoteContainer {

    private final QuoteContainer mQuoteContainer;

    private final ArrayList<AugmentedQuote> mQuotes;

    public AugmentedQuoteContainer(final QuoteContainer quoteContainer, final Context context) {
        mQuoteContainer = quoteContainer;

        mQuotes = new ArrayList<>();

        final int primary = context.getResources().getColor(R.color.default_primary_text);
        final int secondary = context.getResources().getColor(R.color.default_secondary_text);
        for (final Quote quote : quoteContainer.getQuotes()) {
            mQuotes.add(new AugmentedQuote(quote, context, primary, secondary));
        }
    }

    @Override
    public List<AugmentedQuote> getQuotes() {
        return mQuotes;
    }

    @Override
    public int getTotalPages() {
        return mQuoteContainer.getTotalPages();
    }

    @Override
    public int getPerPage() {
        return mQuoteContainer.getPerPage();
    }

    @Override
    public int getCurrentPage() {
        return mQuoteContainer.getCurrentPage();
    }
}