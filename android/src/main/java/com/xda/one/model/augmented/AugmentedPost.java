package com.xda.one.model.augmented;

import com.xda.one.api.model.interfaces.Post;
import com.xda.one.api.model.response.ResponseAttachment;
import com.xda.one.api.model.response.ResponsePost;
import com.xda.one.parser.ContentParser;
import com.xda.one.parser.TextDataStructure;
import com.xda.one.util.PostUtils;

import android.content.Context;
import android.os.Parcel;
import android.text.Spannable;

import java.util.List;

public class AugmentedPost implements Post {

    public static final ResponsePost.Creator CREATOR = new ResponsePost.Creator() {
        @Override
        public AugmentedPost createFromParcel(final Parcel in) {
            return new AugmentedPost(in);
        }

        @Override
        public AugmentedPost[] newArray(final int size) {
            return new AugmentedPost[size];
        }
    };

    private final Post mPost;

    private String mFormatlessText;

    private String mCreatedText;

    private TextDataStructure mTextDataStructure;

    private boolean mThanked;

    private int mThanksCount;

    private AugmentedPost(final Parcel parcel) {
        mPost = new ResponsePost(parcel);

        mFormatlessText = parcel.readString();
        mCreatedText = parcel.readString();
        mThanked = parcel.readByte() != 0;
        mThanksCount = parcel.readInt();
    }

    public AugmentedPost(final Post post, final Context context) {
        mPost = post;
        mThanked = post.isThanked();
        mThanksCount = post.getThanksCount();

        final Spannable formattedContent = ContentParser.parseAndSmilifyBBCode(context,
                post.getPageText());
        mTextDataStructure = new TextDataStructure(formattedContent);
        mFormatlessText = formattedContent.toString();

        mCreatedText = PostUtils.getCreatedText(mTextDataStructure);
    }

    public String getFormatlessText() {
        return mFormatlessText;
    }

    public TextDataStructure getTextDataStructure() {
        return mTextDataStructure;
    }

    public String getCreatedText() {
        return mCreatedText;
    }

    @Override
    public int getPostId() {
        return mPost.getPostId();
    }

    @Override
    public int getVisible() {
        return mPost.getVisible();
    }

    @Override
    public String getUserId() {
        return mPost.getUserId();
    }

    @Override
    public String getTitle() {
        return mPost.getTitle();
    }

    @Override
    public String getPageText() {
        return mPost.getPageText();
    }

    @Override
    public String getUserName() {
        return mPost.getUserName();
    }

    @Override
    public long getDateline() {
        return mPost.getDateline();
    }

    @Override
    public List<ResponseAttachment> getAttachments() {
        return mPost.getAttachments();
    }

    @Override
    public String getAvatarUrl() {
        return mPost.getAvatarUrl();
    }

    @Override
    public int getThanksCount() {
        return mThanksCount;
    }

    @Override
    public void setThanksCount(int newCount) {
        mThanksCount = newCount;
    }

    @Override
    public boolean isThanked() {
        return mThanked;
    }

    @Override
    public void setThanked(boolean thanked) {
        mThanked = thanked;
    }

    @Override
    public int describeContents() {
        return mPost.describeContents();
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        mPost.writeToParcel(dest, flags);

        dest.writeString(mFormatlessText);
        dest.writeString(mCreatedText);
        dest.writeByte(mThanked ? (byte) 1 : (byte) 0);
        dest.writeInt(mThanksCount);
    }
}