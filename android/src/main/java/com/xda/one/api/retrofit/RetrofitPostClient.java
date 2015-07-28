package com.xda.one.api.retrofit;

import android.content.Context;

import com.xda.one.api.inteface.PostClient;
import com.xda.one.api.misc.Consumer;
import com.xda.one.api.misc.EventBus;
import com.xda.one.api.misc.Result;
import com.xda.one.api.model.interfaces.Post;
import com.xda.one.api.model.interfaces.UnifiedThread;
import com.xda.one.api.model.request.RequestNewPost;
import com.xda.one.api.model.request.RequestPostAttachment;
import com.xda.one.api.model.request.RequestThanks;
import com.xda.one.api.model.response.container.ResponsePostContainer;
import com.xda.one.constants.XDAConstants;
import com.xda.one.event.post.PostCreatedEvent;
import com.xda.one.event.post.PostCreationFailedEvent;
import com.xda.one.util.Utils;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.POST;
import retrofit.http.Query;

import static com.xda.one.api.retrofit.RetrofitClient.getAuthToken;

public class RetrofitPostClient implements PostClient {

    private static PostClient sPostClient;

    private final PostAPI mPostAPI;

    private final EventBus mBus;

    private RetrofitPostClient(final Context context) {
        mPostAPI = RetrofitClient.getRestBuilder(context, XDAConstants.ENDPOINT_URL)
                .build()
                .create(PostAPI.class);
        mBus = new EventBus();
    }

    public static PostClient getClient(final Context context) {
        if (sPostClient == null) {
            sPostClient = new RetrofitPostClient(context);
        }
        return sPostClient;
    }

    @Override
    public EventBus getBus() {
        return mBus;
    }

    @Override
    public ResponsePostContainer getPosts(final String threadId, final int page) {
        try {
            return mPostAPI.getPosts(getAuthToken(), threadId, page);
        } catch (RetrofitError error) {
            Utils.handleRetrofitErrorQuietly(error);
        }
        return null;
    }

    @Override
    public void getPostsAsync(final String threadId, final int page,
                              final Callback<ResponsePostContainer> callback) {
        mPostAPI.getPostsAsync(getAuthToken(), threadId, page, callback);
    }

    @Override
    public void getPostsById(final String postId, final Consumer<ResponsePostContainer>
            container, final Runnable failure) {
        mPostAPI.getPostsById(getAuthToken(), postId, new Callback<ResponsePostContainer>() {
            @Override
            public void success(final ResponsePostContainer responsePostContainer,
                                final Response response) {
                container.run(responsePostContainer);
            }

            @Override
            public void failure(final RetrofitError error) {
                Utils.handleRetrofitErrorQuietly(error);
            }
        });
    }

    @Override
    public void getUnreadPostFeed(final UnifiedThread unifiedThread,
                                  final Consumer<ResponsePostContainer> consumer, final Runnable failure) {
        mPostAPI.getUnreadPostFeed(getAuthToken(), unifiedThread.getThreadId(),
                new Callback<ResponsePostContainer>() {
                    @Override
                    public void success(final ResponsePostContainer responsePostContainer,
                                        final Response response) {
                        consumer.run(responsePostContainer);
                    }

                    @Override
                    public void failure(final RetrofitError error) {
                        Utils.handleRetrofitErrorQuietly(error);
                        failure.run();
                    }
                });
    }

    @Override
    public void createNewPostAsync(final Post post, final String message) {
        createNewPost(post.getPostId(), message);
    }

    @Override
    public void createNewPostAsync(final UnifiedThread unifiedThread, final String message) {
        createNewPost(unifiedThread.getFirstPostId(), message);
    }

    private void createNewPost(final int postId, final String message) {
        final RequestNewPost newPost = new RequestNewPost(postId, message);
        mPostAPI.createNewPost(getAuthToken(), newPost, new CreatePostCallback());
    }

