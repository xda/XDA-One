package com.xda.one.api.model.response.container;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.xda.one.api.model.interfaces.container.MessageContainer;
import com.xda.one.api.model.response.ResponseMessage;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponseMessageContainer implements MessageContainer {

    @JsonProperty(value = "results")
    private List<ResponseMessage> mMessages;

    @JsonProperty(value = "total_pages")
    private int mTotalPages;

    @JsonProperty(value = "per_page")
    private int mMessagesPerPage;

    @JsonProperty(value = "current_page")
    private int mCurrentPage;

    @Override
    public List<ResponseMessage> getMessages() {
        return mMessages;
    }

    @Override
    public int getTotalPages() {
        return mTotalPages;
    }

    @Override
    public int getMessagesPerPage() {
        return mMessagesPerPage;
    }

    @Override
    public int getCurrentPage() {
        return mCurrentPage;
    }
}
