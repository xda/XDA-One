package com.xda.one.api.model.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.xda.one.api.model.interfaces.Post;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponsePost implements Post {

    public static final Parcelable.Creator<ResponsePost> CREATOR
            = new Parcelable.Creator<ResponsePost>() {
        @Override
        public ResponsePost createFromParcel(Parcel in) {
            return new ResponsePost(in);
        }

        @Override
        public ResponsePost[] newArray(int size) {
            return new ResponsePost[size];
        }
    };

    private final ResponseAvatar mResponseAvatar;

    @JsonProperty(value = "postid")
    private int mPostId;

    @JsonProperty(value = "visible")
    private int mVisible;

    @JsonProperty(value = "userid")
    private String mUserId;

    @JsonProperty(value = "title")
    private String mTitle;

    @JsonProperty(value = "pagetext")
    private String mPageText;

    @JsonProperty(value = "username")
    private String mUserName;

    @JsonProperty(value = "dateline")
    private long mDateline;

    @JsonProperty(value = "attachments")
    private List<ResponseAttachment> mAttachments;

    @JsonProperty(value = "thanks_count")
    private int mThanksCount;

    @JsonProperty(value = "has_thanked")
    private int mThanked;

    public ResponsePost() {
        mResponseAvatar = new ResponseAvatar();
    }

    public ResponsePost(final Parcel in) {
        mResponseAvatar = new ResponseAvatar(in);

        mPostId = in.readInt();
        mVisible = in.readInt();
        mUserId = in.readString();
        mTitle = in.readString();
        mPageText = in.readString();
        mUserName = in.readString();
        mDateline = in.readLong();

        mAttachments = new ArrayList<>();
        in.readTypedList(mAttachments, ResponseAttachment.CREATOR);

        mThanksCount = in.readInt();
        mThanked = in.readInt();
    }

    @Override
    public String getAvatarUrl() {
        return mResponseAvatar.getAvatarUrl();
    }

    @JsonProperty(value = "avatar_url")
    public void setAvatarUrl(final String avatarUrl) {
        mResponseAvatar.setAvatarUrl(avatarUrl);
    }

    @Override
    public int getPostId() {
        return mPostId;
    }

    @Override
    public int getVisible() {
        return mVisible;
    }

    @Override
    public String getUserId() {
        return mUserId;
    }

    @Override
    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    @Override
    public String getPageText() {
        return mPageText;
    }

    @Override
    public String getUserName() {
        return mUserName;
    }

    @Override
    public long getDateline() {
        return mDateline * 1000;
    }

    @Override
    public List<ResponseAttachment> getAttachments() {
        return mAttachments;
    }

    @Override
    public int getThanksCount() {
        return mThanksCount;
    }

    @Override
    public void setThanksCount(final int newCount) {
    }

    @Override
    public boolean isThanked() {
        return mThanked != 0;
    }

    // Post interface
    // Do nothing - this is invalid in a ResponsePost
    @Override
    public void setThanked(boolean thanked) {
    }

    // Parcelable interface
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        mResponseAvatar.writeToParcel(dest, flags);

        dest.writeInt(mPostId);
        dest.writeInt(mVisible);
        dest.writeString(mUserId);
        dest.writeString(mTitle);
        dest.writeString(mPageText);
        dest.writeString(mUserName);
        dest.writeLong(mDateline);
        dest.writeTypedList(mAttachments);
        dest.writeInt(mThanksCount);
        dest.writeInt(mThanked);
    }
}