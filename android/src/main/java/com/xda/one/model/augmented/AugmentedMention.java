package com.xda.one.model.augmented;

import com.xda.one.api.model.interfaces.Mention;
import com.xda.one.api.model.response.ResponseMention;
import com.xda.one.parser.ContentParser;

import android.content.Context;
import android.os.Parcel;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;

public class AugmentedMention implements Mention {

    private static final String MENTION_USERNAME_CONTENT_SEPARATOR = " - ";

    public static final Creator<AugmentedMention> CREATOR = new Creator<AugmentedMention>() {
        @Override
        public AugmentedMention createFromParcel(Parcel source) {
            return new AugmentedMention(source);
        }

        @Override
        public AugmentedMention[] newArray(int size) {
            return new AugmentedMention[size];
        }
    };

    private final Mention mMention;

    private final AugmentedUnifiedThread mUnifiedThread;

    private final Spanned mCombinedUsernameContent;

    public AugmentedMention(final Mention mention, final Context context, final int primary,
            final int secondary) {
        mMention = mention;
        mUnifiedThread = new AugmentedUnifiedThread(mention.getThread(), context);

        final Spanned parsed = ContentParser.parseBBCode(context, mention.getPageText());
        final SpannableStringBuilder builder = new SpannableStringBuilder(mention
                .getUserName());

        final int primaryEnd = builder.length();
        builder.setSpan(new ForegroundColorSpan(primary), 0, primaryEnd,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        builder.append(MENTION_USERNAME_CONTENT_SEPARATOR);
        builder.append(parsed);
        builder.setSpan(new ForegroundColorSpan(secondary), primaryEnd, builder.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        mCombinedUsernameContent = builder;
    }

    public AugmentedMention(final Parcel source) {
        mMention = new ResponseMention(source);

        mCombinedUsernameContent = Html.fromHtml(source.readString());
        mUnifiedThread = new AugmentedUnifiedThread(source);
    }

    public Spanned getCombinedUsernameContent() {
        return mCombinedUsernameContent;
    }

    @Override
    public String getPageText() {
        return mMention.getPageText();
    }

    @Override
    public String getDateLine() {
        return mMention.getDateLine();
    }

    @Override
    public String getPostId() {
        return mMention.getPostId();
    }

    @Override
    public String getType() {
        return mMention.getType();
    }

    @Override
    public String getUserId() {
        return mMention.getUserId();
    }

    @Override
    public String getUserName() {
        return mMention.getUserName();
    }

    @Override
    public String getMentionedUserId() {
        return mMention.getMentionedUserId();
    }

    @Override
    public String getMentionedUsername() {
        return mMention.getMentionedUsername();
    }

    @Override
    public String getMentionedUserGroupId() {
        return mMention.getMentionedUserGroupId();
    }

    @Override
    public String getMentionedInfractionGroupId() {
        return mMention.getMentionedInfractionGroupId();
    }

    @Override
    public AugmentedUnifiedThread getThread() {
        return mUnifiedThread;
    }

    @Override
    public String getAvatarUrl() {
        return mMention.getAvatarUrl();
    }

    @Override
    public int describeContents() {
        return mMention.describeContents();
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        mMention.writeToParcel(dest, flags);
        dest.writeString(Html.toHtml(mCombinedUsernameContent));
        dest.writeParcelable(mUnifiedThread, flags);
    }
}