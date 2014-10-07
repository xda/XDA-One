package com.xda.one.api.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RequestRegisterUser {

    private final String mUserName;

    private final String mPassword;

    private final String mEmail;

    public RequestRegisterUser(final String userName, final String password, final String email) {
        mUserName = userName;
        mPassword = password;
        mEmail = email;
    }

    @JsonProperty(value = "username")
    public String getUserName() {
        return mUserName;
    }

    @JsonProperty(value = "password")
    public String getPassword() {
        return mPassword;
    }

    @JsonProperty(value = "email")
    public String getEmail() {
        return mEmail;
    }
}