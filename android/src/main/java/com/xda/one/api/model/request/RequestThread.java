package com.xda.one.api.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RequestThread {

    private final int mForumId;

    private final String mTitle;

    private final String mMessage;

    public RequestThread(final int forumId, final String title, final String message) {
        mForumId = forumId;
        mTitle = title;
        mMessage = message;
    }

    @JsonProperty(value = "forumid")
    public int getForumId() {
        return mForumId;
    }

    @JsonProperty(value = "title")
    public String getTitle() {
        return mTitle;
    }

    @JsonProperty(value = "message")
    public String getMessage() {
        return mMessage;
    }
}