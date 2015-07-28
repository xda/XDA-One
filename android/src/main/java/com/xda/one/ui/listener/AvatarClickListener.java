package com.xda.one.ui.listener;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.xda.one.ui.UserProfileActivity;

public class AvatarClickListener implements View.OnClickListener {

    private final Context mContext;

    public AvatarClickListener(final Context context) {
        mContext = context;
    }

    @Override
    public void onClick(final View avatar) {
        final String userId = (String) avatar.getTag();

        final Intent intent = new Intent(mContext, UserProfileActivity.class);
        intent.putExtra(UserProfileActivity.USER_ID_ARGUMENT, userId);
        mContext.startActivity(intent);
    }
}
