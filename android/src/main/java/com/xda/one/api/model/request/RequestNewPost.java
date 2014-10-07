package com.xda.one.api.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RequestNewPost {

    private final int mPostId;

    private final String mPostMessage;

    public RequestNewPost(int postId, String postMessage) {
        mPostId = postId;
        mPostMessage = postMessage;
    }

    @JsonProperty(value = "postid")
    public int getPostId() {
        return mPostId;
    }

    @JsonProperty(value = "message")
    public String getPostMessage() {
        return mPostMessage;
    }
}