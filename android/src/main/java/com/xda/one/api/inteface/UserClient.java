package com.xda.one.api.inteface;

import com.xda.one.api.misc.Consumer;
import com.xda.one.api.misc.EventBus;
import com.xda.one.api.misc.Result;
import com.xda.one.api.model.interfaces.container.MentionContainer;
import com.xda.one.api.model.interfaces.container.QuoteContainer;
import com.xda.one.api.model.response.ResponseUserProfile;

import retrofit.client.Response;

public interface UserClient {

    public EventBus getBus();

    //Create Method to Call API, Method name should be same as api request name
    public void login(final String username, final String password);

    public void googlelogin(final String accessToken);

    public void googleregister(final String username, final String accessToken, final Consumer<Response> success,
                               final Consumer<Result> failure);


    void register(final String email, final String username, final String password,
                  final String challenge, final String response, final Consumer<Response> success,
                  final Consumer<Result> failure);

    public MentionContainer getMentions(final int page);

    public QuoteContainer getQuotes(final int page);

    public ResponseUserProfile getUserProfile();

    public void getUserProfileAsync();

    public ResponseUserProfile getUserProfile(final String userId);
}