package com.xda.one.event.forum;

import com.xda.one.api.model.interfaces.Forum;
import com.xda.one.event.Event;

public class ForumSubscriptionChangingFailedEvent extends Event {

    public final Forum forum;

    public ForumSubscriptionChangingFailedEvent(final Forum forum) {
        this.forum = forum;
    }
}