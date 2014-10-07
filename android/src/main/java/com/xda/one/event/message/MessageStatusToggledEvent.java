package com.xda.one.event.message;

import com.xda.one.api.model.interfaces.Message;

public class MessageStatusToggledEvent extends MessageEvent {

    public MessageStatusToggledEvent(final Message message) {
        super(message);
    }
}