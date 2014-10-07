package com.xda.one.loader;

import com.xda.one.api.inteface.ThreadClient;
import com.xda.one.api.model.response.container.ResponseUnifiedThreadContainer;
import com.xda.one.api.retrofit.RetrofitThreadClient;
import com.xda.one.model.augmented.container.AugmentedUnifiedThreadContainer;

import android.content.Context;

public class ParticipatedThreadLoader extends AsyncLoader<AugmentedUnifiedThreadContainer> {

    private final int mPage;

    private final ThreadClient mThreadClient;

    public ParticipatedThreadLoader(final Context context, final int page) {
        super(context);
        mPage = page;
        mThreadClient = RetrofitThreadClient.getClient(getContext());
    }

    @Override
    public void releaseResources(final AugmentedUnifiedThreadContainer data) {
    }

    @Override
    public AugmentedUnifiedThreadContainer loadInBackground() {
        final ResponseUnifiedThreadContainer container = mThreadClient
                .getParticipatedThreads(mPage);
        if (container == null) {
            return null;
        }
        return new AugmentedUnifiedThreadContainer(container, getContext());
    }
}