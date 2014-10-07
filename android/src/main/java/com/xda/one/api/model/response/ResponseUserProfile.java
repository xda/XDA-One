package com.xda.one.api.model.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.xda.one.api.model.response.container.ResponseUserProfileNotificationContainer;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponseUserProfile {

    private final ResponseAvatar mResponseAvatar = new ResponseAvatar();

    @JsonProperty("userid")
    private String mUserId;

    @JsonProperty("signature")
    private String mSignature;

    @JsonProperty("username")
    private String mUserName;

    @JsonProperty("usertitle")
    private String mUserTitle;

    @JsonProperty("posts")
    private int mPosts;

    @JsonProperty("post_thanks_thanked_posts")
    private int mThankedPosts;

    @JsonProperty("post_thanks_thanked_times")
    private int mThankedTimes;

    @JsonProperty("devices")
    private List<ResponseUserProfileDevice> mDevices;

    @JsonProperty("email")
    private String mEmail;

    @JsonProperty("notifications")
    private ResponseUserProfileNotificationContainer mNotifications;

    private CharSequence mParsedSignature;

    public CharSequence getParsedSignature() {
        return mParsedSignature;
    }

    public void setParsedSignature(final CharSequence parsedSignature) {
        mParsedSignature = parsedSignature;
    }

    public String getUserId() {
        return mUserId;
    }

    public String getSignature() {
        return mSignature;
    }

    public String getUserName() {
        return mUserName;
    }

    public String getUserTitle() {
        return mUserTitle;
    }

    public int getPosts() {
        return mPosts;
    }

    public int getThankedPosts() {
        return mThankedPosts;
    }

    public int getThankedTimes() {
        return mThankedTimes;
    }

    public List<ResponseUserProfileDevice> getDevices() {
        return mDevices;
    }

    public String getEmail() {
        return mEmail;
    }

    public ResponseUserProfileNotificationContainer getNotifications() {
        return mNotifications;
    }

    // Delegate methods
    public String getAvatarUrl() {
        return mResponseAvatar.getAvatarUrl();
    }

    @JsonProperty(value = "avatar_url")
    public void setAvatarUrl(final String avatarUrl) {
        mResponseAvatar.setAvatarUrl(avatarUrl);
    }
}