package com.xda.one.api.retrofit;

import android.content.Context;

import com.xda.one.R;
import com.xda.one.api.inteface.UserClient;
import com.xda.one.api.misc.Consumer;
import com.xda.one.api.misc.EventBus;
import com.xda.one.api.misc.Result;
import com.xda.one.api.model.interfaces.container.MentionContainer;
import com.xda.one.api.model.interfaces.container.QuoteContainer;
import com.xda.one.api.model.response.ResponseUserProfile;
import com.xda.one.api.model.response.container.ResponseMentionContainer;
import com.xda.one.api.model.response.container.ResponseQuoteContainer;
import com.xda.one.auth.XDAAccount;
import com.xda.one.constants.XDAConstants;
import com.xda.one.event.user.UserLoginEvent;
import com.xda.one.event.user.UserLoginFailedEvent;
import com.xda.one.event.user.UserProfileEvent;
import com.xda.one.event.user.UserProfileFailedEvent;
import com.xda.one.parser.ContentParser;
import com.xda.one.util.Utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Header;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

import static com.xda.one.api.misc.Result.parseResultFromResponse;
import static com.xda.one.api.retrofit.RetrofitClient.getAuthToken;
import static com.xda.one.api.retrofit.RetrofitClient.setAuthToken;

public class RetrofitUserClient implements UserClient {

    private static UserClient sUserClient;

    private final UserAPI mUserAPI;

    private final Context mContext;

    private final EventBus mBus;

    private RetrofitUserClient(final Context context) {
        mUserAPI = RetrofitClient.getRestBuilder(context, XDAConstants.ENDPOINT_URL)
                .build()
                .create(UserAPI.class);
        mContext = context.getApplicationContext();
        mBus = new EventBus();
    }

    public static UserClient getClient(final Context context) {
        if (sUserClient == null) {
            sUserClient = new RetrofitUserClient(context);
        }
        return sUserClient;
    }

    private static String getCookieFromHeaders(final List<Header> headers) {
        final StringBuilder sb = new StringBuilder();
        for (final Header c : headers) {
            if ("Set-Cookie".equals(c.getName())) {
                sb.append(c.getValue()).append("; ");
            }
        }
        return sb.toString();
    }

    @Override
    public EventBus getBus() {
        return mBus;
    }

    @Override
    public void login(final String username, final String password) {
        final Map<String, String> map = new HashMap<>();
        map.put("username", username);
        map.put("password", password);

        mUserAPI.login(map, new Callback<Response>() {
            @Override
            public void success(final Response response, final Response response2) {
                final String authToken = getCookieFromHeaders(response.getHeaders());
                setAuthToken(authToken);
                getUserInternalAsync(new Consumer<ResponseUserProfile>() {
                    @Override
                    public void run(final ResponseUserProfile profile) {
                        final XDAAccount account = XDAAccount.fromProfile(profile);
                        mBus.post(new UserLoginEvent(account));
                    }
                }, new Consumer<RetrofitError>() {
                    @Override
                    public void run(final RetrofitError data) {
                        failure(data);
                    }
                });
            }

            @Override
            public void failure(final RetrofitError error) {
                final String output = Utils.handleRetrofitErrorQuietly(error);
                final Result result = Result.parseResultFromString(output);
                mBus.post(new UserLoginFailedEvent(result));
            }
        });
    }

    @Override
    public void googlelogin(final String accesToken) {
        final Map<String, String> map = new HashMap<>();
        map.put("access_token", accesToken);

        mUserAPI.googlelogin(map, new Callback<Response>() {
            @Override
            public void success(final Response response, final Response response2) {
                final String authToken = getCookieFromHeaders(response.getHeaders());
                setAuthToken(authToken);
                getUserInternalAsync(new Consumer<ResponseUserProfile>() {
                    @Override
                    public void run(final ResponseUserProfile profile) {
                        final XDAAccount account = XDAAccount.fromProfile(profile);
                        mBus.post(new UserLoginEvent(account));
                    }
                }, new Consumer<RetrofitError>() {
                    @Override
                    public void run(final RetrofitError data) {
                        failure(data);
                    }
                });
            }

            @Override
            public void failure(final RetrofitError error) {
                final String output = Utils.handleRetrofitErrorQuietly(error);
                final Result result = Result.parseResultFromString(output);
                result.setFromGoogle(true);
                mBus.post(new UserLoginFailedEvent(result));
            }
        });
    }

    @Override
    public void googleregister(final String username, final String accesToken, final Consumer<Response> success,
                               final Consumer<Result> failure) {
        final Map<String, String> map = new HashMap<>();
        map.put("username", username);
        map.put("access_token", accesToken);

        mUserAPI.googleregister(map, new Callback<Response>() {
            @Override
            public void success(final Response response, final Response response2) {
                final String authToken = getCookieFromHeaders(response.getHeaders());
                setAuthToken(authToken);
                success.run(response);
            }

            @Override
            public void failure(final RetrofitError error) {
                final String result = Utils.handleRetrofitErrorQuietly(error);
                failure.run(Result.parseResultFromString(result));
            }
        });
    }

