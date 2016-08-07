package com.xda.one.api.model.interfaces.container;

import com.xda.one.api.model.interfaces.Quote;

import java.util.List;

public interface QuoteContainer {

    List<? extends Quote> getQuotes();

    int getTotalPages();

    int getPerPage();

    int getCurrentPage();
}
