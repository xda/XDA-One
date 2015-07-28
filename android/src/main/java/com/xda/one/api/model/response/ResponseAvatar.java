package com.xda.one.api.model.response;

import android.os.Parcel;
import android.text.TextUtils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class ResponseAvatar {

    private String mAvatarUrl;

    public ResponseAvatar(final Parcel in) {
        mAvatarUrl = in.readString();
    }

    public ResponseAvatar() {
    }

    public String getAvatarUrl() {
        if (TextUtils.isEmpty(mAvatarUrl)) {
            return null;
        }
        return mAvatarUrl;
    }

    public void setAvatarUrl(final String avatarUrl) {
        mAvatarUrl = avatarUrl;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mAvatarUrl);
    }
}