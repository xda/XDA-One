package com.xda.one.loader;

import android.content.Context;

import com.xda.one.api.inteface.PostClient;
import com.xda.one.api.model.response.container.ResponsePostContainer;
import com.xda.one.api.retrofit.RetrofitPostClient;
import com.xda.one.model.augmented.AugmentedPostContainer;

public class PostLoader extends AsyncLoader<AugmentedPostContainer> {

    private final PostClient mPostClient;

    private final ResponsePostContainer mResponseContainer;

    private int mPage;

    private String mThreadId;

    public PostLoader(Context context, String threadId, int page) {
        super(context);

        mPage = page;
        mThreadId = threadId;
        mResponseContainer = null;
        mPostClient = RetrofitPostClient.getClient(getContext());
    }

    public PostLoader(final Context context, final ResponsePostContainer responseContainer) {
        super(context);

        mResponseContainer = responseContainer;
        mPostClient = RetrofitPostClient.getClient(getContext());
    }

    @Override
    public void releaseResources(final AugmentedPostContainer data) {
    }

    @Override
    public AugmentedPostContainer loadInBackground() {
        final ResponsePostContainer container = mResponseContainer == null
                ? mPostClient.getPosts(mThreadId, mPage)
                : mResponseContainer;
        if (container == null) {
            return null;
        }
        return new AugmentedPostContainer(container, getContext());
    }
}