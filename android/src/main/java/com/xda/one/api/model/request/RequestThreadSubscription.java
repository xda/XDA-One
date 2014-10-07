package com.xda.one.api.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RequestThreadSubscription {

    private final String mThreadId;

    public RequestThreadSubscription(String threadId) {
        mThreadId = threadId;
    }

    @JsonProperty(value = "threadid")
    public String getThreadId() {
        return mThreadId;
    }
}