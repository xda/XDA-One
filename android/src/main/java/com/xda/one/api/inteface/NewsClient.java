package com.xda.one.api.inteface;

import com.xda.one.api.misc.EventBus;
import com.xda.one.api.model.response.container.ResponseNewsContainer;
import com.xda.one.api.model.response.container.ResponsePostContainer;

import retrofit.Callback;

public interface NewsClient {

    public EventBus getBus();

    public ResponseNewsContainer getNews(final int page);

    public void getNewsAsync(final int page, final Callback<ResponsePostContainer> consumer);
}
