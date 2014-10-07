package com.xda.one.api.inteface;

import com.xda.one.api.misc.Consumer;
import com.xda.one.api.misc.EventBus;
import com.xda.one.api.misc.Result;
import com.xda.one.api.model.interfaces.UnifiedThread;
import com.xda.one.api.model.response.container.ResponseUnifiedThreadContainer;

public interface ThreadClient {

    public EventBus getBus();

    public ResponseUnifiedThreadContainer getThreads(final int forumId, final int page);

    public ResponseUnifiedThreadContainer getParticipatedThreads(final int page);

    public ResponseUnifiedThreadContainer getSubscribedThreads(final int page);

    public void createThread(final int forumId, final String title, final String message,
            final Consumer<Result> runnable);

    public void subscribeAsync(final UnifiedThread normalDefaultUnifiedThread);

    public void unsubscribeAsync(final UnifiedThread normalDefaultUnifiedThread);

    public void toggleSubscribeAsync(final UnifiedThread normalDefaultUnifiedThread);
}