package com.xda.one.loader;

import com.xda.one.api.inteface.NewsClient;
import com.xda.one.api.model.response.container.ResponseNewsContainer;
import com.xda.one.api.retrofit.RetrofitNewsClient;

import android.content.Context;

public class NewsLoader extends AsyncLoader<ResponseNewsContainer> {

    private final NewsClient mNewsClient;

    private int mPage;

    public NewsLoader(Context context, int page) {
        super(context);

        mPage = page;
        mNewsClient = RetrofitNewsClient.getClient(getContext());
    }

    @Override
    public void releaseResources(ResponseNewsContainer data) {
    }

    @Override
    public ResponseNewsContainer loadInBackground() {
        return mNewsClient.getNews(mPage);
    }
}