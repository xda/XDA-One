package com.xda.one.api.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RequestThanks {

    private final String mPostId;

    public RequestThanks(final int postId) {
        mPostId = String.valueOf(postId);
    }

    @JsonProperty(value = "postid")
    public String getPostId() {
        return mPostId;
    }
}