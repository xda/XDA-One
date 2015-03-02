package com.xda.one.api.model.interfaces.container;

import com.xda.one.api.model.interfaces.Post;
import com.xda.one.api.model.response.ResponseUnifiedThread;

import android.os.Parcelable;

import java.util.List;

public interface PostContainer extends Parcelable {

    List<? extends Post> getPosts();

    int getTotalPages();

    int getPerPage();

    int getCurrentPage();

    int getIndex();

    ResponseUnifiedThread getThread();
}
