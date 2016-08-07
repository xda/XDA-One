package com.xda.one.api.model.interfaces.container;

import com.xda.one.api.model.interfaces.Message;

import java.util.List;

public interface MessageContainer {

    List<? extends Message> getMessages();

    int getTotalPages();

    int getMessagesPerPage();

    int getCurrentPage();
}
