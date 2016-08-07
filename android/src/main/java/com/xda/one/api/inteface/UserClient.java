package com.xda.one.api.inteface;

import com.xda.one.api.misc.Consumer;
import com.xda.one.api.misc.EventBus;
import com.xda.one.api.misc.Result;
import com.xda.one.api.model.interfaces.container.MentionContainer;
import com.xda.one.api.model.interfaces.container.QuoteContainer;
import com.xda.one.api.model.response.ResponseUserProfile;

import retrofit.client.Response;

public interface UserClient {

    EventBus getBus();

    void login(final String username, final String password);

    void register(final String email, final String username, final String password,
            final String challenge, final String response, final Consumer<Response> success,
            final Consumer<Result> failure);

    MentionContainer getMentions(final int page);

    QuoteContainer getQuotes(final int page);

    ResponseUserProfile getUserProfile();

    void getUserProfileAsync();

    ResponseUserProfile getUserProfile(final String userId);
}