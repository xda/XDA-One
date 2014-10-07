package com.xda.one.event.user;

import com.xda.one.api.misc.Result;
import com.xda.one.event.Event;

public class UserLoginFailedEvent extends Event {

    public final Result result;

    public UserLoginFailedEvent(final Result result) {
        this.result = result;
    }
}