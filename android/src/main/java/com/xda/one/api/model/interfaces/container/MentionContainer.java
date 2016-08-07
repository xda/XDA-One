package com.xda.one.api.model.interfaces.container;

import com.xda.one.api.model.interfaces.Mention;

import java.util.List;

public interface MentionContainer {

    List<? extends Mention> getMentions();

    int getTotalPages();

    int getPerPage();

    int getCurrentPage();
}
