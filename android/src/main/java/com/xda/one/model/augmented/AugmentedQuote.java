package com.xda.one.model.augmented;

import android.content.Context;
import android.os.Parcel;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;

import com.xda.one.api.model.interfaces.Quote;
import com.xda.one.api.model.response.ResponseQuote;
import com.xda.one.parser.ContentParser;

public final class AugmentedQuote implements Quote {

    public static final Creator<AugmentedQuote> CREATOR = new Creator<AugmentedQuote>() {
        @Override
        public AugmentedQuote createFromParcel(Parcel source) {
            return new AugmentedQuote(source);
        }

        @Override
        public AugmentedQuote[] newArray(int size) {
            return new AugmentedQuote[size];
        }
    };

    private static final String QUOTE_USERNAME_CONTENT_SEPARATOR = " - ";

    private final Quote mQuote;

    private final AugmentedUnifiedThread mUnifiedThread;

    private final Spanned mCombinedUsernameContent;

    public AugmentedQuote(final Quote quote, final Context context, final int primary,
                          final int secondary) {
        mQuote = quote;
        mUnifiedThread = new AugmentedUnifiedThread(quote.getThread(), context);

        final Spanned parsed = ContentParser.parseBBCode(context, quote.getPageText());
        final SpannableStringBuilder builder = new SpannableStringBuilder(quote
                .getUserName());

        final int primaryEnd = builder.length();
        builder.setSpan(new ForegroundColorSpan(primary), 0, primaryEnd,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        builder.append(QUOTE_USERNAME_CONTENT_SEPARATOR);
        builder.append(parsed);
        builder.setSpan(new ForegroundColorSpan(secondary), primaryEnd, builder.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        mCombinedUsernameContent = builder;
    }

    private AugmentedQuote(final Parcel source) {
        mQuote = new ResponseQuote(source);

        mCombinedUsernameContent = Html.fromHtml(source.readString());
        mUnifiedThread = new AugmentedUnifiedThread(source);
    }

    public Spanned getCombinedUsernameContent() {
        return mCombinedUsernameContent;
    }

    @Override
    public String getPageText() {
        return mQuote.getPageText();
    }

    @Override
    public int getDateLine() {
        return mQuote.getDateLine();
    }

    @Override
    public String getPostId() {
        return mQuote.getPostId();
    }

    @Override
    public String getType() {
        return mQuote.getType();
    }

    @Override
    public String getUserId() {
        return mQuote.getUserId();
    }

    @Override
    public String getUserName() {
        return mQuote.getUserName();
    }

    @Override
    public String getQuotedUserId() {
        return mQuote.getQuotedUserId();
    }

    @Override
    public String getQuotedUserName() {
        return mQuote.getQuotedUserName();
    }

    @Override
    public int getQuotedUserGroupId() {
        return mQuote.getQuotedUserGroupId();
    }

    @Override
    public int getQuotedInfractionGroupId() {
        return mQuote.getQuotedInfractionGroupId();
    }

    @Override
    public AugmentedUnifiedThread getThread() {
        return mUnifiedThread;
    }

    @Override
    public String getAvatarUrl() {
        return mQuote.getAvatarUrl();
    }

    @Override
    public int describeContents() {
        return mQuote.describeContents();
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        mQuote.writeToParcel(dest, flags);

        dest.writeString(Html.toHtml(mCombinedUsernameContent));
        dest.writeParcelable(mUnifiedThread, flags);
    }
}