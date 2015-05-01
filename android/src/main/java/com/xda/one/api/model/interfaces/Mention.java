package com.xda.one.api.model.interfaces;

import android.os.Parcelable;

public interface Mention extends Parcelable {

    String getPageText();

    String getDateLine();

    String getPostId();

    String getType();

    String getUserId();

    String getUserName();

    String getMentionedUserId();

    String getMentionedUsername();

    String getMentionedUserGroupId();

    String getMentionedInfractionGroupId();

    UnifiedThread getThread();

    String getAvatarUrl();
}