    @Override
    public void register(final String email, final String username, final String password,
            final String challenge, final String response, final Consumer<Response> success,
            final Consumer<Result> failure) {
        final Map<String, String> map = new HashMap<>();
        map.put("username", username);
        map.put("password", password);
        map.put("email", email);
        map.put("captcha_chal", challenge);
        map.put("captcha_resp", response);

        mUserAPI.register(map, new Callback<Response>() {
            @Override
            public void success(final Response response, final Response response2) {
                final String authToken = getCookieFromHeaders(response.getHeaders());
                setAuthToken(authToken);
                success.run(response);
            }

            @Override
            public void failure(final RetrofitError error) {
                final String result = Utils.handleRetrofitErrorQuietly(error);
                failure.run(Result.parseResultFromString(result));
            }
        });
    }

    private void getUserInternalAsync(final Consumer<ResponseUserProfile> success,
            final Consumer<RetrofitError> failure) {
        mUserAPI.getUser(getAuthToken(), new Callback<ResponseUserProfile>() {
            @Override
            public void success(final ResponseUserProfile profile,
                    final Response response) {
                final CharSequence parsedSig = ContentParser.parseBBCode(mContext,
                        String.format(mContext.getString(R.string.user_profile_signature),
                                profile.getSignature()));
                profile.setParsedSignature(parsedSig);
                success.run(profile);
            }

            @Override
            public void failure(final RetrofitError error) {
                Utils.handleRetrofitErrorQuietly(error);
                failure.run(error);
            }
        });
    }

    @Override
    public MentionContainer getMentions(int page) {
        try {
            return mUserAPI.getMentions(getAuthToken(), page);
        } catch (RetrofitError error) {
            Utils.handleRetrofitErrorQuietly(error);
        }
        return null;
    }

    @Override
    public QuoteContainer getQuotes(int page) {
        try {
            return mUserAPI.getQuotes(getAuthToken(), page);
        } catch (RetrofitError error) {
            Utils.handleRetrofitErrorQuietly(error);
        }
        return null;
    }

    @Override
    public ResponseUserProfile getUserProfile() {
        try {
            ResponseUserProfile profile = mUserAPI.getUser(getAuthToken());

            final CharSequence parsedSig = ContentParser.parseBBCode(mContext,
                    String.format(mContext.getString(R.string.user_profile_signature),
                            profile.getSignature()));
            profile.setParsedSignature(parsedSig);

            return profile;
        } catch (RetrofitError error) {
            Utils.handleRetrofitErrorQuietly(error);
        }
        return null;
    }

    @Override
    public void getUserProfileAsync() {
        getUserInternalAsync(new Consumer<ResponseUserProfile>() {
            @Override
            public void run(final ResponseUserProfile profile) {
                mBus.post(new UserProfileEvent(XDAAccount.fromProfile(profile), profile));
            }
        }, new Consumer<RetrofitError>() {
            @Override
            public void run(final RetrofitError error) {
                Utils.handleRetrofitErrorQuietly(error);
                mBus.post(new UserProfileFailedEvent(parseResultFromResponse(error.getResponse())));
            }
        });
    }

    @Override
    public ResponseUserProfile getUserProfile(final String userId) {
        try {
            final ResponseUserProfile profile = mUserAPI.getUserProfile(getAuthToken(), userId);

            final CharSequence parsedSig = ContentParser.parseBBCode(mContext,
                    String.format(mContext.getString(R.string.user_profile_signature),
                            profile.getSignature()));
            profile.setParsedSignature(parsedSig);

            return profile;
        } catch (RetrofitError error) {
            Utils.handleRetrofitErrorQuietly(error);
        }
        return null;
    }

    private static interface UserAPI {

        @POST("/user/login")
        public void login(@Body final Map<String, String> body,
                final Callback<Response> callback);

        @POST("/user/googlelogin")
        public void googlelogin(@Body final Map<String, String> body,
                          final Callback<Response> callback);

        @POST("/user/googleregister")
        public void googleregister(@Body final Map<String, String> body,
                                final Callback<Response> callback);
        @POST("/user/register")
        public void register(@Body final Map<String, String> body,
                final Callback<Response> callback);

        @GET("/user")
        public ResponseUserProfile getUser(@retrofit.http.Header("Cookie") final String cookie);

        @GET("/user")
        public void getUser(@retrofit.http.Header("Cookie") final String cookie,
                final Callback<ResponseUserProfile> callback);

        @GET("/user/quotes")
        public ResponseQuoteContainer getQuotes(@retrofit.http.Header("Cookie") final String cookie,
                @Query("page") final int page);

        @GET("/user/mentions")
        public ResponseMentionContainer getMentions(@retrofit.http.Header("Cookie") final String
                cookie, @Query("page") final int page);

        @GET("/user/userinfo")
        public ResponseUserProfile getUserProfile(@retrofit.http.Header("Cookie") final String
                cookie, @Query("userid") final String userId);
    }
}