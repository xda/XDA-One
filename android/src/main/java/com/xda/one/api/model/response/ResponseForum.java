package com.xda.one.api.model.response;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.xda.one.api.model.interfaces.Forum;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponseForum implements Forum {

    public static final Parcelable.Creator<ResponseForum> CREATOR = new Parcelable
            .Creator<ResponseForum>() {
        @Override
        public ResponseForum createFromParcel(Parcel in) {
            return new ResponseForum(in);
        }

        @Override
        public ResponseForum[] newArray(int size) {
            return new ResponseForum[size];
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

    @JsonProperty(value = "children")
    private List<ResponseForum> mChildren;

    @JsonProperty(value = "children_count")
    private int mChildrenCount;

    private ResponseForum(final Parcel in) {
        mTitle = in.readString();
        mForumId = in.readInt();
        mParentId = in.readInt();
        mForumSlug = in.readString();
        mIsSubscribed = in.readInt();
        mImageUrl = in.readString();
        mSearchable = in.readString();
        mCanContainThreads = in.readInt();
        mWebUri = in.readString();

        mChildren = new ArrayList<>();
        in.readTypedList(mChildren, CREATOR);

        mChildrenCount = in.readInt();
    }

    public ResponseForum() {
    }

    @Override
    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    @Override
    public int getForumId() {
        return mForumId;
    }

    public void setForumId(int forumId) {
        mForumId = forumId;
    }

    @Override
    public int getParentId() {
        return mParentId;
    }

    public void setParentId(int parentId) {
        mParentId = parentId;
    }

    @Override
    public String getForumSlug() {
        return mForumSlug;
    }

    public void setForumSlug(String forumSlug) {
        mForumSlug = forumSlug;
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

    public void setImageUrl(String imageUrl) {
        mImageUrl = imageUrl;
    }

    public String getSearchable() {
        return mSearchable;
    }

    public void setSearchable(final String searchable) {
        mSearchable = searchable;
    }

    @Override
    public String getWebUri() {
        return mWebUri;
    }

    public void setWebUri(final String webUri) {
        mWebUri = webUri;
    }

    @Override
    public boolean canContainThreads() {
        return mCanContainThreads != 0;
    }

    public void setCanContainThreads(final boolean canContainThreads) {
        mCanContainThreads = canContainThreads ? 1 : 0;
    }

    public List<ResponseForum> getChildren() {
        return mChildren;
    }

    @Override
    public boolean hasChildren() {
        return mChildrenCount != 0;
    }

    public void setChildrenCount(final int childrenCount) {
        mChildrenCount = childrenCount;
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

        dest.writeTypedList(mChildren);
        dest.writeInt(mCanContainThreads);
    }
}