package com.xda.one.api.retrofit;

import com.xda.one.api.inteface.ForumClient;
import com.xda.one.api.misc.Consumer;
import com.xda.one.api.misc.EventBus;
import com.xda.one.api.misc.Result;
import com.xda.one.api.model.interfaces.Forum;
import com.xda.one.api.model.response.ResponseForum;
import com.xda.one.api.model.response.container.ResponseForumContainer;
import com.xda.one.constants.XDAConstants;
import com.xda.one.db.ForumDbHelper;
import com.xda.one.event.forum.ForumSubscriptionChangedEvent;
import com.xda.one.event.forum.ForumSubscriptionChangingFailedEvent;

import android.content.Context;

import java.util.List;

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
import static com.xda.one.util.Utils.handleRetrofitErrorQuietly;

public class RetrofitForumClient implements ForumClient {

    private static ForumClient sForumClient;

    private final ForumAPI mForumAPI;

    private final EventBus mBus;

    private final Context mContext;

    private RetrofitForumClient(final Context context) {
        mForumAPI = RetrofitClient.getRestBuilder(context, XDAConstants.ENDPOINT_URL).build()
                .create(ForumAPI.class);
        mBus = new EventBus();
        mContext = context.getApplicationContext();
    }

    public static ForumClient getClient(final Context context) {
        if (sForumClient == null) {
            sForumClient = new RetrofitForumClient(context);
        }
        return sForumClient;
    }

    @Override
    public EventBus getBus() {
        return mBus;
    }

    @Override
    public List<ResponseForum> getForums() {
        return getForums(false);
    }

    @Override
    public List<ResponseForum> getForums(final boolean forceReload) {
        try {
            // Don't use cache if we are force reloading
            return mForumAPI.getForums(getAuthToken(), forceReload ? "no-cache" : null).getForums();
        } catch (RetrofitError error) {
            handleRetrofitErrorQuietly(error);
        }
        return null;
    }

    @Override
    public List<ResponseForum> getGeneralForums() {
        return getGeneralForums(false);
    }

    @Override
    public List<ResponseForum> getGeneralForums(final boolean forceReload) {
        try {
            // Don't use cache if we are force reloading
            return mForumAPI.getGeneralForums(getAuthToken(), forceReload ? "no-cache" : null)
                    .getForums();
        } catch (RetrofitError error) {
            handleRetrofitErrorQuietly(error);
        }
        return null;
    }

    @Override
    public List<ResponseForum> getNewestForums() {
        return getNewestForums(false);
    }

    @Override
    public List<ResponseForum> getNewestForums(final boolean forceReload) {
        try {
            return mForumAPI.getNewestForums(getAuthToken(), forceReload ? "no-cache" : null)
                    .getForums();
        } catch (RetrofitError error) {
            handleRetrofitErrorQuietly(error);
        }
        return null;
    }

    @Override
    public List<ResponseForum> getForumChildren(final Forum forum) {
        try {
            return mForumAPI.getForumChildren(getAuthToken(), forum.getForumId()).getForums();
        } catch (RetrofitError error) {
            handleRetrofitErrorQuietly(error);
        }
        return null;
    }

    @Override
    public List<ResponseForum> getSubscribedForums() {
        try {

            return mForumAPI.getSubscribedForums(getAuthToken()).getForums();
        } catch (RetrofitError error) {
            handleRetrofitErrorQuietly(error);
        }
        return null;
    }

    @Override
    public List<ResponseForum> getTopForums() {
        return getTopForums(false);
    }

    @Override
    public List<ResponseForum> getTopForums(final boolean forceReload) {
        try {
            return mForumAPI.getTopForums(getAuthToken(), forceReload ? "no-cache" : null)
                    .getForums();
        } catch (RetrofitError error) {
            handleRetrofitErrorQuietly(error);
        }
        return null;
    }

    @Override
    public void toggleForumSubscriptionAsync(final Forum forum) {
        if (forum.isSubscribed()) {
            unsubscribeAsync(forum);
        } else {
            subscribeAsync(forum);
        }
    }

    @Override
    public void subscribeAsync(final Forum forum) {
        mForumAPI.subscribe(getAuthToken(), forum.getForumId(), new SubscribeCallback(forum, true));
    }

    @Override
    public void unsubscribeAsync(final Forum forum) {
        mForumAPI.unsubscribe(getAuthToken(), forum.getForumId(),
                new SubscribeCallback(forum, false));
    }

    @Override
    public void markReadAsync(final Forum forum, final Consumer<Result> callable) {
        mForumAPI.markRead(getAuthToken(), forum.getForumId(), new Callback<Response>() {
            @Override
            public void success(final Response response, final Response response2) {
                final Result result = Result.parseResultFromResponse(response);
                if (Result.isSuccess(result)) {
                    // TODO - set forum as read
                    callable.run(result);
                }
            }

            @Override
            public void failure(final RetrofitError error) {
                handleRetrofitErrorQuietly(error);
            }
        });
    }

    public interface ForumAPI {

        @GET("/forums")
        ResponseForumContainer getForums(@Header("Cookie") final String cookie,
                                         @Header("Cache-Control") final String header);

        @GET("/forums/children")
        ResponseForumContainer getForumChildren(@Header("Cookie") final String cookie,
                                                @Query("forumid") final int forumId);

        @GET("/forums/general")
        ResponseForumContainer getGeneralForums(@Header("Cookie") final String cookie,
                                                @Header("Cache-Control") final String header);

        @GET("/forums/newest")
        ResponseForumContainer getNewestForums(@Header("Cookie") final String cookie,
                                               @Header("Cache-Control") final String header);

        @GET("/forums/top")
        ResponseForumContainer getTopForums(@Header("Cookie") final String cookie,
                                            @Header("Cache-Control") final String header);

        @GET("/forums/subscribed")
        ResponseForumContainer getSubscribedForums(
                @Header("Cookie") final String cookie);

        @POST("/forums/subscribe")
        void subscribe(@Header("Cookie") final String cookie, @Body final int forumId,
                       final Callback<Response> callable);

        @DELETE("/forums/unsubscribe")
        void unsubscribe(@Header("Cookie") final String cookie,
                         @Query("forumid") final int forumId, final Callback<Response> callable);

        @POST("/forums/markread")
        void markRead(@Header("Cookie") final String cookie, @Body final int forumId,
                      final Callback<Response> callable);
    }

    private class SubscribeCallback implements Callback<Response> {

        private final Forum mForum;

        private final boolean mNewValue;

        public SubscribeCallback(final Forum forum, final boolean newValue) {
            mForum = forum;
            mNewValue = newValue;
        }

        @Override
        public void success(final Response response, final Response response2) {
            final Result result = Result.parseResultFromResponse(response);
            if (Result.isSuccess(result)) {
                // Update the database
                ForumDbHelper.getInstance(mContext).updateSubscribedFlag(mForum, mNewValue);

                mBus.post(new ForumSubscriptionChangedEvent(mForum, mNewValue));
            } else {
                mBus.post(new ForumSubscriptionChangingFailedEvent(mForum));
            }
        }

        @Override
        public void failure(final RetrofitError error) {
            handleRetrofitErrorQuietly(error);

            mBus.post(new ForumSubscriptionChangingFailedEvent(mForum));
        }
    }
}