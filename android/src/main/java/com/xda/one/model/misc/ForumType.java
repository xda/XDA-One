package com.xda.one.model.misc;

import com.xda.one.R;

public enum ForumType {
    TOP(R.string.forum_home_title),
    NEWEST(R.string.forum_home_title),
    GENERAL(R.string.forum_home_title),
    ALL(R.string.forum_home_title),
    CHILD(R.string.placeholder);

    private final int mStringTitleId;

    ForumType(int stringId) {
        mStringTitleId = stringId;
    }

    public int getStringTitleId() {
        return mStringTitleId;
    }
}