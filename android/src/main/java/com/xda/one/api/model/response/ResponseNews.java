package com.xda.one.api.model.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import android.os.Parcel;
import android.os.Parcelable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponseNews implements Parcelable {

    public static final Creator<ResponseNews> CREATOR
            = new Creator<ResponseNews>() {
        @Override
        public ResponseNews createFromParcel(Parcel source) {
            return new ResponseNews(source);
        }

        @Override
        public ResponseNews[] newArray(int size) {
            return new ResponseNews[size];
        }
    };

    @JsonProperty(value = "title")
    private String mTitle;

    @JsonProperty(value = "content")
    private String mContent;

    @JsonProperty(value = "url")
    private String mUrl;

    @JsonProperty(value = "thumbnail")
    private String mThumbnail;

    public ResponseNews() {

    }

    private ResponseNews(Parcel in) {

        mTitle = in.readString();
        mContent = in.readString();
        mUrl = in.readString();
        mThumbnail = in.readString();
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String mContent) {
        this.mContent = mContent;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String mUrl) {
        this.mUrl = mUrl;
    }

    public String getThumbnail() {
        return mThumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.mThumbnail = thumbnail;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mTitle);
        dest.writeString(mContent);
        dest.writeString(mUrl);
        dest.writeString(mThumbnail);
    }
}
