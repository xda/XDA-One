package com.xda.one.api.model.interfaces;

import android.os.Parcelable;

public interface UnifiedThread extends Parcelable {

    String getThreadId();

    boolean isAttach();

    boolean hasAttachment();

    int getViews();

    long getLastPost();

    String getTitle();

    String getFirstPostContent();

    String getPostUsername();

    boolean isSticky();

    int getTotalPosts();

    int getLastPostId();

    String getLastPoster();

    int getFirstPostId();

    String getThreadSlug();

    String getForumTitle();

    int getForumId();

    int getReplyCount();

    boolean isSubscribed();

    String getAvatarUrl();

    boolean isUnread();

    boolean isOpen();

    String getWebUri();
}