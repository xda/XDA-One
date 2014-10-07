package com.xda.one.api.model.interfaces;

import android.os.Parcelable;

public interface UnifiedThread extends Parcelable {

    public String getThreadId();

    public boolean isAttach();

    public boolean hasAttachment();

    public int getViews();

    public long getLastPost();

    public String getTitle();

    public String getFirstPostContent();

    public String getPostUsername();

    public boolean isSticky();

    public int getTotalPosts();

    public int getLastPostId();

    public String getLastPoster();

    public int getFirstPostId();

    public String getThreadSlug();

    String getForumTitle();

    public int getForumId();

    public int getReplyCount();

    public boolean isSubscribed();

    public String getAvatarUrl();

    public boolean isUnread();

    boolean isOpen();

    public String getWebUri();
}