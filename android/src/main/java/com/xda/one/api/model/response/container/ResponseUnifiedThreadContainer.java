package com.xda.one.api.model.response.container;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.xda.one.api.model.interfaces.container.UnifiedThreadContainer;
import com.xda.one.api.model.response.ResponseUnifiedThread;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponseUnifiedThreadContainer implements UnifiedThreadContainer {

    @JsonProperty(value = "results")
    private List<ResponseUnifiedThread> mThreads;

    @JsonProperty(value = "total_pages")
    private int mTotalPages;

    @JsonProperty(value = "per_page")
    private int mPerPage;

    @JsonProperty(value = "current_page")
    private int mCurrentPage;

    // This is actually used by Jackson
    @SuppressWarnings("unused")
    public ResponseUnifiedThreadContainer() {
    }

    @Override
    public List<ResponseUnifiedThread> getThreads() {
        return mThreads;
    }

    @Override
    public int getTotalPages() {
        return mTotalPages;
    }

    @Override
    public int getPerPage() {
        return mPerPage;
    }

    @Override
    public int getCurrentPage() {
        return mCurrentPage;
    }
}