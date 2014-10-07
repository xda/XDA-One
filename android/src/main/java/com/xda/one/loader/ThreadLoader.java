package com.xda.one.loader;

import com.xda.one.api.inteface.ThreadClient;
import com.xda.one.api.model.response.container.ResponseUnifiedThreadContainer;
import com.xda.one.api.retrofit.RetrofitThreadClient;
import com.xda.one.model.augmented.container.AugmentedUnifiedThreadContainer;

import android.content.Context;

public class ThreadLoader extends AsyncLoader<AugmentedUnifiedThreadContainer> {

    private final int mPage;

    private final ThreadClient mThreadClient;

    private int mForumId;

    public ThreadLoader(final Context context, final int forumId, final int page) {
        super(context);
        mForumId = forumId;
        mPage = page;
        mThreadClient = RetrofitThreadClient.getClient(getContext());
    }

    @Override
    public void releaseResources(final AugmentedUnifiedThreadContainer data) {
    }

    @Override
    public AugmentedUnifiedThreadContainer loadInBackground() {
        final ResponseUnifiedThreadContainer container = mThreadClient.getThreads(mForumId, mPage);
        if (container == null) {
            return null;
        }
        return new AugmentedUnifiedThreadContainer(container, getContext());
    }
}