package com.xda.one.api.inteface;

import com.xda.one.api.misc.Consumer;
import com.xda.one.api.misc.EventBus;
import com.xda.one.api.misc.Result;
import com.xda.one.api.model.interfaces.Forum;
import com.xda.one.api.model.response.ResponseForum;

import java.util.List;

public interface ForumClient {

    public EventBus getBus();

    public List<ResponseForum> getForums();

    public List<ResponseForum> getForums(boolean forceReload);

    public List<ResponseForum> getGeneralForums();

    public List<ResponseForum> getGeneralForums(boolean forceReload);

    public List<ResponseForum> getNewestForums();

    public List<ResponseForum> getNewestForums(boolean forceReload);

    public List<ResponseForum> getForumChildren(final Forum forum);

    public List<ResponseForum> getSubscribedForums();

    public List<ResponseForum> getTopForums();

    public List<ResponseForum> getTopForums(boolean forceReload);

    public void toggleForumSubscriptionAsync(final Forum forum);

    public void subscribeAsync(final Forum forum);

    public void unsubscribeAsync(final Forum forum);

    public void markReadAsync(final Forum forum, final Consumer<Result> callable);
}
