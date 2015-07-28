package com.xda.one.api.model.response.container;

import android.os.Parcel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.xda.one.api.model.interfaces.container.PostContainer;
import com.xda.one.api.model.response.ResponsePost;
import com.xda.one.api.model.response.ResponseUnifiedThread;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponsePostContainer implements PostContainer {

    public static final Creator<ResponsePostContainer> CREATOR
            = new Creator<ResponsePostContainer>() {
        @Override
        public ResponsePostContainer createFromParcel(Parcel source) {
            return new ResponsePostContainer(source);
        }

        @Override
        public ResponsePostContainer[] newArray(int size) {
            return new ResponsePostContainer[size];
        }
    };

    @JsonProperty(value = "results")
    private List<ResponsePost> mPosts;

    @JsonProperty(value = "total_pages")
    private int mTotalPages;

    @JsonProperty(value = "per_page")
    private int mPerPage;

    @JsonProperty(value = "current_page")
    private int mCurrentPage;

    @JsonProperty(value = "index")
    private int mIndex;

    @JsonProperty(value = "thread")
    private ResponseUnifiedThread mThread;

    public ResponsePostContainer() {
    }

    public ResponsePostContainer(Parcel in) {
        mPosts = new ArrayList<>();
        in.readTypedList(mPosts, ResponsePost.CREATOR);

        mTotalPages = in.readInt();
        mPerPage = in.readInt();
        mCurrentPage = in.readInt();
        mIndex = in.readInt();
    }

    @Override
    public List<ResponsePost> getPosts() {
        return mPosts;
    }

    @Override
    public int getTotalPages() {
        return mTotalPages;
    }

    @Override
    public int getPerPage() {
        return mPerPage;
    }

    @Override
    public int getCurrentPage() {
        return mCurrentPage;
    }

    @Override
    public int getIndex() {
        return mIndex;
    }

    @Override
    public ResponseUnifiedThread getThread() {
        return mThread;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(mPosts);

        dest.writeInt(mTotalPages);
        dest.writeInt(mPerPage);
        dest.writeInt(mCurrentPage);
        dest.writeInt(mIndex);
    }
}