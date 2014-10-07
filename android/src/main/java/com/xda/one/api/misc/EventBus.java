package com.xda.one.api.misc;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;
import com.xda.one.event.Event;

public class EventBus {

    private final Bus mBus = new Bus(ThreadEnforcer.ANY);

    public void register(Object object) {
        mBus.register(object);
    }

    public void unregister(Object object) {
        mBus.unregister(object);
    }

    public void post(Event event) {
        mBus.post(event);
    }
}