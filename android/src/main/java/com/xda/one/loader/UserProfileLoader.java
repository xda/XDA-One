package com.xda.one.loader;

import android.content.Context;

import com.xda.one.api.inteface.UserClient;
import com.xda.one.api.model.response.ResponseUserProfile;
import com.xda.one.api.retrofit.RetrofitUserClient;

public class UserProfileLoader extends AsyncLoader<ResponseUserProfile> {

    private final UserClient mUserClient;

    private final String mUserId;

    public UserProfileLoader(final Context context, final String userId) {
        super(context);

        mUserClient = RetrofitUserClient.getClient(context);
        mUserId = userId;
    }

    @Override
    public ResponseUserProfile loadInBackground() {
        if (mUserId == null) {
            return mUserClient.getUserProfile();
        }
        return mUserClient.getUserProfile(mUserId);
    }

    @Override
    protected void releaseResources(final ResponseUserProfile data) {
    }
}
