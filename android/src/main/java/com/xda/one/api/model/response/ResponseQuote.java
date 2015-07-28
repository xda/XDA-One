package com.xda.one.api.model.response;

import android.os.Parcel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.xda.one.api.model.interfaces.Quote;
import com.xda.one.api.model.interfaces.UnifiedThread;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponseQuote implements Quote {

    public static final Creator<ResponseQuote> CREATOR = new Creator<ResponseQuote>() {
        @Override
        public ResponseQuote createFromParcel(Parcel source) {
            return new ResponseQuote(source);
        }

        @Override
        public ResponseQuote[] newArray(int size) {
            return new ResponseQuote[size];
        }
    };

    private final ResponseAvatar mResponseAvatar;

    @JsonProperty("pagetext")
    private String mPageText;

    @JsonProperty("dateline")
    private int mDateLine;

    @JsonProperty("postid")
    private String mPostId;

    @JsonProperty("type")
    private String mType;

    @JsonProperty("userid")
    private String mUserId;

    @JsonProperty("username")
    private String mUserName;

    @JsonProperty("quoteduserid")
    private String mQuotedUserId;

    @JsonProperty("quotedusername")
    private String mQuotedUserName;

    @JsonProperty("quotedusergroupid")
    private int mQuotedUserGroupId;

    @JsonProperty("quotedinfractiongroupid")
    private int mQuotedInfractionGroupId;

    @JsonProperty("thread")
    private ResponseUnifiedThread mThread;

    public ResponseQuote() {
        mResponseAvatar = new ResponseAvatar();
    }

    public ResponseQuote(final Parcel in) {
        mResponseAvatar = new ResponseAvatar(in);

        mPageText = in.readString();
        mDateLine = in.readInt();
        mPostId = in.readString();
        mType = in.readString();
        mUserId = in.readString();
        mUserName = in.readString();
        mQuotedUserId = in.readString();
        mQuotedUserName = in.readString();
        mQuotedUserGroupId = in.readInt();
        mQuotedInfractionGroupId = in.readInt();
        mThread = in.readParcelable(ResponseUnifiedThread.class.getClassLoader());
    }

    @Override
    public String getPageText() {
        return mPageText;
    }

    @Override
    public int getDateLine() {
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
    public String getQuotedUserId() {
        return mQuotedUserId;
    }

    @Override
    public String getQuotedUserName() {
        return mQuotedUserName;
    }

    @Override
    public int getQuotedUserGroupId() {
        return mQuotedUserGroupId;
    }

    @Override
    public int getQuotedInfractionGroupId() {
        return mQuotedInfractionGroupId;
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
    public void writeToParcel(final Parcel dest, final int flags) {
        mResponseAvatar.writeToParcel(dest, flags);

        dest.writeString(mPageText);
        dest.writeInt(mDateLine);
        dest.writeString(mPostId);
        dest.writeString(mType);
        dest.writeString(mUserId);
        dest.writeString(mUserName);
        dest.writeString(mQuotedUserId);
        dest.writeString(mQuotedUserName);
        dest.writeInt(mQuotedUserGroupId);
        dest.writeInt(mQuotedInfractionGroupId);
        dest.writeParcelable(mThread, 0);
    }
}