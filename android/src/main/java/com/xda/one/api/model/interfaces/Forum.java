package com.xda.one.api.model.interfaces;

import android.os.Parcelable;

import java.util.List;

public interface Forum extends Parcelable {

    public String getTitle();

    public int getForumId();

    public int getParentId();

    public String getForumSlug();

    public boolean isSubscribed();

    public void setSubscribed(boolean subs);

    public String getImageUrl();

    public boolean hasChildren();

    public String getWebUri();

    public boolean canContainThreads();
}