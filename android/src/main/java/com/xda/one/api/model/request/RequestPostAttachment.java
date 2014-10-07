package com.xda.one.api.model.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class RequestPostAttachment {

    private final String mPostId;

    private final String mPostHash;

    private final String mPostStartTime;

    public RequestPostAttachment(String postId, String postHash, String postStartTime) {
        mPostId = postId;
        mPostHash = postHash;
        mPostStartTime = postStartTime;
    }

    @JsonProperty(value = "postid")
    public String getPostId() {
        return mPostId;
    }

    @JsonProperty(value = "posthash")
    public String getPostHash() {
        return mPostHash;
    }

    @JsonProperty(value = "poststarttime")
    public String getPostStartTime() {
        return mPostStartTime;
    }
}