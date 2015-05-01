package com.xda.one.api.inteface;

import com.xda.one.api.misc.Consumer;
import com.xda.one.api.misc.EventBus;
import com.xda.one.api.misc.Result;
import com.xda.one.api.model.interfaces.UnifiedThread;
import com.xda.one.api.model.response.container.ResponseUnifiedThreadContainer;

public interface ThreadClient {

    EventBus getBus();

    ResponseUnifiedThreadContainer getThreads(final int forumId, final int page);

    ResponseUnifiedThreadContainer getParticipatedThreads(final int page);

    ResponseUnifiedThreadContainer getSubscribedThreads(final int page);

    void createThread(final int forumId, final String title, final String message,
                      final Consumer<Result> runnable);

    void subscribeAsync(final UnifiedThread normalDefaultUnifiedThread);

    void unsubscribeAsync(final UnifiedThread normalDefaultUnifiedThread);

    void toggleSubscribeAsync(final UnifiedThread normalDefaultUnifiedThread);
}