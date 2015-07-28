package com.xda.one.api.model.response;

import android.os.Parcel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.xda.one.api.model.interfaces.UnifiedThread;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponseUnifiedThread implements UnifiedThread {

    public static final Creator<ResponseUnifiedThread> CREATOR
            = new Creator<ResponseUnifiedThread>() {
        @Override
        public ResponseUnifiedThread createFromParcel(Parcel source) {
            return new ResponseUnifiedThread(source);
        }

        @Override
        public ResponseUnifiedThread[] newArray(int size) {
            return new ResponseUnifiedThread[size];
        }
    };

    private final ResponseAvatar mResponseAvatar;

    @JsonProperty(value = "threadid")
    private String mThreadId;

    @JsonProperty(value = "lastpost")
    private long mLastPost;

    @JsonProperty(value = "lastposter")
    private String mLastPoster;

    @JsonProperty(value = "lastpostid")
    private int mLastPostId;

    @JsonProperty(value = "replycount")
    private int mReplyCount;

    @JsonProperty(value = "firstpostid")
    private int mFirstPostId;

    @JsonProperty(value = "threadslug")
    private String mThreadSlug;

    @JsonProperty(value = "forumtitle")
    private String mForumTitle;

    @JsonProperty(value = "forumid")
    private int mForumId;

    @JsonProperty(value = "views")
    private int mViews;

    @JsonProperty(value = "title")
    private String mTitle;

    @JsonProperty(value = "pagetext")
    private String mPageText;

    @JsonProperty(value = "postusername")
    private String mPostUsername;

    @JsonProperty(value = "sticky")
    private int mSticky;

    @JsonProperty(value = "attach")
    private int mAttach;

    @JsonProperty(value = "has_attachment")
    private int mHasAttachment;

    @JsonProperty(value = "subscribed")
    private int mIsSubscribed;

    @JsonProperty(value = "total_posts")
    private int mTotalPosts;

    @JsonProperty(value = "unread")
    private int mUnread;

    @JsonProperty(value = "open")
    private int mOpen;

    @JsonProperty(value = "web_uri")
    private String mWebUri;

    public ResponseUnifiedThread() {
        mResponseAvatar = new ResponseAvatar();
    }

    public ResponseUnifiedThread(final Parcel in) {
        mResponseAvatar = new ResponseAvatar(in);

        mThreadId = in.readString();
        mLastPost = in.readLong();
        mLastPoster = in.readString();
        mLastPostId = in.readInt();
        mFirstPostId = in.readInt();
        mReplyCount = in.readInt();
        mThreadSlug = in.readString();
        mForumTitle = in.readString();
        mForumId = in.readInt();
        mViews = in.readInt();
        mTitle = in.readString();
        mPageText = in.readString();
        mPostUsername = in.readString();
        mSticky = in.readInt();
        mAttach = in.readInt();
        mHasAttachment = in.readInt();
        mIsSubscribed = in.readInt();
        mTotalPosts = in.readInt();
        mOpen = in.readInt();
        mWebUri = in.readString();
    }

    @Override
    public String getThreadId() {
        return mThreadId;
    }

    @Override
    public int getLastPostId() {
        return mLastPostId;
    }

    @Override
    public String getLastPoster() {
        return mLastPoster;
    }

    @Override
    public int getFirstPostId() {
        return mFirstPostId;
    }

    @Override
    public String getThreadSlug() {
        return mThreadSlug;
    }

    @Override
    public String getForumTitle() {
        return mForumTitle;
    }

    @Override
    public int getForumId() {
        return mForumId;
    }

    @Override
    public int getReplyCount() {
        return mReplyCount;
    }

    @Override
    public boolean isSubscribed() {
        return mIsSubscribed != 0;
    }

    @Override
    public boolean isAttach() {
        return mAttach != 0;
    }

    @Override
    public boolean hasAttachment() {
        return mHasAttachment != 0;
    }

    @Override
    public int getViews() {
        return mViews;
    }

    @Override
    public long getLastPost() {
        return mLastPost * 1000;
    }

    @Override
    public String getTitle() {
        return mTitle;
    }

    @Override
    public String getFirstPostContent() {
        return mPageText;
    }

    @Override
    public String getPostUsername() {
        return mPostUsername;
    }

    @Override
    public boolean isSticky() {
        return mSticky != 0;
    }

    @Override
    public int getTotalPosts() {
        return mTotalPosts;
    }

    @Override
    public String getAvatarUrl() {
        return mResponseAvatar.getAvatarUrl();
    }

    // Avatar delegate
    @JsonProperty(value = "avatar_url")
    private void setAvatarUrl(final String avatarUrl) {
        mResponseAvatar.setAvatarUrl(avatarUrl);
    }

    @Override
    public boolean isUnread() {
        return mUnread != 0;
    }

    @Override
    public boolean isOpen() {
        return mOpen != 0;
    }

    @Override
    public String getWebUri() {
        return mWebUri;
    }

    // Parcelable interface
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        mResponseAvatar.writeToParcel(dest, flags);

        dest.writeString(mThreadId);
        dest.writeLong(mLastPost);
        dest.writeString(mLastPoster);
        dest.writeInt(mLastPostId);
        dest.writeInt(mFirstPostId);
        dest.writeString(mThreadSlug);
        dest.writeString(mForumTitle);
        dest.writeInt(mForumId);
        dest.writeInt(mReplyCount);
        dest.writeInt(mViews);
        dest.writeString(mTitle);
        dest.writeString(mPageText);
        dest.writeString(mPostUsername);
        dest.writeInt(mSticky);
        dest.writeInt(mAttach);
        dest.writeInt(mHasAttachment);
        dest.writeInt(mIsSubscribed);
        dest.writeInt(mTotalPosts);
        dest.writeInt(mOpen);
        dest.writeString(mWebUri);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        } else if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final ResponseUnifiedThread that = (ResponseUnifiedThread) o;
        return mThreadId.equals(that.mThreadId);
    }

    @Override
    public int hashCode() {
        return mThreadId.hashCode();
    }
}
