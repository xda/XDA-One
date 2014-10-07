package com.xda.one.api.model.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.xda.one.api.model.interfaces.Mention;
import com.xda.one.api.model.interfaces.UnifiedThread;

import android.os.Parcel;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponseMention implements Mention {

    public static final Creator<ResponseMention> CREATOR = new Creator<ResponseMention>() {
        @Override
        public ResponseMention createFromParcel(Parcel source) {
            return new ResponseMention(source);
        }

        @Override
        public ResponseMention[] newArray(int size) {
            return new ResponseMention[size];
        }
    };

    private final ResponseAvatar mResponseAvatar;

    @JsonProperty("pagetext")
    private String mPageText;

    @JsonProperty("dateline")
    private String mDateLine;

    @JsonProperty("postid")
    private String mPostId;

    @JsonProperty("type")
    private String mType;

    @JsonProperty("userid")
    private String mUserId;

    @JsonProperty("username")
    private String mUserName;

    @JsonProperty("mentioneduserid")
    private String mMentionedUserId;

    @JsonProperty("mentionedusername")
    private String mMentionedUsername;

    @JsonProperty("mentionedusergroupid")
    private String mMentionedUserGroupId;

    @JsonProperty("mentionedinfractiongroupid")
    private String mMentionedInfractionGroupId;

    @JsonProperty("thread")
    private ResponseUnifiedThread mThread;

    public ResponseMention() {
        mResponseAvatar = new ResponseAvatar();
    }

    public ResponseMention(Parcel in) {
        mResponseAvatar = new ResponseAvatar(in);

        mPageText = in.readString();
        mDateLine = in.readString();
        mPostId = in.readString();
        mType = in.readString();
        mUserId = in.readString();
        mUserName = in.readString();
        mMentionedUserId = in.readString();
        mMentionedUsername = in.readString();
        mMentionedUserGroupId = in.readString();
        mMentionedInfractionGroupId = in.readString();
        mThread = in.readParcelable(UnifiedThread.class.getClassLoader());
    }

    @Override
    public String getPageText() {
        return mPageText;
    }

    @Override
    public String getDateLine() {
        return mDateLine;
    }

    @Override
    public String getPostId() {
        return mPostId;
    }

    @Override
    public String getType() {
        return mType;
    }

    @Override
    public String getUserId() {
        return mUserId;
    }

    @Override
    public String getUserName() {
        return mUserName;
    }

    @Override
    public String getMentionedUserId() {
        return mMentionedUserId;
    }

    @Override
    public String getMentionedUsername() {
        return mMentionedUsername;
    }

    @Override
    public String getMentionedUserGroupId() {
        return mMentionedUserGroupId;
    }

    @Override
    public String getMentionedInfractionGroupId() {
        return mMentionedInfractionGroupId;
    }

    @Override
    public UnifiedThread getThread() {
        return mThread;
    }

    @Override
    public String getAvatarUrl() {
        return mResponseAvatar.getAvatarUrl();
    }

    @JsonProperty("avatar_url")
    public void setAvatarUrl(final String avatarUrl) {
        mResponseAvatar.setAvatarUrl(avatarUrl);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        mResponseAvatar.writeToParcel(dest, flags);

        dest.writeString(mPageText);
        dest.writeString(mDateLine);
        dest.writeString(mPostId);
        dest.writeString(mType);
        dest.writeString(mUserId);
        dest.writeString(mUserName);
        dest.writeString(mMentionedUserId);
        dest.writeString(mMentionedUsername);
        dest.writeString(mMentionedUserGroupId);
        dest.writeString(mMentionedInfractionGroupId);
        mThread.writeToParcel(dest, flags);
    }
}
