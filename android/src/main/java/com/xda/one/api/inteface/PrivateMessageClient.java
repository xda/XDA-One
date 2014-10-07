package com.xda.one.api.inteface;

import com.xda.one.api.misc.EventBus;
import com.xda.one.api.model.interfaces.Message;
import com.xda.one.api.model.response.container.ResponseMessageContainer;

public interface PrivateMessageClient {

    public EventBus getBus();

    public ResponseMessageContainer getInboxMessages(int page);

    public ResponseMessageContainer getSentMessages(int page);

    public void sendMessageAsync(final String username, final String subject, final String message);

    public void markMessageReadAsync(final Message message);

    public void markMessageUnreadAsync(final Message message);

    public void deleteMessageAsync(final Message message);

    public void toggleMessageReadAsync(final Message message);
}