package com.xda.one.api.model.response.container;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.xda.one.api.model.interfaces.Mention;
import com.xda.one.api.model.interfaces.container.MentionContainer;
import com.xda.one.api.model.response.ResponseMention;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponseMentionContainer implements MentionContainer {

    @JsonProperty("results")
    private List<ResponseMention> mMentions = new ArrayList<>();

    @JsonProperty("total_pages")
    private int mTotalPages;

    @JsonProperty("per_page")
    private int mPerPage;

    @JsonProperty("current_page")
    private int mCurrentPage;

    @Override
    public List<? extends Mention> getMentions() {
        return mMentions;
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