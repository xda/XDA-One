package com.xda.one.api.model.interfaces.container;

import android.os.Parcelable;

import com.xda.one.api.model.interfaces.Post;
import com.xda.one.api.model.interfaces.UnifiedThread;

import java.util.List;

public interface PostContainer extends Parcelable {

    List<? extends Post> getPosts();

    int getTotalPages();

    int getPerPage();

    int getCurrentPage();

    int getIndex();

    UnifiedThread getThread();
}
