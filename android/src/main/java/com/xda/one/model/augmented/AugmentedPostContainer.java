package com.xda.one.model.augmented;

import android.content.Context;
import android.os.Parcel;

import com.xda.one.api.model.interfaces.Post;
import com.xda.one.api.model.interfaces.container.PostContainer;
import com.xda.one.api.model.response.ResponseUnifiedThread;
import com.xda.one.api.model.response.container.ResponsePostContainer;

import java.util.ArrayList;
import java.util.List;

public class AugmentedPostContainer implements PostContainer {

    public static final Creator<AugmentedPostContainer> CREATOR
            = new Creator<AugmentedPostContainer>() {
        @Override
        public AugmentedPostContainer createFromParcel(Parcel source) {
            return new AugmentedPostContainer(source);
        }

        @Override
        public AugmentedPostContainer[] newArray(int size) {
            return new AugmentedPostContainer[size];
        }
    };

    private final PostContainer mContainer;

    private List<AugmentedPost> mPosts;

    public AugmentedPostContainer(final PostContainer container, final Context context) {
        mContainer = container;

        final List<? extends Post> posts = container.getPosts();
        mPosts = new ArrayList<>(posts.size());
        for (final Post post : posts) {
            final AugmentedPost formattedPost = new AugmentedPost(post, context);
            mPosts.add(formattedPost);
        }
    }

    private AugmentedPostContainer(final Parcel in) {
        mContainer = new ResponsePostContainer(in);
    }

    @Override
    public List<AugmentedPost> getPosts() {
        return mPosts;
    }

    @Override
    public int getTotalPages() {
        return mContainer.getTotalPages();
    }

    @Override
    public int getPerPage() {
        return mContainer.getPerPage();
    }

    @Override
    public int getCurrentPage() {
        return mContainer.getCurrentPage();
    }

    @Override
    public int getIndex() {
        return mContainer.getIndex();
    }

    @Override
    public ResponseUnifiedThread getThread() {
        return mContainer.getThread();
    }

    @Override
    public int describeContents() {
        return mContainer.describeContents();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(mContainer, flags);
    }
}