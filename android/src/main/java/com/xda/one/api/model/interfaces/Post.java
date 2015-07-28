package com.xda.one.api.model.interfaces;

import android.os.Parcelable;

import com.xda.one.api.model.response.ResponseAttachment;

import java.util.List;

public interface Post extends Parcelable {

    public int getPostId();

    public int getVisible();

    public String getUserId();

    public String getTitle();

    public String getPageText();

    public String getUserName();

    public long getDateline();

    public List<ResponseAttachment> getAttachments();

    public String getAvatarUrl();

    public int getThanksCount();

    public void setThanksCount(int newCount);

    public boolean isThanked();

    public void setThanked(boolean thanked);
}
