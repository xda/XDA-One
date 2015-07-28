package com.xda.one.loader;

import android.content.Context;

import com.xda.one.api.inteface.ForumClient;
import com.xda.one.api.model.response.ResponseForum;
import com.xda.one.api.retrofit.RetrofitForumClient;

import java.util.List;

public class SubscribedForumLoader extends AsyncLoader<List<ResponseForum>> {

    private final ForumClient mForumClient;

    public SubscribedForumLoader(Context context) {
        super(context);

        mForumClient = RetrofitForumClient.getClient(context);
    }

    @Override
    public void releaseResources(List<ResponseForum> data) {
    }

    @Override
    public List<ResponseForum> loadInBackground() {
        return mForumClient.getSubscribedForums();
    }
}