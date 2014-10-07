package com.xda.one.api.model.response.container;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.xda.one.api.model.response.ResponseUserProfileNotification;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponseUserProfileNotificationContainer {

    @JsonProperty("pmunread")
    private ResponseUserProfileNotification mPmUnread;

    @JsonProperty("friendreqcount")
    private ResponseUserProfileNotification mFriendReqCount;

    @JsonProperty("socgroupreqcount")
    private ResponseUserProfileNotification mSocGroupReqCount;

    @JsonProperty("socgroupinvitecount")
    private ResponseUserProfileNotification mSocGroupInviteCount;

    @JsonProperty("pcunreadcount")
    private ResponseUserProfileNotification mPcUnreadCount;

    @JsonProperty("pcmoderatedcount")
    private ResponseUserProfileNotification mPcModeratedCount;

    @JsonProperty("gmmoderatedcount")
    private ResponseUserProfileNotification mGmModeratedCount;

    @JsonProperty("dbtech_usertag_mentioncount")
    private ResponseUserProfileNotification mDbTechMetionCount;

    @JsonProperty("dbtech_usertag_quotecount")
    private ResponseUserProfileNotification mDebTechQuoteCount;

    @JsonProperty("devdbupdates")
    private ResponseUserProfileNotification mDevDbUpdates;

    @JsonProperty("total")
    private int mTotal;

    public ResponseUserProfileNotification getPmUnread() {
        return mPmUnread;
    }

    public ResponseUserProfileNotification getFriendReqCount() {
        return mFriendReqCount;
    }

    public ResponseUserProfileNotification getSocGroupReqCount() {
        return mSocGroupReqCount;
    }

    public ResponseUserProfileNotification getSocGroupInviteCount() {
        return mSocGroupInviteCount;
    }

    public ResponseUserProfileNotification getPcUnreadCount() {
        return mPcUnreadCount;
    }

    public ResponseUserProfileNotification getPcModeratedCount() {
        return mPcModeratedCount;
    }

    public ResponseUserProfileNotification getGmModeratedCount() {
        return mGmModeratedCount;
    }

    public ResponseUserProfileNotification getDbTechMetionCount() {
        return mDbTechMetionCount;
    }

    public ResponseUserProfileNotification getDbTechQuoteCount() {
        return mDebTechQuoteCount;
    }

    public ResponseUserProfileNotification getDevDbUpdates() {
        return mDevDbUpdates;
    }

    public int getTotal() {
        return mTotal;
    }

}