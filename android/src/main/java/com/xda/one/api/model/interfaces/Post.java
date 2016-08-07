package com.xda.one.api.model.interfaces;

import com.xda.one.api.model.response.ResponseAttachment;

import android.os.Parcelable;

import java.util.List;

public interface Post extends Parcelable {

    int getPostId();

    int getVisible();

    String getUserId();

    String getTitle();

    String getPageText();

    String getUserName();

    long getDateline();

    List<ResponseAttachment> getAttachments();

    String getAvatarUrl();

    int getThanksCount();

    void setThanksCount(int newCount);

    boolean isThanked();

    void setThanked(boolean thanked);
}
