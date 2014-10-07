package com.xda.one.api.model.interfaces.container;

import com.xda.one.api.model.interfaces.Quote;

import java.util.List;

public interface QuoteContainer {

    public List<? extends Quote> getQuotes();

    public int getTotalPages();

    public int getPerPage();

    public int getCurrentPage();
}
