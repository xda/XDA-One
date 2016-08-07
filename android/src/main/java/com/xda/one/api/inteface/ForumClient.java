package com.xda.one.api.inteface;

import com.xda.one.api.misc.Consumer;
import com.xda.one.api.misc.EventBus;
import com.xda.one.api.misc.Result;
import com.xda.one.api.model.interfaces.Forum;
import com.xda.one.api.model.response.ResponseForum;

import java.util.List;

public interface ForumClient {

    EventBus getBus();

    List<ResponseForum> getForums();

    List<ResponseForum> getForums(boolean forceReload);

    List<ResponseForum> getGeneralForums();

    List<ResponseForum> getGeneralForums(boolean forceReload);

    List<ResponseForum> getNewestForums();

    List<ResponseForum> getNewestForums(boolean forceReload);

    List<ResponseForum> getForumChildren(final Forum forum);

    List<ResponseForum> getSubscribedForums();

    List<ResponseForum> getTopForums();

    List<ResponseForum> getTopForums(boolean forceReload);

    void toggleForumSubscriptionAsync(final Forum forum);

    void subscribeAsync(final Forum forum);

    void unsubscribeAsync(final Forum forum);

    void markReadAsync(final Forum forum, final Consumer<Result> callable);
}
