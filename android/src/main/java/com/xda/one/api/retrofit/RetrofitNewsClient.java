package com.xda.one.api.retrofit;

import com.xda.one.api.inteface.NewsClient;
import com.xda.one.api.misc.EventBus;
import com.xda.one.api.model.response.container.ResponseNewsContainer;
import com.xda.one.api.model.response.container.ResponsePostContainer;
import com.xda.one.constants.XDAConstants;
import com.xda.one.util.Utils;

import android.content.Context;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.http.GET;
import retrofit.http.Query;

public class RetrofitNewsClient implements NewsClient {

    private static NewsClient sNewsClient;

    private final NewsAPI mNewsAPI;

    private final EventBus mBus;

    private RetrofitNewsClient(final Context context) {
        mNewsAPI = RetrofitClient.getRestBuilder(context, XDAConstants.XDA_NEWS_URL)
                .build()
                .create(NewsAPI.class);
        mBus = new EventBus();
    }

    public static NewsClient getClient(final Context context) {
        if (sNewsClient == null) {
            sNewsClient = new RetrofitNewsClient(context);
        }
        return sNewsClient;
    }

    @Override
    public EventBus getBus() {
        return mBus;
    }

    @Override
    public ResponseNewsContainer getNews(final int page) {
        try {
            final ResponseNewsContainer news = mNewsAPI.getNews(1, page);
            // This is a hack - TODO - fix it
            news.setCurrentPage(page);
            return news;
        } catch (RetrofitError error) {
            Utils.handleRetrofitErrorQuietly(error);
        }
        return null;
    }

    @Override
    public void getNewsAsync(final int page, final Callback<ResponsePostContainer> callback) {
        mNewsAPI.getNewsAsync(1, page, callback);
    }

    public static interface NewsAPI {

        @GET("/")
        public ResponseNewsContainer getNews(@Query("json") final int json,
                @Query("page") final int page);

        @GET("/")
        public void getNewsAsync(@Query("json") final int json, @Query("page") final int page,
                final Callback<ResponsePostContainer> containerCallback);
    }
}