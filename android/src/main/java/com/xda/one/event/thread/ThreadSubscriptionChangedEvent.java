package com.xda.one.event.thread;

import com.xda.one.api.model.interfaces.UnifiedThread;
import com.xda.one.event.Event;

public class ThreadSubscriptionChangedEvent extends Event {

    public final UnifiedThread thread;

    public final boolean isNowSubscribed;

    public ThreadSubscriptionChangedEvent(final UnifiedThread thread,
                                          final boolean isNowSubscribed) {
        this.thread = thread;
        this.isNowSubscribed = isNowSubscribed;
    }
}