package com.xda.one.event.forum;

import com.xda.one.api.model.interfaces.Forum;
import com.xda.one.event.Event;

public class ForumSubscriptionChangedEvent extends Event {

    public final Forum forum;

    public final boolean isNowSubscribed;

    public ForumSubscriptionChangedEvent(final Forum forum, final boolean isNowSubscribed) {
        this.forum = forum;
        this.isNowSubscribed = isNowSubscribed;
    }
}