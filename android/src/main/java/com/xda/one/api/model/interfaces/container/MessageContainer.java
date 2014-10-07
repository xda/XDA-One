package com.xda.one.api.model.interfaces.container;

import com.xda.one.api.model.interfaces.Message;

import java.util.List;

public interface MessageContainer {

    public List<? extends Message> getMessages();

    public int getTotalPages();

    public int getMessagesPerPage();

    public int getCurrentPage();
}
