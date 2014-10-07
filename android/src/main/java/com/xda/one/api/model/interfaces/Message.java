package com.xda.one.api.model.interfaces;

import com.xda.one.parser.TextDataStructure;

import android.os.Parcelable;

public interface Message extends Parcelable {

    public int getPmId();

    public String getFromUserId();

    public String getFromUserName();

    public String getTitle();

    public CharSequence getMessageContent();

    public long getDate();

    public boolean isMessageUnread();

    String getToUserArray();

    boolean isShowSignature();

    boolean isAllowSmilie();

    public String getAvatarUrl();

    public String getSubMessage();

    public TextDataStructure getTextDataStructure();

    public int getMessageReadStatus();

    public void setMessageReadStatus(int messageRead);
}