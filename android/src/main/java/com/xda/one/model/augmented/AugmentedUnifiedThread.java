package com.xda.one.model.augmented;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.Spannable;

import com.xda.one.api.model.interfaces.UnifiedThread;
import com.xda.one.api.model.response.ResponseUnifiedThread;
import com.xda.one.parser.ContentParser;
import com.xda.one.parser.TextDataStructure;
import com.xda.one.util.PostUtils;

public class AugmentedUnifiedThread implements UnifiedThread {

    public static final Parcelable.Creator<AugmentedUnifiedThread> CREATOR
            = new Parcelable.Creator<AugmentedUnifiedThread>() {
        @Override
        public AugmentedUnifiedThread createFromParcel(Parcel source) {
            return new AugmentedUnifiedThread(source);
        }

        @Override
        public AugmentedUnifiedThread[] newArray(int size) {
            return new AugmentedUnifiedThread[size];
        }
    };

    private static final int MAX_STRING_LENGTH = 200;

    private final UnifiedThread mUnifiedThread;

    private final String mSubPageText;

    private boolean mSubscribed;

    public AugmentedUnifiedThread(final UnifiedThread thread, final Context context) {
        mUnifiedThread = thread;
        mSubscribed = thread.isSubscribed();

        final String text = thread.getFirstPostContent();
        final Spannable formattedContent = ContentParser.parseAndSmilifyBBCode(context, text);
        final TextDataStructure dataStructure = new TextDataStructure(formattedContent);

        mSubPageText = PostUtils.getCreatedText(dataStructure, MAX_STRING_LENGTH);
    }

    public AugmentedUnifiedThread(final Parcel source) {
        mUnifiedThread = new ResponseUnifiedThread(source);

        mSubscribed = source.readByte() != 0;
        mSubPageText = source.readString();
    }

    // Important methods
    @Override
    public boolean isSubscribed() {
        return mSubscribed;
    }

    public void setSubscribedFlag(boolean newValue) {
        mSubscribed = newValue;
    }

    public String getSubPageText() {
        return mSubPageText;
    }

    // Delegated methods
    @Override
    public String getThreadId() {
        return mUnifiedThread.getThreadId();
    }

    @Override
    public int getLastPostId() {
        return mUnifiedThread.getLastPostId();
    }

    @Override
    public String getLastPoster() {
        return mUnifiedThread.getLastPoster();
    }

    @Override
    public boolean isAttach() {
        return mUnifiedThread.isAttach();
    }

    @Override
    public int getFirstPostId() {
        return mUnifiedThread.getFirstPostId();
    }

    @Override
    public int getReplyCount() {
        return mUnifiedThread.getReplyCount();
    }

    @Override
    public boolean hasAttachment() {
        return mUnifiedThread.hasAttachment();
    }

    @Override
    public String getThreadSlug() {
        return mUnifiedThread.getThreadSlug();
    }

    @Override
    public String getForumTitle() {
        return mUnifiedThread.getForumTitle();
    }

    @Override
    public int getForumId() {
        return mUnifiedThread.getForumId();
    }

    @Override
    public int getViews() {
        return mUnifiedThread.getViews();
    }

    @Override
    public long getLastPost() {
        return mUnifiedThread.getLastPost();
    }

    @Override
    public String getTitle() {
        return mUnifiedThread.getTitle();
    }

    @Override
    public String getFirstPostContent() {
        return mUnifiedThread.getFirstPostContent();
    }

    @Override
    public String getPostUsername() {
        return mUnifiedThread.getPostUsername();
    }

    @Override
    public boolean isSticky() {
        return mUnifiedThread.isSticky();
    }

    @Override
    public int getTotalPosts() {
        return mUnifiedThread.getTotalPosts();
    }

    @Override
    public String getAvatarUrl() {
        return mUnifiedThread.getAvatarUrl();
    }

    @Override
    public boolean isUnread() {
        return mUnifiedThread.isUnread();
    }

    @Override
    public boolean isOpen() {
        return mUnifiedThread.isOpen();
    }

    @Override
    public String getWebUri() {
        return mUnifiedThread.getWebUri();
    }

    // Parcelable interface
    @Override
    public int describeContents() {
        return mUnifiedThread.describeContents();
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        mUnifiedThread.writeToParcel(parcel, i);

        parcel.writeByte(mSubscribed ? (byte) 1 : (byte) 0);
        parcel.writeString(mSubPageText);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        } else if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final AugmentedUnifiedThread thread = (AugmentedUnifiedThread) o;
        return mUnifiedThread.equals(thread.mUnifiedThread);
    }

    @Override
    public int hashCode() {
        return mUnifiedThread.hashCode();
    }
}