package com.xda.one.api.model.response.container;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.xda.one.api.model.response.ResponseForum;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponseForumContainer {

    @JsonProperty(value = "results")
    private List<ResponseForum> mForums;

    public List<ResponseForum> getForums() {
        return mForums;
    }
}
