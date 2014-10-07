package com.xda.one.api.model.interfaces;

import android.os.Parcelable;

public interface Quote extends Parcelable {

    public String getPageText();

    public int getDateLine();

    public String getPostId();

    public String getType();

    String getUserId();

    public String getUserName();

    public String getQuotedUserId();

    public String getQuotedUserName();

    public int getQuotedUserGroupId();

    public int getQuotedInfractionGroupId();

    public UnifiedThread getThread();

    public String getAvatarUrl();
}
