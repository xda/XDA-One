package com.xda.one.api.inteface;

import com.xda.one.api.misc.EventBus;
import com.xda.one.api.model.interfaces.Message;
import com.xda.one.api.model.interfaces.container.MessageContainer;
import com.xda.one.api.model.response.container.ResponseMessageContainer;

public interface PrivateMessageClient {

    EventBus getBus();

    MessageContainer getInboxMessages(int page);

    MessageContainer getSentMessages(int page);

    void sendMessageAsync(final String username, final String subject, final String message);

    void markMessageReadAsync(final Message message);

    void markMessageUnreadAsync(final Message message);

    void deleteMessageAsync(final Message message);
}