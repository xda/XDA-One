package com.xda.one.api.retrofit;

import com.xda.one.api.inteface.ThreadClient;
import com.xda.one.api.misc.Consumer;
import com.xda.one.api.misc.EventBus;
import com.xda.one.api.misc.Result;
import com.xda.one.api.model.interfaces.UnifiedThread;
import com.xda.one.api.model.request.RequestThread;
import com.xda.one.api.model.request.RequestThreadSubscription;
import com.xda.one.api.model.response.container.ResponseUnifiedThreadContainer;
import com.xda.one.constants.XDAConstants;
import com.xda.one.event.thread.ThreadSubscriptionChangedEvent;
import com.xda.one.event.thread.ThreadSubscriptionChangingFailedEvent;
import com.xda.one.util.Utils;

import android.content.Context;

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

public class RetrofitThreadClient implements ThreadClient {

    private static ThreadClient sThreadClient;

    private final ThreadAPI mThreadAPI;

    private final EventBus mBus;

    private RetrofitThreadClient(final Context context) {
        mThreadAPI = RetrofitClient.getRestBuilder(context, XDAConstants.ENDPOINT_URL)
                .build()
                .create(ThreadAPI.class);
        mBus = new EventBus();
    }

    public static ThreadClient getClient(final Context context) {
        if (sThreadClient == null) {
            sThreadClient = new RetrofitThreadClient(context);
        }
        return sThreadClient;
    }

    @Override
    public EventBus getBus() {
        return mBus;
    }

    @Override
    public ResponseUnifiedThreadContainer getThreads(final int forumId, final int page) {
        try {
            return mThreadAPI.getThreads(getAuthToken(), String.valueOf(forumId), page);
        } catch (RetrofitError error) {
            Utils.handleRetrofitErrorQuietly(error);
        }
        return null;
    }

    @Override
    public ResponseUnifiedThreadContainer getParticipatedThreads(final int page) {
        try {
            return mThreadAPI.getParticipatedThreads(getAuthToken(), page);
        } catch (RetrofitError error) {
            Utils.handleRetrofitErrorQuietly(error);
        }
        return null;
    }

    @Override
    public ResponseUnifiedThreadContainer getSubscribedThreads(final int page) {
        try {
            return mThreadAPI.getSubscribedThreads(getAuthToken(), page);
        } catch (RetrofitError error) {
            Utils.handleRetrofitErrorQuietly(error);
        }
        return null;
    }

    @Override
    public void createThread(final int forumId, final String title, final String message,
            final Consumer<Result> runnable) {
        final RequestThread thread = new RequestThread(forumId, title, message);
        mThreadAPI.createThread(getAuthToken(), thread, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                final Result result = Result.parseResultFromResponse(response);
                if (Result.isSuccess(result)) {
                    runnable.run(result);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Utils.handleRetrofitErrorQuietly(error);
            }
        });
    }

    @Override
    public void subscribeAsync(final UnifiedThread unifiedThread) {
        final RequestThreadSubscription subs = new RequestThreadSubscription(
                unifiedThread.getThreadId());
        mThreadAPI.subscribe(getAuthToken(), subs, new SubscribeCallback(unifiedThread, true));
    }

    @Override
    public void unsubscribeAsync(final UnifiedThread unifiedThread) {
        final RequestThreadSubscription subs = new RequestThreadSubscription(
                unifiedThread.getThreadId());
        mThreadAPI.unsubscribe(getAuthToken(), subs.getThreadId(), new SubscribeCallback(
                unifiedThread, false));
    }

    @Override
    public void toggleSubscribeAsync(final UnifiedThread unifiedThread) {
        if (unifiedThread.isSubscribed()) {
            unsubscribeAsync(unifiedThread);
        } else {
            subscribeAsync(unifiedThread);
        }
    }

    public static interface ThreadAPI {

        @GET("/threads")
        public ResponseUnifiedThreadContainer getThreads(@Header("Cookie") final String cookie,
                @Query("forumid") final String forumId, @Query("page") final int page);

        @GET("/threads/participated")
        public ResponseUnifiedThreadContainer getParticipatedThreads(
                @Header("Cookie") final String cookie, @Query("page") final int page);

        @GET("/threads/subscribed")
        public ResponseUnifiedThreadContainer getSubscribedThreads(@Header("Cookie") final String
                cookie, @Query("page") final int page);

        @POST("/threads/new")
        public void createThread(@Header("Cookie") final String cookie,
                @Body final RequestThread thread, final Callback<Response> response);

        @POST("/threads/subscribe")
        public void subscribe(@Header("Cookie") final String cookie,
                @Body final RequestThreadSubscription subscription,
                final Callback<Response> response);

        @DELETE("/threads/unsubscribe")
        public void unsubscribe(@Header("Cookie") final String cookie,
                @Query("threadid") final String threadid, final Callback<Response> response);
    }

    private class SubscribeCallback implements Callback<Response> {

        private final UnifiedThread mUnifiedThread;

        private final boolean mNewValue;

        public SubscribeCallback(final UnifiedThread unifiedThread, final boolean newValue) {
            mUnifiedThread = unifiedThread;
            mNewValue = newValue;
        }

        @Override
        public void success(final Response response, final Response response2) {
            final Result result = Result.parseResultFromResponse(response);
            if (Result.isSuccess(result)) {
                mBus.post(new ThreadSubscriptionChangedEvent(mUnifiedThread, mNewValue));
            } else {
                mBus.post(new ThreadSubscriptionChangingFailedEvent(mUnifiedThread));
            }
        }

        @Override
        public void failure(RetrofitError error) {
            Utils.handleRetrofitErrorQuietly(error);

            mBus.post(new ThreadSubscriptionChangingFailedEvent(mUnifiedThread));
        }
    }
}