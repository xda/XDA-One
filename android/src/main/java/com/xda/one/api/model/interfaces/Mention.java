package com.xda.one.api.model.interfaces;

import android.os.Parcelable;

public interface Mention extends Parcelable {

    public String getPageText();

    public String getDateLine();

    public String getPostId();

    public String getType();

    String getUserId();

    public String getUserName();

    public String getMentionedUserId();

    public String getMentionedUsername();

    public String getMentionedUserGroupId();

    public String getMentionedInfractionGroupId();

    public UnifiedThread getThread();

    String getAvatarUrl();
}
