package com.xda.one.loader;

import com.xda.one.api.inteface.UserClient;
import com.xda.one.api.model.interfaces.container.MentionContainer;
import com.xda.one.api.retrofit.RetrofitUserClient;
import com.xda.one.model.augmented.container.AugmentedMentionContainer;

import android.content.Context;

public class MentionLoader extends AsyncLoader<AugmentedMentionContainer> {

    private final int mPage;

    private final UserClient mUserClient;

    public MentionLoader(final Context context, final int page) {
        super(context);
        mPage = page;
        mUserClient = RetrofitUserClient.getClient(getContext());
    }

    @Override
    public void releaseResources(final AugmentedMentionContainer data) {
    }

    @Override
    public AugmentedMentionContainer loadInBackground() {
        final MentionContainer container = mUserClient.getMentions(mPage);
        if (container == null) {
            return null;
        }
        return new AugmentedMentionContainer(container, getContext());
    }
}