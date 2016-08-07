package com.xda.one.api.model.interfaces;

import com.xda.one.parser.TextDataStructure;

import android.os.Parcelable;

public interface Message extends Parcelable {

    int getPmId();

    String getFromUserId();

    String getFromUserName();

    String getTitle();

    CharSequence getMessageContent();

    long getDate();

    boolean isMessageUnread();

    String getToUserArray();

    boolean isShowSignature();

    boolean isAllowSmilie();

    String getAvatarUrl();

    String getSubMessage();

    TextDataStructure getTextDataStructure();

    int getMessageReadStatus();

    void setMessageReadStatus(int messageRead);
}