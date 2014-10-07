package com.xda.one.api.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RequestMessage {

    private final String mUserName;

    private final String mSubject;

    private final String mMessage;

    public RequestMessage(final String userName, final String subject, final String message) {
        mUserName = userName;
        mSubject = subject;
        mMessage = message;
    }

    @JsonProperty(value = "message")
    public String getMessage() {
        return mMessage;
    }

    @JsonProperty(value = "subject")
    public String getSubject() {
        return mSubject;
    }

    @JsonProperty(value = "username")
    public String getUserName() {
        return mUserName;
    }
}