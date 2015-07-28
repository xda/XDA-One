package com.xda.one.loader;

import android.content.Context;

import com.xda.one.api.inteface.UserClient;
import com.xda.one.api.model.interfaces.container.QuoteContainer;
import com.xda.one.api.retrofit.RetrofitUserClient;
import com.xda.one.model.augmented.container.AugmentedQuoteContainer;

public class QuoteLoader extends AsyncLoader<AugmentedQuoteContainer> {

    private final int mPage;

    private final UserClient mUserClient;

    public QuoteLoader(final Context context, final int page) {
        super(context);
        mPage = page;
        mUserClient = RetrofitUserClient.getClient(getContext());
    }

    @Override
    public void releaseResources(final AugmentedQuoteContainer data) {
    }

    @Override
    public AugmentedQuoteContainer loadInBackground() {
        final QuoteContainer container = mUserClient.getQuotes(mPage);
        if (container == null) {
            return null;
        }
        return new AugmentedQuoteContainer(container, getContext());
    }
}