    @Override
    public void addThanksAsync(final Post post, final Consumer<Result> runnable) {
        final RequestThanks newPost = new RequestThanks(post.getPostId());
        mPostAPI.addThanks(getAuthToken(), newPost, new ThanksCallback(post, runnable, true));
    }

    @Override
    public void removeThanksAsync(final Post post, final Consumer<Result> runnable) {
        mPostAPI.removeThanks(getAuthToken(), post.getPostId(), new ThanksCallback(post,
                runnable, false));
    }

    @Override
    public void toggleThanksAsync(final Post post, final Consumer<Result> runnable) {
        if (post.isThanked()) {
            removeThanksAsync(post, runnable);
        } else {
            addThanksAsync(post, runnable);
        }
    }

    @Override
    public void addAttachmentAsync(final Post post, final Consumer<Result> runnable) {
        final RequestPostAttachment newPost = new RequestPostAttachment(
                String.valueOf(post.getPostId()), null, null);
        mPostAPI.addAttachment(getAuthToken(), newPost, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                final Result result = Result.parseResultFromResponse(response);
                if (result.isSuccess()) {
                    runnable.run(result);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Utils.handleRetrofitErrorQuietly(error);
            }
        });
    }

    public static interface PostAPI {

        @GET("/posts")
        public ResponsePostContainer getPosts(@Header("Cookie") final String cookie,
                                              @Query("threadid") final String threadId, @Query("page") final int page);

        @GET("/posts")
        public void getPostsAsync(@Header("Cookie") final String cookie,
                                  @Query("threadid") final String threadId, @Query("page") final int page,
                                  final Callback<ResponsePostContainer> containerCallback);

        @GET("/posts/bypostid")
        public void getPostsById(@Header("Cookie") final String authToken,
                                 @Query("postid") final String postId,
                                 final Callback<ResponsePostContainer> containerCallback);

        @GET("/posts/newpost")
        void getUnreadPostFeed(@Header("Cookie") final String authToken,
                               @Query("threadid") String threadId, final Callback<ResponsePostContainer> callback);

        @POST("/posts/addattachment")
        public void addAttachment(@Header("Cookie") final String cookie,
                                  @Body final RequestPostAttachment attachment, final Callback<Response> callback);

        @POST("/posts/new")
        public void createNewPost(@Header("Cookie") final String cookie,
                                  @Body final RequestNewPost post, final Callback<Response> callback);

        @POST("/posts/thanks")
        public void addThanks(@Header("Cookie") final String cookie,
                              @Body final RequestThanks thanks, final Callback<Response> callback);

        @DELETE("/posts/thanks")
        public void removeThanks(@Header("Cookie") final String cookie,
                                 @Query("postid") final int postid, final Callback<Response> callback);
    }

    private static class ThanksCallback implements Callback<Response> {

        private final Consumer<Result> mRunnable;

        private final Post mPost;

        private final boolean mNewState;

        public ThanksCallback(final Post post, final Consumer<Result> runnable,
                              final boolean newState) {
            mPost = post;
            mRunnable = runnable;
            mNewState = newState;
        }

        @Override
        public void success(final Response response, final Response response2) {
            final Result result = Result.parseResultFromResponse(response);
            if (result.isSuccess()) {
                mPost.setThanked(mNewState);
                mPost.setThanksCount(mPost.getThanksCount() + (mNewState ? 1 : -1));
                mRunnable.run(result);
            }
        }

        @Override
        public void failure(final RetrofitError error) {
            Utils.handleRetrofitErrorQuietly(error);
        }
    }

    private class CreatePostCallback implements Callback<Response> {

        @Override
        public void success(final Response response, final Response response2) {
            final Result result = Result.parseResultFromResponse(response);
            mBus.post(Result.isSuccess(result)
                    ? new PostCreatedEvent()
                    : new PostCreationFailedEvent());
        }

        @Override
        public void failure(final RetrofitError error) {
            Utils.handleRetrofitErrorQuietly(error);
            mBus.post(new PostCreationFailedEvent());
        }
    }
}