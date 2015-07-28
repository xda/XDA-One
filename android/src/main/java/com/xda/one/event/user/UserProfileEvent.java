package com.xda.one.event.user;

import com.xda.one.api.model.response.ResponseUserProfile;
import com.xda.one.auth.XDAAccount;
import com.xda.one.event.Event;

public class UserProfileEvent extends Event {

    public final ResponseUserProfile userProfile;

    public final XDAAccount account;

    public UserProfileEvent(final XDAAccount account,
                            final ResponseUserProfile responseUserProfile) {
        this.account = account;
        this.userProfile = responseUserProfile;
    }
}
