package com.xda.one.api.model.response.container;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.xda.one.api.model.interfaces.container.QuoteContainer;
import com.xda.one.api.model.response.ResponseQuote;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponseQuoteContainer implements QuoteContainer {

    @JsonProperty("results")
    private List<ResponseQuote> mQuotes;

    @JsonProperty("total_pages")
    private int mTotalPages;

    @JsonProperty("per_page")
    private int mPerPage;

    @JsonProperty("current_page")
    private int mCurrentPage;

    @Override
    public List<ResponseQuote> getQuotes() {
        return mQuotes;
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