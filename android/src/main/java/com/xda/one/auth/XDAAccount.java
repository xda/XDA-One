package com.xda.one.auth;

import android.accounts.Account;
import android.os.Parcel;

import com.xda.one.api.model.response.ResponseUserProfile;
import com.xda.one.api.model.response.container.ResponseUserProfileNotificationContainer;
import com.xda.one.api.retrofit.RetrofitClient;

public class XDAAccount extends Account {

    public static final Creator<XDAAccount> CREATOR = new Creator<XDAAccount>() {
        @Override
        public XDAAccount createFromParcel(Parcel source) {
            return new XDAAccount(source);
        }

        @Override
        public XDAAccount[] newArray(int size) {
            return new XDAAccount[size];
        }
    };

    private final String mEmail;

    private final String mUserId;

    private final String mAvatarUrl;

    private final int mPmCount;

    private final int mQuoteCount;

    private final int mMentionCount;

    private final String mAuthToken;

    public XDAAccount(final String name, final String userId, final String email,
            final String avatarUrl, final int pmCount, final int quoteCount,
            final int mentionCount, final String authToken) {
        super(name, "com.one");
        mEmail = email;
        mUserId = userId;
        mAvatarUrl = avatarUrl;
        mPmCount = pmCount;
        mQuoteCount = quoteCount;
        mMentionCount = mentionCount;

        mAuthToken = authToken;
    }

    public XDAAccount(final Parcel source) {
        super(source);

        mEmail = source.readString();
        mUserId = source.readString();
        mAvatarUrl = source.readString();
        mPmCount = source.readInt();
        mQuoteCount = source.readInt();
        mMentionCount = source.readInt();
        mAuthToken = source.readString();
    }

    public static XDAAccount fromProfile(final ResponseUserProfile profile) {
        final ResponseUserProfileNotificationContainer notifications = profile.getNotifications();
        return new XDAAccount(profile.getUserName(), profile.getUserId(), profile.getEmail(),
                profile.getAvatarUrl(), notifications.getPmUnread().getTotal(),
                notifications.getDbTechQuoteCount().getTotal(),
                notifications.getDbTechMetionCount().getTotal(),
                RetrofitClient.getAuthToken());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);

        dest.writeString(mEmail);
        dest.writeString(mUserId);
        dest.writeString(mAvatarUrl);
        dest.writeInt(mPmCount);
        dest.writeInt(mQuoteCount);
        dest.writeInt(mMentionCount);
        dest.writeString(mAuthToken);
    }

    public String getEmail() {
        return mEmail;
    }

    public String getUserId() {
        return mUserId;
    }

    public String getAvatarUrl() {
        return mAvatarUrl;
    }

    public String getUserName() {
        return name;
    }

    public int getPmCount() {
        return mPmCount;
    }

    public int getQuoteCount() {
        return mQuoteCount;
    }

    public int getMentionCount() {
        return mMentionCount;
    }

    public String getAuthToken() {
        return mAuthToken;
    }
}