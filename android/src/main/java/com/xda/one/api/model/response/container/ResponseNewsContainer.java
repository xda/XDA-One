package com.xda.one.api.model.response.container;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.xda.one.api.model.response.ResponseNews;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponseNewsContainer implements Parcelable {

    public static final Creator<ResponseNewsContainer> CREATOR
            = new Creator<ResponseNewsContainer>() {
        @Override
        public ResponseNewsContainer createFromParcel(Parcel source) {
            return new ResponseNewsContainer(source);
        }

        @Override
        public ResponseNewsContainer[] newArray(int size) {
            return new ResponseNewsContainer[size];
        }
    };

    @JsonProperty(value = "posts")
    private List<ResponseNews> mPosts;

    @JsonProperty(value = "pages")
    private int mTotalPages;

    private int mCurrentPage;

    public ResponseNewsContainer() {
    }

    private ResponseNewsContainer(Parcel in) {
        mPosts = new ArrayList<>();
        in.readTypedList(mPosts, ResponseNews.CREATOR);
        mTotalPages = in.readInt();
        mCurrentPage = in.readInt();
    }

    public List<ResponseNews> getNewsItems() {
        return mPosts;
    }

    public int getTotalPages() {
        return mTotalPages;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(mPosts);
        dest.writeInt(mTotalPages);
        dest.writeInt(mCurrentPage);
    }

    public int getCurrentPage() {
        return mCurrentPage;
    }

    public void setCurrentPage(final int currentPage) {
        mCurrentPage = currentPage;
    }
}