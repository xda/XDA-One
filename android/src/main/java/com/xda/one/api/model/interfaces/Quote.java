package com.xda.one.api.model.interfaces;

import android.os.Parcelable;

public interface Quote extends Parcelable {

    String getPageText();

    int getDateLine();

    String getPostId();

    String getType();

    String getUserId();

    String getUserName();

    String getQuotedUserId();

    String getQuotedUserName();

    int getQuotedUserGroupId();

    int getQuotedInfractionGroupId();

    UnifiedThread getThread();

    String getAvatarUrl();
}
