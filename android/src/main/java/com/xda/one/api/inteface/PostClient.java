package com.xda.one.api.inteface;

import com.xda.one.api.misc.Consumer;
import com.xda.one.api.misc.EventBus;
import com.xda.one.api.misc.Result;
import com.xda.one.api.model.interfaces.Post;
import com.xda.one.api.model.interfaces.UnifiedThread;
import com.xda.one.api.model.response.container.ResponsePostContainer;

import retrofit.Callback;

public interface PostClient {

    public EventBus getBus();

    public ResponsePostContainer getPosts(final String threadId, final int page);

    public void getPostsAsync(final String threadId, final int page,
                              final Callback<ResponsePostContainer> consumer);

    public void getPostsById(String postId, Consumer<ResponsePostContainer> consumer,
                             final Runnable failure);

    public void getUnreadPostFeed(final UnifiedThread unifiedThread,
                                  final Consumer<ResponsePostContainer> consumer, final Runnable failure);

    public void addAttachmentAsync(final Post post, final Consumer<Result> runnable);

    public void createNewPostAsync(final Post post, final String message);

    public void createNewPostAsync(final UnifiedThread unifiedThread, final String message);

    public void addThanksAsync(final Post post, final Consumer<Result> runnable);

    public void removeThanksAsync(final Post post, final Consumer<Result> runnable);

    public void toggleThanksAsync(final Post post, final Consumer<Result> runnable);
}
