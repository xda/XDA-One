package com.xda.one.api.model.interfaces;

import com.xda.one.api.model.response.ResponseAttachment;

import android.os.Parcelable;

import java.util.List;

public interface News extends Parcelable {

    int getTitle();

    int getContent();

    String getUrl();

    String getThumb();

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
