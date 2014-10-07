package com.xda.one.api.model.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.xda.one.api.model.interfaces.Forum;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponseUserProfileDevice implements Forum {

    public static final Parcelable.Creator<ResponseUserProfileDevice> CREATOR = new Parcelable
            .Creator<ResponseUserProfileDevice>() {
        @Override
        public ResponseUserProfileDevice createFromParcel(Parcel in) {
            return new ResponseUserProfileDevice(in);
        }

        @Override
        public ResponseUserProfileDevice[] newArray(int size) {
            return new ResponseUserProfileDevice[size];
        }
    };

    @JsonProperty(value = "title")
    private String mTitle;

    @JsonProperty(value = "forumid")
    private int mForumId;

    @JsonProperty(value = "parentid")
    private int mParentId;

    @JsonProperty(value = "forumslug")
    private String mForumSlug;

    @JsonProperty(value = "subscribed")
    private int mIsSubscribed;

    @JsonProperty(value = "image")
    private String mImageUrl;

    @JsonProperty(value = "searchable")
    private String mSearchable;

    @JsonProperty(value = "cancontainthreads")
    private int mCanContainThreads;

    @JsonProperty(value = "web_uri")
    private String mWebUri;

    @JsonProperty(value = "has_children")
    private int mHasChildren;

    private ResponseUserProfileDevice(final Parcel in) {
        mTitle = in.readString();
        mForumId = in.readInt();
        mParentId = in.readInt();
        mForumSlug = in.readString();
        mIsSubscribed = in.readInt();
        mImageUrl = in.readString();
        mSearchable = in.readString();
        mCanContainThreads = in.readInt();
        mWebUri = in.readString();
        mHasChildren = in.readInt();
    }

    public ResponseUserProfileDevice() {
    }

    @Override
    public String getTitle() {
        return mTitle;
    }

    @Override
    public int getForumId() {
        return mForumId;
    }

    @Override
    public int getParentId() {
        return mParentId;
    }

    @Override
    public String getForumSlug() {
        return mForumSlug;
    }

    @Override
    public boolean isSubscribed() {
        return mIsSubscribed != 0;
    }

    @Override
    public void setSubscribed(boolean subs) {
        mIsSubscribed = subs ? 1 : 0;
    }

    @Override
    public String getImageUrl() {
        if (TextUtils.isEmpty(mImageUrl)) {
            return null;
        }
        return mImageUrl;
    }

    public String getSearchable() {
        return mSearchable;
    }

    @Override
    public String getWebUri() {
        return mWebUri;
    }

    @Override
    public boolean canContainThreads() {
        return mCanContainThreads != 0;
    }

    @Override
    public boolean hasChildren() {
        return mHasChildren != 0;
    }

    // Two forums are equal if their forumids are equal
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ResponseForum)) {
            return false;
        }

        final ResponseForum other = (ResponseForum) o;
        return other.getForumId() == getForumId();
    }

    @Override
    public int hashCode() {
        return mForumId;
    }

    // Parcelable interface
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mTitle);
        dest.writeInt(mForumId);
        dest.writeInt(mParentId);
        dest.writeString(mForumSlug);
        dest.writeInt(mIsSubscribed);
        dest.writeString(mImageUrl);
        dest.writeString(mSearchable);
        dest.writeInt(mCanContainThreads);
        dest.writeString(mWebUri);
        dest.writeInt(mHasChildren);
    }
}