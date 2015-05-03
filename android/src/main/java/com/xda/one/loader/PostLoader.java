package com.xda.one.loader;

import android.content.Context;

import com.xda.one.api.inteface.PostClient;
import com.xda.one.api.model.interfaces.container.PostContainer;
import com.xda.one.api.retrofit.RetrofitPostClient;
import com.xda.one.model.augmented.AugmentedPostContainer;

public class PostLoader extends AsyncLoader<AugmentedPostContainer> {

    private final PostClient mPostClient;

    private final PostContainer mPostContainer;

    private int mPage;

    private String mThreadId;

    public PostLoader(Context context, String threadId, int page) {
        super(context);

        mPage = page;
        mThreadId = threadId;
        mPostContainer = null;
        mPostClient = RetrofitPostClient.getClient(getContext());
    }

    public PostLoader(final Context context, final PostContainer container) {
        super(context);

        mPostContainer = container;
        mPostClient = RetrofitPostClient.getClient(getContext());
    }

    @Override
    public void releaseResources(final AugmentedPostContainer data) {
    }

    @Override
    public AugmentedPostContainer loadInBackground() {
        final PostContainer container = mPostContainer == null
                ? mPostClient.getPosts(mThreadId, mPage)
                : mPostContainer;
        if (container == null) {
            return null;
        }
        return new AugmentedPostContainer(container, getContext());
    }
}