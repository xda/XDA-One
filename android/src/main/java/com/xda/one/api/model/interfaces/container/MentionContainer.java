package com.xda.one.api.model.interfaces.container;

import com.xda.one.api.model.interfaces.Mention;

import java.util.List;

public interface MentionContainer {

    public List<? extends Mention> getMentions();

    public int getTotalPages();

    public int getPerPage();

    public int getCurrentPage();
}
