package com.xda.one.event.user;

import com.xda.one.auth.XDAAccount;
import com.xda.one.event.Event;

public class UserLoginEvent extends Event {

    public final XDAAccount account;

    public UserLoginEvent(final XDAAccount account) {
        this.account = account;
    }
}