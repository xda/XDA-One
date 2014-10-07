package com.xda.one.event.thread;

import com.xda.one.api.model.interfaces.UnifiedThread;
import com.xda.one.event.Event;

public class ThreadSubscriptionChangingFailedEvent extends Event {

    public final UnifiedThread thread;

    public ThreadSubscriptionChangingFailedEvent(final UnifiedThread thread) {
        this.thread = thread;
    }
}