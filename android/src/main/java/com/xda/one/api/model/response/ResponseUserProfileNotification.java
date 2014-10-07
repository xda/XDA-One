package com.xda.one.api.model.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponseUserProfileNotification {

    @JsonProperty("phrase")
    private String mPhrase;

    @JsonProperty("link")
    private String mLink;

    @JsonProperty("order")
    private int mOrder;

    @JsonProperty("total")
    private int mTotal;

    public String getPhrase() {
        return mPhrase;
    }

    public String getLink() {
        return mLink;
    }

    public int getOrder() {
        return mOrder;
    }

    public int getTotal() {
        return mTotal;
    }
}
