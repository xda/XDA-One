package com.xda.one.api.inteface;

import com.xda.one.api.misc.Consumer;
import com.xda.one.api.misc.EventBus;
import com.xda.one.api.misc.Result;
import com.xda.one.api.model.interfaces.Post;
import com.xda.one.api.model.interfaces.UnifiedThread;
import com.xda.one.api.model.interfaces.container.PostContainer;
import com.xda.one.api.model.response.container.ResponsePostContainer;

import retrofit.Callback;

public interface PostClient {

    EventBus getBus();

    ResponsePostContainer getPosts(final String threadId, final int page);

    void getPostsAsync(final String threadId,
                       final int page,
                       final Callback<PostContainer> consumer);

    void getPostsById(final String postId,
                      final Consumer<PostContainer> consumer,
                      final Runnable failure);

    void getUnreadPostFeed(final UnifiedThread unifiedThread,
                           final Consumer<PostContainer> consumer,
                           final Runnable failure);

    void addAttachmentAsync(final Post post, final Consumer<Result> runnable);

    void createNewPostAsync(final Post post, final String message);

    void createNewPostAsync(final UnifiedThread unifiedThread, final String message);

    void addThanksAsync(final Post post, final Consumer<Result> runnable);

    void removeThanksAsync(final Post post, final Consumer<Result> runnable);
}
