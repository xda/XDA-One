package com.xda.one.api.model.interfaces;

import android.os.Parcelable;

public interface Forum extends Parcelable {

    String getTitle();

    int getForumId();

    int getParentId();

    String getForumSlug();

    boolean isSubscribed();

    void setSubscribed(boolean subs);

    String getImageUrl();

    boolean hasChildren();

    String getWebUri();

    boolean canContainThreads();
}