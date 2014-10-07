package com.xda.one.loader;

import com.xda.one.api.inteface.ForumClient;
import com.xda.one.api.model.interfaces.Forum;
import com.xda.one.api.model.response.ResponseForum;
import com.xda.one.api.retrofit.RetrofitForumClient;
import com.xda.one.db.ForumDbHelper;
import com.xda.one.model.misc.ForumType;
import com.xda.one.util.Utils;

import android.content.Context;

import java.util.List;

public class ForumLoader extends AsyncLoader<List<ResponseForum>> {

    private final ForumClient mClient;

    private final ForumDbHelper mHelper;

    private final Forum mForum;

    private final boolean mForceReload;

    private final ForumType mForumType;

    public ForumLoader(final Context context, final ForumType forumType, final Forum forum,
            boolean forceReload) {
        super(context);

        mClient = RetrofitForumClient.getClient(getContext());
        mHelper = ForumDbHelper.getInstance(getContext());

        mForumType = forumType;
        mForum = forum;
        mForceReload = forceReload;
    }

    @Override
    public void releaseResources(final List<ResponseForum> data) {
    }

    @Override
    public List<ResponseForum> loadInBackground() {
        final List<ResponseForum> list;
        switch (mForumType) {
            case TOP:
                list = mClient.getTopForums(mForceReload);
                break;
            case NEWEST:
                list = mClient.getNewestForums(mForceReload);
                break;
            case GENERAL:
                list = mClient.getGeneralForums(mForceReload);
                break;
            case ALL:
                list = getAllForums();
                break;
            case CHILD:
                list = getForumChildren();
                break;
            default:
                list = null;
        }
        return list;
    }

    private List<ResponseForum> getForumChildren() {
        if (!mForceReload) {
            final List<ResponseForum> forums = mHelper.getForumChildren(mForum.getForumId());
            if (forums.size() > 0) {
                return forums;
            }
        }

        final List<ResponseForum> list = mClient.getForumChildren(mForum);
        if (Utils.isCollectionEmpty(list)) {
            return null;
        }
        mHelper.updateForumCollection(list);
        return mHelper.getForumChildren(mForum.getForumId());
    }

    private List<ResponseForum> getAllForums() {
        if (!mForceReload) {
            final List<ResponseForum> forums = mHelper.getTopLevelForums();
            if (forums.size() > 0) {
                return forums;
            }
        }

        final List<ResponseForum> list = mClient.getForums(mForceReload);
        if (Utils.isCollectionEmpty(list)) {
            return null;
        }
        mHelper.replaceRawForumResponse(list);
        return mHelper.getTopLevelForums();
    }
}