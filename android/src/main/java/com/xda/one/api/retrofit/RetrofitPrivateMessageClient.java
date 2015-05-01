package com.xda.one.api.retrofit;

import com.xda.one.api.inteface.PrivateMessageClient;
import com.xda.one.api.misc.EventBus;
import com.xda.one.api.misc.Result;
import com.xda.one.api.model.interfaces.Message;
import com.xda.one.api.model.request.RequestMessage;
import com.xda.one.api.model.response.container.ResponseMessageContainer;
import com.xda.one.constants.XDAConstants;
import com.xda.one.event.message.MessageDeletedEvent;
import com.xda.one.event.message.MessageSendingFailedEvent;
import com.xda.one.event.message.MessageSentEvent;
import com.xda.one.event.message.MessageStatusToggledEvent;
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
import retrofit.http.PUT;
import retrofit.http.Query;

import static com.xda.one.api.retrofit.RetrofitClient.getAuthToken;

public class RetrofitPrivateMessageClient implements PrivateMessageClient {

    private static PrivateMessageClient sMessageClient;

    private final PrivateMessageAPI mMessageAPI;

    private final EventBus mBus;

    private RetrofitPrivateMessageClient(final Context context) {
        mMessageAPI = RetrofitClient.getRestBuilder(context, XDAConstants.ENDPOINT_URL)
                .build()
                .create(PrivateMessageAPI.class);
        mBus = new EventBus();
    }

    public static PrivateMessageClient getClient(final Context context) {
        if (sMessageClient == null) {
            sMessageClient = new RetrofitPrivateMessageClient(context);
        }
        return sMessageClient;
    }

    @Override
    public EventBus getBus() {
        return mBus;
    }

    @Override
    public ResponseMessageContainer getInboxMessages(final int page) {
        try {
            return mMessageAPI.getInboxMessages(getAuthToken(), page);
        } catch (RetrofitError error) {
            Utils.handleRetrofitErrorQuietly(error);
        }
        return null;
    }

    @Override
    public ResponseMessageContainer getSentMessages(final int page) {
        try {
            return mMessageAPI.getSentMessages(getAuthToken(), page);
        } catch (RetrofitError error) {
            Utils.handleRetrofitErrorQuietly(error);
        }
        return null;
    }

    @Override
    public void sendMessageAsync(final String username, final String subject,
            final String message) {
        final RequestMessage requestMessage = new RequestMessage(username, subject, message);
        mMessageAPI.sendMessageAsync(getAuthToken(), requestMessage, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                final Result result = Result.parseResultFromResponse(response);
                mBus.post(Result.isSuccess(result)
                        ? new MessageSentEvent()
                        : new MessageSendingFailedEvent());
            }

            @Override
            public void failure(final RetrofitError error) {
                Utils.handleRetrofitErrorQuietly(error);

                mBus.post(new MessageSendingFailedEvent());
            }
        });
    }

    @Override
    public void toggleMessageReadAsync(final Message responseMessage) {
        if (responseMessage.isMessageUnread()) {
            markMessageReadAsync(responseMessage);
        } else {
            markMessageUnreadAsync(responseMessage);
        }
    }

    @Override
    public void markMessageReadAsync(final Message message) {
        final int id = message.getPmId();
        mMessageAPI.markMessageReadAsync(getAuthToken(), id, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                final Result result = Result.parseResultFromResponse(response);
                if (result != null && result.isSuccess()) {
                    // TODO - due to the way the int works this may not actually be correct
                    message.setMessageReadStatus(1);
                    mBus.post(new MessageStatusToggledEvent(message));
                } else {
                    // TODO
                }
            }

            @Override
            public void failure(final RetrofitError error) {
                Utils.handleRetrofitErrorQuietly(error);
            }
        });
    }

    @Override
    public void markMessageUnreadAsync(final Message message) {
        final int id = message.getPmId();
        mMessageAPI.markMessageUnreadAsync(getAuthToken(), id, new Callback<Response>() {
            @Override
            public void success(final Response response, final Response response2) {
                final Result result = Result.parseResultFromResponse(response);
                if (result != null && result.isSuccess()) {
                    message.setMessageReadStatus(0);
                    mBus.post(new MessageStatusToggledEvent(message));
                } else {
                    // TODO
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Utils.handleRetrofitErrorQuietly(error);
            }
        });
    }

    @Override
    public void deleteMessageAsync(final Message message) {
        mMessageAPI.deleteMessageAsync(getAuthToken(), message.getPmId(), new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                final Result result = Result.parseResultFromResponse(response);
                if (Result.isSuccess(result)) {
                    mBus.post(new MessageDeletedEvent(message));
                } else {
                    // TODO
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Utils.handleRetrofitErrorQuietly(error);
            }
        });
    }

    protected interface PrivateMessageAPI {

        @GET("/pms/inbox")
        ResponseMessageContainer getInboxMessages(@Header("Cookie") final String cookie,
                                                  @Query("page") final int page);

        @GET("/pms/sent")
        ResponseMessageContainer getSentMessages(@Header("Cookie") final String cookie,
                                                 @Query("page") final int page);

        @POST("/pms/send")
        void sendMessageAsync(@Header("Cookie") final String cookie,
                              @Body final RequestMessage message, final Callback<Response> response);

        @PUT("/pms/markread")
        void markMessageReadAsync(@Header("Cookie") final String cookie,
                                  @Query("pmid") final int messageId, final Callback<Response> response);

        @PUT("/pms/markunread")
        void markMessageUnreadAsync(@Header("Cookie") final String cookie,
                                    @Query("pmid") final int messageId, final Callback<Response> response);

        @DELETE("/pms")
        void deleteMessageAsync(@Header("Cookie") final String cookie,
                                @Query("pmid") final int messageId, final Callback<Response> response);
    }
}