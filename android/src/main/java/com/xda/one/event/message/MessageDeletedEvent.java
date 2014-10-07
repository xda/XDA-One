package com.xda.one.event.message;

import com.xda.one.api.model.interfaces.Message;

public class MessageDeletedEvent extends MessageEvent {

    public MessageDeletedEvent(final Message message) {
        super(message);
    }
}