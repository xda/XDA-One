package com.xda.one.event.message;

import com.xda.one.api.model.interfaces.Message;
import com.xda.one.event.Event;

public class MessageEvent extends Event {

    public final Message message;

    public MessageEvent() {
        message = null;
    }

    public MessageEvent(final Message message) {
        this.message = message;
    }
}