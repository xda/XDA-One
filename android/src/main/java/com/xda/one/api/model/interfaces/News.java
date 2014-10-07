package com.xda.one.api.model.interfaces;

import com.xda.one.api.model.response.ResponseAttachment;

import android.os.Parcelable;

import java.util.List;

public interface News extends Parcelable {

    public int getTitle();

    public int getContent();

    public String getUrl();

    public String getThumb();

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
